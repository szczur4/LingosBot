package szczur4.lingosBot;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
public class Main{
	String login,password;
	public static Border border=new LineBorder(Color.LIGHT_GRAY);
	JButton startButton=new JButton(new AbstractAction(){public void actionPerformed(ActionEvent e){exec.execute(()->task.execute(login=loginField.getText(),password=passwordField.getText()));}}),loginButton=new JButton(new AbstractAction(){public void actionPerformed(ActionEvent e){exec.execute(()->{
		disableAll();
		task.login(login=loginField.getText(),password=passwordField.getText());
		enableAll();
	});}});
	LogArea logArea=new LogArea();
	JTextField loginField=new TextField("Login");
	JPasswordField passwordField=new PasswordField("Password");
	Task task;
	ExecutorService exec=Executors.newSingleThreadExecutor();
	JFrame frame=new JFrame();
	void main(String[]args)throws IOException{
		loadUser();
		if(Arrays.asList(args).contains("autostart")&&login!=null&&password!=null)try{
			task.execute(login,password);
			System.exit(0);
		}catch(Exception ex){System.err.println("Failed to do it automatically, launching the GUI");}
		frame.addComponentListener(new ComponentAdapter(){public void componentResized(ComponentEvent e){
			loginField.setBounds(5,5,(frame.getWidth()-10)*7/10,20);
			startButton.setBounds(loginField.getWidth()+10,5,frame.getWidth()-loginField.getWidth()-15,45);
			int w=loginField.getWidth();
			passwordField.setBounds(5,30,w*7/10,20);
			loginButton.setBounds(passwordField.getWidth()+10,30,w-passwordField.getWidth()-5,20);
			logArea.setBounds(5,55,frame.getWidth()-10,frame.getContentPane().getHeight()-60);
		}});
		frame.setPreferredSize(new Dimension(300,200));
		frame.setMinimumSize(frame.getPreferredSize());
		frame.setSize(800,300);
		frame.setIconImage(ImageIO.read(Main.class.getResource("/icon.png")));
		frame.setTitle("szczur4 Lingos bot");
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setLayout(null);
		startButton.setText("Start");
		startButton.setBorder(border);
		startButton.setBackground(Color.DARK_GRAY.darker().darker());
		startButton.setForeground(Color.LIGHT_GRAY);
		startButton.setFocusable(false);
		loginButton.setText("Login");
		loginButton.setBorder(border);
		loginButton.setBackground(Color.DARK_GRAY.darker().darker());
		loginButton.setForeground(Color.LIGHT_GRAY);
		loginButton.setFocusable(false);
		logArea.setBounds(100,100,300,100);
		logArea.setBorder(border);
		loginField.setBackground(Color.BLACK);
		loginField.setForeground(Color.LIGHT_GRAY);
		loginField.setBorder(border);
		loginField.setCaretColor(Color.LIGHT_GRAY);
		passwordField.setBackground(Color.BLACK);
		passwordField.setForeground(Color.LIGHT_GRAY);
		passwordField.setBorder(border);
		passwordField.setCaretColor(Color.LIGHT_GRAY);
		frame.add(startButton);
		frame.add(loginButton);
		frame.add(logArea);
		frame.add(loginField);
		frame.add(passwordField);
		disableAll();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		System.out.println("Waiting for the WebDriver");
		task=new Task(this);
	}
	public void loadUser(){
		File f=new File("lingosUser");
		if(f.exists())try{
			BufferedReader br=new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));
			loginField.setText(login=br.readLine());
			passwordField.setText(password=br.readLine());
		}catch(Exception ex){System.err.println("Failed to read user data");}
		else System.out.println("No user data file found");
	}
	public void enableAll(){for(Component c:frame.getContentPane().getComponents())if(c instanceof JButton b)b.setEnabled(true);}
	public void disableAll(){for(Component c:frame.getContentPane().getComponents())if(c instanceof JButton b)b.setEnabled(false);}
}
