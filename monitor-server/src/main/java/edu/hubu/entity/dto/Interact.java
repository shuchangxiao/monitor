package edu.hubu.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@AllArgsConstructor
@Data
public class Interact {
    Integer tid;
    Integer uid;
    Date time;
    String type;
    public String toKey(){
        return tid + ":" + uid;
    }
    public static Interact parseInteract(String str,String type){
        String[] keys = str.split(":");
        return new Interact(Integer.parseInt(keys[0]),Integer.parseInt(keys[1]),new Date(),type);
    }
}
