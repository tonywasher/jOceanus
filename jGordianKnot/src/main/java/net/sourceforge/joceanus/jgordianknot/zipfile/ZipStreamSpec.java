/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.zipfile;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.CipherMode;
import net.sourceforge.joceanus.jgordianknot.CipherSet;
import net.sourceforge.joceanus.jgordianknot.DataDigest;
import net.sourceforge.joceanus.jgordianknot.DataMac;
import net.sourceforge.joceanus.jgordianknot.DigestType;
import net.sourceforge.joceanus.jgordianknot.MacSpec;
import net.sourceforge.joceanus.jgordianknot.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.StreamKey;
import net.sourceforge.joceanus.jgordianknot.StreamKeyType;
import net.sourceforge.joceanus.jgordianknot.SymKeyType;
import net.sourceforge.joceanus.jgordianknot.SymmetricKey;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Zip Stream specification.
 */
public abstract class ZipStreamSpec {
    /**
     * The Stream Type property.
     */
    private static final String PROP_TYPE = "StreamType";

    /**
     * The Stream Value property.
     */
    private static final String PROP_VALUE = "StreamValue";

    /**
     * StreamType.
     */
    private final ZipStreamType theType;

    /**
     * Obtain Stream type.
     * @return the stream type
     */
    public ZipStreamType getStreamType() {
        return theType;
    }

    /**
     * Constructor.
     * @param pType the StreamType
     */
    private ZipStreamSpec(final ZipStreamType pType) {
        theType = pType;
    }

    /**
     * Obtain Encoded stream type.
     * @param pValue the value to encode
     * @return the encoded value
     */
    protected long getEncoded(final long pValue) {
        return (pValue << DataConverter.NYBBLE_SHIFT)
               + theType.getId();
    }

    /**
     * Allocate Properties.
     * @param pIndex the index of the stream
     * @param pProperties the properties
     */
    protected abstract void allocateProperties(final int pIndex,
                                               final ZipFileProperties pProperties);

    /**
     * Update signature.
     * @param pSignature the signature
     * @throws SignatureException on error
     */
    protected abstract void updateSignature(final Signature pSignature) throws SignatureException;

    /**
     * Build input stream.
     * @param pCurrent the current input stream
     * @param pCipherSet the cipher set
     * @return the new input stream
     * @throws JOceanusException on error
     */
    protected abstract InputStream buildInputStream(final InputStream pCurrent,
                                                    final CipherSet pCipherSet) throws JOceanusException;

    /**
     * ZipStream List.
     */
    protected static class ZipStreamList {
        /**
         * List of streams.
         */
        private final List<ZipStreamSpec> theStreams;

        /**
         * Data length.
         */
        private final long theDataLength;

        /**
         * Obtain Data length.
         * @return the data length
         */
        public long getDataLength() {
            return theDataLength;
        }

