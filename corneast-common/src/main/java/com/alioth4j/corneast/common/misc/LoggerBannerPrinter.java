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
