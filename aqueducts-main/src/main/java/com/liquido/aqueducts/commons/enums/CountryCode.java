package com.liquido.aqueducts.commons.enums;


public enum CountryCode {
    US,
    MX,
    BR,
    CL,
    PE,
    ZA,
    CO,
    AR,
    BO,
    CR,
    DO,
    SV,
    EC,
    GT,
    HN,
    NI,
    PA,
    PY,
    UY,
    ;

    public static CountryCode fromString(final String countryCode) {
        if (countryCode == null) {
            return null;
        }
        for (CountryCode code : values()) {
            if (code.name().equals(countryCode.toUpperCase())) {
                return code;
            }
        }
        return null;
    }

    public static boolean isValid(String countryCode) {
        for (CountryCode code : values()) {
            if (code.name().equals(countryCode.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

}
