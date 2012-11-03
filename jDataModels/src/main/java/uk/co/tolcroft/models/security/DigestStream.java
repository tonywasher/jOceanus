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

import java.io.IOException;
import java.security.MessageDigest;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.security.SecurityControl.DigestType;

public class DigestStream {
	/**
	 * Buffer size for skip reads
	 */
	private final static int    BUFSIZE   		= 1024;
	
	/**
	 * Provides a digest OutputStream. This class simply calculates a digest of the data in the stream at this 
	 * point and passes the data onto the next Output Stream in the chain.
	 */
	public static class Output extends java.io.OutputStream {
		/**
		 * The underlying output stream
		 */
		private java.io.OutputStream	theStream	= null;

		/**
		 * has this stream been closed
		 */
		private boolean					isClosed 	= false;

		/**
		 * The Message Digest of the data written 
		 */
		private MessageDigest			theDigest	= null;
		
		/** 
		 * The length of the data written 
		 */
		private long					theDataLen	= 0;
		
		/** 
		 * Access the length of the data written
		 * @return the length of data written 
		 */
		public long		getDataLen()	{ return theDataLen; }
		
		/** 
		 * Access the digest of the data written
		 * @return the digest of data written 
		 */
		public byte[]	getDigest()		{ return theDigest.digest(); }
		
		/**
		 * Construct the output stream
		 * @param pDigestType the type of digest
		 * @param pStream the stream to write encrypted data to
		 */
		public Output(DigestType 			pDigestType,
					  java.io.OutputStream 	pStream) throws ModelException {		
			/* Protect against exceptions */
			try {
				/* Create the message digest */
				theDigest = MessageDigest.getInstance(pDigestType.getAlgorithm(), 
													  SecurityControl.getProvider().getProvider());

				/* Store the stream */
				theStream = pStream;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
									"Exception creating digest output stream",
									e);
			}			
		}
		
		@Override
		public void close() throws IOException {
			/* Null operation if we are already closed */
			if (!isClosed) {
				/* Flush the output stream */
				theStream.flush();
				
				/* Close the output stream */
				theStream.close();
				isClosed = true;
			}
		}
		
		@Override
		public void flush() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");

