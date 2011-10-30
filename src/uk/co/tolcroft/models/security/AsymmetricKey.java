package uk.co.tolcroft.models.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;
import uk.co.tolcroft.models.Utils;

/**
 * Asymmetric Key class.
 * Note that the RSA asymmetric key cannot be used for bulk encryption due to limitations in the RSA implementation. The Asymmetric Keys 
 * should only be used for Signatures and Wrapping keys.
 */
public class AsymmetricKey {
	/**
	 * Initialisation Vector size 
	 */
	public    final static int		IVSIZE   			= 16;
	
	/**
	 * Encoded Size for Public Keys
	 */
	public static final int 		PUBLICSIZE	 		= 512;
	
	/**
	 * Encrypted Size for Private Keys
	 */
	public static final int 		PRIVATESIZE	 		= 1280;
	
	/**
	 * The Public/Private Key Pair
	 */
	private KeyPair					theKeyPair			= null;

	/**
	 * The Key Type 
	 */
	private AsymKeyType				theKeyType			= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom			theRandom			= null;

	/**
	 * The Key Agreement object
	 */
	private KeyAgreement			theKeyAgreement		= null;

	/**
	 * The Encoded Public Key 
	 */
	private byte[]					thePublicKeyDef		= null;

	/**
	 * The Encoded Private Key 
	 */
	private byte[]					thePrivateKeyDef	= null;

	/**
	 * The CipherSet 
	 */
	private CipherSet				theCipherSet		= null;

	/**
	 * The CipherSet map
	 */
	private Map<AsymmetricKey, CipherSet>	theMap		= null;
	
	/* Access methods */
	public AsymKeyType	getKeyType()		{ return theKeyType; }
	public boolean		isPublicOnly()		{ return (theKeyPair.getPrivate() == null); }
	public PrivateKey	getPrivateKey()		{ return theKeyPair.getPrivate(); }
	public byte[]		getPublicKeyDef()	{ return thePublicKeyDef; }
	
	/**
	 * Constructor for new key
	 * @param pKeyType the key type 
	 * @param pPassKey the password key 
	 * @param pRandom the secure random generator 
	 */
	public AsymmetricKey(AsymKeyType	pKeyType,
						 SecureRandom	pRandom) throws Exception {
		/* Store the password key, key type and the secure random */
		theKeyType		= pKeyType;
		theRandom		= pRandom;
		
		/* Generate the new KeyPair */
		theKeyPair	= AsymKeyPairGenerator.getInstance(theKeyType, theRandom);
		
		/* Access the encoded formats */
		thePrivateKeyDef = theKeyPair.getPrivate().getEncoded();
		thePublicKeyDef  = theKeyPair.getPublic().getEncoded();
	
		/* Create the map for elliptic keys */
		if (theKeyType.isElliptic())
			theMap = new HashMap<AsymmetricKey, CipherSet>();
		
		/* Check whether the PublicKey is too large */
		if (thePublicKeyDef.length > PUBLICSIZE)
			throw new Exception(ExceptionClass.DATA,
								"PublicKey too large: " + thePublicKeyDef.length);			
	}
	
	/**
	 * Constructor from public KeySpec
	 * @param pKeySpec the public KeySpec 
	 * @param pKeyType the key type 
	 * @param pRandom the secure random generator 
	 */
	public AsymmetricKey(byte[]			pKeySpec,
						 AsymKeyType	pKeyType,
		 				 SecureRandom	pRandom) throws Exception {
		/* Store the key types */
		theKeyType	= pKeyType;
		theRandom	= pRandom;
		
		/* Obtain the KeyPair */
		theKeyPair = AsymKeyFactory.getKeyPair(null, pKeySpec, pKeyType);

		/* Access the encoded formats */
		thePublicKeyDef  = theKeyPair.getPublic().getEncoded();

		/* Create the map for elliptic keys */
		if (theKeyType.isElliptic())
			theMap = new HashMap<AsymmetricKey, CipherSet>();
	}
	
