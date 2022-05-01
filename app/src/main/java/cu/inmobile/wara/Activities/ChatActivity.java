package cu.inmobile.wara.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cu.inmobile.wara.Adapters.ChatMessageAdapter;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.AdditionalChatState;
import cu.inmobile.wara.Networking.AdditionalChatStateExtension;
import cu.inmobile.wara.Networking.Endpoints;
import cu.inmobile.wara.R;
import cu.inmobile.wara.Receivers.ServiceReceiver;
import cu.inmobile.wara.RoomModels.MessageViewModel;
import cu.inmobile.wara.RoomModels.User;
import cu.inmobile.wara.RoomModels.UserViewModel;
import cu.inmobile.wara.Services.ChatService;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.HelperFunctions;
import cu.inmobile.wara.Utils.SharedPreferencesUtils;
import io.socket.client.Socket;

//TODO Migrar todo de ChatMessage al Message de Room

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ServiceReceiver.Receiver {

    private int START = 0, incrementRate = 20;
    private boolean isMoreDataAvailable = false, isLoadingData = false;

    private List<cu.inmobile.wara.RoomModels.Message> messages;
    private ListView mListView;
    private ImageView mButtonSend, imgBackground, imgProfile, imgStatus, imgTyping, imgBack;
    private EditText mEditTextMessage;
    private SwipyRefreshLayout swipeContainer;
    private TextView tvUserName, tvUserStatus, tvUserDescr;

    private boolean isTyping = false;
    private String lastMessage_Id = "";

    private MessageViewModel messageViewModel;
    private UserViewModel userViewModel;

    private LiveData<User> targetUserLive;

    private ChatMessageAdapter mAdapter;

    private String target_id, receiverId, receiverImageUrl, receiverName, contactId, userAboutMe, slug_name;

    private int receiverStatus;

    private Socket mSocket;

    private Chat currentChat;

    private ServiceReceiver mServiceReceiver;

    private WaraApp waraApp;


    // Binding service attribs
    ChatService chatService;
    boolean mBound = false;


    //Request code for the gift.
    private int GIFT_REQUEST = 101;

    //Request code for image picking.
    private int PICK_IMAGE = 105;

    private android.arch.lifecycle.LifecycleOwner owner;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("--- ChatActivity" , "onCreate()");


        setContentView(R.layout.activity_chat);

        iniVariables();
        settingInterface();
        //messageViewModel.sendunsendedMessage( receiverId );

        waraApp.iniService(mServiceReceiver);

        //checkChatConx();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("--- ChatActivity" , "onStart()");

        bindService();

        //ChatManager chatManager = ChatManager.getInstanceFor(Connection);
        //currentChat = chatManager.createChat(receiverId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("--- ChatActivity" , "onStop()");

        chatService.stopReconnectChatTimer();

        if (mBound) {
            Log.d("--- ChatActivity" , "unbind()" );
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void iniVariables() {
        owner = this;


        waraApp = (WaraApp) getApplication();

        //TODO Hay muchas cosas aqui que puedo extraer de la BD


        messages = new ArrayList<>();
        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        target_id = getIntent().getExtras().getString("target_id");

        Log.d("--- ChatActivity" , "iniVariables() -> target_id: " + target_id );

        User targetUser = userViewModel.getmUser(target_id);

        if (targetUser == null){
            return;
        }

        receiverId = targetUser.getChatUser();
        receiverImageUrl = targetUser.getProfilePicture();
        receiverName = targetUser.getName();
        contactId = "";//targetUser.getChatUser().split("@")[0];
        userAboutMe = targetUser.getAboutMe();
        receiverStatus = targetUser.getState().equals("online") ? 1 : 0;
        Log.d("--- ChatActivity", "onChangedUser() receiverId = " + receiverId);

        userViewModel.getmUserLive(target_id).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User targetUser) {
                Log.d("--- ChatActivity", "onChangedUser() target_id = " + target_id);
                if (targetUser == null){
                    return;
                }
                receiverId = targetUser.getChatUser();
                receiverImageUrl = targetUser.getProfilePicture();
                receiverName = targetUser.getName();
                contactId = "";//targetUser.getChatUser().split("@")[0];
                userAboutMe = targetUser.getAboutMe();
                receiverStatus = targetUser.getState().equals("online") ? 1 : 0;
                Log.d("--- ChatActivity", "onChangedUser() receiverId = " + receiverId);
            }
        });

