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
	Wait<WebDriver>wait=new FluentWait<>((WebDriver)null).withTimeout(Duration.ofSeconds(5)).pollingEvery(Duration.ofMillis(5)).ignoring(ElementNotInteractableException.class).ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
	StringBuilder sb=new StringBuilder();
	boolean finished;
	void main(String[]args){
		WebDriver driver=new ChromeDriver();
		driver.manage().window().setSize(new Dimension(0,800));
		driver.get("https://lingos.pl/h/login");
		wait.until(_->{driver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline")).click();return true;});
		byte[]k=Base64.getUrlDecoder().decode(URLDecoder.decode(args[0],StandardCharsets.UTF_8));
		for(byte b:k)sb.append((char)b);
		driver.findElement(By.name("login")).sendKeys(sb.toString());
		k=Base64.getUrlDecoder().decode(URLDecoder.decode(args[1],StandardCharsets.UTF_8));
		sb=new StringBuilder();
		for(byte b:k)sb.append((char)b);
		driver.findElement(By.name("password")).sendKeys(sb.toString());
		driver.findElement(By.id("submit-login-button")).click();
		wait.until(_->{driver.findElement(By.className("me-2")).click();return true;});
		List<WebElement>options=driver.findElement(By.className("form-select")).findElements(By.tagName("option"));
		ArrayList<String>classLinks=new ArrayList<>();
        for(WebElement w:options)classLinks.add(w.getAttribute("value"));
		for(String classLink:classLinks){
			driver.get("https://lingos.pl"+classLink);
			driver.get("https://lingos.pl/student-confirmed/wordsets");
			wait.until(_->{driver.findElements(By.className("rounded-3"));return true;});
			ArrayList<String>wordSetLinks=new ArrayList<>(),lessonLinks=new ArrayList<>();
			for(WebElement w:driver.findElements(By.className("rounded-3"))){
				String str=w.findElement(By.className("pb-2")).getAttribute("href");
				wordSetLinks.add(str);
				lessonLinks.add("https://lingos.pl/s/lesson/0,"+str.substring(str.lastIndexOf("/")+1)+','+classLink.substring(classLink.lastIndexOf("/")+1));
			}
			Map<String,String>answers=new HashMap<>();
			for(String wordSetLink:wordSetLinks){
				driver.get(wordSetLink);
				wait.until(_->{driver.findElements(By.className("flashcard-border-end"));return true;});
				List<WebElement>keys=driver.findElements(By.className("flashcard-border-start")),values=driver.findElements(By.className("flashcard-border-end"));
				for(int i=0;i<values.size();i++)answers.put(keys.get(i).getText(),values.get(i).getText());
			}
			for(String lessonLink:lessonLinks){
				driver.get(lessonLink);
				wait.until(_->{driver.findElement(By.id("progress_counter"));return true;});
				while(true){
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
						if(driver.getCurrentUrl().equals("https://lingos.pl/student-confirmed/premium-buy"))return finished=true;
						return true;
					});
					if(finished)break;
					wait.until(_->{driver.findElement(By.id("flashcard_main_text"));return true;});
					String str=answers.get(driver.findElement(By.id("flashcard_main_text")).getText());
					if(str==null)break;
					driver.findElement(By.id("flashcard_answer_input")).sendKeys(str+Keys.ENTER);
					wait.until(_->{driver.findElement(By.id("enterBtn")).click();return true;});
				}
				finished=false;
			}
		}
		System.out.println("done");
		driver.quit();
	}
}
