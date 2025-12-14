package com.example.jsso.dto;

import com.example.jsso.utils.JsonInstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenInfoDto {

    private Boolean active;
    private String sub;
    private List<String> aud;
    @JsonSerialize(using = JsonInstantSerializer.class)
    private Instant nbf;
    private List<String> scopes;
    private URL iss;
    @JsonSerialize(using = JsonInstantSerializer.class)
    private Instant exp;
    @JsonSerialize(using = JsonInstantSerializer.class)
    private Instant iat;
    private String jti;
    private String clientId;
    private String tokenType;

    private IntrospectionPrincipal principal;
}
