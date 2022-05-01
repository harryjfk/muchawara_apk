package cu.inmobile.wara.Applications;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.login.LoginManager;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

import cu.inmobile.wara.Activities.ChatActivity;
import cu.inmobile.wara.Activities.ChatHistoryActivity;
import cu.inmobile.wara.Activities.FilterActivity;
import cu.inmobile.wara.MainActivity;
import cu.inmobile.wara.R;
import cu.inmobile.wara.Receivers.ServiceReceiver;
import cu.inmobile.wara.RoomModels.City;
import cu.inmobile.wara.RoomModels.CityRepo;
import cu.inmobile.wara.RoomModels.MessageRepo;
import cu.inmobile.wara.RoomModels.UserRepo;
import cu.inmobile.wara.Services.ChatService;
import cu.inmobile.wara.Services.WaraService;
import cu.inmobile.wara.Utils.Const;
import cu.inmobile.wara.Utils.SharedPreferencesUtils;

/**
 * Created by amal on 20/02/17.
 */
public class WaraApp extends Application implements SharedPreferences.OnSharedPreferenceChangeListener, ServiceReceiver.Receiver {

    private static final String TAG = "-- WaraApp";

    public static int locationPreference = 0;

    public static String id = "";
    public static String token = "";
    public static String chatToken = "";
    public static String chatUser = "";
    public static String popularity = "";

    public static int balas = 0;
    public static int users_count = 0;
    public static String imageUrl = "";
    public static String imageDrawer = "";
    public static String name = "";
    public static String about_me = "";

    public static boolean first_time = true;
    public static String gender = Const.Params.GENDER_BOTH ;
    public static int minAge = 0;
    public static int maxAge = 0;
    public static String city_user = "Cuba";
    public static String city_filter= "Cuba";

    public static String lastUpdate = "0";

    public static String CHANNEL_ID = "88";

    private static Context my_context;
    private static Activity currentActivity;
    private static Fragment currentFragment;

    public static CityRepo cityRepo;
    public static UserRepo userRepo;
    public static MessageRepo messageRepo;

