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

import java.util.Map;

/**
 * OpenID service abstraction.
 */
public interface OpenIDService {

	// Attribute Exchange 1.0
	String AX_10 = "http://openid.net/srv/ax/1.0";

	// Simple Registration 1.0
	String SREG_10 = "http://openid.net/sreg/1.0";

	// Simple Registration 1.1
	String SREG_11 = "http://openid.net/extensions/sreg/1.1";

	enum Attribute {

		// @formatter:off
		NICKNAME ( "nickname" , "http://axschema.org/namePerson/friendly" ), 
		FULLNAME ( "fullname" , "http://axschema.org/namePerson" ), 
		GENDER   ( "gender"   , "http://axschema.org/person/gender" ), 
		DOB      ( "dob"      , "http://axschema.org/birthDate" ), 
		COUNTRY  ( "country"  , "http://axschema.org/contact/country/home" ), 
		POSTCODE ( "postcode" , "http://axschema.org/contact/postalCode/home" ), 
		EMAIL    ( "email"    , "http://axschema.org/contact/email" ), 
		LANGUAGE ( "language" , "http://axschema.org/pref/language" ), 
		TIMEZONE ( "timezone" , "http://axschema.org/pref/timezone" );
		// @formatter:on

		public final String sr;
		public final String ax;

		private Attribute(String sr, String ax) {
			this.sr = sr;
			this.ax = ax;
		}
	}

	/**
	 * Performs OpenID discovery and returns a data object with the information
	 * required to make an actual authentication request to the OpenID provider.
	 */
	RequestData setupRequest(String identifier, String returnTo, String realm);

	/**
	 * Verifies a response from an OpenID provider and returns a data object
	 * that contains the user identifier and other (possible) information.
	 */
	ResponseData verifyResponse(String requestURL, Map<String, String[]> parameterMap, Object discovery);

	class RequestData {

		private String url;
		private String endpoint;
		private Map<String, String> parameters;
		private Object discovery;

		public RequestData(String url, Object discovery) {
			this.url = url;
			this.discovery = discovery;
		}

		public RequestData(String endpoint, Map<String, String> parameters, Object discovery) {
			this.endpoint = endpoint;
			this.parameters = parameters;
			this.discovery = discovery;
		}

		public String getUrl() {
			return url;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public Object getDiscovery() {
			return discovery;
		}

		public boolean isVersion2() {
			return endpoint != null && parameters != null;
		}

		public boolean isVersion1() {
			return url != null;
		}
	}

	class ResponseData {

		private String identifier;
		private Map<String, String> attributes;

		public ResponseData(String identifier, Map<String, String> attributes) {
			this.identifier = identifier;
			this.attributes = attributes;
		}

		public String getIdentifier() {
			return identifier;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}
	}
}
