package bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItem implements Serializable {
    private Resfood food;
    private Integer num;
    private Double smallCount;
    public Double getSmallCount(){
        if ( food !=null){
            smallCount = this.food.getRealprice() * this.num;
        }
        return smallCount;
    }
}
