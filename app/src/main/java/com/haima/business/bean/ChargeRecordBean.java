package com.haima.business.bean;

import com.haima.business.utils.MathUtil;

/**
 * Created by ${flyjiang} on 2019/10/17.
 * 文件说明：
 * "id":74,"createdBy":-1,"creationDate":1571238297000,
 * "lastUpdatedBy":-1,"lastUpdateDate":1571238297000,
 * "status":1,"userId":36,"amount":20.000,"type":2,"balance":300.000,"shopId":1}
 */
public class ChargeRecordBean {
    private String status = "";
    private String balance = "";
    private String type = "";//交易类型：1：自提清算；2：服务清算；3：提现申请
    private String amount = "";
    private String creation_date = "";
    private String current_balance = "";

    public String getStatus() {
        return status;
    }

    public String getBalance() {
        return MathUtil.round_half_down(balance, 0);
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return MathUtil.round_half_down(amount, 0);
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getCurrent_balance() {
        return current_balance;
    }
}
