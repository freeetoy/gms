package com.gms.web.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
	
	@RequestMapping(value = "/index.do")
	public String openBoardWrite(Model model) {

		return "gms/index";
	}


}
