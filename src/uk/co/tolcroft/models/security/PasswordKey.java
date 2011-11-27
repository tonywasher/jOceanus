package uk.co.tolcroft.models.security;

import java.util.Arrays;

import javax.crypto.*;

import java.security.*;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.security.AsymmetricKey.AsymKeyType;

public class PasswordKey {
	/**
	 * Password Based Encryption algorithms
	 */
	private final static String algoAES 			= "PBEWITHSHA256AND128BITAES-CBC-BC";
	private final static String algoTwofish			= "PBEWithSHAAndTwofish-CBC";
	private final static String algo3KDES			= "PBEWithSHAAnd3-KeyTripleDES-CBC";
	private final static String algoRC2				= "PBEWithSHAAnd128BitRC2-CBC";
	
	/**
	 * Mode length for passwords 
	 */
	private static final int 	MODELENGTH	 		= 4;
	
	/**
	 * Salt length for passwords 
	 */
	private static final int 	SALTLENGTH	 		= 16;
	
	/**
	 * Hash size for password keys 
	 */
	public static final int 	HASHSIZE	 		= 100;
	
	/**
	 * Key Mode 
	 */
	private PBEKeyMode			theKeyMode			= null;
	
	/**
	 * The secure random generator
	 */
	private SecureRandom		theRandom			= null;
	
	/**
	 * Password salt and hash 
	 */
	private byte[] 				theSaltAndHash 		= null;
	
	/**
	 * Key hash 
	 */
	private byte[] 				theKeyHash			= null;

	/**
	 * SecretKey 
	 */
	private SecretKey			thePassKey			= null;

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
	 * Obtain the SecurityKey
	 * @return the Security Key 
	 */
	public 		byte[] 			getPasswordHash() 	{ return theSaltAndHash; }
	
	/**
	 * Obtain the KeyMode
	 * @return the KeyMode 
	 */
	protected 	PBEKeyMode		getKeyMode()		{ return theKeyMode; }
	
	/**
	 * Constructor for a completely new password key 
	 * @param pPassword the password (cleared after usage)
	 * @param useRestricted use restricted keys
	 * @param pRandom Secure Random byte generator
	 */
	protected PasswordKey(char[] 			pPassword,
						  boolean			useRestricted,
						  SecureRandom		pRandom) throws WrongPasswordException,
						  									Exception {
		/* Create a random ControlMode */
		theKeyMode 	= PBEKeyMode.getMode(useRestricted, pRandom);
		
		/* Store the secure random generator */
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
		/* Store the salt and hash and extract the mode */
		theSaltAndHash 	= pSaltAndHash;
		extractMode();
		
		/* Store the secure random generator */
		theRandom		= pRandom;
		
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
		CipherSet mySet = pSource.theCipherSet;
		
		/* Access the original password */
		myPassword = mySet.decryptChars(pSource.thePassword);
		
		/* Protect against exceptions */
		try {
			/* Store the secure random generator */
			theRandom = pSource.theRandom;
			boolean useRestricted = pSource.getKeyMode().useRestricted();
			
			/* Create a random ControlMode */
			theKeyMode = PBEKeyMode.getMode(useRestricted, theRandom);
			
			/* Validate the password */
			setPassword(myPassword);
		}
		
		/* Catch Exceptions */
		catch (Exception e) { throw e; }
		finally { if (myPassword != null) Arrays.fill(myPassword, (char)0); }
	}
	
	/**
	 * Hash for the Password Key
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
		
		/* Make sure that the object is a Password Key */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the target Key */
		PasswordKey myThat = (PasswordKey)pThat;
	
