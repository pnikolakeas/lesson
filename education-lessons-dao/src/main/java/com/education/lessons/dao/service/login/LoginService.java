package com.education.lessons.dao.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.education.lessons.dao.model.general.Country;
import com.education.lessons.dao.model.general.Language;
import com.education.lessons.dao.model.user.OpenID;
import com.education.lessons.dao.model.user.User;

@Service
@Transactional
public class LoginService {
	
	@Autowired protected LoginDao loginDao;
	@Autowired protected OpenIDDao openIDDao;
	
	public void createUser(User user) {
		loginDao.insert(user);
	}

	public void createUserWithOpenID(User user, String address) {
		createUser(user);
		OpenID openID = user.addOpenID(address);
		openIDDao.insert(openID);
	}

	public User findUserByNicknameOrEmail(String username) {
		return loginDao.findUserByNicknameOrEmail(username);
	}

	public User findUserByOpenID(String identifier) {
		return loginDao.findUserByOpenID(identifier);
	}

	public Country findCountryByCode(String code) {
		return loginDao.findCountryByCode(code);
	}

	public Language findLanguageByCode(String code) {
		return loginDao.findLanguageByCode(code);
	}
}
