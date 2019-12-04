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

import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.ResultRowDataHandler;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.common.ExcelDownloadService;
import com.gms.web.admin.service.manage.BottleService;
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

   @RequestMapping(value = "/gms/bottle/excelDownload.do")
   public void excelDownloadBottle(HttpServletResponse response,BottleVO param){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
			List<BottleVO> list = bottleService.getBottleListToExcel(param);
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("용기");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    // 가는 경계선을 가집니다.
		    headStyle.setBorderTop(BorderStyle.THIN);
		    headStyle.setBorderBottom(BorderStyle.THIN);
		    headStyle.setBorderLeft(BorderStyle.THIN);
		    headStyle.setBorderRight(BorderStyle.THIN);
	
		    // 배경색은 노란색입니다.
		    headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		    headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	
		    // 데이터는 가운데 정렬합니다.
		    headStyle.setAlignment(HorizontalAlignment.CENTER);
	
		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    bodyStyle.setBorderTop(BorderStyle.THIN);
		    bodyStyle.setBorderBottom(BorderStyle.THIN);
		    bodyStyle.setBorderLeft(BorderStyle.THIN);
		    bodyStyle.setBorderRight(BorderStyle.THIN);
	
		    // 헤더 생성
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    cell = row.createCell(0);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("용기ID");
		    cell = row.createCell(1);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("용기바코드");
		    cell = row.createCell(2);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("가스명");
		    cell = row.createCell(3);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("용량");
		    cell = row.createCell(4);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("작업");
		    cell = row.createCell(5);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("거래처");
		    cell = row.createCell(6);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("등록일");
	
		   // 용기코드	바코드	가스	용량	작업	거래처	등록일
		    // 데이터 부분 생성
		    for(BottleVO vo : list) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleId());
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleBarCd());
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getGasNm());
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleCapa());
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleWorkCdNm());
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        cell = row.createCell(6);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCreateDt());
	
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Bottle.xls");	
	
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
			List<OrderVO> list = orderService.getOrderListToExcel(param);
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("주문");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    // 가는 경계선을 가집니다.
		    headStyle.setBorderTop(BorderStyle.THIN);
		    headStyle.setBorderBottom(BorderStyle.THIN);
		    headStyle.setBorderLeft(BorderStyle.THIN);
		    headStyle.setBorderRight(BorderStyle.THIN);
	
		    // 배경색은 노란색입니다.
		    headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		    headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	
		    // 데이터는 가운데 정렬합니다.
		    headStyle.setAlignment(HorizontalAlignment.CENTER);
	
		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    bodyStyle.setBorderTop(BorderStyle.THIN);
		    bodyStyle.setBorderBottom(BorderStyle.THIN);
		    bodyStyle.setBorderLeft(BorderStyle.THIN);
		    bodyStyle.setBorderRight(BorderStyle.THIN);
	
		    // 헤더 생성
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    cell = row.createCell(0);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("순번");
		    cell = row.createCell(1);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("거래처명");
		    cell = row.createCell(2);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("상품명");
		    cell = row.createCell(3);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("용량");
		    cell = row.createCell(4);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("접수자");
		    cell = row.createCell(5);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("상태");
		    cell = row.createCell(6);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("요청일자");
		    cell = row.createCell(7);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("접수일");
	
		    //순번	거래처	품명	용량	접수자	상태	요청일자	접수일
		    // 데이터 부분 생성
		    int i = 0;
		    for(OrderVO vo : list) {
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
		        cell.setCellValue(DateUtils.convertDateFormat(vo.getDeliveryReqDt(), "YYYY/MM/DD"));
		        cell = row.createCell(7);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(DateUtils.convertDateFormat(vo.getCreateDt(), "YYYY/MM/DD"));
	
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=order.xls");	
	
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