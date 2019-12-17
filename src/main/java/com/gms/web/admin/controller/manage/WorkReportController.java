package com.gms.web.admin.controller.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.manage.WorkReportService;

import groovyjarjarpicocli.CommandLine.Model;

@Controller
public class WorkReportController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private WorkReportService workService;
	
	
	@RequestMapping(value = "/gms/report/list.do")
	public ModelAndView getWorkReportList(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		logger.info("WorkReportController getWorkReportList");
		
		RequestUtils.initUserPrgmInfo(request, params);		
		
		
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());
		
		
		logger.info("WorkReportController getWorkReportList User_id= "+ params.getUserId());
		
		
		List<WorkReportVO> workList = workService.getWorkReportList(params);
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		mav.setViewName("gms/report/list");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/report/register.do", method = RequestMethod.POST)
	public String registerWorkReport(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, OrderBottleVO params) {
		
		logger.info("WorkReportController registerWorkReport");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//ModelAndView mav = new ModelAndView();	
		//검색조건 셋팅
		logger.info("WorkReportController OrderBottleVO "+ params.getOrderId());
		logger.info("WorkReportController bottleWorkCd "+ params.getBottleWorkCd());
		logger.info("WorkReportController searchBottleIds "+ params.getBottleIds());
		
		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			work.setOrderId(params.getOrderId());
			work.setBottlesIds(params.getBottleIds());
			work.setBottleWorkCd(params.getBottleWorkCd());
			
			result = workService.registerWorkReport(work);					

			//mav.setViewName("/gms/mypage/assign");			
		
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		return "redirect:/gms/mypage/assign.do";
		
	}
	
	@RequestMapping(value = "/gms/report/registerAll.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportAll(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {
		
		logger.info("WorkReportController registerWorkReportAll");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		ModelAndView mav = new ModelAndView();	
		//검색조건 셋팅
		
		logger.info("WorkReportController bottleWorkCd "+ params.getBottleWorkCd());
		logger.info("WorkReportController BottleIds "+ params.getBottlesIds());
		logger.info("WorkReportController customerId "+ params.getCustomerId());
		
		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			//work.setOrderId(params.getOrderId());
			//work.setBottlesIds(params.getBottleIds());
			//work.setBottleWorkCd(params.getBottleWorkCd());
			
			result = workService.registerWorkReportNoOrder(params);					

			//mav.setViewName("/gms/mypage/assign");			
		
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		//return "redirect:/gms/mypage/assign.do";
		
		if(result > 0){
			String alertMessage = "등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/report/list.do");
		}
		return null;
		
	}
	
}
