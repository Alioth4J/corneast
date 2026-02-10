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

package com.alioth4j.corneast.common.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerBannerPrinter implements BannerPrinter {

    private final Logger logger;

    public LoggerBannerPrinter() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public LoggerBannerPrinter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void print() {
        logger.info(System.lineSeparator() + CORNEAST_BANNER);
    }

}
