package web.filter;


import model.JsonModel;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@WebFilter(value = "*.action" )  //*.action拦截所有的请求
@WebFilter(value = {"/custOp.action","/resorder.action","/resfood.action" }  )//只拦截两个请求地址
public class RightFilter extends CommonFilter {

//    public RightFilter(){
//        System.out.println("6666");
//    }
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        System.out.println("init");
//    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //doFilter 类似于Servlet中的servlet方法
        //配置的请求都会经过这个方法
        // System.out.println("doFilter");
        //即判断httpSession中是否有  resuser这个键，如果没有，则说明没有登录
        //如果没有登录，则回送  JsonModel的对象 code=0,error的信息
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String op = req.getParameter("op");

        HttpSession session = req.getSession();
//        if (session.getAttribute("resuser")==null){
//            JsonModel jm = new JsonModel();
//            jm.setCode(-1);
//            jm.setError("用户暂未登录");
//            super.writeJson(jm,resp);
//        }else {
//            //   过滤器链
//            chain.doFilter(request,response);///****及重要  这样请求才可以继续向后
//        }
        if ( session.getAttribute("resuser")!=null){
            chain.doFilter(request,response);///****及重要  这样请求才可以继续向后
        }else if ( "findFoodByPage".equals(op) || "findAllFoods".equals(op) ){
            chain.doFilter(request,response);///****及重要  这样请求才可以继续向后
        }else {
            //是过滤的地址  但没有登录
            JsonModel jm = new JsonModel();
            jm.setCode(-1);
            jm.setError("用户暂未登录");
            super.writeJson(jm,resp);
        }
    }

    public void destroy() {
//        System.out.println("destroy");
    }
}
