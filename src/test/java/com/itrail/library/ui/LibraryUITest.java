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

//@Disabled 
@UsePlaywright 
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование UI")
@DisplayName("Тестирование UI")
public class LibraryUITest {
    
    @Test
    @DisplayName("Проверка навигации по вкладкам")
    public void navigatePageTest(Page page) {
        Allure.step("Открыть главную страницу - Вкладка: Пользователи", () -> {
            page.navigate("http://localhost:8094/library/app/index.html");
        });
        AllurePlaywright.attachScreenshot(page, "Вкладка: Пользователи");

        Allure.step("Открыть вкладку: Авторы", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Авторы")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Авторы");

        Allure.step("Открыть вкладку: Книги", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Книги")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Книги");

        Allure.step("Открыть вкладку: Выдача Книг", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Выдача книг")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Выдача Книг");

        Allure.step("Открыть вкладку: Карты пользователей", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Карты пользователей")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Карты пользователей");

        Allure.step("Открыть вкладку: Трансляции", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Трансляция")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Трансляции");

        Allure.step("Открыть вкладку: Записи", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Запись")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Записи");
    }


    @Test
    @DisplayName("Проверка ленивой загрузки данных")
    public void clickTest( Page page ){
        Allure.step("Открыть главную страницу - Вкладка: Пользователи", () -> {
            page.navigate("http://localhost:8094/library/app/index.html");
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Пользователи - страница 1");

        Allure.step("Вкладка: Пользователи - Перейти с 1 → 2", () -> {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("2")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Пользователи - страницы 1 → 2");
        
        Allure.step("Вкладка: Пользователи - Перейти с 2 → 3", () -> {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("3")).click();
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Пользователи - страницы 2 → 3");

        /**Allure.step("Открыть вкладку Карты пользователей и выполнить поиск", () -> {
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(" Карты пользователей")).click();
            Allure.step("Поиск карты 'Test1234!'", () -> {
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Поиск карт")).click();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Поиск карт")).fill("Test1234!");
            });
        });
        AllurePlaywright.attachScreenshot( page, "Вкладка: Карты пользователей");*/

    }

}
