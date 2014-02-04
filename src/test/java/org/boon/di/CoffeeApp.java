package org.boon.di;


import org.boon.core.Supplier;
import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.di.SupplierInfo.supplierOf;

public class CoffeeApp implements Runnable {
    @Inject
    CoffeeMaker coffeeMaker;
    @Inject
    Coffee coffee;
    @Inject
    Sugar sugar;
    @Inject
    Bacon bacon;
    @Inject
    @Named( "brown" )
    Bacon brownBacon;
    @Inject
    @Named( "french" )
    Food frenchFood;
    @Inject
    @Named( "american" )
    Food americanFood;

    @Inject
    @Named( "new york" )
    Food newYorkFood;


    @Inject
    @Named( "rick's habit" )
    Food rickDrinks;


    @Inject
    @Named( "rick's habit" )
    Coffee rickCoffee;


    @Inject
    @Named( "black" )
    Coffee blackCoffee;

    boolean started = false;

    @PostConstruct
    void init() {
        started = true;
    }


    @Override
    public void run() {
        coffeeMaker.brew();
    }

    @Test
    public void test() {
        CoffeeApp.main();
    }

    static Sugar staticSugar = new Sugar();//singleton ish


    static Bacon prototypeBacon = new Bacon();//prototype

    static {
        prototypeBacon.crispy = true;
    }

    public static void main( String... args ) {
        Module m1 = ContextFactory.classes( CoffeeApp.class, CoffeeMaker.class );
        Module m2 = ContextFactory.module( new DripCoffeeModule() );
        Module m3 = ContextFactory.module( new PumpModule() );
        Module m4 = ContextFactory.suppliers( supplierOf( Coffee.class, new Supplier<Coffee>() {
            @Override
            public Coffee get() {
                return new Coffee();
            }
        } ) );

        Module m5 = ContextFactory.objects( staticSugar );
        Module m6 = ContextFactory.prototypes( prototypeBacon );

        Module m7 = ContextFactory.suppliers(
                supplierOf( Bacon.class ),
                supplierOf( "orange", Bacon.class ),
                supplierOf( "red", new Bacon() ),
                supplierOf( "brown", Bacon.class, new Supplier<Bacon>() {
                    @Override
                    public Bacon get() {
                        Bacon bacon = new Bacon();
                        bacon.tag = "m7";
                        return bacon;
                    }
                } ) );


        Module m0 = ContextFactory.suppliers( supplierOf( "blue", new Supplier<Bacon>() {
            @Override
            public Bacon get() {
                Bacon bacon = new Bacon();
                bacon.tag = "m7";
                return bacon;
            }
        } ) );


        Module m8 = ContextFactory.classes( Cheese.class );
        Module m9 = ContextFactory.objects( new FrenchFries() );

        Module m10 = ContextFactory.objects( supplierOf( "new york", new Hotdogs() ) );


        Module m11 = ContextFactory.classes( supplierOf( "rick's habit", Coffee.class ) );

        Context context = ContextFactory.context( m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11 );
        Heater heater = context.get( Heater.class );
        boolean ok = heater instanceof ElectricHeater || die();
        CoffeeApp coffeeApp = context.get( CoffeeApp.class );
        coffeeApp.run();

        validateApp( coffeeApp );


        context = ContextFactory.context( m0, m8, m9, m10, m11, m1, m2, m3, m4, m5, m6, m7 );
        coffeeApp = context.get( CoffeeApp.class );
        validateApp( coffeeApp );

        Bacon blueBacon = context.get( Bacon.class, "blue" );
        ok = blueBacon != null || die();


        ok = context.has( Coffee.class ) || die();

        ok = context.has( "black" ) || die();

        ok = context.get( "black" ) != null || die();

        context.remove( m0 );
        context.add( m11 );
        context.addFirst( m0 );
        coffeeApp = context.get( CoffeeApp.class );
        validateApp( coffeeApp );

    }

    private static void validateApp( CoffeeApp coffeeApp ) {
        boolean ok;


        ok = coffeeApp.started || die();

        ok = coffeeApp.coffee != null || die();

        ok = coffeeApp.sugar == staticSugar || die();


        ok = coffeeApp.bacon != prototypeBacon || die();


        ok = coffeeApp.bacon.crispy || die();


        ok = coffeeApp.brownBacon != null || die();


        ok = coffeeApp.frenchFood != null || die();

        ok = coffeeApp.frenchFood instanceof Cheese || die( coffeeApp.frenchFood.toString() );


        ok = coffeeApp.americanFood != null || die();

        ok = coffeeApp.americanFood instanceof FrenchFries || die( coffeeApp.americanFood.toString() );


        ok = coffeeApp.newYorkFood != null || die();

        ok = coffeeApp.newYorkFood instanceof Hotdogs || die( coffeeApp.newYorkFood.toString() );


        ok = coffeeApp.rickDrinks == null || die();

        ok = !( coffeeApp.rickDrinks instanceof Coffee ) || die( coffeeApp.rickDrinks.toString() );


        ok = coffeeApp.rickCoffee != null || die();

        ok = ( coffeeApp.rickCoffee instanceof Coffee ) || die( coffeeApp.rickCoffee.toString() );


        ok = coffeeApp.blackCoffee != null || die();

        ok = ( coffeeApp.blackCoffee instanceof Coffee ) || die( coffeeApp.blackCoffee.toString() );
    }


}