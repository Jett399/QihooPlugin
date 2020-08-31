package com.example.qihooplugin.util;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Class: DownLoadTask
 * @Description:
 * @author: Jett
 * @Date: 8/24/20-2:14 PM
 */

public class DownLoadTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "DownLoadTask";
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ThreadLocal<ProgressDialog> mProgressDialog = new ThreadLocal<>();


    public DownLoadTask(Context context, ProgressDialog progressDialog) {
        this.mProgressDialog.set(progressDialog);
        this.context = context;
    }


    @Override
    protected String doInBackground(String... sUrl) {
        Log.e(TAG,"开始准备下载");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG,connection.getResponseCode()+ ","+connection.getResponseMessage()) ;
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            File apkUnzipDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            output = new FileOutputStream(apkUnzipDir + "/plugin1.apk");
            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        ProgressDialog progressDialog = mProgressDialog.get();
        if (progressDialog != null) {
            progressDialog.show();

        }
    }

    @Override
    protected void onProgressUpdate(final Integer... progress) {
        super.onProgressUpdate(progress);
        ProgressDialog progressDialog = mProgressDialog.get();
        if (progressDialog != null) {
            progressDialog.setProgress(progress[0]);
        }
        Log.e("DownLoadTask", "当前进度:" + progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mWakeLock!=null){
            mWakeLock.release();
            context = null ;
        }
        ProgressDialog progressDialog = mProgressDialog.get();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (result != null) {
            Log.d(TAG, "onPostExecute: Download error: " + result);
            if (onDownloadCompletedListener != null) {
                onDownloadCompletedListener.onDownloadFail();
            }
        } else {
            Log.d(TAG, "onPostExecute: File downloaded");
            if (onDownloadCompletedListener != null) {
                onDownloadCompletedListener.onDownloadCom();
            }
        }
    }


    private OnDownloadCompletedListener onDownloadCompletedListener;

    public void setOnDownloadCompletedListener(OnDownloadCompletedListener onDownloadCompletedListener) {
        this.onDownloadCompletedListener = onDownloadCompletedListener;
    }

    public interface OnDownloadCompletedListener {
        void onDownloadCom();

        void onDownloadFail();
    }
}