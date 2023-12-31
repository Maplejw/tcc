package com.igg.boot.framework.rest.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class IggGsonHttpMessageConverters extends AbstractGenericHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


    private Gson gson =
            new GsonBuilder().disableHtmlEscaping().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private String jsonPrefix;


    /**
     * Construct a new {@code GsonHttpMessageConverter}.
     */
    public IggGsonHttpMessageConverters() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        setDefaultCharset(DEFAULT_CHARSET);
    }


    /**
     * Set the {@code Gson} instance to use.
     * If not set, a default {@link Gson#Gson() Gson} instance will be used.
     * <p>Setting a custom-configured {@code Gson} is one way to take further
     * control of the JSON serialization process.
     */
    public void setGson(Gson gson) {
        Assert.notNull(gson, "A Gson instance is required");
        this.gson = gson;
    }

    /**
     * Return the configured {@code Gson} instance for this converter.
     */
    public Gson getGson() {
        return this.gson;
    }

    /**
     * Specify a custom prefix to use for JSON output. Default is none.
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with ")]}', ".
     * Default is {@code false}.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON
     * Hijacking. The prefix renders the string syntactically invalid as a script
     * so that it cannot be hijacked.
     * This prefix should be stripped before parsing the string as JSON.
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? ")]}', " : null);
    }


    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        TypeToken<?> token = getTypeToken(type);
        return readTypeToken(token, inputMessage);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        TypeToken<?> token = getTypeToken(clazz);
        return readTypeToken(token, inputMessage);
    }

    /**
     * Return the Gson {@link TypeToken} for the specified type.
     * <p>The default implementation returns {@code TypeToken.get(type)}, but
     * this can be overridden in subclasses to allow for custom generic
     * collection handling. For instance:
     * <pre class="code">
     * protected TypeToken<?> getTypeToken(Type type) {
     *   if (type instanceof Class && List.class.isAssignableFrom((Class<?>) type)) {
     *     return new TypeToken<ArrayList<MyBean>>() {};
     *   }
     *   else {
     *     return super.getTypeToken(type);
     *   }
     * }
     * </pre>
     * @param type the type for which to return the TypeToken
     * @return the type token
     * @deprecated as of Spring Framework 4.3.8, in favor of signature-based resolution
     */
    @Deprecated
    protected TypeToken<?> getTypeToken(Type type) {
        return TypeToken.get(type);
    }

    private Object readTypeToken(TypeToken<?> token, HttpInputMessage inputMessage) throws IOException {
        Reader json = new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
        return this.gson.fromJson(json, token.getType());
    }

    private Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharset();
    }

    @Override
    protected void writeInternal(Object o, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        Charset charset = getCharset(outputMessage.getHeaders());
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), charset);
        try {
            if (this.jsonPrefix != null) {
                writer.append(this.jsonPrefix);
            }

            // In Gson, toJson with a type argument will exclusively use that given type,
            // ignoring the actual type of the object... which might be more specific,
            // e.g. a subclass of the specified type which includes additional fields.
            // As a consequence, we're only passing in parameterized type declarations
            // which might contain extra generics that the object instance doesn't retain.
            if (type instanceof ParameterizedType) {
                this.gson.toJson(o, type, writer);
            }
            else {
                this.gson.toJson(o, writer);
            }

            writer.flush();
        }
        catch (JsonIOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

}