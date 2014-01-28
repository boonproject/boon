package org.boon.di;

public class DripCoffeeModule {
    Heater providesHeater() {
        return new ElectricHeater();
    }
}