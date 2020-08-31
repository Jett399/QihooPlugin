package com.example.qihooplugin.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.qihooplugin.R;

/**
 * @Class: ProgressDialog
 * @Description:
 * @author: Jett
 * @Date: 8/28/20-11:31 AM
 */
public class ProgressDialog extends LinearLayout {

    private AlertDialog dialog ;
    private TextView tvTitle;
    private ProgressBar progressBar;
    private TextView tvNumber;
    private TextView tvProgress;


    public ProgressDialog(Context context) {
        super(context);
        initView(context);
    }

    public ProgressDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProgressDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.progress_dialog,null);
        dialog.setView(view,0,0,0,0);
        dialog.setCancelable(false);
        tvTitle = view.findViewById(R.id.progress_dialog_tv_title);
        progressBar = view.findViewById(R.id.progress_dialog_pb);
        tvNumber = view.findViewById(R.id.progress_dialog_tv_number);
        tvProgress = view.findViewById(R.id.progress_dialog_tv_percent);

    }

    public void show(){
        if (dialog!=null && !dialog.isShowing()){
            dialog.show();
        }
    }

    public void setTvTitleText(String text){
        if (text == null) throw new NullPointerException("tVTitle不能为空") ;
        if (tvTitle!=null){
            tvTitle.setText(text);
        }
    }


    public void setTvNumberText(String text){
        if (tvNumber!=null){
            tvNumber.setText(text);
        }
    }

    public void setTvProgress(String text){
        if (tvProgress!=null){
            tvProgress.setText(text);
        }
    }

    public void dismiss(){
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void setProgress(Integer progress) {
//        System.out.println("hahahhah哈哈");
        progressBar.setProgress(progress);
        setTvNumberText(progress+"%");
        setTvProgress(progress+"/100");
    }

    public void printLog(){

    }
}
