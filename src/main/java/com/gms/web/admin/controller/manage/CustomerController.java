package com.gms.web.admin.controller.manage;

import java.util.HashMap;
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

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.UserService;

@Controller
public class CustomerController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * CustomerService 빈(Bean) 선언
	 */
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;
	
	
	@RequestMapping(value = "/gms/customer/list.do")
	public String openCustomerList(CustomerVO params, Model model) {
		
		Map<String, Object> map = customerService.getCustomerList(params);

		model.addAttribute("customerList", map.get("list"));
		model.addAttribute("searchUserNm", params.getSearchCustomerNm());
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));	
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		return "gms/customer/list";
	}

	
	@RequestMapping(value = "/gms/customer/write.do")
	public String openCustomerWrite(@RequestParam(value = "customerId", required = false) String customerId, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		UserVO param = new UserVO();
		param.setUserPartCd(PropertyFactory.getProperty("common.user.SALES"));
		
		Map<String, Object> map = userService.getUserList(param);
		model.addAttribute("userList", map.get("list"));
		
		//Map<String, Object> map1 = productService.getProductList();
		model.addAttribute("productList", productService.getProductList());
		
					
		return "/gms/customer/write";
	}
	
	@RequestMapping(value = "/gms/customer/register.do", method = RequestMethod.POST)
	public String registerCustomer(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, CustomerVO params) {
		logger.debug("CustomerContoller registerCustomer");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			//ID 중복체크			
			boolean result = customerService.registerCustomer(params);
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/customer/list.do";
	}

	@RequestMapping(value = "/gms/customer/update.do")
	public String openCustomerUpdate(@RequestParam(value = "customerId", required = false) Integer customerId, Model model) {

		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));		
		
		if (customerId == null) {
			return "redirect:/gms/customer/list.do";
		} else {
			
			CustomerVO customer = customerService.getCustomerDetails(customerId);
			
			if (customer == null) {
				return "redirect:/gms/customer/list.do";
			}
			
			model.addAttribute("customer", customer);
		}
		
		return "/gms/customer/update";
	}
	
	@RequestMapping(value = "/gms/customer/modify.do", method = RequestMethod.POST)
	public String modifyCustomer(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, CustomerVO params) {
		logger.info("CustomerContoller modifyCustomer");
		
		RequestUtils.initUserPrgmInfo(request, params);
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		try {			
			logger.debug("******params.getCustomerId()()) *****===*"+params.getCustomerId());
			
			boolean result = customerService.modifyCustomer(params);
			
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/customer/list.do";
	}
	
	@RequestMapping(value = "/gms/customer/detail.do")
	@ResponseBody
	public CustomerVO getCustomerDetails(@RequestParam(value = "customerId", required = false) Integer customerId, Model model)	{
		
		CustomerVO result = customerService.getCustomerDetails(customerId);
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		if(result != null) logger.info("******result *****===*"+result.getCustomerId());
		else logger.info("******result is null  *****===*"); 
		
		return result;
	}
	
	
	@RequestMapping(value = "/gms/customer/CheckBusiId.do")
	@ResponseBody
	public Object checkBusinessRegId(CustomerVO param){
		Map<String, Object> result = customerService.checkBusinessRegIdDuplicate(param);
		
		return result;
	}
	
	
	@RequestMapping(value = "/gms/price/write.do")
	public String openCustomerPriceWrite(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer.price"));
		
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		model.addAttribute("customerList", map.get("list"));
		
		model.addAttribute("productList", productService.getProductList());
					
		return "/gms/price/write";
	}
	
	
}
