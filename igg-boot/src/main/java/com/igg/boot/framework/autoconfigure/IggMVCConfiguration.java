package com.igg.boot.framework.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.igg.boot.framework.rest.api.*;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(RestConfiguration.class)
public class IggMVCConfiguration implements WebMvcConfigurer {
    private final List<WebMvcConfigurer> delegates = new ArrayList<WebMvcConfigurer>();
    @Autowired
    private RestConfiguration restConfiguration;

    @Bean
    public ResponseMethodBodyHandler responseMethodBody() {
        return new ResponseMethodBodyHandler(fetchMessageConverter());
    }

    @Bean
    public ApplicationContextHolder getApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new ResponseMethodBodyHandler(fetchMessageConverter()));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, fetchMessageConverter());
        for (WebMvcConfigurer delegate : this.delegates) {
            delegate.configureMessageConverters(converters);
        }
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new IggHandleException(fetchMessageConverter()));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new IggHandlerMethodArgumentResolver());
    }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilterRegistration() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter());
        registration.addUrlPatterns("/*");
        registration.setName("logFilter");
        registration.setOrder(1);

        return registration;
    }

    private HttpMessageConverter<?> fetchMessageConverter() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(1);

        ClassLoader classLoader = IggMVCConfiguration.class.getClassLoader();
        boolean gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        if (gsonPresent) {
            messageConverters.add(new IggGsonHttpMessageConverters());
        } else {
            messageConverters.add(mappingJackson2HttpMessageConverter());
        }

        return messageConverters.get(0);
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        return objectMapper;
    }
    
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Bean
    @ConditionalOnMissingBean(ErrorController.class)
    public BasicErrorDefaultController basicErrorController(ErrorAttributes errorAttributes) {
        return new BasicErrorDefaultController(errorAttributes);
    }

    @Bean("error")
    @ConditionalOnMissingBean(name = "error")
    public View getDefaultView() {
        return new DefaultErrorResourceView();
    }

}
