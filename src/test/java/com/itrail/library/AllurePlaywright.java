package com.itrail.library;

import io.qameta.allure.Allure;
import com.microsoft.playwright.Page;
import java.io.ByteArrayInputStream;

public final class AllurePlaywright {

    private AllurePlaywright() {}

    public static void attachScreenshot(Page page, String name) {
        byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), "png");
    }

    public static void attachPageSource(Page page, String name) {
        Allure.addAttachment(name, "text/html", page.content(), "html");
    }

    public static void attachUrl(Page page) {
        Allure.addAttachment("URL", "text/plain", page.url());
    }
}
