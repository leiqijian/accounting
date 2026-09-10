package com.liquido.core.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class LocalDateTimeUtil {

    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    public static final DateTimeFormatter FORMAT_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter FORMAT_DATETIME_Z =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    /**
     * Get UTC LocalDateTime of now
     *
     * @return LocalDateTime
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(UTC_ZONE);
    }

    /**
     * Get UTC ZonedDateTime of now
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime nowUtcZonedDateTime() {
        return LocalDateTime.now(UTC_ZONE).atZone(UTC_ZONE);
    }

    /**
     * Get Local LocalDateTime of now
     *
     * @param localZoneId zoneId
     * @return LocalDateTime
     */
    public static LocalDateTime nowUtcToLocal(final ZoneId localZoneId) {
        return utcToLocal(LocalDateTime.now(UTC_ZONE), localZoneId);
    }

    /**
     * Get Local LocalDateTime of now
     *
     * @param localZoneId zoneId
     * @return LocalDateTime
     */
    public static LocalDateTime nowUtcToLocal(final String localZoneId) {
        return utcToLocal(LocalDateTime.now(UTC_ZONE), localZoneId);
    }

    /**
     * Convert instant to UTC LocalDateTime
     *
     * @param instant instant
     * @return LocalDateTime
     */
    public static LocalDateTime instantToUtc(final Long instant) {
        return instantToUtc(Instant.ofEpochSecond(instant));
    }

    /**
     * Convert instant to UTC LocalDateTime
     *
     * @param instant instant
     * @return LocalDateTime
     */
    public static LocalDateTime instantToUtc(final Instant instant) {
        return instant.atZone(UTC_ZONE).toLocalDateTime();
    }

    /**
     * Convert instant to Local LocalDateTime
     *
     * @param instant     instant
     * @param localZoneId zoneId of local
     * @return LocalDateTime
     */
    public static LocalDateTime instantToLocal(final Long instant,
                                               final String localZoneId) {
        return utcToLocal(instantToUtc(Instant.ofEpochSecond(instant)), localZoneId);
    }

    /**
     * Convert UTC LocalDateTime to second Instant
     *
     * @param dateTime UTC LocalDateTime
     * @return Long
     */
    public static Long utcToInstant(final LocalDateTime dateTime) {
        return Objects.isNull(dateTime) ? null : dateTime.atZone(UTC_ZONE).toEpochSecond();
    }

    /**
     * Convert UTC LocalDateTime to millisecond Instant
     *
     * @param dateTime UTC LocalDateTime
     * @return Long
     */
    public static Long utcToInstantMilli(final LocalDateTime dateTime) {
        return Objects.isNull(dateTime) ? null :
                dateTime.atZone(UTC_ZONE).toInstant().toEpochMilli();
    }

    /**
     * Convert Local LocalDateTime to UTC LocalDateTime
     *
     * @param local       Local LocalDateTime
     * @param localZoneId zoneId
     * @return LocalDateTime
     */
    public static LocalDateTime localToUtc(final LocalDateTime local,
                                           final ZoneId localZoneId) {
        return zoneTransfer(local, localZoneId, UTC_ZONE);
    }

    /**
     * Convert Local LocalDateTime to UTC LocalDateTime
     *
     * @param local       Local LocalDateTime
     * @param localZoneId zoneId
     * @return LocalDateTime
     */
    public static LocalDateTime localToUtc(final LocalDateTime local,
                                           final String localZoneId) {
        return zoneTransfer(local, ZoneId.of(localZoneId), UTC_ZONE);
    }

    /**
     * Convert UTC LocalDateTime to Local LocalDateTime
     *
     * @param utc         UTC LocalDateTime
     * @param localZoneId zoneId
     * @return LocalDateTime
     */
    public static LocalDateTime utcToLocal(final LocalDateTime utc,
                                           final ZoneId localZoneId) {
        return zoneTransfer(utc, UTC_ZONE, localZoneId);
    }

    /**
     * Convert UTC LocalDateTime to Local LocalDateTime
     *
     * @param utc         UTC LocalDateTime
     * @param localZoneId zoneId
     * @return LocalDateTime
     */
    public static LocalDateTime utcToLocal(final LocalDateTime utc,
                                           final String localZoneId) {
        return zoneTransfer(utc, UTC_ZONE, ZoneId.of(localZoneId));
    }

    /**
     * Make LocalDateTime from ZoneId to other ZoneId
     *
     * @param dateTime LocalDateTime
     * @param from     from ZoneId
     * @param to       to ZoneId
     * @return LocalDateTime
     */
    public static LocalDateTime zoneTransfer(final LocalDateTime dateTime,
                                             final ZoneId from,
                                             final ZoneId to) {
        return dateTime.atZone(from).withZoneSameInstant(to).toLocalDateTime();
    }

    /**
     * Make LocalDateTime from ZoneId to other ZoneId
     *
     * @param dateTime LocalDateTime
     * @param from     from ZoneId
     * @param to       to ZoneId
     * @return LocalDateTime
     */
    public static LocalDateTime zoneTransfer(final LocalDateTime dateTime,
                                             final String from,
                                             final String to) {
        return dateTime.atZone(ZoneId.of(from)).withZoneSameInstant(ZoneId.of(to))
                .toLocalDateTime();
    }

    /**
     * Date time of string format to LocalDateTime
     *
     * @param dateTimeStr Date time of string
     * @return LocalDateTime
     */
    public static LocalDateTime formatToLocalDateTime(final String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, FORMAT_DATETIME);
    }

    /**
     * Date time of string format to LocalDateTime
     *
     * @param dateTimeStr Date time of string
     * @param formatter   DateTimeFormatter formatter
     * @return LocalDateTime
     */
    public static LocalDateTime formatToLocalDateTime(final String dateTimeStr,
                                                      final DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * Get now of UTC LocalDateTime's String view
     *
     * @return String
     */
    public static String nowUtcStr() {
        return nowUtc().format(FORMAT_DATETIME);
    }

}
