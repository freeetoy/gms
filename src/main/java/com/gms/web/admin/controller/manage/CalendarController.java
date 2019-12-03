package com.gms.web.admin.controller.manage;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gms.web.admin.common.utils.utilClass;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CalendarParam;
import com.gms.web.admin.domain.manage.CalendarVO;
import com.gms.web.admin.service.manage.CalendarService;
import com.gms.web.admin.service.manage.OrderService;



@Controller
public class CalendarController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CalendarService calendarService;

	@RequestMapping(value = "/gms/calendar/list.do")
	public String getCalendarList(Model model, HttpServletRequest req, String year, String month) {

		logger.info("CalendarContoller getCalendarList");
		
		
		utilClass util = new utilClass();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);

        /*
        String syear = request.getParameter("year");
        String smonth = request.getParameter("month");
		*/
        
        int yearn = cal.get(Calendar.YEAR);
        if(util.nvl(year) == false){	// 파라메타가 넘어왔을때
        	yearn = Integer.parseInt(year);
        }
        int monthn = cal.get(Calendar.MONTH) + 1;
        if(util.nvl(month) == false){
        	monthn = Integer.parseInt(month);
        }

        if(monthn < 1){
        	monthn = 12;
        	yearn--;
        }
        if(monthn > 12){
        	monthn = 1;
        	yearn++;
        }

        cal.set(yearn, monthn - 1, 1);

        //요일
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        //<<	year--
        String pp = String.format("<a href='%s?year=%d&month=%d'><img src='image/left.gif'></a>", 
        					"calendar.do", yearn-1, monthn);

        //<		month--
        String p = String.format("<a href='%s?year=%d&month=%d'><img src='image/prec.gif'></a>", 
        					"calendar.do", yearn, monthn-1);

        //>		month++
        String n = String.format("<a href='%s?year=%d&month=%d'><img src='image/next.gif'></a>", 
        					"calendar.do", yearn, monthn+1);

        //>>	year++
        String nn = String.format("<a href='%s?year=%d&month=%d'><img src='image/last.gif'></a>", 
        					"calendar.do", yearn+1, monthn);


		
		HttpSession session = req.getSession();
		
		//RequestUtils.initUserPrgmInfo(request, params);		
		//MemberDto user = (MemberDto)session.getAttribute("login");

		CalendarParam param = new CalendarParam("freetoy", yearn+util.two(monthn+""));
		
		List<CalendarVO> list = calendarService.getCalendarList(param);
		
		//logger.info(param.toString());
		//logger.info(list.toString());
		
		model.addAttribute("pp", pp);
		model.addAttribute("p", p);
		model.addAttribute("n", n);
		model.addAttribute("nn", nn);
		
		model.addAttribute("year", yearn);
		model.addAttribute("month", monthn);
		
		model.addAttribute("dayOfWeek", dayOfWeek);
		
		model.addAttribute("cal", cal);
		
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		model.addAttribute("lastDay", lastDay);
		model.addAttribute("list", list);
		
		
		return "gms/calendar/list";
	}
	
}
