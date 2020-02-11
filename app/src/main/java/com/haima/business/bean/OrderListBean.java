package com.haima.business.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 2019/10/30.
 * 文件说明：
 receive_mobile: "15900691357",
 amountState: null,
 total_number: 2,
 self_state: "1",
 total_price: 61,
 user_mobile: "15900691357",
 amountTime: null,
 trade_no: "HMJ2019113011504595200000001",
 insert_time: "2019-11-30 11:50:45",
 provider_id: "ORG00001",
 */
public class OrderListBean {
    private String total_number = "";
    private String self_state = "";
    private String total_price ="";
    private String trade_no="";
    private String insert_time="";
    private List<OrderProductItemBean> detail = new ArrayList<>();

    public String getTotal_number() {
        return total_number;
    }

    public String getSelf_state() {
        return self_state;
    }

    public String getTotal_price() {
        return total_price;
    }

    public List<OrderProductItemBean> getDetail() {
        return detail;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public String getInsert_time() {
        return insert_time;
    }
}
