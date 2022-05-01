package cu.inmobile.wara.Networking;

import cu.inmobile.wara.Pojo.CityApi;
import cu.inmobile.wara.Pojo.GetBulletsApi;
import cu.inmobile.wara.Pojo.GetFilterApi;
import cu.inmobile.wara.Pojo.GetProfileInfoApi;
import cu.inmobile.wara.Pojo.LoginApi;
import cu.inmobile.wara.Pojo.MessageUserApi;
import cu.inmobile.wara.Pojo.ProfileApi;
import cu.inmobile.wara.Pojo.ReceivedMessageApi;
import cu.inmobile.wara.Pojo.RegisterApi;
import cu.inmobile.wara.Pojo.SaveFilterApi;
import cu.inmobile.wara.Pojo.SendLikeStatusApi;
import cu.inmobile.wara.Pojo.SendMessageApi;
import cu.inmobile.wara.Pojo.SetEncountersSeenApi;
import cu.inmobile.wara.Pojo.SetMatchesSeenApi;
import cu.inmobile.wara.Pojo.UnrecievedMessagesApi;
import cu.inmobile.wara.Pojo.UpdateAppValuesApi;
import cu.inmobile.wara.Pojo.UploadProfileApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("login")
    Call<LoginApi> login(@Query("username") String username,
                         @Query("password") String password);

    @GET("register")
    Call<RegisterApi> register(@Query("name") String name,
                               @Query("username") String username,
                               @Query("city") String city,
                               @Query("gender") String gender,
                               @Query("lat") String lat,
                               @Query("lng") String lng,
                               @Query("password") String password,
                               @Query("password_confirmation") String password_confirmation,
                               @Query("dob") String dob);

    @GET("city")
    Call<CityApi> doGetCities();

    @GET("messenger/search")
    Call<MessageUserApi> getMessageHistory(@Query("id") String userId,
                                           @Query("access_token") String accessToken,
                                           @Query("from_date") String date);

    @GET("messenger/sendMessage")
    Call<SendMessageApi> sendMessage (@Query("id") String id,
                                      @Query("access_token") String accessToken,
                                      @Query("token") String mesToken,
                                      @Query("dest_id") String toId,
                                      @Query("body") String body,
                                      @Query("time") String time);

    @GET("update_app_values")
    Call<UpdateAppValuesApi> getupdateAppValues(@Query("user_id") String values,
                                                @Query("access_token") String accessToken,
                                                @Query("os") String os,
                                                @Query("app_version") String appVersion);

    @GET("profile/me")
    Call<ProfileApi> getProfile (@Query("user_id") String values,
                                 @Query("access_token") String accessToken);

    @GET("profile")
    Call<GetProfileInfoApi> getProfileInfo (@Query("user_id") String values,
                                            @Query("access_token") String accessToken,
                                            @Query("view_user_id") String target_id);
    @GET("encounter/user/like")
    Call<SendLikeStatusApi> sendLikeStatus (@Query("user_id") String values,
                                            @Query("access_token") String accessToken,
                                            @Query("encounter_id") String target_id,
                                            @Query("like") String like);

    @GET("get_my_bullets")
    Call<GetBulletsApi> getBullets (@Query("user_id") String values,
                                    @Query("access_token") String accessToken);


    @GET("profile/me/update-basic-info")
    Call<UploadProfileApi> uploadProfile     (@Query("user_id") String values,
                                              @Query("access_token") String accessToken,
                                              @Query("name") String name,
                                              @Query("about_me") String about_me,
                                              @Query("profile_picture_url") String profile_picture_url,
                                              @Query("city") String city);

    @GET("messenger/setRecievedMessage")
    Call<ReceivedMessageApi> setReceivedMessage (@Query("id") String id,
                                                 @Query("access_token") String accessToken,
                                                 @Query("message_id") String message);

    @GET("messenger/getUnrecievedMessage")
    Call<UnrecievedMessagesApi> getUnrecievedMessage (@Query("id") String id,
                                                      @Query("access_token") String accessToken);

   /* @GET("get_my_bullets")
    Call<GetMyBulletApi> getMyBullet (@Query("user_id") String values,
                                      @Query("access_token") String accessToken);*/
    @GET("people-nearby/filter-settings")
    Call<GetFilterApi> getFilter (@Query("user_id") String values,
                                  @Query("access_token") String accessToken);

    @GET("people-nearby/filter/save")
    Call<SaveFilterApi> saveFilter (@Query("user_id") String values,
                                   @Query("access_token") String accessToken,
                                   @Query("prefered_genders") String prefered_genders,
                                   @Query("prefered_ages") String prefered_ages,
                                   @Query("prefered_location") String prefered_location);

    @GET("encounter/setencountersseen")
    Call<SetEncountersSeenApi> setEncountersSeen (@Query("user_id") String values,
                                                  @Query("access_token") String accessToken);

    @GET("encounter/setmatchesseen")
    Call<SetMatchesSeenApi> setMatchesSeen (@Query("user_id") String values,
                                            @Query("access_token") String accessToken);

    /*@GET("/api/unknown")
    Call<MultipleResource> doGetListResources();

    @POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
}