package cu.inmobile.wara.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.PackageDetail;
import cu.inmobile.wara.Models.userDetail;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.SetEncountersSeenApi;
import cu.inmobile.wara.Utils.Const;

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
public class LikedYouFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterLikedYou likedYouListAdapter;
    private ArrayList<userDetail> userDetailArrayList;
    private TextView tvEmptyText;
    private LinearLayout llExplication;
    private ProgressBar progess;
    private static final String TAG = "-- LikedYouFragment";
    protected ArrayList<String> packageList;
    protected ArrayList<PackageDetail> packageDetailList;
    int arrayCount = 0;
    private static final int REQUEST_CODE_PAYPAL_1 = 2;
    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(Const.CONFIG_ENVIRONMENT)
            .clientId(Const.CONFIG_CLIENT_ID);


    public LikedYouFragment() {
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
        View view = inflater.inflate(R.layout.fragment_liked_you, container, false);

        settingInterface(view);
        getPeopleLikedYou();


        return view;
    }

    private void settingInterface (View view){

        recyclerView = (RecyclerView) view.findViewById(R.id.nearby_ppl);
        tvEmptyText = (TextView) view.findViewById(R.id.tv_empty_text);
        llExplication = (LinearLayout) view.findViewById(R.id.ll_explanation);
        progess = (ProgressBar) view.findViewById(R.id.progess);
        likedYouListAdapter = new AdapterLikedYou(userDetailArrayList, getActivity());
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(likedYouListAdapter);
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

    private void getPeopleLikedYou() {
        userDetailArrayList.clear();
        progess.setVisibility(View.VISIBLE);
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);

        Ion.with(getActivity())
                .load(Endpoints.LikedYouUrl)
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
                            Log.d("LY/getPeopleLikedYou" , jsonObject.toString(2));
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {

                                JSONObject successObj = jsonObject.getJSONObject("success_data");
                                boolean superPowerActivated = successObj.optBoolean("superpower_activated");
                                JSONArray userArray = successObj.getJSONArray("users_liked_me");
                                for (int i = 0; i < userArray.length(); i++) {
                                    JSONObject userObj = userArray.getJSONObject(i);
                                    userDetail user = new userDetail();
                                    user.setId(userObj.optString("id"));
                                    user.setName(userObj.optString("name"));
                                    JSONObject pictureObj = userObj.getJSONObject("profile_picture_url");
                                    user.setPicture(pictureObj.optString("encounter"));
                                    user.setShould_show(superPowerActivated);
                                    userDetailArrayList.add(user);
                                    setEncountersSeen();
                                }

                                if (userDetailArrayList.isEmpty()) {
                                    tvEmptyText.setVisibility(View.VISIBLE);
                                    llExplication.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    tvEmptyText.setVisibility(View.GONE);
                                    llExplication.setVisibility(View.VISIBLE);

                                }
                                likedYouListAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    private void setEncountersSeen(){
        Log.d(TAG, "setEncountersSeen()");


        /*if (!HelperMethods.isNetworkAvailable(WaraApp.getMy_context())) {
            Toast.makeText(WaraApp.getMy_context(), WaraApp.getMy_context().getString(R.string.toast_no_cnx), Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_dialog_updating_tittle));
        progressDialog.setCancelable(true);
        progressDialog.show();*/

        Call<SetEncountersSeenApi> call = WSWorker.apiInterface.setEncountersSeen(WaraApp.id, WaraApp.token);
        Log.d(TAG, "setEncountersSeen() -url: " + call.request().url());

        //HAGO LA PETICION AL SERVE CON RETROFIT
        call.enqueue(new Callback<SetEncountersSeenApi>() {
            @Override
            public void onResponse(Call<SetEncountersSeenApi> call, Response<SetEncountersSeenApi> response) {

                try {

                    if (response.body().getStatus().equals("success")) {
                        Log.d(TAG, "setEncountersSeen().onCompleted() - OK");
                        //progressDialog.cancel();

                    }
                    else {
                        Log.d(TAG, "setEncountersSeen().onCompleted() - ERROR" + response.toString());
                         }
                } catch (Exception e) {
                    Log.d(TAG, "setEncountersSeen().catch(): " + e.getMessage());
                    Toast.makeText(WaraApp.getMy_context(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SetEncountersSeenApi> call, Throwable t) {
                Log.d(TAG, "setEncountersSeen().onFailure : " + t.getMessage());
                Toast.makeText(WaraApp.getMy_context(), getString(R.string.toast_bad_answer), Toast.LENGTH_SHORT).show();

            }


        });
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
