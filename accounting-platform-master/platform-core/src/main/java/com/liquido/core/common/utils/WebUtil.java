package com.liquido.core.common.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.WebUtils;

/**
 * WEB WebUtil
 */
@Slf4j
public class WebUtil extends WebUtils {

    public static final String UNKNOWN = "unknown";

    public static final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    /**
     * json response contentType
     */
    public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * jsonp
     */
    public static final String JSONP_FUNCTION_NAME = "callback";
    private static final UserAgentAnalyzer USER_AGENT_ANALYZER = UserAgentAnalyzer
            .newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .withField(UserAgent.AGENT_NAME_VERSION)
            .build();

    /**
     * @param object
     * @return JSONP string
     */
    public static final String toJsonpString(final Object object) {
        return JsonUtil.toJson(object);
    }

    /**
     * @param response
     * @param string
     * @return null
     */
    public static String renderString(final HttpServletResponse response,
                                      final String string) {
        try {
            response.setStatus(200);
            response.setContentType(JSON_CONTENT_TYPE);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            log.error("WebUtil.renderString error:", e);
        }
        return null;
    }

    /**
     * Convert JSON string to JSONP string
     *
     * @param request
     * @param text    JSON string
     * @return JSONP
     */
    public static final String toJsonpString(final HttpServletRequest request,
                                             final String text) {
        final String functionName = WebUtil.getQueryParameter(request, JSONP_FUNCTION_NAME);
        if (StringUtils.isBlank(functionName)) {
            try {
                request.getParameter(JSONP_FUNCTION_NAME);
            } catch (Exception ignore) {
                log.warn(ignore.getMessage());
            }
        }

        if (StringUtils.isBlank(functionName)) {
            return text;
        }

        final StringBuffer buffer = new StringBuffer(functionName);
        buffer.append('(');
        if (StringUtils.isNotBlank(text)) {
            buffer.append(text);
        }
        buffer.append(')');

        return buffer.toString();
    }

