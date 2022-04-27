package com.trepudox;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

public class Client {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {

            String identificacao = "";
            String mensagem = "";

            while(!identificacao.equals("0") && !mensagem.equals("0")) {
                // cria a conexao
                Socket cliente = new Socket(InetAddress.getLocalHost(), 10000);

                // objeto que cria a mensagem para outro host
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                saida.flush();

                System.out.print("Digite sua identificação: ");
                identificacao = scanner.nextLine();

                System.out.print("Digite sua mensagem: ");
                mensagem = scanner.nextLine();

                LocalDateTime l = LocalDateTime.now();
                System.out.println(l);
                saida.writeObject(String.format("%s '%s': %s", LocalDateTime.now(), identificacao, mensagem));

                // objeto que recebe a mensagem do outro host
                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                String retorno = entrada.readObject().toString();
                System.out.println("Retorno: " + retorno);

                cliente.close();
                System.out.println();
            }
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
