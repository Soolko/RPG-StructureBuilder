package rpg.sdk.structurebuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.awt.event.KeyEvent.*;

import rpg.RPG;

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
	public double panSpeed = 1.0;
	public double x, y;
	public double gridSize = 128;
	
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
			if(input.getKey(VK_W) || input.getKey(VK_UP)) y += panSpeed;
			if(input.getKey(VK_S) || input.getKey(VK_DOWN)) y -= panSpeed;
			if(input.getKey(VK_A) || input.getKey(VK_LEFT)) x += panSpeed;
			if(input.getKey(VK_D) || input.getKey(VK_RIGHT)) x -= panSpeed;
			
			// Render
			Graphics2D g2d = canvas.createGraphics();
			
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, width, height);
			
			// Draw grid
			g2d.setColor(Color.blue);
			
			for(int x = (int) (this.x % gridSize); x < frame.getWidth(); x += gridSize)		g2d.drawLine(x, 0, x, frame.getHeight());
			for(int y = (int) (this.y % gridSize); y < frame.getHeight(); y += gridSize)	g2d.drawLine(0, y, frame.getWidth(), y);
			
			// Dispose
			g2d.dispose();
			
			// Draw frame to screen
			Graphics g = frame.getGraphics();
			g.drawImage(canvas, 0, 0, null);
			g.dispose();
		}
		
		frame.setVisible(false);
		frame.dispose();
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
