package rpg.sdk.structurebuilder.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;

import rpg.sdk.structurebuilder.ResourceManager;
import rpg.sdk.structurebuilder.StructureBuilder;
import rpg.world.structure.Structure;

public class PalleteWindow extends JFrame
{
	private static final long serialVersionUID = 89675458873590747L;
	
	public final ResourceManager resources = new ResourceManager();
	
	protected final JPanel renderPanel = new JPanel();
	
	public PalleteWindow()
	{
		final ChooseFile chooseFile = new ChooseFile(this);
		
		renderPanel.addMouseListener(new MouseListener()
		{

			@Override public void mouseClicked(MouseEvent e) { onClickNone(); }
			
			@Override public void mouseEntered(MouseEvent e) { }
			@Override public void mouseExited(MouseEvent e) { }
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseReleased(MouseEvent e) { }
		});
		
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
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(renderPanel);
		
		renderPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
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
					resources.resourceDirectory = chooser.getSelectedFile();
					break;
				default: return;
			}
			
			refresh();
		}
	}
	
	public void refresh()
	{
		clear();
		if(resources.resourceDirectory == null) return;
		
		final List<File> files;
		try { files = resources.listFiles(); }
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		for(File f : files)
		{
			if(f.getPath().endsWith(".png"))
			{
				Structure.Entry entry = new Structure.Entry();
				entry.resource = f.getPath();
				entry.collideable = true;
				entry.water = false;
				
				final PalleteEntry palleteEntry;
				try { palleteEntry = new PalleteEntry(entry); }
				catch(IOException e)
				{
					System.err.println("Failed to load icon of resource \"" + entry.resource + "\".\nMake sure this is an image.\n\nStacktrace:");
					e.printStackTrace();
					continue;
				}
				
				renderPanel.add(palleteEntry);
				pack();
			}
		}
	}
	
	public void clear() { renderPanel.removeAll(); }
	
	private void onClickNone()
	{
		for(Component c : renderPanel.getComponents())
		{
			if(!(c instanceof PalleteEntry)) continue;
			
			PalleteEntry entry = (PalleteEntry) c;
			entry.setSelected(false);
		}
	}
	
	public class PalleteEntry extends JLabel
	{
		private static final long serialVersionUID = 7369091394609412469L;
		
		public static final int iconSize = 128;
		
		public final Structure.Entry entry;
		
		public final BufferedImage iconImage, iconHoveredImage, iconSelectedImage;
		protected final ImageIcon icon, iconHovered, iconSelected;
		
		// Selected?
		private boolean selected = false;
		
		public void setSelected(boolean selected)
		{
			this.selected = selected;
			
			if(selected) setIcon(iconSelected);
			else setHovered(getHovered());
		}
		
		// Hovered?
		private boolean hovered = false;
		
		/*
		 * Much cleaner in C#.
		 */
		protected void setHovered(boolean hovered)
		{
			this.hovered = hovered;
			
			if(!selected) setIcon(hovered ? iconHovered : icon);
		}
		public boolean getHovered() { return hovered; }
		
		protected PalleteEntry(Structure.Entry entry) throws IOException
		{
			super("");
			this.entry = entry;
			
			// Get & Scale icon
			BufferedImage baseIcon = ImageIO.read(new File(entry.resource));
			BufferedImage scaledIcon = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = scaledIcon.createGraphics();
			g2d.drawImage(baseIcon, 0, 0, iconSize, iconSize, 0, 0, baseIcon.getWidth(), baseIcon.getHeight(), null);
			g2d.dispose();
			
			this.iconImage = scaledIcon;
			this.icon = new ImageIcon(scaledIcon);
			setIcon(this.icon);
			
			// Create hovered icon
			BufferedImage hoveredIcon = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
			g2d = hoveredIcon.createGraphics();
			g2d.setColor(ResourceManager.hoveredColour);
			g2d.drawImage(scaledIcon, 0, 0, null);
			g2d.drawRect(0, 0, iconSize - 1, iconSize - 1);
			g2d.dispose();
			
			this.iconHoveredImage = hoveredIcon;
			this.iconHovered = new ImageIcon(hoveredIcon);
			
			// Create selected icon
			BufferedImage selectedIcon = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
			g2d = selectedIcon.createGraphics();
			g2d.setColor(ResourceManager.selectedColour);
			g2d.drawImage(scaledIcon, 0, 0, null);
			g2d.drawRect(0, 0, iconSize - 1, iconSize - 1);
			g2d.dispose();
			
			this.iconSelectedImage = selectedIcon;
			this.iconSelected = new ImageIcon(selectedIcon);
			
			// Add listeners
			addMouseListener(new PalleteMouseListener());
		}
		
		private class PalleteMouseListener implements MouseListener
		{
			@Override public void mouseClicked(MouseEvent e) { onClick(); }
			
			@Override public void mouseEntered(MouseEvent e) { setHovered(true); }
			@Override public void mouseExited(MouseEvent e) { setHovered(false); }
			
			// Unused
			@Override public void mousePressed(MouseEvent e) { }
			@Override public void mouseReleased(MouseEvent e) { }
		}
		
		private void onClick()
		{
			// Set this as selected
			setSelected(true);
			
			// Set all others as not selected
			for(Component c : renderPanel.getComponents())
			{
				if(c instanceof PalleteEntry && !c.equals(this))
				{
					PalleteEntry entry = (PalleteEntry) c;
					entry.setSelected(false);
				}
			}
		}
	}
}
