/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 来源 SpringCloud-Commons {@link org.springframework.cloud.commons.util.InetUtils}.
 *
 * @author nieqiurong 2024年4月13日 20:49:47
 * @since 3.5.7
 */
public class NetUtils {

    private static final Log LOG = LogFactory.getLog(NetUtils.class);

    private final NetProperties netProperties;

    public NetUtils(NetProperties netProperties) {
        this.netProperties = netProperties;
    }

    public InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                 networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp()) {
                    if (!ignoreInterface(networkInterface.getDisplayName())) {
                        for (Enumeration<InetAddress> addresses = networkInterface
                            .getInetAddresses(); addresses.hasMoreElements(); ) {
                            InetAddress address = addresses.nextElement();
                            if (address instanceof Inet4Address
                                && !address.isLoopbackAddress()
                                && isPreferredAddress(address)) {
                                result = address;
                            }
                        }
                    }
                }
            }
        } catch (IOException exception) {
            LOG.error("Cannot get first non-loopback address", exception);
        }
        if (result != null) {
            return result;
        }
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to retrieve localhost");
        }
    }

    boolean isPreferredAddress(InetAddress address) {
        final List<String> preferredNetworks = this.netProperties.getPreferredNetworks();
        if (preferredNetworks.isEmpty()) {
            return true;
        }
        for (String regex : preferredNetworks) {
            final String hostAddress = address.getHostAddress();
            if (hostAddress.matches(regex) || hostAddress.startsWith(regex)) {
                return true;
            }
        }
        return false;
    }

    boolean ignoreInterface(String interfaceName) {
        List<String> ignoredInterfaces = this.netProperties.getIgnoredInterfaces();
        for (String regex : ignoredInterfaces) {
            if (interfaceName.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetProperties {

        /**
         * 首选网络地址 (例如: 192.168.1,支持正则)
         */
        private List<String> preferredNetworks = new ArrayList<>();

        /**
         * 忽略网卡(例如:eth0,,支持正则)
         */
        private List<String> ignoredInterfaces = new ArrayList<>();

    }
}
