package org.spring;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Scanner;

public class SimpleClient {
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            new Bootstrap().group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                                 public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                                     Scanner in = new Scanner(System.in);
                                                     String st = in.nextLine();
                                                     ctx.writeAndFlush(st);
                                                 }
                                             }
                            );

                        }
                    })
                    .connect("localhost", 1234)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
