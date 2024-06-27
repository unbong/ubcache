package io.unbong.ubcache.command;

import io.unbong.ubcache.core.Command;
import io.unbong.ubcache.core.Reply;
import io.unbong.ubcache.core.UBCache;

/**
 * delete command
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:15
 */
public class DelCommand implements Command {
    @Override
    public String name() {
        return "SET";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {

        String[] key = getParams(args);
        return Reply.integer(cache.del(key));
    }




}
