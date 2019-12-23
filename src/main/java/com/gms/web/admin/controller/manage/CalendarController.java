package com.gms.web.admin.controller.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.utilClass;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CalendarParam;
import com.gms.web.admin.domain.manage.CalendarVO;
import com.gms.web.admin.domain.manage.EventVO;
import com.gms.web.admin.domain.manage.ScheduleVO;
import com.gms.web.admin.service.manage.CalendarService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.ScheduleService;



@Controller
public class CalendarController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CalendarService calendarService;
	
	@Autowired
	private ScheduleService scheduleService;

	@RequestMapping(value = "/gms/calendar/calendar.do")
	public String getCalendarList(Model model, HttpServletRequest req, String year, String month) {

		logger.info("CalendarContoller getCalendarList");
		
		List<ScheduleVO> scheduleList = scheduleService.getScheduleList();
		JSONObject obj =  new JSONObject();
        JSONArray jArray = new JSONArray();
        
        
		try {
			
			ScheduleVO temp = new ScheduleVO();					
			
			for(int i=0;i<scheduleList.size();i++) {
				temp = scheduleList.get(i);				
				
				JSONObject sObject = new JSONObject();
				
                sObject.put("title", temp.getScheduleTitle());
                if(temp.getVacationGubun().equals(PropertyFactory.getProperty("Schedule.vacation.All"))) {
                	
                	sObject.put("start", temp.getScheduleStartDt());
                	
                }else {
                	String startH = "";
                	if(temp.getVacationGubun().equals(PropertyFactory.getProperty("Schedule.vacation.AM"))) {
                		startH = "09:00:00";
                	}else {
                		startH = "13:00:00";
                	}    	                	                	
                	//sObject.put("start", DateUtils.convertDateFormat(temp.getScheduleStartDt(),"yyyy-MM-dd")+"T"+startH);
                	sObject.put("start", temp.getScheduleStartDt()+"T"+startH);
                	
                }
                
                if(!temp.getScheduleStartDt().equals(temp.getScheduleEndDt()))
                	sObject.put("end", temp.getScheduleEndDt2());
                	
                sObject.put("groupId", temp.getScheduleSeq());
                //sObject.put("url", "/test/schedule?field="+temp.getScheduleSeq());
                jArray.put(sObject);
				
			}
		
			obj.put("item", jArray);			
			model.addAttribute("list", obj);
			 
		}catch(Exception e) {
			
		}
        //model.addAttribute("scheduleList", scheduleList);
		
		
		return "gms/calendar/calendar";
	}
	
	@RequestMapping("/gms/calendar/getEvent.do")
	@ResponseBody
	public List<ScheduleVO> getEvents() {
		List<ScheduleVO> scheduleList = scheduleService.getScheduleList();		
		
        return scheduleList;
    }
	
	
	
}
