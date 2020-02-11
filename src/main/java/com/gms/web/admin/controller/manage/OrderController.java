package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.common.CodeService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.WorkReportService;


@Controller
public class OrderController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private WorkReportService workService;
	
	@RequestMapping(value = "/gms/order/list.do")
	public ModelAndView getOrderList(OrderVO params) {

		logger.info("BottleContoller getBottleList");

		ModelAndView mav = new ModelAndView();
		
		String searchOrderDt = params.getSearchOrderDt();	
		
		String searchOrderDtFrom = null;
		String searchOrderDtEnd = null;
				
		if(searchOrderDt != null && searchOrderDt.length() > 20) {						
			searchOrderDtFrom = searchOrderDt.substring(0, 10) ;			
			searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
			
			params.setSearchOrderDtFrom(searchOrderDtFrom);
			params.setSearchOrderDtEnd(searchOrderDtEnd);			
		}
				
		Map<String, Object> map = orderService.getOrderList(params);
		
		mav.addObject("orderList", map.get("list"));			
		
		//Order_Process_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.process"));
		mav.addObject("codeList", codeList);
		//검색어 셋팅
		mav.addObject("searchOrderDt", params.getSearchOrderDt());	
		mav.addObject("searchCustomerNm", params.getSearchCustomerNm());	
		mav.addObject("searchOrderProcessCd", params.getSearchOrderProcessCd());	
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		mav.addObject("rowPerPage", params.getRowPerPage());
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));	 		
		
		mav.setViewName("gms/order/list");
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/order/monitor.do")
	public ModelAndView getWorkBottleListToday(BottleVO params) {

		logger.info("BottleContoller getWorkBottleListToday");

		ModelAndView mav = new ModelAndView();		
		
		Map<String, Object> map =  workService.getWorkBottleListTotal(params);
		
		mav.addObject("bottleList", map.get("list"));			
				
		//검색어 셋팅
		mav.addObject("searchChargeDt", params.getSearchChargeDt());	
		mav.addObject("searchCustomerNm", params.getSearchCustomerNm());	
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		mav.addObject("rowPerPage", params.getRowPerPage());		
				
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.trasnaction"));		
		
		mav.setViewName("gms/order/monitor");
		return mav;
	}
	
	@RequestMapping(value = "/gms/order/write.do")
	public ModelAndView openOrderWrite(Model model) {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
	
		// 주문 타입 불러오기
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.type"));
		mav.addObject("codeList", codeList);	
		
		// 주문 타입 불러오기
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.setViewName("gms/order/write");
		
		return mav;
					
	}

	
	@RequestMapping(value = "/gms/order/register.do", method = RequestMethod.POST)
	public ModelAndView registerOrder(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.registerOrder(request,params);
		if(result > 0){
			String alertMessage = "등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/order/list.do");
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/order/update.do")
	public ModelAndView openOrderUpdate(HttpServletRequest request
			, HttpServletResponse response,
			OrderVO params) {
		
		logger.info("OrderContoller openOrderUpate "+ params.getOrderId());
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));	
		
			
		if (params.getOrderId() == null) {
			return null;
		} else {
			
			OrderVO order = orderService.getOrderDetail(params.getOrderId());		
			
			if (order == null) {
				return null;
			}
			
			// 주문 타입 불러오기
			List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.type"));
			mav.addObject("codeList", codeList);	
			
			// 주문 타입 불러오기
			Map<String, Object> map = customerService.searchCustomerList("");
			mav.addObject("customerList", map.get("list"));
			
			mav.addObject("order", order);
			//mav.addObject("orderProductList", orderProductList);
			mav.addObject("currentPage", params.getCurrentPage());
			mav.addObject("searchCustomerNm", params.getSearchCustomerNm());
			mav.addObject("searchOrderDt", params.getSearchOrderDt());
			
		}
		mav.setViewName("gms/order/update");
		
		return mav;
					
	}
	
	@RequestMapping(value = "/gms/order/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyOrder(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.modifyOrder(request,params);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt());
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/order/modifyBottleId.do", method = RequestMethod.POST)
	public ModelAndView modifyOrderBottlId(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderProductVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.modifyOrderBottleId(params);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do");
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/order/orderProductList.do")
	@ResponseBody
	public List<OrderProductVO> getOrderProductList(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{	
				
		logger.info("OrderContoller getOrderProductList "+ orderId);
		
		List<OrderProductVO> orderProductList = orderService.getOrderProductList(orderId);
		//model.addAttribute("orderProductList", orderProductList);
		
		return orderProductList;
	}
	
	
	@RequestMapping(value = "/gms/order/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteOrder(HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {
		
		logger.info("OrderContoller deleteOrder");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		logger.debug("OrderContoller searchOrderDt "+ params.getSearchOrderDt());
		logger.debug("OrderContoller searchCustomerNm "+ params.getSearchCustomerNm());
		
		int  result = orderService.deleteOrder(params);
		
		if(result > 0){
			String alertMessage = "삭제하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt());
		}
		return null;
		
	}
	
	
	@RequestMapping(value = "/gms/order/detail.do")
	@ResponseBody
	public OrderExtVO getOrderDetail(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{
		
		OrderExtVO result = orderService.getOrder(orderId);			
		
		return result;
	}
		
	@RequestMapping(value = "/gms/order/changeProcess.do", method = RequestMethod.POST)
	public ModelAndView modifyOrderWorkCd(HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {
		
		logger.info("OrderContoller modifyOrderWorkCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		logger.debug("OrderContoller orderProcessCd=="+params.getOrderProcessCd());		
		logger.debug("OrderContoller befeor service searchCustomerNm=="+params.getSearchCustomerNm());
		
		int result = orderService.changeOrderProcessCd(params);
		logger.debug("OrderContoller after service searchCustomerNm=="+params.getSearchCustomerNm());
		if(result > 0){
			String alertMessage = "진행상태를 변경하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt());
		}
		return null;
		
	}
	
	
	@RequestMapping(value = "/gms/order/popupOrder.do")
	public String getPopupOrderDetail(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{
		
		logger.info("OrderContoller getPopupOrderDetail");
		
		OrderExtVO result = orderService.getOrder(orderId);			
		
		model.addAttribute("orderExt", result);
		
		model.addAttribute("orderTotalAmountHan",StringUtils.numberToHan(String.valueOf(result.getOrder().getOrderTotalAmount())));
		
		//logger.debug("OrderContoller getPopupOrderDetail Money 1024 "+StringUtils.numberToHan("1024"));
		CustomerVO customer = customerService.getCustomerDetails(result.getOrder().getCustomerId());			
		
		model.addAttribute("customer", customer);
		
		return "gms/order/popupOrder";
	}
	
}