	/**
	 * Constructor from Security Signature
	 * @param pSignature the signature 
	 * @param pKeyType the key type 
	 * @param pRandom the secure random generator 
	 */
	protected  AsymmetricKey(byte[]			pPrivateKey,
							 byte[]			pPublicKey,
							 AsymKeyType	pKeyType,
			 				 SecureRandom	pRandom) throws Exception {
		/* Store the password key, key type and the secure random */
		theKeyType	= pKeyType;
		theRandom	= pRandom;
		
		/* Obtain the KeyPair */
		theKeyPair = AsymKeyFactory.getKeyPair(pPrivateKey, pPublicKey, pKeyType);

		/* Access the encoded formats */
		thePrivateKeyDef = theKeyPair.getPrivate().getEncoded();
		thePublicKeyDef  = theKeyPair.getPublic().getEncoded();

		/* Create the map for elliptic keys */
		if (theKeyType.isElliptic())
			theMap = new HashMap<AsymmetricKey, CipherSet>();
	}
	
	/**
	 * Hash for the Asymmetric Key
	 * @return the hash value
	 */
	public int hashCode() {
		/* Calculate and return the hashCode for this asymmetric key */
		int hashCode = 19 * thePublicKeyDef.hashCode();
		if (thePrivateKeyDef != null)
			hashCode += thePrivateKeyDef.hashCode();
		hashCode *= 19;
		hashCode += theKeyType.getId();
		return hashCode;
	}
	
	/**
	 * Compare this asymmetric key to another for equality 
	 * @param pThat the key to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is an Asymmetric Key */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Key */
		AsymmetricKey myThat = (AsymmetricKey)pThat;
	
		/* Not equal if different key-types */
		if (myThat.theKeyType != theKeyType) return false;
		
		/* Ensure that the private/public keys are identical */
		if (Utils.differs(myThat.thePrivateKeyDef, thePrivateKeyDef).isDifferent()) return false;
		if (Utils.differs(myThat.thePublicKeyDef, thePublicKeyDef).isDifferent()) 	return false;
		
