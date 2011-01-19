package uk.co.tolcroft.security;

import javax.crypto.Cipher;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class SecurityCipher {
	/**
	 * Buffer size for transfers
	 */
	protected final static int  BUFSIZE   		= 1024;	

	/**
	 * The cipher
	 */
	private Cipher 				theCipher 		= null;
		
	/**
	 * The transfer buffer
	 */
	private byte[]    			theBuffer		= null;
	
	/**
	 * Obtain the output buffer
	 * @return the output buffer
	 */
	public byte[] getBuffer() { return theBuffer; }
	
	/**
	 * Constructor
	 * @param pCipher the cipher
	 * @param pInitVect the initialisation vector
	 */
	protected SecurityCipher(Cipher pCipher) {
		theCipher 		= pCipher;
		theBuffer		= new byte[BUFSIZE];
	}
	
	/**
	 * Get Initialisation vector
	 * @return the initialisation vector
	 */
	public byte[] getInitVector() {
		return theCipher.getIV();
	}
	
	/**
	 * Encrypt character array
	 * @param pChars Characters to encrypt
	 * @return Encrypted bytes
	 * @throws finObject.Exception 
	 */
	public byte[] encryptChars(char[] pChars) throws Exception {
		byte[] myBytes;
		
		/* Protect against exceptions */
		try {
			/* Convert the characters to a byte array */
			myBytes = Utils.charToByteArray(pChars);
			
			/* Encrypt the characters */
			myBytes = theCipher.doFinal(myBytes);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to encrypt string",
								e);
		}
		
		/* Return to caller */
		return myBytes;
	}		
	
	/**
	 * Decrypt bytes into a character array
	 * @param pString String to encrypt
	 * @return Encrypted bytes
	 * @throws finObject.Exception 
	 */
	public char[] decryptBytes(byte[] pBytes) throws Exception {
		byte[] 	myBytes;
		char[]	myChars;
		
		/* Protect against exceptions */
		try {
			/* Decrypt the bytes */
			myBytes  = theCipher.doFinal(pBytes);
			
			/* Convert the bytes to characters */ 
			myChars = Utils.byteToCharArray(myBytes);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to decrypt bytes",
								e);
		}
		
		/* Return to caller */
		return myChars;
	}		

	/**
	 * Update Cipher
	 * @param pBytes Bytes to update cipher with
	 * @param pOffset offset within pBytes to read bytes from
	 * @param pLength length of data to update with
	 * @return number of bytes transferred to output buffer 
	 */
	public int update(byte[] pBytes, int pOffset, int pLength) throws Exception {
		int iNumBytes;
		
		/* Protect against exceptions */
		try {
			/* Check how long a buffer we need */
			iNumBytes = theCipher.getOutputSize(pLength);
		
			/* Extend the buffer if required */
			if (iNumBytes > theBuffer.length)
				theBuffer = new byte[iNumBytes];
		
			/* Update the data */
			iNumBytes = theCipher.update(pBytes, pOffset, pLength, theBuffer);
		
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to update cipher",
								e);
		}
		
		/* Return to caller */
		return iNumBytes;
	}
	
	/**
	 * Finish Cipher encrypting/decrypting any data buffered within the cipher
	 * @return number of bytes transferred to output buffer 
	 */
	public int finish() throws Exception {
		int iNumBytes;
		
		/* Protect against exceptions */
		try {
			/* Check how long a buffer we need to handle buffered data*/
			iNumBytes = theCipher.getOutputSize(0);
		
			/* Extend the buffer if required */
			if (iNumBytes > theBuffer.length)
				theBuffer = new byte[iNumBytes];
		
			/* Update the data */
			iNumBytes = theCipher.doFinal(theBuffer, 0);
		}
		catch (Throwable e) {
			throw new Exception(ExceptionClass.ENCRYPT,
								"Failed to finish cipher operation",
								e);
		}
		
		/* Return to caller */
		return iNumBytes;
	}
}
