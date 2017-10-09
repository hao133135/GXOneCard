package com.qindao.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.qindao.coalfield.R;
import com.qindao.model.BuckleTons;

import java.util.List;

/**
 * Created by admin on 2017/9/29.
 */

public class choiceadapter<T> extends BaseAdapter {
    private List<BuckleTons> buckleTonsList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;
    private int selectedItem = -1;



    /**
     *
     * @param context mainActivity
     * @param buckleTonsList   显示的数据
     * @param resource  一个Item的布局
     */
    public choiceadapter(int resource, Context context, List<BuckleTons> buckleTonsList) {
        this.resource = resource;
        this.context = context;
        this.buckleTonsList = buckleTonsList;
    }
    /*
     * 获得数据总数
     * */
    @Override
    public int getCount() {
        return buckleTonsList.size();
    }
    /*
     * 根据索引为position的数据
      * */
    @Override
    public Object getItem(int position) {
        return buckleTonsList.get(position);
    }
    /*
     * 根据索引值获得Item的Id
     * */
    @Override
    public long getItemId(int position) {
        return position;
    }
    /*
     *通过索引值position将数据映射到视图
     *convertView具有缓存功能，在第一页时为null，在第二第三....页时不为null
     * */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(resource, null);
            viewHolder = new ViewHolder();
            //扣吨项
            viewHolder.nameTextView = view.findViewById(R.id.choice_project_name);
            viewHolder.reamber = view.findViewById(R.id.acceptance_project_water);
            view.setTag(viewHolder);
            viewHolder.initListener();
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.setData(buckleTonsList.get(position),position);
        return view;
    }


    public void setSelectedItem(int selectedItem)
    {
        this.selectedItem = selectedItem;
    }
    class ViewHolder{
        private TextView nameTextView;
        private EditText reamber;
        private BuckleTons buckleTons;
        int position;
        void initListener(){
            reamber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    buckleTonsList.get(position).setWEIGHT(charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
        void setData(final BuckleTons buckleTons, final int position){
            this.buckleTons = buckleTons;
            this.position = position;
            nameTextView.setText(buckleTons.getName());
            if(buckleTons.getWEIGHT() != null || !"".equals(buckleTons.getWEIGHT())){
                reamber.setText(buckleTons.getWEIGHT());
            }else{
                reamber.setText("");
            }
        }
    }
}

