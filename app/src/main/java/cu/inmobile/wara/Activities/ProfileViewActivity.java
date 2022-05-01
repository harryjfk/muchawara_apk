package cu.inmobile.wara.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cu.inmobile.wara.Adapters.ProfileImagesViewPagerAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.GetProfileInfoApi;
import cu.inmobile.wara.Pojo.Photo;
import cu.inmobile.wara.Pojo.ProfileApi;
import cu.inmobile.wara.Pojo.SendLikeStatusApi;
import cu.inmobile.wara.Pojo.SuccessDataProfile;
import cu.inmobile.wara.Pojo.SuccessDataSendLike;
import cu.inmobile.wara.Pojo.TargetDistance;
import cu.inmobile.wara.Pojo.TargetSuccessData;
import cu.inmobile.wara.Pojo.TargetUser;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.MessageViewModel;
import cu.inmobile.wara.RoomModels.User;
import cu.inmobile.wara.RoomModels.UserViewModel;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.HelperMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Cuando se invoque este Activity se pasa el target_id para intentar cargar los datos del Usuario de
 * la Base de Datos del telefono, en caso que no exista este Usuario aun se cargaran sus datos desde
 * el Servidor.
 * */
public class ProfileViewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final int EDIT_PROFILE_CODE = 12;
    boolean result = false;
    private String target_id;
    private ViewPager intro_images;
    private LinearLayout pager_indicator, llWara, llLikesButtons, llButtonChat;
    private ProfileImagesViewPagerAdapter mAdapter;
    private ImageView[] dots;
    private ImageView imgUser1, imgUser2, imgPopularity, imgBack;
    private int dotsCount;
    private ArrayList<String> images;
    private TextView tvName, tvAbout_me, tvCity, tvPopularity;
    private Button btnReport, btnContinue, btnChat, btnChatUser, btn_deja_wara;
    private int reason_number = 0;
    private int actionOnProfile; // 0 (NO ACTION) -- 1 (LIKE) -- 2 (DISLIKE)
    private String TAG = "--ProfileViewActivity";

    private ImageView dislike_button, like_button;

    private boolean isSpinnerSet = false;

    private boolean blurImage;
    private String from;
    private User targetUser;

    private UserViewModel userViewModel;
    private MessageViewModel messageViewModel;

    /**
     * Other user's profile info.
     */
    private LiveData<User> targetUserLive;

    private Integer distance_value, reciver_age;
    private String userId, userName, gender, distance_unit, reciver_state, userApiAboutme;
    private String profileImageUrl;
    private String slug_name, receiverImagePerfil, reciverChatUser, receiverName, contactId, userAboutMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_profile);


        iniVariables();
        settingInterface();
        getProfileInfo();

      /*  if (fromWhere.equals("discover")) {

            button_layout.setVisibility(View.VISIBLE);

        } else if (fromWhere.equals("account")) {
            button_layout.setVisibility(View.GONE);

        }*/
    }

    private void iniVariables() {
        WaraApp.setCurrentActivity(this);
        actionOnProfile = 0;
        images = new ArrayList<>();
        target_id = getIntent().getExtras().getString("target_id");
        blurImage = getIntent().getExtras().getBoolean("blur_image", false);

        Log.d("--ProfileViewActivity", "iniVariables() -> target_id: " + target_id);

        from = getIntent().getExtras().getString("from", "");
        //slug_name = getIntent().getExtras().getString("slug_name");
        //receiverImagePerfil = getIntent().getExtras().getString("receiverImagePerfil");
        //receiverImageGalery = getIntent().getExtras().getString("receiverImageGalery");
        //receiverName = getIntent().getExtras().getString("receiverName");
        //contactId = getIntent().getExtras().getString("contact_id");
        //userAboutMe = getIntent().getExtras().getString("aboutme");

    }

    private void settingInterface() {


        intro_images = (ViewPager) findViewById(R.id.images_viewpager);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        tvName = (TextView) findViewById(R.id.tv_profile_info);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvAbout_me = (TextView) findViewById(R.id.tv_about_me);
        tvPopularity = (TextView) findViewById(R.id.tv_profile_popularity);

        btnReport = (Button) findViewById(R.id.btn_report);
        btnContinue = (Button) findViewById(R.id.btn_keep_looking);
        btnChat = (Button) findViewById(R.id.btn_chat);
        btnChatUser = (Button) findViewById(R.id.btn_chat_user);
        btn_deja_wara = (Button) findViewById(R.id.btn_deja_wara);

        llWara = (LinearLayout) findViewById(R.id.ll_wara);
        llLikesButtons = (LinearLayout) findViewById(R.id.ll_likes_buttons);
        //llButtonChat = (LinearLayout) findViewById(R.id.llButtonChat);
        like_button = (ImageView) findViewById(R.id.btn_profile_like);
        dislike_button = (ImageView) findViewById(R.id.btn_profile_dislike);
        imgUser1 = (ImageView) findViewById(R.id.imgPhotoUser1);
        imgUser2 = (ImageView) findViewById(R.id.imgPhotoUser2);
        imgPopularity = (ImageView) findViewById(R.id.img_popularity);
        imgBack = (ImageView) findViewById(R.id.img_back);

        if (from.equals("DisplayFragment") || from.equals("AdapterLikedYou1")) {
            getProfileInfo();
        } else {

            userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
            messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
            //targetUserLive =
            targetUser = userViewModel.getmUser(target_id);


            if (targetUser != null) {
                images.add(Endpoints.baseNoThumbUrl + targetUser.getProfilePicture());
                updateView(targetUser);
            }

            userViewModel.getmUserLive(target_id).observe(this, new Observer<User>() {
                @Override
                public void onChanged(@Nullable User targetUser) {
                    if (targetUser == null) {
                        return;
                    }
//                reciverChatUser = targetUser.getChatUser();
//                slug_name = targetUser.getChatUser().split("@")[0];
//                receiverImagePerfil = targetUser.getProfilePicture();
//                receiverName = targetUser.getName();
//                reciver_state = targetUser.getState();
//                reciver_age = targetUser.getAge();
//                contactId = targetUser.getChatUser();
//                userAboutMe = targetUser.getAboutMe();
                    updateView(targetUser);

                }
            });
        }
        //slug_name = targetUser.getChatUser().split("@")[0];
        //receiverImagePerfil = targetUser.getProfilePicture();
        //receiverName = targetUser.getName();
        //contactId = targetUser.getChatUser();
        //userAboutMe = targetUser.getAboutMe();



        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportDialog();
            }
        });

        btnChatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("-- ProfileViewActivity", "btnChatUser().onClick() " +
                        "Slug Name: " + slug_name +
                        " Target_Id: " + target_id +
                        " match_name: " + receiverName +
                        " AboutMe: " + userAboutMe +
                        " match_picture" + receiverImagePerfil);


                Intent intent = new Intent(WaraApp.getMy_context(), ChatActivity.class);
                intent.putExtra("target_id", target_id);

                //intent.putExtra("receiverId", slug_name + "@muchawara.com");
                //intent.putExtra("receiverImageUrl",receiverImagePerfil);
                //intent.putExtra("receiverName", receiverName);
                //intent.putExtra("contact_id",contactId);
                //intent.putExtra("aboutme",userAboutMe);
                WaraApp.getMy_context().startActivity(intent);

            }
        });
        btn_deja_wara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLikeStatus(2);
            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaraApp.getMy_context().startActivity(new Intent(WaraApp.getMy_context(), DisplayActivity.class));
            }
        });

        like_button.setOnClickListener(this);
        dislike_button.setOnClickListener(this);


        switch (from) {

            case "DisplayFragment":
                llLikesButtons.setVisibility(View.VISIBLE);
                btnChatUser.setVisibility(View.GONE);
                btn_deja_wara.setVisibility(View.GONE);
                break;
            case "FragmentWara":
                llLikesButtons.setVisibility(View.GONE);
                btnChatUser.setVisibility(View.VISIBLE);
                btn_deja_wara.setVisibility(View.VISIBLE);

            case "ChatActivity":
                llLikesButtons.setVisibility(View.GONE);
                btnChatUser.setVisibility(View.VISIBLE);
                btn_deja_wara.setVisibility(View.VISIBLE);

            case "MessageListAdapter":
                llLikesButtons.setVisibility(View.GONE);
                btnChatUser.setVisibility(View.VISIBLE);
                btn_deja_wara.setVisibility(View.VISIBLE);

            case "ProfileViewActivity":
                llLikesButtons.setVisibility(View.GONE);
                btnChatUser.setVisibility(View.VISIBLE);
                btn_deja_wara.setVisibility(View.VISIBLE);

        }

    }

    private void report() {

        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("reported_user_id", target_id);
        json.addProperty("reason", getResources().getStringArray(R.array.report_reasons)[reason_number]);

        Ion.with(this)
                .load(Endpoints.blockUrl)
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
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, ProfileViewActivity.this)) {
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.optJSONObject("success_data");
                                if (successObj != null)
                                    Toast.makeText(ProfileViewActivity.this, R.string.report_message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject errorObj = jsonObject.optJSONObject("error_data");
                                if (errorObj != null)
                                    Toast.makeText(ProfileViewActivity.this, R.string.report_message_send_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Loads the other user's profile.
     */


    /**
     * Loads the other user's profile.
     */
    private void getProfileInfo() {

        if (!HelperMethods.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }
        //CREO LA CONEXION CON LA API PASANDO LOS PARAMENTROS USER_ID, ACCESS_TOKEN


        Call<GetProfileInfoApi> call = WSWorker.apiInterface.getProfileInfo(WaraApp.id, WaraApp.token, target_id);
        Log.d(TAG, "getProfileInfo() -url: " + call.request().url());

       /* JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);*/

        //HAGO LA PETICION AL SERVE CON RETROFIT

        call.enqueue(new Callback<GetProfileInfoApi>() {
            @Override
            public void onResponse(Call<GetProfileInfoApi> call, Response<GetProfileInfoApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        TargetSuccessData successData = response.body().getSuccessData();
                        Log.d(TAG, "getProfileInfo() -successData: " + response.toString());
                        images.clear();
                        List<Photo> photoArray = successData.getPhotos();
                        //myImages = new ArrayList<String>();

                        for (int i = 0; i < photoArray.size(); i++) {
                            images.add(photoArray.get(i).getPhotoUrl().getEncounter());
                        }


                        User userApi = new User(
                                successData.getUser().getId().toString(),
                                successData.getUser().getSlugName() + "@muchawara.com",
                                successData.getUser().getName(),
                                successData.getUser().getAge(),
                                successData.getUser().getProfilePicture(),
                                successData.getUser().getStatus(),
                                successData.getUser().getAboutme(),
                                true,
                                successData.getCity(),
                                successData.getUserPopularity().getPopularityType(),
                                successData.getUser().getUsername()
                        );

                        if (targetUser == null) {
                            if (images.size() == 0) {
                                images.add(successData.getUser().getTargetProfilePicUrl().getEncounter());
                            }
                            updateView(userApi);
                        } else {
                            if (images.size() == 0) {
                                images.add(successData.getUser().getTargetProfilePicUrl().getEncounter());
                            }
                            User mergeUser = targetUser.mergeUser(userApi);
                            WaraApp.userRepo.insert(mergeUser);
                        }
                    } else {
                        Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_error_web), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "getProfileInfo() .catch(): " + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetProfileInfoApi> call, Throwable t) {
                Log.d(TAG, "getProfileInfo().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateView(User user) {
        if (user.getChatUser().equals("wara@muchawara.com")){
            btn_deja_wara.setVisibility(View.GONE);
            btnReport.setVisibility(View.GONE);
        }
        if (user.getAboutMe().equals("") || user.getAboutMe().equals("null")) {
            tvAbout_me.setText(R.string.btn_about_me_blank_text);
        } else {
            tvAbout_me.setText(user.getAboutMe());
        }

        if (user.getCity().equals("")) {
            tvCity.setText(R.string.text_city);
        } else {
            tvCity.setText(user.getCity());
        }

        if (user.getName().equals("") || user.getAge().equals("")) {
            tvName.setText(R.string.text_name + ", " + "1");
        } else {
            tvName.setText(user.getName() + ", " + user.getAge());
        }

        setUpProfileImages();
        updatePopularity(user.getPopularity());


    }

    private void updatePopularity(String popularity) {

        switch (popularity) {
            case "very_very_low":
                imgPopularity.setImageResource(R.drawable.ic_pop_bajo);
                tvPopularity.setText(R.string.text_popularity_low);
                break;
            case "very_low":
                imgPopularity.setImageResource(R.drawable.ic_pop_bajo);
                tvPopularity.setText(R.string.text_popularity_low);
                break;
            case "low":
                imgPopularity.setImageResource(R.drawable.ic_pop_media);
                tvPopularity.setText(R.string.text_popularity_medium);
                break;
            case "medium":
                imgPopularity.setImageResource(R.drawable.ic_pop_alta);
                tvPopularity.setText(R.string.text_popularity_high);
                break;
            case "high":
                imgPopularity.setImageResource(R.drawable.ic_pop_mango);
                tvPopularity.setText(R.string.text_popularity_mango);
                break;

            default:
                imgPopularity.setImageResource(R.drawable.ic_pop_bajo);
                tvPopularity.setText(R.string.text_popularity_low);
                break;
        }


    }

//    private void getProfileInfo() {
//        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_profile));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        JsonObject json = new JsonObject();
//        json.addProperty(Const.Params.ID, WaraApp.id);
//        json.addProperty(Const.Params.TOKEN, WaraApp.token);
//        json.addProperty("view_user_id", target_id);
//
//        images.clear();
//        Ion.with(this)
//                .load(Endpoints.viewUserUrl)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        progressDialog.dismiss();
//                        if (result == null)
//                            return;
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(result.toString());
//                            Log.d("PI/getProfileInfo", jsonObject.toString(2));
//                            /**
//                             * Logging out user if authentication fails, if user has logged in his/her account
//                             * on some other device as well.
//                             */
//                            if(HelperFunctions.IsUserAuthenticated(jsonObject, ProfileViewActivity.this)){
//                                return;
//                            }
//
//                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
//                                JSONObject successObject = jsonObject.getJSONObject("success_data");
//                                JSONObject userObject = successObject.getJSONObject("user");
//                                tvName.setText(userObject.optString("name") + ", " + userObject.optString("age"));
//
//                                //Getting user's profile info
//                                userName = userObject.optString("name");
//                                //userId = userObject.getString("id");
//                                JSONObject profilePicture = userObject.getJSONObject("profile_pic_url");
//                                profileImageUrl = profilePicture.getString("encounter");
//                               // contactId = userId;
//                                String gender = userObject.optString("gender");
//                                JSONObject distanceObject = successObject.optJSONObject("distance");
//
//                                /**
//                                 * Setting up the about me information in the user's profile
//                                 */
//
//                                if (userObject.getString("aboutme").equals("") || userObject.getString("aboutme").equals("null")) {
//                                    tvAbout_me.setText(R.string.btn_about_me_blank_text);
//                                } else {
//                                    tvAbout_me.setText(userObject.getString("aboutme"));
//                                }
//                                /**
//                                 * Setting up the user's city and country
//                                 */
//                                if (!successObject.getString("city").equals("")) {
//                                    tvCity.setText(successObject.getString("city"));
//                                }
//
//
//                                JSONArray imageArray = successObject.optJSONArray("photos");
//                                if (imageArray != null) {
//                                    for (int j = 0; j < imageArray.length(); j++) {
//                                        JSONObject imageObj = imageArray.getJSONObject(j);
//                                        JSONObject photoObj = imageObj.getJSONObject("photo_url");
//                                        images.add(photoObj.optString("encounter"));
//                                        Log.d("PI/getProfileInfo", "images: " + j);
//
//                                    }
//                                }
//                                //mAdapter.notifyDataSetChanged();
//                                setUpProfileImages();
//
//                                JSONObject popularityObj = successObject.optJSONObject("user_popularity");
//                                if (popularityObj != null) {
//                                    switch (popularityObj.optString("popularity_type")) {
//                                        case "very_very_low":
//                                            imgPopularity.setImageResource(R.drawable.ic_pop_bajo);
//                                            tvPopularity.setText( R.string.text_popularity_low);
//                                            break;
//                                        case "very_low":
//                                            imgPopularity.setImageResource(R.drawable.ic_pop_bajo);
//                                            tvPopularity.setText( R.string.text_popularity_low);
//                                            break;
//                                        case "low":
//                                            imgPopularity.setImageResource(R.drawable.ic_pop_media);
//                                            tvPopularity.setText( R.string.text_popularity_medium);
//                                            break;
//                                        case "medium":
//                                            imgPopularity.setImageResource(R.drawable.ic_pop_alta);
//                                            tvPopularity.setText( R.string.text_popularity_high);
//                                            break;
//                                        case "high":
//                                            imgPopularity.setImageResource(R.drawable.ic_pop_mango);
//                                            tvPopularity.setText( R.string.text_popularity_mango);
//                                            break;
//                                    }
//                                }
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//    }

    private void setUpProfileImages() {
        // Set product images
        Log.d("PI/setUpProfileImages", "cant img: " + images.size());

        mAdapter = new ProfileImagesViewPagerAdapter(this, images, blurImage);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewIndicator();
    }

    private void setUiPageViewIndicator() {
        Log.d("PI/setUiPageView", "cant img: " + images.size());
        dotsCount = images.size();
        dots = new ImageView[dotsCount];
        pager_indicator.removeAllViews();

        if (dotsCount == 0)
            return;

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                result = data.getBooleanExtra("isUpdated", false);
                if (result) {
                    getProfileInfo();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();

        switch (actionOnProfile) {
            case 0:
                finish();
                break;
            case 1:
                resultIntent.putExtra("action", "like");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
            case 2:
                resultIntent.putExtra("action", "dislike");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        switch (view.getId()) {
            case R.id.btn_profile_dislike:
                actionOnProfile = 2;
                sendLikeStatus(2);
                break;
            case R.id.btn_profile_like:
                actionOnProfile = 1;
                sendLikeStatus(1);
                break;
            case R.id.btn_deja_wara:
                actionOnProfile = 2;
                sendLikeStatus(2);
                break;
        }
    }

    private void sendLikeStatus(int status) {
        if (!WaraApp.isBalasAvailable() && status == 1) {
            Toast.makeText(this, "Lo siento, no puedes evaluar sin BALAS", Toast.LENGTH_SHORT).show();
            return;
        }
        String like = "";
        if (status == 1)
            like = "_like";
        if (status == 2)
            like = "_dislike";

        Call<SendLikeStatusApi> call = WSWorker.apiInterface.sendLikeStatus(
                WaraApp.id,
                WaraApp.token,
                target_id,
                like);
        Log.d(TAG, "sendLikeStatus() - url: " + call.request().url());
        call.enqueue(new Callback<SendLikeStatusApi>() {
            @Override
            public void onResponse(Call<SendLikeStatusApi> call, Response<SendLikeStatusApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        SuccessDataSendLike successData = response.body().getSuccessData();
                        Log.d(TAG, "sendLikeStatus() -successData: " + response.toString());
                        WaraApp.balas = successData.getCredits();
                        WaraApp.saveSharedPreferences();

                        Log.d(TAG, "sendLikeStatus() -match_found: " + successData.getMatchFound());
                        if (successData.getMatchFound()) {
                            Log.d("sendLikeStatus", "match okk");

                            User userNew = new User(
                                    target_id,
                                    successData.getUser().getSlugName()+"@muchawara.com",
                                    successData.getUser().getName(),
                                    successData.getUser().getAge(),
                                    successData.getUser().getProfilePicUrl(),
                                    "",
                                    successData.getUser().getAboutme(),
                                    true,
                                    successData.getUser().getFullcity(),
                                    successData.getUser().getPopularitye(),
                                    successData.getUser().getName()
                            );
                            WaraApp.userRepo.insert(userNew);
                            showMatchScreen(successData.getUser().getProfilePics().getEncounter());

                        }
                        else{
                            switch (from) {
                                case "AdapterLikedYou1":
                                    finish();
                                    break;
                                case "DisplayFragment":
                                    WaraApp.getMy_context().startActivity(
                                            new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                    );
                                    break;
                                case "FragmentWara":
                                    if (successData.getSuccessText().equals("User disliked successfully.")) {
                                        userViewModel.deleteTargetUserById(target_id);
                                        messageViewModel.deleteTargetMessageById(target_id);
                                        Toast.makeText(ProfileViewActivity.this, R.string.toast_dialog_dislike, Toast.LENGTH_SHORT).show();
                                        WaraApp.getMy_context().startActivity(
                                                new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                        );

                                    }
                                    break;

                                case "ChatActivity":
                                    if (successData.getSuccessText().equals("User disliked successfully.")) {
                                        userViewModel.deleteTargetUserById(target_id);
                                        messageViewModel.deleteTargetMessageById(target_id);
                                        Toast.makeText(ProfileViewActivity.this, R.string.toast_dialog_dislike, Toast.LENGTH_SHORT).show();
                                        WaraApp.getMy_context().startActivity(
                                                new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                        );

                                    }
                                    break;

                                case "MessageListAdapter":
                                    if (successData.getSuccessText().equals("User disliked successfully.")) {
                                        userViewModel.deleteTargetUserById(target_id);
                                        messageViewModel.deleteTargetMessageById(target_id);
                                        Toast.makeText(ProfileViewActivity.this, R.string.toast_dialog_dislike, Toast.LENGTH_SHORT).show();
                                        WaraApp.getMy_context().startActivity(
                                                new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                        );

                                    }
                                    break;
                            }

                        }
                    } else {
                        Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_error_web), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "sendLike() .catch(): " + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SendLikeStatusApi> call, Throwable t) {
                Log.d("PROFILE/ProfileApi", "getMyProfile().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendLikeStatus_old(int status) {

        Log.d("sendLikeStatus", status + "");

        if (!WaraApp.isBalasAvailable() && status == 1) {
            Toast.makeText(this, "Lo siento, no puedes evaluar sin BALAS", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("encounter_id", target_id);
        if (status == 1)
            json.addProperty("like", "_like");
        if (status == 2)
            json.addProperty("like", "_dislike");
        final ProfileViewActivity c = this;
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

                            Log.d("sendLikeStatus", jsonObject.toString(2) + "");

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, WaraApp.getCurrentActivity())) {
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                Log.d("sendLikeStatus", jsonObject.toString(1) + "");

                                final JSONObject successObj = jsonObject.getJSONObject("success_data");
                                WaraApp.balas = successObj.optInt("credits");
                                if (successObj.optBoolean("match_found")) {

                                    Log.d("sendLikeStatus", "match okk");
                                    int match_id = successObj.optJSONObject("user").optInt("id");
                                    JSONObject userObj = successObj.getJSONObject("user");
                                    JSONObject profileObj = userObj.getJSONObject("profile_pics");
                                    JsonObject json = new JsonObject();
                                    json.addProperty("id", WaraApp.id);
                                    json.addProperty("dest_id", match_id);
                                    showMatchScreen( userObj.optString("profile_pic_url"));

//                                    Ion.with(c)
//                                            .load(Endpoints.bindUsers + "?id=" + WaraApp.id + "&dest_id=" + match_id + "&access_token=" + WaraApp.token)
//                                            .setJsonObjectBody(json)
//                                            .asJsonObject()
//                                            .setCallback(new FutureCallback<JsonObject>() {
//                                                @Override
//                                                public void onCompleted(Exception e, JsonObject result) {
//                                                    // do stuff with the result or error
//
//                                                    try {
//                                                        JSONObject userObj = successObj.getJSONObject("user");
//                                                        JSONObject profileObj = userObj.getJSONObject("profile_pics");
//                                                        showMatchScreen(userObj.optString("name"), userObj.optString("profile_pic_url"), userObj.optString("chat_username"), successObj.optString("contact_id"));
//
//                                                    } catch (JSONException ew) {
//
//                                                    }
//
//
//                                                }
//                                            });

                                }
                                switch (from) {
                                    case "AdapterLikedYou1":
                                        finish();
                                        break;
                                    case "DisplayFragment":
                                        finish();
                                        break;
                                    case "FragmentWara":
                                        if (successObj.optString("success_text").equals("User disliked successfully.")) {
                                            userViewModel.deleteTargetUserById(target_id);
                                            messageViewModel.deleteTargetMessageById(target_id);
                                            Toast.makeText(ProfileViewActivity.this, R.string.toast_dialog_dislike, Toast.LENGTH_SHORT).show();
                                            WaraApp.getMy_context().startActivity(
                                                    new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                            );

                                        }
                                        break;

                                    case "ChatActivity":
                                        if (successObj.optString("success_text").equals("User disliked successfully.")) {
                                            userViewModel.deleteTargetUserById(target_id);
                                            messageViewModel.deleteTargetMessageById(target_id);
                                            Toast.makeText(ProfileViewActivity.this, R.string.toast_dialog_dislike, Toast.LENGTH_SHORT).show();
                                            WaraApp.getMy_context().startActivity(
                                                    new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                            );

                                        }
                                        break;

                                    case "MessageListAdapter":
                                        if (successObj.optString("success_text").equals("User disliked successfully.")) {
                                            userViewModel.deleteTargetUserById(target_id);
                                            messageViewModel.deleteTargetMessageById(target_id);
                                            Toast.makeText(ProfileViewActivity.this, R.string.toast_dialog_dislike, Toast.LENGTH_SHORT).show();
                                            WaraApp.getMy_context().startActivity(
                                                    new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                                            );

                                        }
                                        break;
                                }


                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                });
        llLikesButtons.setVisibility(View.GONE);


    }

    private void showMatchScreen( final String match_picture) {

        llWara.setVisibility(View.VISIBLE);
        llLikesButtons.setVisibility(View.GONE);

        Glide.with(this).load(WaraApp.imageUrl).placeholder(R.drawable.user_profile).dontAnimate().into(imgUser1);
        Glide.with(this).load(match_picture).placeholder(R.drawable.user_profile).dontAnimate().into(imgUser2);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llWara.setVisibility(View.GONE);
                //blurImage = false;
                //llLikesButtons.setVisibility(View.INVISIBLE);
                //mAdapter.notifyDataSetChanged();
                //intro_images.setAdapter(mAdapter);
                Intent intent = new Intent(WaraApp.getCurrentActivity(), ProfileViewActivity.class);
                intent.putExtra("target_id", target_id);
                intent.putExtra("from","ProfileViewActivity");
                WaraApp.getCurrentActivity().startActivity(intent);

            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("-- ProfileViewActivity", "btnChatUser().onClick() " +
                        "Slug Name: " + slug_name +
                        " Target_Id: " + target_id +
                        " match_name: " + receiverName +
                        " AboutMe: " + userAboutMe +
                        " match_picture" + receiverImagePerfil);


                Intent intent = new Intent(WaraApp.getMy_context(), ChatActivity.class);
                intent.putExtra("target_id", target_id);

                //intent.putExtra("receiverId", slug_name + "@muchawara.com");
                //intent.putExtra("receiverImageUrl",receiverImagePerfil);
                //intent.putExtra("receiverName", receiverName);
                //intent.putExtra("contact_id",contactId);
                //intent.putExtra("aboutme",userAboutMe);
                WaraApp.getMy_context().startActivity(intent);
            }
        });

    }


    private void reportDialog() {
        reason_number = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.report_tittle)

                .setSingleChoiceItems(R.array.report_reasons, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reason_number = which;
                    }
                })

                .setPositiveButton(R.string.report_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        report();
                    }
                })
                .setNegativeButton(R.string.report_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private static class CustomAdapter<T> extends ArrayAdapter<String> {
        public CustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText("");
            return view;
        }
    }

}
