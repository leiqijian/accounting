package com.liquido.base.controller;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.service.DictionaryService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @PostMapping("/base/dict/value/query")
    public ResponseDto<String> queryDictValue(
            @NotNull @RequestParam("dictionaryType") final DictionaryTypeEnum dictionaryType,
            @NotBlank @RequestParam("key") final String key,
            @RequestParam(value = "subTypes", required = false) final String... subTypes) {
        return ResponseDto.success(
                dictionaryService.queryDictValue(dictionaryType, String.class, key, subTypes));
    }

    @PostMapping("/base/dict/all/value/query")
    public ResponseDto<Map<String, String>> queryDictAllValue(
            @NotNull @RequestParam("dictionaryType") final DictionaryTypeEnum dictionaryType,
            @RequestParam(value = "subTypes", required = false) final String... subTypes) {
        return ResponseDto.success(
                dictionaryService.queryDictAllValue(dictionaryType, String.class, subTypes));
    }

    @PostMapping("/base/dict/value/add")
    public ResponseDto<Void> addDictValue(
            @NotNull @RequestParam("dictionaryType") final DictionaryTypeEnum dictionaryType,
            @NotBlank @RequestParam("key") final String key,
            @NotBlank @RequestParam("value") final String value,
            @RequestParam(value = "subTypes", required = false) final String... subTypes) {
        dictionaryService.addDictValue(dictionaryType, key, value, subTypes);
        return ResponseDto.success();
    }
}
