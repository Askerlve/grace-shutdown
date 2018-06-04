package com.askerlve.grace.shutdown.wrapper;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;

/**
 * @author Askerlve
 * @Description: Undertow handler wrapper
 * @date 2018/5/31下午5:20
 */
public class UndertowShutdownHandlerWrapper implements HandlerWrapper {

    /**
     * graceful shutdown handler.
     */
    private GracefulShutdownHandler gracefulShutdownHandler;

    /**
     * Wrapper.
     * @param handler is the http handler from chain.
     * @return the Undertown shutdown handler.
     */
    public HttpHandler wrap(final HttpHandler handler) {
        if(this.gracefulShutdownHandler == null) {
            this.gracefulShutdownHandler = new GracefulShutdownHandler(handler);
        }
        return this.gracefulShutdownHandler;
    }

    /**
     * Return the graceful shutdown handler to perform manual command : pause/shutdown.
     * @return the shutdown handler.
     */
    public GracefulShutdownHandler getGracefulShutdownHandler() {
        return this.gracefulShutdownHandler;
    }

}
