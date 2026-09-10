package com.liquido.statement.service.impl;

import java.util.LinkedHashMap;
import java.util.List;

import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.pojo.dto.BillDetailDto;
import com.liquido.statement.pojo.vo.QueryMerchantBillDetailVo;
import com.liquido.statement.service.OldSystemDataMigrationService;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OldSystemDataMigrationServiceImpl implements OldSystemDataMigrationService {
    private final RestTemplate restTemplate;

    @Override
    public List<BillDetailDto> importBillDetails(final QueryMerchantBillDetailVo vo) {
        final String url = "https://merchant.liquido.com/br-api/dashboard-service/balance_"
                + vo.getMerchant()
                + "/" + vo.getProduct() + "?startDate="
                + vo.getStartDate().format(LocalDateUtil.FORMAT_DATE)
                + "&endDate=" + vo.getEndDate().format(LocalDateUtil.FORMAT_DATE)
                + "&page=0&size=1000";
        // request header
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType.APPLICATION_JSON.toString());
        headers.add("Authorization", vo.getAuthorization());
        // request
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        final ResponseEntity<JSONObject> response =
                restTemplate.exchange(url, HttpMethod.GET, request, JSONObject.class);
        final JSONObject body = response.getBody();
        final LinkedHashMap linkedHashMap = (LinkedHashMap) body.get("data");
        return (List<BillDetailDto>) linkedHashMap.get("content");
    }
}
