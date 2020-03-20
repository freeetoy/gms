package com.gms.web.admin.common.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionCheckInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//log.debug("preHandle!!");
		String requestURI = request.getRequestURI().trim();
		
		/*
		String[] menu = requestURI.split("/");
		String currentMenu ="";
		
		if(menu.length > 1) {
			log.debug("#### requestURI menu : " + menu[2]);
			currentMenu = menu[2];
		}
        */
		log.debug("requestURI : " + requestURI);
        
		HttpSession session = request.getSession(false);
        
        String userId = "";
        String systemRole = "";
        boolean isAuthChk = false;
        String forwardUrl = "gms/login";
        
        LoginUserVO sessionInfo = SessionUtil.getSessionInfo(request);
        //String userTempId= session.getAttribute("userId").toString();
        //log.info("preHandle userTempId ************ "+userTempId);
        
        if(sessionInfo != null) {  
            
            userId 		= StringUtils.defaultString(sessionInfo.getUserId());
            systemRole 	= StringUtils.defaultString(sessionInfo.getUserAuthority());
            log.info("preHandle userId ************ "+userId);
            //log.debug("preHandle userNm ************ "+StringUtils.defaultString(sessionInfo.getUserNm()));
            //log.debug("preHandle userPartCd ************ "+StringUtils.defaultString(sessionInfo.getUserPartCd()));
            //log.debug("preHandle systemRole ************ "+systemRole);
                      
            session.setAttribute("compNm", PropertyFactory.getProperty("common.Member.Comp.Daehan.name"));		
            
            // Session에 있는 ID가 존재하는지 확인하여 없으면, 강제 로그아웃 처리
            if ("".equals(userId) || "".equals(systemRole)) {
                request.getSession().invalidate();
				response.sendRedirect(request.getContextPath() + "/" + forwardUrl);
				return false;   				
            }
        }
        else {   
        	log.debug("preHandle userId ************null ");
        	
        	//request.getSession().invalidate();
        	response.sendRedirect(request.getContextPath() + "/login");
			return false;   	
			
        }
        
        //return super.preHandle(request, response, handler);
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.debug("postHandle!!");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.debug("afterCompletion!!");
	}

}
