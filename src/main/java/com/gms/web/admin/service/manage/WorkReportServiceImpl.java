package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.TempProductVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.mapper.manage.WorkReportMapper;

@Service
public class WorkReportServiceImpl implements WorkReportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WorkReportMapper workMapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	
	@Override
	public List<WorkReportVO> getWorkReportList(WorkReportVO param) {

		String searchDt = param.getSearchDt();		
		
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		logger.info("WorkReportServiceImpl getWorkReportList Work_Dt= "+ param.getSearchDt());
		
		//List<WorkBottleVO> workBottleList = workMapper.selectWorkReportList(param);
		
		
		return workMapper.selectWorkReportList(param);
	}
	
	
	@Override
	public List<WorkReportViewVO> getWorkReportList1(WorkReportVO param) {

		String searchDt = param.getSearchDt();		
		
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		logger.debug("WorkReportServiceImpl getWorkReportList1 Work_Dt= "+ param.getSearchDt());
		
		
		List<WorkBottleVO> workBottleList = workMapper.selectWorkReportList1(param);
		
		List<WorkReportVO> reportList = workMapper.selectWorkReportOnlyList(param);
		
		for(int i = 0 ; i < reportList.size() ; i++ ) {
			
			WorkReportViewVO temp = new WorkReportViewVO();
			temp.setWorkReportSeq(reportList.get(i).getWorkReportSeq());
			
			List<WorkBottleVO> tempBottle = new ArrayList<WorkBottleVO>();
			List<WorkBottleVO> tempBottle1 = new ArrayList<WorkBottleVO>();
			
			temp.setBackBottles(tempBottle);
			temp.setSalesBottles(tempBottle1);
			viewList.add(temp);			
		}
		
		for(int i = 0 ; i < workBottleList.size() ; i++ ) {
			
			WorkBottleVO workBottle = workBottleList.get(i);
			
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				// Work_Report_Seq 동일
				if(temp.getWorkReportSeq() == workBottle.getWorkReportSeq()) {
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());
					temp.setOrderAmount(workBottle.getOrderTotalAmount());
					
					logger.debug("WorkReportServiceImpl getWorkReportList1 temp.getWorkReportSeq()= "+ temp.getWorkReportSeq());
					logger.debug("WorkReportServiceImpl getWorkReportList1 workBottle.getBottleWorkCd()= "+ workBottle.getBottleWorkCd());
					
					// 회수
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310"))) {
						logger.debug("WorkReportServiceImpl getWorkReportList1 back workBottle.getProductId= "+ workBottle.getProductId());
						temp.getBackBottles().add(workBottle);
						
					}else {
						logger.debug("WorkReportServiceImpl getWorkReportList sales workBottle.getProductId= "+ workBottle.getProductId());
						temp.getSalesBottles().add(workBottle);
					}
					
				}
			}
		}
		
		for(int i=0;i<viewList.size() ; i++) {
			logger.debug("WorkReportServiceImpl getWorkReportList sviewList= "+ viewList.get(i).getCustomerNm());
			logger.debug("WorkReportServiceImpl getWorkReportList salesize= "+ viewList.get(i).getSalesBottles().size());
			logger.debug("WorkReportServiceImpl getWorkReportList backsize= "+ viewList.get(i).getBackBottles().size());
			for(int j=0;j<viewList.get(i).getSalesBottles().size();j++) {
				logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getSalesBottles().size() .getProductId= "+ viewList.get(i).getSalesBottles().get(j).getProductNm());
			}
			
			for(int j=0;j<viewList.get(i).getBackBottles().size();j++) {
				logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getBackBottles().size() .getProductId= "+ viewList.get(i).getBackBottles().get(j).getProductNm());
			}
		}
		
		return viewList;
	}

	@Override
	public List<WorkReportViewVO> getWorkReportListAll(WorkReportVO param) {
		/*
		String searchDt = param.getSearchDt();			
		
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		logger.debug("WorkReportServiceImpl getWorkReportList Work_Dt= "+ param.getSearchDt());
		
		return workMapper.selectWorkReportListAll(param);
		*/
		String searchDt = param.getSearchDt();		
		
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		logger.debug("WorkReportServiceImpl getWorkReportList1 Work_Dt= "+ param.getSearchDt());
		
		List<WorkBottleVO> workBottleList = workMapper.selectWorkReportListAll(param);
		
		List<WorkReportVO> reportList = workMapper.selectWorkReportOnlyListAll(param);
		
		for(int i = 0 ; i < reportList.size() ; i++ ) {
			
			WorkReportViewVO temp = new WorkReportViewVO();
			temp.setWorkReportSeq(reportList.get(i).getWorkReportSeq());
			
			List<WorkBottleVO> tempBottle = new ArrayList<WorkBottleVO>();
			List<WorkBottleVO> tempBottle1 = new ArrayList<WorkBottleVO>();
			
			temp.setBackBottles(tempBottle);
			temp.setSalesBottles(tempBottle1);
			viewList.add(temp);			
		}
		
		for(int i = 0 ; i < workBottleList.size() ; i++ ) {
			
			WorkBottleVO workBottle = workBottleList.get(i);
			
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				// Work_Report_Seq 동일
				if(temp.getWorkReportSeq() == workBottle.getWorkReportSeq()) {
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());
					temp.setOrderAmount(workBottle.getOrderTotalAmount());
					
					logger.debug("WorkReportServiceImpl getWorkReportList1 temp.getWorkReportSeq()= "+ temp.getWorkReportSeq());
					logger.debug("WorkReportServiceImpl getWorkReportList1 workBottle.getBottleWorkCd()= "+ workBottle.getBottleWorkCd());
					
					// 회수
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310"))) {
						logger.debug("WorkReportServiceImpl getWorkReportList1 back workBottle.getProductId= "+ workBottle.getProductId());
						temp.getBackBottles().add(workBottle);
						
					}else {
						logger.debug("WorkReportServiceImpl getWorkReportList sales workBottle.getProductId= "+ workBottle.getProductId());
						temp.getSalesBottles().add(workBottle);
					}
					
				}
			}
		}
		
		for(int i=0;i<viewList.size() ; i++) {
			logger.debug("WorkReportServiceImpl getWorkReportList sviewList= "+ viewList.get(i).getCustomerNm());
			logger.debug("WorkReportServiceImpl getWorkReportList salesize= "+ viewList.get(i).getSalesBottles().size());
			logger.debug("WorkReportServiceImpl getWorkReportList backsize= "+ viewList.get(i).getBackBottles().size());
			for(int j=0;j<viewList.get(i).getSalesBottles().size();j++) {
				logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getSalesBottles().size() .getProductId= "+ viewList.get(i).getSalesBottles().get(j).getProductNm());
			}
			
			for(int j=0;j<viewList.get(i).getBackBottles().size();j++) {
				logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getBackBottles().size() .getProductId= "+ viewList.get(i).getBackBottles().get(j).getProductNm());
			}
		}
		
		return viewList;
	}

	
	@Override
	@Transactional
	public int registerWorkReportForOrder(WorkReportVO param) {
		logger.info("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;
		try {
			logger.debug("WorkReportServiceImpl registerWorkReportForOrder orderId =" + param.getOrderId());
			logger.debug("WorkReportServiceImpl registerWorkReportForOrder bottleIds =" + param.getBottlesIds());
			
			//Order 정보가져오기
			OrderExtVO  orderInfo = orderService.getOrderNotDelivery(param.getOrderId());			
			
			/*
			BottleVO bottle = new BottleVO();
			
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				List<String> list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
			}	
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);		
			*/
			//Bottle 정보 가져오기
			List<BottleVO> bottleList = getBottleList(param);
			
			logger.debug("*****************  WorkReportServiceImpl registerWorkReport bottleList.size()=="+ bottleList.size());
			//Work_Report_Seq 가져오기
			boolean registerFlag = false;
			param.setCustomerId(orderInfo.getOrder().getCustomerId());
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}
			logger.debug("WorkReportServiceImpl registerWorkReport workReportSeq =" + workReportSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			param.setUserId(param.getCreateId());
			param.setCustomerId(orderInfo.getOrder().getCustomerId());	
			
			List<OrderProductVO> orderProductList = orderInfo.getOrderProduct();
			
			// bottle > orderProduct 많은 경우 예외처리
			
			OrderProductVO tempOrderProduct = null;
			int orderProductTotalCount = 0;	// 총 주문 상품수
			
			// 용기수 > 주문한 상품  , 주문 상품 추가
			List<OrderProductVO> addOrderProductList = new ArrayList<OrderProductVO>();			
			List<TempProductVO> tempProductList = new ArrayList<TempProductVO>();
			
			orderProductTotalCount =orderInfo.getOrderProduct().size();		
			
			for(int j=0; j< orderInfo.getOrderProduct().size() ; j++) {
				
				tempOrderProduct = orderProductList.get(j);							
				
				logger.debug("WorkReportServiceImpl registerWorkReport tempOrderProduct.getOrderCount() =" + tempOrderProduct.getOrderCount() );
			
				TempProductVO tempProduct = new TempProductVO();
				tempProduct.setProductId(tempOrderProduct.getProductId());
				tempProduct.setProductPriceSeq(tempOrderProduct.getProductPriceSeq());
				tempProduct.setOrderId(tempOrderProduct.getOrderId());
				tempProduct.setOrderProductSeq(tempOrderProduct.getOrderProductSeq());
				tempProduct.setOrderBottleSeq(tempOrderProduct.getOrderBottleSeq());
				tempProductList.add(tempProduct);
				
				
			}
			logger.debug("WorkReportServiceImpl registerWorkReport orderProductTotalCount =" + orderProductTotalCount );		
			logger.debug("%%%% - WorkReportServiceImpl registerWorkReport tempProductList.size() ==" + tempProductList.size());
			logger.debug("WorkReportServiceImpl registerWorkReport bottleWorkCd =" + param.getBottleWorkCd() );
			
			if(param.getBottleWorkCd() == PropertyFactory.getProperty("common.bottle.status.0308") || param.getBottleWorkCd() == PropertyFactory.getProperty("common.bottle.status.0309")) {
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
			}else {
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
			}
			
			BottleVO tempBottle = null;
			WorkBottleVO workBottle = null;
			//List<String> inBottleId = new ArrayList<String>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();					
			
			List<Integer> chi = new ArrayList<Integer>();
			//tempbottleList = bottleList;				
			
			boolean isGo = false;
			
			logger.debug("***********************  WorkReportServiceImpl registerWorkReport bottleList.size()=="+ bottleList.size());
			
			for(int i = 0; i < bottleList.size() ; i++) {
				isGo = true;
				tempBottle = bottleList.get(i);			
				logger.debug("~~~~~~~~~~~~~~~~~~# WorkReportServiceImpl registerWorkReport tempBottle.getBottleId ==" +i+"=="+ tempBottle.getBottleId());
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport orderService.tempProductList.size() ==" + tempProductList.size());
				
				
				for(int j = 0 ; j < tempProductList.size() ; j++) {
					
					TempProductVO tempProduct = tempProductList.get(j);
					logger.debug("###### ### WorkReportServiceImpl registerWorkReport isGo ==" +j+"=="+ isGo);
					logger.debug("###### ### WorkReportServiceImpl registerWorkReport tempBottle.getBottleId() i==" +i+"== j=" +j+"="+tempBottle.getBottleId());
					
					logger.debug("$$$$$$$$$$$$  WorkReportServiceImpl registerWorkReport tempBottle.getProductId() ==" +j+"=="+ tempBottle.getProductId());
					logger.debug("###### ### WorkReportServiceImpl registerWorkReport tempProduct.getProductId() ==" +j+"=="+ tempProduct.getProductId());
					logger.debug("###### ### WorkReportServiceImpl registerWorkReport tempBottle.getProductPriceSeq() ==" +j+"=="+ tempBottle.getProductPriceSeq());
					logger.debug("###### ### WorkReportServiceImpl registerWorkReport tempProduct.getProductPriceSeq() ==" +j+"=="+ tempProduct.getProductPriceSeq());
					
					if(isGo && tempBottle.getProductId() == tempProduct.getProductId() 
							&& tempBottle.getProductPriceSeq() == tempProduct.getProductPriceSeq()) {
						// 동일 상품
						
						bottleList.get(i).setOrderId(tempProduct.getOrderId());
						bottleList.get(i).setOrderProductSeq(tempProduct.getOrderProductSeq());
						
						tempBottle.setOrderId(tempProduct.getOrderId());
						tempBottle.setOrderProductSeq(tempProduct.getOrderProductSeq());
						tempBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
						tempBottle.setBottleWorkId(param.getCreateId());
						tempBottle.setUpdateId(param.getCreateId());
						tempBottle.setBottleType(param.getBottleType());
						tempBottle.setBottleWorkCd(param.getBottleWorkCd());
						
						// Bottle 정보 업데이트
						result = bottleService.modifyBottleOrder(tempBottle);
						
						OrderBottleVO tempOrderBottle = new OrderBottleVO();
						tempOrderBottle.setOrderId(tempProduct.getOrderId());
						tempOrderBottle.setOrderProductSeq(tempProduct.getOrderProductSeq());
						tempOrderBottle.setBottleId(tempBottle.getBottleId());
						tempOrderBottle.setCreateId(param.getCreateId());
						tempOrderBottle.setUpdateId(param.getCreateId());
						tempOrderBottle.setOrderBottleSeq(tempProduct.getOrderBottleSeq());
						
						result =orderService.modifyOrderBottle(tempOrderBottle);
						//orderBottleList.add(tempOrderBottle);
						logger.debug("###### ### WorkReportServiceImpl registerWorkReport bottleID ==" + tempBottle.getBottleId());
						
						//임시 OrderProduct 생성후 넘김/ 해당 상품 납품일 업데이트
						//OrderProductVO tempV = new OrderProductVO();
						//tempV.setOrderId(tempProduct.getOrderId());
						//tempV.setOrderProductSeq(tempProduct.getOrderProductSeq());
						//result = orderService.modifyOrderProductDeliveryDt(tempV);
						
						tempProductList.remove(j);						
						chi.add(Integer.valueOf(i));
						
						isGo = false;
						
						// 주문 외 추가 된 용기 정보 -> 주문 상품 정보 추가를 위해 List에 댬기
						//inBottleId.add(tempBottle.getBottleId());
						//addOrderProductList.add(e)
						workBottle = new WorkBottleVO();
						
						workBottle.setBottleId(tempBottle.getBottleId());
						workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
						workBottle.setWorkReportSeq(workReportSeq);
						workBottle.setCreateId(param.getCreateId());
						workBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
						workBottle.setBottleWorkCd(param.getBottleWorkCd());		
						
						workBottle.setGasId(tempBottle.getGasId());
						workBottle.setProductId(tempBottle.getProductId());
						workBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());	
						
						workBottleList.add(workBottle);		
						logger.debug("###### ### WorkReportServiceImpl registerWorkReportForOrder tempbottle product ==" + tempBottle.getProductId());
						logger.debug("###### ### WorkReportServiceImpl registerWorkReportForOrder workbottle product ==" + workBottle.getProductId());
					}
				}
			
				
			}
			
			
			//List<BottleVO> tempbottleList = new ArrayList<BottleVO>();	
			logger.debug("***********  WorkReportServiceImpl registerWorkReport chi.size() ==" + chi.size());
			if(chi.size() > 0) {
				for(int i = 0; i < chi.size() ; i++) {
					logger.debug("***********  WorkReportServiceImpl registerWorkReport chi.size()-i-1==" + (chi.size()-i-1));
					bottleList.remove(chi.get(chi.size()-i-1).intValue());
					
				}
			}
			
				
			logger.debug("***********  WorkReportServiceImpl registerWorkReport orderService.bottleList.size() ==" + bottleList.size());
		
			orderBottleList.clear();
			
			OrderProductVO tempOrderProduct1 = null;
			
			int lastOrderProductSeq = orderInfo.getOrderProduct().size();
			//logger.debug("***********  WorkReportServiceImpl registerWorkReport orderService.tempbottleList.size() ==" + tempbottleList.size());
			logger.debug("***********  WorkReportServiceImpl registerWorkReport orderService.tempProductList.size() ==" + tempProductList.size());
			
			if(bottleList.size() > 0){	// 추가 용기가 있는 경우
				BottleVO temp = null;
				
				for(int i=0;i<bottleList.size();i++) {
					temp = bottleList.get(i);					
					
					//ordrProdict 추가
					tempOrderProduct1 = new OrderProductVO();
					temp.setCustomerId(orderInfo.getOrder().getCustomerId());		
					
					ProductTotalVO tempProductTotal = productService.getPrice(temp);		
					
					tempOrderProduct1.setOrderId(orderInfo.getOrder().getOrderId());					
					tempOrderProduct1.setOrderProductSeq(++lastOrderProductSeq);
					
					tempOrderProduct1.setProductId(temp.getProductId());
					tempOrderProduct1.setProductPriceSeq(temp.getProductPriceSeq());
					tempOrderProduct1.setOrderId(orderInfo.getOrder().getOrderId());
					tempOrderProduct1.setBottleId(temp.getBottleId());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308"))) {
						tempOrderProduct1.setBottleChangeYn("N");
						tempOrderProduct1.setBottleSaleYn("Y");
					}else {
						tempOrderProduct1.setBottleChangeYn("Y");
						tempOrderProduct1.setBottleChangeYn("N");
					}
					
					tempOrderProduct1.setOrderProductEtc("초과 용기");
					tempOrderProduct1.setOrderCount(1);
					if(tempProductTotal!=null && tempProductTotal.getCustomerProductPrice() > 0)
						tempOrderProduct1.setOrderAmount(tempProductTotal.getCustomerProductPrice());
					else
						tempOrderProduct1.setOrderAmount(tempProductTotal.getProductPrice());
					tempOrderProduct1.setCreateId(param.getCreateId());
					tempOrderProduct1.setUpdateId(param.getCreateId());		
					
					
					// Bottle 정보 업데이트
					temp.setOrderId(orderInfo.getOrder().getOrderId());
					temp.setOrderProductSeq(tempOrderProduct1.getOrderProductSeq());
					temp.setCustomerId(orderInfo.getOrder().getCustomerId());
					temp.setBottleWorkId(param.getCreateId());
					temp.setUpdateId(param.getCreateId());
					temp.setBottleType(param.getBottleType());
					temp.setBottleWorkCd(param.getBottleWorkCd());
					
					result = bottleService.modifyBottleOrder(temp);
										
					addOrderProductList.add(tempOrderProduct1);
					
					OrderBottleVO tempOrderBottle = new OrderBottleVO();
					tempOrderBottle.setOrderId(orderInfo.getOrder().getOrderId());
					tempOrderBottle.setOrderProductSeq(lastOrderProductSeq);
					tempOrderBottle.setProductId(temp.getProductId());
					tempOrderBottle.setProductPriceSeq(temp.getProductPriceSeq());
					tempOrderBottle.setBottleId(temp.getBottleId());
					tempOrderBottle.setCreateId(param.getCreateId());
					tempOrderBottle.setUpdateId(param.getCreateId());					
					
					orderBottleList.add(tempOrderBottle);					
					
					
					orderInfo.getOrder().setOrderTotalAmount(orderInfo.getOrder().getOrderTotalAmount()+tempOrderProduct1.getOrderAmount());
										
					workBottle = new WorkBottleVO();
					workBottle.setBottleId(temp.getBottleId());
					workBottle.setBottleBarCd(temp.getBottleBarCd());
					workBottle.setWorkReportSeq(workReportSeq);
					workBottle.setCreateId(param.getCreateId());
					workBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
					workBottle.setBottleWorkCd(param.getBottleWorkCd());	
					workBottle.setGasId(temp.getGasId());
					workBottle.setProductId(temp.getProductId());
					workBottle.setProductPriceSeq(temp.getProductPriceSeq());	
					workBottleList.add(workBottle);		
				}			
				
			}
			
			// 주문 상품과 용기정보가 매칭
			if(tempProductList.size() == 0 ) {
				orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));					
			}

			if(addOrderProductList.size() > 0) {
				
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport orderService.addOrderProductList > 0 ");
				result = orderService.registerOrderProducts(addOrderProductList);
				
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport OrderProductNm "+orderInfo.getOrder().getOrderProductNm());
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport OrderProductCapa "+orderInfo.getOrder().getOrderProductCapa());
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport getOrderTotalAmount "+orderInfo.getOrder().getOrderTotalAmount());
				//order정보 업데이트

				//TODO order 셋팅 필요
				orderInfo.getOrder().setOrderProductNm(orderInfo.getOrderProduct().get(0).getProductNm()+" 외"+bottleList.size());
				orderInfo.getOrder().setOrderProductCapa(orderInfo.getOrderProduct().get(0).getProductCapa()+" 외"+bottleList.size());
				//orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
				
				
				result = orderService.modifyOrderAdditionBottles(orderInfo.getOrder());
				
				//result = orderService.modifyOrderProductDeliveryDt(tempOrderProduct);
			}
			orderInfo.getOrder().setChOrderId(orderInfo.getOrder().getOrderId());
			orderInfo.getOrder().setUpdateId(param.getUpdateId());
			orderInfo.getOrder().setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
			
			// Order 상태 정보 변경
			result = orderService.changeOrderProcessCd(orderInfo.getOrder());
					
			// Order_Bottle 등록
			if(orderBottleList.size() > 0)
				result = orderService.registerOrderBottles(orderBottleList);
			
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderInfo.getOrder().getOrderProductNm());
			param.setWorkProductCapa(orderInfo.getOrder().getOrderProductCapa());
			
			
			param.setOrderAmount(orderInfo.getOrder().getOrderTotalAmount());			
			logger.debug("WorkReportServiceImpl registerWorkReport orderAmount =" + orderInfo.getOrder().getOrderTotalAmount() );
			
			if(registerFlag) result = workMapper.insertWorkReport(param);			
			if(result <= 0) return 0;
			
			result = workMapper.insertWorkBottles(workBottleList);	
			if(result <= 0) return 0;
			
			
			/// End of Order & Bottle
			
			// Start Work_Report , Work_Bottle
					
			/*
			//TB_Work_Bottle 등록	
			
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
			
			WorkBottleVO workBottle = null;
			
			
			String notOrderBottleId = "";			
			boolean INCLUDE_ORDER = false;
			int orderProductCount = orderInfo.getOrderProduct().size();
			
			
			
			String beforeBottleId = "";
			
			Integer beforeProductId = 0;
			Integer beforeProductPriceSeq = 0;
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				INCLUDE_ORDER = false;
				tempBottle = bottleList.get(i);
				
				logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getBottleId() =" + tempBottle.getBottleId() );
				workBottle = new WorkBottleVO();
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());						
				
				beforeBottleId = tempBottle.getBottleId();
				
				if(i > 0 ) {
					//beforeBottleId = bottleList.get(i-1).getBottleId();
					beforeProductId = bottleList.get(i-1).getProductId();
					beforeProductPriceSeq = bottleList.get(i-1).getProductPriceSeq();
				}
				//TODO 용기 / orderProduct 비교 다시
				
				int orderProductCnt = 0;
				for(int j=0; j< orderInfo.getOrderProduct().size() ; j++) {
					
					tempOrderProduct = orderProductList.get(j);
					
					orderProductCnt = tempOrderProduct.getOrderCount();
					
					tempBottle.setOrderId(orderInfo.getOrder().getOrderId());
					tempBottle.setOrderProductSeq(tempOrderProduct.getOrderProductSeq());
					tempBottle.setCustomerId(orderInfo.getOrder().getCustomerId());
					tempBottle.setBottleWorkId(param.getCreateId());
					tempBottle.setUpdateId(param.getCreateId());
					tempBottle.setBottleType(param.getBottleType());
					tempBottle.setBottleWorkCd(param.getBottleWorkCd());
					
					logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getProductId() =" + tempBottle.getProductId() +" ** tempOrderProduct.getProductId() == "+ tempOrderProduct.getProductId() );
					logger.debug("WorkReportServiceImpl registerWorkReport tempBottle.getProductPriceSeq() =" + tempBottle.getProductPriceSeq() +" ** tempOrderProduct.getProductPriceSeq() == "+ tempOrderProduct.getProductPriceSeq() );
					
					if(tempBottle.getProductId() == tempOrderProduct.getProductId() 
							&& (tempBottle.getProductPriceSeq()).equals(tempOrderProduct.getProductPriceSeq()) ){
						
						workBottle.setGasId(tempBottle.getGasId());
						workBottle.setProductId(tempOrderProduct.getProductId());
						workBottle.setProductPriceSeq(tempOrderProduct.getProductPriceSeq());						
						
						//TB_Order_Product 설정						
						orderInfo.getOrderProduct().get(j).setBottleId(tempBottle.getBottleId());
						orderInfo.getOrderProduct().get(j).setBottleWorkCd(param.getBottleWorkCd());
						orderInfo.getOrderProduct().get(j).setBottleType(param.getBottleType());						
						tempOrderProduct.setBottleId(tempBottle.getBottleId());
						tempOrderProduct.setUpdateId(param.getCreateId());
						logger.debug("********** WorkReportServiceImpl registerWorkReport modifyBottleOrder *********** ");
						result = bottleService.modifyBottleOrder(tempBottle);
						
						INCLUDE_ORDER = true;
						
						if(result <= 0) return 0;
						result = orderService.modifyOrderProductBottle(tempOrderProduct);
						if(result <= 0) return 0;
					}
						
				}
				if(!INCLUDE_ORDER) {
					notOrderBottleId += tempBottle.getBottleId();
					
					//주문 상품 추가 등록 필요
					ProductPriceVO pPrice = new ProductPriceVO();
					pPrice.setProductId(tempBottle.getProductId());
					pPrice.setProductPriceSeq(tempBottle.getOrderProductSeq());
					
					ProductPriceVO productPrice = productService.getProductPriceDetails(pPrice);
					
					OrderProductVO temp = new OrderProductVO();
					
					temp.setOrderId(orderInfo.getOrder().getOrderId());
					temp.setOrderProductSeq(++orderProductCount);
					temp.setProductId(tempBottle.getProductId());
					temp.setProductPriceSeq(tempBottle.getProductPriceSeq());
					temp.setBottleId(tempBottle.getBottleId());
					temp.setOrderProductEtc("초과 용기");
					temp.setOrderCount(1);
					temp.setOrderAmount(productPrice.getProductPrice());					
					temp.setBottleChangeYn("N");
					temp.setCreateId(param.getCreateId());
					temp.setUpdateId(param.getCreateId());
					
					addOrderProductList.add(temp);
					
					logger.debug("&&&&&      WorkReportServiceImpl registerWorkReport addOrderProductList.add= ");
					orderInfo.getOrder().setOrderTotalAmount(orderInfo.getOrder().getOrderTotalAmount()+productPrice.getProductPrice());
									
				}
			
				workBottleList.add(workBottle);							
			}
			
			logger.debug("WorkReportServiceImpl registerWorkReport notOrderBottleId=" + notOrderBottleId);
			
			// 용기  > 주문한 상품수
			if(bottleList.size() > orderProductTotalCount) {
				
				logger.debug("&&&&&      WorkReportServiceImpl registerWorkReport bottleList.size() > orderInfo.getOrderProduct().size())");
				orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
				
				if(bottleList.size() > 1) {
					orderInfo.getOrder().setOrderProductNm(orderInfo.getOrderProduct().get(0).getProductNm()+" 외"+bottleList.size());
					orderInfo.getOrder().setOrderProductCapa(orderInfo.getOrderProduct().get(0).getProductCapa()+" 외"+bottleList.size());
				}
			}	
			else {		// 용기수 < 주문한 상품수
				orderInfo.getOrder().setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.03"));				
			}
			
			
			if(addOrderProductList.size() > 0) {
				
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport orderService.registerOrderProducts");
				result = orderService.registerOrderProducts(addOrderProductList);
				
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport OrderProductNm "+orderInfo.getOrder().getOrderProductNm());
				logger.debug("---------------- WorkReportServiceImpl registerWorkReport OrderProductCapa "+orderInfo.getOrder().getOrderProductCapa());
				//order정보 업데이트

				result = orderService.modifyOrderAdditionBottles(orderInfo.getOrder());
			}
			
			orderInfo.getOrder().setChOrderId(orderInfo.getOrder().getOrderId());
			orderInfo.getOrder().setUpdateId(param.getUpdateId());
			
			orderInfo.getOrder().setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
			result = orderService.changeOrderProcessCd(orderInfo.getOrder());
			if(result <= 0) return 0;			
			
			
			param.setWorkCd(orderInfo.getOrder().getOrderProcessCd());
			param.setWorkProductNm(orderInfo.getOrder().getOrderProductNm());
			param.setWorkProductCapa(orderInfo.getOrder().getOrderProductCapa());
			
			result = workMapper.insertWorkReport(param);			
			if(result <= 0) return 0;
			
			result = workMapper.insertWorkBottles(workBottleList);	
			if(result <= 0) return 0;
		*/
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			result = 0;
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			result = 0;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	@Transactional
	public int registerWorkReportNoOrder(WorkReportVO param) {
		logger.info("===================== WorkReportServiceImpl registerWorkReportNoOrder start ===============");
		logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleIds =" + param.getBottlesIds());			
		logger.debug("WorkReportServiceImpl registerWorkReportNoOrder customerId =" + param.getCustomerId());
			
		
		int result = 0;
		
		try {		
			//기존 주문이 있는지 여부 확인
			OrderVO orderTemp =  orderService.getLastOrderForCustomer(param.getCustomerId());
			
			if(orderTemp != null ) {
				param.setOrderId(orderTemp.getOrderId());
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder getOrderId =" + param.getOrderId());		
				result = registerWorkReportForOrder(param);
			}else {
				//Bottle 정보 가져오기
				List<BottleVO> bottleList = getBottleList(param);
				/*
				BottleVO bottle = new BottleVO();
				
				List<String> list = null;
				if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
					//bottleIds= request.getParameter("bottleIds");
					list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
					bottle.setBottList(list);
					param.setBottList(list);
				}	
				
				List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);			
			*/
				//Work_Report_Seq 가져오기
				boolean registerFlag = false;
				int workReportSeq = getWorkReportSeqForCustomerToday(param);
				
				if(workReportSeq <= 0) {
					workReportSeq = getWorkReportSeq();
					registerFlag = true;
				}
				
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder workReportSeq =" + workReportSeq);
				//TB_Work_Reprot 등록
				param.setWorkReportSeq(workReportSeq);
				param.setUserId(param.getCreateId());
				
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleWorkCd =" + param.getBottleWorkCd() );
				
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308")) ) {
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
				}else {
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
				}					
				
				//TB_Work_Bottle 등록	
				
				List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
				List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();
				List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();	
				
				OrderVO order = new OrderVO();
				
				int orderId = orderService.getOrderId();
				
				order.setOrderId(orderId);
				order.setCreateId(param.getCreateId());
				order.setUpdateId(param.getCreateId());
				order.setCustomerId(param.getCustomerId());
				order.setMemberCompSeq(1);
				order.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.01"));
				
				
				Calendar cal = Calendar.getInstance();
				int amPm = cal.get(Calendar.AM_PM);
				order.setDeliveryReqDt(cal.getTime());
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder cal.getTime() =" + order.getDeliveryReqDt());
				
				if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder AMPM =" + order.getDeliveryReqAmpm());			
				
				order.setOrderEtc("현장주문");
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.04"));
				order.setSalesId(param.getCreateId());
				//order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				
				OrderProductVO tempOrderProduct = null;
				
				WorkBottleVO workBottle = null;
				BottleVO tempBottle = null;
				ProductTotalVO tempProductTotal = null;
				
				//용기 정보를 이횽해 상품 정보 가져옴
				
				int orderProductSeq = 1;
				int orderCount = 0;
				int orderTotalAmount = 0;
				
				
				String orderProductNm = "";
				String orderProductCapa = "";
				
				for(int i = 0; i < bottleList.size() ; i++) {		
					
					tempBottle = bottleList.get(i);
					
					workBottle = new WorkBottleVO();
					
					tempOrderProduct = new OrderProductVO();
					
					tempProductTotal = productService.getBottleGasCapa(tempBottle);				
					
					tempOrderProduct.setProductId(tempProductTotal.getProductId());
					tempOrderProduct.setProductPriceSeq(tempProductTotal.getProductPriceSeq());
					tempOrderProduct.setOrderId(orderId);
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0308"))) {
						tempOrderProduct.setBottleChangeYn("N");
						tempOrderProduct.setBottleSaleYn("Y");
					}else {
						tempOrderProduct.setBottleChangeYn("Y");
						tempOrderProduct.setBottleChangeYn("N");
					}
					
					//tempOrderProduct.setProductDeliveryDt(cal.getTime());
					tempOrderProduct.setOrderProductEtc("주문없이추가");
					
					//tempOrderProduct.setBottleWorkCd(param.getBottleWorkCd());
					//tempOrderProduct.setBottleType(param.getBottleType());
					
					// TB_Order Product_Nm, Product_Capa 초기값
					if(i==0) {
						orderProductNm = tempProductTotal.getProductNm();
						orderProductCapa = tempProductTotal.getProductCapa();
					}
					
					OrderBottleVO tempOrderBottle = new OrderBottleVO();
					tempOrderBottle.setOrderId(orderId);
					
					tempOrderBottle.setCreateId(param.getCreateId());
					tempOrderBottle.setUpdateId(param.getCreateId());							
						
					
					if(i > 0) {		// 2번쨰부터 
						
						//이전 것고 비교
						if(tempBottle.getProductId() == tempProductTotal.getProductId()
								&& (tempBottle.getProductPriceSeq()).equals(tempProductTotal.getProductPriceSeq()) ){
							
							tempOrderBottle.setOrderProductSeq(orderProductSeq);
							
							tempOrderProduct.setOrderProductSeq(orderProductSeq);
													
							int iTemp = i-1;
							orderProductList.remove(iTemp);
							orderCount++;
							tempOrderProduct.setOrderCount(orderCount);
							tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductPrice());
						}else {
							tempOrderBottle.setOrderProductSeq(orderProductSeq);
							
							tempOrderProduct.setOrderCount(1);
							tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());						
							tempOrderProduct.setOrderProductSeq(orderProductSeq++);
						}					
						
					}else {
						tempOrderBottle.setOrderProductSeq(orderProductSeq);
						
						tempOrderProduct.setOrderProductSeq(orderProductSeq++);
						tempOrderProduct.setOrderCount(1);
						tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());
					}
					
					logger.debug("WorkReportServiceImpl registerWorkReportNoOrder tempBottle.getBottleId() =" + tempBottle.getBottleId() );
					
					workBottle.setBottleId(tempBottle.getBottleId());
					workBottle.setBottleBarCd(tempBottle.getBottleBarCd());
					workBottle.setWorkReportSeq(workReportSeq);
					workBottle.setCreateId(param.getCreateId());
					workBottle.setCustomerId(param.getCustomerId());
					workBottle.setBottleWorkCd(param.getBottleWorkCd());
					workBottle.setGasId(tempBottle.getGasId());
					workBottle.setProductId(tempProductTotal.getProductId());
					workBottle.setProductPriceSeq(tempProductTotal.getProductPriceSeq());	
					workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
									
					tempBottle.setBottleWorkCd(param.getBottleWorkCd());
					tempBottle.setBottleWorkId(param.getCreateId());
					tempBottle.setCustomerId(param.getCustomerId());
					tempBottle.setBottleType(workBottle.getBottleType());
					tempBottle.setUpdateId(param.getCreateId());				
					tempBottle.setOrderId(orderId);
					tempBottle.setOrderProductSeq(i+1);
					
					result = bottleService.modifyBottleOrder(tempBottle);
					
					logger.debug("WorkReportServiceImpl registerWorkReportNoOrder *** workBottle.getWorkReportSeq() =" + workBottle.getWorkReportSeq() );
					logger.debug("WorkReportServiceImpl registerWorkReportNoOrder tempBottle.getBottleBarCd() =" + tempBottle.getBottleBarCd() );
					logger.debug("WorkReportServiceImpl registerWorkReportNoOrder orderInfo.getOrder().getCustomerId() =" + param.getCustomerId());
					logger.debug("WorkReportServiceImpl registerWorkReportNoOrder param.getBottleWorkCd() =" + param.getBottleWorkCd() );
					
					// TB_Order_P
					tempOrderBottle.setProductId(tempBottle.getProductId());
					tempOrderBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());
					tempOrderBottle.setBottleId(tempBottle.getBottleId());			
					
					orderBottleList.add(tempOrderBottle);		
					
					//tempGasId = tempBottle.getGasId();
					//tempCapa = tempBottle.getBottleCapa();
					
					orderProductList.add(tempOrderProduct);
					workBottleList.add(workBottle);							
				}
				
				if( bottleList.size() > 1) {
					orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
					orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
				}
				order.setOrderProductNm(orderProductNm);
				order.setOrderProductCapa(orderProductCapa);
				
				for(int k=0; k < orderProductList.size() ; k++) { 
					orderTotalAmount += orderProductList.get(k).getOrderAmount();
				}
				order.setOrderTotalAmount(orderTotalAmount);
				
				//TB_Order 등록
				//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
				// TB_Order_Product			등록
				result  = orderService.registerOrderAndProduct(order, orderProductList);
				
				// Order_Bottle 등록
				if(orderBottleList.size() > 0)
					result = orderService.registerOrderBottles(orderBottleList);
				
				param.setWorkCd(param.getBottleWorkCd());
				param.setWorkProductNm(orderProductNm);
				param.setWorkProductCapa(orderProductCapa);
				param.setOrderId(orderId);
				param.setOrderAmount(orderTotalAmount);
				param.setWorkDt(cal.getTime());
				param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
				
				if(registerFlag) result = workMapper.insertWorkReport(param);
				
				result = workMapper.insertWorkBottles(workBottleList);	
			
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
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList) {
		logger.info("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;
		boolean insertFlag = false;
		try {			
			
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleIds =" + param.getBottlesIds());			
			
			int workReportSeq = 0;
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.0310"))) {
				workReportSeq = getWorkReportSeqForCustomerToday(param);
			}
			//Work_Report_Seq 가져오기
			//int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				insertFlag = true;
			}
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder workReportSeq =" + workReportSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			
			logger.debug("WorkReportServiceImpl registerWorkReportNoOrder bottleWorkCd =" + param.getBottleWorkCd() );
				
			
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
						
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				
				workBottle = new WorkBottleVO();
				
				if(i==0) {
					orderProductNm = tempBottle.getProductNm();
					orderProductCapa = tempBottle.getBottleCapa();
				}
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());				
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setGasId(tempBottle.getGasId());
				workBottle.setProductId(tempBottle.getProductId());
				workBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());	
				workBottle.setBottleType(param.getBottleType());
				
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder *** workBottle.getWorkReportSeq() =" + workBottle.getWorkReportSeq() );
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder tempBottle.getBottleBarCd() =" + tempBottle.getBottleBarCd() );
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder orderInfo.getOrder().getCustomerId() =" + param.getCustomerId());
				logger.debug("WorkReportServiceImpl registerWorkReportNoOrder param.getBottleWorkCd() =" + param.getBottleWorkCd() );

				workBottleList.add(workBottle);							
			}			
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			Calendar cal = Calendar.getInstance();
			param.setWorkDt(cal.getTime());
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderProductNm);
			param.setWorkProductCapa(orderProductCapa);
			
			if(insertFlag) {
				result = workMapper.insertWorkReport(param);
			}
			result = workMapper.insertWorkBottles(workBottleList);			
			
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			return result;
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			return result;
		}
		return result;
	}
	
	@Override
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int registerWorkBottle(WorkBottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerWorkBottleList(List<WorkBottleVO> param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWorkReportSeq() {
		return  workMapper.selectWorkReportSeq();
	}

	@Override
	public Map<String,Object> getWorkBottleListTotal(BottleVO param) {
							  
		logger.info("****** getWorkBottleListToday *****start===*");		
		
				
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
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchChargeDt = param.getSearchChargeDt();	
		
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			map.put("searchChargeDt", searchChargeDt);
			logger.info("****** getWorkBottleListToday *****getSearchChargeDt===*"+param.getSearchChargeDt());
			
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			map.put("searchChargeDtFrom", searchChargeDtFrom);
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;			
			map.put("searchChargeDtEnd", searchChargeDtEnd);
		}		
				
		
		logger.info("****** getWorkBottleListToday *****currentPage===*"+currentPage);		
		
		int bottleCount = workMapper.selectWorBottleCountTotal(map);
		
		logger.debug("****** getWorkBottleListToday.bottleCount *****===*"+bottleCount);
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);
		
		logger.debug("****** getBottleList.lastPage *****===*"+lastPage);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		if(lastPageNum ==0) lastPageNum=1;
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<BottleVO> bottleList = workMapper.selectWorBottleListTotal(map);
		
		logger.debug("****** getWorkBottleListToday.bottleList *****===*"+bottleList.size());
		
		resutlMap.put("list",  bottleList);
		
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);		
		
		return  resutlMap;
	}

	@Override
	public List<BottleVO> getWorkBottleListToday() {
		// TODO Auto-generated method stub
		return workMapper.selectWorBottleListToday();
	}

	@Override
	public int registerWorkReport0310(WorkReportVO param) {
		logger.info("WorkReportServiceImpl registerWorkReport start ");
		int result = 0;
		
		
		try {			
			
			logger.debug("WorkReportServiceImpl registerWorkReport0310 bottleIds =" + param.getBottlesIds());
			
			//Bottle 정보 가져오기
			BottleVO bottle = new BottleVO();
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
			
			List<String> list = null;
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
				param.setBottList(list);
			}	
			
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);			
			
			//Work_Report_Seq 가져오기
			if(bottleList.size() > 0) {
				param.setCustomerId(bottleList.get(0).getCustomerId());
			}
			boolean registerFlag = false;
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				
				workBottle = new WorkBottleVO();
				
				if(i==0) {
					orderProductNm = tempBottle.getProductNm();
					orderProductCapa = tempBottle.getBottleCapa();
				}
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());				
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setGasId(tempBottle.getGasId());
				workBottle.setProductId(tempBottle.getProductId());
				workBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());	
				workBottle.setBottleType(param.getBottleType());
				
				logger.debug("WorkReportServiceImpl registerWorkReport0310 *** workBottle.getWorkReportSeq() =" + workBottle.getWorkReportSeq() );
				logger.debug("WorkReportServiceImpl registerWorkReport0310 tempBottle.getBottleBarCd() =" + tempBottle.getBottleBarCd() );
				logger.debug("WorkReportServiceImpl registerWorkReport0310 orderInfo.getOrder().getCustomerId() =" + param.getCustomerId());
				logger.debug("WorkReportServiceImpl registerWorkReport0310 param.getBottleWorkCd() =" + param.getBottleWorkCd() );

				workBottleList.add(workBottle);							
			}			
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			
			// bottle Bottle_Work_CD 업데이트
			bottle.setBottleIds(param.getBottlesIds());
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setUpdateId(param.getCreateId());
			bottle.setBottleType(param.getBottleType());
			bottle.setCustomerId(param.getCustomerId());
			
			result = bottleService.changeBottlesWorkCdOnly(bottle);
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			param.setWorkReportSeq(workReportSeq);
			Calendar cal = Calendar.getInstance();
			param.setWorkDt(cal.getTime());
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderProductNm);
			param.setWorkProductCapa(orderProductCapa);
			
			if(registerFlag)
				result = workMapper.insertWorkReport(param);			
			
			result = workMapper.insertWorkBottles(workBottleList);	
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			return result;
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			return result;
		}
		/*
		//Bottle 정보 가져오기
		BottleVO bottle = new BottleVO();
		bottle.setBottleIds(param.getBottlesIds());
		bottle.setBottleWorkCd(param.getBottleWorkCd());
		bottle.setCreateId(param.getCreateId());
		bottle.setUpdateId(param.getCreateId());
		bottle.setBottleType(param.getBottleType());
		
		result = bottleService.changeBottlesWorkCd(bottle);
		
		*/
		return result;
		
	}

	@Override
	public int getWorkReportSeqForCustomerToday(WorkReportVO param) {
		// TODO Auto-generated method stub
		return workMapper.selectWorkReportSeqForCustomerToday(param);
	}

	
	private List<BottleVO> getBottleList(WorkReportVO param){
		BottleVO bottle = new BottleVO();
		
		if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
			//bottleIds= request.getParameter("bottleIds");
			List<String> list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
			bottle.setBottList(list);
		}	
	
		
		return  bottleService.getBottleDetails(bottle);		
	}

}
