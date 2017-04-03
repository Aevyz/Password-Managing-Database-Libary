package io.github.aetherv.exceptions;

/**
 * A person with the same field is found within the database.
 */
public class AlreadyRegisteredException extends Exception {
    private final String data;
    private final String field;

    /**
     * Specifies which data and field caused this Exception to trigger.
     * @param data The data that was already found in the DB.
     * @param field The field where the data was already found in the DB.
     */
    public AlreadyRegisteredException(String data, String field) {
        this.data = data;
        this.field = field;
    }

    @Override
    public String toString(){
        return String.format("The Field %s already contains a cell named %s", field, data);
    }
}
