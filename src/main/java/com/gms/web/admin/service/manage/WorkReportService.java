package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;

public interface WorkReportService {

	public List<WorkReportVO> getWorkReportList(WorkReportVO param);	
	
	public List<WorkReportViewVO> getWorkReportList1(WorkReportVO param);	
	
	public List<WorkReportViewVO> getWorkReportListAll(WorkReportVO param);	
	
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq);	
	
	public Map<String,Object> getWorkBottleListTotal(BottleVO param);	
	
	public List<BottleVO> getWorkBottleListToday(BottleVO param);	
	
	public int getWorkReportSeq() ;
	
	public int getWorkReportSeqForCustomerToday(WorkReportVO param) ;
	
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList);
	
	public int registerWorkReportForOrder(WorkReportVO param);
	
	public int registerWorkReportNoOrder(WorkReportVO param);
	
	public int registerWorkReport0310(WorkReportVO param);
	
	public int registerWorkBottle(WorkBottleVO param);
	
	public int registerWorkBottleList(List<WorkBottleVO> param);
}
