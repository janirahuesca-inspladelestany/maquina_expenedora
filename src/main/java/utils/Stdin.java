package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stdin {
    public static String input(String message) {
        Scanner stdin = new Scanner(System.in);
        System.out.print(message);

        return stdin.nextLine().trim();
    }

    public static int inputInt(String message) {
        boolean exit = false;
        int number = 0;
        while (!exit) {
            try {
                number = Integer.parseInt(input(message));
                exit = true;
            } catch (Exception exception) {
                System.out.println("It needs to be an integer number");
            }
        }

        return number;
    }

    /**
     * Ask user using stdin a date with the following format YYYY-MM-DD
     * @param message
     * @return
     */
    public static LocalDate inputDate(String message) {
        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        Matcher matcher;
        String userInput = null;
        boolean valid = false;
        LocalDate date = LocalDate.now();

        while (!valid) {
            userInput = Stdin.input(message);
            matcher = pattern.matcher(userInput);

            if (!matcher.matches()) {
                System.out.println("Invalid date!");
                continue;
            }

            try {
                date = LocalDate.parse(userInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch(Exception exception) {
                System.out.println("Invalid date!");
                continue;
            }
            valid = true;
        }

        return date;
    }

    public static double inputDouble(String message) {
        boolean exit = false;
        double number = 0;
        while (!exit) {
            try {
                number = Double.parseDouble(input(message));
                exit = true;
            } catch (Exception exception) {
                System.out.println("It needs to be a number!");
            }
        }

        return number;
    }
}
