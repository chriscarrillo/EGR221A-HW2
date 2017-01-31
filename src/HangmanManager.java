import java.util.*;

/**
 * Created by Chris on 1/25/17.
 */
public class HangmanManager {
    private Set<String> words; // this set contains the words available in the dictionary
    private SortedSet<Character> guessedChars; // list of the guessed characters
    private String pattern; // the pattern of the word
    private int guessesRemaining; // the number of remaining guesses

    /**
     * Constructs new HangmanManager. If length < 1 or max < 0, then an IllegalArgumentException is thrown.
     * Sets guessedChars and words to TreeSets of Character and String respectively. Generates the pattern of the word
     * and adds words of the length provided in the dictionary to words. The guessesRemaining is set to the max.
     * @param dictionary contains the words in the dictionary
     * @param length is the length of the word
     * @param max is the max number of guesses
     */
    public HangmanManager(Collection<String> dictionary, int length, int max) {
        if (length < 1 || max < 0) throw new IllegalArgumentException();
        guessedChars = new TreeSet<Character>();
        words = new TreeSet<String>();
        pattern = "-";
        for (int i = 1; i < length; i++) {
            pattern += " -";
        }
        for (String word : dictionary) {
            if (word.length() == length)
                words.add(word);
        }
        guessesRemaining = max;
    }

    // returns the words in the Set of String
    public Set<String> words() {
        return words;
    }

    // returns the guessesRemaining
    public int guessesLeft() {
        return guessesRemaining;
    }

    // returns a deep copy of guessedChars in a TreeSet of Character
    public SortedSet<Character> guesses() {
        return new TreeSet<Character>(guessedChars);
    }

    // If words is empty, then an IllegalStateException is thrown. Else, the pattern is returned
    public String pattern() {
        if (words.isEmpty()) throw new IllegalStateException();
        return pattern;
    }

    /**
     * Adds the guess to the guessedChars sortedSet of Character. Calls createWordMap() to create possible maps
     * Loops throw the pattern to see if the letter guessed is in the pattern. If so, increment num. If not,
     * subtract one from the guessesRemaining.
     * @throws IllegalStateException is guessesRemaining < 1 or words is empty
     * @throws IllegalArgumentException if words is not empty and guessedChars contains guess
     * @param guess is the character guessed
     * @return num of occurrences of the guessed letter
     */
    public int record(char guess) {
        if (guessesRemaining < 1 || words.isEmpty()) throw new IllegalStateException();
        if (!words.isEmpty() && guessedChars.contains(guess)) throw new IllegalArgumentException();

        guessedChars.add(guess);
        createWordMap();
        int num = 0;
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == guess)
                num++;
        }
        if (num == 0)
            guessesRemaining -= 1;
        return num;
    }

    // for each loop sorts "word" from the set of words into different lists of patterns
    private void createWordMap() {
        Map<String, Set<String>> map = new TreeMap<String, Set<String>>();
        for (String word : words) {
            String dashes = "";
            for (int i = 0; i < word.length(); i++) {
                if (guessedChars.contains(word.charAt(i)))
                    dashes += word.charAt(i) + " ";
                else
                    dashes += "- ";
            }
            dashes = dashes.substring(0, dashes.length() - 1);
            if (!map.containsKey(dashes))
                map.put(dashes, new TreeSet<String>());
            map.get(dashes).add(word);
        }
        chooseWordMap(map);
    }

    /**
     * Chooses from the list of patterns that has the most words
     * @param map is the map of possible words
     */
    private void chooseWordMap(Map<String, Set<String>> map) {
        String key = "";
        int max = 0;
        for (String set : map.keySet()) {
            if (max < map.get(set).size()) {
                max = map.get(set).size();
                key = set;
            }
        }
        pattern = key;
        words = map.get(key);
    }
}