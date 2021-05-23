package com.aclab.campus_scud.controller;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import com.aclab.campus_scud.excption.ResultInfo;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.pojo.WeChatUserInfo;
import com.aclab.campus_scud.service.impl.UserServiceImpl;
import com.aclab.campus_scud.util.WeChatUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author 31618
 * @description: 微信小程序用户登录
 * @date 2021-05-21 21:35
 */
@Api(value = "微信用户登录处理")
@RestController
public class WeChatUserLoginController {

	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;


	@ApiOperation(value = "微信用户进行登录" , notes = "微信用户登录进行判断, 第一次登录,新增用户, 否则更新用户信息"
				, httpMethod = "POST" ,tags = {"微信登录","用户登录注册","openId获取","session_key生成"}
	)
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "update/create", dataType = "WeChatUserInfo",
					name = "weChatUserInfo", value = "微信小程序服务器返回的微信用户信息", required = true
			)
	})
	@ApiResponses({
			@ApiResponse(code = HttpStatus.HTTP_OK , message = "操作成功:登录"),
			@ApiResponse(code = HttpStatus.HTTP_INTERNAL_ERROR ,message = "服务器内部错误"),
			@ApiResponse(code = HttpStatus.HTTP_NOT_FOUND , message = "未知的服务资源")
	})
	@PostMapping("/wx/login")
	public ResultInfo user_login(@RequestBody WeChatUserInfo weChatUserInfo) throws Exception {

		// 2.开发者服务器 登录凭证校验接口 appId + appSecret + 接收小程序发送的code
		JSONObject sessionKeyOrOpenId = WeChatUtil.getSessionKeyOrOpenId(weChatUserInfo.getCode());

		// 3.接收微信接口服务 获取返回的参数
		String openid = sessionKeyOrOpenId.get("openid", String.class);
		String sessionKey = sessionKeyOrOpenId.get("session_key", String.class);

		//解密后返回的用户信息
		JSONObject userInfo = WeChatUtil.getUserInfo(weChatUserInfo.getEncrypteData(),
				sessionKey, weChatUserInfo.getIv());

		// 5.根据返回的User实体类，判断用户是否是新用户，是的话，将用户信息存到数据库；不是的话，更新最新登录时间
		QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
		userQueryWrapper.lambda().eq(User::getOpenId, openid);
		int userCount = userService.count(userQueryWrapper);

		// uuid生成唯一key，用于维护微信小程序用户与服务端的会话session_key, （或者生成Token）
		String skey = UUID.randomUUID().toString();

		//业务逻辑操作: 新增用户还是更新用户
		//用户为新用户
		if (userCount <= 0) {
			userService.insertWeChatUser(weChatUserInfo);

		} else {
			userService.updateWeChatUser(weChatUserInfo);
		}

		//将用户对象和 skey , session_key 存入Redis缓存中,
		stringRedisTemplate.opsForValue().set("skey", skey);
		stringRedisTemplate.opsForValue().set("session_key", sessionKey);
		redisTemplate.opsForList().leftPush("weChatUserInfo", userInfo);


		//6. 把新的skey返回给小程序 : 相应成功200, data: skey ,
		return ResultInfo.success(skey);

	}


	@GetMapping("/wx/test")
	public void test(@RequestParam("openId")String openId,
	                 @RequestParam("nickName")String nickName,
	                 @RequestParam("headUrl")String headUrl){
		System.out.println("openId: " + openId);
		System.out.println("nickName: "+nickName);
		System.out.println("headUrl: "+headUrl);
	}


}