		/* Compare the two */
		return Utils.differs(theSaltAndHash, myThat.theSaltAndHash).isIdentical();
	}
	
	/**
	 * Extract the mode from the salt and hash array
	 */
	private void extractMode() throws Exception {
		/* Extract the byte representation */
		byte[] myBytes = Arrays.copyOf(theSaltAndHash, MODELENGTH);
		
		/* Access the integer value from these bytes */
		int myValue = Utils.IntegerFromBytes(myBytes);
		
		/* Convert to PBEKeyMode */
		theKeyMode = new PBEKeyMode(myValue); 
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
				mySalt = Arrays.copyOfRange(theSaltAndHash, MODELENGTH, MODELENGTH+SALTLENGTH);
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
			
			/* Access the Key Type */
			PBEKeyType myType = theKeyMode.getPBEKeyType();

			/* Generate the Key */
			thePassKey	= PBEKeyFactory.getInstance(myType,
													theKeyMode.getSecondIterate(), 
													theKeyHash, 
													pPassword);
			
			/* Create a new cipher */
			Cipher myCipher = Cipher.getInstance(myType.getAlgorithm(), 
					  					  		 SecurityControl.BCSIGN);
			
			/* Initialise the cipher using the KeyHash */
			PBEParameterSpec mySpec	= new PBEParameterSpec(theKeyHash, theKeyMode.getThirdIterate());
			myCipher.init(Cipher.ENCRYPT_MODE, thePassKey, mySpec);

			/* Encrypt the Secret Hash */
			theSecretHash = myCipher.doFinal(theSecretHash);
			
			/* Create the Cipher Set */
			theCipherSet = new CipherSet(theRandom, 
										 theKeyMode.useRestricted(), 
										 CipherSet.DEFSTEPS);
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
		byte[] 			myMainHash;
		byte[] 			mySecondHash;
		byte[] 			myThirdHash;
		byte[]			mySeed = { 'T', 'o', 'L', 'C', 'r', '0', 'F', 't' };
		MessageDigest 	myMainDigest;
		MessageDigest 	mySecondDigest;
		MessageDigest 	myThirdDigest;
		int				iFirst  = theKeyMode.getFirstIterate();
		int				iSecond = theKeyMode.getSecondIterate();
		int				iThird  = theKeyMode.getThirdIterate();
		
		/* Protect against exceptions */
		try {
			/* Create the digests */
			myMainDigest 	= MessageDigest.getInstance(theKeyMode.getFirstDigest().getAlgorithm()); 
			mySecondDigest  = MessageDigest.getInstance(theKeyMode.getSecondDigest().getAlgorithm()); 
			myThirdDigest  	= MessageDigest.getInstance(theKeyMode.getThirdDigest().getAlgorithm()); 
				
			/* Initialise the hash value as the UTF-8 version of the password */
			myMainHash 		= Utils.charToByteArray(pPassword);
			mySecondHash	= Arrays.copyOf(myMainHash, myMainHash.length);
			myThirdHash		= Arrays.copyOf(myMainHash, myMainHash.length);
			
			/* Initialise the digests with the salt and fixed seed */
			myMainDigest.update(pSalt);
			myMainDigest.update(mySeed);
			mySecondDigest.update(pSalt);
			mySecondDigest.update(mySeed);
			myThirdDigest.update(pSalt);
			myThirdDigest.update(mySeed);
			
			/* Loop through the iterations */
			for (int i=0; i < iThird; i++) {
				/* Note the final pass */
				int		iPass		= i+1;
				byte	iCounter	= (byte)(i % 256);
				boolean bFinalPass 	= (iPass == iThird);
				
				/* Update the main digest and calculate it */
				myMainDigest.update(myMainHash);
				myMainDigest.update(pSalt);
				myMainDigest.update(mySeed);
				myMainDigest.update(iCounter);
				
				/* Recalculate the hash skipping every third time */
				if ((bFinalPass) || ((iPass % 3) != 0))
					myMainHash = myMainDigest.digest();
				
				/* Update the second digest and calculate it */
				mySecondDigest.update(mySecondHash);
				mySecondDigest.update(pSalt);
				mySecondDigest.update(mySeed);
				mySecondDigest.update(iCounter);
				
				/* Recalculate the hash skipping every fifth time */
				if ((bFinalPass) || ((iPass % 5) != 0))
					mySecondHash = mySecondDigest.digest();
				
				/* Update the third digest and calculate it */
				myThirdDigest.update(myThirdHash);
				myThirdDigest.update(pSalt);
				myThirdDigest.update(mySeed);
				myThirdDigest.update(iCounter);
				
				/* Recalculate the hash skipping every seventh time */
				if ((bFinalPass) || ((iPass % 7) != 0))
					myThirdHash = myThirdDigest.digest();
				
				/* If we have hit a switch iteration */
				if ((iPass == iFirst) || (iPass == iSecond)) {
					/* Save the hash values */
					byte[] mySecond = mySecondHash;
					byte[] myThird 	= myThirdHash;
					
					/* Cycle the hashes */
					mySecondHash	= myMainHash;
					myThirdHash		= mySecond;
					myMainHash		= myThird;
				}
			}
			
			/* Combine the hashes as required */
			byte[] myExternalHash 	= combineHashes(myMainHash, 	mySecondHash);
			theKeyHash				= combineHashes(myMainHash, 	myThirdHash);
			theSecretHash			= combineHashes(mySecondHash, 	myThirdHash);
			
			/* Combine the salt and hash */
			mySaltAndHash = new byte[pSalt.length+MODELENGTH+myExternalHash.length];
			System.arraycopy(theKeyMode.getByteMode(), 0, mySaltAndHash, 0, MODELENGTH);
			System.arraycopy(pSalt, 0, mySaltAndHash, MODELENGTH, pSalt.length);
			System.arraycopy(myExternalHash, 0, mySaltAndHash, pSalt.length+MODELENGTH, myExternalHash.length);
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
	 * @param pKeyType	the key type
	 */
	public AsymmetricKey getAsymmetricKey(byte[] 		pSecuredPrivateKeyDef,
										  byte[]		pPublicKey,
										  AsymKeyType	pKeyType) throws Exception {
		/* unWrap the Asymmetric Key */
		return theCipherSet.unWrapKey(pSecuredPrivateKeyDef, pPublicKey, pKeyType);
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
				theFactory 	= SecretKeyFactory.getInstance(pKeyType.getAlgorithm(),
														   SecurityControl.BCSIGN);
				
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
		TripleDES(3,algo3KDES),
		RC2(4,algoRC2);
		
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