        /**
         * Constructor.
         * @param pStream the output stream
         * @param pCipherSet the CipherSet
         * @throws JOceanusException on error
         */
        @SuppressWarnings("resource")
        protected ZipStreamList(final OutputStream pStream,
                                final CipherSet pCipherSet) throws JOceanusException {
            /* Allocate the list */
            theStreams = new ArrayList<ZipStreamSpec>();

            /* Loop through the streams */
            long myLen = -1;
            OutputStream myStream = pStream;
            while (myStream != null) {
                /* If this is a Digest Output Stream */
                if (myStream instanceof DigestOutputStream) {
                    /* Process stream */
                    DigestOutputStream myDigest = (DigestOutputStream) myStream;
                    theStreams.add(0, new ZipDigestSpec(myDigest));
                    myStream = myDigest.getNextStream();

                    /* Store dataLength if needed */
                    if (myLen == -1) {
                        myLen = myDigest.getDataLen();
                    }

                    /* If this is a Mac Output Stream */
                } else if (myStream instanceof MacOutputStream) {
                    /* Process stream */
                    MacOutputStream myMac = (MacOutputStream) myStream;
                    theStreams.add(0, new ZipMacSpec(myMac, pCipherSet));
                    myStream = myMac.getNextStream();

                    /* Store dataLength if needed */
                    if (myLen == -1) {
                        myLen = myMac.getDataLen();
                    }

                    /* If this is a LZMA Output Stream */
                } else if (myStream instanceof LZMAOutputStream) {
                    /* Process stream */
                    LZMAOutputStream myLZMA = (LZMAOutputStream) myStream;
                    theStreams.add(0, new ZipLZMASpec());
                    myStream = myLZMA.getNextStream();

                    /* If this is a Encryption Output Stream */
                } else if (myStream instanceof EncryptionOutputStream) {
                    /* Process stream */
                    EncryptionOutputStream myEnc = (EncryptionOutputStream) myStream;
                    myStream = myEnc.getNextStream();
                    switch (myEnc.getStreamType()) {
                        case SYMMETRIC:
                            theStreams.add(0, new ZipSymKeySpec(myEnc, pCipherSet));
                            break;
                        case STREAM:
                            theStreams.add(0, new ZipStreamKeySpec(myEnc, pCipherSet));
                            break;
                        default:
                            myStream = null;
                            break;
                    }

                    /* Else stop loop */
                } else {
                    myStream = null;
                }
            }

            /* Store dataLength if needed */
            theDataLength = myLen;
        }

        /**
         * Constructor.
         * @param pProperties the properties
         * @throws JOceanusException on error
         */
        protected ZipStreamList(final ZipFileProperties pProperties) throws JOceanusException {
            /* Allocate the list */
            theStreams = new ArrayList<ZipStreamSpec>();
            theDataLength = -1;

            /* Loop through the streamSpecs */
            for (int myIndex = 1;; myIndex++) {
                /* Check for property and break loop if not found */
                Long myType = pProperties.getLongProperty(PROP_TYPE
                                                          + myIndex);
                if (myType == null) {
                    break;
                }

                /* Access IDs */
                int myStreamId = (int) (myType & DataConverter.NYBBLE_MASK);
                myType >>= DataConverter.NYBBLE_SHIFT;
                ZipStreamType myStreamType = ZipStreamType.fromId(myStreamId);

                /* Switch on stream type */
                switch (myStreamType) {
                    case SYMMETRIC:
                        theStreams.add(new ZipSymKeySpec(pProperties, myIndex, myType));
                        break;
                    case STREAM:
                        theStreams.add(new ZipStreamKeySpec(pProperties, myIndex, myType));
                        break;
                    case DIGEST:
                        theStreams.add(new ZipDigestSpec(pProperties, myIndex, myType));
                        break;
                    case MAC:
                        theStreams.add(new ZipMacSpec(pProperties, myIndex, myType));
                        break;
                    case LZMA:
                        theStreams.add(new ZipLZMASpec());
                        break;
                    default:
                        break;
                }
            }
        }

        /**
         * Allocate Properties.
         * @param pProperties the properties
         */
        protected void allocateProperties(final ZipFileProperties pProperties) {
            /* Loop through the streamSpecs */
            int myIndex = 0;
            Iterator<ZipStreamSpec> myIterator = theStreams.iterator();
            while (myIterator.hasNext()) {
                ZipStreamSpec mySpec = myIterator.next();
                myIndex++;

                /* Allocate properties for the streamSpec. */
                mySpec.allocateProperties(myIndex, pProperties);
            }
        }

        /**
         * Update signature.
         * @param pSignature the signature
         * @throws SignatureException on error
         */
        protected void updateSignature(final Signature pSignature) throws SignatureException {
            /* Loop through the streamSpecs */
            Iterator<ZipStreamSpec> myIterator = theStreams.iterator();
            while (myIterator.hasNext()) {
                ZipStreamSpec mySpec = myIterator.next();

                /* Update the signature */
                mySpec.updateSignature(pSignature);
            }
        }

