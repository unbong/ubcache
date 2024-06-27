package io.unbong.ubcache.core;

import io.unbong.ubcache.command.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-19 21:16
 */
public class Commands {

    private static final Map<String, Command> ALL = new LinkedHashMap<>();

    static {
        initCommands();
    }

    private static void initCommands() {
        // for order
        register(new PingCommand());
        register(new InfoCommand());
        register(new CommandCommand());

        // string
        register(new SetCommand());
        register(new GetCommand());
        register(new StrLenCommand());
        register(new DelCommand());
        register(new ExistsCommand());
        register(new IncrCommand());
        register(new DecrCommand());
        register(new MGetCommand());
        register(new MSetCommand());
        register(new LPushCommand());
        register(new LPopCommand());
        register(new RPushCommand());
        register(new RPopCommand());
        register(new LLenCommand());
        register(new LIndexCommand());
        register(new LRangeCommand());

        // list
        // lpush rpush lpop rpop llen lindex lrange
        register(new LPushCommand());

    }

    public static void register(Command command){
        ALL.put(command.name(), command);
    }

    public static Command get(String name){
        return ALL.get(name);
    }

    public static String[] getCommandNames(){
        return ALL.keySet().toArray(new String[0]);
    }
}
