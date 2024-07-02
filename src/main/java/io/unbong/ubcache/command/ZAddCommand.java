package io.unbong.ubcache.command;

import io.unbong.ubcache.core.Command;
import io.unbong.ubcache.core.Reply;
import io.unbong.ubcache.core.UBCache;

import java.util.Arrays;

/**
 * ZADD command
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:15
 */
public class ZAddCommand implements Command {
    @Override
    public String name() {
        return "ZADD";
    }

    @Override
    public Reply<?> exec(UBCache cache, String[] args) {

        String key = getKey(args);
        String[] scores = getHKeys(args);
        String[] value = getHValues(args);
        return Reply.integer(cache.zadd(key, toDouble(scores), value));

    }

    private double[] toDouble (String[] scores)
    {
        return Arrays.stream(scores).mapToDouble(Double::parseDouble).toArray();
    }
}
