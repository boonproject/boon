package org.boon.json.test;


import org.boon.json.annotations.JsonProperty;
import org.boon.json.annotations.SerializedName;

public class Person {

    @JsonProperty ( "moniker" )
    public String name;

    @SerializedName ( "homeTown" )
    public String city;


    public Pet pet;
    public Animal pet2;


}
