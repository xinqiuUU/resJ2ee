package web.filter;

import bean.Resuser;
import com.yc.dao.RedisHelper;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
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

/*
      本filter拦截登录请求 ， 只要登录成功了  则记录此用户采用的 设备（浏览器）
 */
@WebFilter(filterName = "DeviceFilter",value = "/resuser.action")
public class DeviceFilter extends CommonFilter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        chain.doFilter(request,response);//过滤器向后调用

        //只针对  login操作  的响应上做处理  判断 是否登陆成功
        HttpServletRequest req = (HttpServletRequest) request;
        String op = req.getParameter("op");
        if ("login".equals(op)){
            //说明是 login操作
            //如果登录成功  在 session 中 一定有resuser的一个attribute
            HttpSession session = req.getSession();
            if (  session.getAttribute(YcConstants.RESUSER)!=null){
                Resuser resuser = (Resuser) session.getAttribute(YcConstants.RESUSER);
                String uid = resuser.getUserid()+"";

                Browser browser = UserAgent.parseUserAgentString(req.getHeader("User-Agent")).getBrowser();
                String browsername = browser.getName();
                Jedis jedis = RedisHelper.getRedisInstance();
                jedis.sadd(browsername+YcConstants.REDIS_DEVICE_USERS,uid);
                jedis.lpush(uid+YcConstants.REDIS_USERS_DEVICE,browsername);
                jedis.close();
            }
        }

    }
}
