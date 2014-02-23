package org.boon.di;

import static org.boon.Boon.puts;

@Named("american")
public class FrenchFries extends FoodImpl implements Food {
    @PostConstruct
    private void init() {
        puts ("FRENCH FRIES ARE TASTY");
    }
}
