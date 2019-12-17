package com.gms.web.admin.controller.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.CryptoUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.common.LoginService;
import com.gms.web.admin.service.manage.OrderService;


@Controller
public class LoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String RETURN_LOGINPAGE = "/login";
	
	@Autowired
	private LoginService loginService;
	
		
	@RequestMapping(value="/login")
	public String loginByGet(Model model,HttpServletRequest req){
		model.addAttribute("message",req.getServletContext());
		
		//req.getSession().invalidate();
		return "gms/login";
	}
	
	@RequestMapping(value="/loginAction.do")
	public ModelAndView loginAction(
			HttpServletRequest request
			, HttpServletResponse response
			, LoginUserVO param) {
		
		logger.info("LoginContoller loginAction Start");
		
		ModelAndView mav = new ModelAndView();				
		
		LoginUserVO user = loginService.getUserInfo(param);
		
		HttpSession session = request.getSession();
		
		if(user!= null) {
			logger.info("LoginContoller loginAction userNm "+user.getUserNm());
			
			logger.info("LoginContoller loginAction userAuthoriy "+user.getUserAuthority());
			logger.info("LoginContoller loginAction userPart "+user.getUserPartCd());
			
			session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, user);		
			
		}
			
		
		if(user != null && user.getUserId() != null){
			String alertMessage = "로그인 되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/order/list.do");
		}else {
			String alertMessage = user.getErrorMessage();
			RequestUtils.responseWriteException(response, alertMessage, RETURN_LOGINPAGE);
		}
		
		return null;
		
	}
	
	@RequestMapping(value="/logout")
	public String logout(Model model,HttpServletRequest req){
		model.addAttribute("message",req.getServletContext());
		
		req.getSession().invalidate();
		
		return "gms/login";
	}
	
}
