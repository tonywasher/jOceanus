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
package io.github.tonywasher.joceanus.gordianknot.impl.core.mac;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Core Mac Factory.
 */
public abstract class GordianCoreMacFactory
        implements GordianMacFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     */
    protected GordianCoreMacFactory(final GordianBaseFactory pFactory) {
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
    public GordianNewMacSpecBuilder newMacSpecBuilder() {
        return GordianCoreMacSpecBuilder.newInstance();
    }

    @Override
    public Predicate<GordianNewMacSpec> supportedMacSpecs() {
        return this::validMacSpec;
    }

    @Override
    public Predicate<GordianNewMacType> supportedMacTypes() {
        return this::validMacType;
    }

    /**
     * Obtain predicate for supported hMac digestSpecs.
     *
     * @return the predicate
     */
    public Predicate<GordianNewDigestSpec> supportedHMacDigestSpecs() {
        return this::validHMacSpec;
    }

    /**
     * Obtain predicate for supported poly1305 symKeySpecs.
     *
     * @return the predicate
     */
    private Predicate<GordianNewSymKeySpec> supportedPoly1305SymKeySpecs() {
        return p -> p == null
                || (validPoly1305SymKeySpec(p)
                && p.getBlockLength() == GordianLength.LEN_128);
    }

    /**
     * Obtain predicate for supported gMac symKeySpecs.
     *
     * @return the predicate
     */
    private Predicate<GordianNewSymKeySpec> supportedGMacSymKeySpecs() {
        return p -> validGMacSymKeySpec(p)
                && p.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * Obtain predicate for supported cMac symKeyTypes.
     *
     * @return the predicate
     */
    private Predicate<GordianNewSymKeySpec> supportedCMacSymKeySpecs() {
        return this::validCMacSymKeySpec;
    }

    /**
     * Check MacType.
     *
     * @param pMacType the macType
     * @return true/false
     */
    protected boolean validMacType(final GordianNewMacType pMacType) {
        return pMacType != null;
    }

    /**
     * Check the macSpec.
     *
     * @param pMacSpec the macSpec
     * @throws GordianException on error
     */
    protected void checkMacSpec(final GordianKeySpec pMacSpec) throws GordianException {
        /* Check validity of MacSpec */
        if (!(pMacSpec instanceof GordianNewMacSpec mySpec)
                || !supportedMacSpecs().test(mySpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pMacSpec));
        }
    }

    /**
     * Check HMacSpec.
     *
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    public boolean validHMacSpec(final GordianNewDigestSpec pDigestSpec) {
        /* Access details */
        final GordianNewDigestType myType = pDigestSpec.getDigestType();
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();

        /* Check validity */
        return theFactory.getValidator().supportedHMacDigestTypes().test(myType)
                && !pDigestSpec.isXofMode()
                && myDigests.supportedDigestSpecs().test(pDigestSpec);
    }

    /**
     * Check MacSpec.
     *
     * @param pMacSpec the macSpec
     * @return true/false
     */
    private boolean validMacSpec(final GordianNewMacSpec pMacSpec) {
        /* Reject invalid macSpec */
        if (pMacSpec == null || !pMacSpec.isValid()) {
            return false;
        }

        /* Check that the macType is supported */
        final GordianNewMacType myType = pMacSpec.getMacType();
        if (!supportedMacTypes().test(myType)) {
            return false;
        }

        /* Switch on MacType */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianCoreMacSpec myMacSpec = (GordianCoreMacSpec) pMacSpec;
        final GordianNewDigestSpec myDigestSpec = myMacSpec.getDigestSpec();
        final GordianNewSymKeySpec mySymSpec = myMacSpec.getSymKeySpec();
        switch (myType) {
            case HMAC:
                return supportedHMacDigestSpecs().test(myDigestSpec);
            case GMAC:
                return supportedGMacSymKeySpecs().test(mySymSpec);
            case CMAC:
                return supportedCMacSymKeySpecs().test(mySymSpec);
            case POLY1305:
                return supportedPoly1305SymKeySpecs().test(mySymSpec);
            case SKEIN:
                return GordianNewDigestType.SKEIN.equals(Objects.requireNonNull(myDigestSpec).getDigestType())
                        && myDigests.supportedDigestSpecs().test(myDigestSpec);
            case BLAKE2:
                return GordianNewDigestType.BLAKE2.equals(Objects.requireNonNull(myDigestSpec).getDigestType())
                        && myDigests.supportedDigestSpecs().test(myDigestSpec);
            case BLAKE3:
                return GordianNewDigestType.BLAKE3.equals(Objects.requireNonNull(myDigestSpec).getDigestType())
                        && myDigests.supportedDigestSpecs().test(myDigestSpec);
            case KUPYNA:
                return GordianNewDigestType.KUPYNA.equals(Objects.requireNonNull(myDigestSpec).getDigestType())
                        && myDigests.supportedDigestSpecs().test(myDigestSpec);
            case KALYNA:
                return GordianNewSymKeyType.KALYNA.equals(Objects.requireNonNull(mySymSpec).getSymKeyType())
                        && myCiphers.validSymKeySpec(mySymSpec);
            case CBCMAC:
            case CFBMAC:
                return (!GordianNewSymKeyType.RC5.equals(Objects.requireNonNull(mySymSpec).getSymKeyType())
                        || !GordianLength.LEN_128.equals(mySymSpec.getBlockLength()))
                        && myCiphers.validSymKeySpec(mySymSpec);
            case ZUC:
            case VMPC:
            case SIPHASH:
            case GOST:
            case KMAC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine supported Poly1305 algorithms.
     *
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validPoly1305SymKeySpec(final GordianNewSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case KUZNYECHIK:
            case RC5:
                return false;
            default:
                final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
                return myCiphers.validSymKeySpec(pKeySpec);
        }
    }

    /**
     * Determine supported GMAC algorithms.
     *
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validGMacSymKeySpec(final GordianNewSymKeySpec pKeySpec) {
        if (GordianNewSymKeyType.RC5.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        return myCiphers.validSymKeySpec(pKeySpec);
    }

    /**
     * Determine supported CMAC algorithms.
     *
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validCMacSymKeySpec(final GordianNewSymKeySpec pKeySpec) {
        if (GordianNewSymKeyType.RC5.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        return myCiphers.validSymKeySpec(pKeySpec);
    }

    @Override
    public List<GordianNewMacSpec> listAllSupportedSpecs(final GordianLength pKeyLen) {
        return listAllPossibleSpecs(pKeyLen)
                .stream()
                .filter(supportedMacSpecs())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * List all possible macSpecs for a keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    public List<GordianNewMacSpec> listAllPossibleSpecs(final GordianLength pKeyLen) {
        return GordianCoreMacSpecBuilder.listAllPossibleSpecs(pKeyLen);
    }
}
