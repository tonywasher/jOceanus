package uk.co.tolcroft.finance.core;

import javax.swing.SwingUtilities;

import uk.co.tolcroft.finance.ui.MainTab;
import uk.co.tolcroft.finance.ui.controls.PasswordDialog;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.*;

public class SecureManager {
	/* Members */
	private SecurityControl.List 	theSecurity = null;
	private MainTab					theCtl 	 	= null;
	
	/* Constructor */
	public SecureManager(MainTab pCtl) {
		/* Record the main window */
		theCtl = pCtl;
		
		/* Allocate a new Security list */ 
		theSecurity = new SecurityControl.List();
	}	

	/**
	 *  Access the required Security Control
	 *  @param pKey the key for the required security control
	 *  @param pSource the description of the secured resource
	 */
	public SecurityControl getSecurityControl(String pKey,
											  String pSource) throws Exception {
		/* Access or create the required Security control */
		SecurityControl myControl = theSecurity.getSecurityControl(pKey);

		/* If the security control is not initialised */
		if (!myControl.isInitialised()) {
			String 	myTitle;
			boolean	needConfirm = false;
			
			/* Determine whether we need confirmation */
			if (pKey == null) needConfirm = true;
			
			/* Create the title for the window */
			if (needConfirm)
				myTitle = "Enter new Password for " + pSource;
			else 
				myTitle = "Enter Password for " + pSource;
			
			/* Create a new password dialog */
			PasswordDialog 	myPass 	= new PasswordDialog(theCtl.getFrame(),
														 myTitle,
														 needConfirm);
			
			/* Prompt for the password */
			boolean 		isPasswordOk 	= false;
			while (showDialog(myPass)) {
				/* Access the password */
				char[] myPassword = myPass.getPassword();
				
				try {
					/* Check the password */
					myControl.initControl(myPassword);
					isPasswordOk 	= true;
					break;
				}
				catch (WrongPasswordException e) {
					myPass.setError("Incorrect password. Please re-enter");
					continue;
				}
			}
			
			/* Throw an exception if we did not get a password */
			if (!isPasswordOk)
				throw new Exception(ExceptionClass.DATA,
									"Invalid Password");
		}
		
		/* Return the control */
		return myControl;
	}	

	/**
	 *  Show the dialog under an invokeAndWait clause 
	 *  @param pDialog the dialog to show
	 */
	public boolean showDialog(final PasswordDialog pDialog) {
		
		/* If this is the event dispatcher thread */
		if (SwingUtilities.isEventDispatchThread()) {
			/* invoke the dialog directly */
			pDialog.showDialog();			
		}
		
		/* else we must use invokeAndWait */
		else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						/* invoke the dialog */
						pDialog.showDialog();
					}
				});
			}
		
			catch (Throwable e) {}
		}
		
		/* Return to caller */
		return pDialog.isPasswordSet();
	}
}