			/* Flush the output stream */
			theStream.flush();
		}
		
		@Override
		public void write(byte[] pBytes, int pOffset, int pLength) throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Update the data digest */
			theDigest.update(pBytes, pOffset, pLength);
		
			/* Adjust the data length */
			theDataLen += pLength;
			
			/* Write the bytes to the stream */
			theStream.write(pBytes, pOffset, pLength);
		}
		
		@Override
		public void write(byte[] pBytes) throws IOException {
			/* Write the bytes to the stream */
			write(pBytes, 0, pBytes.length);
		}
		
		@Override
		public void write(int pByte) throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Update the data digest */
			theDigest.update((byte)pByte);
			
			/* Adjust the data length */
			theDataLen++;
			
			/* Write the byte to the stream */
			theStream.write(pByte);
		}
	}
	
	/**
	 * Provides a digest InputStream. This class simply calculates a digest of the data in the stream at this 
	 * point as it is read. On close of the file, the digest is validated. 
	 */
	public static class Input extends java.io.InputStream {
		/**
		 * The underlying input stream
		 */
		private java.io.InputStream	theStream				= null;

		/**
		 * has this stream been closed
		 */
		private boolean				isClosed 				= false;

		/**
		 * The Message Digest of the data read 
		 */
		private MessageDigest 	 	theDigest				= null;
		
		/** 
		 * The length of the data read 
		 */
		private long				theDataLen				= 0;
		
		/** 
		 * The expected length of the data read 
		 */
		private long				theExpectedDataLen		= 0;
		
		/**
		 * The Expected Message Digest Bytes of the data 
		 */
		private byte[] 	 			theExpectedDigestBytes	= null;
		
		/**
		 * The Skip buffer 
		 */
		private byte[] 	 			theSkipBuffer			= new byte[BUFSIZE];
		
		/**
		 * Has EOF been reached
		 */
		private boolean				hasEOFbeenSeen			= false;
		
		/**
		 * The name of the digest
		 */
		private String				theName					= null;
		
		/**
		 * Set the expected digest details
		 * @param pName the name of the digest
		 * @param pDataLen the expected length of data  
		 * @param pDigest the expected digest value  
		 */
		public	void setExpectedDetails(String  pName,
										long 	pDataLen,
										byte[] 	pDigest) {
			/* Store the name of the digest */
			theName 				= pName;
			
			/* Store the expected details */
			theExpectedDataLen 		= pDataLen;
			theExpectedDigestBytes 	= pDigest;
		}
		
		/**
		 * Construct the input stream
		 * @param pDigestType the type of digest
		 * @param pStream the Stream to read data from
		 * @throws IOException
		 */
		public Input(DigestType 			pDigestType,
				  	 java.io.InputStream 	pStream) throws ModelException {
			/* Protect against exceptions */
			try {
				/* Create a message digest */
				theDigest = MessageDigest.getInstance(pDigestType.getAlgorithm(),
													  SecurityControl.getProvider().getProvider());

				/* Store the stream details */
				theStream = pStream;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new ModelException(ExceptionClass.CRYPTO,
									"Exception creating digest input stream",
									e);
			}			
		}
		
		@Override
		public void close() throws IOException {
			/* Null operation if we are already closed */
			if (!isClosed) {
				/* Close the input stream */
				theStream.close();
				isClosed = true;
				
				/* If we have seen EOF and have a digest to compare */
				if ((hasEOFbeenSeen) && (theExpectedDigestBytes != null)) {
					/* If the data lengths do not match */
					if (theDataLen != theExpectedDataLen) {
						/* Throw an exception */
						throw new IOException("Mismatch on Data lengths for " + theName);
					}
					
					/* If the digest does not match */
					if (!MessageDigest.isEqual(theDigest.digest(),
										   	   theExpectedDigestBytes)) {
						/* Throw an exception */
						throw new IOException("Mismatch on Digest for " + theName);
					}
				}
					
				/* Release allocated encryption resources */
				theDigest				= null;
				theExpectedDigestBytes 	= null;
				theSkipBuffer			= null;
				theName					= null;
			}
		}
		
		@Override
		public long skip(long iNumToSkip) throws IOException {
			long iNumSkipped = 0;
			int iNumToRead;
			int iNumRead;
			
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
		
			/* while we have data left to skip */
			while (iNumToSkip > 0) {
				/* Determine size of next read */
				iNumToRead = BUFSIZE;
				if (iNumToRead > iNumToSkip) iNumToRead = (int)iNumToSkip;
			
				/* Read the next set of data */
				iNumRead = read(theSkipBuffer, 0, iNumToRead);
				
				/* Break loop on EOF */
				if (iNumRead < 0) break;
				
				/* Adjust count */
				iNumToSkip  -= iNumRead;
				iNumSkipped	+= iNumRead;
			}
			
			return iNumSkipped;
		}
		
		@Override
		public int available() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Determine the number of bytes available */
			return theStream.available();
		}
		
		@Override
		public boolean markSupported() {
			/* Always return false */
			return false;
		}
		
		@Override
		public void mark(int readLimit) {
			/* Just ignore the call */
			return;
		}
		
		@Override
		public void reset() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Set the mark */
			throw new IOException("Mark not supported");
		}
		
		@Override
		public int read(byte[] pBuffer, int pOffset, int pLength) throws IOException {			
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Read the bytes from the stream */
			int iNumRead = theStream.read(pBuffer, pOffset, pLength);
			
			/* If we have read some data */
			if (iNumRead > 0) {
				/* Update the message digest */
				theDigest.update(pBuffer, pOffset, iNumRead);
		
				/* Adjust the data length */
				theDataLen += iNumRead;
			}
			
			/* Note if EOF has been seen */
			if (iNumRead == -1)
				hasEOFbeenSeen = true;
			
			/* Return the amount of data read */
			return iNumRead;
		}
		
		@Override
		public int read(byte[] pBytes) throws IOException {
			/* Read the bytes from the stream */
			return read(pBytes, 0, pBytes.length);
		}
		
		@Override
		public int read() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* read the next byte */
			int iByte = theStream.read();
			
			/* If we read data */
			if (iByte > -1) {
				/* Update the message digest */
				theDigest.update((byte)iByte);
			
				/* Adjust the data length */
				theDataLen++;
			}
			
			/* Note if EOF has been seen */
			if (iByte == -1) hasEOFbeenSeen = true;
			
			/* Return to the caller */
			return iByte;
		}
	}	
}
