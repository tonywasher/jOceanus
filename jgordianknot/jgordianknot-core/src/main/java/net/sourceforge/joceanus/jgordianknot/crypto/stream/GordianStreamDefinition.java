/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.InputStream;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Stream definition.
 */
public final class GordianStreamDefinition {
    /**
     * Stream Type.
     */
    private final StreamType theType;

    /**
     * TypeId.
     */
    private final long theTypeId;

    /**
     * TypeDefinition.
     */
    private final byte[] theTypeDefinition;

    /**
     * InitVector.
     */
    private final byte[] theInitVector;

    /**
     * Value.
     */
    private final byte[] theValue;

    /**
     * The length.
     */
    private final Long theLength;

    /**
     * Constructor.
     * @param pExternalId the externalId
     * @param pTypeDef the type definition
     * @param pInitVector the IV
     * @param pValue the value
     * @throws OceanusException on error
     */
    public GordianStreamDefinition(final long pExternalId,
                                   final byte[] pTypeDef,
                                   final byte[] pInitVector,
                                   final byte[] pValue) throws OceanusException {
        int myStreamId = (int) (pExternalId & TethysDataConverter.NYBBLE_MASK);
        theType = StreamType.fromId(myStreamId);
        theTypeId = pExternalId >> TethysDataConverter.NYBBLE_SHIFT;
        theTypeDefinition = pTypeDef;
        theInitVector = pInitVector;
        theValue = pValue;
        theLength = null;
    }

    /**
     * Constructor.
     * @param pKeySet the KeySet
     * @param pStream the DigestOutputStream
     * @throws OceanusException on error
     */
    protected GordianStreamDefinition(final GordianKeySet pKeySet,
                                      final GordianDigestOutputStream pStream) throws OceanusException {
        theType = StreamType.DIGEST;
        GordianDigest myDigest = pStream.getDigest();
        theTypeId = pKeySet.deriveExternalIdForType(myDigest.getDigestSpec());
        theTypeDefinition = null;
        theInitVector = null;
        theValue = pStream.getResult();
        theLength = pStream.getDataLen();
    }

    /**
     * Constructor.
     * @param pKeySet the KeySet
     * @param pStream the MacOutputStream
     * @throws OceanusException on error
     */
    protected GordianStreamDefinition(final GordianKeySet pKeySet,
                                      final GordianMacOutputStream pStream) throws OceanusException {
        theType = StreamType.MAC;
        GordianMac myMac = pStream.getMac();
        theTypeId = pKeySet.deriveExternalIdForType(myMac.getMacSpec());
        GordianFactory myFactory = pKeySet.getFactory();
        GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(myMac.getMacSpec());
        theTypeDefinition = myGenerator.secureKey(myMac.getKey(), pKeySet);
        theInitVector = myMac.getInitVector();
        theValue = pStream.getResult();
        theLength = pStream.getDataLen();
    }

    /**
     * Constructor.
     * @param pKeySet the KeySet
     * @param pStream the CipherOutputStream
     * @throws OceanusException on error
     */
    protected GordianStreamDefinition(final GordianKeySet pKeySet,
                                      final GordianCipherOutputStream<?> pStream) throws OceanusException {
        GordianCipher<?> myCipher = pStream.getCipher();
        GordianFactory myFactory = pKeySet.getFactory();
        theType = pStream.isSymKeyStream()
                                           ? StreamType.SYMMETRIC
                                           : StreamType.STREAM;
        theTypeId = pKeySet.deriveExternalIdForType(myCipher.getCipherSpec());
        GordianKeyGenerator<?> myGenerator = myFactory.getKeyGenerator(myCipher.getKeyType());
        theTypeDefinition = myGenerator.secureKey(myCipher.getKey(), pKeySet);
        theInitVector = myCipher.getInitVector();
        theValue = null;
        theLength = null;
    }

    /**
     * Constructor.
     * @param pStream the LZMAOutputStream
     * @throws OceanusException on error
     */
    protected GordianStreamDefinition(final GordianLZMAOutputStream pStream) throws OceanusException {
        theType = StreamType.LZMA;
        theTypeId = 0;
        theTypeDefinition = null;
        theInitVector = null;
        theValue = null;
        theLength = null;
    }

    /**
     * Obtain Encoded stream type.
     * @return the encoded value
     */
    public long getExternalId() {
        return (theTypeId << TethysDataConverter.NYBBLE_SHIFT)
               + theType.getId();
    }

    /**
     * Obtain Type.
     * @return the type
     */
    public StreamType getType() {
        return theType;
    }

    /**
     * Obtain TypeId.
     * @return the typeId
     */
    public long getTypeId() {
        return theTypeId;
    }

    /**
     * Obtain TypeDefinition.
     * @return the type definition
     */
    public byte[] getTypeDefinition() {
        return theTypeDefinition;
    }

    /**
     * Obtain InitVector.
     * @return the initVector
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Obtain Value.
     * @return the value
     */
    public byte[] getValue() {
        return theValue;
    }

    /**
     * Obtain dataLength.
     * @return the length
     */
    public Long getDataLength() {
        return theLength;
    }

