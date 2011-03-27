package uk.co.tolcroft.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.SymmetricKey.SymKeyType;

/**
 * Asymmetric Key class.
 * Note that the RSA asymmetric key cannot be used for bulk encryption due to limitations in the RSA implementation. The Asymmetric Keys 
 * should only be used for Signatures and Wrapping keys.
 */
public class AsymmetricKey {
	/**
	 * Encrypted ID Key Size
	 */
	public 	  final static int		IDSIZE   		= 8000;
	
	/**
	 * Initialisation Vector size 
	 */
	public    final static int		IVSIZE   		= 16;
	
	/**
	 * Encryption algorithm
	 */
	private final static String 	FULLALGORITHM	= "/None/PKCS1Padding";
	
	/**
	 * Signature algorithm
	 */
	private final static String 	FULLSIGNATURE	= "SHA256with";
	
	/**
	 * The Public/Private Key Pair
	 */
	private KeyPair					theKeyPair		= null;

	/**
	 * The Key Type 
	 */
	private AsymKeyType				theKeyType		= null;
	
	/**
	 * The FullSignature 
	 */
	private String					theFullSignature= null;
	
	/**
	 * The FullAlgorithm 
	 */
	private String					theFullAlgorithm= null;
	
	/**
	 * The password key 
	 */
	private PasswordKey				thePassKey		= null;

	/**
	 * The secure random generator
	 */
	private SecureRandom			theRandom		= null;

	/**
	 * The Security Key 
	 */
	private String					theSecurityKey	= null;

	/**
	 * The Public Key 
	 */
	private String					thePublicKey	= null;

	/**
	 * Constructor
	 * @param pKeyType the key type 
	 * @param pPassKey the password key 
	 * @param pRandom the secure random generator 
	 */
	protected  AsymmetricKey(AsymKeyType	pKeyType,
							 PasswordKey	pPassKey,
							 SecureRandom	pRandom) throws Exception {
		/* Store the password key, key type and the secure random */
		theKeyType	= pKeyType;
		thePassKey	= pPassKey;
		theRandom	= pRandom;
		
		/* Generate the new KeyPair */
		theKeyPair	= AsymKeyPairGenerator.getInstance(theKeyType, theRandom);

		/* Record the full algorithm */
		theFullAlgorithm = pKeyType.toString() + FULLALGORITHM;
		theFullSignature = FULLSIGNATURE + pKeyType.toString();
	}
	
	/**
	 * Constructor
	 * @param pSecurityKey the security key 
	 * @param pKeyType the key type 
	 * @param pPassKey the password key 
	 * @param pRandom the secure random generator 
	 */
	protected  AsymmetricKey(String			pSecurityKey,
							 AsymKeyType	pKeyType,
			 				 PasswordKey	pPassKey,
			 				 SecureRandom	pRandom) throws Exception {
		/* Store the password key, key type and the secure random */
		theKeyType	= pKeyType;
		thePassKey	= pPassKey;
		theRandom	= pRandom;
		
		/* Obtain the unwrapped KeyPair */
		theKeyPair = thePassKey.unwrapKeyPair(pSecurityKey, pKeyType);
		
		/* Store the security key */
		theSecurityKey = pSecurityKey;

		/* Record the full algorithm */
		theFullAlgorithm = pKeyType.toString() + FULLALGORITHM;
		theFullSignature = FULLSIGNATURE + pKeyType.toString();
	}
	
	/**
	 * Compare this asymmetric key to another for equality 
	 * @param pThat the key to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		String myKey;
		String myThatKey;
		
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an Asymmetric Key */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Key */
		AsymmetricKey myThat = (AsymmetricKey)pThat;
	
		/* Protect against exceptions */
		try {
			/* Access the two security keys */
			myKey 		= getSecurityKey();
			myThatKey 	= myThat.getSecurityKey();
		}
		
		/* Handle failure */
		catch (Exception e) { return false; }
		
