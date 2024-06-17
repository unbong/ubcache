package io.unbong.ubcache.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-15 20:11
 */
@Slf4j
public class UBCacheHandler extends SimpleChannelInboundHandler<String> {

    private static final String CRLF = "\r\n";
    private static final String STR_PREFIX = "+";
    private static final String BULK_PREFIX = "$";
    private static final String OK = "OK";

    private static final String INFO = "UBCache server[v1.0.0 created by unbong]." + CRLF ;

    public static final UBCache CACHE = new UBCache();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {


        String[] args = message.split(CRLF);
        log.debug("UBCacheHandler: {}", String.join(",", args));
        String cmd = args[2].toUpperCase();

        if("COMMAND".equals(cmd))
        {
            writeByteBuf(ctx, "*2" +
                    CRLF+"$7" +
                    CRLF + "COMMAND" +
                    CRLF+ "$4"+
                    CRLF+ "PING"  + CRLF);
        }
        else if("PING".equals(cmd))
        {
            String ret = "PONG";
            if(args.length >=5)
                ret = args[4];
            simpleString(ctx,ret);
        }
        else if ("INFO".equals(cmd))
        {
            bulkString(ctx, INFO);
        }
        else if("SET".equals(cmd))
        {
            CACHE.set(args[4], args[6]);
            simpleString(ctx,OK);
        } else if ("GET".equals(cmd)) {
            String value = CACHE.get(args[4]);
            bulkString(ctx, value);
        } else if ("STRLEN".equals(cmd)) {
            String value = CACHE.get(args[4]);
            integer(ctx, value == null?0:value.length());
        } else if ("DEL".equals(cmd)) {
            int len = (args.length-3)/2;
            String[] keys = new String[len];
            for(int i = 0; i < len; i++)
            {
                keys[i] = args[4+i*2];
            }
            int del = CACHE.del(keys);
            integer(ctx, del);
        } else if ("EXISTS".equals(cmd)) {
            int len = (args.length-3)/2;
            String[] keys = new String[len];
            for(int i = 0; i < len; i++)
            {
                keys[i] = args[4+i*2];
            }
            int exists = CACHE.exists(keys);
            integer(ctx, exists);
        } else if ("MGET".equals(cmd)) {
            int len = (args.length-3)/2;
            String[] keys = new String[len];
            for(int i = 0; i < len; i++)
            {
                keys[i] = args[4+i*2];
            }

            String[] array =CACHE.mget(keys);
            array(ctx, array);

        }else if ("MSET".equals(cmd)) {
            int len = (args.length-3)/4;
            String[] keys = new String[len];
            String[] values = new String[len];

            for(int i = 0; i < len; i++)
            {
                keys[i] = args[4+i*4];
                values[i] = args[6+i*4];
            }
            CACHE.mset(keys, values);
            simpleString(ctx, OK);
        } else if ("INCR".equals(cmd)) {
            String key = args[4];
            try{
                integer(ctx, CACHE.incr(key));
            }
            catch (NumberFormatException nfe)
            {
                error(ctx, "NFE " + key + " value is not integer" );
            }
        } else if ("DECR".equals(cmd)) {
            String key = args[4];
            try{
                integer(ctx, CACHE.decr(key));
            }
            catch (NumberFormatException nfe)
            {
                error(ctx, "NFE " + key + " value is not integer" );
            }
        }  else{
            simpleString(ctx, OK);
        }
    }
    private void error(ChannelHandlerContext ctx, String msg) {
        writeByteBuf(ctx, errorEncode(msg));
    }

    private String errorEncode (String msg) {
        return "-" + msg + CRLF;
    }
    private void integer(ChannelHandlerContext ctx, int i) {
        writeByteBuf(ctx, integerEncode(i));

    }

    private  String integerEncode(int i) {
        return ":" + i+ CRLF;
    }


//    *2
//    $3
//            set
//    $1
//            s



//    *2
//    $3
//            get
//    $1
//            s
    private void bulkString(ChannelHandlerContext ctx, String content)
    {
        String ret = bulkStringEncode(content );
        writeByteBuf(ctx,  ret  );
    }

    private  String bulkStringEncode(String content) {
        String ret;
        if(content == null){
            // null return -1
            ret = "$-1" ;
        } else if (content.isEmpty()) {
            ret = "$0";
        }
        else{
            ret = BULK_PREFIX + content.length() + CRLF + content;
        }
        return ret + CRLF;
    }


    private  String simpleStringEncode(String content) {
        String ret;
        if(content == null){
            // null return -1
            ret = "$-1" ;
        } else if (content.isEmpty()) {
            ret = "$0";
        }
        else{
            ret =  STR_PREFIX + content;
        }
        return ret + CRLF;
    }

    private void simpleString(ChannelHandlerContext ctx, String content)
    {
        String ret = simpleStringEncode(content);
        writeByteBuf(ctx, ret);
    }

    private void array(ChannelHandlerContext ctx, String[] array)
    {

        writeByteBuf(ctx, arrayEncode(array));
    }


    private  String arrayEncode(Object[] objects) {
        StringBuilder sb = new StringBuilder();

        if(objects == null)
            sb.append("*-1"+CRLF);
        else if (objects.length == 0) {
            sb.append("*0"+CRLF);
        }else {
            sb.append("*" + objects.length+CRLF);
            for (int i = 0; i < objects.length; i++) {
                // todo array[i] == null
                Object obj = objects[i];
                if(obj == null)
                {
                    sb.append("$-1" + CRLF);
                }
                else{
                    if(obj instanceof Integer){
                        sb.append(integerEncode((Integer) obj));
                    } else if (obj instanceof  String) {
                        sb.append(bulkStringEncode((String) obj));
                    } else if (obj instanceof  Object[] objs) {
                        sb.append(arrayEncode(objs));
                    }
                }
//                sb.append(BULK_PREFIX+ .length() + CRLF+objects[i] +CRLF);
            }
        }
        return sb.toString();
    }

    private void writeByteBuf(ChannelHandlerContext ctx, String content){
        log.debug("wrap byte buffer and reply: {}",content);
        ByteBuf buffer = Unpooled.buffer(128);
        buffer.writeBytes(content.getBytes());
        ctx.writeAndFlush(buffer);
    }

}
