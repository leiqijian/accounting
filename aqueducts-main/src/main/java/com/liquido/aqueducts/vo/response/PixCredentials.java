package com.liquido.aqueducts.vo.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixCredentials {

    private String date;
    private String name;
    private Long amount;
    private String documentId;
    private String description;
    private String pixId;

}
