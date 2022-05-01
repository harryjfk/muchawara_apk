package cu.inmobile.wara.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.skyfishjy.library.RippleBackground;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import cu.inmobile.wara.RoomModels.User;
import cu.inmobile.wara.Activities.ChatActivity;
import cu.inmobile.wara.Activities.DisplayActivity;
import cu.inmobile.wara.Activities.LoginActivity;
import cu.inmobile.wara.Activities.ProfileViewActivity;
import cu.inmobile.wara.Adapters.CardsAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.NotifyProfileRecieved;
import cu.inmobile.wara.Models.userDetail;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.ErrorDataGetBullets;
import cu.inmobile.wara.Pojo.GetBulletsApi;
import cu.inmobile.wara.Pojo.Photo;
import cu.inmobile.wara.Pojo.ProfileApi;
import cu.inmobile.wara.Pojo.SendLikeStatusApi;
import cu.inmobile.wara.Pojo.SuccessDataGetBullets;
import cu.inmobile.wara.Pojo.SuccessDataProfile;
import cu.inmobile.wara.Pojo.SuccessDataSendLike;

import cu.inmobile.wara.R;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Utils.SharedPreferencesUtils;

import com.skyfishjy.library.RippleBackground;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.arjsna.swipecardlib.SwipeCardView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DisplayFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int REQUEST_LOCATION_PERMISSION = 5674;
    private final int PROFILE_VIEW_CODE = 1276;

    private ArrayList<userDetail> userDetailArrayList;

    private ImageView imgCenterImage, imgUser1, imgUser2;
    private CardsAdapter cardsAdapter;
    private SwipeCardView swipeCardView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LinearLayout llButtons, llNoCredits, llNoEncounters, llWara;
    private TextView tvNoMatchText, tvTimeRecharge;
    private ImageButton ibLike, ibDislike;
    private RippleBackground loading_view;
    private FrameLayout housing_frame;
    private String TAG="--DisplayFragment";
    private View view;
    private ViewGroup parent_linear_layout;
    private Button btnFilter, btnReload, btnChat, btnContinue, btn;

    private int[] status = {1/*like*/, 2/*dislike*/, 3/*super_like*/};

    private int encounterLeft;

    private boolean isDataAvailable = false, isDownloadingData = false;
    private boolean encounterQuotaOver = false;

    private boolean fetchingNearbyPeople;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WaraApp.setCurrentFragment(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fetchingNearbyPeople = true;

        view = inflater.inflate(R.layout.fragment_display, container, false);

        iniVariables();
        settingInterface();
        clearScreenForEncounters();
        fetchNearbyPeople(0);

        return view;
    }

    private void iniVariables() {

        encounterLeft = -1;
    }

    private void settingInterface() {

        parent_linear_layout = (ViewGroup) view.findViewById(R.id.parent);
        userDetailArrayList = new ArrayList<>();

        imgCenterImage = (ImageView) view.findViewById(R.id.imgPhotoUser);
        imgUser1 = (ImageView) view.findViewById(R.id.imgPhotoUser1);
        imgUser2 = (ImageView) view.findViewById(R.id.imgPhotoUser2);

        swipeCardView = (SwipeCardView) view.findViewById(R.id.frame);
        llButtons = (LinearLayout) view.findViewById(R.id.button_layout);
        llNoEncounters = (LinearLayout) view.findViewById(R.id.ll_not_found);
        llNoCredits = (LinearLayout) view.findViewById(R.id.ll_not_credits);
        llWara = (LinearLayout) view.findViewById(R.id.ll_wara);
        tvNoMatchText = (TextView) view.findViewById(R.id.no_match_text);
        tvTimeRecharge = (TextView) view.findViewById(R.id.tv_time_to_recharge);
        ibLike = (ImageButton) view.findViewById(R.id.ib_like_button);
        ibDislike = (ImageButton) view.findViewById(R.id.ib_dislike_button);

        ibLike.setOnClickListener(this);
        ibDislike.setOnClickListener(this);

        loading_view = (RippleBackground) view.findViewById(R.id.loading_view);
        loading_view.startRippleAnimation();

        housing_frame = (FrameLayout) view.findViewById(R.id.housing_frame);

        setUpSwipableCard(swipeCardView);

        llButtons.setVisibility(View.GONE);

        btnChat = (Button) view.findViewById(R.id.btn_chat);
        btnReload = (Button) view.findViewById(R.id.btn_reload);
        btnContinue = (Button) view.findViewById(R.id.btn_keep_looking);
        btnFilter = (Button) view.findViewById(R.id.btn_filter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DisplayActivity) getActivity()).changeFilterSettings();
            }
        });

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO RECARGAR ESTATUS COGER LAS BALAS... EL BOTON FUNCIONARA SOLO CONECTADO Y CUANDO SE CUMPLA EL TIEMPO QUE TENDRA EL SERVICIO

                getBullets();
            }
        });


        /*if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/
        //getLocation(LocationManager.NETWORK_PROVIDER);

    }

    private void clearScreenForEncounters() {
        llNoCredits.setVisibility(View.INVISIBLE);
        llNoEncounters.setVisibility(View.INVISIBLE);
        llWara.setVisibility(View.INVISIBLE);
    }


    //TODO REVISAR SI ESTO puede tener que ver con que no se pinte la imagen del centro
    @Subscribe

    public void onProfileNotifyRecieved(NotifyProfileRecieved event) {

        Glide.with(this).load(WaraApp.imageUrl).placeholder(R.drawable.user_profile).dontAnimate().into(imgCenterImage);
    }

    private void setUpSwipableCard(final SwipeCardView swipeCardView) {

        cardsAdapter = new CardsAdapter(getActivity(), userDetailArrayList);
        swipeCardView.setAdapter(cardsAdapter);

        swipeCardView.setOnItemClickListener(new SwipeCardView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Log.d("DisplayFragment" , "setUpSwipableCard CLIKING ! ");
                userDetail user = (userDetail) dataObject;
                Intent intent = new Intent(getActivity(), ProfileViewActivity.class);
                intent.putExtra("target_id", user.getId());
                intent.putExtra("from", "DisplayFragment");
                startActivityForResult(intent, PROFILE_VIEW_CODE);
            }
        });

        swipeCardView.setFlingListener(new SwipeCardView.OnCardFlingListener() {
            @Override
            public void onCardExitLeft(Object dataObject) {

                Log.d("onCardExitLeft", "Items: " + status[1]);
                if (!WaraApp.isBalasAvailable()) {
                    showQoutaOverDialog();
                    return;
                }
                else {
                    userDetail user = (userDetail) dataObject;
                    // lastUserDetail = user;
                    sendLikeStatus(user.getId(), status[1]);
                }
            }

            @Override
            public void onCardExitRight(Object dataObject) {
                Log.d("onCardExitRight", "Items: " + status[0]);

                // lastUserDetail = user;
                if (!WaraApp.isBalasAvailable()) {
                    showQoutaOverDialog();
                    return;
                }
                else{
                userDetail user = (userDetail) dataObject;
                sendLikeStatus(user.getId(), status[0]);
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                Log.d("onAdapterAboutToEmpty", "Items: " + itemsInAdapter);

                if (encounterLeft > 0 && !userDetailArrayList.isEmpty() && itemsInAdapter == 1) {
                    if (!isDownloadingData) {
                        fetchNearbyPeople(itemsInAdapter);
                    }
                }
                if (itemsInAdapter == 0 && encounterLeft == 0) {
                    Log.d("onAdapterAboutToEmpty", "EMPTY");
                    showNoEncounterScreen();
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = swipeCardView.getSelectedView();
                //ImageView like = (ImageView) view.findViewById(R.id.like);
                //ImageView dislike = (ImageView) view.findViewById(R.id.dislike);
                if (scrollProgressPercent > 0) {
                    int transparency = (int) (scrollProgressPercent * 255);
                    //like.setImageAlpha(transparency);
                }
                if (scrollProgressPercent < 0) {
                    int transparency = (int) (scrollProgressPercent * -255);
                    //dislike.setImageAlpha(transparency);
                }

                if (scrollProgressPercent == 0) {
                    //like.setImageAlpha(0);
                    //dislike.setImageAlpha(0);
                }

            }

            @Override
            public void onCardExitTop(Object dataObject) {
               /* userDetail user = (userDetail) dataObject;
                lastUserDetail = user;
                sendLikeStatus(user.getId(), status[2]);
                current_position++;*/
            }

            @Override
            public void onCardExitBottom(Object dataObject) {

            }
        });
    }

    private void showMatchDialog(final String target_id, final String match_picture) {


        llWara.setVisibility(View.VISIBLE);
        swipeCardView.setVisibility(View.GONE);



        Glide.with(this).load(WaraApp.imageUrl).placeholder(R.drawable.user_profile).dontAnimate().into(imgUser1);
        Glide.with(this).load(match_picture).placeholder(R.drawable.user_profile).dontAnimate().into(imgUser2);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llWara.setVisibility(View.GONE);
                swipeCardView.setVisibility(View.VISIBLE);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO Pensar como mandar solo el user id al ChatActivity...

                Intent intent = new Intent(WaraApp.getMy_context() , ChatActivity.class);
                intent.putExtra("target_id", target_id);
                //intent.putExtra("receiverImageUrl", match_picture);
                //intent.putExtra("receiverName", match_name);
                //intent.putExtra("contact_id",contact_id);
                WaraApp.getMy_context().startActivity(intent);

            }
        });


        //TODO PROGRAMAR LOS BOTONES AKI ES DONDE TENGO EL match ID


        /*final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.match_dialog);

        ImageView myImage = (ImageView) dialog.findViewById(R.id.my_image);
        ImageView userImage = (ImageView) dialog.findViewById(R.id.user_image);
        TextView match_text = (TextView) dialog.findViewById(R.id.match_text);
        Button send_message, keep_swiping;
        send_message = (Button) dialog.findViewById(R.id.send_message);
        keep_swiping = (Button) dialog.findViewById(R.id.keep_swiping);
        Glide.with(this).load(WaraApp.imageUrl).placeholder(R.drawable.user_profile).dontAnimate().into(myImage);
        Glide.with(this).load(match_picture).placeholder(R.drawable.user_profile).dontAnimate().into(userImage);
        match_text.setText("You and " + match_name + " have liked each other");
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("receiverId", match_id);
                intent.putExtra("receiverImageUrl", match_picture);
                intent.putExtra("receiverName", match_name);
                intent.putExtra("contact_id", contact_id);
                startActivity(intent);
            }
        });
        keep_swiping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();*/
    }
    private void sendLikeStatus(final String id, int status) {
        if (!WaraApp.isBalasAvailable()) {
            showQoutaOverDialog();
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
                id,
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
                        ((DisplayActivity)getActivity()).updateBulletsView();


                        Log.d(TAG, "sendLikeStatus() -match_found: " + successData.getMatchFound());
                        if (successData.getMatchFound()) {
                            Log.d("sendLikeStatus", "match okk");

                            User userNew = new User(
                                    id,
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
                            showMatchDialog(id,successData.getUser().getProfilePics().getEncounter());

                        }

                    } else {
                        Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_error_web), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("PROFILE/getMyProfile", "getMyProfile() .catch(): " + e.getMessage());
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

    private void sendLikeStatus_old(final String id, int status) {

        Log.d("sendLikeStatus", status + "" + "balas: " + WaraApp.balas);

        if (!WaraApp.isBalasAvailable()) {
            showQoutaOverDialog();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("encounter_id", id);
        if (status == 1)
            json.addProperty("like", "_like");
        if (status == 2)
            json.addProperty("like", "_dislike");
        final DisplayFragment c = this;
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
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())) {
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                final JSONObject successObj = jsonObject.getJSONObject("success_data");
                                WaraApp.balas = successObj.optInt("credits");
                                WaraApp.saveSharedPreferences();
                                ((DisplayActivity)getActivity()).updateBulletsView();
                                if (successObj.optBoolean("match_found")) {
                                    Log.d("sendLikeStatus", jsonObject.toString(2) + "");
                                    int match_id = successObj.optJSONObject("user").optInt("id");
                                    JSONObject userObj = successObj.optJSONObject("user");
                                    JsonObject json = new JsonObject();
                                    json.addProperty("id", WaraApp.id);
                                    json.addProperty("dest_id", match_id);
                                    showMatchDialog(id,userObj.optString("profile_pic_url"));

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
//                                                        showMatchDialog(userObj.optString("name"), userObj.optString("profile_pic_url"), userObj.optString("chat_username"), successObj.optString("contact_id"));
//
//                                                    } catch (JSONException ew) {
//
//                                                    }
//
//
//                                                }
//                                            });
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        //mGoogleApiClient.connect();
        //BusProvider.getInstance().register(this);
        //
        // fetchNearbyPeople();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        //mGoogleApiClient.disconnect();
        super.onDestroy();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*if (mLastLocation == null){
            getLocation();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                //
                // fetchNearbyPeople();
                updateLocation();
            }
        }
    }

    /**
     * Gets the current location of the user based on the provided network provider
     * @param provider
     * @return
     */
    /**
     * private Location getLocation(String provider){
     * long MIN_DISTANCE_FOR_UPDATE = 10;
     * long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
     * final LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
     * if(mLocationManager.isProviderEnabled(provider)){
     * try{
     * mLocationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, new LocationListener() {
     *
     * @Override public void onLocationChanged(Location location) {
     * mLastLocation = location;
     * fetchNearbyPeople();
     * updateLocation();
     * mLocationManager.removeUpdates(this);
     * }
     * @Override public void onStatusChanged(String provider, int status, Bundle extras) {
     * <p>
     * }
     * @Override public void onProviderEnabled(String provider) {
     * <p>
     * }
     * @Override public void onProviderDisabled(String provider) {
     * <p>
     * }
     * });
     * }catch(SecurityException e){
     * <p>
     * }
     * }
     * return mLastLocation;
     * }
     */

    private void updateLocation() {
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.ID, WaraApp.token);
        json.addProperty("latitude", mLastLocation.getLatitude());
        json.addProperty("longitude", mLastLocation.getLongitude());

        Ion.with(this)
                .load(Endpoints.updateLocation)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null)
                            return;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getLocation();
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //getLocation();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void fetchNearbyPeople(int pag) {

        if (!HelperMethods.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }

        fetchingNearbyPeople = true;

        if (!loading_view.isRippleAnimationRunning())
            loading_view.startRippleAnimation();

        isDownloadingData = true;
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty(Const.Params.ENCOUNTER_LEFT, pag);
        /*if (WaraApp.locationPreference == 0) {
            json.addProperty(Const.Params.LATITUDE, mLastLocation.getLatitude());
            json.addProperty(Const.Params.LONGITUDE, mLastLocation.getLongitude());
        }*/


        Ion.with(this)
                .load(Endpoints.getEncountersUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        isDownloadingData = false;
                        loading_view.stopRippleAnimation();
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            Log.d("DF/fetchNearbyPeople", jsonObject.toString(2));

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())) {
                                return;
                            }
                            Log.d("fetchNearbyPeople", "AFTER AUTH");

                            if (jsonObject.getString(Const.Params.STATUS).equals("success")) {
                                Log.d("fetchNearbyPeople", "SUCCESS");
                                JSONObject successObject = jsonObject.getJSONObject("success_data");

                                WaraApp.balas = successObject.optInt("credits_left");

                                //TODO aki nunca debe entrar, cuando el credito sea 0 la respuesta será de tipo "error"
                                /*if (successObject.optInt("credits_left") == 0) {
                                    Log.d("fetchNearbyPeople" , "No credit");
                                    showQoutaOverDialog();
                                }*/
                                llButtons.setVisibility(View.VISIBLE);

                                tvNoMatchText.setVisibility(View.GONE);

                                encounterLeft = successObject.optInt("element_left");

                                JSONArray jsonArray = successObject.optJSONArray("encounters");
                                if (jsonArray != null) {
                                    if (jsonArray.length() > 0) {
                                        isDataAvailable = true;
                                    } else {
                                        isDataAvailable = false;
                                        //llNoEncounters.setVisibility(View.VISIBLE);
                                        //tvNoMatchText.setVisibility(View.VISIBLE);
                                        //llButtons.setVisibility(View.GONE);
                                    }
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject userObject = jsonArray.getJSONObject(i);
                                        userDetail detail = new userDetail();
                                        detail.setId(userObject.optString("id"));
                                        detail.setName(userObject.optString("name"));
                                        detail.setAge(userObject.optString("age"));
                                        detail.setAboutMe(userObject.optString("aboutme"));
                                        JSONObject prof_pic_obj = userObject.optJSONObject("profile_picture_url");
                                        if (prof_pic_obj != null) {
                                            detail.setPicture(prof_pic_obj.optString("encounter"));
                                        }
                                        //  detail.setDistance(userObject.optString("distance"));
                                        userDetailArrayList.add(detail);
                                    }
                                    //  rippleBackground.stopRippleAnimation();
                                    //cardsAdapter.clear();
                                    cardsAdapter.notifyDataSetChanged();
                                    fetchingNearbyPeople = false;
                                    llButtons.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.d("fetchNearbyPeople", "status error");

                                if (jsonObject.getString(Const.Params.ERROR_TYPE).equals("Bullet")) {
                                    showQoutaOverDialog();
                                } else if (jsonObject.getString(Const.Params.ERROR_TYPE).equals("Encounter")) {
                                    showNoEncounterScreen();
                                }
                                //  rippleBackground.stopRippleAnimation();
                                //tvNoMatchText.setVisibility(View.VISIBLE);
                                //llButtons.setVisibility(View.GONE);
                                //Toast.makeText(getActivity(), R.string.toast_conx_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            Log.d("fetchNearbyPeople/catch", "status error");
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void getBullets() {
        if (!HelperMethods.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!loading_view.isRippleAnimationRunning())
            loading_view.startRippleAnimation();

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(getActivity(), getString(R.string.text_credit_recharge));
        progressDialog.setCancelable(true);
        progressDialog.show();

        Call<GetBulletsApi> call = WSWorker.apiInterface.getBullets( WaraApp.id , WaraApp.token);
        Log.d("DisplayFragment", "getBullets() -url: " + call.request().url());

        call.enqueue(new Callback<GetBulletsApi>() {
            @Override
            public void onResponse(Call<GetBulletsApi> call, Response<GetBulletsApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        SuccessDataGetBullets successData = response.body().getSuccessDataBullets();
                        Log.d("DisplayFragment", "getBullets() -successData: " + response.toString());

                        WaraApp.balas = successData.getBullets();
                        WaraApp.saveSharedPreferences();
                        ((DisplayActivity)getActivity()).updateBulletsView();
                        WaraApp.getMy_context().startActivity(
                                new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                        );


                        progressDialog.cancel();
                        Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_get_bullets) , Toast.LENGTH_SHORT).show();


                    }
                    else{
                        progressDialog.cancel();
                        Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_get_bullets_error) , Toast.LENGTH_SHORT).show();

                    }


                }
                catch (Exception e){
                    Log.d("PROFILE/getMyProfile", "getMyProfile() .catch(): "  + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context() , e.getMessage() , Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetBulletsApi> call, Throwable t) {
                Log.d("PROFILE/ProfileApi","getMyProfile().onFailure : " + t.getMessage());

                Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_bad_answer) , Toast.LENGTH_SHORT).show();

            }



        });

    }
    private void getBullets_old() {

        if (!HelperMethods.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!loading_view.isRippleAnimationRunning())
            loading_view.startRippleAnimation();

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(getActivity(), getString(R.string.text_credit_recharge));
        progressDialog.setCancelable(true);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        /*if (WaraApp.locationPreference == 0) {
            json.addProperty(Const.Params.LATITUDE, mLastLocation.getLatitude());
            json.addProperty(Const.Params.LONGITUDE, mLastLocation.getLongitude());
        }*/


        Ion.with(this)
                .load(Endpoints.getBullets)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        progressDialog.dismiss();
                        llNoCredits.setVisibility(View.INVISIBLE);

                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            Log.d("DF/getBullets", jsonObject.toString(2));

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())) {
                                return;
                            }
                            if (jsonObject.getString(Const.Params.STATUS).equals("success")) {
                                Log.d("getBullets", "SUCCESS");
                                JSONObject successObject = jsonObject.getJSONObject("success_data");

                                WaraApp.balas = successObject.optInt("bullets");
                                WaraApp.saveSharedPreferences();


                            } else {
                                Log.d("getBullets", "status error");

                                JSONObject errorObject = jsonObject.getJSONObject("error_data");

                                Toast.makeText(getActivity(), getString(R.string.toast_credit_error), Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e1) {
                            Log.d("getBullets/catch", "JSON error");
                            e1.printStackTrace();
                            return;
                        }
                    }
                });
        return;
    }

    private void showNoEncounterScreen() {
        Log.d("showNoEncounterScreen", "SHOW");
        llNoEncounters.setVisibility(View.VISIBLE);
    }

    private String calcRechargeTime() {

        return "Próxima recarga:\n06:00 AM";
    }

    private void showQoutaOverDialog() {

        Log.d("showQoutaOverDialog", calcRechargeTime());

        llNoCredits.setVisibility(View.VISIBLE);
        //tvTimeRecharge.setText(calcRechargeTime());

        //BTN RELOAD


        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.likes_over, null);
        dialogBuilder.setView(dialogView);

        final TextView timeLeftText = (TextView) dialogView.findViewById(R.id.time_left);

        //Getting the time left till next likes become active
        android.text.format.Time time = new android.text.format.Time();
        time.setToNow();
        long currentTime = time.hour*60*60 + time.minute*60 + time.second;
        long remmainingTime = (24*60*60) - currentTime;

        Log.d("C TIME" , currentTime + "");
        Log.d("R TIME" , remmainingTime + "");
        Log.d("CALC", "" + 24*60*60);

        //Countdown till like feature becomes active
        final CountDownTimer timer = new CountDownTimer(remmainingTime*1000 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftText.setText(HelperFunctions.ConvertSecondsToHMmSs(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {

            }
        };


        final AlertDialog alertDialog = dialogBuilder.create();
        LinearLayout purchase_super_power = (LinearLayout) dialogView.findViewById(R.id.purchase_superpower);
        purchase_super_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                startActivity(new Intent(getActivity(), ProfileActivity.class));

                //stopping the timer
                timer.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
        timer.start();*/
    }

    @Override
    public void onClick(View view) {
        View view1 = swipeCardView.getSelectedView();
        switch (view.getId()) {
            case R.id.ib_like_button:
                //Still fetching results
                //ImageView like = (ImageView) view1.findViewById(R.id.like);
                //like.setImageAlpha(255);
                swipeCardView.throwRight();

                break;
            case R.id.ib_dislike_button:
                swipeCardView.throwLeft();
                break;
        }
    }

    private void getBoostCredits() {
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);

        Ion.with(this)
                .load(Endpoints.checkBoost)
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
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())) {
                                return;
                            }

                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                if (jsonObject.optBoolean("profile_boosted")) {
                                    //Toast.makeText(getActivity(), "Your profile is already boosted.", Toast.LENGTH_LONG).show();
                                } else {
                                    if (jsonObject.optInt("user_credit_balance") >= jsonObject.optInt("boost_credits")) {
                                        showBoostConfirmationDialog(jsonObject.optInt("boost_credits"));
                                    } else {
                                        //
                                        // Toast.makeText(getActivity(), "You do not have enough credit balance to purchase boost", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void showBoostConfirmationDialog(int credit) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Activate Boost")
                .setMessage("Activate Boost for " + credit + " credit(s).")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activateBoost();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void activateBoost() {
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(getActivity(), "Activating boost...");
        progressDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.ID, WaraApp.token);

        Ion.with(getActivity())
                .load(Endpoints.activateBoost)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressDialog.dismiss();
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())) {
                                return;
                            }

                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");
                                Toast.makeText(getActivity(), successObj.optString("success_text"), Toast.LENGTH_SHORT).show();
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
        if (requestCode == PROFILE_VIEW_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final View view1 = swipeCardView.getSelectedView();
                Handler handler = new Handler();
                String status = data.getStringExtra("action");
                if (status.equals("like")) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //ImageView like = (ImageView) view1.findViewById(R.id.like);
                            //like.setImageAlpha(255);
                            swipeCardView.throwRight();
                        }
                    }, 500);

                } else if (status.equals("dislike")) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //ImageView dislike = (ImageView) view1.findViewById(R.id.dislike);
                            //dislike.setImageAlpha(255);
                            swipeCardView.throwLeft();
                        }
                    }, 500);

                }
            }
        }

    }
    /*
    @Subscribe
    public void onFilterChangeListener(NotifyFilterChange event) {
        userDetailArrayList.clear();
        cardsAdapter.notifyDataSetChanged();
        button_layout.setVisibility(View.GONE);

        userDetailArrayList = new ArrayList<>();
        housing_frame.removeView(swipeCardView);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.swipe_layout, parent_linear_layout, false);
        SwipeCardView swipeCardView = new SwipeCardView(getActivity());
        swipeCardView.setMaxVisible(1);

        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        //   newParams.addRule(RelativeLayout.ALIGN_BOTTOM,button_layout.getId());
        // parent_view.addView(swipeCardView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        housing_frame.addView(swipeCardView, newParams);

        setUpSwipableCard(swipeCardView);
        fetchNearbyPeople();
    }
    */
}
