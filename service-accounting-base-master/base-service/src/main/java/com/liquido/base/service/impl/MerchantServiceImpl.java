package com.liquido.base.service.impl;

import static com.liquido.base.pojo.entity.QMerchant.merchant;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.base.common.properties.BaseProperties;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MerchantWeightDto;
import com.liquido.base.pojo.entity.Merchant;
import com.liquido.base.pojo.entity.QMerchant;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.AddMerchantVo;
import com.liquido.base.pojo.vo.EditMerchantVo;
import com.liquido.base.pojo.vo.ListMerchantVo;
import com.liquido.base.pojo.vo.PageMerchantVo;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantVo;
import com.liquido.base.repository.MerchantRepository;
import com.liquido.base.service.MerchantService;
import com.liquido.base.service.SubMerchantService;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.PageUtil;
import com.liquido.core.mvc.vo.PageVo;

import com.github.wenhao.jpa.Specifications;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MerchantServiceImpl implements MerchantService {

    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final MerchantRepository merchantRepository;
    private final BaseProperties baseProperties;
    private final SubMerchantService subMerchantService;

    @Override
    public MerchantDto save(final AddMerchantVo vo) {
        return modelMapper.convert(merchantRepository.save(
                BeanCopierUtil.copyProperties(vo, Merchant.class)));
    }

    @Override
    public void update(final Long id, final EditMerchantVo vo) {
        final Merchant bean = requireOne(id);
        modelMapper.updateMerchant(vo, bean);
        merchantRepository.save(bean);
    }

    @Override
    public List<MerchantDto> queryAllMerchant() {


        return merchantRepository.findAll().stream().map(entity -> {
            final MerchantDto merchantDto = modelMapper.convert(entity);

            if (Objects.nonNull(merchantDto)
                    && Optional.ofNullable(merchantDto.getMergerAccount()).orElse(false)
                    && Objects.nonNull(baseProperties)
                    && Objects.nonNull(baseProperties.getMergerAccount())) {
                baseProperties.getMergerAccount().stream()
                        .filter(v -> merchantDto.getCode().equals(v.getMerchantCode())
                                && ObjectUtils.isNotEmpty(v.getNotSupportCountry())).findFirst()
                        .ifPresent(v -> merchantDto.setNotSupportMergerCountry(
                                v.getNotSupportCountry()));
            }
            setMerchantOtherInfo(merchantDto);
            return merchantDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MerchantDto> queryAllMerchantWithEffectiveReportWeight() {
        return modelMapper.convertMerchantList(jpaQueryFactory.select(merchant)
                .from(merchant)
                .where(merchant.reportWeight.gt(0))
                .orderBy(merchant.reportWeight.desc(), merchant.code.asc())
                .fetch());
    }

    @Override
    public List<MerchantDto> listMerchant(final ListMerchantVo vo) {
        return modelMapper.convertMerchantList(merchantRepository.findAll(getSpecification(vo),
                Sort.by(Sort.Order.desc("weight"), Sort.Order.asc("code"))));
    }

    @Override
    public PageVo<MerchantDto> pageMerchant(final PageMerchantVo vo) {
        final Specification<Merchant> spec = getSpecification(
                BeanCopierUtil.copyProperties(vo, ListMerchantVo.class));

        final Page<Merchant> page = merchantRepository.findAll(spec,
                PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                        Sort.by(Sort.Order.desc("weight"), Sort.Order.asc("code"))));

        return PageUtil.buildPage(page, vo, MerchantDto.class);
    }

    @Override
    public MerchantDto getEffectiveMerchantInfo(final QueryMerchantVo vo) {
        final Specification<Merchant> spec = getSpecification(
                BeanCopierUtil.copyProperties(vo, ListMerchantVo.class));
        final MerchantDto merchantDto =
                merchantRepository.findOne(spec).map(modelMapper::convert).orElse(null);

        if (Objects.nonNull(merchantDto)) {
            boolean result = subMerchantService.existsByMerchantId(
                    QuerySubMerchantVo.builder().merchantId(vo.getId()).build());
            merchantDto.setSubMerchantExist(result);
        }

        if (Objects.nonNull(merchantDto)
                && Optional.ofNullable(merchantDto.getMergerAccount()).orElse(false)
                && Objects.nonNull(baseProperties)
                && Objects.nonNull(baseProperties.getMergerAccount())) {
            baseProperties.getMergerAccount().stream()
                    .filter(v -> merchantDto.getCode().equals(v.getMerchantCode())
                            && ObjectUtils.isNotEmpty(v.getNotSupportCountry())).findFirst()
                    .ifPresent(v -> merchantDto.setNotSupportMergerCountry(
                            v.getNotSupportCountry()));
        }
        setMerchantOtherInfo(merchantDto);
        return merchantDto;
    }

    public Specification<Merchant> getSpecification(final ListMerchantVo vo) {
        return Specifications.<Merchant>and().eq(Objects.nonNull(vo.getId()), "id", vo.getId())
                .in(CollectionUtils.isNotEmpty(vo.getIds()), "id",
                        ListUtils.emptyIfNull(vo.getIds()).toArray())
                .eq(StringUtils.isNotBlank(vo.getCode()), "code", vo.getCode())
                .eq(StringUtils.isNotBlank(vo.getUuid()), "uuid", vo.getUuid())
                // name support fuzzy query
                .like(StringUtils.isNotBlank(vo.getName()), "name", vo.getName() + "%")
                .eq(Objects.nonNull(vo.getMergerAccount()), "mergerAccount", vo.getMergerAccount())
                .eq(Objects.nonNull(vo.getWeight()), "weight", vo.getWeight())
                .eq(Objects.nonNull(vo.getReportWeight()), "reportWeight", vo.getReportWeight())
                .eq(Objects.nonNull(vo.getInnerFlag()), "innerFlag", vo.getInnerFlag())
                .eq(Objects.nonNull(vo.getOwner()), "owner", vo.getOwner()).build();
    }

    @Override
    public boolean existsByMerchantCode(final String merchantCode) {
        if (StringUtils.isBlank(merchantCode)) {
            return false;
        }
        return merchantRepository.existsByCode(merchantCode);
    }

    @Override
    public List<Long> queryLowerWeightMerchantIds() {
        final QMerchant entity = merchant;
        final BooleanExpression condition =
                entity.weight.loe(0).or(entity.delFlag.eq(Boolean.TRUE));

        return jpaQueryFactory.select(entity.id)
                .from(entity)
                .where(condition)
                .orderBy(entity.weight.desc(), entity.code.asc())
                .fetch();
    }

    @Override
    public List<MerchantWeightDto> queryAllMerchantWeight() {
        final QMerchant entity = merchant;
        return jpaQueryFactory.select(Projections.fields(MerchantWeightDto.class,
                        entity.id, entity.code, entity.weight))
                .from(entity).orderBy(entity.weight.desc(), entity.code.asc()).fetch();
    }

    @Override
    public Set<Long> querySubMerchantDividedBillMerchantIds() {
        return Optional.ofNullable(baseProperties.getSubMerchant()).map(
                        BaseProperties.SubMerchantProperties::getDividedBillMerchantIds)
                .orElse(Set.of());
    }

    private Merchant requireOne(final Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }


    private void setMerchantOtherInfo(final MerchantDto merchantDto) {
        if (Objects.nonNull(merchantDto)) {
            Set<Long> dividedBillMerchantIds =
                    Optional.ofNullable(baseProperties.getSubMerchant()).map(
                                    BaseProperties.SubMerchantProperties::getDividedBillMerchantIds)
                            .orElse(Set.of());
            merchantDto.setSubMerchantDividedBill(
                    dividedBillMerchantIds.contains(merchantDto.getId()));

        }
    }
}
