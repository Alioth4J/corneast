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
import java.util.UUID;

public class EurekaConsumer {

    private final EurekaClient eurekaClient;

    private static final String[] defaultEurekaServerUrls = { "http://localhost:8761/eureka/" };

    public EurekaConsumer() {
        this("localhost", "127.0.0.1", 0, defaultEurekaServerUrls);
    }

    public EurekaConsumer(String host, String ip, int nonSecurePort, String... serverUrls) {
        MyDataCenterInstanceConfig instanceConfig = new MyDataCenterInstanceConfig() {
            @Override
            public String getAppname() {
                return getInstanceId();
            }

            @Override
            public String getInstanceId() {
                return "corneast-client-" + UUID.randomUUID().toString();
            }

            @Override
            public String getHostName(boolean refresh) {
                return host;
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

    public List<InstanceInfo> getInstanceInfos() {
        Application application = eurekaClient.getApplication("corneast-core");
        return application.getInstances();
    }

}
