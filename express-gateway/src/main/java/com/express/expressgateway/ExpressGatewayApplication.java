package com.express.expressgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class ExpressGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpressGatewayApplication.class, args);
    }
}
