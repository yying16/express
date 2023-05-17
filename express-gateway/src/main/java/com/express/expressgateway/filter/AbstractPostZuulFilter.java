package com.express.expressgateway.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;


public abstract class AbstractPostZuulFilter extends AbstractZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
