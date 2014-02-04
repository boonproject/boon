package org.boon.di;

public class PumpModule {
    Pump providesPump() {
        return new Pump() {
            @Override
            public void pump() {

            }
        };
    }

}
