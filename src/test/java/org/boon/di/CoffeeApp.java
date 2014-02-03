package org.boon.di;


import org.boon.core.Supplier;
import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.di.SupplierInfo.supplierOf;

public class CoffeeApp implements Runnable {
    @Inject CoffeeMaker coffeeMaker;
    @Inject Coffee coffee;
    @Inject Sugar sugar;
    @Inject Bacon bacon;

    @Override public void run() {
        coffeeMaker.brew();
    }

    @Test
    public void test() {
        CoffeeApp.main (  );
    }

    static Sugar staticSugar = new Sugar();//singleton ish


    static Bacon prototypeBacon = new Bacon();//prototype
    static {
        prototypeBacon.crispy = true;
    }

    public static void main(String... args) {
        Module m1 = ContextFactory.classes ( CoffeeApp.class, CoffeeMaker.class );
        Module m2 = ContextFactory.module ( new DripCoffeeModule () );
        Module m3 = ContextFactory.module ( new PumpModule() );
        Module m4 = ContextFactory.suppliers( supplierOf(Coffee.class, new Supplier<Coffee>() {
            @Override
            public Coffee get() {
                return new Coffee();
            }
        }) );

        Module m5 = ContextFactory.objects( staticSugar );
        Module m6 = ContextFactory.prototypes( prototypeBacon );

        Context context  = ContextFactory.context ( m1, m2, m3, m4, m5, m6 );
        Heater heater = context.get ( Heater.class );
        boolean ok = heater instanceof ElectricHeater || die();
        CoffeeApp coffeeApp = context.get(CoffeeApp.class);
        coffeeApp.run();

        ok = coffeeApp.coffee != null || die();

        ok = coffeeApp.sugar == staticSugar || die();


        ok = coffeeApp.bacon != prototypeBacon || die();


        ok = coffeeApp.bacon.crispy  || die();
    }


}