		/* Identical if those tests succeed */
		return true;
	}

	/**
	 * Get CipherSet for partner Elliptic Curve
	 * @param pPartner partner asymmetric key
	 */
	public CipherSet getCipherSet(AsymmetricKey pPartner) throws Exception {
		/* Look for an already resolved CipherSet */
		CipherSet mySet = theMap.get(pPartner);
		
		/* Return it if found */
		if (mySet != null) return mySet;
		
		/* Access the Shared Secret */
		byte[] mySecret = getSharedSecret(pPartner);
		
		/* Build the CipherSet */
		mySet = new CipherSet(theRandom, CipherSet.DEFSTEPS);
		
		/* Apply the Secret */
		mySet.buildCiphers(mySecret);
		
		/* Store the Set into the map */
		theMap.put(pPartner, mySet);
		
		/* Return the Cipher Set */
		return mySet;
	}
	
	/**
	 * Get CipherSet for internal Elliptic Curve
	 */
	private CipherSet getCipherSet() throws Exception {
		/* Return PreExisting set */
		if (theCipherSet != null) return theCipherSet;
		
		/* Build the internal CipherSet */
		theCipherSet = getCipherSet(this);
		
		/* Return the Cipher Set */
		return theCipherSet;
	}
	
	/**
	 * Rebuild a SymmetricKey from secured key definition
	 * @param pSecuredKeyDef the secured key definition
	 * @param pKeyType the Symmetric key type
	 * @return the Symmetric key
	 */
	public SymmetricKey	getSymmetricKey(byte[] 		pSecuredKeyDef,
										SymKeyType	pKeyType) throws Exception {
		SymmetricKey 	mySymKey;
		CipherSet		mySet;
		SecretKey		myKey;
		Cipher			myCipher;
		
		/* Cannot unwrap unless we have the private key */
		if (isPublicOnly())
			throw new Exception(ExceptionClass.LOGIC,
								"Cannot unwrap without private key"); 
			
		/* Protect against exceptions */
		try {			
			/* If we are elliptic */
			if (theKeyType.isElliptic()) {
				/* Access the internal CipherSet */
				mySet = getCipherSet();

				/* Unwrap the Key */
				mySymKey = mySet.unWrapKey(pSecuredKeyDef, pKeyType);
			}
			
			/* else we use RAS semantics */
			else {
				/* Initialise the cipher */
				myCipher	= Cipher.getInstance(theKeyType.getAlgorithm(), 
												 SecurityControl.BCSIGN);
				myCipher.init(Cipher.UNWRAP_MODE, 
							  theKeyPair.getPrivate());		
				
				/* unwrap the key */
				myKey = (SecretKey)myCipher.unwrap(pSecuredKeyDef, 
										   	       pKeyType.getAlgorithm(),
										   	       Cipher.SECRET_KEY);

				/* Build the symmetric key */
				mySymKey = new SymmetricKey(myKey, pKeyType, theRandom);
			}
		}
		
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to unwrap key",
								e);
		}
		
		/* Return the new key */
		return mySymKey;
	}
	
	/**
	 * Get the Secured Key Definition for a Symmetric Key
	 * @param pKey the Symmetric Key to secure
	 * @return the secured key definition
	 */
	public byte[] getSecuredKeyDef(SymmetricKey 	pKey) throws Exception {
		byte[] 		myWrappedKey;
		Cipher		myCipher;
		CipherSet	mySet;
		
		/* Protect against exceptions */
		try {			
			/* If we are elliptic */
			if (theKeyType.isElliptic()) {
				/* Access the internal CipherSet */
				mySet = getCipherSet();

				/* Wrap the Key */
				myWrappedKey = mySet.wrapKey(pKey);
			}
			
			/* else we are using RSA semantics */
			else {
				/* Initialise the cipher */
				myCipher	= Cipher.getInstance(theKeyType.getAlgorithm(),
												 SecurityControl.BCSIGN);
				myCipher.init(Cipher.WRAP_MODE, 
							  theKeyPair.getPublic());
				
				/* wrap the key */
				myWrappedKey = myCipher.wrap(pKey.getSecretKey());
			}			
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
	 * Obtain shared secret for partner Asymmetric Key
	 * @param pPartner partner asymmetric key
	 * @return the shared secret
	 */
	protected synchronized byte[] getSharedSecret(AsymmetricKey pPartner) throws Exception {		
		/* Both keys must be elliptic */
		if ((!theKeyType.isElliptic()) ||
			(pPartner.theKeyType != theKeyType)) 
			throw new Exception(ExceptionClass.LOGIC,
								"Shared Keys require both partners to be similar Elliptic");

		/* Cannot generate unless we have the private key */
		if (theKeyPair.getPrivate() == null)
			throw new Exception(ExceptionClass.LOGIC,
								"Cannot generate secret without private key"); 
			
		/* Protect against exceptions */
		try {
			/* If we do not currently have a key Agreement */
			if (theKeyAgreement == null) {
				/* Create the key agreement */
				theKeyAgreement = KeyAgreement.getInstance("ECDH", SecurityControl.BCSIGN);
			}
			
			/* Process the key agreement */
			theKeyAgreement.init(theKeyPair.getPrivate());
			theKeyAgreement.doPhase(pPartner.theKeyPair.getPublic(), true);
			
			/* Generate the secret  */
			return theKeyAgreement.generateSecret();
		}
		
		/* Handle exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to negotiate key agreement",
								e); 
		}
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
			mySignature = Signature.getInstance(theKeyType.getSignature(), 
												SecurityControl.BCSIGN);
			mySignature.initSign(theKeyPair.getPrivate(), 
								 theRandom);
			
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
			mySignature = Signature.getInstance(theKeyType.getSignature(), 
												SecurityControl.BCSIGN);
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
	 * @param pTarget target partner of encryption
	 * @return Encrypted bytes
	 * @throws Exception 
	 */
	public byte[] encryptString(String 			pString,
								AsymmetricKey	pTarget) throws Exception {
		/* Target must be identical key type */
		if (pTarget.theKeyType != theKeyType) 
			throw new Exception(ExceptionClass.LOGIC,
								"Asymmetric Encryption must be between similar partners");
		
		/* Protect against exceptions */
		try {
			/* If we are elliptic */
			if (theKeyType.isElliptic()) {
				/* Access the target CipherSet */
				CipherSet mySet = getCipherSet(pTarget);

				/* Encrypt the string */
				return mySet.encryptString(pString);
			}
		
			/* else handle RSA semantics */
			else return encryptRSAString(pString, pTarget);
		}
		
		/* Catch exceptions */
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Exception occurred initialising cipher",
								e);
		}
	}
	
	/**
	 * Encrypt string
	 * @param pString string to encrypt
	 * @param pTarget target partner of encryption
	 * @return Encrypted bytes
	 * @throws Exception 
	 */
	private byte[] encryptRSAString(String 			pString,
									AsymmetricKey	pTarget) throws Exception {
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
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
					  					  SecurityControl.BCSIGN);
			myCipher.init(Cipher.ENCRYPT_MODE, 
						  pTarget.theKeyPair.getPublic());
			
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
	 * @param pSource source partner of encryption
	 * @return Decrypted string
	 * @throws Exception 
	 */
	public String decryptString(byte[] 			pBytes,
								AsymmetricKey	pSource) throws Exception {
		/* Cannot decrypt unless we have the private key */
		if (isPublicOnly())
			throw new Exception(ExceptionClass.LOGIC,
								"Cannot decrypt without private key"); 
			
		/* Source must be identical key type */
		if (pSource.theKeyType != theKeyType) 
			throw new Exception(ExceptionClass.LOGIC,
								"Asymmetric Encryption must be between similar partners");
		
		/* Protect against exceptions */
		try {
			/* If we are elliptic */
			if (theKeyType.isElliptic()) {
				/* Access the required CipherSet */
				CipherSet mySet = getCipherSet(pSource);

				/* Decrypt the string */
				return mySet.decryptString(pBytes);
			}
		
			/* else handle RSA semantics */
			else return decryptRSAString(pBytes);
		}
	
		/* Catch exceptions */
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Exception occurred initialising cipher",
								e);
		}
	}

	/**
	 * Decrypt RSA string
	 * @param pBytes encrypted string to decrypt
	 * @param pSource source partner of encryption
	 * @return Decrypted string
	 * @throws Exception 
	 */
	private String decryptRSAString(byte[] 			pBytes) throws Exception {
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
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
					  					  SecurityControl.BCSIGN);
			myCipher.init(Cipher.DECRYPT_MODE, 
						  theKeyPair.getPrivate());
			
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
			myString = new String(myOutput, SecurityControl.ENCODING);
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
				theGenerator 	= KeyPairGenerator.getInstance(pKeyType.getAlgorithm(), 
															   SecurityControl.BCSIGN);
				
				/* Handle elliptic curve key types differently */
				if (pKeyType.isElliptic()) {
					/* Initialise with the parameter specification for the curve */
					ECGenParameterSpec parms = new ECGenParameterSpec(theKeyType.getCurve());
					theGenerator.initialize(parms, pRandom);					
				}
				
				/* Else standard RSA type */
				else theGenerator.initialize(pKeyType.getKeySize(), pRandom);
				
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
		private static synchronized KeyPair getInstance(AsymKeyType 	pKeyType,
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
			
			/* Generate the Key Pair */
			myKeyPair = myCurr.theGenerator.generateKeyPair();

			/* Return the new key */
			return myKeyPair;
		}
	}
	
	/**
	 * KeyFactory class
	 */
	protected static class AsymKeyFactory {
		/**
		 * Asymmetric key pair generator list
		 */
		private static AsymKeyFactory 	theFactories	= null;
		
		/* Members */
		private AsymKeyType				theKeyType 		= null;
		private KeyFactory				theFactory 		= null;
		private AsymKeyFactory 			theNext			= null;
		
		/**
		 * Constructor
		 * @param pKeyType the symmetric key type
		 */
		private AsymKeyFactory(AsymKeyType 	pKeyType) throws Exception {
			/* Protect against Exceptions */
			try {
				/* Create the key generator */
				theKeyType 		= pKeyType;
				theFactory 		= KeyFactory.getInstance(pKeyType.getAlgorithm(), 
														 SecurityControl.BCSIGN);
				
				/* Add to the list of generators */
				theNext			= theFactories;
				theFactories	= this;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				/* Throw the exception */
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to create key factory",
									e);
			}
		}

		/**
		 * Obtain KeyPair from encoded forms
		 * @param pPrivate the Encoded private form (may be null for public-only)
		 * @param pPublic the Encoded public form 
		 * @param pKeyType the Asymmetric key type
		 * @return the new KeyPair
		 */
		private static synchronized KeyPair getKeyPair(byte[]		pPrivate,
													   byte[]		pPublic,
													   AsymKeyType 	pKeyType) throws Exception {
			AsymKeyFactory 	myCurr;
			
			/* Locate the key factory */
			for (myCurr  = theFactories; 
				 myCurr != null; 
				 myCurr  = myCurr.theNext) {
				/* If we have found the type break the loop */
				if (myCurr.theKeyType == pKeyType) break;
			}
			
			/* If we have not found the factory */
			if (myCurr == null) {
				/* Create a new factory */
				myCurr = new AsymKeyFactory(pKeyType);
			}
			
			/* Protect against exceptions */
			try {
				PrivateKey 	myPrivate 	= null;
				PublicKey	myPublic	= null;
				
				/* if we have a private key */
				if (pPrivate != null) {
					/* Build the private key */
					PKCS8EncodedKeySpec myPrivSpec = new PKCS8EncodedKeySpec(pPrivate);
					myPrivate = myCurr.theFactory.generatePrivate(myPrivSpec);
				}
				
				/* Build the public key */
				X509EncodedKeySpec myPubSpec = new X509EncodedKeySpec(pPublic); 
				myPublic = myCurr.theFactory.generatePublic(myPubSpec);
			
				/* Return the private key */
				return new KeyPair(myPublic, myPrivate);
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				/* Throw the exception */
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to re-build private key",
									e);
			}
		}
	}
	
	/**
	 * Asymmetric key types
	 */
	public enum AsymKeyType {
		//RSA(1, 2048),
		EC1(2, "secp384r1"),
		EC2(3, "secp521r1"),
		EC3(4, "c2tnb431r1"),
		EC4(5, "sect409r1"),
		EC5(6, "sect571r1"),
		EC6(7, "brainpoolp384t1");
		
		/**
		 * Encryption algorithm
		 */
		private final static String 	BASEALGORITHM	= "/None/OAEPWithSHA256AndMGF1Padding";
		
		/**
		 * Signature algorithm
		 */
		private final static String 	BASESIGNATURE	= "SHA256with";
		
		/**
		 * Key values 
		 */
		private int 		theId 			= 0;
		private int 		theKeySize 		= 0;
		private String		theCurve		= null;
		private boolean		isElliptic		= false;
		
		/* Access methods */
		public int 			getId() 		{ return theId; }
		public int 			getKeySize() 	{ return theKeySize; }
		public String 		getCurve() 		{ return theCurve; }
		public boolean		isElliptic() 	{ return isElliptic; }
		public String		getAlgorithm()	{
			if (isElliptic) return "EC";
			else			return toString();
		}
		public String		getSignature()	{
			if (isElliptic) return BASESIGNATURE + "ECDSA";
			else			return BASESIGNATURE + toString();
		}
		public String		getCipher()	{
			if (isElliptic) return "Null";
			else			return toString() + BASEALGORITHM;
		}

		/**
		 * Constructor
		 * @param id the id
		 * @param keySize the RSA Key size
		 */
		private AsymKeyType(int id, int keySize) {
			theId 		= id;
			theKeySize 	= keySize;
		}
		
		/**
		 * Constructor
		 * @param id the id
		 * @param pCurve the keySize the RSA Key size
		 */
		private AsymKeyType(int id, String pCurve) {
			theId 			= id;
			theCurve 		= pCurve;
			isElliptic		= true;
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
