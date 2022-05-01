package cu.inmobile.wara.Networking;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import cu.inmobile.wara.Applications.WaraApp;

/**
 * Created by harry on 27/12/18.
 */

public class XMPPConstant {


    public static int server_port = 5222;

    public static String server_domain = "muchawara.com";

    public static String server_resource = "XAndroid";
    //"192.168.1.100" is the address of my local server. It may be different in your case.
//        public static String server_address = "172.16.10.11";

    public static String server_address = "muchawara.com";
    //public static String server_address = "192.168.1.27";


    public static XMPPTCPConnection Connection = null;


    //TODO Previsto a borrar si es innecesario, esta reconexion la hace XMPPClient

    public XMPPTCPConnection reconnect(Context c, XMPPCallback<Object> okCallBack,  FutureCallback<JsonObject> erroCallBack) {
        Log.d("-- XMPPConstant","reconnect()");

        if(Connection!=null)
        if (Connection.isConnected())
            XMPPConstant.Connection.disconnect();
        final XMPPClient client = new XMPPClient();

        String name = WaraApp.chatUser;
        String password = WaraApp.chatToken;

        client.connect(c,name, password, okCallBack, erroCallBack);
        return XMPPConstant.Connection;

    }

    public static XMPPTCPConnection getConnection (final Context c,final   XMPPCallback<Object> okCallBack, final FutureCallback<JsonObject> errorCallBack) {

        Log.d("-- XMPPConstant","getConnection()");

        if (Connection == null) {
            Log.d("-- XMPPConstant","getConnection() ==null");

            final XMPPClient client = new XMPPClient();
            String name = WaraApp.chatUser;
            String password = WaraApp.chatToken;
            client.connect(c,name, password, okCallBack, errorCallBack);
            return XMPPConstant.Connection;

        } else {

            Log.d("-- XMPPConstant", "getConnection() != null");

            if (Connection.isConnected())
                return XMPPConstant.Connection;



//            XMPPConstant.Connection.disconnect();
//
//
//            final XMPPClient client = new XMPPClient();
//
//            String name = WaraApp.chatUser;
//            String password = WaraApp.chatToken;
//
//            client.connect(c,name, password, okCallBack, errorCallBack);
//            return XMPPConstant.Connection;

//            if (reconnect == false) {
//                JsonObject json = new JsonObject();
//                json.addProperty("id", WaraApp.id);
//                json.addProperty("access_token", WaraApp.token);
//                json.addProperty("user", WaraApp.chatUser);
//
//            Ion.with(c)
//                        .load(Endpoints.status)
//                        .setJsonObjectBody(json)
//                        .asJsonObject()
//                        .setCallback(new FutureCallback<JsonObject>() {
//                            @Override
//                            public void onCompleted(Exception e, JsonObject result) {
//                                // do stuff with the result or error
//
//                                //TODO Aqui esta dando error de autenticacion, debo reloguearme y volver a hacer la peticion
//
//
//                                if (result == null)
//                                {
//                                    Log.d("-- XMPPConstant","getConnection().check : NULL RESULT" );
//
//
//                                    XMPPConstant.Connection = null;
//                                    if(XMPPConstant.Connection!=null)
//                                    XMPPConstant.Connection.disconnect();
//
//                                    XMPPConstant s = new XMPPConstant();
//                                    s.reconnect (c,okCallBack, erroCallBack);
//                                    return;
//                                }
//
//                                //JsonPrimitive userObj = result.getAsJsonPrimitive("user");
//
//                                Log.d("-- XMPPConstant","getConnection().check :" + result.toString() );
//
//                                try{
//                                    boolean authRes = result.get("result").getAsBoolean();
//                                    if (! authRes){
//                                        Log.d("-- XMPPConstant","getConnection(). Auth Error ");
//                                        return;
//                                    }
//                                }catch (Exception ex){
//                                    Log.e("-- XMPPConstant","getConnection() Error: " + ex.getMessage());
//
//                                }
//
//                                if (result.get("state").getAsString().equals("false") ) {
//                                    Log.d("-- XMPPConstant","getConnection() - state = false ");
//
//                                    XMPPConstant.Connection = null;
//                                    if(XMPPConstant.Connection!=null)
//                                        XMPPConstant.Connection.disconnect();
//
//                                    XMPPConstant s = new XMPPConstant();
//                                    //TODO El resultado del API es siempre FALSE aunque el usuario este conectado, revisar y quitar comentarios
//
//                                    s.reconnect (c,okCallBack, erroCallBack);
//
//
//
//                                } else {
//
//                                    Log.d("-- XMPPConstant","getConnection(). LAST ELSE ");
//
//                                    HashMap list = new HashMap();
//                                    list.put("user_object", Connection.getUser());
//                                    list.put("user_id", Connection.getUser());
//                                    list.put("result", true);
//                                    okCallBack.execute(list, Connection);
//
//
////
//                                }
//                            }
//                        });
//            } else
//            {
//                XMPPConstant s = new XMPPConstant();
//                s.reconnect (c,okCallBack, erroCallBack);
//            }

            //return XMPPConstant.Connection;
        }

        return XMPPConstant.Connection;

    }

}
