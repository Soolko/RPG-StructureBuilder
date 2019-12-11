package rpg.sdk.structurebuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class StructureBuilderWindow extends JFrame
{
	private static final long serialVersionUID = 5564676569232749396L;
	
	public final JPanel renderPanel = new JPanel();
	
	public Point getMousePosition() throws IllegalComponentStateException
	{
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point panel = renderPanel.getLocationOnScreen();
		
		return new Point(mouse.x - panel.x, mouse.y - panel.y);
	}
	
	public boolean isMouseInBounds() { return isPointInBounds(getMousePosition()); }
	public boolean isPointInBounds(Point point)
	{
		boolean outOfBounds = !renderPanel.isVisible();
		outOfBounds |= point.x < 0;
		outOfBounds |= point.y < 0;
		outOfBounds |= point.x > renderPanel.getWidth();
		outOfBounds |= point.y > renderPanel.getHeight();
		
		return !outOfBounds;
	}
	
	public StructureBuilderWindow(String title)
	{
		setTitle(title);
		setMinimumSize(new Dimension(640, 480));
		setSize(1280, 720);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem saveButton = new JMenuItem("Save");
		fileMenu.add(saveButton);
		
		getContentPane().add(renderPanel, BorderLayout.CENTER);
	}
}
