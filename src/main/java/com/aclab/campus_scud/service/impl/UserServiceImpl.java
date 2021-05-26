package com.aclab.campus_scud.service.impl;

import cn.hutool.json.JSONObject;
import com.aclab.campus_scud.enums.UserStatusEnum;
import com.aclab.campus_scud.mapper.AddressMapper;
import com.aclab.campus_scud.mapper.UserMapper;
import com.aclab.campus_scud.pojo.Address;
import com.aclab.campus_scud.pojo.User;
import com.aclab.campus_scud.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

import static cn.hutool.core.lang.Console.log;

/**
 *
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

	@Autowired
	private UserMapper userMapper ;
	@Autowired
	private AddressMapper addressMapper ;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * @Title: 更新用户登录
	 * @description:
	 * @author: 31618
	 * @date: 2021/5/23
	 * @param: WechatUserInfo
	 * @return:
	 */
	@Override
	public User updateWeChatUser(JSONObject rawDataJson, String openId, String sessionKey, String skey) {

		//获取信息
		String nickName = rawDataJson.get("nickName",String.class);
		String avatarUrl = rawDataJson.get("avatarUrl",String.class);

		// 已存在，更新用户登录时间
		User one = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getOpenId, openId));
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
			return null;

		}finally {
			log("更新用户: ") ;
		}

		return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getOpenId, openId));
	}

	/**
	 * @Title: 新增用户
	 * @description:
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param : rawData , skey , session_key
	 * @return:
	 */
	@Override
	public User insertWeChatUser(JSONObject rawDataJson, String openId, String sessionKey, String skey) {

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
		newUser.setOpenId(openId);
		newUser.setUsername(nickName);
		newUser.setGender(gender);
		newUser.setCreateTime(new Date());
		newUser.setModifiedTime(newUser.getCreateTime());
		newUser.setLoginTime(newUser.getCreateTime());
		newUser.setDefaultAddress(province+" "+city+" ");
		//设置skey 自定义登录状态
		newUser.setToken(skey);
		newUser.setSkey(skey);
		newUser.setSessionKey(sessionKey);

		System.out.println("新用户信息： "+newUser.toString());


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
			return null ;
		}

		return newUser;
	}

	/**
	 * @Title: 获取一个用户
	 * @description:
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param : skey
	 * @return:
	 */
	@Override
	public User getUserBySkey(String skey) {

		return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getSkey, skey));
	}

	@Override
	public User getUserBySessionKey(String sessionKey) {
		return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getSessionKey, sessionKey));

	}

	@Override
	public User getUserByToken(String token) {
		return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getToken, token));

	}

	/**
	 * @Title: 获取用户状态
	 * @description: 通过skey 获取用户状态
	 * @author: 31618
	 * @date: 2021/5/25
	 * @param :skey
	 * @return:
	 */
	@Override
	public UserStatusEnum getUserLoginStatus(String skey) {

		//获取Redis 中的 skey
		final String redisSkey = stringRedisTemplate.opsForValue().get("skey:_|" + skey);
		//多重验证

		//skey 为空， 用户未登录
		if (redisSkey == null){
			return UserStatusEnum.USER_UNLOGGED_IN;
		}
		return UserStatusEnum.USER_LOGGED_IN;
	}

	@Override
	public UserStatusEnum getUserSignatureStatus(String sessionKey) {

		final String redisSessionKey = stringRedisTemplate.opsForValue().get("session_key:_|" + sessionKey);

		//数字签名不匹配
		if (sessionKey == null){
			return UserStatusEnum.USER_SIGNATURE_ERROR;
		}
		return UserStatusEnum.USER_IDENTITY_LEGAL;
	}

	@Override
	public int userSignOut(String skey, String sessionKey) {

		//清除地址,skey ,sessionKey, user, userInfo
		try {
			redisTemplate.delete("user:_|"+skey);
			stringRedisTemplate.delete("skey:_|"+skey);
			stringRedisTemplate.delete("session_key:_|"+sessionKey);
			redisTemplate.opsForList().leftPop("weChatUserInfo:_|"+skey);
			stringRedisTemplate.delete("defaultAddress:_|"+skey);
			//
		}catch (Exception e){

			return 0;
		}

		return 1;
	}


}




