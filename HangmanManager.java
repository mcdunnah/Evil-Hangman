/**
* The HangmanManager class keeps track of the current word, displayed pattern, 
* guesses left, and provides methods to record guesses and update the game state 
*
* @author Mia McDunnah
* CS 145 Evil HangMan Program
*/

import java.util.*;

public class HangmanManager {
   // Instance variables
   int length;
   int maxGuess;
   int guessesLeft;
   String guessesMade = "";
   String currentPattern = "";
   String displayedPattern = "";
   Set<String> wordSet = new TreeSet<>();
   Map<String, HashSet<String>> patternFamily = new HashMap<>();
   Map<Integer, List<String>> lengthToWords = new HashMap<>();
   

   /**
   * Constructs a HangmanManager with the specified parameters.
   * @param dictionary The list of words to choose from.
   * @param length     The length of the words to guess.
   * @param maxGuess   The maximum number of incorrect guesses allowed.
   */
   public HangmanManager(List<String> dictionary, int length, int maxGuess) {
      this.length = length;
      this.maxGuess = maxGuess;
      this.guessesLeft = this.maxGuess;
      
      // Initialize the displayed pattern with dashes
      for (int l = 0; l < length; l++) {
         this.displayedPattern += "-";
      }
      
      // Create a map of word lengths to words of that length
      for (String word : dictionary) {
         int wordLength = word.length();

         if (wordLength == length) {
            if (!this.lengthToWords.containsKey(wordLength)) {
               this.lengthToWords.put(wordLength, new ArrayList<>());
            }

            this.lengthToWords.get(wordLength).add(word);
         }
      }
      
      // Add words of the specified length to the word set
      if (!lengthToWords.isEmpty()) {
         for (String word : lengthToWords.get(this.length)) {
            String temp = word;
            this.wordSet.add(temp);
         }
      }
   }
   
   /**
   * Gets current set of words being considered by Hangman manager.
   * @return The set of words.
   */
   public Set<String> words() {
      this.wordSet.clear();
      for (String word : lengthToWords.get(this.length)) {
         String temp = word;
         this.wordSet.add(temp);
      }
      return this.wordSet;
   }



   /**
   * Gets the number of guesses left.
   * @return The number of guesses left.
   */
   public int guessesLeft() {
      return this.guessesLeft;
   }
   
   /**
   * Gets the current pattern in use by Hangman.
   * @return The current pattern.
   * @throws IllegalStateException If the set of words is empty.
   */
   public String pattern() throws IllegalStateException {
      if (this.displayedPattern.isEmpty()) {
         // Initialize the displayed pattern with dashes and spaces
         for (int i = 0; i < this.length; i++) {
            this.displayedPattern += "- ";
         }
      }
      // Add spaces between dashes
      String spacedPattern = "";
      for (char ch : this.displayedPattern.toCharArray()) {
         if (ch == '-') {
            spacedPattern += "- ";
         } else {
            spacedPattern += ch + " ";
         }
      }
      return spacedPattern; 
   }
   
   /**
   * Records a guess and updates the game state.
   * @param guess The guessed character.
   * @return The number of occurrences of the guessed character in the word.
   */
   public int record(char guess) {
      int charCount = 0;
      String letter = String.valueOf(guess);
      this.guessesMade += guess;

      if (this.guessesLeft < this.maxGuess) {
         this.patternFamily.clear();
      }
      
      // Loop through each word to update patternFamily
      for (String word : lengthToWords.get(this.length)) {
         String temp = word;
         // If the word contains guessed letter
         if (temp.contains(letter)) {
            this.currentPattern = findPattern(temp, guess);
            // Initialize patternFamily for current pattern
            if (!this.patternFamily.containsKey(this.currentPattern)) {
               this.patternFamily.put(this.currentPattern, new HashSet<>());
            }
            // Add the word to patternFamily under current pattern
            this.patternFamily.get(this.currentPattern).add(temp);
            } else {
               // If word doesn't contain guessed letter, add word to patternFamily
               if (!this.patternFamily.containsKey(this.displayedPattern)) {
                  this.patternFamily.put(this.displayedPattern, new HashSet<>());
               }
               this.patternFamily.get(this.displayedPattern).add(temp);
            }
      }
      // Compare patternFamilies to find suitable pattern
      comparePatternFamily();
      
      // Count occurrences of guessed character
      for (int c = 0; c < this.length; c++) {
         if (this.currentPattern.charAt(c) == guess) {
            charCount++;
         }
      }

      this.guessesLeft--;
      this.lengthToWords.clear();
      
      // Update lengthToWords with words that have current pattern
      for (String word : this.patternFamily.get(this.currentPattern)) {
         if (this.lengthToWords.isEmpty()) {
            this.lengthToWords.put(this.length, new ArrayList<>());
         }
         this.lengthToWords.get(this.length).add(word);
      }

      this.displayedPattern = this.currentPattern;

      return charCount;
   }

   /**
   * Compares pattern families to find most suitable pattern.
   * @return The most suitable pattern.
   */
   public String comparePatternFamily() {
      int count = 0;
      int highest = 0;
      // Temporary map to store pattern frequencies
      Map<String, Integer> comparisonMap = new HashMap<>();
      
      // Iterate through pattern families
      for (String pattern : this.patternFamily.keySet()) {
         count = 0;
         String tempPattern = pattern;
         
         // Count num of words in each pattern family
         for (String word : this.patternFamily.get(pattern)) {
            count++;
         }

         if (count > highest) {
            highest = count;
            this.currentPattern = tempPattern;
            
            // Update displayed pattern if matches most suitable pattern
            if (tempPattern.equals(this.displayedPattern)) {
               this.displayedPattern = this.currentPattern;
            }
         }
      }
      return this.currentPattern;
   }

   /**
   * Finds pattern for a given word and guessed letter.
   * @param str The word.
   * @param letter The guessed letter.
   * @return The pattern.
   */
   public String findPattern(String str, char letter) {
      char[] newPatternArray = this.displayedPattern.toCharArray();
      String initialPattern = "";
      String newPattern = "";
      
      // Iterate through the characters in the word
      for (int i = 0; i < str.length(); i++) {
         if (this.guessesLeft < this.maxGuess) {
            if (str.charAt(i) == letter) {
               // if guessed letter is found, update pattern
               newPatternArray[i] = letter;
               newPattern = String.valueOf(newPatternArray);
            }
         } else {
            if (str.charAt(i) == letter) {
               initialPattern += letter;
            } else {
               initialPattern += "-";
            }
         }
      }
      if (this.guessesLeft < this.maxGuess) { return newPattern; }
      else { return initialPattern; }
   }

   /**
   * Gets set of guessed characters.
   * @return The set of guessed characters.
   */
   public Set<Character> guesses() {
      Set<Character> guessedCharacters = new HashSet<>();
      for (int g = 0; g < this.guessesMade.length(); g++) {
         guessedCharacters.add(this.guessesMade.charAt(g));
      }
      return guessedCharacters;
   }
}