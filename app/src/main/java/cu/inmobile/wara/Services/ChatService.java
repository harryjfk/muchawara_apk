package cu.inmobile.wara.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.AdditionalChatState;
import cu.inmobile.wara.Networking.AdditionalChatStateExtension;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Networking.XMPPCallback;
import cu.inmobile.wara.Networking.XMPPConstant;
import cu.inmobile.wara.R;
import cu.inmobile.wara.Receivers.NetworkStateReceiver;
import cu.inmobile.wara.RoomModels.MessageRepo;
import cu.inmobile.wara.RoomModels.MessageViewModel;
import cu.inmobile.wara.Utils.Callback;
import cu.inmobile.wara.Utils.Const;

import static cu.inmobile.wara.Networking.XMPPConstant.Connection;
import static cu.inmobile.wara.RoomModels.MessageRepo.MES_NOT_RECIEVED;
import static cu.inmobile.wara.RoomModels.MessageRepo.MES_RECIEVED;


public class ChatService extends Service implements NetworkStateReceiver.NetworkStateReceiverListener {

    private static final String TAG = "-- ChatService" ;

    private NetworkStateReceiver networkStateReceiver;
    
    private Chat currentChat;
    private String chatUser;

    private  IBinder activityBinder;

    private static StanzaListener packetListener;

    private MessageViewModel messageViewModel;

    private boolean connectingChat = false;

    private MutableLiveData<Boolean> isTyping = new MutableLiveData<>();

    Timer typingTimer = new Timer();

    Timer reconnectChatTimer = new Timer();




    //----------------------------------------------------------------LIFE CYCLE METHODS

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand()");

        iniVariables();

        if (connectingChat){
            Log.d(TAG, "onStartCommand() - Already connecting");

        }else
            getTOMessages();

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG , "onBind");

        Bundle bundle = intent.getExtras();
        chatUser = bundle.getString("chatUser");


        //openChat();

        return activityBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG , "onBind");

        Bundle bundle = intent.getExtras();
        chatUser = bundle.getString("chatUser");

        //openChat();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG , "onUnbind");

        //closeChat();
        chatUser = "";

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG , "onCreate()");

        createNotificationChannel();
        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);

        //iniVariables();
        //connectChatServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectChatServer();
        Log.d(TAG , "onDestroy()");
        unregisterNetworkBroadcastReceiver(this);
    }

