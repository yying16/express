package com.express.expressgateway.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;


public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
