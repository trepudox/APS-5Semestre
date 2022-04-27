package com.trepudox;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.Future;

public class Client {
    // 1024 Bytes corresponde a uma mensagem de aproximadamente 1000 caracteres

    // Caso usar uma máquina diferente para o servidor, consultar o IP do mesmo e alterar essa variável
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 10000;
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        try(AsynchronousSocketChannel client = AsynchronousSocketChannel.open()) {
            String identificacao = "";
            String mensagem = "";

            client.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));

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
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
