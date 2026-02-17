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
package io.github.tonywasher.joceanus.gordianknot.impl.core.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base Digest Factory.
 */
public abstract class GordianCoreDigestFactory
        implements GordianDigestFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The Digest AlgIds.
     */
    private GordianCoreDigestAlgId theDigestAlgIds;

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     */
    protected GordianCoreDigestFactory(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     *
     * @return the factory
     */
    protected GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianNewDigestSpecBuilder newDigestSpecBuilder() {
        return GordianCoreDigestSpecBuilder.newInstance();
    }

    /**
     * Obtain Identifier for DigestSpec.
     *
     * @param pSpec the digestSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianNewDigestSpec pSpec) {
        return getDigestAlgIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain DigestSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the digestSpec (or null if not found)
     */
    public GordianCoreDigestSpec getDigestSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getDigestAlgIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the digest algorithm Ids.
     *
     * @return the digest Algorithm Ids
     */
    private GordianCoreDigestAlgId getDigestAlgIds() {
        if (theDigestAlgIds == null) {
            theDigestAlgIds = new GordianCoreDigestAlgId(theFactory);
        }
        return theDigestAlgIds;
    }

    @Override
    public Predicate<GordianNewDigestSpec> supportedDigestSpecs() {
        return this::validDigestSpec;
    }

    @Override
    public Predicate<GordianNewDigestType> supportedDigestTypes() {
        return t -> theFactory.getValidator().validDigestType(t);
    }

    /**
     * Check digestSpec.
     *
     * @param pDigestSpec the digestSpec
     * @throws GordianException on error
     */
    public void checkDigestSpec(final GordianNewDigestSpec pDigestSpec) throws GordianException {
        /* Check validity of DigestType */
        if (!supportedDigestSpecs().test(pDigestSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pDigestSpec));
        }
    }

    /**
     * Check DigestSpec.
     *
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    public boolean validDigestSpec(final GordianNewDigestSpec pDigestSpec) {
        /* Reject invalid digestSpec */
        if (pDigestSpec == null || !pDigestSpec.isValid()) {
            return false;
        }

        /* If we have an explicit Xof, check support */
        if (pDigestSpec.isXofMode()
                && !theFactory.getValidator().isXofSupported()) {
            return false;
        }

        /* Check validity */
        return supportedDigestTypes().test(pDigestSpec.getDigestType());
    }

    @Override
    public List<GordianNewDigestSpec> listAllSupportedSpecs() {
        return listAllPossibleSpecs()
                .stream()
                .filter(supportedDigestSpecs())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewDigestType> listAllSupportedTypes() {
        return Arrays.stream(GordianNewDigestType.values())
                .filter(supportedDigestTypes())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewDigestSpec> listAllPossibleSpecs() {
        return GordianCoreDigestSpecBuilder.listAllPossibleSpecs();
    }
}
