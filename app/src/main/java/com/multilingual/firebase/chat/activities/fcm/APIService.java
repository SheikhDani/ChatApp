package com.multilingual.firebase.chat.activities.fcm;

import com.multilingual.firebase.chat.activities.fcmmodels.MyResponse;
import com.multilingual.firebase.chat.activities.fcmmodels.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization: key=AAAAifN1C-g:APA91bE9BcnezbvEjJfesvSRundskjsPe6lMls3AS1lmHfARUnB9JvqZ0F886x6tBbXqEefDSzGPua3Wr7XFjN4YLnqKzgvc4y9oasjx2ZHPz3pR3kPHZSbktJCRSwbToZ316YT-TApN"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
