package com.gms.web.admin.controller.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottlesVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.UserService;
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
	
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value = "/gms/report/list.do")
	public ModelAndView getWorkReportList(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		logger.debug("WorkReportController getWorkReportList");
		
		RequestUtils.initUserPrgmInfo(request, params);				
		
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());		
		params.setSearchUserId(params.getCreateId());
		
		logger.debug("WorkReportController getWorkReportList User_id= "+ params.getUserId());		
		
		//LoginUserVO sessionInfo = SessionUtil.getSessionInfo(request);			
		
		//List<WorkReportViewVO> workList = workService.getWorkReportList1(params);
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		mav.setViewName("gms/report/list");
		
		return mav;
	}
	
	/*
	@RequestMapping(value = "/gms/report/list.do")
	public ModelAndView getWorkReportList(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		logger.debug("WorkReportController getWorkReportList");
		
		RequestUtils.initUserPrgmInfo(request, params);		
		
		
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());
		
		
		logger.debug("WorkReportController getWorkReportList User_id= "+ params.getUserId());
		
		
		LoginUserVO sessionInfo = SessionUtil.getSessionInfo(request);	
		
		
		
		List<WorkReportVO> workList = workService.getWorkReportList(params);
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		mav.setViewName("gms/report/list");
		
		return mav;
	}
	*/
	
	@RequestMapping(value = "/gms/report/listAll.do")
	public ModelAndView getWorkReportListAll(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		logger.debug("WorkReportController getWorkReportList");
		
		RequestUtils.initUserPrgmInfo(request, params);				
		
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());		
		
		logger.debug("WorkReportController getWorkReportList User_id= "+ params.getUserId());
		
		/*
		LoginUserVO sessionInfo = SessionUtil.getSessionInfo(request);
		
		if(sessionInfo.getUserAuthority().equals(PropertyFactory.getProperty("common.user.Authority.manager"))) {
			List<CustomerVO> carList = customerService.searchCustomerListCar();
			mav.addObject("carList",carList);
		}
		*/
		
		UserVO tempUser = new UserVO();		
		List<UserVO> userList = userService.getUserListPart(tempUser);
		
		mav.addObject("userList",userList);
		
		if(params.getSearchUserId() == null && userList.size() > 0) params.setSearchUserId(userList.get(0).getUserId());
		
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);
		
		logger.debug("WorkReportController getWorkReportList workList.size="+workList.size());
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());	
		mav.addObject("searchUserId", params.getSearchUserId());	
		
				
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		mav.setViewName("gms/report/listAll");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/report/register.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportForOrder(HttpServletRequest request
			, HttpServletResponse response
			, OrderBottlesVO params) {
		
		logger.debug("WorkReportController registerWorkReportForOrder");
		
		RequestUtils.initUserPrgmInfo(request, params);
		ModelAndView mav = new ModelAndView();	
		
		//검색조건 셋팅
		logger.debug("WorkReportController OrderBottleVO "+ params.getOrderId());
		logger.debug("WorkReportController bottleWorkCd "+ params.getBottleWorkCd());
		logger.debug("WorkReportController searchBottleIds "+ params.getBottleIds());
		
		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			work.setOrderId(params.getOrderId());
			work.setBottlesIds(params.getBottleIds());
			work.setBottleWorkCd(params.getBottleWorkCd());
			work.setUserId(params.getCreateId());
			work.setCreateId(params.getCreateId());
			
			result = workService.registerWorkReportForOrder(work);					

			//mav.setViewName("/gms/mypage/assign");			
		
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/mypage/assign.do");
		}
		return null;
		//return "redirect:/gms/mypage/assign.do";		
	}
	
	@RequestMapping(value = "/gms/report/registerAll.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportAll(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {
		
		logger.debug("WorkReportController registerWorkReportAll");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		ModelAndView mav = new ModelAndView();	
		//검색조건 셋팅
		
		logger.debug("WorkReportController bottleWorkCd "+ params.getBottleWorkCd());
		logger.debug("WorkReportController BottleIds "+ params.getBottlesIds());
		logger.debug("WorkReportController customerId "+ params.getCustomerId());
		
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
			String alertMessage = "처리되었습니다.";
			LoginUserVO adminLoginUserVO = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME); 
			if(adminLoginUserVO.getUserAuthority().equals(PropertyFactory.getProperty("common.user.Authority.manager")))
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/listAll.do");
			else 
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/list.do");
			
		}
		return null;
		
	}
	
	@RequestMapping(value = "/gms/report/register0310.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReport0310(HttpServletRequest request
			, HttpServletResponse response
			, OrderBottlesVO params) {
		
		logger.debug("WorkReportController registerWorkReportForOrder");
		
		RequestUtils.initUserPrgmInfo(request, params);
		ModelAndView mav = new ModelAndView();	
		
		//ModelAndView mav = new ModelAndView();	
		//검색조건 셋팅
		logger.debug("WorkReportController OrderBottleVO "+ params.getOrderId());
		logger.debug("WorkReportController bottleWorkCd "+ params.getBottleWorkCd());
		logger.debug("WorkReportController searchBottleIds "+ params.getBottleIds());
		
		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			work.setOrderId(params.getOrderId());
			work.setBottlesIds(params.getBottleIds());
			work.setBottleWorkCd(params.getBottleWorkCd());
			work.setUserId(params.getCreateId());
			work.setCreateId(params.getCreateId());
			
			result = workService.registerWorkReport0310(work);					

			//mav.setViewName("/gms/mypage/assign");			
		
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/mypage/assign.do");
		}
		return null;
		//return "redirect:/gms/mypage/assign.do";
		
	}
		
	@RequestMapping(value = "/gms/report/print.do")
	public ModelAndView getWorkReportListPrint(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		logger.debug("WorkReportController getWorkReportList");
		
		RequestUtils.initUserPrgmInfo(request, params);		
		
		ModelAndView mav = new ModelAndView();		
		
		if(params.getSearchUserId()!=null && params.getSearchUserId().length() > 1) {
			params.setUserId(params.getSearchUserId());
		}else {
			params.setUserId(params.getCreateId());
			params.setSearchUserId(params.getCreateId());
		}
		
		logger.debug("WorkReportController getWorkReportList User_id= "+ params.getUserId());		
		
		//List<WorkReportViewVO> workList = workService.getWorkReportList1(params);
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());			 
		
		mav.setViewName("gms/report/print");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/report/noGasSales.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportNoGasProduct(HttpServletRequest request
			, HttpServletResponse response
			, WorkBottleVO param) {
		
		logger.debug("WorkReportController registerWorkReportNoGasProduct");
		
		RequestUtils.initUserPrgmInfo(request, param);
		
		ModelAndView mav = new ModelAndView();	
		//검색조건 셋팅
		
		logger.debug("WorkReportController bottleWorkCd "+ param.getBottleWorkCd());
		logger.debug("WorkReportController customerId "+ param.getCustomerId());
		logger.debug("WorkReportController ProductId "+ param.getProductId());
		logger.debug("WorkReportController ProductPriceSeq "+ param.getProductPriceSeq());
		
		int result =0;
		try {	
				result =1;	
			result = workService.registerWorkNoBottle(param);					
		
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		//return "redirect:/gms/mypage/assign.do";
		
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			LoginUserVO adminLoginUserVO = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME); 
			if(adminLoginUserVO.getUserAuthority().equals(PropertyFactory.getProperty("common.user.Authority.manager")))
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/listAll.do");
			else 
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/list.do");
			
		}
		return null;
		
	}
	
}
