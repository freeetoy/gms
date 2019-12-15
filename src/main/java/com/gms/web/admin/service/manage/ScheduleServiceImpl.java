package com.gms.web.admin.service.manage;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.ScheduleVO;
import com.gms.web.admin.mapper.manage.ScheduleMapper;

@Service
public class ScheduleServiceImpl implements ScheduleService {
	
private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ScheduleMapper scheduleMapper;

	@Override
	public List<ScheduleVO> getScheduleList() {
		// TODO Auto-generated method stub
		return scheduleMapper.selectScheduleList();
	}

	@Override
	public List<ScheduleVO> getScheduleListUser(ScheduleVO param) {
		// TODO Auto-generated method stub
		return scheduleMapper.selectScheduleListUser(param);
	}

	@Override
	public ScheduleVO getSchedule(Integer scheduleSeq) {
		// TODO Auto-generated method stub
		return scheduleMapper.selectSchedule(scheduleSeq);
	}

	@Override
	public int registerSchedule(ScheduleVO param) {
		// TODO Auto-generated method stub
		String stringScheduleType="정기휴가";
		
		String strVacationGubun = "";
		
		param.setUserId(param.getCreateId());
		if(param.getScheduleType().equals(PropertyFactory.getProperty("Schedule.Type.Public"))) stringScheduleType = "공휴";
		else if(param.getScheduleType().equals(PropertyFactory.getProperty("Schedule.Type.Holiday"))) stringScheduleType = "공휴일";
		
		if(param.getVacationGubun().equals(PropertyFactory.getProperty("Schedule.vacation.AM"))) {
			strVacationGubun = "오전반차";
			Date startDt = param.getScheduleStartDt();
			
			Date temp = new Date();
			//startDt.get
			startDt.setHours(9);
		}
		else if(param.getVacationGubun().equals(PropertyFactory.getProperty("Schedule.vacation.PM"))) {
			strVacationGubun = "오후반차";
			Date startDt = param.getScheduleStartDt();
			startDt.setHours(13);
		}
		
		param.setScheduleTitle(param.getUserNm()+" "+stringScheduleType+"("+strVacationGubun+")");
		
		return scheduleMapper.insertSchedule(param);
	}

	@Override
	public int modifySchedule(ScheduleVO param) {
		// TODO Auto-generated method stub
		return scheduleMapper.updateSchedule(param);
	}

	@Override
	public int deletelSchedule(Integer scheduleSeq) {
		// TODO Auto-generated method stub
		return scheduleMapper.deleteSchedule(scheduleSeq);
	}

}
