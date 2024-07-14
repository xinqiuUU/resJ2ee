package web.servlet;

import bean.CartItem;
import bean.Resfood;
import com.yc.dao.DBHelper;
import model.JsonModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ResorderServlet",value = "/resorder.action")
public class ResorderServlet extends BaseServlet {

    //获取当前用户的购物车信息
    protected void getCartInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        HttpSession session = req.getSession();
        if (session.getAttribute("cart")!=null){
            jm.setCode(1);
            Map<Integer,CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
            jm.setObj(  cart.values() );
        }else {
            jm.setCode(0);
        }
        super.writeJson(jm,resp);
    }
    protected void clearAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        HttpSession session = req.getSession();
        session.removeAttribute("cart");
        jm.setCode(1);
        super.writeJson(jm,resp);
    }
        //op=order 加入购物车
    protected void order(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //购物车的操作
        JsonModel jm = new JsonModel();
        //1.判断  是否登录
        HttpSession session = req.getSession();
//        if (session.getAttribute("resuser")==null){
//            jm.setCode(-1);
//            super.writeJson(jm,resp);
//            return;
//        }
        int fid = Integer.parseInt(req.getParameter("fid"));
        int num = Integer.parseInt(req.getParameter("num"));
        //根据fid查出  resfood对象  再放到  购物车
        //      a. 根据fid查数据库
        String sql = "select * from resfood where fid=?";
        DBHelper db = new DBHelper();
        List<Resfood> list = db.select(Resfood.class,sql,fid);
        Resfood food = null;
        if (list!=null&&list.size()>0){
            food=list.get(0);
        }
        //      b. 根据fid从 session中取
//        List<Resfood> list = (List<Resfood>) session.getAttribute("foodList");//性能会好，但测试会有问题

        //2.添加购物车
        //      session：一个客户端一个session( 存map<fid,购物项对象> )
        //          购物项对象：   Resfood对象   数量   小计
        /*  逻辑：1.先从session中取出map
                2.判断 这个map中是否有要购物的fid( containkey),
                3.如果有，则从map中取出fid对应的 购物项目 将购物项中的数量+1
                   如果没有，则创建一个购物项对象，以fid做键，存到map中
                4.将map存到session中
         */
        Map<Integer, CartItem> cart = new HashMap<Integer, CartItem>();
        if ( session.getAttribute("cart")!=null){
            cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        }else {
            session.setAttribute("cart",cart);
        }
        CartItem ci = null;
        if ( cart.containsKey( fid ) ){
            //如果原来有下过单，则加数量
            ci = cart.get(fid);
            int newNum = ci.getNum()+num;
            ci.setNum(newNum);
        }else {
            ci = new CartItem();
            ci.setFood( food );
            ci.setNum(num);
        }
        if ( ci.getNum()<=0 ){
            cart.remove(fid);
        }else {
            //计算小计
            ci.getSmallCount();
            //存入购物车
            cart.put(fid,ci);
        }
        session.setAttribute("cart",cart);
        jm.setCode(1);
        //cart 是一map，我们取这个map的值  Set[CartItem，CartItem，CartItem]
        jm.setObj(  cart.values()  );

        super.writeJson(jm,resp);
    }
}