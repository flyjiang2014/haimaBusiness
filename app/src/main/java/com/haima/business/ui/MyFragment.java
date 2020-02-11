package com.haima.business.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haima.business.R;
import com.haima.business.base.BaseFragmentV4;

/**
 * Created by  on 2019/12/5.
 * 文件说明：
 */
@Deprecated
public class MyFragment extends BaseFragmentV4 {

    private int state = -1;
    private TextView tv;

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_layout;
    }

    @Override
    public void initView(View view) {
        tv = view.findViewById(R.id.tv);
    }

    @Override
    protected void initViewData() {
        state =  getArguments().getInt("state");
    }

    @Override
    public void initData() {

    }

    public static MyFragment getInstance(int state) {
        MyFragment fragment = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        fragment.setArguments(bundle);
        return fragment;
    }
}
