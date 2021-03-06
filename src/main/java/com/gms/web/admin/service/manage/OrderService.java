package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;

public interface OrderService {
	public Map<String,Object> getOrderList(OrderVO params);	
	
	public List<OrderVO> getOrderListToExcel(OrderVO params);	
	
	public List<OrderVO> getCustomerOrderList(Integer customerId);

	public List<OrderVO> getSalseOrderList(String salesId);
	
	public OrderVO getOrderDetail(Integer orderId) ;
	
	public OrderExtVO getOrder(Integer orderId) ;
	
	public List<OrderProductVO> getOrderProductList(Integer orderId);

	public int getOrderCount(Map<String, Object> map);	

	public int getOrderId() ;
	
	public int registerOrder(HttpServletRequest request,OrderVO param);	

	public int registerOrderProduct(OrderProductVO orderProduct);	
	
	public int registerOrderProducts(List<OrderProductVO> orderProduct);	
	
	public int registerOrderAndProduct(OrderVO order, List<OrderProductVO> orderProduct);	

	public int modifyOrder(HttpServletRequest request,OrderVO param);
	
	public int modifyOrderDeposit(OrderVO param);

	public int modifyOrderProduct(OrderProductVO param);
	
	public int modifyOrderBottleId(OrderProductVO param);
	
	public int modifyOrderComplete(OrderProductVO param);

	public int changeOrderProcessCd(OrderVO param);

	public int deleteOrder(OrderVO param);

	public int deleteOrderProduct(OrderProductVO param);
}
