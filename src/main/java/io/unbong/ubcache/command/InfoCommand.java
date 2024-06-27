package io.unbong.ubcache.command;

import io.unbong.ubcache.core.Command;
import io.unbong.ubcache.core.Reply;
import io.unbong.ubcache.core.UBCache;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:27
 */
public class InfoCommand implements Command {

    private static final String INFO = "UBCache server[v1.0.0 created by unbong]." + CRLF ;

    @Override
    public String name() {
        return "INFO";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {
        return Reply.bulkString(INFO);
    }
}
