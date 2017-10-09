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
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qindao.http.AsyncHttpClient;
import com.qindao.http.AsyncHttpResponseHandler;
import com.qindao.http.RequestParams;
import com.qindao.model.BuckleTons;
import com.qindao.model.CoalByTruck;
import com.qindao.model.Coalbytruckbean;
import com.qindao.utils.choiceadapter;
import com.qindao.utils.clickUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by admin on 2017/8/21.
 */

public class acceptance extends AppCompatActivity {

    private Button back, acceptance,btn0, btn1, btn2, btn3, btn4,choiceOk;
    private boolean isSelected0=true,isSelected1 = false, isSelected2 = false, isSelected3 = false, isSelected4 = false,saveok=false;
    private final int OPEN_RESULT = 1; //用来打开相机
    private TextView car, cyAear,sampling,caloricpower,sulphur,choicenumbername,choicecount;
    private EditText allRemark,calorificvalue;
    private View choiceList,choicenumber;
    private ListView choiceList1,choiceList2;
    private List<String> realityAearList =new ArrayList<String>();
    private List<BuckleTons> buckleTonsList = new ArrayList<BuckleTons>();
    private List<BuckleTons> buckleTonsList11 = new ArrayList<BuckleTons>();
    private List<BuckleTons> buckleTonsList1 = new ArrayList<BuckleTons>();
    private List<BuckleTons> buckleTonsList12 = new ArrayList<BuckleTons>();
    private ImageView mImageView1 = null, mImageView2 = null, mImageView3 = null, mImageView4 = null;
    private ArrayAdapter<String> adapter;
    private Spinner choiceSpinner, realityAearSpinner, errorunitSpinner;
    private final CoalByTruck truck = new CoalByTruck();
    private String strImgPath;
    private String fileName;
    private RadioGroup radioGroupID;
    private RadioButton rbtn1, rbtn2;

    private JSONArray jsonReal;
    private String jsoncoal;
    private String username,path,coallist;
    private String coalbytruckid;
    private Camera mCamera01;
    private final int IMAGE_MAX_WIDTH = 320;
    private final int IMAGE_MAX_HEIGHT = 480;
    private File imageFile = null;
    private Bitmap photo;
    private Context mContext = this;
    private int pickCount=0;
    private Coalbytruckbean cya;
    private BuckleTons buckleTons;
    private Context context;
    private JSONArray choiceJson;
    private Double count=0.0;
    private choiceadapter<BuckleTons> choiceadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
        //获取Bundle中的数据，注意类型和key*
        cya = (Coalbytruckbean) getIntent().getSerializableExtra(from.SER_KEY);
        coallist =  bundle.getString("coallist");
        username = bundle.getString("username");
        path = bundle.getString("path");
        cyAear = (TextView) findViewById(R.id.coalYardAear);
        cyAear.setText(cya.getCoalfieldname());
        sampling = (TextView) findViewById(R.id.acceptance_car_sampling_number);
        sampling.setText(cya.getSamplecodemasterid());
        caloricpower = (TextView) findViewById(R.id.acceptance_coal_caloricpower);
        caloricpower.setText(cya.getQnetar());
        sulphur = (TextView) findViewById(R.id.acceptance_coal_sulphur);
        sulphur.setText(cya.getR());
        choicenumbername = (TextView) findViewById(R.id.acceptance_toolbar_car_number);
        choicenumbername.setText(cya.getVehicleno());
        choiceList = findViewById(R.id.choice_project_include);
        choiceList1 = choiceList.findViewById(R.id.choice_project_itme_listview1);
        choiceList2 = choiceList.findViewById(R.id.choice_project_itme_listview2);
        choiceOk = choiceList.findViewById(R.id.choice_project_ok);
        allRemark = (EditText) findViewById(R.id.choice_project_all_remark);
        realityAearSpinner = (Spinner) findViewById(R.id.acceptance_car_realityAear);
        acceptance = (Button) findViewById(R.id.acceptance_ok);
        back = (Button) findViewById(R.id.acceptance_back);
        btn0 = (Button) findViewById(R.id.btn_choice_project);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        choiceList = findViewById(R.id.choice_project_include);
        mImageView1 = (ImageView) findViewById(R.id.imageview1);
        mImageView2 = (ImageView) findViewById(R.id.imageview2);
        mImageView3 = (ImageView) findViewById(R.id.imageview3);
        mImageView4 = (ImageView) findViewById(R.id.imageview4);
        choicecount = (TextView) findViewById(R.id.acceptance_car_choice);
        choicecount.setText("0");
        calorificvalue = (EditText) findViewById(R.id.acceptance_visual_calorific_value);
        acceptance.setOnClickListener(new acceptanceListener());
        back.setOnClickListener(new backListener());
        btn0.setOnClickListener(new btn0Listener());
        btn1.setOnClickListener(new btn1Listener());
        btn2.setOnClickListener(new btn2Listener());
        btn3.setOnClickListener(new btn3Listener());
        btn4.setOnClickListener(new btn4Listener());
        choiceOk.setOnClickListener(new choiceListener());

