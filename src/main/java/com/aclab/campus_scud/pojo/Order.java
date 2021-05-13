package com.aclab.campus_scud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 跑腿订单
 * @TableName tb_order
 */
@TableName(value ="tb_order")
@Data
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
     * 
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
        Order other = (Order) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNumber() == null ? other.getOrderNumber() == null : this.getOrderNumber().equals(other.getOrderNumber()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getOrderContent() == null ? other.getOrderContent() == null : this.getOrderContent().equals(other.getOrderContent()))
            && (this.getCreator() == null ? other.getCreator() == null : this.getCreator().equals(other.getCreator()))
            && (this.getTakeawayId() == null ? other.getTakeawayId() == null : this.getTakeawayId().equals(other.getTakeawayId()))
            && (this.getOrderPhoto() == null ? other.getOrderPhoto() == null : this.getOrderPhoto().equals(other.getOrderPhoto()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getFinancialId() == null ? other.getFinancialId() == null : this.getFinancialId().equals(other.getFinancialId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getPickupLocation() == null ? other.getPickupLocation() == null : this.getPickupLocation().equals(other.getPickupLocation()))
            && (this.getDeliveryLocation() == null ? other.getDeliveryLocation() == null : this.getDeliveryLocation().equals(other.getDeliveryLocation()))
            && (this.getExpectedDatetime() == null ? other.getExpectedDatetime() == null : this.getExpectedDatetime().equals(other.getExpectedDatetime()))
            && (this.getOrderComment() == null ? other.getOrderComment() == null : this.getOrderComment().equals(other.getOrderComment()))
            && (this.getRunnerId() == null ? other.getRunnerId() == null : this.getRunnerId().equals(other.getRunnerId()))
            && (this.getOrderStatus() == null ? other.getOrderStatus() == null : this.getOrderStatus().equals(other.getOrderStatus()))
            && (this.getPaymentStatus() == null ? other.getPaymentStatus() == null : this.getPaymentStatus().equals(other.getPaymentStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifiedTime() == null ? other.getModifiedTime() == null : this.getModifiedTime().equals(other.getModifiedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNumber() == null) ? 0 : getOrderNumber().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getOrderContent() == null) ? 0 : getOrderContent().hashCode());
        result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
        result = prime * result + ((getTakeawayId() == null) ? 0 : getTakeawayId().hashCode());
        result = prime * result + ((getOrderPhoto() == null) ? 0 : getOrderPhoto().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getFinancialId() == null) ? 0 : getFinancialId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getPickupLocation() == null) ? 0 : getPickupLocation().hashCode());
        result = prime * result + ((getDeliveryLocation() == null) ? 0 : getDeliveryLocation().hashCode());
        result = prime * result + ((getExpectedDatetime() == null) ? 0 : getExpectedDatetime().hashCode());
        result = prime * result + ((getOrderComment() == null) ? 0 : getOrderComment().hashCode());
        result = prime * result + ((getRunnerId() == null) ? 0 : getRunnerId().hashCode());
        result = prime * result + ((getOrderStatus() == null) ? 0 : getOrderStatus().hashCode());
        result = prime * result + ((getPaymentStatus() == null) ? 0 : getPaymentStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifiedTime() == null) ? 0 : getModifiedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNumber=").append(orderNumber);
        sb.append(", title=").append(title);
        sb.append(", orderContent=").append(orderContent);
        sb.append(", creator=").append(creator);
        sb.append(", takeawayId=").append(takeawayId);
        sb.append(", orderPhoto=").append(orderPhoto);
        sb.append(", price=").append(price);
        sb.append(", financialId=").append(financialId);
        sb.append(", type=").append(type);
        sb.append(", pickupLocation=").append(pickupLocation);
        sb.append(", deliveryLocation=").append(deliveryLocation);
        sb.append(", expectedDatetime=").append(expectedDatetime);
        sb.append(", orderComment=").append(orderComment);
        sb.append(", runnerId=").append(runnerId);
        sb.append(", orderStatus=").append(orderStatus);
        sb.append(", paymentStatus=").append(paymentStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}