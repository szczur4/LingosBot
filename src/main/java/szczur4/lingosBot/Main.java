package szczur4.lingosBot;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
public class Main{
	static WebDriver driver;
	static Wait<WebDriver>wait=new FluentWait<>((WebDriver)null).withTimeout(Duration.ofSeconds(5)).pollingEvery(Duration.ofMillis(5)).ignoring(ElementNotInteractableException.class).ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
	static Queue<String>classLinks=new ArrayDeque<>();
	static StringBuilder sb=new StringBuilder();
	public static void main(String[]args){
		driver=new ChromeDriver();
		driver.manage().window().setSize(new Dimension(0,800));
		driver.get("https://lingos.pl/h/login");
		wait.until(_->{driver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline")).click();return true;});
		byte[]k=Base64.getUrlDecoder().decode(URLDecoder.decode(args[0],StandardCharsets.UTF_8));
		for(byte b:k)sb.append((char)b);
		driver.findElement(By.name("login")).sendKeys(sb.toString());
		k=Base64.getUrlDecoder().decode(URLDecoder.decode(args[1],StandardCharsets.UTF_8));
		sb.setLength(0);
		for(byte b:k)sb.append((char)b);
		driver.findElement(By.name("password")).sendKeys(sb.toString());
		driver.findElement(By.id("submit-login-button")).click();
		wait.until(_->{driver.findElement(By.className("me-2")).click();return true;});
		List<WebElement>options=driver.findElement(By.className("form-select")).findElements(By.tagName("option"));
		int classCount=options.size();
		for(WebElement w:options)classLinks.add(w.getAttribute("value"));
		Set<Cookie>cookies=driver.manage().getCookies();
		for(int i=0;i<classCount;i++){
			driver.get("https://lingos.pl"+classLinks.poll());
			driver.get("https://lingos.pl/student-confirmed/wordsets");
			wait.until(_->{driver.findElements(By.className("rounded-3"));return true;});
			List<WebElement>sets=driver.findElements(By.className("rounded-3"));
			for(WebElement w:sets)new task(w.findElement(By.className("pb-2")).getAttribute("href"),cookies);
		}
		driver.quit();
	}
}
