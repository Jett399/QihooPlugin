package com.example.qihooplugin

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.qihooplugin.util.DownLoadTask
import com.qihoo360.replugin.RePlugin
import com.qihoo360.replugin.model.PluginInfo
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


const val FORWARD_TO_SETTINGS = -1
const val DOWNLOAD_DIALOG = 0
const val DENIED_ONCE = 1
const val DENIED_NEVER = 2
const val LOAD_PLUGIN = 3
const val RECHECK_PERMISSION = 4

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var pluginInfo: PluginInfo? = null
    private lateinit var rxPermissions: RxPermissions
    private var isAutoComplete: Boolean = false
//    val url = "https://dnsndtbook.imags-google.com/system/app/androidapp/caishen-patch.apk"
    private var pluginName = "android_webView"

    val url = "https://github.com/Jett399/package/blob/master/2" +
            "0200828/202008280928_350qihoo_plugin_fix/app-caishen01-release.apk?raw=true"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this)
        autoBoot()
    }


    fun downloadPlugin(view: View) {
        requestPermission(DOWNLOAD_DIALOG)
    }


    fun loadPlugin(view: View) {
        requestPermission(LOAD_PLUGIN)
    }

    private fun requestPermission(type: Int) {
        rxPermissions
            .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        // 用户已经同意该权限
                        if (type == DOWNLOAD_DIALOG) {
                            if (RePlugin.isPluginInstalled("android_webView")) {
                                openPlugin()
                            }else{
//                                showCustomDialog(type)
                                downLoadBegin()
                            }
                        } else {
                            loadPlugin()
                        }
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时。还会提示请求权限的对话框
                        showCustomDialog(DENIED_ONCE)
                    }
                    else -> {
                        // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                        Toast.makeText(this@MainActivity, "拒绝权限", Toast.LENGTH_SHORT).show()
                        showCustomDialog(DENIED_NEVER)

                    }
                }
            }.isDisposed
    }

    private fun showCustomDialog(tag: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        when (tag) {
            DOWNLOAD_DIALOG -> {
//                dialog.apply {
//                    setTitle("提示").setMessage("确定开始下载")
//                        .setPositiveButton("下载") { _, _ -> downLoadBegin() }
//                        .setNegativeButton("取消") { _, _ -> create().dismiss() }
//                        .create().show()
//                }
            }
            DENIED_ONCE -> {
                dialog.apply {
                    setTitle("警告").setMessage("使用前请允许所有权限！")
                        .setNegativeButton("确定") { _, _ -> requestPermission(RECHECK_PERMISSION) }
                        .create().show()
                }

            }
            DENIED_NEVER -> {
                dialog.apply {
                    setTitle("警告")
                        .setMessage("使用前请允许所有权限，否则将强制退出应用！")
                        .setPositiveButton("前往设置") { _, _ -> forwardToSettings() }
                        .setNegativeButton("退出") { _, _ -> finish() }
                        .create().show()
                }
            }
        }

    }

    private fun downLoadBegin() {
//        val mProgressDialog = ProgressDialog(this)
        val mProgressDialog = com.example.qihooplugin.util.ProgressDialog(this)
        mProgressDialog.setTvTitleText("正在下载资源,静待片刻方可~")
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//        mProgressDialog.setCancelable(false)
        val downLoadTask = DownLoadTask(this, mProgressDialog)
        downLoadTask.setOnDownloadCompletedListener(object :
            DownLoadTask.OnDownloadCompletedListener {
            override fun onDownloadCom() {
                if (isAutoComplete) {
                    tv_tips.visibility = View.VISIBLE
                    btn_retry.visibility = View.GONE
                    Handler().postDelayed({
                        loadPlugin()
                    },100)
                }
            }

            override fun onDownloadFail() {
                btn_retry.visibility =View.VISIBLE
                Toast.makeText(this@MainActivity,"下载失败，请检查网络或点击重试~",Toast.LENGTH_LONG).show()
                btn_retry.setOnClickListener {
                    downLoadBegin()
                }
            }
        })

        val pluginDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        val file = File("$pluginDir/plugin1.apk")
        if (file.exists()) {
            Toast.makeText(this, "资源破损，重新下载中~", Toast.LENGTH_LONG).show()
            file.delete()
        }
        downLoadTask.execute(url)

    }

    private fun loadPlugin() {
        //安装插件
        val file: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (RePlugin.isPluginInstalled(pluginName)) {
            openPlugin()
            return
        }

        pluginInfo = RePlugin.install("${file?.absoluteFile}/plugin1.apk")
        if (pluginInfo != null) {
            //预加载插件
            Toast.makeText(this, "首次加载", Toast.LENGTH_LONG).show()
            val preload = RePlugin.preload(pluginInfo)
            if (preload) {
                Handler().postDelayed({
                    if (isAutoComplete) {
                        openPlugin()
                        isAutoComplete = false
                    }
                }, 100)
            }
            Log.d(TAG, "loadPlugin: 安装完成")
//            Toast.makeText(this, "安装完成", Toast.LENGTH_LONG).show()
        } else {
//            progress.dismiss()
            Log.d(TAG, "loadPlugin: 安装失败 ")
//            Toast.makeText(this, "安装失败", Toast.LENGTH_LONG).show()
        }
    }


    fun startActivityForPlugin(view: View?) {

        openPlugin()


    }

    private fun openPlugin() {
//        val intent = Intent()
//        intent.component = ComponentName(
//            pluginName,
//            "com.example.msi.platformforup.WelcomeActivity"
//        )
//        RePlugin.startActivity(this@MainActivity, intent)

        Log.d(TAG, "startActivityForPlugin: 打开plugin_app")
        if (RePlugin.isPluginInstalled("android_webView")) {
            val intent = Intent()
            //插件app的别名
            val className = "com.example.msi.platformforup.WelcomeActivity"
            intent.component = ComponentName(pluginName, className)
            RePlugin.startActivity(this, intent)
            finish()
        } else {
            Toast.makeText(this, "暂未安装", Toast.LENGTH_LONG).show()
        }
    }


    fun unInstall(view: View) {
        val result = RePlugin.uninstall("android_webView")
        Log.i("TAG", "uninstall result : $result")
    }


    private fun forwardToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, FORWARD_TO_SETTINGS)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FORWARD_TO_SETTINGS -> {
                requestPermission(RECHECK_PERMISSION)
            }
        }
    }

    fun autoComplete(view: View) {
        autoBoot()
    }

    private fun autoBoot() {
        isAutoComplete = true
        requestPermission(DOWNLOAD_DIALOG)
    }


}