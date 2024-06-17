package io.unbong.ubcache.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.unbong.ubcache.UBplugin;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-15 19:53
 */
@Component
public class UBCacheServer implements UBplugin {


    EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("redis-boss"));
    EventLoopGroup workerGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("redis-boss"));

    Channel channel;

    int port = 6379;
    @Override
    public void init() {
    }

    @Override
    public void startup() {

        try{

            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {


                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            socketChannel.pipeline().addLast(new UBCacheDecoder());
                            socketChannel.pipeline().addLast(new UBCacheHandler());
                        }
                    });


            Channel ch = b.bind(port).sync().channel();
            System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void shutdown() {
        if(this.channel != null)
        {
            this.channel.close();
            this.channel = null;
        }

        if(this.bossGroup != null)
        {
            this.bossGroup.shutdownGracefully();
            this.bossGroup = null;
        }

        if(this.workerGroup != null)
        {
            this.workerGroup.shutdownGracefully();
            this.workerGroup = null;
        }
    }

}
