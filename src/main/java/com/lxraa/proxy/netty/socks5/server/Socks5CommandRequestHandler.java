package com.lxraa.proxy.netty.socks5.server;

import com.lxraa.proxy.netty.socks5.client.Client2DestHandler;
import com.lxraa.proxy.netty.socks5.client.Dest2ClientHandler;
import com.lxraa.proxy.netty.socks5.msg.MsgHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;

public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {

    private EventLoopGroup bossGroup;
    Socks5CommandRequestHandler(EventLoopGroup bossGroup){
        this.bossGroup = bossGroup;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) throws Exception {
        System.out.println("enter Sock5CommandRequestHandler");
        if(!msg.type().equals(Socks5CommandType.CONNECT)){
            System.out.println("请求不是socks5 tcp连接");
            // 交给下一个Handler处理
            ctx.fireChannelRead(msg);
            return;
        }
        System.out.println("收到sock5 tcp连接请求");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //ch.pipeline().addLast(new LoggingHandler());//in out
                        //将目标服务器信息转发给客户端
                        ch.pipeline().addLast(new Dest2ClientHandler(ctx));
                    }
                });
        ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    ctx.pipeline().addLast(new Client2DestHandler(future));
                    Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
                    ctx.writeAndFlush(commandResponse);
                } else {
                    Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                    ctx.writeAndFlush(commandResponse);
                }
            }
        });
    }
}
