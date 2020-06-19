package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;

public interface CustomerService {

	public Map<String,Object> getCustomerList(CustomerVO param);
	
	public Map<String,Object> searchCustomerList(String param);
	
	public List<CustomerVO> searchCustomerListExcel(String param);
	
	public List<CustomerVO> searchCustomerListCar();
	
	public List<CustomerSimpleVO> searchCustomerSimpleList(String customerNm);
	
	public List<CustomerSimpleVO> getAgencyCustomerList();
	
	public String searchCustomerSimpleListString(String customerNm);

	public List<CustomerSimpleVO> getCarSimpleList();
	
	public CustomerVO getCustomerDetails(Integer customerId);
	
	public CustomerVO getCustomerDetailsByNm(String customerNm);
	
	public CustomerVO getCustomerDetailsByNmBusi(CustomerVO param);

	public boolean registerCustomer(CustomerVO param);	
	
	public int registerCustomers(List<CustomerVO> param);	
	
	public boolean modifyCustomer(CustomerVO param);
	
	public boolean modifyCustomerExcel(CustomerVO param);
	
	public boolean modifyCustomerStatus(CustomerVO param);
	
	public int deleteCustomer(CustomerVO param);
	
	public int modifyCustomerBottleCount(CustomerVO param);

	public Map<String, Object> checkBusinessRegIdDuplicate(CustomerVO param);
	
	public List<CustomerPriceExtVO>  getCustomerPriceList(Integer customerId);
	
	public boolean registerCustomerPrice(CustomerPriceVO[] param);
	
	public int registerCustomerPrices(List<CustomerPriceVO> param);	
	
	public boolean deleteCustomerPrice(Integer customerId);
	
	public int deleteCustomerPrices(List<CustomerPriceVO> param);
	
	public List<CustomerPriceVO>  getCustomerPriceListAll();
	
	public int modifyCustomerPrice(CustomerPriceVO param);
}
