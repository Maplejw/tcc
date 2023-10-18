package igg.tcc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "igg.tcc.center")
@Data
@Component
public class TccCenterTransactionProperties {
    private int delyTime = 10;
}
