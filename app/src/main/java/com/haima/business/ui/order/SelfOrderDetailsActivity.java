package com.haima.business.ui.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haima.business.R;
import com.haima.business.adapter.ProductBuyAdapter;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.Constants;
import com.haima.business.bean.EventBean;
import com.haima.business.bean.OrderDetailBean;
import com.haima.business.bean.ProductSelectBean;
import com.haima.business.callback.DialogCallback;
import com.haima.business.dialog.CommonOperateDialog;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.widget.DividerItemDecoration;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 商品订单详情
 */
public class SelfOrderDetailsActivity extends BaseActivity {
    @BindView(R.id.tv_trade_no)
    TextView tvTradeNo;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_trade_state)
    TextView tvTradeState;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.ll_note)
    LinearLayout llNote;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ll_confirm)
    LinearLayout llConfirm;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    @BindView(R.id.tv_shop_address)
    TextView tvShopAddress;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.ll_shop)
    LinearLayout llShop;
    @BindView(R.id.tv_list)
    TextView tvList;
    private String tradeNo = "";
    private String totalMoney = "";
    private ProductBuyAdapter productBuyAdapter;
    private List<ProductSelectBean> mData = new ArrayList<>();//已购买商品
    private boolean isShopProduct; //是否商家自提
    private String receiveMobile = "";
    CommonOperateDialog commonOperateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMiddleText("订单详情");
    }

    @Override
    public int setBaseContentView() {
        return R.layout.activity_order_details;
    }

    @Override
    public void init() {
        tradeNo = getIntent().getStringExtra("tradeNo");
        isShopProduct = getIntent().getBooleanExtra("isShopProduct", false);
        productBuyAdapter = new ProductBuyAdapter(mData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
        recyclerView.setAdapter(productBuyAdapter);
        if (isShopProduct) {
            tvAddress.setVisibility(View.GONE);
            llShop.setVisibility(View.VISIBLE);
        } else {
            llShop.setVisibility(View.GONE);
        }
        getData();
    }

    @Override
    public void reLoadData() {
        super.reLoadData();
        getData();
    }

    public void getData() {
        OkGo.<CommonReturnData<OrderDetailBean>>get(Constants.BASE_URL + "provider/queryOrderSingle")
                .params("tradeNo", tradeNo)
                .params("type", isShopProduct ? "1" : "0")
                .execute(new DialogCallback<CommonReturnData<OrderDetailBean>>(this, true) {
                    @Override
                    public void onSuccess(CommonReturnData<OrderDetailBean> commonReturnData) {
                        OrderDetailBean orderDetailBean = commonReturnData.getData();
                        tvTradeNo.setText("订单编号：" + tradeNo);
                        totalMoney = orderDetailBean.getAmount();
                        tvPrice.setText("金额：￥" + orderDetailBean.getAmount());
                        if (isShopProduct) {
                            tvTradeState.setText(getSelfStateShow(orderDetailBean.getSelf_state()));
                            llConfirm.setVisibility("0".equals(orderDetailBean.getSelf_state()) ? View.VISIBLE : View.GONE);
                        }
//                        else {
//                            tvTradeState.setText(getStateShow(orderDetailBean.getState()));
//                            llConfirm.setVisibility("0".equals(orderDetailBean.getState()) ? View.VISIBLE : View.GONE);
//                        }
                        tvTel.setText("电话:" + orderDetailBean.getProviderPhone());//自提商品才有
                        tvShopAddress.setText("地址:" + orderDetailBean.getProviderAddress());//自提商品才有
                        tvShopName.setText(orderDetailBean.getProviderName());
                        tvAddress.setText(orderDetailBean.getReceiveAddress());

                         receiveMobile = orderDetailBean.getReceiveMobile();
                        tvMobile.setText(orderDetailBean.getReceiveMobile());
                        tvName.setText(orderDetailBean.getReceiveName());
                        mData.clear();
                        mData.addAll(orderDetailBean.getMerclist());
                        productBuyAdapter.notifyDataSetChanged();
                        if (TextUtils.isEmpty(orderDetailBean.getReceiveNotes())) {
                            llNote.setVisibility(View.GONE);
                        } else {
                            llNote.setVisibility(View.VISIBLE);
                            tvNote.setText(orderDetailBean.getReceiveNotes());
                        }
                    }
                });
    }

    /**
     * 获取在线订单状态
     *
     * @return
     */
    private String getStateShow(String state) {
        switch (state) {
            case "0":
                return "待支付";
            case "1":
                return "待发货";
            case "2":
                return "待收货";
            case "3":
                return "已完成";
        }
        return "";
    }


    /**
     * 获取自提订单状态
     *
     * @return
     */
    private String getSelfStateShow(String state) {
        switch (state) {
            case "0":
                return "待确认";
            case "1":
                return "待支付";
            case "2":
                return "待提货";
            case "3":
                return "已完成";
        }
        return "";
    }

    @OnClick({R.id.tv_confirm, R.id.img_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_call:
                if (!TextUtils.isEmpty(receiveMobile)) {
                    callAction(receiveMobile);
                }
                break;
            case R.id.tv_confirm:  //确认提货
                commonOperateDialog = new CommonOperateDialog(SelfOrderDetailsActivity.this, new CommonOperateDialog.OperateListener() {
                    @Override
                    public void sure() {
                        orderConfirm(tradeNo);
                        commonOperateDialog.dismiss();
                    }
                });
                commonOperateDialog.setContentText("确定要确认此订单吗？");
                commonOperateDialog.show();
                break;

        }
    }

    /**
     * 订单确认
     */
    public void orderConfirm(String tradeNo) {
        OkGo.<CommonReturnData<Object>>post(Constants.BASE_URL + "provider/updateHorOrderConfirmed")
                .params("tradeNo", tradeNo)
                .execute(new DialogCallback<CommonReturnData<Object>>(this) {
                    @Override
                    public void onSuccess(CommonReturnData<Object> commonReturnData) {
                        showToast("确认成功");
                        EventBus.getDefault().post(new EventBean(EventBean.SHOP_GOODS_CONFIRM));
                        finish();
                    }
                });
    }

    /**
     * 拨打电话
     *
     * @param mobileNumber
     */
    public void callAction(String mobileNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber));
        startActivity(callIntent);
    }
}
