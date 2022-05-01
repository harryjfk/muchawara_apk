package cu.inmobile.wara.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.AutoSizeableTextView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.GenderOptions;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Pojo.CityApi;
import cu.inmobile.wara.Pojo.LoginApi;
import cu.inmobile.wara.Pojo.RegSuccessData;
import cu.inmobile.wara.Pojo.RegisterApi;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.City;
import cu.inmobile.wara.RoomModels.CityViewModel;
import cu.inmobile.wara.Utils.CitiesPresenter;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Networking.*;
import de.measite.minidns.Client;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Anurag on 4/11/2017.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1845;
    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
    private RadioGroup gender_group;
    private EditText name_et, email_et, etCity, confirm_password_et, password_et;
    private ArrayList<GenderOptions> genderOptionsArrayList;
    private int choosenGenderCount = -1;
    private LinearLayout his_birthday_layout, her_birthday_layout;
    private TextView her_birthday, his_birthday;
    private LocalDate dob, couple_dob;
    private Button register;
    private String city, country, locality;
    private Place searchPlace;
    private RadioRealButtonGroup radioGroupGender;
    private ImageView imgGooglePlaces;
    private Autocomplete citiesAutocomplete;

    private CityViewModel cityViewModel;


    private ArrayList<CityApi.City> cityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iniVariables();
        settingInterface();
        //getCitiesR();

        //getFilter();
        //TODO Crear un API que me devuelva todas las ciudades sin estar logueado
        if (cityList.size() > 0){
            //settingAutocomplteCities(cityList);
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Register");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private void iniVariables (){
        country = "Cuba";

        cityList = new ArrayList<CityApi.City>();

        cityViewModel = ViewModelProviders.of(this).get(CityViewModel.class);

        cityViewModel.getAllCities().observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(@Nullable final List<City> cities) {
                Log.d("-- RegisterActivity" , "onChanged()");

                settingAutocomplteCities(cities);
            }
        });
    }

    private void settingInterface (){

        radioGroupGender = (RadioRealButtonGroup) findViewById(R.id.btn_group_gender) ;
        genderOptionsArrayList = new ArrayList<>();

        gender_group = (RadioGroup) findViewById(R.id.gender_group);

        name_et = (EditText) findViewById(R.id.name_et);
        email_et = (EditText) findViewById(R.id.email_et);

        imgGooglePlaces = (ImageView) findViewById(R.id.img_google_places);

        //city_et = (EditText) findViewById(R.id.city_et);
        //city_et.setInputType(InputType.TYPE_CLASS_TEXT);

        etCity = (EditText) findViewById(R.id.et_city);

        imgGooglePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacesDialog();
            }
        });

        his_birthday_layout = (LinearLayout) findViewById(R.id.his_birthday_layout);
        his_birthday_layout.setOnClickListener(this);

        his_birthday = (TextView) findViewById(R.id.his_birthday);

        register = (Button) findViewById(R.id.register);
        password_et = (EditText) findViewById(R.id.password_et);
        confirm_password_et = (EditText) findViewById(R.id.confirm_password_et);
        register.setOnClickListener(this);

        her_birthday_layout = (LinearLayout) findViewById(R.id.her_birthday_layout);

        her_birthday_layout.setOnClickListener(this);

        radioGroupGender.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton radioRealButton, int i) {
                //Toast.makeText(RegisterActivity.this, "position: " + radioGroupGender.getPosition() , Toast.LENGTH_SHORT).show();
            }
        });

        //getCustomFields();
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

        citiesAutocomplete = Autocomplete.<String>on(etCity)
                .with(elevation)
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
    }

