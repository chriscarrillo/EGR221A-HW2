import org.junit.*;
import java.util.*;
import java.io.*;

/**
 * Created by Chris on 1/30/17.
 * Tests the methods contained in HangmanManager.java
 */
public class HangmanManagerTest {
    // returns a set of the words contained in the "dictionary2.txt" file
    private Set<String> fetchWords() {
        try {
            Scanner console = new Scanner(new File("dictionary2.txt"));
            Set<String> words = new HashSet<>();
            while (console.hasNext()) {
                words.add(console.next());
            }
            return words;
        } catch (FileNotFoundException e) {
            Assert.fail("The file was not found.");
        }
        return new HashSet<>();
    }

    /**
     * Helper method used to create a sampleHangman with the given max attempts
     * @param max is the max number of tries
     * @return a new HangmanManager with w, length 4, and given max
     */
    private HangmanManager sampleHangman(int max) {
        Set<String> w = new HashSet<>();
        w.add("wwww");
        return new HangmanManager(w, 4, max);
    }

    /**
     * verifies if the HangmanManager was created properly
     * If constructor throws and exception, it will assert a failure
     */
    @Test
    public void constructorTest() {
        Set<String> w = fetchWords();
        try {
            new HangmanManager(w, 4, 10);
            new HangmanManager(w, 4, 9999);
            new HangmanManager(w, 4, 2);
            new HangmanManager(w, 2, 0);
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }

    // tests negative constructor runs
    @Test
    public void constructorNegativeTest() {
        try {
            Set<String> w = fetchWords();
            new HangmanManager(w, 0, 0); // length has to be > 1
            new HangmanManager(w, -7, 7); // length has to be >= 1
            new HangmanManager(w, 7, -7); // max has to be >= 0
            new HangmanManager(null, 7, 7); // NullPointerException should be thrown
            Assert.fail("A RuntimeException was NOT thrown!");
        } catch (RuntimeException e) {}
    }

    // Verifies that it was copied properly with words()
    @Test
    public void wordsTest() {
        Set<String> w = fetchWords();
        HangmanManager n = new HangmanManager(w, 4, 7);
        Assert.assertNotSame("Was not copied properly", n.words(), w);
        w.add("acbd");
        Assert.assertNotSame("Was not copied properly", n.words(), w);
    }

    // Verifies that the guessesLeft is decreasing properly
    @Test
    public void guessesLeftTest() {
        HangmanManager n = sampleHangman(7);
        Assert.assertEquals("The guesses left does not match the expected", n.guessesLeft(), 7);
        n.record('d');
        Assert.assertEquals("The guesses left does not match the expected", n.guessesLeft(), 6);
        n.record('e');
        Assert.assertEquals("The guesses left does not match the expected", n.guessesLeft(), 5);
        n.record('f');
    }

    // Verifies that guessed chars are, in fact, added to guessedChars
    @Test
    public void guessesTest() {
        HangmanManager n = sampleHangman(7);
        n.record('d');
        n.record('e');
        n.record('f');
        n.record('g');
        n.record('h');
        Set<Character> guessesSet = n.guesses();
        Assert.assertNotSame("Was not copied properly", guessesSet, n.guesses());
        if (guessesSet.size() != 5 || !guessesSet.contains('d') || !guessesSet.contains('e')
                || !guessesSet.contains('f') || !guessesSet.contains('g') || !guessesSet.contains('h'))
            Assert.fail("The guesses in the set are not the guesses that were guessed.");
    }

    // Verifies that the pattern matches the expected pattern
    @Test
    public void patternTest() {
        HangmanManager n = new HangmanManager(fetchWords(), 4, 7);
        Assert.assertEquals(n.pattern(), "- - - -");
        int count = n.record('e');
        Assert.assertEquals(n.pattern(), "- - - -");
        Assert.assertEquals(0, count);
        Assert.assertEquals("Guesses should decrease by 1", 6, n.guessesLeft());

        int count2 = n.record('o');
        Assert.assertEquals(n.pattern(), "- o o -");
        Assert.assertEquals(2, count2);
        Assert.assertEquals("Guesses should not decrease", 6, n.guessesLeft());
    }

    // Verifies that the pattern matches the expected pattern
    @Test
    public void patternNegativeTest() {
        HangmanManager n = new HangmanManager(new LinkedList<>(), 4, 7);
        try {
            n.pattern();
            Assert.fail("An IllegalStateException should have been thrown.");
        } catch (IllegalStateException e) {}
    }

    // Verifies that words match the expected when letters are guessed
    @Test
    public void recordTest() {
        HangmanManager n = new HangmanManager(fetchWords(), 4, 7);
        n.record('e');
        Set<String> set = new HashSet<>();
        set.add("ally");
        set.add("cool");
        set.add("good");
        Assert.assertEquals("Remaining words do not match the expected", n.words(), set);
        n.record('o');
        set.remove("ally");
        Assert.assertEquals("Remaining words do not match the expected", n.words(), set);
    }

    // Verifies that an exception is thrown when there are no more guesses or a letter is guessed again
    @Test
    public void recordNegativeTest() {
        HangmanManager n = sampleHangman(2);
        n.record('c'); // not in the word
        try { // c was already guessed, so an exception must be thrown
            n.record('c');
            Assert.fail("Letter was already guessed");
        } catch (IllegalArgumentException e) {}
        n.record('d');
        try { // no more guesses left, so an IllegalStatException is thrown
            n.record('d');
            Assert.fail();
        } catch (IllegalStateException e) {}
    }
}