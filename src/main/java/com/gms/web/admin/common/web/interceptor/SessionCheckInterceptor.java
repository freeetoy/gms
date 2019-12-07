package com.gms.web.admin.common.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
		log.info("preHandle!!");
		String requestURI = request.getRequestURI().trim();
		
		
		String[] menu = requestURI.split("/");
		String currentMenu ="";
		
		if(menu.length > 1) {
			log.info("#### requestURI menu : " + menu[2]);
			currentMenu = menu[2];
		}
        
		log.info("requestURI : " + requestURI);
        
		HttpSession session = request.getSession(false);
        
        String userId = "";
        String systemRole = "";
        boolean isAuthChk = false;
        String forwardUrl = "gms/login";
        
        LoginUserVO sessionInfo = SessionUtil.getSessionInfo(request);
        
        if(session != null || sessionInfo != null) {
            
            userId 		= StringUtils.defaultString(sessionInfo.getUserId());
            systemRole 	= StringUtils.defaultString(sessionInfo.getUserAuthority());
            log.info("preHandle userId ************ "+userId);
            log.info("preHandle systemRole ************ "+systemRole);
            // Session에 있는 ID가 존재하는지 확인하여 없으면, 강제 로그아웃 처리
            if ("".equals(userId) || "".equals(systemRole)) {
                request.getSession().invalidate();
				response.sendRedirect(request.getContextPath() + "/" + forwardUrl);
				return false;   				
            }
        }
        else {   
        	
        	LoginUserVO userInfo = new LoginUserVO();
        	// 임시
        	userInfo.setUserId("freetoy");
        	userInfo.setUserNm("임의테스터");
        	userInfo.setUserPartCd("03");
        	userInfo.setUserAuthority("99");
        	userInfo.setCurrentMenu(currentMenu);
        	
        	SessionUtil sessionUtil = new SessionUtil();
        	boolean isSuccess = sessionUtil.saveSessionInfo(request, userInfo);
        	
        }
        
        //return super.preHandle(request, response, handler);
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("postHandle!!");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info("afterCompletion!!");
	}

}
