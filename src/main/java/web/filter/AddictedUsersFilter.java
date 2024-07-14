package web.filter;

import bean.Resuser;
import com.yc.dao.RedisHelper;
import redis.clients.jedis.Jedis;
import utils.YcConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;

@WebFilter(filterName = "AddictedUsersFilter" , value = "/resuser.action")
public class AddictedUsersFilter extends CommonFilter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request,response);//过滤器向后调用

        //只针对  login操作  的响应上做处理  判断 是否登陆成功
        //*****连续登录操作
        HttpServletRequest req = (HttpServletRequest) request;
        String op = req.getParameter("op");
        if ("login".equals(op)){
            //说明是 login操作
            //如果登录成功  在 session 中 一定有resuser的一个attribute
            HttpSession session = req.getSession();
            if (  session.getAttribute(YcConstants.RESUSER)!=null){
                //登录成功  -》 取出今天是周几  存到redis中
                Resuser resuser = (Resuser) session.getAttribute(YcConstants.RESUSER);
                long uid = resuser.getUserid();
                Jedis jedis = RedisHelper.getRedisInstance();
                //业务：取出服务器的时间  周几  作为键来存
                int weekday = Calendar.getInstance().get( Calendar.DAY_OF_WEEK);//周日是1 周六是7
                jedis.setbit(weekday+"",uid,true); // 将uid这一位设置为 1
                jedis.close();


            }
        }
    }
}
