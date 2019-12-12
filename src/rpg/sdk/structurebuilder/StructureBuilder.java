package rpg.sdk.structurebuilder;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import rpg.RPG;
import rpg.rendering.ui.StringTools;
import rpg.sdk.structurebuilder.ui.MainWindow;

public class StructureBuilder implements Runnable
{
	public static final String title = RPG.title + " - StructureBuilder";
	
	// Thread
	public AtomicBoolean running = new AtomicBoolean(true);
	
	// Window
	public final MainWindow frame = new MainWindow();
	public final Input input = new Input();
	private int lastWindowX, lastWindowY;
	
	// Close event
	private class CloseListener implements WindowListener
	{
		@Override public void windowClosing(WindowEvent e) { running.set(false); }
		
		@Override public void windowActivated(WindowEvent e) { }
		@Override public void windowClosed(WindowEvent e) { }
		@Override public void windowDeactivated(WindowEvent e) { }
		@Override public void windowDeiconified(WindowEvent e) { }
		@Override public void windowIconified(WindowEvent e) { }
		@Override public void windowOpened(WindowEvent e) { }
	}
	
	public StructureBuilder()
	{
		frame.addKeyListener(input);
		frame.addWindowListener(this.new CloseListener());
	}
	
	// Global position
	public double gridSize = 128;
	public double x = gridSize * 4, y = gridSize * 4;
	
	public double basePanSpeed = 1.0;
	public double sprintMultiplier = 4.0;
	public double getPanSpeed() { return input.getKey(VK_SHIFT) ? basePanSpeed * sprintMultiplier : basePanSpeed; }
	
	@Override
	public synchronized void run()
	{
		frame.setVisible(true);
		
		int width = frame.getWidth();
		int height = frame.getHeight();
		
		BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		while(running.get())
		{
			// Set window size if needed
			width = frame.getWidth();
			height = frame.getHeight();
			
			if(width != lastWindowX || height != lastWindowY)
			{
				lastWindowX = width;
				lastWindowY = height;
				
				canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			}
			
			// Update
			if(input.getKey(VK_W) || input.getKey(VK_UP)) y += getPanSpeed();
			if(input.getKey(VK_S) || input.getKey(VK_DOWN)) y -= getPanSpeed();
			if(input.getKey(VK_A) || input.getKey(VK_LEFT)) x += getPanSpeed();
			if(input.getKey(VK_D) || input.getKey(VK_RIGHT)) x -= getPanSpeed();
			
			// Render
			Graphics2D g2d = canvas.createGraphics();
			
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, width, height);
			
			// Draw neutral position
			g2d.setColor(Color.green);
			g2d.drawLine((int) (x + gridSize / 2 - gridSize), 0, (int) (x + gridSize / 2 - gridSize), height);
			g2d.drawLine(0, (int) (y + gridSize / 2), width, (int) (y + gridSize / 2));
			
			// Draw grid
			g2d.setColor(Color.blue);
			
			for(int x = (int) (this.x % gridSize); x < width; x += gridSize)	g2d.drawLine(x, 0, x, height);
			for(int y = (int) (this.y % gridSize); y < height; y += gridSize)	g2d.drawLine(0, y, width, y);
			
			g2d.setColor(Color.red);
			if(frame.isMouseInBounds())
			{
				Point mouse = null;
				try { mouse = frame.getMousePosition(); }
				catch(IllegalComponentStateException e) { System.exit(1); }
				
				// Highlight selected
				Point tilePos = getAbsoluteGridPosition(mouse);
				tilePos.y = -tilePos.y;
				
				tilePos.x *= gridSize;
				tilePos.y *= gridSize;
				
				tilePos.x += x - gridSize;
				tilePos.y += y;
				
				g2d.setColor(Color.red);
				g2d.drawRect(tilePos.x, tilePos.y, (int) gridSize, (int) gridSize);
				
				// Draw info of position
				tilePos = getAbsoluteGridPosition(mouse);
				mouse.x += 10;
				mouse.y -= 5;
				StringTools.drawLine(g2d, "(" + tilePos.x + ", " + tilePos.y + ")", Color.white, 50, mouse, -1);
			}
			
			// Dispose
			g2d.dispose();
			
			// Draw frame to screen
			Graphics g = frame.renderPanel.getGraphics();
			g.drawImage(canvas, 0, 0, null);
			g.dispose();
		}
		
		frame.renderPanel.setEnabled(false);
		frame.dispose();
	}
	
	public Point getAbsoluteGridPosition(Point position)
	{
		double x = position.getX();
		double y = -position.getY();
		
		x /= gridSize;
		y /= gridSize;
		
		x -= this.x / gridSize;
		y += this.y / gridSize;
		
		return new Point((int) Math.round(x + 0.5), (int) Math.round(y + 0.5));
	}
	
	// Static
	public static StructureBuilder instance;
	
	public static void main(String[] args) throws InterruptedException
	{
		setTheme();
		
		instance = new StructureBuilder();
		
		Thread instanceThread = new Thread(instance);
		instanceThread.start();
		
		instanceThread.join();
	}
	
	private static void setTheme()
	{
		String themeClass = null;
		for(LookAndFeelInfo theme : UIManager.getInstalledLookAndFeels())
		{
			String currentClass = theme.getClassName();
			if(currentClass.endsWith("GTKLookAndFeel"))
			{
				themeClass = currentClass;
				break;
			}
		}
		
		try
		{
			if(themeClass != null) UIManager.setLookAndFeel(themeClass);
			else UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(IllegalAccessException | InstantiationException e) { e.printStackTrace(); }
		catch(ClassNotFoundException | UnsupportedLookAndFeelException e)
		{
			System.err.println("Detected GTK theme but was unable to set it.");
			e.printStackTrace();
		}
	}
}
