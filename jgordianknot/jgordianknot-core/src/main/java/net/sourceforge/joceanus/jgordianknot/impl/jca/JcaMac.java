/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jcajce.spec.SkeinParameterSpec.Builder;

import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for JCA MAC.
 */
public final class JcaMac
        extends GordianCoreMac {
    /**
     * Mac.
     */
    private final Mac theMac;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pMacSpec the MacSpec
     * @param pMac the MAC
     */
    JcaMac(final JcaFactory pFactory,
           final GordianMacSpec pMacSpec,
           final Mac pMac) {
        super(pFactory, pMacSpec);
        theMac = pMac;
    }

    @Override
    public JcaKey<GordianMacSpec> getKey() {
        return (JcaKey<GordianMacSpec>) super.getKey();
    }

    @Override
    public void init(final GordianMacParameters pParams) throws OceanusException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final JcaKey<GordianMacSpec> myKey = JcaKey.accessKey(getKey());

        /* Protect against exceptions */
        try {
            /* Initialise the MAC */
            final byte[] myIV = getInitVector();
            if (myIV == null) {
                theMac.init(myKey.getKey());
            } else {
                theMac.init(myKey.getKey(), buildInitParams(myIV));
            }

            /* Catch exceptions */
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to initialise Mac", e);
        }
    }

    @Override
    public int getMacSize() {
        return theMac.getMacLength();
    }

    /**
     * Build initParameters.
     * @param pIV the initVector
     * @return the parameters
     */
    private AlgorithmParameterSpec buildInitParams(final byte[] pIV) {
        /* Handle Skein Parameters */
        if (GordianMacType.SKEIN.equals(getMacSpec().getMacType())) {
            final Builder myBuilder = new Builder();
            myBuilder.setNonce(pIV);
            return myBuilder.build();
        }

        /* Handle standard MACs */
        return new IvParameterSpec(pIV);
    }

    @Override
    public void doUpdate(final byte[] pBytes,
                         final int pOffset,
                         final int pLength) {
        theMac.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theMac.update(pByte);
    }

    @Override
    public void reset() {
        theMac.reset();
    }

    @Override
    public byte[] finish() {
        return theMac.doFinal();
    }

    @Override
    public int doFinish(final byte[] pBuffer,
                        final int pOffset) throws OceanusException {
        try {
            theMac.doFinal(pBuffer, pOffset);
            return getMacSize();
        } catch (ShortBufferException e) {
            throw new GordianCryptoException("Failed to calculate Mac", e);
        }
    }
}
