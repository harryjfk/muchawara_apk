package cu.inmobile.wara.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import cu.inmobile.wara.Adapters.SpotLightAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Fragments.DisplayFragment;
import cu.inmobile.wara.Fragments.FragmentWara;
import cu.inmobile.wara.Fragments.LikedYouFragment;
import cu.inmobile.wara.Models.NotifyFilterChange;
import cu.inmobile.wara.Models.NotifyProfileRecieved;
import cu.inmobile.wara.Models.userDetail;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.LoginApi;
import cu.inmobile.wara.Pojo.Photo;
import cu.inmobile.wara.Pojo.ProfileApi;
import cu.inmobile.wara.Pojo.RegSuccessData;
import cu.inmobile.wara.Pojo.RegisterApi;
import cu.inmobile.wara.Pojo.SaveFilterApi;
import cu.inmobile.wara.Pojo.SetEncountersSeenApi;
import cu.inmobile.wara.Pojo.SetMatchesSeenApi;
import cu.inmobile.wara.Pojo.SuccessDataProfile;
import cu.inmobile.wara.Pojo.SuccessDataUpdateValues;
import cu.inmobile.wara.Pojo.UpdateAppValuesApi;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.MessageViewModel;
import cu.inmobile.wara.Services.PushNotificationService;
import cu.inmobile.wara.Utils.BusProvider;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Utils.SpotlightHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity
        implements View.OnClickListener {

    private WaraApp _app;

    private int SUPERPOWER_REQUEST = 101;
    private final int FILTER_CODE = 2745;
    private static final String TAG = "-- DisplayActivity";
    private String id, token;
    private TextView tvUserName, tvSuperPowerActive, tvCredit, tvToolbarTittle, tvWara, tvAdminMess;
    private TextView tvWaraCount, tvChatCount, tvLikedYouCount, tvFindingCount, tvUsersCount;
    private ImageView imgUserImage, imgUserSettings, imgPopularity_level, imgToolbarLogo;
    private RelativeLayout rlFinding, rlWara, rlChat, rlLikedYou;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private LinearLayout llFollowUs, llAdminMessage;
    private MessageViewModel messageViewModel;
    private ArrayList<String> myImages;
    //private BebasTextView tvToolbarTittle;

    private static DisplayActivity _Activity;

    /**
     * The singleton class for the default activity
     * @return the activity instance
     */
    public static DisplayActivity getInstance(){
        return _Activity;
    }

    /**
     * The button representing me on the navbar view in the spotlight members list
     */
    private Button Me;

    private boolean PlayRippleEffect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display);

        //Singleton instance
        _Activity = this;

        iniVariables();
        settingToolbar();
        settingInterface();
        getMyProfile();
        updateAppValues();
        updateAppValuesView();
        settingDrawer();
        gotoFragment();
