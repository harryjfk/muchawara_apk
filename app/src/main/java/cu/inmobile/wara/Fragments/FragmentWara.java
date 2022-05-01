package cu.inmobile.wara.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import cu.inmobile.wara.Activities.DisplayActivity;
import cu.inmobile.wara.Adapters.AdapterLikedYou;
import cu.inmobile.wara.Adapters.AdapterWara;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.PackageDetail;
import cu.inmobile.wara.Models.userDetail;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.SetMatchesSeenApi;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;import cu.inmobile.wara.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWara extends Fragment {

    private View view;
    private static final String TAG = "-- FragmentWara";
    private RecyclerView rvWara;
    private AdapterWara waraAdapter;
    private ArrayList<userDetail> userDetailArrayList;
    private TextView tvEmptyText;
    private LinearLayout llEmpty, llExplication;
    //private Button activate_superpower;
    private ProgressBar progess;

    protected ArrayList<String> packageList;
    protected ArrayList<PackageDetail> packageDetailList;
    int arrayCount = 0;
    private static final int REQUEST_CODE_PAYPAL_1 = 2;

    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(Const.CONFIG_ENVIRONMENT)
            .clientId(Const.CONFIG_CLIENT_ID);

    public FragmentWara() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WaraApp.setCurrentFragment(this);
        userDetailArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wara, container, false);

        iniVariables();
        settingInterface();

        return view;
    }

    private void iniVariables (){


    }

    private void settingInterface (){

        rvWara = (RecyclerView) view.findViewById(R.id.rv_wara);
        tvEmptyText = (TextView) view.findViewById(R.id.tv_empty_text);
        llExplication = (LinearLayout) view.findViewById(R.id.ll_explanation);
        progess = (ProgressBar) view.findViewById(R.id.progress);
        waraAdapter = new AdapterWara(userDetailArrayList, getActivity());
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvWara.setLayoutManager(linearLayoutManager);
        rvWara.setItemAnimator(new DefaultItemAnimator());
        rvWara.setAdapter(waraAdapter);

        if (userDetailArrayList.isEmpty())
            getWara();

    }

    private void getWara() {
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);

        progess.setVisibility(View.VISIBLE);
        Ion.with(getActivity())
                .load(Endpoints.getWara)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        progess.setVisibility(View.GONE);
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())){
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObj = jsonObject.getJSONObject("success_data");

                                Log.d ("act/getWara" , successObj.toString(2));

                                JSONArray userArray = successObj.getJSONArray("matched_users");
                                for (int i = 0; i < userArray.length(); i++) {
                                    JSONObject userObj = userArray.getJSONObject(i);

                                   // if (!userObj.getString("slug_name").equals("wara")){
                                        userDetail user = new userDetail();
                                        user.setId(userObj.optString("id"));
                                        user.setName(userObj.optString("name"));
                                        user.setSlugName(userObj.getString("slug_name"));
                                        user.setAboutMe(userObj.getString("aboutme"));
                                        JSONObject pictureObj = userObj.getJSONObject("profile_picture_url");
                                        user.setPicture(pictureObj.optString("encounter"));
                                        user.setPicturePerfil(userObj.getString("profile_picture_name"));
                                        userDetailArrayList.add(user);
                                   // }
                                    setMatchesSeen();
                                }

                                if (userDetailArrayList.isEmpty()){
                                    tvEmptyText.setVisibility(View.VISIBLE);
                                    llExplication.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    tvEmptyText.setVisibility(View.GONE);
                                    llExplication.setVisibility(View.VISIBLE);

                                }

                                waraAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    private void setMatchesSeen(){
        Log.d(TAG, "setMatchesSeen()");
        /*if (!HelperMethods.isNetworkAvailable(WaraApp.getMy_context())) {
            Toast.makeText(WaraApp.getMy_context(), WaraApp.getMy_context().getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_dialog_updating_tittle));
        progressDialog.setCancelable(true);
        progressDialog.show();*/

        Call<SetMatchesSeenApi> call = WSWorker.apiInterface.setMatchesSeen(WaraApp.id, WaraApp.token);
        Log.d(TAG, "setMatchesSeen() -url: " + call.request().url());

        //HAGO LA PETICION AL SERVE CON RETROFIT
        call.enqueue(new Callback<SetMatchesSeenApi>() {
            @Override
            public void onResponse(Call<SetMatchesSeenApi> call, Response<SetMatchesSeenApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        Log.d(TAG, "setMatchesSeen().onCompleted() - OK");
                        //progressDialog.cancel();
                    }
                    else {
                        Log.d(TAG, "setMatchesSeen().onCompleted() - ERROR" + response.toString());
                         }
                } catch (Exception e) {
                    Log.d(TAG, "setMatchesSeen().catch(): " + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SetMatchesSeenApi> call, Throwable t) {
                Log.d(TAG, "setMatchesSeen().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();

            }


        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())){
                                return;
                            }

                            if (jsonObject.optBoolean(Const.Params.SUCCESS)) {
                                //active_status.setText("Active");
                                //active_status.setTextColor(getResources().getColor(R.color.white));
                                DisplayActivity.getInstance().UpdateSuperPowerStatus("Active", true);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    /**
     * This function fetches the packages for the super power from the backend
     */
    protected void getSuperPowerPackages() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
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

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if(HelperFunctions.IsUserAuthenticated(jsonObject, getActivity())){
                                return;
                            }

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

    /**
     * Creates dialog box for the available packages.
     * @param packageList
     * @param packageDetailList
     */
    protected void createDialogSuperSingleChoice(ArrayList<String> packageList, final ArrayList<PackageDetail> packageDetailList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
                        payByPayPalForSuper(packageDetailList.get(arrayCount).getAmount());
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
     * Payment initiation by paypal
     * @param amount to pay
     */
    private void payByPayPalForSuper(String amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", "Total bill",
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(), com.paypal.android.sdk.payments.PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_PAYPAL_1);
    }


}
