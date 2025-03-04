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
package net.sourceforge.joceanus.gordianknot.impl.core.keypair;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * GordianKnot class for KeyPair Generators.
 */
public abstract class GordianCoreKeyPairGenerator
        implements GordianKeyPairGenerator {
    /**
     * The KeySpec.
     */
    private final GordianKeyPairSpec theKeySpec;

    /**
     * The Security Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Random Generator.
     */
    private final GordianRandomSource theRandomSource;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     */
    protected GordianCoreKeyPairGenerator(final GordianCoreFactory pFactory,
                                          final GordianKeyPairSpec pKeySpec) {
        /* Store parameters */
        theKeySpec = pKeySpec;
        theFactory = pFactory;

        /* Cache some values */
        theRandomSource = pFactory.getRandomSource();
    }

    @Override
    public GordianKeyPairSpec getKeySpec() {
        return theKeySpec;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandomSource.getRandom();
    }

    /**
     * Obtain factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain public key from pair.
     * @param pKeyPair the keyPair
     * @return the public key
     */
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) {
        return ((GordianCoreKeyPair) pKeyPair).getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @param pKeyPair the keyPair
     * @return the private key
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) {
        return ((GordianCoreKeyPair) pKeyPair).getPrivateKey();
    }

    /**
     * Check keySpec.
     * @param pKeySpec the keySpec.
     * @throws GordianException on error
     */
    protected void checkKeySpec(final PKCS8EncodedKeySpec pKeySpec) throws GordianException {
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(pKeySpec);
        if (!theKeySpec.equals(myKeySpec)) {
            throw new GordianDataException("KeySpec not supported by this KeyPairGenerator");
        }
    }

    /**
     * Check keySpec.
     * @param pKeySpec the keySpec.
     * @throws GordianException on error
     */
    protected void checkKeySpec(final X509EncodedKeySpec pKeySpec) throws GordianException {
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(pKeySpec);
        if (!theKeySpec.equals(myKeySpec)) {
            throw new GordianDataException("KeySpec not supported by this KeyPairGenerator");
        }
    }
}
