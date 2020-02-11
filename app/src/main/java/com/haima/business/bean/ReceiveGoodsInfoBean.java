package com.haima.business.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 2019/12/15.
 * 文件说明：
 */
public class ReceiveGoodsInfoBean implements Serializable {
    private String amount;
    private int selfState;
    private List<ProductSelectBean> merclist = new ArrayList();

    public String getAmount() {
        return amount;
    }

    public List<ProductSelectBean> getMerclist() {
        return merclist;
    }

    public int getSelfState() {
        return selfState;
    }
}
