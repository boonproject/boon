package org.boon.json;


import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;



/**
 * This mapper (or, data binder, or codec) provides functionality for
 * converting between Java objects (instances of JDK provided core classes,
 * beans), and matching JSON constructs.
 * It will use instances of {@link org.boon.json.JsonParser} and {@link org.boon.json.JsonSerializer}
 * for implementing actual reading/writing of JSON.
 *<p>
 */
public interface ObjectMapper {

    /**
     * Method to deserialize JSON content into a non-container
     * type typically a bean or wrapper type.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link java.util.Collection} or {@link java.util.Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     */
    <T> T readValue(String src, Class<T> valueType);

    /**
     * Method to deserialize JSON content into a non-container
     * type typically a bean or wrapper type.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link java.util.Collection} or {@link java.util.Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     */
    <T> T readValue(File src, Class<T> valueType);


    /**
     * Method to deserialize JSON content into a non-container
     * type typically a bean or wrapper type.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link java.util.Collection} or {@link java.util.Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     */
    <T> T readValue(byte[] src, Class<T> valueType);


    /**
     * Method to deserialize JSON content into a non-container
     * type typically a bean or wrapper type.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link java.util.Collection} or {@link java.util.Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     */
    <T> T readValue(char[] src, Class<T> valueType);


    /**
     * Method to deserialize JSON content into a non-container
     * type typically a bean or wrapper type.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link java.util.Collection} or {@link java.util.Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     */
    <T> T readValue(Reader src, Class<T> valueType);


    /**
     * Method to deserialize JSON content into a non-container
     * type typically a bean or wrapper type.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link java.util.Collection} or {@link java.util.Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     */
    <T> T readValue(InputStream src, Class<T> valueType);



    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(String src, Class<T> valueType, Class<C> componentType);
    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(File src, Class<T> valueType, Class<C> componentType);
    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(byte[] src, Class<T> valueType, Class<C> componentType);
    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(char[] src, Class<T> valueType, Class<C> componentType);
    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(Reader src, Class<T> valueType, Class<C> componentType);
    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(InputStream src, Class<T> valueType, Class<C> componentType);

    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(byte[] src, Charset charset, Class<T> valueType, Class<C> componentType);

    /**
     * Method to deserialize JSON content into a container like Set or List.
     *<p>
     * Note: this method should  be used if the result type is a
     * container ({@link java.util.Collection}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected without using this method.
     */
    <T extends Collection<C>, C> T readValue(InputStream src, Charset charset, Class<T> valueType, Class<C> componentType);

    /**
     * Method that can be used to serialize any Java value as
     * JSON output, written to File provided.
     */
    void writeValue(File dest, Object value);

    /**
     * Method that can be used to serialize any Java value as
     * JSON output, using output stream provided (using encoding
     * UTF8.
     *<p>
     * Note: method does not close the underlying stream explicitly
     * here.
     */
    public void writeValue(OutputStream dest, Object value);


    /**
     * Method that can be used to serialize any Java value as
     * JSON output, using Writer provided.
     *<p>
     * Note: method does not close the underlying stream explicitly
     * here.
     */
    public void writeValue(Writer dest, Object value);


    /**
     * Method that can be used to serialize any Java value as
     * a String. Functionally equivalent to calling
     * {@link #writeValue(Writer,Object)} with {@link java.io.StringWriter}
     * and constructing String, but more efficient.
     *<p>
     */
    public String writeValueAsString(Object value);


    /**
     * Method that can be used to serialize any Java value as
     * a char[]. Functionally equivalent to calling
     * {@link #writeValue(Writer,Object)} with {@link java.io.StringWriter}
     * and constructing String, but more efficient.
     *<p>
     */
    public char[] writeValueAsCharArray(Object value);

    /**
     * Method that can be used to serialize any Java value as
     * a byte array.
     * Encoding used will be UTF-8.
     */
    public byte[] writeValueAsBytes(Object value);

    /**
     * Method that can be used to serialize any Java value as
     * a byte array.
     */
    public byte[] writeValueAsBytes(Object value, Charset charset);




    public JsonParser parser();

    public JsonSerializer serializer();


}
