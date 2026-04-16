package szczur4.lingosBot;
import javax.swing.JTextField;
import java.awt.Graphics;
public class TextField extends JTextField{
	String alt;
	public TextField(String alt){this.alt=alt;}
	public void paint(Graphics g){
		super.paint(g);
		if(getText().isEmpty())g.drawString(alt,1,15);
	}
}
