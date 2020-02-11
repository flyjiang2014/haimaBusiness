package com.haima.business.ui.index;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haima.business.R;
import com.haima.business.adapter.ServiceListOrderAdapter;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.BaseFragmentV4;
import com.haima.business.base.Constants;
import com.haima.business.bean.EventBean;
import com.haima.business.bean.ServiceOrderDataBean;
import com.haima.business.callback.JsonFragmentCallback;
import com.haima.business.dialog.CommonOperateDialog;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.widget.DividerItemDecoration;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.haima.business.base.BaseActivity.PULL_DOWN_TIME;

/**
 * Created by  on 2019/12/5.
 * 文件说明：
 */
public class ServiceOrderItemFragment extends BaseFragmentV4 implements OnRefreshListener {
    RecyclerView recyclerView;
    SmartRefreshLayout smartRefreshLayout;
    private ServiceListOrderAdapter serviceListOrderAdapter;
    private List<ServiceOrderDataBean> serviceOrderDataBeans = new ArrayList<>();
    private View emptyView;
    private int state = -1;
    CommonOperateDialog commonOperateDialog;


    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_service_order_item;
    }

    @Override
    protected void initViewData() {
        state = getArguments().getInt("state");
        emptyView = getEmptyView();
        serviceListOrderAdapter = new ServiceListOrderAdapter(serviceOrderDataBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
        recyclerView.setAdapter(serviceListOrderAdapter);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setOnRefreshListener(this);
        serviceListOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                commonOperateDialog = new CommonOperateDialog((BaseActivity) getActivity(), new CommonOperateDialog.OperateListener() {
                    @Override
                    public void sure() {
                        confirmOrder(position,serviceOrderDataBeans.get(position).getTrade_no());
                        commonOperateDialog.dismiss();
                    }
                });
                commonOperateDialog.setContentText("确定要确认此订单吗？");
                commonOperateDialog.show();

            }
        });
    }

    @Override
    public void initData() {
        getOrderListData();
    }

    private void getOrderListData() {
        GetRequest getRequest = OkGo.<CommonReturnData<List<ServiceOrderDataBean>>>get(Constants.BASE_URL + "provider/queryServiceOrderById")
                .params("providerId", "ORG00001");
        if (state >= 0) {
            getRequest.params("state", state);
        }
        getRequest.execute(new JsonFragmentCallback<CommonReturnData<List<ServiceOrderDataBean>>>(this, true, true) {
            @Override
            public void onSuccess(CommonReturnData<List<ServiceOrderDataBean>> commonReturnData) {
                serviceOrderDataBeans.clear();
                serviceOrderDataBeans.addAll(commonReturnData.getData());
                serviceListOrderAdapter.notifyDataSetChanged();
                if (serviceOrderDataBeans.size() == 0) {
                    serviceListOrderAdapter.setEmptyView(emptyView);
                }
            }
        });
    }

    private void confirmOrder(final int position,String tradeNo) {
        GetRequest getRequest = OkGo.<CommonReturnData<Object>>get(Constants.BASE_URL + "provider/updateServiceOrderConfirmed")
                .params("tradeNo", tradeNo)
                .params("providerId", "ORG00001");

        getRequest.execute(new JsonFragmentCallback<CommonReturnData<Object>>(this, true, true) {
            @Override
            public void onSuccess(CommonReturnData<Object> commonReturnData) {
                showToast("确认成功");
                serviceListOrderAdapter.remove(position);
                if(serviceListOrderAdapter.getItemCount()==0){
                    serviceListOrderAdapter.setEmptyView(emptyView);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
    }

    public static ServiceOrderItemFragment getInstance(int state) {
        ServiceOrderItemFragment fragment = new ServiceOrderItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean data) {
        switch (data.getEvent()) {
            case EventBean.RECEIVE_GOODS_SUCCESS:
                break;
        }
    }

    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                getOrderListData();
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        }, PULL_DOWN_TIME);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
