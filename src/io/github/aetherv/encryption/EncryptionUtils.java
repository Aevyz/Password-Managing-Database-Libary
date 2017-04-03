package io.github.aetherv.encryption;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * -Final SaltLength
 -Final String[] Pepper
 -void Hash(String original, String algorithm)
 -String[] getPepper
 -String genPepper
 -String genSalt
 -String genSalt(int customLength)
 -String genSalt(int upperCustomLength, int lowerCustomLength)
 -String[] genSalt(int howMany, int upperLength, int LowerLength)
 */

public class EncryptionUtils {
    /**
     * @return Array Containing all Upper Case characters
     */
    public static char[] upper(){
        char[] upper = new char[90-65];
        for(int i =65;i<=90;i++)upper[i-65]=(char) i;
        return upper;
    }
    /**
     * @return Array Containing all Lower Case characters
     */
    public static char[] lower(){
        char[] low = new char[122-97];
        for(int i =97;i<=122;i++)low[i-97]=(char) i;
        return low;
    }

    /**
     * @return Array Containing all Numbers as characters
     */
    public static char[] number(){
        return new char[]{'0', '1', '2', '3','4','5','6','7','8','9'};
    }

    /**
     * @return Array Containing all chars between ASCII 32 and 126 (inclusive)
     */
    public static char[] all(){
        char[] all=new char[126-32];
        for(int i =32;i<=126;i++) all[i-32]=(char) i;
        return all;
    }

    /**
     * Useful if you wish to have a salt containing only a mixture of specified chars (upper, lower or numbers)
     * @param a Array 1
     * @param b Array 2
     * @param <T> What Type are the Arrays
     * @return Array1+Array2 in that order
     */
    public <T> T[] concatenateArray(T[] a, T[] b){

        //Method based off of "http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java"
        @SuppressWarnings("unchecked")
        T[] r = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length+b.length);
        int index=0;
        for(T item : a){
            r[index]=item;
            index++;
        }
        for(T item : b){
            r[index]=item;
            index++;
        }
        return r;
    }

    /**
     * Selects a random pepper from an array of peppers
     * @param peppers Array of peppers
     * @return a randomly selected pepper
     */
    public static String selectPepper(String[] peppers){
        if(peppers.length==0) return "";
        Random ran = new Random();
        return peppers[ran.nextInt(peppers.length)];
    }

    /**
     * Generate multiple salts, that are saved in an array
     * @param quantity how many salts should be generated
     * @param minLength Minimum Length of Salt
     * @param maxLength Maximum Length of Salt
     * @return an array of a specified length containing a given quantities worth of salts
     */
    public static String[] genMultipleSalt(int quantity, int minLength, int maxLength){
        String [] r = new String[quantity];
        for(String s : r) s = genSalt(minLength, maxLength);
        return r;
    }

    /**
     * Generates a salt that is 64 chars long
     * @return 64 char long salt
     */
    public static String genSalt(){
        return genSalt(64);
    }

    /**
     * Generates a salt that is a given number of chars long
     * @param saltLength how long the salt should be
     * @return a salt of a given length
     */
    public static String genSalt(int saltLength){
        String s="";
        Random ran = new Random();
        for(int i = 0; i < saltLength; i++) s+=(char) (ran.nextInt(93)+33);
        return s;
    }

    /**
     * Returns a salt of a length between minLength and maxLength
     * @param minLength Minimum length of Salt
     * @param maxLength Maximum length of Salt
     * @return a salt of length between minLength and maxLength
     */
    public static String genSalt(int minLength, int maxLength){
        String s="";
        Random ran = new Random();
        int length = ran.nextInt(maxLength-minLength)+minLength;
        for(int i = 0; i<length;i++)s+=(char) (ran.nextInt(93)+33);
        return s;
    }

    /**
     * Generate a hash from a given string
     *
     * Algorithm Choices:    MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
     *
     *      It is advisable to use a SHA-2 algorithm
     *      MD2 and MD5 are long outdated
     *      SHA-1 should no longer be used, see shattered.io
     *
     * @param toEncrypt String to hash
     * @param algorithm Which hash algorithm shall be used
     * @return A string which contains the hashed version of a given String
     * @throws NoSuchAlgorithmException If an invalid algorithm is selected
     */
    public static String hash(String toEncrypt, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest alg = MessageDigest.getInstance(algorithm);
        BigInteger bigInt = new BigInteger(1,alg.digest(toEncrypt.getBytes()));
        String hashText = bigInt.toString(16);
        // In the event of any leading 0's
        int length;
        switch (algorithm) {
            case "MD2":
                length = 32;
                break;
            case "MD5":
                length = 32;
                break;
            default:
                String[] parts = algorithm.split("-");
                if (parts[1].equals("1")) length = 40;
                else length = Integer.parseInt(parts[1]) / 4;
                break;
        }
        while(hashText.length() < length ) {
            hashText = "0" + hashText;
        }
        return hashText;
    }
}