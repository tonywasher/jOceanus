/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.security;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.Utils;

public class SymmetricKey {
	/**
	 * Encrypted ID Key Size
	 */
	public 	  final static int		IDSIZE   		= 128;
	
	/**
	 * Initialisation Vector size 
	 */
	public    final static int		IVSIZE   		= 16;
	
	/**
	 * Restricted key length 
	 */
	private   final static int		smallKEYLEN		= 128;
	
	/**
	 * Unlimited key length
	 */
	private   final static int		bigKEYLEN  		= 256;
	
	/**
	 * The Secret Key 
	 */
	private SecretKey		theKey				= null;
	
	/**
	 * The Key Type 
	 */
	private SymKeyType		theKeyType			= null;
	
	/**
	 * The Key Length
	 */
	private int				theKeyLen			= bigKEYLEN;
	
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
	public SymKeyType 	getKeyType() 	{ return theKeyType; }

	/**
	 * Obtain the key length
	 * @return the secret key length
	 */
	public int 			getKeyLength() 	{ return theKeyLen; }

	/**
	 * Determine key length
	 * @return key length
	 */
	protected static int	getKeyLen(boolean useRestricted) { 
		return useRestricted ? smallKEYLEN : bigKEYLEN; } 
	
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
	 * @param pKeyType Symmetric KeyType
	 * @param useRestricted use restricted keys
	 * @param pRandom Secure Random byte generator
	 */
	public SymmetricKey(SymKeyType		pKeyType,
						boolean			useRestricted,
						SecureRandom	pRandom) throws ModelException {
		/* Store the KeyType and the Secure Random instance */
		theKeyType		= pKeyType;
		theKeyLen		= getKeyLen(useRestricted);
		theRandom 		= pRandom;
		
		/* Generate the new key */
		theKey				= SymKeyGenerator.getInstance(theKeyType, 
														  theKeyLen,
														  theRandom);
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
						   SecureRandom		pRandom) throws ModelException {
		/* Store the Control, KeyType and the Secure Random instance */
		theKeyType			= pKeyType;
		theKeyLen			= pKey.getEncoded().length;
		theRandom 			= pRandom;
		theKey				= pKey;
		theEncodedKeyDef	= theKey.getEncoded();
	}
	
	@Override
	public int hashCode() {
		/* Calculate and return the hashCode for this symmetric key */
		int hashCode = 19 * theEncodedKeyDef.hashCode();
		hashCode += theKeyType.getId();
		return hashCode;
	}
	
