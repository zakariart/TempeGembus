package com.tempegembus.zakaria.tempegembus.Fragments;

import com.tempegembus.zakaria.tempegembus.Notifications.MyResponse;
import com.tempegembus.zakaria.tempegembus.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAXvIV3ew:APA91bE2Du1-XiCHQ1EYXfvLA4Dx8D5raCye2Q3q43BoCVLhW8fKJ24cwno4N8KutS7mWIl3MdJbvPw6BpUis_xfHTVE47AklhTkN6NYKZzkl3q6-89TTGzZjcpCqEjAtSue3_GHbex3"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}