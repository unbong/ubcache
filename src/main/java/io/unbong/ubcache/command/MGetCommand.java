package io.unbong.ubcache.command;

import io.unbong.ubcache.core.Command;
import io.unbong.ubcache.core.Reply;
import io.unbong.ubcache.core.UBCache;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:15
 */
public class MGetCommand implements Command {
    @Override
    public String name() {
        return "MGET";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {

        String[] keys = getParams(args);
        String[] values = cache.mget(keys);
        return Reply.array(values);
    }
}