    public static String[] cities = new String[]{"Cuba","Cuba, Pinar del R\u00edo","Cuba, Pinar del R\u00edo, Consolaci\u00f3n del Sur","Cuba, Pinar del R\u00edo, Guane","Cuba, Pinar del R\u00edo, La Palma","Cuba, Pinar del R\u00edo, Los Palacios","Cuba, Pinar del R\u00edo, Mantua","Cuba, Pinar del R\u00edo, Minas de Matahambre","Cuba, Pinar del R\u00edo, Pinar del R\u00edo","Cuba, Pinar del R\u00edo, San Juan y Mart\u00ednez","Cuba, Pinar del R\u00edo, San Luis","Cuba, Pinar del R\u00edo, Sandino","Cuba, Pinar del R\u00edo, Vi\u00f1ales","Cuba, Artemisa","Cuba, Artemisa, Mariel","Cuba, Artemisa, Guanajay","Cuba, Artemisa, Caimito","Cuba, Artemisa, Bauta","Cuba, Artemisa, San Antonio de los Ba\u00f1os","Cuba, Artemisa, G\u00fcira de Melena","Cuba, Artemisa, Alqu\u00edzar","Cuba, Artemisa, Artemisa","Cuba, Artemisa, Bah\u00eda Honda","Cuba, Artemisa, Candelaria","Cuba, Artemisa, San Crist\u00f3bal","Cuba, La Habana","Cuba, La Habana, Arroyo Naranjo","Cuba, La Habana, Boyeros","Cuba, La Habana, Centro Habana","Cuba, La Habana, Cerro","Cuba, La Habana, Cotorro","Cuba, La Habana, Diez de Octubre","Cuba, La Habana, Guanabacoa","Cuba, La Habana, La Habana del Este","Cuba, La Habana, La Habana Vieja","Cuba, La Habana, La Lisa","Cuba, La Habana, Marianao","Cuba, La Habana, Playa","Cuba, La Habana, Plaza de la Revoluci\u00f3n","Cuba, La Habana, Regla","Cuba, La Habana, San Miguel del Padr\u00f3n","Cuba, La Habana, San Miguel","Cuba, Mayabeque","Cuba, Mayabeque, Bejucal","Cuba, Mayabeque, San Jos\u00e9 de las Lajas","Cuba, Mayabeque, Jaruco","Cuba, Mayabeque, Santa Cruz del Norte","Cuba, Mayabeque, Madruga","Cuba, Mayabeque, Nueva Paz","Cuba, Mayabeque, San Nicol\u00e1s de Bari","Cuba, Mayabeque, G\u00fcines","Cuba, Mayabeque, Melena del Sur","Cuba, Mayabeque, Bataban\u00f3","Cuba, Mayabeque, Quivic\u00e1n","Cuba, Matanzas","Cuba, Matanzas, Calimete","Cuba, Matanzas, C\u00e1rdenas","Cuba, Matanzas, Ci\u00e9naga de Zapata","Cuba, Matanzas, Col\u00f3n","Cuba, Matanzas, Jag\u00fcey Grande","Cuba, Matanzas, Jovellanos","Cuba, Matanzas, Limonar","Cuba, Matanzas, Los Arabos","Cuba, Matanzas, Mart\u00ed","Cuba, Matanzas, Matanzas","Cuba, Matanzas, Pedro Betancourt","Cuba, Matanzas, Perico","Cuba, Matanzas, Uni\u00f3n de Reyes","Cuba, Cienfuegos","Cuba, Cienfuegos, Abreus","Cuba, Cienfuegos, Aguada de Pasajeros","Cuba, Cienfuegos, Cienfuegos","Cuba, Cienfuegos, Cruces","Cuba, Cienfuegos, Cumanayagua","Cuba, Cienfuegos, Lajas","Cuba, Cienfuegos, Palmira","Cuba, Cienfuegos, Rodas","Cuba, Villa Clara","Cuba, Villa Clara, Caibari\u00e9n","Cuba, Villa Clara, Camajuan\u00ed","Cuba, Villa Clara, Cifuentes","Cuba, Villa Clara, Corralillo","Cuba, Villa Clara, Encrucijada","Cuba, Villa Clara, Manicaragua","Cuba, Villa Clara, Placetas","Cuba, Villa Clara, Quemado de G\u00fcines","Cuba, Villa Clara, Ranchuelo","Cuba, Villa Clara, Remedios","Cuba, Villa Clara, Sagua la Grande","Cuba, Villa Clara, Santa Clara","Cuba, Villa Clara, Santo Domingo","Cuba, Santi Sp\u00edritus","Cuba, Santi Sp\u00edritus, Santi Sp\u00edritus","Cuba, Santi Sp\u00edritus, Trinidad","Cuba, Santi Sp\u00edritus, Cabaigu\u00e1n","Cuba, Santi Sp\u00edritus, Yaguajay","Cuba, Santi Sp\u00edritus, Jatibonico","Cuba, Santi Sp\u00edritus, Taguasco","Cuba, Santi Sp\u00edritus, Fomento","Cuba, Santi Sp\u00edritus, La Sierpe","Cuba, Ciego de \u00c1vila","Cuba, Ciego de \u00c1vila, Ciego de \u00c1vila","Cuba, Ciego de \u00c1vila, Mor\u00f3n","Cuba, Ciego de \u00c1vila, Chambas","Cuba, Ciego de \u00c1vila, Ciro Redondo","Cuba, Ciego de \u00c1vila, Majagua","Cuba, Ciego de \u00c1vila, Florencia","Cuba, Ciego de \u00c1vila, Venezuela","Cuba, Ciego de \u00c1vila, Baragu\u00e1","Cuba, Ciego de \u00c1vila, Primero de Enero","Cuba, Ciego de \u00c1vila, Bolivia","Cuba, Camag\u00fcey","Cuba, Camag\u00fcey, Camag\u00fcey","Cuba, Camag\u00fcey, Gu\u00e1imaro","Cuba, Camag\u00fcey, Nuevitas","Cuba, Camag\u00fcey, C\u00e9spedes","Cuba, Camag\u00fcey, Jimaguay\u00fa","Cuba, Camag\u00fcey, Sibanic\u00fa","Cuba, Camag\u00fcey, Esmeralda","Cuba, Camag\u00fcey, Minas","Cuba, Camag\u00fcey, Sierra de Cubitas","Cuba, Camag\u00fcey, Florida","Cuba, Camag\u00fcey, Najasa","Cuba, Camag\u00fcey, Vertientes","Cuba, Camag\u00fcey, Santa Cruz del Sur","Cuba, Las Tunas","Cuba, Las Tunas, Manat\u00ed","Cuba, Las Tunas, Puerto Padre","Cuba, Las Tunas, Jes\u00fas Men\u00e9ndez","Cuba, Las Tunas, Majibacoa","Cuba, Las Tunas, Las Tunas","Cuba, Las Tunas, Jobabo","Cuba, Las Tunas, Colombia","Cuba, Las Tunas, Amancio","Cuba, Granma","Cuba, Granma, Bartolom\u00e9 Mas\u00f3","Cuba, Granma, Bayamo","Cuba, Granma, Buey Arriba","Cuba, Granma, Campechuela","Cuba, Granma, Cauto Cristo","Cuba, Granma, Guisa","Cuba, Granma, Jiguan\u00ed","Cuba, Granma, Manzanillo","Cuba, Granma, Media Luna","Cuba, Granma, Niquero","Cuba, Granma, Pil\u00f3n","Cuba, Granma, R\u00edo Cauto","Cuba, Granma, Yara","Cuba, Holgu\u00edn","Cuba, Holgu\u00edn, Antilla","Cuba, Holgu\u00edn, B\u00e1guanos","Cuba, Holgu\u00edn, Banes","Cuba, Holgu\u00edn, Cacocum","Cuba, Holgu\u00edn, Calixto Garc\u00eda","Cuba, Holgu\u00edn, Cueto","Cuba, Holgu\u00edn, Frank Pas\u00eds","Cuba, Holgu\u00edn, Gibara","Cuba, Holgu\u00edn, Holgu\u00edn","Cuba, Holgu\u00edn, Mayar\u00ed","Cuba, Holgu\u00edn, Moa","Cuba, Holgu\u00edn, Rafael Freyre","Cuba, Holgu\u00edn, Sagua de T\u00e1namo","Cuba, Holgu\u00edn, Urbano Norris","Cuba, Santiago de Cuba","Cuba, Santiago de Cuba, Contramaestre","Cuba, Santiago de Cuba, Guam\u00e1","Cuba, Santiago de Cuba, Mella","Cuba, Santiago de Cuba, Palma Soriano","Cuba, Santiago de Cuba, San Luis","Cuba, Santiago de Cuba, Santiago de Cuba","Cuba, Santiago de Cuba, Segundo Frente","Cuba, Santiago de Cuba, Songo-La Maya","Cuba, Santiago de Cuba, Tercer Frente","Cuba, Guant\u00e1namo","Cuba, Guant\u00e1namo, Baracoa","Cuba, Guant\u00e1namo, Caimanera","Cuba, Guant\u00e1namo, El Salvador","Cuba, Guant\u00e1namo, Guant\u00e1namo","Cuba, Guant\u00e1namo, Im\u00edas","Cuba, Guant\u00e1namo, Mais\u00ed","Cuba, Guant\u00e1namo, Manuel Tames","Cuba, Guant\u00e1namo, Niceto P\u00e9rez","Cuba, Guant\u00e1namo, San Antonio del Sur","Cuba, Guant\u00e1namo, Yateras","Cuba, Isla de la Juventud","Cuba, Isla de la Juventud, Isla de la Juventud","EEUU","Estados Unidos","Estados Unidos, Las Vegas","EE","EE, Florida"};


