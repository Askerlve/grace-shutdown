package com.askerlve.grace.shutdown.endpoint;

import com.askerlve.grace.shutdown.service.Shutdown;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author Askerlve
 * @Description: 优雅关机Endpoint
 * @date 2018/5/31下午6:27
 */
@Endpoint(id = "graceShutdown")
public class GraceShutdownEndpoint implements ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    /**
     * Graceful shutdown without context.
     */
    private static final Map<String, Object> NO_CONTEXT_MESSAGE = Collections.unmodifiableMap(Collections.<String, Object>singletonMap("message", "No context to shutdown."));

    /**
     * Graceful shutdown with context.
     */
    private static final Map<String, Object> SHUTDOWN_MESSAGE = Collections.unmodifiableMap(Collections.<String, Object>singletonMap("message", "Graceful shutting down, bye..."));

    /**
     * Logger from common util.
     */
    private static final Log logger = LogFactory.getLog(GraceShutdownEndpoint.class);

    private ConfigurableApplicationContext context;

    /**
     * 优雅关闭之前的等待，健康监测将会返回 OUT_OF_SERVICE
     */
    private Integer wait = 30;

    /**
     * 强制关闭之前的等待
     */
    private Integer timeout = 30;

    /**
     * Start date for shutdown.
     */
    private Date startShutdown;

    /**
     * Stop date for shutdown.
     */
    private Date stopShutdown;

    @Autowired
    private Shutdown shutdown;

    public GraceShutdownEndpoint(final Integer timeout, final Integer wait) {
        this.timeout = timeout;
        this.wait = wait;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.context = (ConfigurableApplicationContext) applicationContext;
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        if (this.stopShutdown != null && this.startShutdown != null) {
            final long seconds = (this.stopShutdown.getTime() - this.startShutdown.getTime()) / 1000;
            logger.info("Shutdown performed in " + seconds + " second(s)!");
        }
    }

    /**
     * 优雅停机
     *
     * @return
     */
    @WriteOperation
    public Map<String, Object> graceShutdown() {
        if (this.context == null) {
            return NO_CONTEXT_MESSAGE;
        }
        try {
            return SHUTDOWN_MESSAGE;
        } finally {

            final Thread thread = new Thread(() -> {
                try {
                    // We top the start
                    this.startShutdown = new Date();

                    // Set Health Checker in OUT_OF_SERVICE state.
                    logger.info("We are now in OUT_OF_SERVICE mode, please wait " + this.wait + " second(s)...");
                    Thread.sleep(this.wait * 1000);
                    this.shutdown.pause();

                    // Pause the protocol.
                    logger.info("Graceful shutdown in progess... We don't accept new connection... Wait after latest connections (max : " + this.timeout + " seconds)... ");

                    // perform stop
                    this.shutdown.shutdown(this.timeout);

                    // Close spring context.
                    this.stopShutdown = new Date();
                    this.context.close();
                } catch (final InterruptedException ex) {
                    logger.error("The await termination has been interrupted : " + ex.getMessage());
                    Thread.currentThread().interrupt();
                }
            });

            // Link and start thread.
            thread.setContextClassLoader(this.getClass().getClassLoader());
            thread.start();
        }
    }

    public Date getStartShutdown() {
        return this.startShutdown;
    }
}
