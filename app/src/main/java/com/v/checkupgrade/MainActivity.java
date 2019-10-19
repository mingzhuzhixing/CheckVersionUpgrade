package com.v.checkupgrade;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.callback.OnCancelListener;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadFailedListener;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;

/**
 * V2版震撼来袭，功能强大，链式编程，调用简单，集成轻松，扩展性强大
 *
 * https://github.com/AlexLiuSheng/CheckVersionLib/blob/master/README_UN.MD
 */
public class MainActivity extends AppCompatActivity {

    private DownloadBuilder builder;
    private CheckBox forceUpdateCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forceUpdateCheckBox = findViewById(R.id.checkbox);
    }

    /**
     * 升级
     */
    public void upgrade(View view) {
        builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(crateUIData());

        if (forceUpdateCheckBox.isChecked()) {
            //设置此listener即代表需要强制更新，会在用户想要取消下载的时候回调 需要你自己关闭所有界面
            builder.setForceUpdateListener(new ForceUpdateListener() {
                @Override
                public void onShouldForceUpdate() {
                    forceUpdate();
                }
            });
        }

        //更新界面选择
        builder.setCustomVersionDialogListener(createCustomDialogTwo());

        //下载进度界面选择
        builder.setCustomDownloadingDialogListener(createCustomDownloadingDialog());

        //下载失败界面选择
        builder.setCustomDownloadFailedListener(createCustomDownloadFailedDialog());

        //自定义下载路径
        builder.setDownloadAPKPath(Environment.getExternalStorageDirectory() + "/download/upgradeApk/");

        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel() {
//                Toast.makeText(MainActivity.this, "Cancel Hanlde", Toast.LENGTH_SHORT).show();
            }
        });
        builder.executeMission(this);
    }

    /**
     * 强制更新操作
     * 通常关闭整个activity所有界面，这里方便测试直接关闭当前activity
     */
    private void forceUpdate() {
        Toast.makeText(this, "force update handle", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData() {
        UIData uiData = UIData.create();
        uiData.setTitle(getString(R.string.update_title));
//        uiData.setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk");
        uiData.setDownloadUrl(" http://dlied5.myapp.com/myapp/1105999355/muses/10030965_com.tencent.tako.muses_h101_1.0.101_319e47.apk ");
        uiData.setContent(getString(R.string.update_content));
        return uiData;
    }

    /**
     * 更新界面选择
     */
    private CustomVersionDialogListener createCustomDialogTwo() {
        return new CustomVersionDialogListener() {
            @Override
            public Dialog getCustomVersionDialog(Context context, UIData versionBundle) {
                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_dialog_two_layout);
                TextView textView = baseDialog.findViewById(R.id.tv_msg);
                ImageView cancel = baseDialog.findViewById(R.id.versionchecklib_version_dialog_cancel);
                if(forceUpdateCheckBox.isChecked()){
                    cancel.setVisibility(View.GONE);
                }else{
                    cancel.setVisibility(View.VISIBLE);
                }
                textView.setText(versionBundle.getContent());
//                baseDialog.setCanceledOnTouchOutside(true);
                return baseDialog;
            }
        };
    }

    /**
     * 自定义下载中对话框，下载中会连续回调此方法 updateUI
     * 务必用库传回来的context 实例化你的dialog
     */
    private CustomDownloadingDialogListener createCustomDownloadingDialog() {
        return new CustomDownloadingDialogListener() {
            @Override
            public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_layout);
                ImageView cancel = baseDialog.findViewById(R.id.versionchecklib_loading_dialog_cancel);
                if(forceUpdateCheckBox.isChecked()){
                    cancel.setVisibility(View.GONE);
                }else{
                    cancel.setVisibility(View.VISIBLE);
                }
                return baseDialog;
            }

            @Override
            public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
                TextView tvProgress = dialog.findViewById(R.id.tv_progress);
                ProgressBar progressBar = dialog.findViewById(R.id.pb);
                progressBar.setProgress(progress);
                tvProgress.setText(getString(R.string.versionchecklib_progress, progress));
            }
        };
    }

    /**
     * 务必用库传回来的context 实例化你的dialog
     */
    private CustomDownloadFailedListener createCustomDownloadFailedDialog() {
        return new CustomDownloadFailedListener() {
            @Override
            public Dialog getCustomDownloadFailed(Context context, UIData versionBundle) {
                BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_failed_dialog);
                ImageView cancel = baseDialog.findViewById(R.id.versionchecklib_failed_dialog_cancel);
                if(forceUpdateCheckBox.isChecked()){
                    cancel.setVisibility(View.GONE);
                }else{
                    cancel.setVisibility(View.VISIBLE);
                }
                return baseDialog;
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //合适的地方关闭
        AllenVersionChecker.getInstance().cancelAllMission();
    }
}
