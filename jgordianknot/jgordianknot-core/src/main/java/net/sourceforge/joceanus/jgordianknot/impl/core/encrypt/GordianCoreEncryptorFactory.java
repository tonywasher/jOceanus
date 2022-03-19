/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.encrypt;

import java.util.Iterator;
import java.util.function.Predicate;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    public Predicate<GordianEncryptorSpec> supportedKeyPairEncryptors() {
        return this::validEncryptorSpec;
    }

    @Override
    public GordianKeyPairSetEncryptor createKeyPairSetEncryptor(final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Check valid spec */
        final GordianKeyPairFactory myPairFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairSetFactory mySetFactory = myPairFactory.getKeyPairSetFactory();
        if (!mySetFactory.supportedKeyPairSetSpecs().test(pKeyPairSetSpec)
                || !pKeyPairSetSpec.canEncrypt()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyPairSetSpec));
        }

        /* Create the new encryptor */
        return new GordianCoreKeyPairSetEncryptor(myPairFactory, pKeyPairSetSpec);
    }

    /**
     * Check the encryptorSpec.
     * @param pEncryptorSpec the encryptorSpec
     * @throws OceanusException on error
     */
    protected void checkEncryptorSpec(final GordianEncryptorSpec pEncryptorSpec) throws OceanusException {
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

        /* Disallow McEliece if it is the wrong style key */
        if (GordianKeyPairType.MCELIECE.equals(pKeyPairSpec.getKeyPairType())) {
            return GordianMcElieceEncryptionType.checkValidEncryptionType(pKeyPairSpec.getMcElieceKeySpec(), pEncryptorSpec.getMcElieceType());
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
            if (pairIterator.hasNext() || encIterator.hasNext()) return false;
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
}
