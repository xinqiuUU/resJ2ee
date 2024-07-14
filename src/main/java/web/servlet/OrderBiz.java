package web.servlet;

import bean.CartItem;
import bean.Resorder;
import bean.Resuser;
import com.yc.dao.DBHelper;

import java.sql.*;
import java.util.Set;

//订单
public class OrderBiz {
    public int order(Resorder resorder, Set<CartItem> cartItems, Resuser resuser) throws SQLException {

        if ( cartItems==null || cartItems.size()<=0 ){
            throw new RuntimeException("订单为空。。。。");
        }
        int result = 0;
        DBHelper db = new DBHelper();
        Integer roid = 0;
        //事物处理:  订单信息
        Connection con = null;
        try {
            con = db.getConnection();
            con.setAutoCommit(false); //关闭隐式事物提交
            String sql = "insert into resorder( userid,address,tel,ordertime,deliverytime,ps,status ) values( ?,?,?,now(),?,?,0 )";
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//RETURN_GENERATED_KEYS代表获取自动生成的id
            pstmt.setString(1,resuser.getUserid()+"");
            pstmt.setString(2,resorder.getAddress()+"");
            pstmt.setString(3,resorder.getTel()+"");
            pstmt.setString(4,resorder.getDeliverytime()+"");
            pstmt.setString(5,resorder.getPs()+"");
            pstmt.executeUpdate();
            //取自动生成的id
            ResultSet rs = pstmt.getGeneratedKeys();
            if ( rs.next() ){
                roid = rs.getInt(1);
            }
            //循环所有的订单项，添加到数据库表  resorderitem
            String sql2 = "insert into resorderitem( roid,fid,dealprice,num) values( ?,?,?,?) ";
            for ( CartItem ci:cartItems){
                pstmt=con.prepareStatement(sql2);
                pstmt.setString(1,roid+"");
                pstmt.setString(2,ci.getFood().getFid()+"");
                pstmt.setString(3,ci.getFood().getRealprice()+"");
                pstmt.setString(4,ci.getNum()+"");
                pstmt.executeUpdate();
            }
            con.commit();  //事务提交
        } catch (Exception e) {
            e.printStackTrace();
            if (con!=null){
                con.rollback();  //事务回滚
            }
        }finally {
            if ( con!=null){
                con.close();
            }
        }
        return result;
    }
}
