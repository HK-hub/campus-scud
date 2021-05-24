package com.aclab.campus_scud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户地址表
 * @TableName tb_address
 */
@TableName(value ="tb_address")
@Data
public class Address implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 
     */
    @TableField(value = "summary_address")
    private String summaryAddress;

    /**
     * 
     */
    @TableField(value = "detail_address")
    private String detailAddress;

    /**
     * 
     */
    @TableField(value = "city")
    private String city;

    /**
     * 
     */
    @TableField(value = "province")
    private String province;

    /**
     * 
     */
    @TableField(value = "country")
    private String country;

    /**
     * 
     */
    @TableField(value = "longitude")
    private String longitude;

    /**
     * 
     */
    @TableField(value = "dimension")
    private String dimension;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * @Title: 获取一个拼装地址
     * @description:
     * @author: 31618
     * @date: 2021/5/24
     * @param null:
     * @return:
     */
    public String getAssemblyAddress(){

        String address = ""+this.getProvince() +":"+this.getCity()+":"+this.getDetailAddress();
        this.setSummaryAddress(address);
        return address;
    }


}