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
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerBottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.service.manage.BottleService;
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
	private BottleService bottleService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;
	
	
	@RequestMapping(value = "/gms/customer/list.do")
	public String openCustomerList(CustomerVO params, Model model) {
		
		Map<String, Object> map = customerService.getCustomerList(params);

		model.addAttribute("customerList", map.get("list"));
		model.addAttribute("searchCustomerNm", params.getSearchCustomerNm());
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));	
		model.addAttribute("rowPerPage", params.getRowPerPage());
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
		
					
		return "gms/customer/write";
	}
	
	@RequestMapping(value = "/gms/customer/register.do", method = RequestMethod.POST)
	public String registerCustomer(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, CustomerVO params) {
		logger.info("CustomerContoller registerCustomer");
		
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
	public String openCustomerUpdate(@RequestParam(value = "customerId", required = false) Integer customerId, 
			HttpServletRequest request
			, HttpServletResponse response
			,Model model) {

		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));		
		model.addAttribute("customerId", customerId);
		model.addAttribute("searchCustomerNm", request.getParameter("searchCustomerNm"));
		if(request.getParameter("currentPage")!=null)
			model.addAttribute("currentPage",request.getParameter("currentPage"));
		else
			model.addAttribute("currentPage","1");
		
		String old_url = request.getHeader("referer");
		logger.debug(" 리스트 ======> "+old_url);
		
		if (customerId == null) {
			return "redirect:/gms/customer/list.do";
		} else {
			
			CustomerVO customer = customerService.getCustomerDetails(customerId);
						
			if (customer == null) {
				return "redirect:/gms/customer/list.do";
			}
			
			model.addAttribute("customer", customer);
			
			UserVO param = new UserVO();
			param.setUserPartCd(PropertyFactory.getProperty("common.user.SALES"));
			
			Map<String, Object> map = userService.getUserList(param);
			model.addAttribute("userList", map.get("list"));			
			
			//Customer_Product 목록
			List<CustomerProductVO> productList = customerService.getCustomerProductList(customerId);
			
			List<CustomerProductVO> ownBottleList = new ArrayList<CustomerProductVO>();
			List<CustomerProductVO> rentBottleList = new ArrayList<CustomerProductVO>();
			
			for(int i =0 ; i < productList.size() ; i++) {
				CustomerProductVO customerProduct = productList.get(i);
				
				if(customerProduct.getBottleOwnCount() != 0) {
					ownBottleList.add(customerProduct);
				}
				if(customerProduct.getBottleRentCount() != 0) {
					rentBottleList.add(customerProduct);
				}
			}
			model.addAttribute("ownBottleList", ownBottleList);	
			model.addAttribute("rentBottleList", rentBottleList);				
			
			BottleVO bottle = new BottleVO();
			bottle.setCustomerId(customerId);
			//bottle.
			String searchChargeDt = request.getParameter("searchChargeDt");		
			
			logger.debug(" searchChargeDt ======> "+searchChargeDt);
			String searchChargeDtFrom = null;
			String searchChargeDtEnd = null;
			boolean firstFlag = false;		
			
			if(searchChargeDt != null && searchChargeDt.length() > 20) {
				bottle.setSearchChargeDt(searchChargeDt);		
				
				searchChargeDtFrom = searchChargeDt.substring(0, 10) ;				
				searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;				
				
			}else {
				firstFlag = true;
				searchChargeDtFrom = DateUtils.getDate("yyyy/MM/dd");				
				searchChargeDtEnd = DateUtils.getDate("yyyy/MM/dd");
				
				searchChargeDt = searchChargeDtFrom+" - "+searchChargeDtEnd;
				
				bottle.setSearchChargeDt(searchChargeDt);				
			}
			bottle.setSearchChargeDtFrom(searchChargeDtFrom);
			bottle.setSearchChargeDtEnd(searchChargeDtEnd);
			
			model.addAttribute("searchChargeDt",searchChargeDt);
			
			List<BottleVO> bottleList = bottleService.getCustomerBottleListDate(bottle);
			if(bottleList.size()==0 && firstFlag) {
				BottleVO recentBottle = bottleService.getCustomerBottleRecent(customerId);
				if(recentBottle !=null) {
					bottle.setSearchChargeDtFrom(DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd"));
					bottle.setSearchChargeDtEnd(DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd"));		
					
					searchChargeDt = DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd")+" - "+DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd");
					bottle.setSearchChargeDt(searchChargeDt); 
					
					bottleList = bottleService.getCustomerBottleListDate(bottle);
					model.addAttribute("searchChargeDt",searchChargeDt);
					
				}
			}
			
			model.addAttribute("bottleList", bottleList);		
		}
		
		return "gms/customer/update";
	}
	
	@RequestMapping(value = "/gms/customer/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyCustomer(HttpServletRequest request
			, HttpServletResponse response
			, CustomerVO params) {
		
		ModelAndView mav = new ModelAndView();	
		
		logger.info("CustomerContoller modifyCustomer");
		
		RequestUtils.initUserPrgmInfo(request, params);
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		String searchCustomerNm = params.getSearchCustomerNm();
		boolean result = false;
		try {			
			logger.debug("******params.getCustomerId()()) *****===*"+params.getCustomerId());
			mav.addObject("currentPage", params.getCurrentPage());
			mav.addObject("searchCustomerNm", params.getSearchCustomerNm());
			result = customerService.modifyCustomer(params);
			
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
		
		if(result){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/customer/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm);
		}
		return null;
		//return "/gms/customer/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm;
	}
	
	
	@RequestMapping(value = "/gms/customer/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteCustomer(HttpServletRequest request
			, HttpServletResponse response
			, CustomerVO param) {		
		
		logger.info("CustomerContoller deleteCustomer");
		
		ModelAndView mav = new ModelAndView();	
		RequestUtils.initUserPrgmInfo(request, param);		
		
		String searchCustomerNm = param.getSearchCustomerNm();
		
		int result = 0;
		try {			
			logger.debug("******params.getCustomerId()()) *****===*"+param.getCustomerId());
			mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer"));
			mav.addObject("currentPage", param.getCurrentPage());
			mav.addObject("searchCustomerNm", param.getSearchCustomerNm());
			/*
			if(bottleService.getCustomerBottleList(param.getCustomerId()).size() > 0 ) 
				result = -1;
			else */
			result = customerService.deleteCustomer(param);			
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		
		if(result > 0){
			String alertMessage = "삭제되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/customer/list.do?currentPage="+param.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm);
		/*
		}else if(result == -1) {
			String alertMessage = "삭제되었습니다.";
			http://27.96.134.138/gms/customer/update.do?customerId=5393
			RequestUtils.responseWriteException(response, alertMessage, "/gms/customer/list.do?currentPage="+param.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm);
		*/
		}
		return null;
		//return "/gms/customer/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm;
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
	public ModelAndView openCustomerPriceWrite(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model) {
		
		ModelAndView mav = new ModelAndView();
		//model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer.price"));
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer.price"));	
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		//model.addAttribute("customerList", map.get("list"));
		mav.addObject("customerList", map.get("list"));	
		//model.addAttribute("productList", productService.getProductList());
		
					
		//return "/gms/price/write";
		mav.setViewName("gms/price/write");
		return mav;
	}
	
	@RequestMapping(value = "/gms/price/register.do", method = RequestMethod.POST)
	public String registerCustomerPrice(HttpServletRequest request
			, HttpServletResponse response
			, Model model) {
		
		logger.info("CustomerContoller registerCustomerPrice");	
		
		try {
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));		
			int priceCount  = Integer.parseInt(request.getParameter("priceCount"));
			
			boolean result=false;
			
			logger.debug("CustomerContoller registerCustomerPrice priceCnt== "+ priceCount);
			logger.debug("CustomerContoller registerCustomerPrice request.getParameter(\"customerId1\"== "+ request.getParameter("customerId1"));
			
			//result = customerService.deleteCustomerPrice(Integer.parseInt(request.getParameter("customerId1")));
			CustomerPriceVO[] customerPrice = new CustomerPriceVO[priceCount];
			
			for(int i =0 ; i < priceCount ; i++ ) {
				
				CustomerPriceVO priceVo = new CustomerPriceVO();
				
				RequestUtils.initUserPrgmInfo(request, priceVo);
				result = false;			
				
				priceVo.setCustomerId(Integer.parseInt(request.getParameter("customerId1")));
				priceVo.setProductId(Integer.parseInt(request.getParameter("productId_"+i)));
				priceVo.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
				priceVo.setProductPrice(Integer.parseInt(request.getParameter("productPrice_"+i)));
				priceVo.setProductBottlePrice(Integer.parseInt(request.getParameter("productBottlePrice_"+i)));
					
				customerPrice[i] = priceVo;				
			}
			
			result = customerService.registerCustomerPrice(customerPrice);
			
			// 당일 거래 내역 업데이트
			
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
	
		return "redirect:/gms/price/write.do";
		//return null;
	}
	
	
	@RequestMapping(value = "/gms/cbottle/write.do")
	public ModelAndView openCustomerBottleWrite(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model) {
		
		ModelAndView mav = new ModelAndView();
		//model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer.price"));
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer.price"));	
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		//model.addAttribute("customerList", map.get("list"));
		mav.addObject("customerList", map.get("list"));	
		//model.addAttribute("productList", productService.getProductList());		
					
		UserVO user = new UserVO();
		user.setUserPartCd(PropertyFactory.getProperty("common.user.part.sales"));
		List<UserVO> userList = userService.getUserListPart(user) ;
		//Map<String, Object> map1 = userService.getUserList(user);
		mav.addObject("userList", userList);			
		
		mav.setViewName("gms/cbottle/write");
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/cbottle/register.do", method = RequestMethod.POST)
	public String registerCustomerBottle(HttpServletRequest request
			, HttpServletResponse response
			, Model model) {
		
		logger.info("CustomerContoller registerCustomerBottle");	
		
		try {
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));		
			int priceCount  = Integer.parseInt(request.getParameter("priceCount"));
			
			int result=0;

			logger.debug("CustomerContoller registerCustomerPrice request.getParameter(\"customerId1\"== "+ request.getParameter("customerId1"));
			
			//result = customerService.deleteCustomerPrice(Integer.parseInt(request.getParameter("customerId1")));
			//CustomerBottleVO[] customerBottle = new CustomerBottleVO[priceCount];

			List<CustomerBottleVO> cBottleList = new ArrayList<CustomerBottleVO>();	
			
			for(int i =0 ; i < priceCount ; i++ ) {
				
				CustomerBottleVO cBottle = new CustomerBottleVO();
				
				RequestUtils.initUserPrgmInfo(request, cBottle);					
				
				cBottle.setCustomerId(Integer.parseInt(request.getParameter("customerId1")));
				cBottle.setProductId(Integer.parseInt(request.getParameter("productId_"+i)));
				cBottle.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
				cBottle.setRentCount(Integer.parseInt(request.getParameter("rentCount_"+i)));
				cBottle.setBackCount(Integer.parseInt(request.getParameter("backCount_"+i)));
				cBottle.setSalesId(request.getParameter("salesId_"+i));
				
				cBottleList.add(cBottle);				
			}
			
			result = customerService.registerCustomerBottles(cBottleList);
			//ID 중복체크			
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/cbottle/write.do";
		//return null;
	}
	
	@RequestMapping(value = "/gms/price/customerList.do")
	@ResponseBody
	public List<CustomerPriceExtVO> getCustomerProductPriceList(@RequestParam(value = "customerId", required = false) Integer customerId, Model model)	{	
		
		List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(customerId);
		model.addAttribute("customerPriceList", customerPriceList);
		
		return customerPriceList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/common/customerList.do")
	@ResponseBody
	public List<CustomerVO> getCustomerList(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model)	{	
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		//model.addAttribute("customerList", map.get("list"));
		List<CustomerVO> customerPriceList = (List<CustomerVO>) map.get("list");
		return customerPriceList;
		//return null;
	}
	
	@RequestMapping(value = "/api/customerList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getCustomerList(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm)	{	
				
		List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList(searchCustomerNm);
		//String customerList = customerService.searchCustomerSimpleListString(searchCustomerNm);
		
		return customerList;
		//return null;
	}
	
	
	
	@RequestMapping(value = "/api/carList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getCarList()	{
		logger.info("CustomerContoller getCarList");
				
		List<CustomerSimpleVO> customerList = customerService.getCarSimpleList();
		return customerList;
		//return null;
	}
	
	@RequestMapping(value = "/api/customerAllList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getCustomerAllList()	{	
				
		logger.info("CustomerContoller getCustomerAllList");
		
		List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList("");
		return customerList;
		//return null;
	}
	
	@RequestMapping(value = "/api/customerAllList1.do")
	@ResponseBody
	public String getCustomerListString(@RequestParam(value = "searchCustomerNm", required = false)  String searchCustomerNm)	{	
		
		logger.info("CustomerContoller getCustomerListString");
						
		String customerList = customerService.searchCustomerSimpleListString(searchCustomerNm);
		
		return customerList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/cbottle/cBottleList.do")
	@ResponseBody
	public List<CustomerBottleVO> getCustomerBottleList(@RequestParam(value = "customerId", required = false) Integer customerId)	{			
						
		return customerService.getCustomerBottleList(customerId);

	}
	
}
