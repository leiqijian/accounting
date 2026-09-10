package com.liquido.worker.service;

import javax.annotation.Resource;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.worker.common.AbstractTest;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.feign.StatementService;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.service.calculate.ExchangeRateManager;
import com.liquido.worker.service.calculate.TaskTransactionService;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

@Slf4j
public class TaskTransactionServiceTest extends AbstractTest {

    @Resource
    private TaskTransactionService taskTransactionService;

    //@MockBean
    private BaseService baseService;
    //@MockBean
    private StatementService statementService;
    //@MockBean
    private ExchangeRateManager exchangeRateManager;
    //@Mock
    private TaskFeeCalculationService taskFeeCalculationService;
    //@Mock
    private TaskLogFeeCalculationService taskLogFeeCalculationService;


    @BeforeClass
    public void beforeClass() {
        //MockitoAnnotations.openMocks(this);
        log.info(">>>>> open mock ...");
    }

    @Test(description = "TestTaskTransaction")
    public void testTaskTransaction() throws Exception {
        //preMock();
    }


    @Test(description = "testBatchTaskTransaction")
    public void testBatchTaskTransaction() throws Exception {

        TaskFeeCalculationSettleBo batch = new TaskFeeCalculationSettleBo();
        batch.setRequestId(SnowflakeIdUtil.generate() + "");
        batch.setCountryCode(CountryCodeEnum.BR);
        batch.setMerchantCode("kwai");
        batch.setTransactionTypeCode(TransactionTypeCodeEnum.PAY_OUT);
        batch.setTaskIdList(Lists.newArrayList(117774826678386691L, 117774826678386692L,
                117774826678386693L, 117774826678386694L, 117774826678386695L, 117774826678386696L,
                117774826678386697L, 117774826678386698L, 117774826678386699L, 117774826678386700L
        ));
        taskTransactionService.batchProcessTransactionTask(batch);
    }


//    private void preMock() {
//
//        List<TaskFeeCalculation> taskList = Lists.newArrayList();
//        TaskFeeCalculation fee = new TaskFeeCalculation();
//        fee.setTransactionTypeCode(TransactionTypeCodeEnum.PAY_IN);
//        fee.setId(SnowflakeIdUtil.generate());
//        fee.setTaskType(CalculationTaskTypeEnum.UNREPEATABLE);
//        fee.setUniqueId("TF10000");
//        fee.setMerchantCode("DIDI");
//        fee.setMerchantName("DIDI");
//        fee.setCountryCode(CountryCodeEnum.US);
//        fee.setDirectionType(DirectionTypeEnum.SETTLED);
//        fee.setProductCode(ProductCodeEnum.CREDIT_CARD);
//        fee.setTaskStatus(CalculationTaskStateEnum.WAITING);
//        fee.setMerchantReference("");
//        fee.setTransactionTime(LocalDateTimeUtil.nowUtc());
//        fee.setAmount(BigDecimal.valueOf(10000L));
//        fee.setCurrency(CurrencyEnum.MXN);
//        fee.setTransactionStatus(TransactionStatusEnum.SETTLED);
//        fee.setCreatedBy(0L);
//        fee.setUpdatedBy(0L);
//        fee.setCreatedTime(LocalDateTimeUtil.nowUtc());
//        fee.setUpdatedTime(LocalDateTimeUtil.nowUtc());
//        fee.setVersion(VersionEnum.NORMAL);
//        fee.setDelFlag(false);
//        taskList.add(fee);
//
//        ///Mockito.when(taskFeeCalculationService.batchLoadWaitingTask(any())).thenReturn
//        (taskList);
//
//
//        MerchantDto merchant = new MerchantDto();
//        merchant.setId(1L);
//        merchant.setName("DIDI");
//        merchant.setCode("DIDI");
//        Mockito.when(dashboardService.getMerchantByCode(any())).thenReturn(merchant);
//
//        AccountDto accountDto = new AccountDto();
//        accountDto.setId(1L);
//        accountDto.setMerchantId(1L);
//        accountDto.setCountryCode(CountryCodeEnum.MX);
//        accountDto.setTransactionTypeCode(TransactionTypeCodeEnum.PAY_IN);
//        accountDto.setCurrency(CurrencyEnum.MXN);
//        accountDto.setTimezone("UTC-6");
//        Mockito.when(statementService.queryMerchantAccount(any(), any(), any()))
//                .thenReturn(accountDto);
//
//
//        MonthFeeDto monthFeeDto = new MonthFeeDto();
//        List<MonthFeeConfigDto> monthFeeConfigList = Lists.newArrayList();
//        MonthFeeConfigDto configVo = new MonthFeeConfigDto();
//        configVo.setId(1L);
//        configVo.setAccountId(1L);
//        configVo.setAccountProductId(1L);
//        configVo.setProductCode(ProductCodeEnum.CREDIT_CARD);
//        configVo.setActiveMonth(202205);
//        configVo.setFeeTypeCode(FeeTypeCodeEnum.TRANSACTION_FEE);
//        configVo.setFeeValue(BigDecimal.valueOf(0.01));
//        configVo.setFeeValueModel(FeeValueModelEnum.PERCENTAGE);
//        configVo.setCurrency(CurrencyEnum.MXN);
//        configVo.setMinFeeAmount(new BigDecimal(10));
//        configVo.setMaxFeeAmount(new BigDecimal(20));
//        configVo.setChargeOn(ChargeOnEnum.TRANSACTION);
//        configVo.setFeeOn(FeeOnEnum.AMOUNT);
//        configVo.setDirectionType(DirectionTypeEnum.SETTLED);
//        configVo.setInstantFlag(true);
//        monthFeeConfigList.add(configVo);
//        monthFeeDto.setMonthFeeConfigList(monthFeeConfigList);
//        Mockito.when(baseService.getProductMonthlyFeeConfig(any(), any(), any())).thenReturn
//        (monthFeeDto);
//
//        DailyExchangeRateDto fxRateDto = new DailyExchangeRateDto();
//        fxRateDto.setMerchantId(1L);
//        fxRateDto.setAccountId(1L);
//        fxRateDto.setExchangeDate(LocalDate.now());
//        fxRateDto.setRatioLose(new BigDecimal("-0.020000"));
//        fxRateDto.setMerchantRate(new BigDecimal("19.780716"));
//        Mockito.when(exchangeRateManager.getDailyExchangeRate(anyLong(), anyLong(), any(),
//                        any(), any()))
//                .thenReturn(fxRateDto);
//    }
}
