package com.in.timelinenested.viewClass;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.in.timelinenested.R;

/**
 * Created by Administrator on 2018/6/8.
 */


public class LoadingDialog {
    com.in.timelinenested.viewClass.LVCircularRing cr_loading;
    ImageView iv_state;
    Dialog mLoadingDialog;
    TextView Text;
    Context context;

    public LoadingDialog(Context context, String loading) {
        this.context = context;
        // 首先得到整个View
        View view = View.inflate(context, R.layout.loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = view.findViewById(R.id.dialog_view);
        // 页面中的LoadingView
        cr_loading = view.findViewById(R.id.cr_loading);
        iv_state = view.findViewById(R.id.iv_state);
        iv_state.setVisibility(View.INVISIBLE);
        // 页面中显示文本
        Text = view.findViewById(R.id.loading_text);
        // 显示文本
        Text.setText(loading);
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置返回键无效
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void setSuccessful(String successful){
        cr_loading.setVisibility(View.INVISIBLE);
        iv_state.setVisibility(View.VISIBLE);
        iv_state.setImageResource(R.drawable.tick1);
        Text.setText(successful);
    }

    public void setFailed(String failed){
        cr_loading.setVisibility(View.INVISIBLE);
        iv_state.setVisibility(View.VISIBLE);
        iv_state.setImageResource(R.drawable.failed);
        Text.setText(failed);
    }

    public void setRunTimeOut(String timeOut){
        cr_loading.setVisibility(View.INVISIBLE);
        iv_state.setVisibility(View.VISIBLE);
        iv_state.setImageResource(R.drawable.timeout);
        Text.setText(timeOut);
    }
    public void show() {
        mLoadingDialog.show();
        cr_loading.startAnim();
    }

    public void close() {
        if (mLoadingDialog != null) {
            cr_loading.stopAnim();
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}