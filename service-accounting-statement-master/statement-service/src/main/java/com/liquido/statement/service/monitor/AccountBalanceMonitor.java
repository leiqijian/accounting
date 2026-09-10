package com.liquido.statement.service.monitor;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.vo.FileBytesVo;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.entity.QAccount;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Slf4j
@RefreshScope
@RequiredArgsConstructor
@Service("accountBalanceMonitor")
public class AccountBalanceMonitor implements StatementMonitor {
    private final BaseService baseService;
    private final JPAQueryFactory jpaQueryFactory;
    private final FreeMarkerConfigurer configurer;
    private final RedisDistLock redisDistLock;

    @Value("#{'${statement.account.monitor.all-balance-snapshot-mails:}'.split(',')}")
    private List<String> allBalanceMailTo;

    @Value("#{'${statement.account.monitor.br-balance-snapshot-mails:}'.split(',')}")
    private List<String> brBalanceMailTo;

    @Value("#{'${statement.account.monitor.shift4-balance-snapshot-mails:}'.split(',')}")
    private List<String> shift4MailTo;

    @Value("#{'${statement.account.monitor.balance-snapshot-trigger-hours:3,10,15}'.split(',')}")
    private List<Integer> triggerHours;

    @Value("#{'${statement.account.monitor.balance-snapshot-exclude:0}'.split(',')}")
    private List<Long> excludeMerchantIds;

    @Async("monitorExecutor")
    public void monitor() {

        final LocalDateTime nowTime = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of("UTC+8"))
                .toLocalDateTime();

        // Trigger a monitoring alarm every hour
        if (!triggerHours.contains(nowTime.getHour())) {
            return;
        }

        List<AccountBalanceInfo> allDataList = this.getAccountBalanceInfoList();
        if (CollectionUtils.isEmpty(allDataList)) {
            return;
        }

        // just BR country account balance list
        final List<AccountBalanceInfo> brDataList = allDataList.stream()
                .filter(x -> CountryCodeEnum.BR == x.getCountryCode())
                .collect(Collectors.toList());

        if (triggerHours.contains(nowTime.getHour()) && nowTime.getMinute() < 10) {
            // send all account mail notify
            this.sendAlarmEmail(allDataList, allBalanceMailTo);
        }

