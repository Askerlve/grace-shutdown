package com.askerlve.grace.shutdown.health;

import com.askerlve.grace.shutdown.endpoint.GraceShutdownEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * @author Askerlve
 * @Description: 健康监测
 * @date 2018/5/31下午6:26
 */
@EnableAutoConfiguration
public class GraceHealth implements HealthIndicator {

    @Autowired
    private GraceShutdownEndpoint graceShutdownEndpoint;

    @Override
    public Health health() {
        if(this.graceShutdownEndpoint.getStartShutdown() == null) {
            return Health.up().build();
        } else {
            return Health.outOfService().build();
        }
    }
}
