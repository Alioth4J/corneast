/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alioth4j.corneast.client.eureka;

import com.alioth4j.corneast.client.util.EurekaUtil;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Support Eureka instance infos consumption.
 *
 * @author Alioth Null
 */
public class EurekaConsumer {

    private final EurekaClient eurekaClient;

    private final String applicationName;

    /* default constants */
    private static final String defaultApplicationName = "corneast-core";
    private static final String defaultHost = "localhost";
    private static final String defaultIp = "127.0.0.1";
    private static final int defaultNonSecurePort = 0;
    private static final String[] defaultEurekaServerUrls = { "http://localhost:8761/eureka/" };

    /**
     * Constructs <code>this</code> and <code>EurekaClient</code> with default values.
     */
    public EurekaConsumer() {
        this(defaultApplicationName, defaultHost, defaultIp, defaultNonSecurePort, defaultEurekaServerUrls);
    }

    /**
     * Constructs <code>this</code> and <code>EurekaClient</code>.
     * @param host client host
     * @param ip client ip
     * @param nonSecurePort client port
     * @param serverUrls Eureka server service urls
     */
    public EurekaConsumer(String applicationName, String host, String ip, int nonSecurePort, String... serverUrls) {
        this.applicationName = Objects.requireNonNull(EurekaUtil.trimToNull(applicationName), "applicationName must not be null or empty");
        final String finalHost = Objects.requireNonNull(EurekaUtil.trimToNull(host), "host must not be null or empty");
        if (!EurekaUtil.isValidIp(ip)) {
            throw new IllegalArgumentException("ip format is not valid: " + ip);
        }
        if (nonSecurePort < 0 || nonSecurePort > 65535) {
            throw new IllegalArgumentException("port is not valid: " + nonSecurePort);
        }
        if (serverUrls == null || serverUrls.length < 1) {
            throw new IllegalArgumentException("serverUrls must not be null or empty");
        }

        MyDataCenterInstanceConfig instanceConfig = new MyDataCenterInstanceConfig() {
            @Override
            public String getAppname() {
                return EurekaConsumer.this.applicationName;
            }

            @Override
            public String getInstanceId() {
                return "corneast-client-" + UUID.randomUUID().toString();
            }

            @Override
            public String getHostName(boolean refresh) {
                return finalHost;
            }

            @Override
            public int getNonSecurePort() {
                return nonSecurePort;
            }
        };
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setAppName(instanceConfig.getAppname())
                .setDataCenterInfo(() -> DataCenterInfo.Name.MyOwn)
                .setInstanceId(instanceConfig.getInstanceId())
                .setHostName(instanceConfig.getHostName(false))
                .setIPAddr(ip)
                .setPort(instanceConfig.getNonSecurePort())
                .build();
        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        DefaultEurekaClientConfig clientConfig = new DefaultEurekaClientConfig() {
            @Override
            public List<String> getEurekaServerServiceUrls(String myZone) {
                return Arrays.asList(serverUrls);
            }

            @Override
            public boolean shouldFetchRegistry() {
                return true;
            }

            @Override
            public boolean shouldRegisterWithEureka() {
                return false;
            }
        };
        this.eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
    }

//    public EurekaClient getClient() {
//        return eurekaClient;
//    }

    /**
     * Gets instance infos from Eureka.
     * @return instance infos
     */
    public List<InstanceInfo> getInstanceInfos() {
        return getInstanceInfos(applicationName);
    }

    /**
     * Gets instance infos from Eureka with designated application name.
     * @param applicationName designated application name
     * @return instance infos
     */
    public List<InstanceInfo> getInstanceInfos(String applicationName) {
        Application application = eurekaClient.getApplication(applicationName);
        return application.getInstances();
    }

}
