package uk.co.tolcroft.security;

import java.io.IOException;
import java.security.MessageDigest;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

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
		 * @param pFile the file to write encrypted data to
		 * @throws finObject.Exception
		 */
		public Output(java.io.OutputStream pStream) throws Exception {		
			/* Protect against exceptions */
			try {
				/* Create the message digest */
				theDigest = MessageDigest.getInstance(SecurityControl.DIGEST);

				/* Store the stream */
				theStream = pStream;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.ENCRYPT,
									"Exception creating digest output stream",
									e);
			}			
		}
		
		/**
		 * Close the output stream
		 * @throws IOException
		 */
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
		 * @param pStream the Stream to read data from
		 * @throws IOException
		 */
		public Input(java.io.InputStream pStream) throws Exception {
			/* Protect against exceptions */
			try {
				/* Create a message digest */
				theDigest = MessageDigest.getInstance(SecurityControl.DIGEST);

				/* Store the stream details */
				theStream = pStream;
			}
			
			/* Catch exceptions */
			catch (Throwable e) {
				throw new Exception(ExceptionClass.ENCRYPT,
									"Exception creating digest input stream",
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
		
		/**
		 * Skip a number of bytes in the input stream
		 * @param iNumToSkip the number of bytes to skip
		 * @return the actual number of bytes skipped
		 * @throws IOException
		 */
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
		
		/**
		 * Determine the number of bytes available without blocking
		 * @return the number of bytes available
		 * @throws IOException
		 */
		public int available() throws IOException {
			/* If we are already closed throw IO Exception */
			if (isClosed) throw new IOException("Stream is closed");
			
			/* Determine the number of bytes available */
			return theStream.available();
		}
		
		/**
		 * Determines whether the mark and reset methods are available
		 * @return <code>false</code>
		 */
		public boolean markSupported() {
			/* Always return false */
			return false;
		}
		
		/**
		 * Marks the current position in the input stream
		 * @param readLimit the number of bytes to read before mark is invalidated
		 */
		public void mark(int readLimit) {
			/* Just ignore the call */
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
		
		/**
		 * Read an array of bytes from the Input stream
		 * @param pBytes the buffer to read into to write
		 * @return the actual length of data read or -1 if EOF
		 * @throws IOException
		 */
		public int read(byte[] pBytes) throws IOException {
			/* Read the bytes from the stream */
			return read(pBytes, 0, pBytes.length);
		}
		
		/**
		 * Read a byte from the Input stream
		 * @return the byte read or -1 if EOF
		 * @throws IOException
		 */
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
