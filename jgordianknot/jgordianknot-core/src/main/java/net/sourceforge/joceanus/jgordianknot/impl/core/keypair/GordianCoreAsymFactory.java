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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypair;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Core AsymFactory.
 */
public abstract class GordianCoreAsymFactory
        implements GordianAsymFactory {
    /**
     * AsymAlgId.
     */
    private static final GordianAsymAlgId ASYM_ALG_ID = new GordianAsymAlgId();

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The signature factory.
     */
    private GordianSignatureFactory theSignatureFactory;

    /**
     * The agreement factory.
     */
    private GordianAgreementFactory theAgreementFactory;

    /**
     * The encryptor factory.
     */
    private GordianEncryptorFactory theEncryptorFactory;

    /**
     * The keyStore factory.
     */
    private GordianKeyStoreFactory theKeyStoreFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCoreAsymFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
        theKeyStoreFactory = new GordianCoreKeyStoreFactory(pFactory);
    }

    @Override
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianSignatureFactory getSignatureFactory() {
        return theSignatureFactory;
    }

    /**
     * Set the signature factory.
     * @param pFactory the factory
     */
    protected void setSignatureFactory(final GordianSignatureFactory pFactory) {
        theSignatureFactory = pFactory;
    }

    @Override
    public GordianAgreementFactory getAgreementFactory() {
        return theAgreementFactory;
    }

    /**
     * Set the agreement factory.
     * @param pFactory the factory
     */
    protected void setAgreementFactory(final GordianAgreementFactory pFactory) {
        theAgreementFactory = pFactory;
    }

    @Override
    public GordianEncryptorFactory getEncryptorFactory() {
        return theEncryptorFactory;
    }

    /**
     * Set the encryptor factory.
     * @param pFactory the factory
     */
    protected void setEncryptorFactory(final GordianEncryptorFactory pFactory) {
        theEncryptorFactory = pFactory;
    }

    @Override
    public GordianKeyStoreFactory getKeyStoreFactory() {
        return theKeyStoreFactory;
    }

    @Override
    public GordianAsymKeySpec determineKeySpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
        return ASYM_ALG_ID.determineKeySpec(pEncoded);
    }

    @Override
    public GordianAsymKeySpec determineKeySpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
        return ASYM_ALG_ID.determineKeySpec(pEncoded);
    }

    /**
     * Check the asymKeySpec.
     * @param pKeySpec the asymKeySpec
     * @throws OceanusException on error
     */
    protected void checkAsymKeySpec(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Check validity of keySpec */
        if (pKeySpec == null || !pKeySpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    @Override
    public Predicate<GordianAsymKeySpec> supportedAsymKeySpecs() {
        return this::validAsymKeySpec;
    }

    /**
     * Valid keySpec.
     * @param pKeySpec the asymKeySpec
     * @return true/false
     */
    public boolean validAsymKeySpec(final GordianAsymKeySpec pKeySpec) {
        return pKeySpec != null && pKeySpec.isValid();
    }
}
