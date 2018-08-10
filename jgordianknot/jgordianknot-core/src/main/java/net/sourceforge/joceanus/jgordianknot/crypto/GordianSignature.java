/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for signature.
 */
public abstract class GordianSignature
        implements GordianConsumer {
    /**
     * The Factory.
     */
    private final GordianFactory theFactory;

    /**
     * The Signature Spec.
     */
    private final GordianSignatureSpec theSpec;

    /**
     * The random generator.
     */
    private final SecureRandom theRandom;

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
    protected GordianSignature(final GordianFactory pFactory,
                               final GordianSignatureSpec pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
        theRandom = theFactory.getRandom();
    }

    /**
     * Obtain the signatureSpec.
     * @return the Spec
     */
    public GordianSignatureSpec getSignatureSpec() {
        return theSpec;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the randomGenerator.
     * @return the random
     */
    public SecureRandom getRandom() {
        return theRandom;
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
        if (!theFactory.validSignatureSpecForKeyPair(pKeyPair, theSpec)) {
            throw new GordianDataException("Incorrect KeyPair type");
        }
    }

    /**
     * Initialise for signature.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Store details */
        theMode = GordianSignatureMode.SIGN;
        theKeyPair = pKeyPair;
    }

    /**
     * Initialise for verify.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
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
     * Complete the signature operation and return the signature bytes.
     * @return the signature
     * @throws OceanusException on error
     */
    public abstract byte[] sign() throws OceanusException;

    /**
     * Verify the signature against the supplied signature bytes.
     * @param pSignature the supplied signature
     * @return the signature
     * @throws OceanusException on error
     */
    public abstract boolean verify(byte[] pSignature) throws OceanusException;

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
