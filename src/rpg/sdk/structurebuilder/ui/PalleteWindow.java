package rpg.sdk.structurebuilder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import rpg.sdk.structurebuilder.StructureBuilder;

public class PalleteWindow extends JFrame
{
	private static final long serialVersionUID = 89675458873590747L;
	
	protected JScrollPane scrollPane = new JScrollPane();
	public File resourcesPath = null;
	
	public PalleteWindow()
	{
		final ChooseFile chooseFile = new ChooseFile(this);
		
		setTitle(StructureBuilder.title + " - Pallete");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setMinimumSize(new Dimension(300, 450));
		setSize(300, 450);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu optionsMenu = new JMenu("Options");
		menuBar.add(optionsMenu);
		
		JMenu resourcesMenu = new JMenu("Resources");
		optionsMenu.add(resourcesMenu);
		
		JMenuItem reloadButton = new JMenuItem("Reload");
		reloadButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) { refresh(); }
		});
		resourcesMenu.add(reloadButton);
		
		JSeparator separator = new JSeparator();
		resourcesMenu.add(separator);
		
		JMenuItem pathSetButton = new JMenuItem("Set Resource path");
		pathSetButton.addActionListener(chooseFile);
		resourcesMenu.add(pathSetButton);
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		refresh();
		
		setVisible(true);
	}
	
	private class ChooseFile implements ActionListener
	{
		private final PalleteWindow parent;
		protected ChooseFile(PalleteWindow parent) { this.parent = parent; }
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("./"));
			chooser.setDialogTitle("Select Resource folder");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			switch(chooser.showOpenDialog(parent))
			{
				case JFileChooser.APPROVE_OPTION:
					resourcesPath = chooser.getSelectedFile();
					break;
				default: return;
			}
			
			refresh();
		}
	}
	
	public void refresh()
	{
		if(resourcesPath == null) return;
		
		System.out.println(resourcesPath);
	}
}
