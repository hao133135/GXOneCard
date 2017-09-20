package com.qindao.coalfield;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qindao.http.AsyncHttpClient;
import com.qindao.http.AsyncHttpResponseHandler;
import com.qindao.http.RequestParams;
import com.qindao.model.TruckInformation;
import com.qindao.utils.clickUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by admin on 2017/8/21.
 */

public class acceptance extends AppCompatActivity {

    private Button close, save, photograph, btn1, btn2, btn3, btn4;
    private boolean isSelected1 = false, isSelected2 = false, isSelected3 = false, isSelected4 = false,saveok=false;
    private final int OPEN_RESULT = 1; //用来打开相机
    private TextView car, cyAear;
    private EditText danwei;
    private List<String> choiceList = new ArrayList<String>();
    private List<String> realityAearList = new ArrayList<String>();
    private List<String> errorunitList = new ArrayList<String>();
    private ImageView mImageView1 = null, mImageView2 = null, mImageView3 = null, mImageView4 = null;
    private ArrayAdapter<String> adapter;
    private Spinner choiceSpinner, realityAearSpinner, errorunitSpinner;
    private final TruckInformation truck = new TruckInformation();
    private String strImgPath;
    private String fileName;
    private RadioGroup radioGroupID;
    private RadioButton rbtn1, rbtn2;

