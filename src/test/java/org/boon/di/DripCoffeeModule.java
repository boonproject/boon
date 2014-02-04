package org.boon.di;

public class DripCoffeeModule {
    Heater providesHeater() {
        return new ElectricHeater();
    }


    @Named( "black" )
    Coffee providesBlackCoffee() {
        return new Coffee();
    }
}