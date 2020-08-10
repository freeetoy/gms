package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;

public interface OrderService {
	public Map<String,Object> getOrderList(OrderVO params);	
	
	public List<OrderVO> getOrderListToExcel(OrderVO params);	
	
	public List<OrderVO> getCustomerOrderList(Integer customerId);

	public List<OrderVO> getSalseOrderList(String salesId);
	
	public OrderVO getOrderDetail(Integer orderId) ;
	
	public OrderVO getLastOrderForCustomer(Integer customerId) ;
	
	public OrderExtVO getOrder(Integer orderId) ;
	
	public OrderExtVO getOrderNotDelivery(Integer orderId) ;
	
	public List<OrderProductVO> getOrderProductList(Integer orderId);

	public int getOrderCount(Map<String, Object> map);	

	public int getOrderId() ;
	
	public int getNextOrderProductSeq(Integer orderId) ;
	
	public int registerOrder(HttpServletRequest request,OrderVO param);	
	
	public int registerOrder(OrderVO param);	

	public int registerOrderProduct(OrderProductVO orderProduct);	
	
	public int registerOrderBottle(OrderBottleVO param);	
	
	public int registerOrderBottles(List<OrderBottleVO>  param);	
	
	public int registerOrderProducts(List<OrderProductVO> orderProduct);	
	
	public int registerOrderAndProduct(OrderVO order, List<OrderProductVO> orderProduct);	

	public int modifyOrder(HttpServletRequest request,OrderVO param);
	
	public int modifyOrderAdditionBottles(OrderVO param);
	
	public int modifyOrderDeposit(OrderVO param);

	public int modifyOrderProductCount(OrderProductVO param);
	
	public int modifyOrderBottleId(OrderProductVO param);
	
	public int modifyOrderBottle(OrderBottleVO  param);
	
	public int modifyOrderBottles(List<OrderBottleVO>  param);
	
	public int modifyOrderComplete(OrderProductVO param);

	public int modifyOrderProductBottle(OrderProductVO param);
	
	//public int modifyOrderProductDeliveryDt(OrderProductVO param);
	
	public int changeOrderProcessCd(OrderVO param);
	
	public int changeOrdersProcessCd(OrderVO param);

	public int deleteOrder(OrderVO param);

	public int deleteOrderProduct(OrderProductVO param);	
	
	public OrderVO getTodayOrderForCustomer(Integer customerId) ;
	
	public int modifyOrderAmount(Integer customerId) ;
	
	public int modifyOrderAmountAll() ;
	
	public List<OrderVO> getOrderReqDtTomorrow(OrderVO param) ;
	
	public List<OrderBottleVO> getOrderBottleListOfProduct(OrderProductVO param);
	
	public int deleteOrderBottle(OrderBottleVO param);
	
	public List<OrderProductVO> getOrderProductListNotDelivery(Integer orderId);
	
	public List<OrderBottleVO> getOrderBottleListNotDelivery(Integer orderId);

}
