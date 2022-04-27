package com.trepudox.worker;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Future;

public class WorkerThread extends Thread {

    private int threadNumber;
    private int port;

    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss.SSSSSS";

    public WorkerThread(int threadNumber) {
        super("WorkerThread-" + threadNumber);
        this.threadNumber = threadNumber;
        this.port = 10000 + threadNumber;
    }

    @Override
    public void run() {
        try(AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getLocalHost(), this.port));
            System.out.printf("SERVIDOR %d RODANDO NA PORTA %d%n", this.threadNumber, this.port);

            while(server.isOpen()) {
                Future<AsynchronousSocketChannel> connection = server.accept();
                AsynchronousSocketChannel client = connection.get();

                System.out.printf("Cliente conectado no servidor %d: %s%n", this.threadNumber, client.getRemoteAddress().toString());
                while(client.isOpen()) {

                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    Future<Integer> readValue = client.read(readBuffer);

                    if(readValue.get() == -1) {
                        String returnMsg = "A mensagem enviada não pôde ser lida!";
                        System.out.println(returnMsg);
                        client.write(ByteBuffer.wrap(readBuffer.array()));
                        client.close();
                        break;
                    }

                    readBuffer.flip();
                    String receivedMessage = new String(readBuffer.array()).trim();
                    readBuffer.clear();

                    LocalDateTime receivedMessageDateTime = LocalDateTime.now();
                    String formattedDateTime = receivedMessageDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
                    System.out.printf("[%s] - %s%n", formattedDateTime, receivedMessage);

                    String returnMsg = String.format("Mensagem recebida com sucesso na data e hora %s", formattedDateTime);
                    ByteBuffer writeBuffer = ByteBuffer.wrap(returnMsg.getBytes());
                    client.write(writeBuffer);
                    writeBuffer.clear();
                }

                System.out.println();
            }
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
