package com.liquido.aqueducts.vo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventLogSource {
    private String version;
    private String connector;
    private String name;
    private long ts_ms;
    private String snapshot;
    private String db;
    private String sequence;
    private String table;
    private String server_id;
    private String gtid;
    private String file;
    private String pos;
    private String row;
    private String thread;
    private String query;
}
