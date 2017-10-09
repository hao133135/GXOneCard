package com.qindao.coalfield;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qindao.model.UpdataInfo;
import com.qindao.utils.DownLoadManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText path;
    private Button button,but1,but2;

    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private Button getVersion;
    private UpdataInfo info;
    private String localVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        username = (EditText) findViewById(R.id.activity_login_user);
        password = (EditText) findViewById(R.id.activity_login_password);
        button = (Button) findViewById(R.id.activity_login_btn);
        but1 = (Button) findViewById(R.id.login_but1);
        but2 = (Button) findViewById(R.id.login_but2);
        path = (EditText) findViewById(R.id.loginurl);
        getVersion = (Button) findViewById(R.id.btn_getVersion);
        SharedPreferences sharedPreferences=getSharedPreferences("config",0);
        String name=sharedPreferences.getString("name","");
        String pwd=sharedPreferences.getString("password","");
        String url1 = sharedPreferences.getString("url","http://192.168.0.200:8090");
        username.setText(name);
        password.setText(pwd);
        path.setText(url1);
        but1.setOnClickListener(new but1Listener());
        but2.setOnClickListener(new but2Listener());
        getVersion.setOnClickListener(new versionListener());
        button.setOnClickListener(new buttonListener());
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, from.class);
                startActivity(i);
            }
        });*/

    }

    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
           new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    String user = username.getText().toString();
                    String pwd = password.getText().toString();
                    String url1 = path.getText().toString();
                    //http://39.108.73.207
                    SharedPreferences sp=getSharedPreferences("config",0);
                    SharedPreferences.Editor editor=sp.edit();
                    //把数据进行保存
                    editor.putString("name",user);
                    editor.putString("password",pwd);
                    editor.putString("url",url1);
                    //提交数据
                    editor.commit();
                    if(user.isEmpty()||"".equals(user)||pwd.isEmpty()||"".equals(pwd)){
                        Toast.makeText(LoginActivity.this,"请输入用户名和密码！",Toast.LENGTH_SHORT).show();
                    }else {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(url1 + "/APP/Validate");
                        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                        try {
                            JSONObject param = new JSONObject();
                            param.put("LoginName", user);
                            param.put("LoginPwd", pwd);
                            StringEntity se = new StringEntity(param.toString());
                            se.setContentType("application/json;charset=utf-8");
                            httpPost.setEntity(se);
                            // 设置连接超时、读取超时
                            httpClient.getParams().setIntParameter(
                                    HttpConnectionParams.SO_TIMEOUT, 5000); // 超时设置
                            httpClient.getParams().setIntParameter(
                                    HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时
                            HttpResponse httpResponse = httpClient.execute(httpPost);
                            String key = EntityUtils.toString(httpResponse.getEntity());
                            String stats = key;
                            //JSONObject j = new JSONObject(key);
                            //String stats = (String) j.get("stats");
                            //String stats ="1";
                            if (stats.equals("1")) {
                                Intent i = new Intent(LoginActivity.this, from.class);
                                i.putExtra("user1", user);
                                i.putExtra("path", url1);
                                startActivity(i);
                            } else if(stats.equals("0")){
                                Toast.makeText(LoginActivity.this, "用户名密码错误！", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this, "参数错误！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "服务器链接失败！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } finally {
                            httpClient.getConnectionManager().shutdown();
                        }
                    }
                    Looper.loop();
                }
            }).start();
            /*if (clickUtils.isFastClick()) {
                if (username.getText().toString().isEmpty() || "".equals(username.getText().toString()) || password.getText().toString().isEmpty() || "".equals( password.getText().toString())) {
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else {
                    // 进行点击事件后的逻辑操作
                    String url1 = path.getText().toString();
                    String name = username.getText().toString();
                    String pwd = password.getText().toString();
                   new MyTask().execute(url1 + "/APP/Validate", name, pwd);
                }
            }*/
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private class but1Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            View layout = findViewById(R.id.loginlayout);
            layout.setVisibility(ViewGroup.VISIBLE);
        }
    }

    private class but2Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            View layout = findViewById(R.id.loginlayout);
            layout.setVisibility(ViewGroup.GONE);
        }
    }

    public class MyTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(strings[0]);
                JSONObject param = new JSONObject();
                param.put("LoginName",strings[1]);
                param.put("LoginPwd",strings[2]);
                StringEntity se = new StringEntity(param.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);

                // 设置连接超时、读取超时
                httpClient.getParams().setIntParameter(
                        HttpConnectionParams.SO_TIMEOUT, 5000); // 超时设置
                httpClient.getParams().setIntParameter(
                        HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时
                HttpResponse httpResponse = httpClient.execute(httpPost);

                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    result =  EntityUtils.toString(httpResponse.getEntity());
                }else{
                    return "";
                }
            } catch (IOException e) {
                result = "";
                e.printStackTrace();
            } catch (JSONException e) {
                result="Jsonerror";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String user = username.getText().toString();
            String pwd = password.getText().toString();
            String url1 = path.getText().toString();
            SharedPreferences sp = getSharedPreferences("config", 0);
            SharedPreferences.Editor editor = sp.edit();
            //把数据进行保存
            editor.putString("name", user);
            editor.putString("password", pwd);
            editor.putString("url", url1);
            //提交数据
            editor.commit();

            if(s.equals("Jsonerror")){
                Toast.makeText(LoginActivity.this,"数据错误！",Toast.LENGTH_SHORT).show();
            }else if(s.equals("0")){
                Toast.makeText(LoginActivity.this, "用户名密码错误！", Toast.LENGTH_SHORT).show();
            }else if(s.equals("1")){
                Intent i = new Intent(LoginActivity.this, from.class);
                i.putExtra("user1", user);
                i.putExtra("path", url1);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(LoginActivity.this,s+"服务器链接失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();

        }

        return false;

    }
    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    System.exit(0);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    public static String getHttpByApache(String... strings) throws JSONException, IOException {
        HttpPost httpPost = new HttpPost(strings[0]);
        JSONObject param = new JSONObject();
        param.put("LOGINNAME",strings[1]);
        param.put("LOGINPWD",strings[2]);
        HttpClient httpClient = new DefaultHttpClient();
        // 设置连接超时、读取超时
        httpClient.getParams().setIntParameter(
                HttpConnectionParams.SO_TIMEOUT, 5000); // 超时设置
        httpClient.getParams().setIntParameter(
                HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时
        StringEntity se = new StringEntity(param.toString());
        httpPost.setEntity(se);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(httpResponse.getEntity());
        }
        return null;
    }

    private class versionListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                localVersion = getVersionName();
                CheckVersionTask cv = new CheckVersionTask();
                new Thread(cv).start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private String getVersionName() throws Exception {
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
                0);
        return packInfo.versionName;
    }
    public class CheckVersionTask implements Runnable {
        InputStream is;
        public void run() {
            try {
                String path = getResources().getString(R.string.url_server);
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // 从服务器获得一个输入流
                    is = conn.getInputStream();
                }
                info = UpdataInfoParser.getUpdataInfo(is);
                if (info.getVersion().equals(localVersion)) {
                    Log.i(TAG, "版本号相同");
                    Message msg = new Message();
                    msg.what = UPDATA_NONEED;
                    handler.sendMessage(msg);
                    // LoginMain();
                } else {
                    Log.i(TAG, "版本号不相同 ");
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_NONEED:
                    Toast.makeText(getApplicationContext(), "不需要更新",
                            Toast.LENGTH_SHORT).show();
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败",Toast.LENGTH_LONG).show();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    /*
     *
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new Builder(this);
        builer.setTitle("版本升级");
        builer.setMessage(info.getDescription());
        //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                downLoadApk();
            }
        });
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //do sth
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }}.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}