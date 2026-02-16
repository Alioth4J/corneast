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

package com.alioth4j.corneast.common.algo;

import java.util.List;

/**
 * Abstract class for common fields and methods.
 * @param <T> element type
 * @author Alioth Null
 */
public abstract class AbstractSelector<T> implements Selector<T> {

    protected final List<T> list;
    protected final int size;

    protected AbstractSelector(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list must not be null or empty");
        }
        this.list = list;
        this.size = list.size();
    }

}
