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
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
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
		
		logger.debug("****** getOrderList *****param.getRowPerPage===*" + param.getRowPerPage());
		
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
			logger.debug("****** getOrderList *****getSearchOrderDt===*"+param.getSearchOrderDt());
		}		
		
		if(param.getSearchOrderDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchOrderDtFrom());
			logger.debug("****** getOrderList *****getSearchOrderDtFrom===*"+param.getSearchOrderDtFrom());
		}
		
		if(param.getSearchOrderDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchOrderDtEnd());
			logger.debug("****** getOrderList *****getSearchOrderDtEnd===*"+param.getSearchOrderDtEnd());
		}	
		if(param.getSearchOrderProcessCd() != null) {
			map.put("searchOrderProcessCd", param.getSearchOrderProcessCd());
			logger.debug("****** getOrderList *****getSearchOrderProcessCd===*"+param.getSearchOrderProcessCd());
		}	
		logger.debug("****** getOrderList *****currentPage===*"+currentPage);		
		
		int orderCount = orderMapper.selectOrderCount(map);
		
		logger.debug("****** getOrderList.orderCount *****===*"+orderCount);
		
		//int lastPage = (int)(Math.ceil(orderCount/ROW_PER_PAGE));
		int lastPage = (int)((double)orderCount/ROW_PER_PAGE+0.95);
		
		logger.debug("****** getOrderList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		
		//수정 Start
		int pages = (orderCount == 0) ? 1 : (int) ((orderCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨		
        int blocks;
        int block;
        blocks = (int) Math.ceil(1.0 * pages / ROW_PER_PAGE); // *소숫점 반올림
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;
        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<OrderVO> orderList = orderMapper.selectOrderList(map);
		
		logger.debug("****** getOrderList.orderList *****===*"+orderList.size());
		logger.debug("****** getOrderList.lastPageNum *****===*"+lastPageNum);
		logger.debug("****** getOrderList.startPageNum *****===*"+startPageNum);
		resutlMap.put("list",  orderList);
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", orderCount);
		
		resutlMap.put("rowPerPage", ROW_PER_PAGE);
		
		return resutlMap;
	}

	@Override
	public List<OrderVO> getOrderListToExcel(OrderVO param) {
		logger.info("****** getOrderListToExcel *****start===*");		
		
					
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("searchCustomerNm", param.getSearchCustomerNm());	
		if(param.getSearchOrderDt() != null) {
			map.put("searchOrderDt", param.getSearchOrderDt());
			logger.debug("****** getOrderListToExcel *****getSearchOrderDt===*"+param.getSearchOrderDt());
		}		
		
		if(param.getSearchOrderDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchOrderDtFrom());
			logger.debug("****** getOrderListToExcel *****getSearchOrderDtFrom===*"+param.getSearchOrderDtFrom());
		}
		
		if(param.getSearchOrderDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchOrderDtEnd());
			logger.debug("****** getOrderListToExcel *****getSearchOrderDtEnd===*"+param.getSearchOrderDtEnd());
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
	public OrderVO getLastOrderForCustomer(Integer customerId) {
		return orderMapper.selectLastOrderForCustomer(customerId);	
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
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
					
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
			String bottleSaleYn = "N";
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
					bottleSaleYn = "N";
					boolean bottleFlag = false;
					
					productId = Integer.parseInt(request.getParameter("productId_"+i));
					productPriceSeq = Integer.parseInt(request.getParameter("productPriceSeq_"+i));
					if(request.getParameter("orderCount_"+i) != null)
						orderCount = Integer.parseInt(request.getParameter("orderCount_"+i));
					else 
						orderCount = 1;
					
					if(request.getParameter("bottleChangeYn_"+i) !=null)  bottleChangeYn = "Y";
					if(request.getParameter("bottleSaleYn_"+i) !=null)  bottleSaleYn = "Y";									
					
					for(int k=0;k<productPriceList.size();k++) {
						orderAmount = 0;
						tempProduct = productPriceList.get(k);
						
						if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
							if(tempProduct.getGasId()!=null && tempProduct.getGasId() > 0) bottleFlag = true;
							
							if(i==0) {
								orderProductNm = tempProduct.getProductNm();
								orderProductCapa = tempProduct.getProductCapa();
								logger.debug("OrderContoller registerOrder productPriceList orderProductNm== "+ orderProductNm);
								logger.debug("OrderContoller registerOrder productPriceList productPriceSeq== "+ productPriceSeq);
								logger.debug("OrderContoller registerOrder productPriceList orderProductCapa== "+ orderProductCapa);
							}
							
							orderAmount = tempProduct.getProductPrice() *orderCount;	
							logger.debug("OrderContoller registerOrder orderAmount== "+ orderAmount);
							productVo.setOrderAmount(orderAmount);
							orderTotalAmount += orderAmount;								
						}
					}				
					logger.debug("OrderContoller registerOrder productPriceList productId== "+ productId);
					logger.debug("OrderContoller registerOrder productPriceList productPriceSeq== "+ productPriceSeq);
					logger.debug("OrderContoller registerOrder orderTotalAmount== "+ orderTotalAmount);
					
					productVo.setOrderId(params.getOrderId());
					productVo.setOrderProductSeq(i+1);
					productVo.setProductId(productId);
					productVo.setProductPriceSeq(productPriceSeq);				
					productVo.setOrderCount(orderCount);
					productVo.setOrderProductEtc(request.getParameter("orderProductEtc_"+i));
					productVo.setBottleChangeYn(bottleChangeYn);	
					productVo.setBottleSaleYn(bottleSaleYn);		
					//productVo.setProductDeliveryDt(null);
					
					orderProduct.add(productVo);
					
					if(bottleFlag) {
					
						for(int k=0; k< orderCount ; k++) {
						
							OrderBottleVO orderBottle = new OrderBottleVO();
							
							orderBottle.setOrderId(orderId);
							orderBottle.setOrderProductSeq(i+1);
							orderBottle.setProductId(productId);
							orderBottle.setProductPriceSeq(productPriceSeq);
							orderBottle.setCreateId(params.getCreateId());
							orderBottle.setUpdateId(params.getCreateId());
							
							orderBottleList.add(orderBottle);			
						}
					}
				}
				
				logger.debug("OrderContoller registerOrder orderProductNm== "+ orderProductNm);
				logger.debug("OrderContoller registerOrder orderProductCapa== "+ orderProductCapa);
				
				if(productCount > 1) {
					orderProductNm = orderProductNm +"외 "+ (productCount-1);
					orderProductCapa = orderProductCapa +"외 "+ (productCount-1);
				}
							
				result1 = orderMapper.insertOrderProducts(orderProduct);				
				
				if(orderBottleList.size() > 0)
					result1 = orderMapper.insertOrderBottles(orderBottleList);
				
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
	public int registerOrder(OrderVO param) {
		// TODO Auto-generated method stub
		return orderMapper.insertOrder(param);	
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
	public int registerOrderAndProduct(OrderVO order, List<OrderProductVO> orderProduct) {
		int result = 0;
		try {
			result =  orderMapper.insertOrder(order);	
			
			result = orderMapper.insertOrderProducts(orderProduct);
			
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
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			
			Integer orderId = params.getOrderId();
			//params.setOrderId(Integer.valueOf(orderId));			
						
			String orderTypeCd = params.getOrderTypeCd();
						
			int orderAmount = 0;
			int orderTotalAmount = 0;
			
			Integer productId =0;
			Integer productPriceSeq = 0;
			Integer orderCount = 0;
			
			String orderProductNm = "";
			String orderProductCapa = "";			
			String bottleChangeYn = "N";
			String bottleSaleYn = "N";
			
			CustomerPriceExtVO tempCustomerPrice = null;
			ProductTotalVO tempProduct = null;
		
			logger.debug("OrderContoller modifyOrder orderTypeCd== "+ orderTypeCd);
			
			// Sales ID 설정
			CustomerVO customer = customerService.getCustomerDetails(params.getCustomerId());
			
			params.setSalesId(customer.getSalesId());
			params.setUpdateId(params.getCreateId());
			
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
					bottleSaleYn = "N";
					boolean bottleFlag = false;
					
					productId = Integer.parseInt(request.getParameter("productId_"+i));
					productPriceSeq = Integer.parseInt(request.getParameter("productPriceSeq_"+i));
					orderCount = Integer.parseInt(request.getParameter("orderCount_"+i));
										
					if(request.getParameter("bottleChangeYn_"+i) !=null)  {
						bottleChangeYn = "Y";
					}
					if(request.getParameter("bottleSaleYn_"+i) !=null)  {
						bottleSaleYn = "Y";
					}
					
					//logger.debug("OrderContoller modifyOrder customerPriceList.size() = "+ customerPriceList.size());		
						
					for(int k=0;k<productPriceList.size();k++) {
						orderAmount = 0;
						tempProduct = productPriceList.get(k);
						
						if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
							if(tempProduct.getGasId()!=null && tempProduct.getGasId() > 0) bottleFlag = true;
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
					productVo.setBottleSaleYn(bottleSaleYn);		
					//productVo.setProductDeliveryDt(null);
					
					orderProduct.add(productVo);
					if(bottleFlag) {
						for(int k=0; k< orderCount ; k++) {
							
							OrderBottleVO orderBottle = new OrderBottleVO();
							
							orderBottle.setOrderId(orderId);
							orderBottle.setOrderProductSeq(i+1);
							orderBottle.setProductId(productId);
							orderBottle.setProductPriceSeq(productPriceSeq);
							orderBottle.setCreateId(params.getCreateId());
							orderBottle.setUpdateId(params.getCreateId());
							
							orderBottleList.add(orderBottle);						
						}
					}
				}
				
				logger.debug("OrderContoller modifyOrder orderProductNm== "+ orderProductNm);
				logger.debug("OrderContoller modifyOrder orderProductCapa== "+ orderProductCapa);
				
				if(productCount > 1) {
					orderProductNm = orderProductNm +"외 "+ (productCount-1);
					orderProductCapa = orderProductCapa +"외 "+ (productCount-1);
				}
				/*
				// 기존 상품과 비교
				 * 
				List<OrderProductVO> oldOrderProductList = getOrderProductList(orderId);
				for(int i = 0 ; i<oldOrderProductList.size();i++) {
					
				}
				*/
				if(orderMapper.deleteOrderProducts(orderId) > 0 ) {
					
					result1 = orderMapper.insertOrderProducts(orderProduct);
					
					result1 = orderMapper.deleteOrderBottles(orderId);
					
					result1 = orderMapper.insertOrderBottles(orderBottleList);
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
	public int modifyOrderProductCount(OrderProductVO param) {
		
		return orderMapper.updateOrderProductCount(param);
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
	public int modifyOrderProductBottle(OrderProductVO param) {
		return orderMapper.updateOrderBottleId(param);
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
	
	@Override
	public OrderExtVO getOrderNotDelivery(Integer orderId) {
		
		OrderVO order = orderMapper.selectOrderDetail(orderId);	
		
		List<OrderProductVO> orderProduct = orderMapper.selectOrderProductListNotDelivery(orderId);
		
		OrderExtVO result = new OrderExtVO();
		result.setOrder(order);
		result.setOrderProduct(orderProduct);
		
		return result;
	}

	@Override
	public int modifyOrderAdditionBottles(OrderVO param) {
		return orderMapper.updateOrderAdditionBottles(param);
	}
/*
	@Override
	public int modifyOrderProductDeliveryDt(OrderProductVO param) {
		return orderMapper.updateOrderProductDeliveryDt(param);
	}
*/

	@Override
	public int registerOrderBottle(OrderBottleVO param) {
		// TODO Auto-generated method stub
		return orderMapper.insertOrderBottle(param);	
	}

	@Override
	public int registerOrderBottles(List<OrderBottleVO> param) {
		logger.info("****** registerOrderProduct.registerOrderBottles *****===*");
		int result = 0;
		result =  orderMapper.insertOrderBottles(param);		
		
		return result;
	}

	@Override
	public int modifyOrderBottles(List<OrderBottleVO> param) {
		return orderMapper.updateOrderBottles(param);	
	}

	@Override
	public int modifyOrderBottle(OrderBottleVO param) {
		return orderMapper.updateOrderBottle(param);	
	}

	@Override
	public int getNextOrderProductSeq(Integer orderId) {
		
		return orderMapper.selectNextOrderProductSeq(orderId);	
	}

	@Override
	public int changeOrdersProcessCd(OrderVO param) {
		
		List<String> list = null;
		if(param.getOrderIds()!=null && param.getOrderIds().length() > 0) {
			list = StringUtils.makeForeach(param.getOrderIds(), ","); 	
		}	
		
		List<Integer> orderList = new ArrayList();
		for(int i = 0 ; i < list.size() ; i++) {
			if(list.get(i) != null) {
				int orderId = Integer.parseInt(list.get(i));
				
				Integer order = new Integer(orderId);
				orderList.add(order);	
			}
		}
		param.setOrderList(orderList);
		
		return orderMapper.updateOrdersProcessCd(param);	
	}

	
	
	
}
