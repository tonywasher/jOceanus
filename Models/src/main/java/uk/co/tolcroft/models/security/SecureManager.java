package uk.co.tolcroft.models.security;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.SortedList;
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
											  String 			pSource) throws ModelException {
		/* Access or create the required Security control */
		SecurityControl myControl = theSecurity.getSecurityControl(pKey);

		/* If it is not initialised try known passwords */
		if ((pKey != null) &&
			(!myControl.isInitialised())) 
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
			
			/* If we did not get a password */
			if (!isPasswordOk) {
				/* Remove control if it is not initialised */
				if (!myControl.isInitialised())
					theSecurity.remove(myControl);
				
				/* Throw an exception */
				throw new ModelException(ExceptionClass.DATA,
									"Invalid Password");
			}
		}
		
		/* Return the control */
		return myControl;
	}	

	/**
	 *  Clone the existing Security Control
	 *  @param pControl the control
	 *  @return the cloned security control
	 */
	public SecurityControl cloneSecurityControl(SecurityControl pControl) throws ModelException {
		/* Access or create the required Security control */
		SecurityControl myControl = theSecurity.cloneSecurityControl(pControl);
		
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
	
	/**
	 * Attempt known passwords
	 * @param pControl the control to attempt
	 */
	private void attemptKnownPasswords(SecurityControl pControl) {
		SortedList<SecurityControl>.ListIterator 	myIterator;
		SecurityControl								myCurr;
		PasswordHash								myPassHash;
		
		/* Access the iterator */
		myIterator = theSecurity.listIterator();
		
		/* Loop through the security controls */
		while ((myCurr = myIterator.next()) != null) {
			/* Skip if not initialised */
			if (!myCurr.isInitialised()) continue;
			
			/* Access the password hash for this control */
			myPassHash = myCurr.getPasswordHash();
			
			/* Attempt to initialise the control from this password */
			myPassHash.attemptPassword(pControl);
			
			/* Break loop if we managed to initialise it */
			if (pControl.isInitialised()) break;
		}
	}
}
