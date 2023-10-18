package com.igg.boot.framework.autoconfigure.tcc.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@KafkaListener
public @interface DoTccAction {
    @AliasFor(annotation = KafkaListener.class)
    String id() default "";

    @AliasFor(annotation = KafkaListener.class)
    String topicPattern() default "";

    @AliasFor(annotation = KafkaListener.class)
    String containerFactory() default "";

    @AliasFor(annotation = KafkaListener.class)
    TopicPartition[] topicPartitions() default {};

    @AliasFor(annotation = KafkaListener.class)
    String containerGroup() default "";

    @AliasFor(annotation = KafkaListener.class)
    String errorHandler() default "";

    @AliasFor(annotation = KafkaListener.class)
    boolean idIsGroup() default true;

    @AliasFor(annotation = KafkaListener.class)
    String clientIdPrefix() default "";

    @AliasFor(annotation = KafkaListener.class)
    String groupId() default "";

    @AliasFor(annotation = KafkaListener.class)
    String[] topics() default {};

    @AliasFor(annotation = KafkaListener.class)
    String beanRef() default "__listener";
}