//        receiverId = getIntent().getExtras().getString("receiverId");
//        receiverImageUrl = getIntent().getExtras().getString("receiverImageUrl");
//        receiverName = getIntent().getExtras().getString("receiverName");
//        contactId = getIntent().getExtras().getString("contact_id");
//        userAboutMe = getIntent().getExtras().getString("aboutme");
//        receiverStatus = getIntent().getExtras().getInt("online");


        messageViewModel.setUnreadMessageByUser(receiverId);
        Log.d("-- CHATACTIVITY", "setUnreadMessageByUser()");
        Log.d("-- USER-RECIVER", " => "+receiverId);
        Log.d("-- CHATACTIVITY", "getUnreadMessageByUser_COUNT()"+messageViewModel.getUnreadMessageByUser_count(WaraApp.chatUser+"@muchawara.com"));



        messageViewModel.getMessageByUserLive(receiverId).observe(this, new Observer<List<cu.inmobile.wara.RoomModels.Message>>() {
            @Override
            public void onChanged(@Nullable List<cu.inmobile.wara.RoomModels.Message> messageslive) {

                Log.d("--- ChatActivity" , "onChanged() - messages = " + messageslive.size());

                //messages = messageslive;
                //mAdapter.notifyDataSetChanged();

                mAdapter = new ChatMessageAdapter(WaraApp.getMy_context(), messageslive );
                mListView.setAdapter(mAdapter);

                //TODO aca debo hacer el READED de los mensajes
            }
        });


        mServiceReceiver = new ServiceReceiver(new Handler());
        mServiceReceiver.setReceiver(this);

        WaraApp.userRepo.setUpdatedById(target_id, false);

                /*if(chatService != null){
            chatService.getIsTyping().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable final Boolean isTyping) {
                    Log.d("--- ChatActivity" , "iniVariables() - isTyping = " + isTyping);
                    if(isTyping){
                        imgTyping.setVisibility(View.VISIBLE);
                    }else{
                        imgTyping.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else{
            Log.d("--- ChatActivity222" , "iniVariables() - chatService = null");
        }*/
    }

    private void settingInterface() {

        mListView = (ListView) findViewById(R.id.listView);

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserDescr = (TextView) findViewById(R.id.tv_descr);

        imgBackground = (ImageView) findViewById(R.id.img_background);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgStatus = (ImageView) findViewById(R.id.img_online);
        imgTyping = (ImageView) findViewById(R.id.img_typing);
        imgProfile = (ImageView) findViewById(R.id.img_user_image);

        swipeContainer = (SwipyRefreshLayout) findViewById(R.id.swipeContainer);

        mButtonSend = (ImageView) findViewById(R.id.img_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);

        tvUserName.setText(receiverName);
        tvUserDescr.setText(userAboutMe);
        Glide.with(this).load(Endpoints.baseNoThumbUrl + receiverImageUrl).into(imgProfile);
        Glide.with(this).load(Endpoints.baseNoThumbUrl + receiverImageUrl).into(imgBackground);
        //Glide.with(this).load(Endpoints.baseNoThumbUrl + receiverImageUrl).into(imgStatus);
        Log.d("-- ChatActivity", "CargaInicial().onClick() " +
                " Slug Name: " + slug_name +
                " encounter_id: " + contactId +
                " contact_id: " + contactId +
                " receiverName: " + receiverName +
                " AboutMe: " + userAboutMe +
                " receiver_id: " + receiverId +
                " receiverImagePerfil" + receiverImageUrl);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaraApp.getMy_context(), ProfileViewActivity.class);
                intent.putExtra("target_id", target_id);
//                intent.putExtra("suggestion_id", contactId);
//                intent.putExtra("slug_name", slug_name);
//                intent.putExtra("receiverImagePerfil", receiverImageUrl);
//                intent.putExtra("receiverImageGalery", receiverImageUrl);
                intent.putExtra("blur_image", false);
                intent.putExtra("from", "ChatActivity");
