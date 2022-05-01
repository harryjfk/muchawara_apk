package cu.inmobile.wara.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cu.inmobile.wara.Adapters.MessageListAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Models.MessageThread;
import cu.inmobile.wara.R;
import cu.inmobile.wara.RoomModels.MessageViewModel;
import cu.inmobile.wara.RoomModels.User;
import cu.inmobile.wara.RoomModels.UserViewModel;
import cu.inmobile.wara.Services.ChatService;
import cu.inmobile.wara.Services.WaraService;

public class ChatHistoryActivity extends AppCompatActivity {

    private ArrayList<MessageThread> messageThreadArrayList;
    private LinearLayout no_message_layout;
    private RecyclerView chat_list;
    private MessageListAdapter messageListAdapter;
    private ImageView imgBack;
    private Button btnWara ;

    private UserViewModel userViewModel;
    private MessageViewModel messageViewModel;

    private WaraApp waraApp;
    ChatService chatService;

    WaraService waraService;
    boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Log.d("--- ChatHistoryActivity" , "onServiceConnected()");

            ChatService.ActivityBinder activityBinder = (ChatService.ActivityBinder) service ;
            chatService = activityBinder.getService();

            chatService.getTOMessages();

            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            WaraService.LocalBinder binder = (WaraService.LocalBinder) service;
//            waraService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("--- ChatHistoryActivity" , "onServiceDisconnected()");

            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        WaraApp.setCurrentActivity(this);
        WaraApp.setMy_context(this);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniVariables();
        settingInterface();
        setUpRecyclerView();

        //TODO tener en cuenta como usar el serviceReciever
        waraApp.iniService(null);

        // Mando a enviar mensajes sin enviar, el parametro 0 es para que busque por todos los usuarios.
        //messageViewModel.sendunsendedMessage("0");

        //getAllMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            Log.d("--- ChatHistoryActivity" , "unbind()" );
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void iniVariables (){
        Log.d("--- ChatHistoryActivity" , "iniVariables()");

        waraApp = (WaraApp) getApplication();


        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getmAllUsersLive("0").observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                Log.d("--- ChatHistoryActivity" , "onChanged() - users= " + users.size());

                if (users != null && users.size()>0){

                    messageThreadArrayList.clear();

                    for (int i =0 ; i < users.size() ; i ++){

                        Log.d("--- ChatHistoryActivity" , "onChanged() profilePic: " + users.get(i).getProfilePicture());



                        //TODO Corregir la consulta. Estoy trabajando en el hilo principal
                        // Esto me da error de que no puedo hacerlo en el hilo principal, debo intentarlo con el LIVE o buscar una manera de hacerlo con el list

                        List<cu.inmobile.wara.RoomModels.Message> lastMessageList = messageViewModel.getLastMessageByUser(users.get(i).getChatUser());

                        cu.inmobile.wara.RoomModels.Message lastMes = null;
                        Calendar calendar = Calendar.getInstance();


                        if (lastMessageList != null && lastMessageList.size() > 0){

                            lastMes = lastMessageList.get(0);
                            calendar.setTimeInMillis(Long.parseLong(lastMes.getTime()));

                        }

                        /*Log.d("--- ChatHistoryActivity" , "onChanged() - USER: " + users.get(i).getName());
                        Log.d("--- ChatHistoryActivity" , "onChanged() - TIME: " + calendar.getTime());
                        Log.d("--- ChatHistoryActivity" , "onChanged() - MILISEC: " + calendar.getTimeInMillis());*/
                        messageThreadArrayList.add(new MessageThread(
                                users.get(i).getId(),
                                (lastMes != null ? lastMes.getBody() : ""),
                                null,
                                users.get(i).getName(),
                                users.get(i).getProfilePicture(),
                                "",
                                (lastMes != null ? formatter.format(calendar.getTimeInMillis()) : ""), //TODO Hay un objeto que trae la fecha del ultimo mensaje
                                users.get(i).getAboutMe(),
                                users.get(i).getState().equals("online") ? 1 : 0, //TODO poner el valor real de online
                                (lastMes != null ? lastMes.getReaded() == 1 : false)

                        ));
                        Log.d("messageThreadArrayList" ,
                                    " receiver_id: " + messageThreadArrayList.get(i).getId() +
                                        " encounter_id: " +  messageThreadArrayList.get(i).getUserId() +
                                        " contact_id: " +  messageThreadArrayList.get(i).getContactId() +
                                        " receiverName: " +  messageThreadArrayList.get(i).getName() +
                                        " AboutMe: " +  messageThreadArrayList.get(i).getUserAboutMe() +
                                        " receiverImagePerfil" +  messageThreadArrayList.get(i));

                        Log.d("--- ChatHistoryActivity" , "onChanged() - after add messageThread");
                    }
                    //ordenar la lista
                    BubbleSort(formatter);

                    /*messageThreadArrayList.sort(new Comparator<MessageThread>() {
                        @Override
                        public int compare(MessageThread o1, MessageThread o2) {
                            Date date1 = new Date();
                            Date date2 = new Date();
                            try {
                                date1 = formatter.parse(o1.getDate());
                                date2 = formatter.parse(o2.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return date1.getTime() <= date2.getTime() ? -1 : 1;
                        }
                    });*/
                }
            messageListAdapter.notifyDataSetChanged();

                if (messageThreadArrayList.isEmpty()) {
                    no_message_layout.setVisibility(View.VISIBLE);

                } else {
                    no_message_layout.setVisibility(View.GONE);

                }

            }
        });


        messageThreadArrayList = new ArrayList<>();
    }

    private void bindService (){
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtra("chatUser" , "");
        boolean a = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("-- ChatHistoryActivity","bindService() : binded:" + a );

        mBound = true;
    }


    private void settingInterface (){

        chat_list = (RecyclerView) findViewById(R.id.rv_chat_history);
        no_message_layout = (LinearLayout) findViewById(R.id.ll_not_messages);
        imgBack =  (ImageView) findViewById(R.id.img_back);
        btnWara = (Button) findViewById(R.id.btn_back_wara);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaraApp.getMy_context(), DisplayActivity.class);
                WaraApp.getMy_context().startActivity(intent);
            }
        });

        btnWara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaraApp.getMy_context(), DisplayActivity.class);
                WaraApp.getMy_context().startActivity(intent);
            }
        });
    }

