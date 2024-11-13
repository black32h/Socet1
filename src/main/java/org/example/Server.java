package org.example;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private int port;
    private Set<ClientHandler> clientHandlers = new HashSet<>();

    // Конструктор сервера, який приймає порт для прослуховування підключень
    public Server(int port) {
        this.port = port;
    }

    // Метод для запуску сервера
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущений на порту " + port);

            // Цикл для постійного прослуховування нових підключень
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Новий клієнт підключений: " + socket);

                // Створюємо обробник для клієнта і запускаємо його в новому потоці
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для відправки повідомлень всім клієнтам
    public void broadcastMessage(String message, ClientHandler excludeUser) {
        System.out.println("Розсилаємо повідомлення: " + message);
        for (ClientHandler clientHandler : clientHandlers) {
            // Повідомлення не відправляється користувачу, який його надіслав
            if (clientHandler != excludeUser) {
                clientHandler.sendMessage(message);
            }
        }
    }

    // Метод для видалення клієнта зі списку підключених клієнтів
    public void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Клієнт відключився.");
    }

    // Основний метод для запуску сервера
    public static void main(String[] args) {
        int port = 12345; // Встановлюємо порт для прослуховування
        Server server = new Server(port);
        server.start();
    }
}
