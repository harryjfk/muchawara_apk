package cu.inmobile.wara.Fragments;

import android.support.v4.app.Fragment;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import cu.inmobile.wara.Models.PackageDetail;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Anurag on 4/10/2017.
 */

public class SuperPowerPaymentFragment extends Fragment{

    private ArrayList<String> packageList;
    private ArrayList<PackageDetail> packageDetailList;

    public interface SuperPowerDetailsFetchedListener{
        void onError(Exception e);
        void onResultAvailable(ArrayList<String> packageList, ArrayList<PackageDetail> packageDetailList);
    }

    public SuperPowerPaymentFragment(){

    }

    /**
     *
     * @param listener
     */
    protected void GetSuperPowerPackages(final SuperPowerDetailsFetchedListener listener){
        JsonObject json = new JsonObject();
        json.addProperty("type", "superpower");
        Ion.with(getActivity())
                .load(Endpoints.getCreditPackages)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        if (result == null) {
                            listener.onError(e);
                            return;
                        }
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
                                    packageDetail.setCredits(packObj.optString("credits"));
                                    packageDetail.setPackname_name(packObj.optString("package_name"));
                                    packageDetail.setCurrency(packObj.optString("currency"));
                                    packageDetailList.add(packageDetail);
                                    packageList.add(packageDetail.getCredits() + " credits - " + packageDetail.getCurrency() + " " + packageDetail.getAmount());
                                }
                                listener.onResultAvailable(packageList, packageDetailList);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }
}
