package cu.inmobile.wara.Networking;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Pojo.CityApi;
import cu.inmobile.wara.Pojo.Message;
import cu.inmobile.wara.Pojo.MessageConfirmed;
import cu.inmobile.wara.Pojo.MessageUserApi;
import cu.inmobile.wara.Pojo.ReceivedMessageApi;
import cu.inmobile.wara.Pojo.Related;
import cu.inmobile.wara.Pojo.Result;
import cu.inmobile.wara.Pojo.ResultUser;
import cu.inmobile.wara.Pojo.SendMessageApi;
import cu.inmobile.wara.Pojo.UnrecievedMessagesApi;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.City;
import cu.inmobile.wara.RoomModels.User;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO Crear un método que maneje cada vez que haya un fallo en la llamada WS

public abstract class WSWorker {

    private static final String TAG = "-- WSWorker";

    public static APIInterface apiInterface= APIClient.getClient().create(APIInterface.class);


    public WSWorker (){
    }

    /*public static void getAssetsCities (){
        try {
            AssetManager assetManager = getAssets();
            InputStream ims = assetManager.open("cities.json");

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(ims);

            CityApi gsonObj = gson.fromJson(reader, CityApi.class);

        }catch(IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void getCities (final List <City> dataListCities){
        //APIInterface apiInterface= APIClient.getClient().create(APIInterface.class);
        Call<CityApi> call = apiInterface.doGetCities();
        call.enqueue(new Callback<CityApi>() {

            @Override
            public void onResponse(Call<CityApi> call, Response<CityApi> response) {

                Log.d ("-- WSWorker" , "getCities().onResponse: " + response.message() );


                CityApi parsed = response.body();
                CityApi.SuccessData data = parsed.getSuccessData();
                ArrayList<CityApi.City> onlineListCity = ( ArrayList<CityApi.City> )data.getCity();

                if ( onlineListCity.size() > dataListCities.size() ) {

                    Log.d("--- WSWorker","getCities() -- updating cities");

                    WaraApp.cityRepo.deleteAll();

                    for (int i = 0 ; i < onlineListCity.size() ; i++) {
                        WaraApp.cityRepo.insert(new City(null, onlineListCity.get(i).getName()));
                    }

                }else
                    Log.d("--- WSWorker","getCities() -- No need to update");
            }

            @Override
            public void onFailure(Call<CityApi> call, Throwable t) {
                Log.d ("-- WSWorker" , "getCities().onFailure: " + t.getMessage()  );

                call.cancel();
            }
        });

    }

    public static void setReceivedMessage (String messageId){

        Log.d ("-- WSWorker" , "setReceivedMessage()" );


        //APIInterface apiInterface= APIClient.getClient().create(APIInterface.class);
        Call<ReceivedMessageApi> call = apiInterface.setReceivedMessage( WaraApp.id , WaraApp.token , messageId);
        call.enqueue(new Callback<ReceivedMessageApi>() {

            @Override
            public void onResponse(Call<ReceivedMessageApi> call, Response<ReceivedMessageApi> response) {

                Log.d ("-- WSWorker" , "setReceivedMessage().onResponse: " + response.message() );


                /*CityApi parsed = response.body();
                CityApi.SuccessData data = parsed.getSuccessData();
                ArrayList<CityApi.City> onlineListCity = ( ArrayList<CityApi.City> )data.getCity();

                if ( onlineListCity.size() > dataListCities.size() ) {

                    Log.d("--- WSWorker","getCities() -- updating cities");

                    WaraApp.cityRepo.deleteAll();

                    for (int i = 0 ; i < onlineListCity.size() ; i++) {
                        WaraApp.cityRepo.insert(new City(null, onlineListCity.get(i).getName()));
                    }

                }else
                    Log.d("--- WSWorker","getCities() -- No need to update");*/
            }

            @Override
            public void onFailure(Call<ReceivedMessageApi> call, Throwable t) {
                Log.d ("-- WSWorker" , "setReceivedMessage().onFailure: " + t.getMessage()  );

                call.cancel();
            }
        });

    }

    public static void getUnrecievedMessages (final cu.inmobile.wara.Utils.Callback callback){
        Log.i (TAG , "getUnrecievedMessages()");

        //APIInterface apiInterface= APIClient.getClient().create(APIInterface.class);
        Call<UnrecievedMessagesApi> call = apiInterface.getUnrecievedMessage( WaraApp.id , WaraApp.token );
        if (!call.isExecuted())
        call.enqueue(new Callback<UnrecievedMessagesApi>() {

            @Override
            public void onResponse(Call<UnrecievedMessagesApi> call, Response<UnrecievedMessagesApi> response) {

                Log.d (TAG , "getUnrecievedMessages().onResponse: " + response.message() );

                try {

                    UnrecievedMessagesApi parsed = response.body();

                    if (parsed.getStatus().equals("error")) {
                        if (parsed.getError().equals("Autentication_error")) {
                            Log.e(TAG, "getUnrecievedMessages().onResponse: Autentication_error");
                            //todo desloguearse... pensar logica para todos los metodos retrofit

                            return;
                        }if (parsed.getError().equals("No hay mensajes")) {
                            Log.e(TAG, "getUnrecievedMessages().onResponse: No hay mensajes");

                            callback.execute();

                            return;
                        }
                    }

                    List<List<Result>> result = parsed.getResult();
                    List<ResultUser> resultUser = parsed.getResultUsers();

//                    if (resultUser.size() < WaraApp.userRepo.getAllUsersList().size()){
//                        WaraApp.messageRepo.deleteAll();
//                        WaraApp.userRepo.deleteAll();
//                    }

                    for (int i = 0; i < resultUser.size(); i++) {
                        ResultUser currentUser = resultUser.get(i);
                        WaraApp.userRepo.insert(new User(
                                currentUser.getId().toString(),
                                currentUser.getSlugName() + "@muchawara.com",
                                currentUser.getName(),
                                25,
                                currentUser.getProfilePicUrl(),
                                currentUser.getStatus(),
                                currentUser.getAboutme(),
                                true,
                                "",
                                "",
                                ""
                        ));
                        List<Result> currentMessagesList = result.get(i);
                        for (int j = 0; j < currentMessagesList.size(); j++) {
                            Result currentMessage = currentMessagesList.get(j);
                            //currentMessage.
                            cu.inmobile.wara.RoomModels.Message newMessage = new cu.inmobile.wara.RoomModels.Message(
                                    currentMessage.getMessageID(),
                                    currentMessage.getFromJID(),
                                    currentMessage.getToJID(),
                                    currentMessage.getBody(),
                                    currentMessage.getSentDate(),//currentMessage.getOriginSentdate(),
                                    currentMessage.getMessageToken(),
                                    0,
                                    1,
                                    Integer.parseInt(currentMessage.getReaded()),
                                    ((currentUser.getId().toString().equals(currentMessage.getToJID()) ? true : false)),
                                    false,
                                    false
                            );
                            WaraApp.messageRepo.insert(newMessage, null);
                        }
                    }
                    callback.execute();

                }catch (Exception e){
                    Log.e(TAG, "getUnrecievedMessages() ERROR: " + e.getMessage());

                }

            }

            @Override
            public void onFailure(Call<UnrecievedMessagesApi> call, Throwable t) {
                Log.d ("-- WSWorker" , "getUnrecievedMessages().onFailure: " + t.getMessage()  );

                call.cancel();
            }
        });

    }


    public static void getMessages (String date){

        //APIInterface apiInterface= APIClient.getClient().create(APIInterface.class);
        Call<MessageUserApi> call = apiInterface.getMessageHistory( WaraApp.id , WaraApp.token , date );

        Log.d("-- WSWorker","getMessages() - url: " + call.request().url());


        call.enqueue(new Callback<MessageUserApi>() {
            @Override
            public void onResponse(Call<MessageUserApi> call, Response<MessageUserApi> response) {

                Log.d("-- WSWorker","getMessages().onResponse " + response.body().getStatus());


                try {


                    //TODO Al leer los datos revisar total_unread_messages_count para posible notificacion
                if (response.body().getStatus().equals("error")) {
                    if (response.body().getError().equals("Autentication_error")) {
                        Log.e(TAG, "getUnrecievedMessages().onResponse: Autentication_error");
                        //todo desloguearse... pensar logica para todos los metodos retrofit

                        return;
                    }if (response.body().getError().equals("No hay mensajes")) {
                        Log.e(TAG, "getUnrecievedMessages().onResponse: No hay mensajes");

                        return;
                    }
                }
                if(response.body().getStatus().equals("success")) {

                    List<Related> relateds = response.body().getUsers().getRelated();

                    if (relateds.size() < WaraApp.userRepo.getAllUsersList().size()){
                        WaraApp.messageRepo.deleteAll();
                        WaraApp.userRepo.deleteAll();
                    }

                    if (relateds != null && relateds.size() > 0) {
                        for (Related related : relateds) {
                            Log.d("-- WSWorker", "getMessages().onResponse - user " + related.getName());

                            WaraApp.userRepo.insert(new User(
                                    related.getId(),
                                    related.getChatUser(),
                                    related.getFullname(),
                                    related.getAge(),
                                    related.getProfilePicture().split("/")[3],
                                    related.getState(),
                                    related.getAboutme(),
                                    true,
                                    related.getFullcity(),
                                    related.getPopularity(),
                                    related.getUsername()
                            ));


                            List<Message> messagesPojo = related.getMessages();
                            Integer new_sms = 0;
                            for (Message mes : messagesPojo) {
                                Log.d("-- WSWorker", "getMessages().onResponse - mesID: " + mes.getId());
                                // Log.d("-- WSWorker","EXISTE EN BD: " + WaraApp.messageRepo.getMesIdFromUserID(mes.getId()));
                                if (!WaraApp.messageRepo.getMesIdFromID(mes.getId())) {

                                    WaraApp.messageRepo.insert(new cu.inmobile.wara.RoomModels.Message(
                                            mes.getId(),
                                            mes.getFromUser(),
                                            mes.getToUser(),
                                            mes.getText(),
                                            mes.getDate(),
                                            mes.getToken(),
                                            1,
                                            Integer.parseInt(mes.getRecieved()),
                                            Integer.parseInt(mes.getReaded()),
                                            ((related.getSystemId().equals(mes.getToUser()) ? true : false)),
                                            false,
                                            false
                                    ), null);
                                    new_sms++;


                                } else {
                                    Log.d("-- WSWorker", "getMessageByID -Esste esta en BD: " + mes.getId());

                                }


                            }
                            Log.d("-- WSWorker", "NUEVOS SIN LEER : " + new_sms);
                        }
                    }

                    WaraApp.lastUpdate = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
                }
                else
                    {
                        Log.d("-- WSWorker", "getMessage() ERROR: " + response.body().getError());
                    }
                }catch (Exception e){
                    Log.e(TAG, "getUnrecievedMessages() ERROR: " + e.getMessage());

                }
            }
            @Override
            public void onFailure(Call<MessageUserApi> call, Throwable t) {
                Log.d("-- WSWorker","getMessages().onResponse - error: " + t.getMessage() );
            }
        });
    }

    public static void sendMessage (final cu.inmobile.wara.RoomModels.Message mes){
        //APIInterface apiInterface= APIClient.getClient().create(APIInterface.class);
        Call<SendMessageApi> call = apiInterface.sendMessage(WaraApp.id , WaraApp.token , mes.getToken() , mes.getTo() , mes.getBody() , mes.getTime() );

        Log.d ("-- WSWorker" , "sendMessage(): " + call.request().toString() );

        call.enqueue(new Callback<SendMessageApi>() {

            @Override
            public void onResponse(Call<SendMessageApi> call, Response<SendMessageApi> response) {

                Log.d ("-- WSWorker" , "sendMessage().onResponse: " + response.body().getResult() );

                //TODO Poner el mensaje en enviado

                MessageConfirmed messageResult = response.body().get0().get(0);

                cu.inmobile.wara.RoomModels.Message messageToUpdate = new cu.inmobile.wara.RoomModels.Message(
                        (messageResult.getMessageID()),
                        messageResult.getFromJID(),
                        messageResult.getToJID(),
                        mes.getBody(),
                        messageResult.getSentDate(),
                        messageResult.getToken(),
                        1,
                        0,
                        0,
                        true ,
                        false,
                        false
                );

                WaraApp.messageRepo.insert(messageToUpdate, null);

            }

            @Override
            public void onFailure(Call<SendMessageApi> call, Throwable t) {
                Log.d ("-- WSWorker" , "sendMessage().onFailure: " + t.getMessage()  );

                call.cancel();
            }
        });

    }

    public static void updateFilter () {

        Log.d("-- WSWorker","updateFilter()");


        if (! HelperMethods.isNetworkAvailable(WaraApp.getMy_context() )){
            Toast.makeText(WaraApp.getMy_context() , WaraApp.getMy_context().getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
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

                                Log.d("-- WSWorker","updateFilter().onCompleted() - OK");


                            } else {
                                JSONObject obj = jsonObject.getJSONObject("error_data");

                                Log.d("-- WSWorker","updateFilter().onCompleted() - ERROR" + obj.optString("error_text"));

                                //Toast.makeText(FilterActivity.this, obj.optString("error_text"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            Log.d("-- WSWorker","updateFilter().catch() - ERROR" + e1.getMessage());

                            e1.printStackTrace();

                        }
                    }
                });

    }



}
