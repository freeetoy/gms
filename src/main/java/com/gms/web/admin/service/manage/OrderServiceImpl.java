package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.mapper.manage.BottleMapper;
import com.gms.web.admin.mapper.manage.OrderMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderMapper orderMapper;

	@Override
	public Map<String, Object> getOrderList(OrderVO param) {
		logger.info("****** getOrderList *****start===*");		
		
		logger.info("****** getOrderList *****param.getRowPerPage===*" + param.getRowPerPage());
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int startPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (startPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchCustomerNm", param.getSearchCustomerNm());	
		
		
		if(param.getSearchOrderDt() != null) {
			map.put("searchOrderDt", param.getSearchOrderDt());
			logger.info("****** getOrderList *****getSearchOrderDt===*"+param.getSearchOrderDt());
		}		
		
		if(param.getSearchOrderDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchOrderDtFrom());
			logger.info("****** getOrderList *****getSearchOrderDtFrom===*"+param.getSearchOrderDtFrom());
		}
		
		if(param.getSearchOrderDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchOrderDtEnd());
			logger.info("****** getOrderList *****getSearchOrderDtEnd===*"+param.getSearchOrderDtEnd());
		}
		
		
		
		logger.info("****** getOrderList *****currentPage===*"+currentPage);
		
		
		int orderCount = orderMapper.selectOrderCount(map);
		
		logger.info("****** getOrderList.orderCount *****===*"+orderCount);
		
		//int lastPage = (int)(Math.ceil(orderCount/ROW_PER_PAGE));
		int lastPage = (int)((double)orderCount/ROW_PER_PAGE+0.95);
		
		logger.info("****** getOrderList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<OrderVO> orderList = orderMapper.selectOrderList(map);
		
		logger.info("****** getOrderList.orderList *****===*"+orderList.size());
		logger.info("****** getOrderList.lastPageNum *****===*"+lastPageNum);
		logger.info("****** getOrderList.startPageNum *****===*"+startPageNum);
		resutlMap.put("list",  orderList);
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", orderCount);
		
		return resutlMap;
	}

	@Override
	public List<OrderVO> getCustomerOrderList(Integer customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderVO getOrderDetail(Integer orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOrderCount(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerOrder(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerOrderProduct(OrderProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int modifyOrder(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int modifyOrderDeposit(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int modifyOrderProduct(OrderProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int changeOrderProcessCd(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteOrder(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteOrderProduct(OrderProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}


}
