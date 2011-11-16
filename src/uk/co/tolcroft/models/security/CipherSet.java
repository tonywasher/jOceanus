package uk.co.tolcroft.models.security;

import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.security.AsymmetricKey.AsymKeyType;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class CipherSet {
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
	 * The Number of Steps
	 */
	private int							theNumSteps	= DEFSTEPS;
	
	/**
	 * The Random Generator
	 */
	private SecureRandom				theRandom	= null;
	
	/**
	 * The DataKey Map
	 */
	private Map<SymKeyType, DataCipher>	theMap		= null;

	/**
	 * Constructor
	 * @param pRandom the Secure Random
	 * @param pNumSteps the Number of encryption steps 
	 */
	public CipherSet(SecureRandom	pRandom,
					 int			pNumSteps) {
		/* Store parameters */
		theRandom 	= pRandom;
		theNumSteps	= pNumSteps;
		
		/* Build the Map */
		theMap = new EnumMap<SymKeyType, DataCipher>(SymKeyType.class);
	}
	
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
	public void buildCiphers(byte[] pSecret) throws Exception {
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
							 byte[] 	pSecret) throws Exception {
		/* Determine the key length in bytes */
		int myKeyLen = pKeyType.getKeySize() / 8;
			
		/* If the secret is not long enough */
		if (pSecret.length < myKeyLen) 
			throw new Exception(ExceptionClass.CRYPTO,
								"Secret is insufficient in length"); 
					
		/* Determine index into array for Key Type */
		byte[] myNew = new byte[pSecret.length];
		int    myIndex = 13*pKeyType.getId();
		myIndex %= pSecret.length;
		
		/* If we need to shift the array */
		if (myIndex > 0) {
			/* Access shifted array */
			System.arraycopy(pSecret, myIndex, myNew, 0, pSecret.length-myIndex);
			System.arraycopy(pSecret, 0, myNew, pSecret.length-myIndex, myIndex);
			pSecret = myNew;
		}
			
		/* Build the secret key specification */
		SecretKey myKeyDef = new SecretKeySpec(Arrays.copyOf(pSecret, myKeyLen),
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
	public byte[] encryptBytes(byte[] pBytes) throws Exception {
		/* Allocate a new initialisation vector */
		byte[] 			myVector = new byte[SymmetricKey.IVSIZE];
		theRandom.nextBytes(myVector);
		
		/* Determine the SymKeyTypes to use */
		SymKeyType[] myKeyTypes = SymKeyType.getRandomTypes(theNumSteps, theRandom);
		
		/* Encode the array */
		byte[] myKeyBytes = encodeSymKeyTypes(myKeyTypes);
		
		/* Loop through the SymKeyTypes */
		for (int i=0; i < myKeyTypes.length; i++) {
			/* Access the DataCipher */
			DataCipher myCipher = theMap.get(myKeyTypes[i]);
			
			/* Encrypt the bytes */
			pBytes = myCipher.encryptBytes(pBytes, myVector);
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
	private byte[] encodeSymKeyTypes(SymKeyType[] pTypes) throws Exception {
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
	public byte[] decryptBytes(byte[] pBytes) throws Exception {
		/* Split the bytes into the separate parts */
		byte[] 			myVector = Arrays.copyOf(pBytes, SymmetricKey.IVSIZE);
		SymKeyType[]	myTypes	 = decodeSymKeyTypes(pBytes);	
		byte[] 			myBytes  = Arrays.copyOfRange(pBytes, 
													  SymmetricKey.IVSIZE+numKeyBytes(myTypes.length), 
													  pBytes.length);
		
		/* Loop through the SymKeyTypes */
		for (int i=myTypes.length-1; i >= 0; i--) {
			/* Access the DataCipher */
			DataCipher myCipher = theMap.get(myTypes[i]);
			
			/* Decrypt the bytes */
			myBytes = myCipher.decryptBytes(myBytes, myVector);
		}
		
		/* Return the decrypted bytes */
		return myBytes;
	}
	
	/**
	 * Decode SymKeyTypes
	 * @param pBytes the encrypted bytes
	 * @return the array of SymKeyTypes
	 */
	private SymKeyType[] decodeSymKeyTypes(byte[] pBytes) throws Exception {
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
	public byte[] encryptString(String pString) throws Exception {
		/* Protect against exceptions */
		try {
			/* Access the bytes */
			byte[] myBytes = pString.getBytes(SecurityControl.ENCODING);
			
			/* Encrypt the bytes */
			return encryptBytes(myBytes);
		}
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.DATA,
								"Failed to extract bytes from String - " + pString,
								e);
		}
	}
	
	/**
	 * Decrypt string 
	 * @param pBytes the string to decrypt
	 * @return the decrypted string
	 */
	public String decryptString(byte[] pBytes) throws Exception {
		/* Protect against exceptions */
		try {
			/* Decrypt the bytes */
			byte[] myBytes = decryptBytes(pBytes);
			
			/* ReBuild the string */
			return new String(myBytes, SecurityControl.ENCODING);
		}
		catch (Exception e) { throw e; }
		catch (Throwable e) {
			throw new Exception(ExceptionClass.DATA,
								"Failed to build string from bytes",
								e);
		}
	}
	
	/**
	 * Encrypt character array
	 * @param pChars Characters to encrypt
	 * @return Encrypted bytes
	 */
	public byte[] encryptChars(char[] pChars) throws Exception {
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
	public char[] decryptChars(byte[] pBytes) throws Exception {
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
	public byte[] wrapKey(SymmetricKey pKey) throws Exception {
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
								  SymKeyType	pKeyType) throws Exception {
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
	public byte[] wrapKey(AsymmetricKey pKey) throws Exception {
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
	 * @param pKeyType the KeyType of the ASymmetric Key
	 * @return the Asymmetric key
	 */
	public AsymmetricKey unWrapKey(byte[] 		pEncrypted,
			 					   byte[]		pPublicKey,
			 					   AsymKeyType	pKeyType) throws Exception {
		/* Decrypt the encoded bytes */
		byte[] myEncoded = decryptBytes(pEncrypted);
		
		/* Create the Asymmetric Key */
		AsymmetricKey myKey = new AsymmetricKey(myEncoded,
												pPublicKey,
											  	pKeyType,
											  	theRandom);
		/* Return the key */
		return myKey;
	}	
}
