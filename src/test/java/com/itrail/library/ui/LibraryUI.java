package com.itrail.library.ui;

import com.microsoft.playwright.junit.UsePlaywright;
import com.itrail.library.AllurePlaywright;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;
import java.io.ByteArrayInputStream;

@UsePlaywright 
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование UI")
@DisplayName("Тестирование UI")
public class LibraryUI {
    
    @Test
    @Story("Навигация по каталогу")
    @DisplayName("Проверка навигации по страницам и вкладкам")
    public void libraryWebTest(Page page) {

        Allure.step("Открыть главную страницу", () -> {
            page.navigate("http://localhost:8094/library/app/index.html");
        });

        Allure.step("Перейти по страницам каталога 2 → 3 → 4", () -> {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("2")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("3")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("4")).click();
        });

        Allure.step("Вернуться назад и открыть вкладку Авторы", () -> {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("")).click();
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Авторы")).click();
        });

        Allure.step("Открыть вкладку Карты пользователей и выполнить поиск", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Карты пользователей")).click();
            Allure.step("Поиск карты 'Test1234!'", () -> {
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Поиск карт")).click();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Поиск карт")).fill("Test1234!");
            });
        });

        Allure.step("Выйти из системы и запросить восстановление пароля", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Выход")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Забыли пароль?")).click();
        });

        Allure.step("Отправить запрос на новый пароль для логина '2'", () -> {
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Логин, email или телефон")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Логин, email или телефон")).fill("2");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(" Отправить новый пароль")).click();
        });
    }


  @Test
  @DisplayName("Скриншот главной страницы")
  public void testWebPage(Page page) {
        Allure.step("Открыть страницу логина", () ->
            page.navigate("http://localhost:8094/library/app/index.html"));

        Allure.parameter("Пользователь", "admin");
        AllurePlaywright.attachScreenshot(page, "Главная страница");
        AllurePlaywright.attachUrl(page);
  }
}
