package com.gms.web.admin.service.manage;

import java.util.Map;

import com.gms.web.admin.domain.manage.CustomerVO;

public interface CustomerService {

	public Map<String,Object> getCustomerList(CustomerVO param);
	
	public Map<String,Object> searchCustomerList(String param);

	public CustomerVO getCustomerDetails(Integer customerId);

	public boolean registerCustomer(CustomerVO param);
	
	public boolean modifyCustomer(CustomerVO param);
	
	public boolean modifyCustomerStatus(CustomerVO param);
	
	public boolean deleteCustomer(Integer customerId);

	public Map<String, Object> checkBusinessRegIdDuplicate(CustomerVO param);
}