//    private void settingAutocomplteCities (ArrayList<CityApi.City> cities){
//        float elevation = 6f;
//        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
//        CitiesPresenter presenter = new CitiesPresenter(this, cities);
//        AutocompleteCallback<String> callback = new AutocompleteCallback<String>() {
//            @Override
//            public boolean onPopupItemClicked(Editable editable, String item) {
//                editable.clear();
//                editable.append(item);
//                return true;
//            }
//
//            public void onPopupVisibilityChanged(boolean shown) {}
//        };
//
//        citiesAutocomplete = Autocomplete.<String>on(etCity)
//                .with(elevation)
//                .with(backgroundDrawable)
//                .with(presenter)
//                .with(callback)
//                .build();
//    }

    private void getCustomFields() {
        final JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");

        Ion.with(this)
                .load(Endpoints.getCustomFields)
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
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");
                                JSONObject genderObj = successObj.getJSONObject("gender");
                                JSONArray optionsArray = genderObj.getJSONArray("options");
                                for (int i = 0; i < optionsArray.length(); i++) {
                                    JSONObject optionObj = optionsArray.getJSONObject(i);
                                    GenderOptions genderOptions = new GenderOptions();
                                    genderOptions.setId(optionObj.optString("id"));
                                    genderOptions.setCode(optionObj.optString("code"));
                                    genderOptions.setText(optionObj.optString("text"));
                                    if(optionObj.optString("text").equals("Male") || optionObj.optString("text").equals("Female")){
                                        genderOptionsArrayList.add(genderOptions);
                                    }
                                }
                                setUpRadioButtons();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

//    private void getCities() {
//        Log.d("getCities" , "STARTING");
//
//        if (! HelperMethods.isNetworkAvailable(this)){
//            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_filter));
//        progressDialog.setCanceledOnTouchOutside(true);
//        //progressDialog.show();
//
//        JsonObject json = new JsonObject();
//        //json.addProperty(Const.Params.ID, WaraApp.id);
//        //json.addProperty(Const.Params.TOKEN, WaraApp.token);
//        Ion.with(this)
//                .load(Endpoints.getCities)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        progressDialog.cancel();
//                        if (result == null){
//                            Log.d("REGISTER/getCities", e.getMessage());
//                            Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_error_web ), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(result.toString());
//
//                            Log.d("REGISTER/getCities" , jsonObject.toString(2));
//
//
//                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
//                                JSONObject successObj = jsonObject.getJSONObject("success_data");
//                                //  Toast.makeText(FilterActivity.this,obj.optString("success_text"),Toast.LENGTH_SHORT).show();
//
//                                JSONArray cities = successObj.getJSONArray("city");
//                                //cityList2 = new String [cities.length()];
//
//                                for (int j = 0; j < cities.length(); j++){
//                                    JSONObject city =  cities.getJSONObject(j);
//                                    cityList.add(city.optString("name"));
//                                    //cityList2 [j] = city.optString("text");
//
//                                    //Log.d ("act/getFilter" , "Ciudad: " + city.optString("text"));
//                                }
//
//                                settingAutocomplteCities(cityList);
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
//                            }else {
//                                JSONObject obj = jsonObject.getJSONObject("error_data");
//                                //TODO Este texto está en Ingles
//                                Toast.makeText(WaraApp.getMy_context(), obj.optString("error_text"), Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//    }

    private void getCitiesR (){

        APIInterface apiInterface= APIClient.getClient().create(APIInterface.class) ;

        Call<CityApi> call = apiInterface.doGetCities();
        call.enqueue(new Callback<CityApi>() {

            @Override
            public void onResponse(Call<CityApi> call, Response<CityApi> response) {


                Log.d("TAG",response.code()+"");

                String displayResponse = "";

                CityApi parsed = response.body();
                CityApi.SuccessData data = parsed.getSuccessData();
                ArrayList<CityApi.City> listCity = ( ArrayList<CityApi.City> )data.getCity();
                //settingAutocomplteCities(listCity);
//                for (CityApi.City city : listCity) {
//                    Log.d ("act/getCitiesR" , "city:" + city.getName() );
//                }

            }

            @Override
            public void onFailure(Call<CityApi> call, Throwable t) {

                Log.d ("act/getCitiesR" , "FAILED: " + t.getMessage()  );

                call.cancel();
            }
        });

    }

    private void showPlacesDialog() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d ("act/showPlacesDialog" , "GooglePlayServicesRepairableException");
            Log.d ("act/showPlacesDialog" , e.getMessage() );
            Toast.makeText(this, R.string.toast_google_services_error, Toast.LENGTH_LONG).show();
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e){

            Log.d ("act/showPlacesDialog" , "GooglePlayServicesNotAvailableException");
            // TODO: Handle the error.
        }
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.d ("act/onActivityResult" , "Before RESULT");

            if (resultCode == Activity.RESULT_OK) {
                searchPlace = PlaceAutocomplete.getPlace(this, data);
                Log.d ("act/onActivityResult" , ""+ searchPlace.getAddress());
                List<Address> addresses = null;
                try {
                    //Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
                    Geocoder mGeocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    addresses = mGeocoder.getFromLocation(searchPlace.getLatLng().latitude, searchPlace.getLatLng().longitude, 1);
                    if (addresses != null && addresses.size() > 0) {

                        city = addresses.get(0).getLocality();
                        country = addresses.get(0).getCountryName();
                        //etCity.setText(addresses.get(0).getCountryName() + ", " + addresses.get(0).getLocality() );
                        etCity.setText(searchPlace.getAddress());

                        Log.d ("act/onActivityResult" , "PLACES: " + country+ " " + city + " " + locality);

                        /* country.setText(addresses.get(0).getCountryName());
                        state.setText(addresses.get(0).getAdminArea());
                        pincode.setText(addresses.get(0).getPostalCode());*/
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d ("RA/onActivityResult" , "ERROR: " + searchPlace.toString());
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

                Toast.makeText(this, "", Toast.LENGTH_LONG).show();

                Log.d ("act/method" , status.getStatusMessage());


                // TODO: Handle the error.

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d ("RA/RESULT_CANCELED" , "CANCELED");
                // The user canceled the operation.
            }
        }
    }


    private void setUpRadioButtons() {
        gender_group.removeAllViews();
        gender_group.setOnCheckedChangeListener(null);
        //for (int i = 0; i < genderOptionsArrayList.size(); i++) {
            for (int i = 0; i < 2; i++) {
            RadioButton radioButton = new RadioButton(this);
            //radioButton.setText(genderOptionsArrayList.get(i).getText());
                radioButton.setText("MALE");
            radioButton.setId(i);
            gender_group.addView(radioButton);
        }
        gender_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    choosenGenderCount = checkedId;
                    switch (genderOptionsArrayList.get(choosenGenderCount).getCode()) {
                        case "couple":
                        case "custom_couple":
                            her_birthday_layout.setVisibility(View.VISIBLE);
                            break;
                        default:
                            her_birthday_layout.setVisibility(View.GONE);

                    }
                }
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.his_birthday_layout:
                openDefaultDatePicker();
                break;
            case R.id.her_birthday_layout:
                openCoupleDatePicker();
                break;
            case R.id.register:
                if (checkForEmpty())
                    register();
                break;
        }
    }

    private boolean checkForEmpty() {
        if (name_et.getText().toString().isEmpty() || name_et.getText().length()<1 || name_et.getText().toString().equals(" ") ) {
            name_et.setError(getString(R.string.text_name_error));
            return false;
        }

        if (email_et.getText().toString().isEmpty()) {
            email_et.setError(getString(R.string.text_email_error));
            return false;
        } else {
            if (!isValidEmail(email_et.getText().toString())) {
                email_et.setError(getString(R.string.text_valid_email_error));
                return false;
            }
        }
        Boolean city = false;
        for (int _i = 0; _i < WaraApp.cities.length; _i++) {
            if (etCity.getText().toString().equals(WaraApp.cities[_i])) {
                city = true;
            }
        }
        if (etCity.getText().toString().isEmpty()) {
            etCity.setError(getString(R.string.text_city_error));
            return false;
        }
        if (!city) {
            etCity.setError(getString(R.string.text_wrong_city_error));
            return false;
        }
        //TODO Codigo actualizado
        /*if (choosenGenderCount == -1) {
            Toast.makeText(this, "Choose a gender", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (radioGroupGender.getPosition() == -1) {
            Toast.makeText(this, getString(R.string.text_gender_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dob == null) {
            Toast.makeText(this, getString(R.string.text_dob_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password_et.getText().toString().isEmpty() ) {
            password_et.setError(getString(R.string.text_pass_error));
            return false;
        }

         if (password_et.getText().toString().length() < 8) {
            password_et.setError(getString(R.string.text_pass_length_error));
            return false;
        }
        if (confirm_password_et.getText().toString().isEmpty() ) {
            confirm_password_et.setError(getString(R.string.text_pass2_error));
            return false;
        }

        if(!confirm_password_et.getText().toString().equals(password_et.getText().toString())) {
            confirm_password_et.setError(getString(R.string.text_pass_pass2_error));
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void register() {

        if (! HelperMethods.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_registering));
        progressDialog.setCancelable(false);
        progressDialog.show();


        Call<RegisterApi> call = WSWorker.apiInterface.register(
                name_et.getText().toString(),
                email_et.getText().toString(),
                etCity.getText().toString(),
                (radioGroupGender.getPosition()==0)? Const.Params.GENDER_MALE : Const.Params.GENDER_FEMALE,
                "23.01",
                "-82.27",
                password_et.getText().toString(),
                password_et.getText().toString(),
                fmt.print(dob)
                );

        Log.d("-- LoginActivity","login() - url: " + call.request().url());

        call.enqueue(new Callback<RegisterApi>() {
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                try{


                Log.d("-- RegisterActivity","login().onResponse " + response.body().getStatus());

                if (response.body().getStatus().equals("success")){

                    RegSuccessData successData = response.body().getSuccessData();

                    WaraApp.id = successData.getUserId().toString() ;
                    WaraApp.token = successData.getAccessToken() ;
                    WaraApp.chatToken = successData.getChatToken() ;
                    WaraApp.city_filter= etCity.getText().toString();
                    WaraApp.city_user= etCity.getText().toString();
                    WaraApp.name= name_et.getText().toString();
                    WaraApp.chatUser = successData.getChatUser();
                    WaraApp.chatToken = successData.getChatToken();
                    WaraApp.saveSharedPreferences();

                    startActivity(new Intent(RegisterActivity.this, SetProfilePictureActivity.class));
                    finish();
                }
                else {
                    progressDialog.hide();
                    Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_mail_error) , Toast.LENGTH_SHORT).show();
                }
                }catch (Exception e){
                    progressDialog.hide();
                    Log.d("-- RegisterActivity","login().catch() : " + e.getMessage());

                    Toast.makeText(WaraApp.getMy_context() , e.getMessage() , Toast.LENGTH_SHORT).show();


                }


            }

            @Override
            public void onFailure(Call<RegisterApi> call, Throwable t) {

                Log.d("-- RegisterActivity","login().onFailure : " + t.getMessage());

                progressDialog.hide();

                Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_bad_answer) , Toast.LENGTH_SHORT).show();
            }
        });


//        JsonObject json = new JsonObject();
//        json.addProperty("name", name_et.getText().toString());
//        json.addProperty("username", email_et.getText().toString());
//        json.addProperty("city", etCity.getText().toString());
//        //json.addProperty("city", "Miami, Florida, EE. UU.");
//        //json.addProperty("country", country);
//        json.addProperty("gender", (radioGroupGender.getPosition()==0)? Const.Params.GENDER_MALE : Const.Params.GENDER_FEMALE);
//        json.addProperty("lat", 23.01);
//        json.addProperty("lng", -82.27);
//        json.addProperty("password", password_et.getText().toString());
//        json.addProperty("password_confirmation", confirm_password_et.getText().toString());
//        json.addProperty("dob", fmt.print(dob));
//        if (couple_dob != null)
//            json.addProperty("couple_dob", fmt.print(dob));
//        final Context c = this;
//        Ion.with(this)
//                .load(Endpoints.registerUrl)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        progressDialog.dismiss();
//                        if (result == null){
//                            Toast.makeText(WaraApp.getMy_context() , getString(R.string.toast_bad_answer ), Toast.LENGTH_SHORT).show();
//
//                            Log.d("-- RegisterActivity", "register().onCompleted() - ERROR WEB");
//                            return;
//                        }
//
//                        Log.d("DATA", result.toString());
//                        try {
//                            JSONObject jsonObject = new JSONObject(result.toString());
//                            if (jsonObject.getString("status").equals("success")) {
//                                JSONObject successObject = jsonObject.getJSONObject("success_data");
//                                StringBuilder success_msg = new StringBuilder("");
//                                success_msg.append(getString(R.string.text_registering_success));
//
//                                WaraApp.id = successObject.optString("user_id") ;
//                                WaraApp.token = successObject.optString("access_token") ;
//                                WaraApp.chatToken = successObject.optString("chat_token") ;
//                                WaraApp.saveSharedPreferences();
//
//
//                                //SharedPreferencesUtils.setParam(RegisterActivity.this, SharedPreferencesUtils.SESSION_TOKEN, successObject.optString("access_token"));
//                                //SharedPreferencesUtils.setParam(RegisterActivity.this, SharedPreferencesUtils.USER_ID, successObject.optString("user_id"));
//                                if (successObject.optBoolean("email_verify_required")) {
//                                    success_msg.append("Please verify your email address.");
//                                }
//                                //onBackPressed();
//
//                                JsonObject json = new JsonObject();
//                                json.addProperty("id", WaraApp.id);
//                                json.addProperty("access_token", WaraApp.token);
//
//                                Ion.with(c)
//                                        .load(Endpoints.checkCreated+"?id="+WaraApp.id+"&access_token="+WaraApp.token)
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
//                                                startActivity(new Intent(RegisterActivity.this, SetProfilePictureActivity.class));
//                                                finish();
//
//                                            }
//                                        });
//                                           } else {
//                                 JSONArray errorArray = jsonObject.getJSONArray("error_data");
//                                StringBuilder error_msg = new StringBuilder("");
//                                for (int j = 0; j < errorArray.length(); j++){
//                                    error_msg.append(errorArray.get(j));
//                                    if(j<errorArray.length()-1)
//                                        error_msg.append(",");
//                                }
//                                Toast.makeText(RegisterActivity.this, error_msg.toString(), Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                            Log.d("REGISTER", e1.toString());
//                        }
//
//                    }
//
//
//                });
    }

    private void openDefaultDatePicker() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR) - 18;
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dob = new LocalDate(year, monthOfYear + 1, dayOfMonth + 1);
                        his_birthday.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        DateTime max_date = new DateTime(mYear, mMonth + 1, mDay, 0, 0);
        datePickerDialog.getDatePicker().setMaxDate(max_date.getMillis());

        datePickerDialog.show();
    }

    private void openCoupleDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR) - 18;
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        couple_dob = new LocalDate(year, monthOfYear + 1, dayOfMonth);
                        her_birthday.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        DateTime max_date = new DateTime(mYear, mMonth + 1, mDay, 0, 0);
        datePickerDialog.getDatePicker().setMaxDate(max_date.getMillis());

        datePickerDialog.show();
    }

}