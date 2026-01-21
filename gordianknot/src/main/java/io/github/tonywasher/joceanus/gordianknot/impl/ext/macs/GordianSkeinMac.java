/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.ext.macs;

import io.github.tonywasher.joceanus.gordianknot.impl.ext.digests.GordianSkeinBase;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters.GordianSkeinParametersBuilder;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
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
        implements Mac {
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
     * Constructs a Skein MAC with an internal state size and output size.
     *
     * @param stateSizeBits  the internal state size in bits - one of {@link #SKEIN_256}, {@link #SKEIN_512} or
     *                       {@link #SKEIN_1024}.
     * @param digestSizeBits the output/MAC size to produce in bits, which must be an integral number of bytes.
     */
    public GordianSkeinMac(final int stateSizeBits,
                           final int digestSizeBits) {
        this.theEngine = new GordianSkeinBase(stateSizeBits, digestSizeBits);
    }

    /**
     * Copy constructor.
     *
     * @param mac the source mac
     */
    public GordianSkeinMac(final GordianSkeinMac mac) {
        this.theEngine = new GordianSkeinBase(mac.theEngine);
    }

    /**
     * Obtain the lgorithm name.
     *
     * @return the name
     */
    public String getAlgorithmName() {
        return "Skein-MAC-" + (theEngine.getBlockSize() * Byte.SIZE) + "-" + (theEngine.getOutputSize() * Byte.SIZE);
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
        theEngine.reset();
    }

    @Override
    public void update(final byte in) {
        theEngine.update(in);
    }

    @Override
    public void update(final byte[] in,
                       final int inOff,
                       final int len) {
        theEngine.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out,
                       final int outOff) {
        return theEngine.doFinal(out, outOff);
    }
}
