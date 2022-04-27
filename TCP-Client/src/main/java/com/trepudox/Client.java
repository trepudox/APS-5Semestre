package com.trepudox;

import com.trepudox.logger.CustomLogger;
import jdk.jshell.spi.ExecutionControl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Client {
    // 1024 Bytes corresponde a uma mensagem de aproximadamente 1000 caracteres


    private static final CustomLogger LOGGER = CustomLogger.getLogger();

    // Caso usar uma máquina diferente para o servidor, consultar o IP do mesmo e alterar essa variável
    private static final String ADDRESS = "192.168.0.104";
    private static final int PORT = 10000;
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            String identificacao = "";
            String mensagem = "";

            LOGGER.info("Tentando se conectar ao servidor");
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            for(int i = 0; i < 12; i++) {
                LOGGER.info("PORT: %s", PORT + i);
                Future<Void> connection = client.connect(new InetSocketAddress(ADDRESS, PORT + i));
                try {
                    connection.get(1, TimeUnit.SECONDS);
                    break;
                } catch(TimeoutException | ExecutionException e) {
                    client = AsynchronousSocketChannel.open();
                }
            }

            if(client.getRemoteAddress() == null) {
                throw new RuntimeException("Não conectado");
            }
            LOGGER.info("Conectado com sucesso no servidor %s", client.getRemoteAddress());

            while(!identificacao.equals("0") && !mensagem.equals("0")) {
                System.out.print("Digite sua identificação: ");
                identificacao = SCANNER.nextLine();

                System.out.print("Digite sua mensagem: ");
                mensagem = SCANNER.nextLine();

                String message = String.format("'%s': %s", identificacao, mensagem);
                ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());
                client.write(writeBuffer);

                writeBuffer.clear();

                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                Future<Integer> readValue = client.read(readBuffer);

                if(readValue.get() == -1) {
                    System.out.println("Não foi possível ler a resposta do servidor.");
                    break;
                }

                System.out.println("Retorno: " + new String(readBuffer.array()).trim());

                readBuffer.clear();
                System.out.println();
            }
        } catch(ExecutionException e) {
            System.out.println("Tempo de espera excedido");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
