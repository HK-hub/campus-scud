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
 * 财务物流
 * @TableName tb_financial_flow
 */
@TableName(value ="tb_financial_flow")
@Data
@ToString
public class FinancialFlow implements Serializable {

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Double transactionAmount;

    /**
     * 
     */
    private Integer payerId;

    /**
     * 
     */
    private Integer payeeId;

    /**
     * 
     */
    private Integer flowState;

    /**
     * 
     */
    private Integer transactionState;

    /**
     * 
     */
    private Date transactionDate;

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
        FinancialFlow other = (FinancialFlow) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTransactionAmount() == null ? other.getTransactionAmount() == null : this.getTransactionAmount().equals(other.getTransactionAmount()))
            && (this.getPayerId() == null ? other.getPayerId() == null : this.getPayerId().equals(other.getPayerId()))
            && (this.getPayeeId() == null ? other.getPayeeId() == null : this.getPayeeId().equals(other.getPayeeId()))
            && (this.getFlowState() == null ? other.getFlowState() == null : this.getFlowState().equals(other.getFlowState()))
            && (this.getTransactionState() == null ? other.getTransactionState() == null : this.getTransactionState().equals(other.getTransactionState()))
            && (this.getTransactionDate() == null ? other.getTransactionDate() == null : this.getTransactionDate().equals(other.getTransactionDate()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifiedTime() == null ? other.getModifiedTime() == null : this.getModifiedTime().equals(other.getModifiedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTransactionAmount() == null) ? 0 : getTransactionAmount().hashCode());
        result = prime * result + ((getPayerId() == null) ? 0 : getPayerId().hashCode());
        result = prime * result + ((getPayeeId() == null) ? 0 : getPayeeId().hashCode());
        result = prime * result + ((getFlowState() == null) ? 0 : getFlowState().hashCode());
        result = prime * result + ((getTransactionState() == null) ? 0 : getTransactionState().hashCode());
        result = prime * result + ((getTransactionDate() == null) ? 0 : getTransactionDate().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifiedTime() == null) ? 0 : getModifiedTime().hashCode());
        return result;
    }

}