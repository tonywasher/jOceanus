/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.mac;

import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianNewMacParams;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import org.bouncycastle.util.Arrays;

/**
 * Core Mac Parameters.
 */
public class GordianCoreMacParams
        implements GordianNewMacParams {
    /**
     * The Key.
     */
    private GordianKey<GordianMacSpec> theKey;

    /**
     * The Nonce.
     */
    private byte[] theNonce;

    /**
     * Random Nonce requested?
     */
    private boolean randomNonce;

    /**
     * Personalisation.
     */
    private byte[] thePersonal;

    /**
     * Output length.
     */
    private long theOutLen;

    /**
     * The fanOut.
     */
    private short theFanOut;

    /**
     * The maxDepth.
     */
    private short theMaxDepth;

    /**
     * The leafLength.
     */
    private int theLeafLen;

    /**
     * Constructor.
     */
    GordianCoreMacParams() {
    }

    @Override
    public GordianKey<GordianMacSpec> getKey() {
        return theKey;
    }

    /**
     * Set the key.
     *
     * @param pKey the key
     */
    void setKey(final GordianKey<GordianMacSpec> pKey) {
        theKey = pKey;
    }

    @Override
    public byte[] getNonce() {
        return Arrays.clone(theNonce);
    }

    /**
     * Set the nonce.
     *
     * @param pNonce the nonce
     */
    void setNonce(final byte[] pNonce) {
        theNonce = Arrays.clone(pNonce);
    }

    @Override
    public boolean randomNonce() {
        return randomNonce;
    }

    /**
     * Set the random nonce.
     */
    void setRandomNonce() {
        randomNonce = true;
    }

    @Override
    public byte[] getPersonal() {
        return Arrays.clone(thePersonal);
    }

    /**
     * Set the personalisation.
     *
     * @param pPersonal the personalisation
     */
    void setPersonal(final byte[] pPersonal) {
        thePersonal = Arrays.clone(pPersonal);
    }

    @Override
    public long getOutputLength() {
        return theOutLen;
    }

    /**
     * Set the output length.
     *
     * @param pLength the output length
     */
    void setOutLen(final long pLength) {
        theOutLen = pLength;
    }

    @Override
    public int getTreeLeafLen() {
        return theLeafLen;
    }

    /**
     * Set the tree leaf length.
     *
     * @param pLength the leaf length
     */
    void setTreeLeafLen(final int pLength) {
        theLeafLen = pLength;
    }

    @Override
    public short getTreeFanOut() {
        return theFanOut;
    }

    /**
     * Set the tree fanOut.
     *
     * @param pFanOut the fanOut
     */
    void setTreeFanOut(final short pFanOut) {
        theFanOut = pFanOut;
    }

    @Override
    public short getTreeMaxDepth() {
        return theMaxDepth;
    }

    /**
     * Set the tree max depth.
     *
     * @param pDepth the max depth
     */
    void setTreeMaxDepth(final short pDepth) {
        theMaxDepth = pDepth;
    }
}
