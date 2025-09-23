package szczur4.lingosBot;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
public class task{
	WebDriver driver=new ChromeDriver();
	Wait<WebDriver>wait=new FluentWait<>(driver).withTimeout(Duration.ofSeconds(5)).pollingEvery(Duration.ofMillis(20)).ignoring(ElementNotInteractableException.class).ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
	Map<String,String>answers=new HashMap<>();
	List<WebElement>keys,values;
	boolean finished;
	task(String address,Set<Cookie>cookies){
		driver.manage().window().setSize(new Dimension(0,800));
		driver.get(address);
		for(Cookie c:cookies)driver.manage().addCookie(c);
		driver.get(address);
		wait.until(_->{values=driver.findElements(By.className("flashcard-border-end"));return true;});
		keys=driver.findElements(By.className("flashcard-border-start"));
		for(int i=0;i<values.size();i++)answers.put(keys.get(i).getText(),values.get(i).getText());
		driver.findElement(By.className("btn-primary")).click();
		wait.until(_->{driver.findElement(By.id("progress_counter"));return true;});
		while(true){
			Wait();
			if(finished)break;
			wait.until(_->{driver.findElement(By.id("flashcard_main_text"));return true;});
			String q=driver.findElement(By.id("flashcard_main_text")).getText();
			driver.findElement(By.id("flashcard_answer_input")).sendKeys(answers.get(q)+Keys.ENTER);
			wait.until(_->{driver.findElement(By.id("enterBtn")).click();return true;});
		}
		System.out.println("done");
		driver.quit();
	}
	void Wait(){
		wait.until(_->{
			try{if(driver.findElement(By.className("modal-body"))!=null)return finished=true;}catch(Exception _){}
			WebElement content=driver.findElement(By.cssSelector("html>body>div:nth-of-type(2)>div>div>div>div>div"));
			try{
				WebElement button=content.findElement(By.cssSelector("a"));
				if(button.getAttribute("class").contains("btn-primary"))button.click();
			}catch(Exception _){}
			try{
				content.findElement(By.className("text-uppercase"));
				driver.findElement(By.id("enterBtn")).click();
			}catch(Exception _){}
			try{
				WebElement button=content.findElement(By.cssSelector("a"));
				if(button.getAttribute("class").contains("btn-primary"))button.click();
			}catch(Exception _){}
			if(Objects.equals(driver.getCurrentUrl(),"https://lingos.pl/student-confirmed/premium-buy"))return finished=true;
			return true;
		});
	}
}
