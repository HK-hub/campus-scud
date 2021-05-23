package com.aclab.campus_scud.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aclab.campus_scud.mapper.AddressMapper;
import com.aclab.campus_scud.mapper.UserMapper;
import com.aclab.campus_scud.pojo.Address;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.pojo.WeChatUserInfo;
import com.aclab.campus_scud.service.UserService;
import com.aclab.campus_scud.util.WeChatUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static cn.hutool.core.lang.Console.log;

/**
 *
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

	@Autowired
	UserMapper userMapper ;
	@Autowired
	AddressMapper addressMapper ;


	public void test(){

	}

	/**
	 * @Title: 更新用户登录
	 * @description:
	 * @author: 31618
	 * @date: 2021/5/23
	 * @param: WechatUserInfo
	 * @return:
	 */
	@Override
	public boolean updateWeChatUser(WeChatUserInfo weChatUserInfo) {

		// 2.开发者服务器 登录凭证校验接口 appId + appSecret + 接收小程序发送的code
		JSONObject sessionKeyOrOpenId = WeChatUtil.getSessionKeyOrOpenId(weChatUserInfo.getCode());

		// 3.接收微信接口服务 获取返回的参数
		String openid = sessionKeyOrOpenId.get("openid", String.class);
		String sessionKey = sessionKeyOrOpenId.get("session_key", String.class);

		// 用户非敏感信息：rawData : 一些基本信息
		// 签名：signature
		JSONObject rawDataJson = JSONUtil.parseObj(weChatUserInfo.getRawData());

		// uuid生成唯一key，用于维护微信小程序用户与服务端的会话session_key, （或者生成Token）
		String skey = UUID.randomUUID().toString();

		//获取信息
		String nickName = rawDataJson.get("nickName",String.class);
		String avatarUrl = rawDataJson.get("avatarUrl",String.class);

		// 已存在，更新用户登录时间
		User one = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getOpenId, openid));
		final Date date = new Date();

		//更新wrapper()
		UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
		userUpdateWrapper.lambda().eq(User::getId, one.getId());
		userUpdateWrapper.lambda().set(User::getLoginTime, date);
		userUpdateWrapper.lambda().set(User::getModifiedTime, date);
		//更新 skey
		userUpdateWrapper.lambda().set(User::getToken, skey) ;
		userUpdateWrapper.lambda().set(User::getSkey, skey);
		//更新 session_key
		userUpdateWrapper.lambda().set(User::getSessionKey, sessionKey);
		//更新头像
		if(!one.getAvatarUrl().equals(avatarUrl)){
			userUpdateWrapper.lambda().set(User::getAvatarUrl, avatarUrl);
		}
		//更新昵称
		if (!one.getNickname().equals(nickName)){
			userUpdateWrapper.lambda().set(User::getNickname,nickName);
			userUpdateWrapper.lambda().set(User::getUsername, nickName);
		}
		try {

			//更新数据库中User
			userMapper.update(null, userUpdateWrapper);

		}catch (Exception exception){

			System.out.println(exception.getCause().toString());
			return false;

		}finally {
			log("更新用户: ") ;
		}

		return true;
	}

	@Override
	public boolean insertWeChatUser(WeChatUserInfo weChatUserInfo) {

		// 2.开发者服务器 登录凭证校验接口 appId + appSecret + 接收小程序发送的code
		JSONObject sessionKeyOrOpenId = WeChatUtil.getSessionKeyOrOpenId(weChatUserInfo.getCode());
		// 3.接收微信接口服务 获取返回的参数
		String openid = sessionKeyOrOpenId.get("openid", String.class);
		String sessionKey = sessionKeyOrOpenId.get("session_key", String.class);
		// uuid生成唯一key，用于维护微信小程序用户与服务端的会话session_key, （或者生成Token）
		String skey = UUID.randomUUID().toString();


		// 用户非敏感信息：rawData : 一些基本信息
		// 签名：signature
		JSONObject rawDataJson = JSONUtil.parseObj(weChatUserInfo.getRawData());
		//获取信息
		String nickName = rawDataJson.get("nickName",String.class);
		String avatarUrl = rawDataJson.get("avatarUrl",String.class);
		String gender = rawDataJson.get("gender",String.class);
		String city = rawDataJson.get("city",String.class);
		String country = rawDataJson.get("country",String.class);
		String province = rawDataJson.get("province",String.class);


		//新用户
		User newUser = new User();
		//新用户地址
		Address address = new Address();

		//信息复制
		newUser.setNickname(nickName);
		newUser.setAvatarUrl(avatarUrl);
		newUser.setOpenId(openid);
		newUser.setUsername(nickName);
		newUser.setGender(gender);
		newUser.setCreateTime(new Date());
		newUser.setModifiedTime(newUser.getCreateTime());
		newUser.setLoginTime(newUser.getCreateTime());
		//设置skey 自定义登录状态
		newUser.setToken(skey);
		newUser.setSkey(skey);
		newUser.setSessionKey(sessionKey);

		//用户地址信息入库
		address.setCountry(country);
		address.setProvince(province);
		address.setCity(city);
		address.setCreateTime(new Date());
		address.setModifiedTime(address.getCreateTime());

		try {

			//用户信息入库
			userMapper.insert(newUser);
			//设置新用户ID
			address.setUserId(newUser.getId());
			//用户地址入库
			addressMapper.insert(address);

		}catch (Exception e){
			System.out.println(e.getCause().toString());
			return false ;
		}

		return true;
	}
}




