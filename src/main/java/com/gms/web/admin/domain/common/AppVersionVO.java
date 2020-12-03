package com.gms.web.admin.domain.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppVersionVO implements Serializable {

private static final long serialVersionUID = 1331928141333422978L;
	
	/** App_Ver                */
	private String appVer;
}
