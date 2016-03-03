/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for BouncyCastle MAC.
 */
public final class BouncyMac
        extends GordianMac {
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
    protected BouncyMac(final BouncyFactory pFactory,
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
    public void initMac(final GordianKey<GordianMacSpec> pKey,
                        final byte[] pIV) throws OceanusException {
        /* Access and validate the key */
        BouncyKey<GordianMacSpec> myKey = BouncyKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Initialise the cipher */
        CipherParameters myParms = new KeyParameter(myKey.getKey());
        if (pIV != null) {
            myParms = new ParametersWithIV(myParms, pIV);
        }
        theMac.init(myParms);

        /* Store key and initVector */
        setKey(pKey);
        setInitVector(pIV);
    }

    @Override
    public int getMacSize() {
        return theMac.getMacSize();
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theMac.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theMac.update(pByte);
    }

    @Override
    public void update(final byte[] pBytes) {
        theMac.update(pBytes, 0, pBytes.length);
    }

    @Override
    public void reset() {
        theMac.reset();
    }

    @Override
    public byte[] finish() {
        byte[] myResult = new byte[getMacSize()];
        theMac.doFinal(myResult, 0);
        return myResult;
    }

    @Override
    public int finish(final byte[] pBuffer,
                      final int pOffset) {
        return theMac.doFinal(pBuffer, pOffset);
    }
}
