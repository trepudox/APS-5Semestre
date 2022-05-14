package com.trepudox.thread;

import com.trepudox.logger.CustomLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorkerThread extends Thread {

    private final int threadNumber;
    private final AsynchronousSocketChannel connectedClient;
    private final String connectedClientAddress;

    private static int classThreadNumber;
    private static final CustomLogger LOGGER = CustomLogger.getLogger();
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss.SSSSSS";

    public WorkerThread(AsynchronousSocketChannel connectedClient) throws IOException {
        super("WorkerThread-" + classThreadNumber);
        this.threadNumber = classThreadNumber;
        this.connectedClient = connectedClient;
        this.connectedClientAddress = connectedClient.getRemoteAddress().toString();
        WorkerThread.classThreadNumber++;
    }

    @Override
    public void run() {
        try {
            while(connectedClient.isOpen()) {
                connectedClient.write(ByteBuffer.wrap("OK".getBytes()));

                LOGGER.info("Cliente %s conectado no servidor - Endereço do cliente: %s%n", this.threadNumber, this.connectedClientAddress);

                while(connectedClient.isOpen()) {
                    String returnMsg = readMessage();
                    ByteBuffer writeBuffer = ByteBuffer.wrap(returnMsg.getBytes());

                    connectedClient.write(writeBuffer);
                    writeBuffer.clear();
                }
            }
        } catch(ExecutionException e) {
            LOGGER.error("O cliente %d foi desconectado do servidor", this.threadNumber);
        } catch(IOException e) {
            LOGGER.error("Houve um erro na saída/entrada de dados");
        } catch(Exception e) {
            e.printStackTrace();
            LOGGER.error("Foi encontrado um erro crítico que não pôde ser identificado: " + e.getMessage());
        }
    }

    private String readMessage() throws IOException, ExecutionException, InterruptedException {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        Future<Integer> readValue = connectedClient.read(readBuffer);

        if(readValue.get() == -1) {
            String returnMsg = "A mensagem enviada não pôde ser lida!";
            LOGGER.error(returnMsg);
            connectedClient.write(ByteBuffer.wrap(returnMsg.getBytes()));
            connectedClient.close();
            throw new IOException(returnMsg);
        }

        readBuffer.flip();
        String receivedMessage = new String(readBuffer.array()).trim();
        readBuffer.clear();

        LOGGER.message("[Client-%d: %s] - %s%n", this.threadNumber, this.connectedClientAddress, receivedMessage);

        LocalDateTime receivedMessageDateTime = LocalDateTime.now();
        String formattedDateTime = receivedMessageDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        return String.format("Mensagem recebida com sucesso na data e hora %s", formattedDateTime);
    }

}
