import org.junit.*;     // JUnit tools

import java.util.*;     // Collections
import java.io.*;       // File access

/**
 * Contains testing code for the public functions of the class HangmanManager
 * @Author Christopher Nugent, Mikyung Han for EGR221 Solutions
 * @Version Jan 2017 (Updated on 1/29/2017)
 */
public class HangmanManagerInstructorTest {

    /* Loads the words in dictionary2.txt, all of which have a length of 4 */
    private Set<String> getDictionary() {
        try {
            Scanner fileScanner = new Scanner(new File("dictionary2.txt"));
            Set<String> dictionary = new HashSet<>();
            while(fileScanner.hasNext()) {
                dictionary.add(fileScanner.next());
            }
            return dictionary;
        } catch(FileNotFoundException e) {
            Assert.fail("Something went wrong.");      //Something went wrong
        }
        /* Should never be reached. */
        return new HashSet<>();
    }

    /*
     * Helper for constructorTest()
     * Asserts failure if running the constructor with the given parameters throws an exception.
     */
    private void constructorTestHelper(Set<String> dictionary, int length, int max) {
        try {
            new HangmanManager(dictionary, length, max);
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }

    /*
     * Helper for constructorFailTest()
     * Asserts failure if running the constructor with the given parameters does not throw an exception.
     */
    private void constructorFailHelper(Set<String> d, int l, int m) {
        try {
            new HangmanManager(d, l, m);
            Assert.fail("An exception was not thrown.");
        } catch (RuntimeException e) {/* Intentionally blank */}
    }

    /* Creates HangmanManager with only "zzzz" in the dictionary */
    private HangmanManager getDummyManager(int max) {
        Set<String> d = new HashSet<>();
        d.add("zzzz");
        return new HangmanManager(d, 4, max);
    }

    /* Tests successful runs of the constructor */
    @Test
    public void constructorTest() {
        Set<String> d = getDictionary();
        constructorTestHelper(d, 4, 2);
        constructorTestHelper(d, 4, 1);
        constructorTestHelper(d, 4, 9999);
        constructorTestHelper(d, 1, 0);
    }

    /* Tests unsuccessful runs of the constructor */
    @Test
    public void constructorFailTest() {
        Set<String> d = getDictionary();
        constructorFailHelper(d, 0, 0);         // length must be 1 or greater
        constructorFailHelper(d, 4, -1);        // max guesses must be 0 or greater
        constructorFailHelper(d, -1, 3);        // length must be 1 or greater
        constructorFailHelper(null, 10, 10);    // nullPointerException should be thrown
    }


    /* Checks for deep copy of the game dictionary with the words() method */
    @Test
    public void wordsTest() {
        Set<String> d = getDictionary();
        HangmanManager h = new HangmanManager(d, 4, 10);
        Assert.assertNotSame("Deep copy was not made.", h.words(), d);
        d.add("asdf");
        Assert.assertNotSame("Deep copy was not made.", h.words(), d);
    }

    /* Checks that the number of guesses remaining decreases correctly */
    @Test
    public void guessesLeftTest() {
        HangmanManager h = getDummyManager(10);
        Assert.assertEquals("Guesses left did not match the expected value.", h.guessesLeft(), 10);
        h.record('a');
        Assert.assertEquals("Guesses left did not match the expected value.", h.guessesLeft(), 9);
        h.record('b');
        h.record('c');
        Assert.assertEquals("Guesses left did not match the expected value.", h.guessesLeft(), 7);
    }

    /* Checks that guessed characters are added to the list of guessed characters, and that a deep copy is made */
    @Test
    public void guessesTest() {
        HangmanManager h = getDummyManager(10);
        h.record('a');
        h.record('b');
        h.record('c');
        h.record('d');
        h.record('e');
        Set<Character> set = h.guesses();
        Assert.assertNotSame("A deep copy was not made.", set, h.guesses());
        if (set.size() != 5 || !set.contains('a') || !set.contains('b') || !set.contains('c')
                || !set.contains('d') || !set.contains('e')) {
            Assert.fail("Content of guesses is not the letters guessed.");
        }
    }

    /* Checks that the content of the current dictionary matches what is expected as letters are guessed */
    @Test
    public void recordTest() {
        HangmanManager h = new HangmanManager(getDictionary(), 4, 10);
        h.record('e');
        Set<String> s = new HashSet<>();
        s.add("ally");
        s.add("cool");
        s.add("good");
        Assert.assertEquals("Content of remaining words did not match expected value.", h.words(), s);
        h.record('o');
        s.remove("ally");
        Assert.assertEquals("Content of remaining words did not match expected value.", h.words(), s);
    }

    /* Checks that exceptions are thrown when a letter is reguessed and when no guesses remain */
    @Test
    public void recordFailTest() {
        HangmanManager h = getDummyManager(2);
        h.record('a');          // 1 guess left
        try {                   // a already guessed, throw exception
            h.record('a');
            Assert.fail();
        } catch (IllegalArgumentException e) {}
        h.record('b');          // 0 guesses left
        try {
            h.record('c');
            Assert.fail();
        } catch (IllegalStateException e) {}
    }

    /* Checks that the pattern matches what is expected */
    @Test
    public void patternTest() {
        HangmanManager h = new HangmanManager(getDictionary(), 4, 10);
        Assert.assertEquals(h.pattern(), "- - - -");
        int numOccur = h.record('e');
        Assert.assertEquals(h.pattern(), "- - - -");
        Assert.assertEquals(0, numOccur);
        Assert.assertEquals("With wrong guess, guessesLeft should decrease by 1", 9, h.guessesLeft());

        int numOccur2 = h.record('o');
        Assert.assertEquals(h.pattern(), "- o o -");
        Assert.assertEquals(2, numOccur2);
        Assert.assertEquals("With correct guess, guessesLeft shouldn't decrease" , 9, h.guessesLeft());
    }

    /* Checks that the pattern matches what is expected */
    @Test
    public void patternNegativeTest() {
        HangmanManager h = new HangmanManager(new LinkedList<>(), 4, 10);
        try{
            h.pattern();
            Assert.fail("Must throw IllegalStateException");
        }catch(IllegalStateException e){
        }
    }
}
