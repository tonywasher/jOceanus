/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianBaseKeyPair;
import org.bouncycastle.crypto.params.MLDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;

import java.util.Objects;

/**
 * BouncyCastle Hybrid keyPair.
 */
public class BouncyHybridKeyPair
        implements GordianBaseKeyPair {
    /**
     * The KeySpec.
     */
    private final GordianKeyPairSpec theSpec;

    /**
     * Primary keyPair.
     */
    private final BouncyKeyPair thePrimary;

    /**
     * Traditional keyPair.
     */
    private final BouncyKeyPair theTraditional;

    /**
     * is the keyPair public only?
     */
    private final boolean isPublicOnly;

    /**
     * Is the keyPair destroyed?
     */
    private volatile boolean isDestroyed;

    /**
     * Constructor.
     *
     * @param pSpec        the keyPairSpec.
     * @param pPrimary     the primary keyPair.
     * @param pTraditional the traditional keyPair
     */
    BouncyHybridKeyPair(final GordianKeyPairSpec pSpec,
                        final BouncyKeyPair pPrimary,
                        final BouncyKeyPair pTraditional) {
        theSpec = pSpec;
        thePrimary = pPrimary;
        theTraditional = pTraditional;
        isPublicOnly = pPrimary.isPublicOnly();
    }

    @Override
    public GordianKeyPairSpec getKeyPairSpec() {
        return theSpec;
    }

    @Override
    public boolean isPublicOnly() {
        return isPublicOnly;
    }

    /**
     * Obtain the primary keyPair.
     *
     * @return the primary keyPair
     */
    public BouncyKeyPair getPrimary() {
        return thePrimary;
    }

    /**
     * Obtain the traditional keyPair.
     *
     * @return the traditional keyPair
     */
    public BouncyKeyPair getTraditional() {
        return theTraditional;
    }

    /**
     * Obtain the primary seed.
     *
     * @return the seed
     * @throws GordianException on error
     */
    byte[] getPrimarySeed() throws GordianException {
        return switch (thePrimary.getKeyPairSpec().getKeyPairType()) {
            case MLDSA -> ((MLDSAPrivateKeyParameters) thePrimary.getPrivateKey().getPrivateKey()).getSeed();
            case MLKEM -> ((MLKEMPrivateKeyParameters) thePrimary.getPrivateKey().getPrivateKey()).getSeed();
            default -> throw new GordianLogicException("Invalid Primary keySpec");
        };
    }

    /**
     * Check for bouncyHybridKeyPair.
     *
     * @param pKeyPair the keyPair to check
     * @return the keyPair
     * @throws GordianException on error
     */
    public static BouncyHybridKeyPair checkKeyPair(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check that it is a BouncyHybridKeyPair */
        if (!(pKeyPair instanceof BouncyHybridKeyPair myPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }
        myPair.checkForDestroyedKeyPair();
        return myPair;
    }

    /**
     * Check for bouncyHybridKeyPair.
     *
     * @param pKeyPair the keyPair to check
     * @param pSpec    the required keySpec
     * @throws GordianException on error
     */
    public static void checkKeyPair(final GordianKeyPair pKeyPair,
                                    final GordianKeyPairSpec pSpec) throws GordianException {
        /* Check the keyPair */
        checkKeyPair(pKeyPair);

        /* Check that it the correct key type */
        if (!pSpec.equals(pKeyPair.getKeyPairSpec())) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPairType");
        }
    }

    @Override
    public BouncyHybridKeyPair getPublicOnly() {
        final BouncyKeyPair myPrimary = getPrimary().getPublicOnly();
        final BouncyKeyPair myTraditional = getTraditional().getPublicOnly();
        return new BouncyHybridKeyPair(getKeyPairSpec(), myPrimary, myTraditional);
    }

    @Override
    public boolean checkMatchingPublicKey(final GordianKeyPair pPair) {
        /* Must be composite and matching spec */
        if (!(pPair instanceof BouncyHybridKeyPair myHybrid)
                || !theSpec.equals(pPair.getKeyPairSpec())) {
            return false;
        }

        /* Check primary and Traditional */
        return thePrimary.checkMatchingPublicKey(myHybrid.getPrimary())
                && theTraditional.checkMatchingPublicKey(myHybrid.getTraditional());
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    @Override
    public boolean isClearable() {
        return thePrimary.isClearable() && theTraditional.isClearable();
    }

    @Override
    public synchronized void destroy() throws GordianException {
        isDestroyed = true;
        thePrimary.destroy();
        theTraditional.destroy();
    }

    @Override
    public void checkForDestroyed(final String pName) throws GordianException {
        if (isDestroyed) {
            throw new GordianLogicException(pName + " has been destroyed");
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check object is same class */
        if (!(pThat instanceof BouncyHybridKeyPair myThat)) {
            return false;
        }
        return Objects.equals(theSpec, myThat.getKeyPairSpec())
                && Objects.equals(thePrimary, myThat.getPrimary())
                && Objects.equals(theTraditional, myThat.getTraditional());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSpec, thePrimary, theTraditional);
    }
}
