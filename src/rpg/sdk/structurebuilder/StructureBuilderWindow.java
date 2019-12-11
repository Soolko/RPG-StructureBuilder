package rpg.sdk.structurebuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class StructureBuilderWindow extends JFrame
{
	private static final long serialVersionUID = 5564676569232749396L;
	
	public final JPanel renderPanel = new JPanel();
	
	/**
	 * Create the frame.
	 */
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
