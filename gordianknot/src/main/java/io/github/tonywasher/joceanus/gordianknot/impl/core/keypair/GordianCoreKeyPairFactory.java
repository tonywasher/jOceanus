/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * GordianKnot Core KeyPairFactory.
 */
public abstract class GordianCoreKeyPairFactory
        implements GordianKeyPairFactory {
    /**
     * KeyPairAlgId.
     */
    private static final GordianKeyPairAlgId KEYPAIR_ALG_ID = new GordianKeyPairAlgId();

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianNewKeyPairSpec, GordianCompositeKeyPairGenerator> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreKeyPairFactory(final GordianBaseFactory pFactory) {
        theCache = new HashMap<>();
    }

    @Override
    public GordianNewKeyPairSpecBuilder newKeyPairSpecBuilder() {
        return GordianCoreKeyPairSpecBuilder.newInstance();
    }

    @Override
    public GordianKeyPairGenerator getKeyPairGenerator(final GordianNewKeyPairSpec pKeySpec) throws GordianException {
        /* Look up in the cache */
        GordianCompositeKeyPairGenerator myGenerator = theCache.get(pKeySpec);
        if (myGenerator == null) {
            /* Check the keySpec */
            checkAsymKeySpec(pKeySpec);

            /* Create the new generator */
            myGenerator = new GordianCompositeKeyPairGenerator(this, (GordianCoreKeyPairSpec) pKeySpec);

            /* Add to cache */
            theCache.put(pKeySpec, myGenerator);
        }
        return myGenerator;
    }

    @Override
    public GordianNewKeyPairSpec determineKeyPairSpec(final PKCS8EncodedKeySpec pEncoded) throws GordianException {
        return KEYPAIR_ALG_ID.determineKeyPairSpec(pEncoded);
    }

    @Override
    public GordianNewKeyPairSpec determineKeyPairSpec(final X509EncodedKeySpec pEncoded) throws GordianException {
        return KEYPAIR_ALG_ID.determineKeyPairSpec(pEncoded);
    }

    /**
     * Check the asymKeySpec.
     *
     * @param pKeySpec the asymKeySpec
     * @throws GordianException on error
     */
    protected void checkAsymKeySpec(final GordianNewKeyPairSpec pKeySpec) throws GordianException {
        /* Check validity of keySpec */
        if (pKeySpec == null || !pKeySpec.isValid()) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        }
    }

    @Override
    public Predicate<GordianNewKeyPairSpec> supportedKeyPairSpecs() {
        return this::validAsymKeySpec;
    }

    /**
     * Valid keySpec.
     *
     * @param pKeySpec the asymKeySpec
     * @return true/false
     */
    public boolean validAsymKeySpec(final GordianNewKeyPairSpec pKeySpec) {
        return pKeySpec != null && pKeySpec.isValid();
    }

    @Override
    public List<GordianNewKeyPairSpec> listAllSupportedKeyPairSpecs() {
        return listPossibleKeySpecs()
                .stream()
                .filter(supportedKeyPairSpecs())
                .toList();
    }

    @Override
    public List<GordianNewKeyPairSpec> listAllSupportedKeyPairSpecs(final GordianNewKeyPairType pKeyPairType) {
        return listPossibleKeySpecs()
                .stream()
                .filter(s -> pKeyPairType.equals(s.getKeyPairType()))
                .filter(supportedKeyPairSpecs())
                .toList();
    }

    @Override
    public List<GordianNewKeyPairSpec> listPossibleKeySpecs() {
        return GordianCoreKeyPairSpecBuilder.listPossibleKeySpecs();
    }
}

