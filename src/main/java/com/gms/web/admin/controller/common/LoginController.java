package com.gms.web.admin.controller.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.CryptoUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.service.common.LoginService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.UserService;


@Controller
public class LoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String RETURN_LOGINPAGE = "/login";
	
	private static final String AFTER_LOGIN_PAGE = "start";
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	
		
	@RequestMapping(value="/login")
	public String loginByGet(Model model,HttpServletRequest req){
		model.addAttribute("message",req.getServletContext());
		
		HttpSession session = req.getSession();
		
		LoginUserVO sessionInfo = SessionUtil.getSessionInfo(req);
		if(sessionInfo!=null) return "redirect:/gms/start";
		//req.getSession().invalidate();
		else return "gms/login";
	}
	/*
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
			user.setUserId(param.getUserId());
			session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, user);		
			
			session.setAttribute("userId", user.getUserId());		
		}
			
		
		if(user != null && user.getUserId() != null){
			String alertMessage = "로그인 되었습니다.";
			session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, user);		
			
			RequestUtils.responseWriteException(response, alertMessage, "/gms/order/list.do");
		}else {
			String alertMessage = user.getErrorMessage();
			RequestUtils.responseWriteException(response, alertMessage, RETURN_LOGINPAGE);
		}
		
		return null;
		
	}
	*/
	
	@RequestMapping(value="/loginAction.do")
	public ModelAndView loginAction(Model model,
			HttpServletRequest request
			, HttpServletResponse response
			, LoginUserVO param) {
		
		logger.info("LoginContoller loginAction Start");
		
		ModelAndView mav = new ModelAndView();		
		
		UserVO user = userService.getUserDetails(param.getUserId());
		String alertMessage = "로그인 되었습니다.";
		
		if(user != null) {
		
			LoginUserVO loginUser = loginService.getUserInfo(param);
			
			HttpSession session = request.getSession();
			
			if(loginUser!= null && loginUser.getUserId() != null) {
				logger.debug("LoginContoller loginAction userNm "+loginUser.getUserNm());			
				//logger.debug("LoginContoller loginAction userAuthoriy "+user.getUserAuthority());
				//logger.debug("LoginContoller loginAction userPart "+user.getUserPartCd());
				loginUser.setUserId(param.getUserId());
				session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, loginUser);		
				
				session.setAttribute("userId", loginUser.getUserId());		
				
				RequestUtils.responseWriteException(response, alertMessage, "/gms/start");
			}else {
				alertMessage = "비밀번호를 확인해주세요";			
				
				RequestUtils.responseWriteException(response, alertMessage, "/login");
			}
		}else {
			alertMessage = "사용자 정보가 없습니다. 아이디를 확인해주세요";			
			
			RequestUtils.responseWriteException(response, alertMessage, "/login");
		}
			/*
		
		if(user != null && user.getUserId() != null){
			String alertMessage = "로그인 되었습니다.";
			session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, user);		
			
			RequestUtils.responseWriteException(response, alertMessage, "/gms/order/list.do");
		}else {
			String alertMessage = user.getErrorMessage();
			RequestUtils.responseWriteException(response, alertMessage, RETURN_LOGINPAGE);
		}
		*/
		return null;
		//return "redirect:/gms/start"; //gms/order/list.do";
		
	}
	@RequestMapping(value="/api/loginAction.do")
	@ResponseBody
	public LoginUserVO apiLoginAction(
			HttpServletRequest request
			, HttpServletResponse response
			, String id, String pw) throws UnsupportedEncodingException {
		String result = "";
		boolean res=false;
		ModelAndView mav = new ModelAndView();		
		logger.info("LoginContoller /api/loginAction Start");			
			
		logger.debug("LoginContoller loginAction id "+id);
		logger.debug("LoginContoller loginAction pw "+pw);
		
		LoginUserVO param = new LoginUserVO();
		param.setUserId(id);
		param.setUserPasswd(pw);
			
		LoginUserVO user = loginService.getUserInfo(param);			
		
		logger.debug("LoginContoller loginAction user.getMessage "+user.getErrorMessage());
		
		user.setUserNm(URLEncoder.encode(user.getUserNm(), "UTF-8"));
		if(user != null && user.getUserId() != null){
			result = "success";
			res = true;
			user.setSuccess(res);
			
		}else {
			result = "fail";
			res = false;
			user.setSuccess(res);
		}
		return user;
		
	}
	
	@RequestMapping(value="/logout")
	public String logout(Model model,HttpServletRequest req){
		model.addAttribute("message",req.getServletContext());
		
		req.getSession().invalidate();
		
		return "gms/login";
	}
	
}
