package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8233009842300238104L;

	private int calSeq;
	private String calid;
	private String calTitle;
	private String calContent;
	private String calDate;	// 예약날짜

}
