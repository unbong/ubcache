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
public class PingCommand implements Command {
    @Override
    public String name() {
        return "PING";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {
        String  ret = "PONG";
        if(args.length >=5)
            ret = args[4];
        return Reply.string(ret);
    }
}
