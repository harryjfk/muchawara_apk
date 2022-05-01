package cu.inmobile.wara.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import cu.inmobile.wara.Activities.ProfileActivity;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.MainActivity;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anurag on 4/6/2017.
 */
public class HelperFunctions {

    WaraApp waraApp;

    /**
     * This function updates the user about me status from its profile activity.
     * @param activity who called this function.
     */
    public static void UpdateAboutMeStatus(final Activity activity, final String id, final String token){
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        LayoutInflater inf = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inf.inflate(R.layout.alert_edittext, null, false);
        final EditText getInfo = (EditText) view.findViewById(R.id.alertDialog_editText);
        alert.setMessage(R.string.text_dialog_about_message);
        alert.setTitle(R.string.text_dialog_about_tittle);
        alert.setView(view);
        alert.setCancelable(false);

        alert.setPositiveButton(R.string.btn_dialog_about_update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newUpdate = getInfo.getText().toString();
                dialog.dismiss();
                CallApiToUpdateAboutMeInfo(activity, newUpdate);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });

        alert.show();
    }

    /**
     * This function updates the user's about me status at the server.
     * @param activity who called this function
     * @param newUpdate is new updated message
     */
    private static void CallApiToUpdateAboutMeInfo(final Activity activity, final String newUpdate){

        if (! HelperMethods.isNetworkAvailable(activity)){
            Toast.makeText(activity, R.string.toast_no_cnx , Toast.LENGTH_SHORT).show();
            return;
        }

        /*final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle(R.string.text_dialog_updating_tittle);
        progress.setMessage(activity.getString(R.string.text_dialog_updating_message));
        progress.show();
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("about_me", newUpdate );

        Ion.with(activity)
                .load(Endpoints.updateAboutMe)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("About me Updated", result.toString());
                        progress.dismiss();

                        if(result == null){
                            //ShowSimpleDialogMessage(activity, "Something went wrong", "Something is not right, please try again");
                            Toast.makeText(activity, R.string.toast_no_cnx , Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try{
                            JSONObject obj = new JSONObject(result.toString());
                            if(obj.getString("status").equals("success")){
                                //ShowSimpleDialogMessage(activity, "Congrats", "Your about me status was successfully updated");
                                Toast.makeText(activity, R.string.toast_dialog_updating , Toast.LENGTH_SHORT).show();
                                //Updating the about me status on the UI.
                                if(activity instanceof ProfileActivity){
                                    ProfileActivity act = (ProfileActivity) activity;
                                    act.UpdateAboutMeStatus(newUpdate);
                                    return;
                                }
                            }
                        }catch(JSONException eq){

                        }
                        //ShowSimpleDialogMessage(activity, "Something went wrong", "Something is not right, please try again");
                        Toast.makeText(activity, R.string.toast_dialog_updating_error , Toast.LENGTH_SHORT).show();

                    }
                });*/
    }

    /**
     * This function shows the simple dialog box.
     * @param activity who called this function
     * @param title of the dialog box
     * @param message of the dialog box
     */
    public static void ShowSimpleDialogMessage(Activity activity, String title, String message){
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                        //DisplayActivity.getInstance().UpdateCreditsCount(WaraApp.balas);
                    }})
                .show();
    }

    /**
     * This function converts the seconds into proper time
     * @param seconds
     * @return the proper time in string format
     */
    public static String ConvertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    /**
     * This function converts the xml dp to the screen pixels
     * @param context who called this method
     * @param dp to convert to pixels
     * @return
     */
    public static int ConvertDpToPx(Activity context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    /**
     *
     * Checks whether user is authenticated or not, if not takes user to main login screen.
     * @param obj
     * @return true is user is authenticated, else false
     */
    public static boolean IsUserAuthenticated(JSONObject obj, Activity activity){

       try{
           if(obj == null) {
                Log.d("AUTH OBJ", "AUTH OBJ IS NULL");
               return false;
           }
           //Log.d("IsUserAuthenticated", "" + obj.toString(2));
           String status = obj.getString("status");
           if(status.equalsIgnoreCase("error")){
               //JSONArray arr = obj.getJSONArray("error_data");
               JSONObject ob = obj.getJSONObject("error_data");
               String authText = ob.getString("error_text");
               if(authText.equalsIgnoreCase("Authentication Error")){
                   WaraApp waraApp = (WaraApp) activity.getApplication();
                   waraApp.signOut();
                   //activity.startActivity(new Intent(activity, MainActivity.class));
                   Toast.makeText(activity, activity.getString(R.string.toast_auth_error), Toast.LENGTH_SHORT).show();
                   return false;
               }
               Log.d("Authenticated", "Authenticated");
               return false;
            }
           Log.d("Authenticated", "Authenticated");
       }catch(JSONException e){
           Log.d("Auth Exception", e.toString());
           return true;
       }
    return false;
    }
}
