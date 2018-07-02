package com.buyuweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*天气预报的数据是数组，我们只需要定义出单日天气的实现类，然后在声明实体类引用的时候使用集合类型来进行声明
"daily_forecast":
		[
			{
				"date":"2018-06-30",
				"cond":
				{
					"txt_d":"大雨"
				},
				"tmp":
				{
					"max":"29",
					"min":"25"
				}
			},

			{
				"date":"2018-07-01",
				"cond":
				{
					"txt_d":"雷阵雨"
				},
				"tmp":
				{
					"max":"30",
					"min":"26"
				}
			},

			{
				"date":"2018-07-02",
				"cond":
				{
					"txt_d":"阴"
				},
				"tmp":
				{
					"max":"31",
					"min":"27"
				}
			}
		],
*
* */
public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
