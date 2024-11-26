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
package net.sourceforge.joceanus.gordianknot.impl.ext.macs;

import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianSkeinBase;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianSkeinXof;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters;
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
public class GordianSkeinMac
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
    private final GordianSkeinBase engine;

    /**
     * The Xof.
     */
    private final GordianSkeinXof xof;

    /**
     * Constructs a Skein MAC with an internal state size and output size.
     *
     * @param stateSizeBits  the internal state size in bits - one of {@link #SKEIN_256}, {@link #SKEIN_512} or
     *                       {@link #SKEIN_1024}.
     * @param digestSizeBits the output/MAC size to produce in bits, which must be an integral number of bytes.
     */
    public GordianSkeinMac(final int stateSizeBits,
                           final int digestSizeBits) {
        this.engine = new GordianSkeinBase(stateSizeBits, digestSizeBits);
        this.xof = new GordianSkeinXof(engine);
    }

    /**
     * Copy constructor.
     * @param mac the source mac
     */
    public GordianSkeinMac(final GordianSkeinMac mac) {
        this.engine = new GordianSkeinBase(mac.engine);
        this.xof = new GordianSkeinXof(engine);
    }

    /**
     * Obtain the lgorithm name.
     * @return the name
     */
    public String getAlgorithmName() {
        return "Skein-MAC-" + (engine.getBlockSize() * Byte.SIZE) + "-" + (engine.getOutputSize() * Byte.SIZE);
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
            skeinParameters = new GordianSkeinParameters.Builder().setKey(((KeyParameter) params).getKey()).build();
        } else {
            throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - "
                    + params.getClass().getName());
        }
        if (skeinParameters.getKey() == null) {
            throw new IllegalArgumentException("Skein MAC requires a key parameter.");
        }
        engine.init(skeinParameters);
    }

    @Override
    public int getMacSize() {
        return engine.getOutputSize();
    }

    @Override
    public void reset() {
        xof.reset();
    }

    @Override
    public void update(final byte in) {
        xof.update(in);
    }

    @Override
    public void update(final byte[] in,
                       final int inOff,
                       final int len) {
        xof.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out,
                       final int outOff) {
        return xof.doFinal(out, outOff);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff, final int outLen) {
        return xof.doFinal(out, outOff, outLen);
    }

    @Override
    public int doOutput(final byte[] out, final int outOff, final int outLen) {
        return xof.doOutput(out, outOff, outLen);
    }

    @Override
    public int getByteLength() {
        return xof.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return xof.getDigestSize();
    }
}
