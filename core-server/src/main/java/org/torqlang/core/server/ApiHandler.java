/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.server;

import org.eclipse.jetty.http.*;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.JsonFormatter;
import org.torqlang.core.lang.JsonParser;
import org.torqlang.core.lang.ValueTools;
import org.torqlang.core.local.*;
import org.torqlang.core.local.ApiTarget.ApiTargetActorImage;
import org.torqlang.core.local.ApiTarget.ApiTargetActorRef;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class ApiHandler extends Handler.Abstract.NonBlocking {

    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=utf-8";

    private static final String RESPONSE_ADDRESS_PREFIX = "ApiHandler.ResponseAddress";

    private final ActorSystem system;
    private final ApiRouter router;

    public ApiHandler(ActorSystem system, ApiRouter router) {
        this.system = system;
        this.router = router;
    }

    public static ApiHandlerBuilder builder() {
        return new ApiHandlerBuilder();
    }

    @Override
    public final boolean handle(Request request, Response response, Callback callback) {
        String method = request.getMethod();
        String pathInContext = URLDecoder.decode(Request.getPathInContext(request), StandardCharsets.UTF_8);

        HttpFields headerFields = request.getHeaders();
        CompleteRecBuilder headersRecBuilder = Rec.completeRecBuilder();
        for (HttpField f : headerFields) {
            headersRecBuilder.addField(Str.of(f.getName()), Str.of(f.getValue()));
        }
        CompleteRec headersRec = headersRecBuilder.build();

        // Jetty will decode query parameters to UTF-8
        Fields queryFields = Request.extractQueryParameters(request);
        CompleteRecBuilder queryRecBuilder = Rec.completeRecBuilder();
        for (Fields.Field f : queryFields) {
            queryRecBuilder.addField(Str.of(f.getName()), Str.of(f.getValue()));
        }
        CompleteRec queryRec = queryRecBuilder.build();

        if (method.equals(HttpMethod.GET.name())) {
            sendRequestMessage(request, response, callback, pathInContext, method, headersRec, queryRec, null);
        } else {
            Content.Source.asStringAsync(request, StandardCharsets.UTF_8)
                .thenAccept((requestText -> sendRequestMessage(request, response, callback, pathInContext, method,
                    headersRec, queryRec, requestText)));
        }

        return true;
    }

    public final ApiRouter router() {
        return router;
    }

    private void sendRequestMessage(Request request, Response response, Callback callback,
                                    String pathInContext, String method, CompleteRec headersRec, CompleteRec queryRec,
                                    String requestText)
    {
        ApiPath apiPath = new ApiPath(pathInContext);
        ApiRoute route = router.findRoute(apiPath);
        if (route == null) {
            Response.writeError(request, response, callback, HttpStatus.NOT_FOUND_404);
            return;
        }
        try {
            ActorBuilderInit actorInit = Actor.builder().setSystem(system);
            ActorRef actorRef;
            if (route.apiTarget instanceof ApiTargetActorImage targetActorImage) {
                actorRef = Actor.spawn(Address.create("api-handler"), targetActorImage.value());
            } else {
                actorRef = ((ApiTargetActorRef) route.apiTarget).actorRef;
            }
            CompleteRecBuilder requestRecBuilder = Rec.completeRecBuilder()
                .setLabel(Str.of(method))
                .addField(Str.of("headers"), headersRec)
                .addField(Str.of("path"), ValueTools.toKernelValue(apiPath.segs))
                .addField(Str.of("query"), queryRec);
            if (requestText != null) {
                Complete bodyValue = requestText.isBlank() ?
                    Null.SINGLETON : ValueTools.toKernelValue(new JsonParser(requestText).parse());
                requestRecBuilder.addField(Str.of("body"), bodyValue);
            }
            CompleteRec requestRec = requestRecBuilder.build();
            ActorRef responseActor = new ResponseActor(request, response, callback);
            actorRef.send(Envelope.createRequest(requestRec, responseActor, Null.SINGLETON));
        } catch (Exception exc) {
            Response.writeError(request, response, callback, exc);
        }
    }

    public final ActorSystem system() {
        return system;
    }

    private static class ResponseActor implements ActorRef {
        private final Address address;
        private final Request request;
        private final Response response;
        private final Callback callback;

        private ResponseActor(Request request, Response response, Callback callback) {
            address = Address.create(RESPONSE_ADDRESS_PREFIX + "." + request.getId());
            this.request = request;
            this.response = response;
            this.callback = callback;
        }

        @Override
        public Address address() {
            return address;
        }

        @Override
        public void send(Envelope envelope) {
            try {
                Complete message = (Complete) envelope.message();
                if (!envelope.isResponse()) {
                    response.setStatus(500);
                    response.getHeaders().put(HttpHeader.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8);
                    Content.Sink.write(response, true, "Not a response: " + envelope, callback);
                } else if (message instanceof FailedValue failedValue) {
                    response.setStatus(500);
                    response.getHeaders().put(HttpHeader.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8);
                    Content.Sink.write(response, true, failedValue.toDetailsString(), callback);
                } else {
                    // TODO: Replace with a kernel-to-json instead of kernel-to-native-to-json
                    Object nativeResponseValue = ValueTools.toNativeValue(message);
                    String jsonResponseText = JsonFormatter.SINGLETON.format(nativeResponseValue);
                    response.setStatus(200);
                    response.getHeaders().put(HttpHeader.CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
                    Content.Sink.write(response, true, jsonResponseText, callback);
                }
            } catch (Exception exc) {
                Response.writeError(request, response, callback, exc);
            }
        }
    }

}
