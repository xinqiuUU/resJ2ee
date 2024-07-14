package Fen;

import lombok.Data;

import java.util.List;

@Data
//分页的实体类
public class PageBean<T> {
    //以下四个属性是界面给的已知参数
    private Integer pageno;  //当前第几页
    private Integer pagesize;//每页多少条
    private String sortby;//排序列的列名
    private String sort;//取值为：asc/desc

    //以下属性是数据库查询得到的结果
    private long total;//总记录数
    private List<T> dataset;
    //需要在业务层中计算
    private Integer totalpages;//总共多少页
    private Integer pre;//上一页的页数
    private Integer next;//下一页的页数

}
