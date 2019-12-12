package rpg.sdk.structurebuilder.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import rpg.sdk.structurebuilder.StructureBuilder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class PalleteWindow extends JFrame
{
	private static final long serialVersionUID = 89675458873590747L;
	
	public PalleteWindow()
	{
		setTitle(StructureBuilder.title + " - Pallete");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setMinimumSize(new Dimension(300, 450));
		setSize(300, 450);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenu mnResources = new JMenu("Resources");
		mnOptions.add(mnResources);
		
		JMenuItem mntmReload = new JMenuItem("Reload");
		mnResources.add(mntmReload);
		
		JSeparator separator = new JSeparator();
		mnResources.add(separator);
		
		JMenuItem mntmSetResourcePath = new JMenuItem("Set Resource path");
		mnResources.add(mntmSetResourcePath);
	}
}
