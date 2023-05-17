package com.express.expressgateway.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>校验请求中传递的 Token</h1>
 */
@Slf4j
//@Component
public class TokenFilter extends AbstractPreZuulFilter {

    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));
//        Object token = request.getParameter("token");
//        if (!token.equals("123")) {
//            log.error("error: token is empty");
//            return fail(401, "error: token is empty");
//        }

        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
