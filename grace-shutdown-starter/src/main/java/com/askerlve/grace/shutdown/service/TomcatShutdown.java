package com.askerlve.grace.shutdown.service;

import org.apache.catalina.connector.Connector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Askerlve
 * @Description: TomcatShutdown 支持
 * @date 2018/5/31下午4:28
 */
public class TomcatShutdown implements Shutdown,TomcatConnectorCustomizer{

    private static final Log logger = LogFactory.getLog(TomcatShutdown.class);

    private volatile Connector connector;

    @Override
    public void pause() throws InterruptedException {

        final Executor executor = this.connector.getProtocolHandler().getExecutor();

        //Start the pause.
        this.connector.pause();
    }

    @Override
    public void shutdown(Integer delay) throws InterruptedException {

        final Executor executor = connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {

            final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

            /**
             * 关闭线程池，不在接受新的任务提交
             */
            threadPoolExecutor.shutdown();

            // We wait after the end of the current requests
            if(!threadPoolExecutor.awaitTermination(delay, TimeUnit.SECONDS)) {
                logger.warn("Tomcat thread pool did not shut down gracefully within " + delay + " second(s). Proceeding with force shutdown");
            } else {
                logger.debug("Tomcat thread pool is empty, we stop now");
            }

        }
    }

    /**
     * 设置connector
     * @param connector the catalina connector
     */
    @Override
    public void customize(final Connector connector) {
        this.connector = connector;
    }
}
