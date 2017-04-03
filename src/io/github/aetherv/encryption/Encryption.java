package io.github.aetherv.encryption;


/**
 * An Abstract Class containing a basis for what is required for Encryption. All non-Abstract methods are based off of the abstract methods encrypt(String preparedString) and prepareString(String password, String salt, String ... peppers). It is highly advisable to use some of the methods from EncryptionUtils to help generate the relevant results of the given abstract classes.
 * It is vital, that you double check the current norms for data security and check if there any flaws present with any of the algorithm's you are using.
 */
public abstract class Encryption {

    /**
     * Returns a String[] of length 2 with the salt and the hashed password, the latter being generated by the encrypt(String preparedString) method.
     * @param preparedString A String that is prepared for hashing/encrypting. It is highly advisable to be using a Salt and Pepper.
     * @param salt The salt that was used to hash the String.
     * @return Returns a String[] of length 2, in which the first element is the Salt and the second is the hashed Password
     */
    public String[] saltAndEncrypt(String salt, String preparedString) {
        return new String[]{salt, encrypt(preparedString)};
    }

    /**
     * Returns a String[] of length 2 with the salt and the hashed password, the latter being generated by first creating a preparedString using the preparedString(String password, String Salt, String[] peppers) method, followed by the resulting String being hashed via the encrypt(String preparedString) method.
     * @param password The password that shall be first turned into a prepared String, then hashed.
     * @param salt The Salt that shall be used to generate a prepared String, that shall be hashed.
     * @param peppers An array of peppers that shall be used to generate a prepared String, that shall be hashed. One shall be randomly chosen. If no peppers are used, use ' new String[]{""} '.
     * @return Returns a String[] of length 2, in which the first element is the Salt and the second is the hashed Password.
     */
    public String[] saltAndEncrypt(String password, String salt, String ... peppers)  {
        return saltAndEncrypt(salt, prepareString(password, salt, peppers));
    }
    /**
     * Returns a String[] of length 2 with the salt and the hashed password, the prior being generated via the genSalt() method, the latter being generated by first creating a preparedString using the preparedString(String password, String salt (the one generated previously) , String[] peppers) method, followed by the resulting String being hashed via the encrypt(String preparedString) method.
     * @param password The password that shall be first turned into a prepared String, then hashed.
     * @param peppers An array of peppers that shall be used to generate a prepared String, that shall be hashed. If no peppers are used, use ' new String[]{""} '.
     * @return Returns a String[] of length 2, in which the first element is the Salt and the second is the hashed Password.
     */
    public String[] saltAndEncrypt(String password, String ... peppers) {
        String salt = genSalt();
        return saltAndEncrypt(salt, prepareString(password, salt, peppers));
    }

    /**
     * Specify an algorithm you wish to use to generate a salt
     *  The genSalt set of methods from EncryptionUtils may prove useful, however you can write your own method if you so desire.
     * @return A Salt generated from the algorithm
     */
    public abstract String genSalt();

    /**
     * Hashes a Prepared String using a given algorithm.
     * It should be noted, that one can also make use of external APIs that implement alternative algorithms such as BCrypt.
     * Make sure that you read through on which hashing algorithm's have been broken, before selecting your algorithm.
     * @param preparedString A String that is prepared for hashing/encrypting. It is highly advisable to be using a Salt and Pepper.
     * @return Returns the String hashed
     */
    public abstract String encrypt(String preparedString);

    /**
     * Specify a way of preparing a String for hashing. It is advisable to make use of salts and peppers to make the hashes more secure.
     * @param password The password that you wish to hash.
     * @param salt The salt that you wish to use.
     * @param peppers A list of peppers (or not) that you wish to use. If no peppers are used, use ' new String[]{""} '.
     * @return A prepared String, ready to be hashed
     */
    public abstract String prepareString(String password, String salt, String ... peppers);

    /**
     * Compares 2 Hashes in the format of Strings using the String.equals() function.
     * @param hash1 Hash in String Format.
     * @param hash2 Hash in String Format.
     * @return True if 2 hash's are the same, else false.
     */
    public boolean compareHashToHash(String hash1, String hash2){
        return hash1.equals(hash2);
    }

    /**
     * Compares a password to its hash. Will cycle through all peppers and check if one of them matches the given hash.
     *      Note: This assumes that encrypt(prepareString(password, salt, pepper1)) will always give the same result, assuming that pepper1 is an element of peppers
     *
     * @param hash Hash to compare the password to.
     * @param password Password to be used to generate the prepared Statements
     * @param salt Salt to be used to generate the prepared Statements
     * @param peppers A list of peppers to be used to generate the prepared Statements. If no peppers are used, use ' new String[]{""} '.
     * @return True if the given hash is the same as any of the the generated hashes.
     */
    public boolean compareHashToPassword(String hash, String password, String salt, String ... peppers) {
        for(String pep : peppers){
            if(hash.equals(encrypt(prepareString(password, salt, pep)))) return true;
        }
        return false;
    }
}