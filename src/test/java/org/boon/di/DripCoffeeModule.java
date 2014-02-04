package org.boon.di;

public class DripCoffeeModule {

    ElectricHeater providesHeater() {
        return new ElectricHeater();
    }


    @Named( "black" )
    Coffee providesBlackCoffee() {
        return new Coffee();
    }
}