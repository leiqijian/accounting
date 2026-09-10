package com.liquido.worker.pojo.dto.tokenization;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwSyncPayerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String email;


}
