package com.liquido.statement.pojo.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDiagnostic implements Serializable {
    private static final long serialVersionUID = 1L;

    private long timestamp;

    private int count;
}
