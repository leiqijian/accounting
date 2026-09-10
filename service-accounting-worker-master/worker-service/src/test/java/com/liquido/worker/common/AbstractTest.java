package com.liquido.worker.common;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.Filter;

import com.liquido.core.common.security.Sha256Util;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.RequestVo;
import com.liquido.worker.WorkerApplication;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

@SpringBootTest(classes = WorkerApplication.class)
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public abstract class AbstractTest extends AbstractTestNg {

    private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
    private static final String secret = "S202001010000000001";
    private static final String accessKey = "A202000001";
    public static final String RESPONSE_CODE_FIELD_NAME = "code";
    public static final String RESPONSE_MSG_FIELD_NAME = "msg";
    public static final String RESPONSE_DATA_FIELD_NAME = "data";
    @Resource
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    @BeforeSuite
    public void set() {
    }

    protected Filter[] getServletFilters() {
        final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        return new Filter[] {characterEncodingFilter};
    }

    @BeforeClass
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(getServletFilters())
                .build();
    }

    /**
     *
     */
    protected String postJson(final String url, final Object req) throws Exception {
        final ResultActions result =
                this.mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                JsonUtil.toJson(req)));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        return result.andReturn().getResponse().getContentAsString();
    }

    protected <T> ResponseDto<T> postJson(final String url,
                                         final Object params,
                                         Class<T> returnClazz,
                                         final int expectCode,
                                         final boolean needSign)
            throws Exception {
        return postJson(url, null, params, returnClazz, expectCode, needSign);
    }

    protected <T> ResponseDto<List<T>> postJson(final String url,
                                               final Object params,
                                               final Class<T> returnClazz,
                                               final boolean isCollection,
                                               final int expectCode)
            throws Exception {
        if (!(params instanceof RequestVo)) {
            final RequestVo<Object> opRequest = new RequestVo<>();
            opRequest.setParams(params);

            return postJsonList(url, null, opRequest, returnClazz);
        }

        return postJsonList(url, null, params, returnClazz);
    }

    protected <T> ResponseDto<Page<T>> postJsonPage(final String url,
                                                   final Object params,
                                                   final Class<T> returnClazz,
                                                   final int expectCode) throws Exception {
        if (!(params instanceof RequestVo)) {
            final RequestVo<Object> opRequest = new RequestVo<>();
            opRequest.setParams(params);

            return postJsonPage(url, null, opRequest, returnClazz);
        }

        return postJsonPage(url, null, params, returnClazz);
    }

    /**
     *
     */
    protected <T> ResponseDto<T> postJson(final String url,
                                         final Map<String, String> headerMap,
                                         final Object params,
                                         final Class<T> returnClazz,
                                         final int expectCode,
                                         final boolean sign) throws Exception {
        final HttpHeaders headers = new HttpHeaders();

        final String jsonParams = JsonUtil.toJson(params);
        if (sign) {
            String signature = Sha256Util.getSignature(jsonParams + "&" + accessKey, secret);
            headerMap.put("signature", signature);
        }

        if (headerMap != null && !headerMap.isEmpty()) {
            for (final String key : headerMap.keySet()) {
                headers.put(key, Lists.newArrayList(headerMap.get(key)));
            }
        }

        // call api
        final ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
                        .headers(headers).content(jsonParams));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        result.andExpect(MockMvcResultMatchers.status().isOk());

        final String str = result.andReturn().getResponse().getContentAsString();
        System.out.println("response body:" + str);

        final ResponseDto<T> rsp = convertToResponseDto(str, returnClazz);

        System.out.println("response result：" + rsp);

        //Assert.assertEquals(rsp.getCode(), expectCode, "return result is not " + expectCode);

        return rsp;

    }

    protected <T> ResponseDto<List<T>> postJsonList(final String url,
                                                   final Map<String, String> headerMap,
                                                   final Object params, Class<T> returnClazz)
            throws Exception {

        final String jsonParams = JsonUtil.toJson(params);

        final HttpHeaders headers = new HttpHeaders();
        if (headerMap != null && !headerMap.isEmpty()) {
            for (final String key : headerMap.keySet()) {
                headers.put(key, Lists.newArrayList(headerMap.get(key)));
            }
        }

        final ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
                        .headers(headers).content(jsonParams));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        result.andExpect(MockMvcResultMatchers.status().isOk());

        final String str = result.andReturn().getResponse().getContentAsString();
        System.out.println("response body: " + str);

        final ResponseDto<List<T>> rsp = convertToResponseDtoList(str, returnClazz);

        System.out.println("response result: " + rsp);

        return rsp;
    }

    protected <T> ResponseDto<Page<T>> postJsonPage(final String url,
                                                   final Map<String, String> headerMap,
                                                   final Object params,
                                                   final Class<T> returnClazz)
            throws Exception {

        final String jsonParams = JsonUtil.toJson(params);

        final HttpHeaders headers = new HttpHeaders();
        if (headerMap != null && !headerMap.isEmpty()) {
            for (final String key : headerMap.keySet()) {
                headers.put(key, Lists.newArrayList(headerMap.get(key)));
            }
        }

        final ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
                        .headers(headers).content(jsonParams));

        result.andExpect(MockMvcResultMatchers.status().isOk());

        result.andExpect(MockMvcResultMatchers.status().isOk());

        final String str = result.andReturn().getResponse().getContentAsString();
        System.out.println("response body: " + str);

        final ResponseDto<Page<T>> rsp = convertToResponseDtoPage(str, returnClazz);

        System.out.println("response result：" + rsp);

        return rsp;
    }

    private <T> ResponseDto<List<T>> convertToResponseDtoList(final String str,
                                                            Class<T> returnClazz) {
        final JSONObject json = JSONObject.parseObject(str);

        final ResponseDto<List<T>> opResponse = new ResponseDto<List<T>>();
        opResponse.setCode(json.getIntValue(RESPONSE_CODE_FIELD_NAME));
        opResponse.setMsg(json.getString(RESPONSE_MSG_FIELD_NAME));

        if (returnClazz != null) {
            final List<T> content =
                    json.getObject(RESPONSE_DATA_FIELD_NAME, new TypeReference<List<T>>() {
                    });
            opResponse.setData(content);
        }
        return opResponse;
    }

    private <T> ResponseDto<Page<T>> convertToResponseDtoPage(String str, Class<T> returnClazz) {
        JSONObject json = JSONObject.parseObject(str);

        ResponseDto<Page<T>> opResponse = new ResponseDto<Page<T>>();
        opResponse.setCode(json.getIntValue(RESPONSE_CODE_FIELD_NAME));
        opResponse.setMsg(json.getString(RESPONSE_MSG_FIELD_NAME));

        if (returnClazz != null) {
            Page<T> content =
                    json.getObject(RESPONSE_DATA_FIELD_NAME, new TypeReference<Page<T>>() {
                    });
            opResponse.setData(content);
        }
        return opResponse;
    }

    private <T> ResponseDto<T> convertToResponseDto(final String str, Class<T> returnClazz) {
        final JSONObject json = JSONObject.parseObject(str);

        final ResponseDto<T> opResponse = new ResponseDto<T>();
        opResponse.setCode(json.getIntValue(RESPONSE_CODE_FIELD_NAME));
        opResponse.setMsg(json.getString(RESPONSE_MSG_FIELD_NAME));

        if (returnClazz != null) {
            final T content = json.getObject(RESPONSE_DATA_FIELD_NAME, returnClazz);
            opResponse.setData(content);
        }
        return opResponse;
    }

    /**
     *
     */
    protected String postForm(final String url, final Map<String, String> map) throws Exception {
        final MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();

        final Set<String> keySet = map.keySet();
        for (final String key : keySet) {
            form.add(key, map.get(key));
        }

        final ResultActions result = this.mockMvc.perform(
                MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(form));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        return result.andReturn().getResponse().getContentAsString();

    }

    /**
     *
     */
    protected Object unwrapProxy(Object bean) {
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            try {
                bean = advised.getTargetSource().getTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
}
