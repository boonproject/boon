package org.boon.json.code;

/**
 * Created by piyush.goyal on 9/22/16.
 */
public class JsonClassD implements InterfaceB {

    private String piyush;

    @Override
    public String getPiyush() {
        return piyush;
    }

    @Override
    public void setPiyush(String piyush) {
        this.piyush = piyush;
    }
}