        init();

    }

    private void init() {
        realityAearClick();
        new choiceListTask().execute();
        new choiceListTask1().execute();
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

    private class acceptanceListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickUtils.isFastClick()) {
                // 进行点击事件后的逻辑操作
                new choiceListener();
                new  saveTask().execute();
                Intent i = new Intent(acceptance.this, from.class);
                i.putExtra("user1", username);
                i.putExtra("path", path);
                startActivity(i);
                finish();
            }
        }
    }
    private class backListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(acceptance.this, from.class);
            i.putExtra("user1", username);
            i.putExtra("path", path);
            startActivity(i);
            finish();
        }
    }



    private void realityAearClick() {
        JSONArray coal = null;
        JSONObject coalJson;
        String coalname;
        try {
            coal = new JSONArray(coallist);
            for (int i = 0; i < coal.length(); i++) {
                coalJson = coal.getJSONObject(i);
                coalname = String.valueOf(coalJson.get("FIELDNAME"));
                realityAearList.add(coalname);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, realityAearList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        realityAearSpinner.setAdapter(adapter);
        int k= adapter.getCount();
        for(int i=0;i<k;i++){
            if(cya.getCoalfieldname().equals(adapter.getItem(i).toString())){
                realityAearSpinner.setSelection(i,true);// 默认选中项
                break;
            }
        }

    }

    public void setBackground() {
        if (isSelected0) {
            btn0.setBackgroundResource(R.drawable.backall);
        } else {
            btn0.setBackgroundResource(R.drawable.boder);
        }
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
    private class btn0Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected0= true;
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = false;
            choiceList.setVisibility(ViewGroup.VISIBLE);
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
        }
    }

    private class btn1Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected0 = false;
            isSelected1 = true;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = false;
            choiceList.setVisibility(ViewGroup.GONE);
            mImageView1.setVisibility(ViewGroup.VISIBLE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
            mImageView1.setOnClickListener(new imageListener());
        }
    }

    private class btn2Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected0 = false;
            isSelected1 = false;
            isSelected2 = true;
            isSelected3 = false;
            isSelected4 = false;
            choiceList.setVisibility(ViewGroup.GONE);
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.VISIBLE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
            mImageView2.setOnClickListener(new imageListener());

        }
    }

    private class btn3Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected0 = false;
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = true;
            isSelected4 = false;
            choiceList.setVisibility(ViewGroup.GONE);
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.VISIBLE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
            mImageView3.setOnClickListener(new imageListener());

        }
    }

    private class btn4Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected0 = false;
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = true;
            choiceList.setVisibility(ViewGroup.GONE);
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.VISIBLE);
            setBackground();
            mImageView4.setOnClickListener(new imageListener());
        }
    }

    private class photographListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickUtils.isFastClick()) {
                // 进行点击事件后的逻辑操作
                if (isSelected1 == false && isSelected2 == false && isSelected3 == false && isSelected4 == false) {
                    new choiceListTask().execute();
                }
                if (isSelected1 || isSelected2 || isSelected3 || isSelected4) {
                    File file=new File("/sdcard/Image/image.jpg");
                    if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                    Uri imageUri = Uri.fromFile(file);
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                    i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                    startActivityForResult(i, OPEN_RESULT);
                }
            }
        }
    }

    private class imageListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickUtils.isFastClick()) {
                if (isSelected1&&truck.getIMAGEFILE1()==null || isSelected2&&truck.getIMAGEFILE2()==null || isSelected3&&truck.getIMAGEFILE3()==null || isSelected4&&truck.getIMAGEFILE4()==null) {
                    File file=new File("/sdcard/Image/image.jpg");
                    if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                    Uri imageUri = Uri.fromFile(file);
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                    i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                    startActivityForResult(i, OPEN_RESULT);
                }
            }
        }
    }
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
                        truck.setIMAGEFILE1(file1);
                        truck.setCHECKPHOTO1(fileName);
                        mImageView1.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    if (isSelected2) {
                        File file2 = new File(strImgPath);
                        truck.setIMAGEFILE1(file2);
                        truck.setCHECKPHOTO1(fileName);
                        mImageView2.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    if (isSelected3) {
                        File file3 = new File(strImgPath);
                        truck.setIMAGEFILE1(file3);
                        truck.setCHECKPHOTO1(fileName);
                        mImageView3.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    if (isSelected4) {
                        File file4 = new File(strImgPath);
                        truck.setIMAGEFILE1(file4);
                        truck.setCHECKPHOTO1(fileName);
                        mImageView4.setImageBitmap(newBitmap);
                        pickCount++;
                    }
                    //按指定options显示图片防止OOM
                }else {
                    Toast.makeText(acceptance.this,"保存错误！", Toast.LENGTH_LONG).show();
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
                }
                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    // 上传失败后要做到工作
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
            String param = coalbytruck();
            HttpClient httpClient = new DefaultHttpClient();
            // CoalByTruck{COALBYTRUCKID='C03F4B7B-773D-430F-A13B-48836F73E9DD', RECLCOALFIELDID='A', AD='10.1', REMARK='null', CHECKPHOTO1='1231.jpg', CHECKPHOTO2='1232.jpg', CHECKPHOTO3='1233.jpg', CHECKPHOTO4='1234.jpg', BuckleTons=[BuckleTons{ID='1', COALBYTRUCKID='C03F4B7B-773D-430F-A13B-48836F73E9DD', WEIGHT='1.1', REMARK='11111'}]}
            //String paeam = "{COALBYTRUCKID:'7A947819-D4A4-4AFE-B067-51AC05EF6748', REALCOALFIELDID:'1', AD:10.1, REMARK:'null', CHECKPHOTO1:'1231.jpg', CHECKPHOTO2:'1232.jpg', CHECKPHOTO3:'1233.jpg', CHECKPHOTO4:'1234.jpg','BuckleTons':[{'coalbytruckid':'502DA1EA-C8E9-4046-B2E4-B74CDB86B847','ItemId':'1F14230E-5ED1-4AC2-B244-8EBF4C602B1B','Weight':20,'Remark':null}]}";
            try {
                HttpPost httpPost = new HttpPost(path+"/App/Update");
                httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                StringEntity se = new StringEntity(param);
                se.setContentType("application/json;charset=utf-8");
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String key = EntityUtils.toString(httpResponse.getEntity());
                JSONObject j = new JSONObject(key);
            }catch (Exception e){
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
        String[] params = {String.valueOf(truck.getIMAGEFILE1()),String.valueOf(truck.getIMAGEFILE2()),String.valueOf(truck.getIMAGEFILE3()),String.valueOf(truck.getIMAGEFILE4())};
        try {
            uploadFile(path+"/APP/UploadImg",params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public String  coalbytruck(){
       String REALCOALFIELDID = null;
       String BuckleTonslist= null ;
       try {
           JSONArray jsonArray = new JSONArray(coallist);
           for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject = (JSONObject) jsonArray.get(i);
               if(jsonObject.get( "FIELDNAME").equals(realityAearSpinner.getSelectedItem().toString())){
                   REALCOALFIELDID = String.valueOf(jsonObject.get("COALFIELDID"));
               }
           }
       } catch (JSONException e) {
           e.printStackTrace();
       }
       for (int i = 0; i < buckleTonsList.size(); i++) {
           BuckleTons bt = buckleTonsList.get(i);
           if(i==0){
               BuckleTonslist = "{'coalbytruckid':'"+cya.getCoalbytruckid()+"','ItemId':'"+bt.getID()+"','Weight':"+bt.getWEIGHT()+",'Remark':'"+bt.getREMARK()+"'}";
           }else {
               BuckleTonslist+=",{'coalbytruckid':'"+cya.getCoalbytruckid()+"','ItemId':'"+bt.getID()+"','Weight':"+bt.getWEIGHT()+",'Remark':"+bt.getREMARK()+"}";
           }
       }
       for (int i = 0; i < buckleTonsList11.size(); i++) {
           BuckleTons bt = buckleTonsList11.get(i);
           BuckleTonslist+=",{'coalbytruckid':'"+cya.getCoalbytruckid()+"','ItemId':'"+bt.getID()+"','Weight':"+bt.getWEIGHT()+",'Remark':"+bt.getREMARK()+"}";
       }

     String coalbytruck =   "{COALBYTRUCKID:'"+cya.getCoalbytruckid()+"', REALCOALFIELDID:'"+REALCOALFIELDID+"', AD:"+calorificvalue.getText().toString().trim()+", REMARK:'"+allRemark.getText().toString().trim()+"', CHECKPHOTO1:'"+truck.getCHECKPHOTO1()+"', CHECKPHOTO2:'"+truck.getCHECKPHOTO2()+"', CHECKPHOTO3:'"+truck.getCHECKPHOTO3()+"', CHECKPHOTO4:'"+truck.getCHECKPHOTO4()+"','BuckleTons':["+BuckleTonslist+"]}";
    return coalbytruck;
   }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(acceptance.this, from.class);
            i.putExtra("user1", username);
            i.putExtra("path", path);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    class choiceListTask extends AsyncTask<Object, Void, List<BuckleTons>>{
        @Override
        protected List<BuckleTons> doInBackground(Object... objects) {
            if (buckleTonsList.size() == 0) {
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    JSONObject param = new JSONObject();
                    HttpPost httpPost = new HttpPost(path+"/App/GetItems");
                    httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                    StringEntity se = new StringEntity(param.toString());
                    se.setContentType("application/json;charset=utf-8");
                    httpPost.setEntity(se);
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    String key = EntityUtils.toString(httpResponse.getEntity());
                    JSONArray jsonArray = new JSONArray(key);
                    choiceJson = jsonArray;
                    for (int i = 0; i < jsonArray.length(); i+=2) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        buckleTons = new BuckleTons();
                        buckleTons.setID(jsonObject.getString("Id"));
                        buckleTons.setName(jsonObject.getString("Name"));
                        buckleTonsList.add(buckleTons);
                    }
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(acceptance.this, "获取扣吨项信息错误！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
            return buckleTonsList;
        }

        @Override
        protected void onPostExecute(List<BuckleTons> buckleTonses) {
            super.onPostExecute(buckleTonses);
            if(buckleTonses!=null){
                choiceViewClick();
            }else {
                return;
            }
        }
    }

    private void choiceViewClick() {
        choiceadapter = new choiceadapter<BuckleTons>(R.layout.item,this,buckleTonsList);
        choiceList1.setAdapter(choiceadapter);
    }

    class choiceListTask1 extends AsyncTask<Object, Void, List<BuckleTons>>{
        @Override
        protected List<BuckleTons> doInBackground(Object... objects) {
            if (buckleTonsList11.size() == 0) {
                try {
                    JSONArray jsonArray =choiceJson;
                    for (int i = 1; i < jsonArray.length(); i+=2) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        buckleTons = new BuckleTons();
                        buckleTons.setID(jsonObject.getString("Id"));
                        buckleTons.setName(jsonObject.getString("Name"));
                        buckleTonsList11.add(buckleTons);
                    }
                } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(acceptance.this, "获取扣吨项信息错误！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
            return buckleTonsList11;
        }

        @Override
        protected void onPostExecute(List<BuckleTons> buckleTonses) {
            super.onPostExecute(buckleTonses);
            if(buckleTonses!=null){
                choiceViewClick1();
            }else {
                return;
            }
        }
    }

    private void choiceViewClick1() {
        choiceadapter = new choiceadapter<BuckleTons>(R.layout.item,this,buckleTonsList11);
        choiceList2.setAdapter(choiceadapter);
    }

    private class choiceListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            count=0.0;
            buckleTonsList = new ArrayList<BuckleTons>();
            buckleTonsList11 = new ArrayList<BuckleTons>();
            int j= 0;
            int k= 1;
            for (int i = 0; i < choiceList1.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout)choiceList1.getChildAt(i);// 获得子item的layout
                EditText e = layout.findViewById(R.id.acceptance_project_water);
                String aa = e.getText().toString().trim();
                JSONObject jsonObject = null;
                try {
                    jsonObject = choiceJson.getJSONObject(j);
                    buckleTons = new BuckleTons();
                    buckleTons.setID(jsonObject.getString("Id"));
                    buckleTons.setName(jsonObject.getString("Name"));
                    j+=2;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                if(aa.isEmpty()){
                    buckleTons.setWEIGHT("0");
                    count+=0;
                }else{
                    buckleTons.setWEIGHT(doubleToString(Double.parseDouble(aa)));
                    count+=Double.valueOf(aa);
                }
                buckleTonsList.add(buckleTons);
            }
            for (int i = 0; i < choiceList2.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout)choiceList2.getChildAt(i);// 获得子item的layout
                EditText e = layout.findViewById(R.id.acceptance_project_water);
                String aa = e.getText().toString().trim();
                JSONObject jsonObject = null;
                try {
                    jsonObject = choiceJson.getJSONObject(k);
                    buckleTons = new BuckleTons();
                    buckleTons.setID(jsonObject.getString("Id"));
                    buckleTons.setName(jsonObject.getString("Name"));
                    k+=2;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                if(aa.isEmpty()){
                    buckleTons.setWEIGHT("0");
                    count+=0;
                }else{
                    buckleTons.setWEIGHT(doubleToString(Double.parseDouble(aa)));
                    count+=Double.valueOf(aa);
                }
                buckleTonsList11.add(buckleTons);
            }
            mTimeHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }
    Handler mTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            choicecount.setText(doubleToString(count));
            sendEmptyMessageDelayed(0, 1000);
        }
    };

    /**
     * double转String,保留小数点后两位
     * @param num
     * @return
     */
    public static String doubleToString(double num){
        //使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.00").format(num);
    }
}

