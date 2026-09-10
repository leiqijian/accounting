package com.liquido.statement.service;

import com.liquido.statement.pojo.vo.FundsAutoCollectionVo;

public interface DailyFundsCollectionService {

    void dailyFundsCollection(final FundsAutoCollectionVo vo);
}
