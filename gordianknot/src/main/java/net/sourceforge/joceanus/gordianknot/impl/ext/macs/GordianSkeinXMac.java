/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.ext.macs;

import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianSkeinBase;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianSkeinXof;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters.GordianSkeinParametersBuilder;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Implementation of the Skein parameterised MAC function in 256, 512 and 1024 bit block sizes,
 * based on the {@link ThreefishEngine Threefish} tweakable block cipher.
 * <p>
 * This is the 1.3 version of Skein defined in the Skein hash function submission to the NIST SHA-3
 * competition in October 2010.
 * <p>
 * Skein was designed by Niels Ferguson - Stefan Lucks - Bruce Schneier - Doug Whiting - Mihir
 * Bellare - Tadayoshi Kohno - Jon Callas - Jesse Walker.
 *
 * @see GordianSkeinBase
 * @see GordianSkeinParameters
 */
public class GordianSkeinXMac
        implements Mac, Xof {
    /**
     * 256 bit block size - Skein MAC-256.
     */
    public static final int SKEIN_256 = GordianSkeinBase.SKEIN_256;

    /**
     * 512 bit block size - Skein MAC-512.
     */
    public static final int SKEIN_512 = GordianSkeinBase.SKEIN_512;

    /**
     * 1024 bit block size - Skein MAC-1024.
     */
    public static final int SKEIN_1024 = GordianSkeinBase.SKEIN_1024;

    /**
     * The engine.
     */
    private final GordianSkeinBase theEngine;

    /**
     * The Xof.
     */
    private final GordianSkeinXof theXof;

    /**
     * Constructs a Skein MAC with an internal state size and output size.
     *
     * @param stateSizeBits  the internal state size in bits - one of {@link #SKEIN_256}, {@link #SKEIN_512} or
     *                       {@link #SKEIN_1024}.
     */
    public GordianSkeinXMac(final int stateSizeBits) {
        this.theEngine = new GordianSkeinBase(stateSizeBits, stateSizeBits);
        this.theXof = new GordianSkeinXof(theEngine);
    }

    /**
     * Copy constructor.
     * @param mac the source mac
     */
    public GordianSkeinXMac(final GordianSkeinXMac mac) {
        this.theEngine = new GordianSkeinBase(mac.theEngine);
        this.theXof = new GordianSkeinXof(theEngine);
    }

    /**
     * Obtain the lgorithm name.
     * @return the name
     */
    public String getAlgorithmName() {
        return "SkeinX-MAC-" + (theEngine.getBlockSize() * Byte.SIZE);
    }

    /**
     * Initialises the Skein digest with the provided parameters.<br>
     * See {@link GordianSkeinParameters} for details on the parameterisation of the Skein hash function.
     *
     * @param params an instance of {@link GordianSkeinParameters} or {@link KeyParameter}.
     */
    public void init(final CipherParameters params)
            throws IllegalArgumentException {
        final GordianSkeinParameters skeinParameters;
        if (params instanceof GordianSkeinParameters) {
            skeinParameters = (GordianSkeinParameters) params;
        } else if (params instanceof KeyParameter) {
            skeinParameters = new GordianSkeinParametersBuilder().setKey(((KeyParameter) params).getKey()).build();
        } else {
            throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - "
                    + params.getClass().getName());
        }
        if (skeinParameters.getKey() == null) {
            throw new IllegalArgumentException("Skein MAC requires a key parameter.");
        }
        theEngine.init(skeinParameters);
    }

    @Override
    public int getMacSize() {
        return theEngine.getOutputSize();
    }

    @Override
    public void reset() {
        theXof.reset();
    }

    @Override
    public void update(final byte in) {
        theXof.update(in);
    }

    @Override
    public void update(final byte[] in,
                       final int inOff,
                       final int len) {
        theXof.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out,
                       final int outOff) {
        return theXof.doFinal(out, outOff);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff, final int outLen) {
        return theXof.doFinal(out, outOff, outLen);
    }

    @Override
    public int doOutput(final byte[] out, final int outOff, final int outLen) {
        return theXof.doOutput(out, outOff, outLen);
    }

    @Override
    public int getByteLength() {
        return theXof.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return theXof.getDigestSize();
    }
}
