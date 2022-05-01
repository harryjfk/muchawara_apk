package cu.inmobile.wara.Networking;

/**
 * Created by amal on 13/02/17.
 */
public abstract class Endpoints {
    public static String baseRoot = "http://www.muchawara.com/";
    //public static String baseRoot = "http://192.168.1.27/muchawara/public/";
    public static String basebUrl = baseRoot+"index.php/";
    //    public static String basebUrl = "http://172.16.10.11/muchawara/public/index.php/";
    public static String baseUrl = basebUrl + "api/";
    public static String baseThumbUrl = baseRoot + "uploads/others/thumbnails/";
    public static String baseNoThumbUrl = baseRoot + "uploads/others/encounters/";


    //public static String baseUrl = "https://www.muchawara.com/api/";
    public static String facebookUrl = baseUrl + "facebook/login-or-register";
    public static String saveFilterUrl = baseUrl + "people-nearby/filter/save";
    public static String getFilterUrl = baseUrl + "people-nearby/filter-settings";
    public static String getEncountersUrl = baseUrl + "encounters";
    public static String setEncountersSeen = baseUrl + "encounter/setencountersseen";
    public static String setMatchesSeen = baseUrl + "encounter/setmatchesseen";
    public static String blockUrl = baseUrl + "report/user";
    public static String viewUserUrl = baseUrl + "profile";
    public static String updateLocation = baseUrl + "profile/me/current-location/update";
    public static String likeUrl = baseUrl + "encounter/user/like";
    public static String myProfileUrl = baseUrl + "profile/me";
    public static String mapUserSocket = baseUrl + "chat/map-user-socket";
    public static String messageHistoryUrl = baseUrl + "messenger/messages";
    public static String updateAppValues = baseUrl + "update_app_values";

    public static String getMessageUrl = baseUrl + "messenger/search";
    public static String uploadOtherPhotosUrl = baseUrl + "profile/me/upload-other-photos";
    public static String updateBasicInfoUrl = baseUrl + "profile/me/update-basic-info";
    public static String deleteAccount = baseUrl + "settings/user/delete";
    public static String getCreditPackages = baseUrl + "paypal/packages";
    public static String buyCredits = baseUrl + "paypal/buy/credits";
    public static String checkBoost = baseUrl + "boost/check";
    public static String activateBoost = baseUrl + "people-nearby/pay/riseup";
    public static String buySuperPowerUrl = baseUrl + "paypal/buy/superpower";
    public static String getPeopleNearbyUrl = baseUrl + "people-nearby";
    public static String LikedYouUrl = baseUrl + "likes";
    public static String VisitorUrl = baseUrl + "visitors";
    public static String MyLikeUrl = baseUrl + "mylikes";
    public static String getBullets = baseUrl + "get_my_bullets";
    public static String getWara = baseUrl + "matches";
    public static String getCities = baseUrl + "city";


    //This url to fetch the spotlight members.
    public static String spotlight = baseUrl + "spotlight";

    //This url to add the logged in user to the sotlight
    public static String spotlightAddMe = baseUrl + "spotlight/add";

    //This url to update the logged in user's about me information.
    public static String updateAboutMe = baseUrl + "profile/me/update-aboutme";

    public static String allGifts = baseUrl + "gifts/all";

    public static String getCustomFields = baseUrl + "get-custom-fields";

    public static String loginUrl = baseUrl + "login";

    public static final String registerUrl = baseUrl + "register";

    public static String updateProfilePictureUrl = baseUrl + "profile/me/upload-profile-picture";

    public static String sendGift = baseUrl + "gift/send";

    public static String uploadChatImage = baseUrl + "chat/upload/image";
    public static String readMessages = baseUrl+"messenger/readMessages";
    public static String checkCreated = baseUrl+"messenger/checkCreated";
    public static String bindUsers = baseUrl+"messenger/bindUsers";
    public static String unReadedCount = baseUrl+"messenger/unReadedCount";
    public static String sendMessage = baseUrl+"messenger/sendMessage";
    public static String status = baseUrl+"messenger/status";
}