        /**
         * Build Input Stream.
         * @param pCurrent the current input stream
         * @param pCipherSet the CipherSet
         * @return the constructed input stream
         * @throws JOceanusException on error
         */
        protected InputStream buildInputStream(final InputStream pCurrent,
                                               final CipherSet pCipherSet) throws JOceanusException {
            /* Loop through the streamSpecs */
            InputStream myCurrent = pCurrent;
            Iterator<ZipStreamSpec> myIterator = theStreams.iterator();
            while (myIterator.hasNext()) {
                ZipStreamSpec mySpec = myIterator.next();

                /* Allocate properties for the streamSpec. */
                myCurrent = mySpec.buildInputStream(myCurrent, pCipherSet);
            }

            /* Return the input stream */
            return myCurrent;
        }
    }

    /**
     * Zip symKeySpec.
     */
    protected static final class ZipSymKeySpec
            extends ZipStreamSpec {
        /**
         * SymKeyType.
         */
        private final SymKeyType theKeyType;

        /**
         * CipherMode.
         */
        private final CipherMode theCipherMode;

        /**
         * KeySpec.
         */
        private final byte[] theKeySpec;

        /**
         * Initialisation Vector.
         */
        private final byte[] theInitVector;

        /**
         * Constructor.
         * @param pStream the EncryptionOutputStream
         * @param pCipherSet the CipherSet
         * @throws JOceanusException on error
         */
        private ZipSymKeySpec(final EncryptionOutputStream pStream,
                              final CipherSet pCipherSet) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.SYMMETRIC);

