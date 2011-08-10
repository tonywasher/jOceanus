package uk.co.tolcroft.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.security.SymmetricKey.KeyDef;
import uk.co.tolcroft.security.SymmetricKey.SymKeyType;

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
	public static final int 		PRIVATESIZE	 		= 1024;
	
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

	/* Access methods */
	public AsymKeyType	getKeyType()	{ return theKeyType; }
	public boolean		isPublicOnly()	{ return (theKeyPair.getPrivate() == null); }
	public PrivateKey	getPrivateKey()	{ return theKeyPair.getPrivate(); }
	
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
		
		/* Check whether the PublicKey is too large */
		if (thePublicKeyDef.length > PUBLICSIZE)
			throw new Exception(ExceptionClass.DATA,
								"PublicKey too large: " + thePublicKeyDef.length);			
	}
	
	/**
	 * Constructor from public KeySpec
	 * @param pKeySpec the public KeySpec 
	 * @param pKeyType the key type 
	 */
	public AsymmetricKey(X509EncodedKeySpec	pKeySpec,
						 AsymKeyType		pKeyType) throws Exception {
		/* Store the key types */
		theKeyType		= pKeyType;
		
		/* Protect against exceptions */
		try {
			/* Obtain the public key */
			KeyFactory myFactory = KeyFactory.getInstance(pKeyType.getAlgorithm(),
				 										  SecurityControl.BCSIGN);	
			theKeyPair = new KeyPair(myFactory.generatePublic(pKeySpec), null);

			/* Access the encoded formats */
			thePublicKeyDef  = theKeyPair.getPublic().getEncoded();
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Unable to parse X509 KeySpec",
								e);
		}
	}
	
	/**
	 * Constructor from Security Signature
	 * @param pSignature the signature 
	 * @param pKeyType the key type 
	 * @param pRandom the secure random generator 
	 */
	protected  AsymmetricKey(PrivateKey			pPrivateKey,
							 X509EncodedKeySpec	pPublicKey,
							 AsymKeyType		pKeyType,
			 				 SecureRandom		pRandom) throws Exception {
		/* Store the password key, key type and the secure random */
		theKeyType	= pKeyType;
		theRandom	= pRandom;
		
		/* Protect against exceptions */
		try {
			/* Obtain the public key */
			KeyFactory myFactory = KeyFactory.getInstance(pKeyType.getAlgorithm(),
			 										  	  SecurityControl.BCSIGN);	
			theKeyPair = new KeyPair(myFactory.generatePublic(pPublicKey), pPrivateKey);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Unable to parse X509 KeySpec",
								e);
		}
		
		/* Access the encoded formats */
		thePrivateKeyDef = theKeyPair.getPrivate().getEncoded();
		thePublicKeyDef  = theKeyPair.getPublic().getEncoded();
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
		if (Utils.differs(myThat.thePrivateKeyDef, thePrivateKeyDef)) 	return false;
		if (Utils.differs(myThat.thePublicKeyDef, thePublicKeyDef)) 	return false;
		
		/* Identical if those tests succeed */
		return true;
	}

	/**
	 * Obtain the encoded public key 
	 * @return the encoded public key
	 */
	public X509EncodedKeySpec getPublicKey() throws Exception {
		/* Return X509 format */
		return new X509EncodedKeySpec(thePublicKeyDef); 
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
		SecretKey		myKey;
		KeyDef 			myKeyDef;
		Cipher			myCipher;
		
		/* Cannot unwrap unless we have the private key */
		if (isPublicOnly())
			throw new Exception(ExceptionClass.LOGIC,
								"Cannot unwrap without private key"); 
			
		/* Protect against exceptions */
		try {			
			/* If we are elliptic */
			if (theKeyType.isElliptic()) {
				/* Access the internal secret key definition */
				myKeyDef = getSharedKeyDef(this, pKeyType);

				/* Initialise the cipher */
				myCipher	= Cipher.getInstance(pKeyType.getAlgorithm(), 
												 SecurityControl.BCSIGN);
				myCipher.init(Cipher.UNWRAP_MODE, 
							  myKeyDef.getKey(), 
							  new IvParameterSpec(myKeyDef.getIv()));
			}
			
			/* else we use RAS semantics */
			else {
				/* Initialise the cipher */
				myCipher	= Cipher.getInstance(theKeyType.getAlgorithm(), 
												 SecurityControl.BCSIGN);
				myCipher.init(Cipher.UNWRAP_MODE, 
							  theKeyPair.getPrivate());		
			}
			
			/* unwrap the key */
			myKey = (SecretKey)myCipher.unwrap(pSecuredKeyDef, 
										   	   pKeyType.getAlgorithm(),
										   	   Cipher.SECRET_KEY);

			/* Build the symmetric key */
			mySymKey = new SymmetricKey(myKey, pKeyType, theRandom);
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
		KeyDef 		myKey;
		SymKeyType	myKeyType = pKey.getKeyType();
		
		/* Protect against exceptions */
		try {			
			/* If we are elliptic */
			if (theKeyType.isElliptic()) {
				/* Access the internal secret key definition */
				myKey = getSharedKeyDef(this, myKeyType);

				/* Initialise the cipher */
				myCipher	= Cipher.getInstance(myKeyType.getAlgorithm(), 
												 SecurityControl.BCSIGN);
				myCipher.init(Cipher.WRAP_MODE, 
							  myKey.getKey(), 
							  new IvParameterSpec(myKey.getIv()));
			}
			
			/* else we are using RSA semantics */
			else {
				/* Initialise the cipher */
				myCipher	= Cipher.getInstance(theKeyType.getAlgorithm(),
												 SecurityControl.BCSIGN);
				myCipher.init(Cipher.WRAP_MODE, 
							  theKeyPair.getPublic());
			}
			
			/* wrap the key */
			myWrappedKey = myCipher.wrap(pKey.getSecretKey());
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
	 * Obtain secret key for partner Asymmetric Key
	 * @param pPartner partner asymmetric key
	 * @param pKeyType the symmetric key type to generate
	 * @return the shared key definition
	 */
	protected synchronized KeyDef getSharedKeyDef(AsymmetricKey pPartner,
								  	 		      SymKeyType	pKeyType) throws Exception {
		SecretKey 	myKey = null;
		int			myKeyLen;
		KeyDef		myKeyDef;
		
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
			byte[] mySecret = theKeyAgreement.generateSecret();
			
			/* If we have not been told a KeyType */
			if (pKeyType == null) {
				/* Access the first byte of the secret */
				byte myType = mySecret[0];
				
				/* Normalise it according to the number of SymKeyTypes */
				myType %= SymKeyType.values().length;
				myType += 1;
				
				/* Access the KeyType */
				pKeyType = SymKeyType.fromId(myType);
			}
			
			/* Determine the key length in bytes */
			myKeyLen = pKeyType.getKeySize() / 8;
				
			/* If the secret is not long enough */
			if (mySecret.length < myKeyLen + pKeyType.getIvLen()) 
				throw new Exception(ExceptionClass.CRYPTO,
									"Shared secret is insufficient in length"); 
						
			/* Adjust the secret according to the KeyType to ensure different results */
			byte[] myNew = new byte[mySecret.length];
			int    myIndex = 3*pKeyType.getId();
			System.arraycopy(mySecret, myIndex, myNew, 0, mySecret.length-myIndex);
			System.arraycopy(mySecret, 0, myNew, mySecret.length-myIndex, myIndex);
			mySecret = myNew;
				
			/* Build the secret key specification */
			myKey = new SecretKeySpec(Arrays.copyOf(mySecret, myKeyLen), pKeyType.getAlgorithm());

			/* Build the definition */
			myKeyDef = new KeyDef(pKeyType, 
								  myKey, 
								  Arrays.copyOfRange(mySecret, myKeyLen, myKeyLen+pKeyType.getIvLen()));
		}
		
		/* Handle exceptions */
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to negotiate key agreement",
								e); 
		}
		
		/* Return the secret key */
		return myKeyDef;
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
				/* Access the internal secret key definition */
				KeyDef myKey = getSharedKeyDef(pTarget, null);

				/* Initialise the cipher */
				Cipher myCipher	= Cipher.getInstance(myKey.getType().getAlgorithm(), 
													 SecurityControl.BCSIGN);
				myCipher.init(Cipher.ENCRYPT_MODE, 
							  myKey.getKey(), 
							  new IvParameterSpec(myKey.getIv()));
			
				/* Create a Security Cipher and encrypt the string */
				DataCipher mySecCipher = new DataCipher(myCipher);
				return mySecCipher.encryptString(pString);
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
				/* Access the internal secret key definition */
				KeyDef myKey = getSharedKeyDef(pSource, null);

				/* Initialise the cipher */
				Cipher myCipher	= Cipher.getInstance(myKey.getType().getAlgorithm(), 
													 SecurityControl.BCSIGN);
				myCipher.init(Cipher.DECRYPT_MODE, 
							  myKey.getKey(), 
							  new IvParameterSpec(myKey.getIv()));
			
				/* Create a Security Cipher and encrypt the string */
				DataCipher mySecCipher = new DataCipher(myCipher);
				return mySecCipher.decryptString(pBytes);
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
		RSA(1, 2048),
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
