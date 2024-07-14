package web.listner;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener
//这个监听器用于监听   session的变化情况  单例模式
public class ResSessionChangeListner implements HttpSessionAttributeListener {
    public ResSessionChangeListner(){
        System.out.println("ResSessionChangeListner");
    }
    //session 中的属性的添加  -》 第一次 session.setAttribute("键",obj);
    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
//        System.out.println("session->attributeAdded:");
        String key = se.getName();
        Object obj = se.getValue();
//        System.out.println(key+":"+obj);
    }
    //session 中的属性的替换  -》 第二次 session.setAttribute("键",obj);
    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        System.out.println("session->attributeReplaced:");
        String key = se.getName();
        Object obj = se.getValue();
//        System.out.println(key+":"+obj);
    }
    //session  中的 删除 一个键  session.removeAttribute();
    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
//        System.out.println("session->attributeRemoved:");
        String key = se.getName();
        Object obj = se.getValue();
//        System.out.println(key+":"+obj);
    }


}
