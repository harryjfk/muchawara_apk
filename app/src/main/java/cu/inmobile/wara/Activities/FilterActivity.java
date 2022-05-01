package cu.inmobile.wara.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import cu.inmobile.wara.Adapters.PurposeListAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.APIClient;
import cu.inmobile.wara.Networking.APIInterface;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.CityApi;
import cu.inmobile.wara.Pojo.GetFilterApi;
import cu.inmobile.wara.Pojo.Photo;
import cu.inmobile.wara.Pojo.ProfileApi;
import cu.inmobile.wara.Pojo.SaveFilterApi;
import cu.inmobile.wara.Pojo.SuccessDataGetFilter;
import cu.inmobile.wara.Pojo.SuccessDataProfile;
import cu.inmobile.wara.Pojo.SuccessDataSaveFilter;
import cu.inmobile.wara.RoomModels.City;
import cu.inmobile.wara.RoomModels.CityViewModel;
import cu.inmobile.wara.Utils.CitiesPresenter;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Utils.SharedPreferencesUtils;
import cu.inmobile.wara.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1845;
    private static final String TAG = "-- FilterActivity";

    private Autocomplete citiesAutocomplete;

    private CityViewModel cityViewModel;

    private RadioRealButtonGroup radioGroupGender;
    private RadioRealButton radioMale;
    private RadioRealButton radioFemale;
    private RadioRealButton radioBoth;
    private CrystalRangeSeekbar barAgeRange;
    private ImageView imgBack;
    private TextView tvAgeText;
    private EditText editCity;
    private Button btnDone;
    private AutoCompleteTextView acCity;

    private String unit = "km";
    private boolean is_updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        iniVariables();
        settingInterface();

        getFilter();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void iniVariables() {

        cityViewModel = ViewModelProviders.of(this).get(CityViewModel.class);

//        cityViewModel.getAllCities().observe(this, new Observer<List<City>>() {
//            @Override
//            public void onChanged(@Nullable final List<City> cities) {
//                Log.d(TAG, "iniVariables() - onChanged()");
//
//                settingAutocomplteCities(cities);
//
//            }
//        });


    }

    private void settingInterface() {

        Window window = this.getWindow();


        //-------------------------------- INITIALIZING

        radioGroupGender = (RadioRealButtonGroup) findViewById(R.id.radio_group_gender);
        radioMale = (RadioRealButton) findViewById(R.id.radio_male);
        radioFemale = (RadioRealButton) findViewById(R.id.radio_female);
        radioBoth = (RadioRealButton) findViewById(R.id.radio_both);
        barAgeRange = (CrystalRangeSeekbar) findViewById(R.id.bar_age);
        tvAgeText = (TextView) findViewById(R.id.tv_age_text);
        //editCity = (EditText) findViewById(R.id.edit_filter_city);
        btnDone = (Button) findViewById(R.id.btn_continue);
        editCity = (EditText) findViewById(R.id.et_filter_city);
        imgBack = (ImageView) findViewById(R.id.img_back);

        btnDone.requestFocus();


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaraApp.getMy_context().startActivity(
                        new Intent(WaraApp.getMy_context(), DisplayActivity.class)
                );
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFilter();

            }
        });

