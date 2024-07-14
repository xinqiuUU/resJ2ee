package bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Resfood implements Serializable {
    private Integer fid;
    private String fname;
    private Double normprice;
    private Double realprice;
    private String detail;
    private String fphoto;

    //增加一个属性  点赞数  这个属性的值是redis提供的
    private Long praise;
}
