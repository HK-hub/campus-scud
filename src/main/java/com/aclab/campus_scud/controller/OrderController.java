package com.aclab.campus_scud.controller;

import com.aclab.campus_scud.excption.CommonEnum;
import com.aclab.campus_scud.excption.ResultInfo;
import com.aclab.campus_scud.pojo.Order;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.impl.AddressServiceImpl;
import com.aclab.campus_scud.service.impl.OrderServiceImpl;
import com.aclab.campus_scud.service.impl.UidGenerateService;
import com.aclab.campus_scud.service.impl.UserServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author 31618
 * @description
 * @date 2021-05-23 15:06
 */
@Api(value = "用户订单处理")
@RestController
public class OrderController {

	@Autowired
	private UidGenerateService uidGenerateService ;
	@Autowired
	private OrderServiceImpl orderService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private AddressServiceImpl addressService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;



	/**
	 * @Title: 新增一个订单
	 * @description:
	 * @author: 31618
	 * @date: 2021/5/24
	 * @param : 前端订单需要的信息，用户信息skey
	 * @return: 新增的订单的订单ID
	 */
	@ApiOperation(value = "新增订单", httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "订单基本数据", value = "标题,内容,地点,类型等", type = "JSONObject:order", required = true, paramType = "query"),
			@ApiImplicitParam(name = "用户Skey", value = "user.skey", type = "String", required = true, paramType = "query"),
			@ApiImplicitParam(name = "session_key",value = "业务校验",type = "String", paramType = "query")
	})
	@ApiResponses({
			@ApiResponse(code = 200,message = "新增订单成功",responseContainer = "新增的订单"),
			@ApiResponse(code = 202,message = "用户未登录", responseContainer = "错误信息"),
			@ApiResponse(code = 401,message = "数字签名不匹配", responseContainer = "错误信息"),
			@ApiResponse(code = 201, message = "非法参数"),
			@ApiResponse(code = 404, message = "请求资源不存在"),
			@ApiResponse(code = 400, message = "请求数据错误"),
			@ApiResponse(code = 500, message = "服务器内部错误")
	})
	public ResultInfo addNewOrder(@RequestParam(name = "order") JSONObject orderData,
	                              @RequestParam(name = "skey")String skey,
                                  @RequestParam(name = "session_key")String sessionKey
	){
		//校验登录状态
		final String redisSkey = stringRedisTemplate.opsForValue().get("skey:_|" + skey);
		final User redisUser = (User)redisTemplate.opsForValue().get("user:_|" + skey);
		Order newOrder = null;
		//用户未登录
		if (redisSkey == null && redisUser == null){
			ResultInfo.error(CommonEnum.USER_NOT_LOGIN);
		}else {
			//用户已登录->进行校验数字业务签名
			final String redisSessionKey = stringRedisTemplate.opsForValue().get("session_key:_|" + sessionKey);
			if(redisUser.getSessionKey().equals(redisSessionKey)){
				//业务数字签名校验通过->进行业务操作
				newOrder = orderService.addNewOrder(skey, sessionKey, orderData);
			}else{
				//数字签名校验不通过->
				ResultInfo.error(CommonEnum.SIGNATURE_NOT_MATCH);
			}
		}
		//服务器内部错误
		if (newOrder == null){
			ResultInfo.error(CommonEnum.INTERNAL_SERVER_ERROR);
		}
		//返回请求成功：返回新增加的订单
		return ResultInfo.success(newOrder);
	}

	
	//查询所有跑腿订单
	/**
	 * @Title:
	 * @description: 
	 * @author: 31618
	 * @date: 2021/5/24
	 * @param :
	 * @return: 
	 */
	public ArrayList<Order> getAllOrder(){
		
		return null ;
	}




}
