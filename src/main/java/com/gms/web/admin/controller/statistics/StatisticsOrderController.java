package com.gms.web.admin.controller.statistics;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.statistics.StatisticsBottleVO;
import com.gms.web.admin.domain.statistics.StatisticsOrderVO;
import com.gms.web.admin.service.statistics.StatisticsOrderService;
import com.gms.web.admin.service.statistics.StatisticsProductService;

@Controller
public class StatisticsOrderController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	
	@Autowired
	private StatisticsOrderService statService;
	
	
	@RequestMapping(value = "/gms/statistics/order/daily.do")
	public ModelAndView getStatisticsOrderDaily(StatisticsOrderVO params) {

		logger.info("StatisticsOrderContoller getStatisticsOrderDaily");
		logger.info("StatisticsOrderContoller searchStatisticsOrderDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchOrderId = params.getSearchOrderId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {						
			
			searchStatDtFrom = DateUtils.getNextDate(-30,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			
			params.setSearchStatDt(searchStatDt);
		}		
		
		List<StatisticsOrderVO> statOrderList = statService.getDailylStatisticsOrderList(params);
		
		mav.addObject("statList", statOrderList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);			
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_order"));	 		
		
		mav.setViewName("/gms/statistics/order/daily");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/order/monthly.do")
	public ModelAndView getStatisticsOrderMonthly(StatisticsOrderVO params) {

		logger.info("StatisticsOrderContoller getStatisticsOrderMonthly");
		logger.info("StatisticsOrderContoller searchStatisticsOrderDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchOrderId = params.getSearchOrderId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {						
			
			searchStatDtFrom = DateUtils.getNextDate(-365,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderMonthly *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderMonthly *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			params.setSearchStatDt(searchStatDt);
		}		
		
		List<StatisticsOrderVO> statOrderList = statService.getMontlylStatisticsOrderList(params);
		
		mav.addObject("statList", statOrderList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);			
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_order"));	 		
		
		mav.setViewName("/gms/statistics/order/monthly");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/order/excelDown.do")
	public void excelDownloadBottle(HttpServletResponse response,StatisticsOrderVO params){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
		   String searchStatDt = params.getSearchStatDt();	
			
			String searchStatDtFrom = null;
			String searchStatDtEnd = null;
					
			if(searchStatDt != null && searchStatDt.length() > 20) {						
				searchStatDtFrom = searchStatDt.substring(0, 10) ;			
				searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
				
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);			
			}else {						
					
				searchStatDtFrom = DateUtils.getNextDate(-30,"yyyy/MM/dd");
				logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
		
				searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
				logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
		
				
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				params.setSearchStatDt(searchStatDt);
			}		
			
			List<StatisticsOrderVO> statOrderList = null;
			
			String fileName="StatisticsOrder";
			if(params.getPeriodType()==1) {
				statOrderList= statService.getDailylStatisticsOrderList(params);
				fileName +="Daily_"+DateUtils.getDate()+".xls";
			}else {
				statOrderList = statService.getMontlylStatisticsOrderList(params);
				fileName +="Monthly_"+DateUtils.getDate()+".xls";
			}
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
		   
		    // 헤더 생성 날짜		주문건수	주문금액	취소주문	회수요청	점검요청	기타요청
		    //			0	1		2		3		4		5		6
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.order.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		   // 날짜		주문건수	주문금액	취소주문	회수요청	점검요청	기타요청
		    // 데이터 부분 생성
		    for(StatisticsOrderVO vo : statOrderList) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getStatDt());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderTotalCount());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderTotalAmount());
		        
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderCancelCount());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderRetrievedCount());
		        
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderCheckCount());
		        
		        cell = row.createCell(6);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderEtcCount());
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    
		    
		    response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
	
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
