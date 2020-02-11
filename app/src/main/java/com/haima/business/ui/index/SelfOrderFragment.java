package com.haima.business.ui.index;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.haima.business.R;
import com.haima.business.base.BaseFragmentV4;
import com.haima.business.bean.EventBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by  on 2019/12/5.
 * 文件说明：
 */
public class SelfOrderFragment extends BaseFragmentV4 {
    private SlidingTabLayout tab_layout;
    private ArrayList<BaseFragmentV4> mFragments = new ArrayList<>();
    private ViewPager viewPager;
    private MyFragmentAdapter mAdapter;

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_self_order;
    }

    @Override
    protected void initViewData() {
        for (int i = 0; i <5 ; i++) {
            SelfOrderItemFragment selfOrderItemFragment = SelfOrderItemFragment.getInstance(i-1);
            mFragments.add(selfOrderItemFragment);
        }
        mAdapter = new MyFragmentAdapter(getChildFragmentManager(), mFragments);
        viewPager.setAdapter(mAdapter);
        tab_layout.setViewPager(viewPager);
    }

    @Override
    public void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        tab_layout = view.findViewById(R.id.tab_layout);
    }

    public static SelfOrderFragment getInstance() {
        SelfOrderFragment fragment = new SelfOrderFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
    public class MyFragmentAdapter extends FragmentStatePagerAdapter {
        private ArrayList<BaseFragmentV4> mFragments;//碎片数组
        private String[] types = {"全部", "待确认", "待支付", "待提货", "已完成"};
        FragmentManager fm;
        public MyFragmentAdapter(FragmentManager fm, ArrayList<BaseFragmentV4> mFragments) {
            super(fm);
            this.fm = fm;
            this.mFragments = mFragments;
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE; //这个是必须的
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return types[position];
        }

        @Override
        public Fragment getItem(int position) {
            int size = mFragments.size();
            if (position >= size && size > 0)
                return mFragments.get(size - 1);
            return mFragments.get(position);
        }
        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean data) {
        switch (data.getEvent()) {
            case EventBean.RECEIVE_GOODS_SUCCESS:
                if(viewPager.getCurrentItem()== 0||viewPager.getCurrentItem()== 3||viewPager.getCurrentItem()== 4){//全部，待提货,已完成
                  mFragments.get(viewPager.getCurrentItem()).initData();
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
