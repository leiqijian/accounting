package com.liquido.aqueducts.util;


import java.util.Optional;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.vo.document.EventLog;
import com.liquido.aqueducts.vo.document.PayoutBack;
import com.liquido.aqueducts.vo.document.eventlog.PayoutBackEventLog;
import com.liquido.aqueducts.vo.payout.PayeeInfo;
import com.liquido.aqueducts.vo.payout.PaymentInfo;

import org.springframework.util.StringUtils;

public class PayoutEventLogUtil {

    public static String getBankCode(final String bankCode) {
        return Optional.ofNullable(bankCode)
                .map(String::trim)
                .map(e -> e.replaceFirst("^0*", "")) // trim left '0'
                .orElse(null);
    }

    public static String getBankCode(final PayoutBackEventLog lastRecord) {
        return Optional.ofNullable(lastRecord)
                .map(EventLog::getAfter)
                .map(PayoutBack::getBank_code)
                .map(String::trim)
                .map(e -> e.replaceFirst("^0*", "")) // trim left '0'
                .orElse(null);
    }

    public static String getName(
            final PaymentInfo paymentInfo,
            final PayeeInfo payeeInfo,
            final String response
    ) {
        if (paymentInfo.getPix() != null &&
                StringUtils.hasText(paymentInfo.getPix().getFullNameFromPixKey())) {
            return paymentInfo.getPix().getFullNameFromPixKey().trim();
        }

        String name = "", lastName = "";
        if (StringUtils.hasText(response)) {
            try {
                int nameIndex = response.indexOf(", name=");
                int lastNameIndex = response.indexOf(", lastName=");
                name = response.substring(nameIndex + 7, lastNameIndex);
                return name.trim();
            } catch (Exception ignored) {
            }
        }
        name = StringUtils.trimWhitespace(payeeInfo.getTargetName());
        lastName = StringUtils.trimWhitespace(payeeInfo.getTargetLastName());

        if (StringUtils.hasText(name) && StringUtils.hasText(lastName)) {
            String[] nameArr = name.split(" ");
            String[] lastNameArr = lastName.split(" ");
            int i = 0, j = 0;
            while (i < nameArr.length && !lastNameArr[j].equalsIgnoreCase(nameArr[i])) {
                i++;
            }
            if (i >= nameArr.length) {
                return name + " " + lastName;
            } else {
                while (i < nameArr.length && j < lastNameArr.length) {
                    if (!StringUtils.hasText(lastNameArr[j])) {
                        j++;
                    }
                    if (!StringUtils.hasText(nameArr[i])) {
                        i++;
                    }
                    if (lastNameArr[j].equalsIgnoreCase(nameArr[i])) {
                        i++;
                        j++;
                    } else {
                        return name + " " + lastName;
                    }
                }
            }
            return name;
        }
        return StringUtils.hasText(name) ? name : lastName;
    }

    public static String getName(PayoutBackEventLog lastRecord) {

        if (StringUtils.hasText(lastRecord.getAfter().getFull_name_from_pix_key())) {
            return lastRecord.getAfter().getFull_name_from_pix_key().trim();
        }

        String response = lastRecord.getAfter().getResponse();
        String name = "", lastName = "";
        if (StringUtils.hasText(response)) {
            try {
                int nameIndex = response.indexOf(", name=");
                int lastNameIndex = response.indexOf(", lastName=");
                name = response.substring(nameIndex + 7, lastNameIndex);
                return name.trim();
            } catch (Exception ignored) {
            }
        }
        name = lastRecord.getAfter().getTarget_name().trim();
        lastName = lastRecord.getAfter().getTarget_last_name().trim();

        if (StringUtils.hasText(name) && StringUtils.hasText(lastName)) {
            String[] nameArr = name.split(" ");
            String[] lastNameArr = lastName.split(" ");
            int i = 0, j = 0;
            while (i < nameArr.length && !lastNameArr[j].equalsIgnoreCase(nameArr[i])) {
                i++;
            }
            if (i >= nameArr.length) {
                return name + " " + lastName;
            } else {
                while (i < nameArr.length && j < lastNameArr.length) {
                    if (!StringUtils.hasText(lastNameArr[j])) {
                        j++;
                    }
                    if (!StringUtils.hasText(nameArr[i])) {
                        i++;
                    }
                    if (lastNameArr[j].equalsIgnoreCase(nameArr[i])) {
                        i++;
                        j++;
                    } else {
                        return name + " " + lastName;
                    }
                }
            }
            return name;
        }
        return StringUtils.hasText(name) ? name : lastName;
    }

    public static String paymentTypeToProductCode(final String paymentType, final String country) {
        String result = null;

        if ("BankTransfer".equalsIgnoreCase(paymentType)
                || "Bank_Transfer".equalsIgnoreCase(paymentType)) {
            result = ProductCode.BANK_TRANSFER.name();
        }

        if ("Pix".equals(paymentType)) {
            result = ProductCode.PIX.name();
        } else if ("PixQRCode".equals(paymentType)) {
            result = ProductCode.PIX_QR_CODE.name();
        } else if (CountryCode.BR.name().equals(country)) {
            result = ProductCode.TED.name();
        } else if (CountryCode.MX.name().equals(country)) {
            result = ProductCode.SPEI.name();
        } else if (CountryCode.CO.name().equals(country)) {
            result = ProductCode.CO_BANK_TRANSFER.name();
        } else if (CountryCode.CL.name().equals(country)) {
            result = ProductCode.CL_BANK_TRANSFER.name();
        } else if (CountryCode.PE.name().equals(country)
                && "BankTransfer".equalsIgnoreCase(paymentType)) {
            result = ProductCode.PE_BANK_TRANSFER.name();
        } else if (CountryCode.ZA.name().equals(country)
                && "BankTransfer".equalsIgnoreCase(paymentType)) {
            result = ProductCode.ZA_BANK_TRANSFER.name();
        }

        return result;
    }
}
