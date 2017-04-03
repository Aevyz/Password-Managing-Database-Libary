package io.github.aetherv.exceptions;

import java.util.ArrayList;

/**
 * This Exception triggers when a given Field is not found within an ArrayList containing all Field Names. If this occurs, it could be that an SQL Inject is triggering this Exception, or your code is just malfunctioning...
 */
public class FieldNotFoundException extends Exception {

    private final String errorField;
    private final ArrayList<String> listOfFields;

    /**
     * Saves the variables that caused the exception to trigger.
     * @param errorField The field that caused the exception to occur.
     * @param listOfFields A list of all fields present in the ArrayList field.
     */
    public FieldNotFoundException(String errorField, ArrayList<String> listOfFields){
        this.errorField = errorField;
        this.listOfFields = listOfFields;
    }
    //TODO Possibly revealing the list of fields may be a waste of space and processing power. It should be assumed that someone is capable of knowing what fields are in their database.
    @Override
    public String toString(){
        String s = "The Field \""+errorField+"\" cannot be found. Only the following fields are accepted: ";
        for(String ss : listOfFields) s+="\n\t-"+ss;
        s+="\n\nIt is possible that someone may be trying to SQL Inject You";
        return s;
    }
}
