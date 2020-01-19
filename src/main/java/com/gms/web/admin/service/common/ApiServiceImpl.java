package com.gms.web.admin.service.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.WorkReportMapper;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.WorkReportService;

@Service
public class ApiServiceImpl implements ApiService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private WorkReportMapper workMapper;
	
	@Autowired
	private WorkReportService workService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Override
	public int registerWorkReportForSale(WorkReportVO param) {
		// TODO Auto-generated method stub
		
		int result = 0;	
		
		//Customer 정보가져
		CustomerVO customer = getCustomer(param.getCustomerNm());
		
		if(customer!=null) {
			param.setCustomerId(customer.getCustomerId());

			result = workService.registerWorkReportNoOrder(param);
			
		}else {
			return result;
		}
		
				
		return result;
	}
	
	@Override
	public int registerWorkReportForChangeCd(WorkReportVO param) {
		
		int result = 0;
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
		
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0301") ) 
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0306"))
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0307") ) 
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310")) ){
		
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());				
			bottle.setCustomerId(customer.getCustomerId());
			param.setCustomerId(customer.getCustomerId());
		
		}else {
			if(bottleList.size() > 0 ) param.setCustomerId(bottleList.get(0).getCustomerId());
		}
		
		
		param.setUserId(param.getCreateId());
		
		result = workService.registerWorkReportByBottle(param, bottleList);
		if(result <= 0) return result;
		
		result =  bottleService.changeWorkCdsAndHistory(bottle, bottleList);
		
		return result;
	}

	
	private CustomerVO getCustomer(String customerNm) {				
		return customerService.getCustomerDetailsByNm(customerNm);

	}

	

	
}
