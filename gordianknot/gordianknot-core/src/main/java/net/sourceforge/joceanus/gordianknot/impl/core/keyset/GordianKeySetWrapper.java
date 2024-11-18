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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianMultiCipher.GordianSymKeyCipherSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for KeySetWrapper.
 * <p>
 * This class extends GordianCoreWrapper to wrap using a keySet rather than a key.
 */
public class GordianKeySetWrapper
        extends GordianCoreWrapper {
    /**
     * The cipher array.
     */
    private final GordianSymKeyCipherSet[] theCiphers;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCiphers the CipherSets
     */
    GordianKeySetWrapper(final GordianCoreFactory pFactory,
                         final GordianSymKeyCipherSet[] pCiphers) {
        super(pFactory, GordianLength.LEN_128.getByteLength());
        theCiphers = pCiphers;
    }

    @Override
    protected void initCipherForWrapping() throws OceanusException {
        for (GordianSymKeyCipherSet myCipher : theCiphers) {
            myCipher.getStandardCipher().initForEncrypt(GordianCipherParameters.key(myCipher.getKey()));
        }
    }

    @Override
    protected void initCipherForUnwrapping() throws OceanusException {
        for (GordianSymKeyCipherSet myCipher : theCiphers) {
            myCipher.getStandardCipher().initForDecrypt(GordianCipherParameters.key(myCipher.getKey()));
        }
    }

    @Override
    protected void iterateCipher(final byte[] pInBuffer,
                                 final int pBufferLen,
                                 final byte[] pResult) throws OceanusException {
        byte[] myInBuffer = pInBuffer;
        final byte[][] myTempBuffers = { new byte[pBufferLen], new byte[pBufferLen] };
        int myNextIndex = 0;
        byte[] myOutBuffer = myTempBuffers[myNextIndex++];
        for (GordianSymKeyCipherSet myCipher : theCiphers) {
            myCipher.getStandardCipher().finish(myInBuffer, 0, pBufferLen, myOutBuffer, 0);
            myInBuffer = myOutBuffer;
            myNextIndex %= 2;
            myOutBuffer = myTempBuffers[myNextIndex++];
        }
        System.arraycopy(myInBuffer, 0, pResult, 0, pBufferLen);
    }
}
