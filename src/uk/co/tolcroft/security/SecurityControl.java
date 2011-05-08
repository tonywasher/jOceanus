package uk.co.tolcroft.security;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.AsymmetricKey.AsymKeyType;
import uk.co.tolcroft.security.SymmetricKey.SymKeyType;

public class SecurityControl extends DataItem {
	/**
	 * The name of the object
	 */
	private static final String objName = "SecurityControl";

	/**
	 * Byte encoding
	 */
	public final static String 		ENCODING	= "UTF-8";
	
	/**
	 * The public/private separator 
	 */
	protected final static char		KEYSEP 		= '!';
	
	/**
	 * The BouncyCastle signature 
	 */
	protected final static String	BCSIGN 		= "BC";
	
	/**
	 * Have providers been added 
	 */
	protected static boolean		providersAdded	= false;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom			theRandom		= null;
	
	/**
	 * The control mode
	 */
	private ControlMode				theControlMode	= null;
	
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
	private X509EncodedKeySpec		thePublicKey	= null;

	/* Access methods */
	public 		boolean				isInitialised()		{ return isInitialised; }
	public 		boolean				newPassword()		{ return (theSecurityKey == null); }
	protected 	AsymmetricKey		getAsymKey()		{ return theAsymKey; }
	protected 	PasswordKey			getPassKey()		{ return thePassKey; }
	public 		ControlMode			getControlMode()	{ return theControlMode; }
	public 		String				getSecurityKey()	{ return theSecurityKey; }
	public 		X509EncodedKeySpec	getPublicKey()		{ return thePublicKey; }
	public 		SecureRandom		getRandom()			{ return theRandom; }
	
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

