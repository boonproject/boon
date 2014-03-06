package org.boon.di;


import org.boon.core.Supplier;
import org.junit.Test;

import static org.boon.Exceptions.die;

//@Named ("/coffeeApp")
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


//    @Inject
//    @Named( "rick's habit" )
//    Food rickDrinks;
//
//    @In( "rick's habit")
//    Food rickDrinks2;

    @Inject
    @Named( "rick's habit" )
    Coffee rickCoffee;


    @Inject
    @Named( "black" )
    Coffee blackCoffee;



    @Inject
    @Named( "blue" )
    Supplier<Bacon> blueBaconSupplier;


    //Todo this works but I need a real unit test.
//    @Inject
//    @Named( "this is not found" )
//    @Required
//    Coffee notFound;

//    @In("more stuff not found")
//    Coffee notFound2;

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

    static Module m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12;

    public static void createModules() {
         m1 = DependencyInjection.classes(CoffeeApp.class, CoffeeMaker.class, FoodImpl.class);
         m2 = DependencyInjection.module(new DripCoffeeModule());
         m3 = DependencyInjection.module(new PumpModule());
         m4 = DependencyInjection.suppliers(ProviderInfo.providerOf(Coffee.class, new Supplier<Coffee>() {
             @Override
             public Coffee get() {
                 return new Coffee();
             }
         }));

         m5 = DependencyInjection.objects(staticSugar);
         m6 = DependencyInjection.prototypes(prototypeBacon);

         m7 = DependencyInjection.suppliers(
                 ProviderInfo.providerOf(Bacon.class),
                 ProviderInfo.providerOf("orange", Bacon.class),
                 ProviderInfo.providerOf("red", new Bacon()),
                 ProviderInfo.providerOf("brown", Bacon.class, new Supplier<Bacon>() {
                     @Override
                     public Bacon get() {
                         Bacon bacon = new Bacon();
                         bacon.tag = "m7";
                         return bacon;
                     }
                 }));


         m0 = DependencyInjection.suppliers(ProviderInfo.providerOf("blue", new Supplier<Bacon>() {
             @Override
             public Bacon get() {
                 Bacon bacon = new Bacon();
                 bacon.tag = "m7";
                 return bacon;
             }
         }));


         m8 = DependencyInjection.classes(Cheese.class);
         m9 = DependencyInjection.objects(new FrenchFries());

         m10 = DependencyInjection.suppliers(ProviderInfo.providerOf("new york", new Hotdogs()));


         m11 = DependencyInjection.suppliers(ProviderInfo.providerOf("rick's habit", Coffee.class));

    }


    public static void main( String... args ) {
        createModules();
        Context context = DependencyInjection.context(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m0);
        Heater heater = context.get( Heater.class );
        boolean ok = heater instanceof ElectricHeater || die();
        CoffeeApp coffeeApp = context.get( CoffeeApp.class );
        coffeeApp.run();

        validateApp( coffeeApp );

        createModules();
        context = DependencyInjection.context(m0, m8, m9, m10, m11, m1, m2, m3, m4, m5, m6, m7);
        coffeeApp = context.get( CoffeeApp.class );
        validateApp( coffeeApp );

        Bacon blueBacon = context.get( Bacon.class, "blue" );
        ok = blueBacon != null || die();


        ok = context.has( Coffee.class ) || die();

        ok = context.has( "black" ) || die();

        ok = context.get( "black" ) != null || die();
        ok = context.get( "electricHeater" ) != null || die();

        ok = context.get( "foodImpl" ) != null || die();





        context.remove( m0 );
        context.add( m11 );
        context.addFirst( m0 );

        coffeeApp = context.get( CoffeeApp.class );
        validateApp( coffeeApp );

        context.get(FrenchFries.class);
        context.get(FrenchFries.class);
        context.get(FrenchFries.class);
        context.get(FrenchFries.class);
        context.get(FrenchFries.class, "american");

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

//
//        ok = coffeeApp.rickDrinks != null || die();
//
//        ok = ( coffeeApp.rickDrinks instanceof Coffee ) || die( coffeeApp.rickDrinks.toString() );
//
//
//
//        ok = coffeeApp.rickDrinks2 != null || die();
//
//        ok = ( coffeeApp.rickDrinks2 instanceof Coffee ) || die( coffeeApp.rickDrinks.toString() );

        ok = coffeeApp.rickCoffee != null || die();

        ok = ( coffeeApp.rickCoffee instanceof Coffee ) || die( coffeeApp.rickCoffee.toString() );


        ok = coffeeApp.blackCoffee != null || die();

        ok = ( coffeeApp.blackCoffee instanceof Coffee ) || die( coffeeApp.blackCoffee.toString() );



        ok = coffeeApp.blueBaconSupplier != null || die();

        ok = ( coffeeApp.blueBaconSupplier.get() instanceof Bacon ) || die( coffeeApp.blueBaconSupplier.get().toString() );
    }


}