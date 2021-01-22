/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.json.test;

import org.boon.json.annotations.JsonIgnore;
import org.boon.json.annotations.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

@JsonIgnoreProperties ("ignoreMe2")
public class AllTypes {

    public Animal pet2;

    public Pet pet;

    public FooBasket getFooBasket () {
        return fooBasket;
    }

    public void setFooBasket ( FooBasket fooBasket ) {
        this.fooBasket = fooBasket;
    }

    FooBasket fooBasket = new FooBasket ();

    public String ignoreMe3;

    public String ignoreMe2;

    int myInt;
    boolean myBoolean;
    short myShort;
    long myLong;
    String string;
    String string2;
    BigDecimal bigDecimal;
    BigInteger bigInteger;
    Date date;

    ZonedDateTime zonedDateTimeMillis;
    ZonedDateTime zonedDateTimeIso;
    ZonedDateTime zonedDateTimeToString;
    LocalDate localDate;
    LocalTime localTime;
    LocalDateTime localDateTime;
    Duration durationNanos;
    Duration durationString;

    float myFloat;
    double myDouble;
    byte myByte;

    FooEnum foo;
    FooEnum bar;

    @JsonIgnore
    public String ignoreMe;



    public long someTimeStamp = new Date (  ).getTime ();

    AllTypes allType;

    List<AllTypes> allTypeList = new ArrayList<> (  );

    Set<AllTypes> allTypesSet = new HashSet<> (  );

    public Set<AllTypes> getAllTypesSet() {
        return allTypesSet;
    }

    public void setAllTypesSet( Set<AllTypes> allTypesSet ) {
        this.allTypesSet = allTypesSet;
    }

    public String getString2 () {
        return string2;
    }

    public void setString2 ( String string2 ) {
        this.string2 = string2;
    }

    public List<AllTypes> getAllTypeList() {
        return allTypeList;
    }

    public void setAllTypeList( List<AllTypes> allTypeList ) {
        this.allTypeList = allTypeList;
    }

    public AllTypes getAllType () {
        return allType;
    }

    public void setAllType ( AllTypes allType ) {
        this.allType = allType;
    }

    public byte getMyByte () {
        return myByte;
    }

    public void setMyByte ( byte myByte ) {
        this.myByte = myByte;
    }

    public int getMyInt () {
        return myInt;
    }

    public void setMyInt ( int myInt ) {
        this.myInt = myInt;
    }

    public boolean isMyBoolean () {
        return myBoolean;
    }

    public void setMyBoolean ( boolean myBoolean ) {
        this.myBoolean = myBoolean;
    }

    public short getMyShort () {
        return myShort;
    }

    public void setMyShort ( short myShort ) {
        this.myShort = myShort;
    }

    public long getMyLong () {
        return myLong;
    }

    public void setMyLong ( long myLong ) {
        this.myLong = myLong;
    }

    public String getString () {
        return string;
    }

    public void setString ( String string ) {
        this.string = string;
    }

    public float getMyFloat () {
        return myFloat;
    }

    public void setMyFloat ( float myFloat ) {
        this.myFloat = myFloat;
    }

    public double getMyDouble () {
        return myDouble;
    }

    public void setMyDouble ( double myDouble ) {
        this.myDouble = myDouble;
    }


    public BigDecimal getBigDecimal () {
        return bigDecimal;
    }

    public void setBigDecimal ( BigDecimal bigDecimal ) {
        this.bigDecimal = bigDecimal;
    }

    public BigInteger getBigInteger () {
        return bigInteger;
    }

    public void setBigInteger ( BigInteger bigInteger ) {
        this.bigInteger = bigInteger;
    }

    public Date getDate () {
        return date;
    }

    public void setDate ( Date date ) {
        this.date = date;
    }

    public ZonedDateTime getZonedDateTimeMillis() {
        return zonedDateTimeMillis;
    }

