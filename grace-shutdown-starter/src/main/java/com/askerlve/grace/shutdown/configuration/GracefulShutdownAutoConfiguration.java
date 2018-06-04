package com.askerlve.grace.shutdown.configuration;

import com.askerlve.grace.shutdown.endpoint.GraceShutdownEndpoint;
import com.askerlve.grace.shutdown.health.GraceHealth;
import com.askerlve.grace.shutdown.properties.GraceShutdownProperties;
import com.askerlve.grace.shutdown.service.TomcatShutdown;
import com.askerlve.grace.shutdown.service.UndertowShutdown;
import com.askerlve.grace.shutdown.wrapper.UndertowShutdownHandlerWrapper;
import io.undertow.Undertow;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.xnio.SslClientAuthMode;

import javax.servlet.Servlet;

/**
 * @author Askerlve
 * @Description: grace shutdown 自动配置
 * @date 2018/5/31下午7:17
 */
@Configuration
@ConditionalOnProperty(prefix = "endpoints.shutdown.grace", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GraceShutdownProperties.class)
@Import(ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class)
public class GracefulShutdownAutoConfiguration {

    @Autowired
    private GraceShutdownProperties gracefulShutdownProperties;

    /**
     * Configuration for Tomcat.
     */
    @Configuration
    @ConditionalOnClass({Servlet.class, Tomcat.class})
    @ConditionalOnProperty(prefix = "grace.shutdown.server", name = "container", havingValue = "tomcat", matchIfMissing = true)
    public static class tomcatCfg {
        @Bean
        public TomcatShutdown tomcatShutdown() {
            return new TomcatShutdown();
        }

        @Bean
        public ServletWebServerFactory tomcatServletContainer() {
            TomcatServletWebServerFactory servletWebServerFactory = new TomcatServletWebServerFactory();
            servletWebServerFactory.addConnectorCustomizers(tomcatShutdown());
            return servletWebServerFactory;
        }
    }

    /**
     * Configuration for Undertow.
     */
    @Configuration
    @ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
    @ConditionalOnProperty(prefix = "grace.shutdown.server", name = "container", havingValue = "undertow")
    public static class undertowCfg {

        @Bean
        public UndertowShutdown undertowShutdown() {
            return new UndertowShutdown();
        }

        @Bean
        public ServletWebServerFactory undertowServletContainer() {
            UndertowServletWebServerFactory servletWebServerFactory = new UndertowServletWebServerFactory();
            servletWebServerFactory.addDeploymentInfoCustomizers(undertowDeploymentInfoCustomizer());
            return servletWebServerFactory;
        }

        @Bean
        public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer() {
            return (deploymentInfo) -> deploymentInfo.addOuterHandlerChainWrapper(undertowShutdownHandlerWrapper());
        }

        @Bean
        public UndertowShutdownHandlerWrapper undertowShutdownHandlerWrapper() {
            return new UndertowShutdownHandlerWrapper();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    protected GraceShutdownEndpoint graceShutdownEndpoint() {
        return new GraceShutdownEndpoint(this.gracefulShutdownProperties.getTimeout(), this.gracefulShutdownProperties.getWait());
    }

    @Bean
    public GraceHealth gracefulHealth() {
        return new GraceHealth();
    }

}