        if (nowTime.getHour() == 3 && nowTime.getMinute() > 20 && nowTime.getMinute() < 40) {
            // 15:30(UTC+8) send BR account mail notify
            this.sendAlarmEmail(brDataList, brBalanceMailTo);
        }
    }

    @Async("monitorExecutor")
    public void manualPush() {
        final List<AccountBalanceInfo> dataList = this.getAccountBalanceInfoList();
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        this.sendAlarmEmail(dataList, allBalanceMailTo);
    }

    //@Scheduled(cron = "0 */5 * * * ?")
    public void scheduledAutoPush() {
        try {
            log.info("execute balance snapshot job begin");
            if (redisDistLock.tryLock("BALANCE_SNAPSHOT_JOB",
                    DataUtil.getUuid(), 60, TimeUnit.SECONDS)) {

                final List<AccountBalanceInfo> dataList = this.getAccountBalanceInfoList();
                if (CollectionUtils.isEmpty(dataList)) {
                    return;
                }

                this.sendAlarmEmail(dataList, allBalanceMailTo);
                log.info("execute balance snapshot job end");
            }
        } catch (Exception e) {
            log.error("execute balance snapshot job error", e);
        }
    }

    @Scheduled(cron = "0 0 0,3,4,5,6,7,16,23 * * ?")
    public void autoPush2Shift4() {
        try {
            log.info("shift4 execute balance snapshot job begin, at:{}", LocalDateTime.now());
            if (redisDistLock.tryLock("SHIFT4_BALANCE_SNAPSHOT_JOB",
                    DataUtil.getUuid(), 60, TimeUnit.SECONDS)) {

                final List<AccountBalanceInfo> dataList = this.getAccountBalanceInfoList();
                if (CollectionUtils.isEmpty(dataList)) {
                    return;
                }

                this.sendAlarmEmail(dataList, shift4MailTo);
                log.info("shift4 execute balance snapshot job end, at:{}", LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("shift4 execute balance snapshot job error", e);
        }
    }

    @SneakyThrows
    private void sendAlarmEmail(
            final List<AccountBalanceInfo> dataList,
            final List<String> sendMailTo) {

        if (CollectionUtils.isEmpty(sendMailTo)) {
            return;
        }

        final String nowTime = LocalDateTimeUtil.nowUtc()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ("(UTC)");
        final Template tpl = configurer.getConfiguration()
                .getTemplate("account-balance-reminder-email-template.ftl");
        final Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("dataList", dataList);
        dataMap.put("nowTime", nowTime);

        final String content = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, dataMap);

        final List<FileBytesVo> attachments =
                Lists.newArrayList(this.generateAttachments(dataList, nowTime));

        baseService.sendMail("Account Balance Snapshot", sendMailTo, content, attachments);
    }


    public final FileBytesVo generateAttachments(
            final List<AccountBalanceInfo> dataList,
            final String nowTime) {

        try {
            final String titleName = String.format("Account Balance Snapshot-%s", nowTime);
            @Cleanup final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
            try (final ExcelWriter excelWriter =
                         EasyExcel.write(outputStream, AccountBalanceInfo.class).build()) {

                excelWriter.write(dataList, EasyExcelFactory.writerSheet().build());
                excelWriter.finish();

                final FileBytesVo attachment = new FileBytesVo();
                attachment.setFileName(titleName.concat(".xlsx"));
                attachment.setBytes(outputStream.toByteArray());

                outputStream.close();
                return attachment;
            }
        } catch (Exception e) {
            log.error("generate attachments error", e);
        }
        return null;
    }

    private List<AccountBalanceInfo> getAccountBalanceInfoList() {
        final QAccount account = QAccount.account;
        final QBean<AccountBalanceInfo> bean = Projections.fields(AccountBalanceInfo.class,
                account.merchantId,
                account.countryCode,
                account.transactionTypeCode,
                (account.latestDailyBalance.add(account.subTotalAmount)).as("accountBalance"),
                account.currency,
                account.timezone);

        final List<AccountBalanceInfo> dataList = jpaQueryFactory.select(bean)
                .from(account)
                .where(account.delFlag.eq(Boolean.FALSE))
                .fetch();

        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        for (final AccountBalanceInfo info : dataList) {
            try {
                info.setAccountBalance(AmountUtil.centToYuan(info.getAccountBalance()));
                final MerchantDto merchant = baseService.getMerchantById(info.merchantId);
                info.setMerchantCode(merchant.getCode());
                info.setInnerFlag(Optional.ofNullable(merchant.getInnerFlag()).orElse(false));
                info.setWeight(Optional.ofNullable(merchant.getReportWeight()).orElse(0));
            } catch (Exception e) {
                info.setMerchantCode(info.getMerchantId().toString() + "-UNKNOWN");
                info.setInnerFlag(false);
                info.setWeight(0);
            }
        }

        return dataList.stream()
                .filter(x -> !x.getInnerFlag())
                .filter(x -> !excludeMerchantIds.contains(x.getMerchantId()))
                .sorted(Comparator.comparing(AccountBalanceInfo::getCountryCode)
                        .thenComparing(AccountBalanceInfo::getAccountBalance,
                                Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelIgnoreUnannotated
    @HeadRowHeight(35)
    @ContentRowHeight(25)
    @HeadStyle(fillForegroundColor = 30, fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
    @HeadFontStyle(fontName = "等线", fontHeightInPoints = 14, color = 9)
    @ContentFontStyle(fontName = "等线", fontHeightInPoints = 14)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    public static class AccountBalanceInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long merchantId;

        @ExcelProperty(index = 0, value = "Country", converter = ExcelCountryConverter.class)
        @ColumnWidth(value = 20)
        @Convert(converter = CountryCodeEnum.Convert.class)
        private CountryCodeEnum countryCode;

        @ExcelProperty(index = 1, value = "Merchant")
        @ColumnWidth(value = 40)
        @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
        private String merchantCode;

        @ExcelProperty(index = 2, value = "TransactionType",
                converter = ExcelTransactionTypeConverter.class)
        @ColumnWidth(value = 30)
        @Convert(converter = TransactionTypeCodeEnum.Convert.class)
        private TransactionTypeCodeEnum transactionTypeCode;

        // latestDailyBalance + subTotalAmount
        @ExcelProperty(index = 3, value = "AccountBalance")
        @ColumnWidth(value = 30)
        @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.RIGHT)
        private BigDecimal accountBalance;

        @ExcelProperty(index = 4, value = "Currency",
                converter = ExcelCurrencyConverter.class)
        @ColumnWidth(value = 20)
        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum currency;

        @ExcelProperty(index = 5, value = "Timezone")
        @ColumnWidth(value = 15)
        private String timezone;

        private Boolean innerFlag;

        private Integer weight;
    }

    public static class ExcelCountryConverter implements Converter<CountryCodeEnum> {

        @Override
        public Class supportJavaTypeKey() {
            return CountryCodeEnum.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData convertToExcelData(final CountryCodeEnum value,
                                                final ExcelContentProperty contentProperty,
                                                final GlobalConfiguration globalConfiguration) {
            return new WriteCellData(value.getCode());
        }
    }

    public static class ExcelTransactionTypeConverter
            implements Converter<TransactionTypeCodeEnum> {

        @Override
        public Class supportJavaTypeKey() {
            return TransactionTypeCodeEnum.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData convertToExcelData(final TransactionTypeCodeEnum value,
                                                final ExcelContentProperty contentProperty,
                                                final GlobalConfiguration globalConfiguration) {
            return new WriteCellData(value.getCode());
        }
    }

    public static class ExcelCurrencyConverter implements Converter<CurrencyEnum> {

        @Override
        public Class supportJavaTypeKey() {
            return CurrencyEnum.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData convertToExcelData(final CurrencyEnum value,
                                                final ExcelContentProperty contentProperty,
                                                final GlobalConfiguration globalConfiguration) {
            return new WriteCellData(value.getCode());
        }
    }
}
