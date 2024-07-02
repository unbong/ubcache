package io.unbong.ubcache.command;

import io.unbong.ubcache.core.Command;
import io.unbong.ubcache.core.Reply;
import io.unbong.ubcache.core.UBCache;

/**
 * HGET
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:15
 */
public class HGetCommand implements Command {
    @Override
    public String name() {
        return "HGET";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {

        String key = getKey(args);
        String hkey = getValue(args);

        return Reply.bulkString(cache.hget(key,hkey));
    }
}
