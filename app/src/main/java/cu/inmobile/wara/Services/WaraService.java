package cu.inmobile.wara.Services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.sql.Timestamp;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.AdditionalChatState;
import cu.inmobile.wara.Networking.AdditionalChatStateExtension;
import cu.inmobile.wara.Networking.XMPPCallback;
import cu.inmobile.wara.Networking.XMPPConstant;
import cu.inmobile.wara.Receivers.ServiceReceiver;

import static cu.inmobile.wara.Networking.XMPPConstant.Connection;


//TODO Revisar todos los mensajes que esten por enviar y enviarlos


public class WaraService extends JobIntentService {

    //private final IBinder mBinder = new LocalBinder();
    private static StanzaListener packetListener;

    private ResultReceiver mServiceReceiver;
    public static final String RECEIVER = "receiver";
    public static final int SHOW_RESULT = 123;

    public static final int JOB_ID = 1;

    private Chat currentChat;




    public WaraService() {
    }

    public static void enqueueWork(Context context , ServiceReceiver serviceReceiver) {

        Log.d("--- WaraService" , "enqueueWork()");

        Intent jobService = new Intent(WaraApp.getMy_context() , WaraService.class);
        jobService.putExtra(RECEIVER , serviceReceiver);

        enqueueWork(WaraApp.getMy_context(), WaraService.class, JOB_ID, jobService);
    }



    @Override
    public void onCreate() {
        Log.d("--- WaraService" , "onCreate()");

        super.onCreate();
        iniVariables();

    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        Log.d("--- WaraService" , "onStartCommand()");
//
//        //WSWorker.getCities(new ArrayList<City>());
//        //WSWorker.getMessages();
//        connectChatServer();
//
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Log.d("--- WaraService" , "onHandleWork()");

//      mServiceReceiver = new ServiceReceiver(new Handler());

        mServiceReceiver = intent.getParcelableExtra(RECEIVER);

        connectChatServer();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("--- WaraService" , "onDestroy()");

    }

//    @Override
//    public IBinder onBind(Intent intent) {
//
//        Log.d("--- WaraService" , "onBind()");
//
//
//        return mBinder;
//    }

    //------------------------------------------------------------------------------------BIND CLASS

//    public class LocalBinder extends Binder {
//        public WaraService getService() {
//            // Return this instance of LocalService so clients can call public methods
//
//            return WaraService.this;
//        }
//    }


    //------------------------------------------------------------------------------------CREATED METHODS

    private void iniVariables (){


        packetListener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                Log.d("-- WaraService", "packetReceived: " + packet.toString());


                Bundle bundle = new Bundle();

                if (packet instanceof Message) {

                    Log.d("-- WaraService", "iniVariables().isMessage - " + packet.toString());

                    Message message = (Message) packet;
                    String message_txt = message.getBody();

                    if (message_txt != null) {
                        if (!message_txt.isEmpty()) {

                            Log.d("-- WaraService", "iniVariables().noEmpty - " + message_txt);

                            // TODO revisar si el paquete contiene received o readed y sacar el token y editar el mensaje en la bd

                            message.addExtension(new AdditionalChatStateExtension(AdditionalChatState.recieved));
                                DefaultExtensionElement tokenExtension = new DefaultExtensionElement(
                                        "token", "urn:xmpp:token");
                                message.addExtension(tokenExtension);

                            String mesToken = ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value");


                            WaraApp.messageRepo.insert(new cu.inmobile.wara.RoomModels.Message(
                                    "0",
                                    message.getFrom().split("/")[0],
                                    WaraApp.chatUser,
                                    message_txt,
                                    String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()),
                                    mesToken,
                                    0,
                                    0,
                                    0,
                                    false,
                                    false,
                                    false
                            ), null);

                            sendRecieved(message);

                            //WaraApp.messageRepo.setReceived("1ec67683-b7e0-4d1b-9cbf-218e536b1589");

//                            if (message.getFrom().contains(receiverId)) {
//                                message.addExtension(new AdditionalChatStateExtension(AdditionalChatState.recieved));
//                                DefaultExtensionElement tokenExtension = new DefaultExtensionElement(
//                                        "token", "urn:xmpp:token");
//                                message.addExtension(tokenExtension);
//
//
////                                addToReceiveMessage(message_txt, ((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"), false, false, -1);
////                                ((ChatActivity) c).sendRecieved(message); //TODO Esto debera ir en el servicio al descargar los mensajes
////                                ((ChatActivity) c).sendReaded(message);
//
//
//                            } else {
//                                //WaraApp.showNotification();
//                            }


                            //onTypingStop.call(message);


                        }
                    } else
                    {
                        String data = message.toString();
                        Log.d("-- WaraService", "iniVariables().noEmpty: " + message.toString() );


                        if (data.contains("composing")){
                            //onTyping.call(message);
                            Log.d("-- WaraService", "iniVariables().noEmpty - composing" );

                            bundle.putString("state","composing");

                            mServiceReceiver.send( SHOW_RESULT ,bundle );


                        }

                        if (data.contains("paused")) {
                            //onTypingStop.call(message);
                            Log.d("-- WaraService", "iniVariables().noEmpty - paused" );

                        }

                        if (data.contains("recieved")) {

                            Log.d("-- WaraService", "iniVariables().noEmpty - received" );




//                            final ChatMessage t = ((ChatActivity) c).getChatMessageFromToken(((DefaultExtensionElement) message.getExtension("urn:xmpp:token")).getValue("value"));
//
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    if(t!=null)
//                                    {
//                                        t.setIsRecieved(true);
//                                        t.getView().requestFocus();
//                                        t.getView().requestLayout();
//
//                                    }
//
//                                    ;
//                                }
//                            });

                        }
                        if (data.contains("readed")) {
                            Log.d("-- WaraService", "iniVariables().noEmpty - readed" );

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
                }
            }
        };
    }



    public static void connectChatServer (){

        Log.d("--- WaraService" , "connectChatServer()");


        XMPPConstant.getConnection( WaraApp.getMy_context() , (new XMPPCallback<Object>() {

            public void execute(final Object result, XMPPTCPConnection connection) {

                Log.d("--- WaraService" , "execute() / OK CALLBACK");

                XMPPConstant.Connection.addAsyncStanzaListener(packetListener , null);

                if(connection==null)
                   return;

            }

                }),(new FutureCallback<JsonObject>() {

                    @Override
                    public void onCompleted(final Exception e, JsonObject result) {
                        // do stuff with the result or error

                        Log.d("--- WaraService" , "onCompleted() / ERROR CALLBACK " + e.getMessage());


                    }
                }));
    }

    private void sendRecieved(Message message) {
        Log.d("-- WaraService","sendRecieved()");

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
            ChatManager chatManager = ChatManager.getInstanceFor(Connection);
            currentChat = chatManager.createChat(message.getFrom());
            currentChat.sendMessage(msg);

        } catch (SmackException.NotConnectedException e) {

            Log.e("-- WaraService","sendRecieved(). ERROR: " + e.getMessage());

        }

    }


    public void checkBinding (){

        Log.d("--- WaraService" , "checkBinding()");


    }


}
