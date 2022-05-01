package cu.inmobile.wara.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.ion.Ion;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Pojo.RegisterApi;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperMethods;
import cu.inmobile.wara.Utils.SharedPreferencesUtils;
import cu.inmobile.wara.R;
import retrofit2.Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Anurag on 12/4/17.
 */

public class SetProfilePictureActivity extends AppCompatActivity{

    static final int  REQUEST_IMAGE_CAPTURE = 1;
    private String mTakedPhotoPath;
    private Activity mActivity;

    private TextView text;
    private int PICK_IMAGE = 101;
    private ImageView image;
    private File mainFile;
    private TextView chooseAnother;

    private Button btnUploadPictureGallery;
    private Button btnUploadPictureCamera;
    private Button btnRedo;
    private Button btnContinue;
    private TextView tvLastThing;
    private TextView tvPhotoDone;
    private ImageView imgPreview;
    private LinearLayout llTakePhoto;
    private LinearLayout llPreviewPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        mActivity = this;

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Upload Profile Picture");

        settingInterface();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private void settingInterface (){

        mTakedPhotoPath = null;

        btnUploadPictureCamera = (Button) findViewById(R.id.btn_upload_photo_camera);
        btnUploadPictureGallery = (Button) findViewById(R.id.btn_upload_photo_gallery);
        btnRedo = (Button) findViewById(R.id.btn_again);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        tvLastThing = (TextView) findViewById(R.id.tv_last_thing);
        tvPhotoDone = (TextView) findViewById(R.id.tv_photo_done);
        imgPreview = (ImageView) findViewById(R.id.img_preview);
        llTakePhoto = (LinearLayout) findViewById(R.id.ll_make_photo);
        llPreviewPhoto = (LinearLayout) findViewById(R.id.ll_show_photo);



        Typeface bebaBold = Typeface.createFromAsset(getApplication().getAssets(), "fonts/BebasNeue Bold.otf");
        tvLastThing.setTypeface(bebaBold);
        tvPhotoDone.setTypeface(bebaBold);

        btnUploadPictureCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(mActivity);
            }
        });

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llTakePhoto.setVisibility(View.VISIBLE);
                llPreviewPhoto.setVisibility(View.GONE);

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToServer(mainFile);
                Intent intent = new Intent(SetProfilePictureActivity.this, DisplayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    llPreviewPhoto.setVisibility(View.VISIBLE);
                    llTakePhoto.setVisibility(View.GONE);
                    imgPreview.setImageURI(resultUri);
                    mainFile = new File(resultUri.getPath());
                    WaraApp.imageDrawer = resultUri.getPath();
                    WaraApp.saveSharedPreferences();
                    Log.d("SP/onActivityResult" , "Ok creating File and img uri: " +resultUri.getPath());
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
        }
    }

    /**
     *
     * @param imageFile
     */
    private void sendToServer(File imageFile) {


        if (! HelperMethods.isNetworkAvailable(this)){
            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, "Uploading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

       /* List<Part> files = new ArrayList();
        files.add(new FilePart("profile_picture", imageFile));
        Call<UploadProfilePictureApi> call = WSWorker.apiInterface.uploadProfileImage(WaraApp.id, WaraApp.token);
        Log.d("-- LoginActivity","login() - url: " + call.request().url());*/

        //=========================================//
      //  final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, "Uploading..");
       // progressDialog.setCancelable(false);
      //  progressDialog.show();


        final List<Part> files = new ArrayList();
        files.add(new FilePart("profile_picture", imageFile));
        Ion.with(this)
                .load(Endpoints.updateProfilePictureUrl)
                //.setJsonObjectBody(json)
                .setMultipartParameter(Const.Params.ID, WaraApp.id)
                .setMultipartParameter(Const.Params.TOKEN, WaraApp.token)

                .setMultipartParameter("crop_x", 100 + "")
                .setMultipartParameter("crop_y", 100 + "")
                .setMultipartParameter("crop_width", 100 + "")
                .setMultipartParameter("crop_height", 100 + "")
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
                            Log.d("SP/sendToServer", jsonObject.toString(2)+files);
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                              //  WaraApp.imageUrl=files;
                                Toast.makeText(SetProfilePictureActivity.this, "Profile Picture Set", Toast.LENGTH_SHORT).show();
                                chooseAnother.setVisibility(View.GONE);

                            }
                            text.setText("Proceed");

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.d("Error", e1.toString());
                        }

                    }
                });

    }
}
