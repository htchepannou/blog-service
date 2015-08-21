package com.tchepannou.blog.auth;

import com.google.common.base.Joiner;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        private List<String> permissions = Collections.emptyList();

        public OKHandler(String accessTokenId, long userId, List<String> permissions) {
            this.accessTokenId = accessTokenId;
            this.userId = userId;
            this.permissions = permissions;
        }

        @Override
        public void handle(String uri, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
                throws IOException, ServletException {

            if (uri.contains("/access_token")) {
                if (request.getPathInfo().endsWith("/" + accessTokenId)) {
                    httpServletResponse.addHeader("Content-Type", "application/json");
                    httpServletResponse.setStatus(200);

                    httpServletResponse.getWriter().write(
                            "{"
                                    + "\"id\":\"" + accessTokenId + "\","
                                    + "\"userId\":" + userId
                                    + "}"
                    );
                } else {
                    httpServletResponse.setStatus(401);
                }
            } else if (uri.contains("/permission")){
                httpServletResponse.getWriter().write(
                        "{"
                                + "\"permissions\":[" +
                                Joiner.on(",").skipNulls().join(
                                        permissions.stream().map(p -> String.format("\"%s\"", p)).collect(Collectors.toList())
                                )
                                + "]}"
                );

            }
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
