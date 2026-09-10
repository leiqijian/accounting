package com.liquido.base.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.AccountProductGroupDto;
import com.liquido.base.pojo.dto.PaymentLinkProductDto;
import com.liquido.base.pojo.entity.AccountProduct;
import com.liquido.base.pojo.vo.BatchAddAccountProductVo;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.base.pojo.vo.QueryAccountProductGroupVo;
import com.liquido.base.pojo.vo.QueryPaymentLinkSupportProductVo;
import com.liquido.base.repository.AccountProductRepository;
import com.liquido.base.service.AccountProductService;
import com.liquido.base.service.DictionaryService;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountProductServiceImpl implements AccountProductService {

    private final DictionaryService dictionaryService;
    private final AccountProductRepository accountProductRepository;

    public List<AccountProductDto> batchAddAccountProduct(final BatchAddAccountProductVo vo) {
        final LocalDateTime now = LocalDateTimeUtil.nowUtc();

        final List<AccountProduct> accountProductList = vo.getListVo().stream().map(x -> {
            // Copy input data to an AccountProduct entity
            final AccountProduct accountProduct =
                    BeanCopierUtil.copyProperties(x, AccountProduct.class);

            // Check if MonthlyVolumeGroup is blank; if so, set it to the account's ID as a default
            if (StringUtils.isBlank(x.getMonthlyVolumeGroup())) {
                accountProduct.setMonthlyVolumeGroup(x.getAccountId().toString());
            }

            // Set the opening time
            accountProduct.setOpenTime(now);
            return accountProduct;
        }).collect(Collectors.toList());

        return toDto(accountProductRepository.saveAll(accountProductList));
    }

    @Override
    public List<AccountProductDto> listAccountProduct(final ListAccountProductVo vo) {
        // Retrieve the list of account products based on the specifications
        final PredicateBuilder<AccountProduct> spec = Specifications.<AccountProduct>and()
                .eq(Objects.nonNull(vo.getAccountId()), "accountId", vo.getAccountId())
                .in(CollectionUtils.isNotEmpty(vo.getAccounts()), "accountId",
                        ListUtils.emptyIfNull(vo.getAccounts()).toArray())
                .eq(Objects.nonNull(vo.getAccountProductId()), "id", vo.getAccountProductId())
                .in(CollectionUtils.isNotEmpty(vo.getAccountProductIds()), "id",
                        ListUtils.emptyIfNull(vo.getAccountProductIds()).toArray());

        // Convert the entity objects to DTOs and collect them in a list
        return toDto(accountProductRepository.findAll(spec.build()));
    }

    @Override
    public List<AccountProductDto> findAllByTransactionTypeCode(
            final TransactionTypeCodeEnum code) {
        return toDto(accountProductRepository.findAllByTransactionTypeCode(code));
    }

    @Override
    public AccountProductGroupDto queryGroupProductsOfProduct(final QueryAccountProductGroupVo vo) {

        final List<AccountProductDto> productList = accountProductRepository.findAll(
                Specifications.<AccountProduct>and().in("id", vo.getAccountProductIds().toArray())
                        .build()).stream().map(this::toDto).collect(Collectors.toList());

        final List<AccountProductDto> otherProductsInSameGroup =
                getOtherProductsInSameGroup(productList);

        final List<AccountProductDto> productGroupList = new ArrayList<>();
        productGroupList.addAll(productList);
        productGroupList.addAll(otherProductsInSameGroup);

        return new AccountProductGroupDto(productList, productGroupList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PaymentLinkProductDto> querySupportPaymentLinkProduct(
            final QueryPaymentLinkSupportProductVo vo) {

        final List<ProductCodeEnum> productList =
                this.listAccountProduct(ListAccountProductVo.builder()
                                .accountId(vo.getAccountId()).build())
                        .stream().map(AccountProductDto::getProductCode)
                        .distinct().collect(Collectors.toList());

        final Map<String, PaymentLinkProductDto> dictMap =
                dictionaryService.queryDictAllValue(DictionaryTypeEnum.PAYMENT_LINK_PRODUCT,
                        PaymentLinkProductDto.class, vo.getCountryCode().getCode());

        final List<PaymentLinkProductDto> productDtoList = Lists.newArrayList(dictMap.values());

        final Map<ProductCodeEnum, PaymentLinkProductDto> paymentLinkProductMap =
                productDtoList.stream().collect(Collectors.toMap(
                        PaymentLinkProductDto::getProductCode, Function.identity()));

        final List<PaymentLinkProductDto> resultList = Lists.newArrayList();
        for (final ProductCodeEnum productCode : productList) {
            final PaymentLinkProductDto data = paymentLinkProductMap.get(productCode);
            if (Objects.nonNull(data)) {
                resultList.add(PaymentLinkProductDto.builder()
                        .productCode(data.getProductCode())
                        .maxAmount(data.getMaxAmount())
                        .minAmount(data.getMinAmount())
                        .currency(data.getCurrency())
                        .build());
            }
        }

        return resultList;
    }


    private List<AccountProductDto> getOtherProductsInSameGroup(
            final List<AccountProductDto> productList) {
        // collect account ids from the products list
        final List<Long> accountIds = productList.stream().map(AccountProductDto::getAccountId)
                .collect(Collectors.toList());

        // collect group names containing ","
        final List<String> groupNamesWithComma =
                productList.stream().map(AccountProductDto::getMonthlyVolumeGroup)
                        .filter(groupName -> groupName.contains(",")).distinct()
                        .collect(Collectors.toList());

        // extract missing account ids from group names
        final List<Long> missingAccountIds = groupNamesWithComma.stream()
                .flatMap(groupName -> Arrays.stream(groupName.split(","))).map(Long::valueOf)
                .filter(accountId -> accountIds.stream().noneMatch(id -> id.equals(accountId)))
                .collect(Collectors.toList());


        if (!missingAccountIds.isEmpty()) {
            // fetch other account products based on missing account ids
            final List<AccountProduct> accountProductList = accountProductRepository.findAll(
                    Specifications.<AccountProduct>and()
                            .in("accountId", missingAccountIds.toArray()).build());
            return accountProductList.stream().map(this::toDto).collect(Collectors.toList());
        }

        // return an empty list if no missing account ids
        return new ArrayList<>();
    }

    private AccountProductDto toDto(final AccountProduct original) {
        final AccountProductDto dto = new AccountProductDto();
        BeanCopierUtil.copyProperties(original, dto);
        return dto;
    }

    private List<AccountProductDto> toDto(final List<AccountProduct> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