	@Override
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
		return Utils.differs(myThat.theEncodedKeyDef, theEncodedKeyDef).isIdentical();
	}
	
	/**
	 * Initialise data cipher for encryption/decryption
	 * @return the Data Cipher
	 */
	public DataCipher initDataCipher() throws ModelException {
		Cipher	myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
										  SecurityControl.getProvider().getProvider());
			
			/* Return the Data Cipher */
			return new DataCipher(myCipher, this);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Initialise stream cipher for encryption with random initialisation vector
	 * @return the Stream Cipher
	 */
	public StreamCipher initEncryptionStream() throws ModelException {
		Cipher	myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
					  					  SecurityControl.getProvider().getProvider());
			
			/* Initialise the cipher generating a random Initialisation vector */
			myCipher.init(Cipher.ENCRYPT_MODE, theKey, theRandom);
			
			/* Return the Stream Cipher */
			return new StreamCipher(myCipher, myCipher.getIV());
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to initialise cipher",
								e);
		}
	}
	
	/**
	 * Initialise Stream cipher for decryption with initialisation vector
	 * @param pInitVector Initialisation vector for cipher
	 * @return the Stream Cipher
	 */
	public StreamCipher initDecryptionStream(byte[] pInitVector) throws ModelException {
		AlgorithmParameterSpec 	myParms;
		Cipher					myCipher;

		/* Protect against exceptions */
		try {
			/* Create a new cipher */
			myCipher = Cipher.getInstance(theKeyType.getCipher(), 
					  					  SecurityControl.getProvider().getProvider());
			
			/* Initialise the cipher using the password */
			myParms = new IvParameterSpec(pInitVector);
			myCipher.init(Cipher.DECRYPT_MODE, theKey, myParms);
			
			/* Return the Stream Cipher */
			return new StreamCipher(myCipher, pInitVector);
		}
		
		/* catch exceptions */
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.CRYPTO,
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
		private final SymKeyType 		theKeyType;
		private final KeyGenerator		theGenerator;
		private final SymKeyGenerator	theNext;
		private	final int				theKeyLen;
		
		/**
		 * Constructor
		 * @param pKeyType the symmetric key type
		 * @param pKeyLen the keyLength
		 * @param pRandom the SecureRandom instance
		 */
		private SymKeyGenerator(SymKeyType 		pKeyType,
								int				pKeyLen,
								SecureRandom	pRandom) throws ModelException {
			/* Protect against Exceptions */
			try {
				/* Create the key generator */
				theKeyType 		= pKeyType;
				theKeyLen		= pKeyLen;
				theGenerator 	= KeyGenerator.getInstance(pKeyType.getAlgorithm(), 
						  								   SecurityControl.getProvider().getProvider());
				theGenerator.init(pKeyLen, pRandom);
				
				/* Add to the list of generators */
				theNext			= theGenerators;
				theGenerators	= this;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				/* Throw the exception */
				throw new ModelException(ExceptionClass.CRYPTO,
									"Failed to create key generator",
									e);
			}
		}

		/**
		 * Generate a new key of the specified type
		 * @param pKeyType the symmetric key type
		 * @param pKeyLen the keyLength
		 * @param pRandom the SecureRandom instance
		 * @return the new key
		 */
		private static SecretKey getInstance(SymKeyType 	pKeyType,
											 int			pKeyLen,
											 SecureRandom	pRandom) throws ModelException {
			SymKeyGenerator myCurr;
			SecretKey		myKey;
			
			/* Locate the key generator */
			for (myCurr  = theGenerators; 
				 myCurr != null; 
				 myCurr  = myCurr.theNext) {
				/* If we have found the type break the loop */
				if ((myCurr.theKeyType	== pKeyType) &&
					(myCurr.theKeyLen	== pKeyLen))
						break;
			}
			
			/* If we have not found the generator */
			if (myCurr == null) {
				/* Create a new generator */
				myCurr = new SymKeyGenerator(pKeyType, 
											 pKeyLen, 
											 pRandom);
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
		AES(1),
		TwoFish(2),
		Serpent(3),
		CAMELLIA(4),
		RC6(5),
		CAST6(6);
		
		/**
		 * Symmetric full algorithm
		 */
		private final static String 	FULLALGORITHM	= "/CBC/PKCS5PADDING";
		
		/**
		 * Key values 
		 */
		private final int theId;
		
		/* Access methods */
		public int 		getId() 		{ return theId; }
		public String 	getAlgorithm() 	{ return toString(); }
		public String 	getCipher() 	{ return getAlgorithm() + FULLALGORITHM; }
		
		/**
		 * Constructor
		 */
		private SymKeyType(int id) {
			theId 		= id;
		}
		
		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static SymKeyType fromId(int id) throws ModelException {
			for (SymKeyType myType: values()) {	if (myType.getId() == id) return myType; }
			throw new ModelException(ExceptionClass.DATA,
								"Invalid SymKeyType: " + id);
		}
		
		/**
		 * Get random unique set of key types
		 * @param pNumTypes the number of types
		 * @param pRandom the random generator
		 * @return the random set
		 */
		public static SymKeyType[] getRandomTypes(int pNumTypes, SecureRandom pRandom) throws ModelException {
			/* Access the values */
			SymKeyType[] myValues 	= values();
			int			 iNumValues = myValues.length;
			int			 iIndex;
			
			/* Reject call if invalid number of types */
			if ((pNumTypes < 1) || (pNumTypes > iNumValues))
				throw new ModelException(ExceptionClass.LOGIC,
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
