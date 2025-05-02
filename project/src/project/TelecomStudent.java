package project;

import java.util.HashMap;
import java.util.Map;

public class TelecomStudent extends Student {
    public TelecomStudent(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public void performSkill() {
        System.out.println("Morse code: " + toMorse(firstName + " " + lastName));
    }

    private String toMorse(String text) {
        Map<Character, String> morseCode = new HashMap<>();
        morseCode.put('A', ".-"); morseCode.put('B', "-..."); morseCode.put('C', "-.-.");
        morseCode.put('D', "-.."); morseCode.put('E', "."); morseCode.put('F', "..-.");
        morseCode.put('G', "--."); morseCode.put('H', "...."); morseCode.put('I', "..");
        morseCode.put('J', ".---"); morseCode.put('K', "-.-"); morseCode.put('L', ".-..");
        morseCode.put('M', "--"); morseCode.put('N', "-."); morseCode.put('O', "---");
        morseCode.put('P', ".--."); morseCode.put('Q', "--.-"); morseCode.put('R', ".-.");
        morseCode.put('S', "..."); morseCode.put('T', "-"); morseCode.put('U', "..-");
        morseCode.put('V', "...-"); morseCode.put('W', ".--"); morseCode.put('X', "-..-");
        morseCode.put('Y', "-.--"); morseCode.put('Z', "--..");
        morseCode.put(' ', "/");

        StringBuilder morse = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            morse.append(morseCode.getOrDefault(c, "")).append(" ");
        }
        return morse.toString();
    }
}