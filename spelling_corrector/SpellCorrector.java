package spell;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Admin on 1/20/17.
 */

public class SpellCorrector implements ISpellCorrector {
    protected Trie dictionary;
    Set<String> firstEditWords = new HashSet<String>();
    Set<String> secondEditWords = new HashSet<String>();

   char[] alphabet = {'a','b','c',
                       'd','e','f',
                       'g','h','i',
                       'j','k','l',
                       'm','n','o',
                       'p','q','r',
                       's','t','u',
                       'v','w','x',
                       'y','z'};

    @SuppressWarnings("serial")
    public static class NoSimilarWordFoundException extends Exception {
    }

    /**
     * Tells this <code>ISpellCorrector</code> to use the given file as its dictionary
     * for generating suggestions.
     *
     * @param dictionaryFileName File containing the words to be used
     * @throws IOException If the file cannot be read
     */
    public void useDictionary(String dictionaryFileName) throws IOException {
        dictionary = new Trie();

        Scanner input = new Scanner(new BufferedInputStream(new FileInputStream(dictionaryFileName)));

        while(input.hasNext())
        {
            dictionary.add(input.next());
        }

    }

    /**
     * Suggest a word from the dictionary that most closely matches
     * <code>inputWord</code>
     *
     * @param inputWord
     * @return The suggestion
     * @throws spell.ISpellCorrector.NoSimilarWordFoundException If no similar word is in the dictionary
     */
    public String suggestSimilarWord(String inputWord) throws ISpellCorrector.NoSimilarWordFoundException {

        String suggestedWord = firstOrderEdit(inputWord.toLowerCase());
       if(suggestedWord.equals(""))
           return "Sorry No Word Found";
        else
            return suggestedWord;
    }

    private String firstOrderEdit(String inputWord)
    {
        alterChar(inputWord, 1);
        deleteChar(inputWord, 1);
        addChar(inputWord, 1);
        switchChar(inputWord, 1);

        String bestWord = find_in_dict(1);

        if(bestWord.equals(""))
        {
            return secondOrderEdit();
        }
        else{
            return bestWord;
        }
    }

    private String secondOrderEdit() {
        String bestWord = "";
        for (String word : firstEditWords) {
            alterChar(word, 2);
            deleteChar(word, 2);
            addChar(word, 2);
            switchChar(word, 2);
        }
        bestWord = find_in_dict(2);
        return bestWord;
    }

    private String find_in_dict(int firstOrSecond) {
        Trie.TrieNode node;
        String bestWord = "";
        int max = 0;
        if(firstOrSecond == 1) {
            for (String word : firstEditWords) {
                node = dictionary.find(word);
                if (node != null) {
                    if (node.getValue() > max)
                        bestWord = word;
                }
            }
        }
        if(firstOrSecond == 2) {
            for (String word : secondEditWords) {
                node = dictionary.find(word);
                if (node != null) {
                    if (node.getValue() > max)
                        bestWord = word;
                }
            }
        }
        return bestWord;
    }

    private void switchChar(String inputWord, int firstOrSecond) {
        StringBuilder sb_word = new StringBuilder();

        for (int i = 0; i < inputWord.length()-1; i++)
        {
            sb_word.setLength(0);
            if(i!=0)
             sb_word.append(inputWord.substring(0,i));
            sb_word.append(inputWord.charAt(i+1));
            sb_word.append(inputWord.charAt(i));

            if(i < inputWord.length()-2)
                sb_word.append(inputWord.substring(i+2));

            if(firstOrSecond == 1)
                firstEditWords.add(sb_word.toString());
            if(firstOrSecond == 2)
                secondEditWords.add(sb_word.toString());
        }
    }

    private void addChar(String inputWord, int firstOrSecond) {
        StringBuilder sb_word = new StringBuilder();

        for (int i = 0; i < inputWord.length()+1; i++)
        {
            for (int j = 0; j < alphabet.length; j++)
            {
                sb_word.setLength(0);
                sb_word.append(inputWord);
                sb_word.insert(i, alphabet[j]);
                if(firstOrSecond == 1)
                    firstEditWords.add(sb_word.toString());
                if(firstOrSecond == 2)
                    secondEditWords.add(sb_word.toString());
            }
        }

    }

    private void deleteChar(String inputWord, int firstOrSecond) {
        StringBuilder sb_word = new StringBuilder();
        sb_word.append(inputWord);

        for (int i = 0; i < inputWord.length(); i++) {
            sb_word.setLength(0);
            sb_word.append(inputWord);
            sb_word.deleteCharAt(i);
            if(firstOrSecond == 1)
                firstEditWords.add(sb_word.toString());
            if(firstOrSecond == 2)
                secondEditWords.add(sb_word.toString());
        }

    }


    private void alterChar(String inputWord, int firstOrSecond) {
        StringBuilder sb_word = new StringBuilder();
        sb_word.append(inputWord);

        for (int i = 0; i < inputWord.length(); i++)
        {
            sb_word.setLength(0);
            sb_word.append(inputWord);
            for (int j = 0; j < alphabet.length; j++)
            {
                sb_word.deleteCharAt(i);
                sb_word.insert(i, alphabet[j]);
                if(firstOrSecond == 1)
                    firstEditWords.add(sb_word.toString());
                if(firstOrSecond == 2)
                    secondEditWords.add(sb_word.toString());
            }
        }
    }

}