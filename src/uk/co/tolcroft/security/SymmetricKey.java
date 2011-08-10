package uk.co.tolcroft.security;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Utils;

public class SymmetricKey {
	/**
	 * Encrypted ID Key Size
	 */
	public 	  final static int		IDSIZE   		= 256;
	
	/**
	 * Initialisation Vector size 
	 */
	public    final static int		IVSIZE   		= 16;
	
	/**
	 * The Secret Key 
	 */
	private SecretKey		theKey				= null;
	
	/**
	 * The Key Type 
	 */
	private SymKeyType		theKeyType			= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom	theRandom			= null;

	/**
	 * The Encoded KeyDef 
	 */
	private byte[]			theEncodedKeyDef	= null;

	/**
	 * Obtain the secret key
	 * @return the secret key
	 */
	protected SecretKey getSecretKey() 	{ return theKey; }

	/**
	 * Obtain the secret key type
	 * @return the secret key type
	 */
	public SymKeyType getKeyType() 	{ return theKeyType; }

	/**
	 * Encryption length
	 * @param pDataLength the length of data to be encrypted
	 * @return the length of encrypted data
	 */
	public static int	getEncryptionLength(int pDataLength) {
		int iBlocks = 1 +((pDataLength-1) % IVSIZE);
		return iBlocks * IVSIZE;
	}
	
	/**
	 * Constructor for a new randomly generated key
	 * @param pControl the security control 
	 * @param pKeyType Symmetric KeyType
	 * @param pRandom Secure Random byte generator
	 */
	public SymmetricKey(SymKeyType		pKeyType,
						SecureRandom	pRandom) throws Exception {
		/* Store the KeyType and the Secure Random instance */
		theKeyType		= pKeyType;
		theRandom 		= pRandom;
		
		/* Generate the new key */
		theKey				= SymKeyGenerator.getInstance(theKeyType, theRandom);
		theEncodedKeyDef	= theKey.getEncoded();
	}
	
	/**
	 * Constructor for a decoded symmetric key
	 * @param pKey Secret Key for algorithm
	 * @param pKeyType Symmetric KeyType
	 * @param pRandom Secure Random byte generator
	 */
	protected SymmetricKey(SecretKey		pKey,
			   			   SymKeyType		pKeyType,
						   SecureRandom		pRandom) throws Exception {
		/* Store the Control, KeyType and the Secure Random instance */
		theKeyType			= pKeyType;
		theRandom 			= pRandom;
		theKey				= pKey;
		theEncodedKeyDef	= theKey.getEncoded();
	}
	
	/**
	 * Hash for the Symmetric Key
	 * @return the hash value
	 */
	public int hashCode() {
		/* Calculate and return the hashCode for this symmetric key */
		int hashCode = 19 * theEncodedKeyDef.hashCode();
		hashCode += theKeyType.getId();
		return hashCode;
	}
	
	/**
	 * Compare this symmetric key to another for equality 
	 * @param pThat the key to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Symmetric Key */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Key */
		SymmetricKey myThat = (SymmetricKey)pThat;
	
		/* Not equal if different key-types */
		if (myThat.theKeyType != theKeyType) return false;
		
		/* Ensure that the secret key is identical */
		if (Utils.differs(myThat.theEncodedKeyDef, theEncodedKeyDef)) return false;
		
