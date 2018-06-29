package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

//创建类用于匹配数据库的province表，存放省的信息
public class Province extends DataSupport { //LitePal中每一个实体类都必须继承自DataSupport类。
    private int id;//存放数据表的id
    private String provinceName;//省的名字
    private int provinceCode;//存放省的代码

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getProvinceName(){
        return provinceName;
    }

    public void setProvinceName(String provinceName){
        this.provinceName=provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
