package com.gms.web.admin.service.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CashFlowService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.UserService;
import com.gms.web.admin.service.manage.WorkReportService;

@Service
public class ApiServiceImpl implements ApiService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int CUSOTMER_NOT_EXIST = -3;
	private final int USER_NOT_EXIST = -4;


	@Autowired
	private WorkReportService workService;

	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CashFlowService cashService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public int registerWorkReportForSale(WorkReportVO param) {
		
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
		
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
	
				result = workService.registerWorkReportNoOrder(param);			
				
			}else {
				return CUSOTMER_NOT_EXIST;
			}		
		}else {
			return USER_NOT_EXIST;
		}
		return result;
	}
	
	@Override
	public int registerWorkReportForChangeCd(WorkReportVO param) {
		
		int result = 0;
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			List<String> list = null;				
			BottleVO bottle = new BottleVO();
			
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setBottleType(param.getBottleType());		
			bottle.setBottleWorkId(param.getCreateId());
			bottle.setCreateId(param.getCreateId());
			bottle.setUpdateId(param.getCreateId());
					
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
			}			
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);
			
			CustomerVO customer = getCustomer(param.getCustomerNm());		
			if(customer !=null) {
				bottle.setCustomerId(customer.getCustomerId());
				param.setCustomerId(customer.getCustomerId());
			}else {
				return CUSOTMER_NOT_EXIST;
			}
			
			param.setUserId(param.getCreateId());
			
			result = workService.registerWorkReportByBottle(param, bottleList);
			if(result <= 0) return result;
			
			result =  bottleService.changeWorkCdsAndHistory(bottle, bottleList);
		}else {
			return USER_NOT_EXIST;
		}
		return result;
	}

	
	private CustomerVO getCustomer(String customerNm) {				
		return customerService.getCustomerDetailsByNm(customerNm);
	}

	@Override
	public int registerWorkReportNoGas(WorkBottleVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getCreateId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
						&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
						&& param.getProductCount() > 1000 ) {
					param.setProductPrice(param.getProductCount());
					param.setProductCount(1);
				}
				
				result = workService.registerWorkNoBottle(param);		
			}else {
				return CUSOTMER_NOT_EXIST;
			}
		}else {
			return USER_NOT_EXIST;
		}			
		return result;
	}

	@Override
	public int registerCashFlow(CashFlowVO param) {
		int result = 0;	
		UserVO user = userService.getUserDetails(param.getCreateId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			
			if(customer!=null) {
				
				param.setCustomerId(customer.getCustomerId());
				// 수금액 정보 업데이트
				
				WorkReportVO workReport = new WorkReportVO();
				workReport.setCustomerId(customer.getCustomerId());
				workReport.setUserId(param.getCreateId());
				int workReportSeq = workService.getWorkReportSeqForCustomerToday(workReport);
				
				if(workReportSeq <= 0) {
					workReportSeq = workService.getWorkReportSeq();		
					workReport.setWorkReportSeq(workReportSeq);
					workReport.setCreateId(param.getCreateId());
					workReport.setUserId(param.getCreateId());
					workReport.setReceivedAmount(param.getIncomeAmount());
					workReport.setIncomeWay(param.getIncomeWay());
					workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.0312"));
					
					workService.registerWorkReportOnly(workReport);
					
				}else {
					workReport.setUpdateId(param.getCreateId());
					workReport.setReceivedAmount(param.getIncomeAmount());
					workReport.setIncomeWay(param.getIncomeWay());
					workReport.setWorkReportSeq(workReportSeq);
					
					result = workService.modifyWorkReportReceivedAmount(workReport);
				}			
				
				result = cashService.registerCashFlow(param);		
			}else {
				return CUSOTMER_NOT_EXIST;
			}
		}else {
			return USER_NOT_EXIST;
		}	
		return result;
	}

	@Override
	public List<SimpleBottleVO> getCustomerSimpleBottleList(String customerNm) {
		//Customer 정보가져
		CustomerVO customer = getCustomer(customerNm);
		
		if(customer!=null) {			
			return bottleService.getCustomerSimpleBottleList(customer.getCustomerId());
		}else {
			return null;
		}
	}

	@Override
	public int registerWorkReportGasAndBottle(WorkReportVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<WorkBottleVO> getWorkReportList(WorkReportVO param) {
		
		return workService.getWorkBottleListOfUser(param);
	}

	@Override
	public int registerWorkReportMassForSale(WorkReportVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {	
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
	
				result = workService.registerWorkReportMassNoOrder(param);		
				
			}else {
				return CUSOTMER_NOT_EXIST;
			}		
		}else {
			return USER_NOT_EXIST;
		}
		return result;
				
	}

	@Override
	public int registerWorkReportMassForChangeCd(WorkReportVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {		
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
	
				result = workService.registerWorkReportMassByBottle(param);		
				
			}else {
				return CUSOTMER_NOT_EXIST;
			}		
		}else {
			return USER_NOT_EXIST;
		}
		return result;						   
	}

	@Override
	public List<BottleVO> getDummyBottleList() {
		
		return bottleService.getSimpleDummyBottleList();
	}

	@Override
	public String getAppVersion() {
		
		return codeService.getAppVersion().getAppVer();
	}

	@Override
	public List<ProductPriceSimpleVO> getCustomerLn2List(String customerNm) {
		CustomerVO customer = getCustomer(customerNm);
		List<ProductPriceSimpleVO> productList= null;
		if(customer!=null) {
			
			productList = productService.getCustomerLn2List(customer.getCustomerId());	
			
		}else {
			return null;
		}				
		
		return productList;
	}


}
