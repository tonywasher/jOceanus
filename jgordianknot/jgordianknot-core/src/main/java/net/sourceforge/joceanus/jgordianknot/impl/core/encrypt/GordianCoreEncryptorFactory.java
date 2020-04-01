/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.util.function.Predicate;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianMcElieceEncryptionType;
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
    public GordianCoreEncryptorFactory(final GordianCoreFactory pFactory) {
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

        /* Check that spec is supported */
        return pSpec.isSupported();
    }

    @Override
    public boolean validEncryptorSpecForKeySpec(final GordianAsymKeySpec pKeySpec,
                                                final GordianEncryptorSpec pEncryptorSpec) {
        /* Reject invalid encryptorSpec */
        if (pEncryptorSpec == null || !pEncryptorSpec.isValid()) {
            return false;
        }

        /* Check encryptor matches keyPair */
        if (pEncryptorSpec.getKeyType() != pKeySpec.getKeyType()) {
            return false;
        }

        /* Check that the encryptorSpec is supported */
        if (!validEncryptorSpec(pEncryptorSpec)) {
            return false;
        }

        /* Disallow EC if the curve does not support encryption */
        if (GordianAsymKeyType.EC.equals(pKeySpec.getKeyType())) {
            return pKeySpec.getElliptic().canEncrypt();
        }

        /* Disallow McEliece if it is the wrong style key */
        if (GordianAsymKeyType.MCELIECE.equals(pKeySpec.getKeyType())) {
            return GordianMcElieceEncryptionType.checkValidEncryptionType(pKeySpec.getMcElieceKeySpec(), pEncryptorSpec.getMcElieceType());
        }

        /* If this is a RSA encryption */
        if (GordianAsymKeyType.RSA.equals(pKeySpec.getKeyType())) {
            /* The digest length cannot be too large wrt to the modulus */
            int myLen = pEncryptorSpec.getDigestSpec().getDigestLength().getByteLength();
            myLen = (myLen + 1) * Byte.SIZE;
            return pKeySpec.getRSAModulus().getLength() >= (myLen << 1);
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
