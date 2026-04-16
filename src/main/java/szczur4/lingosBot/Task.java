package szczur4.lingosBot;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.util.*;
import java.util.zip.GZIPOutputStream;
public class Task{
	Wait<WebDriver>wait=new FluentWait<>((WebDriver)null).withTimeout(Duration.ofSeconds(3)).pollingEvery(Duration.ofMillis(5)).ignoring(ElementNotInteractableException.class).ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
	WebDriver driver=new ChromeDriver(new ChromeOptions().addArguments("--headless"));
	boolean finished,loggedIn;
	Main parent;
	public Task(Main parent){
		this.parent=parent;
		driver.manage().window().setSize(new Dimension(0,800));
		System.out.println("Waiting for lingos.pl");
		driver.get("https://lingos.pl");
		System.out.println("Waiting for the cookie prompt");
		wait.until(_->{driver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline")).click();return true;});
		System.out.println("Ready");
		parent.enableAll();
	}
	public void login(String login,String password){
		if(loggedIn)logout();
		System.out.println("Logging in");
		driver.get("https://lingos.pl/h/login");
		driver.findElement(By.name("login")).sendKeys(login);
		driver.findElement(By.name("password")).sendKeys(password);
		driver.findElement(By.id("submit-login-button")).click();
		try{wait.until(_->{
			WebElement element=driver.findElement(By.className("alert-danger"));
			if(element.getText().contains("email")||element.getText().contains("awne has")){
				System.err.println(element.getText());
				return true;
			}
			else return false;
		});}catch(Exception e){
			loggedIn=true;
			System.out.println("Login successful");
			try{
				OutputStreamWriter osw=new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("lingosUser")));
				osw.write(login+'\n'+password);
				osw.close();
				System.out.println("Saved user login and password");
			}catch(Exception ex){System.err.println("Failed to save lingos user");}
		}
	}
	public void logout(){
		driver.get("https://lingos.pl/h/logout");
		loggedIn=false;
		System.out.println("Logged out");
	}
	public void execute(String login,String password){
		parent.disableAll();
		if(!loggedIn)login(login,password);
		wait.until(_->{driver.findElement(By.className("me-2")).click();return true;});
		List<WebElement>options=driver.findElement(By.className("form-select")).findElements(By.tagName("option"));
		Map<String,String>classLinks=new HashMap<>();
		for(WebElement w:options)classLinks.put(w.getAttribute("value"),w.getAttribute("text"));
		for(String classLink:classLinks.keySet()){
			System.out.println("Getting the word lists for class "+classLinks.get(classLink));
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
				String previousQuestion="";
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
					String question=driver.findElement(By.id("flashcard_main_text")).getText();
					String answer=answers.get(question);
					if(answer==null||question.equals(previousQuestion)){
						System.err.println("Something went wrong. You will probably have to finish the lesson manually");
						break;
					}
					previousQuestion=question;
					System.out.println("Question: "+question+", answer: "+answer);
					driver.findElement(By.id("flashcard_answer_input")).sendKeys(answer+Keys.ENTER);
					wait.until(_->{driver.findElement(By.id("enterBtn")).click();return true;});
				}
				finished=false;
			}
		}
		System.out.println("Done");
		parent.enableAll();
	}
}
