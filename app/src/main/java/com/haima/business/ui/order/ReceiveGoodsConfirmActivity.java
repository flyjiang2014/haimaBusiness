package com.haima.business.ui.order;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haima.business.R;
import com.haima.business.adapter.ProductBuyAdapter;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.Constants;
import com.haima.business.bean.EventBean;
import com.haima.business.bean.ReceiveGoodsInfoBean;
import com.haima.business.callback.JsonCallback;
import com.haima.business.dialog.CommonOperateDialog;
import com.haima.business.dialog.MessageShowDialog;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.widget.DividerItemDecoration;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 提货操作
 */
public class ReceiveGoodsConfirmActivity extends BaseActivity {
    @BindView(R.id.tv_trade_no)
    TextView tvTradeNo;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.ll_confirm_receive)
    LinearLayout llConfirmReceive;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_confirm_receive)
    TextView tvConfirmReceive;
    private String tradeNo = "";
    private String userMobile= "";
    private ProductBuyAdapter productBuyAdapter;
    private CommonOperateDialog commonOperateDialog;
    private  ReceiveGoodsInfoBean receiveGoodsInfoBean;
    private MessageShowDialog messageShowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMiddleText("提货");
    }

    @Override
    public int setBaseContentView() {
        return R.layout.activity_receive_goods;
    }

    @Override
    public void init() {
        tradeNo = getIntent().getStringExtra("tradeNo");
        userMobile = getIntent().getStringExtra("userMobile");
        receiveGoodsInfoBean = (ReceiveGoodsInfoBean) getIntent().getSerializableExtra("receiveGoodsInfoBean");
        productBuyAdapter = new ProductBuyAdapter(receiveGoodsInfoBean.getMerclist());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
        recyclerView.setAdapter(productBuyAdapter);
        tvTradeNo.setText("订单编号："+tradeNo);
        tvPrice.setText("金额：￥" +receiveGoodsInfoBean.getAmount());
        if(2==receiveGoodsInfoBean.getSelfState()){
            tvConfirmReceive.setVisibility(View.VISIBLE);
            tvConfirmReceive.setText("确认提货");
            tvConfirmReceive.setEnabled(true);
            tvConfirmReceive.setBackgroundResource(R.drawable.frame_solid_orange);
        }else if(3==receiveGoodsInfoBean.getSelfState()){
            tvConfirmReceive.setVisibility(View.VISIBLE);
            tvConfirmReceive.setText("已提货");
            tvConfirmReceive.setEnabled(false);
            tvConfirmReceive.setBackgroundResource(R.drawable.frame_solid_grey);
        }
    }

    @OnClick(R.id.tv_confirm_receive)
    public void onViewClicked() {
        commonOperateDialog = new CommonOperateDialog(ReceiveGoodsConfirmActivity.this, new CommonOperateDialog.OperateListener() {
            @Override
            public void sure() {
                receiveGoodsConfirm();
                commonOperateDialog.dismiss();
            }
        });
        commonOperateDialog.setContentText("确定要提货吗？");
        commonOperateDialog.show();
    }

    public void receiveGoodsConfirm(){
        OkGo.<CommonReturnData<Object>>get(Constants.BASE_URL + "provider/updateHotOrderGotten")
                .params("tradeNo", tradeNo)
                .params("userMobile", userMobile)
                .execute(new JsonCallback<CommonReturnData<Object>>(this) {
                    @Override
                    public void onSuccess(CommonReturnData<Object> commonReturnData) {
                        EventBus.getDefault().post(new EventBean(EventBean.RECEIVE_GOODS_SUCCESS));
                        messageShowDialog = new MessageShowDialog(ReceiveGoodsConfirmActivity.this, new MessageShowDialog.OperateListener() {
                            @Override
                            public void sure() {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        messageShowDialog.setContentText("提货完成");
                        messageShowDialog.show();
                    }
                });
    }
}
