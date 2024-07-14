package web.servlet;

import Fen.PageBean;
import Fen.ResfoodFen;
import bean.Resfood;
import bean.Resuser;
import com.yc.dao.DBHelper;
import com.yc.dao.RedisHelper;
import lombok.Data;
import model.JsonModel;
import redis.clients.jedis.Jedis;
import utils.YcConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@WebServlet(value = "/resfood.action")
public class FoodServlet extends BaseServlet{

    //分页查询
    // 参数： pageno=1  &  pagesize=5  &   sortby=realprice  &   sort=asc
    protected void findFoodByPage(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        //取前端的参数
        PageBean pb = super.parseObjectFromRequest(req, PageBean.class);
        //调用业务层完成分页查询
        ResfoodFen rb = new ResfoodFen();
        try {
            pb =  rb.findByPage(pb);
            //tomcat提供的缓存 redis  做缓存  菜单放到 session中
            HttpSession session=req.getSession();
            session.setAttribute("foodList",pb.getDataset());
            jm.setCode(1);
            jm.setObj( pb );
        } catch (Exception e) {
           e.printStackTrace();
           jm.setCode(0);
           jm.setError("分页查询失败");
        }finally {
            super.writeJson(jm,resp);
        }
    }

    //op=findAllFoods
    protected void findAllFoods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        String sql = "select * from resfood";
        DBHelper db = new DBHelper();
        List<Resfood> list =  db.select(Resfood.class,sql);

        //tomcat提供的缓存 redis  做缓存  菜单放到 session中
        HttpSession session=req.getSession();
        session.setAttribute("foodList",list);

        jm.setCode(1);
        jm.setObj(list);
        super.writeJson(jm,resp);
    }

    /*
        clickPraise
        登录用户:点赞，并返回此商品最新的点赞数：
        要求: 一个用户不能重复点赞一个商品  只有登录用户能点赞
     */
    protected void clickPraise(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonModel jm = new JsonModel();
        //取菜品名
        try {
            Resfood resfood = super.parseObjectFromRequest(req, Resfood.class);
            int fid = resfood.getFid();
            HttpSession session = req.getSession();

            Resuser resuser = (Resuser) session.getAttribute(YcConstants.RESUSER);
            int userid = resuser.getUserid();

            Jedis jedis = RedisHelper.getRedisInstance();
            //菜品号_praise    Set<用户编号>
            if (  jedis.sismember(  fid+YcConstants.REDIS_FOOD_PRAISE,userid+""   )){
                //此用户已经点过这道菜的赞  再点就是取消
                jedis.srem(  fid+ YcConstants.REDIS_FOOD_PRAISE,userid+"");
                //用户编号_praise   Set<菜品号>  此处也要删除
                jedis.srem(  userid+ YcConstants.REDIS_PRAISE,fid+"");
            }else {
                //此用户没有对这道菜点过赞
                jedis.sadd(  fid+ YcConstants.REDIS_FOOD_PRAISE,userid+"");
                //记录此用户对哪些菜点过赞
                jedis.sadd(  userid+ YcConstants.REDIS_PRAISE,fid+"");
            }
            //取出最新点赞数
            long praise = jedis.scard(fid+ YcConstants.REDIS_FOOD_PRAISE );
            resfood.setPraise(  praise );
            jm.setCode(1);
            jm.setObj(   resfood  );
        }  catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setError(e.getMessage());
        }
        super.writeJson(jm,resp);
    }

    // 登录用户:  获取用户的浏览记录
    protected void getHistory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        HttpSession session = req.getSession();
        //权限  判断的这一段功能  写到  righFilter中去
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        int userid = resuser.getUserid();

        Jedis jedis = new Jedis("localhost",6379);
        Set<String> visitedFoodIds = jedis.zrevrange(  userid+"_visited",0,4);//获取最近的5个访问过的菜品
        //如果还没有访问过任何一个商品则直接返回  code=0;
        if (  visitedFoodIds==null || visitedFoodIds.size()<=0){
            jm.setCode(0);
            super.writeJson(  jm ,  resp );
            return;
        }
        //采用  sql 中的  拼接多个 商品的查询  减少多个sql语句操作
        //select * from resfood where fid in( ?, ?, ?, ?, ?)
        StringBuffer sqlbuffer = new StringBuffer("select * from resfood where fid in(  ");
        for ( String fid:visitedFoodIds){
            sqlbuffer.append(" ?,");
        }
        //select * from resfood where fid in(  ?, ?, ?, ?, ?,
        String sql = sqlbuffer.toString();
        //去掉最后的 ,
        //得到 ： select * from resfood where fid in(  ?, ?, ?, ?, ?
        sql = sql.substring( 0 ,  sql.length()-1 );
        sql=sql+")";
        //最后的语句:  select * from resfood where fid in(  ?, ?, ?, ?, ?)
        System.out.println("执行的sql语句:"+ sql );
        DBHelper db = new DBHelper();
        List<Resfood> list = db.select(Resfood.class,sql,visitedFoodIds.toArray());
        jm.setCode(1);
        jm.setObj(list);
        super.writeJson( jm , resp );

    }
    //把用户浏览记录保存到redis数据库
    protected void traceBrowseFood(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JsonModel jm = new JsonModel();
        String fid = req.getParameter("fid");
        HttpSession session = req.getSession();
        if (  session.getAttribute("resuser")==null){
            jm.setCode(0);
            jm.setError("未登录用户不记录足迹");
            super.writeJson(jm,resp);
            return;
        }
        Resuser resuser = (Resuser) session.getAttribute("resuser");
        int userid = resuser.getUserid();

        Date date = new Date();

        Jedis jedis = new Jedis("localhost",6379);
        //先检查此键的长度  如超过4个  则删除4以后的数据
        // 有序集合的 key
        String key = userid+"_visited";
        // 判断成员是否存在
        Long rank = jedis.zrank(key, fid);
        //key的长度
        long length = jedis.zcard(userid+"_visited");
        if ( length>4 && rank == null){
            //rem表示降序排序
            jedis.zremrangeByRank(userid+"_visited",0,0);
        }
        //再存
        jedis.zadd(userid+"_visited",date.getTime(),fid);
        jm.setCode(1);
        super.writeJson(jm,resp);
    }


}
