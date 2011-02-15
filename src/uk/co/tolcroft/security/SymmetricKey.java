package uk.co.tolcroft.security;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class SymmetricKey {
	/**
	 * Key size for symmetric algorithm
	 */
	protected final static int		KEYSIZE   		= 256;
	
	/**
	 * Encrypted ID Key Size
	 */
	public 	  final static int		IDSIZE   		= 1000;
	
	/**
	 * Initialisation Vector size 
	 */
	public    final static int		IVSIZE   		= 16;
	
	/**
	 * Symmetric algorithm
	 */
	protected final static String 	ALGORITHM 		= "AES";
	
	/**
	 * Symmetric full algorithm
	 */
	private final static String 	FULLALGORITHM	= "AES/CBC/PKCS5PADDING";
	
	/**
	 * The Symmetric Key 
	 */
	private SecretKey		theKey			= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom	theRandom		= null;

	/**
	 * The Security Control 
	 */
	private SecurityControl	theControl		= null;

	/**
	 * The Security Key 
	 */
	private byte[]			theSecurityKey	= null;

	/**
	 * Obtain the secret key
	 * @return the secret key
	 */
	protected SecretKey getSecretKey() { return theKey; }

	/**
	 * Constructor
	 * @param pControl the security control 
	 * @param pKey Secret Key for algorithm
	 * @param pRandom Secure Random byte generator
	 */
	protected SymmetricKey(SecurityControl	pControl,
			 			   SecretKey		pKey,
						   SecureRandom		pRandom) {
		/* Store the key and the Secure Random instance */
		theControl	= pControl;
		theKey	  	= pKey;
		theRandom 	= pRandom;			
	}
	
	/**
	 * Constructor
	 * @param pControl the security control 
	 * @param pKey Secret Key for algorithm
	 * @param pWrappedKey Wrapped Key
	 * @param pRandom Secure Random byte generator
	 */
	protected SymmetricKey(SecurityControl	pControl,
			 			   SecretKey		pKey,
			 			   byte[]			pWrappedKey,
						   SecureRandom		pRandom) {
		/* Call the standard constructor */
		this(pControl, pKey, pRandom);
		
		/* Store the wrapped key */
		theSecurityKey	= pWrappedKey;			
	}
	
	/**
	 * Compare this symmetric key to another for equality 
	 * @param pThat the key to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		byte[] myKey;
		byte[] myThatKey;
		
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Symmetric Key */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Key */
		SymmetricKey myThat = (SymmetricKey)pThat;
	
		/* Protect against exceptions */
		try {
			/* Access the two security keys */
			myKey 		= getSecurityKey();
			myThatKey 	= myThat.getSecurityKey();
		}
		
		/* Handle failure */
		catch (Exception e) { return false; }
		
		/* Compare the two */
		return Arrays.equals(myKey, myThatKey);
	}
	
	/**
	 * Obtain the SecurityKey
	 * @return the Security Key 
	 */
	public byte[] 	getSecurityKey() throws Exception {
		/* If we do not know the security key */
		if (theSecurityKey == null) {
			/* Calculate it */
			theSecurityKey = theControl.getWrappedKey(this);
			
			/* Keep a look out for the key being too large */
			if (theSecurityKey.length > IDSIZE)
				throw new Exception(ExceptionClass.ENCRYPT,
									"Security Key length too large: " + theSecurityKey.length);
		}
		
		/* Return it */
		return theSecurityKey; 
	}
	
	/**
	 * Transfer the symmetric key to a new controller
	 * @param pControl the new control
	 */
	public void	setSecurityControl(SecurityControl pControl) {
		/* Reset variables */
		theControl = pControl;
		theSecurityKey = null;
	}
	
	/**
	 * Initialise cipher for encryption with initialisation vector
	 * @param pInitVector Initialisation vector for cipher
	 * @return the Security Cipher
	 */
	public SecurityCipher initEncryption(byte[] pInitVector) throws Exception {
		AlgorithmParameterSpec 	myParms;
		Cipher					myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(FULLALGORITHM);
			
			/* Initialise the cipher using the password */
			myParms = new IvParameterSpec(pInitVector);
			myCipher.init(Cipher.ENCRYPT_MODE, theKey, myParms);
			
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
	 * Initialise cipher for encryption with random initialisation vector
	 * @return the Security Cipher
	 */
	public SecurityCipher initEncryption() throws Exception {
		Cipher	myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(FULLALGORITHM);
			
			/* Initialise the cipher generating a random Initialisation vector */
			myCipher.init(Cipher.ENCRYPT_MODE, theKey, theRandom);
			
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
	 * Initialise cipher for decryption with initialisation vector
	 * @param Initialisation vector for cipher
	 */
	public SecurityCipher initDecryption(byte[] pInitVector) throws Exception {
		AlgorithmParameterSpec 	myParms;
		Cipher					myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(FULLALGORITHM);
			
			/* Initialise the cipher using the password */
			myParms = new IvParameterSpec(pInitVector);
			myCipher.init(Cipher.DECRYPT_MODE, theKey, myParms);
			
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
}
