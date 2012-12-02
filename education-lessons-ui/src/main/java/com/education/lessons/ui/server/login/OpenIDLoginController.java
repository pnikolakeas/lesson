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
package com.education.lessons.ui.server.login;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.education.lessons.dao.model.user.User;
import com.education.lessons.ui.server.openid.OpenIDService;
import com.education.lessons.ui.server.openid.OpenIDService.RequestData;
import com.education.lessons.ui.server.openid.OpenIDService.ResponseData;

@Controller
@RequestMapping("/login/openid")
public class OpenIDLoginController extends AbstractLoginController {

	private static final String DISCOVERY_ATTR = "openidDiscovery";
	private static final String IDENTIFIER_ATTR = "openidIdentifier";

	private static final String IDENTIFIER_PARAM = "openid_identifier";

	@Autowired
	@Qualifier("openidService")
	private OpenIDService openIDService;

	public OpenIDService getOpenIDService() {
		return openIDService;
	}

	public void setOpenIDService(OpenIDService openIDService) {
		this.openIDService = openIDService;
	}

	/**
	 * Handles OpenID login preparation.<br>
	 * Performs OP discovery. Redirects to the OP endpoint.
	 */
	@RequestMapping(value = "/setup")
	public ModelAndView doSetup(HttpServletRequest request) {
		String identifier = request.getParameter(IDENTIFIER_PARAM);
		String returnTo = getReturnTo(request);
		String realm = getRealm(request);

		RequestData requestData = getOpenIDService().setupRequest(identifier, returnTo, realm);

		// discovery completed
		request.getSession().setAttribute(DISCOVERY_ATTR, requestData.getDiscovery());

		if (requestData.isVersion2()) {
			// OpenID v2 - use html form redirect
			ModelAndView mav = new ModelAndView("/login/openid/setup");
			mav.addObject("endpoint", requestData.getEndpoint());
			mav.addObject("parameters", requestData.getParameters());
			return mav;
		}

		// OpenID v1 - use http redirect
		return new ModelAndView("redirect:" + requestData.getUrl());
	}

	/**
	 * Handles OpenID login verification.<br>
	 * Determines whether the user is already a member (performs sign-in) or not
	 * (initiates sign-up). In either case, the control is transferred to the
	 * client.
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/verify")
	public ModelAndView doVerify(HttpServletRequest request) {
		String requestURL = getRequestURL(request, true);
		Map<String, String[]> parameterMap = request.getParameterMap();
		Object discovery = request.getSession().getAttribute(DISCOVERY_ATTR);

		ResponseData responseData = getOpenIDService().verifyResponse(requestURL, parameterMap, discovery);

		// look up for existing user
		User user = getLoginService().findUserByOpenID(responseData.getIdentifier());
		ModelAndView mav = new ModelAndView("/login/openid/verify");

		if (user != null) {
			// user found; perform sign-in
			signIn(request.getSession(), user);
		}
		else {
			// create user ...
			user = new User();
			user.setNickname(responseData.getAttributes().get("nickname"));
			user.setFullname(responseData.getAttributes().get("fullname"));
			user.setEmail(responseData.getAttributes().get("email"));
			user.setPassword("123");
			user.setAdmin(Boolean.FALSE);
			getLoginService().createUserWithOpenID(user, responseData.getIdentifier());
			user = getLoginService().findUserByOpenID(responseData.getIdentifier());
			signIn(request.getSession(), user);
		}

		return mav;
	}

	/**
	 * Handles registration submission.
	 */
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ModelAndView doSignUp(HttpServletRequest request) {
		String identifier = (String) request.getSession().getAttribute(IDENTIFIER_ATTR);

		if (identifier == null || identifier.isEmpty()) {
			throw new RuntimeException();
		}

		User user = extractUser(request);
		getLoginService().createUserWithOpenID(user, identifier);
		signIn(request.getSession(), user);
		return null;
	}

	/**
	 * Builds the current full URL.
	 */
	private String getRequestURL(HttpServletRequest request, boolean qs) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (qs && queryString != null && !queryString.isEmpty()) {
			requestURL.append("?").append(queryString);
		}

		return requestURL.toString();
	}

	/**
	 * Builds the current base URL; up to the the domain name, e.g.
	 * http://www.mydomain.com/
	 */
	private String getRealm(HttpServletRequest request) {
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();

		StringBuilder sb = new StringBuilder();
		sb.append(scheme).append("://").append(serverName);

		if (serverPort != 80) {
			sb.append(":").append(serverPort);
		}

		sb.append("/");
		return sb.toString();
	}

	/**
	 * Builds the callback URL; realm + context path + return path, e.g.
	 * http://www.mydomain.com/context/return.htm
	 */
	private String getReturnTo(HttpServletRequest request) {
		String realm = getRealm(request);
		realm = realm.substring(0, realm.length() - 1);
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		return realm + contextPath + servletPath + "/login/openid/verify";
	}
}
