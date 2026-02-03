package com.alioth4j.corneast.common.misc;

import org.slf4j.Logger;

import java.io.PrintStream;

public class Banner {

    private static final String CORNEAST_BANNER = """
        
        /**
         * ▄▖▄▖▄▖▖ ▖▄▖▄▖▄▖▄▖    ▜ ▘  ▗ ▌ ▖▖ ▘
         * ▌ ▌▌▙▘▛▖▌▙▖▌▌▚ ▐   ▀▌▐ ▌▛▌▜▘▛▌▙▌ ▌  ▛▘▛▌▛▛▌
         * ▙▖▙▌▌▌▌▝▌▙▖▛▌▄▌▐ ▗ █▌▐▖▌▙▌▐▖▌▌ ▌ ▌▗ ▙▖▙▌▌▌▌
         *                                 ▙▌
         * @author Alioth Null
         */
        """;

    public void print() {
        print(System.out);
    }

    public void print(PrintStream printStream) {
        printStream.println(CORNEAST_BANNER);
    }

    public void print(Logger logger) {
        logger.info(CORNEAST_BANNER);
    }

}
