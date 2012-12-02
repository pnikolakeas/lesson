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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;

import com.education.lessons.dao.model.user.User;
import com.education.lessons.dao.model.user.User.Gender;
import com.education.lessons.dao.service.login.LoginService;
import com.education.lessons.ui.server.core.WebContext;
import com.education.lessons.ui.server.core.WebException;

public class AbstractLoginController {

	@Autowired
	@Qualifier("loginService")
	private LoginService loginService;

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	/**
	 * Handles login (internally).
	 */
	protected void signIn(HttpSession session, User user) {
		session.setAttribute(WebContext.CONTEXT_USER_ATTR, user);
	}

	/**
	 * Handles sign out.
	 */
	@RequestMapping(value = "/logout")
	protected String signOut(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	protected User extractUser(HttpServletRequest request) {
		String email = str(request.getParameter("email"));
		String nickname = str(request.getParameter("nickname"));
		String password = str(request.getParameter("password"));
		String fullname = str(request.getParameter("fullname"));
		String gender = str(request.getParameter("gender"));
		String birthday = str(request.getParameter("birthday"));
		String country = str(request.getParameter("country"));
		String language = str(request.getParameter("language"));
		String postcode = str(request.getParameter("postcode"));
		String timezone = str(request.getParameter("timezone"));

		if (email == null) {
			throw new WebException(AbstractLoginController.class.getName() + ".email.empty");
		}

		if (nickname == null) {
			throw new WebException(AbstractLoginController.class.getName() + ".nickname.empty");
		}

		if (password == null) {
			throw new WebException(AbstractLoginController.class.getName() + ".password.empty");
		}

		User user = new User();
		user.setEmail(email);
		user.setNickname(nickname);
		user.setPassword(password);
		user.setFullname(fullname);

		if (gender != null) {
			user.setGender(Gender.valueOf(gender));
		}

		if (birthday != null) {
			try {
				DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				user.setBirthday(format.parse(birthday));
			}
			catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		if (country != null) {
			user.setCountry(loginService.findCountryByCode(country));
		}

		if (language != null) {
			user.setLanguage(loginService.findLanguageByCode(language));
		}

		user.setPostcode(postcode);
		user.setTimezone(timezone);
		return user;
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
