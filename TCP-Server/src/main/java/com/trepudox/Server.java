package com.trepudox;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {

    private static final int PORT = 10000;

    public static void main(String[] args) {
        try(ServerSocket servidor = new ServerSocket(PORT)) {
            System.out.printf("Servidor ouvindo a porta %d%n", PORT);

            while(!servidor.isClosed()) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

                System.out.println("Mensagem: " + entrada.readObject());
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                saida.flush();
                saida.writeObject("OK");

                saida.close();
                cliente.close();
                System.out.println();
            }
        } catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
