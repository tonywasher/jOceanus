package uk.co.tolcroft.finance.core;

import uk.co.tolcroft.finance.ui.*;

public class Control {
	/* Members */
	private static	MainTab theWindow 	= null;
	
	private static void createAndShowGUI() {
		try { 
			theWindow = new MainTab();			
			theWindow.makeFrame();
			
		}
		catch (Exception e) {
			System.out.println("Help");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
