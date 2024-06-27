package io.unbong.ubcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reply<T> {

    T value;
    ReplyType type;


    public static Reply<String> string(String value){
        return  new Reply<>(value, ReplyType.SIMPLE_STRING);
    }

    public static Reply<String> bulkString(String value){
        return  new Reply<>(value, ReplyType.BULK_STRING);
    }

    public static Reply<Integer> integer(Integer value){
        return  new Reply<Integer>(value, ReplyType.INT);
    }

    public static Reply<String> error(String value){
        return  new Reply<String>(value, ReplyType.ERROR);
    }

    public static Reply<String[]> array(String[] value){
        return  new Reply<String[]>(value, ReplyType.ARRAY);
    }
}
