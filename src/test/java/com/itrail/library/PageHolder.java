package com.itrail.library;

import com.microsoft.playwright.Page;

public class PageHolder {
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    public static void set(Page page) {
        PAGE.set(page);
    }

    public static Page get() {
        return PAGE.get();
    }

    public static void clear() {
        PAGE.remove();
    }
}
