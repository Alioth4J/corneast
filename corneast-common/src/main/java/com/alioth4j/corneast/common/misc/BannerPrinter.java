package com.alioth4j.corneast.common.misc;

public interface BannerPrinter {

    public static final String CORNEAST_BANNER = """
        /**
         * ▄▖▄▖▄▖▖ ▖▄▖▄▖▄▖▄▖    ▜ ▘  ▗ ▌ ▖▖ ▘
         * ▌ ▌▌▙▘▛▖▌▙▖▌▌▚ ▐   ▀▌▐ ▌▛▌▜▘▛▌▙▌ ▌  ▛▘▛▌▛▛▌
         * ▙▖▙▌▌▌▌▝▌▙▖▛▌▄▌▐ ▗ █▌▐▖▌▙▌▐▖▌▌ ▌ ▌▗ ▙▖▙▌▌▌▌
         *                                 ▙▌
         * @author Alioth Null
         */
        """;

    void print();

}
