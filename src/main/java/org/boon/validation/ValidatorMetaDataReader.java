package org.boon.validation;

import java.util.List;

/**
 * ValidatorMetaDataReader is an extension point for classes that need
 * to read validation meta-data.
 * <p/>
 * There are currently two implementations (planned) for this.
 * <p/>
 * One implementation reads the meta-data from a properties file.
 * The other implementation reads the data from Java 5 Annotation.
 *
 * @author Rick Hightower
 */
public interface ValidatorMetaDataReader {

    public List<ValidatorMetaData> readMetaData( Class<?> clazz, String propertyName );

}