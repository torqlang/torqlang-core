module org.torqlang.core.server {

    requires org.torqlang.core.local;
    requires org.torqlang.core.lang;
    requires org.torqlang.core.klvm;
    requires org.torqlang.core.util;

    requires org.eclipse.jetty.server;

    exports org.torqlang.core.server;

}