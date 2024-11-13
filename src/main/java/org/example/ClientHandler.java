package org.example;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter out;

    // Конструктор, який приймає підключення клієнта (сокет) та посилання на сервер
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    // Основний метод, який запускається в окремому потоці для кожного клієнта
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true); // Вивід для відправки повідомлень клієнту
            String message;

            // Цикл для читання повідомлень від клієнта
            while ((message = in.readLine()) != null) {
                System.out.println("Отримано від клієнта: " + message); // Виводимо повідомлення на сервері
                server.broadcastMessage(message, this); // Розсилаємо повідомлення іншим клієнтам
            }
        } catch (IOException e) {
            System.err.println("IOException у ClientHandler: " + e.getMessage());
        } finally {
            server.removeClient(this); // Видаляємо клієнта зі списку підключень сервера

            // Закриваємо сокет підключення після завершення роботи з клієнтом
            try {
                System.out.println("Закриття сокету у ClientHandler...");
                socket.close();
                System.out.println("Сокет закрито у ClientHandler.");
            } catch (IOException e) {
                System.err.println("Не вдалося закрити сокет: " + e.getMessage());
            }
        }
    }

    // Метод для відправки повідомлення клієнту
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message); // Надсилаємо повідомлення клієнту через `PrintWriter`
        }
    }
}
