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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSAKeyPair.BouncyDSASignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDSTUKeyPair.BouncyDSTUSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEdDSAKeyPair.BouncyEdDSASignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyGOSTKeyPair.BouncyGOSTSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyLMSKeyPair.BouncyLMSSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSASignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRainbowKeyPair.BouncyRainbowSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySM2KeyPair.BouncySM2Signature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySPHINCSKeyPair.BouncySPHINCSSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySPHINCSPlusKeyPair.BouncySPHINCSPlusSignature;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXMSSKeyPair.BouncyXMSSSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCompositeSigner;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Bouncy Signature Factory.
 */
public class BouncySignatureFactory
        extends GordianCoreSignatureFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncySignatureFactory(final BouncyFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    protected BouncyFactory getFactory() {
        return (BouncyFactory) super.getFactory();
    }

    @Override
    public Predicate<GordianSignatureSpec> supportedKeyPairSignatures() {
        return this::validSignatureSpec;
    }

    @Override
    public GordianSignature createSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        checkSignatureSpec(pSignatureSpec);

        /* Create the signer */
        return getBCSigner(pSignatureSpec);
    }

    /**
     * Create the BouncyCastle Signer.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianSignature getBCSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getKeyPairType()) {
            case RSA:
                return new BouncyRSASignature(getFactory(), pSignatureSpec);
            case EC:
                return new BouncyECSignature(getFactory(), pSignatureSpec);
            case DSTU4145:
                return new BouncyDSTUSignature(getFactory(), pSignatureSpec);
            case GOST2012:
                return new BouncyGOSTSignature(getFactory(), pSignatureSpec);
            case SM2:
                return new BouncySM2Signature(getFactory(), pSignatureSpec);
            case EDDSA:
                return new BouncyEdDSASignature(getFactory(), pSignatureSpec);
            case DSA:
                return new BouncyDSASignature(getFactory(), pSignatureSpec);
            case SPHINCS:
                return new BouncySPHINCSSignature(getFactory(), pSignatureSpec);
            case SPHINCSPLUS:
                return new BouncySPHINCSPlusSignature(getFactory(), pSignatureSpec);
            case RAINBOW:
                return new BouncyRainbowSignature(getFactory(), pSignatureSpec);
            case XMSS:
                return new BouncyXMSSSignature(getFactory(), pSignatureSpec);
            case LMS:
                return new BouncyLMSSignature(getFactory(), pSignatureSpec);
            case COMPOSITE:
                return new GordianCompositeSigner(getFactory(), pSignatureSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSignatureSpec.getKeyPairType()));
        }
    }
}