//                intent.putExtra("receiverName",receiverName);
//                intent.putExtra("contact_id",contactId);
//                intent.putExtra("aboutme",userAboutMe);
                startActivity(intent);
            }
        });
        if (receiverStatus == 0){
            imgStatus.setColorFilter(ContextCompat.getColor(this, R.color.gray));
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaraApp.getMy_context(), ChatHistoryActivity.class);
                startActivity(intent);
            }
        });

        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isTyping)
                    //emitTyping(true);
                    chatService.emitTyping(true);
                    isTyping = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isTyping)
                    //emitStopTyping(true);
                    isTyping = false;
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                Log.d("-- ChatActivity", "buttonSend.onClick() : " + message);
                if (TextUtils.isEmpty(message)) {
                    return;
                }

                //sendTextMessage(message);

                mEditTextMessage.setText("");

                chatService.sendTextMessage(message , receiverId );

            }
        });

        swipeContainer.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                // getUnreadChatHistory();
                START = mListView.getAdapter().getCount();
                //getChatHistory(lastMessage_Id);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new ChatMessageAdapter(this, messages );
        mListView.setAdapter(mAdapter);
    }

    //Chequea que haya conexion y si no crea una para despues iniciar un socket

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            ChatService.ActivityBinder activityBinder = (ChatService.ActivityBinder) service ;
            chatService = activityBinder.getService();

            Log.d("-- ChatActivity","chatService.getIsTyping() = " + chatService.getIsTyping().getValue());
            chatService.getIsTyping().observe(owner, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable final Boolean isTyping) {
                    Log.d("--- ChatActivity" , "iniVariables() - isTyping = " + isTyping);
                    if(isTyping){
                        imgTyping.setVisibility(View.VISIBLE);
                    }else{
                        imgTyping.setVisibility(View.INVISIBLE);
                    }
                }
            });


            chatService.setChatUser(receiverId);
            //Activar el Timer de reconeccion cada 3 segundos
            chatService.startReconnectChatTimer();
            //chatService.connectChatServer();
            chatService.getTOMessages();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("-- ChatActivity","onServiceDisconnected()");


        }
    };


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        Log.d("-- ChatActivity" , "onReceiveResult()" + resultData.get("state"));

        imgTyping.setVisibility(View.VISIBLE);

    }
    private void bindService (){
        Log.i("-- ChatActivity","bindService() : " + receiverId );

        Intent intent = new Intent(WaraApp.getMy_context() , ChatService.class);
        intent.putExtra("chatUser" , receiverId);
        boolean a = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("-- ChatActivity","bindService() : binded:" + a );

        mBound = true;
    }

    private void sendTextMessage(String message) {

        Log.d("-- ChatActivity","sendTextMessage() :" + message);


        cu.inmobile.wara.RoomModels.Message mes = new cu.inmobile.wara.RoomModels.Message(
                null,
                WaraApp.id,
                receiverId,
                message,
                String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()),
                null,
                1,
                0,
                0,
                true,
                false,
                false

        );

        try{

            Message messageToSend = new Message();
            messageToSend.setBody(mes.getBody());
            DefaultExtensionElement tokenExtenxion = new DefaultExtensionElement("token","urn:xmpp:token");
            tokenExtenxion.setValue("value" , mes.getToken());
            messageToSend.addExtension(tokenExtenxion);


            currentChat.sendMessage(messageToSend);

            messageViewModel.insert(mes);

            Log.d("-- ChatActivity","sendTextMessage().messageSended () " );


        }catch (SmackException.NotConnectedException e){
            Log.e("-- ChatActivity","sendTextMessage().NotConnectedException: " + e.getMessage());

            mes.setSended(0);
            messageViewModel.sendMessage(mes);

        }catch (Exception ex){
            Log.e("-- ChatActivity","sendTextMessage().Exception: " + ex.getCause().toString()+ " " +ex.getMessage());

            mes.setSended(0);
            messageViewModel.sendMessage(mes);
        }
