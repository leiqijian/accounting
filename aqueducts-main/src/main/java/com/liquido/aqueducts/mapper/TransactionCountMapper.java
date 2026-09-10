package com.liquido.aqueducts.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.PaymentMethodCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.util.PayinEventLogUtil;
import com.liquido.aqueducts.util.PayoutEventLogUtil;
import com.liquido.aqueducts.vo.document.count.MarketPlaceCountItem;
import com.liquido.aqueducts.vo.document.count.PayinCountItem;
import com.liquido.aqueducts.vo.document.count.PayoutCountItem;
import com.liquido.aqueducts.vo.document.count.SubAccountCountItem;
import com.liquido.aqueducts.vo.document.count.TransactionCountGroupId;
import com.liquido.aqueducts.vo.response.TransactionCountItem;

public class TransactionCountMapper {

    public static List<TransactionCountItem> payinListToItem(
            List<PayinCountItem> payinTotalItems,
            List<PayinCountItem> payinSuccessItems) {
        List<TransactionCountItem> res = new ArrayList<>();
        Map<String, TransactionCountItem> map = new HashMap<>();
        for (PayinCountItem payinCountItem : payinTotalItems) {
            TransactionCountGroupId id = payinCountItem.get_id();
            String product =
                    PayinEventLogUtil.paymentMethodToProductCode(id.getProduct(), id.getCountry());
            String key = id.getMerchant() + "-" + id.getCountry() + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.PAY_IN.name())
                                .countryCode(id.getCountry())
                                .merchantCode(id.getMerchant())
                                .product(product)
                                .build();

                int totalCount = newTransactionCountItem.getTotalCount();
                totalCount += payinCountItem.getCount();
                newTransactionCountItem.setTotalCount(totalCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int totalCount = oldTransactionCountItem.getTotalCount();
                totalCount += payinCountItem.getCount();
                oldTransactionCountItem.setTotalCount(totalCount);
            }
        }

        for (PayinCountItem payinCountItem : payinSuccessItems) {
            TransactionCountGroupId id = payinCountItem.get_id();
            String product =
                    PayinEventLogUtil.paymentMethodToProductCode(id.getProduct(), id.getCountry());
            String key = id.getMerchant() + "-" + id.getCountry() + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.PAY_IN.name())
                                .countryCode(id.getCountry())
                                .merchantCode(id.getMerchant())
                                .product(product)
                                .build();

