package com.gms.web.admin.service.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.ProductService;

@Service
public class ExcelServiceImpl implements ExcelService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Override
	@Transactional
	public int uploadBottleExcelFile(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		
        List<BottleVO> list = new ArrayList<BottleVO>();
        
        List<BottleVO> bottlelist = bottleService.getBottleListAll();
        
        int result = 0;
        int updateCount = 0;
        int insertCount = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            boolean isRegisteFlag = false;
            StringBuffer sb = new StringBuffer();
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
            	isRegisteFlag = true;
            	
                BottleVO bottle = new BottleVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                
                
              //용기	바코드/RFID	가스	품명	용기체적	가스용량	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                //0		1			2	3	4		5		6		7		8		9		10		11	12

                //N:자사소유                //Y:타사소유                
                
                String colValue="";
               
                String productNm = "";
            	String productCapa = "";
            	
                for(int j=0; j< 13; j++) {
                	XSSFCell cell = row.getCell(j);
                //if(null != cell) fruit.setGasNm(cell.getNumericCellValue());
                	//logger.debug("ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        //logger.debug("ExcelSerive uploadExcelFile j ==="+ j);
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();
	                            
	                            if(j==6) bottle.setBottleChargeDt(cell.getDateCellValue());
	                            else if(j==8) bottle.setBottleCreateDt(cell.getDateCellValue());
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	
                	//logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
                	
                	//용기	바코드/RFID	가스	품명	용기체적	가스용량	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                    //0		1			2	3	4		5		6		7		8		9		10		11	12

                    //N:자사소유
                    //Y:타사소유                	
                	
                	if(j == 0) bottle.setBottleId(colValue);
                	else if(j == 1) bottle.setBottleBarCd(colValue);
                	else if(j == 2) bottle.setGasCd(colValue);
                	else if(j == 3) productNm = colValue;
                	else if(j == 4) bottle.setBottleVolumn(colValue);
                	else if(j == 5) {
                		bottle.setBottleCapa(colValue);
                		productCapa = colValue;
                	}
                	else if(j == 6) bottle.setChargeCapa(colValue);
                	else if(j == 7) {
                		if(colValue!=null && colValue.length() > 9) {
	                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	                		Date date = sdf.parse(colValue);
	                		
	                		bottle.setBottleChargeDt(date);
                		}
                	}
                	else if(j == 8) bottle.setBottleChargePrss(colValue);
                	else if(j == 9) {
                		if(colValue!=null && colValue.length() > 9) {
	                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	                		Date date = sdf.parse(colValue);
	                		
	                		bottle.setBottleCreateDt(date);
                		}
                	}
                	else if(j == 10) {
                		CustomerVO customer = customerService.getCustomerDetailsByNm(colValue) ;
                		if(customer != null &&  customer.getCustomerId()!=null)
                			bottle.setCustomerId(customerService.getCustomerDetailsByNm(colValue).getCustomerId());
                	}
                	else if(j == 11) bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0388"));
                	else if(j == 12) { 
                		if(colValue.equals("자사")) bottle.setBottleOwnYn("N");
                		else bottle.setBottleOwnYn("Y");
                	}
                }
                
                ProductTotalVO productTotal = new ProductTotalVO();
                productTotal.setProductNm(productNm);
                productTotal.setProductCapa(productCapa);
                
                //logger.debug("&&&  ExcelService productNm "+ productNm);
                //logger.debug("$$$$$$$$$$$$$$ ExcelService productCapa "+ productCapa);
                
                productTotal = productService.getProductTotalDetails(productTotal);
                
                if(productTotal != null) {
	                
	                bottle.setProductId(productTotal.getProductId());
	                bottle.setProductPriceSeq(productTotal.getProductPriceSeq());
	                bottle.setGasId(productTotal.getGasId());
	                RequestUtils.initUserPrgmInfo(request, bottle);
	                bottle.setBottleWorkId(bottle.getCreateId());
	                
	                bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
	                bottle.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
	              
	                for(int k=0 ; k < bottlelist.size() ; k++) {
	                	if(bottle.getBottleId().equals(bottlelist.get(k).getBottleId())) {
	                		isRegisteFlag = false;
	                		result = bottleService.modifyBottle(bottle);
	                		updateCount++;
	                	}
	                		
	                }
	                if(isRegisteFlag) {
	                	list.add(bottle);
	                	//result = bottleService.registerBottle(bottle);
	                	insertCount++;
	                }
                }else {
                	sb.append(bottle.getBottleId());
                	sb.append(";");
                	
                }
            }
            logger.info("$$$$$$$$$$$$$$ ExcelService sb "+ sb.toString());
            result = bottleService.registerBottles(list);
            
            logger.debug("$$$$$$$$$$$$$$ ExcelService result "+ result+"==updateCount ="+updateCount+" insertCount=="+insertCount);
            
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
	public int uploadCustomerExcelFile(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		List<CustomerVO> list = new ArrayList<CustomerVO>();
		List<CustomerVO> customerList = customerService.searchCustomerListExcel("");
		
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 8;
            boolean isRegisteFlag = false;
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
            	isRegisteFlag = true;
            	
            	CustomerVO customer = new CustomerVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
                
                //	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
                //		0		1		2				3	4		5	6	7	
                             
                String colValue="";               
                
                for(int j=0; j< COLUMN_COUNT; j++) {
                	XSSFCell cell = row.getCell(j);
                //if(null != cell) fruit.setGasNm(cell.getNumericCellValue());
                	//logger.debug("ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();	                            
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	
                	logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
                	
                	//사업자등록번호	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
                    //			0		1		2				3		4		5	6	7	
                	if(j == 0) customer.setCustomerNm(colValue);
                	else if(j == 1) customer.setCustomerAddr(colValue);
                	else if(j == 2) customer.setBusinessRegId(colValue);
                	else if(j == 3) customer.setCustomerPhone(colValue);
                	else if(j == 4) customer.setCustomerRepNm(colValue);
                	else if(j == 5) customer.setCustomerBusiType(colValue);
                	else if(j == 6) customer.setCustomerItem(colValue);
                	else if(j == 7) customer.setCustomerEmail(colValue);
                	
                }
                RequestUtils.initUserPrgmInfo(request, customer);
                
                customer.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
              
                for(int k=0;k<customerList.size();k++) {
                	if(customer.getCustomerNm().equals(customerList.get(k).getCustomerNm())) {
                		customer.setCustomerId(customerList.get(k).getCustomerId());
                		boolean result1 = customerService.modifyCustomer(customer);
                		isRegisteFlag = false;
                	}                 		
                }
                
                if(isRegisteFlag) list.add(customer);
            }
            
            result = customerService.registerCustomers(list);
            
            logger.debug("$$$$$$$$$$$$$$ ExcelService result "+ result);
            
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
	public int uploadCustomerPriceExcelFile(MultipartHttpServletRequest request, 
			MultipartFile excelFile) {
		
			List<CustomerPriceVO> list = new ArrayList<CustomerPriceVO>();
        
        int result = 0;
        try {
        	
        	List<CustomerPriceVO> cPriceList = customerService.getCustomerPriceListAll();
        	boolean isRegisteFlag = false;
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 144;
            
            String strProductPrice="";


            List<ProductTotalVO> productList = productService.getProductTotalList();
            
            List<String> strlist = null;
            Map<String, Object> map = new HashMap<String, Object>();
            
            // 상품정보 가져옴
        	XSSFRow row1 = sheet.getRow(0);
        	  
        	for(int j=3; j< COLUMN_COUNT; j++) {
        		
        		isRegisteFlag = true;
        		XSSFCell cell = row1.getCell(j);
        		
        		switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                    	strProductPrice = cell.getRichStringCellValue().getString();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                        	strProductPrice = cell.getDateCellValue().toString();	                            
                        } else {
                            Long roundVal = Math.round(cell.getNumericCellValue());
                            Double doubleVal = cell.getNumericCellValue();
                            if (doubleVal.equals(roundVal.doubleValue())) {
                            	strProductPrice = String.valueOf(roundVal);
                            } else {
                            	strProductPrice = String.valueOf(doubleVal);
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                    	strProductPrice = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                    	strProductPrice = cell.getCellFormula();
                        break;
         
                    default:
                    	strProductPrice = "";
        		} 
        		
        		//strProductPrice = cell.getRichStringCellValue().getString();
        		
        		strlist = StringUtils.makeForeach(strProductPrice, "_"); 	
        		/*
        		logger.debug("ExcelSerive uploadExcelFile strProductPrice =="+j+"=="+ strProductPrice);
        		logger.debug("ExcelSerive uploadExcelFile strlist.size() =="+j+"=="+ strlist.size());
        		for(int k=0;k<strlist.size();k++) {
        			logger.debug("ExcelSerive uploadExcelFile starlist =="+k+"=="+ strlist.get(k).toString());
        		}
        		*/
        		String key = Integer.toString(j-3);
        		map.put(key, strlist);
        	}
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	logger.debug("ExcelSerive uploadExcelFile i=1 ==");
            	
            	CustomerVO customer = null;
            	CustomerVO tempcustomer = new CustomerVO();
            	
            	
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
                
               	/*
            	1	2	3	
            	거래처명	거래처 사업자등록번호	소계	
            	4	5	6	7	8	9	10
            	O2(의료용)_2.8	O2(의료용)_4.6	O2(의료용)_5	O2(의료용)_10	O2(의료용)_20	O2(의료용)_35	O2(의료용)_40	
            	11	12	13	14	15
            	O2_2.8	O2_5	O2_10	O2_35	O2_40	
            	16	17
            	O2(N45)_10	O2(N45)_47	
            	18	19
            	O2(N50)_10	O2(N50)_47	
            	20	21	22	23	24	25
            	CO2(의료용)_5L	CO2(의료용)_10L	CO2(의료용)_13.5L	CO2(의료용)_20L	CO2(의료용)_20Kg	CO2(의료용)_40Kg	
            	26	27	28	29	30	31
            	CO2_5L	CO2_10L	CO2_20L	CO2_18Kg	CO2_20Kg	CO2_40Kg	
            	31	32
            	CO2(H.P)_10	CO2(H.P)_47	
            	33	34	35	36	37
            	N2(의료용)_2.8	N2(의료용)_4.6	N2(의료용)_10	N2(의료용)_40	N2(의료용)_47	
            	38	39	40
            	N2_10	N2_40	N2_47	
            	41	42
            	N2(N50)_10	N2(N50)_47	
            	43	44
            	N2(N60)_10	N2(N60)_47	
            	45	46	47	48
            	Air_10	Air_4.6	Air_40	Air_47	
            	49	50
            	Air(H.P)_10	Air(H.P)_47	
            	51	52	53	54	55
            	Ar_5	Ar_10	Ar_20	Ar_46(삭제)	Ar_47	
            	56	57	58
            	Ar(N50)_5	Ar(N50)_10	Ar(N50)_47	
            	59	60
            	Ar(N60)_10	Ar(N60)_47	
            	61	62
            	H2_10	H2_40	
            	63	64
            	H2(N50)_10	H2(N50)_47	
            	65	66
            	H2(N60)_10	H2(N60)_47	
            	67	68	69	70	71	72	73
            	HE_10	HE_47	He(N50)_10	He(N50)_47	He(N60)_10	He(N60)_47	
            	74
            	LAr(160L)_160	
            	75	76	77	78	79	80
            	LN2_100	LN2_160	LN2_230	LN2_240	LN2_병	LN2_L	
            	81	82	83	84
            	LN2(의료용)_80	LN2(의료용)_160	LN2(의료용)_230	LN2(의료용)_240	
            	85	86	87	88	89	90
            	LO2_80	LO2_160	LO2(의료용)_40	LO2(의료용)_45	LO2(의료용)_80	LO2(의료용)_160	
            	91	92	93	94	95	96
            	N2O_3kg	N2O_5Kg	N2O_15kg	N2O_18kg	N2O_20kg	N2O_25kg	
            	97	98	99	100	101
            	고순도믹스가스_2.8	고순도믹스가스_4.6	고순도믹스가스_10	고순도믹스가스_20(삭제)	고순도믹스가스_47	
            	102	103	104	105
            	대한E.O가스_10	대한E.O가스_25	대한E.O가스(HF)_25	대한E.O가스(HF)_50	
            	106	107	108	109
            	믹스가스_10	믹스가스_20	믹스가스_40	믹스가스_47	
            	110	111
            	AC(DMF)_3(수정)	AC(DMF)_5(수정)	
            	112	113	114
            	AC(일반)_3	AC(일반)_5	AC(일반)_41(삭제)	
            	115
            	냉매가스	
            	116
            	물통(국산)게이지	
            	117
            	삼성게이지	
            	118
            	아답타	
            	119	120	121	122	123	124	125	126	127
            	용기및가스_2.8	용기및가스_4.6	용기및가스_5	용기및가스_10	용기및가스_13.5	용기및가스_20	용기및가스_35	용기및가스_40	용기및가스_47	
            	128	129	130
            	이동카_10	이동카_20	이동카_40	
            	131	132	133
            	지요다게이지_N2	지요다게이지_N2O	지요다게이지_O2	
            	134
            	캡	
            	135	136
            	크라운게이지_N2O	크라운게이지_O2	
            	137
            	특수가스	
            	138
            	플렉시블	
            	139
            	플로우메타	
            	140	141	142	143
            	헤리스게이지_2.5	헤리스게이지_5	헤리스게이지_10(삭제)	헤리스게이지_16	
            	144	145
            	호스_고압	호스_트윈	
            	146
            	용기검사비	
            	147
            	1단 받침대
*/
  
                String colValue="";       
                int productPrice = 0;
                String customerNm ="";
            	String businessRegId="";
            	
            	
                for(int j=0; j< COLUMN_COUNT; j++) {
                	CustomerPriceVO customerPrice = new CustomerPriceVO();
                	
                	RequestUtils.initUserPrgmInfo(request, customerPrice);
                	productPrice = 0;
                	XSSFCell cell = row.getCell(j);
                
                	//logger.debug(" *** ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();	                            
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            productPrice = (int)cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	
                	logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ productPrice);
                	
                	if(j == 0) customerNm = colValue;
                	else if(j==1) businessRegId = colValue;
                	
                	if(j==2) {
                		logger.debug("ExcelSerive uploadExcelFile customerNm j =="+j+"=="+ customerNm);                	
                		logger.debug("ExcelSerive uploadExcelFile businessRegId j =="+j+"=="+ businessRegId);

	                	tempcustomer.setCustomerNm(customerNm);
	                	tempcustomer.setBusinessRegId(businessRegId);
	                	
                		customer = customerService.getCustomerDetailsByNmBusi(tempcustomer);
                		if(customer!=null)
                			RequestUtils.initUserPrgmInfo(request, customer);
                			logger.debug("ExcelSerive uploadExcelFile customerId ** =="+ customer.getCustomerId());
                	
                	}
                	
					if(j>2 && colValue.length() > 0) {
						
						ProductTotalVO productTotal = productList.get(j-3);
						logger.debug("&& ExcelSerive uploadExcelFile getProductNm j =="+j+"=="+ productTotal.getProductNm());
						logger.debug("&& ExcelSerive uploadExcelFile getProductCapa j =="+j+"=="+ productTotal.getProductCapa());
					
					
						customerPrice.setProductId(productTotal.getProductId());
						customerPrice.setProductPriceSeq(productTotal.getProductPriceSeq());
						customerPrice.setProductPrice(productPrice);        
					
						if(customer != null) {
							customerPrice.setCustomerId(customer.getCustomerId());
							 for(int k=0;k<cPriceList.size();k++) {
			                	if(customerPrice.getCustomerId() == cPriceList.get(k).getCustomerId() 
			                			&& customerPrice.getProductId() == cPriceList.get(k).getProductId()
			                			&& customerPrice.getProductPriceSeq() == cPriceList.get(k).getProductPriceSeq()) {
			                		
			                		result = customerService.modifyCustomerPrice(customerPrice);
			                		isRegisteFlag = false;
			                	}                 		
			                }
							
							logger.debug("&& ExcelSerive uploadExcelFile customerId j =="+j+"=="+ customerPrice.getCustomerId());
							if(isRegisteFlag) {
								list.add(customerPrice);
								
								if(list.size()%500==0) {
									result = customerService.registerCustomerPrices(list);
									list.clear();
								}
								
							}
						}
						
					}                	
                }
            }
            
           //result = customerService.registerCustomerPrices(list);
            /*
            for(int i=0 ; i< list.size();i++) {
            	CustomerPriceVO temp = list.get(i);
            	logger.debug("---- ExcelSerive uploadExcelFile customerId="+ temp.getCustomerId());
            	logger.debug("ExcelSerive uploadExcelFile productId="+ temp.getProductId());
            	logger.debug("ExcelSerive uploadExcelFile productPriceId="+ temp.getProductPrice());
            	logger.debug("---ExcelSerive uploadExcelFile price="+ temp.getProductPrice());
            }
            */
            if(list.size() > 0) result = customerService.registerCustomerPrices(list);
            logger.debug("$$$$$$$$$$$$$$ ExcelService result "+ result);
            
    } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
    	e.printStackTrace();
	} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
		e.printStackTrace();
	}
	return result;
}


	
	public void readExcelXLSX(String excel) {
        try {
            String[] tempFileName = excel.split("/");
            String fileName = tempFileName[excel.split("/").length - 1];
 
           
            File file = new File(excel.trim());
 
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }
 
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
 
            makeExcelToObject(wb.getSheetAt(0), "xlsx", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void makeExcelToObject(Object sheet, String flag, String fileName) throws IOException {
        Row titles = null;
 
        for (Row row : (flag.equals("xls") ? (HSSFSheet) sheet : (XSSFSheet) sheet)) {
            if (row.getRowNum() == 0) {
                titles = row;
                continue;
            }
 
            updateData(titles, row);
        }
    }
    
    @Transactional
    private void updateData(Row titles, Row row) throws IOException {
        String colName = null;
        String colValue = null;
        String name = null;
        String value = null;
 
        // FOR Section (S)
        for (Cell cell : row) {
            if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
                break;
            }
 
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();
 
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                colValue = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    colValue = cell.getDateCellValue().toString();
                } else {
                    Long roundVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (doubleVal.equals(roundVal.doubleValue())) {
                        colValue = String.valueOf(roundVal);
                    } else {
                        colValue = String.valueOf(doubleVal);
                    }
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                colValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                colValue = cell.getCellFormula();
                break;
 
            default:
                colValue = "";
            }
 
            
            
            colValue = colValue.trim();
 
            if (colName.equals("NAME")) {
                //if (!StringUtil.nullToBlank(colValue).isEmpty()) {
                    name = colValue;
                //}
            }
 
            if (colName.equals("VALUE")) {
                //if (!StringUtil.nullToBlank(colValue).isEmpty()) {
                    value = colValue;
                //}
            }
        }
        // FOR Section (E)
 
        // TODO : DAO 영역 메소드 호출 후 Data insert 또는 update 로직 구현
        // excel로부터 읽어들인 name, value 값을 DB에 insert하거나 update하는 로직을 구현할 수 있다.
 
    }


	

}
