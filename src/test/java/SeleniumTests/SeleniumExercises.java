package SeleniumTests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.swing.*;
import java.security.SecureRandom;
import java.time.Duration;

public class SeleniumExercises {

    public Actions actions;

    WebDriver driver;
    WebElement signInText;
    WebElement usernameOrEmailInput;
    WebElement passwordInput;
    WebElement signInBtn;
    WebElement registerBtn;
    WebElement profileBtn;
    WebElement logoutBtn;
    WebElement newPostBtn;
    WebElement username;
    WebElement email;
    String defaultPassword = "Qwerty1";
    String usernameGenerator;
    WebElement password;
    WebElement confirmPassword;
    WebElement signUpBtn;
    WebElement loginBtn;
    String fileName = "cat.jpg";

    @BeforeTest
    public void SetUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920x1080");
        options.addArguments("--no-sandbox");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

    }

    @AfterTest
    public void tearDown() {
        driver.close();
    }

    @Test
    public void testOpenHomePage() throws InterruptedException {
        driver.get("http://training.skillo-bg.com/");
    }


    @Test(groups = "signUp")
    public void testOpenLoginForm() {

        driver.get("http://training.skillo-bg.com/");

        loginBtn = driver.findElement(By.xpath("//a[@id='nav-link-login']"));
        loginBtn.click();

        signInText = driver.findElement(By.xpath("//p[text()='Sign in']"));
        usernameOrEmailInput = driver.findElement(By.xpath("//input[@id='defaultLoginFormUsername']"));
        passwordInput = driver.findElement(By.xpath("//input[@id='defaultLoginFormPassword']"));
        signInBtn = driver.findElement(By.xpath("//button[@id='sign-in-button']"));

        WebElement rememberMeCheckBoxDefault = driver.findElement(By.xpath("//input[@type='checkbox']"));

        registerBtn = driver.findElement(By.xpath("//a[@href='/users/register']"));

        Assert.assertTrue(signInText.isDisplayed());
        Assert.assertTrue(usernameOrEmailInput.isDisplayed());
        Assert.assertTrue(passwordInput.isDisplayed());
        Assert.assertTrue(rememberMeCheckBoxDefault.isDisplayed());
        Assert.assertTrue(registerBtn.isDisplayed());

    }

    @Test(dependsOnGroups = "signUp", groups = "signUp")
    public void testSignUp() {

        SecureRandom random = new SecureRandom();
        usernameGenerator = "ivo" + String.valueOf(random.nextInt(1000));

        registerBtn.click();

        WebElement signUpText = driver.findElement(By.xpath("//h4[text()='Sign up']"));
        username = driver.findElement(By.xpath("//input[@name='username']"));
        email = driver.findElement(By.xpath("//input[@formcontrolname='email']"));
        password = driver.findElement(By.xpath("//input[@id='defaultRegisterFormPassword']"));
        confirmPassword = driver.findElement(By.xpath("//input[@id='defaultRegisterPhonePassword']"));
        signUpBtn = driver.findElement(By.xpath("//button[@id='sign-in-button']"));

        username.click();
        username.sendKeys(usernameGenerator);
        email.click();
        email.sendKeys(usernameGenerator + "@t.bg");
        password.click();
        password.sendKeys(defaultPassword);
        confirmPassword.click();
        confirmPassword.sendKeys(defaultPassword);

        signUpBtn.click();


        profileBtn = driver.findElement(By.xpath("//a[@id='nav-link-profile']"));
        logoutBtn = driver.findElement(By.xpath("//i[@class='fas fa-sign-out-alt fa-lg']"));
        newPostBtn = driver.findElement(By.xpath("//a[@id='nav-link-new-post']"));

        Assert.assertTrue(profileBtn.isDisplayed());
        Assert.assertTrue(logoutBtn.isDisplayed());
        Assert.assertTrue(newPostBtn.isDisplayed());

    }

    @Test(dependsOnGroups = "signUp", dependsOnMethods = "testLogin")
    public void testLogout() {

        driver.get("http://training.skillo-bg.com/posts/all");
        logoutBtn = driver.findElement(By.xpath("//i[@class='fas fa-sign-out-alt fa-lg']"));
        logoutBtn.click();

        loginBtn = driver.findElement(By.xpath("//a[@id='nav-link-login']"));
        usernameOrEmailInput = driver.findElement(By.xpath("//input[@id='defaultLoginFormUsername']"));

        Assert.assertTrue(loginBtn.isDisplayed());


    }

    @Test(dependsOnGroups = "signUp")
    public void testLogin() {

        driver.get("http://training.skillo-bg.com/users/login");

        usernameOrEmailInput = driver.findElement(By.xpath("//input[@id='defaultLoginFormUsername']"));
        usernameOrEmailInput.click();
        usernameOrEmailInput.sendKeys(usernameGenerator);
        passwordInput = driver.findElement(By.xpath("//input[@id='defaultLoginFormPassword']"));
        passwordInput.click();
        passwordInput.sendKeys(defaultPassword);
        signUpBtn = driver.findElement(By.xpath("//button[@id='sign-in-button']"));
        signUpBtn.click();

        profileBtn = driver.findElement(By.xpath("//a[@id='nav-link-profile']"));
        logoutBtn = driver.findElement(By.xpath("//i[@class='fas fa-sign-out-alt fa-lg']"));
        newPostBtn = driver.findElement(By.xpath("//a[@id='nav-link-new-post']"));

        Assert.assertTrue(profileBtn.isDisplayed());
        Assert.assertTrue(logoutBtn.isDisplayed());
        Assert.assertTrue(newPostBtn.isDisplayed());
    }

    @Test(dependsOnGroups = "signUp", dependsOnMethods = "testLogin")
    public void testCreatePost() {

        driver.get("http://training.skillo-bg.com/posts/all");

        newPostBtn = driver.findElement(By.xpath("//a[@id='nav-link-new-post']"));
        newPostBtn.click();

        WebElement uploadFileContainer = driver.findElement(By.xpath("//button[@id='choose-file']"));
        uploadFileContainer.sendKeys("C:\\Users\\Ivaylo Metodiev\\Downloads\\" + fileName);
        WebElement submitPostBtn = driver.findElement(By.xpath("//button[@id='create-post']"));
        submitPostBtn.click();

        //I could not upload image :(


    }




}
