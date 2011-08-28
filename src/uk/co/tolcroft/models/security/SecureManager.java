package uk.co.tolcroft.models.security;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.ui.PasswordDialog;

public class SecureManager {
	/* Members */
	private SecurityControl.List 	theSecurity = null;
	private JFrame					theFrame 	= null;
	
	/* Constructor */
	public SecureManager() {
		/* Allocate a new Security list */ 
		theSecurity = new SecurityControl.List();
	}	

	/**
	 * Set the Frame for the Secure Manager
	 * @param pFrame the frame 
	 */
	public void setFrame(JFrame pFrame) { theFrame = pFrame; }
	
	/**
	 *  Access the required Security Control
	 *  @param pKey the key for the required security control
	 *  @param pSource the description of the secured resource
	 *  @return the security control
	 */
	public SecurityControl getSecurityControl(SecuritySignature pKey,
											  String 			pSource) throws Exception {
		/* Access or create the required Security control */
		SecurityControl myControl = theSecurity.getSecurityControl(pKey);

		/* If it is not initialised try known passwords */
		if (!myControl.isInitialised()) 
			attemptKnownPasswords(myControl);
		
		/* If the security control is not initialised */
		if (!myControl.isInitialised()) {
			String 	myTitle;
			boolean	needConfirm = false;
			
			/* Determine whether we need confirmation */
			if (pKey == null) needConfirm = true;
			
			/* Create the title for the window */
			if (needConfirm)
				myTitle = "Enter New Password for " + pSource;
			else 
				myTitle = "Enter Password for " + pSource;
			
			/* Create a new password dialog */
			PasswordDialog 	myPass 	= new PasswordDialog(theFrame,
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
				finally {
					/* Clear out the password */
					if (myPassword != null)
						Arrays.fill(myPassword, (char) 0);
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
	 *  Update the Security Control
	 *  @param pControl the existing security control
	 *  @param pSource the description of the secured resource
	 *  @return was the password changed <code>true/false</code>
	 */
	public boolean updateSecurityControl(SecurityControl 	pControl,
									     String 			pSource) throws Exception {
		/* Create the title for the window */
		String myTitle = "Enter New Password for " + pSource;
			
		/* Create a new password dialog */
		PasswordDialog 	myPass 	= new PasswordDialog(theFrame,
													 myTitle,
													 true);
			
		/* Prompt for the password */
		if (showDialog(myPass)) {
			/* Access the password */
			char[] myPassword = myPass.getPassword();

			/* Update the password */
			pControl.setNewPassword(myPassword);
			return true;
		}
		
		/* Return to caller */
		return false;
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
	
	/**
	 * Attempt known passwords
	 * @param pControl the control to attempt
	 */
	private void attemptKnownPasswords(SecurityControl pControl) {
		DataList<SecurityControl>.ListIterator 	myIterator;
		SecurityControl							myCurr;
		PasswordKey								myPassKey;
		
		/* Access the iterator */
		myIterator = theSecurity.listIterator();
		
		/* Loop through the security controls */
		while ((myCurr = myIterator.next()) != null) {
			/* Skip if not initialised */
			if (!myCurr.isInitialised()) continue;
			
			/* Access the password key for this control */
			myPassKey = myCurr.getPassKey();
			
			/* Attempt to initialise the control from this password */
			myPassKey.attemptPassword(pControl);
			
			/* Break loop if we managed to initialise it */
			if (pControl.isInitialised()) break;
		}
	}
}
