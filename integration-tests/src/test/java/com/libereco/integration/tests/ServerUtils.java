package com.libereco.integration.tests;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public abstract class ServerUtils {

    public static Server startServer(int port, String contextPath, String defaultWarLocation) throws Exception {
        Server server = new Server(port);
        server.setStopAtShutdown(true);
        WebAppContext wac = new WebAppContext(getWarPath(defaultWarLocation), contextPath);
        wac.setClassLoader(ServerUtils.class.getClass().getClassLoader());
        server.addHandler(wac);
        server.start();
        return server;
    }

    public static void stopServer(Server server) throws Exception {
        server.stop();
    }

    private static String getWarPath(String defaultWarLocation) {
        String path = System.getProperty("libereco.war.path");
        path = StringUtils.isBlank(path) == true ? defaultWarLocation : path;
        return path;
    }
}
