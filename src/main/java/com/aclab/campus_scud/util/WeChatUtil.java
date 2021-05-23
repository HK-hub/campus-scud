package com.aclab.campus_scud.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author 31618
 * @description: 微信小程序工具
 * @date 2021-05-21 21:18
 */
public class WeChatUtil {

	private static String appId="wxd5d575d492358be7";
	private static String appSecret="e73d74b1ceede1035003cf386c2b2f54";

	/**
	 * @Title: 通过微信小程序端返回的 code , 获取小程序后台返回的session_key , openId
	 * @description: 
	 * @author: 31618
	 * @date: 2021/5/21
	 * @param :
	 * @return: 
	 */
	public static JSONObject getSessionKeyOrOpenId(String code) {
		//请求的接口
		String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";

		HashMap<String, Object> requestUrlParam = new HashMap<>();
		//小程序appId
		requestUrlParam.put("appid", WeChatUtil.appId);
		//小程序secret
		requestUrlParam.put("secret",WeChatUtil.appSecret);

		//小程序端返回的code
		requestUrlParam.put("js_code", code);

		//默认参数
		requestUrlParam.put("grant_type", "authorization_code");

		//发送post请求读取调用微信接口获取openid用户唯一标识: openId
		String result = HttpUtil.get(requestUrl, requestUrlParam);

		JSONObject jsonObject = JSONUtil.parseObj(result);
		return jsonObject;
	}



	/**
	 * @Title: 获取用户数据信息,
	 * @description: 返回被加密的数据
	 * @author: 31618
	 * @date: 2021/5/21
	 * @param :
	 * @return:
	 */
	public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) {
		// 被加密的数据
		byte[] dataByte = Base64.decode(encryptedData);
		// 加密秘钥
		byte[] keyByte = Base64.decode(sessionKey);
		// 偏移量
		byte[] ivByte = Base64.decode(iv);
		try {
			// 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
			int base = 16;
			if (keyByte.length % base != 0) {
				int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
				byte[] temp = new byte[groups * base];
				Arrays.fill(temp, (byte) 0);
				System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
				keyByte = temp;
			}
			// 初始化
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
			parameters.init(new IvParameterSpec(ivByte));
			// 初始化
			cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
			byte[] resultByte = cipher.doFinal(dataByte);
			if (null != resultByte && resultByte.length > 0) {
				String result = new String(resultByte, "UTF-8");
				return JSONUtil.parseObj(result);
			}
		} catch (Exception e) {
		}
		return null;
	}
	





}
