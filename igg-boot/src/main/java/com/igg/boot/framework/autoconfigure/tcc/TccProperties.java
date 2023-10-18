package com.igg.boot.framework.autoconfigure.tcc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "igg.tcc")
@Data
public class TccProperties {
    private boolean enable;
    private String host;
    private String path = "getTransactionNo";
    private String notifyPath = "notifyPath";
}
