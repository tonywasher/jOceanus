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

package io.github.tonywasher.joceanus.gordianknot.api.keypair.spec;

/**
 * XMSS KeySpec.
 */
public interface GordianXMSSSpec {
    /**
     * Obtain the keyType.
     *
     * @return the keyType
     */
    GordianXMSSKeyType getKeyType();

    /**
     * Obtain the digestType.
     *
     * @return the digestType
     */
    GordianXMSSDigestType getDigestType();

    /**
     * Obtain the height.
     *
     * @return the height
     */
    GordianXMSSHeight getHeight();

    /**
     * Obtain the layers.
     *
     * @return the layers
     */
    GordianXMSSMTLayers getLayers();

    /**
     * Is this an XMSSMT spec?
     *
     * @return true/false.
     */
    boolean isMT();

    /**
     * Is the keySpec valid?
     *
     * @return true/false.
     */
    boolean isValid();

    /**
     * XMSS keyTypes.
     */
    enum GordianXMSSKeyType {
        /**
         * XMSS.
         */
        XMSS,

        /**
         * XMSSMT.
         */
        XMSSMT;
    }

    /**
     * XMSS digestType.
     */
    enum GordianXMSSDigestType {
        /**
         * SHA256.
         */
        SHA256,

        /**
         * SHA512.
         */
        SHA512,

        /**
         * SHAKE128.
         */
        SHAKE128,

        /**
         * SHAKE256.
         */
        SHAKE256;
    }

    /**
     * XMSS Height.
     */
    enum GordianXMSSHeight {
        /**
         * 10.
         */
        H10(10),

        /**
         * 16.
         */
        H16(16),

        /**
         * 20.
         */
        H20(20),

        /**
         * 40.
         */
        H40(40),

        /**
         * 60.
         */
        H60(60);

        /**
         * The Height.
         */
        private final int theHeight;

        /**
         * Constructor.
         *
         * @param pHeight the height
         */
        GordianXMSSHeight(final int pHeight) {
            theHeight = pHeight;
        }

        /**
         * Obtain the height.
         *
         * @return the height
         */
        public int getHeight() {
            return theHeight;
        }
    }

    /**
     * XMSSMT Layers.
     */
    enum GordianXMSSMTLayers {
        /**
         * 2.
         */
        L2(2),

        /**
         * 3.
         */
        L3(3),

        /**
         * 4.
         */
        L4(4),

        /**
         * 6.
         */
        L6(6),

        /**
         * 40.
         */
        L8(8),

        /**
         * 12.
         */
        L12(12);

        /**
         * The layers.
         */
        private final int theLayers;

        /**
         * Constructor.
         *
         * @param pLayers the layers
         */
        GordianXMSSMTLayers(final int pLayers) {
            theLayers = pLayers;
        }

        /**
         * Obtain the layers.
         *
         * @return the layers
         */
        public int getLayers() {
            return theLayers;
        }
    }
}
