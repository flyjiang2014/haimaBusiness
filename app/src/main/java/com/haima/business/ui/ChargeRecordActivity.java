package com.haima.business.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haima.business.R;
import com.haima.business.adapter.ChargeRecordAdapter;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.Constants;
import com.haima.business.bean.ChargeRecordBean;
import com.haima.business.callback.DialogCallback;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.widget.DividerItemDecoration;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ChargeRecordActivity extends BaseActivity implements OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    ChargeRecordAdapter chargeRecordAdapter;
    private View emptyView;
    private List<ChargeRecordBean> chargeRecordBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMiddleText("交易记录");
    }

    @Override
    public int setBaseContentView() {
        return R.layout.activity_charge_record;
    }

    @Override
    public void init() {
        emptyView = getEmptyView();
        chargeRecordAdapter = new ChargeRecordAdapter(chargeRecordBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
        recyclerView.setAdapter(chargeRecordAdapter);
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setEnableLoadMore(false);
        getChargeRecordData();
    }

    @Override
    public void reLoadData() {
        super.reLoadData();
        getChargeRecordData();
    }

    /**
     * 获取交易记录
     */
    private void getChargeRecordData() {
        OkGo.<CommonReturnData<List<ChargeRecordBean>>>get(Constants.BASE_URL + "provider/getOrgTrans")
                .params("providerId", "ORG00001")
                .execute(new DialogCallback<CommonReturnData<List<ChargeRecordBean>>>(this, true) {
                    @Override
                    public void onSuccess(CommonReturnData<List<ChargeRecordBean>> commonReturnData) {
                        chargeRecordBeanList.clear();
                        chargeRecordBeanList.addAll(commonReturnData.getData());
                        chargeRecordAdapter.notifyDataSetChanged();
                        if (chargeRecordBeanList.size() == 0) {
                            chargeRecordAdapter.setEmptyView(emptyView);
                        }
                    }
                });
    }

    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                getChargeRecordData();
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        }, PULL_DOWN_TIME);
    }
}
