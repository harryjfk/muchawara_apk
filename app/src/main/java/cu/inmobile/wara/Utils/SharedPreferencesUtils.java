package cu.inmobile.wara.Utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {

    private static final String FILE_NAME = "WaraApp_preferences";

    public static final String PROPERTY_GCM_REG_ID = "cu.inmobile.ppyonki";

    public static final String SESSION_TOKEN = "cu.inmobile.SESSION_TOKEN";
    public static final String IS_PRO_USER = "cu.inmobile.IS_PRO_USER";
    public static final String USER_ID = "cu.inmobile.USER_ID";
    public static final String CHAT_TOKEN = "cu.inmobile.CHAT_TOKEN";
    public static final String CHAT_USER = "cu.inmobile.CHAT_USER";
    public static final String NAME = "NAME";
    public static final String POPULARITY = "POPULARITY";
    public static final String BALAS = "BALAS";
    public static final String IMG_URL = "IMG_URL";
    public static final String IMG_DRAWER = "IMG_DRAWER";
    public static final String GENDER = "GENDER";
    public static final String MIN_EDAD = "MIN_EDAD";
    public static final String MAX_EDAD = "MAX_EDAD";
    public static final String CITY_FILTER = "CITY";
    public static final String CITY_USER = "CITY_USER";
    public static final String FIRST_TIME = "FIRST_TIME";
    public static final String LAST_UPDATE = "LAST_UPDATE";
    public static final String USER_MESS = "USER_MESS";
    public static final String USER_COUNT = "USER_COUNT";
    public static final String LIKES = "LIKES";
    public static final String MATCHES = "MATCHES";
    public static final String ABOUT_ME = "ABOUT_ME";








    /**
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context, String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }

    /**
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context, String key,
                                  Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static void clearPreferences (Context context){

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        sp.edit().clear().apply();

    }

    public static String getMemberId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("memberId", null);
    }

    public static boolean hasBind(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setIsShortCut(Context context, Boolean isLogin) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isShortCut", isLogin);
        editor.commit();
    }

    public static Boolean isShortCut(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean("isShortCut", false);
    }


}
