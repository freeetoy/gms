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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CashSumVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.manage.CashFlowService;
import com.gms.web.admin.service.manage.CustomerService;

@Controller
public class CashFlowController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private CashFlowService cashService;
	
	@Autowired
	private CustomerService customerService;
	
	
	@RequestMapping(value = "/gms/cash/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyCashFlow(
			HttpServletRequest request
			, HttpServletResponse response
			, CashFlowVO param) {

		ModelAndView mav = new ModelAndView();		
		
		RequestUtils.initUserPrgmInfo(request, param);		
		param.setUpdateId(param.getCreateId());
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));
		
		int result = 0;
		//TODO 판매완료된 상품 주문 수정 안되게 처리
		
		result = cashService.modifyCashFlow(param);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/cash/list.do?currentPage="+param.getCurrentPage()+"&customerId="+param.getCustomerId()+"&searchCreateDt="+param.getSearchCreateDt() );
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/cash/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteCashFlow(
			HttpServletRequest request
			, HttpServletResponse response
			, CashFlowVO param) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, param);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));
		
		int result = 0;
		//TODO 판매완료된 상품 주문 수정 안되게 처리
		
		result = cashService.deleteCashFlow(param);
		if(result > 0){
			String alertMessage = "삭제되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/cash/list.do?currentPage="+param.getCurrentPage()+"&customerId="+param.getCustomerId()+"&searchCreateDt="+param.getSearchCreateDt() );
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/cash/list.do")
	public ModelAndView getCashFlowList(CashFlowVO param) {

		logger.debug("CashFlowController getCashFlowList");
		logger.debug("CashFlowController currentPage="+param.getCurrentPage());
		
		ModelAndView mav = new ModelAndView();		
				
		Map<String, Object> map = cashService.getCashFlowList(param);
		
		mav.addObject("list", map.get("list"));		
		mav.addObject("customerId", param.getCustomerId());	
	
		String searchCreateDt = param.getSearchCreateDt();	
		mav.addObject("searchCreateDt", param.getSearchCreateDt());		
		
		CashSumVO cashSum = cashService.getCashFlowSum(param);
		if(cashSum == null) {
			cashSum = new CashSumVO();
			cashSum.setIncomeAmountSum(0);
			cashSum.setReceivableAmountNet(0);
			cashSum.setReceivableAmountSum(0);
		}
		mav.addObject("cashSum", cashSum);
		
		// 주문 타입 불러오기
		Map<String, Object> map1 = customerService.searchCustomerList("");
		mav.addObject("customerList", map1.get("list"));		
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		mav.addObject("rowPerPage", param.getRowPerPage());
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));	 		
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/cash/detail.do")
	@ResponseBody
	public CashFlowVO getCashFlow(CashFlowVO param) {

		logger.debug("CashFlowController getCashFlow");
		
		return cashService.getCashFlowDetail(param);
	}

}
