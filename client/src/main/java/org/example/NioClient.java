package org.example;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author xiaomingcong
 * @date 2022/7/28 10:08
 *         Version 1.0
 */
public class NioClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
        socketChannel.configureBlocking(false);
        //连接服务器
        boolean connect = socketChannel.connect(address);
        //判断是否连接成功
        if(!connect){
            //等待连接的过程中
            while (!socketChannel.finishConnect()){
                System.out.println("连接服务器需要时间，期间可以做其他事情...");
            }
        }
        String msg = "hello java技术爱好者！";
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        //把byteBuffer数据写入到通道中
        socketChannel.write(byteBuffer);

        TimeUnit.SECONDS.sleep(5);
        byteBuffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(byteBuffer);
        TimeUnit.SECONDS.sleep(5);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        TimeUnit.SECONDS.sleep(5);
        //可以看到不处理byteBuffer直接再次发送，服务器是收不到数据的
        socketChannel.write(byteBuffer);
        TimeUnit.SECONDS.sleep(5);
        socketChannel.write(byteBuffer);

        //让程序卡在这个位置，不关闭连接
//        System.in.read();
//        socketChannel.socket().close();
        socketChannel.close();
    }
}
