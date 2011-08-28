package uk.co.tolcroft.models.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.security.SymmetricKey.SymKeyType;

public class EncryptionStream {
	/**
	 * Provide an encrypt OutputStream wrapper. This class simply wraps an output buffer
	 * and encrypts the data before passing it on.
	 */
	public static class Output extends java.io.OutputStream {
		/**
		 * The underlying output stream
		 */
		private OutputStream 			theStream		= null;

		/**
		 * has this stream been closed
		 */
		private boolean					isClosed 		= false;

		/**
		 * The Stream Cipher
		 */
		private StreamCipher 			theCipher		= null;
		
		/**
		 * The Wrapped Key 
		 */
		private byte[]    				theWrappedKey	= null;
		
		/**
		 * The initialisation vector
		 */
		private byte[]    				theInitVector	= null;
		
		/**
		 *  A buffer for single byte writes 
		 */
		private byte[] 					theByte 		= new byte[1];
		
		/** 
		 * Access the initialisation vector
		 * @return the initialisation vector
		 */
		public byte[]	getInitVector()			{ return theInitVector; }
		
		/** 
		 * Access the wrapped key
		 * @return the wrapped key
		 */
		public byte[]	getWrappedKey()			{ return theWrappedKey; }
		
		/**
		 * Construct a symmetric key encryption output stream
		 * @param pControl the security control
		 * @param pKeyType the symmetric key type to generate
		 * @param pStream the stream to encrypt to
		 */
		public Output(SecurityControl		pControl,
					  SymKeyType			pKeyType,
				 	  java.io.OutputStream 	pStream) throws Exception {
			SymmetricKey myKey;
			
			/* Protect against exceptions */
			try {
				/* record the output stream */
				theStream 	= pStream;
				
				/* Generate the Secret key and initialise for encryption */
				myKey 		= pControl.getSymmetricKey(pKeyType);
				theCipher 	= myKey.initEncryptionStream();
				
				/* Access the initialisation vector */
				theInitVector	= theCipher.getInitVector();
				
				/* Access the secured key definition */
				theWrappedKey 	= pControl.getSecuredKeyDef(myKey);
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.CRYPTO,
									"Exception creating encryption output stream",
									e);
			}			
		}
		
