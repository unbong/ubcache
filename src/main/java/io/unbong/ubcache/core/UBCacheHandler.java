package io.unbong.ubcache.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static io.unbong.ubcache.core.Command.CRLF;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-15 20:11
 */
@Slf4j
public class UBCacheHandler extends SimpleChannelInboundHandler<String> {

    private static final String STR_PREFIX = "+";
    private static final String BULK_PREFIX = "$";

    private static final String INFO = "UBCache server[v1.0.0 created by unbong]." + CRLF ;

    public static final UBCache cache = new UBCache();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {

        String[] args = message.split(CRLF);
        log.debug("UBCacheHandler: {}", String.join(",", args));
        String cmd = args[2].toUpperCase();

        Command command = Commands.get(cmd);
        if(command != null){
            try
            {
                Reply<?> reply = command.exec(cache, args);
                log.debug("cmd {} -> {} -> {} ", cmd, reply.getType(), reply.getValue());
                replyContext(ctx, reply);
            }
            catch (Exception e)
            {
                Reply<?> reply = Reply.error("EXP exception with msg. " + e.getMessage());
                replyContext(ctx, reply);
            }

        }
        else{
            Reply<?> reply = Reply.error("ERR unsupportted command");
            replyContext(ctx, reply);
        }

    }

    private void replyContext(ChannelHandlerContext ctx, Reply<?> reply) {
        switch (reply.getType())
        {
            case INT:
                integer(ctx, (Integer)reply.getValue());
                break;
            case ERROR:
                error(ctx, (String)reply.getValue());
                break;
            case SIMPLE_STRING:
                simpleString(ctx, (String)reply.getValue());
                break;
            case BULK_STRING:
                bulkString(ctx, (String)reply.getValue());
                break;
            case ARRAY:
                array(ctx, (String[])reply.getValue());
                break;
            default:
                simpleString(ctx, (String)reply.getValue());
                break;
        }
    }

    private void error(ChannelHandlerContext ctx, String msg) {
        writeByteBuf(ctx, errorEncode(msg));
    }

    private String errorEncode (String msg) {
        return "-" + msg + CRLF;
    }
    private void integer(ChannelHandlerContext ctx, Integer i) {
        writeByteBuf(ctx, integerEncode(i));

    }

    private  String integerEncode(Integer i) {
        if (i == null) return "$-1" + CRLF;
        return ":" + i+ CRLF;
    }


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
