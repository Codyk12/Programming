package hangman;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by codyk12 on 2/3/17.
 */

public class evilHangman implements IEvilHangmanGame {
    HashSet<String> dictionarySet = new HashSet<String>();
    HashSet<String> activeDictionary = new HashSet<String>();
    TreeSet<String> alreadyGuessed = new TreeSet<String>();
    HashMap<String, HashSet<String>> partitions = new HashMap<String, HashSet<String>>();
    String curWord = "";

    public String getCurWord()
    {
        return curWord;
    }


    @SuppressWarnings("serial")
    public static class GuessAlreadyMadeException extends Exception {
    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength)
    {
        try {
            Scanner input = new Scanner(new BufferedInputStream(new FileInputStream(dictionary)));

            while(input.hasNext())
            {
                dictionarySet.add(input.next());
            }

            for (String word: dictionarySet) {
                if(word.length() == wordLength)
                    activeDictionary.add(word);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     * @throws IEvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     * has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException
    {
        StringBuilder guessS = new StringBuilder();
        guessS.append(guess);
        if(alreadyGuessed.contains(guessS.toString()))
            throw new IEvilHangmanGame.GuessAlreadyMadeException();
        else
        {
            alreadyGuessed.add(guessS.toString());

            partition(guess);

            setLargestPartition(guessS);

            return activeDictionary;

        }

    }

    private void setLargestPartition(StringBuilder guess) {
        int max = 0;

        for (String key: partitions.keySet())
        {
            if(partitions.get(key).size() > max)
            {
                max = partitions.get(key).size();
                activeDictionary = partitions.get(key);
                curWord = key;
            }
            else if(partitions.get(key).size() == max)
            {
                compareHelper(curWord, key, guess);
            }
        }
    }


    //returns best partition key
    private String compareHelper(String thisWord, String thatWord, StringBuilder guess) {
        int thisWordInt = 0;
        int thatWordInt = 0;

        for (int i = 0; i < thisWord.length(); i++) {
            if(thisWord.charAt(i) == guess.charAt(0))
                thisWordInt++;
            if(thatWord.charAt(i) == guess.charAt(0))
                thatWordInt++;
        }
        if(thisWordInt == thatWordInt)
        {
            thisWordInt = 0;
            thatWordInt = 0;

            for (int i = thisWord.length()-1; i > 0; i--) {
                if(thisWord.charAt(i) == guess.charAt(0))
                    thisWordInt=i;
                if(thatWord.charAt(i) == guess.charAt(0))
                    thatWordInt=i;
                if(thisWordInt != thatWordInt)
                    break;
                thisWordInt = 0;
                thatWordInt = 0;
            }
            if(thisWordInt > thatWordInt)
                return thisWord;
            if(thatWordInt > thisWordInt)
                return thatWord;
        }
        else
        {
            if(thisWordInt > thatWordInt)
                return thisWord;
            if(thatWordInt > thisWordInt)
                return thatWord;
        }
        return thisWord;
    }

    private void partition(char guess) {
        StringBuilder formattedWord = new StringBuilder();
        for (String word: activeDictionary)
        {

            for (int i = 0; i < word.length(); i++) {
                if(word.charAt(i) == guess)
                    formattedWord.append(guess);
                if(curWord.length() > 0) {
                    if (curWord.charAt(i) == word.charAt(i)) {
                        formattedWord.append(word.charAt(i));
                    }
                    else
                        formattedWord.append("_");
                }
                else
                    formattedWord.append("_");

            }

            if(partitions.containsKey(formattedWord.toString()))
                partitions.get(formattedWord.toString()).add(word);
            else
            {
                HashSet<String> partition = new HashSet<String>();
                partition.add(word);
                partitions.put(formattedWord.toString(), partition);
            }
            formattedWord.setLength(0);
        }
    }
}
