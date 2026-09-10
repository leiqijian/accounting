package com.liquido.aqueducts.vo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventLog<T> {
    private Object _id;
    private T before;
    private T after;
    private EventLogSource source;
    private String op;
    private Long ts_ms;
    private Object transaction;
}
