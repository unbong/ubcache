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
public class RPopCommand implements Command {
    @Override
    public String name() {
        return "RPOP";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {

        String key = getKey(args);
        int count = 1;
        if(args.length > 6)
        {
            String value = getValue(args);
            count = Integer.parseInt(value);
            return Reply.array(cache.rpop(key, count));
        }

        String[] lpop = cache.rpop(key, count);
        return Reply.bulkString(lpop == null? null: lpop[0]);

    }
}
