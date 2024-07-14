package utils;
/*
       常量类:用于管理维护系统中的一个属性名
 */
public class YcConstants {
    /*  redis中保存用户的历史记录的键名  后缀   */
    public final static String REDIS_VISITED="_visited";
    /*  redis中保存的用户点赞记录  后缀   格式 : 用户id_后缀 */
    public final static String REDIS_PRAISE="_praise";
    /*  redis中保存的一个菜品被哪些用户点过赞  后缀 格式:  菜品id_后缀 */
    public final static String REDIS_FOOD_PRAISE="_food_praise";
    /* 系统中保存的登录用户  键名    */
    public final static String RESUSER="resuser";
    /*      系统中的购物车的  键  */
    public final static String CART="cart";

    public final static String REDIS_DEVICE_USERS="_decixe_users";

    public final static String REDIS_USERS_DEVICE="_users_decixe";
}
