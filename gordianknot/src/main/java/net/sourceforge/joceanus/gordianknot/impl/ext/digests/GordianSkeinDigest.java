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
package net.sourceforge.joceanus.gordianknot.impl.ext.digests;

import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.util.Memoable;

/**
 * Implementation of the Skein parameterised hash function in 256, 512 and 1024 bit block sizes,
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
public class GordianSkeinDigest
        implements ExtendedDigest, Memoable {
    /**
     * 256 bit block size - Skein-256.
     */
    public static final int SKEIN_256 = GordianSkeinBase.SKEIN_256;
    /**
     * 512 bit block size - Skein-512.
     */
    public static final int SKEIN_512 = GordianSkeinBase.SKEIN_512;
    /**
     * 1024 bit block size - Skein-1024.
     */
    public static final int SKEIN_1024 = GordianSkeinBase.SKEIN_1024;

    /**
     * The underlying engine.
     */
    private GordianSkeinBase engine;

    /**
     * Constructs a Skein digest with an internal state size and output size.
     *
     * @param stateSizeBits  the internal state size in bits - one of {@link #SKEIN_256}, {@link #SKEIN_512} or
     *                       {@link #SKEIN_1024}.
     * @param digestSizeBits the output/digest size to produce in bits, which must be an integral number of
     *                       bytes.
     */
    public GordianSkeinDigest(final int stateSizeBits, final int digestSizeBits) {
        this.engine = new GordianSkeinBase(stateSizeBits, digestSizeBits);
        init(null);
    }

    /**
     * Constructor.
     * @param digest the digest to copy
     */
    public GordianSkeinDigest(final GordianSkeinDigest digest) {
        this.engine = new GordianSkeinBase(digest.engine);
    }

    @Override
    public void reset(final Memoable other) {
        final GordianSkeinDigest d = (GordianSkeinDigest) other;
        engine.reset(d.engine);
    }

    @Override
    public Memoable copy() {
        return new GordianSkeinDigest(this);
    }

    @Override
    public String getAlgorithmName() {
        return "Skein-" + (engine.getBlockSize() * Byte.SIZE) + "-" + (engine.getOutputSize() * Byte.SIZE);
    }

    @Override
    public int getDigestSize() {
        return engine.getOutputSize();
    }

    @Override
    public int getByteLength() {
        return engine.getBlockSize();
    }

    /**
     * Obtain the base.
     * @return the base
     */
    GordianSkeinBase getBase() {
        return engine;
    }

    /**
     * Optionally initialises the Skein digest with the provided parameters.<br>
     * See {@link GordianSkeinParameters} for details on the parameterisation of the Skein hash function.
     *
     * @param params the parameters to apply to this engine, or <code>null</code> to use no parameters.
     */
    public void init(final GordianSkeinParameters params) {
        engine.init(params);
    }

    @Override
    public void reset() {
        engine.reset();
    }

    @Override
    public void update(final byte in) {
        engine.update(in);
    }

    @Override
    public void update(final byte[] in, final int inOff, final int len) {
        engine.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff) {
        return engine.doFinal(out, outOff);
    }
}
