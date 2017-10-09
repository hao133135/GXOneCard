package com.qindao.coalfield;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qindao.model.Coalbytruckbean;
import com.qindao.model.Coalfieldzonebean;
import com.qindao.utils.clickUtils;
import com.qindao.utils.myadapter;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/21.
 */

public class from extends AppCompatActivity {

    public  final static String SER_KEY = "com.qindao.model.Coalbytruckbean";
    private List<String > slist = new ArrayList<>();
    private List<String > sslist = new ArrayList<>();
    private List<Coalbytruckbean> llist = new ArrayList<Coalbytruckbean>();
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    private Button buttonCheck;
    private Button buttonClose;
    private ListView mylistview;
    private TextView myTextView;
    private Map map = new HashMap();
    private JSONArray jsonreal,jsonzone;
    private String username,path;
    private List<String> myslist;
    private List<Coalbytruckbean> mylistView;
    private String fieldname;
    private boolean spinnerboolean=false,spinnerboolean1=false;
    private String coalfieldlist,coalzone;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
        /*获取Bundle中的数据，注意类型和key*/
        username = bundle.getString("user1");
        path = bundle.getString("path");
        mySpinner = (Spinner) findViewById(R.id.spinner);
        buttonCheck = (Button) findViewById(R.id.Check);
        buttonClose = (Button) findViewById(R.id.close);
        mylistview = (ListView) findViewById(R.id.listview);
        buttonClose.setOnClickListener(new closeListener());
         buttonCheck.setOnClickListener(new clickListener());
       /* buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(from.this, acceptance.class);
                startActivity(i);
            }
        });*/

