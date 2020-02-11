package com.haima.business.ui.index;

import android.os.Bundle;
import android.view.View;

import com.haima.business.R;
import com.haima.business.base.BaseFragmentV4;

/**
 * Created by  on 2019/12/20.
 * 文件说明：
 */
public class PublishFragment extends BaseFragmentV4 {

    @Override
    protected int onLayoutRes() {
        return R.layout.fragment_publish;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    protected void initViewData() {

    }

    @Override
    public void initData() {

    }

    public static PublishFragment getInstance() {
        PublishFragment fragment = new PublishFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}
