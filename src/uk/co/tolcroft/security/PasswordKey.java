package uk.co.tolcroft.security;

import java.util.Arrays;

import javax.crypto.*;

import java.security.*;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.AsymmetricKey.AsymKeyType;

public class PasswordKey {
	/**
	 * Password Based Encryption algorithms
	 */
	private final static String algoAES 			= "PBEWITHSHA256AND256BITAES-CBC-BC";
	private final static String algoTwofish			= "PBEWithSHAAndTwofish-CBC";
	private final static String algoDESede			= "PBEWithSHAAnd3-KeyTripleDES-CBC";
	
	/**
	 * Mode length for passwords 
	 */
	private static final int 	MODELENGTH	 		= 8;
	
	/**
	 * Salt length for passwords 
	 */
	private static final int 	SALTLENGTH	 		= 16;
	
	/**
	 * Secret key 
	 */
	private SecretKey 			thePassKey			= null;
	
	/**
	 * Key Type 
	 */
	private PBEKeyType			theKeyType			= null;
	
	/**
	 * Key Mode 
	 */
	private ControlMode			theKeyMode			= null;
	
	/**
	 * Algorithm 
	 */
	private String 				theAlgorithm		= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom		theRandom			= null;
	
	/**
	 * Password salt and hash 
	 */
	private byte[] 				theSaltAndHash 		= null;
	
	/**
	 * Partial hash 
	 */
	private byte[] 				thePartialHash 		= null;
	
	/**
	 * Alternate hash 
	 */
	private byte[] 				theAlternateHash	= null;

	/**
	 * Encrypted password 
	 */
	private byte[] 				thePassword			= null;

	/**
	 * Obtain the SecurityKey
	 * @return the Security Key 
	 */
	public 		byte[] 			getSecurityKey() 	{ return theSaltAndHash; }
	
	/**
	 * Obtain the KeyMode
	 * @return the KeyMode 
	 */
	protected 	ControlMode		getKeyMode()		{ return theKeyMode; }
	
	/**
	 * Constructor for a completely new password key 
	 * @param pPassword the password (cleared after usage)
	 * @param pKeyMode the key mode
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordKey(char[] 			pPassword,
						  ControlMode		pKeyMode,
						  SecureRandom		pRandom) throws WrongPasswordException,
						  									Exception {
		/* Store the key type and secure random generator */
		theKeyMode		= pKeyMode;
		theKeyType		= pKeyMode.getPBEKeyType();
		theRandom		= pRandom;
		theAlgorithm	= theKeyType.getAlgorithm();
		
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
		/* Store the salt and has and extract the mode */
		theSaltAndHash 	= pSaltAndHash;
		extractMode();
		
		/* Store the key and hash types and secure random generator */
		theKeyType		= theKeyMode.getPBEKeyType();
		theRandom		= pRandom;
		theAlgorithm	= theKeyType.getAlgorithm();
		
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
		/* Locate the first KeySeparator in the string */
		int myLoc = pSecurityKey.indexOf(SecurityControl.KEYSEP);
		
		/* If string is invalid */
		if (myLoc == -1) {
			/* Throw and exception */
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid Security Key");
		}
		
		/* Access the Password salt and hash and extract the mode */
		theSaltAndHash 	= Utils.BytesFromHexString(pSecurityKey.substring(0, myLoc));
		extractMode();
		
		/* Store the key type and secure random generator */
		theKeyType		= theKeyMode.getPBEKeyType();
		theRandom		= pRandom;
		theAlgorithm	= theKeyType.getAlgorithm();
		
