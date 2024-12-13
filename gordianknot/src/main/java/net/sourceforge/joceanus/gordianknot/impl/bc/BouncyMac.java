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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.gordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianBlake2Parameters;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.util.Objects;

/**
 * Wrapper for BouncyCastle MAC.
 */
public class BouncyMac
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
    public void init(final GordianMacParameters pParams) throws GordianException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final BouncyKey<GordianMacSpec> myKey = BouncyKey.accessKey(getKey());

        /* Initialise the cipher */
        final CipherParameters myParms = buildInitParams(myKey, getInitVector());
        theMac.init(myParms);
    }

    @Override
    public int getMacSize() {
        return theMac.getMacSize();
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
            final GordianSkeinParameters.Builder myBuilder = new GordianSkeinParameters.Builder();
            myBuilder.setKey(pKey.getKey());
            if (pIV != null) {
                myBuilder.setNonce(pIV);
            }
            return myBuilder.build();
        }

        /* Handle Blake Parameters */
        if (GordianMacType.BLAKE2.equals(getMacSpec().getMacType())) {
            final GordianBlake2Parameters.Builder myBuilder = new GordianBlake2Parameters.Builder();
            myBuilder.setKey(pKey.getKey());
            if (pIV != null) {
                myBuilder.setSalt(pIV);
            }
            if (Objects.requireNonNull(getMacSpec().getDigestSpec()).isXof()) {
                myBuilder.setMaxOutputLen(-1);
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
        doFinish(myResult, 0);
        return myResult;
    }

    @Override
    public int doFinish(final byte[] pBuffer,
                        final int pOffset) {
        return theMac.doFinal(pBuffer, pOffset);
    }
}
