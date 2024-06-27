package io.unbong.ubcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * cache entries
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-17 10:53
 */
public class UBCache {

    // 1 可用？ 指定为泛型
    // 2 新的类
    Map<String, CacheEntry<?>> map = new HashMap<>();

    // =============== 1. String ================
    public String get(String key){
        CacheEntry<String> cacheEntry = (CacheEntry<String>)map.get(key);
        return cacheEntry.getValue();
    }

    public void set(String key, String value)
    {

        CacheEntry<String> entry = new CacheEntry<>(value);
        map.put(key, entry);
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
        return Arrays.stream(keys).map(this::get)
                .toArray(String[]::new);
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
        String str = get(key);
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
        String str = get(key);
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

    public Integer strlen(String key) {
        return get(key) == null ? null: get(key).length();
    }

    // =============== 1. String end ================

    // =============== 2. list ================
    public Integer lpush(String key, String... value) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null)
        {
            entry = new CacheEntry<>(new LinkedList<>());
            this.map.put(key, entry);
        }
        LinkedList<String> exist = entry.getValue();
        Arrays.stream(value).forEach(exist::addFirst);
        return value.length;
    }

    public String[] lpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if(exist == null ) return  null;

        int len =Math.min( count, exist.size());
        String[] ret = new String[len];
        int index = 0;
        while (index < len)
        {
            ret[index++] = exist.removeFirst();
        }
        return  ret;

    }

    public String[] rpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if(exist == null ) return  null;

        int len =Math.min( count, exist.size());
        String[] ret = new String[len];
        int index = 0;
        while (index < len)
        {
            ret[index++] = exist.removeLast();
        }
        return  ret;
    }

    public Integer rpush(String key, String[] value) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null)
        {
            entry = new CacheEntry<>(new LinkedList<>());
            this.map.put(key, entry);
        }
        //  check value is null
        if(value == null || value.length == 0) return 0;
        LinkedList<String> exist = entry.getValue();
        //Arrays.stream(value).forEach(exist::addLast);
        exist.addAll(List.of(value));
        return value.length;
    }

    public Integer llen(String key) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null) return 0;
        LinkedList<String> exist = entry.getValue();
        if(exist == null ) return  0;
        return exist.size();
    }

    public String lindex(String key, int index) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if(exist == null ) return  null;
        if(index < 0 || index >= exist.size()) return null;
        return exist.get(index);
    }

    public String[] lrange(String key, int start, int end) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>)map.get(key);
        if(entry == null) return null;
        LinkedList<String> exist = entry.getValue();
        if(exist == null ) return  null;

        if (start < 0)  return  null;
        int size = exist.size();
        if(start >= size) return null;
        if(end >= size) end = size-1;
        int len = Math.min(size, end-start+1);
        String[] ret = new String[len];
        for (int i = 0; i < len; i++) {
            ret[i] = exist.get(start+i);
        }
        return  ret;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CacheEntry<T> {
        private T value;
    }
}