		/* Validate the password */
		setPassword(pPassword);
	}
	
	/**
	 * Constructor for alternate password key sharing same password
	 * @param pSource the source key
	 */
	protected PasswordKey(PasswordKey pSource) throws Exception {
		char[] 		myPassword = null;
		
		/* Build the encryption cipher */
		SecurityCipher myCipher = pSource.initDecryption(pSource.theAlternateHash);
		
		/* Access the original password */
		myPassword = myCipher.decryptChars(pSource.thePassword);
		
		/* Protect against exceptions */
		try {
			/* Store the secure random generator */
			theRandom = pSource.theRandom;
			
			/* Create a random ControlMode */
			ControlMode myMode = ControlMode.getControlMode(theRandom);
			
			/* Store the key type and secure random generator */
			theKeyMode		= myMode;
			theKeyType		= myMode.getPBEKeyType();
			theAlgorithm	= theKeyType.getAlgorithm();
			
			/* Validate the password */
			setPassword(myPassword);
		}
		
		/* Catch Exceptions */
		catch (Exception e) { throw e; }
		finally { if (myPassword != null) Arrays.fill(myPassword, (char)0); }
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
	
		/* Not equal if different key-types */
		if (myThat.theKeyType != theKeyType) return false;
		
		/* Access the two security keys */
		myKey 		= getSecurityKey();
		myThatKey 	= myThat.getSecurityKey();
		
		/* Compare the two */
		return Arrays.equals(myKey, myThatKey);
	}
	
	/**
	 * Extract the mode from the salt and hash array
	 */
	private void extractMode() throws Exception {
		/* Extract the byte representation */
		byte[] myBytes = Arrays.copyOfRange(theSaltAndHash, SALTLENGTH, SALTLENGTH+MODELENGTH);
		
		/* Access the long value from these bytes */
		long myValue = Utils.LongFromBytes(myBytes);
		
		/* Convert to PBEKeyMode */
		theKeyMode = new ControlMode(myValue); 
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
			
			/* Generate the key */
			thePassKey 		= PBEKeyFactory.getInstance(theKeyType,
														theKeyMode.getSecondIterate(), 
														theAlternateHash, 
														pPassword);
			
			/* Encrypt the password */
			encryptPassword(pPassword);
			
			/* Clear out the password */
			Arrays.fill(pPassword, (char) 0);
		}
		catch (WrongPasswordException e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
		byte[] 			myMainHash;
		byte[] 			myAltHash;
		byte[]			mySeed = { 'T', 'o', 'L', 'C', 'r', '0', 'F', 't' };
		MessageDigest 	myMainDigest;
		MessageDigest 	myAltDigest;
		int				iFirst  = theKeyMode.getFirstIterate();
		int				iSecond = theKeyMode.getSecondIterate();
		int				iThird  = theKeyMode.getThirdIterate();
		
		/* Protect against exceptions */
		try {
			/* Create the two digests */
			myMainDigest = MessageDigest.getInstance(theKeyMode.getFirstDigest().getAlgorithm()); 
			myAltDigest  = MessageDigest.getInstance(theKeyMode.getSecondDigest().getAlgorithm()); 
				
			/* Initialise the hash value as the UTF-8 version of the password */
			myMainHash = Utils.charToByteArray(pPassword);
			myAltHash  = Arrays.copyOf(myMainHash, myMainHash.length);
			
			/* Initialise the digests with the salt and fixed seed */
			myMainDigest.update(pSalt);
			myMainDigest.update(mySeed);
			myAltDigest.update(pSalt);
			myAltDigest.update(mySeed);
			
			/* Loop through the iterations */
			for (int i=0; i < iThird; i++) {
				/* If we have hit the partial iteration point store the partial hash */
				if (i == iSecond) 
					thePartialHash = Arrays.copyOf(myAltHash, myAltHash.length); 
				
				/* Update the main digest and calculate it */
				myMainDigest.update(myMainHash);
				myMainDigest.update(pSalt);
				myMainDigest.update(mySeed);
				myMainDigest.update((byte)(i % 256));
				myMainHash = myMainDigest.digest();
				
				/* Reset the main digest skipping every third time */
				if (((i+1) % 3) != 0) myMainDigest.reset();
				
				/* Update the digest and calculate it */
				myAltDigest.update(myAltHash);
				myAltDigest.update(pSalt);
				myAltDigest.update(mySeed);
				myAltDigest.update((byte)(i % 256));
				myAltHash = myAltDigest.digest();
				
				/* Reset the alternate digest skipping every fifth time */
				if (((i+1) % 5) != 0) myAltDigest.reset();
				
				/* If we have hit the switch iteration */
				if (i == iFirst) {
					/* Switch the hash values */
					byte[] myTemp = myAltHash;
					myAltHash  = myMainHash;
					myMainHash = myTemp;
				}
			}
			
			/* Obscure the hash arrays */
			myMainHash = obscureArray(myMainHash);
			myAltHash  = obscureArray(myAltHash);
			
			/* Store the alternate hash */
			theAlternateHash = myAltHash;
			
			/* Combine the salt and hash */
			mySaltAndHash = new byte[pSalt.length+MODELENGTH+myMainHash.length];
			System.arraycopy(pSalt, 0, mySaltAndHash, 0, pSalt.length);
			System.arraycopy(theKeyMode.getByteMode(), 0, mySaltAndHash, pSalt.length, MODELENGTH);
			System.arraycopy(myMainHash, 0, mySaltAndHash, pSalt.length+MODELENGTH, myMainHash.length);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
			myCipher	= Cipher.getInstance(theAlgorithm);
			
			/* If we have a private key and are not wrapping public only */
			if ((!bPublicOnly) && (pKeyPair.getPrivate() != null)) {
				/* Seed the new salt */
				theRandom.nextBytes(mySalt);
			
				/* Initialise the cipher */
				mySpec 		= new PBEParameterSpec(mySalt, theKeyMode.getThirdIterate());
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
			mySpec 		= new PBEParameterSpec(mySalt, theKeyMode.getThirdIterate());
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
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to wrap key",
								e);
		}
		
		/* Return to caller */
		return mySecurityKey.toString();
	}
	
	/**
	 * Unwrap KeyPair
	 * @param pSecurityKey the Security Key  
	 * @param pKeyType	the key type
	 */
	protected KeyPair unwrapKeyPair(String 		pSecurityKey,
							   		AsymKeyType	pKeyType) throws Exception {
		byte[] 				mySaltAndKey;
		byte[] 				mySaltAndHash;
		byte[] 				mySalt;
		byte[] 				myKeyEnc;
		PBEParameterSpec 	mySpec;
		PrivateKey			myPrivateKey = null;
		PublicKey			myPublicKey;
		Cipher				myCipher;
		KeyPair				myKeyPair;
				
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
		
		/* Shift down the security key over the salt and hash */
		pSecurityKey = pSecurityKey.substring(myLoc+1);
		
		/* Locate the KeySeparator in the string */
		myLoc = pSecurityKey.indexOf(SecurityControl.KEYSEP);
		
		/* If string is invalid */
		if (myLoc == -1) {
			/* Throw an exception */
			throw new Exception(ExceptionClass.LOGIC,
								"Invalid Security Key");
		}
		
		/* Protect against exceptions */
		try {
			/* Create a cipher */
			myCipher	= Cipher.getInstance(theAlgorithm);
			
			/* If we have a private key */
			if (myLoc > 0) {
				/* Access the Private Key salt and key */
				mySaltAndKey	= Utils.BytesFromHexString(pSecurityKey.substring(0, myLoc));
			
				/* Reverse the obscuring of the array */
				mySaltAndKey = obscureArray(mySaltAndKey);
			
				/* Pick out the salt and key from the array */
				mySalt 		= Arrays.copyOf(mySaltAndKey, SALTLENGTH);
				myKeyEnc	= Arrays.copyOfRange(mySaltAndKey, SALTLENGTH, mySaltAndKey.length);
			
				/* Initialise the cipher */
				mySpec 		= new PBEParameterSpec(mySalt, theKeyMode.getThirdIterate());
				myCipher.init(Cipher.UNWRAP_MODE, thePassKey, mySpec);
		
				/* unwrap the private key */
				myPrivateKey = (PrivateKey)myCipher.unwrap(myKeyEnc, pKeyType.getAlgorithm(), Cipher.PRIVATE_KEY);
			}
			
			/* Access the Public Key salt and key */
			mySaltAndKey	= Utils.BytesFromHexString(pSecurityKey.substring(myLoc+1));
			
			/* Reverse the obscuring of the array */
			mySaltAndKey = obscureArray(mySaltAndKey);
			
			/* Pick out the salt and key from the array */
			mySalt 		= Arrays.copyOf(mySaltAndKey, SALTLENGTH);
			myKeyEnc	= Arrays.copyOfRange(mySaltAndKey, SALTLENGTH, mySaltAndKey.length);
			
			/* Initialise the cipher */
			mySpec 		= new PBEParameterSpec(mySalt, theKeyMode.getThirdIterate());
			myCipher.init(Cipher.UNWRAP_MODE, thePassKey, mySpec);
		
			/* unwrap the private key */
			myPublicKey = (PublicKey)myCipher.unwrap(myKeyEnc, pKeyType.getAlgorithm(), Cipher.PUBLIC_KEY);
			
			/* Create the Key Pair */
			myKeyPair = new KeyPair(myPublicKey, myPrivateKey);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to unwrap key pair",
								e);
		}
		
		/* Return to caller */
		return myKeyPair;
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
		if (thePartialHash == null) {
			/* Throw an exception */
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
			myCipher = Cipher.getInstance(theAlgorithm);
			
			/* Initialise the cipher using the password */
			mySpec 		= new PBEParameterSpec(pInitVector, theKeyMode.getThirdIterate());
			myCipher.init(Cipher.ENCRYPT_MODE, thePassKey, mySpec);
			
			/* Return the Security Cipher */
			return new SecurityCipher(myCipher, pInitVector);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
			myCipher = Cipher.getInstance(theAlgorithm);
			
			/* Create the new salt */
			myInitVector = new byte[SALTLENGTH];
			theRandom.nextBytes(myInitVector);
			
			/* Initialise the cipher generating a random Initialisation vector */
			mySpec 		= new PBEParameterSpec(myInitVector, theKeyMode.getThirdIterate());
			myCipher.init(Cipher.ENCRYPT_MODE, thePassKey, mySpec);
			
			/* Return the Security Cipher */
			return new SecurityCipher(myCipher, myInitVector);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
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
			myCipher = Cipher.getInstance(theAlgorithm);
			
			/* Initialise the cipher using the password */
			mySpec 		= new PBEParameterSpec(pInitVector, theKeyMode.getThirdIterate());
			myCipher.init(Cipher.DECRYPT_MODE, thePassKey, mySpec);

			/* Return the Security Cipher */
			return new SecurityCipher(myCipher, pInitVector);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Record the encrypted password 
	 */
	private void encryptPassword(char[] pPassword) throws Exception {
		/* Build the encryption cipher */
		SecurityCipher myCipher = initEncryption(theAlternateHash);
		
		/* Encrypt the password characters */
		thePassword = myCipher.encryptChars(pPassword);
	}
	
	/**
	 * Create alternate Password key for this password
	 * @param pRandom the random generator
	 * @return the cloned key
	 */
	protected void attemptPassword(SecurityControl pControl) {
		char[] 		myPassword = null;

		/* Protect against exceptions */
		try {
			/* Build the encryption cipher */
			SecurityCipher myCipher = initDecryption(theAlternateHash);
		
			/* Access the original password */
			myPassword = myCipher.decryptChars(thePassword);
			
			/* Try to initialise the control */
			pControl.initControl(myPassword);
		}
		
		/* Catch Exceptions */
		catch (Exception e) { }
		finally { if (myPassword != null) Arrays.fill(myPassword, (char)0); }		
	}
	
	/**
	 * Factory class
	 */
	private static class PBEKeyFactory {
		/**
		 * Symmetric key generator list
		 */
		private static PBEKeyFactory	theFactories	= null;
		
		/* Members */
		private PBEKeyType 			theKeyType	= null;
		private SecretKeyFactory	theFactory 	= null;
		private PBEKeyFactory		theNext		= null;
		
		/**
		 * Constructor
		 * @param pKeyType the password key type
		 */
		private PBEKeyFactory(PBEKeyType 	pKeyType) throws Exception {
			/* Protect against Exceptions */
			try {
				/* Create the key generator */
				theKeyType	= pKeyType;
				theFactory 	= SecretKeyFactory.getInstance(pKeyType.getAlgorithm());
				
				/* Add to the list of factories */
				theNext			= theFactories;
				theFactories	= this;
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
		 * @param pSalt the salt bytes
		 * @param pPassword the password
		 * @return the new key
		 */
		private static SecretKey getInstance(PBEKeyType 	pKeyType,
											 int			pIterations,
											 byte[]			pSalt,
											 char[]			pPassword) throws Exception {
			PBEKeyFactory 	myCurr;
			PBEKeySpec 		myKeySpec = null;
			SecretKey		myKey;
			
			/* Locate the key factory */
			for (myCurr  = theFactories; 
				 myCurr != null; 
				 myCurr  = myCurr.theNext) {
				/* If we have found the type break the loop */
				if (myCurr.theKeyType == pKeyType) break;
			}
			
			/* If we have not found the generator */
			if (myCurr == null) {
				/* Create a new generator */
				myCurr = new PBEKeyFactory(pKeyType);
			}
			
			/* protect against exceptions */
			try {
				/* Generate the Secret key */
				myKeySpec 	= new PBEKeySpec(pPassword, pSalt, pIterations);
				myKey 		= myCurr.theFactory.generateSecret(myKeySpec);
			}

			/* Catch Exceptions */
			catch (Throwable e) {
				/* Throw the exception */
				throw new Exception(ExceptionClass.CRYPTO,
									"Failed to create password key",
									e);				
			}
			
			/* Ensure that we clear out password */
			finally {
				/* Clear out the password */
				if (myKeySpec != null) myKeySpec.clearPassword();
			}
			
			/* Return the new key */
			return myKey;
		}
	}
	
	/**
	 * PBE key types
	 */
	public enum PBEKeyType {
		AES(1,algoAES),
		TwoFish(2,algoTwofish),
		DESede(3,algoDESede);
		
		/**
		 * Key values 
		 */
		private int 	theId 			= 0;
		private String	theAlgorithm 	= null;
		
		/* Access methods */
		public int 		getId() 		{ return theId; }
		public String	getAlgorithm() 	{ return theAlgorithm; }
		
		/**
		 * Constructor
		 */
		private PBEKeyType(int id, String pAlgorithm) {
			theId 			= id;
			theAlgorithm	= pAlgorithm;
		}
		
		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static PBEKeyType fromId(int id) throws Exception {
			for (PBEKeyType myType: values()) {	if (myType.getId() == id) return myType; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid PBEKeyType: " + id);
		}

		/**
		 * Get random unique set of key types
		 * @param pNumTypes the number of types
		 * @param pRandom the random generator
		 * @return the random set
		 */
		public static PBEKeyType[] getRandomTypes(int pNumTypes, SecureRandom pRandom) throws Exception {
			/* Access the values */
			PBEKeyType[] myValues 	= values();
			int			 iNumValues = myValues.length;
			int			 iIndex;
			
			/* Reject call if invalid number of types */
			if ((pNumTypes < 1) || (pNumTypes > iNumValues))
				throw new Exception(ExceptionClass.LOGIC,
									"Invalid number of types: " + pNumTypes);
			
			/* Create the result set */
			PBEKeyType[] myTypes  = new PBEKeyType[pNumTypes];
			
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
