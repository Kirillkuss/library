package com.itrail.library;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import java.io.ByteArrayInputStream;

public class AllureOnFailureExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();
        Page page = PageHolder.get();
        if (page != null) {
            try {
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment("Скриншот при падении", "image/png",new ByteArrayInputStream(png), "png");
                Allure.addAttachment("HTML при падении", "text/html", page.content(), "html");
                Allure.addAttachment("URL при падении", "text/plain", page.url());
            } catch (Exception ignored) { }
        }
    }
}