//        editCity.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d(TAG, "setOnFocusChangeListener() - onFocusChange() " +  hasFocus);
//            }
//        });
        updateFilterView();

        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.d(TAG, "beforeTextChanged() ");
                if (citiesAutocomplete == null) {
                    cityViewModel.getAllCities().observe(FilterActivity.this , new Observer<List<City>>() {
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


    }

    //todo El autocomplete debe trabajar con una lista de City Rooms en vez del API CITY

    private void settingAutocomplteCities(List<City> cities) {
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

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };

        citiesAutocomplete = Autocomplete.<String>on(editCity)
                .with(elevation)
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_apply) {
            updateFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFilter() {
        if (!HelperMethods.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }

        //CREO LA CONEXION CON LA API PASANDO LOS PARAMENTROS USER_ID, ACCESS_TOKEN
        Call<GetFilterApi> call = WSWorker.apiInterface.getFilter(WaraApp.id, WaraApp.token);
        Log.d(TAG, "getFilter() -url: " + call.request().url());

        //HAGO LA PETICION AL SERVE CON RETROFIT
        call.enqueue(new Callback<GetFilterApi>() {
            @Override
            public void onResponse(Call<GetFilterApi> call, Response<GetFilterApi> response) {

                try {

                    if (response.body().getStatus().equals(true)) {
                        SuccessDataGetFilter successData = response.body().getSuccessDataGetFilter();
                        Log.d(TAG, "onResponse() -successData: " + response.toString());

                        WaraApp.minAge = Integer.parseInt(successData.getPerferedAges().getMin());
                        WaraApp.maxAge = Integer.parseInt(successData.getPerferedAges().getMax());


                        if (successData.getPreferedGenders().size() == 2) {
                            WaraApp.gender = Const.Params.GENDER_BOTH;
                        } else {
                            WaraApp.gender = successData.getPreferedGenders().get(0);
                        }

                        WaraApp.city_filter = successData.getPreferedLocation();
                        WaraApp.saveSharedPreferences();
                        updateFilterView();
                    }

                } catch (Exception e) {
                    Log.d(TAG, "getFilter() .catch(): " + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetFilterApi> call, Throwable t) {
                Log.d(TAG, "getFilter().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateFilterView() {
        barAgeRange.setMinStartValue(WaraApp.minAge);
        barAgeRange.setMaxStartValue(WaraApp.maxAge);
        barAgeRange.apply();

        barAgeRange.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void valueChanged(Number minValue, Number maxValue) {

                tvAgeText.setText(getString(R.string.text_filter_age_text1)
                        + " " + minValue
                        + " " + getString(R.string.text_filter_age_text2)
                        + " " + maxValue);
            }
        });


        if (WaraApp.gender.equals(Const.Params.GENDER_BOTH))
            radioGroupGender.setPosition(1);
        else if (WaraApp.gender.equals(Const.Params.GENDER_MALE))
            radioGroupGender.setPosition(0);
        else
            radioGroupGender.setPosition(2);

        editCity.setText(WaraApp.city_filter);


    }

    private void getFilter_old() {
        Log.d("getFilter JSON", "STARTING");

        if (!HelperMethods.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_filter));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("user_id", WaraApp.id);
        json.addProperty("access_token", WaraApp.token);
        Ion.with(this)
                .load(Endpoints.getFilterUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progressDialog.cancel();
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            Log.d("getFilter JSON", jsonObject.toString(2));

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, FilterActivity.this)) {
                                return;
                            }

                            if (jsonObject.optBoolean(Const.Params.STATUS)) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");
                                //  Toast.makeText(FilterActivity.this,obj.optString("success_text"),Toast.LENGTH_SHORT).show();
                                JSONObject ageObj = successObj.optJSONObject("perfered_ages");
                                if (ageObj != null) {
                                    barAgeRange.setMaxStartValue(ageObj.optInt("max")).apply();
                                    barAgeRange.setMinStartValue(ageObj.optInt("min")).apply();
                                }
                                JSONArray genderArray = successObj.optJSONArray("prefered_genders");
                                if (genderArray != null) {
                                    radioBoth.setChecked(true);
                                    if (genderArray.length() == 2)
                                        radioGroupGender.setPosition(1);
                                    else {
                                        if (genderArray.getString(0).equals("male"))
                                            radioGroupGender.setPosition(0);
                                        else
                                            radioGroupGender.setPosition(2);
                                    }
                                }

                                String prefered_location = successObj.optString("prefered_location");
                                if (prefered_location != null)
                                    editCity.setText(prefered_location);

//



                                /*JSONObject distanceObj = successObj.optJSONObject("perfered_distance");
                                if (distanceObj != null) {
                                    distance_seekbar.setMinStartValue(distanceObj.optInt("value")).apply();
                                    unit = distanceObj.optString("unit");
                                    distance_text.setText("Show me people within " + distance_seekbar.getSelectedMinValue() + " " + unit);
                                }
                                JSONObject locationObject = successObj.optJSONObject("locations");
                                if (locationObject != null) {
                                    JSONObject nearbyObj = locationObject.optJSONObject("people_nearby");
                                    if (nearbyObj != null) {
                                        location = new Location("");
                                        location.setLatitude(nearbyObj.optDouble("latitude"));
                                        location.setLongitude(nearbyObj.optDouble("longitude"));
                                        locationName = nearbyObj.optString("location_name");
                                        if (locationName == null || locationName.equals("null"))
                                            locationName = "";
                                    }
                                }
                                updateLocationUi();*/

                            } else {
                                JSONObject obj = jsonObject.getJSONObject("error_data");
                                //TODO Este texto está en Ingles
                                Toast.makeText(FilterActivity.this, obj.optString("error_text"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    /*private void updateLocationUi() {
        if (!locationName.isEmpty() && !locationName.equals("null")) {
            anotherLocationButton.setText(locationName);
            change_button.setVisibility(View.VISIBLE);
        } else change_button.setVisibility(View.GONE);
    }*/


    private void updateFilter() {

        if (radioMale.isChecked())
            WaraApp.gender = Const.Params.GENDER_MALE;
        else if (radioFemale.isChecked())
            WaraApp.gender = Const.Params.GENDER_FEMALE;
        else if (radioBoth.isChecked())
            WaraApp.gender = Const.Params.GENDER_BOTH;

        WaraApp.maxAge = barAgeRange.getSelectedMaxValue().intValue();
        WaraApp.minAge = barAgeRange.getSelectedMinValue().intValue();

        Log.d("-- FilterActivity", "updateFilter() - maxValueAfterSet: " + WaraApp.maxAge + WaraApp.gender);


        WaraApp.city_filter = editCity.getText().toString();
        updateFilterInServer();


    }


    private void updateFilterInServer() {

        Log.d(TAG, "updateFilterInServer()");


        if (!HelperMethods.isNetworkAvailable(WaraApp.getMy_context())) {
            Toast.makeText(WaraApp.getMy_context(), WaraApp.getMy_context().getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_saving_filter));
        progressDialog.setCancelable(true);
        progressDialog.show();

        String age = WaraApp.minAge + "-" + WaraApp.maxAge;
        Boolean city = false;
        for (int _i = 0; _i < WaraApp.cities.length; _i++) {
            if (editCity.getText().toString().equals(WaraApp.cities[_i])) {
                city = true;
            }
        }
        if (editCity.getText().toString().equals("")) {
            progressDialog.cancel();
            Toast.makeText(FilterActivity.this, "Debe escoger una ciudad para comenzar a buscar", Toast.LENGTH_SHORT).show();
        } else if (!city) {
            progressDialog.cancel();
            Toast.makeText(FilterActivity.this, "Debe escoger una ciudad del listado mostrado.", Toast.LENGTH_SHORT).show();
        } else {
            Call<SaveFilterApi> call = WSWorker.apiInterface.saveFilter(WaraApp.id, WaraApp.token, WaraApp.gender, age, WaraApp.city_filter);
            Log.d(TAG, "getFilter() -url: " + call.request().url());

            //HAGO LA PETICION AL SERVE CON RETROFIT
            call.enqueue(new Callback<SaveFilterApi>() {
                @Override
                public void onResponse(Call<SaveFilterApi> call, Response<SaveFilterApi> response) {

                    try {

                        if (response.body().getStatus().equals("success")) {
                            Log.d(TAG, "updateFilterInServer().onCompleted() - OK");
                            progressDialog.cancel();
                            onBackPressed();

                        } else {
                            Log.d("-- FilterActivity", "updateFilterInServer().onCompleted() - ERROR" + response.toString());
                            Toast.makeText(FilterActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "getFilter() .catch(): " + e.getMessage());
                        Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<SaveFilterApi> call, Throwable t) {
                    Log.d(TAG, "getFilter().onFailure : " + t.getMessage());
                    Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();

                }


            });
        }

       /* JsonObject json = new JsonObject();
        json.addProperty("user_id", WaraApp.id);
        json.addProperty("access_token", WaraApp.token);
        json.addProperty("prefered_genders", WaraApp.gender);
        String age = WaraApp.minAge + "-" + WaraApp.maxAge;
        json.addProperty("prefered_ages", age);

        json.addProperty("prefered_location", WaraApp.city_filter);

        Ion.with(WaraApp.getMy_context())
                .load(Endpoints.saveFilterUrl)
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

//                            if(HelperFunctions.IsUserAuthenticated(jsonObject, FilterActivity.class)){
//                                return;
//                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject obj = jsonObject.getJSONObject("success_data");
                                Log.d("-- FilterActivity", "updateFilterInServer().onCompleted() - OK");
                                progressDialog.cancel();
                                onBackPressed();

                            } else {
                                JSONObject obj = jsonObject.getJSONObject("error_data");

                                Log.d("-- FilterActivity", "updateFilterInServer().onCompleted() - ERROR" + obj.optString("error_text"));

                                //Toast.makeText(FilterActivity.this, obj.optString("error_text"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            Log.d("-- FilterActivity", "updateFilterInServer().catch() - ERROR" + e1.getMessage());

                            e1.printStackTrace();

                        }
                    }
                });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place searchPlace = PlaceAutocomplete.getPlace(this, data);
                locationName = searchPlace.getName().toString();
                if(location == null){
                    location = new Location("");
                }
                location.setLatitude(searchPlace.getLatLng().latitude);
                location.setLongitude(searchPlace.getLatLng().longitude);
                anotherLocationButton.setChecked(true);
                updateLocationUi();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }*/
    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent();
        if (is_updated)
            intent.putExtra("is_updated", true);
        else intent.putExtra("is_updated", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {


    }

    public class AutocompletePlacesAdapter extends ArrayAdapter<String> implements Filterable {

        final String TAG = "AutocompleteCustomArrayAdapter.java";

        Context mContext;
        int layoutResourceId;
        String[] data = null;

        public AutocompletePlacesAdapter(Context mContext, int layoutResourceId, String[] data) {

            super(mContext, layoutResourceId, data);

            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            try {

                if (convertView == null) {
                    // inflate the layout
                    LayoutInflater inflater = ((FilterActivity) mContext).getLayoutInflater();
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                }

                String objectItem = data[position];

                TextView textViewItem = (TextView) convertView.findViewById(android.R.id.text1);
                textViewItem.setText(objectItem);

                textViewItem.setBackgroundColor(Color.CYAN);

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;

        }
    }

    /*public class CustomChangedListener implements TextWatcher {

        public static final String TAG = "CustomChangedListener";
        Context context;

        public CustomChangedListener(Context context){
            this.context = context;
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence userInput, int start, int before, int count) {

            try{

                // if you want to see in the logcat what the user types
                Log.e(TAG , "User input: " + userInput);

                FilterActivity filterActivity = ((FilterActivity) context);

                // update the adapater
                filterActivity.myAdapter.notifyDataSetChanged();

                // get suggestions from the database
                MyObject[] myObjs = mainActivity.databaseH.read(userInput.toString());

                // update the adapter
                mainActivity.myAdapter = new AutocompleteCustomArrayAdapter(mainActivity, R.layout.list_view_row_item, myObjs);

                mainActivity.myAutoComplete.setAdapter(mainActivity.myAdapter);

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }*/
}
