package com.trepudox;

import com.trepudox.worker.WorkerThread;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final int PORT = 10000;
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss.SSSSSS";

    public static void main(String[] args) {
        int availabeProcessors = Runtime.getRuntime().availableProcessors();
//        System.out.println(availabeProcessors);
        ExecutorService executorService = Executors.newFixedThreadPool(availabeProcessors);
        for(int i = 0; i < availabeProcessors; i++) {
            executorService.execute(new WorkerThread(i));
        }
    }

}