    public void setZonedDateTimeMillis(ZonedDateTime zonedDateTimeMillis) {
        this.zonedDateTimeMillis = zonedDateTimeMillis;
    }

    public ZonedDateTime getZonedDateTimeToString() {
        return zonedDateTimeToString;
    }

    public void setZonedDateTimeToString(ZonedDateTime zonedDateTimeToString) {
        this.zonedDateTimeToString = zonedDateTimeToString;
    }

    public ZonedDateTime getZonedDateTimeIso() {
        return zonedDateTimeIso;
    }

    public void setZonedDateTimeIso(ZonedDateTime zonedDateTimeIso) {
        this.zonedDateTimeIso = zonedDateTimeIso;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Duration getDurationNanos() {
        return durationNanos;
    }

    public void setDurationNanos(Duration durationNanos) {
        this.durationNanos = durationNanos;
    }

    public Duration getDurationString() {
        return durationString;
    }

    public void setDurationString(Duration durationString) {
        this.durationString = durationString;
    }

    public FooEnum getFoo () {
        return foo;
    }

    public void setFoo ( FooEnum foo ) {
        this.foo = foo;
    }

    public FooEnum getBar () {
        return bar;
    }

    public void setBar ( FooEnum bar ) {
        this.bar = bar;
    }


    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof AllTypes ) ) return false;

        AllTypes allTypes1 = ( AllTypes ) o;

        if ( myBoolean != allTypes1.myBoolean ) return false;
        if ( myByte != allTypes1.myByte ) return false;
        if ( Double.compare ( allTypes1.myDouble, myDouble ) != 0 ) return false;
        if ( Float.compare ( allTypes1.myFloat, myFloat ) != 0 ) return false;
        if ( myInt != allTypes1.myInt ) return false;
        if ( myLong != allTypes1.myLong ) return false;
        if ( myShort != allTypes1.myShort ) return false;
        if ( bigDecimal != null ? !bigDecimal.equals ( allTypes1.bigDecimal ) : allTypes1.bigDecimal != null )
            return false;
        if ( bigInteger != null ? !bigInteger.equals ( allTypes1.bigInteger ) : allTypes1.bigInteger != null )
            return false;
        if ( string != null ? !string.equals ( allTypes1.string ) : allTypes1.string != null ) return false;
        if ( string2 != null ? !string2.equals ( allTypes1.string2 ) : allTypes1.string2 != null ) return false;

        if ( zonedDateTimeMillis != null ? !zonedDateTimeMillis.equals ( allTypes1.zonedDateTimeMillis ) : allTypes1.zonedDateTimeMillis != null ) return false;
        if ( zonedDateTimeIso != null ? !zonedDateTimeIso.equals ( allTypes1.zonedDateTimeIso ) : allTypes1.zonedDateTimeIso != null ) return false;
        if ( zonedDateTimeToString != null ? !zonedDateTimeToString.equals ( allTypes1.zonedDateTimeToString ) : allTypes1.zonedDateTimeToString != null ) return false;
        if ( localDate != null ? !localDate.equals ( allTypes1.localDate ) : allTypes1.localDate != null ) return false;
        if ( localTime != null ? !localTime.equals ( allTypes1.localTime ) : allTypes1.localTime != null ) return false;
        if ( localDateTime != null ? !localDateTime.equals ( allTypes1.localDateTime ) : allTypes1.localDateTime != null ) return false;
        if ( durationNanos != null ? !durationNanos.equals ( allTypes1.durationNanos ) : allTypes1.durationNanos != null ) return false;
        if ( durationString != null ? !durationString.equals ( allTypes1.durationString ) : allTypes1.durationString != null ) return false;

        if ( allTypeList == null && allTypes1.allTypeList.size () == 0) {
            return true;
        } else {

            if (allTypeList.size() == allTypes1.allTypeList.size()) {

                for (int index = 0; index < allTypeList.size(); index++) {
                    AllTypes theirs = allTypes1.allTypeList.get(index);
                    AllTypes ours = allTypeList.get( index );
                    if (!ours.equals( theirs ))  {
                        return false;
                    }
                }
            } else {
                return false;
            }

        }

        if ( date != null  && allTypes1.date!=null) {

            long delta = Math.abs ( date.getTime () - allTypes1.date.getTime ());

            if ( delta < 1000) {
                return true;
            } else {
                return false;
            }

        }

        if ( allType != null ? !allType.equals ( allTypes1.allType ) : allTypes1.allType != null ) return false;
        if ( bar != allTypes1.bar ) return false;
        if ( foo != allTypes1.foo ) return false;


        return true;
    }

    @Override
    public int hashCode () {
        int result;
        long temp;
        result = myInt;
        result = 31 * result + ( myBoolean ? 1 : 0 );
        result = 31 * result + ( int ) myShort;
        result = 31 * result + ( int ) ( myLong ^ ( myLong >>> 32 ) );
        result = 31 * result + ( string != null ? string.hashCode () : 0 );
        result = 31 * result + ( string2 != null ? string2.hashCode () : 0 );
        result = 31 * result + ( bigDecimal != null ? bigDecimal.hashCode () : 0 );
        result = 31 * result + ( bigInteger != null ? bigInteger.hashCode () : 0 );
        result = 31 * result + ( date != null ? date.hashCode () : 0 );

        result = 31 * result + ( zonedDateTimeMillis != null ? zonedDateTimeMillis.hashCode () : 0 );
        result = 31 * result + ( zonedDateTimeIso != null ? zonedDateTimeIso.hashCode () : 0 );
        result = 31 * result + ( zonedDateTimeToString != null ? zonedDateTimeToString.hashCode () : 0 );
        result = 31 * result + ( localDate != null ? localDate.hashCode () : 0 );
        result = 31 * result + ( localTime != null ? localTime.hashCode () : 0 );
        result = 31 * result + ( localDateTime != null ? localDateTime.hashCode () : 0 );
        result = 31 * result + ( durationNanos != null ? durationNanos.hashCode () : 0 );
        result = 31 * result + ( durationString != null ? durationString.hashCode () : 0 );

        result = 31 * result + ( myFloat != +0.0f ? Float.floatToIntBits ( myFloat ) : 0 );
        temp = Double.doubleToLongBits ( myDouble );
        result = 31 * result + ( int ) ( temp ^ ( temp >>> 32 ) );
        result = 31 * result + ( int ) myByte;
        result = 31 * result + ( foo != null ? foo.hashCode () : 0 );
        result = 31 * result + ( bar != null ? bar.hashCode () : 0 );
        result = 31 * result + ( allType != null ? allType.hashCode () : 0 );
        result = 31 * result + ( allTypeList != null ? allTypeList.hashCode () : 0 );
        return result;
    }

    @Override
    public String toString () {
        return "AllTypes{" +
                "myInt=" + myInt +
                ", myBoolean=" + myBoolean +
                ", myShort=" + myShort +
                ", myLong=" + myLong +
                ", string='" + string + '\'' +
                ", string2='" + string2 + '\'' +
                ", bigDecimal=" + bigDecimal +
                ", bigInteger=" + bigInteger +
                ", zonedDateTimeMillis=" + zonedDateTimeMillis +
                ", zonedDateTimeIso=" + zonedDateTimeIso +
                ", zonedDateTimeToString=" + zonedDateTimeToString +
                ", localDate=" + localDate +
                ", localTime=" + localTime +
                ", localDateTime=" + localDateTime +
                ", durationNanos=" + durationNanos +
                ", durationString=" + durationString +
                ", myFloat=" + myFloat +
                ", myDouble=" + myDouble +
                ", myByte=" + myByte +
                ", foo=" + foo +
                ", bar=" + bar +
                ", allType=" + allType +
                ", allTypeList=" + allTypeList +
                '}';
    }
}
