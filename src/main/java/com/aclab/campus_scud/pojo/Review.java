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
 * 订单评价
 * @TableName tb_review
 */
@TableName(value ="tb_review")
@Data
@ToString
public class Review implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer orderId;

    /**
     * 
     */
    private Integer creatorId;

    /**
     * 
     */
    private Integer runnerId;

    /**
     * 
     */
    private Integer creatorScoreId;

    /**
     * 
     */
    private Integer runnerScoreId;

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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Review other = (Review) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderId() == null ? other.getOrderId() == null : this.getOrderId().equals(other.getOrderId()))
            && (this.getCreatorId() == null ? other.getCreatorId() == null : this.getCreatorId().equals(other.getCreatorId()))
            && (this.getRunnerId() == null ? other.getRunnerId() == null : this.getRunnerId().equals(other.getRunnerId()))
            && (this.getCreatorScoreId() == null ? other.getCreatorScoreId() == null : this.getCreatorScoreId().equals(other.getCreatorScoreId()))
            && (this.getRunnerScoreId() == null ? other.getRunnerScoreId() == null : this.getRunnerScoreId().equals(other.getRunnerScoreId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifiedTime() == null ? other.getModifiedTime() == null : this.getModifiedTime().equals(other.getModifiedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderId() == null) ? 0 : getOrderId().hashCode());
        result = prime * result + ((getCreatorId() == null) ? 0 : getCreatorId().hashCode());
        result = prime * result + ((getRunnerId() == null) ? 0 : getRunnerId().hashCode());
        result = prime * result + ((getCreatorScoreId() == null) ? 0 : getCreatorScoreId().hashCode());
        result = prime * result + ((getRunnerScoreId() == null) ? 0 : getRunnerScoreId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifiedTime() == null) ? 0 : getModifiedTime().hashCode());
        return result;
    }

}