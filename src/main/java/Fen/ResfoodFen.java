package Fen;

import bean.Resfood;
import com.yc.dao.DBHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ResfoodFen {
    public PageBean findByPage( PageBean pageBean ) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Resfood> dataset = this.findByPage(pageBean.getPageno(),pageBean.getPagesize(),pageBean.getSortby(),pageBean.getSort());
        long total = this.countAll();

        pageBean.setTotal(total);
        pageBean.setDataset(dataset);
        //其它的分页数据
        //计算总页数
        long totalPages=total%pageBean.getPagesize()==0?total/pageBean.getPagesize():total/pageBean.getPagesize()+1;
        pageBean.setTotalpages( (int) totalPages );
        //上一页页号的计算
        if ( pageBean.getPageno()<=1){
            pageBean.setPre(   1   );
        }else {
            pageBean.setPre(  pageBean.getPageno()-1  );
        }
        //下一页的页号
        if (  pageBean.getPageno() == totalPages ){
            pageBean.setNext((int) totalPages);
        }else {
            pageBean.setNext(  pageBean.getPageno()+1 );
        }
        return pageBean;
    }
    //查询resfood的记录条数
    private long countAll(){
        String sql = "select count(fid) c from resfood";
        DBHelper db = new DBHelper();
        long l = (long) db.select(sql).get(0).get("c");
        return l;
    }
    //查询当前pageno这一页的数据
    //pageno 当前第几页
    //pagesize 每页多少条
    private List<Resfood> findByPage(int pageno,int pagesize,String sortby,String sort) throws InvocationTargetException, InstantiationException, IllegalAccessException {
//        String sql = "select * from resfood order by ? ? limit ?,?"; //***排序会失效
        String sql = "select * from resfood order by "+sortby+" "+sort+" limit ?,? ";//***会有注入风险
        DBHelper db = new DBHelper();
        int start = (pageno-1)*pagesize;
//      List<Resfood> list = db.select(Resfood.class,sql,sortby,sort,start,pagesize);
        List<Resfood> list = db.select(Resfood.class,sql,start,pagesize);

        return list;
    }
}
