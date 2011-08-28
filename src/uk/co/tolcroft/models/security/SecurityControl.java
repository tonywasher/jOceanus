package uk.co.tolcroft.models.security;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.security.AsymmetricKey.AsymKeyType;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class SecurityControl extends DataItem<SecurityControl> {
	/**
	 * The name of the object
	 */
	private static final String 		objName 				= "SecurityControl";

	/**
	 * Byte encoding
	 */
	public final static String 			ENCODING				= "UTF-8";
	
	/**
	 * The BouncyCastle signature 
	 */
	protected final static String		BCSIGN 					= "BC";
	
	/**
	 * Have providers been added 
	 */
	protected static boolean			providersAdded			= false;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom				theRandom				= null;
	
	/**
	 * The password key 
	 */
	private AsymmetricKey				theAsymKey				= null;

	/**
	 * The password key 
	 */
	private PasswordKey					thePassKey				= null;

	/**
	 * Is the security control initialised 
	 */
	private boolean						isInitialised			= false;

	/**
	 * The Signature 
	 */
	private SecuritySignature			theSignature			= null;

	/**
	 * The Symmetric Key Map
	 */
	private Map<SymmetricKey, byte[]>	theKeyDefMap	= null;
	
	/* Access methods */
	public 		boolean				isInitialised()			{ return isInitialised; }
	protected 	AsymmetricKey		getAsymKey()			{ return theAsymKey; }
	protected 	PasswordKey			getPassKey()			{ return thePassKey; }
	public 		SecuritySignature	getSignature()			{ return theSignature; }
	public 		SecureRandom		getRandom()				{ return theRandom; }
	
	/**
	 * Constructor
	 * @param pSignature the Security Signature Bytes (or null if first initialisation)  
	 */
	private SecurityControl(List				pList,
						    SecuritySignature	pSignature) throws Exception {
		/* Call super-constructor */
		super(pList, 0);
		
		/* Store the security key */
		theSignature = pSignature;

		/* Create the SymmetricKeyDef Map */
		theKeyDefMap = new HashMap<SymmetricKey, byte[]>();
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
		thePassKey  = new PasswordKey(pSource.getPassKey());
		
		/* Generate the new key mode */
		AsymKeyType[] myType 	= AsymKeyType.getRandomTypes(1, theRandom);		
		
		/* Create the asymmetric key */
		theAsymKey  = new AsymmetricKey(myType[0],
										theRandom);			
		
		/* Create the signature */
		theSignature = new SecuritySignature(thePassKey.getPasswordHash(),
											 myType[0],
											 theAsymKey.getPublicKey(),
											 thePassKey.getSecuredPrivateKey(theAsymKey));
		
		/* Create the SymmetricKeyDef Map */
		theKeyDefMap = new HashMap<SymmetricKey, byte[]>();
		
		/* Note that we are now initialised and add to the list */
		isInitialised = true;
		getList().add(this);
	}
	
	/**
	 * Initialise the security control with a password
	 * @param pPassword the password (cleared after usage)
	 */
	public synchronized void initControl(char[] pPassword) throws WrongPasswordException, Exception {
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
			
			/* Create a new secure random generator */
			theRandom 	= new SecureRandom();

			/* If the security key is currently null */
			if (theSignature == null) {
				/* Generate the password key */
				thePassKey 	= new PasswordKey(pPassword,
											  theRandom);
							
				/* Generate the new key mode */
				AsymKeyType[] myType 	= AsymKeyType.getRandomTypes(1, theRandom);		
				
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(myType[0],
												theRandom);			

				/* Create the signature */
				theSignature = new SecuritySignature(thePassKey.getPasswordHash(),
													 myType[0],
													 theAsymKey.getPublicKey(),
													 thePassKey.getSecuredPrivateKey(theAsymKey));
			}
			
			/* Else we need to decode the keys */
			else {
				/* Rebuild the password key */
				thePassKey 	= new PasswordKey(theSignature.getPasswordHash(),
											  pPassword,
											  theRandom);
				/* Access the control mode */
				//theKeyType = thePassKey.getKeyMode().getAsymKeyType();
				
				/* Rebuild the asymmetric key */
				theAsymKey  = thePassKey.getAsymmetricKey(theSignature.getSecuredKeyDef(),
														  theSignature.getPublicKey(),
														  theSignature.getKeyType());
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
			
		/* Clear the Symmetric KeyDef map */
		theKeyDefMap.clear();
		
		/* Update the pass key with the new password */
		thePassKey.setNewPassword(pPassword);
		
		/* Access the updated key definitions */
		theSignature.setSecuredKeyDef(thePassKey.getSecuredPrivateKey(theAsymKey));
		theSignature.setPasswordHash(thePassKey.getPasswordHash());
	}

	/**
	 * ReSeed the random number generator
	 */
	public void reSeedRandom() {
		/* Generate and apply the new seed */
		byte[] mySeed = SecureRandom.getSeed(32);
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
	 * @return the Password key
	 */
	public PasswordKey	getPasswordKey(char[]		pPassword) throws Exception {
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
		myAsymKey = new AsymmetricKey(pKeyType, theRandom);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Rebuild an AsymmetricKey from a security key 
	 * @param pSecurityKey the SecurityKey for the key
	 * @param pKeyType the Asymmetric key type
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(byte[] 			pSecuredPrivateKey,
											 X509EncodedKeySpec	pPublicKey,
											 AsymKeyType		pKeyType) throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the asymmetric key class */
		myAsymKey = thePassKey.getAsymmetricKey(pSecuredPrivateKey, pPublicKey, pKeyType);
		
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
			
		/* Generate the symmetric key */
		mySymKey = new SymmetricKey(pType, theRandom);
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Rebuild a SymmetricKey from secured key definition
	 * @param pSecuredKeyDef the secured key definition
	 * @param pType the Symmetric key type
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey(byte[] 		pSecuredKeyDef,
										SymKeyType	pType) throws Exception {
		SymmetricKey 	mySymKey;
		byte[]			myKeyDef;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Reverse the obscuring of the array */
		myKeyDef = thePassKey.obscureArray(pSecuredKeyDef);
	
		/* Obtain the symmetric key via the Asymmetric key */
		mySymKey = theAsymKey.getSymmetricKey(myKeyDef, pType);
		
		/* Add the key definition to the map */
		theKeyDefMap.put(mySymKey, pSecuredKeyDef);
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Obtain the Secured Key Definition for a Symmetric Key
	 * @param pKey the Symmetric Key to secure
	 * @return the Secured Key Definition
	 */
	public byte[] 	getSecuredKeyDef(SymmetricKey pKey) throws Exception {
		byte[] myKeyDef;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Look for an entry in the map and return it if found */
		myKeyDef = theKeyDefMap.get(pKey);
		if (myKeyDef != null) return myKeyDef;
		
		/* wrap the key definition */
		myKeyDef = theAsymKey.getSecuredKeyDef(pKey);
				
		/* Obscure the key  definition */
		myKeyDef = thePassKey.obscureArray(myKeyDef);
		
		/* Check whether the KeyDef is too large */
		if (myKeyDef.length > SymmetricKey.IDSIZE)
			throw new Exception(ExceptionClass.DATA,
								"Secured KeyDefinition too large: " + myKeyDef.length);
			
		/* Add the key to the map */
		theKeyDefMap.put(pKey, myKeyDef);
		
		/* Return it */
		return myKeyDef; 
	}
	
	/* Field IDs */
	public static final int FIELD_INIT 		= 0;
	public static final int FIELD_HASH		= 1;
	public static final int FIELD_TYPE		= 2;
	public static final int FIELD_PRVKEY	= 3;
	public static final int FIELD_PUBKEY	= 4;
	public static final int NUMFIELDS	    = 5;
	
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
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_INIT: 		return "Initialised";
			case FIELD_HASH: 		return "PasswordHash";
			case FIELD_TYPE: 		return "KeyType";
			case FIELD_PRVKEY: 		return "PrivateKey";
			case FIELD_PUBKEY: 		return "PublicKey";
			default:		  		return DataItem.fieldName(iField);
		}
	}
	
	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { return fieldName(iField); }
	
	/**
	 * Format the value of a particular field as a table row
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(int iField, HistoryValues<SecurityControl> pValues) {
		String myString = ""; 
		switch (iField) {
			case FIELD_INIT: 
				myString +=	(isInitialised() ? "true" : "false");
				break;
			case FIELD_HASH:
				myString += (theSignature == null) ? null : Utils.HexStringFromBytes(theSignature.getPasswordHash()); 
				break;
			case FIELD_TYPE:
				myString += (theSignature == null) ? null : theSignature.getKeyType().toString(); 
				break;
			case FIELD_PRVKEY:
				myString += (theSignature == null) ? null : Utils.HexStringFromBytes(theSignature.getSecuredKeyDef()); 
				break;
			case FIELD_PUBKEY:
				myString += (theSignature == null) ? null : Utils.HexStringFromBytes(theSignature.getEncodedPublicKey()); 
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
		
		/* Check for signature differences */
		if (theSignature == null) {
			return (myControl.theSignature == null);
		}
		else {
			return theSignature.equals(myControl.theSignature);
		}
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
		public List() { super(SecurityControl.class, ListStyle.VIEW, false); }

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
		public SecurityControl addNewItem(DataItem<?> pItem) { return null; }
	
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
		 * @param pSignature the Signature (or null)
		 */
		public SecurityControl getSecurityControl(SecuritySignature pSignature) throws Exception {
			SecurityControl myControl = null;
			ListIterator	myIterator;
			
			/* Create an iterator */
			myIterator = listIterator();
			
			/* If we have a signature */
			if (pSignature != null) {
				/* Loop through the existing controls */
				while ((myControl = myIterator.next()) != null) {
					/* Break loop if we have found the control */
					if (pSignature.equals(myControl.getSignature()))
						break;
				}
			}
			
			/* If we did not find it */
			if (myControl == null) {
				/* Create a new control and add it to the list */
				myControl = new SecurityControl(this, pSignature);
				add(myControl);
			}
			
			/* Return to caller */
			return myControl;
		}
	}
	
	/**
	 * Digest type
	 */
	public enum DigestType {
		SHA256(1, 256),
		Tiger(2, 192),
		WHIRLPOOL(3, 512),
		RIPEMD(4, 320),
		GOST(5, 256);

		/**
		 * Key values 
		 */
		private int theId = 0;
		private int theHashLen = 0;
		
		/* Access methods */
		public int getId() 		{ return theId; }
		public int getHashLen()	{ return theHashLen; }
		
		/**
		 * Constructor
		 */
		private DigestType(int id, int iLen) {
			theId 		= id;
			theHashLen	= iLen;
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
				case RIPEMD: 	return "RIPEMD320";
				case GOST: 		return "GOST3411";
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