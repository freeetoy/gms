package com.gms.web.admin.controller.common;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.ECountVO;
import com.gms.web.admin.service.manage.ECountService;

@Controller
public class ECountController {
	
	@Autowired
	private ECountService eService;
	
	@RequestMapping(value = "/gms/excelDownloadEcount.do")
   public void excelDownloadBottle(HttpServletResponse response){
	// 게시판 목록조회

	   try {			   
		   
		   // 가스 정보 불러오기
			List<ECountVO> eList = eService.getECountList();
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("판매입력");
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
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.ecount.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		   
		    //일자,순번,거래처코드,거래처명,담당자,출하창고,거래유형,통화,환율,수금내역,비고,품목코드,품목명,규격,수량,단가,외화금액,공급가액,부가세,적요,단가(VAT포함),생산전표생성,Ecount
            // 0   1	2	  3		4		5	6	  7	  8		9  10	11   12   13  14 15	  16    17    18   19  20          21      22
	
		    String curDate = DateUtils.getDate("yyyy-MM-dd");
		    int excelIndex = 0;
		    // 데이터 부분 생성
		    for(ECountVO vo : eList) {
		    	excelIndex++;
		    	int k=0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        
		        //일자
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(curDate);
		        
		        //순번			        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(Integer.toString(excelIndex));
		        
		        //거래처코드
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerId().toString());
		        
		        //거래처명
		        cell = row.createCell(k++);;
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        
		        //담당자
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSalesNm());
		        
		        //출하창고
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getWareHouse());
		        
		        //거래유형	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getDealType());
		        
		        //통화	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCurrency());
		        
		        //환율	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getChangetRate());
		        
		        //수금내역	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCollectionContents());
		        
		        //비고	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());
		        
		        //품목코드	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductId());
		        
		        //품목명	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductNm());
		        
		        //규격	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductCapa());
		        
		        //수량	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderCount());
		        
		        //단가	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductPrice());
		        
		        //외화금액
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCurrencyPrice());			        
		       
		        //공급가액	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSupplyPrice());
		        
		        //부가세	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getVat());
		        
		        //적요	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSummary());
		        
		        // 단가(VAT포함)	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getPrdouctPriceVAT());
		        
		        //생산전표생성	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getReceipt());
		        
		       //Ecount
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEcountString());
	
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Ecount"+DateUtils.getDate()+".xls");	
	
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
