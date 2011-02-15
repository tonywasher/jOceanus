package uk.co.tolcroft.security;

import java.security.KeyPair;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class AsymmetricKey {
	/**
	 * Public/Private key algorithm
	 */
	protected final static String 	ALGORITHM 		= "RSA";
	
	/**
	 * Key size for asymmetric algorithm
	 */
	protected final static int    	KEYSIZE   		= 2048;
	
	/**
	 * Encrypted ID Key Size
	 */
	public 	  final static int		IDSIZE   		= 8000;
	
	/**
	 * Signature algorithm
	 */
	private final static String 	SIGNATURE		= "SHA256withRSA";
	
	/**
	 * The Public/Private Key Pair
	 */
	private KeyPair					theKeyPair		= null;
	
	/**
	 * The password key 
	 */
	private PasswordKey				thePassKey		= null;

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
	 * @param pKeyPair the key pair 
	 * @param pPassKey the password key 
	 */
	protected  AsymmetricKey(KeyPair 		pKeyPair,
			 				 PasswordKey	pPassKey) {
		/* Store the wrapped key */
		theKeyPair 	= pKeyPair;
		thePassKey	= pPassKey;
	}
	
	/**
	 * Constructor
	 * @param pKeyPair the key pair 
	 * @param pSecurityKey the security key 
	 * @param pPassKey the password key 
	 */
	protected  AsymmetricKey(KeyPair 		pKeyPair,
							 String			pSecurityKey,
			 				 PasswordKey	pPassKey) {
		/* Call the standard constructor */
		this(pKeyPair, pPassKey);
		
		/* Store the security key */
		theSecurityKey = pSecurityKey;
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
				throw new Exception(ExceptionClass.ENCRYPT,
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
	 * Obtain symmetric key from wrapped key
	 * @param pWrappedKey the wrapped key
	 * @param pPassKey the password key to use to unlock the wrap
	 * @return the Symmetric key
	 */
	protected SecretKey	unwrapSecretKey(byte[] 		pWrappedKey) throws Exception {
		SecretKey 		myKey;
		byte[]			myWrappedKey;
		Cipher			myCipher;
		
		/* Protect against exceptions */
		try {			
			/* Initialise the cipher */
			myCipher	= Cipher.getInstance(ALGORITHM);
			myCipher.init(Cipher.UNWRAP_MODE, theKeyPair.getPrivate());
		
			/* Reverse the obscuring of the array */
			myWrappedKey = thePassKey.obscureArray(pWrappedKey);
			
			/* wrap the key */
			myKey = (SecretKey)myCipher.unwrap(myWrappedKey, SymmetricKey.ALGORITHM, Cipher.SECRET_KEY);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to unwrap key",
								e);
		}
		
		/* Return the new key */
		return myKey;
	}
	
	/**
	 * Wrap secret key
	 * @param pKey the Key to wrap  
	 * @param pPassKey the password key to use to lock the wrap
	 * @return the wrapped secret key
	 */
	protected byte[] wrapSecretKey(SecretKey 	pKey) throws Exception {
		byte[] 				myWrappedKey;
		Cipher				myCipher;
		
		/* Protect against exceptions */
		try {			
			/* Initialise the cipher */
			myCipher	= Cipher.getInstance(ALGORITHM);
			myCipher.init(Cipher.WRAP_MODE, theKeyPair.getPublic());
		
			/* wrap the key */
			myWrappedKey = myCipher.wrap(pKey);
			
			/* Obscure the array */
			myWrappedKey = thePassKey.obscureArray(myWrappedKey);			
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to wrap key",
								e);
		}
		
		/* Return to caller */
		return myWrappedKey;
	}	

	/**
	 * Initialise cipher for encryption
	 */
	public SecurityCipher initEncryption() throws Exception {
		Cipher	myCipher;
		
		/* Protect against exceptions */
		try {
			/* Initialise the cipher for encryption using the public key */
			myCipher	= Cipher.getInstance(ALGORITHM);
			myCipher.init(Cipher.ENCRYPT_MODE, theKeyPair.getPublic());

			/* Return the Security Cipher */
			return new SecurityCipher(myCipher);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Initialise cipher for decryption
	 */
	public SecurityCipher initDecryption() throws Exception {
		Cipher	myCipher;

		/* Protect against exceptions */
		try {
			/* Cannot decrypt unless we have the private key */
			if (theKeyPair.getPrivate() == null)
				throw new Exception(ExceptionClass.LOGIC,
									"Cannot decrypt without private key"); 
				
			/* Initialise the cipher */
			myCipher	= Cipher.getInstance(ALGORITHM);
			myCipher.init(Cipher.DECRYPT_MODE, theKeyPair.getPrivate());

			/* Return the Security Cipher */
			return new SecurityCipher(myCipher);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Obtain the signature for the file entry
	 * @param pEntry the ZipFile properties
	 * @return the signature 
	 */
	protected byte[] signFile(ZipFileEntry pEntry) throws Exception {
		byte[]			 	myKeyEnc;
		byte[]			 	myEncDigest;
		byte[]			 	myCompDigest;
		byte[]			 	myRawDigest;
		byte[]				myInitVector;
		byte[]			 	mySignEnc;	
		Signature		 	mySignature;
		
		/* Protect against exceptions */
		try { 
			/* Cannot sign unless we have the private key */
			if (theKeyPair.getPrivate() == null)
				throw new Exception(ExceptionClass.LOGIC,
									"Cannot sign without private key"); 
				
			/* Access the parameters for the entry */
			myEncDigest 	= pEntry.getEncryptedDigest();
			myCompDigest 	= pEntry.getCompressedDigest();
			myRawDigest 	= pEntry.getRawDigest();
			myInitVector 	= pEntry.getInitVector();
			myKeyEnc 		= pEntry.getSecretKey();

			/* Create a signature */
			mySignature = Signature.getInstance(SIGNATURE);
			
			/* Sign the sender key and digest using the private key */
			mySignature.initSign(theKeyPair.getPrivate());
			if (myKeyEnc 	 != null) mySignature.update(myKeyEnc);
			if (myInitVector != null) mySignature.update(myInitVector);
			if (myEncDigest  != null) mySignature.update(myEncDigest);
			if (myCompDigest != null) mySignature.update(myCompDigest);
			if (myRawDigest  != null) mySignature.update(myRawDigest);
			
			/* Complete the signature */
			mySignEnc = mySignature.sign();
		} 
	
		/* Catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Exception calculating signature",
								e);
		}
		
		/* Return the signature */
		return mySignEnc;
	}		
	
	/**
	 * Verify the signature for the zipFileEntry
	 * @param pEntry the ZipFile properties
	 */
	public void verifyFile(ZipFileEntry pEntry) throws Exception {
		byte[]			 		myKeyEnc;
		byte[]			 		myEncDigest;
		byte[]			 		myCompDigest;
		byte[]			 		myRawDigest;
		byte[]					myInitVector;
		byte[]			 		mySignEnc;	
		Signature		 		mySignature;
		
		/* Protect against exceptions */
		try { 
			/* Access the parameters for the entry */
			myEncDigest 	= pEntry.getEncryptedDigest();
			myCompDigest 	= pEntry.getCompressedDigest();
			myRawDigest 	= pEntry.getRawDigest();
			myInitVector 	= pEntry.getInitVector();
			myKeyEnc 		= pEntry.getSecretKey();
			mySignEnc 		= pEntry.getSignature();

			/* Create a signature */
			mySignature = Signature.getInstance(SIGNATURE);
			
			/* Verify the signature */
			mySignature.initVerify(theKeyPair.getPublic());
			if (myKeyEnc 	 != null) mySignature.update(myKeyEnc);
			if (myInitVector != null) mySignature.update(myInitVector);
			if (myEncDigest  != null) mySignature.update(myEncDigest);
			if (myCompDigest != null) mySignature.update(myCompDigest);
			if (myRawDigest  != null) mySignature.update(myRawDigest);

			/* Check the signature */
			if (!mySignature.verify(mySignEnc)) {
				/* Throw an invalid file exception */
				throw new Exception(ExceptionClass.ENCRYPT, 
									"Signature does not match");
			}
		} 
	
		/* Catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Exception occurred verifying signature",
								e);
		}
	}				
}