			/* Store the security key */
			theSecurityKey = pSecurityKey;
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to initialise security control",
								e);
		}
	}
	
	/**
	 * Constructor as a clone of another security control
	 * @param pSource
	 */
	public SecurityControl(SecurityControl pSource) throws Exception {
		/* Call super-constructor */
		super((List)pSource.getList(), 0);

		/* Copy the random generator */
		theRandom 	= pSource.getRandom();

		/* Generate a cloned password key */
		thePassKey = new PasswordKey(pSource.getPassKey());
		
		/* Access the key mode */
		theControlMode = thePassKey.getKeyMode();
		
		/* Create the asymmetric key */
		theAsymKey  = new AsymmetricKey(theControlMode.getAsymKeyType(),
										theControlMode.getSymKeyType(),
										thePassKey,
										theRandom);			
		
		/* Access the security keys */
		theSecurityKey = theAsymKey.getSecurityKey();
		thePublicKey   = theAsymKey.getPublicKey();		
		
		/* Note that we are now initialised and add to the list */
		isInitialised = true;
		addToList();
	}
	
	/**
	 * Initialise the security control with a password
	 * @param pPassword the password (cleared after usage)
	 */
	public synchronized void initControl(char[] pPassword) throws WrongPasswordException,
													 			  Exception {
		/* Handle already initialised */
		if (isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control already initialised");
			
		/* Protect against exceptions */
		try {
			/* If we have not previously added providers */
			if (!providersAdded) {
				/* Ensure addition of Bouncy castle security provider */
				Security.addProvider(new BouncyCastleProvider());
				providersAdded = true;
			}
			
			/* If the security key is currently null */
			if (theSecurityKey == null) {
				/* Generate the key mode */
				theControlMode = ControlMode.getControlMode(theRandom);
				
				/* Generate the password key */
				thePassKey 	= new PasswordKey(pPassword,
											  theControlMode,
											  theRandom);
							
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(theControlMode.getAsymKeyType(),
												theControlMode.getSymKeyType(),
												thePassKey,
												theRandom);			

				/* Access the security keys */
				theSecurityKey = theAsymKey.getSecurityKey();
				thePublicKey   = theAsymKey.getPublicKey();
			}
			
			/* Else we need to decode the keys */
			else {
				/* Rebuild the password key */
				thePassKey 	= new PasswordKey(theSecurityKey,
											  pPassword,
											  theRandom);
				/* Access the control mode */
				theControlMode = thePassKey.getKeyMode();
				
				/* Rebuild the asymmetric key */
				theAsymKey  = new AsymmetricKey(theSecurityKey,
												theControlMode.getAsymKeyType(),
												theControlMode.getSymKeyType(),
												thePassKey,
												theRandom);

				/* Access the public keys */
				thePublicKey   = theAsymKey.getPublicKey();
			}
			
			/* Note that we are now initialised */
			isInitialised = true;
		}
		
		catch (WrongPasswordException e) { throw e; }

		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
		theAsymKey.setPasswordKey(thePassKey);
		
		/* Access the security keys */
		theSecurityKey = theAsymKey.getSecurityKey();
	}

	/**
	 * ReSeed the random number generator
	 */
	public void reSeedRandom() {
		/* Generate and apply the new seed */
		byte[] mySeed = SecureRandom.getSeed(8);
		theRandom.setSeed(mySeed);
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
	 * @param pKeyMode the key mode
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]		pPassword,
									   ControlMode 	pKeyMode) throws Exception {
		PasswordKey 	myPassKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the password key class */
		myPassKey = new PasswordKey(pPassword, pKeyMode, theRandom);
		
		/* Return the new key */
		return myPassKey;
	}
	
	/**
	 * Generate a new PasswordKey for an existing salt
	 * @param pPassword the password (cleared after usage)
	 * @param pSaltAndHash the Salt And Hash array for the password 
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]		pPassword,
									   byte[]		pSaltAndHash) throws WrongPasswordException,
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
	 * @param pKeyType the Asymmetric key type
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(AsymKeyType pKeyType) throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(pKeyType, theControlMode.getSymKeyType(), thePassKey, theRandom);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Rebuild an AsymmetricKey from a security key 
	 * @param pSecurityKey the SecurityKey for the key
	 * @param pKeyType the Asymmetric key type
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(String 		pSecurityKey,
											 AsymKeyType	pKeyType) throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(pSecurityKey, pKeyType, theControlMode.getSymKeyType(), thePassKey, theRandom);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Generate a new SymmetricKey 
	 * @param pType the Symmetric key type
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey(SymKeyType pType) throws Exception {
		SymmetricKey 	mySymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the symmetric key class */
		mySymKey = new SymmetricKey(this, pType, theRandom);
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Rebuild a SymmetricKey from wrapped key
	 * @param pWrappedKey the wrapped key
	 * @param pType the Symmetric key type
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey(byte[] 		pWrappedKey,
										SymKeyType	pType) throws Exception {
		SymmetricKey 	mySymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Build the symmetric key class */
		mySymKey = new SymmetricKey(this, pWrappedKey, pType, theRandom);
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Obtain secret key from wrapped key
	 * @param pWrappedKey the wrapped key
	 * @param pKeyType the key type that is being unwrapped
	 * @return the Secret key
	 */
	protected SecretKey	unwrapSecretKey(byte[]		pWrappedKey,
										SymKeyType 	pKeyType) throws Exception {
		/* Pass call to the Asymmetric Key */
		return theAsymKey.unwrapSecretKey(pWrappedKey, pKeyType);
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
			myWrappedKey = theAsymKey.wrapSecretKey(pKey.getSecretKey(),
													pKey.getKeyType());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public SecurityControl addNewItem(DataItem pItem) { return null; }
	
		/**
		 * Add a new item to the edit list
		 * @param isCredit - ignored
		 */
		public SecurityControl addNewItem(boolean isCredit) { return null; }
	
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
	
	/**
	 * Digest type
	 */
	public enum DigestType {
		SHA256(1),
		Tiger(2),
		WHIRLPOOL(3);

		/**
		 * Key values 
		 */
		private int theId = 0;
		
		/* Access methods */
		public int getId() 		{ return theId; }
		
		/**
		 * Constructor
		 */
		private DigestType(int id) {
			theId 		= id;
		}
		
		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static DigestType fromId(int id) throws Exception {
			for (DigestType myType: values()) {	if (myType.getId() == id) return myType; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid DigestType: " + id);
		}

		/**
		 * Return the associated algorithm
		 * @return the algorithm
		 */
		public String getAlgorithm() {
			switch (this) {
				case SHA256: 	return "SHA-256";
				default:		return toString();
			}
		}
		
		/**
		 * Get random unique set of digest types
		 * @param pNumTypes the number of types
		 * @param pRandom the random generator
		 * @return the random set
		 */
		public static DigestType[] getRandomTypes(int pNumTypes, SecureRandom pRandom) throws Exception {
			/* Access the values */
			DigestType[] myValues 	= values();
			int			 iNumValues = myValues.length;
			int			 iIndex;
			
			/* Reject call if invalid number of types */
			if ((pNumTypes < 1) || (pNumTypes > iNumValues))
				throw new Exception(ExceptionClass.LOGIC,
									"Invalid number of digests: " + pNumTypes);
			
			/* Create the result set */
			DigestType[] myTypes  = new DigestType[pNumTypes];
			
			/* Loop through the types */
			for (int i=0; i<pNumTypes; i++) {
				/* Access the next random index */
				iIndex = pRandom.nextInt(iNumValues);
				
				/* Store the type */
				myTypes[i] = myValues[iIndex];
				
				/* Shift last value down in place of the one thats been used */
				myValues[iIndex] = myValues[iNumValues - 1];
				iNumValues--;
			}
			
			/* Return the types */
			return myTypes;
		}
	}
}
