package com.gruastremart.api.utils.tools;

public class PaginationUtil {
    public static boolean isValidPagination(int page, int size) {
        return page >= 0 && size > 0;
    }
}
