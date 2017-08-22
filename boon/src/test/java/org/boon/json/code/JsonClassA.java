package org.boon.json.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piyush.goyal on 9/22/16.
 */
public class JsonClassA {


    private Map<String, InterfaceB> xx = new HashMap<>();

    private List<InterfaceB> yy = new ArrayList<>();
    private String textValue;

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public Map<String, InterfaceB> getXx() {
        return xx;
    }

    public void setXx(Map<String, InterfaceB> xx) {
        this.xx = xx;
    }

    public List<InterfaceB> getYy() {
        return yy;
    }

    public void setYy(List<InterfaceB> yy) {
        this.yy = yy;
    }
}