//
    }

    private void emitTyping(boolean tryAgain) {
        Log.d("-- ChatActivity","emitTyping()");

        //TODO corregir... esto envia muchas veces en falso


        Message msg = new Message();
        msg.setBody(null);
        msg.addExtension(new ChatStateExtension(ChatState.composing));
        try{
            currentChat.sendMessage(msg);

        } catch (SmackException.NotConnectedException e) {
            Log.e("-- ChatActivity","emitTyping().NotConnectedException: " + e.getMessage());
            if (tryAgain){
                //checkChatConx();
                emitTyping(false);
            }
        }catch (Exception e){
            Log.e("-- ChatActivity","emitTyping().Exception: " + e.getCause().toString() + "  " +e.getMessage());
            if (tryAgain){
                //checkChatConx();
                //emitTyping(false);
            }
        }
    }

    private void emitStopTyping(boolean tryAgain) {
        Log.d("-- ChatActivity","emitStopTyping()");

        Message msg = new Message();
        msg.setBody(null);
        msg.addExtension(new ChatStateExtension(ChatState.paused));
        try{
            currentChat.sendMessage(msg);


        } catch (SmackException.NotConnectedException e) {
            Log.e("-- ChatActivity","emitStopTyping().NotConnectedException: " + e.getMessage());

            if (tryAgain){
                //checkChatConx();
                //emitStopTyping(false);

            }

        }catch (Exception e){
            Log.e("-- ChatActivity","emitStopTyping().Exception: " + e.getMessage());

            if (tryAgain){
                //checkChatConx();
                //emitStopTyping(false);
            }

        }

    }


    //TODO Eliminar
    public void refreshState (Bundle stateBundle){

        Log.d("-- ChatActivity", "refreshState()");
        mAdapter.notifyDataSetChanged();

    }






    private cu.inmobile.wara.RoomModels.Message getChatMessageFromToken(String token) {

        for (int i = 0; i < mAdapter.getCount(); i++)
            if (mAdapter.getItem(i).getToken().equals(token))
                return mAdapter.getItem(i);
        return null;
    }

    // Avisa cuando se recibe un mensaje
    // TODO Deberia estar en el servicio, cuando descargue mensajes nuevos notificarlo al servidor

    private void sendRecieved(Message message) {
        Message msg = new Message();
        msg.setBody(null);
        msg.setType(Message.Type.chat);
        msg.setThread(message.getThread());
        msg.setTo(message.getFrom());
        msg.addExtension(new AdditionalChatStateExtension(AdditionalChatState.recieved));
        DefaultExtensionElement tokenExtension = new DefaultExtensionElement(
                "token", "urn:xmpp:token");
        tokenExtension.setValue("value", ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));
        msg.addExtension(tokenExtension);
        try

        {
            getCurrentChat().sendMessage(msg);
        } catch (SmackException.NotConnectedException e) {

        }
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);

        Log.d("CA/getChatHistory", "" + json.toString());

        Ion.with(this)
                .load(Endpoints.readMessages + "?id=" + WaraApp.id + "&token=" + WaraApp.token + "&user=" + receiverId + "&action=recieved&msgtoken=" + ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"))
                .setJsonObjectBody(json)
                .asJsonObject(Charset.defaultCharset())
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result == null) {
                            return;
                        }
                        // do stuff with the result or error
                        Log.d("Chat messages Response", result.toString());


                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, ChatActivity.this)) {
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {


                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    // Avisa cuando lee un mensaje
    // TODO debe estar en el ChatActivity, al iniciar el chat mandar el aviso

    private void sendReaded(Message message) {
        Message msg = new Message();
        msg.setBody(null);
        msg.addExtension(new AdditionalChatStateExtension(AdditionalChatState.readed));
        DefaultExtensionElement tokenExtension = new DefaultExtensionElement(
                "token", "urn:xmpp:token");
        tokenExtension.setValue("value", ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));
        msg.addExtension(tokenExtension);
        try

        {
            getCurrentChat().sendMessage(msg);
        } catch (SmackException.NotConnectedException e) {

        }
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
//        json.addProperty("other_user_id", receiverId);

//        if (!id.equals("")) {
//            json.addProperty("last_message_id", id);
//        }

        Log.d("CA/getChatHistory", "" + json.toString());

        Ion.with(this)
                .load(Endpoints.readMessages + "?id=" + WaraApp.id + "&token=" + WaraApp.token + "&user=" + receiverId + "&action=readed&msgtoken=" + ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"))
                .setJsonObjectBody(json)
                .asJsonObject(Charset.defaultCharset())
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error


                        if (result == null) {
                            return;
                        }
                        Log.d("Chat messages Response", result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, ChatActivity.this)) {
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {


                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public Chat getCurrentChat() {
        //if (currentChat == null)

            //checkChatConx();

        return currentChat;
    }

    //Arranca el CHAT a partir de la conexion ya existente al servidor
    //TODO debe existir un metodo como este en el Servicio, que mantenga actualizado de mensajes y cambios de estado

//    private void InitSockets() {
//
//
//        ChatManager chatManager = ChatManager.getInstanceFor(Connection);
//        final Context c = this;
//        currentChat = chatManager.createChat(receiverId);
//
//
//
//        // Se crea un listener que estara atento a los mensajes y
////        StanzaListener packetListener = new StanzaListener() {
////            @Override
////            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
////
////                if (packet instanceof Message) {
////                    Message message = (Message) packet;
////                    String message_txt = message.getBody();
////                    if (message_txt != null) {
////
////
////                        if (!message_txt.isEmpty()) {
////                            if (message.getFrom().contains(receiverId)) {
////                                message.addExtension(new AdditionalChatStateExtension(AdditionalChatState.recieved));
////                                DefaultExtensionElement tokenExtension = new DefaultExtensionElement(
////                                        "token", "urn:xmpp:token");
////                                message.addExtension(tokenExtension);
////
////
////                                addToReceiveMessage(message_txt, ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"), false, false, -1);
////                                ((ChatActivity) c).sendRecieved(message); //TODO Esto debera ir en el servicio al descargar los mensajes
////                                ((ChatActivity) c).sendReaded(message);
////
////
////                            } else {
////                                WaraApp.showNotification();
////                            }
////                            onTypingStop.call(message);
////
////
////                        }
////                    } else
////
////                    {
////                        String data = message.toString();
////                        if (data.contains("composing"))
////                            onTyping.call(message);
////
////                        if (data.contains("paused"))
////                            onTypingStop.call(message);
////                        if (data.contains("recieved")) {
////                            final ChatMessage t = ((ChatActivity) c).getChatMessageFromToken(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));
////
////                            runOnUiThread(new Runnable() {
////                                public void run() {
////                                    if(t!=null)
////                                    {
////                                        t.setIsRecieved(true);
////                                        t.getView().requestFocus();
////                                        t.getView().requestLayout();
////
////                                    }
////
////                                    ;
////                                }
////                            });
////
////                        }
////                        if (data.contains("readed")) {
////                            final ChatMessage t = ((ChatActivity) c).getChatMessageFromToken(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));
////
////                            runOnUiThread(new Runnable() {
////                                public void run() {
////                                    if(t!=null)
////                                    {
////                                        t.setIsReaded(true);
////                                        t.getView().requestFocus();
////                                        t.getView().requestLayout();
////                                    }
////
////                                    ;
////                                }
////                            });
////                        }
////
////                    }
////
//////                Log.d(TAG, “Message Body : ” + message.getBody());
//////                Log.d(TAG, “MessageFrom : ” + message.getFrom());
////
////                }
////
////
////            }
////
////        };
//
//
//        //El listener se le setea a la conexion
//        //TODO El listener se puede crear en el servicio sin tener que abrir un chat, si me llegan todo slos
//
//
//        Connection.addAsyncStanzaListener(packetListener, null);
//
//        JsonObject json = new JsonObject();
//        json.addProperty("id", WaraApp.id);
//        json.addProperty("user", this.contactId);
//
//        Ion.with(this)
//                .load(Endpoints.status + "?id=" + WaraApp.id + "&user=" + this.contactId)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//
//                        if (result == null)
//                            return;
////                        try {
//                        JsonPrimitive userObj = result.getAsJsonPrimitive("user");
//                        if(result.get("state").getAsString()=="false")
//                            onUserOffline.call(null);
//                            else
//                        onUserOnline.call(null);
//                        ((ChatActivity) c)._userStatus =  result.get("state").getAsString()!="false";
////                                    JSONObject profileObj = userObj.getJSONObject("profile_pics");
////                                    showMatchDialog(userObj.optString("name"), profileObj.optString("encounter"), userObj.optString("id"), successObj.optString("contact_id"));
//
////                        } catch (
////                                ) {
////
////                        }
//
//
//                    }
//                });
//
//
//        //TODO ???????
//
//        final Roster roster = Roster.getInstanceFor(Connection);
//        ;
//        roster.addRosterListener(new RosterListener() {
//
//            public void entriesAdded(Collection<String> param) {
//
//                Log.d("aa", "aaaa");
//            }
//
//            public void entriesDeleted(Collection<String> addresses) {
//
//            }
//
//            public void entriesUpdated(Collection<String> addresses) {
//            }
//
//            public void presenceChanged(Presence presence) {
//
//                String user = presence.getFrom();
//                Presence bestPresence = roster.getPresence(user);
//
////                Log.d(TAG, "BestPresence: " + user + ": " + bestPresence);
////
////                String[] temp = presence.getFrom().split("\\@");
////                Log.d(TAG, "Presence: " + temp[0] + "-" + presence.toString());
//
//                ((ChatActivity) c)._userStatus = presence.getType() == Presence.Type.available;
//                if (presence.getType() == Presence.Type.available)
//                    onUserOnline.call(presence);
//                else
//                    onUserOffline.call(presence);
//                String status = presence.toString();
//                // ShowInfoDialog(temp[0]+"is "+status);
//
//
//            }
//        });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gift_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        //Send Gift
        if (id == R.id.action_send_gift) {
            startActivityForResult(new Intent(ChatActivity.this, GiftActivity.class), GIFT_REQUEST);
        }

        //Send Image
        if (id == R.id.action_send_image) {
            getImageFromGallery();
        }
        return super.onOptionsItemSelected(item);

    }


    /**
     * Fires an Intent to pick image from the gallery
     */
    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        super.onActivityResult(requestCode, result, data);
        if (requestCode == GIFT_REQUEST) {
            if (result == RESULT_OK) {
                //User selected to send some gift to the other person.
                String gift_id = data.getStringExtra("gift_id");
                String gift_name = data.getStringExtra("gift_name");
                String gift_url = data.getStringExtra("gift_url");
                String gift_icon_name = data.getStringExtra("gift_icon_name");
                int price = data.getIntExtra("gift_price", 200);
                sendGift(gift_id, gift_name, gift_url, gift_icon_name, price);
            }
        }

        if (requestCode == PICK_IMAGE) {
            if (result == RESULT_OK) {
                //File file = new File(getPath(data.getData()));
                try {
//                    InputStream selectedImage = getContentResolver().openInputStream(data.getData());
//                    Bitmap bitmap = BitmapFactory.decodeStream(selectedImage);
//                    File mainFile = persistImage(bitmap, String.valueOf(System.currentTimeMillis()));
//                    ChatMessage chatMessage = new ChatMessage("", true, true, false);
//                    chatMessage.setImageBitmao(bitmap);
//                    mAdapter.add(chatMessage);
//                    uploadImage(mainFile);
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * @param bitmap
     * @param name
     * @return
     */
    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        return imageFile;
    }

    /**
     * @param file
     */
    private void uploadImage(File file) {
        String id = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.USER_ID, "");
        String token = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.SESSION_TOKEN, "");

        List<Part> files = new ArrayList();
        files.add(new FilePart("image", file));
        Ion.with(this)
                .load(Endpoints.uploadChatImage)
                .setMultipartParameter(Const.Params.ID, WaraApp.id)
                .setMultipartParameter(Const.Params.ID, WaraApp.token)
                .addMultipartParts(files)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result == null) {
                            Log.e("RESULT IS NULL", e.toString());
                            return;
                        }
                        Log.d("IMAGE RESULT", result.toString());
                        try {
                            JSONObject obj = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(obj, ChatActivity.this)) {
                                return;
                            }

                            if (obj.getString("status").equalsIgnoreCase("success")) {
                                String imageUrl = obj.getString("image_url");
                                Log.d("IMAGE URL", imageUrl);
                                sendImageMessage(imageUrl);
                                //sendTextMessage(imageUrl);
                            }
                        } catch (Exception e2) {

                        }
                    }
                });
    }

    /**
     * @param imageUrl
     */
    private void sendImageMessage(String imageUrl) {
        try {
            JSONObject object = new JSONObject();
            object.put("from_user", WaraApp.id);
            object.put("to_user", receiverId);
            object.put("message_text", imageUrl);
            object.put("contact_id", contactId);
            object.put("message_type", 2);
            mSocket.emit("new_message_sent", object);
            //mSocket.emit("new_message", object);

        } catch (Exception e) {
            Log.d("SPI", e.toString());
            e.printStackTrace();
        }
    }



    private void mapSocketIdToUser(String socket_id) {
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("socket_id", socket_id);
        Log.d("API", "Map socket" + socket_id);
        Ion.with(this)
                .load(Endpoints.mapUserSocket)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null) {
                            Log.d("API", "Result is null");
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, ChatActivity.this)) {
                                return;
                            }
                            Log.d("API", jsonObject.toString());
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                String base_image_chat_url = jsonObject.optString("base_chat_images_url");
                                mSocket.emit("user_socket_mapped", new JsonObject());
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

