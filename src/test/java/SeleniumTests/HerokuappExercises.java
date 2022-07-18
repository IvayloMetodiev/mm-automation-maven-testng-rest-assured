package SeleniumTests;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.nio.channels.WritableByteChannel;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HerokuappExercises {

    WebDriver driver;
    Actions actions;
    JavascriptExecutor executor;
    WebDriverWait wait;

    @BeforeTest
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920x1080");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, 15);
        executor = (JavascriptExecutor) driver;
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void addRemoveElement() {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");

        List<WebElement> deleteButtonsList = driver.findElements(By.xpath("//div[@id='elements']/button"));

        Assert.assertEquals(deleteButtonsList.size(), 0);

        WebElement addBtn = driver.findElement(By.xpath("//div[@class='example']/button"));
        addBtn.click();

        deleteButtonsList = driver.findElements(By.xpath("//div[@id='elements']/button"));
        Assert.assertEquals(deleteButtonsList.size(), 1);

        addBtn = driver.findElement(By.xpath("//div[@class='example']/button"));
        addBtn.click();
        addBtn.click();
        addBtn.click();

        deleteButtonsList = driver.findElements(By.xpath("//div[@id='elements']/button"));
        Assert.assertEquals(deleteButtonsList.size(), 4);


        for (int i = 0; i < 4; i++) {
            WebElement deletedBtn = driver.findElement(By.xpath("//div[@id='elements']/button[1]"));
            deletedBtn.click();
        }

        deleteButtonsList = driver.findElements(By.xpath("//div[@id='elements']/button"));
        Assert.assertEquals(deleteButtonsList.size(), 0);


    }

    @Test
    public void basicAuth() throws InterruptedException {

        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");

        By congratsText = By.xpath("//p");

        Assert.assertEquals(driver.findElement(congratsText).getText(), "Congratulations! You must have the proper credentials.");

    }

    @Test
    public void checkBoxed() {
        driver.get("https://the-internet.herokuapp.com/checkboxes");

        WebElement checkboxOne = driver.findElement(By.xpath("//form/input[1]"));
        WebElement checkboxTwo = driver.findElement(By.xpath("//form/input[2]"));


        Assert.assertFalse(checkboxOne.isSelected());
        Assert.assertTrue(checkboxTwo.isSelected());

        checkboxOne.click();
        checkboxTwo.click();

        checkboxOne = driver.findElement(By.xpath("//form/input[1]"));
        checkboxTwo = driver.findElement(By.xpath("//form/input[2]"));

        Assert.assertTrue(checkboxOne.isSelected());
        Assert.assertFalse(checkboxTwo.isSelected());
    }

    @Test(invocationCount = 10)
    public void disappearingElementsFour() {

        driver.get("https://the-internet.herokuapp.com/disappearing_elements");

        List<WebElement> navigationElements = driver.findElements(By.xpath("//ul//li"));

        String check = "not done";

        while (check.equals("not done")) {

            if (navigationElements.size() == 4) {

                WebElement homeBtn = driver.findElement(By.xpath("//a[@href='/']"));
                WebElement aboutBtn = driver.findElement(By.xpath("//a[@href='/about/']"));
                WebElement contactUsBtn = driver.findElement(By.xpath("//a[@href='/contact-us/']"));
                WebElement portfolioBtn = driver.findElement(By.xpath("//a[@href='/portfolio/']"));

                Assert.assertTrue(homeBtn.isDisplayed());
                Assert.assertTrue(aboutBtn.isDisplayed());
                Assert.assertTrue(contactUsBtn.isDisplayed());
                Assert.assertTrue(portfolioBtn.isDisplayed());

                check = "done";

            } else {
                driver.get("https://the-internet.herokuapp.com/disappearing_elements");
                navigationElements = driver.findElements(By.xpath("//ul//li"));

            }

        }


    }

    @Test(invocationCount = 10)
    public void disappearingElementsFive() {

        driver.get("https://the-internet.herokuapp.com/disappearing_elements");

        List<WebElement> navigationElements = driver.findElements(By.xpath("//ul//li"));

        String check = "not done";

        while (check.equals("not done")) {

            if (navigationElements.size() == 5) {

                WebElement homeBtn = driver.findElement(By.xpath("//a[@href='/']"));
                WebElement aboutBtn = driver.findElement(By.xpath("//a[@href='/about/']"));
                WebElement contactUsBtn = driver.findElement(By.xpath("//a[@href='/contact-us/']"));
                WebElement portfolioBtn = driver.findElement(By.xpath("//a[@href='/portfolio/']"));
                WebElement galleryBtn = driver.findElement(By.xpath("//a[@href='/gallery/']"));

                Assert.assertTrue(homeBtn.isDisplayed());
                Assert.assertTrue(aboutBtn.isDisplayed());
                Assert.assertTrue(contactUsBtn.isDisplayed());
                Assert.assertTrue(portfolioBtn.isDisplayed());
                Assert.assertTrue(galleryBtn.isDisplayed());

                check = "done";

            } else {
                driver.get("https://the-internet.herokuapp.com/disappearing_elements");
                navigationElements = driver.findElements(By.xpath("//ul//li"));

            }

        }


    }

    @Test
    public void contextMenu() {

        driver.get("https://the-internet.herokuapp.com/context_menu");

        WebElement hotSpot = driver.findElement(By.cssSelector("#hot-spot"));
        Assert.assertTrue(hotSpot.isDisplayed());

        actions.contextClick(hotSpot).perform();
        Alert alert = driver.switchTo().alert();
        alert.getText();

        Assert.assertEquals(alert.getText(), "You selected a context menu");
        alert.accept();
    }

    @Test
    public void dragAndDrop() {
        driver.get("https://jqueryui.com/droppable/");

        WebElement iFrame = driver.findElement(By.xpath("//iframe[@class='demo-frame']"));
        driver.switchTo().frame(iFrame);

        WebElement boxA = driver.findElement(By.xpath("//div[@id='draggable']"));
        WebElement boxB = driver.findElement(By.xpath("//div[@id='droppable']"));

        Assert.assertEquals(boxA.getText(), "Drag me to my target");
        Assert.assertEquals(boxB.getText(), "Drop here");

        actions.dragAndDrop(boxA, boxB).perform();

        boxB = driver.findElement(By.xpath("//div[@id='droppable']"));
        Assert.assertEquals(boxB.getText(), "Dropped!");


    }

    @Test
    public void dropDowns() throws InterruptedException {
        driver.get("https://www.mobile.bg/");
        Thread.sleep(2000);

        WebElement gdprAgreementBtn = driver.findElement(By.xpath("//button[@class='fc-button fc-cta-consent fc-primary-button']"));
        gdprAgreementBtn.click();


        WebElement brand = driver.findElement(By.xpath("//select[@name='marka']"));
        WebElement model = driver.findElement(By.xpath("//select[@name='model']"));
        WebElement maxPrice = driver.findElement(By.xpath("//input[@name='price1']"));
        WebElement sortBy = driver.findElement(By.xpath("//select[@name='sort']"));
        WebElement newCarsCheckbox = driver.findElement(By.xpath("//input[@id='nup1']"));
        WebElement usedCarsCheckbox = driver.findElement(By.xpath("//input[@id='nup0']"));
        WebElement searchBtn = driver.findElement(By.xpath("//input[@name='button2']"));

        Select brandDropdown = new Select(brand);
        brandDropdown.selectByVisibleText("Honda");

        Select modelDropdown = new Select(model);
        modelDropdown.selectByVisibleText("Civic");

        maxPrice.sendKeys("10000");

        Select sortDropdown = new Select(sortBy);
        sortDropdown.selectByVisibleText("Цена");

        Assert.assertTrue(newCarsCheckbox.isSelected());
        Assert.assertTrue(usedCarsCheckbox.isSelected());

        searchBtn.click();

        WebElement yourSearchText = driver.findElement(By.xpath("//b[text()='Резултат от Вашето търсене на:']"));
        WebElement saveSearchBtn = driver.findElement(By.xpath("//a[@class='listFav']"));
        WebElement newSearchBtn = driver.findElement(By.xpath("//div/a[@class='clever-link navLinksTop'][1]"));
        WebElement editSearchBtn = driver.findElement(By.xpath("//div/a[@class='clever-link navLinksTop'][2]"));

        Assert.assertTrue(yourSearchText.isDisplayed());
        Assert.assertTrue(saveSearchBtn.isDisplayed());
        Assert.assertTrue(newSearchBtn.isDisplayed());
        Assert.assertTrue(editSearchBtn.isDisplayed());

        List<WebElement> carsResultList = driver.findElements(By.xpath("//td//a[@class='mmm']"));

        carsResultList.forEach(add -> {
            Assert.assertTrue(add.getText().contains("Honda Civic"));
        });
    }

    @Test
    public void dynamicContent() {

        driver.get("https://the-internet.herokuapp.com/dynamic_content");

        List<WebElement> imageWebList = driver.findElements(By.xpath("//div[@class='large-2 columns']/img"));
        List<WebElement> textsWebList = driver.findElements(By.xpath("//div[@class='large-10 columns']"));

        List<String> imageUrlsBefore = new ArrayList<>();
        for (WebElement element : imageWebList) {
            imageUrlsBefore.add(element.getAttribute("src"));
        }


        List<String> textsBefore = new ArrayList<>();
        for (WebElement element : textsWebList) {
            textsBefore.add(element.getText());
        }

        WebElement refreshBtn = driver.findElement(By.xpath("//a[@href='/dynamic_content?with_content=static']"));
        refreshBtn.click();

        List<WebElement> imageListAfter = driver.findElements(By.xpath("//div[@class='large-2 columns']/img"));
        List<WebElement> textListAfter = driver.findElements(By.xpath("//div[@class='large-10 columns']"));

        List<String> imageUrlsAfter = new ArrayList<>();
        for (WebElement element : imageListAfter) {
            imageUrlsAfter.add(element.getAttribute("src"));
        }

        List<String> textsAfter = new ArrayList<>();
        for (WebElement element : textListAfter) {
            textsAfter.add(element.getText());
        }

        for (int i = 0; i < imageUrlsAfter.size(); i++) {
            Assert.assertNotEquals(imageUrlsAfter.get(i), imageUrlsBefore.get(i));
        }

        for (int i = 0; i < textsAfter.size(); i++) {
            Assert.assertNotEquals(textsBefore.get(i), textsAfter.get(i));
        }
    }


    @Test
    public void floatingMenu() {

        driver.get("https://the-internet.herokuapp.com/floating_menu");

        WebElement floatingMenu = driver.findElement(By.xpath("//div[@id='menu']"));
        Assert.assertTrue(floatingMenu.isDisplayed());

        executor.executeScript("window.scrollBy(0,4000)", "");

        floatingMenu = driver.findElement(By.xpath("//div[@id='menu']"));
        Assert.assertTrue(floatingMenu.isDisplayed());

        executor.executeScript("window.scrollBy(0,2000)", "");

        floatingMenu = driver.findElement(By.xpath("//div[@id='menu']"));
        Assert.assertTrue(floatingMenu.isDisplayed());


    }

    @Test
    public void dynamicControls() {


        driver.get("https://the-internet.herokuapp.com/dynamic_controls");

        // Checkbox basic check
        WebElement checkBox = driver.findElement(By.xpath("//input[@label='blah']"));
        Assert.assertTrue(checkBox.isDisplayed());
        Assert.assertFalse(checkBox.isSelected());
        checkBox.click();
        checkBox = driver.findElement(By.xpath("//input[@label='blah']"));
        Assert.assertTrue(checkBox.isSelected());
        checkBox.click();
        checkBox = driver.findElement(By.xpath("//input[@label='blah']"));
        Assert.assertFalse(checkBox.isSelected());

        WebElement removeBtn = driver.findElement(By.xpath("//button[@onclick='swapCheckbox()']"));
        removeBtn.click();

        WebElement loader = driver.findElement(By.xpath("//div[@id='loading']"));
        Assert.assertTrue(loader.isDisplayed());

        WebElement removedCheckBoxConfirmation = driver.findElement(By.xpath("//p[@id='message']"));
        wait.until(ExpectedConditions.visibilityOf(removedCheckBoxConfirmation));
        Assert.assertTrue(removedCheckBoxConfirmation.isDisplayed());

        WebElement addBtn = driver.findElement(By.xpath("//button[@onclick='swapCheckbox()']"));
        addBtn.click();

        loader = driver.findElement(By.xpath("//div[@id='loading'][1]"));
        Assert.assertTrue(loader.isDisplayed());

        WebElement returnConfirmation = driver.findElement(By.xpath("//p[@id='message']"));
        wait.until(ExpectedConditions.visibilityOf(returnConfirmation));
        Assert.assertTrue(returnConfirmation.isDisplayed());


        checkBox = driver.findElement(By.xpath("//input[@id='checkbox']"));
        Assert.assertTrue(checkBox.isDisplayed());
        Assert.assertFalse(checkBox.isSelected());
        checkBox.click();
        checkBox = driver.findElement(By.xpath("//input[@id='checkbox']"));
        Assert.assertTrue(checkBox.isSelected());
        checkBox.click();
        checkBox = driver.findElement(By.xpath("//input[@id='checkbox']"));
        Assert.assertFalse(checkBox.isSelected());


    }

    @Test
    public void hovers() {

        driver.get("https://the-internet.herokuapp.com/hovers");

        WebElement profileOne = driver.findElement(By.xpath("//div[@class='figure'][1]"));
        actions.moveToElement(profileOne).perform();


        WebElement profileName = driver.findElement(By.xpath("//h5[1]"));
        WebElement profileLink = driver.findElement(By.xpath("//a[@href='/users/1']"));


        Assert.assertEquals(profileName.getText(), "name: user1");
        Assert.assertEquals(profileLink.getAttribute("href"), "https://the-internet.herokuapp.com/users/1");

        WebElement profileTwo = driver.findElement(By.xpath("//div[@class='figure'][2]"));

        actions.moveToElement(profileTwo).perform();
        profileName = driver.findElement(By.xpath("//div[2]/div/h5"));
        profileLink = driver.findElement(By.xpath("//a[@href='/users/2']"));

        Assert.assertEquals(profileName.getText(), "name: user2");
        Assert.assertEquals(profileLink.getAttribute("href"), "https://the-internet.herokuapp.com/users/2");

        WebElement profileThree = driver.findElement(By.xpath("//div[@class='figure'][3]"));

        actions.moveToElement(profileThree).perform();

        profileName = driver.findElement(By.xpath("//div[3]/div/h5"));
        profileLink = driver.findElement(By.xpath("//a[@href='/users/3']"));

        Assert.assertEquals(profileName.getText(), "name: user3");
        Assert.assertEquals(profileLink.getAttribute("href"), "https://the-internet.herokuapp.com/users/3");


    }

    @Test
    public void multipleWindows(){

        driver.get("https://the-internet.herokuapp.com/windows");

        String firstWinHandle = driver.getWindowHandle();

        WebElement newWindowBtn = driver.findElement(By.xpath("//a[@href='/windows/new']"));
        newWindowBtn.click();

        String secondWinHandle = driver.getWindowHandle();

        driver.switchTo().window(secondWinHandle);

        WebElement newWindowText = driver.findElement(By.xpath("//div[@class='example']//h3"));
        Assert.assertTrue(newWindowText.isDisplayed());

        driver.switchTo().window(firstWinHandle);

        WebElement firstWinHeadline = driver.findElement(By.xpath("//div[@class='example']//h3"));
        Assert.assertTrue(firstWinHeadline.isDisplayed());
        Assert.assertEquals(firstWinHeadline.getText(), "Opening a new window");

        newWindowBtn = driver.findElement(By.xpath("//a[@href='/windows/new']"));
        Assert.assertTrue(newWindowBtn.isDisplayed());

    }


    @Test
    public void redirectLink(){
        driver.get("https://the-internet.herokuapp.com/redirector");

        String originalUrl = driver.getCurrentUrl();

        WebElement redirectBtn = driver.findElement(By.xpath("//a[@id='redirect']"));
        redirectBtn.click();

        String currentUrl = driver.getCurrentUrl();

        WebElement newPageHeadline = driver.findElement(By.xpath("//div[@class='example']/h3"));
        Assert.assertTrue(newPageHeadline.isDisplayed());
        Assert.assertEquals(newPageHeadline.getText(), "Status Codes");

        List<WebElement> statusCodesList = driver.findElements(By.xpath("//ul//li"));

        Assert.assertEquals(statusCodesList.size(), 4);

        Assert.assertNotEquals(currentUrl, originalUrl);

    }

}





