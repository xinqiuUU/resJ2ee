package model;

import lombok.Data;

import java.io.Serializable;

@Data
public class JsonModel implements Serializable {
    private Integer code;  //响应码  ：  0：表示失败  1：表示成功
    private Object obj;
    private String error;
}
