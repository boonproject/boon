package org.boon.di;

public class CoffeeMaker extends BaseObject {

    @Inject
    Heater heater; // Don't want to create a possibly costly heater until we need it.
    @Inject
    Pump pump;

    public void brew() {
        heater.on();
        pump.pump();
        System.out.println( " [_]P coffee! [_]P " );
        heater.off();
    }
}
