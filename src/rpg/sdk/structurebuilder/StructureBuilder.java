package rpg.sdk.structurebuilder;

import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import rpg.RPG;
import rpg.rendering.ui.StringTools;
import rpg.sdk.structurebuilder.objects.SaveableStructure;
import rpg.sdk.structurebuilder.ui.MainWindow;
import rpg.sdk.structurebuilder.ui.PalleteWindow;

public class StructureBuilder implements Runnable
{
	// Global
	public static final String title = RPG.title + " - StructureBuilder";
	
	// Current working object
	public File currentFile = null;
	public SaveableStructure currentStructure = new SaveableStructure();
	
	// Thread
	public AtomicBoolean running = new AtomicBoolean(true);
	
	// Window
	public final PalleteWindow pallete = new PalleteWindow();
	
	public final MainWindow frame = new MainWindow(pallete, new NewStructureAction());
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
	
	private class NewStructureAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			currentFile = null;
			currentStructure = new SaveableStructure();
		}
	}
	
	public final Input input = new Input();
	private int lastWindowX, lastWindowY;
	
	public StructureBuilder()
	{
		frame.addKeyListener(input);
		frame.addMouseListener(new TileSelectListener());
		frame.addWindowListener(new CloseListener());
	}
	
	// Global position
	public double gridSize = 128;
	public double x = gridSize * 4, y = gridSize * 4;
	
	public double basePanSpeed = 1.0;
	public double sprintMultiplier = 4.0;
	public double getPanSpeed() { return input.getKey(VK_SHIFT) ? basePanSpeed * sprintMultiplier : basePanSpeed; }
	
	public Point selectedTile = null;
	
	private class TileSelectListener implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(frame.isMouseInBounds()) selectedTile = getAbsoluteGridPosition(frame.getMousePosition());
			else selectedTile = null;
		}
		
		@Override public void mouseEntered(MouseEvent e) { }
		@Override public void mouseExited(MouseEvent e) { }
		@Override public void mousePressed(MouseEvent e) { }
		@Override public void mouseReleased(MouseEvent e) { }
	}
	
	@Override
	public synchronized void run()
	{
		frame.setVisible(true);
		
		int width = frame.getWidth();
		int height = frame.getHeight();
		
		BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		while(running.get())
		{
			if(!frame.shouldRender.get()) continue;
			
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
			
			g2d.setColor(Color.darkGray);
			g2d.fillRect(0, 0, width, height);
			
			if(frame.gridActive())
			{
				// Draw neutral position
				g2d.setColor(Color.green);
				g2d.drawLine((int) (x + gridSize / 2 - gridSize), 0, (int) (x + gridSize / 2 - gridSize), height);
				g2d.drawLine(0, (int) (y + gridSize / 2), width, (int) (y + gridSize / 2));
				
				// Draw grid
				g2d.setColor(Color.lightGray);
				for(int x = (int) (this.x % gridSize); x < width; x += gridSize)	g2d.drawLine(x, 0, x, height);
				for(int y = (int) (this.y % gridSize); y < height; y += gridSize)	g2d.drawLine(0, y, width, y);
			}
			
			if(frame.isMouseInBounds())
			{
				Point mouse = null;
				try { mouse = frame.getMousePosition(); }
				catch(IllegalComponentStateException e) { System.exit(1); }
				
				// Highlight hovered
				Point tilePos = getAbsoluteGridPosition(mouse);
				tilePos = tilePosToScreenSpace(tilePos);
				
				g2d.setColor(ResourceManager.hoveredColour);
				g2d.drawRect(tilePos.x, tilePos.y, (int) gridSize, (int) gridSize);
				
				// Draw info of position
				tilePos = getAbsoluteGridPosition(mouse);
				mouse.x += 10;
				mouse.y -= 5;
				StringTools.drawLine(g2d, "(" + tilePos.x + ", " + tilePos.y + ")", Color.white, 50, mouse, -1);
			}
			
			// Draw selected
			if(selectedTile != null)
			{
				Point selectedScreenSpace = tilePosToScreenSpace(selectedTile);
				
				g2d.setColor(ResourceManager.selectedColour);
				g2d.drawRect(selectedScreenSpace.x, selectedScreenSpace.y, (int) gridSize, (int) gridSize);
			}
			
			// Dispose
			g2d.dispose();
			
			// Draw frame to screen
			Graphics g = frame.renderPanel.getGraphics();
			g.drawImage(canvas, 0, 0, null);
			g.dispose();
		}
		
		pallete.dispose();
		frame.renderPanel.setEnabled(false);
		frame.dispose();
	}
	
	public Point tilePosToScreenSpace(Point tilePos)
	{
		Point newPos = (Point) tilePos.clone();
		
		newPos.y = -newPos.y;
		
		newPos.x *= gridSize;
		newPos.y *= gridSize;
		
		newPos.x += x - gridSize;
		newPos.y += y;
		
		return newPos;
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
		catch(ClassNotFoundException | UnsupportedLookAndFeelException e)
		{
			System.err.println("Detected GTK theme but was unable to set it.");
			e.printStackTrace();
		}
		catch(IllegalAccessException | InstantiationException e) { e.printStackTrace(); }
	}
}
