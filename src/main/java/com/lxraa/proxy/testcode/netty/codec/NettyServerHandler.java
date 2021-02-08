package com.lxraa.proxy.testcode.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyServerHandler extends SimpleChannelInboundHandler<MyMessagePOJO.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessagePOJO.MyMessage msg) throws Exception {
        if(msg.getDataType().equals(MyMessagePOJO.MyMessage.DataType.StudentType)){
            MyMessagePOJO.Student student = msg.getStudent();
            System.out.println("recv student" + student.getId() + student.getName());
        }
        if(msg.getDataType().equals(MyMessagePOJO.MyMessage.DataType.WorkerType)){
            MyMessagePOJO.Worker worker = msg.getWorker();
            System.out.println("recv worker" + worker.getName() + worker.getTag());
        }

        ctx.fireChannelRead(msg);
    }
    //    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        StudentPOJO.Student student = (StudentPOJO.Student) msg;
//        System.out.println("客户端发送 id=" +student.getId());
//
//        //        ctx.channel().eventLoop().execute(new Runnable() {
////            @Override
////            public void run(){
////                try {
////                    Thread.sleep(10000);
////                }catch(InterruptedException e){
////                    e.printStackTrace();
////                }
////                ctx.writeAndFlush(Unpooled.copiedBuffer("hello client",CharsetUtil.UTF_8));
////            }
////        });
////        ByteBuf buf = (ByteBuf) msg;
////        System.out.println("客户端发送"+buf.toString(CharsetUtil.UTF_8));
////        System.out.println("客户端地址"+ctx.channel().remoteAddress());
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello ,client",CharsetUtil.UTF_8));
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
//    }
}
