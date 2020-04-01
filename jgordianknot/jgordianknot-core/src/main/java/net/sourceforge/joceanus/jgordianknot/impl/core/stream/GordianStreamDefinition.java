/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.core.stream;

import java.io.InputStream;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
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
    private final int theTypeId;

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
        final int myStreamId = (int) (pExternalId & TethysDataConverter.NYBBLE_MASK);
        theType = StreamType.fromId(myStreamId);
        theTypeId = (int) (pExternalId >> TethysDataConverter.NYBBLE_SHIFT);
        theTypeDefinition = pTypeDef == null
                            ? null
                            : Arrays.copyOf(pTypeDef, pTypeDef.length);
        theInitVector = pInitVector == null
                        ? null
                        : Arrays.copyOf(pInitVector, pInitVector.length);
        theValue = pValue == null
                   ? null
                   : Arrays.copyOf(pValue, pValue.length);
        theLength = null;
    }

    /**
     * Constructor.
     * @param pKeySet the KeySet
     * @param pStream the DigestOutputStream
     * @throws OceanusException on error
     */
    GordianStreamDefinition(final GordianCoreKeySet pKeySet,
                            final GordianDigestOutputStream pStream) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Access DigestType */
        theType = StreamType.DIGEST;
        final GordianDigest myDigest = pStream.getDigest();
        theTypeId = myKnuth.deriveExternalIdFromType(myDigest.getDigestSpec());

        /* Build details */
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
    GordianStreamDefinition(final GordianCoreKeySet pKeySet,
                            final GordianMacOutputStream pStream) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Access MacType */
        theType = StreamType.MAC;
        final GordianMac myMac = pStream.getMac();
        theTypeId = myKnuth.deriveExternalIdFromType(myMac.getMacSpec());

        /* Build details */
        theTypeDefinition = pKeySet.secureKey(myMac.getKey());
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
    GordianStreamDefinition(final GordianCoreKeySet pKeySet,
                            final GordianCipherOutputStream<?> pStream) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Access CipherType */
        final GordianKeyedCipher<?> myCipher = pStream.getCipher();
        theType = pStream.isSymKeyStream()
                  ? StreamType.SYMMETRIC
                  : StreamType.STREAM;
        theTypeId = myKnuth.deriveExternalIdFromType(myCipher.getCipherSpec());

        /* Build details */
        theTypeDefinition = pKeySet.secureKey(myCipher.getKey());
        theInitVector = myCipher.getInitVector();
        theValue = null;
        theLength = null;
    }

    /**
     * Constructor.
     * @param pStream the LZMAOutputStream
     */
    GordianStreamDefinition(final GordianLZMAOutputStream pStream) {
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
        return ((long) theTypeId << TethysDataConverter.NYBBLE_SHIFT)
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
        return theTypeDefinition == null
               ? null
               : Arrays.copyOf(theTypeDefinition, theTypeDefinition.length);
    }

    /**
     * Obtain InitVector.
     * @return the initVector
     */
    public byte[] getInitVector() {
        return theInitVector == null
               ? null
               : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain Value.
     * @return the value
     */
    public byte[] getValue() {
        return theValue == null
               ? null
               : Arrays.copyOf(theValue, theValue.length);
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
     * @param pMacStream the Mac input stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    InputStream buildInputStream(final GordianCoreKeySet pKeySet,
                                 final InputStream pCurrent,
                                 final GordianMacInputStream pMacStream) throws OceanusException {
        switch (theType) {
            case DIGEST:
                return buildDigestInputStream(pKeySet, pCurrent, pMacStream);
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
     * @param pMacStream the Mac input stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildDigestInputStream(final GordianCoreKeySet pKeySet,
                                               final InputStream pCurrent,
                                               final GordianMacInputStream pMacStream) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Parse the TypeId */
        final GordianDigestSpec mySpec = (GordianDigestSpec) myKnuth.deriveTypeFromExternalId(theTypeId);

        /* Generate the Digest */
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigests.createDigest(mySpec);

        /* Create the stream */
        return new GordianDigestInputStream(myDigest, theValue, pCurrent, pMacStream);
    }

    /**
     * Build MAC input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildMacInputStream(final GordianCoreKeySet pKeySet,
                                            final InputStream pCurrent) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Parse the TypeId */
        final GordianMacSpec mySpec = (GordianMacSpec) myKnuth.deriveTypeFromExternalId(theTypeId);

        /* Generate the MAC */
        final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) myFactory.getMacFactory();
        final GordianMac myMac = myMacs.createMac(mySpec);
        final GordianKey<GordianMacSpec> myKey = pKeySet.deriveKey(theTypeDefinition, mySpec);
        myMac.init(GordianMacParameters.keyAndNonce(myKey, theInitVector));

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
    private InputStream buildSymKeyInputStream(final GordianCoreKeySet pKeySet,
                                               final InputStream pCurrent) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Parse the TypeId */
        final GordianSymCipherSpec mySpec = (GordianSymCipherSpec) myKnuth.deriveTypeFromExternalId(theTypeId);
        final GordianSymKeySpec myKeySpec = mySpec.getKeyType();

        /* Generate the Cipher */
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        final GordianSymCipher myCipher = myCiphers.createSymKeyCipher(mySpec);
        final GordianKey<GordianSymKeySpec> myKey = pKeySet.deriveKey(theTypeDefinition, myKeySpec);
        myCipher.init(false, GordianCipherParameters.keyAndNonce(myKey, theInitVector));

        /* Create the stream */
        return new GordianCipherInputStream<>(myCipher, pCurrent);
    }

    /**
     * Build StreamKey input Stream.
     * @param pKeySet the keySet
     * @param pCurrent the current stream
     * @return the new input stream
     * @throws OceanusException on error
     */
    private InputStream buildStreamKeyInputStream(final GordianCoreKeySet pKeySet,
                                                  final InputStream pCurrent) throws OceanusException {
        /* Access factory */
        final GordianCoreFactory myFactory = pKeySet.getFactory();
        final GordianCoreKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Parse the TypeId */
        final GordianStreamCipherSpec mySpec = (GordianStreamCipherSpec) myKnuth.deriveTypeFromExternalId(theTypeId);
        final GordianStreamKeySpec myType = mySpec.getKeyType();

        /* Generate the Cipher */
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        final GordianStreamCipher myCipher = myCiphers.createStreamKeyCipher(mySpec);
        final GordianKey<GordianStreamKeySpec> myKey = pKeySet.deriveKey(theTypeDefinition, myType);
        myCipher.init(false, GordianCipherParameters.keyAndNonce(myKey, theInitVector));

        /* Create the stream */
        return new GordianCipherInputStream<>(myCipher, pCurrent);
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
            for (final StreamType myType : values()) {
                if (myType.getId() == id) {
                    return myType;
                }
            }
            throw new GordianDataException("Invalid StreamType: "
                    + id);
        }
    }
}
