package com.liquido.core.mvc.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.liquido.core.common.exception.MvcExceptionCode;
import com.liquido.core.mvc.crypto.SignConstants;
import com.liquido.core.mvc.crypto.SignatureStrategy;
import com.liquido.core.mvc.vo.AccessPartnerVo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Sign Request Wrapper
 */
@Slf4j
public class SignRequestWrapper extends HttpServletRequestWrapper {

    private final ByteArrayInputStream byteArrayInputStream;

    public SignRequestWrapper(final HttpServletRequest request,
                              final SignatureStrategy signatureStrategy,
                              final AccessPartnerVo accessPartner) throws Exception {
        super(request);
        final String reqSignature = request.getHeader(SignConstants.HEADER_SIGNATURE_NAME);
        final String content = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

        final boolean result = checkSign(content, signatureStrategy, accessPartner, reqSignature);
        if (!result) {
            throw MvcExceptionCode.SIGNATURE_ERROR.exception();
        }

        this.byteArrayInputStream =
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * check Sign
     *
     * @param content           content
     * @param signatureStrategy signatureStrategy
     * @param accessPartner     accessPartner
     * @param reqSignature      reqSignature
     * @return boolean
     */
    private boolean checkSign(final String content,
                              final SignatureStrategy signatureStrategy,
                              final AccessPartnerVo accessPartner,
                              final String reqSignature) {
        log.info("before check signature={}", reqSignature);
        if (StringUtils.isBlank(reqSignature)) {
            throw MvcExceptionCode.SIGNATURE_ERROR.exception();
        }

        final StringBuilder signContent = new StringBuilder();
        signContent.append(content).append(SignConstants.SIGNATURE_CONTENT_SEPARATOR)
                .append(accessPartner.getAccessKey());
        final boolean result =
                signatureStrategy.signature(signContent.toString(), accessPartner.getSecretKey(),
                        reqSignature);
        log.info("after check signature result={}", result);

        return result;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
