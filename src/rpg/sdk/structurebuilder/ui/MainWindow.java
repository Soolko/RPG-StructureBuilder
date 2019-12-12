package rpg.sdk.structurebuilder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import rpg.sdk.structurebuilder.StructureBuilder;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 5564676569232749396L;
	
	public final JPanel renderPanel = new JPanel();
	
	private JCheckBoxMenuItem showGrid = new JCheckBoxMenuItem("Show Grid");
	public boolean gridActive() { return showGrid.isSelected(); }
	
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
	
	public AtomicBoolean shouldRender = new AtomicBoolean(true);
	private MenuListener focusListener = new MenuListener()
	{
		@Override public void menuSelected(MenuEvent e)		{ shouldRender.set(false); }
		@Override public void menuDeselected(MenuEvent e)	{ shouldRender.set(true); }
		@Override public void menuCanceled(MenuEvent e)		{ shouldRender.set(true); }
	};
	
	public MainWindow(ActionListener newButtonListener)
	{
		setTitle(StructureBuilder.title);
		setMinimumSize(new Dimension(640, 480));
		setSize(1280, 720);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.addMenuListener(focusListener);
		menuBar.add(fileMenu);
		
		JMenuItem newButton = new JMenuItem("New");
		newButton.addActionListener(newButtonListener);
		fileMenu.add(newButton);
		
		JSeparator separator = new JSeparator();
		fileMenu.add(separator);
		
		JMenuItem saveButton = new JMenuItem("Save");
		fileMenu.add(saveButton);
		
		JMenuItem loadButton = new JMenuItem("Load");
		fileMenu.add(loadButton);
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.addMenuListener(focusListener);
		menuBar.add(viewMenu);
		
		showGrid.setSelected(true);
		viewMenu.add(showGrid);
		
		getContentPane().add(renderPanel, BorderLayout.CENTER);
	}
}
