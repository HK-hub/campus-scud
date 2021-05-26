package com.aclab.campus_scud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 跑腿订单
 * @TableName tb_order
 */
@TableName(value ="tb_order")
@Data
@ToString
@EqualsAndHashCode
public class Order implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String orderNumber;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String orderContent;

    /**
     * 
     */
    private Integer creator;

    /**
     * 
     */
    private String takeawayId;

    /**
     * 
     */
    private String orderPhoto;

    /**
     * 
     */
    private Double price;

    /**
     * 
     */
    private Integer financialId;

    /**
     * 
     */
    private Integer type;

    /**
     * 
     */
    private String pickupLocation;

    /**
     * 
     */
    private String deliveryLocation;

    /**
     * 
     */
    private Date expectedDatetime;

    /**
     * 
     */
    private String orderComment;

    /**
     * 
     */
    private Integer runnerId;

    /**
     * 1：发布 ， 0：接单， -1：结束
     */
    private Integer orderStatus;

    /**
     * 
     */
    private Integer paymentStatus;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date modifiedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}