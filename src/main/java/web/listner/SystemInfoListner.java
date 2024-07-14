package web.listner;

import com.yc.dao.RedisHelper;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
//应用启动时，记录当前系统的信息  单例模式
public class SystemInfoListner implements ServletContextListener {
    Timer timer;
    //容器初始化方法  在应用程序 在服务器中加载时调用 当应用程序在服务器中加载时调用
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("应用程序  启动。。。创建  appliction:");
        //记录系统信息
        String osName = System.getProperty("os.name");
        String osVersion=System.getProperty("os.version");
        System.out.println("应用程序启动，时间："+new Date());
        System.out.println("操作系统类型："+osName);
        System.out.println("操作系统版本："+osVersion);
        System.out.println("空闲内存："+Runtime.getRuntime().freeMemory() );

        //启动一个Timer定时任务 创建一个新的 Timer 实例，设置为守护线程模式。
        timer = new Timer(true);
        Calendar c = Calendar.getInstance();//获取当前时间的日历实例
        c.add( Calendar.DATE,1);//将当前日期加一天
        //设置时间为午夜
        c.set( Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        //计划定时任务，每24小时执行一次
        /*
            new MyTimerTask()：定时任务类
            c.getTime()：第一次执行的时间
            24*60*60*1000：每24小时执行一次
         */
        timer.schedule(  new MyTimerTask(),c.getTime(),24*60*60*1000 );
        System.out.println("redis-delete");
    }

    //销毁方法，在服务器关闭时调用
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        System.out.println("Destroyed");
        if (timer!=null){
            timer.cancel();
        }
    }
}

//  定时任务类  MyTimerTask一个继承自 TimerTask 的类，用于定义定时任务
class MyTimerTask extends TimerTask{

    //定时任务的主要逻辑
    /*
     此代码的作用是在应用启动时设置一个定时任务，
     每天午夜删除 Redis 中与当前星期几对应的数据，并在服务器关闭时取消该定时任务
     */
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();//获取当前时间的日历实例
        int weekday = calendar.get(  Calendar.DAY_OF_WEEK );//获取当前星期几（1-7，对应星期天到星期六）
        Jedis jedis = RedisHelper.getRedisInstance();
        jedis.del(  weekday+"");//删除 Redis 中键为当前星期几（字符串形式）的数据
        jedis.close();
    }
}