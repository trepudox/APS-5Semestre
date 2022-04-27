package com.trepudox;

import com.trepudox.logger.CustomLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Client {

    private static final CustomLogger LOGGER = CustomLogger.getLogger();
    private static final Scanner SCANNER = new Scanner(System.in);

    // Caso usar uma máquina diferente para o servidor, consultar o IP do mesmo e alterar essa variável
    private static final String ADDRESS = "192.168.0.104";
    private static final int PORT = 10000;
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static void connect(AsynchronousSocketChannel client) throws InterruptedException, IOException {
        for(int i = 0; i < AVAILABLE_PROCESSORS; i++) {
            LOGGER.info("Tentando se conectar no endereço /%s:%s", ADDRESS, PORT + i);
            Future<Void> connection = client.connect(new InetSocketAddress(ADDRESS, PORT + i));
            try {
                connection.get(1, TimeUnit.SECONDS);

                ByteBuffer connectionReadBuffer = ByteBuffer.allocate(4);
                Future<Integer> read = client.read(connectionReadBuffer);
                read.get();

                connectionReadBuffer.flip();
                String s = new String(connectionReadBuffer.array()).trim();
                connectionReadBuffer.clear();

                if (s.equals("OK")) {
                    LOGGER.info("Conectado com sucesso no servidor %s", client.getRemoteAddress());
                    break;
                }

            } catch (TimeoutException | ExecutionException e) {
                client = AsynchronousSocketChannel.open();
            }
        }

        if(client.getRemoteAddress() == null) {
            LOGGER.error("Todos servidores estão ocupados ou fora do ar, não foi possível se conectar.");
            client.close();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        LOGGER.info("De acordo com a potência da máquina local, o Client buscrá um total de %s servidores", AVAILABLE_PROCESSORS);

        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            connect(client);

            String identificacao;
            String mensagem;

            do {
                System.out.print("Digite sua identificação: ");
                identificacao = SCANNER.nextLine();

                System.out.print("Digite sua mensagem: ");
                mensagem = SCANNER.nextLine();

                String message = String.format("'%s': %s", identificacao, mensagem);
                client.write(ByteBuffer.wrap(message.getBytes()));

                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                Future<Integer> readValue = client.read(readBuffer);

                if(readValue.get() == -1) {
                    System.out.println("Não foi possível ler a resposta do servidor.");
                    break;
                }

                System.out.println("Retorno: " + new String(readBuffer.array()).trim());

                readBuffer.clear();
                System.out.println();
            } while(!identificacao.equals("0") && !mensagem.equals("0"));
        } catch(ExecutionException e) {
            System.out.println("Tempo de espera excedido");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
