package com.education.lessons.ui.server.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequireLoginFilter implements Filter {

	public static final Logger logger = LoggerFactory.getLogger(RequireLoginFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (WebContextHolder.getCurrentContext().getUser() == null) {
			logger.debug("unauthorized request: " + WebContextHolder.getCurrentContext().getRequest().getRequestURI());
			WebContextHolder.getCurrentContext().getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
