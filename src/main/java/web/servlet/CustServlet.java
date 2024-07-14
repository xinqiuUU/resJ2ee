package web.servlet;

import bean.CartItem;
import bean.Resorder;
import bean.Resuser;
import model.JsonModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebServlet(value = "/custOp.action")
public class CustServlet extends BaseServlet {
    //确认订单
    protected void confirmOrder(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        //取参数   订单的参数  购物车
        Resorder resorder = super.parseObjectFromRequest(req, Resorder.class);
        //调用业务层处理
        HttpSession session = req.getSession();
        //取出购物车中的数据
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        Set<CartItem> cartItems = new HashSet<>(cart.values()) ;
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        OrderBiz ob = new OrderBiz();
        try {
            int result = ob.order( resorder ,cartItems, resuser);
            jm.setCode(1);

            session.removeAttribute("cart");//删除session中的商品数据，购物车

        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setError(e.getMessage());
        }
        super.writeJson(jm,resp);
    }
}