package com.askerlve.grace.shutdown.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Askerlve
 * @Description: Global grace shutdown properties
 * @date 2018/5/31下午5:59
 */
@ConfigurationProperties(prefix = "endpoints.shutdown.grace")
public class GraceShutdownProperties {

    /**
     * 优雅关闭之前的等待，健康监测将会返回 OUT_OF_SERVICE
     */
    private Integer wait = 30;

    /**
     * 强制关闭之前的等待
     */
    private Integer timeout = 30;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getWait() {
        return wait;
    }

    public void setWait(Integer wait) {
        this.wait = wait;
    }

}
