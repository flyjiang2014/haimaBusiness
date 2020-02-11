package com.haima.business.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.haima.business.R;
import com.haima.business.base.BaseActivity;
import com.haima.business.base.Constants;
import com.haima.business.bean.ReceiveGoodsInfoBean;
import com.haima.business.bean.ServiceOrderDataBean;
import com.haima.business.callback.JsonCallback;
import com.haima.business.okgomodel.CommonReturnData;
import com.haima.business.ui.order.ReceiveGoodsConfirmActivity;
import com.haima.business.ui.order.ReceiveServiceConfirmActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZXingView mZXingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleMiddleText("扫一扫");
    }

    @Override
    public int setBaseContentView() {
        return R.layout.activity_scan;
    }

    @Override
    public void init() {
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        String[] info = result.split("&");
        if(info[0].equals("haima")){
            vibrate();
            if("self".equals(info[1])){
                confirmSelfOrder(info[2],info[3]);
            }else if("service".equals(info[1])) {
                confirmServiceOrder(info[2],info[3]);
            }else {
                showToast("二维码来源错误");
                mZXingView.startSpot(); // 开始识别
            }

        }else{
            vibrate();
            showToast("二维码来源错误");
            mZXingView.startSpot(); // 开始识别
        }
    }

    /**
     * 自提码
     * @param tradeNo
     * @param userMobile
     */
    public void confirmSelfOrder(final String tradeNo, final String userMobile){
        OkGo.<CommonReturnData<ReceiveGoodsInfoBean>>get(Constants.BASE_URL + "provider/scanHotOrder")
                .params("tradeNo", tradeNo)
                .params("providerId", "ORG00001")
                .params("userMobile",userMobile)
                .execute(new JsonCallback<CommonReturnData<ReceiveGoodsInfoBean>>(this) {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onError(Response<CommonReturnData<ReceiveGoodsInfoBean>> response) {
                        super.onError(response);
                        mZXingView.startSpot(); // 开始识别
                    }
                    @Override
                    public void onSuccess(CommonReturnData<ReceiveGoodsInfoBean> commonReturnData) {
                        ReceiveGoodsInfoBean receiveGoodsInfoBean = commonReturnData.getData();
                        Intent intent  = new Intent(mContext,ReceiveGoodsConfirmActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("receiveGoodsInfoBean", receiveGoodsInfoBean);
                        intent.putExtras(bundle);
                        intent.putExtra("tradeNo",tradeNo);
                        intent.putExtra("userMobile",userMobile);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    /**
     * 服务码
     * @param tradeNo
     * @param userMobile
     */
    public void confirmServiceOrder(final String tradeNo, final String userMobile){
        OkGo.<CommonReturnData<ServiceOrderDataBean>>get(Constants.BASE_URL + "provider/queryServiceOrderByTradeNo")
                .params("tradeNo", tradeNo)
                .params("providerId", "ORG00001")
                .execute(new JsonCallback<CommonReturnData<ServiceOrderDataBean>>(this) {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onError(Response<CommonReturnData<ServiceOrderDataBean>> response) {
                        super.onError(response);
                        mZXingView.startSpot(); // 开始识别
                    }
                    @Override
                    public void onSuccess(CommonReturnData<ServiceOrderDataBean> commonReturnData) {
                        showToast("识别成功");
                        ServiceOrderDataBean serviceOrderDataBean = commonReturnData.getData();
                        Intent intent  = new Intent(mContext, ReceiveServiceConfirmActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("serviceOrderDataBean", serviceOrderDataBean);
                        intent.putExtras(bundle);
                        intent.putExtra("tradeNo",tradeNo);
                        intent.putExtra("userMobile",userMobile);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

}
