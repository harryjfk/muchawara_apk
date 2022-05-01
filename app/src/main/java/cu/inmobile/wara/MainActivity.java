package cu.inmobile.wara;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import cu.inmobile.wara.Activities.DisplayActivity;
import cu.inmobile.wara.Activities.LoginActivity;
import cu.inmobile.wara.Activities.RegisterActivity;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MAIN ACTIVITY";

    private CallbackManager callbackManager;
    private LinearLayout llLoginButtons;
    private ImageButton fb_sign_in;
    private Button btn_register;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Fabric.with(this, new Crashlytics());

        // If there a TOKEN in the SharedPreferences open DisplayActivity
        //String session_token = (String) SharedPreferencesUtils.getParam(this,SharedPreferencesUtils.SESSION_TOKEN,"");


        settingInterface();
        registerCallBack();

        Log.d(TAG, "Activity created");
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!WaraApp.token.equals("") &&  !WaraApp.token.equals(null) ){
                    Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d(TAG, "" + WaraApp.token);
                    llLoginButtons.setVisibility(View.VISIBLE);
                }
            }
        }, 2000);
    }

    private void settingInterface (){

        llLoginButtons = (LinearLayout) findViewById(R.id.ll_action_buttons);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        fb_sign_in = (ImageButton) findViewById(R.id.btn_fb_sign_in);
        fb_sign_in.setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    private void registerCallBack() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //TODO ?? App code
                        Log.d ("FB/registerCallBack" , "onSuccess");
                        String token = loginResult.getAccessToken().getToken();
                        callLoginApi(token);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d ("FB/registerCallBack" , "onCancel");



                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d ("FB/registerCallBack" , "onError");
                        Log.d ("FB/registerCallBack" , exception.getMessage());

                    }
                });
    }


    private void callLoginApi(String token) {

        if (! HelperMethods.isNetworkAvailable(this)){

            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;

        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_loading_fb));
        progressDialog.setCancelable(true);
        progressDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("auth_token", token);

        Ion.with(this)
                .load(Endpoints.facebookUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressDialog.hide();
                        if (result == null){
                            Log.d ("act/callLoginApi" , "ERROR WEB");
                            Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_error_web ), Toast.LENGTH_SHORT).show();

                            return;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            Log.d ("act/callLoginApi" , jsonObject.toString(2));
                            if (jsonObject.optBoolean(Const.Params.STATUS)){
                                JSONObject successObject = jsonObject.getJSONObject("success_data");
                                //SharedPreferencesUtils.setParam(MainActivity.this, SharedPreferencesUtils.SESSION_TOKEN, );
                                //SharedPreferencesUtils.setParam(MainActivity.this, SharedPreferencesUtils.USER_ID, );
                                WaraApp.id = successObject.optString("user_id");
                                WaraApp.token = successObject.optString("access_token");
                                WaraApp.saveSharedPreferences();

                                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fb_sign_in:

                LoginManager.getInstance().logOut();
                Log.d(TAG, "OnClick FB");
                LoginManager.getInstance()
                        .logInWithReadPermissions
                                (this, Arrays.asList(
                                        "public_profile",
                                        "email",
                                        "user_birthday",
                                        "user_likes",
                                        "user_location",
                                        "user_hometown",
                                        "user_friends"));
                break;
            case R.id.btn_register:
                Log.d(TAG, "OnClick REGISTER");
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                Log.d(TAG, "OnClick LOGIN");

                //WaraApp.showNotification(Const.Params.NOTIFICATION_MES);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
    }
}
