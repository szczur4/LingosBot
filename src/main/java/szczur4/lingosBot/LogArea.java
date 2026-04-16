package szczur4.lingosBot;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
public class LogArea extends JScrollPane{
	JTextArea area;
	public LogArea(){
		super(area=new JTextArea());
		area.setBackground(Color.BLACK);
		area.setForeground(Color.LIGHT_GRAY);
		area.setEditable(false);
		area.setBorder(null);
		area.setCaretColor(Color.LIGHT_GRAY);
		setBackground(Color.BLACK);
		getHorizontalScrollBar().setUI(new ScrollUI());
		getVerticalScrollBar().setUI(new ScrollUI());
		System.setOut(new PrintStream(new OutStream(false),true));
		System.setErr(new PrintStream(new OutStream(true),true));
	}
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(getWidth()-(getHorizontalScrollBar().isVisible()?9:0),getHeight()-(getVerticalScrollBar().isVisible()?9:0),8,8);
	}
	class OutStream extends OutputStream{
		byte[]typeString;
		OutputStream stream;
		public OutStream(boolean errStream){
			typeString=errStream?"[ERROR]: ".getBytes():"[INFO]: ".getBytes();
			stream=errStream?System.err:System.out;
		}
		public void write(byte[]b,int off,int len)throws IOException{
			super.write(typeString,off,typeString.length);
			super.write(b,off,len);
		}
		public void write(int b)throws IOException{
			area.append((char)b+"");
			area.setCaretPosition(area.getDocument().getLength());
			stream.write(b);
		}
	}
	static class ScrollUI extends BasicScrollBarUI{
		protected void installDefaults(){
			scrollBarWidth=8;
			minimumThumbSize=(Dimension)UIManager.get("ScrollBar.minimumThumbSize");
			maximumThumbSize=(Dimension)UIManager.get("ScrollBar.maximumThumbSize");
			trackHighlight=NO_HIGHLIGHT;
			if(scrollbar.getLayout()==null||(scrollbar.getLayout()instanceof UIResource))scrollbar.setLayout(this);
			scrollbar.setBackground(new Color(0x0,true));
			scrollbar.setForeground(new Color(0x0,true));
			thumbHighlightColor=Color.DARK_GRAY.darker();
			thumbLightShadowColor=Color.DARK_GRAY.darker();
			thumbDarkShadowColor=Color.DARK_GRAY.darker();
			thumbColor=Color.DARK_GRAY.darker();
			trackColor=new Color(0x0,false);
			trackHighlightColor=new Color(0x0,false);
			LookAndFeel.installProperty(scrollbar,"opaque",false);
			incrGap=-16;
			decrGap=-16;
			scrollbar.setBorder(scrollbar.getOrientation()==VERTICAL?new AbstractBorder(){public void paintBorder(Component c,Graphics g,int x,int y,int width,int height){
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0,0,0,height);
				g.drawLine(0,height,width,height);
			}}:new AbstractBorder(){public void paintBorder(Component c,Graphics g,int x,int y,int width,int height){
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0,0,width,0);
				g.drawLine(width,0,width,height);
			}});
		}
		protected void installComponents(){
			incrButton=createIncreaseButton(SOUTH);
			decrButton=createDecreaseButton(NORTH);
			scrollbar.setEnabled(scrollbar.isEnabled());
		}
		public void paint(Graphics g,JComponent c){
			super.paint(g,c);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(c.getWidth(),c.getHeight()-8,8,8);
		}
	}
}
