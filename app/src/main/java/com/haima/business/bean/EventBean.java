package com.haima.business.bean;

/**
 * Created by jpchen on 2017/6/12.
 */

public class EventBean {

    private int mEvent;
    private Object mData;

    public static final int RECEIVE_GOODS_SUCCESS = 101;  //提货成功
    public static final int SHOP_GOODS_CONFIRM = 102;  //确认自提商品
    public static final int APPLY_MONEY_SUCCESS= 103;  //提现申请成功

    public static final int SERVICE_FINISH_SUCCESS= 104;  //确认服务完成

    public EventBean(int event) {
        this.mEvent = event;
    }

    public EventBean(int event, Object data) {
        this(event);
        this.mData = data;
    }

    public int getEvent() {
        return this.mEvent;
    }

    public void setEvent(int event) {
        this.mEvent = event;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }

}
