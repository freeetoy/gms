package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.mapper.manage.CustomerMapper;
import com.gms.web.admin.mapper.manage.OrderMapper;
import com.gms.web.admin.mapper.manage.ProductMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private CustomerMapper customerMapper;
	
	@Autowired
	private ProductMapper productMapper;

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
	@Transactional
	public int registerOrder(HttpServletRequest request,OrderVO params) {
		// 정보 등록
		logger.info("****** registerBottle.getBottleId()()) *****===*");
		/*
		int result = 0;
			
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		*/
		RequestUtils.initUserPrgmInfo(request, params);		
		int result = 0;
		int result1 =0;
		//mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			int productCount  = params.getProductCount();
			
			logger.debug("OrderContoller registerOrder productCount== "+ params.getProductCount());			
		
			
			List<OrderProductVO> orderProduct = new ArrayList<OrderProductVO>();
					
			int orderId = getOrderId();
			params.setOrderId(Integer.valueOf(orderId));
			
			logger.debug("OrderContoller registerOrder orderId== "+ orderId);
			
			// 거래처 상품별 단가 정보 가져오기
			List<CustomerPriceExtVO> customerPriceList = customerMapper.selectCustomerPriceList(params.getCustomerId());			
			
			// 거래처 상품별 단가 정보 가져오기
			List<ProductTotalVO> productPriceList = productMapper.selectProductTotalList();;			
						
						
			int orderAmount = 0;
			int orderTotalAmount = 0;
			
			Integer productId =0;
			Integer productPriceSeq = 0;
			Integer orderCount = 0;
			
			String orderProductNm = "";
			String orderProductCapa = "";
			
			String bottleChangeYn = null;
			CustomerPriceExtVO tempCustomerPrice = null;
			ProductTotalVO tempProduct = null;
					
			for(int i =0 ; i < productCount ; i++ ) {
				
				OrderProductVO productVo = new OrderProductVO();
				
				RequestUtils.initUserPrgmInfo(request, productVo);
				
				productId =0;
				productPriceSeq = 0;
				orderCount = 0;
				bottleChangeYn = "N";
				
				productId = Integer.parseInt(request.getParameter("productId_"+i));
				productPriceSeq = Integer.parseInt(request.getParameter("productPriceSeq_"+i));
				orderCount = Integer.parseInt(request.getParameter("orderCount_"+i));
				if(request.getParameter("bottleChangeYn_"+i) !=null)  bottleChangeYn = "Y";
				
				
				for(int j=0;j<customerPriceList.size();j++) {
					tempCustomerPrice = customerPriceList.get(j);
					orderAmount = 0;
					if(productId == tempCustomerPrice.getProductId() && productPriceSeq == tempCustomerPrice.getProductPriceSeq()  ) {
						
						if(i==0) {
							orderProductNm = tempCustomerPrice.getProductNm();
							orderProductCapa = tempCustomerPrice.getProductCapa();
							
							logger.debug("OrderContoller registerOrder customerPriceList orderProductNm== "+ orderProductNm);
							logger.debug("OrderContoller registerOrder customerPriceList orderProductCapa== "+ orderProductCapa);
						}
						orderAmount = tempCustomerPrice.getProductPrice() *orderCount;						
						logger.debug("OrderContoller registerOrder orderAmount== "+ orderAmount);
						productVo.setOrderAmount(orderAmount);
						orderTotalAmount += orderAmount;
					}else {
						for(int k=0;k<productPriceList.size();k++) {
							tempProduct = productPriceList.get(k);
							
							if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
								
								if(i==0) {
									orderProductNm = tempProduct.getProductNm();
									orderProductCapa = tempProduct.getProductCapa();
									logger.debug("OrderContoller registerOrder productPriceList orderProductNm== "+ orderProductNm);
									logger.debug("OrderContoller registerOrder productPriceList orderProductCapa== "+ orderProductCapa);
								}
								
								orderAmount = tempProduct.getProductPrice() *orderCount;	
								logger.debug("OrderContoller registerOrder orderAmount== "+ orderAmount);
								productVo.setOrderAmount(orderAmount);
								orderTotalAmount += orderAmount;								
							}
						}						
						
					}
					
				}		
				logger.debug("OrderContoller registerOrder orderTotalAmount== "+ orderTotalAmount);
				
				productVo.setOrderId(params.getOrderId());
				productVo.setOrderProductSeq(i+1);
				productVo.setProductId(productId);
				productVo.setProductPriceSeq(productPriceSeq);				
				productVo.setOrderCount(orderCount);
				productVo.setOrderProductEtc(request.getParameter("orderProductEtc_"+i));
				productVo.setBottleChangeYn(bottleChangeYn);								
				
				orderProduct.add(productVo);
			}
			
			logger.debug("OrderContoller registerOrder orderProductNm== "+ orderProductNm);
			logger.debug("OrderContoller registerOrder orderProductCapa== "+ orderProductCapa);
			
			if(productCount > 1) {
				orderProductNm = orderProductNm +"외 "+ (productCount-1);
				orderProductCapa = orderProductCapa +"외 "+ (productCount-1);
			}
						
			result1 = orderMapper.insertOrderProducts(orderProduct);	
			
			//
			params.setOrderProductNm(orderProductNm);
			params.setOrderProductCapa(orderProductCapa);
			params.setOrderTotalAmount(orderTotalAmount);
			params.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.01"));
		
			result =  orderMapper.insertOrder(params);	
		
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		
		
		return result;
	}

	@Override
	@Transactional
	public int registerOrderProduct(OrderProductVO param) {
		// 정보 등록
		logger.info("****** registerOrderProduct()()) *****===*");
		int result = 0;
		result =  orderMapper.insertOrderProduct(param);		
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public int modifyOrder(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int modifyOrderDeposit(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	@Transactional
	public int modifyOrderProduct(OrderProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int changeOrderProcessCd(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int deleteOrder(OrderVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int deleteOrderProduct(OrderProductVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int registerOrderProducts(List<OrderProductVO> orderProduct) {
		logger.info("****** registerOrderProduct.getOrderI()) *****===*");
		int result = 0;
		result =  orderMapper.insertOrderProducts(orderProduct);		
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
	}

	@Override
	public int getOrderId() {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			result =  orderMapper.selectOrderId();	
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			result = 1;
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		if(result ==0) result = 1;
		return result;
	}


}
