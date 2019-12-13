package com.gms.web.admin.domain.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventVO {

	private String title;

    /**
     * カレンダーの開始日付
     */
    private String start;

    /**
     * カレンダーの終了日付
     */
    private String end;
}
