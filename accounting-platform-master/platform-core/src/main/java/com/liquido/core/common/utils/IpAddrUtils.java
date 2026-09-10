package com.liquido.core.common.utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
@SuppressWarnings({"PMD.UseUtilityClass"})
public class IpAddrUtils {
    private static final Predicate<String> IP_ADDRESS_PREDICATE = ipAddress ->
            StringUtils.isNotBlank(ipAddress) && !"unknown".equalsIgnoreCase(ipAddress);

    public static String getIpAddress(final HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader("cloudfront-viewer-address"))
                .filter(IP_ADDRESS_PREDICATE)
                .map(IpAddrUtils::subStringBeforeLastColonIpv4OrIpv6)
                .or(() -> Optional.ofNullable(request.getHeader("X-Forwarded-For")))
                .filter(IP_ADDRESS_PREDICATE)
                .or(() -> Optional.ofNullable(request.getRemoteAddr()))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(ips -> ips[0])
                .orElse("unknown");
    }

    public static String getFrontendRiskData(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Frontend-Risk-Data"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .orElse("{}");
    }

    private static String subStringBeforeLastColonIpv4OrIpv6(final String ipAddr) {
        if (StringUtils.isNotBlank(ipAddr)) {
            int lastColonIndex = ipAddr.lastIndexOf(":");
            if (lastColonIndex != -1) {
                return ipAddr.substring(0, lastColonIndex);
            }
        }

        return null;
    }

    public static boolean isIpv4Addr(final String ipv4Addr) {
        try {
            if (StringUtils.isNotBlank(ipv4Addr)
                    && InetAddress.getByName(ipv4Addr) instanceof java.net.Inet4Address) {
                return true;
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException:ipv4Addr=[" + ipv4Addr + "]," + e.getMessage());
        }

        return false;
    }

    public static boolean isIpv6Addr(final String ipv6Addr) {
        try {
            if (StringUtils.isNotBlank(ipv6Addr)
                    && InetAddress.getByName(ipv6Addr) instanceof java.net.Inet6Address) {
                return true;
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException:ipv6Addr=[" + ipv6Addr + "]," + e.getMessage());
        }

        return false;
    }

    public static boolean checkIpv4InRange(final String ipv4Addr,
                                           final String ipStart,
                                           final String ipEnd) {
        try {
            if (isIpv4Addr(ipv4Addr) && isIpv4Addr(ipStart) && isIpv4Addr(ipEnd)) {

                final InetAddress ipAddress = InetAddress.getByName(ipv4Addr);
                final InetAddress startAddress = InetAddress.getByName(ipStart);
                final InetAddress endAddress = InetAddress.getByName(ipEnd);

                final long ipLong = ipToLong(ipAddress);
                final long ipStartLong = ipToLong(startAddress);
                final long ipEndLong = ipToLong(endAddress);
                return ipLong >= ipStartLong && ipLong <= ipEndLong;
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException:ipv4Addr=[" + ipv4Addr + "]," + e.getMessage());
        }

        return false;
    }

    public static boolean checkIpv4CidrInRange(final String ipv4Addr,
                                               final String cidr) {
        try {
            if (isIpv4Addr(ipv4Addr)) {
                final String[] parts = StringUtils.split(cidr, "/");
                final String cidrAddress = parts[0];
                final int prefixLength = Integer.parseInt(parts[1]);

                final InetAddress inetAddress = InetAddress.getByName(ipv4Addr);
                final InetAddress cidrInetAddress = InetAddress.getByName(cidrAddress);

                final byte[] addressBytes = inetAddress.getAddress();
                final byte[] cidrBytes = cidrInetAddress.getAddress();

                final int addressInt = byteArrayToInt(addressBytes);
                final int cidrInt = byteArrayToInt(cidrBytes);

                final int mask = -1 << (32 - prefixLength);

                return (addressInt & mask) == (cidrInt & mask);
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException:ipv4Addr=[" + ipv4Addr + "]," + e.getMessage());
        }

        return false;
    }

    public static boolean checkIpv4CidrInRange2(String ipv4Addr, String cidr) {
        try {
            if (isIpv4Addr(ipv4Addr)) {
                final String[] ipAndMask = cidr.split("/");
                final int mask = Integer.parseInt(ipAndMask[1]);
                final int ipInt = ipToInt(ipAndMask[0]);
                final int network = ipInt & -1 << (32 - mask);
                final int broadcast = network | (~(-1 << (32 - mask)));
                final int testIpInt = ipToInt(ipv4Addr);

                return testIpInt >= network && testIpInt <= broadcast;
            }
        } catch (Exception e) {
            log.error("Exception:ipv4Addr=[" + ipv4Addr + "]," + e.getMessage());
        }

        return false;
    }

    private static int ipToInt(String ip) {
        final String[] octets = ip.split("\\.");
        return (Integer.parseInt(octets[0]) << 24)
                | (Integer.parseInt(octets[1]) << 16)
                | (Integer.parseInt(octets[2]) << 8)
                | Integer.parseInt(octets[3]);
    }

    public static boolean checkIpv4CidrFormat(final String cidr) {
        try {
            final String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            final InetAddress inetAddress = InetAddress.getByName(parts[0]);
            if (inetAddress == null) {
                return false;
            }


            final int prefixLength = Integer.parseInt(parts[1]);
            if (prefixLength < 0 || prefixLength > 32) {
                return false;
            }

            return true;
        } catch (UnknownHostException | NumberFormatException e) {
            return false;
        }
    }

    public static int byteArrayToInt(byte[] bytes) {
        int result = 0;
        for (byte b : bytes) {
            result = result << 8 | (b & 0xFF);
        }
        return result;
    }

    private static long ipToLong(final InetAddress ipAddr) {
        final byte[] ipBytes = ipAddr.getAddress();
        long ipLong = 0;
        for (byte b : ipBytes) {
            ipLong <<= 8;
            ipLong |= b & 0xFF;
        }

        return ipLong;
    }


    public static boolean checkIpv6InRange(final String ipv6Addr,
                                           final String startIp,
                                           final String endIp) {
        try {
            if (isIpv6Addr(ipv6Addr) && isIpv6Addr(startIp) && isIpv6Addr(endIp)) {
                final InetAddress ipAddress = InetAddress.getByName(ipv6Addr);
                final InetAddress startIpAddress = InetAddress.getByName(startIp);
                final InetAddress endIpAddress = InetAddress.getByName(endIp);

                final BigInteger ipNum = ipToBigInteger(ipAddress);
                final BigInteger startIpNum = ipToBigInteger(startIpAddress);
                final BigInteger endIpNum = ipToBigInteger(endIpAddress);

                return (ipNum.compareTo(startIpNum) >= 0 && ipNum.compareTo(endIpNum) <= 0);
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException:ipv6Addr=[" + ipv6Addr + "]," + e.getMessage());
        }

        return false;
    }

    public static boolean checkIpv6CidrInRange(final String ipv6Addr,
                                               final String cidr) {
        try {
            if (isIpv6Addr(ipv6Addr)) {
                final String[] parts = StringUtils.split(cidr, "/");
                final String cidrAddress = parts[0];
                final int prefixLength = Integer.parseInt(parts[1]);

                final InetAddress inetAddress = InetAddress.getByName(ipv6Addr);
                final InetAddress cidrInetAddress = InetAddress.getByName(cidrAddress);

                final byte[] addressBytes = inetAddress.getAddress();
                final byte[] cidrBytes = cidrInetAddress.getAddress();

                final BigInteger addressInt = new BigInteger(1, addressBytes);
                final BigInteger cidrInt = new BigInteger(1, cidrBytes);

                final BigInteger mask = BigInteger.valueOf(-1).shiftLeft(128 - prefixLength);

                return addressInt.and(mask).equals(cidrInt.and(mask));
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException:ipv6Addr=[" + ipv6Addr + "]," + e.getMessage());
        }

        return false;
    }


    private static BigInteger ipToBigInteger(InetAddress ipAddr) {
        byte[] octets = ipAddr.getAddress();
        return new BigInteger(1, octets);
    }

    public static void main(String[] args) {

        String ip = "192.168.200.200";
        String cidr = "192.168.1.10/32";


        System.out.println(checkIpv4CidrFormat(cidr));
        System.out.println(checkIpv4CidrInRange(ip, cidr));
        System.out.println(checkIpv4CidrInRange2(ip, cidr));

    }
}
