package com.baomidou.test.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
/**
 * <p>
 * 
 * </p>
 *
 * @author Yanghu
 * @since 2017-01-11
 */
@TableName("t_product_order")
public class TProductOrder extends Model<TProductOrder> {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableField(value="receiver_mobile")
	private String receiverMobile;
	/**
	 * 
	 */
	@TableField(value="bank_Card_Number")
	private String bankCardNumber;


	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