//    private void getChatHistory(String id) {
//        JsonObject json = new JsonObject();
//        json.addProperty("id", WaraApp.id);
//        json.addProperty("user", receiverId);
////        json.addProperty(Const.Params.TOKEN, WaraApp.token);
////        json.addProperty("other_user_id", receiverId);
//
////        if (!id.equals("")) {
////            json.addProperty("last_message_id", id);
////        }
//
//        Log.d("CA/getChatHistory", "" + json.toString());
//
//        Ion.with(this)
//                .load(Endpoints.messageHistoryUrl + "?id=" + WaraApp.id + "&user=" + receiverId)
//                .setJsonObjectBody(json)
//                .asJsonObject(Charset.defaultCharset())
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        if (result == null) {
//                            return;
//                        }
//                        // do stuff with the result or error
//                        Log.d("Chat messages Response", result.toString());
//                        swipeContainer.setRefreshing(false);
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(result.toString());
//
//                            /**
//                             * Logging out user if authentication fails, if user has logged in his/her account
//                             * on some other device as well.
//                             */
//                            if (HelperFunctions.IsUserAuthenticated(jsonObject, ChatActivity.this)) {
//                                return;
//                            }
//
//                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
//
//
//                                JSONArray messageArray = jsonObject.getJSONArray("messages");
//
//                                for (int i = messageArray.length() - 1; i >= 0; i--) {
//                                    JSONObject object = messageArray.optJSONObject(i);
//                                    //Log.d("SINGLE MESSAGES", object.toString());
//                                    lastMessage_Id = object.optString("id");
//
//                                    //text message type
////                                    if (object.optString("type").equalsIgnoreCase("0")) {
//                                    if (object.optString("sended").equals("1")) {
//                                        addToSendMessage(object.optString("text"), object.optString("token"), object.optString("recieved").equals("1"), object.optString("readed").equals("1"), 0);
//                                    } else {
//                                        addToReceiveMessage(object.optString("text"), object.optString("token"), object.optString("recieved").equals("1"), object.optString("readed").equals("1"), 0);
//                                    }
////                                    }
////                                    //Gift message type
////                                    else if(object.optString("type").equalsIgnoreCase("4")){
////                                        if(object.optString("from_user").equalsIgnoreCase(WaraApp.id)){
////                                            //my sent gift
////                                            Log.d("GIFT SENT", object.getString("meta"));
////                                            addToSentGifts("", "", object.getString("meta"), 0);
////                                        }else{
////                                            //my received gifts
////                                            Log.d("GIFT RECEIVED ", object.getString("meta"));
////                                            addToReceivedGifts("", "", object.getString("meta"), 0);
////                                        }
////                                    }
////                                    //image
////                                    else{
////                                        Log.d("MSG", object.toString());
////                                        if(object.optString("from_user").equalsIgnoreCase(WaraApp.id)){
////                                            //my sent image
////                                            addToSentImage(object.optString("meta"), 0);
////                                        }else{
////                                            //my received image
////                                            addToReceivedImage(object.optString("meta"), 0);
////                                        }
////                                    }
//
//                                }
//                            }
//
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//    }

    /**
     * This function resolves the received gift from other user.
     */
    private void GetGift() {
        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.TOKEN, WaraApp.token);
        json.addProperty("other_user_id", receiverId);

        Ion.with(this)
                .load(Endpoints.messageHistoryUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        Log.d("Chat messages Response", result.toString());
                        swipeContainer.setRefreshing(false);
                        if (result == null) {
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            /**
                             * Logging out user if authentication fails, if user has logged in his/her account
                             * on some other device as well.
                             */
                            if (HelperFunctions.IsUserAuthenticated(jsonObject, ChatActivity.this)) {
                                return;
                            }

                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONArray messageArray = jsonObject.getJSONArray("messages");

                                //for (int i = messageArray.length() - 1; i >= 0; i--)
                                {
                                    JSONObject object = messageArray.optJSONObject(messageArray.length() - 1);
                                    //Log.d("SINGLE MESSAGES", object.toString());
                                    //lastMessage_Id = object.optString("id");

                                    if (object.optString("type").equalsIgnoreCase("4")) {
                                        if (object.optString("from_user").equalsIgnoreCase(WaraApp.id)) {
                                            //my sent gift
                                            Log.d("GIFT SENT", object.getString("meta"));
//                                            addToSentGifts("", "", object.getString("meta"), 10);
                                        } else {
                                            //my received gifts
                                            Log.d("GIFT RECEIVED ", object.getString("meta"));
//                                            addToReceivedGifts("", "", object.getString("meta"), 10);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e1) {

                        }

                    }
                });

    }


    /**
     * @param message
     */


    /**
     * @param imageUrl
     * @param pos
     */
    private void addToSentImage(String imageUrl, int pos) {
//        ChatMessage chatMessage = new ChatMessage("", true, true, false);
//        chatMessage.setImageUrl(imageUrl);
//        if (pos >= 0)
//            mAdapter.insert(chatMessage, pos);
//        else mAdapter.add(chatMessage);
    }

    /**
     * @param imageUrl
     * @param pos
     */
    private void addToReceivedImage(String imageUrl, int pos) {
//        ChatMessage chatMessage = new ChatMessage("", false, true, false);
//        chatMessage.setImageUrl(imageUrl);
//        if (pos >= 0)
//            mAdapter.insert(chatMessage, pos);
//        else mAdapter.add(chatMessage);
    }


    /**
     * @param message
     * @param pos
     */
//    private void addToSendMessage(String message, String token, Boolean recieved, Boolean readed, int pos) {
//        ChatMessage chatMessage = new ChatMessage(message, true, false, false, token, recieved, readed);
//        if (pos >= 0)
//            mAdapter.insert(chatMessage, pos);
//        else mAdapter.add(chatMessage);
//
//    }

    /**
     * @param message
     * @param pos
     */
//    private void addToReceiveMessage(String message, String token, Boolean recieved, Boolean readed, final int pos) {
//        final ChatMessage chatMessage = new ChatMessage(message, false, false, false, token, recieved, readed);
//        runOnUiThread(new Runnable() {
//            public void run() {
//                if (pos >= 0)
//                    mAdapter.insert(chatMessage, pos);
//                else mAdapter.add(chatMessage);
//            }
//        });
//
//    }

    /**
     * This function sends the message to the other user
     *
     * @param id             of the gift to be sent
     * @param name           of the gift to be sent
     * @param gift_url       of the gift to be sent
     * @param gift_icon_name of the gift to be sent
     * @param price          of the gift to sent
     */
    private void sendGift(String id, String name, String gift_url, String gift_icon_name, int price) {
//        try {
//            JSONObject object = new JSONObject();
//            object.put("from_user", WaraApp.id);
//            object.put("to_user", receiverId);
//            object.put("gift_name", name);
//            object.put("gift_url", gift_url);
//            object.put("gift_id", id);
//            object.put("gift_icon_name", gift_icon_name);
//            object.put("price", price);
//            object.put("contact_id", contactId);
//            object.put("message_type", 4);
//            //mSocket.emit("new_message", object);
//            addToSentGifts(id, name, gift_url, 10);
//            addGiftToReceiverProfile(id, "");
//        } catch (Exception e) {
//            Log.d("SPI", e.toString());
//            e.printStackTrace();
//        }
    }

    /**
     * This function adds this gift to the receiver's profile to view.
     *
     * @param gift_id to add
     * @param msg     to add(optional)
     */
    private void addGiftToReceiverProfile(String gift_id, String msg) {
        String id = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.USER_ID, "");
        String token = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.SESSION_TOKEN, "");

        JsonObject json = new JsonObject();
        json.addProperty(Const.Params.ID, WaraApp.id);
        json.addProperty(Const.Params.ID, WaraApp.token);
        json.addProperty("gift_id", gift_id);
        json.addProperty("gift_receiver_id", receiverId);
        Ion.with(ChatActivity.this)
                .load(Endpoints.sendGift)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            Log.d("GIFT", result.toString());
                            try {
                                JSONObject res = new JSONObject(result.toString());
                                boolean success = res.getBoolean("success");
                                if (success) {
                                    int balance = res.getInt("user_credit_balance");
                                    WaraApp.balas = balance;
                                }
                            } catch (JSONException e1) {

                            }
                        }
                    }
                });
    }

    /*
     * @param id
     * @param name
     * @param gift_url
     */
