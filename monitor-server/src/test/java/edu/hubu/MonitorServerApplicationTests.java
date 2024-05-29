package edu.hubu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@SpringBootTest
class MonitorServerApplicationTests {

    @Test
    void contextLoads() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(10);
        //使用方式基本和之前是一样的
        buffer.put((byte) 66);
        buffer.flip();
        System.out.println(buffer.get());
    }

}