    private static SharedPreferences prefs;

    //var os y version updateAppValues
    public static int likes = 0;
    public static int matches = 0;
    public static String admin_mess = "";
    public static String admin_url = "";
    public static int user_mess = 0;

    private WaraService waraService;

    private ServiceReceiver mServiceReceiver;



    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("-- WaraApp" , "Creating app");

        iniVariables();
        createNotificationChannel();
        iniService(null);

        my_context = getApplicationContext();
        JodaTimeAndroid.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("-- WaraApp" , "Terminating app");
        saveSharedPreferences();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    private void iniVariables (){

        WaraApp.my_context = getApplicationContext();
        WaraApp.setCurrentActivity(null);
        WaraApp.setCurrentFragment(null);

        cityRepo = new CityRepo(this);
        userRepo = new UserRepo(this);
        messageRepo = new MessageRepo(this);

        loadSharedPreferences();


        //if (first_time == true){
        if(cityRepo.getAllCitiesList() == null){
            firstRun();
        }


        //createNotificationChannel();

    }


    //@ppyonki.cu
    //Inicializando el servicio

    //Voy a recibir un RECIEVER en dependencia del Activity que mande a crear el servicio, si es null los result los controla la app y manda notificaciones

    public void iniService (ServiceReceiver serviceReceiver){
        Log.d("-- WaraApp" , "iniService()");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(my_context, ChatService.class));
        }else
            startService(new Intent(my_context, ChatService.class));
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("-- WaraApp" , "onReceiveResult()" + resultData.get("state"));


    }


    private void loadSharedPreferences (){

        id = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.USER_ID, "");
        chatToken = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.CHAT_TOKEN, "");
        chatUser = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.CHAT_USER, "");
        token = (String) SharedPreferencesUtils.getParam(this, SharedPreferencesUtils.SESSION_TOKEN, "");
        balas = (Integer) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.BALAS , 0) ;
        user_mess = (Integer) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.USER_MESS , 0) ;
        users_count = (Integer) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.USER_COUNT , 0) ;
        likes = (Integer) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.LIKES , 0) ;
        matches = (Integer) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.MATCHES , 0) ;
        name = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.NAME , "") ;
        popularity = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.POPULARITY , "") ;
        about_me = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.ABOUT_ME , "") ;
        imageUrl = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.IMG_URL , "") ;
        imageDrawer = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.IMG_DRAWER , "") ;
        gender = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.GENDER , "") ;
        minAge = (int) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.MIN_EDAD , 0) ;
        maxAge = (int) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.MAX_EDAD , 70) ;
        city_filter = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.CITY_FILTER , "") ;
        city_user = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.CITY_USER , "") ;
        first_time = (boolean) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.FIRST_TIME , true) ;
        lastUpdate = (String) SharedPreferencesUtils.getParam(my_context , SharedPreferencesUtils.LAST_UPDATE , "0") ;



        Log.d("W/loadSharedPreferences" , "id: " + id);
        Log.d("W/loadSharedPreferences" , "token: " + token);
        Log.d("W/loadSharedPreferences" , "balas: " + balas);
        Log.d("W/loadSharedPreferences" , "user_mess: " + user_mess);
        Log.d("W/loadSharedPreferences" , "likes: " + likes);
        Log.d("W/loadSharedPreferences" , "matches: " + matches);
        Log.d("W/loadSharedPreferences" , "lastUpdate: " + lastUpdate);
        Log.d("W/loadSharedPreferences" , "about_me: " + about_me);
        Log.d("W/loadSharedPreferences" , "popularity: " + popularity);
        Log.d("W/loadSharedPreferences" , "popularity: " + users_count);

    }

    public static void saveSharedPreferences (){
        Log.d("W/saveSharedPreferences" , "SAVING");
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.USER_ID , id ) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.CHAT_TOKEN , chatToken ) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.CHAT_USER , chatUser ) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.SESSION_TOKEN , token ) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.BALAS , balas ) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.NAME , name) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.IMG_URL , imageUrl) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.IMG_DRAWER , imageDrawer) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.GENDER , gender) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.MIN_EDAD , minAge) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.MAX_EDAD , maxAge) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.CITY_FILTER , city_filter) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.CITY_USER , city_user) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.FIRST_TIME , first_time) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.LAST_UPDATE , lastUpdate) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.USER_MESS , user_mess) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.USER_COUNT , users_count) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.LIKES , likes) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.MATCHES , matches) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.ABOUT_ME , about_me) ;
        SharedPreferencesUtils.setParam(my_context , SharedPreferencesUtils.POPULARITY , popularity) ;

    }

    public static boolean isBalasAvailable (){
        return ( balas > 0 );
    }



    public static Bitmap getImageFromFile (String imgName){
        try {
            File file = new File(getAppPrivateDirectory() + "Pictures/" + imgName);
            if (file.exists() && file.length()>0 && !file.isDirectory() ) {
                return  BitmapFactory.decodeFile(file.getAbsolutePath());
            }else
                return  BitmapFactory.decodeResource( my_context.getResources() , R.drawable.bg);
        }catch (Exception e){
            return  BitmapFactory.decodeResource(my_context.getResources(),R.drawable.bg);
        }
    }

    public void signOut() {
        /*SharedPreferencesUtils.setParam(my_context, SharedPreferencesUtils.SESSION_TOKEN, "");
        SharedPreferencesUtils.setParam(my_context, SharedPreferencesUtils.USER_ID, "");
        SharedPreferencesUtils.setParam(my_context, SharedPreferencesUtils.IS_PRO_USER, "");
        SharedPreferencesUtils.setParam(my_context, SharedPreferencesUtils.PROPERTY_GCM_REG_ID, "");*/
        token = "";
        id = "";
        lastUpdate = "";
        name="";
        imageUrl="";
        imageDrawer="";
        chatUser="";
        chatToken="";
        city_filter="";
        city_user="";
        likes=0;
        matches=0;
        user_mess=0;
        users_count=0;


        stopService(new Intent(my_context, ChatService.class));

        //saveSharedPreferences();
        SharedPreferencesUtils.clearPreferences(my_context);
        messageRepo.deleteAll();
        userRepo.deleteAll();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(my_context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        my_context.startActivity(intent);
    }

    /*
    @ppyonki.cu
    Al recibir cualquier evento del servicio a partir de la conexion con el servidor, este metodo
    determinara que hacer con esa informacion.
    TODO Si esta activa la vista del CHAT debe actualizar la lista de mensajes (Llego el mensaje, esta escribiendo, mensaje nuevo)
    TODO Si esta activa la vista del CHATHISTORY debe actualizar la lista de mensajes (Mensaje nuevo, esta activo)
    TODO Si esta activa la vista de DISPLAY debe estar atento a nuevas WARA, ADMIRADORES
    TODO Si no mandara una notificacion


     */

    public static void eventReveiced (Bundle stateBundle) {

        if (currentActivity instanceof ChatActivity ){
            ((ChatActivity) currentActivity).refreshState(stateBundle);
        }


    }

    public static void showNotification(int notificationType){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder( my_context , CHANNEL_ID );

        switch (notificationType){
            case Const.Params.NOTIFICATION_MES:
                Log.d(TAG , "showNotification() - Message notification");
                mBuilder.setSmallIcon(R.drawable.ic_notification_medium );
                mBuilder.setContentTitle(my_context.getString(R.string.notification_new_message));
                mBuilder.setContentText(my_context.getString(R.string.notification_new_message_text));
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                mBuilder.setAutoCancel(true);
                break;
        }

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(my_context, ChatHistoryActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(my_context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(FilterActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) my_context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify( 1 , mBuilder.build());
    }



    public static String getAppPrivateDirectory () {
        return (ContextCompat.getExternalFilesDirs(my_context, null)[0]).getAbsolutePath() + "/";
    }

    public static Context getMy_context() {
        return my_context;
    }

    public static void setMy_context(Context my_context) {
        WaraApp.my_context = my_context;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        WaraApp.currentActivity = currentActivity;
    }

    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(Fragment currentFragment) {
        WaraApp.currentFragment = currentFragment;
    }

    public static SharedPreferences getPrefs() {
        return prefs;
    }

    public static void setPrefs(SharedPreferences prefs) {
        WaraApp.prefs = prefs;
    }

    public static CityRepo getCityRepo() {
        return cityRepo;
    }

    public static void setCityRepo(CityRepo cityRepo) {
        WaraApp.cityRepo = cityRepo;
    }

    public static void setMaxAge(int maxAge) {
        WaraApp.maxAge = maxAge;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void firstRun(){
        Log.d(TAG , "firstRun()");
        for (int i = 0; i < cities.length; i++){
            cityRepo.insert(new City(null, cities[i]));
        }
        first_time = false;
        saveSharedPreferences();
    }



}