//TODO se desactivo temporalmente el alerta de cambiar city en el profile
       // if (WaraApp.city_user.equals("") || WaraApp.city_user.equals(null))
       //     cityDialog();

        //Starting the push notification service.
        Intent notificationService = new Intent(DisplayActivity.this, PushNotificationService.class);
        startService(notificationService);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    private void iniVariables (){


        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        WaraApp.setMy_context(this);
        WaraApp.setCurrentActivity(this);
        /*id = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.USER_ID, "");
        token = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.SESSION_TOKEN, "");*/
        Log.d("USERID", WaraApp.id);
        Log.d("TOKEN", WaraApp.token);
        Log.d("TOKEN_user_chat", WaraApp.chatUser);
        Log.d(TAG,"getUnreadMessageByUser_count() - sucessData : "+ messageViewModel.getUnreadMessageByUser_count(WaraApp.chatUser+"@muchawara.com") );
        Log.d(TAG,"getAllMessage_count() - sucessData : "+ messageViewModel.getAllMessage_count());
        WaraApp.user_mess = messageViewModel.getUnreadMessageByUser_count(WaraApp.chatUser+"@muchawara.com");


    }

    private void settingInterface (){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        View HeaderView = navigationView.getHeaderView(0);
        imgUserImage = (ImageView) HeaderView.findViewById(R.id.img_user_image);
        tvUserName = (TextView) HeaderView.findViewById(R.id.tv_user_name);
        imgUserSettings = (ImageView) HeaderView.findViewById(R.id.img_user_settings);
        imgPopularity_level = (ImageView) HeaderView.findViewById(R.id.img_popularity_level);

        tvCredit = (TextView) HeaderView.findViewById(R.id.tv_credits);
        tvSuperPowerActive = (TextView) HeaderView.findViewById(R.id.super_active);
        tvWara = (TextView) findViewById(R.id.tv_wara);
        tvWaraCount = (TextView) findViewById(R.id.tv_wara_count);
        tvLikedYouCount= (TextView) findViewById(R.id.tv_liked_count);
        tvUsersCount= (TextView) findViewById(R.id.users_count);
        tvAdminMess= (TextView) findViewById(R.id.tv_admin_message);
        tvChatCount = (TextView) findViewById(R.id.tv_chat_count);
        rlFinding = (RelativeLayout) findViewById(R.id.rl_finding);
        rlWara = (RelativeLayout) findViewById(R.id.rl_wara);
        rlChat = (RelativeLayout) findViewById(R.id.rl_chat);
        rlLikedYou = (RelativeLayout) findViewById(R.id.rl_liked_you);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        llFollowUs = (LinearLayout) findViewById(R.id.ll_follow_us);
        llAdminMessage = (LinearLayout) findViewById(R.id.ll_admin_message);

        rlFinding.setOnClickListener(this);
        rlWara.setOnClickListener(this);
        rlChat.setOnClickListener(this);
        rlLikedYou.setOnClickListener(this);
        llFollowUs.setOnClickListener(this);

        tvUserName.setText(WaraApp.name);
        tvCredit.setText(""+ WaraApp.balas);
        tvWaraCount.setText(""+ WaraApp.matches);
        tvLikedYouCount.setText(""+WaraApp.likes);

        //TODO SetProfilePictureActivity --- ProfileActivity

        imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
                //WaraApp.showNotification();
                startActivity(new Intent(DisplayActivity.this, ProfileActivity.class));
            }
        });
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayActivity.this, ProfileActivity.class));
            }
        });
        imgUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayActivity.this, ProfileActivity.class));
            }
        });

        /*imgPopularity_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayActivity.this, PopularityActivity.class));
            }
        });*/



        Me = (Button) findViewById(R.id.ripple_effect);
        /**
         * Click handler for Me button in Nav view
         * Starts the api call to add user to the spotlight and handles the appropriate errors.
         */
        Me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotlightHelper.AddMeToSpotLight(_Activity);
            }
        });

        /**
         * Click handler for credits and super power in the nav bar
         */
        LinearLayout creditsLayout = (LinearLayout) HeaderView.findViewById(R.id.credit_header);
        LinearLayout superPowerLayout = (LinearLayout) HeaderView.findViewById(R.id.super_power_header);
        creditsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayActivity.this, RefillCreditsActivity.class));
            }
        });
        superPowerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DisplayActivity.this, SuperPowerActivity.class), SUPERPOWER_REQUEST);
            }
        });

    }

    public void updateBulletsView(){
        tvCredit.setText(""+WaraApp.balas);
    }
    private void drawerClick (View view){
        Log.d("DA/drawerClick" , ""+ view.getId() );
        Log.d("DA/drawerClick" , ""+ WaraApp.balas );


    }

    private void settingToolbar (){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbarTittle = (TextView) toolbar.findViewById(R.id.tv_title_toolbar);
        imgToolbarLogo = (ImageView) toolbar.findViewById(R.id.img_toolbar_logo);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        tvToolbarTittle.setText(R.string.text_finding);
        imgToolbarLogo.setImageResource(R.drawable.ic_hook);
    }

    private void setToolbarLogoTittle (){

        if (WaraApp.getCurrentFragment() instanceof FragmentWara ){
            tvToolbarTittle.setText(R.string.text_wara);
            imgToolbarLogo.setImageResource(R.drawable.wara);
        }if (WaraApp.getCurrentFragment() instanceof LikedYouFragment ){
            tvToolbarTittle.setText(R.string.text_liked_you);
            imgToolbarLogo.setImageResource(R.drawable.ic_wink);
        }if (WaraApp.getCurrentFragment() instanceof DisplayFragment ){
            tvToolbarTittle.setText(R.string.text_finding);
            imgToolbarLogo.setImageResource(R.drawable.ic_hook);
        }
    }

    private void settingDrawer (){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.d("DA/onDrawerClosed" , ""+ WaraApp.balas );
                //Since the drawer is closed so no need to play the ripple effect at all.
                PlayRippleEffect = false;
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d("DA/onDrawerOpened" , ""+ WaraApp.balas );
                // Do whatever you want here

                //As drawer is opened start the ripple effect on the Me button
                PlayRippleEffect = true;
                ForceRippleAnimation(Me);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }


    private void gotoFragment() {
        DisplayFragment fragment = new DisplayFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.display_container, fragment).commit();
    }


    private void getMyProfile() {

        if (! HelperMethods.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            updateAppValuesView();
            return;
        }
        Call<ProfileApi> call = WSWorker.apiInterface.getProfile( WaraApp.id , WaraApp.token);
        Log.d("DispAct/getMyProfile", "getMyProfile() -url: " + call.request().url());

        //HAGO LA PETICION AL SERVE CON RETROFIT
        call.enqueue(new Callback<ProfileApi>() {
            @Override
            public void onResponse(Call<ProfileApi> call, Response<ProfileApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        SuccessDataProfile successData = response.body().getSuccessDataProfile();
                        Log.d("PROFILE/getMyProfile", "getMyProfile() -successData: "  + response.toString());

                        WaraApp.name = successData.getUser().getName();
                        WaraApp.balas =  successData.getBalas();
                        WaraApp.popularity = successData.getUserPopularity().getPopularityType();
                        WaraApp.imageUrl = Endpoints.baseNoThumbUrl+successData.getUser().getProfilePicUrl();
                        WaraApp.saveSharedPreferences();
                        updateAppValues();




                    }


                }
                catch (Exception e){
                    Log.d("PROFILE/getMyProfile", "getMyProfile() .catch(): "  + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context() , e.getMessage() , Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ProfileApi> call, Throwable t) {
                Log.d("PROFILE/ProfileApi","getMyProfile().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_bad_answer) , Toast.LENGTH_SHORT).show();

            }



        });
       /* JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);

        Ion.with(this)
                .load(Endpoints.myProfileUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                     /*      if(HelperFunctions.IsUserAuthenticated(jsonObject, DisplayActivity.this)){
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");

                                Log.d("DispAct/getProfile" , successObj.toString(2));

                                JSONObject userObj = successObj.getJSONObject("user");
                                JSONArray photoObj = successObj.getJSONArray("photos");


                                WaraApp.name = userObj.optString("name");
                                tvUserName.setText(WaraApp.name);
                                WaraApp.balas = successObj.optInt("balas");
                                Log.d("DA/getMyProfile" , ""+ WaraApp.balas );
                                //tvCredit.setText("" + WaraApp.balas);

                                if (userObj.optString("superpower_activated").equals("true")) {
                                    tvSuperPowerActive.setText("Active");
                                    tvSuperPowerActive.setTextColor(getResources().getColor(R.color.white));
                                } else {
                                    tvSuperPowerActive.setText("Inactive");
                                    tvSuperPowerActive.setTextColor(getResources().getColor(R.color.colorAccent));
                                }
                                WaraApp.gender = userObj.getString("gender");
                                //JSONObject profileObj = successObj.getJSONObject("photo_url");
                                if(photoObj.length()>0)
                                {
                                    JSONObject photosUrlObj = (JSONObject) photoObj.get(0) ;
                                    JSONObject photoUrlObj = photosUrlObj.getJSONObject("photo_url");
                                    WaraApp.imageUrl = photoUrlObj.optString("encounter");
                                }
                                else
                                    WaraApp.imageUrl = WaraApp.gender+".jpg";


                                 WaraApp.saveSharedPreferences();
                                Glide.with(DisplayActivity.this).load(WaraApp.imageUrl).placeholder(R.drawable.profile_placeholder).dontAnimate().into(imgUserImage);

                                //TODO Llamar al metodo que pinta el medio

                                JSONObject popularityObj = successObj.optJSONObject("user_popularity");
                                if (popularityObj != null) {
                                    switch (popularityObj.optString("popularity_type")) {
                                        case "very_very_low":
                                            imgPopularity_level.setImageResource(R.drawable.ic_pop_bajo);
                                            break;
                                        case "very_low":
                                            imgPopularity_level.setImageResource(R.drawable.ic_pop_bajo);
                                            break;
                                        case "low":
                                            imgPopularity_level.setImageResource(R.drawable.ic_pop_media);
                                            break;
                                        case "medium":
                                            imgPopularity_level.setImageResource(R.drawable.ic_pop_alta);
                                            break;
                                        case "high":
                                            imgPopularity_level.setImageResource(R.drawable.ic_pop_mango);
                                            break;
                                    }
                                }
                                BusProvider.getInstance().post(new NotifyProfileRecieved());

                                //Fetching the spot light data after user profile is loaded successfully.
                                WaraApp.saveSharedPreferences();
                                //getSpotLightData();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });*/
    }

    private void actualizarDatosPerfil(){
        //ACTUALIZAR LOS TEXTVIEWS

        tvUserName.setText(""+WaraApp.name);
        tvCredit.setText(""+WaraApp.balas);
        if(WaraApp.imageDrawer=="" || WaraApp.imageDrawer == null){
            Glide.with(DisplayActivity.this).load(WaraApp.imageUrl).placeholder(R.drawable.profile_placeholder).dontAnimate().into(imgUserImage);
            WaraApp.imageDrawer = WaraApp.imageUrl;
            WaraApp.saveSharedPreferences();
        }
        else {
            Glide.with(DisplayActivity.this).load(WaraApp.imageDrawer).placeholder(R.drawable.profile_placeholder).dontAnimate().into(imgUserImage);
        }


    }
    private void updatePopularity(String popularity){


        switch (popularity) {
            case "very_very_low":
                imgPopularity_level.setImageResource(R.drawable.ic_pop_bajo);
                break;
            case "very_low":
                imgPopularity_level.setImageResource(R.drawable.ic_pop_bajo);
                break;
            case "low":
                imgPopularity_level.setImageResource(R.drawable.ic_pop_media);
                break;
            case "medium":
                imgPopularity_level.setImageResource(R.drawable.ic_pop_alta);
                break;
            case "high":
                imgPopularity_level.setImageResource(R.drawable.ic_pop_mango);
                break;
        }

    }
    private void updateAppValues () {
        if (! HelperMethods.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            updateAppValuesView();
            return;
        }

        Call<UpdateAppValuesApi> call = WSWorker.apiInterface.getupdateAppValues(  WaraApp.id , WaraApp.token, getString(R.string.os_name), getString(R.string.version_name));
        Log.d("DispAct/updateAppValues","updateAppValues() - url: " + call.request().url());

        call.enqueue(new Callback<UpdateAppValuesApi>() {
            @Override
            public void onResponse(Call<UpdateAppValuesApi> call, Response<UpdateAppValuesApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        SuccessDataUpdateValues successData = response.body().getSuccessDataUpdateValues();
                        Log.d(TAG,"onResponse() - sucessData : " + response.toString() +" "+ successData.getLikes());
                        Log.d(TAG,"onResponse() - sucessData : " + successData.getUsersCount());

                        WaraApp.likes = successData.getLikes();
                        WaraApp.matches = successData.getMatches();
                        WaraApp.balas = successData.getBullets();
                        WaraApp.admin_mess = successData.getAdminMess();
                        WaraApp.admin_url = successData.getAdminUrl();
                        WaraApp.users_count = successData.getUsersCount();
                        WaraApp.saveSharedPreferences();
                        updateAppValuesView();
                    }


                }
                catch (Exception e){
                    Log.d("DispAct/updateAppValues","updateAppValues().catch() : " + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context() , e.getMessage() , Toast.LENGTH_SHORT).show();


                }

            }
            @Override
            public void onFailure(Call<UpdateAppValuesApi> call, Throwable t) {
                Log.d("DispAct/updateAppValues","updateAppValues().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_bad_answer) , Toast.LENGTH_SHORT).show();

            }


        });
/**
        Log.d("DispAct/updateAppValues" , "STARTING");
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty(Const.Params.OS, "android");
        json.addProperty(Const.Params.VERSION, getString(R.string.version_name));
        final DisplayActivity c= this;

        Ion.with(c)
                .load(Endpoints.updateAppValues)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null){
                            Log.d ("act/updateAppValues" , "ERROR WEB");
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                        /**    if(HelperFunctions.IsUserAuthenticated(jsonObject, DisplayActivity.this)){
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                              final  JSONObject successObj = jsonObject.getJSONObject("success_data");


                                Log.d("DispAct/updateAppValues" , successObj.toString(2));

                                JsonObject json = new JsonObject();
                                json.addProperty("id", WaraApp.id);
                                json.addProperty("access_token", WaraApp.token);

                                Ion.with(c)
                                        .load(Endpoints.unReadedCount + "?id=" + WaraApp.id + "&dest_id=" +  "&access_token=" + WaraApp.token)
                                        .setJsonObjectBody(json)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result1) {
                                                // do stuff with the result or error

                                                try {
                                                    JSONObject jsonObject1 = new JSONObject(result1.toString());
                                                    if (jsonObject1.optString(Const.Params.STATUS).equals("success")) {
                                                        int w = jsonObject1.getInt("count");
                                                        updateAppValuesView(
                                                                successObj.getInt("likes"),
                                                                successObj.getInt("matches"),
                                                             w);

                                                        updateAdminMessage(
                                                                successObj.getString("admin_mess"),
                                                                successObj.getString("admin_url"));
                                                    }

                                                } catch (JSONException ew) {

                                                }


                                            }
                                        });



                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });**/
    }

    private void updateAppValuesView(){

        actualizarDatosPerfil();
        updatePopularity(WaraApp.popularity);
        updateAdminMessage(WaraApp.admin_mess, WaraApp.admin_url);


        if (WaraApp.balas > 0){
            tvCredit.setText(""+WaraApp.balas);
            tvCredit.setVisibility(View.VISIBLE);
        }
       if (WaraApp.matches > 0){
            tvWaraCount.setText(""+WaraApp.matches);
            tvWaraCount.setVisibility(View.VISIBLE);
        }
        if (WaraApp.likes > 0){
            tvLikedYouCount.setText(""+WaraApp.likes);
            tvLikedYouCount.setVisibility(View.VISIBLE);
        }
        if (WaraApp.user_mess > 0){
            tvChatCount.setText(""+WaraApp.user_mess);
            tvChatCount.setVisibility(View.VISIBLE);
        }
        if (WaraApp.users_count > 0){
            tvUsersCount.setText(""+WaraApp.users_count);
            tvUsersCount.setVisibility(View.VISIBLE);
        }

    }

    private void updateAdminMessage (String text , final String url){

        if (!text.equals("")){
            tvAdminMess.setText(text);

            llAdminMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!url.equals("") && !url.equals(null)){

                        WaraApp.getMy_context().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }}
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            changeFilterSettings();
            return true;
        }else if (id == R.id.action_refresh) {
            gotoFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onClick (View view) {
        // Handle navigation view item clicks here.
        int id = view.getId();
        Log.d("DA/onClick" , "" + id);

        if (id == R.id.rl_chat) {
            Log.d("DA/onClick" , "CHAT");
            startActivity(new Intent(this, ChatHistoryActivity.class));
            tvChatCount.setVisibility(View.INVISIBLE);
            WaraApp.user_mess = 0;
            WaraApp.saveSharedPreferences();
            tvToolbarTittle.setText(R.string.text_chat);
            imgToolbarLogo.setImageResource(R.drawable.wara);

        } else if (id == R.id.rl_wara) {
            Log.d("DA/onClick" , "WARA");
            FragmentWara newFragment = new FragmentWara();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.display_container, newFragment);
            transaction.commit();
            tvWaraCount.setVisibility(View.INVISIBLE);
            WaraApp.matches=0;
            WaraApp.saveSharedPreferences();
            tvToolbarTittle.setText(R.string.text_wara);
            imgToolbarLogo.setImageResource(R.drawable.wara);

        } else if (id == R.id.rl_finding) {
            Log.d("DA/onClick" , "ENCUENTRA");
            DisplayFragment newFragment = new DisplayFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.display_container, newFragment);
            transaction.commit();
            tvToolbarTittle.setText(R.string.text_finding);
            imgToolbarLogo.setImageResource(R.drawable.ic_hook);

        }else if (id==R.id.rl_liked_you){
            Log.d("DA/onClick" , "ADMIRADORES");
            LikedYouFragment newFragment = new LikedYouFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.display_container, newFragment);
            transaction.commit();
            tvLikedYouCount.setVisibility(View.INVISIBLE);
            WaraApp.likes = 0;
            WaraApp.saveSharedPreferences();
            tvToolbarTittle.setText(R.string.text_liked_you);
            imgToolbarLogo.setImageResource(R.drawable.ic_wink);


        }else if (id==R.id.ll_follow_us){
            Intent fbIntent;
            try {
                this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/910445832483973" ));
                //fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/muchawara" ));
            } catch (Exception e) {
                fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/muchawara"));
            }
            startActivity(fbIntent);
        }

        drawer.closeDrawer(GravityCompat.START);
    }
    

    private void cityDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.dialog_city_tittle)

                .setMessage(R.string.dialog_city_message)

                .setPositiveButton(R.string.dialog_city_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        WaraApp.getMy_context().startActivity(
                                new Intent( WaraApp.getMy_context() , ProfileActivity.class )
                        );
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    public void changeFilterSettings (){
        startActivityForResult(new Intent(DisplayActivity.this, FilterActivity.class), FILTER_CODE);
    }

    private void sendLikeStatus(String id, int status , final Activity activity) {

        Log.d("DispAct/getProfile" , "STARTING");

        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.ID, WaraApp.token);
        json.addProperty("encounter_id", id);
        if (status == 1)
            json.addProperty("like", "_like");
        if (status == 2)
            json.addProperty("like", "_dislike");

        Ion.with(this)
                .load(Endpoints.likeUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            //Log.d("JOSON LIKE" , result.getAsString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, activity)){
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");
                                if (successObj.optBoolean("match_found")) {
                                    JSONObject userObj = successObj.getJSONObject("user");
                                    JSONObject profileObj = userObj.getJSONObject("profile_pics");
                                    //showMatchDialog(userObj.optString("name"), profileObj.optString("encounter"), userObj.optString("id"), successObj.optString("contact_id"));
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Boolean status = data.getBooleanExtra("is_updated", false);
                if (status) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BusProvider.getInstance().post(new NotifyFilterChange());
                        }
                    }, 500);

                }
                gotoFragment();
            }
        }

        //Some purchase was made in the super power activity.
        if(requestCode == SUPERPOWER_REQUEST){
            if(resultCode == RESULT_OK){

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        setToolbarLogoTittle();
        //ChatManager.Init(DisplayActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //ChatManager.Destroy();
    }

    /**
     * Function to fetch the spotlight members and menu_toolbar them in
     * navigation bar.
     * Author ANURAG
     */
    private void getSpotLightData(){
        Log.d("DA/getSpotLightData" , "STARTING");
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        Ion.with(this)
                .load(Endpoints.spotlight)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result == null)
                            return;

                        ArrayList<userDetail> userDetailArrayList  = new ArrayList<userDetail>();
                        try{
                            JSONObject data = new JSONObject(result.toString());
                            Log.d("DA/getSpotLightData" , ""+ data.toString(2));

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if(HelperFunctions.IsUserAuthenticated(data, DisplayActivity.this)){
                                return;
                            }

                            JSONObject obj = data.getJSONObject("success_data");
                            JSONArray arr = obj.getJSONArray("spotlight_users");
                            userDetail me = new userDetail();
                            me.setName("Add me");
                            me.setPicture(WaraApp.imageUrl);


                            userDetailArrayList.add(me);
                            for(int i = 0 ; i < arr.length() ; ++i){
                                JSONObject ob = arr.getJSONObject(i);
                                String name = ob.getString("name");
                                JSONObject ob1 = ob.getJSONObject("profile_picture_url");
                                String url = ob1.getString("thumbnail");
                                int id = ob.getInt("id");
                                userDetail user = new userDetail();
                                user.setName(name);
                                user.setPicture(url);
                                user.setId(""+id);
                                userDetailArrayList.add(user);
                            }
                            RecyclerView spot = (RecyclerView) findViewById(R.id.spotlight);
                            GridLayoutManager linearLayoutManager = new GridLayoutManager(DisplayActivity.this, 3);
                            spot.setNestedScrollingEnabled(false);
                            spot.setLayoutManager(linearLayoutManager);
                            spot.setItemAnimator(new DefaultItemAnimator());
                            spot.setAdapter(new SpotLightAdapter(userDetailArrayList, DisplayActivity.this));

                            //Changing the visibility of the views after the spotlight data is fetched
                            // eg after data is fetched hiding the progressbar from the nav view.

                            TextView loadingText = (TextView) findViewById(R.id.loading_spotlight_msg);
                            ProgressBar loadingProgress = (ProgressBar) findViewById(R.id.loading_spotlight_progress);
                            ImageView addImage = (ImageView) findViewById(R.id.add_Image);

                            loadingProgress.setVisibility(View.GONE);
                            loadingText.setVisibility(View.GONE);
                            spot.setVisibility(View.VISIBLE);
                            addImage.setVisibility(View.VISIBLE);
                            Me.setVisibility(View.VISIBLE);
                        }catch (JSONException eq){
                            //Toast.makeText(getApplicationContext(), eq.toString() , Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * This function updates the user's credits count after any purchase or deduction.
     */
    public void UpdateCreditsCount(int newCredits){
        Log.d("DA/UpdateCreditsCount" , ""+ WaraApp.balas );

        //tvCredit.setText("" + WaraApp.balas);
    }

    /**
     * This function updates the super power status
     * @param string new status
     */
    public void UpdateSuperPowerStatus(String string, boolean active){
        tvSuperPowerActive.setText(string);
        if(active){
            tvSuperPowerActive.setTextColor(getResources().getColor(R.color.white));
        }else{
            tvSuperPowerActive.setTextColor(Color.RED);
        }
    }


    /**
     * This function recursively plays the ripple effect on the view
     * @param view on which ripple animation has to be played.
     */
    private void ForceRippleAnimation(final View view){
        if(!PlayRippleEffect){
            return;
        }
        Drawable background = view.getBackground();
        if(Build.VERSION.SDK_INT >= 21 && background instanceof RippleDrawable)
        {
            final RippleDrawable rippleDrawable = (RippleDrawable) background;

            rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});

            Handler handler = new Handler();

            handler.postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    rippleDrawable.setState(new int[]{});
                    ForceRippleAnimation(view);
                }
            }, 1500);
        }
    }
}
