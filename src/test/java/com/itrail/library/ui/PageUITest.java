package com.itrail.library.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.itrail.library.AllurePlaywright;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@UsePlaywright 
@Owner (value = "Barysevich K. A.")
@Epic (value = "Тестирование UI - Открытие страниц")
@DisplayName("Тестирование UI - Открытие страниц")
public class PageUITest {
    
    @Test
    @DisplayName("Скриншот главной страницы")
    public void testWebPage(Page page) {
        Allure.step( "Открыть главную страницу", () -> page.navigate( "http://localhost:8094/library/app/index.html" ));
        AllurePlaywright.attachScreenshot( page, "Главная страница" );
        AllurePlaywright.attachUrl( page );
    }


    @Test
    @DisplayName("Скриншот страницы - Авторизация")
    public void testWebPageLogin(Page page) {
        Allure.step( "Открыть страницу авторизации", () -> page.navigate( "http://localhost:8094/library/login" ));
        AllurePlaywright.attachScreenshot( page, "Авторизация" );
        AllurePlaywright.attachUrl( page );
    }

    @Test
    @DisplayName("Скриншот страницы - Восстановление пароля")
    public void testWebPageChangePassword( Page page ) {
        Allure.step( "Открыть страницу восстановления пароля", () -> page.navigate( "http://localhost:8094/library/change-password" ));
        AllurePlaywright.attachScreenshot( page, "Воссталновление пароля" );
        AllurePlaywright.attachUrl( page );
    }

    @Test
    @DisplayName("Скриншот страницы - Регистрация")
    public void testWebPageRegister( Page page ) {
        Allure.step( "Открыть страницу регисрации", () -> page.navigate( "http://localhost:8094/library/register" ));
        AllurePlaywright.attachScreenshot( page, "Регистрация" );
        AllurePlaywright.attachUrl( page );
    }

    @Test
    @DisplayName("Скриншот страницы - Страница не найдена")
    public void testWebPageNotFound( Page page ) {
        Allure.step( "Открыть страницу - страница не найдена", () -> page.navigate( "http://localhost:8094/library/loginasd" ));
        AllurePlaywright.attachScreenshot( page, "Страница не найдена" );
        AllurePlaywright.attachUrl( page );
    }
}
