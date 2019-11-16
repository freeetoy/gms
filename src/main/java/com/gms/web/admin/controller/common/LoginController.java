package com.gms.web.admin.controller.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class LoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value="/login.do")
	public String loginByGet(Model model,HttpServletRequest req){
		model.addAttribute("message",req.getServletContext());
		return "gms/login";
	}
}
