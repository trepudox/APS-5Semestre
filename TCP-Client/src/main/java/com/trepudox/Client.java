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

    private static void connect(AsynchronousSocketChannel client) throws InterruptedException, IOException {
        while(true) {
            LOGGER.info("Tentando se conectar no endereço /%s:%s", ADDRESS, PORT);

            Future<Void> connection = client.connect(new InetSocketAddress(ADDRESS, PORT));
            try {
                connection.get(5, TimeUnit.SECONDS);

                ByteBuffer connectionReadBuffer = ByteBuffer.allocate(4);
                Future<Integer> read = client.read(connectionReadBuffer);
                read.get(2, TimeUnit.SECONDS);

                connectionReadBuffer.flip();
                String s = new String(connectionReadBuffer.array()).trim();
                connectionReadBuffer.clear();

                if (s.equals("OK")) {
                    LOGGER.info("Conectado com sucesso no servidor %s", client.getRemoteAddress());
                    break;
                }

            } catch (TimeoutException | ExecutionException e) {
                client = AsynchronousSocketChannel.open();
                e.printStackTrace();
                // TODO: Analisar casos de erro e depois remover printStackTrace
            }
        }

        if(client.getRemoteAddress() == null) {
            // TODO: Analisar necessidade
            LOGGER.info("Analisar necessidade");
            client.close();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            connect(client);
            pulaLinha();

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
                    client.close();
                    break;
                }

                pulaLinha();
                LOGGER.message("Retorno: %s", new String(readBuffer.array()).trim());

                readBuffer.clear();
                pulaLinha();

                System.out.print("Manter conexão ativa?\nDigite 0 para NÃO\nDigite 1 para SIM\nResposta: ");
                String exit = SCANNER.nextLine();
                pulaLinha();

                if(exit.equals("0"))
                    break;
            } while(true);
        } catch(IOException e) {
            System.out.println("Servidor fechou");
            e.printStackTrace();
        } catch(ExecutionException e) {
            System.out.println("Tempo de espera excedido");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
        // TODO: Verificar e estressar casos de exceção
    }

    private static void pulaLinha() {
        System.out.println();
    }

}
