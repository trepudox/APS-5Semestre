package com.trepudox;

import com.trepudox.logger.CustomLogger;
import com.trepudox.thread.WorkerThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server {

    private static final CustomLogger LOGGER = CustomLogger.getLogger();

    public static void main(String[] args) {
        try(AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(InetAddress.getLocalHost(), 10000));) {
            LOGGER.info("Servidor disponível no endereço: " + server.getLocalAddress().toString());

            while(server.isOpen()) {
                Future<AsynchronousSocketChannel> connection = server.accept();
                AsynchronousSocketChannel connectedClient = connection.get();
                new WorkerThread(connectedClient).start();
            }
        } catch(IOException e) {
            LOGGER.error("Houve um erro inesperado na criação do servidor");
        } catch (ExecutionException e) {
            LOGGER.error("Houve um erro inesperado na hora da conexão com um dos clientes");
        } catch (InterruptedException e) {
            LOGGER.error("Houve um erro inesperado na execução do servidor");
        }

        System.out.println("Aplicação encerrada");

    }

}
