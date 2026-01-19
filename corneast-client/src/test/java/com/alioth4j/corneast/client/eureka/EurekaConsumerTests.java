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

import com.netflix.appinfo.InstanceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class EurekaConsumerTests {

//    // for debug
//    @Test
//    void printInstanceInfos() {
//        EurekaConsumer eurekaConsumer = new EurekaConsumer();
//        List<InstanceInfo> instanceInfos = eurekaConsumer.getInstanceInfos();
//        if (instanceInfos == null) {
//            System.err.println("instanceInfos is null");
//            return;
//        }
//        for (InstanceInfo instanceInfo : instanceInfos) {
//            System.out.println(instanceInfo);
//        }
//    }

    @Test
    void testGetInstanceInfosSuccessfully() {
        List<InstanceInfo> instanceInfos = new EurekaConsumer().getInstanceInfos();
        Assertions.assertNotNull(instanceInfos);
        Assertions.assertNotEquals(0, instanceInfos.size());
    }

}
