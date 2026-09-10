package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwAuthVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @JsonProperty("grant_type")
    private String grantType;

    @NotBlank
    @JsonProperty("client_id")
    private String clientId;

    @NotBlank
    @JsonProperty("client_secret")
    private String clientSecret;

}
