package com.aclab.campus_scud.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author 31618
 * @description: 微信用户信息, 微信返回的用户信息
 * @date 2021-05-21 21:17
 */
@Data
@ToString
public class WeChatUserInfo {

	/**
	 * 微信返回的code
	 */
	private String code;
	/**
	 * 非敏感的用户信息 : nickname, avatar, province, city 等
	 */
	private String rawData;
	/**
	 * 签名信息
	 */
	private String signature;
	/**
	 * 加密的数据
	 */
	private String encrypteData;
	/**
	 * 加密密钥
	 */
	private String iv;



}
