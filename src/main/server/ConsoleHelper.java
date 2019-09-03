package main.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {

        String str = null;
        while (str == null) {
            try {

                str = reader.readLine();

            } catch (IOException ex) {
                writeMessage("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
                readString();
            }
        }
        return str;
    }

    public static int readInt() {

        int result = 0;
        while (result == 0) {
            try {
                result = Integer.parseInt(readString());
            } catch (NumberFormatException ex) {
                writeMessage("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                result = Integer.parseInt(readString());
            }
        }
        return result;
    }
}