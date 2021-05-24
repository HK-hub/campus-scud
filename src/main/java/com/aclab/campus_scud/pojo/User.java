package com.aclab.campus_scud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @TableName tb_user
 */
@TableName(value ="tb_user")
@Data
@ToString
public class User implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "open_id")
    private String openId;

    @TableField(value = "session_key")
    private String sessionKey ;

    @TableField(value = "skey")
    private String skey;

    /**
     * 
     */
    @TableField(value = "token")
    private String token;

    /**
     * 
     */
    @TableField(value = "code")
    private String code;

    /**
     * 
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 
     */
    @TableField(value = "username")
    private String username;

    /**
     *
     */
    @TableField(value = "nickname")
    private String nickname;

    @TableField(value = "gender")
    private String gender;

    /**
     * 
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    @TableField(value = "default_address")
    private String defaultAddress ;

    /**
     * 
     */
    @TableField(value = "actual_name")
    private String actualName;

    /**
     * 
     */
    @TableField(value = "identity_number")
    private String identityNumber;

    /**
     * 
     */
    @TableField(value = "identity_photo")
    private String identityPhoto;

    /**
     * 
     */
    @TableField(value = "reputation_score")
    private Double reputationScore;

    /**
     * 
     */
    @TableField(value = "user_type")
    private Integer userType;

    /**
     * 
     */
    @TableField(value = "login_time")
    private Date loginTime;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "modified_time")
    private Date modifiedTime;



}