package org.bouncycastle.crypto.ext.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.ext.digests.SkeinBase;
import org.bouncycastle.crypto.ext.digests.SkeinXof;
import org.bouncycastle.crypto.ext.params.SkeinXParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SkeinParameters;

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
 * @see SkeinBase
 * @see SkeinParameters
 */
public class SkeinMac
        implements Mac, Xof
{
    /**
     * 256 bit block size - Skein MAC-256
     */
    public static final int SKEIN_256 = SkeinBase.SKEIN_256;
    /**
     * 512 bit block size - Skein MAC-512
     */
    public static final int SKEIN_512 = SkeinBase.SKEIN_512;
    /**
     * 1024 bit block size - Skein MAC-1024
     */
    public static final int SKEIN_1024 = SkeinBase.SKEIN_1024;

    private SkeinBase engine;
    private SkeinXof xof;

    /**
     * Constructs a Skein MAC with an internal state size and output size.
     *
     * @param stateSizeBits  the internal state size in bits - one of {@link #SKEIN_256}, {@link #SKEIN_512} or
     *                       {@link #SKEIN_1024}.
     * @param digestSizeBits the output/MAC size to produce in bits, which must be an integral number of bytes.
     */
    public SkeinMac(int stateSizeBits, int digestSizeBits)
    {
        this.engine = new SkeinBase(stateSizeBits, digestSizeBits);
        this.xof = new SkeinXof(engine);
    }

    public SkeinMac(SkeinMac mac)
    {
        this.engine = new SkeinBase(mac.engine);
        this.xof = new SkeinXof(engine);
    }

    public String getAlgorithmName()
    {
        return "Skein-MAC-" + (engine.getBlockSize() * 8) + "-" + (engine.getOutputSize() * 8);
    }

    /**
     * Initialises the Skein digest with the provided parameters.<br>
     * See {@link SkeinParameters} for details on the parameterisation of the Skein hash function.
     *
     * @param params an instance of {@link SkeinParameters} or {@link KeyParameter}.
     */
    public void init(CipherParameters params)
            throws IllegalArgumentException
    {
        SkeinXParameters skeinParameters;
        if (params instanceof SkeinXParameters)
        {
            skeinParameters = (SkeinXParameters)params;
        }
        else if (params instanceof KeyParameter)
        {
            skeinParameters = new SkeinXParameters.Builder().setKey(((KeyParameter)params).getKey()).build();
        }
        else
        {
            throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - "
                    + params.getClass().getName());
        }
        if (skeinParameters.getKey() == null)
        {
            throw new IllegalArgumentException("Skein MAC requires a key parameter.");
        }
        engine.init(skeinParameters);
    }

    public int getMacSize()
    {
        return engine.getOutputSize();
    }

    public void reset()
    {
        xof.reset();
    }

    public void update(byte in)
    {
        xof.update(in);
    }

    public void update(byte[] in, int inOff, int len)
    {
        xof.update(in, inOff, len);
    }

    public int doFinal(byte[] out, int outOff)
    {
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
