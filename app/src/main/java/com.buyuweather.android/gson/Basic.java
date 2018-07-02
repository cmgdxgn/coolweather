package com.buyuweather.android.gson;

import com.google.gson.annotations.SerializedName;

//GSON中默认类成员变量名字与json数据中的名称一致，但是也可以使用SerializedName建立别名映射,json格式如下
/*
{"HeWeather":
[
	{
		"basic":
		{
			"cid":"CN101190401",
			"location":"苏州",
			"parent_city":"苏州",
			"admin_area":"江苏",
			"cnty":"中国",
			"lat":"31.29937935",
			"lon":"120.61958313",
			"tz":"+8.00",
			"city":"苏州",
			"id":"CN101190401",
			"update":
			{
				"loc":"2018-06-30 10:49",
				"utc":"2018-06-30 02:49"
			}
		},

		"update":
		{
			"loc":"2018-06-30 10:49",
			"utc":"2018-06-30 02:49"
		},

		"status":"ok",

		"now":
		{
			"cloud":"44",
			"cond_code":"305",
			"cond_txt":"小雨",
			"fl":"31",
			"hum":"76",
			"pcpn":"0.0",
			"pres":"1003",
			"tmp":"28",
			"vis":"17",
			"wind_deg":"113",
			"wind_dir":"东南风",
			"wind_sc":"2",
			"wind_spd":"7",

			"cond":
			{
				"code":"305",
				"txt":"小雨"
			}
		},

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

		"aqi":
		{
			"city":
			{
				"aqi":"37",
				"pm25":"13",
				"qlty":"优"
			}
		},

		"suggestion":
		{
			"comf":
			{
				"type":"comf",
				"brf":"较舒适",
				"txt":"白天有雨，从而使空气湿度加大，会使人们感觉有点儿闷热，但早晚的天气很凉爽、舒适。"
			},

			"sport":
			{
				"type":"sport",
				"brf":"较不宜",
				"txt":"有较强降水，建议您选择在室内进行健身休闲运动。"
			},

			"cw":
			{
				"type":"cw",
				"brf":"不宜",
				"txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"
			}
		}
	}
]
}
*/
public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String UpdateTime;
    }
}
