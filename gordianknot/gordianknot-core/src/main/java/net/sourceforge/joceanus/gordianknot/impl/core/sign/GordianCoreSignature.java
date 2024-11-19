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
package net.sourceforge.joceanus.gordianknot.impl.core.sign;

import java.security.SecureRandom;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * GordianKnot base for signature.
 */
public abstract class GordianCoreSignature
        implements GordianSignature {
    /**
     * The Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Signature Spec.
     */
    private final GordianSignatureSpec theSpec;

    /**
     * Signature Mode.
     */
    private GordianSignatureMode theMode;

    /**
     * The KeyPair.
     */
    private GordianKeyPair theKeyPair;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the signature Spec
     */
    protected GordianCoreSignature(final GordianCoreFactory pFactory,
                                   final GordianSignatureSpec pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the digest factory.
     * @return the digest factory
     */
    public GordianCoreDigestFactory getDigestFactory() {
        return (GordianCoreDigestFactory) theFactory.getDigestFactory();
    }

    /**
     * Obtain the random.
     * @return the random
     */
    public SecureRandom getRandom() {
        return theFactory.getRandomSource().getRandom();
    }

    @Override
    public GordianSignatureSpec getSignatureSpec() {
        return theSpec;
    }

    /**
     * Obtain the keyPair.
     * @return the keyPair
     */
    protected GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    /**
     * Check that the KeyPair is the correct type.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianSignatureFactory mySigns = myFactory.getSignatureFactory();
        if (!mySigns.validSignatureSpecForKeyPair(pKeyPair, theSpec)) {
            throw new GordianDataException("Incorrect KeyPair type");
        }
    }

    @Override
    public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Check that we have the private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Missing privateKey");
        }

        /* Store details */
        theMode = GordianSignatureMode.SIGN;
        theKeyPair = pKeyPair;
    }

    @Override
    public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Store details */
        theMode = GordianSignatureMode.VERIFY;
        theKeyPair = pKeyPair;
    }

    /**
     * Check that we are in the correct mode.
     * @param pMode the required mode
     * @throws OceanusException on error
     */
    protected void checkMode(final GordianSignatureMode pMode) throws OceanusException {
        if (!pMode.equals(theMode)) {
            throw new GordianDataException("Incorrect signature Mode");
        }
    }

    /**
     * SignatureMode.
     */
    public enum GordianSignatureMode {
        /**
         * Signing.
         */
        SIGN,

        /**
         * Verify.
         */
        VERIFY;
    }
}
