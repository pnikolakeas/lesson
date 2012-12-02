package com.education.lessons.ui.client.services;

import com.education.lessons.ui.client.utils.IConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

@SuppressWarnings("unchecked")
public final class AsyncServiceFactory {

	private static DataProviderServiceAsync dataProviderService;

	private AsyncServiceFactory() {
	}

	public static <T> T getService(Class<T> serviceTypeClass) {
		if (IConstants.DATA_PROVIDER_SERVICE.equalsIgnoreCase(serviceTypeClass.getName())) {
			if (dataProviderService == null)
				dataProviderService = initDataProviderService();

			return (T) dataProviderService;
		}
		return null;
	}

	private static DataProviderServiceAsync initDataProviderService() {
		final DataProviderServiceAsync dpService = (DataProviderServiceAsync) GWT.create(DataProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) dpService;
		String moduleRelativeURL = GWT.getModuleBaseURL() + IConstants.DATA_PROVIDER;
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return dpService;
	}
}
