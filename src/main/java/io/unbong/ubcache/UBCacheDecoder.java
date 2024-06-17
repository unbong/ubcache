package io.unbong.ubcache;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-15 20:21
 */
@Slf4j
public class UBCacheDecoder extends ByteToMessageDecoder {
    AtomicLong counter = new AtomicLong();



    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf in, List<Object> out) throws Exception {

        log.debug("decoder count {}", counter.incrementAndGet());
        if(in.readableBytes() <=0)
        {
            return;
        }

        int count = in.readableBytes();
        int index = in.readerIndex();

        log.debug("count: {}, index {}", count, index);

        byte[] bytes = new byte[count];
        in.readBytes(bytes);
        String ret = new String(bytes);
        log.debug("ret: {}", ret);
        out.add(ret);
    }
}


