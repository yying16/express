package com.express.expressgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>在过滤器中存储客户端发起请求的时间戳</h1>
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter {

    @Override
    protected Object cRun() {

        context.set("startTime", System.currentTimeMillis());

        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