           new SpinnerTask().execute();
           mySpinnerListener();
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(from.this, "请选择验收车辆！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();

        }
    }

    private class closeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (clickUtils.isFastClick()) {
                // 进行点击事件后的逻辑操作
                jsonzone=null;
                Intent i = new Intent(from.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(from.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    class SpinnerTask extends AsyncTask<Object, Void, List<String>>
    {
        @Override
        protected List<String> doInBackground(Object... params) {
            HttpClient httpClient = new DefaultHttpClient();

            // String key = "{"FIELDNAME":"B","COALFIELDID":2.0},{"FIELDNAME":"A","COALFIELDID":1.0},{"FIELDNAME":"C","COALFIELDID":3.0}]";10.67.60.93:6678//App/GetCoalField
           // String key ="[{\"FIELDNAME\":\"B\",\"COALFIELDID\":\"2\"},{\"FIELDNAME\":\"A\",\"COALFIELDID\":\"1\"},{\"FIELDNAME\":\"C\",\"COALFIELDID\":\"3\"}]";
            try {
                HttpGet httpGet = new HttpGet(path+"/App/GetCoalField");
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String key = EntityUtils.toString(httpResponse.getEntity());
                JSONArray jsonArray = new JSONArray(key);
                jsonreal=jsonArray;
                slist.add("全部");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String name = (String) jsonObject.get("FIELDNAME");
                    String id = String.valueOf(jsonObject.get("COALFIELDID"));
                   // String iid = String.valueOf(id);
                    sslist.add(name);
                    sslist.add(id);
                }
                for (int i = 0; i < sslist.size(); ) {
                    String s1 = null;
                    String s2 = null;
                    for (int j = i; j < i + 2; j++) {
                        if (j % 2 == 0) {
                            s1 = sslist.get(j);
                            slist.add(s1);
                        } else {
                            s2 = sslist.get(j);
                        }

                    }
                    map.put(s1, s2);
                    i += 2;
                }
            }catch (Exception e){
                Looper.prepare();
                Toast.makeText(from.this, "获取菜单错误！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }
            map.put("未验收","101");
            map.put("已验收","101");
            map.put("全部","101");
            slist.add("未验收");
            slist.add("已验收");
            jsonzone = null;
            return slist;
        }
        @Override
        protected void onPostExecute(List<String> result) {
            // TODO Auto-generated method stub
            spinnerClick();
        }
    }
    private void spinnerClick() {
        //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, slist);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);

    }
    private void mySpinnerListener() {
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                /* 将所选mySpinner 的值带入myTextView 中*/
                fieldname = adapter.getItem(arg2).toString();
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
                cleanlist();
                if(jsonzone == null){
                    new listViewTask().execute();
                }else {
                    try {
                        if (fieldname != null && !"".equals(fieldname)) {
                            int number=1;
                            for (int i = 0; i < jsonzone.length(); i++) {
                                JSONObject jsonObject = jsonzone.getJSONObject(i);
                                Coalbytruckbean cya = new Coalbytruckbean();
                                cya.setCoalfieldid(String.valueOf(jsonObject.getInt("coalfieldid")));
                                String coalfieldname = null;
                                for (int j = 0; j < jsonreal.length(); j++) {
                                    JSONObject jsonObject1 = jsonreal.getJSONObject(j);
                                    if(jsonObject.get("coalfieldid").equals(jsonObject1.get("COALFIELDID"))){
                                        coalfieldname = jsonObject1.getString("FIELDNAME");
                                    }
                                }
                                cya.setCoalfieldname(coalfieldname);
                                cya.setSamplecodemasterid(jsonObject.get("samplecodemasterid").toString());
                                cya.setQnetar(String.valueOf(jsonObject.get("qnetar")));
                                cya.setR(String.valueOf(jsonObject.get("r")));
                                cya.setSampletype(jsonObject.get("sampletype").toString());
                                cya.setCoalbytruckid(jsonObject.get("COALBYTRUCKID").toString());
                                String s = (String) jsonObject.get("vehicleno");
                                cya.setVehicleno(jsonObject.get("vehicleno").toString());
                                String stats = (String) jsonObject.get("state");
                                if(stats.equals("4")){cya.setState("未验收");}else if(stats.equals("5")){cya.setState("已验收");}else {
                                    Looper.prepare();
                                    Toast.makeText(from.this, "获取车辆信息错误！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    break;
                                }
                                if (fieldname.equals("全部")) {
                                    cya.setNumber(number++);
                                    llist.add(cya);
                                } else if (fieldname.equals("已验收") && jsonObject.get("state").toString().equals("5")){
                                    cya.setNumber(number++);
                                    llist.add(cya);
                                } else if (fieldname.equals("未验收") && jsonObject.get("state").toString().equals("4")){
                                    cya.setNumber(number++);
                                    llist.add(cya);
                                }else if (map.get(fieldname).toString().equals( String.valueOf(jsonObject.get("coalfieldid")))&&jsonObject.get("state").toString().equals("4")){
                                    cya.setNumber(number++);
                                    llist.add(cya);
                                }
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    listViewClick();
                }

            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });
    }

    class listViewTask extends AsyncTask<Object, Void, List<Coalbytruckbean>>
    {
        @Override
        protected List<Coalbytruckbean> doInBackground(Object... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                JSONObject param = new JSONObject();
                if (fieldname != null&&!map.get(fieldname).toString().isEmpty()) {
                    param.put("coalId", map.get(fieldname));
                }else {
                    return null;
                }
                HttpPost httpPost = new HttpPost(path+"/App/GetCoalByTruck");
                httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                StringEntity se = new StringEntity(param.toString());
                se.setContentType("application/json;charset=utf-8");
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String key = EntityUtils.toString(httpResponse.getEntity());
                //String key = "[{\"COALBYTRUCKID\":213439.0,\"VEHICLENO\":\"陕C41908\",\"ZONENAME\":\"北区\"},{\"COALBYTRUCKID\":206363.0,\"VEHICLENO\":\"陕C51229\",\"ZONENAME\":\"北区\"}]";
                //String key ="[{\"COALBYTRUCKID\":\"502DA1EA-C8E9-4046-B2E4-B74CDB86B847\",\"vehicleno\":\"11\",\"zonename\":\"南区\",\"samplecodemasterid\":null,\"qnetar\":null,\"r\":null,\"sampletype\":null,\"state\":\"4\",\"grossweighttime\":null,\"REALCOALFIELDID\":1.0,\"coalfieldid\":1,\"coalfieldzoneid\":3},{\"COALBYTRUCKID\":\"7A947819-D4A4-4AFE-B067-51AC05EF6748\",\"vehicleno\":\"1111111111\",\"zonename\":\"南区\",\"samplecodemasterid\":null,\"qnetar\":null,\"r\":null,\"sampletype\":null,\"state\":\"4\",\"grossweighttime\":null,\"REALCOALFIELDID\":2.0,\"coalfieldid\":2,\"coalfieldzoneid\":1},{\"COALBYTRUCKID\":\"C03F4B7B-773D-430F-A13B-48836F73E9DD\",\"vehicleno\":\"1111111111\",\"zonename\":\"南区\",\"samplecodemasterid\":null,\"qnetar\":null,\"r\":null,\"sampletype\":null,\"state\":\"4\",\"grossweighttime\":null,\"REALCOALFIELDID\":null,\"coalfieldid\":2,\"coalfieldzoneid\":1},{\"COALBYTRUCKID\":\"C8FF5E4B-8ECA-4288-81A1-56D3D48C1992\",\"vehicleno\":\"11\",\"zonename\":\"北区\",\"samplecodemasterid\":null,\"qnetar\":null,\"r\":null,\"sampletype\":null,\"state\":\"4\",\"grossweighttime\":null,\"REALCOALFIELDID\":null,\"coalfieldid\":2,\"coalfieldzoneid\":2},{\"COALBYTRUCKID\":\"F699D432-7968-48F8-917D-B70AF9719A54\",\"vehicleno\":\"11\",\"zonename\":\"南区\",\"samplecodemasterid\":null,\"qnetar\":null,\"r\":null,\"sampletype\":null,\"state\":\"4\",\"grossweighttime\":null,\"REALCOALFIELDID\":null,\"coalfieldid\":1,\"coalfieldzoneid\":3}]";
               // COALBYTRUCKID:单号  vehicleno：车号    zonename：煤场分区名称     samplecodemasterid：样品编号     qnetar：预估热值     r：预估硫份      sampletype：采样方式         state：验收状态      grossweighttime：称重时间        REALCOALFIELDID：实际区域id      coalfieldid：煤场id    coalfieldzoneid：分区id
                JSONArray jsonArray = new JSONArray(key);
                jsonzone=jsonArray;
                int number=1;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Coalbytruckbean cya = new Coalbytruckbean();
                    cya.setNumber(number++);
                    cya.setCoalfieldid(String.valueOf(jsonObject.getInt("coalfieldid")));
                    String coalfieldname = null;
                    for (int j = 0; j < jsonreal.length(); j++) {
                        JSONObject jsonObject1 = jsonreal.getJSONObject(j);
                        if(jsonObject.get("coalfieldid").equals(jsonObject1.get("COALFIELDID"))){
                            coalfieldname = jsonObject1.getString("FIELDNAME");
                        }
                    }
                    cya.setCoalfieldname(coalfieldname);
                    cya.setSamplecodemasterid(jsonObject.get("samplecodemasterid").toString());
                    cya.setQnetar(String.valueOf(jsonObject.get("qnetar")));
                    cya.setR(String.valueOf(jsonObject.get("r")));
                    cya.setSampletype(jsonObject.get("sampletype").toString());
                    cya.setCoalbytruckid(jsonObject.get("COALBYTRUCKID").toString());
                    String s = (String) jsonObject.get("vehicleno");
                    cya.setVehicleno(jsonObject.get("vehicleno").toString());
                    String stats = (String) jsonObject.get("state");
                    if(stats.equals("4")){cya.setState("未验收");}else if(stats.equals("5")){cya.setState("已验收");}else {
                        Looper.prepare();
                        Toast.makeText(from.this, "获取车辆信息错误！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        break;
                    }
                    llist.add(cya);
                }

            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(from.this, "获取车辆信息错误！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }
            return llist;
        }
        @Override
        protected void onPostExecute(List<Coalbytruckbean> result) {
            // TODO Auto-generated method stub
            if(result!=null){
                listViewClick();
            }else {
                return;
            }
        }
    }

    private void listViewClick() {
        final myadapter<Coalfieldzonebean>  myArrayAdapter = new myadapter<Coalfieldzonebean>
                (this,llist,R.layout.items);
        View item = adapter.getView(0, null, mylistview);
        ViewGroup.LayoutParams params = item.getLayoutParams();
        params.height = 3;
        item.setLayoutParams(params);
        mylistview.setAdapter(myArrayAdapter);
        mylistview.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                if (clickUtils.isFastClick()) {
                    // 进行点击事件后的逻辑操作
                    myArrayAdapter.setSelectedItem(position);
                    myArrayAdapter.notifyDataSetChanged();
                    try {
                        final JSONObject jsonObject = (JSONObject) jsonzone.get(0);
                        for (int i = 0; i < parent.getCount(); i++) {
                            if (position == i) {
                                buttonCheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(from.this, acceptance.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(SER_KEY,llist.get(position));
                                        i.putExtra("username", username);
                                        i.putExtra("path", path);
                                        i.putExtra("coallist",jsonreal.toString());
                                        i.putExtras(bundle);
                                        jsonzone=null;
                                        startActivity(i);
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //清除处理
    private void cleanlist(){
        int size=llist.size();
        if(size>0){
            llist.removeAll(llist);
            adapter.notifyDataSetChanged();
        }
    }
}
