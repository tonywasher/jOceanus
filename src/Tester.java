package Apps;

import finance.*;

public class Tester {
	
	private static void createAndShowGUI() {
		try { 
			finSwing  mySwing = new finSwing();			
			mySwing.makeFrame();
		}
		catch (finObject.Exception e) {
			
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