//    private void getAllMessages() {
//
//        if (! HelperMethods.isNetworkAvailable(this)){
//            Toast.makeText(this, getString(R.string.toast_no_cnx ), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, getString(R.string.text_loading_chat));
//        progressDialog.setCancelable(true);
//        progressDialog.show();
//
//
//        //TODO se consume el servicio sin el envio del TOKEN
//        JsonObject json = new JsonObject();
//        json.addProperty("id", WaraApp.id);
////      json.addProperty(Const.Params.TOKEN, WaraApp.token);
//        final  Context c = this;
//        Ion.with(this)
//                .load(Endpoints.getMessageUrl+"?id="+WaraApp.id)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//
//
//
//                        if (result == null)
//                        {
//                            progressDialog.dismiss();
//                            return;
//                        }
//
//                        try {
//                            final JSONObject jsonObject = new JSONObject(result.toString());
//
//                            /**
//                             * Logging out user if authentication fails, if user has logged in his/her account
//                             * on some other device as well.
//                             */
//                            if(HelperFunctions.IsUserAuthenticated(jsonObject, ChatHistoryActivity.this)){
//                                return;
//                            }
//
//                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
//
//
//                                JSONObject userObject = jsonObject.optJSONObject("users").optJSONObject("user");
//
////                                String name=userObject.optString("name");
//
//                                /*XMPPConstant.getConnection(c,(new XMPPCallback<Object>() {
//
//                                    public void execute(final Object result, XMPPTCPConnection connection) {
//
//                                //        if(connection==null)
//                                 //           return;
//
//*/
//                                JSONArray messageArray = jsonObject.optJSONObject("users").optJSONArray("related");
//
//                                try{
//                                    Log.d ("CH/getAllMessages" , messageArray.toString(2) );
//
//                                    if (messageArray != null) {
//                                        for (int i = 0; i < messageArray.length(); i++) {
//                                        MessageThread messageThread = new MessageThread();
//                                        userObject = messageArray.getJSONObject(i);
//                                        //i = i;
/////
//                                        messageThread.setUserId(userObject.optString("system_id"));
//                                        messageThread.setName(userObject.optString("fullname"));
//                                        messageThread.setPicture(Endpoints.baseRoot+userObject.optString("profile_picture"));
//
//                                        try
//
//                                        {
//                                            JSONObject last_msg= userObject.optJSONObject("last_msg");
//                                            if(last_msg!=null)
//                                            {
//                                                messageThread.setLastMessage(last_msg.getString("text"));
//                                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                                                String date =userObject.optString("last_time");
//                                                Long date_value = Long.parseLong(date);
//                                                if(date.length()==10)
//                                                    date_value = date_value*1000;
//
//                                                String dateString = formatter.format(new Date(date_value));
//
//                                                messageThread.setDate(dateString);
//                                            }
//                                            else
//
//                                                messageThread.setLastMessage("");
//
//                                        }
//                                        catch (JSONException ex)
//                                        {
//                                            Log.e("---ChatHistory" , "ERROR Last Message" + ex.getMessage());
//                                        }
//
//                                        messageThread.setOnline(userObject.optString("state").equals("online")?1:0);
//
//                                        messageThread.setContactId(userObject.optString("contact_id"));
//                                        messageThread.setUserAboutMe(userObject.optString("aboutme"));
//                                        messageThread.setUnread(userObject.optInt("total_unread_messages_count") > 0);
//                                        messageThreadArrayList.add(messageThread);
//                                    }
//                                                runOnUiThread(new Runnable() {
//                                                    public void run() {
//                                                        progressDialog.hide();
//                                                        messageListAdapter.notifyDataSetChanged();
//
//                                                        if (messageThreadArrayList.isEmpty()) {
//                                                            no_message_layout.setVisibility(View.VISIBLE);
//
//                                                        } else {
//                                                            no_message_layout.setVisibility(View.GONE);
//
//                                                        }
//
//                                                        // RUN THE CODE WHICH IS GIVING THAT EXCEPTION HERE
//
//                                                    }
//                                                });
//
//                                }
//                                        }
//                                        catch (JSONException exp)
//                                        {
//                                            Log.e("---ChatHistory" , "ERROR get message" + exp.getMessage());
//
//                                        }
//
//                                    /*}
//                                }),(new FutureCallback<JsonObject>() {
//                                    @Override
//                                    public void onCompleted(final Exception e, JsonObject result) {
//                                        // do stuff with the result or error
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                Toast.makeText(ChatHistoryActivity.this, e.getMessage() , Toast.LENGTH_LONG).show();
//                                                progressDialog.hide();
//                                                // RUN THE CODE WHICH IS GIVING THAT EXCEPTION HERE
//
//                                            }
//                                        });
//                                    }
//                                })); */
////
//
//                            }
//
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//
//    }

    private void setUpRecyclerView() {
        messageListAdapter = new MessageListAdapter(messageThreadArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chat_list.setLayoutManager(mLayoutManager);
        chat_list.setItemAnimator(new DefaultItemAnimator());
        chat_list.setAdapter(messageListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void BubbleSort(SimpleDateFormat formatter){
        Date date1;
        Date date2;

        ArrayList<MessageThread> noChatBefore = new ArrayList<>();
        int noChatCount = 0;

        for(int i = 0; i < messageThreadArrayList.size(); i++){
            if(messageThreadArrayList.get(i).getDate() == ""){
                noChatBefore.add(messageThreadArrayList.get(i));
                noChatCount++;
            }
        }

        MessageThread[] arrTemp = new MessageThread[messageThreadArrayList.size() - noChatCount];
        int c = 0;

        for (int i = 0; i < messageThreadArrayList.size(); i++){
            if(messageThreadArrayList.get(i).getDate() != ""){
                arrTemp[c] = messageThreadArrayList.get(i);
                c++;
            }
        }

        try {
            for (int i = 0; i < arrTemp.length - 1; i++){
                for (int j = i + 1; j < arrTemp.length; j++){

                    date1 = formatter.parse(arrTemp[i].getDate());
                    date2 = formatter.parse(arrTemp[j].getDate());
                    if(date1.getTime() <= date2.getTime()){

                        MessageThread temp = arrTemp[i];
                        arrTemp[i] = arrTemp[j];
                        arrTemp[j] = temp;
                    }

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        messageThreadArrayList.clear();

        for(int i = 0; i < arrTemp.length; i++){
            messageThreadArrayList.add(arrTemp[i]);
        }

        for (int i = 0; i < noChatBefore.size(); i++){
            messageThreadArrayList.add(noChatBefore.get(i));
        }

    }
}
