package com.buyuweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.buyuweather.android.MainActivity;
import com.buyuweather.android.R;
import com.buyuweather.android.WeatherActivity;
import com.buyuweather.android.db.City;
import com.buyuweather.android.db.County;
import com.buyuweather.android.db.Province;
import com.buyuweather.android.gson.Weather;
import com.buyuweather.android.util.HttpUtil;
import com.buyuweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//将要显示的不同内容界面放在不同的碎片中，在一个活动中加载两个碎片适配屏幕
public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButtoon;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();

    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//城市列表

    private Province selectedProvince;//选中的省份
    private City selectedCity;//选中的城市
    private int currentLevel;//选中的级别

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.choose_area,container,false);
        //设置choose_area根节点各属性有效，但是不处在任何容器中，因为后面要手动将view放入容器
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButtoon=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        //配置适配器的数据源及子布局
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //依次向下展开查询级别
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                } else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){//跳转到天气的详情界面
                    String weatherId=countyList.get(position).getWeatherId();//获取具体需要得到天气信息的县城的WeatherId
                    //如果是在启动页面进行选择，直接将参数传递给WeatherActivity
                    if (getActivity() instanceof MainActivity){//如果当前活动是MainActivity
                        Intent intent=new Intent(getActivity(), com.buyuweather.android.WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    //如果是在天气详情页面选择其他地方，则刷新weatherActivity信息，并关闭导航菜单
                    }else if (getActivity() instanceof com.buyuweather.android.WeatherActivity){
                        com.buyuweather.android.WeatherActivity activity=(WeatherActivity)getActivity();//得到当前活动的实例
                        activity.drawerLayout.closeDrawers();//关闭导航菜单
                        activity.swipeRefresh.setRefreshing(true);//开启刷新
                        activity.requestWeather(weatherId);//调用requestWeather方法刷新天气

                    }

                }
            }
        });
        //点击退出按钮依次向上展开查询级别
        backButtoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();//首先将省的数据显示出来
    }

    //查询全国所有的省，优先在数据库查询，如果没有的话再去服务器上查询
    private void queryProvinces(){
        titleText.setText("中国");
        backButtoon.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);//按照Peovince的格式获取所有数据填入列表
        if (provinceList.size()>0){
            //如果本地服务器有数据，则将省的名字放入适配器数据源列表，再通知适配器更新
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{ //如果本地没有则到服务器拉取
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询选中省内所有的市，优先查询数据库，没有则到服务器查询
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButtoon.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        //从city表中获取省id对应的所有city信息
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    //查询选中市内所有的县，优先查询数据库，没有则到服务器查询
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButtoon.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        //从county表中获取市id对应的所有county信息
        Log.d("Code", "countyList: "+countyList);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            Log.d("Code", "queryCounties: "+address);
            queryFromServer(address,"county");
        }
    }

    //根据传入的地址和类型从服务器上查询省市县数据
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败，请检查网络",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Log.d("Code", "onResponse: "+responseText);
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);//处理返回的json数据，信息存入数据库
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    //显示进度对话框
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
