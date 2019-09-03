package main.client;


import main.server.Connection;
import main.server.ConsoleHelper;
import main.server.Message;
import main.server.MessageType;
import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected;

    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("участник " + userName + " присоединился к чату");
        }

        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("участник " + userName + " покинул чат");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this){
                Client.this.notify();
            }
        }

        public void run() {
            String address = getServerAddress();
            int port = getServerPort();
            Socket socket;
            try {
                socket = new Socket(address,port);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException e) {
                notifyConnectionStatusChanged(false);
            } catch (ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }


        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message msg = connection.receive();
                if (msg.getType() == MessageType.NAME_REQUEST) {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else if (msg.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    break;
                } else throw new IOException("Unexpected MessageType");
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message msg = connection.receive();
                if (msg.getType() == MessageType.TEXT) {
                    processIncomingMessage(msg.getData());
                } else if (msg.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(msg.getData());
                } else if (msg.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(msg.getData());
                } else throw new IOException("Unexpected MessageType");
            }

        }

    }

    protected String getServerAddress() {

        while (true){

            return ConsoleHelper.readString();
        }
    }

    protected int getServerPort() {
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        }catch (IOException ex){
            ConsoleHelper.writeMessage("Не удалось отправить сообщение");
            clientConnected = false;
        }
    }

    //@Override
    public void run() {
        Thread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Программа завершила свою работу аварийно " + e.getMessage());
            System.exit(0);
        }

        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
            String line = "";
            while (clientConnected && !(line = ConsoleHelper.readString()).equals("exit")) {
                if (shouldSendTextFromConsole())
                    sendTextMessage(line);
            }
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
