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

package com.alioth4j.corneast.core;

import com.alioth4j.corneast.common.misc.BannerPrinter;
import com.alioth4j.corneast.common.misc.LoggerBannerPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Corneast Core.
 *
 * @author Alioth Null
 */
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CorneastCoreApplication {

	private static final Logger log = LoggerFactory.getLogger(CorneastCoreApplication.class);

	public static void main(String[] args) {
		printBanner();
		SpringApplication.run(CorneastCoreApplication.class, args);
	}

	private static void printBanner() {
		BannerPrinter bannerPrinter = new LoggerBannerPrinter(log);
		bannerPrinter.print();
	}

}