		/**
		 * Construct a password key encryption output stream
		 * @param pKey the password key
		 * @param pStream the stream to encrypt to
		 */
		public Output(PasswordKey			pKey,
				 	  java.io.OutputStream 	pStream) throws Exception {
			
			/* Protect against exceptions */
			try {
				/* record the output stream */
				theStream 	= pStream;
				
				/* initialise for encryption */
				theCipher 	= pKey.initEncryptionStream();
				
				/* Access the initialisation vector */
				theInitVector	= theCipher.getInitVector();
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.CRYPTO,
									"Exception creating encryption output stream",
									e);
			}			
		}
		
		/**
		 * Close the output stream
		 * @throws IOException
		 */
		public void close() throws IOException {
			byte[] myBytes;
			
			/* Protect against exceptions */
			try { 
				/* Null operation if we are already closed */
				if (!isClosed) {
					/* Finish the cipher operation */
					int iNumBytes = theCipher.finish();
					myBytes = theCipher.getBuffer();
					
					/* If we have data to write then write it */
					if (iNumBytes > 0) theStream.write(myBytes, 0, iNumBytes);
					
					/* Close the output stream */
					theStream.close();
					isClosed = true;
					
					/* Release allocated buffers */
					theByte   = null;
				}
			}
			
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Exception e) 	{ throw new IOException(e);	}
		}
		
		/**
		 * Flush the output stream
		 * @throws IOException
		 */
		public void flush() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");

			/* Flush the output stream */
			theStream.flush();
		}
		
		/**
		 * Write an array of bytes to the Output stream
		 * @param pBytes the bytes to write
		 * @param pOffset the offset from which to start writing
		 * @param pLength the length of data to write
		 * @throws IOException
		 */
		public void write(byte[] pBytes, int pOffset, int pLength)	throws IOException {
			int		iNumBytes;
			byte[] 	myBytes;

			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Ignore a null write */
			if (pLength == 0) return;
		
			/* Protect against exceptions */
			try { 
				/* Update the cipher with these bytes */
				iNumBytes = theCipher.update(pBytes, pOffset, pLength);
				myBytes   = theCipher.getBuffer();
			
				/* Write the bytes to the stream */
				theStream.write(myBytes, 0, iNumBytes);
			}
			
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Exception e) 	{ throw new IOException(e);	}
		}
		
		/**
		 * Write an array of bytes to the Output stream
		 * @param pBytes the bytes to write
		 * @throws IOException
		 */
		public void write(byte[] pBytes) throws IOException {
			/* Write the bytes to the stream */
			write(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Write a byte to the Output stream
		 * @param pByte the byte to write
		 * @throws IOException
		 */
		public void write(int pByte) throws IOException {
			/* Copy the byte to the buffer */
			theByte[0] = (byte)pByte;
			
			/* Write the byte to the stream */
			write(theByte, 0, 1);
		}
	}
	
	/**
	 * Provide an decryptInputStream wrapper. This class simply wraps an input buffer and processes
	 * it as a Zip file. It will read control information from the HEADER zip entry and will use this
	 * information to decrypts the data from the DATA Zip Entry
	 */
	public static class Input extends java.io.InputStream {
		/**
		 * Buffer size for transfers
		 */
		protected final static int	BUFSIZE   			= 1024;
		
		/**
		 * The underlying input stream
		 */
		private InputStream 		theStream			= null;

		/**
		 * has this stream been closed
		 */
		private boolean				isClosed 			= false;

		/**
		 * The Stream Cipher
		 */
		private StreamCipher	 	theCipher			= null;
		
		/**
		 *  A buffer for single byte reads 
		 */
		private byte[] 				theByte 			= new byte[1];
		
		/**
		 * The buffer used for reading from input stream
		 */
		private byte[] 	 			theBuffer			= new byte[StreamCipher.BUFSIZE];
		
		/**
		 * The holding buffer for data that has been decrypted but not read
		 */
		private decryptBuffer		theDecrypted		= new decryptBuffer();
		
		/**
		 * Construct the decryption input stream
		 * @param pControl the security control
		 * @param pSecretKey the encoded secret key  
		 * @param pKeyType the symmetric key type
		 * @param pInitVector the initialisation vector  
		 * @param pStream the stream to decrypt from
		 */
		public Input(SecurityControl		pControl,
					 byte[]  				pSecretKey,
					 SymKeyType				pKeyType,
					 byte[]  				pInitVector,
					 java.io.InputStream 	pStream) throws Exception {
			SymmetricKey myKey;
			
			/* Protect from exceptions */
			try {
				/* record the input stream */
				theStream 	= pStream;

				/* Access the new cipher */
				myKey = pControl.getSymmetricKey(pSecretKey, pKeyType);

				/* Initialise the decryption */
				theCipher = myKey.initDecryptionStream(pInitVector);
			}

			/* Catch exceptions */
			catch (Exception e) {
				throw new Exception(ExceptionClass.CRYPTO,
									"Exception deciphering secret key",
									e);
			}
		}
		
		/**
		 * Construct the password key decryption input stream
		 * @param pKey the password key
		 * @param pInitVector the initialisation vector
		 * @param pStream the stream to decrypt from
		 */
		public Input(PasswordKey			pKey,
					 byte[]					pInitVector,
					 java.io.InputStream 	pStream) throws Exception {
			/* Protect from exceptions */
			try {
				/* record the input stream */
				theStream 	= pStream;

				/* Initialise the decryption */
				theCipher = pKey.initDecryptionStream(pInitVector);
			}				
			
			/* Catch exceptions */
			catch (Exception e) {
				throw new Exception(ExceptionClass.CRYPTO,
									"Exception initialising password decryption",
									e);
			}
		}
		
		/**
		 * Close the input stream
		 * @throws IOException
		 */
		public void close() throws IOException {
			/* Null operation if we are already closed */
			if (!isClosed) {
				/* Close the input stream */
				theStream.close();
				isClosed = true;
				
				/* release buffers */
				theByte					= null;
				theDecrypted			= null;
				theBuffer				= null;
			}
		}
		
		/**
		 * Skip a number of bytes in the input stream
		 * @param iNumToSkip the number of bytes to skip
		 * @return the actual number of bytes skipped
		 * @throws IOException
		 */
		public long skip(long iNumToSkip) throws IOException {
			long 	iNumSkipped = 0;
			int 	iNumToRead;
			int 	iNumRead;
			byte[] 	myBuffer	= new byte[BUFSIZE];
			
			
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
		
			/* while we have data left to skip */
			while (iNumToSkip > 0) {
				/* Determine size of next read */
				iNumToRead = BUFSIZE;
				if (iNumToRead > iNumToSkip) iNumToRead = (int)iNumToSkip;
			
				/* Read the next set of data */
				iNumRead = read(myBuffer, 0, iNumToRead);
				
				/* Break loop on EOF */
				if (iNumRead < 0) break;
				
				/* Adjust count */
				iNumToSkip  -= iNumRead;
				iNumSkipped	+= iNumRead;
			}
			
			return iNumSkipped;
		}
					
		/**
		 * Determine the number of bytes available without blocking
		 * @return the number of bytes available
		 * @throws IOException
		 */
		public int available() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Determine the number of bytes available */
			return theDecrypted.available();
		}
		
		/**
		 * Determines whether the mark and reset methods are available
		 * @return <code>false</code>
		 */
		public boolean markSupported() {
			/* return false */
			return false;
		}
		
		/**
		 * Marks the current position in the input stream
		 * @param readLimit the number of bytes to read before mark is invalidated
		 */
		public void mark(int readLimit) {
			/* Just ignore */
			return;
		}
		
		/**
		 * Resets the current position in the input stream to that which was last marked
		 * @throws IOException
		 */
		public void reset() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Set the mark */
			throw new IOException("Mark not supported");
		}
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBuffer the buffer to read into to write
		 * @param pOffset the offset from which to start reading
		 * @param pLength the maximum length of data to read
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBuffer, int pOffset, int pLength) throws IOException {
			int 	iNumRead;
		
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Protect against exceptions */
			try {
				/* If there is no data in the decrypt buffer */
				if (theDecrypted.available() == 0) {
					/* If we have already exhausted the source return now */
					if (theDecrypted.hasEOFbeenSeen) return -1;
					
					/* Read more data from the input stream */
					iNumRead = theStream.read(theBuffer, 0, BUFSIZE);
					
					/* If we read no data just return details */
					if (iNumRead == 0) return iNumRead;
					
					/* Decrypt and store the decrypted bytes into the decrypt buffer */
					theDecrypted.storeBytes(theBuffer, iNumRead);
				}
				
				/* Read from the decrypted buffer */
				iNumRead  = theDecrypted.readBytes(pBuffer, pOffset, pLength);												
			}
						
			/* Catch exceptions */
			catch (IOException e) 	{ throw e; }
			catch (Throwable e) 	{ throw new IOException(e);	}
			
			/* Return the amount of data read */
			return iNumRead;
		}
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBytes the buffer to read into to write
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBytes) throws IOException {
			/* Write the byte to the stream */
			return read(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Read a byte from the Input stream
		 * @return the byte read or -1 if EOF
		 * @throws IOException
		 */
		public int read() throws IOException {
			int iNumRead;
			
			/* Loop until we get a byte or EOF */
			while ((iNumRead = read(theByte, 0, 1)) == 0);
			
			/* Convert the byte read into an integer */
			if (iNumRead > 0) iNumRead = (theByte[0] & 0xff);
				
			/* Return to the caller */
			return iNumRead;
		}
		
		/**
		 * Buffer to hold the decrypted data prior to returning to returning to caller
		 */
		private class decryptBuffer {
			/**
			 * The buffer itself
			 */
			private byte[]	theStore 		= null;
			
			/**
			 * The length of data in the buffer
			 */
			private int		theDataLen		= 0;
			
			/**
			 * The read offset of data in the buffer
			 */
			private int		theReadOffset	= 0;
			
			/**
			 * have we seen EOF
			 */
			private boolean	hasEOFbeenSeen	= false;
			
			/**
			 * Determine the amount of data in the buffer
			 * @return the number of data bytes in the buffer
			 */
			public int		available() { return theDataLen - theReadOffset; }
			
			/**
			 * Read a number of bytes out from the buffer
			 * @param pBuffer the buffer to read bytes into
			 * @param pOffset the offset from which to start reading
			 * @param pLength the maximum length of data to read
			 * @return the actual length of data read or -1 if EOF
			 */
			public int		readBytes(byte[] pBuffer, int pOffset, int pLength) {
				/* Determine how much data we have available */
				int iNumRead  = theDataLen - theReadOffset;
				
				/* Determine how much data we can transfer */
				iNumRead = (iNumRead <= pLength) ? iNumRead
												 : pLength;
				
				/* If we have data to copy */
				if (iNumRead > 0) {
					/* Transfer the bytes */
					System.arraycopy(theStore, theReadOffset, pBuffer, pOffset, iNumRead);

					/* Adjust ReadOffset */
					theReadOffset += iNumRead;
				
					/* If we have finished with the data in the buffer */
					if (theReadOffset >= theDataLen) {
						/* Reset the values */
						theDataLen    = 0;
						theReadOffset = 0;
					}
				}
				
				/* else if we have no data check for EOF and report it if required */
				else if (hasEOFbeenSeen)
					iNumRead = -1;
				
				/* Return the number of bytes transferred */
				return iNumRead;
			}
			
			/**
			 * Decrypt bytes into the buffer and update the message digests
			 * @param pBuffer the buffer from which to store bytes 
			 * @param pLength the number of bytes read into the buffer (must not be zero)
			 */
			public void	storeBytes(byte[] pBuffer, int pLength)	throws 	Exception {
				int iNumBytes = 0; 
			
				/* If we have EOF from the input stream */
				if (pLength == -1) {
					/* Record the fact and reset the read length to zero */
					hasEOFbeenSeen  = true;
					pLength			= 0;
				}
				
				/* If we have data that we read from the input stream */
				if (pLength > 0) {
					/* Decrypt the data */
					iNumBytes = theCipher.update(pBuffer, 0, pLength);
				}
				
				/* else we have EOF */
				else if (hasEOFbeenSeen) {
					/* Finish the cipher operation to pick up remaining bytes */
					iNumBytes = theCipher.finish();
				}
			
				/* Set up holding variables */
				theStore  		= theCipher.getBuffer();
				theDataLen    	= iNumBytes;
				theReadOffset 	= 0;
				
				/* Return to caller */
				return;
			}
		}
	}	
}
