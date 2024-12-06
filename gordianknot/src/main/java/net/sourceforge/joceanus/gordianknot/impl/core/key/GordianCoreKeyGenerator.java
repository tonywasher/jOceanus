/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.key;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFMulti;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFParams;
import net.sourceforge.joceanus.oceanus.OceanusDataConverter;
import net.sourceforge.joceanus.oceanus.OceanusException;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * GordianKnot interface for Key Generators.
 * @param <T> the keyType
 */
public abstract class GordianCoreKeyGenerator<T extends GordianKeySpec>
    implements GordianKeyGenerator<T> {
    /**
     * The Key Type.
     */
    private final T theKeyType;

    /**
     * The Key Length.
     */
    private final int theKeyLength;

    /**
     * The Security Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Random Source.
     */
    private final GordianRandomSource theRandom;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     */
    protected GordianCoreKeyGenerator(final GordianCoreFactory pFactory,
                                      final T pKeyType) {
        /* Store parameters */
        theKeyType = pKeyType;
        theFactory = pFactory;

        /* Cache some values */
        theKeyLength = pKeyType.getKeyLength().getLength();
        theRandom = pFactory.getRandomSource();
    }

    @Override
    public T getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandom.getRandom();
    }

    /**
     * Obtain factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Generate a new Key.
     * @param pBytes the bytes for the key.
     * @return the new Key
     */
    public abstract GordianKey<T> buildKeyFromBytes(byte[] pBytes);

    /**
     * Generate a Key from a Secret.
     * @param pSecret the derived Secret
     * @param pSeededRandom the deterministic random
     * @return the new Secret Key
     * @throws OceanusException on error
     */
    public GordianKey<T> generateKeyFromSecret(final byte[] pSecret,
                                               final Random pSeededRandom) throws OceanusException {
        /* Determine the key length in bytes */
        final int myKeyLen = theKeyLength
                / Byte.SIZE;

        /* Create data that will be cleared */
        byte[] myKeyBytes = null;
        GordianHKDFParams myParams = null;

        /* Derive the two digestTypes from the seededRandom */
        final GordianDigestType[] myDigestTypes = theFactory.getIdManager().deriveKeyGenDigestTypesFromSeed(pSeededRandom, 2);

        /* Determine info bytes */
        final byte[] myAlgo = OceanusDataConverter.stringToByteArray(theKeyType.toString());
        final byte[] myKeyLenBytes = OceanusDataConverter.integerToByteArray(theKeyLength);
        final byte[] mySeed = new byte[Long.BYTES];
        pSeededRandom.nextBytes(mySeed);

        /* Protect against exceptions */
        try {
            /* Create the HKDF and parameters */
            final GordianHKDFMulti myEngine = new GordianHKDFMulti(theFactory,
                    new GordianDigestSpec(myDigestTypes[0], GordianLength.LEN_512),
                    new GordianDigestSpec(myDigestTypes[1], GordianLength.LEN_512));
            myParams = GordianHKDFParams.expandOnly(pSecret, myKeyLen)
                    .withInfo(myAlgo).withInfo(myKeyLenBytes).withInfo(mySeed);
            theFactory.getPersonalisation().updateInfo(myParams);
            myKeyBytes = myEngine.deriveBytes(myParams);

            /* Return the new key */
            return buildKeyFromBytes(myKeyBytes);

            /* Clear build buffer */
        } finally {
            if (myKeyBytes != null) {
                Arrays.fill(myKeyBytes, (byte) 0);
            }
            if (myParams != null) {
                myParams.clearParameters();
            }
        }
    }

    @Override
    public <X extends GordianKeySpec> GordianKey<T> translateKey(final GordianKey<X> pSource) throws OceanusException {
        /* Check that the keyLengths are compatible */
        if (pSource.getKeyType().getKeyLength() != theKeyType.getKeyLength()) {
            throw new GordianDataException("Incorrect length for key");
        }

        /* Build the key */
        final GordianCoreKey<X> mySource = (GordianCoreKey<X>) pSource;
        return buildKeyFromBytes(mySource.getKeyBytes());
    }

    /**
     * Init Mac keyBytes.
     * @param pMac the Mac.
     * @param pKeyBytes the keyBytes
     * @throws OceanusException on error
     */
    public abstract void initMacKeyBytes(GordianMac pMac, byte[] pKeyBytes) throws OceanusException;
}
