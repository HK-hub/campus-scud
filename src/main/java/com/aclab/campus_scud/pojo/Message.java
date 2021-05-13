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
 * 聊天记录表
 * @TableName tb_message
 */
@TableName(value ="tb_message")
@Data
@ToString
public class Message implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String postMessage;

    /**
     * 
     */
    private Integer receiveStatus;

    /**
     * 
     */
    private Date postTime;

    /**
     * 
     */
    private Integer messageTypeId;

    /**
     * 
     */
    private Integer senderId;

    /**
     * 
     */
    private Integer receiverId;

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