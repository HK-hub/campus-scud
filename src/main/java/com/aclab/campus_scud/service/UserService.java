package com.aclab.campus_scud.service;

import cn.hutool.json.JSONObject;
import com.aclab.campus_scud.enums.UserStatusEnum;
import com.aclab.campus_scud.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 */
@Mapper
public interface UserService extends IService<User> {

	public User updateWeChatUser(JSONObject rawDataJson, String openId, String sessionKey, String skey);
	public User insertWeChatUser(JSONObject rawDataJson, String openId, String sessionKey, String skey);
	public User getUserBySkey(String skey);
	public User getUserBySessionKey(String sessionKey);
	public User getUserByToken(String token);
	public UserStatusEnum getUserLoginStatus(String skey);
	public UserStatusEnum getUserSignatureStatus(String sessionKey);

	public int userSignOut(String skey, String sessionKey);
}
