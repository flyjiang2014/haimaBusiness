package com.haima.business.ui.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haima.business.R;
import com.haima.business.adapter.SelfOrderListAdapter;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.BaseFragmentV4;
import com.haima.business.base.Constants;
import com.haima.business.bean.EventBean;
import com.haima.business.bean.OrderListBean;
import com.haima.business.callback.JsonFragmentCallback;
import com.haima.business.dialog.CommonOperateDialog;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.ui.order.SelfOrderDetailsActivity;
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
 * Created by  on 2019/11/8.
 * 文件说明：自提订单列表展示
 */
public class SelfOrderItemFragment extends BaseFragmentV4 implements OnRefreshListener {

    private int state = -1;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private SelfOrderListAdapter selfOrderListAdapter;
    List<OrderListBean> orderListBeans = new ArrayList<>();
    CommonOperateDialog commonOperateDialog;
    int tempClickPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static SelfOrderItemFragment getInstance(int state) {
        SelfOrderItemFragment fragment = new SelfOrderItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_self_order_item;
    }

    @Override
    protected void initViewData() {
        state = getArguments().getInt("state");
        selfOrderListAdapter = new SelfOrderListAdapter(orderListBeans);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(selfOrderListAdapter);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setOnRefreshListener(this);
        selfOrderListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if (view.getId() == R.id.tv_confirm) {
                    commonOperateDialog = new CommonOperateDialog((BaseActivity) getActivity(), new CommonOperateDialog.OperateListener() {
                        @Override
                        public void sure() {
                            orderConfirm(position,orderListBeans.get(position).getTrade_no());
                            commonOperateDialog.dismiss();
                        }
                    });
                    commonOperateDialog.setContentText("确定要确认此订单吗？");
                    commonOperateDialog.show();
                }
            }
        });
        selfOrderListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                tempClickPosition = position;
                Intent intent = new Intent(mContext, SelfOrderDetailsActivity.class);
                intent.putExtra("tradeNo", orderListBeans.get(position).getTrade_no());
                intent.putExtra("isShopProduct", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        getDataList();
    }

    @Override
    public void reLoadData() {
        super.reLoadData();
        getDataList();
    }

    @Override
    public void initView(View view) {
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        recyclerView =  view.findViewById(R.id.recyclerView);
        emptyView = getEmptyView();
    }

    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataList();
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        }, PULL_DOWN_TIME);
    }

    /**
     * 获取订单列表数据
     */
    private void getDataList() {
        GetRequest getRequest = OkGo.<CommonReturnData<List<OrderListBean>>>get(Constants.BASE_URL + "provider/queryOrderById")
                .params("providerId", "ORG00001");
        if (state >= 0) {
            getRequest.params("state", state);
        }

        getRequest.execute(new JsonFragmentCallback<CommonReturnData<List<OrderListBean>>>(this, true, true) {
            @Override
            public void onSuccess(CommonReturnData<List<OrderListBean>> commonReturnData) {
                orderListBeans.clear();
                orderListBeans.addAll(commonReturnData.getData());
                selfOrderListAdapter.notifyDataSetChanged();
                if (orderListBeans.size() == 0) {
                    selfOrderListAdapter.setEmptyView(emptyView);
                }
            }
        });
    }

    /**
     * 订单确认
     */
    public void orderConfirm(final int position,String tradeNo) {
        OkGo.<CommonReturnData<Object>>post(Constants.BASE_URL + "provider/updateHorOrderConfirmed")
                .params("tradeNo", tradeNo)
                .execute(new JsonFragmentCallback<CommonReturnData<Object>>(this, false, true) {
            @Override
            public void onSuccess(CommonReturnData<Object> commonReturnData) {
                showToast("确认成功");
                selfOrderListAdapter.remove(position);
                if(selfOrderListAdapter.getItemCount()==0){
                    selfOrderListAdapter.setEmptyView(emptyView);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean data) {
        switch (data.getEvent()) {
            case EventBean.SHOP_GOODS_CONFIRM:
                if(state==0){
                    selfOrderListAdapter.remove(tempClickPosition);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

}
