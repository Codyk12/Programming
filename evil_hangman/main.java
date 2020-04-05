package hangman;

import java.io.File;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Created by codyk12 on 2/3/17.
 */

public class main {

    public static void main(String args[]) throws IEvilHangmanGame.GuessAlreadyMadeException {
        TreeSet<String> alreadyGuessed = new TreeSet<String>();
        String curWord = "";
        String guess = "";
        boolean guessed = false;

        File input = new File(args[0]);
        Scanner reader = new Scanner(System.in);

        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);
        if( wordLength > 1 && guesses > 0) {

            IEvilHangmanGame game = new evilHangman();
            game.startGame(input, wordLength);

            System.out.println("Welcome to Hangman!!!");

            while(guesses > 1) {

                System.out.println("You have " + guesses + " guesses left");
                System.out.println("Used Guesses: " + alreadyGuessed.toString());
                System.out.println("Your word: " + curWord);
                System.out.println("Your guess:");
                guess = reader.next();

                while (!guess.matches("[a-zA-Z]")) {
                    System.out.println("Sorry, invalid guess, please guess again:");
                    guess = reader.next();
                }
                while (alreadyGuessed.contains(guess)) {
                    System.out.println("Sorry, already guessed. Please try again:");
                    guess = reader.next();
                }

                alreadyGuessed.add(guess);
                game.makeGuess(guess.charAt(0));
                curWord = game.getCurWord();
                for (int i = 0; i < curWord.length(); i++) {
                    if (curWord.charAt(i) == guess.charAt(0))
                        guessed= true;
                }
                if (guessed)
                    System.out.println("NICE GUESS!!!");
                else
                {
                    System.out.println("Sorry not in the word. Try again:");
                    guesses--;
                }

            }
            System.out.println("You loose");
        }

    }
}
