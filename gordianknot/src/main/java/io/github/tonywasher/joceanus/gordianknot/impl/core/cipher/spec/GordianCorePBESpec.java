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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.spec;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBEType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;

import java.util.Objects;

/**
 * PBE Specification.
 */
public class GordianCorePBESpec
        implements GordianNewPBESpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The PBEType.
     */
    private final GordianNewPBEType theType;

    /**
     * is the Spec valid?
     */
    private boolean isValid;

    /**
     * Constructor.
     *
     * @param pPBEType the PBEType.
     */
    GordianCorePBESpec(final GordianNewPBEType pPBEType) {
        theType = pPBEType;
    }

    @Override
    public GordianNewPBEType getPBEType() {
        return theType;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Set as valid.
     */
    void setValid() {
        isValid = true;
    }

    /**
     * DigestAndCountSpec.
     */
    static class GordianCorePBEDigestAndCountSpec
            extends GordianCorePBESpec
            implements GordianNewPBEDigestAndCountSpec {
        /**
         * The DigestSpec.
         */
        private final GordianNewDigestSpec theDigestSpec;

        /**
         * The count.
         */
        private final int theCount;

        /**
         * Constructor.
         *
         * @param pPBEType    the PBEType.
         * @param pDigestSpec the digestSpec.
         * @param pCount      the iteration count
         */
        GordianCorePBEDigestAndCountSpec(final GordianNewPBEType pPBEType,
                                         final GordianNewDigestSpec pDigestSpec,
                                         final int pCount) {
            /* Init underlying class and store params */
            super(pPBEType);
            theDigestSpec = pDigestSpec;
            theCount = pCount;

            /* Check validity */
            checkValidity();
        }

        @Override
        public GordianNewDigestSpec getDigestSpec() {
            return theDigestSpec;
        }

        @Override
        public int getIterationCount() {
            return theCount;
        }

        /**
         * Check validity.
         */
        private void checkValidity() {
            /* Check PBEType */
            if (getPBEType() != GordianNewPBEType.PBKDF2
                    && getPBEType() != GordianNewPBEType.PKCS12) {
                return;
            }

            /* Check DigestSpec and Count > 0 */
            if (theDigestSpec != null
                    && theDigestSpec.isValid()
                    && theCount > 0) {
                setValid();
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check count, digestSpec and PBEType */
            return pThat instanceof GordianCorePBEDigestAndCountSpec myThat
                    && theCount == myThat.getIterationCount()
                    && theDigestSpec.equals(myThat.getDigestSpec())
                    && getPBEType() == myThat.getPBEType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(theDigestSpec, theCount, getPBEType());
        }

        @Override
        public String toString() {
            return getPBEType().toString() + SEP + theDigestSpec.toString() + SEP + theCount;
        }
    }

    /**
     * SCryptSpec.
     */
    static class GordianCorePBESCryptSpec
            extends GordianCorePBESpec
            implements GordianNewPBESCryptSpec {
        /**
         * Max Small Block Cost.
         */
        private static final int MAX_SMALL_COST = 0xFFFF;

        /**
         * Parallel limit.
         */
        private static final int PARALLEL_LIMIT = 128;

        /**
         * The BlockSize.
         */
        private final int theBlockSize;

        /**
         * The cost.
         */
        private final int theCost;

        /**
         * The Parallelism.
         */
        private final int theParallel;

        /**
         * Constructor.
         *
         * @param pCost      the cost
         * @param pBlockSize the blockSize
         * @param pParallel  the parallelism
         */
        GordianCorePBESCryptSpec(final int pCost,
                                 final int pBlockSize,
                                 final int pParallel) {
            /* Init underlying class and store params */
            super(GordianNewPBEType.SCRYPT);
            theCost = pCost;
            theBlockSize = pBlockSize;
            theParallel = pParallel;

            /* Check validity */
            checkValidity();
        }

        @Override
        public int getBlockSize() {
            return theBlockSize;
        }

        @Override
        public int getCost() {
            return theCost;
        }

        @Override
        public int getParallel() {
            return theParallel;
        }

        /**
         * Check validity.
         */
        private void checkValidity() {
            /* Check BlockSize is > 0 */
            if (theBlockSize <= 0) {
                return;
            }

            /* Check Cost is > 1 and power of two */
            if (theCost <= 1
                    || (theCost & (theCost - 1)) != 0) {
                return;
            }

            /* Check Cost restriction for BlockSize of 1 */
            if (theBlockSize == 1
                    && theCost > MAX_SMALL_COST) {
                return;
            }

            /* Check Parallel restriction */
            final int maxParallel = Integer.MAX_VALUE / (PARALLEL_LIMIT * theBlockSize * Byte.SIZE);
            if (theParallel >= 1
                    && theParallel <= maxParallel) {
                setValid();
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check cost, blockSize and parallel */
            return pThat instanceof GordianCorePBESCryptSpec myThat
                    && theCost == myThat.getCost()
                    && theBlockSize == myThat.getBlockSize()
                    && theParallel == myThat.getParallel()
                    && getPBEType() == myThat.getPBEType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(theBlockSize, theCost, theParallel, getPBEType());
        }

        @Override
        public String toString() {
            return getPBEType().toString() + SEP + theBlockSize + SEP + theCost + SEP + theParallel;
        }
    }

    /**
     * Argon2Spec.
     */
    static class GordianCorePBEArgon2Spec
            extends GordianCorePBESpec
            implements GordianNewPBEArgon2Spec {
        /**
         * The Memory.
         */
        private final int theMemory;

        /**
         * The lanes.
         */
        private final int theLanes;

        /**
         * The Iterations.
         */
        private final int theIterations;

        /**
         * Constructor.
         *
         * @param pLanes      the Lanes
         * @param pMemory     the Memory
         * @param pIterations the iterations
         */
        GordianCorePBEArgon2Spec(final int pLanes,
                                 final int pMemory,
                                 final int pIterations) {
            /* Init underlying class and store params */
            super(GordianNewPBEType.ARGON2);
            theLanes = pLanes;
            theMemory = pMemory;
            theIterations = pIterations;

            /* Check validity */
            checkValidity();
        }

        @Override
        public int getLanes() {
            return theLanes;
        }

        @Override
        public int getMemory() {
            return theMemory;
        }

        @Override
        public int getIterationCount() {
            return theIterations;
        }

        /**
         * Check validity.
         */
        private void checkValidity() {
            /* Check Iterations and Lanes are > 0 */
            if (theIterations <= 0
                    || theLanes <= 0) {
                return;
            }

            /* Check Memory is >= 2 * lanes */
            if (theMemory >= theLanes << 1) {
                setValid();
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check lanes, memory and iterations */
            return pThat instanceof GordianCorePBEArgon2Spec myThat
                    && theLanes == myThat.getLanes()
                    && theMemory == myThat.getMemory()
                    && theIterations == myThat.getIterationCount()
                    && getPBEType() == myThat.getPBEType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(theLanes, theMemory, theIterations, getPBEType());
        }

        @Override
        public String toString() {
            return getPBEType().toString() + SEP + theLanes + SEP + theMemory + SEP + theIterations;
        }
    }
}
