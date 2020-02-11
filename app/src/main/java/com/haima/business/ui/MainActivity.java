package com.haima.business.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.haima.business.R;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.BaseFragmentV4;
import com.haima.business.ui.index.MineFragment;
import com.haima.business.ui.index.PublishFragment;
import com.haima.business.ui.index.SelfOrderFragment;
import com.haima.business.ui.index.ServiceOrderFragment;
import com.haima.business.utils.AndroidWorkaround;
import com.haima.business.widget.SpecialTab;
import com.haima.business.widget.SpecialTabRound;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.util.CLog;
import io.reactivex.functions.Consumer;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private List<BaseFragmentV4> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
    }

    @Override
    public int setBaseContentView() {
        setIsShowTitle(false);
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        permissionApply();
        getmTitleLeftImageView().setVisibility(View.GONE);
        PageNavigationView tab = findViewById(R.id.tab);
       final NavigationController navigationController = tab.custom()
                .addItem(newItem(R.drawable.self_unselect, R.drawable.self_select, "自提"))
                .addItem(newItem(R.drawable.service_unselect, R.drawable.service_select, "服务"))
                .addItem(newRoundItem(R.drawable.ic_nearby_teal_24dp, R.drawable.ic_nearby_teal_24dp, "扫一扫"))
                .addItem(newItem(R.drawable.public_unselect, R.drawable.public_select, "发布"))
                .addItem(newItem(R.drawable.mine_unselect, R.drawable.mine_select, "我的"))
                .build();

        SelfOrderFragment fragment = SelfOrderFragment.getInstance();
        fragments.add(fragment);
        ServiceOrderFragment fragment1 = ServiceOrderFragment.getInstance();
        fragments.add(fragment1);
        PublishFragment publishFragment = PublishFragment.getInstance();
        fragments.add(publishFragment);
        MineFragment mineFragment = MineFragment.getInstance();
        fragments.add(mineFragment);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                navigationController.setSelect(i<2?i:i+1);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                if(index<2){
                    viewPager.setCurrentItem(index);
                }else if(index==2){
                    navigationController.setSelect(old);
                    startActivity(new Intent(mContext,ScanActivity.class));
                }else {
                    viewPager.setCurrentItem(index-1);
                }
            }
            @Override
            public void onRepeat(int index) {
                //重复选中时触发
            }
        });

    }

    /**
     * 正常tab
     */
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        SpecialTab mainTab = new SpecialTab(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0xFF009688);
        return mainTab;
    }

    /**
     * 圆形tab
     */
    private BaseTabItem newRoundItem(int drawable, int checkedDrawable, String text) {
        SpecialTabRound mainTab = new SpecialTabRound(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0xFF009688);
        return mainTab;
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }

    /**
     * permission申请
     */
    @SuppressLint("CheckResult")
    public void permissionApply() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            CLog.d(TAG, permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            CLog.d(TAG, permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                           showToast("拍照权限被关闭,");
                        }
                    }
                });
    }
}
