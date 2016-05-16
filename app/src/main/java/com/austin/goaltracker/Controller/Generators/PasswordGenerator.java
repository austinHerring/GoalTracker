package com.austin.goaltracker.Controller.Generators;

import java.util.Random;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Generates random passwords of length 10 with at least one int, uppercase and lowercase letter
 */
public class PasswordGenerator {
    final private static String[] chars = {"0","1","2","3","4","5,","6","7","8","9","a","b","c","d","e",
            "f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A",
            "B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W",
            "X","Y","Z"};
    final private static int charLength = chars.length;

    public static String generatePassword() {
        StringBuilder sb = new StringBuilder();
        Random rand= new Random();
        sb.append(chars[rand.nextInt(26) + 10]);    // Makes sure to include lowercase
        sb.append(chars[rand.nextInt(10)]);         // Makes sure to include int
        sb.append(chars[rand.nextInt(26) + 36]);    // Makes sure to include uppercase
        for(int i = 0; i < 7; i++) {
            sb.append(chars[rand.nextInt(charLength)]);
        }
        return sb.toString();
    }
}
