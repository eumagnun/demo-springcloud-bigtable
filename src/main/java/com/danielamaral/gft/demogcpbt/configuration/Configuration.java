package com.danielamaral.gft.demogcpbt.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter @NoArgsConstructor @ToString
@ConfigurationProperties("custom-config")
@Component
public class Configuration {
    private String message1;
    private String btProjectId;
    private String btInstanceId;
    private String btTable;
    private String btFamilyName;


}
