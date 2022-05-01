package cu.inmobile.wara.Networking;

import android.content.Context;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import cu.inmobile.wara.Applications.WaraApp;

import static cu.inmobile.wara.Networking.XMPPConstant.Connection;

/**
 * Created by harry on 27/12/18.
 */

public class XMPPClient {



    public void Register(String user,String Password,Handler.Callback callback)
    {

    }
    protected void doConnected( final XMPPCallback<Object> okCallBack)
    {
        HashMap list = new HashMap();
        list.put("user_object",Connection.getUser());
        list.put("user_id",Connection.getUser());
        list.put("result",true);
        okCallBack.execute(list,Connection);
    }
    public void connect(final Context c,final String userName, final String password, final XMPPCallback<Object> okCallBack, final FutureCallback<JsonObject> erroCallBack)
    {
        Log.d("-- XMPPClient", "connect()" ); //+ userName+" "+password


        if (userName == null)
            return;

        new Thread()
        {
            public void run() {

        try
        {
            Boolean doConnect = true;
//            if(Connection!=null)
//                doConnect = !Connection.isConnected();
//            if(!doConnect)
//            {
//                doConnected(okCallBack);
//
//                return;
//
//            }


            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();



            configBuilder.setUsernameAndPassword(userName , password);

            configBuilder.setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled);

            configBuilder.setServiceName(XMPPConstant.server_domain);
            configBuilder.setResource(XMPPConstant.server_resource);
            configBuilder.setPort(XMPPConstant.server_port);

            configBuilder.setHost(XMPPConstant.server_address);

            configBuilder.setDebuggerEnabled(true);


//            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
//                    .setUsernameAndPassword(userName, password)
//                    .setXmppDomain(XMPPConstant.server_domain)
//                    .setHost(XMPPConstant.server_address)
//                    .setPort(XMPPConstant.server_port)
//                    .setResource(XMPPConstant.server_resource)
//                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                    .build();

            Connection = new XMPPTCPConnection(configBuilder.build());

            Connection.addConnectionListener(new ConnectionListener() {
                @Override
                public void connected(XMPPConnection connection) {
                    Log.d("-- XMPPClient", "connected()" );
                }

                @Override
                public void authenticated(XMPPConnection connection, boolean resumed) {
                    Log.d("-- XMPPClient", "authenticated()" );
                    okCallBack.execute(false , XMPPConstant.Connection);
                }

                @Override
                public void connectionClosed() {
                    Log.d("-- XMPPClient", "connectionClosed()" );
                    Connection = null;
                    WaraApp.lastUpdate = String.valueOf( new Timestamp(System.currentTimeMillis()).getTime());

                    //Connection = XMPPConstant.getConnection(c,okCallBack,erroCallBack);
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    Log.d("-- XMPPClient", "connectionClosedOnError: " + e.getMessage());
                    Connection = null;
                    WaraApp.lastUpdate = String.valueOf( new Timestamp(System.currentTimeMillis()).getTime());
                    //Connection = XMPPConstant.getConnection(c,okCallBack,erroCallBack);
                }

                @Override
                public void reconnectionSuccessful() {
                    Log.d("-- XMPPClient", "reconnectionSuccessful()" );

                }

                @Override
                public void reconnectingIn(int seconds) {
                    Log.d("-- XMPPClient", "reconnectingIn()" );
                }

                @Override
                public void reconnectionFailed(Exception e) {
                    Log.d("-- XMPPClient", "reconnectionFailed()" );
                }
            });

            Connection.connect().login();

            //Log.d("-- XMPPClient","getConnection() - Login : ");


            //TODO comentariado hasta tanto no se arrgle el metodo status, igual es posible que no haga falta chequear de esta manera que el chat esta conectado, puede que por el listener pueda controlarlo
            //            JsonObject json = new JsonObject();
//            json.addProperty("id", WaraApp.id);
//            json.addProperty("access_token", WaraApp.token);
//            json.addProperty("user", WaraApp.chatUser);
//            Ion.with(c)
//                    .load(Endpoints.status)
//                    .setJsonObjectBody(json)
//                    .asJsonObject()
//                    .setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//                            // do stuff with the result or error
//
//                            if (result == null)
//                                return;
//
//                            JsonPrimitive userObj = result.getAsJsonPrimitive("user");
//
//
//                            if (result.get("state").getAsString() == "false") {
//
//                                Log.d("-- XMPPClient","getConnection() - state = false ");
//
//                                XMPPConstant.Connection =null;
//                               XMPPConstant s =new XMPPConstant();
//
//                               //TODO El resultado del API es siempre FALSE aunque el usuario este conectado, revisar y quitar comentarios
//                               //s.reconnect(c,okCallBack,erroCallBack);
//
//
//                            } else {
//                                HashMap list = new HashMap();
//                                list.put("user_object", Connection.getUser());
//                                list.put("user_id", Connection.getUser());
//                                list.put("result", true);
//                                okCallBack.execute(list, Connection);
//
////
//                            }
//                        }
//                    });


        }
        catch (NetworkOnMainThreadException ex)
        {
            erroCallBack.onCompleted(ex,null);
            Log.e("-- XMPPClient", "NetworkOnMainThreadException()" + ex.getMessage());
        }

        catch (XMPPException ex)
        {
            Log.e("-- XMPPClient", "XMPPException()" + ex.getMessage());

            erroCallBack.onCompleted(ex,null);
        }
        catch (SmackException ex)
        {
            // La app esta conectada y se desactiva la WIFI.

            Log.e("-- XMPPClient", "SmackException()" + ex.getMessage());

            erroCallBack.onCompleted(ex,null);
        }
        catch (IOException ex)
        {
            Log.e("-- XMPPClient", "IOException()" + ex.getMessage());
            erroCallBack.onCompleted(ex,null);
        }catch (Exception e)
        {
            Log.e("-- XMPPClient", "Exception()" + e.getMessage());
            erroCallBack.onCompleted(e,null);
        }
            }
        }.start();


    }
}
