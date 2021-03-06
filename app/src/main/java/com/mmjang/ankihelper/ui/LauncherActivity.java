package com.mmjang.ankihelper.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.dict.CustomDictionary;
import com.mmjang.ankihelper.data.dict.Definition;
import com.mmjang.ankihelper.data.dict.YoudaoOnline;
import com.mmjang.ankihelper.data.plan.DefaultPlan;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.domain.CBWatcherService;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.about.AboutActivity;
import com.mmjang.ankihelper.ui.customdict.CustomDictionaryActivity;
import com.mmjang.ankihelper.ui.plan.PlansManagerActivity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    AnkiDroidHelper mAnkiDroid;
    Settings settings;
    //views
    Switch switchMoniteClipboard;
    Switch switchCancelAfterAdd;
    Switch switchLeftHandMode;
    TextView textViewOpenPlanManager;
    TextView textViewCustomDictionary;
    TextView textViewAbout;
    TextView textViewHelp;
    TextView textViewAddDefaultPlan;
    TextView textViewAddQQGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initAnkiApi();
        settings = Settings.getInstance(this);

        switchMoniteClipboard = (Switch) findViewById(R.id.switch_monite_clipboard);
        switchCancelAfterAdd = (Switch) findViewById(R.id.switch_cancel_after_add);
        switchLeftHandMode = (Switch) findViewById(R.id.left_hand_mode);
        textViewOpenPlanManager = (TextView) findViewById(R.id.btn_open_plan_manager);
        textViewCustomDictionary = (TextView) findViewById(R.id.btn_open_custom_dictionary);
        textViewAbout = (TextView) findViewById(R.id.btn_about_and_support);
        textViewHelp = (TextView) findViewById(R.id.btn_help);
        textViewAddDefaultPlan = (TextView) findViewById(R.id.btn_add_default_plan);
        textViewAddQQGroup = (TextView) findViewById(R.id.btn_qq_group);

        textViewAbout.setText(Html.fromHtml("<font color='red'>❤</font>" + getResources().getString(R.string.btn_about_and_support_str)));
        switchMoniteClipboard.setChecked(
                settings.getMoniteClipboardQ()
        );

        switchCancelAfterAdd.setChecked(
                settings.getAutoCancelPopupQ()
        );

        switchMoniteClipboard.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setMoniteClipboardQ(isChecked);
                        if (isChecked) {
                            startCBService();
                        } else {
                            stopCBService();
                        }
                    }
                }
        );

        switchLeftHandMode.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setLeftHandModeQ(isChecked);
                    }
                }
        );

        switchCancelAfterAdd.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settings.setAutoCancelPopupQ(isChecked);
                    }
                }
        );

        textViewOpenPlanManager.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAnkiDroid == null) {
                            mAnkiDroid = new AnkiDroidHelper(LauncherActivity.this);
                        }
                        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
                            Toast.makeText(LauncherActivity.this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mAnkiDroid.shouldRequestPermission()) {
                            mAnkiDroid.requestPermission(LauncherActivity.this, 0);
                            return;
                        }
                        else{

                        }
                        Intent intent = new Intent(LauncherActivity.this, PlansManagerActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewCustomDictionary.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LauncherActivity.this, CustomDictionaryActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewAbout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LauncherActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
                }
        );

        textViewHelp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://github.com/mmjang/ankihelper/blob/master/README.md";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
        );

        textViewAddDefaultPlan.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAnkiDroid == null) {
                            mAnkiDroid = new AnkiDroidHelper(LauncherActivity.this);
                        }
                        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
                            Toast.makeText(LauncherActivity.this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (mAnkiDroid.shouldRequestPermission()) {
                            mAnkiDroid.requestPermission(LauncherActivity.this, 0);
                            return;
                        }
                        else{

                        }
                        askIfAddDefaultPlan();
                    }
                }
        );

        textViewAddQQGroup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        joinQQGroup("-1JxtFYckXpYUMpZKRbrMWuceCgM23R7");
                    }
                }
        );
        if (settings.getMoniteClipboardQ()) {
            startCBService();
        }

        //debug new feature
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    YoudaoOnline.getDefinition("dedicate");
//                }
//                catch (IOException e){
//
//                }
//            }
//        });
//        thread.start();
    }

    private void initAnkiApi() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, 0);
        }
        else{

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_about_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            askIfAddDefaultPlan();
        } else {
            //Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(LauncherActivity.this)
                    .setMessage(R.string.permission_denied)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            openSettingsPage();
                        }
                    }).show();
        }
    }

    private void startCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        startService(intent);
    }

    private void stopCBService() {
        Intent intent = new Intent(this, CBWatcherService.class);
        stopService(intent);
    }
    
    void askIfAddDefaultPlan(){
        List<OutputPlan> plans = DataSupport.findAll(OutputPlan.class);
        for(OutputPlan plan : plans){
            if(plan.getPlanName().equals(DefaultPlan.DEFAULT_PLAN_NAME)){
                new AlertDialog.Builder(LauncherActivity.this)
                        .setMessage(R.string.duplicate_plan_name_complain)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                return;
                            }
                        }).show();
                return ;
            }
        }
        if(plans.size() == 0) {
            new AlertDialog.Builder(LauncherActivity.this)
                    .setTitle(R.string.confirm_add_default_plan)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DefaultPlan plan = new DefaultPlan(LauncherActivity.this);
                            plan.addDefaultPlan();
                            Toast.makeText(LauncherActivity.this, R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }

        else{
            new AlertDialog.Builder(LauncherActivity.this)
                    .setMessage(R.string.confirm_add_default_plan_when_exists_already)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DefaultPlan plan = new DefaultPlan(LauncherActivity.this);
                            plan.addDefaultPlan();
                            Toast.makeText(LauncherActivity.this, R.string.default_plan_added, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void openSettingsPage(){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /****************
     *
     * 发起添加群流程。群号：安卓划词助手用户群(871406754) 的 key 为： -1JxtFYckXpYUMpZKRbrMWuceCgM23R7
     * 调用 joinQQGroup(-1JxtFYckXpYUMpZKRbrMWuceCgM23R7) 即可发起手Q客户端申请加群 安卓划词助手用户群(871406754)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}
