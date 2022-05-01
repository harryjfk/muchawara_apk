package cu.inmobile.wara.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.ion.Ion;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cu.inmobile.wara.Adapters.InterestListAdapter;
import cu.inmobile.wara.Adapters.MyImageListAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.InterestInfo;
import cu.inmobile.wara.Models.PackageDetail;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.CityApi;
import cu.inmobile.wara.Pojo.Photo;
import cu.inmobile.wara.Pojo.ProfileApi;
import cu.inmobile.wara.Pojo.SuccessDataProfile;
import cu.inmobile.wara.Pojo.UploadProfileApi;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.City;
import cu.inmobile.wara.RoomModels.CityViewModel;
import cu.inmobile.wara.Utils.CitiesPresenter;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Utils.SeekArc;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements MyImageListAdapter.CallbackInterface, View.OnClickListener {

    WaraApp waraApp ;

    private int REFILL_CREDIT_REQUEST = 5;
    private int CREDIT_UPDATE_REQUEST = 3;
    private String TAG="--ProfileActivity";
    private static final int PICK_IMAGE = 1243;
    private static final int REQUEST_CODE_PAYPAL = 1;
    private static final int REQUEST_CODE_PAYPAL_1 = 2;
//    public static PayPalConfiguration config = new PayPalConfiguration()
//            .environment(Const.CONFIG_ENVIRONMENT)
//            .clientId(Const.CONFIG_CLIENT_ID);
    int arrayCount = 0;
    private LinearLayout llInterest, llScore, llVerify, llSuper, llPopularity, llCredits;
    private RecyclerView my_image_list;
    private MyImageListAdapter myImageListAdapter;
    private ArrayList<String> myImages;
    private ImageView imgPopularityLevel, imgPrincipalPhoto, imgBack;
    private TextView tvActiveStatus, tvPopularityStatus, tvCredits, tvScore, tvLikeNumber, tvVerifiedText, tvAboutMe, tvCity;
    private EditText tvInfo, etAboutMe, editCity;
    private Button btnLogout, btnSave;
    private LinearLayout mLinearLayout;
    private RecyclerView interest_list;
    private ArrayList<InterestInfo> interestInfoArrayList, fullInterestInfoArrayList;
    private InterestListAdapter interestListAdapter;
    private SeekArc scoreArc;
    private ArrayList<String> packageList;
    private ArrayList<PackageDetail> packageDetailList;

    private Autocomplete citiesAutocomplete;
    private CityViewModel cityViewModel;

    private ArrayList<CityApi.City> cityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniVariables();
        settingInterface();

        updatePopularity(WaraApp.popularity);
        getMyProfile();
        //getFilter();

        //settingAutocomplteCities(cityList);

//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startService(intent);
    }

    private void iniVariables (){
        waraApp = (WaraApp) getApplication();

        WaraApp.setMy_context(this);
        WaraApp.setCurrentActivity(this);

        cityViewModel = ViewModelProviders.of(this).get(CityViewModel.class);

//        cityViewModel.getAllCities().observe(this, new Observer<List<City>>() {
//            @Override
//            public void onChanged(@Nullable final List<City> cities) {
//                Log.d("-- ProfileActivity" , "onChanged() Cities");
//                settingAutocomplteCities(cities);
//
//
//            }
//        });
        /*id = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.USER_ID, "");
        token = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.SESSION_TOKEN, "");*/
    }

    private void settingInterface (){
        myImages = new ArrayList<>();
        interestInfoArrayList = new ArrayList<>();
        fullInterestInfoArrayList = new ArrayList<>();
        my_image_list = (RecyclerView) findViewById(R.id.my_image_list);
        imgPopularityLevel = (ImageView) findViewById(R.id.img_popularity);
        imgPrincipalPhoto = (ImageView) findViewById(R.id.img_profile_photo);
        tvCredits = (TextView) findViewById(R.id.tv_profile_credits);
        tvPopularityStatus = (TextView) findViewById(R.id.tv_profile_popularity);
        tvInfo = (EditText) findViewById(R.id.tv_profile_name);
        etAboutMe= (EditText) findViewById(R.id.edit_about_me);
        //tvCity = (TextView) findViewById(R.id.tv_city);
        editCity = (EditText) findViewById(R.id.edit_city) ;
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnSave = (Button) findViewById(R.id.btn_save);
        imgBack = (ImageView) findViewById(R.id.img_back);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaraApp.imageUrl=WaraApp.imageDrawer;
                WaraApp.saveSharedPreferences();
                WaraApp.getMy_context().startActivity(
                        new Intent( WaraApp.getMy_context() , DisplayActivity.class)
                );
            /*
                {
                    WaraApp.getMy_context().startActivity(
                            new Intent( WaraApp.getMy_context() , DisplayActivity.class)
                    );
                }
                else
                {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ProfileActivity.this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿ Desea guardar los cambios realizados ?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            saveProfile();
                            WaraApp.getMy_context().startActivity(
                                    new Intent( WaraApp.getMy_context() , ProfileActivity.class)
                            );
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            WaraApp.getMy_context().startActivity(
                                    new Intent( WaraApp.getMy_context() , DisplayActivity.class)
                            );
                        }
                    });
                    dialogo1.show();
                }
*/
            }
        });

        /*tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperFunctions.UpdateAboutMeStatus(ProfileActivity.this, WaraApp.id, WaraApp.token);
            }
        });*/

