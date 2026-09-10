package com.liquido.base.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradingModelEnum {

    // Natural day(including holidays)
    D0("D+0", 0),
    D1("D+1", 1),
    D2("D+2", 2),
    D3("D+3", 3),
    D4("D+4", 4),
    D5("D+5", 5),
    D6("D+6", 6),
    D7("D+7", 7),
    D10("D+10", 10),
    D14("D+14", 14),
    D15("D+15", 15),
    D20("D+20", 20),
    D30("D+30", 30),
    D31("D+31", 31),
    D60("D+60", 60),
    D62("D+62", 62),
    D90("D+90", 90),
    D93("D+93", 93),

    // Trading day(excluding holidays)
    T0("T+0", 0),
    T1("T+1", 1),
    T2("T+2", 2),
    T3("T+3", 3),
    T4("T+4", 4),
    T5("T+5", 5),
    T6("T+6", 6),
    T7("T+7", 7),
    T10("T+10", 10),
    T14("T+14", 14),
    T15("T+15", 15),
    T20("T+20", 20),
    T30("T+30", 30),
    T31("T+31", 31),
    T60("T+60", 60),
    T62("T+62", 62),
    T90("T+90", 90),
    T93("T+93", 93),
    ;

    private final String code;
    private final int value;

    public static final List<TradingModelEnum> INSTANT_TRADING =
            List.of(TradingModelEnum.D0, TradingModelEnum.T0);

    public static TradingModelEnum parse(final String code) {
        return Arrays.stream(TradingModelEnum.values())
                .filter(tmp -> tmp.getCode().trim().equals(Optional.ofNullable(code)
                        .map(String::trim).orElse(null))).findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<TradingModelEnum, String> {
        @Override
        public String convertToDatabaseColumn(final TradingModelEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return null;
            }
            return enumValue.getCode();
        }

        @Override
        public TradingModelEnum convertToEntityAttribute(final String dbValue) {
            return TradingModelEnum.parse(dbValue);
        }
    }
}
