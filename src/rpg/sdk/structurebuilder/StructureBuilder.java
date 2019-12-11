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

import rpg.RPG;
import rpg.rendering.ui.StringTools;

public class StructureBuilder implements Runnable
{
	// Thread
	public AtomicBoolean running = new AtomicBoolean(true);
	
	// Window
	public final StructureBuilderWindow frame = new StructureBuilderWindow(RPG.title + " - Structure Builder");
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
	public double x, y;
	public double gridSize = 128;
	
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
			
			// Draw grid
			g2d.setColor(Color.blue);
			
			for(int x = (int) (this.x % gridSize); x < frame.getWidth(); x += gridSize)		g2d.drawLine(x, 0, x, frame.getHeight());
			for(int y = (int) (this.y % gridSize); y < frame.getHeight(); y += gridSize)	g2d.drawLine(0, y, frame.getWidth(), y);
			
			g2d.setColor(Color.red);
			if(frame.isMouseInBounds())
			{
				Point mouse = null;
				try { mouse = frame.getMousePosition(); }
				catch(IllegalComponentStateException e) { System.exit(1); }
				
				// Highlight selected
				int x = (int) (mouse.x / gridSize);
				int y = (int) (mouse.y / gridSize);
				
				x *= gridSize;
				y *= gridSize;
				
				x += this.x % gridSize;
				y += this.y % gridSize;
				
				g2d.drawRect(x, y, (int) gridSize, (int) gridSize);
				
				// Draw info of position
				Point tilePos = getAbsoluteGridPosition();
				
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
	
	public Point getAbsoluteGridPosition()
	{
		Point mouse = frame.getMousePosition();
		int x = (int) (mouse.x / gridSize);
		int y = (int) (mouse.y / gridSize);
		
		int absoluteX = (int) (-this.x / gridSize) + x;
		int absoluteY = (int) (-this.y / gridSize) + y;
		return new Point(absoluteX, absoluteY);
	}
	
	// Static
	public static StructureBuilder instance;
	
	public static void main(String[] args) throws InterruptedException
	{
		instance = new StructureBuilder();
		
		Thread instanceThread = new Thread(instance);
		instanceThread.start();
		
		instanceThread.join();
	}
}
