/*
 * Copyright (C) 2010 Dimitrios Menounos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.education.lessons.ui.server.openid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.Message;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.education.lessons.ui.server.core.WebException;

public class OpenID4JavaService implements OpenIDService {

	private static final Logger logger = LoggerFactory.getLogger(OpenID4JavaService.class);

	private ConsumerManager consumerManager;

	public ConsumerManager getConsumerManager() {
		return consumerManager;
	}

	public void setConsumerManager(ConsumerManager consumerManager) {
		this.consumerManager = consumerManager;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RequestData setupRequest(String identifier, String returnTo, String realm) {
		if (identifier == null || identifier.trim().equals("")) {
			throw new WebException(OpenID4JavaService.class.getName() + ".identifier.empty");
		}

		List<?> discoveries;

		try {
			// perform discovery on the supplied identifier
			discoveries = consumerManager.discover(identifier);
		}
		catch (DiscoveryException e) {
			throw new WebException(OpenID4JavaService.class.getName() + ".discovery.failed");
		}

		if (discoveries == null || discoveries.size() == 0) {
			throw new WebException(OpenID4JavaService.class.getName() + ".discovery.empty");
		}

		// attempt association with a provider
		DiscoveryInformation discovery = consumerManager.associate(discoveries);

		try {
			// build the request message
			AuthRequest message = consumerManager.authenticate(discovery, returnTo, realm);

			if (discovery.hasType(SREG_10) || discovery.hasType(SREG_11)) {
				logger.debug("OP supports simple-registration");
				SRegRequest sregRequest = SRegRequest.createFetchRequest();

				for (Attribute attribute : Attribute.values()) {
					sregRequest.addAttribute(attribute.sr, false);
				}

				message.addExtension(sregRequest);
			}

			if (discovery.hasType(AX_10)) {
				logger.debug("OP supports attribute-exchange");
				FetchRequest fetchRequest = FetchRequest.createFetchRequest();

				for (Attribute attribute : Attribute.values()) {
					fetchRequest.addAttribute(attribute.sr, attribute.ax, false);
				}

				message.addExtension(fetchRequest);
			}

			if (discovery.isVersion2()) {
				return new RequestData(message.getOPEndpoint(), message.getParameterMap(), discovery);
			}

			return new RequestData(message.getDestinationUrl(true), discovery);
		}
		catch (OpenIDException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResponseData verifyResponse(String requestURL, Map<String, String[]> parameterMap, Object discovery) {
		try {
			// extract the parameters from the authentication response
			ParameterList parameterList = new ParameterList(parameterMap);

			// verify the response; consumerManager needs to be the
			// same instance used to place the authentication request
			DiscoveryInformation discoveryInformation = (DiscoveryInformation) discovery;
			VerificationResult verification = consumerManager.verify(requestURL, parameterList, discoveryInformation);

			Identifier identifier = verification.getVerifiedId();
			Message authResponse = verification.getAuthResponse();

			if (identifier == null || identifier.getIdentifier() == null) {
				throw new WebException(OpenID4JavaService.class.getName() + ".authentication.failed");
			}

			Map<String, String> attributes = new HashMap<String, String>();

			if (authResponse.hasExtension(SREG_10) || authResponse.hasExtension(SREG_11)) {
				logger.debug("Fetching simple-registration response");
				SRegResponse sregResp = (SRegResponse) authResponse.getExtension(SREG_10);

				for (Iterator<String> iter = sregResp.getAttributeNames().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = str(sregResp.getParameterValue(name));
					logger.debug(name + ": " + value);
					attributes.put(name, value);
				}
			}

			if (authResponse.hasExtension(AX_10)) {
				logger.debug("Fetching attribute-exchange response");
				FetchResponse fetchResp = (FetchResponse) authResponse.getExtension(AX_10);

				for (Iterator<String> iter = fetchResp.getAttributeAliases().iterator(); iter.hasNext();) {
					String alias = (String) iter.next();
					String value = str(fetchResp.getAttributeValue(alias));
					logger.debug(alias + ": " + value);

					if (attributes.get(alias) == null) {
						attributes.put(alias, value);
					}
				}
			}

			return new ResponseData(identifier.getIdentifier(), attributes);
		}
		catch (OpenIDException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Filters out empty strings.
	 */
	protected String str(String str) {
		if (str != null) {
			str = str.trim();

			if (!str.isEmpty()) {
				return str;
			}
		}

		return null;
	}
}
