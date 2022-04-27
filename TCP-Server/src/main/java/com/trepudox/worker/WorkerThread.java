package com.trepudox.worker;

import com.trepudox.logger.CustomLogger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Future;

public class WorkerThread extends Thread {

    private final int port;
    private final int threadNumber;

    private static final CustomLogger LOGGER = CustomLogger.getLogger();
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss.SSSSSS";

    public WorkerThread(int threadNumber) {
        super("WorkerThread-" + threadNumber);
        this.port = 10000 + threadNumber;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        try(AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(InetAddress.getLocalHost(), this.port))) {
            LOGGER.info("SERVIDOR %d RODANDO NA PORTA %d", this.threadNumber, this.port);

            while(server.isOpen()) {
                Future<AsynchronousSocketChannel> connection = server.accept();
                AsynchronousSocketChannel connectedClient = connection.get();
                String connectedClientAddress = connectedClient.getRemoteAddress().toString();

                connectedClient.write(ByteBuffer.wrap("OK".getBytes()));

                LOGGER.info("Cliente conectado no servidor %d: %s%n", this.threadNumber, connectedClientAddress);
                while(connectedClient.isOpen()) {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    Future<Integer> readValue = connectedClient.read(readBuffer);

                    if(readValue.get() == -1) {
                        String returnMsg = "A mensagem enviada não pôde ser lida!";
                        LOGGER.error(returnMsg);
                        connectedClient.write(ByteBuffer.wrap(returnMsg.getBytes()));
                        connectedClient.close();
                        break;
                    }

                    readBuffer.flip();
                    String receivedMessage = new String(readBuffer.array()).trim();
                    readBuffer.clear();

                    LOGGER.info("[Client: %s] - %s%n", connectedClientAddress, receivedMessage);

                    LocalDateTime receivedMessageDateTime = LocalDateTime.now();
                    String formattedDateTime = receivedMessageDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
                    String returnMsg = String.format("Mensagem recebida com sucesso na data e hora %s", formattedDateTime);
                    ByteBuffer writeBuffer = ByteBuffer.wrap(returnMsg.getBytes());

                    connectedClient.write(writeBuffer);
                    writeBuffer.clear();
                }

                System.out.println();
            }
        } catch(Exception e) {
            LOGGER.error("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