    /**
     * is json request
     *
     * @param request
     * @return
     */
    public static final boolean isJsonRequest(final HttpServletRequest request,
                                              final Object handler) {
        final String requestedWith = request.getHeader("X-Requested-With");
        if (StringUtils.equalsIgnoreCase(requestedWith, "XMLHttpRequest")) {
            return true;
        }

        final String requestUri = request.getRequestURI();
        if (StringUtils.endsWithIgnoreCase(requestUri, ".json")
                || StringUtils.endsWithIgnoreCase(requestUri, ".jsonp")) {
            return true;
        }

        if (handler instanceof HandlerMethod) {
            final HandlerMethod hm = (HandlerMethod) handler;
            if (hm.getMethod().getAnnotation(ResponseBody.class) != null) {
                return true;
            }
            if (AnnotationUtils.findAnnotation(hm.getBean().getClass(), RestController.class)
                    != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * getQueryParameter
     *
     * @param request
     * @param name
     * @return
     */
    public static final String getQueryParameter(final HttpServletRequest request,
                                                 final String name) {
        final String[] values = getQueryParameterValues(request, name);
        return ArrayUtils.isEmpty(values) ? null : values[0];
    }

    /**
     * get query parameter values
     *
     * @param request
     * @param name
     * @return
     */
    public static final String[] getQueryParameterValues(final HttpServletRequest request,
                                                         final String name) {
        final List<String> values = new ArrayList<>();
        final String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            final String[] nvs = StringUtils.split(queryString, '&');

            for (final String query : nvs) {
                if (StringUtils.isNotBlank(query)) {
                    final String[] parameter = StringUtils.split(queryString, '=');

                    if (parameter.length == 2 && StringUtils.equals(parameter[0], name)) {
                        values.add(parameter[1]);
                    }
                }
            }
        }
        return values.toArray(new String[] {});
    }

    /**
     * get request uri with query string
     *
     * @param request
     * @return
     */
    public static final String getRequestUriWithQueryString(final HttpServletRequest request) {
        final String requestUri = request.getRequestURI();
        final String queryString = request.getQueryString();
        if (StringUtils.isBlank(queryString)) {
            return requestUri;
        } else {
            return requestUri + '?' + queryString;
        }
    }

    /**
     * get ip address
     * <p>
     * please use new function com.liquido.core.common.utils.IpAddrUtils.getIpAddress()
     */
    public static final String getIpAddr(final HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip) && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }

    public static String getBrowser(final HttpServletRequest request) {
        final UserAgent.ImmutableUserAgent userAgent =
                USER_AGENT_ANALYZER.parse(request.getHeader("User-Agent"));
        return userAgent.get(UserAgent.AGENT_NAME_VERSION).getValue();
    }

    public static Map<String, String> getHeaderMap(final HttpServletRequest request) {
        final Map<String, String> returnMap = new HashMap<String, String>();
        final Enumeration<String> names = request.getHeaderNames();
        String header;
        while (names.hasMoreElements()) {
            header = names.nextElement();
            returnMap.put(header, request.getHeader(header));
        }
        return returnMap;
    }

    public static List<String> getIpv4List() {
        final List<String> ips = Lists.newArrayList();
        try {
            final Enumeration<NetworkInterface> interfaces;
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp()) {
                    final Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        final InetAddress nextElement = address.nextElement();
                        if (nextElement instanceof Inet4Address) {
                            if (nextElement.isLoopbackAddress()) {
                                continue;
                            }
                            ips.add(nextElement.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("WebUtil get ipv4 list error:", e);
        }

        Collections.sort(ips);
        return ips;
    }

    public static List<String> getIpv6List() {
        final List<String> ips = Lists.newArrayList();
        try {
            final Enumeration<NetworkInterface> interfaces;
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface ni = interfaces.nextElement();
                final Enumeration<InetAddress> address = ni.getInetAddresses();
                if (ni.isUp()) {
                    while (address.hasMoreElements()) {
                        final InetAddress nextElement = address.nextElement();
                        if (nextElement instanceof Inet6Address) {
                            if (nextElement.isLoopbackAddress()) {
                                continue;
                            }
                            ips.add(nextElement.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("WebUtil get ipv6 list error:", e);
        }

        Collections.sort(ips);
        return ips;
    }

    public static long inetAton(final String ipAddress) {
        if (StringUtils.isBlank(ipAddress)) {
            return 0L;
        }

        try {
            final byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
            long result = 0;
            for (final byte b : bytes) {
                result = result << 8 | (b & 0xFF);
            }
            return result;
        } catch (Exception e) {
            log.error("ipaddress inet aton error", e);
        }

        return 0L;
    }

    public static String inetNtoa(final long ip) {
        try {
            final byte[] bytes = new byte[] {
                    (byte) ((ip >> 24) & 0xFF),
                    (byte) ((ip >> 16) & 0xFF),
                    (byte) ((ip >> 8) & 0xFF),
                    (byte) (ip & 0xFF)
            };

            return InetAddress.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            log.error("ipaddress inet ntoa error", e);
        }

        return "";
    }


    public static byte[] ipv6ToBytes(final String ipv6Address) {
        try {
            final InetAddress inetAddress = InetAddress.getByName(ipv6Address);
            if (inetAddress instanceof Inet6Address) {
                return inetAddress.getAddress();
            } else {
                throw new IllegalArgumentException("Not a valid IPv6 address: " + ipv6Address);
            }
        } catch (Exception e) {
            log.error("ipv6 to bytes error", e);
        }
        return new byte[0];
    }

    public static String bytesToIpv6(final byte[] bytes) {
        if (Objects.isNull(bytes) || bytes.length <= 0) {
            return "";
        }

        try {
            final InetAddress inetAddress = InetAddress.getByAddress(bytes);
            if (inetAddress instanceof Inet6Address) {
                return inetAddress.getHostAddress();
            } else {
                throw new IllegalArgumentException("Not a valid IPv6 address bytes");
            }
        } catch (Exception e) {
            log.error("bytes to ipv6 error", e);
        }

        return "";
    }

    public static boolean checkIpv4Addr(final String ip) {
        final Pattern pattern = Pattern.compile(IP_PATTERN);
        final Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
