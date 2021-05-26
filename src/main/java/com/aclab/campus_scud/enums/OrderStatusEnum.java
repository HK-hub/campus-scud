package com.aclab.campus_scud.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author 31618
 * @description: 订单状态
 * @date 2021-05-25 18:27
 */
@ToString
@AllArgsConstructor
public enum OrderStatusEnum {

	ORDER_PENDING(1,"待接单"),
	ORDER_RECEIVED(2,"已接单"),
	ORDER_OVER(0,"已结束"),
	ORDER_REVOKED(-1,"已撤销");


	private Integer code;
	private String message ;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
