package rpg.sdk.structurebuilder;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input implements KeyListener
{
	// Key Handling
	protected boolean[] keys = new boolean[65535];
	public boolean getKey(int code) { return keys[code]; }
	
	@Override public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }
	@Override public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }
	
	@Override public void keyTyped(KeyEvent e) { }
}
