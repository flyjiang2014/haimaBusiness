package com.haima.business.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.haima.business.R;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.Constants;
import com.haima.business.bean.EventBean;
import com.haima.business.callback.DialogCallback;
import com.haima.business.dialog.CommonOperateDialog;
import com.haima.business.dialog.MessageShowDialog;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.utils.Arith;
import com.haima.business.utils.ClickUtil;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class ApplyMoneyActivity extends BaseActivity {
    @BindView(R.id.et_apply_money)
    EditText etApplyMoney;
    @BindView(R.id.tv_charge)
    TextView tvCharge;
    private String balance="";
    private MessageShowDialog messageShowDialog;
    private CommonOperateDialog commonOperateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMiddleText("提现");
    }

    @Override
    public int setBaseContentView() {
        return R.layout.activity_apply_money;
    }

    @Override
    public void init() {
        balance = getIntent().getStringExtra("balance");
    }

    @OnClick(R.id.tv_charge)
    public void onViewClicked() {
        if (ClickUtil.isFastDoubleClick()) {
            return;  //防止快速多次点击
        }
       final String money = etApplyMoney.getText().toString().trim();
        if(TextUtils.isEmpty(money)){
            showToast("请输入提现金额");
            return;
        }
       if(Arith.sub(Double.parseDouble(balance),Double.parseDouble(money))<0){
           showToast("提现余额不足");
           return;
       }
        commonOperateDialog = new CommonOperateDialog(this, new CommonOperateDialog.OperateListener() {
            @Override
            public void sure() {
                applyMoney(money);
                commonOperateDialog.dismiss();
            }
        });
        commonOperateDialog.setContentText("确认提现");
        commonOperateDialog.show();

    }

    public void applyMoney(String amount) {
        OkGo.<CommonReturnData<Object>>post(Constants.BASE_URL + "provider/orgWithdraw")
                .params("providerId", "ORG00001")
                .params("amount", amount)
                .execute(new DialogCallback<CommonReturnData<Object>>(this) {
                    @Override
                    public void onSuccess(CommonReturnData<Object> commonReturnData) {
                       EventBus.getDefault().post(new EventBean(EventBean.APPLY_MONEY_SUCCESS));
                        messageShowDialog = new MessageShowDialog(ApplyMoneyActivity.this, new MessageShowDialog.OperateListener() {
                            @Override
                            public void sure() {
                                messageShowDialog.dismiss();
                                ApplyMoneyActivity.this.finish();
                            }
                        });
                        messageShowDialog.setContentText(commonReturnData.getMessage());
                        messageShowDialog.show();
                    }
                });
    }
}
