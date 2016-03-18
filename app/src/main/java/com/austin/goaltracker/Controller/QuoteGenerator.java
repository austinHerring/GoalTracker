package com.austin.goaltracker.Controller;

import java.util.Random;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Generates random inspirational quote for the splash screen
 */
public class QuoteGenerator {
    final private static String[] quotes = {
            "Do or do not. There is no try. \n-Master Yoda",
            "Whether you think you can or you think you can’t, you’re right. \n—Henry Ford",
            "Remember no one can make you feel inferior without your consent. \n—Eleanor Roosevelt",
            "Never let your fears decide your fate. \n-AWOLNATION",
            "Strive not to be a success, but rather to be of value. \n—Albert Einstein",
            "I am not a product of my circumstances. I am a product of my decisions. \n—Stephen Covey",
            "The most difficult thing is the decision to act, the rest is merely tenacity. \n—Amelia Earhart",
            "When everything seems to be going against you, remember that the airplane takes off against the wind, not with it. \n—Henry Ford",
            "Perfection is not attainable, but if we chase perfection we can catch excellence. \n—Vince Lombardi"
    };
    final private static int numQuotes = quotes.length;

    public static String generateQuote() {
        Random rand= new Random();
        return quotes[rand.nextInt(numQuotes)];
    }
}
