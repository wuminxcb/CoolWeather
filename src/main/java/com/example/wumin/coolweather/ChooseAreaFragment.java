package com.example.wumin.coolweather;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wumin.coolweather.db.City;
import com.example.wumin.coolweather.db.County;
import com.example.wumin.coolweather.db.Province;
import com.example.wumin.coolweather.util.HttpUtil;
import com.example.wumin.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;


    private ProgressDialog progressDialog;  //定义进度窗口
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;   //适配器
    private List<String> dataList = new ArrayList<>();  //List列表数据
    //定义具体的省市县的list数据
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    //确定当前的level
    private int currentLevel;

    @Nullable
    @Override
    //创建视图并定义布局视图绑定id与适配器设置---listview
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //定义视图并绑定布局
        View view =  inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    //创建活动界面，实现列表和按钮点击事件
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince =provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity =cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces(); //初始联动在省界面
    }
    //查询省信息，优先查询数据库，没有查询结果之后到服务器上获取
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();   //模块dataList刷新
            for(Province province : provinceList){
                 dataList.add(province.getProvinceName()); //获取省级列表名称
            }
            adapter.notifyDataSetChanged();   //通知适配器数据变化，刷新
            listView.setSelection(0);        //listview的定位到第一个位置
            currentLevel= LEVEL_PROVINCE;

        }else{
            String address ="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceId = ?",String
                .valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city :cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel =LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china"+"/"+ provinceCode;
            queryFromServer(address,"city");
        }
    }
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String
                .valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel =LEVEL_COUNTY;
        }else {
            int provinceCode =selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+ cityCode;
            queryFromServer(address,"county");
        }
    }

    //服务器上查询数据
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        //使用网络请求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       closeProgressDialog();
                       Toast.makeText(getContext(),"加载数据失败",Toast.LENGTH_SHORT).show();
                   }
               });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               String responseTest = response.body().string();
                boolean result =false;   //对应Utility的方法调用
                //根据类型不同判断，调用函数进行数据解析JSON
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(responseTest);
                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(responseTest,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(responseTest,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
