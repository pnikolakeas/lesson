package com.education.lessons.test;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.education.lessons.test.util.SpringUtils;

public abstract class BaseTest extends TestCase {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected PlatformTransactionManager transactionManager;
	protected ApplicationContext appContext;

	public BaseTest(String name) {
		super(name);

		appContext = SpringUtils.getApplicationContext();
		transactionManager = getBean("transactionManager");
	}

	@Override
	protected void setUp() {
		logger.debug("--> " + getName());
	}

	@Override
	protected void tearDown() {
		logger.debug("<-- " + getName());
	}

	protected void log(String msg) {
		logger.debug("### " + msg);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getBean(String name) {
		return (T) appContext.getBean(name);
	}

	protected TransactionTemplate createTransactionTemplate() {
		return new TransactionTemplate(transactionManager);
	}
}
