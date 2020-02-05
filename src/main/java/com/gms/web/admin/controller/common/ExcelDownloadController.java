package com.gms.web.admin.controller.common;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.ResultRowDataHandler;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.common.ExcelDownloadService;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.GasService;
import com.gms.web.admin.service.manage.OrderService;

@Controller
public class ExcelDownloadController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private GasService gasService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;


   @RequestMapping(value = "/gms/bottle/excelDownload.do")
   public void excelDownloadBottle(HttpServletResponse response, BottleVO param){
	// 게시판 목록조회

	   try {
		   
		   logger.info("BottleContoller searchGasId "+ param.getSearchGasId());
		   // 가스 정보 불러오기
			List<BottleVO> bottleList = bottleService.getBottleListToExcel(param);
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("용기");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		    
		    // 헤더 생성
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.bottle.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		   
			    sheet.autoSizeColumn(i);
		    }
		   
		    //용기	바코드/RFID	가스	가스용량	용기체적	충전용량	충전기한	충전압력	제조일	거래처	작업		소유	
            //0		1			2	3		4		5		6		7		8		9		10		11	
	
		    // 데이터 부분 생성
		    for(BottleVO vo : bottleList) {
		    	int k=0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleId());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleBarCd());
		        
		        cell = row.createCell(k++);;
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductNm());
		        
		        cell = row.createCell(k++);;
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleCapa());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleVolumn());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getChargeCapa());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getBottleChargeDt() !=null) 
		        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleChargeDt(),"yyyy-MM-dd"));
		        else
		        	cell.setCellValue("");
		       
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleChargePrss());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getBottleCreateDt()!=null)
		        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleCreateDt(),"yyyy-MM-dd"));
		        else
		        	cell.setCellValue("");
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleWorkCdNm());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleOwnYn());
	
		    }	
		    // width 자동조절
			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
				sheet.autoSizeColumn(x);
				int width = sheet.getColumnWidth(x);
				int minWidth = list.get(x).getBytes().length * 450;
				int maxWidth = 18000;
				if (minWidth > width) {
					sheet.setColumnWidth(x, minWidth);
				} else if (width > maxWidth) {
					sheet.setColumnWidth(x, maxWidth);
				} else {
					sheet.setColumnWidth(x, width + 2000);
				}
			}
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Bottle_"+DateUtils.getDate()+".xls");	
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	}
      
   @RequestMapping(value = "/gms/order/excelDownload.do")
   public void excelDownloadOrder(HttpServletResponse response,OrderVO param){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
			List<OrderVO> orderlist = orderService.getOrderListToExcel(param);
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("주문");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);
	
		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);		   
		   
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    //순번,거래처명,상품명,용량,접수자,상태,요청일자,접수일
		    // 헤더 생성
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.order.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		    //순번	거래처	품명	용량	접수자	상태	요청일자	접수일
		    // 데이터 부분 생성
		    int i = 1;
		    for(OrderVO vo : orderlist) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(i++);
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderProductNm());
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderProductCapa());
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCreateNm()+"("+vo.getCreateId()+")");
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderProcessCdNm());
		        cell = row.createCell(6);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(DateUtils.convertDateFormat(vo.getDeliveryReqDt(), "yyyy/MM/dd"));
		        cell = row.createCell(7);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(DateUtils.convertDateFormat(vo.getCreateDt(), "yyyy/MM/dd"));
	
		    }	
	
		 // width 자동조절
 			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
 				sheet.autoSizeColumn(x);
 				int width = sheet.getColumnWidth(x);
 				int minWidth = list.get(x).getBytes().length * 450;
 				int maxWidth = 18000;
 				if (minWidth > width) {
 					sheet.setColumnWidth(x, minWidth);
 				} else if (width > maxWidth) {
 					sheet.setColumnWidth(x, maxWidth);
 				} else {
 					sheet.setColumnWidth(x, width + 2000);
 				}
 			}
		 			
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=order_"+DateUtils.getDate()+".xls");	
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	}
   
   @RequestMapping(value = "/gms/customer/excelDownload.do")
   public void excelDownloadCustomer(HttpServletResponse response,CustomerVO param){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
			List<CustomerVO> customerlist = customerService.searchCustomerListExcel(param.getSearchCustomerNm());
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("거래처");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);
	
		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		   
		   
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    //거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
		    //0		1		2				3		4		5	6	7

		    // 헤더 생성
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.customer.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		  //거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
		    //0		1		2				3		4		5	6	7
		    // 데이터 부분 생성
		    int i = 1;
		    
		    for(CustomerVO vo : customerlist) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        
		        
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerAddr());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBusinessRegId());
		        
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerPhone());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerRepNm());
		        
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerBusiType());
		        
		        cell = row.createCell(6);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerItem());
		        
		        cell = row.createCell(7);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerEmail());
	
		    }	
	
		 // width 자동조절
 			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
 				sheet.autoSizeColumn(x);
 				int width = sheet.getColumnWidth(x);
 				int minWidth = list.get(x).getBytes().length * 450;
 				int maxWidth = 18000;
 				if (minWidth > width) {
 					sheet.setColumnWidth(x, minWidth);
 				} else if (width > maxWidth) {
 					sheet.setColumnWidth(x, maxWidth);
 				} else {
 					sheet.setColumnWidth(x, width + 2000);
 				}
 			}
		 			
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=customer_"+DateUtils.getDate()+".xls");	
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	}

}