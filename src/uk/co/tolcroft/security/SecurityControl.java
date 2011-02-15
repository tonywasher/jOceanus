package uk.co.tolcroft.security;

import java.security.*;

import javax.crypto.*;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class SecurityControl extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "SecurityControl";

	/**
	 * Message Digest algorithm
	 */
	protected final static String 	DIGEST 		= "SHA-256";
	
	/**
	 * Byte encoding
	 */
	public final static String 		ENCODING	= "UTF-8";
	
	/**
	 * The public/private separator 
	 */
	protected final static char		KEYSEP 		= '!';
	
	/**
	 * The secure random generator
	 */
	private SecureRandom			theRandom		= null;
	
	/**
	 * The secret key generator
	 */
	private KeyGenerator			theKeyGen		= null;

	/**
	 * The key pair generator
	 */
	private KeyPairGenerator		thePairGen		= null;

	/**
	 * The password key 
	 */
	private AsymmetricKey			theAsymKey		= null;

	/**
	 * The password key 
	 */
	private PasswordKey				thePassKey		= null;

	/**
	 * Is the security control initialised 
	 */
	private boolean					isInitialised	= false;

	/**
	 * The Security Key 
	 */
	private String					theSecurityKey	= null;

	/**
	 * The public key
	 */
	private String					thePublicKey	= null;

	/* Access methods */
	public 		boolean		isInitialised()		{ return isInitialised; }
	public 		boolean		newPassword()		{ return (theSecurityKey == null); }
	protected 	PasswordKey	getPassKey()		{ return thePassKey; }
	public 		String		getSecurityKey()	{ return theSecurityKey; }
	public 		String		getPublicKey()		{ return thePublicKey; }
	
	/**
	 * Constructor
	 * @param pSecurityKey the Encoded Security Bytes (or null if first initialisation)  
	 */
	private SecurityControl(List	pList,
						    String	pSecurityKey) throws Exception {
		/* Call super-constructor */
		super(pList, 0);
		
		/* Protect against exceptions */
		try { 
			/* Create a new secure random generator */
			theRandom 	= new SecureRandom();

			/* Create the secret key generator */
			theKeyGen 	= KeyGenerator.getInstance(SymmetricKey.ALGORITHM);
			theKeyGen.init(SymmetricKey.KEYSIZE, theRandom);

			/* Create an instance of the asymmetric key generator */
			thePairGen  = KeyPairGenerator.getInstance(AsymmetricKey.ALGORITHM);
			thePairGen.initialize(AsymmetricKey.KEYSIZE, theRandom);
		
			/* Store the security key */
			theSecurityKey = pSecurityKey;
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to initialise security control",
								e);
		}
	}
	
	/**
	 * Initialise the security control with a password
	 * @param pPassword the password (cleared after usage)
	 */
	public void initControl(char[] 	pPassword) throws WrongPasswordException,
													  Exception {
		/* Handle already initialised */
		if (isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control already initialised");
			
		/* Protect against exceptions */
		try { 
			/* If the security key is currently null */
			if (theSecurityKey == null) {
				/* Generate the password key */
				thePassKey 	= new PasswordKey(pPassword,
											  theRandom);
							
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(thePairGen.generateKeyPair(),
												thePassKey);			

				/* Access the security keys */
				theSecurityKey = theAsymKey.getSecurityKey();
				thePublicKey   = theAsymKey.getPublicKey();
			}
			
			/* Else we need to decode the keys */
			else {
				/* Generate the password key */
				thePassKey 	= new PasswordKey(theSecurityKey,
											  pPassword,
											  theRandom);
						
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(thePassKey.getKeyPair(),
												theSecurityKey,
												thePassKey);

				/* Access the public keys */
				thePublicKey   = theAsymKey.getPublicKey();
			}
			
			/* Note that we are now initialised */
			isInitialised = true;
		}
		
		catch (WrongPasswordException e) { throw e; }

		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to initialise security control",
								e);
		}
	}
	
	/**
	 * Seed the password key with the password
	 * @param pPassword the password (cleared after usage)
	 */
	public void setNewPassword(char[] pPassword) throws WrongPasswordException,
														Exception {
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Update the pass key with the new password */
		thePassKey.setNewPassword(pPassword);
		
		/* Adjust the Asymmetric key */
		theAsymKey.setSecurityControl(this);
		
		/* Access the security keys */
		theSecurityKey = theAsymKey.getSecurityKey();
		thePublicKey   = theAsymKey.getPublicKey();		
	}
	
	/**
	 * Obtain the signature for the file entry
	 * @param pEntry the ZipFile properties
	 * @return the signature 
	 */
	public byte[] signFile(ZipFileEntry pEntry) throws Exception {
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Sign the file */
		return theAsymKey.signFile(pEntry);		
	}
	
	/**
	 * Verify the signature for the zipFileEntry
	 * @param pEntry the ZipFile properties
	 */
	public void verifyFile(ZipFileEntry pEntry) throws Exception {
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* verify the file */
		theAsymKey.verifyFile(pEntry);		
	}
	
	/**
	 * Generate a new PasswordKey 
	 * @param pPassword the password (cleared after usage)
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]	pPassword) throws Exception {
		PasswordKey 	myPassKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the password key class */
		myPassKey = new PasswordKey(pPassword, theRandom);
		
		/* Return the new key */
		return myPassKey;
	}
	
	/**
	 * Generate a new PasswordKey for an existing salt
	 * @param pPassword the password (cleared after usage)
	 * @param pSaltAndHash the Salt And Hash array for the password 
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]	pPassword,
									   byte[]	pSaltAndHash) throws WrongPasswordException,
									   								 Exception {
		PasswordKey 	myPassKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the password key class */
		myPassKey = new PasswordKey(pSaltAndHash, pPassword, theRandom);
		
		/* Return the new key */
		return myPassKey;
	}
	
	/**
	 * Generate a new AsymmetricKey 
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey() throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(thePairGen.generateKeyPair(), thePassKey);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Generate a new AsymmetricKey 
	 * @param pSecurityKey the SecurityKey for the key 
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(String pSecurityKey) throws Exception {
		AsymmetricKey 	myAsymKey;
		KeyPair			myKeyPair;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Access the KeyPair */
		myKeyPair = thePassKey.getKeyPair(pSecurityKey);
		
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(myKeyPair, pSecurityKey, thePassKey);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Generate a new SymmetricKey 
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey() throws Exception {
		SecretKey 		myKey;
		SymmetricKey 	mySymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the Secret key */
		myKey = theKeyGen.generateKey();
		
		/* Generate the symmetric key class */
		mySymKey = new SymmetricKey(this, myKey, theRandom);
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Generate a SymmetricKey from wrapped key
	 * @param pWrappedKey the wrapped key
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey(byte[] pWrappedKey) throws Exception {
		SecretKey 		myKey;
		SymmetricKey 	mySymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Protect against exceptions */
		try {			
			/* unwrap the key */
			myKey = theAsymKey.unwrapSecretKey(pWrappedKey);

			/* Generate the symmetric key class */
			mySymKey = new SymmetricKey(this, myKey, pWrappedKey, theRandom);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to unwrap key",
								e);
		}
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Wrap secret key
	 * @param pKey the Key to wrap  
	 * @return the wrapped secret key
	 */
	protected byte[] getWrappedKey(SymmetricKey pKey) throws Exception {
		byte[] 				myWrappedKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Protect against exceptions */
		try {			
			/* wrap the key */
			myWrappedKey = theAsymKey.wrapSecretKey(pKey.getSecretKey());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to wrap key",
								e);
		}
		
		/* Return to caller */
		return myWrappedKey;
	}	

	/* Field IDs */
	public static final int FIELD_ID  		= 0;
	public static final int FIELD_INIT 		= 1;
	public static final int FIELD_SECKEY	= 2;
	public static final int FIELD_PUBKEY	= 3;
	public static final int NUMFIELDS	    = 4;
	
	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public String	fieldName(int iField) {
		switch (iField) {
			case FIELD_ID: 			return "ID";
			case FIELD_INIT: 		return "Initialised";
			case FIELD_SECKEY: 		return "SecurityKey";
			case FIELD_PUBKEY: 		return "PublicKey";
			default:		  		return super.fieldName(iField);
		}
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pObj the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, histObject pObj) {
		String myString = ""; 
		switch (iField) {
			case FIELD_ID: 			
				myString += getId();
				break;
			case FIELD_INIT: 
				myString +=	(isInitialised() ? "true" : "false");
				break;
			case FIELD_SECKEY:
				myString += theSecurityKey; 
				break;
			case FIELD_PUBKEY:
				myString += thePublicKey; 
				break;
		}
		return myString;
	}

	/**
	 * Compare this Bucket to another to establish equality.
	 * 
	 * @param pThat The Bucket to compare to
	 * @return <code>true</code> if the bucket is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a SecurityControl */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a SecurityControl */
		SecurityControl myControl = (SecurityControl)pThat;
		
		/* Check for equality */
		if (getId() != myControl.getId()) 		return false;
		return true;
	}

	/**
	 * Compare this Bucket to another to establish sort order.
	 * 
	 * @param pThat The Bucket to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a SecurityControl */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a SecurityControl */
		SecurityControl myThat = (SecurityControl)pThat;
		
		
		/* Compare the IDs */
		int iDiff = (int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}
	
	/* List of SecurityControls */
	public static class List extends DataList<SecurityControl> {
		/**
		 * Construct a top-level List
		 */
		public List() { super(ListStyle.VIEW, false); }

		/** 
	 	 * Clone a Bucket list
	 	 * @return the cloned list
	 	 */
		protected List cloneIt() { return null; }
		
		/**
		 * Add a new item to the list
		 * 
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public DataItem addNewItem(DataItem pItem) { return null; }
	
		/**
		 * Add a new item to the edit list
		 * 
		 * @param isCredit - ignored
		 */
		public void addNewItem(boolean isCredit) { return; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }		
		
		/**
		 * Access Security control
		 * @param pSecurityKey the SecurityKey (or null)
		 */
		public SecurityControl getSecurityControl(String pSecurityKey) throws Exception {
			SecurityControl myControl;
			ListIterator	myIterator;
			
			/* Create an iterator */
			myIterator = listIterator();
			
			/* Loop through the existing controls */
			while ((myControl = myIterator.next()) != null) {
				/* Break loop if we have found the control */
				if (!Utils.differs(pSecurityKey, myControl.getSecurityKey()))
					break;
			}
			
			/* If we did not find it */
			if (myControl == null) {
				/* Create a new control and add it to the list */
				myControl = new SecurityControl(this, pSecurityKey);
				myControl.addToList();
			}
			
			/* Return to caller */
			return myControl;
		}
	}
}
