package org.spring;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.ArrayList;
import java.util.List;

public class SimpleServer {
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            List<ChannelHandlerContext> ctxs = new ArrayList<>();
            new ServerBootstrap().group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                    ctxs.add(ctx);
                                    ctxs.forEach(System.out::println);
                                }
                            });
                        }
                    })
                    .bind("localhost", 1234)
                    .sync()
                    .channel()
                    .closeFuture()
                    .syncUninterruptibly();


        } finally {
            group.shutdownGracefully();
        }
    }
}