//    private void addToSentGifts(String id, String name, String gift_url, int pos) {
//        ChatMessage msg = new ChatMessage("", true, false, true);
//        msg.setGiftUrl(gift_url);
//        msg.setGiftId(id);
//        if (pos == 0) {
//            mAdapter.insert(msg, 0);
//        } else {
//            mAdapter.add(msg);
//        }
//    }

//    /**
//     * @param gift_id
//     * @param gift_name
//     * @param gift_url
//     */
//    private void addToReceivedGifts(String gift_id, String gift_name, String gift_url, int pos) {
//        ChatMessage msg = new ChatMessage("", false, false, true);
//        msg.setGiftUrl(gift_url);
//        msg.setGiftId(gift_id);
//        if (pos == 0) {
//            Log.d("GIFT INSERTED", gift_url);
//            mAdapter.insert(msg, 0);
//        } else {
//            mAdapter.add(msg);
//        }
//
//    }
//
//    private void mimicOtherMessage(String message) {
//        ChatMessage chatMessage = new ChatMessage(message, false, false, false);
//        mAdapter.add(chatMessage);
//    }
    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mSocket.disconnect();
//        mSocket.off("new_message_received", onNewMessage)
//                .off("connected", onConnected)
//                .off("user_online", onUserOnline)
//                .off("user_offline", onUserOffline)
//                .off("typing", onTyping)
//                .off("typing_stop", onTypingStop)
//                .off("new_message_sent", onNewMessageSent)
//                .off("notifications", onNotification);
    }
}
