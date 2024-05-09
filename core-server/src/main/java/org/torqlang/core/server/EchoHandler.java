/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.server;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public final class EchoHandler extends Handler.Abstract.NonBlocking {

    private static String formatHeaderFields(HttpFields httpFields) {
        if (httpFields == null) {
            return "  (none)";
        }
        Iterator<HttpField> it = httpFields.stream().iterator();
        if (!it.hasNext()) {
            return "  (none)";
        }
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            HttpField f = it.next();
            sb.append("  ").append(f.getName()).append(": ").append(f.getValue());
            if (it.hasNext()) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private static String formatQueryFields(Fields fields) {
        if (fields == null) {
            return "  (none)";
        }
        Iterator<Fields.Field> it = fields.stream().iterator();
        if (!it.hasNext()) {
            return "  (none)";
        }
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Fields.Field f = it.next();
            sb.append("  ").append(f.getName()).append(": ").append(f.getValue());
            if (it.hasNext()) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public final boolean handle(Request request, Response response, Callback callback) {
        Content.Source.asStringAsync(request, StandardCharsets.UTF_8)
            .thenAccept((s -> onContentComplete(s, request, response, callback)));
        return true;
    }

    private void onContentComplete(String content, Request request, Response response, Callback callback) {
        // Build response text
        StringBuilder sb = new StringBuilder();
        sb.append("request.getId() = ").append(request.getId()).append('\n');
        sb.append("request.getMethod() = ").append(request.getMethod()).append('\n');
        sb.append("request.getHttpURI() = ").append(request.getHttpURI()).append('\n');
        sb.append("request.getHttpURI().getPath() = ").append(request.getHttpURI().getPath()).append('\n');
        sb.append("request.getHttpURI().getQuery() = ").append(request.getHttpURI().getQuery()).append('\n');
        sb.append("request.getHttpURI().getPathQuery() = ").append(request.getHttpURI().getPathQuery()).append('\n');
        sb.append("URLDecoder.decode(request.getHttpURI().getPathQuery(), StandardCharsets.UTF_8) = ")
            .append(URLDecoder.decode(request.getHttpURI().getPathQuery(), StandardCharsets.UTF_8)).append('\n');
        sb.append("Request.getPathInContext(request) = ").append(Request.getPathInContext(request)).append('\n');
        sb.append("Request.extractQueryParameters(request) {").append('\n')
            .append(formatQueryFields(Request.extractQueryParameters(request))).append('\n')
            .append("}").append('\n');
        sb.append("request.getHeaders() {").append('\n')
            .append(formatHeaderFields(request.getHeaders())).append('\n')
            .append("}").append('\n');
        sb.append("request.getTrailers() {").append('\n')
            .append(formatHeaderFields(request.getTrailers())).append('\n')
            .append("}").append('\n');
        sb.append("request.getLength() = ").append(request.getLength()).append('\n');
        sb.append("Content.Source.asStringAsync(request, StandardCharsets.UTF_8) = ")
            .append(content.isEmpty() ? "(empty)" : content).append('\n');
        // Complete response
        response.setStatus(200);
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/plain; charset=utf-8");
        Content.Sink.write(response, true, sb.toString(), callback);
    }

}
