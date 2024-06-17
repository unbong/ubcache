package io.unbong.ubcache.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.unbong.ubcache.core.UBCacheHandler.CACHE;

/**
 * cache entries
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-17 10:53
 */
public class UBCache {

    Map<String, String> map = new HashMap<>();

    public String get(String key){
        return map.get(key);
    }

    public void set(String key, String value)
    {
        map.put(key, value);
    }

    public int del(String... keys ) {
        if(keys == null) return 0;
        return (int)Arrays.stream(keys).map(map::remove).filter(Objects::nonNull).count();

    }

    public int exists(String... keys)
    {
        if(keys == null) return 0;
        return (int)Arrays.stream(keys).map(map::containsKey).filter(x-> x).count();
    }

    public String [] mget(String... keys){
        if(keys == null) return null;
        return Arrays.stream(keys).map(map::get).toArray(String[]::new);
    }

    public void mset(String[] keys, String[] values) {
        if(keys == null  || values == null) return;
        for (int i = 0; i < keys.length; i++) {
            set(keys[i], values[i]);
        }
    }

    public int incr(String key)
    {
        int val =0 ;
        String str = map.get(key);
        try{
            if(str == null) return val;
            val = Integer.parseInt(str);
            val++;
            set(key, String.valueOf(val));
            return val;
        }catch (NumberFormatException nfe)
        {
            throw nfe;
        }
    }

    public int decr(String key)
    {
        int val =0 ;
        String str = map.get(key);
        try{
            if(str == null) return val;
            val = Integer.parseInt(str);
            val--;
            set(key, String.valueOf(val));
            return val;
        }catch (NumberFormatException nfe)
        {
            throw nfe;
        }
    }
}
