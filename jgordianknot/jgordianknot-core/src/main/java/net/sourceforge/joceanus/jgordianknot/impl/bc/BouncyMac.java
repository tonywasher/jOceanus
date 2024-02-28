/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.ext.params.Blake2Parameters;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.SkeinParameters;

import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for BouncyCastle MAC.
 */
public final class BouncyMac
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
    BouncyMac(final BouncyFactory pFactory,
              final GordianMacSpec pMacSpec,
              final Mac pMac) {
        super(pFactory, pMacSpec);
        theMac = pMac;
    }

    @Override
    public BouncyKey<GordianMacSpec> getKey() {
        return (BouncyKey<GordianMacSpec>) super.getKey();
    }

    @Override
    public void init(final GordianMacParameters pParams) throws OceanusException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final BouncyKey<GordianMacSpec> myKey = BouncyKey.accessKey(getKey());

        /* Initialise the cipher */
        final CipherParameters myParms = buildInitParams(myKey, getInitVector());
        theMac.init(myParms);
    }

    @Override
    public int getMacSize() {
        return theMac instanceof KMAC
               ? getMacSpec().getDigestSpec().getDigestLength().getByteLength()
               : theMac.getMacSize();
    }

    /**
     * Build initParameters.
     * @param pKey the key
     * @param pIV the initVector
     * @return the parameters
     */
    private CipherParameters buildInitParams(final BouncyKey<GordianMacSpec> pKey,
                                             final byte[] pIV) {
        /* Handle Skein Parameters */
        if (GordianMacType.SKEIN.equals(getMacSpec().getMacType())) {
            final SkeinParameters.Builder myBuilder = new SkeinParameters.Builder();
            myBuilder.setKey(pKey.getKey());
            if (pIV != null) {
                myBuilder.setNonce(pIV);
            }
            return myBuilder.build();
        }

        /* Handle Blake Parameters */
        if (GordianMacType.BLAKE2.equals(getMacSpec().getMacType())) {
            final Blake2Parameters.Builder myBuilder = new Blake2Parameters.Builder();
            myBuilder.setKey(pKey.getKey());
            if (pIV != null) {
                myBuilder.setSalt(pIV);
            }
            return myBuilder.build();
        }

        /* Handle standard MACs */
        CipherParameters myParms = new KeyParameter(pKey.getKey());
        if (pIV != null) {
            myParms = new ParametersWithIV(myParms, pIV);
        }
        return myParms;
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
        final byte[] myResult = new byte[getMacSize()];
        if (theMac instanceof KMAC) {
            ((KMAC) theMac).doFinal(myResult, 0, getMacSize());
        } else {
            theMac.doFinal(myResult, 0);
        }
        return myResult;
    }

    @Override
    public int doFinish(final byte[] pBuffer,
                        final int pOffset) {
        return theMac instanceof KMAC
            ? ((KMAC) theMac).doFinal(pBuffer, pOffset, getMacSize())
            : theMac.doFinal(pBuffer, pOffset);
    }
}
