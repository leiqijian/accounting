package com.liquido.worker.common.utils;

import java.io.InputStream;
import java.net.InetAddress;

import com.liquido.worker.WorkerApplication;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import lombok.SneakyThrows;

@SuppressWarnings("PMD.UseUtilityClass")
public class IpUtils {

    @SneakyThrows
    public static String reverseLookupAddress(final String ip) {
        try (InputStream database = WorkerApplication.class.getClassLoader()
                .getResourceAsStream("appendix/GeoLite2-Country.mmdb");
             DatabaseReader reader = new DatabaseReader.Builder(database)
                     .withCache(new CHMCache()).build()) {

            final InetAddress ipAddress = InetAddress.getByName(ip);

            final CountryResponse response = reader.country(ipAddress);

            final Country country = response.getCountry();

            return country.getIsoCode();
        }
    }

}
