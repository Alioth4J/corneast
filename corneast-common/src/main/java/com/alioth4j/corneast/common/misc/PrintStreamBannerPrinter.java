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
