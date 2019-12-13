package com.gms.web.admin.service.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.mapper.manage.BottleMapper;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.GasService;
import com.gms.web.admin.service.manage.ProductService;

@Service
public class ExcelServiceImpl implements ExcelService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private GasService gasService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Override
	@Transactional
	public int uploadBottleExcelFile(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		
        List<BottleVO> list = new ArrayList<BottleVO>();
        
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
                BottleVO bottle = new BottleVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }
                
                
                //용기	바코드/RFID	가스	품명	용기체적	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                //0		1			2	3	4		5		6		7		8		9		10	11	

                //N:자사소유
                //Y:타사소유
                String colValue="";
               
                String productNm = "";
            	String productCapa = "";
            	
                for(int j=0; j< 12; j++) {
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
                	
                	logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
                	
                	//용기	바코드/RFID	가스	품명	용기체적	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                    //0		1			2	3	4		5		6		7		8		9		10	11	

                    //N:자사소유
                    //Y:타사소유
                	
                	
                	if(j == 0) bottle.setBottleId(colValue);
                	else if(j == 1) bottle.setBottleBarCd(colValue);
                	//else if(j == 2) bottle.setGasId(gasService.getGasDetailsByNm(colValue).getGasId());
                	else if(j == 3) productNm = colValue;
                	else if(j == 4) bottle.setBottleVolumn(colValue);
                	else if(j == 5) {
                		bottle.setBottleCapa(colValue);
                		productCapa = colValue;
                	}
                	//else if(j == 6) bottle.setBottleChargeDt(DateUtils.);
                	else if(j == 7) bottle.setBottleChargePrss(colValue);
                	//else if(j == 8) bottle.setBottleId(colValue);
                	else if(j == 9) {
                		if(customerService.getCustomerDetailsByNm(colValue) != null)
                			bottle.setCustomerId(customerService.getCustomerDetailsByNm(colValue).getCustomerId());
                	}
                	else if(j == 10) bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0388"));
                	else if(j == 11) { 
                		if(colValue.equals("자사")) bottle.setBottleSalesYn("N");
                		else bottle.setBottleSalesYn("Y");
                	}
                }
                
                ProductTotalVO productTotal = new ProductTotalVO();
                productTotal.setProductNm(productNm);
                productTotal.setProductCapa(productCapa);
                
                logger.debug("&&&  ExcelService productNm "+ productNm);
                logger.debug("$$$$$$$$$$$$$$ ExcelService productCapa "+ productCapa);
                
                productTotal = productService.getProductTotalDetails(productTotal);
                
                bottle.setProductId(productTotal.getProductId());
                bottle.setProductPriceSeq(productTotal.getProductPriceSeq());
                bottle.setGasId(productTotal.getGasId());
                RequestUtils.initUserPrgmInfo(request, bottle);
                bottle.setBottleWorkId(bottle.getCreateId());
                
                bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
                bottle.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
              
                list.add(bottle);
            }
            
            result = bottleService.registerBottles(list);
            
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
	public int uploadCustomerExcelFile(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		List<CustomerVO> list = new ArrayList<CustomerVO>();
        
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 8;
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
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
              
                list.add(customer);
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
		
			List<CustomerVO> list = new ArrayList<CustomerVO>();
        
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 128;
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
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
                	/*
                	 * 거래처,본사사업자등록번호,거래처사업자등록번호,소계,
                	 * 1	2				3			4	
                	 * O2(의료용)_40,O2(의료용)_20,O2(의료용)_10,O2(의료용)_5,O2(의료용)_4.6,O2(의료용)_2.8,
                	 * 5			6			7			8			9			10			
                	 * 물통(국산)게이지,헤리스게이지_2.5bar,헤리스게이지_5bar,헤리스게이지_16bar,크라운게이지_O2,크라운게이지_N2O,지요다게이지_O2,지요다게이지_N2,지요다게이지_N2O,삼성게이지,
                	 * 11			12				13				14				15			16			17			18			19			20	
                	
                	 * 플로우메타,1단 받침대,이동카_40L,이동카_20L,이동카_10L,캡,아답타,호스,플렉시블,
                	 * 21		22		23		24		25		26	27	28	29		
                	 * O2_40,O2_10,O2(N45),O2(N50),
                 	 * 30	31		32		33		
                	 * LO2(의료용)_160,LO2(의료용)_80,LO2(의료용)_45,LO2_160,
                	 * 34			35				36			37		
                	 * CO2(의료용)_47,CO2(의료용)_40,CO2(의료용)_20,CO2(의료용)_13.5,CO2(의료용)_10,CO2(의료용)_5,
                	 * 38			39			40				41			42				43			
                	 * CO2_47,CO2_40,CO2_10,CO2_5,CO2(H.P)_47,
                	 * 44	45		46		47		48					
                	 * LN2(의료용)_240,LN2(의료용)_230,LN2(의료용)_160,LN2(의료용)_80,LN2_230,LN2_160,LN2_병,LN2_L,
                	 * 49				50				51			52			53		54		55		56	
                	 * N2(의료용)_40,N2(의료용)_10,N2(의료용)_2.8,
                	 * 57			58			59				
                	 * N2_40,N2_10,N2(N50)_47,N2(N50)_10,N2(N60)_47,
                	 * 60		61	62			63			64			
                	 * 대한E.O가스(HF)_50,대한E.O가스(HF)_25,대한E.O가스_25,대한E.O가스_10,
                	 * 65				66				67			68			
                	 * AC(DMF)_5,AC(일반)_3,AC(일반)_5,
                	 * 69			70		71		
                	 * Air_40,Air_10,Air_4.6,Air(H.P)_47,Air(H.P)_10,	
                	 * 72		73		74		75			76	
                	 * Ar_47,Ar_20,Ar_10,Ar_5,Ar(N50)_47,Ar(N50)_10,Ar(N50)_5,Ar(N60)_47,
                	 * 77	78		79	80		81			82		83			84			
                	 * H2_40,H2_10,H2(N50)_47,H2(N50)_10,H2(N60)_47,H2(N60)_10,
                	 * 85		86		87		88			89			90			
                	 * He_47,He_10,He(N50)_47,He(N50)_10,He(N60)_47,He(N60)_10,
                	 * 91		92		93		94			95			96		
                	 * LAr(160L),LN2_230,LN2_160,LN2(의료용)_240,LN2(의료용)_230,LN2(의료용)_160,LN2(의료용)_80,
                	 * 	97			98		99		100			101				102			103				
                	 * LO2_160,LO2(의료용)_160,LO2(의료용)_80,LO2(의료용)_45,	
                	 * 104		105			106			107	                	
                	 * N2O_25,N2O_20,N2O_15,N2O_18,N2O_5,
                	 * 108		109	110		111		112		
                	 * 냉매가스,특수가스,고순도믹스가스_47,고순도믹스가스_20,고순도믹스가스_10,믹스가스_40,용기검사비,
                	 * 113		114		115			116				117			118		119		
                	 * 용기및가스_47L,용기및가스_40L,용기및가스_35L,용기및가스_20L,용기및가스_13.5L,용기및가스_10L,용기및가스_5L,용기및가스_4.6L,용기및가스_2.8L
                	 * 120			121			122			123			124			125			126			127			128
                	       	
                	//CustomerId / ProductId, Product_Price_Seq
                	*/
                	/*
                	if(j == 0) customer.setCustomerNm(colValue);
                	else if(j == 1) customer.setCustomerAddr(colValue);
                	else if(j == 2) customer.setBusinessRegId(colValue);
                	else if(j == 3) customer.setCustomerPhone(colValue);
                	else if(j == 4) customer.setCustomerRepNm(colValue);
                	else if(j == 5) customer.setCustomerBusiType(colValue);
                	else if(j == 6) customer.setCustomerItem(colValue);
                	else if(j == 7) customer.setCustomerEmail(colValue);
                	
                	*/
                	
                	
                }
                RequestUtils.initUserPrgmInfo(request, customer);
                
               // customer.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
              
                //list.add(customer);
            }
            
           //result = customerService.registerCustomers(list);
            
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
