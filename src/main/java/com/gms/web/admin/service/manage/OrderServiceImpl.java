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
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.mapper.manage.OrderMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	

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
	public List<OrderVO> getOrderListToExcel(OrderVO param) {
		logger.info("****** getOrderListToExcel *****start===*");		
		
					
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("searchCustomerNm", param.getSearchCustomerNm());	
		if(param.getSearchOrderDt() != null) {
			map.put("searchOrderDt", param.getSearchOrderDt());
			logger.info("****** getOrderListToExcel *****getSearchOrderDt===*"+param.getSearchOrderDt());
		}		
		
		if(param.getSearchOrderDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchOrderDtFrom());
			logger.info("****** getOrderListToExcel *****getSearchOrderDtFrom===*"+param.getSearchOrderDtFrom());
		}
		
		if(param.getSearchOrderDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchOrderDtEnd());
			logger.info("****** getOrderListToExcel *****getSearchOrderDtEnd===*"+param.getSearchOrderDtEnd());
		}	
				
		List<OrderVO> orderList = orderMapper.selectOrderListToExcel(map);
		
			
		return orderList;
	}


	
	@Override
	public List<OrderVO> getCustomerOrderList(Integer customerId) {
		// TODO Auto-generated method stub
		return orderMapper.selectCustomerOrderList(customerId);
	}
	
	@Override
	public List<OrderVO> getSalseOrderList(String salesId) {
		// TODO Auto-generated method stub
		return orderMapper.selectSalesOrderList(salesId);
	}


	@Override
	public OrderVO getOrderDetail(Integer orderId) {
		return orderMapper.selectOrderDetail(orderId);	
	}

	@Override
	public List<OrderProductVO> getOrderProductList(Integer orderId) {
		return orderMapper.selectOrderProductList(orderId);	
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
		logger.info("****** registerOrder Start *****===*");
		
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
			
			String orderTypeCd = params.getOrderTypeCd();
						
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
			
			
			// Sales ID 설정
			CustomerVO customer = customerService.getCustomerDetails(params.getCustomerId());
			
			params.setSalesId(customer.getSalesId());
		
		
			if(orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.01")) ||  orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.02"))
					|| orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.03"))) {
			
				// 거래처 상품별 단가 정보 가져오기
				//List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(params.getCustomerId());			
				
				// 거래처 상품별 단가 정보 가져오기
				List<ProductTotalVO> productPriceList = productService.getCustomerProductTotalList(params.getCustomerId());
						
				
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
									
					
					for(int k=0;k<productPriceList.size();k++) {
						orderAmount = 0;
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
				
				params.setOrderProductNm(orderProductNm);
				params.setOrderProductCapa(orderProductCapa);
				params.setOrderTotalAmount(orderTotalAmount);
			}
			//
			
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
	public int modifyOrder(HttpServletRequest request, OrderVO params) {
		// 정보 등록
		logger.info("****** modifyOrder Start *****===*");
		
		RequestUtils.initUserPrgmInfo(request, params);		
		int result = 0;
		int result1 =0;
		//mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			int productCount  = params.getProductCount();
			
			logger.debug("OrderContoller modifyOrder orderId== "+ params.getOrderId());				
			logger.debug("OrderContoller modifyOrder productCount== "+ params.getProductCount());	
			
			List<OrderProductVO> orderProduct = new ArrayList<OrderProductVO>();
					
			Integer orderId = params.getOrderId();
			//params.setOrderId(Integer.valueOf(orderId));
			
			logger.debug("OrderContoller modifyOrder orderId== "+ orderId);
			
			String orderTypeCd = params.getOrderTypeCd();
						
			int orderAmount = 0;
			int orderTotalAmount = 0;
			
			Integer productId =0;
			Integer productPriceSeq = 0;
			Integer orderCount = 0;
			
			String orderProductNm = "";
			String orderProductCapa = "";			
			String bottleChangeYn = "N";
			
			CustomerPriceExtVO tempCustomerPrice = null;
			ProductTotalVO tempProduct = null;
		
			logger.debug("OrderContoller modifyOrder orderTypeCd== "+ orderTypeCd);
			
			// Sales ID 설정
			CustomerVO customer = customerService.getCustomerDetails(params.getCustomerId());
			
			params.setSalesId(customer.getSalesId());
			
			if(orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.01")) ||  orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.02"))
					|| orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.03"))) {
				
				// 거래처 상품별 단가 정보 가져오기
				//List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(params.getCustomerId());		
				
				// 상품별 단가 정보 가져오기
				List<ProductTotalVO> productPriceList = productService.getCustomerProductTotalList(params.getCustomerId());
								
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
										
					if(request.getParameter("bottleChangeYn_"+i) !=null)  {
						bottleChangeYn = "Y";
					}
					
					//logger.debug("OrderContoller modifyOrder customerPriceList.size() = "+ customerPriceList.size());		
												
						
					for(int k=0;k<productPriceList.size();k++) {
						orderAmount = 0;
						tempProduct = productPriceList.get(k);
						
						if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
							
							if(i==0) {
								orderProductNm = tempProduct.getProductNm();
								orderProductCapa = tempProduct.getProductCapa();
								logger.debug("OrderContoller modifyOrder productPriceList orderProductNm== "+ orderProductNm);
								logger.debug("OrderContoller modifyOrder productPriceList orderProductCapa== "+ orderProductCapa);
							}
							
							orderAmount = tempProduct.getProductPrice() *orderCount;	
							logger.debug("OrderContoller registerOrder orderAmount== "+ orderAmount);
							productVo.setOrderAmount(orderAmount);
							orderTotalAmount += orderAmount;								
						}
					}	
					
					logger.debug("OrderContoller modifyOrder orderTotalAmount== "+ orderTotalAmount);
					
					productVo.setOrderId(params.getOrderId());
					productVo.setOrderProductSeq(i+1);
					productVo.setProductId(productId);
					productVo.setProductPriceSeq(productPriceSeq);				
					productVo.setOrderCount(orderCount);
					productVo.setOrderProductEtc(request.getParameter("orderProductEtc_"+i));
					productVo.setBottleChangeYn(bottleChangeYn);								
					
					orderProduct.add(productVo);
				}
				
				logger.debug("OrderContoller modifyOrder orderProductNm== "+ orderProductNm);
				logger.debug("OrderContoller modifyOrder orderProductCapa== "+ orderProductCapa);
				
				if(productCount > 1) {
					orderProductNm = orderProductNm +"외 "+ (productCount-1);
					orderProductCapa = orderProductCapa +"외 "+ (productCount-1);
				}
							
				if(orderMapper.deleteOrderProducts(orderId) > 0 ) {
					result1 = orderMapper.insertOrderProducts(orderProduct);
				}
				params.setOrderProductNm(orderProductNm);
				params.setOrderProductCapa(orderProductCapa);
				params.setOrderTotalAmount(orderTotalAmount);
			}
			//
			
			params.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.01"));
		
			result =  orderMapper.updateOrder(params);	
		
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
	public int modifyOrderBottleId(OrderProductVO param) {
		
		//TODO  용기거래테이블 등록 필요
		BottleVO bottle = new BottleVO();
		bottle.setBottleId(param.getBottleId());
		bottle.setOrderId(param.getOrderId());
		bottle.setOrderProductSeq(param.getOrderProductSeq());
		bottle.setUpdateId(param.getUpdateId());
		
		int result = bottleService.modifyBottleOrder(bottle);
		
		if(result >0 )
			return orderMapper.updateOrderBottleId(param);
		else
			return 0;
	}

	@Override
	public int modifyOrderComplete(OrderProductVO param) {
		int result = 0;
		try {
			//TODO  용기거래테이블 등록 필요
			BottleVO bottle = new BottleVO();
			bottle.setBottleId(param.getBottleId());
			bottle.setOrderId(param.getOrderId());
			bottle.setOrderProductSeq(param.getOrderProductSeq());
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setBottleWorkId(param.getUpdateId());		
			bottle.setBottleType(param.getBottleType());
			bottle.setUpdateId(param.getUpdateId());
			
			result = bottleService.modifyBottleOrder(bottle);
			
			if(result >0 ) {
				result =  orderMapper.updateOrderBottleId(param);
				/*
				OrderVOreturn  order = new OrderVO();
				
				order.setOrderId(param.getOrderId());
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
				order.setUpdateId(param.getUpdateId());
				
				return orderMapper.updateOrderProcessCd(order);
				*/
			}else {
				return 0;
			}
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
	public int changeOrderProcessCd(OrderVO params) {
		
		String searchOrderDtFrom = null;
		String searchOrderDtEnd = null;
		int  result = 0;
		
		try {		
			logger.debug("******params.getOrderId()()) *****===*"+params.getChOrderId());
			logger.debug("Orderserive  searchOrderDt "+ params.getSearchOrderDt());
			String searchOrderDt = params.getSearchOrderDt();	
					
			if(searchOrderDt != null && searchOrderDt.length() > 20) {
				
				logger.debug("OrderContoller searchOrderDt "+ searchOrderDt.length());

				searchOrderDtFrom = searchOrderDt.substring(0, 10) ;				
				searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
				
				params.setSearchOrderDtFrom(searchOrderDtFrom);
				params.setSearchOrderDtEnd(searchOrderDtEnd);
				
			}			
			
			result = orderMapper.updateOrderProcessCd(params);
			logger.debug("Orderserive end searchOrderDt "+ params.getSearchOrderDt());
					
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
	public int deleteOrder(OrderVO param) {
		return orderMapper.deleteOrder(param);
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

	@Override
	public OrderExtVO getOrder(Integer orderId) {
		
		OrderVO order = orderMapper.selectOrderDetail(orderId);	
		
		List<OrderProductVO> orderProduct = orderMapper.selectOrderProductList(orderId);
		
		OrderExtVO result = new OrderExtVO();
		result.setOrder(order);
		result.setOrderProduct(orderProduct);
		
		return result;
	}

	
}
