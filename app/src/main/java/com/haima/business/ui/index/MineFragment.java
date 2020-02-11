package com.haima.business.ui.index;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haima.business.R;
import com.haima.business.base.BaseFragmentV4;
import com.haima.business.base.Constants;
import com.haima.business.bean.BusinessInfoBean;
import com.haima.business.bean.EventBean;
import com.haima.business.callback.JsonFragmentCallback;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.ui.ApplyMoneyActivity;
import com.haima.business.ui.ChargeRecordActivity;
import com.haima.business.utils.PhoneUtil;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends BaseFragmentV4 {
//    @BindView(R.id.tv_shop_name)
//    TextView tvShopName;
//    @BindView(R.id.tv_shop_address)
//    TextView tvShopAddress;
//    @BindView(R.id.tv_shop_mobile)
//    TextView tvShopMobile;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.tv_bank_card)
    TextView tvBankCard;
    @BindView(R.id.ll_free_delivery)
    LinearLayout llFreeDelivery;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.btn_apply)
    Button btnApply;
    @BindView(R.id.ll_balance)
    LinearLayout llBalance;
    @BindView(R.id.ll_charge_record)
    LinearLayout llChargeRecord;
    Unbinder unbinder;
    View viewTop;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_business_info)
    LinearLayout llBusinessInfo;
    private String balance;

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(View view) {
        viewTop = view.findViewById(R.id.view_top);
    }

    @Override
    protected void initViewData() {
        ViewGroup.LayoutParams params = viewTop.getLayoutParams();
        //获取当前控件的布局对象
        params.height = PhoneUtil.getStatusHeight(getActivity());//设置当前控件布局的高度
        viewTop.setLayoutParams(params);
    }

    @Override
    public void initData() {
        getBusinessData();
    }

    @Override
    public void reLoadData() {
        super.reLoadData();
        getBusinessData();
    }

    public static MineFragment getInstance() {
        MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 获取数据
     */
    private void getBusinessData() {
        OkGo.<CommonReturnData<BusinessInfoBean>>get(Constants.BASE_URL + "provider/getProviderInfoByIdDetail")
                .params("providerId", "ORG00001")
                .execute(new JsonFragmentCallback<CommonReturnData<BusinessInfoBean>>(this, true, true) {
                    @Override
                    public void onSuccess(CommonReturnData<BusinessInfoBean> commonReturnData) {
                        BusinessInfoBean businessInfoBean = commonReturnData.getData();
                        if (businessInfoBean == null) {
                            return;
                        }
//                        tvShopAddress.setText("地址:" + businessInfoBean.getProvider_address());
//                        tvShopMobile.setText("电话:" + businessInfoBean.getProvider_phone());
                        tvTitle.setText(businessInfoBean.getProvider_name());
                        tvBalance.setText("¥" + businessInfoBean.getProvider_balance());
                        balance = businessInfoBean.getProvider_balance();
                        tvBankName.setText(businessInfoBean.getProvider_card_bank());
                        tvBankCard.setText("卡号:" + businessInfoBean.getProvider_card_no());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean data) {
        switch (data.getEvent()) {
            case EventBean.APPLY_MONEY_SUCCESS:
                getBusinessData();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @OnClick({R.id.btn_apply, R.id.ll_charge_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_apply:
                if (!TextUtils.isEmpty(balance) && Double.parseDouble(balance) >= 1) {
                    startActivity(new Intent(mContext, ApplyMoneyActivity.class).putExtra("balance", balance));
                }else {
                    showToast("余额至少1元方可提现");
                }
                break;
            case R.id.ll_charge_record:
                startActivity(new Intent(mContext, ChargeRecordActivity.class));
                break;

        }
    }
}
