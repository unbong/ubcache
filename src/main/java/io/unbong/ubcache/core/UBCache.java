package io.unbong.ubcache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Stream;

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


    // ======================== 3 set ========================
    public Integer sadd(String key, String[] value) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>)map.get(key);
        if(entry == null)
        {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<String> exist = entry.getValue();
        Arrays.stream(value).forEach(exist::add);
        //todo return length
        return value.length;

    }

    public String[] smembers(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>)map.get(key);
        if(entry == null) return null;

        LinkedHashSet<String> exist = entry.getValue();
        return exist.toArray(String[]::new);
    }

    public Integer scard(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>)map.get(key);
        if(entry == null) return null;

        LinkedHashSet<String> exist = entry.getValue();
        return exist.size();
    }

    public Integer sismember(String key, String value) {

        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>)map.get(key);
        if(entry == null) return 0;

        LinkedHashSet<String> exist = entry.getValue();
        return exist.contains(value)?1:0;
    }

    public Integer sremove(String key, String[] value) {

        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>)map.get(key);
        if(entry == null) return 0;

        LinkedHashSet<String> exist = entry.getValue();
        return value == null ? 0: (int)Arrays.stream(value).
                map(exist::remove).filter(x->x).count();
    }

    Random random = new Random();

    public String[] spop(String key, int count) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>)map.get(key);
        if(entry == null) return null;
        LinkedHashSet<String> exist = entry.getValue();
        if(exist == null ) return  null;

        int len =Math.min( count, exist.size());
        String[] ret = new String[len];
        int index = 0;
        // randomly remove item
        while (index < len)
        {
            String[] arr = exist.toArray(String[]::new);
            String obj = arr[random.nextInt(exist.size())];
            exist.remove(obj);
            ret[index++] = obj;
        }
        return  ret;
    }




    // ======================== 3 set end ========================

    // ======================== 4 hash  ========================
    public Integer hset(String key, String[] hkeys, String[] values) {
        if(hkeys == null || hkeys.length == 0) return 0;
        if(values == null || values.length == 0) return 0;
        if(hkeys.length != values.length) throw new IllegalArgumentException("key and val size not equal");
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
        {
            entry = new CacheEntry<>(new LinkedHashMap<>());
            this.map.put(key, entry);
        }
        LinkedHashMap<String, String> exist = entry.getValue();

        for (int i = 0; i < hkeys.length; i++) {
            exist.put(hkeys[i], values[i]);
        }
        //todo return length
        return hkeys.length;

    }

    public String hget(String key, String hkey) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
            return null;
        LinkedHashMap<String, String> exist = entry.getValue();
        // hum? key is array
        return exist.get(hkey);
    }

    public String[] hgetall(String key) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
            return null;
        LinkedHashMap<String, String> exist = entry.getValue();

        // flatMap 打平map
        // Strram.of
        return exist.entrySet().stream().flatMap(
                e-> Stream.of(e.getKey(), e.getValue())).toArray(String[]::new);

    }

    public String[] hmgt(String key, String[] hkeys) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
            return null;
        LinkedHashMap<String, String> exist = entry.getValue();
        if(hkeys == null) return null;
        return Arrays.stream(hkeys).map(exist::get)
                .toArray(String[]::new);

    }

    public Integer hlen(String key) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
            return 0;
        LinkedHashMap<String, String> exist = entry.getValue();
        return exist.size();
    }

    public Integer hexist(String key, String val) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
            return 0;
        LinkedHashMap<String, String> exist = entry.getValue();
        return exist.containsKey(val) ? 1:0;

    }

    public Integer hdel(String key, String[] hkeys) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>)map.get(key);
        if(entry == null)
            return 0;
        LinkedHashMap<String, String> exist = entry.getValue();
        if(hkeys == null) return 0;
        return (int)Arrays.stream(hkeys).map(exist::remove).filter(Objects::nonNull).count();

    }

    // ======================== 4 hash end ========================

    // ======================== 5 zset ========================
    public Integer zadd(String key, double[] scores, String[] value) {

        // todo add check
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>)map.get(key);
        if(entry == null)
        {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<ZsetEntry> exist = entry.getValue();

        for (int i = 0; i < value.length; i++) {

            exist.add(new ZsetEntry(value[i], scores[i]));
        }
        //todo return length
        return value.length;
    }

    public Integer zcard(String key) {

        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>)map.get(key);
        if(entry == null)
            return null;

        LinkedHashSet<ZsetEntry> exist = entry.getValue();
        return exist.size();
    }

    public Integer zcount(String key, double min, double max) {

        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>)map.get(key);
        if(entry == null)
            return null;

        LinkedHashSet<ZsetEntry> exist = entry.getValue();
        return (int)exist.stream().filter(x-> x.getScore()>= min && x.getScore()<= max).count();
    }

    public Double zscore(String key, String value) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>)map.get(key);
        if(entry == null)
            return null;

        LinkedHashSet<ZsetEntry> exist = entry.getValue();
        return exist.stream().filter(x-> x.getValue().equals(value))
                .map(ZsetEntry::getScore).findFirst().orElse(null);
    }

    public Integer zrank(String key, String value) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>)map.get(key);
        if(entry == null)
            return null;

        LinkedHashSet<ZsetEntry> exist = entry.getValue();
        Double zscore = zscore(key, value);
        if(zscore == null) return  null;
        return (int)exist.stream().filter(x->x.getScore() < zscore).count();
    }

    public Integer zrem(String key, String[] value) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>)map.get(key);
        if(entry == null)
            return null;

        LinkedHashSet<ZsetEntry> exist = entry.getValue();
        if(exist== null || exist.size()== 0) return null;
        return value == null ? 0: (int)Arrays.stream(value)
                .map(x->exist.removeIf(y->y.getValue().equals(x))).filter(x->x).count();

    }


    // ======================== 5 zset end ========================

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ZsetEntry{
        private String value;
        private double score;


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CacheEntry<T> {
        private T value;
    }
}
