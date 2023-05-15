package org.lpw.photon.ctrl.http;

import org.lpw.photon.bean.BeanFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全过滤器。
 */
public class SecurityFilter implements Filter {
    private SecurityHelper securityHelper;

    @Override
    public void init(FilterConfig config) throws ServletException {
        securityHelper = BeanFactory.getBean(SecurityHelper.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (securityHelper.isEnable(request))
            chain.doFilter(request, response);
        else
            response.sendError(404);
    }

    @Override
    public void destroy() {
    }
}
