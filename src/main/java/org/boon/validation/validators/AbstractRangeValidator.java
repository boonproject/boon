package org.boon.validation.validators;


/**
 * Abstract class for range validation.
 */
@SuppressWarnings ( "unchecked" )
public abstract class AbstractRangeValidator extends BaseValidator {

    /* The min value for comparison. */
    private Comparable realMin;
    /* The max value for comparison. */
    private Comparable realMax;
    /* Has this been initialized? */
    private boolean isInitialized;

    /* Allows this base class to be initialized by the subclasses. */
    protected void init( Comparable min, Comparable max ) {
        this.realMin = min;
        this.realMax = max;
        assert min.compareTo( max ) < 0;
        isInitialized = true;
    }

    /* Checks to see if the value is less than the min. */
    protected boolean isValueGreaterThanMin( Comparable value ) {
        if ( realMin == null ) {
            return true;
        }
        return value.compareTo( realMin ) >= 0;
    }

    /* Checks to see if the value is greater than the max. */
    protected boolean isValueLessThanMax( Comparable value ) {
        if ( realMax == null ) {
            return true;
        }
        return value.compareTo( realMax ) <= 0;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
