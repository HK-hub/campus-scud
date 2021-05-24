package com.aclab.campus_scud.controller;

import com.aclab.campus_scud.excption.CommonEnum;
import com.aclab.campus_scud.excption.ResultInfo;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.impl.AddressServiceImpl;
import com.aclab.campus_scud.service.impl.UserServiceImpl;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 31618
 * @description：地址数据处理
 * @date 2021-05-23 22:10
 */
@Api
@Slf4j
@Controller
public class AddressController {

	@Autowired
	private AddressServiceImpl addressService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;


	@ApiOperation(value = "获取用户的默认地址", notes = "默认地址", httpMethod = "GET",
					response = ResultInfo.class,responseContainer = "默认地址，响应状态码，响应消息")
	@ApiParam(name = "reqHeader", value = "微信公共请求头数据",type = "JSONObject",required = true)
	@GetMapping("/defaultAddress")
	public ResultInfo getUserDefaultAddress(@RequestParam(name = "reqHeader")JSONObject reqHeader){
		//查看是否登录
		final String skey = reqHeader.getString("skey");
		final String redisSkey = stringRedisTemplate.opsForValue().get("skey:_|" + skey);
		final String sessionKey = reqHeader.getString("session_key");

		//从数据库中获取
		final User userBySkey = userService.getUserBySkey(skey);

		//不存在 skey ，
		if (redisSkey==null){

			//新用户： 返回未注册信息
			if (userBySkey==null){
				//用户未注册
				return ResultInfo.error("unregistered user");

			}else{  //将用户加入到Redis 中
				redisTemplate.opsForValue().set("user:_|"+userBySkey.getSkey(), userBySkey);
				stringRedisTemplate.opsForValue().set("skey:_|"+skey, skey);
			}
		}
		String defaultAddress;
		//检验 skey 和 session_key: 此时 skey 已经校验通过
		if (sessionKey.equals(userBySkey.getSessionKey())){

			//校验通过， 允许执行业务--> 获取 默认地址
			defaultAddress = addressService.getDefaultAddress(userBySkey.getOpenId(), skey);

			//将默认地址填写进入Redis 更新用户和地址
			stringRedisTemplate.opsForValue().getAndSet("defaultAddress:_|"+skey, defaultAddress);
			userBySkey.setDefaultAddress(defaultAddress);
			redisTemplate.opsForValue().getAndSet("user:_|"+userBySkey.getSkey() ,userBySkey);

		}else{
			//没有权限：
			return ResultInfo.error(CommonEnum.SIGNATURE_NOT_MATCH);
		}
		//返回成功的 ResultInfo: 带上 默认地址数据
		return  ResultInfo.success(defaultAddress);

	}






}