		/* Identical if those tests succeed */
		return true;
	}
	
	/**
	 * Initialise data cipher for encryption/decryption
	 * @return the Data Cipher
	 */
	public DataCipher initDataCipher() throws Exception {
		Cipher	myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
										  SecurityControl.BCSIGN);
			
			/* Return the Data Cipher */
			return new DataCipher(myCipher, this);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Initialise stream cipher for encryption with random initialisation vector
	 * @return the Stream Cipher
	 */
	public StreamCipher initEncryptionStream() throws Exception {
		Cipher	myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
					  					  SecurityControl.BCSIGN);
			
			/* Initialise the cipher generating a random Initialisation vector */
			myCipher.init(Cipher.ENCRYPT_MODE, theKey, theRandom);
			
			/* Return the Stream Cipher */
			return new StreamCipher(myCipher, myCipher.getIV());
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Initialise Stream cipher for decryption with initialisation vector
	 * @param Initialisation vector for cipher
	 * @return the Stream Cipher
	 */
	public StreamCipher initDecryptionStream(byte[] pInitVector) throws Exception {
		AlgorithmParameterSpec 	myParms;
		Cipher					myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
					  					  SecurityControl.BCSIGN);
			
			/* Initialise the cipher using the password */
			myParms = new IvParameterSpec(pInitVector);
			myCipher.init(Cipher.DECRYPT_MODE, theKey, myParms);
			
			/* Return the Stream Cipher */
			return new StreamCipher(myCipher, pInitVector);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Generator class
	 */
	private static class SymKeyGenerator {
		/**
		 * Symmetric key generator list
		 */
		private static SymKeyGenerator 	theGenerators	= null;
		
		/* Members */
		private SymKeyType 		theKeyType 		= null;
		private KeyGenerator	theGenerator 	= null;
		private SymKeyGenerator theNext			= null;
		
		/**
		 * Constructor
		 * @param pKeyType the symmetric key type
		 * @param pRandom the SecureRandom instance
		 */
		private SymKeyGenerator(SymKeyType 		pKeyType,
								SecureRandom	pRandom) throws Exception {
			/* Protect against Exceptions */
			try {
				/* Create the key generator */
				theKeyType 		= pKeyType;
				theGenerator 	= KeyGenerator.getInstance(pKeyType.getAlgorithm(), 
						  								   SecurityControl.BCSIGN);
				theGenerator.init(pKeyType.getKeySize(), pRandom);
				
				/* Add to the list of generators */
				theNext			= theGenerators;
				theGenerators	= this;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				/* Throw the exception */
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to create key generator",
									e);
			}
		}

		/**
		 * Generate a new key of the specified type
		 * @param pKeyType the symmetric key type
		 * @param pRandom the SecureRandom instance
		 * @return the new key
		 */
		private static SecretKey getInstance(SymKeyType 	pKeyType,
											 SecureRandom	pRandom) throws Exception {
			SymKeyGenerator myCurr;
			SecretKey		myKey;
			
			/* Locate the key generator */
			for (myCurr  = theGenerators; 
				 myCurr != null; 
				 myCurr  = myCurr.theNext) {
				/* If we have found the type break the loop */
				if (myCurr.theKeyType == pKeyType) break;
			}
			
			/* If we have not found the generator */
			if (myCurr == null) {
				/* Create a new generator */
				myCurr = new SymKeyGenerator(pKeyType, pRandom);
			}
			
			/* Generate the Secret key */
			myKey = myCurr.theGenerator.generateKey();

			/* Return the new key */
			return myKey;
		}
	}
	
	/**
	 * Symmetric key definition
	 */
	public static class KeyDef {
		/* Members */
		private SymKeyType	theType	= null;
		private SecretKey 	theKey 	= null;
		private byte[]		theIv	= null;
		
		/* Access methods */
		public SymKeyType 	getType() 	{ return theType; }
		public SecretKey 	getKey() 	{ return theKey; }
		public byte[]		getIv()		{ return theIv; }
		
		/**
		 * Constructor
		 */
		protected KeyDef(SymKeyType pType, SecretKey pKey, byte[] pIv) {
			theType = pType;
			theKey 	= pKey;
			theIv  	= pIv;
		}
	}
	
	/**
	 * Symmetric key types
	 */
	public enum SymKeyType {
		AES(1, 256, IVSIZE),
		TwoFish(2, 256, IVSIZE),
		Serpent(3, 256, IVSIZE),
		CAMELLIA(4, 256, IVSIZE),
		RC6(5, 256, IVSIZE),
		CAST6(6, 256, IVSIZE);
		
		/**
		 * Symmetric full algorithm
		 */
		private final static String 	FULLALGORITHM	= "/CBC/PKCS5PADDING";
		
		/**
		 * Key values 
		 */
		private int theId = 0;
		private int theKeySize = 0;
		private int theIvLen = 0;
		
		/* Access methods */
		public int 		getId() 		{ return theId; }
		public int 		getKeySize() 	{ return theKeySize; }
		public int 		getIvLen() 		{ return theIvLen; }
		public String 	getAlgorithm() 	{ return toString(); }
		public String 	getCipher() 	{ return getAlgorithm() + FULLALGORITHM; }
		
		/**
		 * Constructor
		 */
		private SymKeyType(int id, int keySize, int IvLen) {
			theId 		= id;
			theKeySize 	= keySize;
			theIvLen	= IvLen;
		}
		
		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static SymKeyType fromId(int id) throws Exception {
			for (SymKeyType myType: values()) {	if (myType.getId() == id) return myType; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid SymKeyType: " + id);
		}
		
		/**
		 * Get random unique set of key types
		 * @param pNumTypes the number of types
		 * @param pRandom the random generator
		 * @return the random set
		 */
		public static SymKeyType[] getRandomTypes(int pNumTypes, SecureRandom pRandom) throws Exception {
			/* Access the values */
			SymKeyType[] myValues 	= values();
			int			 iNumValues = myValues.length;
			int			 iIndex;
			
			/* Reject call if invalid number of types */
			if ((pNumTypes < 1) || (pNumTypes > iNumValues))
				throw new Exception(ExceptionClass.LOGIC,
									"Invalid number of types: " + pNumTypes);
			
			/* Create the result set */
			SymKeyType[] myTypes  = new SymKeyType[pNumTypes];
			
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
