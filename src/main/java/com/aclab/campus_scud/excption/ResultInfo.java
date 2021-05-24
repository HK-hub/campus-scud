package com.aclab.campus_scud.excption;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 31618
 * @description : 响应结果信息
 * @date 2021-05-21 21:40
 */
@Data
@ToString
public class ResultInfo {

	/**
	 * 响应代码
	 */
	private String code;

	/**
	 * 响应消息
	 */
	private String message;

	/**
	 * 响应结果
	 */
	private Object result;

	/**
	 *
	 *  响应数据
	 * */
	private Map<String, Object> data = new HashMap<>() ;


	public ResultInfo() {

	}

	public ResultInfo data(String key , Object value){
		this.data.put(key, value);
		return this;
	}

	public ResultInfo data(Map<String , Object> map){
		this.setData(map);
		return this;
	}

	public ResultInfo(BaseErrorInfoInterface errorInfo) {
		this.code = errorInfo.getResultCode();
		this.message = errorInfo.getResultMsg();
	}

	/**
	 * 成功 : 不返回数据
	 *
	 * @return
	 */
	public static ResultInfo success() {
		return success(null);
	}

	/**
	 * 成功 : 返回数据: 响应码, 响应消息, 响应数据
	 * @param data
	 * @return
	 */
	public static ResultInfo success(Object data) {
		ResultInfo rb = new ResultInfo();
		rb.setCode(CommonEnum.SUCCESS.getResultCode());
		rb.setMessage(CommonEnum.SUCCESS.getResultMsg());
		rb.setResult(data);
		return rb;
	}

	/**
	 * 失败:
	 */
	public static ResultInfo error(BaseErrorInfoInterface errorInfo) {
		ResultInfo rb = new ResultInfo();
		rb.setCode(errorInfo.getResultCode());
		rb.setMessage(errorInfo.getResultMsg());
		rb.setResult(null);
		return rb;
	}

	/**
	 * 失败: 自定义响应码, 消息
	 */
	public static ResultInfo error(String code, String message) {
		ResultInfo rb = new ResultInfo();
		rb.setCode(code);
		rb.setMessage(message);
		rb.setResult(null);
		return rb;
	}

	/**
	 * 失败 : 自定义消息
	 */
	public static ResultInfo error(String message) {
		ResultInfo rb = new ResultInfo();
		rb.setCode("-1");
		rb.setMessage(message);
		rb.setResult(null);
		return rb;
	}


}
