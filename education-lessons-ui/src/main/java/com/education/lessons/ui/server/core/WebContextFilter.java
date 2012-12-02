package com.education.lessons.ui.server.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebContextFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(WebContextFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			logger.debug("Creating thread-local context object");
			WebContext ctx = new WebContext(req, res);
			WebContextHolder.setCurrentContext(ctx);
			chain.doFilter(req, res);
		}
		finally {
			logger.debug("Removing thread-local context object");
			WebContextHolder.removeCurrentContext();
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
