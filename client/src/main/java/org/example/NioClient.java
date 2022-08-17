package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author xiaomingcong
 * @date 2022/7/28 10:08
 *         Version 1.0
 */
public class NioClient {
    private static final String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

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

//        byteBuffer.flip();
//        socketChannel.write(byteBuffer);
//        TimeUnit.SECONDS.sleep(5);
//
//        //可以看到不处理byteBuffer直接再次发送，服务器是收不到数据的
//        socketChannel.write(byteBuffer);
//        TimeUnit.SECONDS.sleep(5);

        //发送一段长文件
        RandomAccessFile aFile = null;
        long startTime = new Date().getTime();
        long size = 0;
        try{
            aFile = new RandomAccessFile(classPath +"journal.txt","rw");
            System.out.println(classPath);
            FileChannel fileChannel = aFile.getChannel();
            //可以看到传输的数据量大的时候directByteBuffer效率更高
            ByteBuffer buf = ByteBuffer.allocateDirect(1024 * 8);
//            ByteBuffer buf = ByteBuffer.allocate(1024 * 8);
            int bytesRead = fileChannel.read(buf);
            System.out.println(bytesRead);
            while(bytesRead != -1)
            {
                size++;
                buf.flip();
                while(buf.hasRemaining())
                {
//                    System.out.println();
                    socketChannel.write(buf);
                }
                buf.compact();
                bytesRead = fileChannel.read(buf);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile != null){
                    aFile.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        long endTime = new Date().getTime();
        System.out.println();
        System.out.println("cost time:" + String.valueOf(endTime - startTime));
        System.out.println(size);

        //让程序卡在这个位置，不关闭连接
//        System.in.read();
//        socketChannel.socket().close();
        socketChannel.close();
    }
}
