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
 * 评分,评价
 * @TableName tb_score
 */
@TableName(value ="tb_score")
@Data
@ToString
public class Score implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

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
    private Integer type;

    /**
     * 
     */
    private Double overallSocre;

    /**
     * 
     */
    private Double serviceAttitudeScore;

    /**
     * 
     */
    private Double logisticsServicesScore;

    /**
     * 
     */
    private Double serviceQualityScore;

    /**
     * 
     */
    private String description;

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
        Score other = (Score) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCreatorId() == null ? other.getCreatorId() == null : this.getCreatorId().equals(other.getCreatorId()))
            && (this.getRunnerId() == null ? other.getRunnerId() == null : this.getRunnerId().equals(other.getRunnerId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getOverallSocre() == null ? other.getOverallSocre() == null : this.getOverallSocre().equals(other.getOverallSocre()))
            && (this.getServiceAttitudeScore() == null ? other.getServiceAttitudeScore() == null : this.getServiceAttitudeScore().equals(other.getServiceAttitudeScore()))
            && (this.getLogisticsServicesScore() == null ? other.getLogisticsServicesScore() == null : this.getLogisticsServicesScore().equals(other.getLogisticsServicesScore()))
            && (this.getServiceQualityScore() == null ? other.getServiceQualityScore() == null : this.getServiceQualityScore().equals(other.getServiceQualityScore()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifiedTime() == null ? other.getModifiedTime() == null : this.getModifiedTime().equals(other.getModifiedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCreatorId() == null) ? 0 : getCreatorId().hashCode());
        result = prime * result + ((getRunnerId() == null) ? 0 : getRunnerId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getOverallSocre() == null) ? 0 : getOverallSocre().hashCode());
        result = prime * result + ((getServiceAttitudeScore() == null) ? 0 : getServiceAttitudeScore().hashCode());
        result = prime * result + ((getLogisticsServicesScore() == null) ? 0 : getLogisticsServicesScore().hashCode());
        result = prime * result + ((getServiceQualityScore() == null) ? 0 : getServiceQualityScore().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifiedTime() == null) ? 0 : getModifiedTime().hashCode());
        return result;
    }


}