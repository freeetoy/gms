package com.gms.web.admin.controller.manage;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.manage.OrderService;

@Controller
public class MypageController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value = "/gms/mypage/assign.do")
	public ModelAndView getAssignList(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		logger.info("MypageContoller getAssignList");
		
		RequestUtils.initUserPrgmInfo(request, params);		
		
		
		ModelAndView mav = new ModelAndView();		
		
		List<OrderVO> orderList = orderService.getSalseOrderList(params.getCreateId());
		
		mav.addObject("orderList", orderList);	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.assign"));	 	
		
		mav.setViewName("gms/mypage/assign");
		
		return mav;
	}
}
