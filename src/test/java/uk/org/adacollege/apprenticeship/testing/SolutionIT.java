package uk.org.adacollege.apprenticeship.testing;

import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Function;

public class SolutionIT {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String startUrl;
    private static String myWhipbirdsMenuId = "my-whipbirds-menu";
    private static String aboutMenuId = "about-menu";
    private static String logOutMenuId = "log-out-menu";
    private static String logInMenuId = "log-in-menu";
    private static String emailInputId = "email";
    private static String passwordInputId = "password";
    private static String validEmail = "quinn.stevens@adacollege.org.uk";
    private static String invalidEmail = validEmail + ".nothing";
    private static String validPassword = "whipit";
    private static String invalidPassword = validPassword + "-invalid";
    private static String logInButtonId = "login-button";
    private static String logOutButtonId = "log-out-button";
    private static String popupMessageId = "popup-message";
    private static String footerRightId = "footer-right";
    private static String whipbirdNameFieldId = "name";
    private static String whipbirdAgeFieldId = "age";
    private static String newWhipbirdButtonId = "add-new-whipbird-button";
    private static String whipbirdName = "Geraldine";
    private static String whipbirdAge = "52";

    // ========= UTILITY METHODS =========

    /**
     * Source & usage: https://stackoverflow.com/a/5709805
     */
    private static Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
        return new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        };
    }

    private static void logIn(Boolean withValidCredentials) {
        String email = withValidCredentials ? validEmail : invalidEmail;
        String password = withValidCredentials ? validPassword : invalidPassword;

        wait.until(presenceOfElementLocated(By.id(logInMenuId)));
        driver.findElement(By.id(logInMenuId)).click();

        wait.until(presenceOfElementLocated(By.id(emailInputId)));
        driver.findElement(By.id(emailInputId)).sendKeys(email);

        wait.until(presenceOfElementLocated(By.id(passwordInputId)));
        driver.findElement(By.id(passwordInputId)).sendKeys(password);

        wait.until(presenceOfElementLocated(By.id(logInButtonId)));
        driver.findElement(By.id(logInButtonId)).click();

        if (withValidCredentials) {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.getTitle().equals("whipbird: my whipbirds");
                }
            });
        }
    }

    private static void logOut() {
        Boolean isLoggedIn = (driver.findElements(By.id(logOutMenuId)).size() > 0);

        if (isLoggedIn) {
            wait.until(presenceOfElementLocated(By.id(logOutMenuId)));
            driver.findElement(By.id(logOutMenuId)).click();

            wait.until(presenceOfElementLocated(By.id(logOutButtonId)));
            driver.findElement(By.id(logOutButtonId)).click();
        }
    }

    private static void assertElementPresent(String elementId) {
        wait.until(presenceOfElementLocated(By.id(elementId)));
        assertTrue(driver.findElements(By.id(elementId)).size() == 1);
    }

    private static void assertElementNotPresent(String elementId) {
        assertTrue(driver.findElements(By.id(elementId)).size() == 0);
    }

    private static void assertTitleEquals(String expectedTitle) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getTitle().equals(expectedTitle);
            }
        });
        assertTrue(result);
    }

    private static void assertUrlEquals(String expectedUrl) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) { return driver.getCurrentUrl().equals(expectedUrl); }
        });
        assertTrue(result);
    }

    private static void assertElementTextEquals(By selector, String expectedText) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) { return driver.findElement(selector).getText().equals(expectedText); }
        });
        assertTrue(result);
    }

    private static void deleteAllWhipbirds() {
        List<WebElement> whipbirds = driver.findElements(By.className("delete-whipbird-button"));

        for (WebElement whipbird : whipbirds) {
            whipbird.click();
        }
    }

    private static void addNewWhipbird(String name, String age) {
        wait.until(presenceOfElementLocated(By.id(whipbirdNameFieldId)));
        driver.findElement(By.id(whipbirdNameFieldId)).sendKeys(name);
        wait.until(presenceOfElementLocated(By.id(whipbirdAgeFieldId)));
        driver.findElement(By.id(whipbirdAgeFieldId)).sendKeys(age);
        wait.until(presenceOfElementLocated(By.id(newWhipbirdButtonId)));
        driver.findElement(By.id(newWhipbirdButtonId)).click();
    }

    // ========= SCAFFOLDING =========

    @BeforeClass
    public static void beforeAll() {
        startUrl = "http://whipbird.mattcalthrop.com/";
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @AfterClass
    public static void afterAll() {
        driver.close();
        driver.quit();
    }

    @Before
    public void beforeEach() {
        driver.get(startUrl);
    }

    @After
    public void afterEach() {
        logOut();
    }

    // ========= TESTS =========

    // --------- WHEN NOT LOGGED IN ---------

    // Step 1
    @Test
    public void notLoggedIn_checkMenus() {
        assertElementPresent(logInMenuId);
        assertElementNotPresent(logOutMenuId);
        assertElementPresent(aboutMenuId);
        assertElementNotPresent(myWhipbirdsMenuId);
    }

    // Step 2
    @Test
    public void notLoggedIn_checkCurrentPage() {
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/login");
        assertTitleEquals("whipbird: log in");
        assertElementTextEquals(By.tagName("h4"), "Log in");
        assertElementTextEquals(By.id(footerRightId), "");
    }

    // Step 3
    @Test
    public void notLoggedIn_clickAboutMenu() {
        wait.until(presenceOfElementLocated(By.id(aboutMenuId)));
        driver.findElement(By.id(aboutMenuId)).click();
        assertTitleEquals("whipbird: about");
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/about");
        assertElementTextEquals(By.tagName("h4"), "About this app");
    }

    // Step 4
    @Test
    public void notLoggedIn_logInWithIncorrectCredentials() {
        logIn(false);
        // check menu items
        assertElementPresent(logInMenuId);
        assertElementNotPresent(logOutMenuId);
        assertElementPresent(aboutMenuId);
        assertElementNotPresent(myWhipbirdsMenuId);
        // check url & title
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/login");
        assertTitleEquals("whipbird: log in");
        // check error popup
        assertElementTextEquals(By.id(popupMessageId), "Username or password incorrect");
    }

    // --------- WHEN LOGGED IN ---------

    // Step 5
    @Test
    public void loggedIn_checkMenus() {
        logIn(true);
        assertElementPresent(myWhipbirdsMenuId);
        assertElementPresent(aboutMenuId);
        assertElementPresent(logOutMenuId);
        assertElementNotPresent(logInMenuId);
    }

    // Step 6
    @Test
    public void loggedIn_checkCurrentPage() {
        logIn(true);
        assertTitleEquals("whipbird: my whipbirds");
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/my-whipbirds");
    }

    // Step 7
    @Test
    public void loggedIn_clickLogOutMenu() {
        logIn(true);
        wait.until(presenceOfElementLocated(By.id(logOutMenuId)));
        driver.findElement(By.id(logOutMenuId)).click();
        assertTitleEquals("whipbird: log out");
        assertUrlEquals("http://whipbird.mattcalthrop.com/#!/logout");
    }

    // Step 8
    @Test
    public void loggedIn_addNewWhipbird() {
        // get to start state
        logIn(true);
        deleteAllWhipbirds();
        assertElementPresent("no-whipbirds-saved");

        // create whipbird
        addNewWhipbird(whipbirdName, whipbirdAge);

        // check the whipbird has been added
        wait.until(presenceOfElementLocated(By.id("whipbird-name-0")));
        assertElementTextEquals(By.id("whipbird-name-0"), whipbirdName);
    }

    // Step 9
    @Test
    public void loggedIn_addNewWhipbirdThenDeleteIt() {
        // get to start state
        logIn(true);
        deleteAllWhipbirds();
        assertElementPresent("no-whipbirds-saved");

        // create whipbird
        addNewWhipbird(whipbirdName, whipbirdAge);

        // check whipbird has been added
        wait.until(presenceOfElementLocated(By.id("whipbird-name-0")));
        assertElementTextEquals(By.id("whipbird-name-0"), whipbirdName);

        // delete whipbird
        wait.until(presenceOfElementLocated(By.id("delete-whipbird-button-0")));
        driver.findElement(By.id("delete-whipbird-button-0")).click();

        // check whipbird has been deleted
        assertElementPresent("no-whipbirds-saved");

    }
}
