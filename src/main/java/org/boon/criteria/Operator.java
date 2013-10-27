package org.boon.criteria;

public enum Operator {
    EQUAL,          //Indexed
    NOT_EQUAL,      //Not Indexed

    //Not implemented
    NOT_NULL,       //Not Indexed
    IS_NULL,       //Not Indexed
    IS_EMPTY,       //Not Indexed
    NOT_EMPTY,        //Not indexed


    LESS_THAN,      //Indexed
    LESS_THAN_EQUAL, //Indexed
    GREATER_THAN,    //Indexed
    GREATER_THAN_EQUAL,//Indexed
    BETWEEN,   //Indexed for strings


    STARTS_WITH, //Indexed for strings
    ENDS_WITH,  //Not indexed
    CONTAINS,   //Not indexed
    NOT_CONTAINS,//Not indexed
    MATCHES,    //Not implemented yet
    IN,         //Not indexed
    NOT_IN,     //Not Indexed
    NOT,


}
