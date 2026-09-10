package com.liquido.statement.service.dailycut;

import java.time.LocalDate;
import java.util.List;

import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.pojo.vo.ReRunAccountDailyCutVo;

public interface DailyCutService {

    /**
     * run daily cut
     */
    void runAccountDailyCut();

    /**
     * rerun daily cut
     *
     * @param vo
     */
    void reRunAccountDailyCut(final ReRunAccountDailyCutVo vo);

    /**
     * daily generate account report
     *
     * @param dataList
     * @param autoPayment true: Execute the payment operation immediately after
     *                    the successful day cut
     */
    void publishDailyCutSuccessEvent(final List<DailyCutSuccessBo> dataList,
                                     boolean autoPayment);


    /**
     * pre-check whether the daily cut has been done
     *
     * @param accountId
     * @param billDate
     * @return
     */
    boolean checkDailyCutCompleted(final Long accountId, final LocalDate billDate);
}
