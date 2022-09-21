/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.automationanywhere.toolchain.runtime.operation.DesktopOperationTablePage;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationTableRequest.PageRequest;
import java.util.List;

public class PageUtils {
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LENGTH = 1000;

    public static <R> Page<R> paginate(List<R> rows) {
        return paginate(rows, DEFAULT_OFFSET, DEFAULT_LENGTH);
    }

    public static <R> Page<R> paginate(List<R> rows, PageRequest pageRequest) {
        return pageRequest == null
                ? paginate(rows, DEFAULT_OFFSET, DEFAULT_LENGTH)
                : paginate(rows, pageRequest.getOffset(), pageRequest.getLength());
    }

    public static <R> Page<R> paginate(List<R> rows, int offset, int length) {
        // Normalizing offset just in case it is out of bounds
        int startInclusive = min(max(offset, 0), rows.size());
        int endExclusive = max(min(rows.size(), offset + length), 0);
        return new Page<>(
                rows.subList(startInclusive, endExclusive),
                new DesktopOperationTablePage(startInclusive, rows.size(), rows.size(), length));
    }

    public static class Page<R> {
        private final List<R> rows;
        private final DesktopOperationTablePage pagination;

        public Page(List<R> rows, DesktopOperationTablePage pagination) {
            this.rows = rows;
            this.pagination = pagination;
        }

        public List<R> getRows() {
            return rows;
        }

        public DesktopOperationTablePage getPagination() {
            return pagination;
        }
    }
}
