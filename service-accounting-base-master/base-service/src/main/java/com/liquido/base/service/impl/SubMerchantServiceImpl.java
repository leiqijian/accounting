package com.liquido.base.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.entity.SubMerchantInfo;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.AddSubMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantInfoVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.base.pojo.vo.QuerySubMerchantVo;
import com.liquido.base.repository.SubMerchantInfoRepository;
import com.liquido.base.service.SubMerchantService;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SubMerchantServiceImpl implements SubMerchantService {

    private final ModelMapper modelMapper;
    private final SubMerchantInfoRepository subMerchantInfoRepository;

    @Override
    public void save(final AddSubMerchantVo vo) {

        final List<SubMerchantInfo> subMerchantInfos =
                subMerchantInfoRepository.queryAllByMerchantIdAndSubMerchantId(
                        vo.getMerchantId(), vo.getSubMerchantId());

        if (CollectionUtils.isEmpty(subMerchantInfos)) {
            modelMapper.convert(subMerchantInfoRepository.save(
                    BeanCopierUtil.copyProperties(vo, SubMerchantInfo.class)));
        }
    }

    @Override
    public List<SubMerchantDto> queryBySubMerchantId(final QuerySubMerchantListVo vo) {
        final PredicateBuilder<SubMerchantInfo> predicateBuilder =
                Specifications.<SubMerchantInfo>and().eq("merchantId", vo.getMerchantId());

        if (ObjectUtils.isNotEmpty(vo.getSubMerchantId())) {
            predicateBuilder.in("subMerchantId", vo.getSubMerchantId());
        }
        final List<SubMerchantInfo> subMerchantInfos =
                subMerchantInfoRepository.findAll(predicateBuilder.build());

        if (CollectionUtils.isEmpty(subMerchantInfos)) {
            return Collections.emptyList();
        }

        return subMerchantInfos.stream().map(modelMapper::convert).collect(Collectors.toList());
    }

    @Override
    public boolean existsByMerchantId(final QuerySubMerchantVo vo) {
        final List<SubMerchantInfo> infos =
                subMerchantInfoRepository.queryAllByMerchantId(vo.getMerchantId());

        return infos != null && !infos.isEmpty();
    }

    @Override
    public List<SubMerchantDto> querySubMerchantListByMerchantId(final QuerySubMerchantVo vo) {
        final List<SubMerchantInfo> infos =
                subMerchantInfoRepository.queryAllByMerchantId(vo.getMerchantId());

        if (infos != null && !infos.isEmpty()) {
            return modelMapper.convertSubMerchantInfo(infos);
        }
        return Collections.emptyList();
    }

    @Override
    public SubMerchantDto querySubMerchant(final QuerySubMerchantInfoVo vo) {

        final Specification<SubMerchantInfo> sp = Specifications.<SubMerchantInfo>and()
                .eq("merchantId", vo.getMerchantId())
                .eq("subMerchantId", vo.getSubMerchantId()).build();

        return subMerchantInfoRepository.findOne(sp).map(modelMapper::convert).orElseThrow(
                CommonExceptionCode.PARAMETER_ILLEGAL::exception);
    }
}
