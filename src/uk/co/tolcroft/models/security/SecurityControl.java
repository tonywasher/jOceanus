package uk.co.tolcroft.models.security;

import java.security.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.PropertySet.PropertySetChooser;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class SecurityControl extends SortedItem<SecurityControl> implements PropertySetChooser {
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
	 * Security Properties
	 */
	private SecurityProperties			theProperties			= null;
	
	/**
	 * Use restricted security 
	 */
	protected boolean					useRestricted			= false;
	
	/**
	 * Have providers been added 
	 */
	protected static boolean			providersAdded			= false;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom				theRandom				= null;
	
	/**
	 * The asymmetric key 
	 */
	private AsymmetricKey				theAsymKey				= null;

	/**
	 * The password hash 
	 */
	private PasswordHash				thePassHash				= null;

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
	private Map<SymmetricKey, byte[]>	theKeyDefMap			= null;
	
	/* Access methods */
	public 		boolean				isInitialised()			{ return isInitialised; }
	protected 	AsymmetricKey		getAsymKey()			{ return theAsymKey; }
	protected 	PasswordHash		getPasswordHash()		{ return thePassHash; }
	public 		SecuritySignature	getSignature()			{ return theSignature; }
	public 		SecureRandom		getRandom()				{ return theRandom; }
	public 		boolean				useRestricted()			{ return useRestricted; }
	
	@Override
	public Class<? extends PropertySet> getPropertySetClass() { return SecurityProperties.class; }
	
	/**
	 * Constructor
	 * @param pSignature the Security Signature Bytes (or null if first initialisation)  
	 */
	private SecurityControl(List				pList,
						    SecuritySignature	pSignature) throws Exception {
		/* Call super-constructor */
		super(pList);
		
		/* Access the security properties */
		theProperties 	= (SecurityProperties)PropertyManager.getPropertySet(this);

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
		super(pSource.getList());

		/* Access the security properties */
		theProperties 	= (SecurityProperties)PropertyManager.getPropertySet(this);

		/* Copy the random generator */
		theRandom 	= pSource.getRandom();

		/* Generate a cloned password hash */
		thePassHash	= new PasswordHash(pSource.getPasswordHash());
		
		/* Determine whether we are using restricted mode */
		/* TODO pick up new Restricted property */
		useRestricted = thePassHash.getSecurityMode().useRestricted();
		
		/* Generate the new key mode */
		SecurityMode myMode 	= SecurityMode.getAsymmetricMode(useRestricted, theRandom);		
		
		/* Create the asymmetric key */
		theAsymKey  = new AsymmetricKey(myMode,
										theRandom);			
		
		/* Create the signature */
		theSignature = new SecuritySignature(thePassHash.getPasswordHash(),
											 myMode,
											 theAsymKey.getPublicKeyDef(),
											 thePassHash.getSecuredPrivateKey(theAsymKey));
		
		/* Create the SymmetricKeyDef Map */
		theKeyDefMap = new HashMap<SymmetricKey, byte[]>();
		
		/* Note that we are now initialised and add to the list */
		isInitialised = true;
		pSource.getList().add(this);
	}

	/**
	 * Print out a set of algorithms
	 * @param setName the name of the set
	 * @param algorithms the set of algorithms
	 */
	private void printSet(String setName, Set<String> algorithms) {
		System.out.println(setName + ":");
		if (algorithms.isEmpty()) {
			System.out.println("            None available.");
		} else {
			Iterator<String> it = algorithms.iterator();
			while (it.hasNext()) {
				String name = it.next();
				System.out.println("            " + name);
			}
		}
	}
	
	/**
	 * List the supported algorithms
	 */
	private void listAlgorithms() {
		Provider[] providers = Security.getProviders();
		Set<String> ciphers = new HashSet<String>();
		Set<String> keyFactories = new HashSet<String>();
		Set<String> messageDigests = new HashSet<String>();
		Set<String> signatures = new HashSet<String>();

		for (int i = 0; i != providers.length; i++) {
			if (!providers[i].getName().equals(BCSIGN)) continue;
			Iterator<Object> it = providers[i].keySet().iterator();
			while (it.hasNext()) {
				String entry = (String) it.next();
				if (entry.startsWith("Alg.Alias.")) {
					entry = entry.substring("Alg.Alias.".length());
				}
				if (entry.startsWith("Cipher.")) {
					ciphers.add(entry.substring("Cipher.".length()));
				} else if (entry.startsWith("SecretKeyFactory.")) {
					keyFactories.add(entry.substring("SecretKeyFactory.".length()));
				} else if (entry.startsWith("MessageDigest.")) {
					messageDigests.add(entry.substring("MessageDigest.".length()));
				} else if (entry.startsWith("Signature.")) {
					signatures.add(entry.substring("Signature.".length()));
				}
			}
		}

		printSet("Ciphers", ciphers);
		printSet("SecretKeyFactories", keyFactories);
		printSet("MessageDigests", messageDigests);
		printSet("Signatures", signatures);
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
				boolean bDebug = false;
				
				/* Ensure addition of Bouncy castle security provider */
				Security.addProvider(new BouncyCastleProvider());
				providersAdded = true;
				if (bDebug) listAlgorithms();
			}
			
			/* Create a new secure random generator */
			theRandom 	= new SecureRandom();

			/* If the security key is currently null */
			if (theSignature == null) {
				/* Generate the password hash */
				/* TODO pick up new Restricted property */
				thePassHash 	= new PasswordHash(pPassword,
												   useRestricted,
												   theRandom);
							
				/* Generate the new key mode */
				SecurityMode myMode 	= SecurityMode.getAsymmetricMode(useRestricted, theRandom);		
				
				/* Create the asymmetric key */
				theAsymKey  = new AsymmetricKey(myMode,
												theRandom);			

				/* Create the signature */
				theSignature = new SecuritySignature(thePassHash.getPasswordHash(),
													 myMode,
													 theAsymKey.getPublicKeyDef(),
													 thePassHash.getSecuredPrivateKey(theAsymKey));
			}
			
			/* Else we need to decode the keys */
			else {
				/* Rebuild the password hash */
				thePassHash	= new PasswordHash(theSignature.getPasswordHash(),
											   pPassword,
											   theRandom);
				
				/* Determine whether we are using restricted mode */
				useRestricted = thePassHash.getSecurityMode().useRestricted();
				
				/* Rebuild the asymmetric key */
				theAsymKey  = thePassHash.getAsymmetricKey(theSignature.getSecuredKeyDef(),
														   theSignature.getPublicKey(),
														   theSignature.getKeyMode());
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
	 * Generate a new PasswordHash 
	 * @param pPassword the password (cleared after usage)
	 * @return the Password hash
	 */
	public PasswordHash	getPasswordHash(char[]		pPassword) throws Exception {
		PasswordHash 	myPassHash;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the password hash class */
		myPassHash = new PasswordHash(pPassword, 
									  useRestricted, 
									  theRandom);
		
		/* Return the new hash */
		return myPassHash;
	}
	
	/**
	 * Generate a new PasswordHash for an existing salt
	 * @param pPassword the password (cleared after usage)
	 * @param pSaltAndHash the Salt And Hash array for the password 
	 * @return the Password Hash
	 */
	public PasswordHash	getPasswordHash(char[]	pPassword,
										byte[]	pSaltAndHash) throws WrongPasswordException,
									   							 	 Exception {
		PasswordHash 	myPassHash;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the password hash class */
		myPassHash = new PasswordHash(pSaltAndHash, pPassword, theRandom);
		
		/* Return the new hash */
		return myPassHash;
	}
	
	/**
	 * Generate a new AsymmetricKey 
	 * @param pKeyMode the Asymmetric key mode
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(SecurityMode pKeyMode) throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the asymmetric key class */
		myAsymKey = new AsymmetricKey(pKeyMode, theRandom);
		
		/* Return the new key */
		return myAsymKey;
	}
	
	/**
	 * Rebuild an AsymmetricKey from a security key 
	 * @param pSecuredPrivateKey the Secured Private Key definition  
	 * @param pPublicKey the Public Key  
	 * @param pKeyMode the Asymmetric key mode
	 * @return the Asymmetric key
	 */
	public AsymmetricKey	getAsymmetricKey(byte[] 		pSecuredPrivateKey,
											 byte[]			pPublicKey,
											 SecurityMode	pKeyMode) throws Exception {
		AsymmetricKey 	myAsymKey;
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Generate the asymmetric key class */
		myAsymKey = thePassHash.getAsymmetricKey(pSecuredPrivateKey, pPublicKey, pKeyMode);
		
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
		mySymKey = new SymmetricKey(pType, useRestricted, theRandom);
		
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
		
		/* Handle not initialised */
		if (!isInitialised)
			throw new Exception(ExceptionClass.LOGIC,
								"Security Control uninitialised");
			
		/* Obtain the symmetric key via the Asymmetric key */
		mySymKey = theAsymKey.getSymmetricKey(pSecuredKeyDef, useRestricted, pType);
		
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
		myKeyDef = theAsymKey.getSecuredKeyDef(pKey, useRestricted);
				
		/* Check whether the KeyDef is too large */
		if (myKeyDef.length > SymmetricKey.IDSIZE)
			throw new Exception(ExceptionClass.DATA,
								"Secured KeyDefinition too large: " + myKeyDef.length);
			
		/* Add the key to the map */
		theKeyDefMap.put(pKey, myKeyDef);
		
		/* Return it */
		return myKeyDef; 
	}
	
	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Compare this Control to another to establish equality.
	 * @param pThat The Control to compare to
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
	 * Compare this Control to another to establish sort order.
	 * @param pThat The Control to compare to
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
				
		/* Compare the Signatures */
		if (theSignature == myThat.theSignature) return 0;
		if (theSignature == null) return 1;
		return theSignature.compareTo(myThat.theSignature);
	}
	
	/* List of SecurityControls */
	public static class List extends SortedList<SecurityControl> {
		/**
		 * Construct a top-level List
		 */
		public List() { super(SecurityControl.class); }

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

		/**
		 * Clone Security control
		 * @param pControl the existing control
		 */
		protected SecurityControl cloneSecurityControl(SecurityControl pControl) throws Exception {
			SecurityControl myControl = null;

			/* Create a new control and add it to the list */
			myControl = new SecurityControl(pControl);
			add(myControl);
			
			/* Return to caller */
			return myControl;
		}
	}
	
	/**
	 * Security Properties
	 */
	public static class SecurityProperties extends PropertySet {
		/**
		 * Registry name for Restricted Security
		 */
		protected final static String 	nameRestricted	= "RestrictedKeys";

		/**
		 * Registry name for Cipher Steps
		 */
		protected final static String 	nameCipherSteps	= "CipherSteps";

		/**
		 * Display name for Restricted Security
		 */
		protected final static String 	dispRestricted	= "Restricted Keys";

		/**
		 * Display name for Cipher Steps
		 */
		protected final static String 	dispCipherSteps	= "Number of CipherSteps";

		/**
		 * Default Restricted Security
		 */
		private final static Boolean	defRestricted	= Boolean.FALSE;		

		/**
		 * Default Cipher Steps
		 */
		private final static Integer	defCipherSteps	= 3;		

		/**
		 * Constructor
		 * @throws Exception
		 */
		public SecurityProperties() throws Exception { super();	}

		@Override
		protected void defineProperties() {
			/* Define the properties */
			defineProperty(nameRestricted, PropertyType.Boolean);
			defineProperty(nameCipherSteps, PropertyType.Integer);
		}

		@Override
		protected Object getDefaultValue(String pName) {
			/* Handle default values */
			if (pName.equals(nameRestricted))	return defRestricted;
			if (pName.equals(nameCipherSteps))	return defCipherSteps;
			return null;
		}
		
		@Override
		protected String getDisplayName(String pName) {
			/* Handle default values */
			if (pName.equals(nameRestricted)) 	return dispRestricted;
			if (pName.equals(nameCipherSteps)) 	return dispCipherSteps;
			return null;
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
