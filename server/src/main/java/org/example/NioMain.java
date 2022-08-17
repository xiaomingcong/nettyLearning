package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * @Description: TODO
 * @Author xiaomingcong
 * @date 2022/8/17 12:21
 *         Version 1.0
 */
public class NioMain {

    public static final String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();


    /*
    为什么在执行网络IO或者文件IO时，一定要通过堆外内存呢？

    @ETIN 的答案也说了，如果是使用DirectBuffer就会少一次内存拷贝。如果是非DirectBuffer，JDK会先创建一个DirectBuffer，再去执行真正的写操作。
    这是因为，当我们把一个地址通过JNI传递给底层的C库的时候，有一个基本的要求，就是这个地址上的内容不能失效。然而，在GC管理下的对象是会在Java堆中移动的。
    也就是说，有可能我把一个地址传给底层的write，但是这段内存却因为GC整理内存而失效了。所以我必须要把待发送的数据放到一个GC管不着的地方。
    这就是调用native方法之前，数据一定要在堆外内存的原因。
    可见，DirectBuffer并没有节省什么内存拷贝，只是因为HeapBuffer必须多做一次拷贝，才显得DirectBuffer更快一点而已。
     */
    public static void main(String[] args) {
//        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
//        System.out.println(buf.isDirect());
//        Unsafe unsafe = Unsafe.getUnsafe();
//        unsafe.allocateMemory(1024);
        method1();


    }

    public static void method1(){
        long startTime = new Date().getTime();
        System.out.println(startTime);
//        System.out.println(classPath);
        RandomAccessFile aFile = null;
        try{
            aFile = new RandomAccessFile(classPath +"journal.txt","rw");
            FileChannel fileChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocateDirect(1024);
            int bytesRead = fileChannel.read(buf);
            System.out.println(bytesRead);
            while(bytesRead != -1)
            {
                buf.flip();
                while(buf.hasRemaining())
                {
                    System.out.print((char)buf.get());
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
    }

}