                int successCount = newTransactionCountItem.getSuccessCount();
                successCount += payinCountItem.getCount();
                newTransactionCountItem.setSuccessCount(successCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int successCount = oldTransactionCountItem.getSuccessCount();
                successCount += payinCountItem.getCount();
                oldTransactionCountItem.setSuccessCount(successCount);
            }
        }

        return res;

    }

    public static List<TransactionCountItem> payoutListToItem(
            List<PayoutCountItem> payoutTotalItems,
            List<PayoutCountItem> payoutSuccessItems) {
        List<TransactionCountItem> res = new ArrayList<>();
        Map<String, TransactionCountItem> map = new HashMap<>();
        for (PayoutCountItem payoutCountItem : payoutTotalItems) {
            TransactionCountGroupId id = payoutCountItem.get_id();

            final String product = PayoutEventLogUtil.paymentTypeToProductCode(
                    id.getProduct(), id.getCountry());

            final String key = id.getMerchant() + "-" + id.getCountry() + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.PAY_OUT.name())
                                .countryCode(id.getCountry())
                                .merchantCode(id.getMerchant())
                                .product(product)
                                .build();

                int totalCount = newTransactionCountItem.getTotalCount();
                totalCount += payoutCountItem.getCount();
                newTransactionCountItem.setTotalCount(totalCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int totalCount = oldTransactionCountItem.getTotalCount();
                totalCount += payoutCountItem.getCount();
                oldTransactionCountItem.setTotalCount(totalCount);
            }
        }
        for (PayoutCountItem payoutCountItem : payoutSuccessItems) {
            TransactionCountGroupId id = payoutCountItem.get_id();

            final String product = PayoutEventLogUtil.paymentTypeToProductCode(
                    id.getProduct(), id.getCountry());

            final String key = id.getMerchant() + "-" + id.getCountry() + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.PAY_OUT.name())
                                .countryCode(id.getCountry())
                                .merchantCode(id.getMerchant())
                                .product(product)
                                .build();

                int successCount = newTransactionCountItem.getSuccessCount();
                successCount += payoutCountItem.getCount();
                newTransactionCountItem.setSuccessCount(successCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int successCount = oldTransactionCountItem.getSuccessCount();
                successCount += payoutCountItem.getCount();
                oldTransactionCountItem.setSuccessCount(successCount);
            }
        }
        return res;
    }


    public static List<TransactionCountItem> marketplaceListToItem(
            List<MarketPlaceCountItem> marketplaceTotalItems,
            List<MarketPlaceCountItem> marketplaceSuccessItems) {
        List<TransactionCountItem> res = new ArrayList<>();
        Map<String, TransactionCountItem> map = new HashMap<>();
        for (MarketPlaceCountItem marketPlaceCountItem : marketplaceTotalItems) {
            TransactionCountGroupId id = marketPlaceCountItem.get_id();
            String product = id.getProduct();
            if ("GiftCard".equals(id.getProduct())) {
                product = ProductCode.GIFTCARD.name();
            } else if ("PhoneTopup".equals(id.getProduct())) {
                product = ProductCode.TOPUP.name();
            } else if ("Utility".equals(id.getProduct())) {
                product = ProductCode.UTILITY.name();
            }
            String key = id.getMerchant() + "-" + id.getCountry() + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.MARKET_PLACE_ORDERS.name())
                                .countryCode(id.getCountry())
                                .merchantCode(id.getMerchant().substring(7))
                                .product(product)
                                .build();

                int totalCount = newTransactionCountItem.getTotalCount();
                totalCount += marketPlaceCountItem.getCount();
                newTransactionCountItem.setTotalCount(totalCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int totalCount = oldTransactionCountItem.getTotalCount();
                totalCount += marketPlaceCountItem.getCount();
                oldTransactionCountItem.setTotalCount(totalCount);
            }
        }
        for (MarketPlaceCountItem marketPlaceCountItem : marketplaceSuccessItems) {
            TransactionCountGroupId id = marketPlaceCountItem.get_id();

            String product = id.getProduct();
            if ("GiftCard".equals(id.getProduct())) {
                product = ProductCode.GIFTCARD.name();
            } else if ("PhoneTopup".equals(id.getProduct())) {
                product = ProductCode.TOPUP.name();
            } else if ("Utility".equals(id.getProduct())) {
                product = ProductCode.UTILITY.name();
            }
            String key = id.getMerchant() + "-" + id.getCountry() + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.MARKET_PLACE_ORDERS.name())
                                .countryCode(id.getCountry())
                                .merchantCode(id.getMerchant().substring(7))
                                .product(product)
                                .build();

                int successCount = newTransactionCountItem.getSuccessCount();
                successCount += marketPlaceCountItem.getCount();
                newTransactionCountItem.setSuccessCount(successCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int successCount = oldTransactionCountItem.getSuccessCount();
                successCount += marketPlaceCountItem.getCount();
                oldTransactionCountItem.setSuccessCount(successCount);
            }
        }
        return res;
    }

    public static List<TransactionCountItem> SubAccountListToItem(
            List<SubAccountCountItem> subAccountTotalItems,
            List<SubAccountCountItem> subAccountSuccessItems) {
        List<TransactionCountItem> res = new ArrayList<>();
        Map<String, TransactionCountItem> map = new HashMap<>();
        for (SubAccountCountItem subAccountCountItem : subAccountTotalItems) {
            String db = subAccountCountItem.get_id();
            String country = CountryCode.MX.name();
            String product = ProductCode.SPEI_VA.name();
            String key = db + "-" + country + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.PAY_IN.name())
                                .countryCode(country)
                                .merchantCode(db.substring(20))
                                .product(product)
                                .build();
                int totalCount = newTransactionCountItem.getTotalCount();
                totalCount += subAccountCountItem.getCount();
                newTransactionCountItem.setTotalCount(totalCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int totalCount = oldTransactionCountItem.getTotalCount();
                totalCount += subAccountCountItem.getCount();
                oldTransactionCountItem.setTotalCount(totalCount);
            }
        }
        for (SubAccountCountItem subAccountCountItem : subAccountSuccessItems) {
            String db = subAccountCountItem.get_id();
            String country = CountryCode.MX.name();
            String product = ProductCode.SPEI_VA.name();
            String key = db + "-" + country + "-" + product;
            TransactionCountItem oldTransactionCountItem = map.get(key);
            if (oldTransactionCountItem == null) {
                TransactionCountItem newTransactionCountItem =
                        TransactionCountItem.builder()
                                .transactionType(TransactionType.PAY_IN.name())
                                .countryCode(country)
                                .merchantCode(db.substring(20))
                                .product(product)
                                .build();
                int successCount = newTransactionCountItem.getSuccessCount();
                successCount += subAccountCountItem.getCount();
                newTransactionCountItem.setSuccessCount(successCount);
                map.put(key, newTransactionCountItem);
                res.add(newTransactionCountItem);
            } else {
                int successCount = oldTransactionCountItem.getSuccessCount();
                successCount += subAccountCountItem.getCount();
                oldTransactionCountItem.setSuccessCount(successCount);
            }
        }
        return res;
    }
}
