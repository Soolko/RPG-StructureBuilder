package rpg.sdk.structurebuilder;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Input implements KeyListener, MouseListener
{
	// Key Handling
	protected boolean[] keys = new boolean[65535];
	public boolean getKey(int code) { return keys[code]; }
	
	@Override public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }
	@Override public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }
	
	// Mouse Handling
	public boolean mousePressed = false;
	
	@Override public void mousePressed(MouseEvent e) { mousePressed = true; }
	@Override public void mouseReleased(MouseEvent e) { mousePressed = false; }
	
	// Unused
	@Override public void keyTyped(KeyEvent e) { }
	@Override public void mouseClicked(MouseEvent e) { }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
}