            /* Store details */
            theKeyType = pStream.getSymKeyType();
            theCipherMode = pStream.getCipherMode();
            theKeySpec = pCipherSet.secureSymmetricKey(pStream.getSymKey());
            theInitVector = pStream.getInitVector();
        }

        /**
         * Constructor.
         * @param pProperties the Properties
         * @param pIndex the index
         * @param pEncodedId the EncodedId
         * @throws JOceanusException on error
         */
        private ZipSymKeySpec(final ZipFileProperties pProperties,
                              final int pIndex,
                              final long pEncodedId) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.SYMMETRIC);

            /* Determine the Id */
            long myId = pEncodedId >> DataConverter.NYBBLE_SHIFT;
            long myModeId = pEncodedId
                            & DataConverter.NYBBLE_MASK;

            /* Store details */
            theKeyType = SymKeyType.fromId((int) myId);
            theCipherMode = CipherMode.fromId((int) myModeId);
            theKeySpec = pProperties.getByteProperty(PROP_TYPE
                                                     + pIndex);
            theInitVector = pProperties.getByteProperty(PROP_VALUE
                                                        + pIndex);
        }

        @Override
        protected void allocateProperties(final int pIndex,
                                          final ZipFileProperties pProperties) {
            /* Determine the Id */
            int myId = theKeyType.getId() << DataConverter.NYBBLE_SHIFT;
            myId += theCipherMode.getId();

            /* Set the Stream Properties */
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, getEncoded(myId));
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, theKeySpec);
            pProperties.setProperty(PROP_VALUE
                                    + pIndex, theInitVector);
        }

        @Override
        protected void updateSignature(final Signature pSignature) throws SignatureException {
            /* Update bytes */
            pSignature.update(theKeySpec);
            pSignature.update(theInitVector);
        }

        @Override
        protected InputStream buildInputStream(final InputStream pCurrent,
                                               final CipherSet pCipherSet) throws JOceanusException {
            /* Create the decryption stream */
            SymmetricKey myKey = pCipherSet.deriveSymmetricKey(theKeySpec, theKeyType);
            return new DecryptionInputStream(myKey, theCipherMode, theInitVector, pCurrent);
        }
    }

    /**
     * Zip streamKeySpec.
     */
    protected static final class ZipStreamKeySpec
            extends ZipStreamSpec {
        /**
         * StreamKeyType.
         */
        private final StreamKeyType theKeyType;

        /**
         * KeySpec.
         */
        private final byte[] theKeySpec;

        /**
         * Initialisation Vector.
         */
        private final byte[] theInitVector;

        /**
         * Constructor.
         * @param pStream the EncryptionOutputStream
         * @param pCipherSet the CipherSet
         * @throws JOceanusException on error
         */
        private ZipStreamKeySpec(final EncryptionOutputStream pStream,
                                 final CipherSet pCipherSet) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.STREAM);

            /* Store details */
            theKeyType = pStream.getStreamKeyType();
            theKeySpec = pCipherSet.secureStreamKey(pStream.getStreamKey());
            theInitVector = pStream.getInitVector();
        }

        /**
         * Constructor.
         * @param pProperties the Properties
         * @param pIndex the index
         * @param pEncodedId the EncodedId
         * @throws JOceanusException on error
         */
        private ZipStreamKeySpec(final ZipFileProperties pProperties,
                                 final int pIndex,
                                 final long pEncodedId) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.STREAM);

            /* Store details */
            theKeyType = StreamKeyType.fromId((int) pEncodedId);
            theKeySpec = pProperties.getByteProperty(PROP_TYPE
                                                     + pIndex);
            theInitVector = pProperties.getByteProperty(PROP_VALUE
                                                        + pIndex);
        }

        @Override
        protected void allocateProperties(final int pIndex,
                                          final ZipFileProperties pProperties) {
            /* Set the Stream Properties */
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, getEncoded(theKeyType.getId()));
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, theKeySpec);
            pProperties.setProperty(PROP_VALUE
                                    + pIndex, theInitVector);
        }

        @Override
        protected void updateSignature(final Signature pSignature) throws SignatureException {
            /* Update bytes */
            pSignature.update(theKeySpec);
            pSignature.update(theInitVector);
        }

        @Override
        protected InputStream buildInputStream(final InputStream pCurrent,
                                               final CipherSet pCipherSet) throws JOceanusException {
            /* Create the decryption stream */
            StreamKey myKey = pCipherSet.deriveStreamKey(theKeySpec, theKeyType);
            return new DecryptionInputStream(myKey, theInitVector, pCurrent);
        }
    }

    /**
     * Zip digestSpec.
     */
    protected static final class ZipDigestSpec
            extends ZipStreamSpec {
        /**
         * DigestType.
         */
        private final DigestType theDigestType;

        /**
         * Digest.
         */
        private final byte[] theDigest;

        /**
         * Constructor.
         * @param pStream the DigestOutputStream
         */
        private ZipDigestSpec(final DigestOutputStream pStream) {
            /* Note type of stream */
            super(ZipStreamType.DIGEST);

            /* Store details */
            theDigestType = pStream.getDigestType();
            theDigest = pStream.getDigest();
        }

        /**
         * Constructor.
         * @param pProperties the Properties
         * @param pIndex the index
         * @param pEncodedId the EncodedId
         * @throws JOceanusException on error
         */
        private ZipDigestSpec(final ZipFileProperties pProperties,
                              final int pIndex,
                              final long pEncodedId) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.DIGEST);

            /* Store details */
            theDigestType = DigestType.fromId((int) pEncodedId);
            theDigest = pProperties.getByteProperty(PROP_VALUE
                                                    + pIndex);
        }

        @Override
        protected void allocateProperties(final int pIndex,
                                          final ZipFileProperties pProperties) {
            /* Set the Stream Properties */
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, getEncoded(theDigestType.getId()));
            pProperties.setProperty(PROP_VALUE
                                    + pIndex, theDigest);
        }

        @Override
        protected void updateSignature(final Signature pSignature) throws SignatureException {
            /* Update bytes */
            pSignature.update(theDigest);
        }

        @Override
        protected InputStream buildInputStream(final InputStream pCurrent,
                                               final CipherSet pCipherSet) throws JOceanusException {
            /* Create the digest stream */
            SecurityGenerator myGenerator = pCipherSet.getSecurityGenerator();
            DataDigest myDigest = myGenerator.generateDigest(theDigestType);
            return new DigestInputStream(myDigest, theDigest, pCurrent);
        }
    }

    /**
     * Zip macSpec.
     */
    protected static final class ZipMacSpec
            extends ZipStreamSpec {
        /**
         * MacSpec.
         */
        private final MacSpec theMacSpec;

        /**
         * KeySpec.
         */
        private final byte[] theKeySpec;

        /**
         * AuthenticationCode.
         */
        private final byte[] theAuthCode;

        /**
         * Constructor.
         * @param pStream the DigestOutputStream
         * @param pCipherSet the CipherSet
         * @throws JOceanusException on error
         */
        private ZipMacSpec(final MacOutputStream pStream,
                           final CipherSet pCipherSet) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.MAC);

            /* Store details */
            theMacSpec = pStream.getMacSpec();
            theKeySpec = pCipherSet.secureDataMac(pStream.getDataMac());
            theAuthCode = pStream.getAuthCode();
        }

        /**
         * Constructor.
         * @param pProperties the Properties
         * @param pIndex the index
         * @param pEncodedId the EncodedId
         * @throws JOceanusException on error
         */
        private ZipMacSpec(final ZipFileProperties pProperties,
                           final int pIndex,
                           final long pEncodedId) throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.MAC);

            /* Store details */
            theMacSpec = new MacSpec((int) pEncodedId);
            theKeySpec = pProperties.getByteProperty(PROP_TYPE
                                                     + pIndex);
            theAuthCode = pProperties.getByteProperty(PROP_VALUE
                                                      + pIndex);
        }

        @Override
        protected void allocateProperties(final int pIndex,
                                          final ZipFileProperties pProperties) {
            /* Set the Stream Properties */
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, getEncoded(theMacSpec.getEncoded()));
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, theKeySpec);
            pProperties.setProperty(PROP_VALUE
                                    + pIndex, theAuthCode);
        }

        @Override
        protected void updateSignature(final Signature pSignature) throws SignatureException {
            /* Update bytes */
            pSignature.update(theKeySpec);
            pSignature.update(theAuthCode);
        }

        @Override
        protected InputStream buildInputStream(final InputStream pCurrent,
                                               final CipherSet pCipherSet) throws JOceanusException {
            /* Create the Mac stream */
            DataMac myMac = pCipherSet.deriveDataMac(theKeySpec, theMacSpec);
            return new MacInputStream(myMac, theAuthCode, pCurrent);
        }
    }

    /**
     * Zip LZMASpec.
     */
    protected static final class ZipLZMASpec
            extends ZipStreamSpec {
        /**
         * Constructor.
         * @throws JOceanusException on error
         */
        private ZipLZMASpec() throws JOceanusException {
            /* Note type of stream */
            super(ZipStreamType.LZMA);
        }

        @Override
        protected void allocateProperties(final int pIndex,
                                          final ZipFileProperties pProperties) {
            /* Set the Stream Properties */
            pProperties.setProperty(PROP_TYPE
                                    + pIndex, getEncoded(0));
        }

        @Override
        protected void updateSignature(final Signature pSignature) throws SignatureException {
            /* Nothing to do */
        }

        @Override
        protected InputStream buildInputStream(final InputStream pCurrent,
                                               final CipherSet pCipherSet) throws JOceanusException {
            /* Create the decryption stream */
            return new LZMAInputStream(pCurrent);
        }
    }

    /**
     * Stream Type.
     */
    protected enum ZipStreamType {
        /**
         * Symmetric.
         */
        SYMMETRIC(1),

        /**
         * Stream.
         */
        STREAM(2),

        /**
         * DIGEST.
         */
        DIGEST(3),

        /**
         * MAC.
         */
        MAC(4),

        /**
         * LZMA.
         */
        LZMA(5);

        /**
         * The external Id of the stream type.
         */
        private final int theId;

        /**
         * Obtain the external Id.
         * @return the external Id
         */
        public int getId() {
            return theId;
        }

        /**
         * Constructor.
         * @param id the id
         */
        private ZipStreamType(final int id) {
            theId = id;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enumeration object
         * @throws JOceanusException on error
         */
        public static ZipStreamType fromId(final int id) throws JOceanusException {
            for (ZipStreamType myType : values()) {
                if (myType.getId() == id) {
                    return myType;
                }
            }
            throw new JOceanusException("Invalid ZipStreamType: "
                                        + id);
        }
    }
}
