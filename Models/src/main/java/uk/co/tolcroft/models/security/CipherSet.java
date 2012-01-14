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

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.PropertySet;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.PropertySet.PropertySetChooser;
import uk.co.tolcroft.models.security.SecurityControl.SecurityProperties;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class CipherSet  implements PropertySetChooser {
	/**
	 * Maximum number of encryption steps
	 */
	public final static int MAXSTEPS	= SymmetricKey.SymKeyType.values().length-1;

	/**
	 * Key Id byte allowance
	 */
	public final static int KEYIDLEN 	= numKeyBytes(MAXSTEPS);

	/**
	 * Default number of steps
	 */
	public final static int DEFSTEPS 	= 3;

	/**
	 * Multiplier to obtain IV from vector
	 */
	private final static int vectMULT 	= 7; 

	/**
	 * The Number of Steps
	 */
	private int							theNumSteps	= DEFSTEPS;
	
	/**
	 * Security Mode
	 */
	protected SecurityMode				theMode		= null;
	
	/**
	 * The Random Generator
	 */
	private SecureRandom				theRandom		= null;
	
	/**
	 * The DataKey Map
	 */
	private Map<SymKeyType, DataCipher>	theMap			= null;

	/**
	 * Constructor
	 * @param pRandom the Secure Random
	 * @param pSecMode the Security Mode
	 */
	public CipherSet(SecureRandom	pRandom,
					 SecurityMode	pSecMode) {
		/* Store parameters */
		theRandom 	= pRandom;
		theMode		= pSecMode;
		
		/* Access the security properties and the number of steps */
		SecurityProperties myProperties = (SecurityProperties)PropertyManager.getPropertySet(this);
		theNumSteps = myProperties.getIntegerValue(SecurityProperties.nameCipherSteps);
		
		/* Build the Map */
		theMap = new EnumMap<SymKeyType, DataCipher>(SymKeyType.class);
	}
	
	@Override
	public Class<? extends PropertySet> getPropertySetClass() { return SecurityProperties.class; }
	
	/**
	 * Add a Cipher
	 * @param pCipher the Cipher
	 */
	public void addCipher(DataCipher	pCipher) {
		/* Store into map */
		theMap.put(pCipher.getSymKeyType(), pCipher);
	}
	
	/**
	 * Build Secret Ciphers
	 * @param pSecret the Secret bytes
	 */
	public void buildCiphers(byte[] pSecret) throws ModelException {
		/* Loop through the Cipher values */
		for (SymKeyType myType : SymKeyType.values()) {
			/* Build the Cipher */
			buildCipher(myType, pSecret);
		}
	}
	
	/**
	 * Build Secret Cipher for a Key Type
	 * @param pKeyType the Key type
	 * @param pSecret the Secret Key
	 */
	private void buildCipher(SymKeyType pKeyType,
							 byte[] 	pSecret) throws ModelException {
		/* Determine the key length in bytes */
		int myKeyLen = SymmetricKey.getKeyLen(theMode.useRestricted()) / 8;
			
		/* Create a buffer to hold the resulting key and # of bytes built */
		byte[] 	myKeyBytes 	= new byte[myKeyLen];
		int		myBuilt		= 0;
		
		/* Protect against exceptions */
		try {
			/* Create the MessageDigest and standard data */
			byte[]			myCount	 = new byte[4];
			Arrays.fill(myCount, (byte)0);
			byte[] 			myAlgo	 = pKeyType.getAlgorithm().getBytes(SecurityControl.ENCODING);
			MessageDigest 	myDigest = MessageDigest.getInstance(theMode.getCipherDigest().getAlgorithm(),
														   		 SecurityControl.getProvider().getProvider());
		
			/* while we need to generate more bytes */
			while (myBuilt < myKeyLen) {
				/* Increment count and add to hash */
				myCount[3]++;
				myDigest.update(myCount);
		
				/* Update with secret and algorithm */
				myDigest.update(pSecret);
				myDigest.update(myAlgo);
		
				/* Obtain the calculated hash */
				byte[] myHash = myDigest.digest();
			
				/* Determine how many bytes of this hash should be used */
				int myNeeded = myKeyLen - myBuilt;
				if (myNeeded > myHash.length) myNeeded = myHash.length;
			
				/* Copy bytes across */
				System.arraycopy(myHash, 0, myKeyBytes, myBuilt, myNeeded);
				myBuilt += myNeeded;
			}
		}
		
		/* Catch exceptions */
		catch (Throwable e) {
			/* Throw exception */
			throw new ModelException(ExceptionClass.CRYPTO,
								"Failed to Derive KeyDefinition",
								e);
		}
		
		/* Build the secret key specification */
		SecretKey myKeyDef = new SecretKeySpec(myKeyBytes,
											   pKeyType.getAlgorithm());

		/* Create the Symmetric Key */
		SymmetricKey myKey = new SymmetricKey(myKeyDef,
											  pKeyType,
											  theRandom);  
		
		/* Access a Cipher */
		DataCipher myCipher = myKey.initDataCipher();
		
		/* Store into map */
		theMap.put(pKeyType, myCipher);
	}
	
	/**
	 * Encrypt item 
	 * @param pBytes the bytes to encrypt
	 * @return the encrypted bytes
	 */
	public byte[] encryptBytes(byte[] pBytes) throws ModelException {
		/* Allocate a new initialisation vector */
		byte[] 			myVector = new byte[SymmetricKey.IVSIZE];
		theRandom.nextBytes(myVector);
		
		/* Determine the SymKeyTypes to use */
		SymKeyType[] myKeyTypes = SymKeyType.getRandomTypes(theNumSteps, theRandom);
		
		/* Encode the array */
		byte[] myKeyBytes = encodeSymKeyTypes(myKeyTypes);
		
		/* Loop through the SymKeyTypes */
		for (int i=0; i < myKeyTypes.length; i++) {
			/* Access the Key Type */
			SymKeyType myType = myKeyTypes[i];
			
			/* Access the DataCipher */
			DataCipher myCipher = theMap.get(myType);
			
			/* Access the shifted vector */
			byte[] myShift = getShiftedVector(myType, myVector);
			
			/* Encrypt the bytes */
			pBytes = myCipher.encryptBytes(pBytes, myShift);
		}
		
		/* Allocate the bytes */
		byte[] myEncrypt = new byte[SymmetricKey.IVSIZE + myKeyBytes.length + pBytes.length];
		System.arraycopy(myVector, 0, myEncrypt, 0, SymmetricKey.IVSIZE);
		System.arraycopy(myKeyBytes, 0, myEncrypt, SymmetricKey.IVSIZE, myKeyBytes.length);
		System.arraycopy(pBytes, 0, myEncrypt, SymmetricKey.IVSIZE+myKeyBytes.length,  pBytes.length);
		
		/* Return the encrypted bytes */
		return myEncrypt;
	}

	/**
	 * Determine length of bytes to encode the number of keys
	 * @param pNumKeys the number of keys
	 * @return the number of key bytes 
	 */
	private static int numKeyBytes(int pNumKeys) {
		/* Determine the number of bytes */
		return 1 + (pNumKeys/2);
	}
	
	/**
	 * Encode SymKeyTypes
	 * @param pTypes the types to encode
	 * @return the encoded types
	 */
	private byte[] encodeSymKeyTypes(SymKeyType[] pTypes) throws ModelException {
		/* Determine the number of bytes */
		int myNumBytes = numKeyBytes(pTypes.length);
		
		/* Allocate the bytes */
		byte[] myBytes = new byte[myNumBytes];
		Arrays.fill(myBytes, (byte)0);
		
		/* Loop through the keys */
		for (int i=0, j=0; i<pTypes.length; i++) {
			/* Access the id of the Symmetric Key */
			int myId = pTypes[i].getId();
			
			/* Access the id */
			if ((i % 2) == 1) { myId *= 16; j++; }
			myBytes[j] |= myId;
		}
		
		/* Encode the number of keys */
		myBytes[0] |= (16*pTypes.length);
		
		/* Return the bytes */
		return myBytes;
	}
	
	/**
	 * Decrypt item 
	 * @param pBytes the bytes to decrypt
	 * @return the decrypted bytes
	 */
	public byte[] decryptBytes(byte[] pBytes) throws ModelException {
		/* Split the bytes into the separate parts */
		byte[] 			myVector = Arrays.copyOf(pBytes, SymmetricKey.IVSIZE);
		SymKeyType[]	myTypes	 = decodeSymKeyTypes(pBytes);	
		byte[] 			myBytes  = Arrays.copyOfRange(pBytes, 
													  SymmetricKey.IVSIZE+numKeyBytes(myTypes.length), 
													  pBytes.length);
		
		/* Loop through the SymKeyTypes */
		for (int i=myTypes.length-1; i >= 0; i--) {
			/* Access the Key Type */
			SymKeyType myType = myTypes[i];
			
			/* Access the DataCipher */
			DataCipher myCipher = theMap.get(myType);
			
			/* Access the shifted vector */
			byte[] myShift = getShiftedVector(myType, myVector);
			
			/* Decrypt the bytes */
			myBytes = myCipher.decryptBytes(myBytes, myShift);
		}
		
		/* Return the decrypted bytes */
		return myBytes;
	}
	
	/**
	 * Obtain shifted Initialisation vector
	 * @param pKeyType the Symmetric Key Type
	 * @param pVector the initialisation vector
	 * @return the shifted vector
	 */
	private byte[] getShiftedVector(SymKeyType	pKeyType,
									byte[] 		pVector) {
		/* Determine index into array for Key Type */
		byte[] myNew = new byte[pVector.length];
		int    myIndex = vectMULT*pKeyType.getId();
		myIndex %= pVector.length;
	
		/* If we need to shift the array */
		if (myIndex > 0) {
			/* Access shifted array */
			System.arraycopy(pVector, myIndex, myNew, 0, pVector.length-myIndex);
			System.arraycopy(pVector, 0, myNew, pVector.length-myIndex, myIndex);
			pVector = myNew;
		}
		
		/* return the shifted vector */
		return pVector;
	}
	
	/**
	 * Decode SymKeyTypes
	 * @param pBytes the encrypted bytes
	 * @return the array of SymKeyTypes
	 */
	private SymKeyType[] decodeSymKeyTypes(byte[] pBytes) throws ModelException {
		/* Extract the number of SymKeys */
		int myNumKeys = pBytes[SymmetricKey.IVSIZE] / 16;
		
		/* Allocate the array */
		SymKeyType[] myTypes = new SymKeyType[myNumKeys];
		
		/* Loop through the keys */
		for (int i=0, j=SymmetricKey.IVSIZE; i<myNumKeys; i++) {
			/* Access the id of the Symmetric Key */
			int myId = pBytes[j];
			
			/* Isolate the id */
			if ((i % 2) == 1)  myId /= 16; else j++;
			myId &= 15;
			
			/* Determine the SymKeyType */
			myTypes[i] = SymKeyType.fromId(myId);
		}
		
		/* Return the array */
		return myTypes;
	}	

	/**
	 * Encrypt string 
	 * @param pString the string to encrypt
	 * @return the encrypted bytes
	 */
	public byte[] encryptString(String pString) throws ModelException {
		/* Protect against exceptions */
		try {
			/* Access the bytes */
			byte[] myBytes = pString.getBytes(SecurityControl.ENCODING);
			
			/* Encrypt the bytes */
			return encryptBytes(myBytes);
		}
		catch (ModelException e) { throw e; }
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.DATA,
								"Failed to extract bytes from String - " + pString,
								e);
		}
	}
	
	/**
	 * Decrypt string 
	 * @param pBytes the string to decrypt
	 * @return the decrypted string
	 */
	public String decryptString(byte[] pBytes) throws ModelException {
		/* Protect against exceptions */
		try {
			/* Decrypt the bytes */
			byte[] myBytes = decryptBytes(pBytes);
			
			/* ReBuild the string */
			return new String(myBytes, SecurityControl.ENCODING);
		}
		catch (ModelException e) { throw e; }
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.DATA,
								"Failed to build string from bytes",
								e);
		}
	}
	
	/**
	 * Encrypt character array
	 * @param pChars Characters to encrypt
	 * @return Encrypted bytes
	 */
	public byte[] encryptChars(char[] pChars) throws ModelException {
		byte[] myBytes;
		byte[] myRawBytes;
		
		/* Convert the characters to a byte array */
		myRawBytes = Utils.charToByteArray(pChars);
			
		/* Encrypt the characters */
		myBytes = encryptBytes(myRawBytes);
						
		/* Return to caller */
		return myBytes;
	}		
	
	/**
	 * Decrypt bytes into a character array
	 * @param pBytes Bytes to decrypt
	 * @return Decrypted character array
	 */
	public char[] decryptChars(byte[] pBytes) throws ModelException {
		byte[] 	myBytes;
		char[]	myChars;
		
		/* Decrypt the bytes */
		myBytes  = decryptBytes(pBytes);
			
		/* Convert the bytes to characters */ 
		myChars = Utils.byteToCharArray(myBytes);
			
		/* Clear out the bytes */
		Arrays.fill(myBytes, (byte)0);
	
		/* Return to caller */
		return myChars;
	}		

	/**
	 * Wrap SymmetricKey
	 * @param pKey the key to wrap
	 * @return the wrapped symmetric key
	 */
	public byte[] wrapKey(SymmetricKey pKey) throws ModelException {
		/* Extract the encoded version of the key */
		byte[] myEncoded = pKey.getSecretKey().getEncoded();
		
		/* Encode the key */
		byte[] myEncrypted = encryptBytes(myEncoded);
		
		/* Return the wrapped key */
		return myEncrypted;
	}	

	/**
	 * UnWrap SymmetricKey
	 * @param pEncrypted the wrapped symmetric key
	 * @param pKeyType the KeyType of the Symmetric Key
	 * @return the symmetric key
	 */
	public SymmetricKey unWrapKey(byte[] 		pEncrypted,
								  SymKeyType	pKeyType) throws ModelException {
		/* Decrypt the encoded bytes */
		byte[] myEncoded = decryptBytes(pEncrypted);
		
		/* Create the Secret Key */
		SecretKey mySecret = new SecretKeySpec(myEncoded, 
											   pKeyType.getAlgorithm());
		
		/* Create the Symmetric Key */
		SymmetricKey myKey = new SymmetricKey(mySecret,
											  pKeyType,
											  theRandom);
		/* Return the key */
		return myKey;
	}
	
	/**
	 * Wrap AsymmetricKey (privateKey)
	 * @param pKey the key to wrap
	 * @return the wrapped Asymmetric key
	 */
	public byte[] wrapKey(AsymmetricKey pKey) throws ModelException {
		/* Access the Private Key */
		PrivateKey myPrivate = pKey.getPrivateKey();
		
		/* Return null if there is no PrivateKey */
		if (myPrivate == null) return null;

		/* Extract the encoded version of the key */
		byte[] myEncoded = myPrivate.getEncoded();
		
		/* Encode the key */
		byte[] myEncrypted = encryptBytes(myEncoded);
		
		/* Return the wrapped key */
		return myEncrypted;
	}	
	
	/**
	 * Unwrap AsymmetricKey
	 * @param pEncrypted the wrapped private key
	 * @param pPublicKey the wrapped private key
	 * @param pKeyMode the KeyMode of the ASymmetric Key
	 * @return the Asymmetric key
	 */
	public AsymmetricKey unWrapKey(byte[] 		pEncrypted,
			 					   byte[]		pPublicKey,
			 					   SecurityMode	pKeyMode) throws ModelException {
		/* Decrypt the encoded bytes */
		byte[] myEncoded = decryptBytes(pEncrypted);
		
		/* Create the Asymmetric Key */
		AsymmetricKey myKey = new AsymmetricKey(myEncoded,
												pPublicKey,
											  	pKeyMode,
											  	theRandom);
		/* Return the key */
		return myKey;
	}	
}
