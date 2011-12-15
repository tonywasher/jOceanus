package uk.co.tolcroft.models.security;

import java.util.Arrays;

import java.security.*;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class PasswordHash {
	/**
	 * Salt length for passwords 
	 */
	private static final int 	SALTLENGTH	 		= 16;
	
	/**
	 * Hash size for password hash 
	 */
	public static final int 	HASHSIZE	 		= 100;
	
	/**
	 * Key Mode 
	 */
	private SecurityMode		theSecureMode		= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom		theRandom			= null;
	
	/**
	 * Password salt and hash 
	 */
	private byte[] 				theSaltAndHash 		= null;
	
	/**
	 * Secret hash 
	 */
	private byte[] 				theSecretHash		= null;

	/**
	 * Encrypted password 
	 */
	private byte[] 				thePassword			= null;

	/**
	 * CipherSet 
	 */
	private CipherSet			theCipherSet		= null;

	/**
	 * Obtain the PasswordHash
	 * @return the PasswordHash 
	 */
	public 		byte[] 			getPasswordHash() 	{ return theSaltAndHash; }
	
	/**
	 * Obtain the SecurityMode
	 * @return the SecurityMode 
	 */
	protected 	SecurityMode	getSecurityMode()	{ return theSecureMode; }
	
	/**
	 * Constructor for a completely new password hash 
	 * @param pPassword the password (cleared after usage)
	 * @param useRestricted use restricted keys
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordHash(char[] 		pPassword,
						   boolean		useRestricted,
						   SecureRandom	pRandom) throws WrongPasswordException,
						  								Exception {
		/* Create a random SecurityMode */
		theSecureMode 	= SecurityMode.getSecurityMode(useRestricted, pRandom);
		
		/* Store the secure random generator */
		theRandom	= pRandom;
		
		/* Validate the password */
		setPassword(pPassword);
	}
	
	/**
	 * Constructor for a password hash whose hash is known
	 * @param pSaltAndHash the Salt And Hash array 
	 * @param pPassword the password (cleared after usage)
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordHash(byte[]		pSaltAndHash,
						   char[] 		pPassword,
						   SecureRandom	pRandom) throws WrongPasswordException,
						  								Exception {
		/* Store the salt and hash and extract the mode */
		theSaltAndHash 	= pSaltAndHash;
		extractMode();
		
		/* Store the secure random generator */
		theRandom		= pRandom;
		
		/* Validate the password */
		setPassword(pPassword);
	}
	
	/**
	 * Constructor for alternate password hash sharing same password
	 * @param pSource the source hash
	 * @param useRestricted use restricted keys
	 */
	protected PasswordHash(PasswordHash pSource,
			   			   boolean		useRestricted) throws Exception {
		char[] 		myPassword = null;
		
		/* Build the encryption cipher */
		CipherSet mySet = pSource.theCipherSet;
		
		/* Access the original password */
		myPassword = mySet.decryptChars(pSource.thePassword);
		
		/* Protect against exceptions */
		try {
			/* Store the secure random generator */
			theRandom = pSource.theRandom;
			
			/* Create a random SecurityMode */
			theSecureMode = SecurityMode.getSecurityMode(useRestricted, theRandom);
			
			/* Validate the password */
			setPassword(myPassword);
		}
		
		/* Catch Exceptions */
		catch (Exception e) { throw e; }
		finally { if (myPassword != null) Arrays.fill(myPassword, (char)0); }
	}
	
	/**
	 * HashCode for the Password Hash
	 * @return the hash value
	 */
	public int hashCode() {
		/* Calculate and return the hashCode for this password key */
		int hashCode = theSaltAndHash.hashCode();
		return hashCode;
	}
	
	/**
	 * Compare this password key to another for equality 
	 * @param pThat the key to compare to
	 * @return <code>true/false</code> 
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Password Hash */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Hash */
		PasswordHash myThat = (PasswordHash)pThat;
	
		/* Compare the two */
		return Utils.differs(theSaltAndHash, myThat.theSaltAndHash).isIdentical();
	}
	
	/**
	 * Extract the mode from the salt and hash array
	 */
	private void extractMode() throws Exception {
		/* Extract the byte representation */
		byte[] myBytes = Arrays.copyOf(theSaltAndHash, SecurityMode.MODELENGTH);
		
		/* Access the integer value from these bytes */
		int myValue = Utils.IntegerFromBytes(myBytes);
		
		/* Convert to SecurityMode */
		theSecureMode = new SecurityMode(myValue); 
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
				mySalt = Arrays.copyOfRange(theSaltAndHash, 
											SecurityMode.MODELENGTH, 
											SecurityMode.MODELENGTH+SALTLENGTH);
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
			
			/* Create the Cipher Set */
			theCipherSet = new CipherSet(theRandom, 
										 theSecureMode);
			theCipherSet.buildCiphers(theSecretHash);
			
			/* Encrypt the password */
			thePassword = theCipherSet.encryptChars(pPassword);
			
			/* Clear out the password */
			Arrays.fill(pPassword, (char) 0);
		}
		catch (WrongPasswordException e) { throw e; }
		catch (Exception e) { throw e; }
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
		byte[] 			myPrimeHash;
		byte[] 			myAlternateHash;
		byte[] 			mySecretHash;
		byte[]			myCounter = new byte[4];
		byte[]			mySeed = { 'T', 'o', 'L', 'C', 'r', '0', 'F', 't' };
		MessageDigest 	myPrimeDigest;
		MessageDigest 	myAlternateDigest;
		MessageDigest 	mySecretDigest;
		int				iSwitch = theSecureMode.getSwitchIterate();
		int				iFinal 	= theSecureMode.getFinalIterate();
		
		/* Protect against exceptions */
		try {
			/* Create the digests */
			myPrimeDigest 		= MessageDigest.getInstance(theSecureMode.getPrimeDigest().getAlgorithm()); 
			myAlternateDigest  	= MessageDigest.getInstance(theSecureMode.getAltDigest().getAlgorithm()); 
			mySecretDigest  	= MessageDigest.getInstance(theSecureMode.getSecretDigest().getAlgorithm()); 
				
			/* Initialise the hash values as the UTF-8 version of the password */
			myPrimeHash 		= Utils.charToByteArray(pPassword);
			myAlternateHash		= Arrays.copyOf(myPrimeHash, myPrimeHash.length);
			mySecretHash		= Arrays.copyOf(myPrimeHash, myPrimeHash.length);
			
			/* Loop through the iterations */
			for (int i=0; i < iFinal; i++) {
				/* Note the final pass */
				int		iPass		= i+1;
				boolean bFinalPass 	= (iPass == iFinal);
				
				/* Build the counter */
				myCounter[3] = (byte)(iPass       & 0xFF); 
				myCounter[2] = (byte)((iPass>>8)  & 0xFF); 
				myCounter[1] = (byte)((iPass>>16) & 0xFF); 
				myCounter[0] = (byte)((iPass>>24) & 0xFF); 

				/* Update the prime digest */
				myPrimeDigest.update(myPrimeHash);
				myPrimeDigest.update(pSalt);
				myPrimeDigest.update(myCounter);
				myPrimeDigest.update(mySeed);
				
				/* Recalculate the prime hash skipping every third time */
				if ((bFinalPass) || ((iPass % 3) != 0))
					myPrimeHash = myPrimeDigest.digest();
				
				/* Update the alternate digest */
				myAlternateDigest.update(myAlternateHash);
				myAlternateDigest.update(pSalt);
				myAlternateDigest.update(myCounter);
				myAlternateDigest.update(mySeed);
				
				/* Recalculate the alternate hash skipping every fifth time */
				if ((bFinalPass) || ((iPass % 5) != 0))
					myAlternateHash = myAlternateDigest.digest();
				
				/* Update the secret digest */
				mySecretDigest.update(mySecretHash);
				mySecretDigest.update(pSalt);
				mySecretDigest.update(myCounter);
				mySecretDigest.update(mySeed);
				
				/* Every seventh pass (apart from last pass) */
				if ((!bFinalPass) && ((iPass % 7) == 0)) {
					/* Add in the Prime and Alternate hashes */
					mySecretDigest.update(myPrimeHash);
					mySecretDigest.update(myAlternateHash);
				}
				
				/* If we have hit the switch point */
				if (iPass == iSwitch) {
					/* Save the alternate hash value */
					byte[] myAlt = myAlternateHash;
					
					/* Switch the hashes */
					myAlternateHash	= myPrimeHash;
					myPrimeHash		= myAlt;
				}
			}
			
			/* Combine the Primary and Alternate hashes */
			byte[] myExternalHash 	= combineHashes(myPrimeHash,	myAlternateHash);
			
			/* Store the Secret Hash */
			theSecretHash			= mySecretDigest.digest();
			
			/* Combine the salt and hash */
			mySaltAndHash = new byte[pSalt.length+SecurityMode.MODELENGTH+myExternalHash.length];
			System.arraycopy(theSecureMode.getByteMode(), 0, mySaltAndHash, 0, SecurityMode.MODELENGTH);
			System.arraycopy(pSalt, 0, mySaltAndHash, SecurityMode.MODELENGTH, pSalt.length);
			System.arraycopy(myExternalHash, 0, mySaltAndHash, pSalt.length+SecurityMode.MODELENGTH, myExternalHash.length);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.CRYPTO,
								"Failed to generate salt and hash",
								e);
		}
		
		/* Check whether the SaltAndHash is too large */
		if (mySaltAndHash.length > HASHSIZE)
			throw new Exception(ExceptionClass.DATA,
								"Password Hash too large: " + mySaltAndHash.length);
			
		/* Return to caller */
		return mySaltAndHash;
	}
	
	/**
	 * Get the secured private key definition from an Asymmetric Key
	 * @param pKey the AsymmetricKey whose private key is to be secured
	 * @return the secured key
	 */
	public byte[] getSecuredPrivateKey(AsymmetricKey pKey) throws Exception {
		/* Return the Secured Key */
		byte[] myKeyDef = theCipherSet.wrapKey(pKey);
		
		/* Check whether the SecuredKey is too large */
		if (myKeyDef.length > AsymmetricKey.PRIVATESIZE)
			throw new Exception(ExceptionClass.DATA,
								"PrivateKey too large: " + myKeyDef.length);			

		/* Return to caller */
		return myKeyDef;
	}
	
	/**
	 * Generate an AsymmetricKey from its definition
	 * @param pSecuredPrivateKeyDef the Secured Private Key definition  
	 * @param pPublicKey the Public Key  
	 * @param pKeyMode	the key mode
	 */
	public AsymmetricKey getAsymmetricKey(byte[] 		pSecuredPrivateKeyDef,
										  byte[]		pPublicKey,
										  SecurityMode	pKeyMode) throws Exception {
		/* unWrap the Asymmetric Key */
		return theCipherSet.unWrapKey(pSecuredPrivateKeyDef, pPublicKey, pKeyMode);
	}
	
	/**
	 * Simple function to combine hashes. Hashes are simply XORed together.
	 * @param pFirst the first Hash
	 * @param pSecond the second Hash
	 * @return the combined hash
	 */
	private byte[] combineHashes(byte[] pFirst, byte[] pSecond) throws Exception {
		byte[] 			myTarget	= pSecond;
		byte[]			mySource	= pFirst;
		int	   			myLen;
		int	   			i;
		
		/* If the target is smaller than the source */
		if (myTarget.length < mySource.length) {
			/* Reverse the order to make use of all bits */
			myTarget = pFirst;
			mySource = pSecond;
		}
		
		/* Allocate the target as a copy of the input */
		myTarget = Arrays.copyOf(myTarget, myTarget.length);
		
		/* Determine length of operation */
		myLen = mySource.length;
		
		/* Loop through the array bytes */
		for (i=0; i<myTarget.length; i++) {
			/* Combine the bytes */
			myTarget[i] ^= mySource[i % myLen];
		}
					
		/* return the array */
		return myTarget;
	}	

	/**
	 * Attempt the cached password against the passed control
	 * @param pControl the security control to test against
	 * @return the cloned key
	 */
	protected void attemptPassword(SecurityControl pControl) {
		char[] 		myPassword = null;

		/* Protect against exceptions */
		try {
			/* Access the original password */
			myPassword = theCipherSet.decryptChars(thePassword);
			
			/* Try to initialise the control */
			pControl.initControl(myPassword);
		}
		
		/* Catch Exceptions */
		catch (Exception e) { }
		finally { if (myPassword != null) Arrays.fill(myPassword, (char)0); }		
	}
}
