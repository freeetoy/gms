package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

public interface WorkReportService {

	public List<WorkReportVO> getWorkReportList(WorkReportVO param);	
	
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq);	
	
	public int getWorkReportSeq() ;
	
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList);
	
	public int registerWorkReportForOrder(WorkReportVO param);
	
	public int registerWorkReportNoOrder(WorkReportVO param);
	
	public int registerWorkBottle(WorkBottleVO param);
	
	public int registerWorkBottleList(List<WorkBottleVO> param);
}
