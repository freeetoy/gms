package com.gms.web.admin.domain.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserVO implements Serializable {
	
	private static final long serialVersionUID = 1331928141433496978L;

	public static final String ATTRIBUTE_NAME = "LoginUser";
	
	/** 사용자ID */
	private String userId;
	
	/** 사용자명 */
	private String userNm;
	
	/** 사용자부서 */
	private String userPartCd;
	
	/** 사용자권한 */
	private String userAuthority;
	
	/** 패스워드 */
	private String userPasswd;

	/**ID 존재 여부  */
	private String idCheckYn;
	
	private String errorMessage;	
    
    private String returnUrl;
    
    /**현재 메뉴  */
	private String currentMenu;
}
