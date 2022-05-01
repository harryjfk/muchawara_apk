package cu.inmobile.wara.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.LoginApi;
import cu.inmobile.wara.Pojo.SuccessData;
import cu.inmobile.wara.R;
import cu.inmobile.wara.Utils.HelperMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anurag on 4/11/2017.
 */
public class LoginActivity extends AppCompatActivity {

    WaraApp waraApp;

    private EditText email_et, password_et;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        waraApp = (WaraApp) getApplication();
        settingInterface();
    }

    private void settingInterface() {

        //TODO No voy a utilizar el TOOLBAR aqui

        email_et = (EditText) findViewById(R.id.email_et);
        password_et = (EditText) findViewById(R.id.password_et);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForEmpty()) {

                    login();
                }
            }
        });
    }

    private void login() {


        if (!HelperMethods.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_login));
        progressDialog.setCancelable(true);
        progressDialog.show();


        Call<LoginApi> call = WSWorker.apiInterface.login(email_et.getText().toString(), password_et.getText().toString());

        Log.d("-- LoginActivity", "login() - url: " + call.request().url());


        call.enqueue(new Callback<LoginApi>() {
            @Override
            public void onResponse(Call<LoginApi> call, Response<LoginApi> response) {

                Log.d("-- LoginActivity", "login().onResponse " + response.body().getStatus());

                //TODO Al leer los datos revisar total_unread_messages_count para posible notificacion

                //TODO {"status":"error","error_data":{"error_text":"User not registered."}}


                if (response.body().getStatus().equals("success"))
                {
                    SuccessData successData = response.body().getSuccessData();
                    WaraApp.id = successData.getUserId().toString();
                    WaraApp.chatToken = successData.getChatToken();
                    WaraApp.chatUser = successData.getChatUser();
                    WaraApp.token = successData.getAccessToken();
                    WaraApp.saveSharedPreferences();

                    Intent intent = new Intent(WaraApp.getMy_context(), DisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    waraApp.iniService(null);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    if (response.body().getErrorData().getError().equals("User not registered."))
                    {
                        progressDialog.hide();
                        Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_auth_user_error), Toast.LENGTH_SHORT).show();
                    }
                    else  {
                        progressDialog.hide();
                        Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_auth_pass_error), Toast.LENGTH_SHORT).show();

                    }
                }


            }

            @Override
            public void onFailure(Call<LoginApi> call, Throwable t) {
                Log.d("-- LoginActivity", "login().onFailure - error: " + t.getMessage());
                progressDialog.hide();
            }
        });


//        JsonObject json = new JsonObject();
//        json.addProperty("username", email_et.getText().toString());
//        json.addProperty("password", password_et.getText().toString());
//   final Context c = this;
//        Ion.with(this)
//                .load(Endpoints.loginUrl)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        progressDialog.hide();
//                        if (result == null)
//                             return;
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(result.toString());
//                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
//                                JSONObject successObject = jsonObject.getJSONObject("success_data");
//
//                                Log.d ("LA/loginSuccess" , jsonObject.toString(2) );
//
//                                WaraApp.id = successObject.optString("user_id");
//                                WaraApp.chatToken = successObject.optString("chat_token");
//                                WaraApp.chatUser = successObject.optString("chat_user");
//                                WaraApp.token = successObject.optString("access_token");
//                                WaraApp.saveSharedPreferences();
//
//                                JsonObject json = new JsonObject();
//                                json.addProperty("id", WaraApp.id);
//                                json.addProperty("access_token", WaraApp.token);
//
//                                Ion.with(c)
//                                        .load(Endpoints.checkCreated)
//                                        .setJsonObjectBody(json)
//                                        .asJsonObject()
//                                        .setCallback(new FutureCallback<JsonObject>() {
//
//                                            @Override
//                                            public void onCompleted(Exception e, JsonObject result) {
//                                                // do stuff with the result or error
//                                                progressDialog.hide();
////                                                if (result == null)
////                                                    return;
//
//                                                                            Intent intent = new Intent(LoginActivity.this, DisplayActivity.class);
//                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                                                        startActivity(intent);
//                                                        finish();
//
//                                            }
//                                        });
//
//
//                            } else {
//                                //TODO Revisar el error que esta mandando que de seguro es en INGLES así que debo poner un STRING
//                                JSONObject erorObject = jsonObject.getJSONObject("error_data");
//                                Toast.makeText(LoginActivity.this, erorObject.optString("error_text"), Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                            Toast.makeText(LoginActivity.this, e1.getMessage() , Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

    }

    private boolean checkForEmpty() {

        //TODO a lo mejor debo poner el texto de error en TOAST si no se logra ver bien en los edits

        if (email_et.getText().toString().isEmpty()) {
            email_et.setError(getString(R.string.text_login_email_error));
            return false;
        } else {
            if (!isValidEmail(email_et.getText().toString())) {
                email_et.setError(getString(R.string.text_login_valid_email_error));
                return false;
            }
        }
        if (password_et.getText().toString().isEmpty() || password_et.getText().toString().length() < 8) {
            password_et.setError(getString(R.string.text_login_pass_error));
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}