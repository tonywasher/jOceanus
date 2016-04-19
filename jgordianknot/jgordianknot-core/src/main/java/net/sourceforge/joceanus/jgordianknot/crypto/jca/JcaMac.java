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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Wrapper for JCA MAC.
 */
public final class JcaMac
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
     * @throws OceanusException on error
     */
    protected JcaMac(final JcaFactory pFactory,
                     final GordianMacSpec pMacSpec,
                     final Mac pMac) throws OceanusException {
        super(pFactory, pMacSpec);
        theMac = pMac;
    }

    @Override
    public JcaKey<GordianMacSpec> getKey() {
        return (JcaKey<GordianMacSpec>) super.getKey();
    }

    @Override
    public void initMac(final GordianKey<GordianMacSpec> pKey,
                        final byte[] pIV) throws OceanusException {
        /* Access and validate the key */
        JcaKey<GordianMacSpec> myKey = JcaKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Protect against exceptions */
        try {
            /* Initialise the MAC */
            if (pIV == null) {
                theMac.init(myKey.getKey());
            } else {
                theMac.init(myKey.getKey(), new IvParameterSpec(pIV));
            }

            /* Catch exceptions */
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to initialise Mac", e);
        }

        /* Store key and initVector */
        setKey(pKey);
        setInitVector(pIV);
    }

    @Override
    public int getMacSize() {
        return theMac.getMacLength();
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
        return theMac.doFinal();
    }

    @Override
    public int finish(final byte[] pBuffer,
                      final int pOffset) throws OceanusException {
        try {
            theMac.doFinal(pBuffer, pOffset);
            return getMacSize();
        } catch (ShortBufferException e) {
            throw new GordianCryptoException("Failed to calculate Mac", e);
        }
    }
}