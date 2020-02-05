package com.gms.web.admin.controller.common;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.service.manage.WorkReportService;

@Controller
public class startController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WorkReportService workService;
	
	
	@RequestMapping(value = "/gms/start")
	public ModelAndView getStartWorkBottleList(BottleVO params) {

		logger.info("BottleContoller getWorkBottleListToday");

		ModelAndView mav = new ModelAndView();		
		
		List<BottleVO> bottleList =  workService.getWorkBottleListToday();
		
		mav.addObject("bottleList", bottleList);		
		
		mav.setViewName("gms/start");
		return mav;
	}
}
