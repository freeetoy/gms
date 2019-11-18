package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerVO;

@Mapper
public interface CustomerMapper {
	
	public List<CustomerVO> selectCustomerList(Map<String, Object> map);	
	
	public List<CustomerVO> searchCustomerList(String searchCustomerNm);
	
	public int selectCustomerCount(Map<String, Object> map);	
	
	public int selectBusinessRegId(CustomerVO param);	
	
	public CustomerVO selectCustomerDetail(Integer customerId) ;

	public int insertCustomer(CustomerVO param);
	
	public int updateCustomer(CustomerVO param);
	
	public int updateCustomerStatus(CustomerVO param);

	public int deleteCustomer(Integer customerId);	
		
	public int insertCustomerPrice(CustomerPriceVO param);
	
	public int deleteCustomerPrice(Integer customerId);	
	
	public List<CustomerPriceExtVO> selectCustomerPriceList(Integer customerId);

}
