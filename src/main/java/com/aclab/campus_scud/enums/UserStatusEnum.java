package com.aclab.campus_scud.enums;

/**
 * @author 31618
 * @description :用户状态枚举
 * @date 2021-05-25 18:40
 */
public enum UserStatusEnum {

	USER_LOGGED_IN(111,"用户已登录"),
	USER_UNLOGGED_IN(000,"用户未登录"),
	USER＿LOGGED_OUT(-111,"用户退出"),
	USER_SIGNATURE_ERROR(-222,"用户数字签名错误"),
	USER_IDENTITY_LEGAL(222,"用户数字签名通过");

	private Integer code;
	private String message ;

	private UserStatusEnum(Integer code, String message){
		this.code = code ;
		this.message = message ;
	}




}
