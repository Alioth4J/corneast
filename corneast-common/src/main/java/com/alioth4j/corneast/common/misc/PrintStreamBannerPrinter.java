package com.alioth4j.corneast.common.misc;

import java.io.PrintStream;

public class PrintStreamBannerPrinter implements BannerPrinter {

    private final PrintStream printStream;

    public PrintStreamBannerPrinter() {
        printStream = System.out;
    }

    public PrintStreamBannerPrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void print() {
        printStream.println(CORNEAST_BANNER);
    }

}
