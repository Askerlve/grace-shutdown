package com.askerlve.grace.shutdown.service;

import com.askerlve.grace.shutdown.wrapper.UndertowShutdownHandlerWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Askerlve
 * @Description: UndertowShutdown 支持
 * @date 2018/5/31下午5:18
 */
public class UndertowShutdown implements Shutdown {

    private static final Log logger = LogFactory.getLog(UndertowShutdown.class);

    @Autowired
    private UndertowShutdownHandlerWrapper undertowShutdownHandlerWrapper;

    @Override
    public void pause() throws InterruptedException {

    }

    @Override
    public void shutdown(Integer delay) throws InterruptedException {
        this.undertowShutdownHandlerWrapper.getGracefulShutdownHandler().shutdown();
        this.undertowShutdownHandlerWrapper.getGracefulShutdownHandler().awaitShutdown(delay * 1000);
    }
}
