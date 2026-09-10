package com.liquido.worker;

import java.io.File;
import java.util.List;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class JustTest {
    /*
        me.CODE,
        account_id,
        merchant_id,
        min( id ) as min_id,
        max( id ) as max_id,
        min( created_time ) as begin_time,
        max( created_time )as end_time,
        count( id ) AS total
     */
    @SneakyThrows
    public static void main(String[] args) {
        List<String> lines =
                FileUtils.readLines(new File("/Users/qizhaoqin/Downloads/result-001.csv"));

        String sql = "update service_accounting_statement.transaction_in_progress "
                + "set status= 'COMPLETED' "
                + "where id >= %s and id <= %s "
                + "and account_id = %s and transaction_status='SETTLED' "
                + "and created_time <= '2025-01-01 00:00:00';";
        for (String line : lines) {
            String[] fields = StringUtils.split(line, ",");
            System.out.println(String.format("-- %s: %s", fields[0], fields[7]));
            System.out.println(String.format(sql, fields[3], fields[4], fields[1]));

        }
    }
}
