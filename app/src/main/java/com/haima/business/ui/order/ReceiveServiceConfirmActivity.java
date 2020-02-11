package com.haima.business.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haima.business.R;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.Constants;
import com.haima.business.bean.EventBean;
import com.haima.business.bean.ServiceOrderDataBean;
import com.haima.business.callback.JsonCallback;
import com.haima.business.dialog.CommonOperateDialog;
import com.haima.business.dialog.MessageShowDialog;
import com.haima.business.okgomodel.CommonReturnData;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 服务订单确认服务
 */
public class ReceiveServiceConfirmActivity extends BaseActivity {
    ServiceOrderDataBean serviceOrderDataBean;
    @BindView(R.id.tv_trade_no)
    TextView tvTradeNo;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_confirm_service)
    TextView tvConfirmService;
    @BindView(R.id.ll_confirm_service)
    LinearLayout llConfirmService;
    @BindView(R.id.tv_service_content)
    TextView tvServiceContent;
    private String tradeNo = "";
    private String userMobile = "";
    private CommonOperateDialog commonOperateDialog;
    private MessageShowDialog messageShowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMiddleText("服务");
    }

    @Override
    public int setBaseContentView() {
        return R.layout.activity_receive_service_confirm;
    }

    @Override
    public void init() {
        tradeNo = getIntent().getStringExtra("tradeNo");
        userMobile = getIntent().getStringExtra("userMobile");
        serviceOrderDataBean = (ServiceOrderDataBean) getIntent().getSerializableExtra("serviceOrderDataBean");
        tvTradeNo.setText("订单编号："+tradeNo);
        tvPrice.setText("金额：￥" +serviceOrderDataBean.getAmount());
        tvServiceContent.setText(serviceOrderDataBean.getOrg_summary());
        if("2".equals(serviceOrderDataBean.getState())){
            tvConfirmService.setVisibility(View.VISIBLE);
            tvConfirmService.setText("确认服务");
            tvConfirmService.setEnabled(true);
            tvConfirmService.setBackgroundResource(R.drawable.frame_solid_orange);
        }else if("3".equals(serviceOrderDataBean.getState())){
            tvConfirmService.setVisibility(View.VISIBLE);
            tvConfirmService.setText("服务完成");
            tvConfirmService.setEnabled(false);
            tvConfirmService.setBackgroundResource(R.drawable.frame_solid_grey);
        }else if("4".equals(serviceOrderDataBean.getState())){
            tvConfirmService.setVisibility(View.VISIBLE);
            tvConfirmService.setText("待评价");
            tvConfirmService.setEnabled(false);
            tvConfirmService.setBackgroundResource(R.drawable.frame_solid_grey);
        }
        else if("5".equals(serviceOrderDataBean.getState())){
            tvConfirmService.setVisibility(View.VISIBLE);
            tvConfirmService.setText("已关闭");
            tvConfirmService.setEnabled(false);
            tvConfirmService.setBackgroundResource(R.drawable.frame_solid_grey);
        }
    }
    /**
     * 获取订单状态
     *
     * @return
     */
    private String getStateShow(String state) {
        switch (state) {
            case "0":
                return "待商家确认";
            case "1":
                return "待支付";
            case "2":
                return "待服务";
            case "3":
                return "已完成";
            case "4":
                return "待评价";
            case "5":
                return "已关闭";
        }
        return "";
    }


    @OnClick(R.id.tv_confirm_service)
    public void onViewClicked() {
        commonOperateDialog = new CommonOperateDialog(ReceiveServiceConfirmActivity.this, new CommonOperateDialog.OperateListener() {
            @Override
            public void sure() {
                serviceFinishConfirm();
                commonOperateDialog.dismiss();
            }
        });
        commonOperateDialog.setContentText("确定已完成服务吗？");
        commonOperateDialog.show();
    }
    public void serviceFinishConfirm(){
        OkGo.<CommonReturnData<Object>>get(Constants.BASE_URL + "provider/updateServiceOrderServiced")
                .params("tradeNo", tradeNo)
                .params("providerId", "ORG00001")
                .execute(new JsonCallback<CommonReturnData<Object>>(this) {
                    @Override
                    public void onSuccess(CommonReturnData<Object> commonReturnData) {
                        EventBus.getDefault().post(new EventBean(EventBean.SERVICE_FINISH_SUCCESS));
                        messageShowDialog = new MessageShowDialog(ReceiveServiceConfirmActivity.this, new MessageShowDialog.OperateListener() {
                            @Override
                            public void sure() {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        messageShowDialog.setContentText("服务完成");
                        messageShowDialog.show();
                    }
                });
    }
}
