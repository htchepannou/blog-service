package com.tchepannou.blog.auth;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthServer {
    private Server server;

    public void start (int port, Handler handler) throws Exception {
        server = new Server(port);
        server.setHandler(handler);
        server.start();

    }

    public void stop () throws Exception {
        server.stop();
    }

    //-- Inner Class
    public static class OKHandler extends AbstractHandler {
        private String accessTokenId;
        private long userId;

        public OKHandler(String accessTokenId, long userId) {
            this.accessTokenId = accessTokenId;
            this.userId = userId;
        }

        @Override
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
                throws IOException, ServletException {

            httpServletResponse.addHeader("Content-Type", "application/json");
            httpServletResponse.setStatus(200);

            httpServletResponse.getWriter().write(
                    "{"
                            + "\"id\":\"" + accessTokenId + "\","
                            + "\"userId\":" + userId
                    + "}"
            );
            request.setHandled(true);
        }
    }

    public static class FailHandler extends AbstractHandler {
        @Override
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
                throws IOException, ServletException {
            httpServletResponse.setStatus(401);
            request.setHandled(true);
        }
    }

}