//      interest_list = (RecyclerView) findViewById(R.id.interest_list);

/*        scoreArc = (SeekArc) findViewById(R.id.seekArc);
        scoreArc.setMax(10);
        scoreArc.setArcWidth(10);
        scoreArc.setProgressWidth(10);
        scoreArc.setRoundedEdges(true);
        scoreArc.setSweepAngle(320);
        scoreArc.setProgress(5);
        scoreArc.setStartAngle(20);
        scoreArc.setProgressColor(getResources().getColor(R.color.violet));
        scoreArc.setArcColor(getResources().getColor(R.color.black_100));
        verified_text = (TextView) findViewById(R.id.verified_text);
        verified_image = (ImageView) findViewById(R.id.verified_image);*/


        /*tvAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperFunctions.UpdateAboutMeStatus(ProfileActivity.this, WaraApp.id, WaraApp.token);
            }
        });*/

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 waraApp.signOut();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });
        btnSave.requestFocus();
        actualizarDatosPerfil();
        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.d(TAG, "beforeTextChanged() ");
                if (citiesAutocomplete == null) {
                    cityViewModel.getAllCities().observe(ProfileActivity.this , new Observer<List<City>>() {
                        @Override
                        public void onChanged(@Nullable final List<City> cities) {
                            Log.d(TAG, "iniVariables() - onChanged()");

                            settingAutocomplteCities(cities);

                        }
                    });
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged() ");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged() ");
            }
        });
        //setUpInterestRecyclerView();
        setUpImageRecyclerView();
    }

    private void settingAutocomplteCities (List<City> cities){
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        CitiesPresenter presenter = new CitiesPresenter(this, cities);
        AutocompleteCallback<String> callback = new AutocompleteCallback<String>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, String item) {
                editable.clear();
                editable.append(item);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {}
        };


        citiesAutocomplete = Autocomplete.<String>on(editCity)
                .with(elevation)
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
    }

    /*private void settingAutocomplteCities (ArrayList<CityApi.City> cities){
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        CitiesPresenter presenter = new CitiesPresenter(this, cities);
        AutocompleteCallback<String> callback = new AutocompleteCallback<String>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, String item) {
                editable.clear();
                editable.append(item);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {}
        };

        citiesAutocomplete = Autocomplete.<String>on(editCity)
                .with(elevation)
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
    }*/





    private void setUpInterestRecyclerView() {
        interestListAdapter = new InterestListAdapter(interestInfoArrayList, fullInterestInfoArrayList, this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        interest_list.setLayoutManager(mLayoutManager);
        interest_list.setItemAnimator(new DefaultItemAnimator());
        interest_list.setAdapter(interestListAdapter);
    }

//    private void payByPayPal(String amount) {
//        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", "Total bill",
//                PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
//        // send the same configuration for restart resiliency
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
//        startActivityForResult(intent, REQUEST_CODE_PAYPAL);
//    }

//    private void payByPayPalForSuper(String amount) {
//        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", "Total bill",
//                PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
//        // send the same configuration for restart resiliency
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
//        startActivityForResult(intent, REQUEST_CODE_PAYPAL_1);
//    }

    /**
     * Loads the my profile
     */
    private void getMyProfile() {

        if (! HelperMethods.isNetworkAvailable(this)){
            Glide.with(ProfileActivity.this).load(WaraApp.imageDrawer).placeholder(R.drawable.profile_placeholder).dontAnimate().into(imgPrincipalPhoto);
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_loading_profile));
        progressDialog.setCancelable(true);
        progressDialog.show();




            //CREO LA CONEXION CON LA API PASANDO LOS PARAMENTROS USER_ID, ACCESS_TOKEN


            Call<ProfileApi> call = WSWorker.apiInterface.getProfile(WaraApp.id, WaraApp.token);
            Log.d("PROFILE/getMyProfile", "getMyProfile() -url: " + call.request().url());

       /* JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);*/

            //HAGO LA PETICION AL SERVE CON RETROFIT
            call.enqueue(new Callback<ProfileApi>() {
                @Override
                public void onResponse(Call<ProfileApi> call, Response<ProfileApi> response) {

                    try {

                        if (response.body().getStatus().equals("success")) {
                            SuccessDataProfile successData = response.body().getSuccessDataProfile();
                            Log.d("PROFILE/getMyProfile", "getMyProfile() -successData: " + response.toString());

                            WaraApp.name = successData.getUser().getName();
                            WaraApp.balas = successData.getBalas();
                            WaraApp.city_user = successData.getCity();
                            WaraApp.about_me = successData.getUser().getAboutme();
                            WaraApp.popularity = successData.getUserPopularity().getPopularityType();
                            WaraApp.saveSharedPreferences();

                            myImages.add(null);

                            List<Photo> photoArray = successData.getPhotos();
                            //myImages = new ArrayList<String>();
                            if (myImages != null) {
                                for (int j = 0; j < photoArray.size(); j++) {
                                    myImages.add(photoArray.get(j).getPhotoUrl().getEncounter());
                                }
                            }

                        }
                        actualizarDatosPerfil();
                        updatePopularity(WaraApp.popularity);

                        myImageListAdapter.notifyDataSetChanged();
                        if (myImages.size() > 1)
                            setProfileImage(1);


                        progressDialog.cancel();
                    } catch (Exception e) {
                        Log.d("PROFILE/getMyProfile", "getMyProfile() .catch(): " + e.getMessage());
                        Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ProfileApi> call, Throwable t) {
                    Log.d("PROFILE/ProfileApi", "getMyProfile().onFailure : " + t.getMessage());

                    Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();

                }


            });

/*
        Ion.with(this)
                .load(Endpoints.myProfileUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressDialog.dismiss();
                        if (result == null) {
                            Log.d("PROFILE/getMyProfile", "ERROR WEB");
                            Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_error_web ), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {

                            JSONObject jsonObject = new JSONObject(result.toString());

                            Log.d("PROFILE/getMyProfile", jsonObject.toString(2));
                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                         /*   if(HelperFunctions.IsUserAuthenticated(jsonObject, ProfileActivity.this)){
                                return;
                            }
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");

                                JSONObject userObj = successObj.getJSONObject("user");
                                //JSONObject profileObj = userObj.getJSONObject("photo_url");

                                WaraApp.name = userObj.optString("name");
                                WaraApp.balas =  successObj.optInt("balas", 0 );
                                WaraApp.city = successObj.optString("city");
                                WaraApp.imageUrl = userObj.optString("profile_pic_url");

                                WaraApp.saveSharedPreferences();

                                tvInfo.setText(""+ WaraApp.name);
                                tvCredits.setText(""+ WaraApp.balas);*/


                                //TODO Falta poner la edad o la fecha de nacimiento

                                /*if (userObj.optString("superpower_activated").equals("true")) {
                                    active_status.setText("Active");
                                    active_status.setTextColor(getResources().getColor(R.color.white));
                                } else {
                                    active_status.setText("Inactive");
                                    active_status.setTextColor(getResources().getColor(R.color.colorAccent));
                                }*/
                                /*if (!userObj.optString("age").equals("")) {
                                    age.setText(", " + userObj.optString("age"));
                                }*/

                                /*if (userObj.optString("verified").equalsIgnoreCase("verified")) {
                                    boolean isAny = false;
                                    if (userObj.optString("register_from").equalsIgnoreCase("facebook")) {
                                        verified_text.setText("Facebook");
                                        verified_image.setImageResource(R.drawable.facebook);
                                        isAny = true;
                                    }
                                    if (userObj.optString("register_from").equalsIgnoreCase("google")) {
                                        verified_text.setText("Google");
                                        verified_image.setImageResource(R.drawable.google_plus);
                                        isAny = true;
                                    }
                                    if (!isAny)
                                        verify_layout.setVisibility(View.GONE);
                                } else {
                                    verify_layout.setVisibility(View.GONE);
                                }*/

                                /**
                                 * Getting more details on the about me .
                                 */
                                /*JSONArray field_section = successObj.getJSONArray("field_sections");
                                String moreInfo = "";
                                for(int i = 0 ; i < field_section.length() ; ++i){
                                    JSONObject o1 = field_section.getJSONObject(i);
                                    JSONArray fields = o1.getJSONArray("fields");
                                    for(int j = 0 ; j < fields.length() ; ++j){
                                        JSONObject o2 = fields.getJSONObject(j);
                                        String text = o2.getString("text") + " - ";
                                        //moreInfo += o2.getString("text") + " - ";
                                        JSONArray opt = o2.getJSONArray("options");
                                        boolean flag = false;
                                        for(int k = 0 ; k < opt.length() ; ++k){
                                            JSONObject o3 = opt.getJSONObject(k);
                                            if(o3.getString("is_selected").equals("true")){
                                                moreInfo += text + (o3.getString("text") + System.getProperty("line.separator"));
                                                flag = true;
                                            }
                                        }
                                        if(!flag){
                                           // moreInfo += "Not Available" + System.getProperty("line.separator");
                                        }
                                    }
                                }*/

                                /**
                                 * Setting up the about me information in the user's profile
                                 */
                                /*
                                if(userObj.getString("aboutme").equals("") || userObj.getString("aboutme").equals("null")){
                                    etAboutMe.setText( getString(R.string.btn_about_me_blank_text));
                                }else{
                                    etAboutMe.setText(userObj.getString("aboutme") );
                                }

                                //editCity.setText(userObj.optString("country") + ", " + userObj.optString("city") + ", " +userObj.optString("township") );
                                editCity.setText(WaraApp.city);*/

                                /*JSONArray interestArray = successObj.optJSONArray("user_interests");
                                if (interestArray != null) {
                                    for (int k = 0; k < interestArray.length(); k++) {
                                        JSONObject interestObj = interestArray.getJSONObject(k);
                                        InterestInfo interestInfo = new InterestInfo();
                                        interestInfo.setInterestId(interestObj.optString("interest_id"));
                                        interestInfo.setInterestName(interestObj.optString("interest_text"));
                                        if (k < 4) {
                                            if (k == 3)
                                                interestInfoArrayList.add(null);
                                            else
                                                interestInfoArrayList.add(interestInfo);
                                        }
                                        fullInterestInfoArrayList.add(interestInfo);
                                    }

                                }*/

                                //JSONObject profileObj = userObj.getJSONObject("profile_pic_url");

                                //TODO PUT TEXT ON STRINGS

                             /*   JSONObject popularityObj = successObj.optJSONObject("user_popularity");
                                if (popularityObj != null) {
                                    switch (popularityObj.optString("popularity_type")) {
                                        case "very_very_low":
                                            imgPopularityLevel.setImageResource(R.drawable.ic_pop_bajo);
                                            tvPopularityStatus.setText( R.string.text_popularity_low);
                                            break;
                                        case "very_low":
                                            imgPopularityLevel.setImageResource(R.drawable.ic_pop_bajo);
                                            tvPopularityStatus.setText( R.string.text_popularity_low);
                                            break;
                                        case "low":
                                            imgPopularityLevel.setImageResource(R.drawable.ic_pop_media);
                                            tvPopularityStatus.setText( R.string.text_popularity_medium);
                                            break;
                                        case "medium":
                                            imgPopularityLevel.setImageResource(R.drawable.ic_pop_alta);
                                            tvPopularityStatus.setText( R.string.text_popularity_high);
                                            break;
                                        case "high":
                                            imgPopularityLevel.setImageResource(R.drawable.ic_pop_mango);
                                            tvPopularityStatus.setText( R.string.text_popularity_mango);
                                            break;
                                    }
                                }*/

                                /*JSONObject scoreObj = successObj.optJSONObject("user_score");
                                if (scoreObj != null) {
                                    scoreArc.setProgress((int) scoreObj.optDouble("score"));
                                    score_text.setText(String.format("%.1f", scoreObj.optDouble("score")));
                                    String likes = scoreObj.optString("likes", "0");
                                    if (likes.equals("0")) {

                                        like_number.setText("No one has liked you yet");
                                    } else {

                                        like_number.setText(Html.fromHtml("<b>" + likes + "</b> people has liked you"));
                                    }
                                } else {
                                    score_layout.setVisibility(View.GONE);
                                }*/

                        /*        JSONArray photoArray = successObj.getJSONArray("photos");
                                //myImages = new ArrayList<String>();
                                if (myImages != null) {
                                for (int j = 0; j < photoArray.length(); j++) {
                                    JSONObject photoObj = photoArray.getJSONObject(j);
                                    JSONObject photoUrlObj = photoObj.getJSONObject("photo_url");
                                    myImages.add(photoUrlObj.optString("encounter"));
                                }
                                }

                            }
                            myImages.add(null);
                            myImageListAdapter.notifyDataSetChanged();
                            if (myImages.size() > 1)
                                setProfileImage(0);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });*/
    }
 private void actualizarDatosPerfil(){
     //ACTUALIZAR LOS TEXTVIEWS
     if(WaraApp.about_me.equals("") || WaraApp.about_me.equals("null")){
         etAboutMe.setText( getString(R.string.btn_about_me_blank_text));
     }else{
         etAboutMe.setText(""+WaraApp.about_me);
     }
     editCity.setText(WaraApp.city_user);


     tvInfo.setText(""+WaraApp.name);
     tvCredits.setText(""+WaraApp.balas);

     Glide.with(ProfileActivity.this).load(WaraApp.imageUrl).placeholder(R.drawable.profile_placeholder).dontAnimate().into(imgPrincipalPhoto);



 }
 private void updatePopularity(String popularity){


         switch (popularity) {
             case "very_very_low":
                 imgPopularityLevel.setImageResource(R.drawable.ic_pop_bajo);
                 tvPopularityStatus.setText( R.string.text_popularity_low);
                 break;
             case "very_low":
                 imgPopularityLevel.setImageResource(R.drawable.ic_pop_bajo);
                 tvPopularityStatus.setText( R.string.text_popularity_low);
                 break;
             case "low":
                 imgPopularityLevel.setImageResource(R.drawable.ic_pop_media);
                 tvPopularityStatus.setText( R.string.text_popularity_medium);
                 break;
             case "medium":
                 imgPopularityLevel.setImageResource(R.drawable.ic_pop_alta);
                 tvPopularityStatus.setText( R.string.text_popularity_high);
                 break;
             case "high":
                 imgPopularityLevel.setImageResource(R.drawable.ic_pop_mango);
                 tvPopularityStatus.setText( R.string.text_popularity_mango);
                 break;
         }

 }
    public void alertSave(){
        Toast.makeText(this,getString(R.string.alert_profile_save),Toast.LENGTH_SHORT).show();

    /*
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ProfileActivity.this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿ Desea guardar los cambios realizados ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                saveProfile();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
            }
        });
        dialogo1.show();*/

    }
    public void setProfileImage (int position){
        Glide.with(this).load(myImages.get(position)).into(imgPrincipalPhoto);
        Log.d ("PA/setProfileImage" , myImages.get(position));
        WaraApp.imageUrl = myImages.get(position);
        WaraApp.saveSharedPreferences();
    }

    private void saveProfile() {

        if (! HelperMethods.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;
        }

        if (tvInfo.getText().length()<1 || tvInfo.getText()==null){
            Toast.makeText(this, getString(R.string.toast_name_error ), Toast.LENGTH_SHORT).show();
            return;
        }
        if (tvInfo.getText().equals(" ")){
            Toast.makeText(this, "Debe escribir su nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_saving_profile));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Boolean city = false;
        for (int _i = 0; _i < WaraApp.cities.length; _i++) {
            if (editCity.getText().toString().equals(WaraApp.cities[_i])) {
                city = true;
            }
        }
        if (editCity.getText().toString().equals("")) {
            progressDialog.cancel();
            Toast.makeText(ProfileActivity.this, "Debe escoger una ciudad para comenzar a buscar", Toast.LENGTH_SHORT).show();
        }
        else if (!city) {
            progressDialog.cancel();
            Toast.makeText(ProfileActivity.this, "Debe escoger una ciudad del listado mostrado.", Toast.LENGTH_SHORT).show();
        }else {

            Call<UploadProfileApi> call = WSWorker.apiInterface.uploadProfile(

                    WaraApp.id,
                    WaraApp.token,
                    tvInfo.getText().toString(),
                    etAboutMe.getText().toString(),
                    WaraApp.imageUrl,
                    editCity.getText().toString()

            );
            Log.d("-- ProfileActivity", "saveProfile() - url: " + call.request().url());
            call.enqueue(new Callback<UploadProfileApi>() {
                @Override
                public void onResponse(Call<UploadProfileApi> call, Response<UploadProfileApi> response) {

                    try {

                        if (response.body().getStatus().equals("success")) {
                            Log.d("-- ProfileActivity", "saveProfile().onResponse " + response.body().getStatus());

                            WaraApp.city_user = editCity.getText().toString();
                            WaraApp.name = tvInfo.getText().toString();
                            WaraApp.about_me = etAboutMe.getText().toString();
                            WaraApp.imageDrawer = WaraApp.imageUrl;
                            WaraApp.saveSharedPreferences();
                       /* WaraApp.getMy_context().startActivity(
                                new Intent( WaraApp.getMy_context() , ProfileActivity.class)
                        );*/
                            progressDialog.cancel();
                            Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_dialog_updating), Toast.LENGTH_SHORT).show();


                        } else {
                            progressDialog.hide();
                            Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_conx_error), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.hide();
                        Log.d("-- ProfileActivity", "saveProfile().catch() : " + e.getMessage());

                        Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onFailure(Call<UploadProfileApi> call, Throwable t) {

                    Log.d("-- ProfileActivity", "saveProfile().onFailure : " + t.getMessage());
                    progressDialog.hide();
                    Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();
                }
            });
        }
/*
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty(Const.Params.NAME,"" + tvInfo.getText());
        json.addProperty(Const.Params.GENDER,Const.Params.GENDER_MALE);
        json.addProperty(Const.Params.PICTURE, WaraApp.imageUrl);
        json.addProperty(Const.Params.ABOUT_ME, "" + etAboutMe.getText());
        json.addProperty(Const.Params.CITY, "" + (
                (! editCity.getText().equals(R.string.profile_location_text))
                    ? editCity.getText() : "") );
        WaraApp.city = editCity.getText().toString();

        Ion.with(this)
                .load(Endpoints.updateBasicInfoUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressDialog.dismiss();
                        if (result == null){
                            Log.d("PROFILE/saveProfile", "ERROR WEB");
                            return;
                        }
                        try {

                            JSONObject jsonObject = new JSONObject(result.toString());

                            Log.d("PROFILE/getMyProfile", jsonObject.toString(2));
                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                     /*       if (HelperFunctions.IsUserAuthenticated(jsonObject, ProfileActivity.this)) {
                                return;
                            }
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");

                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });*/
    }

   /* private void getFilter() {
        Log.d("getFilter JSON" , "STARTING");

        if (! HelperMethods.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_filter));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        APIInterface apiInterface= APIClient.getClient().create(APIInterface.class) ;

        Call<CityApi> call = apiInterface.doGetCities();
        call.enqueue(new Callback<CityApi>() {

            @Override
            public void onResponse(Call<CityApi> call, Response<CityApi> response) {

                progressDialog.cancel();
                Log.d("TAG",response.code()+"");

                String displayResponse = "";

                CityApi parsed = response.body();
                CityApi.SuccessData data = parsed.getSuccessData();
                ArrayList<CityApi.City> listCity = ( ArrayList<CityApi.City> )data.getCity();
                //settingAutocomplteCities(listCity);

            }

            @Override
            public void onFailure(Call<CityApi> call, Throwable t) {
                progressDialog.cancel();
                Log.d ("act/getCitiesR" , "FAILED: " + t.getMessage()  );

                call.cancel();
            }
        });*/
//        JsonObject json = new JsonObject();
//        json.addProperty(Const.Params.ID, WaraApp.id);
//        json.addProperty(Const.Params.TOKEN, WaraApp.token);
//        Ion.with(this)
//                .load(Endpoints.getFilterUrl)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        progressDialog.cancel();
//                        if (result == null)
//                            return;
//                        try {
//                            JSONObject jsonObject = new JSONObject(result.toString());
//
//                            //Log.d("getFilter JSON" , jsonObject.toString(2));
//
//                            /**
//                             * Logging out user if authentication fails, if user has logged in his/her account
//                             * on some other device as well.
//                             */
//                            if(HelperFunctions.IsUserAuthenticated(jsonObject, ProfileActivity.this)){
//                                return;
//                            }
//
//                            if (jsonObject.optBoolean(Const.Params.STATUS)) {
//                                JSONObject successObj = jsonObject.getJSONObject("success_data");
//                                //  Toast.makeText(FilterActivity.this,obj.optString("success_text"),Toast.LENGTH_SHORT).show();
//
//                                JSONArray cities = successObj.getJSONArray("countriesCities");
//                                //cityList2 = new String [cities.length()];
//
//                                for (int j = 0; j < cities.length(); j++){
//                                    JSONObject city =  cities.getJSONObject(j);
//                                    cityList.add(city.optString("text"));
//                                    //cityList2 [j] = city.optString("text");
//
//                                    //Log.d ("act/getFilter" , "Ciudad: " + city.optString("text"));
//                                }
//
//
//                                /*JSONObject distanceObj = successObj.optJSONObject("perfered_distance");
//                                if (distanceObj != null) {
//                                    distance_seekbar.setMinStartValue(distanceObj.optInt("value")).apply();
//                                    unit = distanceObj.optString("unit");
//                                    distance_text.setText("Show me people within " + distance_seekbar.getSelectedMinValue() + " " + unit);
//                                }
//                                JSONObject locationObject = successObj.optJSONObject("locations");
//                                if (locationObject != null) {
//                                    JSONObject nearbyObj = locationObject.optJSONObject("people_nearby");
//                                    if (nearbyObj != null) {
//                                        location = new Location("");
//                                        location.setLatitude(nearbyObj.optDouble("latitude"));
//                                        location.setLongitude(nearbyObj.optDouble("longitude"));
//                                        locationName = nearbyObj.optString("location_name");
//                                        if (locationName == null || locationName.equals("null"))
//                                            locationName = "";
//                                    }
//                                }
//                                updateLocationUi();*/
//
//
//
//                            }else {
//                                JSONObject obj = jsonObject.getJSONObject("error_data");
//                                //TODO Este texto está en Ingles
//                                Toast.makeText(ProfileActivity.this, obj.optString("error_text"), Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
   // }

    private void setUpImageRecyclerView() {


        myImageListAdapter = new MyImageListAdapter(myImages, this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        my_image_list.setLayoutManager(mLayoutManager);
        my_image_list.setItemAnimator(new DefaultItemAnimator());
        my_image_list.setAdapter(myImageListAdapter);




    }

    @Override
    public void onHandleSelection(int position, String text) {
        if (position == 0) {
            getImageFromGallery();
        }

    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    private void createDialogSaveImageChoice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a option")

                .setSingleChoiceItems(packageList.toArray(new String[packageList.size()]), 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayCount = which;
                    }
                })

                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //payByPayPalForSuper(packageDetailList.get(arrayCount).getAmount());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    File imageFile = persistImage(bitmap, String.valueOf(System.currentTimeMillis()));
                    sendToServer(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //llPreviewPhoto.setVisibility(View.VISIBLE);
                //llTakePhoto.setVisibility(View.GONE);
                //imgPreview.setImageURI(resultUri);
                File imageFile = new File(resultUri.getPath());
                sendToServer(imageFile);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == REQUEST_CODE_PAYPAL) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));

                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.
                        String paymentId = confirm.getProofOfPayment().getPaymentId();
                        addMoney(paymentId, "paypal", confirm.getPayment().getAmountAsLocalizedString());

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }

        if (requestCode == REQUEST_CODE_PAYPAL_1) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));

                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.
                        String paymentId = confirm.getProofOfPayment().getPaymentId();
                        addSuperPower(paymentId, "paypal", confirm.getPayment().getAmountAsLocalizedString());

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }

        /**
         * Request for credit update from the popularity activity or refill credit activity.
         */
        if(requestCode == CREDIT_UPDATE_REQUEST || requestCode == REFILL_CREDIT_REQUEST){
            if(resultCode == RESULT_OK){
                tvCredits.setText("" + WaraApp.balas);
            }
        }
    }

    private void addMoney(String paymentId, String paypal, String amount) {
        final ProgressDialog progress = new ProgressDialog(ProfileActivity.this);
        progress.setTitle("Adding Money");
        progress.setMessage("Please wait while we are adding money");
        progress.show();

        final JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("transaction_id", paymentId);
        json.addProperty("package_id", packageDetailList.get(arrayCount).getId());
        json.addProperty("amount", packageDetailList.get(arrayCount).getAmount());

        Ion.with(this)
                .load(Endpoints.buyCredits)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        progress.dismiss();
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, ProfileActivity.this)){
                                return;
                            }

                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                String credit = jsonObject.optString("user_credit_balance");
                                tvCredits.setText(credit);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void addSuperPower(String paymentId, String paypal, String amount) {
        final JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("transaction_id", paymentId);
        json.addProperty("package_id", packageDetailList.get(arrayCount).getId());
        json.addProperty("amount", packageDetailList.get(arrayCount).getAmount());

        Ion.with(this)
                .load(Endpoints.buySuperPowerUrl)
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
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, ProfileActivity.this)){
                                return;
                            }

                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                /*active_status.setText("Active");
                                active_status.setTextColor(getResources().getColor(R.color.white));*/
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void sendToServer(File imageFile) {
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, "Uploading..");
        progressDialog.show();
        List<Part> files = new ArrayList();
        files.add(new FilePart("photos[]", imageFile));
        Ion.with(this)
                .load(Endpoints.uploadOtherPhotosUrl)
                .setMultipartParameter(Const.Params.ID, WaraApp.id)
                .setMultipartParameter(Const.Params.TOKEN, WaraApp.token)
                .addMultipartParts(files)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            Log.d("PROFILE/sendToServer" , jsonObject.toString(2));

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, ProfileActivity.this)){
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");
                                JSONArray photoArray = successObj.optJSONArray("photo_urls");
                                //myImages.clear();
                                /*for (int i = 0; i < photoArray.length(); i++) {
                                    JSONObject photoObj = photoArray.getJSONObject(i);
                                    myImages.add(photoObj.optString("encounter"));
                                }*/
                                JSONObject photoObj = photoArray.getJSONObject(0);
                                //myImages.remove(myImages.size());
                                myImages.add(myImages.size() -1 ,photoObj.optString("encounter"));
                                myImageListAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        return imageFile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.credits_layout:
               //getCreditPackages();
                //startActivityForResult(new Intent(ProfileActivity.this, RefillCreditsActivity.class), REFILL_CREDIT_REQUEST);
                break;
           /* case R.id.super_layout:
                //getSuperPowerPackages();
                startActivity(new Intent(ProfileActivity.this, SuperPowerActivity.class));
                break;*/
            case R.id.popularity_layout:
                //start popularity activity to provide options to increase the popularity.
                //startActivityForResult(new Intent(ProfileActivity.this, PopularityActivity.class), CREDIT_UPDATE_REQUEST);
                break;
        }
    }

    private void getCreditPackages() {

        final ProgressDialog progress = new ProgressDialog(ProfileActivity.this);
        progress.setTitle("Getting Credit Packages");
        progress.setMessage("Wait while getting credit packages from server");
        progress.show();

        JsonObject json = new JsonObject();
        json.addProperty("type", "credit");

        Ion.with(this)
                .load(Endpoints.getCreditPackages)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        progress.dismiss();

                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                JSONArray packageArray = jsonObject.getJSONArray("packages");
                                packageList = new ArrayList<>();
                                packageDetailList = new ArrayList<>();
                                for (int i = 0; i < packageArray.length(); i++) {
                                    JSONObject packObj = packageArray.getJSONObject(i);
                                    PackageDetail packageDetail = new PackageDetail();
                                    packageDetail.setId(packObj.optString("package_id"));
                                    packageDetail.setAmount(packObj.optString("amount"));
                                    packageDetail.setCredits(packObj.optString("credits"));
                                    packageDetail.setCurrency(packObj.optString("currency"));
                                    packageDetailList.add(packageDetail);
                                    packageList.add(packageDetail.getCredits() + " credits - " + packageDetail.getCurrency() + " " + packageDetail.getAmount());
                                }
                                createDialogSingleChoice(packageList, packageDetailList);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void getSuperPowerPackages() {
        final ProgressDialog progress = new ProgressDialog(ProfileActivity.this);
        progress.setTitle("Wait while loading");
        progress.setMessage("We are getting list of packages for you.");
        progress.show();

        JsonObject json = new JsonObject();
        json.addProperty("type", "superpower");

        Ion.with(this)
                .load(Endpoints.getCreditPackages)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        progress.dismiss();

                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                JSONArray packageArray = jsonObject.getJSONArray("packages");
                                packageList = new ArrayList<>();
                                packageDetailList = new ArrayList<>();
                                for (int i = 0; i < packageArray.length(); i++) {
                                    JSONObject packObj = packageArray.getJSONObject(i);
                                    PackageDetail packageDetail = new PackageDetail();
                                    packageDetail.setId(packObj.optString("package_id"));
                                    packageDetail.setAmount(packObj.optString("amount"));
                                    packageDetail.setPackname_name(packObj.optString("package_name"));
                                    packageDetail.setCurrency(packObj.optString("currency"));
                                    packageDetailList.add(packageDetail);
                                    packageList.add(packageDetail.getPackname_name() + "  - " + packageDetail.getCurrency() + " " + packageDetail.getAmount());
                                }
                                createDialogSuperSingleChoice(packageList, packageDetailList);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void createDialogSingleChoice(ArrayList<String> packageList, final ArrayList<PackageDetail> packageDetailList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a Package")

                .setSingleChoiceItems(packageList.toArray(new String[packageList.size()]), 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayCount = which;
                    }
                })

                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //payByPayPal(packageDetailList.get(arrayCount).getAmount());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    private void createDialogSuperSingleChoice(ArrayList<String> packageList, final ArrayList<PackageDetail> packageDetailList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a Package")

                .setSingleChoiceItems(packageList.toArray(new String[packageList.size()]), 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayCount = which;
                    }
                })

                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //payByPayPalForSuper(packageDetailList.get(arrayCount).getAmount());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    /**
     * This function is to update the about me status of the current logged in user.
     * @param newUpdate is the new status.
     */
    public void UpdateAboutMeStatus(String newUpdate){
        TextView abt_me_text = (TextView) findViewById(R.id.tv_about_me);
        abt_me_text.setText(newUpdate);
    }
}
