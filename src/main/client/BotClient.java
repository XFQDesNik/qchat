package main.client;

import main.server.ConsoleHelper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message){
            String[] nameText = message.split(": ");

            if (nameText.length != 2) {
                ConsoleHelper.writeMessage(message);
                return;
            }

            ConsoleHelper.writeMessage(nameText[1]);

            HashMap<String, SimpleDateFormat> textFormat = new HashMap<>();
            textFormat.put("дата", new SimpleDateFormat("d.MM.YYYY"));
            textFormat.put("день", new SimpleDateFormat("d"));
            textFormat.put("месяц", new SimpleDateFormat("MMMM"));
            textFormat.put("год", new SimpleDateFormat("YYYY"));
            textFormat.put("время", new SimpleDateFormat("H:mm:ss"));
            textFormat.put("час", new SimpleDateFormat("H"));
            textFormat.put("минуты", new SimpleDateFormat("m"));
            textFormat.put("секунды", new SimpleDateFormat("s"));
            if (textFormat.get(nameText[1]) != null) {
                String answer = String.format("%s %s: %s", "Информация для", nameText[0], textFormat.get(nameText[1]).format(Calendar.getInstance().getTime()));
                sendTextMessage(answer);
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int)(Math.random() * 100);
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
