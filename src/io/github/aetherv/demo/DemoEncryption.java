package io.github.aetherv.demo;

import java.security.NoSuchAlgorithmException;
import java.util.Random;
import io.github.aetherv.encryption.*;

/**
 * A Demonstration for an Encryption Algorithm.
 */

public class DemoEncryption extends Encryption{
    /**
     * Generates a 64 char long Salt in the form of a String.
     * @return A Salt in String form, that is 64 chars long.
     */
    @Override
    public String genSalt() {
        return EncryptionUtils.genSalt(64);
    }

    /**
     * Hashes a Prepared String using a given algorithm.
     * In this case, SHA512 is used, however any algorithm can be used (pre-implemented algorithm's can be found in EncryptionUtils).
     * It should be noted, that one can also make use of external APIs that implement alternative algorithms such as BCrypt.
     * Make sure that you read through on which hashing algorithm's have been broken, before selecting your algorithm.
     * @param preparedString A prepared String. It is advised to add a salt and a pepper to this.
     * @return A hash of the given String
     */
    @Override
    public String encrypt(String preparedString) {
        try {
            return EncryptionUtils.hash(preparedString, "SHA-512");
        } catch (NoSuchAlgorithmException e) {
            //Since this is a self written method and not reliant on user input, this should never be triggered
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a string that can be hashed securely. In the following implementation we simply return salt+password+randomly selected pepper
     * @param password The password that you wish to hash.
     * @param salt The salt that you wish to use.
     * @param peppers A list of peppers (or not) that you wish to use. If no peppers are used, use ' new String[]{""} '.
     * @return Returns a String containing salt+password+randomly selected pepper.
     */
    //@TODO Remove System out print
    @Override
    public String prepareString(String password, String salt, String... peppers) {
        Random ran = new Random();
        String preparedString = salt+password;
        if(peppers!=null && peppers.length!=0) preparedString+=peppers[ran.nextInt(peppers.length)];
        return preparedString;
    }
}
