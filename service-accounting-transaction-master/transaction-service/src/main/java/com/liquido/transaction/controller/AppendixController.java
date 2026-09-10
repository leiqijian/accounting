package com.liquido.transaction.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.transaction.api.AppendixApi;
import com.liquido.transaction.pojo.dto.AppendixDto;
import com.liquido.transaction.pojo.vo.DownloadAppendixVo;
import com.liquido.transaction.pojo.vo.QueryAppendixVo;
import com.liquido.transaction.service.AppendixService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AppendixController implements AppendixApi {

    private final AppendixService appendixService;

    @PostMapping("/transaction/appendix/download")
    public void downloadAppendix(@RequestBody @Valid final DownloadAppendixVo vo,
                                 final HttpServletResponse response) {
        appendixService.downloadAppendix(vo, response);
    }

    @Override
    @PostMapping("/transaction/appendix/download-url/generator")
    public ResponseDto<List<AppendixDto>> generatorAppendixDownloadUrl(
            @RequestBody @Valid final QueryAppendixVo vo) {
        return ResponseDto.success(appendixService.generatorAppendixDownloadUrl(vo));
    }

    @PostMapping("/transaction/appendix/merchantId/add")
    public void appendixMerchantIdAdd() {
        appendixService.appendixMerchantIdAdd();
    }

}
