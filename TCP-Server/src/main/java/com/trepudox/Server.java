package com.trepudox;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final int PORT = 10000;
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss.SSSSSS";

    public static void main(String[] args) {
        try(AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
            System.out.printf("Servidor ouvindo a porta %d%n", PORT);

            while(server.isOpen()) {
                Future<AsynchronousSocketChannel> connection = server.accept();
                AsynchronousSocketChannel client = connection.get(10, TimeUnit.SECONDS);

                System.out.println("Cliente conectado: " + client.getRemoteAddress());
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
