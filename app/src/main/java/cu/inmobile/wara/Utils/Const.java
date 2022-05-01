package cu.inmobile.wara.Utils;

import com.paypal.android.sdk.payments.PayPalConfiguration;

/**
 * Created by amal on 03/01/17.
 */
public class Const {

    // PayPal configuration data
    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String CONFIG_CLIENT_ID = "AQ34wZtxoyme2qpLB3vsx1vjEGWQZ1amipmJ6lrcoZSqUVyX8otrGzjTeIJ30923NXDcXnp5vJuRcuOK";


    public static class Params{
        public static String EMAIL = "email";
        public static String PASSWORD = "password";
        public static String SUCCESS = "success";
        public static String ERROR_CODE = "error_code";
        public static String ERROR = "error";
        public static String ID = "user_id";
        public static String TOKEN = "access_token";
        public static String ENCOUNTER_LEFT = "encounter_left";
        public static String LATITUDE = "latitude";
        public static String LONGITUDE = "longitude";
        public static String NAME = "name";
        public static String PICTURE = "profile_picture_url";
        public static String PICTURE_URL = "profile_pic_url";
        public static String ABOUT_ME = "about_me";
        public static String CITY = "city";
        public static String STATUS = "status";
        public static String ERROR_TYPE = "type_error";
        public static String OS = "os";
        public static String VERSION = "app_version";

        public static String GENDER = "gender";
        public static String GENDER_MALE = "male";
        public static String GENDER_FEMALE = "female";
        public static String GENDER_BOTH = "male,female";

        public static final int NOTIFICATION_MES = 1000;


    }
}
