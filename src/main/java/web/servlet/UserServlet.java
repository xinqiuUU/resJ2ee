package web.servlet;

import bean.Resuser;
import com.yc.dao.DBHelper;
import model.JsonModel;
import utils.Md5;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@WebServlet(name = "UserServlet",value="/resuser.action")
public class UserServlet extends BaseServlet{


    protected void checkLogin(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        JsonModel jm = new JsonModel();
        //取出session中的验证码
        HttpSession session = req.getSession();
        if (session.getAttribute("resuser")==null){
            jm.setCode(0);
        }else {
            jm.setCode(1);
            jm.setObj(  session.getAttribute("resuser") );
        }
        super.writeJson(jm,resp);
    }
    //op=logout
    protected void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        HttpSession session = req.getSession();
        session.removeAttribute("resuser");
        jm.setCode(1);
        super.writeJson(jm,resp);
    }


    //op=login
    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        Resuser resuser =  super.parseObjectFromRequest(req, Resuser.class);
        String valcode = req.getParameter("valcode");
        //取出标准验证码
        HttpSession session = req.getSession();
        boolean validate = false; //默认验证码通过
        if (    validate    ){
            String code = (String) session.getAttribute("code");
            //验证码判断
            if (  ! valcode.equals(code)    ){
                jm.setCode(0);
                jm.setError("验证码错误！");
                super.writeJson(jm,resp);
            }
        }
        //查询数据库
        resuser.setPwd(Md5.toMD5(Md5.toMD5(resuser.getPwd())));
        //简单场景：直接在servlet控制层访问数据库
        String sql = "select * from resuser where username=? and pwd=?";
        DBHelper db = new DBHelper();
        List<Resuser> list = db.select(Resuser.class,sql,resuser.getUsername(),resuser.getPwd());
        if (list!=null && list.size()>0){
            Resuser rs = list.get(0);
            rs.setPwd("");
            jm.setCode(1);
            jm.setObj( rs );
            //*******登录用户的信息记录到session中
            session.setAttribute("resuser",rs);
        }else {
            jm.setObj(0);
            jm.setError( "用户名或密码错误，登录失败");
        }
        super.writeJson(jm,resp);
    }

}
