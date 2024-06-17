 import java.io.*;
 import java.util.*;

 /**
  * Stemmer, implementing the Porter Stemming Algorithm
  *
  * The Stemmer class transforms a word into its root form. The input word can be
  * provided a character at a time (by calling add()), or at once by calling one
  * of the various stem(something) methods.
  */
 class Stemmer {
     private List<Character> wordBuffer;
     private int endIndex, j, k;

     public Stemmer() {
         wordBuffer = new ArrayList<>();
     }

     /**
      * Adds a character to the word being stemmed.
      * @param ch the character to add
      */
     public void add(char ch) {
         wordBuffer.add(ch);
     }

     /**
      * Adds a portion of a character array to the word being stemmed.
      * @param word the character array
      * @param wordLen the length of the portion to add
      */
     public void add(char[] word, int wordLen) {
         for (int i = 0; i < wordLen; i++) {
             wordBuffer.add(word[i]);
         }
     }

     /**
      * Returns the stemmed word as a string.
      * @return the stemmed word
      */
     public String toString() {
         StringBuilder result = new StringBuilder();
         for (int i = 0; i < endIndex; i++) {
             result.append(wordBuffer.get(i));
         }
         return result.toString();
     }

     /**
      * Returns the length of the stemmed word.
      * @return the length of the stemmed word
      */
     public int getResultLength() {
         return endIndex;
     }

     /**
      * Returns a reference to the character buffer containing the stemmed word.
      * @return the character buffer
      */
     public char[] getResultBuffer() {
         char[] buffer = new char[endIndex];
         for (int i = 0; i < endIndex; i++) {
             buffer[i] = wordBuffer.get(i);
         }
         return buffer;
     }

     /**
      * Checks if the character at the given index is a consonant.
      * @param index the index to check
      * @return true if the character is a consonant, false otherwise
      */
     private final boolean isConsonant(int index) {
         switch (wordBuffer.get(index)) {
             case 'a': case 'e': case 'i': case 'o': case 'u': return false;
             case 'y': return (index == 0) ? true : !isConsonant(index - 1);
             default: return true;
         }
     }

     /**
      * Measures the number of consonant sequences between 0 and j.
      * @return the number of consonant sequences
      */
     private final int measureConsonantSequences() {
         int n = 0;
         int i = 0;
         while (true) {
             if (i > j) return n;
             if (!isConsonant(i)) break;
             i++;
         }
         i++;
         while (true) {
             while (true) {
                 if (i > j) return n;
                 if (isConsonant(i)) break;
                 i++;
             }
             i++;
             n++;
             while (true) {
                 if (i > j) return n;
                 if (!isConsonant(i)) break;
                 i++;
             }
             i++;
         }
     }

     /**
      * Checks if 0,...,j contains a vowel.
      * @return true if there is a vowel, false otherwise
      */
     private final boolean containsVowel() {
         for (int i = 0; i <= j; i++) if (!isConsonant(i)) return true;
         return false;
     }

     /**
      * Checks if j,(j-1) contain a double consonant.
      * @return true if there is a double consonant, false otherwise
      */
     private final boolean hasDoubleConsonant(int j) {
         if (j < 1) return false;
         if (wordBuffer.get(j) != wordBuffer.get(j - 1)) return false;
         return isConsonant(j);
     }

     /**
      * Checks if i-2,i-1,i has the form consonant-vowel-consonant.
      * @return true if the pattern is found, false otherwise
      */
     private final boolean cvc(int i) {
         if (i < 2 || !isConsonant(i) || isConsonant(i - 1) || !isConsonant(i - 2)) return false;
         char ch = wordBuffer.get(i);
         if (ch == 'w' || ch == 'x' || ch == 'y') return false;
         return true;
     }

     /**
      * Checks if the word ends with the given string.
      * @param s the string to check
      * @return true if the word ends with the string, false otherwise
      */
     private final boolean endsWith(String s) {
         int length = s.length();
         int offset = k - length + 1;
         if (offset < 0) return false;
         for (int i = 0; i < length; i++) if (wordBuffer.get(offset + i) != s.charAt(i)) return false;
         j = k - length;
         return true;
     }

     /**
      * Sets (j+1),...k to the characters in the given string, readjusting k.
      * @param s the string to set
      */
     private final void setTo(String s) {
         int length = s.length();
         int offset = j + 1;
         for (int i = 0; i < length; i++) wordBuffer.set(offset + i, s.charAt(i));
         k = j + length;
     }

     /**
      * Replaces the ending with the given string if measureConsonantSequences() > 0.
      * @param s the string to replace with
      */
     private final void repIfMeasureGreaterZero(String s) {
         if (measureConsonantSequences() > 0) setTo(s);
     }

     /**
      * Step 1 of the Porter Stemming Algorithm.
      * Removes plurals and -ed or -ing endings.
      */
     private final void removePluralsAndEdOrIng() {
         if (wordBuffer.get(k) == 's') {
             if (endsWith("sses")) k -= 2;
             else if (endsWith("ies")) setTo("i");
             else if (wordBuffer.get(k - 1) != 's') k--;
         }
         if (endsWith("eed")) {
             if (measureConsonantSequences() > 0) k--;
         } else if ((endsWith("ed") || endsWith("ing")) && containsVowel()) {
             k = j;
             if (endsWith("at")) setTo("ate");
             else if (endsWith("bl")) setTo("ble");
             else if (endsWith("iz")) setTo("ize");
             else if (hasDoubleConsonant(k)) {
                 k--;
                 char ch = wordBuffer.get(k);
                 if (ch == 'l' || ch == 's' || ch == 'z') k++;
             } else if (measureConsonantSequences() == 1 && cvc(k)) setTo("e");
         }
     }

     /**
      * Step 2 of the Porter Stemming Algorithm.
      * Turns terminal y to i when there is another vowel in the stem.
      */
     private final void transformTerminalYToI() {
         if (endsWith("y") && containsVowel()) wordBuffer.set(k, 'i');
     }

     /**
      * Step 3 of the Porter Stemming Algorithm.
      * Maps double suffixes to single ones.
      */
     private final void mapDoubleSuffixesToSingle() {
         if (k == 0) return; /* For Bug 1 */
         switch (wordBuffer.get(k - 1)) {
             case 'a': if (endsWith("ational")) { repIfMeasureGreaterZero("ate"); break; }
                       if (endsWith("tional")) { repIfMeasureGreaterZero("tion"); break; }
                       break;
             case 'c': if (endsWith("enci")) { repIfMeasureGreaterZero("ence"); break; }
                       if (endsWith("anci")) { repIfMeasureGreaterZero("ance"); break; }
                       break;
             case 'e': if (endsWith("izer")) { repIfMeasureGreaterZero("ize"); break; }
                       break;
             case 'l': if (endsWith("bli")) { repIfMeasureGreaterZero("ble"); break; }
                       if (endsWith("alli")) { repIfMeasureGreaterZero("al"); break; }
                       if (endsWith("entli")) { repIfMeasureGreaterZero("ent"); break; }
                       if (endsWith("eli")) { repIfMeasureGreaterZero("e"); break; }
                       if (endsWith("ousli")) { repIfMeasureGreaterZero("ous"); break; }
                       break;
             case 'o': if (endsWith("ization")) { repIfMeasureGreaterZero("ize"); break; }
                       if (endsWith("ation")) { repIfMeasureGreaterZero("ate"); break; }
                       if (endsWith("ator")) { repIfMeasureGreaterZero("ate"); break; }
                       break;
             case 's': if (endsWith("alism")) { repIfMeasureGreaterZero("al"); break; }
                       if (endsWith("iveness")) { repIfMeasureGreaterZero("ive"); break; }
                       if (endsWith("fulness")) { repIfMeasureGreaterZero("ful"); break; }
                       if (endsWith("ousness")) { repIfMeasureGreaterZero("ous"); break; }
                       break;
             case 't': if (endsWith("aliti")) { repIfMeasureGreaterZero("al"); break; }
                       if (endsWith("iviti")) { repIfMeasureGreaterZero("ive"); break; }
                       if (endsWith("biliti")) { repIfMeasureGreaterZero("ble"); break; }
                       break;
             case 'g': if (endsWith("logi")) { repIfMeasureGreaterZero("log"); break; }
         }
     }

     /**
      * Step 4 of the Porter Stemming Algorithm.
      * Deals with -ic-, -full, -ness etc.
      */
     private final void handleSuffixes() {
         switch (wordBuffer.get(k)) {
             case 'e': if (endsWith("icate")) { repIfMeasureGreaterZero("ic"); break; }
                       if (endsWith("ative")) { repIfMeasureGreaterZero(""); break; }
                       if (endsWith("alize")) { repIfMeasureGreaterZero("al"); break; }
                       break;
             case 'i': if (endsWith("iciti")) { repIfMeasureGreaterZero("ic"); break; }
                       break;
             case 'l': if (endsWith("ical")) { repIfMeasureGreaterZero("ic"); break; }
                       if (endsWith("ful")) { repIfMeasureGreaterZero(""); break; }
                       break;
             case 's': if (endsWith("ness")) { repIfMeasureGreaterZero(""); break; }
                       break;
         }
     }

     /**
      * Step 5 of the Porter Stemming Algorithm.
      * Takes off -ant, -ence etc., in context <c>vcvc<v>.
      */
     private final void removeSuffixesInContext() {
         if (k == 0) return; /* for Bug 1 */
         switch (wordBuffer.get(k - 1)) {
             case 'a': if (endsWith("al")) break; return;
             case 'c': if (endsWith("ance")) break;
                       if (endsWith("ence")) break; return;
             case 'e': if (endsWith("er")) break; return;
             case 'i': if (endsWith("ic")) break; return;
             case 'l': if (endsWith("able")) break;
                       if (endsWith("ible")) break; return;
             case 'n': if (endsWith("ant")) break;
                       if (endsWith("ement")) break;
                       if (endsWith("ment")) break;
                       if (endsWith("ent")) break; return;
             case 'o': if (endsWith("ion") && j >= 0 && (wordBuffer.get(j) == 's' || wordBuffer.get(j) == 't')) break;
                       if (endsWith("ou")) break; return;
             case 's': if (endsWith("ism")) break; return;
             case 't': if (endsWith("ate")) break;
                       if (endsWith("iti")) break; return;
             case 'u': if (endsWith("ous")) break; return;
             case 'v': if (endsWith("ive")) break; return;
             case 'z': if (endsWith("ize")) break; return;
             default: return;
         }
         if (measureConsonantSequences() > 1) k = j;
     }

     /**
      * Step 6 of the Porter Stemming Algorithm.
      * Removes a final -e if measureConsonantSequences() > 1.
      */
     private final void removeFinalE() {
         j = k;
         if (wordBuffer.get(k) == 'e') {
             int a = measureConsonantSequences();
             if (a > 1 || a == 1 && !cvc(k - 1)) k--;
         }
         if (wordBuffer.get(k) == 'l' && hasDoubleConsonant(k) && measureConsonantSequences() > 1) k--;
     }

     /**
      * Step 7 of the Porter Stemming Algorithm.
      * Handles verb suffixes and other additional rules.
      */
     private final void handleVerbSuffixes() {
         if (endsWith("ing") && containsVowel()) {
             setTo("e");
         } else if (endsWith("ed") && containsVowel()) {
             setTo("");
         } else if (endsWith("es") && measureConsonantSequences() > 0) {
             setTo("e");
         } else if (endsWith("s") && measureConsonantSequences() > 1) {
             setTo("");
         } else if (endsWith("ational")) {
             repIfMeasureGreaterZero("ate");
         } else if (endsWith("tional")) {
             repIfMeasureGreaterZero("tion");
         } else if (endsWith("ing")) {
             setTo("");
         }
     }

     /**
      * Stems the word placed into the Stemmer buffer through calls to add().
      * Returns true if the stemming process resulted in a word different
      * from the input.
      */
     public void stem() {
         k = wordBuffer.size() - 1;
         if (k > 1) {
             removePluralsAndEdOrIng();
             transformTerminalYToI();
             mapDoubleSuffixesToSingle();
             handleSuffixes();
             removeSuffixesInContext();
             removeFinalE();
             handleVerbSuffixes();  // Added step for handling verb suffixes
         }
         endIndex = k + 1;
         wordBuffer = new ArrayList<>();  // Clear the buffer for the next word
     }

     /**
      * Test program for demonstrating the Stemmer. It reads text from a
      * string, stems each word, and writes the result to standard output.
      */
     public static void main(String[] args)
       {
          char[] w = new char[501];
          Stemmer s = new Stemmer();
          for (int i = 0; i < args.length; i++)
          try
          {
             FileInputStream in = new FileInputStream(args[i]);

             try
             { while(true)

               {  int ch = in.read();
                  if (Character.isLetter((char) ch))
                  {
                     int j = 0;
                     while(true)
                     {  ch = Character.toLowerCase((char) ch);
                        w[j] = (char) ch;
                        if (j < 500) j++;
                        ch = in.read();
                        if (!Character.isLetter((char) ch))
                        {
                           /* to test add(char ch) */
                           for (int c = 0; c < j; c++) s.add(w[c]);

                           /* or, to test add(char[] w, int j) */
                           /* s.add(w, j); */

                           s.stem();
                           {  String u;

                              /* and now, to test toString() : */
                              u = s.toString();

                              /* to test getResultBuffer(), getResultLength() : */
                              /* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */

                              System.out.print(u);
                           }
                           break;
                        }
                     }
                  }
                  if (ch < 0) break;
                  System.out.print((char)ch);
               }
             }
             catch (IOException e)
             {  System.out.println("Error In Reading " + args[i]);
                break;
             }
          }
          catch (FileNotFoundException e)
          {  System.out.println("File " + args[i] + " Not Found");
             break;
          }
       }
    }
