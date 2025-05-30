/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;

import java.util.Objects;

/**
 * PBE Specification.
 */
public abstract class GordianPBESpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The PBEType.
     */
    private final GordianPBEType theType;

    /**
     * is the Spec valid?
     */
    private boolean isValid;

    /**
     * Constructor.
     * @param pPBEType the PBEType.
     */
    GordianPBESpec(final GordianPBEType pPBEType) {
        theType = pPBEType;
    }

    /**
     * Obtain the PBEType.
     * @return the PBEType
     */
    public GordianPBEType getPBEType() {
        return theType;
    }

    /**
     * Is the Spec valid?
     * @return true/false
     */
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
    public static class GordianPBEDigestAndCountSpec
            extends GordianPBESpec {
        /**
         * The DigestSpec.
         */
        private final GordianDigestSpec theDigestSpec;

        /**
         * The count.
         */
        private final int theCount;

        /**
         * Constructor.
         * @param pPBEType the PBEType.
         * @param pDigestSpec the digestSpec.
         * @param pCount the iteration count
         */
        GordianPBEDigestAndCountSpec(final GordianPBEType pPBEType,
                                     final GordianDigestSpec pDigestSpec,
                                     final int pCount) {
            /* Init underlying class and store params */
            super(pPBEType);
            theDigestSpec = pDigestSpec;
            theCount = pCount;

            /* Check validity */
            checkValidity();
        }

        /**
         * Obtain the digestSpec.
         * @return the digestSpec
         */
        public GordianDigestSpec getDigestSpec() {
            return theDigestSpec;
        }

        /**
         * Obtain the iteration count.
         * @return the count
         */
        public int getIterationCount() {
            return theCount;
        }

        /**
         * Check validity.
         */
        private void checkValidity() {
            /* Check PBEType */
            if (getPBEType() != GordianPBEType.PBKDF2
                && getPBEType() != GordianPBEType.PKCS12) {
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
            return pThat instanceof GordianPBEDigestAndCountSpec myThat
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
    public static class GordianPBESCryptSpec
            extends GordianPBESpec {
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
         * @param pCost the cost
         * @param pBlockSize the blockSize
         * @param pParallel the parallelism
         */
        GordianPBESCryptSpec(final int pCost,
                             final int pBlockSize,
                             final int pParallel) {
            /* Init underlying class and store params */
            super(GordianPBEType.SCRYPT);
            theCost = pCost;
            theBlockSize = pBlockSize;
            theParallel = pParallel;

            /* Check validity */
            checkValidity();
        }

        /**
         * Obtain the blockSize.
         * @return the blockSize
         */
        public int getBlockSize() {
            return theBlockSize;
        }

        /**
         * Obtain the cost.
         * @return the cost
         */
        public int getCost() {
            return theCost;
        }

        /**
         * Obtain the parallelism.
         * @return the parallelism
         */
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
            return pThat instanceof GordianPBESCryptSpec myThat
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
    public static class GordianPBEArgon2Spec
            extends GordianPBESpec {
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
         * @param pLanes the Lanes
         * @param pMemory the Memory
         * @param pIterations the iterations
         */
        GordianPBEArgon2Spec(final int pLanes,
                             final int pMemory,
                             final int pIterations) {
            /* Init underlying class and store params */
            super(GordianPBEType.ARGON2);
            theLanes = pLanes;
            theMemory = pMemory;
            theIterations = pIterations;

            /* Check validity */
            checkValidity();
        }

        /**
         * Obtain the lanes.
         * @return the lanes
         */
        public int getLanes() {
            return theLanes;
        }

        /**
         * Obtain the memory.
         * @return the memory
         */
        public int getMemory() {
            return theMemory;
        }

        /**
         * Obtain the iteration count.
         * @return the count
         */
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
            return pThat instanceof GordianPBEArgon2Spec myThat
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