		/* Compare the two */
		return myKey.equals(myThatKey);
	}

	/**
	 * Set new security control
	 * @param pControl the security control
	 */
	public void	setSecurityControl(SecurityControl pControl) {
		/* Access the relevant pass key */
		thePassKey = pControl.getPassKey();
		
		/* Reset the Security keys */
		theSecurityKey 	= null;
		thePublicKey 	= null;
	}
	
	/**
	 * Obtain the wrapped security key 
	 * @return the Wrapped Public/Private pair
	 */
	public String		getSecurityKey() throws Exception {
		/* If we do not know the security key */
		if (theSecurityKey == null) {
			/* Calculate it */
			theSecurityKey = thePassKey.wrapKeyPair(theKeyPair, false);
			
			/* Keep a look out for the key being too large */
			if (theSecurityKey.length() > IDSIZE)
				throw new Exception(ExceptionClass.CRYPTO,
									"Security Key length too large: " + theSecurityKey.length());
		}
		
		/* Return it */
		return theSecurityKey; 
	}
	
	/**
	 * Obtain the wrapped public key 
	 * @return the Wrapped public key
	 */
	public String		getPublicKey() throws Exception {
		/* If we do not know the public key */
		if (thePublicKey == null) {
			/* Calculate it */
			thePublicKey = thePassKey.wrapKeyPair(theKeyPair, true);
		}
		
		/* Return it */
		return thePublicKey; 
	}
	
	/**
	 * Obtain secret key from wrapped key
	 * @param pWrappedKey the wrapped key
	 * @param pKeyType the key type that is being unwrapped
	 * @return the Secret key
	 */
	protected SecretKey	unwrapSecretKey(byte[]		pWrappedKey,
										SymKeyType 	pKeyType) throws Exception {
		SecretKey 		myKey;
		byte[]			myWrappedKey;
		Cipher			myCipher;
		
		/* Protect against exceptions */
		try {			
			/* Initialise the cipher */
			myCipher	= Cipher.getInstance(theKeyType.toString());
			myCipher.init(Cipher.UNWRAP_MODE, theKeyPair.getPrivate());
		
			/* Reverse the obscuring of the array */
			myWrappedKey = thePassKey.obscureArray(pWrappedKey);
			
			/* wrap the key */
			myKey = (SecretKey)myCipher.unwrap(myWrappedKey, 
											   pKeyType.toString(),
											   Cipher.SECRET_KEY);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to unwrap key",
								e);
		}
		
		/* Return the new key */
		return myKey;
	}
	
	/**
	 * Wrap secret key
	 * @param pKey the Key to wrap  
	 * @return the wrapped secret key
	 */
	protected byte[] wrapSecretKey(SecretKey pKey) throws Exception {
		byte[] 				myWrappedKey;
		Cipher				myCipher;
		
		/* Protect against exceptions */
		try {			
			/* Initialise the cipher */
			myCipher	= Cipher.getInstance(theKeyType.toString());
			myCipher.init(Cipher.WRAP_MODE, theKeyPair.getPublic());
		
			/* wrap the key */
			myWrappedKey = myCipher.wrap(pKey);
			
			/* Obscure the array */
			myWrappedKey = thePassKey.obscureArray(myWrappedKey);			
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to wrap key",
								e);
		}
		
		/* Return to caller */
		return myWrappedKey;
	}	

	/**
	 * Obtain the signature for the file entry
	 * @param pEntry the ZipFile properties
	 * @return the signature 
	 */
	protected byte[] signFile(ZipFileEntry pEntry) throws Exception {
		byte[]		myValue;	
		Signature	mySignature;
		int			iIndex;
		
		/* Protect against exceptions */
		try { 
			/* Cannot sign unless we have the private key */
			if (theKeyPair.getPrivate() == null)
				throw new Exception(ExceptionClass.LOGIC,
									"Cannot sign without private key"); 
				
			/* Create a signature */
			mySignature = Signature.getInstance(theFullSignature);
			mySignature.initSign(theKeyPair.getPrivate());
			
			/* Loop through the digests */
			for(iIndex=1; ; iIndex++) {
				myValue	= pEntry.getDigest(iIndex);
				if (myValue == null) break;
				mySignature.update(myValue);
			}
			
			/* Loop through the secret keys */
			for(iIndex=1; ; iIndex++) {
				myValue	= pEntry.getSecretKey(iIndex);
				if (myValue == null) break;
				mySignature.update(myValue);
			}
			
			/* Loop through the initialisation vectors */
			for(iIndex=1; ; iIndex++) {
				myValue	= pEntry.getInitVector(iIndex);
				if (myValue == null) break;
				mySignature.update(myValue);
			}
			
			/* Complete the signature */
			myValue = mySignature.sign();
		} 
	
		/* Catch exceptions */
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Exception calculating signature",
								e);
		}
		
		/* Return the signature */
		return myValue;
	}		
	
	/**
	 * Verify the signature for the zipFileEntry
	 * @param pEntry the ZipFile properties
	 */
	public void verifyFile(ZipFileEntry pEntry) throws Exception {
		byte[]		myValue;
		Signature	mySignature;
		int			iIndex;
		
		/* Protect against exceptions */
		try { 
			/* Create a signature */
			mySignature = Signature.getInstance(theFullSignature);
			mySignature.initVerify(theKeyPair.getPublic());

			/* Loop through the digests */
			for(iIndex=1; ; iIndex++) {
				myValue	= pEntry.getDigest(iIndex);
				if (myValue == null) break;
				mySignature.update(myValue);
			}
			
			/* Loop through the secret keys */
			for(iIndex=1; ; iIndex++) {
				myValue	= pEntry.getSecretKey(iIndex);
				if (myValue == null) break;
				mySignature.update(myValue);
			}
			
			/* Loop through the initialisation vectors */
			for(iIndex=1; ; iIndex++) {
				myValue	= pEntry.getInitVector(iIndex);
				if (myValue == null) break;
				mySignature.update(myValue);
			}
			
			/* Check the signature */
			if (!mySignature.verify(pEntry.getSignature())) {
				/* Throw an invalid file exception */
				throw new Exception(ExceptionClass.CRYPTO, 
									"Signature does not match");
			}
		} 
	
		/* Catch exceptions */
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Exception occurred verifying signature",
								e);
		}
	}				
	
	/**
	 * Encrypt string
	 * @param pString string to encrypt
	 * @return Encrypted bytes
	 * @throws Exception 
	 */
	public byte[] encryptString(String pString) throws Exception {
		byte[] myBytes;
		byte[] myOutput;
		int	   iBlockSize;
		int	   iOutSize;
		int	   iNumBlocks;
		int	   iNumBytes;
		int	   iOffset;
		int	   iOutOffs;
		Cipher myCipher;
		
		/* Protect against exceptions */
		try {
			/* Create the cipher */
			myCipher = Cipher.getInstance(theFullAlgorithm);
			myCipher.init(Cipher.ENCRYPT_MODE, theKeyPair.getPublic());
			
			/* Convert the string to a byte array */
			myBytes = pString.getBytes(SecurityControl.ENCODING);
			
			/* Determine the block sizes */
			iBlockSize 	= myCipher.getBlockSize();
			iOutSize 	= myCipher.getOutputSize(iBlockSize);
			
			/* Determine the number of blocks */
			int iDataLen = myBytes.length;
			iNumBlocks  = 1+((iDataLen-1)/iBlockSize);
			
			/* Allocate the output buffer */
			myOutput 	= new byte[iNumBlocks*iOutSize];
			
			/* Initialise offsets */
			iOffset  = 0;
			iOutOffs = 0;
			
			/* Loop through the bytes in units of iBlockSize */
			while (iDataLen > 0) {
				/* Determine the length of data to process */
				iNumBytes = iDataLen;
				if (iNumBytes > iBlockSize) iNumBytes = iBlockSize;
				
				/* Encrypt the data */
				iOutSize = myCipher.doFinal(myBytes, iOffset, iNumBytes, myOutput, iOutOffs);
					
				/* Adjust offsets */
				iDataLen -= iNumBytes;
				iOffset  += iNumBytes;
				iOutOffs += iOutSize;
			}
			
			/* Adjust output array correctly */
			if (iOutOffs < myOutput.length)
				myOutput = Arrays.copyOf(myOutput, iOutOffs);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to encrypt string",
								e);
		}
		
		/* Return to caller */
		return myOutput;
	}		
	
	/**
	 * Decrypt string
	 * @param pBytes encrypted string to decrypt
	 * @return Descrypted string
	 * @throws Exception 
	 */
	public String decryptString(byte[] pBytes) throws Exception {
		String myString;
		byte[] myOutput;
		int	   iBlockSize;
		int	   iOutSize;
		int	   iNumBlocks;
		int	   iNumBytes;
		int	   iOffset;
		int	   iOutOffs;
		Cipher myCipher;
		
		/* Protect against exceptions */
		try {
			/* Create the cipher */
			myCipher = Cipher.getInstance(theFullAlgorithm);
			myCipher.init(Cipher.DECRYPT_MODE, theKeyPair.getPrivate());
			
			/* Determine the block sizes */
			iBlockSize 	= myCipher.getBlockSize();
			iOutSize 	= myCipher.getOutputSize(iBlockSize);
			
			/* Determine the number of blocks */
			int iDataLen = pBytes.length;
			iNumBlocks  = 1+((iDataLen-1)/iBlockSize);
			
			/* Allocate the output buffer */
			myOutput 	= new byte[iNumBlocks*iOutSize];
			
			/* Initialise offsets */
			iOffset  = 0;
			iOutOffs = 0;
			
			/* Loop through the bytes in units of iBlockSize */
			while (iDataLen > 0) {
				/* Determine the length of data to process */
				iNumBytes = iDataLen;
				if (iNumBytes > iBlockSize) iNumBytes = iBlockSize;
				
				/* Encrypt the data */
				iOutSize = myCipher.doFinal(pBytes, iOffset, iNumBytes, myOutput, iOutOffs);
					
				/* Adjust offsets */
				iDataLen -= iNumBytes;
				iOffset  += iNumBytes;
				iOutOffs += iOutSize;
			}			
			
			/* Adjust output array correctly */
			if (iOutOffs < myOutput.length)
				myOutput = Arrays.copyOf(myOutput, iOutOffs);
			
			/* Create the string */
			myString = new String(myOutput);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to decrypt string",
								e);
		}
		
		/* Return to caller */
		return myString;
	}		
	
	/**
	 * Generator class
	 */
	private static class AsymKeyPairGenerator {
		/**
		 * Asymmetric key pair generator list
		 */
		private static AsymKeyPairGenerator 	theGenerators	= null;
		
		/* Members */
		private AsymKeyType				theKeyType 		= null;
		private KeyPairGenerator		theGenerator 	= null;
		private AsymKeyPairGenerator 	theNext			= null;
		
		/**
		 * Constructor
		 * @param pKeyType the symmetric key type
		 * @param pRandom the SecureRandom instance
		 */
		private AsymKeyPairGenerator(AsymKeyType 	pKeyType,
				 					 SecureRandom	pRandom) throws Exception {
			/* Protect against Exceptions */
			try {
				/* Create the key generator */
				theKeyType 		= pKeyType;
				theGenerator 	= KeyPairGenerator.getInstance(pKeyType.toString());
				theGenerator.initialize(pKeyType.getKeySize(), pRandom);
				
				/* Add to the list of generators */
				theNext			= theGenerators;
				theGenerators	= this;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				/* Throw the exception */
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to create key pair generator",
									e);
			}
		}

		/**
		 * Generate a new KeyPair of the specified type
		 * @param pKeyType the Asymmetric key type
		 * @param pRandom the SecureRandom instance
		 * @return the new KeyPair
		 */
		private static KeyPair getInstance(AsymKeyType 	pKeyType,
				 						   SecureRandom	pRandom) throws Exception {
			AsymKeyPairGenerator 	myCurr;
			KeyPair					myKeyPair;
			
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
				myCurr = new AsymKeyPairGenerator(pKeyType, pRandom);
			}
			
			/* Generate the Secret key */
			myKeyPair = myCurr.theGenerator.generateKeyPair();

			/* Return the new key */
			return myKeyPair;
		}
	}
	
	/**
	 * Asymmetric key types
	 */
	public enum AsymKeyType {
		RSA(1, 2048);
		
		/**
		 * Key values 
		 */
		private int theId = 0;
		private int theKeySize = 0;
		
		/* Access methods */
		public int getId() 		{ return theId; }
		public int getKeySize() { return theKeySize; }
		
		/**
		 * Constructor
		 */
		private AsymKeyType(int id, int keySize) {
			theId 		= id;
			theKeySize 	= keySize;
		}
		
		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static AsymKeyType fromId(int id) throws Exception {
			for (AsymKeyType myType: values()) {	if (myType.getId() == id) return myType; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid AsymKeyType: " + id);
		}

		/**
		 * Get random unique set of key types
		 * @param pNumTypes the number of types
		 * @param pRandom the random generator
		 * @return the random set
		 */
		public static AsymKeyType[] getRandomTypes(int pNumTypes, SecureRandom pRandom) throws Exception {
			/* Access the values */
			AsymKeyType[] myValues 	 = values();
			int			  iNumValues = myValues.length;
			int			  iIndex;
			
			/* Reject call if invalid number of types */
			if ((pNumTypes < 1) || (pNumTypes > iNumValues))
				throw new Exception(ExceptionClass.LOGIC,
									"Invalid number of types: " + pNumTypes);
			
			/* Create the result set */
			AsymKeyType[] myTypes  = new AsymKeyType[pNumTypes];
			
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
