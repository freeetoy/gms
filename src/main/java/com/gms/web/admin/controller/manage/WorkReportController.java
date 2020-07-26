package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottlesVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.service.manage.BottleService;
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
	
	@Autowired
	private BottleService bottleService;
	
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
	
	
	
	@RequestMapping(value = "/gms/report/listAll.do")
	public ModelAndView getWorkReportListAll(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		logger.debug("WorkReportController getWorkReportListAll");
		
		RequestUtils.initUserPrgmInfo(request, params);				
		
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());	
		
		UserVO tempUser = new UserVO();		
		List<UserVO> userList = userService.getUserListPartNot(tempUser);
		
		mav.addObject("userList",userList);
		
		if(params.getSearchUserId() == null && userList.size() > 0) params.setSearchUserId(userList.get(0).getUserId());
		
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);		
		
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
		
		ModelAndView mav = new ModelAndView();		//검색조건 셋팅		
		int result =0;
		try {	
			
			if(params.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) ){
				List<String> list = null;				
				BottleVO bottle = new BottleVO();
				params.setBottleType("E");
				if(params.getBottlesIds()!=null && params.getBottlesIds().length() > 0) {
					//bottleIds= request.getParameter("bottleIds");
					list = StringUtils.makeForeach(params.getBottlesIds(), ","); 		
					bottle.setBottList(list);
					bottle.setBottleWorkCd(params.getBottleWorkCd());
					bottle.setBottleWorkId(params.getCreateId());
					bottle.setCustomerId(params.getCustomerId());
					bottle.setUpdateId(params.getCreateId());
					bottle.setBottleType("E");
				}			
				
				List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);
				
				params.setUserId(params.getCreateId());
				result = workService.registerWorkReportByBottle(params,bottleList);
				
				result =  bottleService.changeWorkCdsAndHistory(bottle, bottleList);
				
			}else {
				params.setBottleType("F");
				result = workService.registerWorkReportNoOrder(params);					
			}
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
		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			work.setOrderId(params.getOrderId());
			work.setBottlesIds(params.getBottleIds());
			work.setBottleWorkCd(params.getBottleWorkCd());
			work.setUserId(params.getCreateId());
			work.setCreateId(params.getCreateId());
			
			result = workService.registerWorkReport0310(work);					
		
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
		
		int result =0;
		try {	
			
			if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
					&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
					&& param.getProductCount() > 1000 ) {
				param.setProductPrice(param.getProductCount());
				param.setProductCount(1);
			}
			
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
	
	@RequestMapping(value = "/gms/report/workBottleList.do")
	@ResponseBody
	public List<WorkBottleVO> getWorkBottleList(@RequestParam(value = "workReportSeq", required = false) Integer workReportSeq, Model model)	{	
				
		logger.debug("OrderContoller getWorkBottleList "+ workReportSeq);
		
		List<WorkBottleVO> workBottleList = workService.getWorkBottleList(workReportSeq);
		//model.addAttribute("orderProductList", orderProductList);
		
		return workBottleList;
	}
	
	@RequestMapping(value = "/gms/report/update.do")
	public ModelAndView getWorkReportUpdate(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		logger.debug("WorkReportController getWorkReportUpdate");
		
		RequestUtils.initUserPrgmInfo(request, param);				
		
		ModelAndView mav = new ModelAndView();		
			
		WorkReportVO workReport = workService.getWorkReport(param.getWorkReportSeq());
		
		mav.addObject("workReport", workReport);	 	
		
		mav.setViewName("gms/report/update");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/report/modify.do")
	public ModelAndView getWorkReportModify(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		int result = 1;
		try {			
		
			logger.debug("WorkReportController getWorkReportModify");
			
			RequestUtils.initUserPrgmInfo(request, param);				
			
			ModelAndView mav = new ModelAndView();		
			logger.debug("WorkReportController getWorkReportModify param.workReportSeq =="+param.getWorkReportSeq());
			logger.debug("WorkReportController getWorkReportModify productCount=="+request.getParameter("productCount"));
			
			result = workService.modifyWorkBottleManual(request,param);
			// WorkReport 정보 변경			
			
			mav.addObject("workReportSeq", param.getWorkReportSeq());	 	
			
			mav.setViewName("gms/report/update");
			if(result > 0){
				String alertMessage = "수정되었습니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/report/update.do?workReportSeq="+param.getWorkReportSeq());
			}
		} catch (DataAccessException e) {		
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;
		
	}
}
