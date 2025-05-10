/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.security.SecureRandom;

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
     * The Context.
     */
    private byte[] theContext;

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
     * Obtain the context.
     * @return the context
     */
    protected byte[] getContext() {
        return theContext;
    }

    /**
     * Check that the KeyPair is the correct type.
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    private void checkKeyPair(final GordianKeyPair pKeyPair) throws GordianException {
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianSignatureFactory mySigns = myFactory.getSignatureFactory();
        if (!mySigns.validSignatureSpecForKeyPair(pKeyPair, theSpec)) {
            throw new GordianDataException("Incorrect KeyPair type");
        }
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Store details */
        theMode = GordianSignatureMode.SIGN;
        theKeyPair = pParams.getKeyPair();
        theContext = pParams.getContext();

        /* Check that the keyPair matches */
        checkKeyPair(theKeyPair);

        /* Check that we have the private key */
        if (theKeyPair.isPublicOnly()) {
            throw new GordianDataException("Missing privateKey");
        }
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Store details */
        theMode = GordianSignatureMode.VERIFY;
        theKeyPair = pParams.getKeyPair();
        theContext = pParams.getContext();

        /* Check that the keyPair matches */
        checkKeyPair(theKeyPair);
    }

    /**
     * Check that we are in the correct mode.
     * @param pMode the required mode
     * @throws GordianException on error
     */
    protected void checkMode(final GordianSignatureMode pMode) throws GordianException {
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
