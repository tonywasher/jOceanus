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
package net.sourceforge.joceanus.gordianknot.impl.core.encrypt;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * GordianKnot base for encryptorFactory.
 */
public abstract class GordianCoreEncryptorFactory
    implements GordianEncryptorFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The algorithm Ids.
     */
    private GordianEncryptorAlgId theAlgIds;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianCoreEncryptorFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianEncryptorSpec> supportedEncryptors() {
        return this::validEncryptorSpec;
    }

    /**
     * Check the encryptorSpec.
     * @param pEncryptorSpec the encryptorSpec
     * @throws GordianException on error
     */
    protected void checkEncryptorSpec(final GordianEncryptorSpec pEncryptorSpec) throws GordianException {
        /* Check validity of encryptor */
        if (!validEncryptorSpec(pEncryptorSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pEncryptorSpec));
        }
    }

    /**
     * Check EncryptorSpec.
     * @param pSpec the encryptorSpec
     * @return true/false
     */
    protected boolean validEncryptorSpec(final GordianEncryptorSpec pSpec) {
        /* Reject invalid encryptorSpec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* For Composite EncryptorSpec */
        if (pSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the specs */
            final Iterator<GordianEncryptorSpec> myIterator = pSpec.encryptorSpecIterator();
            while (myIterator.hasNext()) {
                final GordianEncryptorSpec mySpec = myIterator.next();
                if (!validEncryptorSpec(mySpec)) {
                    return false;
                }
            }
        }

        /* Check that spec is supported */
        return pSpec.isSupported();
    }

    @Override
    public boolean validEncryptorSpecForKeyPairSpec(final GordianKeyPairSpec pKeyPairSpec,
                                                    final GordianEncryptorSpec pEncryptorSpec) {
        /* Check that the encryptorSpec is supported */
        if (!validEncryptorSpec(pEncryptorSpec)) {
            return false;
        }

        /* Check encryptor matches keyPair */
        final GordianKeyPairType myKeyType = pKeyPairSpec.getKeyPairType();
        final GordianKeyPairType myEncType = pEncryptorSpec.getKeyPairType();
        switch (myEncType) {
            case SM2:
            case EC:
                if (!GordianKeyPairType.EC.equals(myKeyType)
                    && !GordianKeyPairType.GOST2012.equals(myKeyType)
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
            return pKeyPairSpec.getElliptic().canEncrypt();
        }

        /* If this is a RSA encryption */
        if (GordianKeyPairType.RSA.equals(pKeyPairSpec.getKeyPairType())) {
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = pEncryptorSpec.getDigestSpec().getDigestLength().getByteLength();
            myLen = (myLen + 1) * Byte.SIZE;
            return pKeyPairSpec.getRSAModulus().getLength() >= (myLen << 1);
        }

        /* For Composite EncryptorSpec */
        if (pKeyPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the keyPairs */
            final Iterator<GordianKeyPairSpec> pairIterator = pKeyPairSpec.keySpecIterator();
            final Iterator<GordianEncryptorSpec> encIterator = pEncryptorSpec.encryptorSpecIterator();
            while (pairIterator.hasNext() && encIterator.hasNext()) {
                final GordianKeyPairSpec myPairSpec = pairIterator.next();
                final GordianEncryptorSpec myEncSpec = encIterator.next();
               if (!validEncryptorSpecForKeyPairSpec(myPairSpec, myEncSpec)) {
                    return false;
                }
            }
            if (pairIterator.hasNext() || encIterator.hasNext()) {
                return false;
            }
        }

        /* OK */
        return true;
    }

    /**
     * Obtain Identifier for EncryptorSpec.
     * @param pSpec the encryptorSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianEncryptorSpec pSpec) {
        return getAlgorithmIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain EncryptorSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the encryptorSpec (or null if not found)
     */
    public GordianEncryptorSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the encryptor algorithm Ids.
     * @return the encryptor Algorithm Ids
     */
    private GordianEncryptorAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianEncryptorAlgId(theFactory);
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
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianEncryptorSpec> listPossibleEncryptors(final GordianKeyPairType pKeyPairType) {
        /* Create list */
        final List<GordianEncryptorSpec> myEncryptors = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairType) {
            case RSA:
                myEncryptors.add(GordianEncryptorSpecBuilder.rsa(GordianDigestSpecBuilder.sha2(GordianLength.LEN_224)));
                myEncryptors.add(GordianEncryptorSpecBuilder.rsa(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)));
                myEncryptors.add(GordianEncryptorSpecBuilder.rsa(GordianDigestSpecBuilder.sha2(GordianLength.LEN_384)));
                myEncryptors.add(GordianEncryptorSpecBuilder.rsa(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)));
                break;
            case ELGAMAL:
                myEncryptors.add(GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_224)));
                myEncryptors.add(GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)));
                myEncryptors.add(GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_384)));
                myEncryptors.add(GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)));
                break;
            case EC:
            case SM2:
            case GOST2012:
                /* Add EC-ElGamal */
                myEncryptors.add(GordianEncryptorSpecBuilder.ec());

                /* Loop through the encryptionSpecs */
                for (GordianSM2EncryptionSpec mySpec : GordianSM2EncryptionSpec.listPossibleSpecs()) {
                    myEncryptors.add(GordianEncryptorSpecBuilder.sm2(mySpec));
                }
                break;
            default:
                break;
        }

        /* Return the list */
        return myEncryptors;
    }

    @Override
    public GordianEncryptorSpec defaultForKeyPair(final GordianKeyPairSpec pKeySpec) {
        switch (pKeySpec.getKeyPairType()) {
            case RSA:
                return GordianEncryptorSpecBuilder.rsa(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512));
            case EC:
            case SM2:
            case GOST2012:
                return GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.sm3()));
            case ELGAMAL:
                return GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512));
            case COMPOSITE:
                final List<GordianEncryptorSpec> mySpecs = new ArrayList<>();
                final Iterator<GordianKeyPairSpec> myIterator = pKeySpec.keySpecIterator();
                while (myIterator.hasNext()) {
                    final GordianKeyPairSpec mySpec = myIterator.next();
                    mySpecs.add(defaultForKeyPair(mySpec));
                }
                final GordianEncryptorSpec mySpec = GordianEncryptorSpecBuilder.composite(mySpecs);
                return mySpec.isValid() ? mySpec : null;
            default:
                return null;
        }
    }
}
