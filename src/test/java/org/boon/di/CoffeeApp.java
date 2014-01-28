package org.boon.di;


import org.junit.Test;

import static org.boon.Exceptions.die;

public class CoffeeApp implements Runnable {
    @Inject CoffeeMaker coffeeMaker;

    @Override public void run() {
        coffeeMaker.brew();
    }

    @Test
    public void test() {
        CoffeeApp.main (  );
    }

    public static void main(String... args) {
        Module m1 = ContextFactory.classes ( CoffeeApp.class, CoffeeMaker.class );
        Module m2 = ContextFactory.module ( new DripCoffeeModule () );
        Module m3 = ContextFactory.module ( new PumpModule() );

        Context context  = ContextFactory.context ( m1, m2, m3 );
        Heater heater = context.get ( Heater.class );
        boolean ok = heater instanceof ElectricHeater || die();
        CoffeeApp coffeeApp = context.get(CoffeeApp.class);
        coffeeApp.run();
    }


}