    private JSONArray jsonReal;
    private String jsoncoal;
    private String username,path;
    private String coalbytruckid;
    private Camera mCamera01;
    private final int IMAGE_MAX_WIDTH = 320;
    private final int IMAGE_MAX_HEIGHT = 480;
    private File imageFile = null;
    private Bitmap photo;
    private String vehicleno;
    private String coalfieldid;
    private Context mContext = this;
    private int pickCount=0;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
        //获取Bundle中的数据，注意类型和key*
        vehicleno = bundle.getString("vehicleno");
        coalfieldid = bundle.getString("coalfieldid");
        username = bundle.getString("username");
        coalbytruckid = bundle.getString("coalbytruckid");
        path = bundle.getString("path");
        car = (TextView) findViewById(R.id.car);
        car.setText(vehicleno);
        cyAear = (TextView) findViewById(R.id.coalYardAear);
        cyAear.setText(coalfieldid);
        choiceSpinner = (Spinner) findViewById(R.id.choice);
        realityAearSpinner = (Spinner) findViewById(R.id.realityAear);
        errorunitSpinner = (Spinner) findViewById(R.id.errorunit);
        danwei = (EditText) findViewById(R.id.danwei);
        radioGroupID = (RadioGroup) findViewById(R.id.radioGroupID);
        rbtn1 = (RadioButton) findViewById(R.id.femaleGroupID);
        rbtn2 = (RadioButton) findViewById(R.id.maleGroupID);
        save = (Button) findViewById(R.id.save);
        close = (Button) findViewById(R.id.close);
        photograph = (Button) findViewById(R.id.photograph);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        mImageView1 = (ImageView) findViewById(R.id.imageview1);
        mImageView2 = (ImageView) findViewById(R.id.imageview2);
        mImageView3 = (ImageView) findViewById(R.id.imageview3);
        mImageView4 = (ImageView) findViewById(R.id.imageview4);
        rbtn1.setChecked(true);
        radioGroupID.setOnCheckedChangeListener(new radioListener());
        save.setOnClickListener(new saveListener());
        close.setOnClickListener(new closeListener());
        btn1.setOnClickListener(new btn1Listener());
        btn2.setOnClickListener(new btn2Listener());
        btn3.setOnClickListener(new btn3Listener());
        btn4.setOnClickListener(new btn4Listener());
        photograph.setOnClickListener(new photographListener());
        init();
        save.setOnClickListener(new saveListener());

    }

    private void init() {
        truck.setState(rbtn1.getText().toString());
        choiceClick();
        errorunit();
        new realTask().execute();
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

    private class saveListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            new saveTask().execute();
            if (clickUtils.isFastClick()) {
                // 进行点击事件后的逻辑操作
                Intent i = new Intent(acceptance.this, from.class);
                i.putExtra("user1", username);
                i.putExtra("path", path);
                startActivity(i);
                finish();
            }
        }
    }
    private class closeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class photographListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickUtils.isFastClick()) {
                // 进行点击事件后的逻辑操作

                if (isSelected1 == false && isSelected2 == false && isSelected3 == false && isSelected4 == false) {
                    isSelected1 = true;
                }
                if (isSelected1 || isSelected2 || isSelected3 || isSelected4) {
                    File file=new File("/sdcard/Image/image.jpg");
                    if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                    Uri imageUri = Uri.fromFile(file);
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                    i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                    startActivityForResult(i, OPEN_RESULT);
                 /*   if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(acceptance.this, Manifest.permission.CAMERA);
                        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(acceptance.this,new String[]{Manifest.permission.CAMERA},222);
                            return;
                        }else{

                            File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                            if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                            Uri imageUri = Uri.fromFile(file);
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                             //添加这一句表示对目标应用临时授权该Uri所代表的文件
                            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                            startActivityForResult(i, OPEN_RESULT);
                        }
                    } else {

                        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                        Uri imageUri = Uri.fromFile(file);
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                        //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                        startActivityForResult(i, OPEN_RESULT);
                    }*/

                }
            }
        }
    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case 222:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                    if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                    Uri imageUri = FileProvider.getUriForFile(context, "com.qindao.coalfield.acceptance.fileprovider", file);//通过FileProvider创建一个content类型的Uri
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                    startActivityForResult(i, OPEN_RESULT);
                } else {
                    // Permission Denied
                    Toast.makeText(acceptance.this, "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_RESULT:
                Bitmap newBitmap = null;
                if (resultCode == RESULT_OK) {
                    switch (requestCode) {
                        case OPEN_RESULT:
                            //将保存在本地的图片取出并缩小后显示在界面上
                            Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/Image/image.jpg");
                            // 获得图片的宽高
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            // 设置想要的大小
                            int newWidth = 640;
                            int newHeight = 960;
                            // 计算缩放比例
                            float scaleWidth = ((float) newWidth) / width;
                            float scaleHeight = ((float) newHeight) / height;
                            // 取得想要缩放的matrix参数
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth, scaleHeight);
                            // 得到新的图片
                            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                                    true);
                            //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                            bitmap.recycle();
                            fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                                    .format(new Date()) + ".jpg";
                            FileOutputStream b = null;
                            File file = new File("/sdcard/Image/");
                            file.mkdirs();// 创建文件夹
                            strImgPath = "/sdcard/Image/"+fileName;

                            try {
                                b = new FileOutputStream(strImgPath);
                                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    b.flush();
                                    b.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }
                }
                if (resultCode == RESULT_OK && requestCode == OPEN_RESULT) {
                    imageFile = new File(strImgPath);
                    if (isSelected1) {
                        File file1 = new File(strImgPath);
                        truck.setImagefile1(file1);
                        truck.setCheckphoto1(fileName);
                        mImageView1.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    if (isSelected2) {
                        File file2 = new File(strImgPath);
                        truck.setImagefile2(file2);
                        truck.setCheckphoto2(fileName);
                        mImageView2.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    if (isSelected3) {
                        File file3 = new File(strImgPath);
                        truck.setImagefile3(file3);
                        truck.setCheckphoto3(fileName);
                        mImageView3.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    if (isSelected4) {
                        File file4 = new File(strImgPath);
                        truck.setImagefile4(file4);
                        truck.setCheckphoto4(fileName);
                        mImageView4.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    //按指定options显示图片防止OOM
                }else {
                    Toast.makeText(acceptance.this,"保存错误！", Toast.LENGTH_LONG).show();
                }
        }
    }
    private void realityAearClick() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, realityAearList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        realityAearSpinner.setAdapter(adapter);
        int k= adapter.getCount();
        for(int i=0;i<k;i++){
            if(coalfieldid.equals(adapter.getItem(i).toString())){
                realityAearSpinner.setSelection(i,true);// 默认选中项
                break;
            }
        }

    }
    public class realTask extends AsyncTask<Object, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(Object... strings) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(path+"/App/GetCoalFieldZone");
            // String key = "[{"COALFIELDZONEID":2.0,"COALFIELDID":2.0,"FIELDNAME":"B","ZONENAME":"北区"},{"COALFIELDZONEID":1.0,"COALFIELDID":2.0,"FIELDNAME":"B","ZONENAME":"南区"},{"COALFIELDZONEID":4.0,"COALFIELDID":1.0,"FIELDNAME":"A","ZONENAME":"北区"},{"COALFIELDZONEID":3.0,"COALFIELDID":1.0,"FIELDNAME":"A","ZONENAME":"南区"},{"COALFIELDZONEID":5.0,"COALFIELDID":3.0,"FIELDNAME":"C","ZONENAME":"北区"},{"COALFIELDZONEID":7.0,"COALFIELDID":3.0,"FIELDNAME":"C","ZONENAME":"南区"}]";
            JSONArray jsonArray = null;
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String key = EntityUtils.toString(httpResponse.getEntity());
                jsonArray = new JSONArray(key);
            }catch (Exception e){
                e.printStackTrace();
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray s) {
            super.onPostExecute(s);
            jsonReal = s;
            try{
                for (int i = 0; i < s.length(); i++) {
                    JSONObject jsonObject = (JSONObject) s.get(i);
                    String fieldname = (String) jsonObject.get("FIELDNAME");
                    String zonename = (String) jsonObject.get("ZONENAME");
                    realityAearList.add(fieldname + "-" + zonename);
                }
            } catch (JSONException e) {
                    e.printStackTrace();
            }
            realityAearClick();
        }
    }

    private void errorunit() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                errorunitList());
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        errorunitSpinner.setAdapter(adapter);
    }

    public List<String> errorunitList() {
        errorunitList.add("有");
        errorunitList.add("无");
        return errorunitList;
    }

    private void choiceClick() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                choiceList());
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        choiceSpinner.setAdapter(adapter);

    }

    public List<String> choiceList() {
        choiceList.add("扣矸");
        choiceList.add("扣水");
        choiceList.add("扣杂");
        return choiceList;
    }

    public void setBackground() {
        if (isSelected1) {
            btn1.setBackgroundResource(R.drawable.backall);
        } else {
            btn1.setBackgroundResource(R.drawable.boder);
        }
        if (isSelected2) {
            btn2.setBackgroundResource(R.drawable.backall);
        } else {
            btn2.setBackgroundResource(R.drawable.boder);
        }
        if (isSelected3) {
            btn3.setBackgroundResource(R.drawable.backall);
        } else {
            btn3.setBackgroundResource(R.drawable.boder);
        }
        if (isSelected4) {
            btn4.setBackgroundResource(R.drawable.backall);
        } else {
            btn4.setBackgroundResource(R.drawable.boder);
        }

    }

    private class btn1Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = true;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = false;
            mImageView1.setVisibility(ViewGroup.VISIBLE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
        }
    }

    private class btn2Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = false;
            isSelected2 = true;
            isSelected3 = false;
            isSelected4 = false;
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.VISIBLE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
        }
    }

    private class btn3Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = true;
            isSelected4 = false;
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.VISIBLE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
        }
    }

    private class btn4Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = true;
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.VISIBLE);
            setBackground();
        }
    }

    private class radioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

            if (rbtn1.getId() == i) {
                truck.setState(rbtn1.getText().toString());
            } else if (rbtn2.getId() == i) {
                truck.setState(rbtn2.getText().toString());
            }

        }
    }

    public void uploadFile(String url,String... parmas) throws Exception {
        String path =parmas[0];
        String path2 =parmas[1];
        String path3 = parmas[2];
        String path4 = parmas[3];
        File file = new File(path);
        File file2 = new File(path2);
        File file3 = new File(path3);
        File file4 = new File(path4);
        Looper.prepare();
        int count = 0;
        if(file.length()>0)count++;
        if(file2.length()>0)count++;
        if(file3.length()>0)count++;
        if(file4.length()>0)count++;
        if (file.exists() && file.length() > 0) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("file1", file);
            if (file2.exists() && file2.length() > 0) params.put("file2", file2);
            if (file3.exists() && file3.length() > 0) params.put("file3", file3);
            if (file4.exists() && file4.length() > 0) params.put("file4", file4);
            // 上传文件
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBody) {
                    // 上传成功后要做的工作
                    System.out.print(responseBody);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    // 上传失败后要做到工作
                    System.out.print(responseBody);
                }


                @Override
                public void onRetry(int retryNo) {
                    // TODO Auto-generated method stub
                    super.onRetry(retryNo);
                    // 返回重试次数
                }

            });
        } else {
            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
        }
    }

    public class saveTask extends AsyncTask<Object, Object, Integer> {

        @Override
        protected Integer doInBackground(Object... strings) {
            int count=0;
            if(pickCount>0){
                savepick();
                count++;
            }
            coalbutruck();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(path+"/App/Update");
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");

            JSONObject param = newJson();
            StringEntity se = null;
            String key = null;

            try {
                se = new StringEntity(param.toString());
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                key = EntityUtils.toString(httpResponse.getEntity());
                if (key.equals("1")) {
                    count+=2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            if(s==3){
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_LONG).show();
            }else if(s==2){
                Toast.makeText(mContext, "保存成功,未拍照", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mContext, "保存失败", Toast.LENGTH_LONG).show();
            }


        }
    }
    public void savepick(){
        String[] params = {String.valueOf(truck.getImagefile1()),String.valueOf(truck.getImagefile2()),String.valueOf(truck.getImagefile3()),String.valueOf(truck.getImagefile4())};
        try {
            uploadFile(path+"/APP/UploadImg",params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void coalbutruck(){
        truck.setVehicleno(car.getText().toString());
        truck.setCoalfieldid(cyAear.getText().toString());
        truck.setRealcoalfieldid(realityAearSpinner.getSelectedItem().toString());
        truck.setDiscount(choiceSpinner.getSelectedItem().toString());
        truck.setRemark(errorunitSpinner.getSelectedItem().toString());
        truck.setDeductweight(danwei.getText().toString());
        String s = realityAearSpinner.getSelectedItem().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < jsonReal.length(); i++) {
                jsonObject = (JSONObject) jsonReal.get(i);
                String fieldname = (String) jsonObject.get("FIELDNAME");
                String zonename = (String) jsonObject.get("ZONENAME");
                if ((fieldname + "-" + zonename).equals(realityAearSpinner.getSelectedItem().toString())) {
                    String coalfieldid = jsonObject.get("COALFIELDID").toString();
                    truck.setCoalmineid(coalfieldid);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public JSONObject newJson(){
        JSONObject param = new JSONObject();
        try{
            if (truck.getState().equals("通过验收")) {
                param.put("state", 5);
            } else if (truck.getState().equals("退车")) {
                param.put("state", 7);
            }
            param.put("coalbytruckid", coalbytruckid);
            param.put("checkuser", username);
            param.put("vehicleno", truck.getVehicleno());
            param.put("realcoalfieldid", truck.getCoalmineid());
            if (truck.getRemark().equals("有")) {
                param.put("illegaid", 1);
            } else if (truck.getRemark().equals("无")) {
                param.put("illegaid", 0);
            }
            if (truck.getDiscount().equals("扣水")) {
                param.put("waterdiscount", truck.getDeductweight());
            } else if (truck.getDiscount().equals("扣矸")) {
                param.put("wasterockdiscount", truck.getDeductweight());
            } else if (truck.getDiscount().equals("扣杂")) {
                param.put("impuritydiscount", truck.getDeductweight());
            }

            if (truck.getCheckphoto1() != null) {
                param.put("checkphoto1", truck.getCheckphoto1());
            }
            if (truck.getCheckphoto2() != null) {
                param.put("checkphoto2", truck.getCheckphoto2());
            }
            if (truck.getCheckphoto3() != null) {
                param.put("checkphoto3", truck.getCheckphoto3());
            }
            if (truck.getCheckphoto4() != null) {
                param.put("checkphoto4", truck.getCheckphoto4());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }
}

