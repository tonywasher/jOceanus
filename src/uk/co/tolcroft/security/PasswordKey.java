package uk.co.tolcroft.security;

import java.util.Arrays;

import javax.crypto.*;

import java.security.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class PasswordKey {
	/**
	 * Password Based Encryption algorithm
	 */
	private final static String ALGORITHM 		= "PBEWithSHA1AndDESede";
	
	/**
	 * Iteration count for passwords 
	 */
	private static final int 	ITERATIONS 		= 2027;
	
	/**
	 * Iteration count for hash 
	 */
	private static final int 	PARTITERATIONS 	= 1019;
	
	/**
	 * Salt length for passwords 
	 */
	private static final int 	SALTLENGTH	 	= 16;
	
	/**
	 * Secret key for wrapping 
	 */
	private SecretKey 			thePassKey		= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom		theRandom		= null;
	
	/**
	 * Password salt and hash 
	 */
	private byte[] 				theSaltAndHash 	= null;
	
	/**
	 * Partial hash 
	 */
	private byte[] 				thePartialHash 	= null;
	
	/**
	 * KeyPair 
	 */
	private KeyPair				theKeyPair 		= null;
	
	/**
	 * Obtain the SecurityKey
	 * @return the Security Key 
	 */
	public byte[] 		getSecurityKey() 	{ return theSaltAndHash; }
	
	/**
	 * Obtain the KeyPair
	 * @return the KeyPair 
	 */
	protected KeyPair 	getKeyPair() 		{ return theKeyPair; }
	
	/**
	 * Constructor for a completely new password key 
	 * @param pPassword the password (cleared after usage)
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordKey(char[] 			pPassword,
						  SecureRandom		pRandom) throws WrongPasswordException,
						  									Exception {
		/* Store the salt and hash */
		theRandom	= pRandom;
		
		/* Validate the password */
		setPassword(pPassword);
	}
	
	/**
	 * Constructor for a password key whose hash is known
	 * @param pSaltAndHash the Salt And Hash array 
	 * @param pPassword the password (cleared after usage)
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordKey(byte[]			pSaltAndHash,
			  			  char[] 			pPassword,
						  SecureRandom		pRandom) throws WrongPasswordException,
						  									Exception {
		/* Store the salt and hash */
		theSaltAndHash 	= pSaltAndHash;
		theRandom		= pRandom;
		
		/* Validate the password */
		setPassword(pPassword);
	}
	
	/**
	 * Constructor for a password key associated with an asymmetric key
	 * @param pSecurityKey the wrapped security key 
	 * @param pPassword the password (cleared after usage)
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordKey(String		pSecurityKey,
						  char[] 		pPassword,
						  SecureRandom	pRandom) throws WrongPasswordException,
						  								Exception {
		/* Store the Random byte generator */
		theRandom	= pRandom;
		
		/* Locate the first KeySeparator in the string */
		int myLoc = pSecurityKey.indexOf(SecurityControl.KEYSEP);
		
		/* If string is invalid */
		if (myLoc == -1) {
			/* Throw and exception */
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid Security Key");
		}
		
		/* Access the Password salt and hash */
		theSaltAndHash 	= Utils.BytesFromHexString(pSecurityKey.substring(0, myLoc));
		
		/* Validate the password */
		setPassword(pPassword);
		
		/* Unwrap the keyPair */
		unwrapKeyPair(pSecurityKey.substring(myLoc+1));
	}
	
	/**
	 * Compare this password key to another for equality 
	 * @param pThat the key to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		byte[] myKey;
		byte[] myThatKey;
		
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Password Key */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Key */
		PasswordKey myThat = (PasswordKey)pThat;
	
		/* Access the two security keys */
		myKey 		= getSecurityKey();
		myThatKey 	= myThat.getSecurityKey();
		
		/* Compare the two */
		return Arrays.equals(myKey, myThatKey);
	}
	
	/**
	 * Access the KeyPair from a security string
	 * @param pSecurityKey the wrapped security key 
	 */
	protected KeyPair getKeyPair(String		pSecurityKey) throws Exception {
		byte[] mySaltAndHash;
		
		/* Locate the first KeySeparator in the string */
		int myLoc = pSecurityKey.indexOf(SecurityControl.KEYSEP);
		
		/* If string is invalid */
		if (myLoc == -1) {
			/* Throw and exception */
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid Security Key");
		}
		
		/* Access the Password salt and hash */
		mySaltAndHash 	= Utils.BytesFromHexString(pSecurityKey.substring(0, myLoc));
		
		/* Check that the arrays match */
		if (!Arrays.equals(theSaltAndHash, mySaltAndHash)) {
			/* Fail the password attempt */
			throw new Exception(ExceptionClass.LOGIC, 
								"Invalid Password");
		}
		
		/* Unwrap the keyPair */
		unwrapKeyPair(pSecurityKey.substring(myLoc+1));
		
		/* Return the KeyPair */
		return theKeyPair;
	}
	
	/**
	 * Seed the password key with the password
	 * @param pPassword the password (cleared after usage)
	 */
	public void setNewPassword(char[] pPassword) throws WrongPasswordException,
														Exception {
		/* Clear the salt and hash */
		theSaltAndHash = null;
		
		/* Reset the password */
		setPassword(pPassword);
	}
	
	/**
	 * Seed the password key with the password
	 * @param pPassword the password (cleared after usage)
	 */
	private void setPassword(char[] pPassword) throws WrongPasswordException,
													  Exception {
		PBEKeySpec 			myKeySpec;
		SecretKeyFactory 	myKeyFactory;
		byte[]				mySalt;
		byte[]				mySaltAndHash;
		
		/* Protect against exceptions */
		try {
			/* If we already have a salt */
			if (theSaltAndHash != null) {
				/* Pick out the salt from the array */
				mySalt = Arrays.copyOf(theSaltAndHash, SALTLENGTH);
			}
			
			/* Else this is the initialisation phase */
			else {
				/* Generate a new salt */
				mySalt = new byte[SALTLENGTH];
				theRandom.nextBytes(mySalt);
			}
			
			/* Generate the saltAndHash */
			mySaltAndHash = generateSaltAndHash(mySalt, pPassword);
				
			/* If we already have a salt */
			if (theSaltAndHash != null) {
				/* Check that the arrays match */
				if (!Arrays.equals(theSaltAndHash, mySaltAndHash)) {
					/* Fail the password attempt */
					throw new WrongPasswordException("Invalid Password");
				}
			}
				
			/* Else this is the initialisation phase */
			else {
				/* Record the Salt and Hash */
				theSaltAndHash = mySaltAndHash;
			}
			
			/* Generate the key and cipher */
			myKeySpec 		= new PBEKeySpec(pPassword, mySalt, ITERATIONS);
			myKeyFactory 	= SecretKeyFactory.getInstance(ALGORITHM);
			thePassKey 		= myKeyFactory.generateSecret(myKeySpec);
			
			/* Clear out the password */
			Arrays.fill(pPassword, (char) 0);
			myKeySpec.clearPassword();
		}
		catch (WrongPasswordException e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to initialise using password",
								e);
		}
		
		/* Return to caller */
		return;
	}
	
	/**
	 * Generate Salt And Hash array
	 * @param pSalt the salt for the password 
	 * @param pPassword the password for the keys
	 * @return the Salt and Hash array 
	 */
	private byte[] generateSaltAndHash(byte[] pSalt, char[] pPassword) throws Exception {
		byte[] 			mySaltAndHash;
		byte[] 			myHash;
		byte[]			mySeed = { 'T', 'o', 'L', 'C', 'r', 'o', 'F', 't' };
		MessageDigest 	myDigest;
		
		/* Protect against exceptions */
		try {
			/* Create a new digest */
			myDigest = MessageDigest.getInstance(SecurityControl.DIGEST); 
				
			/* Initialise the hash value as the UTF-8 version of the password */
			myHash = Utils.charToByteArray(pPassword);
		
			/* Initialise the digest with the salt and fixed seed */
			myDigest.update(pSalt);
			myDigest.update(mySeed);
			
			/* Loop through the iterations */
			for (int i=0; i < ITERATIONS; i++) {
				/* If we have hit the partial iteration count store the partial hash */
				if (i == PARTITERATIONS) 
					thePartialHash = Arrays.copyOf(myHash, myHash.length); 
				
				/* Update the digest and calculate it */
				myDigest.update(myHash);
				myHash = myDigest.digest();
				
				/* Reset the digest skipping every third time */
				if (((i+1) % 3) != 0) myDigest.reset();
			}
			
			/* Combine the salt and hash */
			mySaltAndHash = new byte[pSalt.length+ myHash.length];
			System.arraycopy(pSalt, 0, mySaltAndHash, 0, pSalt.length);
			System.arraycopy(myHash, 0, mySaltAndHash, pSalt.length, myHash.length);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to generate salt and hash",
								e);
		}
		
		/* Return to caller */
		return mySaltAndHash;
	}
	
	/**
	 * Wrap KeyPair
	 * @param pKeyPair the KeyPair to wrap
	 * @param bPublicOnly only wrap the public key  
	 * @return the wrapped secret key with random salt
	 */
	protected String wrapKeyPair(KeyPair pKeyPair,
								 boolean bPublicOnly) throws Exception {
		byte[] 				mySalt;
		byte[] 				myKeyEnc;
		byte[] 				mySaltAndKey;
		PBEParameterSpec 	mySpec;
		StringBuilder		mySecurityKey;
		Cipher				myCipher;
		
		/* Protect against exceptions */
		try {
			/* Create the StringBuilder */
			mySecurityKey = new StringBuilder(1000);
			
			/* Create the hex version of the bytes */
			mySecurityKey.append(Utils.HexStringFromBytes(theSaltAndHash));
			mySecurityKey.append(SecurityControl.KEYSEP);
			
			/* Create the new salt */
			mySalt = new byte[SALTLENGTH];

			/* Create a cipher */
			myCipher	= Cipher.getInstance(ALGORITHM);
			
			/* If we have a private key and are not wrapping public only */
			if ((!bPublicOnly) && (pKeyPair.getPrivate() != null)) {
				/* Seed the new salt */
				theRandom.nextBytes(mySalt);
			
				/* Initialise the cipher */
				mySpec 		= new PBEParameterSpec(mySalt, ITERATIONS);
				myCipher.init(Cipher.WRAP_MODE, thePassKey, mySpec);
		
				/* wrap the private key */
				myKeyEnc = myCipher.wrap(pKeyPair.getPrivate());

				/* Combine the salt and hash */
				mySaltAndKey = new byte[mySalt.length+ myKeyEnc.length];
				System.arraycopy(mySalt, 0, mySaltAndKey, 0, mySalt.length);
				System.arraycopy(myKeyEnc, 0, mySaltAndKey, mySalt.length, myKeyEnc.length);

				/* Obscure the array */
				mySaltAndKey = obscureArray(mySaltAndKey);

				/* Add to the key */
				mySecurityKey.append(Utils.HexStringFromBytes(mySaltAndKey));
			}
			
			/* Add the Key Separator */
			mySecurityKey.append(SecurityControl.KEYSEP);

			/* Seed a new salt */
			theRandom.nextBytes(mySalt);
			
			/* Initialise the cipher */
			mySpec 		= new PBEParameterSpec(mySalt, ITERATIONS);
			myCipher.init(Cipher.WRAP_MODE, thePassKey, mySpec);
		
			/* wrap the private key */
			myKeyEnc = myCipher.wrap(pKeyPair.getPublic());

			/* Combine the salt and hash */
			mySaltAndKey = new byte[mySalt.length+ myKeyEnc.length];
			System.arraycopy(mySalt, 0, mySaltAndKey, 0, mySalt.length);
			System.arraycopy(myKeyEnc, 0, mySaltAndKey, mySalt.length, myKeyEnc.length);

			/* Obscure the array */
			mySaltAndKey = obscureArray(mySaltAndKey);

			/* Add to the key */
			mySecurityKey.append(Utils.HexStringFromBytes(mySaltAndKey));
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to wrap key",
								e);
		}
		
		/* Return to caller */
		return mySecurityKey.toString();
	}
	
	/**
	 * Unwrap KeyPair
	 * @param pWrappedKeyPair the Wrapped KeyPair  
	 */
	private void unwrapKeyPair(String pWrappedKeyPair) throws Exception {
		byte[] 				mySaltAndKey;
		byte[] 				mySalt;
		byte[] 				myKeyEnc;
		PBEParameterSpec 	mySpec;
		PrivateKey			myPrivateKey = null;
		PublicKey			myPublicKey;
		Cipher				myCipher;
		
		/* Protect against exceptions */
		try {
			/* Locate the KeySeparator in the string */
			int myLoc = pWrappedKeyPair.indexOf(SecurityControl.KEYSEP);
			
			/* If string is invalid */
			if (myLoc == -1) {
				/* Throw and exception */
				throw new Exception(ExceptionClass.LOGIC,
									"Invalid Security Key");
			}
			
			/* Create a cipher */
			myCipher	= Cipher.getInstance(ALGORITHM);
			
			/* If we have a private key */
			if (myLoc > 0) {
				/* Access the Private Key salt and key */
				mySaltAndKey	= Utils.BytesFromHexString(pWrappedKeyPair.substring(0, myLoc));
			
				/* Reverse the obscuring of the array */
				mySaltAndKey = obscureArray(mySaltAndKey);
			
				/* Pick out the salt and key from the array */
				mySalt 		= Arrays.copyOf(mySaltAndKey, SALTLENGTH);
				myKeyEnc	= Arrays.copyOfRange(mySaltAndKey, SALTLENGTH, mySaltAndKey.length);
			
				/* Initialise the cipher */
				mySpec 		= new PBEParameterSpec(mySalt, ITERATIONS);
				myCipher.init(Cipher.UNWRAP_MODE, thePassKey, mySpec);
		
				/* unwrap the private key */
				myPrivateKey = (PrivateKey)myCipher.unwrap(myKeyEnc, AsymmetricKey.ALGORITHM, Cipher.PRIVATE_KEY);
			}
			
			/* Access the Public Key salt and key */
			mySaltAndKey	= Utils.BytesFromHexString(pWrappedKeyPair.substring(myLoc+1));
			
			/* Reverse the obscuring of the array */
			mySaltAndKey = obscureArray(mySaltAndKey);
			
			/* Pick out the salt and key from the array */
			mySalt 		= Arrays.copyOf(mySaltAndKey, SALTLENGTH);
			myKeyEnc	= Arrays.copyOfRange(mySaltAndKey, SALTLENGTH, mySaltAndKey.length);
			
			/* Initialise the cipher */
			mySpec 		= new PBEParameterSpec(mySalt, ITERATIONS);
			myCipher.init(Cipher.UNWRAP_MODE, thePassKey, mySpec);
		
			/* unwrap the private key */
			myPublicKey = (PublicKey)myCipher.unwrap(myKeyEnc, AsymmetricKey.ALGORITHM, Cipher.PUBLIC_KEY);
			
			/* Create the Key Pair */
			theKeyPair = new KeyPair(myPublicKey, myPrivateKey);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to unwrap key pair",
								e);
		}
		
		/* Return to caller */
		return;
	}
	
	/**
	 * Simple function to obscure a byte array.
	 * Since it uses XOR functionality, repeating the call reverses the previous call
	 * @param pArray the array to obscure/clear
	 * @return the obscured or clear array
	 * @throws Exception 
	 */
	protected byte[] obscureArray(byte[] pArray) throws Exception {
		byte[] 			myArray;
		int	   			myLen;
		int	   			i;
		
		/* If password has not been set */
		if (thePassKey == null) {
			/* Throw and exception */
			throw new Exception(ExceptionClass.LOGIC,
								"Password not set");
		}
		
		/* Allocate the new array as a copy of the input */
		myArray = Arrays.copyOf(pArray, pArray.length);
		
		/* Determine length of operation */
		myLen = thePartialHash.length;
		
		/* Loop through the array bytes */
		for (i=0; i<pArray.length; i++) {
			/* Obscure the byte */
			myArray[i] ^= thePartialHash[i % myLen];
		}
					
		/* return the array */
		return myArray;
	}	

	/**
	 * Initialise cipher for encryption with initialisation vector
	 * @param pInitVector Initialisation vector for cipher
	 */
	public SecurityCipher initEncryption(byte[] pInitVector) throws Exception {
		PBEParameterSpec 	mySpec;
		Cipher				myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(ALGORITHM);
			
			/* Initialise the cipher using the password */
			mySpec 		= new PBEParameterSpec(pInitVector, ITERATIONS);
			myCipher.init(Cipher.ENCRYPT_MODE, thePassKey, mySpec);
			
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
	 */
	public SecurityCipher initEncryption() throws Exception {
		PBEParameterSpec 	mySpec;
		Cipher				myCipher;
		byte[]				myInitVector;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(ALGORITHM);
			
			/* Create the new salt */
			myInitVector = new byte[SALTLENGTH];
			theRandom.nextBytes(myInitVector);
			
			/* Initialise the cipher generating a random Initialisation vector */
			mySpec 		= new PBEParameterSpec(myInitVector, ITERATIONS);
			myCipher.init(Cipher.ENCRYPT_MODE, thePassKey, mySpec);
			
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
		PBEParameterSpec 	mySpec;
		Cipher				myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(ALGORITHM);
			
			/* Initialise the cipher using the password */
			mySpec 		= new PBEParameterSpec(pInitVector, ITERATIONS);
			myCipher.init(Cipher.DECRYPT_MODE, thePassKey, mySpec);

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
