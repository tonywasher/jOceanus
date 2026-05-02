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
package io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * GordianKnot base for encryptorFactory.
 */
public abstract class GordianCoreEncryptorFactory
        implements GordianEncryptorFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The algorithm Ids.
     */
    private GordianCoreEncryptorAlgId theAlgIds;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreEncryptorFactory(final GordianBaseFactory pFactory) {
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
    public GordianEncryptorSpecBuilder newEncryptorSpecBuilder() {
        return GordianCoreEncryptorSpecBuilder.newInstance();
    }

    @Override
    public Predicate<GordianEncryptorSpec> supportedEncryptors() {
        return this::validEncryptorSpec;
    }

    /**
     * Check the encryptorSpec.
     *
     * @param pEncryptorSpec the encryptorSpec
     * @throws GordianException on error
     */
    protected void checkEncryptorSpec(final GordianEncryptorSpec pEncryptorSpec) throws GordianException {
        /* Check validity of encryptor */
        if (!validEncryptorSpec(pEncryptorSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pEncryptorSpec));
        }
    }

    /**
     * Check EncryptorSpec.
     *
     * @param pSpec the encryptorSpec
     * @return true/false
     */
    protected boolean validEncryptorSpec(final GordianEncryptorSpec pSpec) {
        /* Reject invalid encryptorSpec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* For Composite EncryptorSpec */
        final GordianCoreEncryptorSpec myEncSpec = (GordianCoreEncryptorSpec) pSpec;
        if (pSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the specs */
            final Iterator<GordianEncryptorSpec> myIterator = myEncSpec.encryptorSpecIterator();
            while (myIterator.hasNext()) {
                final GordianEncryptorSpec mySpec = myIterator.next();
                if (!validEncryptorSpec(mySpec)) {
                    return false;
                }
            }
        }

        /* Check that spec is supported */
        return myEncSpec.isSupported();
    }

    @Override
    public boolean validEncryptorSpecForKeyPairSpec(final GordianKeyPairSpec pKeyPairSpec,
                                                    final GordianEncryptorSpec pEncryptorSpec) {
        /* Check that the encryptorSpec is supported */
        if (!validEncryptorSpec(pEncryptorSpec)) {
            return false;
        }

        /* Check encryptor matches keyPair */
        final GordianCoreEncryptorSpec mySpec = (GordianCoreEncryptorSpec) pEncryptorSpec;
        final GordianKeyPairType myKeyType = pKeyPairSpec.getKeyPairType();
        final GordianKeyPairType myEncType = pEncryptorSpec.getKeyPairType();
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeyPairSpec;
        switch (myEncType) {
            case SM2, EC:
                if (!GordianKeyPairType.EC.equals(myKeyType)
                        && !GordianKeyPairType.GOST.equals(myKeyType)
                        && !GordianKeyPairType.SM2.equals(myKeyType)) {
                    return false;
                }
                break;
            default:
                if (!myKeyType.equals(myEncType)) {
                    return false;
                }
                break;
        }

        /* Disallow EC if the curve does not support encryption */
        if (GordianKeyPairType.EC.equals(pKeyPairSpec.getKeyPairType())) {
            return true;
        }

        /* If this is an RSA encryption */
        if (GordianKeyPairType.RSA.equals(pKeyPairSpec.getKeyPairType())) {
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = mySpec.getDigestSpec().getDigestLength().getByteLength();
            myLen = (myLen + 1) * Byte.SIZE;
            return myKeySpec.getRSASpec().getLength() >= (myLen << 1);
        }

        /* If this is an ELGAMAL encryption */
        if (GordianKeyPairType.ELGAMAL.equals(pKeyPairSpec.getKeyPairType())) {
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = mySpec.getDigestSpec().getDigestLength().getByteLength();
            myLen = (myLen + 1) * Byte.SIZE;
            return myKeySpec.getDHSpec().getLength() >= (myLen << 1);
        }

        /* For Composite EncryptorSpec */
        if (pKeyPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the keyPairs */
            final Iterator<GordianKeyPairSpec> pairIterator = myKeySpec.keySpecIterator();
            final Iterator<GordianEncryptorSpec> encIterator = mySpec.encryptorSpecIterator();
            while (pairIterator.hasNext() && encIterator.hasNext()) {
                final GordianKeyPairSpec myPairSpec = pairIterator.next();
                final GordianEncryptorSpec myEncSpec = encIterator.next();
                if (!validEncryptorSpecForKeyPairSpec(myPairSpec, myEncSpec)) {
                    return false;
                }
            }
            return !pairIterator.hasNext() && !encIterator.hasNext();
        }

        /* OK */
        return true;
    }

    /**
     * Obtain Identifier for EncryptorSpec.
     *
     * @param pSpec the encryptorSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianEncryptorSpec pSpec) {
        return getAlgorithmIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain EncryptorSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the encryptorSpec (or null if not found)
     */
    public GordianEncryptorSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the encryptor algorithm Ids.
     *
     * @return the encryptor Algorithm Ids
     */
    private GordianCoreEncryptorAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianCoreEncryptorAlgId(theFactory);
        }
        return theAlgIds;
    }

    @Override
    public List<GordianEncryptorSpec> listAllSupportedEncryptors(final GordianKeyPair pKeyPair) {
        return listAllSupportedEncryptors(pKeyPair.getKeyPairSpec());
    }

    @Override
    public List<GordianEncryptorSpec> listAllSupportedEncryptors(final GordianKeyPairSpec pKeyPairSpec) {
        return listPossibleEncryptors(pKeyPairSpec.getKeyPairType())
                .stream()
                .filter(s -> validEncryptorSpecForKeyPairSpec(pKeyPairSpec, s))
                .toList();
    }

    @Override
    public List<GordianEncryptorSpec> listPossibleEncryptors(final GordianKeyPairType pKeyPairType) {
        return GordianCoreEncryptorSpecBuilder.listAllPossibleSpecs(pKeyPairType);
    }

    @Override
    public GordianEncryptorSpec defaultForKeyPair(final GordianKeyPairSpec pKeySpec) {
        final GordianEncryptorSpecBuilder myEncBuilder = GordianCoreEncryptorSpecBuilder.newInstance();
        final GordianDigestSpecBuilder myDigestBuilder = GordianCoreDigestSpecBuilder.newInstance();
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return myEncBuilder.rsa(myDigestBuilder.sha2(GordianLength.LEN_512));
            case EC, SM2, GOST:
                return myEncBuilder.sm2(GordianSM2EncryptionType.C1C2C3, myDigestBuilder.sm3());
            case ELGAMAL:
                return myEncBuilder.elGamal(myDigestBuilder.sha2(GordianLength.LEN_512));
            case COMPOSITE:
                final List<GordianEncryptorSpec> mySpecs = new ArrayList<>();
                final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
                final Iterator<GordianKeyPairSpec> myIterator = myKeySpec.keySpecIterator();
                while (myIterator.hasNext()) {
                    final GordianKeyPairSpec mySpec = myIterator.next();
                    mySpecs.add(defaultForKeyPair(mySpec));
                }
                final GordianEncryptorSpec mySpec = myEncBuilder.composite(mySpecs);
                return mySpec.isValid() ? mySpec : null;
            default:
                return null;
        }
    }
}
