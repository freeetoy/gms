package com.gms.web.admin.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.mapper.common.LoginMapper;

@Service
public class LoginServiceImpl implements LoginService{

	private static final String RETURN_LOGINPAGE = "/common/login/loginForm";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private LoginMapper loginMapper;
	
	/**
	 *  사용자 정보 조회
	 * @param param 		- LoginUserVO
	 */
	@Override
	@Transactional
	public LoginUserVO modifyUserInfo(LoginUserVO param) {
		LoginUserVO userInfo = null;
	
		userInfo = loginMapper.selectUserInfo(param);
		
		if(userInfo != null){ //아이디가 있을경우
			
			
			param.setIdCheckYn("Y");
			userInfo = loginMapper.selectUserInfo(param);  //비번까지 검색
			
			if(userInfo == null){ //비번 오류				
				
				userInfo = new LoginUserVO();
					
				userInfo.setErrorMessage("비밀번호를 확인해주세요.");
				userInfo.setReturnUrl(RETURN_LOGINPAGE);
			}		
			
		}else{  //아이디 오류
			
			userInfo = new LoginUserVO();
			
			userInfo.setErrorMessage("아이디를 확인해주세요.");
			userInfo.setReturnUrl(RETURN_LOGINPAGE);
		}
		
		return userInfo;
	
	}
}