//------------------------------------------------------------------- CREATED METHODS

    private void iniVariables () {


        activityBinder = new ActivityBinder();
        chatUser = "";
        isTyping.postValue(false);

        //messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);

        packetListener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                Log.d(TAG, "packetReceived!!: " + packet.toString());


                Bundle bundle = new Bundle();

                if (packet instanceof Message) {

                    Log.d(TAG, "iniVariables().isMessage - ");

                    final Message message = (Message) packet;
                    String message_txt = message.getBody();

                    if (message_txt != null) {
                        if (!message_txt.isEmpty()) {

                            //desactivar el isTyping porque se acaba de recibir el mensaje
                            typingTimer.cancel();
                            isTyping.postValue(false);
                            //Log.d(TAG, "iniVariables().noEmpty - stop composing" );

                            Log.d(TAG, "iniVariables().noEmpty - " + message_txt);

                            // TODO revisar si el paquete contiene received o readed y sacar el token y editar el mensaje en la bd

                            message.addExtension(new AdditionalChatStateExtension(AdditionalChatState.recieved));
                            DefaultExtensionElement tokenExtension = new DefaultExtensionElement(
                                    "token", "urn:xmpp:token");
                            message.addExtension(tokenExtension);

                            String mesToken = ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value");
                            //"urn:xmpp:sentDate"
                            //String sentDate = ((DefaultExtensionElement) message.getExtension("urn:xmpp:sentDate")).getValue("value");

                            WaraApp.messageRepo.insert(new cu.inmobile.wara.RoomModels.Message(
                                    "0",
                                    message.getFrom().split("/")[0],
                                    WaraApp.chatUser,
                                    message_txt,
                                    String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()),
                                    mesToken,
                                    1,
                                    0,
                                    0,
                                    false,
                                    false,
                                    false
                            ), new Callback() {
                                @Override
                                public void execute() {
                                    sendReceived(message);
                                }
                            });

                            WaraApp.userRepo.setStatusByChatUSer(message.getFrom().split("/")[0], "online");

                            if (chatUser.equals("") || !message.getFrom().contains(chatUser))
                                WaraApp.showNotification(Const.Params.NOTIFICATION_MES);

                        }
                    } else {
                        String data = message.toString();
                        Log.d(TAG, "iniVariables().noEmpty: " + message.toString());


                        if (data.contains("composing")) {
                            if (message.getFrom().contains(chatUser)) {
                                typingTimer.cancel();

                                //onTyping.call(message);
                                Log.d(TAG, "iniVariables().noEmpty - composing");

                                bundle.putString("state", "composing");

                                //Aqui se activa isTyping visualmente
                                if (!isTyping.getValue()) {
                                    isTyping.postValue(true);
                                }

                                int delayInSeconds = 3;
                                typingTimer = new Timer();
                                typingTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        //Aqui se desactiva isTyping visualmente
                                        Log.d(TAG, "iniVariables().noEmpty - stop composing");
                                        isTyping.postValue(false);
                                    }
                                }, delayInSeconds * 1000);

                                //mServiceReceiver.send( SHOW_RESULT ,bundle );


                            }
                        }

                        if (data.contains("paused")) {
                            //onTypingStop.call(message);
                            Log.d(TAG, "iniVariables().noEmpty - paused");

                        }

                        if (data.contains("recieved")) {

                            Log.d(TAG, "iniVariables().noEmpty - received");

                            WaraApp.messageRepo.setReceived(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"), MessageRepo.MES_RECIEVED);

                            //WSWorker.setReceivedMessage("");
                            //String messageId = message.


                            //final ChatMessage t = ((ChatActivity) c).getChatMessageFromToken(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));

//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    WaraApp.messageRepo.setReceived(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"), 0 );
//
//                                }
//                            });


                        }
                        if (data.contains("readed")) {
                            Log.d(TAG, "iniVariables().noEmpty - readed");

                            //final ChatMessage t = ((ChatActivity) c).getChatMessageFromToken(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));

//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    if(t!=null)
//                                    {
//                                        t.setIsReaded(true);
//                                        t.getView().requestFocus();
//                                        t.getView().requestLayout();
//                                    }
//
//                                    ;
//                                }
//                            });
                        }
                    }
                }else if (packet instanceof Presence) {

                    Log.d(TAG, "iniVariables().presence!!!: ");
                    final Presence presence = (Presence) packet;
                    String chatUserFrom = presence.getFrom().split("/")[0];
                    Boolean unavailable = presence.getType().equals(Presence.Type.unavailable);

                    Log.d(TAG, "iniVariables().presence unavailable: " + unavailable);

                    String status = unavailable ? "offline" : "online";
                    WaraApp.userRepo.setStatusByChatUSer(chatUserFrom, status);
                }
            }



        };
    }

    public void connectChatServer (){

        Log.i(TAG , "connectChatServer()");

        connectingChat = true;

        XMPPConstant.getConnection( WaraApp.getMy_context() , (new XMPPCallback<Object>() {

            public void execute(final Object result, XMPPTCPConnection connection) {

                Log.d("--- ChatService" , "execute() / OK CALLBACK");

                connectingChat = false;

                Connection.addAsyncStanzaListener(packetListener , null);

                sendUnsendedMessages();

                //sendUnrecievedMessages();

                //openChat();

            }

        }),(new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(final Exception e, JsonObject result) {
                // do stuff with the result or error

                Log.d("--- ChatService" , "onCompleted() / ERROR CALLBACK " + e.getMessage());
//                if( e instanceof SmackException){
//                    Log.e("--- ChatService" , "onCompleted() - ERROR - NO DISCONNECT");
//                    connectingChat = false;
//                    return;
//                }

                disconnectChatServer();

                //TODO TRY AGAIN

            }
        }));
    }

    public void disconnectChatServer(){
        if (Connection != null)
            Connection.disconnect();
        Connection = null;
        connectingChat = false;
    }

    public void reconnectChatServer () {
        Log.d(TAG , "reconnectChatServer()" );

        //Connection = null;
        //connectingChat = false;
        disconnectChatServer();
        connectChatServer();
    }

    public void startReconnectChatTimer(){
        int delayInSeconds = 3;
        reconnectChatTimer = new Timer();
        reconnectChatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Aqui se intenta conectar al Chat en caso que Connection == null
                Log.d(TAG, "startReconnectChatTimer - Verifying connection to the Chat...");
                if(Connection == null){
                    Log.d(TAG, "startReconnectChatTimer - Connection is null :(");
                    connectChatServer();
                }
                Log.d(TAG, "startReconnectChatTimer - Connected :)");
            }
        }, delayInSeconds * 1000, delayInSeconds * 1000);
    }

    public void stopReconnectChatTimer(){
        reconnectChatTimer.cancel();
        Log.d(TAG, "stopReconnectChatTimer - Disconnected :)");
    }

    //Enviara tanto mensajes como informes de recibo

    public void sendUnsendedMessages (){

        Log.i(TAG , "sendUnsendedMessages()");

        List<cu.inmobile.wara.RoomModels.Message> messagesUnsended = WaraApp.messageRepo.getUnsendedMessage("0");

        if (messagesUnsended.size() > 0 ){

            for(cu.inmobile.wara.RoomModels.Message mes : messagesUnsended ){

                if (mes.getSended() == 0 && mes.getReceived() == 1){

                    Log.d("--- ChatService" , "sendUnsendedMessages() - RECIEVED MESSAGE: " + mes.getBody());

                    Message messageToSend = new Message();
                    messageToSend.setBody(null);
                    messageToSend.setFrom(mes.getFrom());
                    messageToSend.setTo(mes.getTo());

                    //En el iniVariables cuando se recibe un mensaje se manda el mensaje de recieved con esta extension
                    //messageToSend.addExtension(new AdditionalChatStateExtension(AdditionalChatState.recieved));


                    DefaultExtensionElement tokenExtenxion = new DefaultExtensionElement("token","urn:xmpp:token");
                    tokenExtenxion.setValue("value" , mes.getToken());
                    messageToSend.addExtension(tokenExtenxion);

                    sendReceived(messageToSend);
                }else {
                    Log.d("--- ChatService" , "sendUnsendedMessages() - UNSENDED MESSAGE: " + mes.getBody());

                    sendChatMessage(mes);

                }
            }

        }




    }

    private void createNotificationChannel () {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "cu.inmobile.wara";
            String channelName = "Chat Service";

            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            chan.setLightColor(Color.RED);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            Notification.Builder notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification_w)
                    .setContentTitle("WARA Chat")
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }else
            startForeground(2 , new Notification());

    }


    public void openChat (String chatUserr) {
        Log.d("--- ChatService" , "openChat() USER:  " + chatUserr);


        if (!chatUserr.equals("")) {
            ChatManager chatManager = ChatManager.getInstanceFor(Connection);
            currentChat = chatManager.createChat(chatUserr);
            Log.d("--- ChatService" , "openChat() - opened");

        }
    }

    private void closeChat () {

        currentChat.close();
    }

    public void emitTyping (boolean tryAgain) {
        Log.d(TAG,"emitTyping()");

        //TODO corregir... esto envia muchas veces en falso


        Message msg = new Message();
        msg.setBody(null);
        msg.addExtension(new ChatStateExtension(ChatState.composing));
        try{
            openChat(chatUser);
            currentChat.sendMessage(msg);
            closeChat();

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG,"emitTyping().NotConnectedException: " + e.getMessage());
            if (tryAgain){
                //checkChatConx();

                if (connectingChat){
                    Log.d(TAG, "emitTyping() - Already connecting");
                    return;
                }
                //connectChatServer();
                //emitTyping(false);
            }
        }catch (Exception e){
            Log.e(TAG,"emitTyping().Exception: " + e.getMessage());
            if (tryAgain){

                if (connectingChat){
                    Log.d(TAG, "emitTyping() - Already connecting");
                    return;
                }
                //connectChatServer();
                //checkChatConx();
                //emitTyping(false);
            }
        }
    }

    public void sendTextMessage(String message, String chatUser) {

        Log.d(TAG,"sendTextMessage() :" + message);


        cu.inmobile.wara.RoomModels.Message mes = new cu.inmobile.wara.RoomModels.Message(
                null,
                WaraApp.chatUser,
                chatUser,
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

        sendChatMessage(mes);

    }

    private void sendChatMessage (cu.inmobile.wara.RoomModels.Message mes ){

        try{

            Message messageToSend = new Message();
            messageToSend.setBody(mes.getBody());
            //token data
            DefaultExtensionElement tokenExtenxion = new DefaultExtensionElement("token","urn:xmpp:token");
            tokenExtenxion.setValue("value" , mes.getToken());
            //time data
            /*DefaultExtensionElement sentDateExtention = new DefaultExtensionElement("sentDate","urn:xmpp:sentDate");
            sentDateExtention.setValue("value" , mes.getTime());*/

            messageToSend.addExtension(tokenExtenxion);
            //messageToSend.addExtension(sentDateExtention);

            Log.d(TAG,"sendTextMessage() - chatUser: " +  mes.getTo() );


            openChat( mes.getTo());

            currentChat.sendMessage(messageToSend);

            closeChat();

            mes.setSended(1);
            WaraApp.messageRepo.insert(mes, null);

            Log.d(TAG,"sendTextMessage().messageSended()" );


        }catch (SmackException.NotConnectedException e){
            Log.e(TAG,"sendTextMessage().NotConnectedException: " + e.getMessage());

            mes.setSended(0);
            WaraApp.messageRepo.insert(mes, null);

            //WaraApp.messageRepo.sendMessage(mes);
            //connectChatServer();

        }catch (Exception ex){
            Log.e(TAG,"sendTextMessage().Exception: " +ex.getMessage());

            mes.setSended(0);
            WaraApp.messageRepo.insert(mes, null);

            //WaraApp.messageRepo.sendMessage(mes);
            //connectChatServer();

        }
    }


    // Cambia el FROM por el TO para que la respuesta llegue al origen

    private void sendReceived(Message message) {

        Log.d(TAG,"sendReceived()" );


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
            //Log.d(TAG,"sendReceived() - TOKEN: " + tokenExtension.getValue("value") + " TXT: " + msg.getBody() );

            openChat(msg.getTo());
            currentChat.sendMessage(msg);
            WaraApp.messageRepo.setReceived(tokenExtension.getValue("value") , MES_RECIEVED );

            closeChat();

            // El mensaje que me llego de el no tengo que marcarlo como recibido pues se encarga el otro usuario
            //WaraApp.messageRepo.setReceived(tokenExtension.getValue("value") , MessageRepo.MES_RECIEVED );


        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG,"sendReceived() - NotConnectedException " + e.getMessage() );
            WaraApp.messageRepo.setReceived(tokenExtension.getValue("value") , MES_NOT_RECIEVED );

        } catch (Exception e) {
            Log.e(TAG,"sendReceived() - Exception: " + e.getMessage()); //+ e.getCause().toString() + e.getMessage()
            WaraApp.messageRepo.setReceived(tokenExtension.getValue("value")  , MES_NOT_RECIEVED );


        }

    }

    public void getTOMessages () {

        Log.d(TAG,"getTOMessages()" );

        WSWorker.getUnrecievedMessages(new Callback(){

            @Override
            public void execute() {
                connectChatServer();
            }
        });

    }

    private void sendUnrecievedMessages(){
        Log.d(TAG , "sendUnrecievedMessages() start");
        List<cu.inmobile.wara.RoomModels.Message> messages = WaraApp.messageRepo.getUnrecievedMessages("0");

        if (messages.size() > 0)
        for(int i = 0; i < messages.size(); i++){
            Log.d(TAG , "sendUnrecievedMessages() - mes.body: " + messages.get(i).getBody());

            Message messageToSend = convertMessageFromRoomToChat(messages.get(i));
            sendReceived(messageToSend);
        }
        Log.d("--- ChatService" , "sendUnrecievedMessages() end");
    }

    private Message convertMessageFromRoomToChat(cu.inmobile.wara.RoomModels.Message mes){
        Message messageToSend = new Message();
        messageToSend.setBody(mes.getBody());
        messageToSend.setFrom(mes.getFrom());
        DefaultExtensionElement tokenExtenxion = new DefaultExtensionElement("token","urn:xmpp:token");
        tokenExtenxion.setValue("value" , mes.getToken());
        messageToSend.addExtension(tokenExtenxion);
        return messageToSend;
    }
    
    // --------------------------------------------------------------------------NETWORK STATUS RECEIVER

    @Override
    public void networkAvailable() {
        Log.i(TAG, "networkAvailable()");
        //Proceed with online actions in activity (e.g. hide offline UI from user, start services, etc...)

        //WSWorker.getUnrecievedMessages();

        //todo esto recibe llamadas dobles, hay que validar que solo se ejecuten los metodos una vez


        if (connectingChat){
            Log.d(TAG, "networkAvailable() - Already connecting");
            return;
        }
        getTOMessages();

        //connectChatServer();


        //TODO comprobar que no sea asincrono WSWorker.getUnrecievedMessages()

    }

    @Override
    public void networkUnavailable() {
        Log.i(TAG, "networkUnavailable()");
        //Proceed with offline actions in activity (e.g. sInform user they are offline, stop services, etc...)
        disconnectChatServer();
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }

    /**
     * Register the NetworkStateReceiver with your activity
     * @param currentContext
     */
    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     Unregister the NetworkStateReceiver with your activity
     * @param currentContext
     */
    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }



    //--------------------------------------------------------------------------- GET & SETTERS

    public void setChatUser(String chatUser) {
        Log.i(TAG,"setChatUser() : " + chatUser );

        this.chatUser = chatUser;
    }

    public MutableLiveData<Boolean> getIsTyping(){
        return isTyping;
    }


//----------------------------------------------------------------------------- INNER CLASS

public class ActivityBinder extends Binder {

        public ChatService getService () {
            return ChatService.this;
        }

}

    private class UnrecievedAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private static final String TAG = "-- UnrecievedAsyncTask";

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground()");

            //WSWorker.getUnrecievedMessages();

            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "doInBackground()");

            //sendUnrecievedMessages();

        }
    }


}
