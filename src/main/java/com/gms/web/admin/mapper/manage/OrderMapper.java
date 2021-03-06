package com.gms.web.admin.mapper.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;

@Mapper
public interface OrderMapper {

	public List<OrderVO> selectOrderList(Map<String, Object> map);	
	
	public List<OrderVO> selectOrderListToExcel(Map<String, Object> map);	

	public List<OrderProductVO> selectOrderProductList(Integer orderId);	

	public int selectOrderCount(Map<String, Object> map);	

	public List<OrderVO> selectCustomerOrderList(Integer customerId);	
	
	public List<OrderVO> selectSalesOrderList(String customerId);	
	
	public OrderVO selectOrderDetail(Integer orderId) ;		
	
	public int selectOrderId() ;

	public int insertOrder(OrderVO param);

	public int insertOrderProduct(OrderProductVO param);
	
	public int insertOrderProducts(List<OrderProductVO>  param);
	
	public int updateOrder(OrderVO param);
	
	public int updateOrderDeposit(OrderVO param);

	public int updateOrderProduct(OrderProductVO param);
	
	public int updateOrderBottleId(OrderProductVO param);
	
	public int updateOrderProcessCd(OrderVO param);

	public int deleteOrder(OrderVO param);

	public int deleteOrderProduct(OrderProductVO param);
	
	public int deleteOrderProducts(Integer orderId);

}
