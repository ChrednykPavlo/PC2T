package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputHelper {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static int readInt(String prompt, int min, int max) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Value must be between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    public static String readName(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine().trim();
            if (!input.isEmpty() && input.matches("[a-zA-Z ]+")) {
                return input;
            }
            System.out.println("Please enter a valid name (letters and spaces only)");
        }
    }
}