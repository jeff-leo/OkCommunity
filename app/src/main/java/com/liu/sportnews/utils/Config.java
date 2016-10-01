package com.liu.sportnews.utils;

/**
 * Created by Liujianfeng on 2016/7/12.
 */
public class Config {

    /*
    新闻api
     */
    public static final String REQUEST_URL = "http://v.juhe.cn/toutiao/index?type=";
    public static final String KEY = "&key=";
    public static final String APP_KEY = "263b95480eb3742c864a49a0481e72aa";

    public static final String TOP = "top";
    public static final String SHEHUI = "shehui";
    public static final String GUONEI = "guonei";
    public static final String GUOJI = "guoji";
    public static final String YULE = "yule";
    public static final String TIYU = "tiyu";
    public static final String JUNSHI = "junshi";
    public static final String KEJI = "keji";
    public static final String CAIJING = "caijing";
    public static final String SHISHANG = "shishang";

    public static final String SP_KEY = "spkey";

    public static final String FAIL = "fail";

    /*
    本地登录注册api
     */
    //119.29.158.110
    public static final String LOGIN_URL = "http://127.0.0.1/NewsServer/";
    public static final String LOCAL_URL = "http://127.0.0.1/NewsServer/base";
    public static final String UPLOAD_HEAD_IMG_URL = "http://127.0.0.1/NewsServer/uploadImage";//上传头像的地址
    public static final String APK_URL = "http://127.0.0.1/NewsServer/newApk/app-release.apk";//新版本apk的地址
    public static final String GET_PICTURE_URL = "http://127.0.0.1/NewsServer/getpictures";//图集图片数据地址的存放url
    public static final String PICTURE_URL = "http://127.0.0.1/NewsServer/pictures/";
    public static final String APP_NAME = "ok社区.apk";
    public static final String ACTION = "action";
    public static final String STATUS= "status";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_REGISTER = "register";
    public static final String ACTION_COLLECTS= "collect";
    public static final String ACTION_GET_COLLECTS= "get_collects";
    public static final String ACTION_DELETE_COLLECTS= "delete_collects";
    public static final String ACTION_SEARCH_COLLECTS= "search_collects";
    public static final String ACTION_UPLOAD_INFO= "upload_info";
    public static final String ACTION_SEARCH_INFO= "search_info";
    public static final String ACTION_ADD_TIMELINE= "add_timeline";
    public static final String ACTION_GET_TIMELINE= "get_timeline";
    public static final String ACTION_ADD_COMMENT= "add_comment";
    public static final String ACTION_GET_COMMENT= "get_comment";
    public static final String ACTION_GET_USER_COMMENT= "get_user_comment";
    public static final String ACTION_GET_ALERT_COMMENT= "get_alert_comment";
    public static final String ACTION_ADD_COMMENT_COUNT= "add_comm_count";
    public static final String ACTION_CHECK_VERSION= "check_version";
    public static final String ACTION_ADD_FEED_BACK= "add_feedback";


    //用于设置sharedPreference
    public static final String ISLOGIN = "islogin";//登录状态
    public static final String USERNAME = "username";//登录用户名
    public static final String REGISTE_DATE = "days";//注册日期
    public static final String HEAD_URL_KEY = "headUrlKey";//头像连接spkey
    public static final String NICKNAME_KEY = "nickNameKey";//昵称spkey
    public static final String SEX_KEY = "sexKey";//性别spkey

    //设置Intent的key
    public static final String NEWS_DATA = "newsData";
    public static final String WECHAT_DATA = "weChatData";
    public static final String COLLECT_DATA = "collectData";

    //登录之后保存的用户名
    public static String login_name = "";
    public static Boolean login_status = false;

    //城市的json文件
    public static final String CITIES = "cities";

    /*
     图灵机器人api
     */
    public static final String TULING_URL = "http://www.tuling123.com/openapi/api";
    public static final String ROBOT_KEY = "f5d2132e81a76d1ac747414275e4f0ad";

    /*
    微信精选api
     */
    public static final String WE_CHAT_URL = "http://v.juhe.cn/weixin/query?";
    public static final String WE_CHAT_KEY = "db0dc75f073094769b1a2c8e1f20d0c3";

    /*
    天气预报api
     */
    public static final String WEATHER_URL = "http://op.juhe.cn/onebox/weather/query?cityname=";
    public static final String WEATHER_KEY= "&key=f9bbbafcb958a98914ac09f56b87869b";
    public static final String CITY = "city";//当前城市的sp键
    public static final String CITY_WEATHER = "cityWeather";//天气状况的sp键
    public static final String PROVINCE_CITY = "province";//省份的sp键

    /*
    用于辨别更新的方法
     */
    public static final int MAIN = 1;
    public static final int ITEM = 0;
}
