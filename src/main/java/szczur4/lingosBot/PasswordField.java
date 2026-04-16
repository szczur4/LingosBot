package szczur4.lingosBot;
import javax.swing.JPasswordField;
import java.awt.Graphics;
public class PasswordField extends JPasswordField{
	String alt;
	public PasswordField(String alt){this.alt=alt;}
	public void paint(Graphics g){
		super.paint(g);
		if(getText().isEmpty())g.drawString(alt,1,15);
	}
}