    /**
     * Build input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    protected InputStream buildInputStream(final GordianKeySet pKeySet,
                                           final InputStream pCurrent) throws OceanusException {
        switch (theType) {
            case DIGEST:
                return buildDigestInputStream(pKeySet, pCurrent);
            case MAC:
                return buildMacInputStream(pKeySet, pCurrent);
            case SYMMETRIC:
                return buildSymKeyInputStream(pKeySet, pCurrent);
            case STREAM:
                return buildStreamKeyInputStream(pKeySet, pCurrent);
            case LZMA:
            default:
                return buildLZMAInputStream(pCurrent);
        }
    }

    /**
     * Build digest input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildDigestInputStream(final GordianKeySet pKeySet,
                                               final InputStream pCurrent) throws OceanusException {
        /* Parse the TypeId */
        GordianDigestSpec mySpec = pKeySet.deriveTypeFromExternalId(theTypeId, GordianDigestSpec.class);

        /* Generate the Digest */
        GordianFactory myFactory = pKeySet.getFactory();
        GordianDigest myDigest = myFactory.createDigest(mySpec);

        /* Create the stream */
        return new GordianDigestInputStream(myDigest, theValue, pCurrent);
    }

    /**
     * Build MAC input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildMacInputStream(final GordianKeySet pKeySet,
                                            final InputStream pCurrent) throws OceanusException {
        /* Parse the TypeId */
        GordianMacSpec mySpec = pKeySet.deriveTypeFromExternalId(theTypeId, GordianMacSpec.class);

        /* Generate the MAC */
        GordianFactory myFactory = pKeySet.getFactory();
        GordianMac myMac = myFactory.createMac(mySpec);
        GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(mySpec);
        GordianKey<GordianMacSpec> myKey = myGenerator.deriveKey(theTypeDefinition, pKeySet);
        myMac.initMac(myKey, theInitVector);

        /* Create the stream */
        return new GordianMacInputStream(myMac, theValue, pCurrent);
    }

    /**
     * Build SymKey input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildSymKeyInputStream(final GordianKeySet pKeySet,
                                               final InputStream pCurrent) throws OceanusException {
        /* Parse the TypeId */
        GordianSymCipherSpec mySpec = pKeySet.deriveTypeFromExternalId(theTypeId, GordianSymCipherSpec.class);
        GordianSymKeyType myType = mySpec.getKeyType();

        /* Generate the Cipher */
        GordianFactory myFactory = pKeySet.getFactory();
        GordianCipher<GordianSymKeyType> myCipher = myFactory.createSymKeyCipher(mySpec);
        GordianKeyGenerator<GordianSymKeyType> myGenerator = myFactory.getKeyGenerator(myType);
        GordianKey<GordianSymKeyType> myKey = myGenerator.deriveKey(theTypeDefinition, pKeySet);
        myCipher.initCipher(myKey, theInitVector, false);

        /* Create the stream */
        return new GordianCipherInputStream<GordianSymKeyType>(myCipher, pCurrent);
    }

    /**
     * Build StreamKey input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildStreamKeyInputStream(final GordianKeySet pKeySet,
                                                  final InputStream pCurrent) throws OceanusException {
        /* Parse the TypeId */
        GordianStreamCipherSpec mySpec = pKeySet.deriveTypeFromExternalId(theTypeId, GordianStreamCipherSpec.class);
        GordianStreamKeyType myType = mySpec.getKeyType();

        /* Generate the Cipher */
        GordianFactory myFactory = pKeySet.getFactory();
        GordianCipher<GordianStreamKeyType> myCipher = myFactory.createStreamKeyCipher(mySpec);
        GordianKeyGenerator<GordianStreamKeyType> myGenerator = myFactory.getKeyGenerator(myType);
        GordianKey<GordianStreamKeyType> myKey = myGenerator.deriveKey(theTypeDefinition, pKeySet);
        myCipher.initCipher(myKey, theInitVector, false);

        /* Create the stream */
        return new GordianCipherInputStream<GordianStreamKeyType>(myCipher, pCurrent);
    }

    /**
     * Build LZMA input Stream.
     * @param pCurrent the current stream
     * @return the new input stream
     */
    private static InputStream buildLZMAInputStream(final InputStream pCurrent) {
        /* Create the stream */
        return new GordianLZMAInputStream(pCurrent);
    }

    /**
     * Stream Type.
     */
    public enum StreamType {
        /**
         * Symmetric.
         */
        SYMMETRIC(1),

        /**
         * Stream.
         */
        STREAM(2),

        /**
         * Digest.
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
         * Constructor.
         * @param id the id
         */
        StreamType(final int id) {
            theId = id;
        }

        /**
         * Obtain the external Id.
         * @return the external Id
         */
        public int getId() {
            return theId;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enumeration object
         * @throws OceanusException on error
         */
        public static StreamType fromId(final int id) throws OceanusException {
            for (StreamType myType : values()) {
                if (myType.getId() == id) {
                    return myType;
                }
            }
            throw new GordianDataException("Invalid StreamType: "
                                           + id);
        }
    }
}
