package io.unbong.ubcache.command;

import io.unbong.ubcache.core.Command;
import io.unbong.ubcache.core.Reply;
import io.unbong.ubcache.core.UBCache;

/**
 * Lpush command
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:15
 */
public class RPushCommand implements Command {
    @Override
    public String name() {
        return "RPUSH";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {

        String key = getKey(args);
        String[] value = getParamsNoKey(args);
        return Reply.integer(cache.rpush(key, value));
    }
}
