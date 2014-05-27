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

package org.boon.json;

import org.boon.core.reflection.Mapper;
import org.boon.core.reflection.fields.*;
import org.boon.json.implementation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JsonParserFactory {


    private Charset charset = StandardCharsets.UTF_8;
    private boolean lax;
    private boolean chop = false;
    private boolean lazyChop = true;
    private FieldAccessMode fieldAccessType = FieldAccessMode.FIELD;
    private boolean useAnnotations;

    private  Set<String> ignoreSet;
    private  String view;
    private  boolean respectIgnore;



    public FieldAccessMode getFieldAccessType() {
        return fieldAccessType;
    }


    public boolean isChop() {
        return chop;
    }

    public JsonParserFactory setChop( boolean chop ) {
        this.chop = chop;
        return this;
    }

    public boolean isLazyChop() {
        return lazyChop;
    }

    public JsonParserFactory setLazyChop( boolean lazyChop ) {
        this.lazyChop = lazyChop;
        return this;
    }

    public JsonParserFactory lax() {
        lax = true;
        return this;
    }

    public JsonParserFactory strict() {
        lax = false;
        return this;
    }


    public JsonParserFactory setCharset( Charset charset ) {
        this.charset = charset;
        return this;
    }







    public JsonParserAndMapper createFastParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonFastParser (  false, chop, lazyChop ),   new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));
        jsonParser.setCharset ( charset );
        return jsonParser;
    }



    public JsonParserAndMapper createFastObjectMapperParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonFastParser (  true ),  new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));
        jsonParser.setCharset ( charset );
        return jsonParser;
    }




    public JsonParserAndMapper createUTF8DirectByteParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonUTF8Parser (  ),  new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( StandardCharsets.UTF_8 );
        return jsonParser;

    }

    public JsonParserAndMapper createASCIIParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonAsciiParser (  ),  new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( StandardCharsets.US_ASCII );
        return jsonParser;

    }


    public JsonParserAndMapper createLaxParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserLax ( false, chop, lazyChop  ),  new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }



    public JsonParserAndMapper createParserWithEvents(JsonParserEvents events) {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserLax ( false, chop, lazyChop, false, events  ),
                new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }


    public JsonParserAndMapper createCharacterSourceParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserUsingCharacterSource ( ),
                new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }

    public JsonParserAndMapper createJsonCharArrayParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserCharArray( ),
                new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }

    public JsonParserAndMapper createPlistParser() {

        if (charset==null) {
           charset= StandardCharsets.US_ASCII;
        }
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new PlistParser ( false, chop, lazyChop  ),
                new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }

    public JsonParserAndMapper createLazyFinalParser() {
        return createFastParser();
    }

    public JsonParserAndMapper createJsonParserForJsonPath() {
        return createFastParser();
    }

    public JsonParserAndMapper create() {



        if ( charset == null ) {
            charset = StandardCharsets.UTF_8;
        }

        return new JsonMappingParser ( new Mapper(fieldAccessType, useAnnotations, ignoreSet, view, respectIgnore), charset,
                 lax,  chop, lazyChop );
    }


    public boolean isUsePropertiesFirst () {
        return fieldAccessType == FieldAccessMode.PROPERTY_THEN_FIELD;
    }


    public JsonParserFactory usePropertiesFirst () {
        fieldAccessType = FieldAccessMode.PROPERTY_THEN_FIELD;
        return this;
    }

    public boolean isUseFieldsFirst () {
        return this.fieldAccessType == FieldAccessMode.FIELD_THEN_PROPERTY;

    }


    public JsonParserFactory useFieldsFirst () {
        this.fieldAccessType  = FieldAccessMode.FIELD_THEN_PROPERTY;
        return this;
    }


    public JsonParserFactory useFieldsOnly () {
        this.fieldAccessType  = FieldAccessMode.FIELD;
        return this;
    }



    public JsonParserFactory usePropertyOnly () {
        this.fieldAccessType  = FieldAccessMode.PROPERTY;
        return this;
    }



    public JsonParserFactory useAnnotations () {
        this.useAnnotations  = true;
        return this;
    }

    public boolean isUseAnnotations() {
        return useAnnotations;
    }

    public JsonParserFactory setUseAnnotations( boolean useAnnotations ) {
        this.useAnnotations = useAnnotations;
        return this;

    }




    public Set<String> getIgnoreSet() {
        return ignoreSet;
    }

    public JsonParserFactory setIgnoreSet(Set<String> ignoreSet) {
        this.ignoreSet = ignoreSet;
        return this;
    }

    public String getView() {
        return view;
    }

    public JsonParserFactory setView(String view) {
        this.view = view;
        return this;
    }

    public boolean isRespectIgnore() {
        return respectIgnore;
    }

    public JsonParserFactory setRespectIgnore(boolean respectIgnore) {
        this.respectIgnore = respectIgnore;
        return this;
    }

}
