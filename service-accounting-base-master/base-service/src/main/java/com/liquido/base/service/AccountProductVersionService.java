package com.liquido.base.service;

import java.util.List;

import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.base.pojo.vo.AccountProductVersionVo;
import com.liquido.base.pojo.vo.EditAccountProductVersionMonthlyFlagVo;
import com.liquido.base.pojo.vo.QueryAccountProductVersionVo;

public interface AccountProductVersionService {

    Long save(AccountProductVersionVo vo);

    List<AccountProductVersionDto> saveAll(List<AccountProductVersionVo> listVo);

    List<AccountProductVersionDto> listAccountProductVersion(QueryAccountProductVersionVo vo);

    Long updateAccountProductVersionMonthFlag(EditAccountProductVersionMonthlyFlagVo vo);

}
