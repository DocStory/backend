package com.ky.docstory.dto.history;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.entity.History;
import lombok.Getter;

@Getter
public enum HistoryFilter {

    MAIN("main"),
    SUB("sub"),
    ABAND("aband"),
    ALL("all");

    private final String value;

    HistoryFilter(String value) {
        this.value = value;
    }

    public static boolean matches(History.HistoryStatus status, String filterValue) {

        for (HistoryFilter filter : values()) {
            if (filter.value.equalsIgnoreCase(filterValue)) {
                return filter.statusCheck(status);
            }
        }

        throw new BusinessException(DocStoryResponseCode.PARAMETER_ERROR);
    }

    private boolean statusCheck(History.HistoryStatus status) {

        if (this == ALL) {
            return true;
        }
        if (this == MAIN) {
            return status == History.HistoryStatus.MAIN;
        }
        if (this == SUB) {
            return status == History.HistoryStatus.MAIN || status == History.HistoryStatus.NORMAL;
        }
        if (this == ABAND) {
            return status == History.HistoryStatus.MAIN || status == History.HistoryStatus.ABANDONED;
        }

        return false;
    }
}
