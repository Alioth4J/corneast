/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
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

package com.alioth4j.corneast_client.config;

import com.alioth4j.corneast_client.util.StringUtil;

/**
 * Config class for client to construct a <code>Corneast[B|N|A]ioClient</code>.
 *
 * @author Alioth Null
 */
public class CorneastConfig {

    /* server host */
    private String host;

    /* server port */
    private int port;

    public CorneastConfig() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if (!StringUtil.hasLength(host)) {
            throw new IllegalArgumentException("host must not be null or empty");
        }
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("port must between 0 and 65535, current port: " + port);
        }
        this.port = port;
    }

    /**
     * Performs a comprehensive check of fields.
     * @return true if the validation passes
     */
    public boolean validate() {
        if (!StringUtil.hasLength(host)) {
            return false;
        }
        if (port < 0 || port > 65535) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CorneastConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

}
