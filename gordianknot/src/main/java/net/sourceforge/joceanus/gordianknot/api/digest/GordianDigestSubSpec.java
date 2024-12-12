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
package net.sourceforge.joceanus.gordianknot.api.digest;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;

/**
 * Digest SubSpec.
 */
public interface GordianDigestSubSpec {
    /**
     * Obtain the possible subSpecTypes for the digestType.
     * @param pType the digestType
     * @return the subSpec types
     */
    static GordianDigestSubSpec[] getPossibleSubSpecsForType(final GordianDigestType pType) {
        switch (pType) {
            case SHA2:
            case BLAKE2:
            case HARAKA:
                return new GordianDigestState[] { GordianDigestState.STATE256, GordianDigestState.STATE512 };
            case SKEIN:
                return new GordianDigestState[] { GordianDigestState.STATE256, GordianDigestState.STATE512, GordianDigestState.STATE1024 };
            case SHAKE:
            case KANGAROO:
                return new GordianDigestState[] { GordianDigestState.STATE128, GordianDigestState.STATE256 };
            default:
                return new GordianDigestState[] { null };
        }
    }

    /**
     * Obtain the subSpec for the type and length.
     * @param pType the digestType
     * @param pLength the length
     * @return the subSpec
     */
    static GordianDigestSubSpec getDefaultSubSpecForTypeAndLength(final GordianDigestType pType,
                                                                  final GordianLength pLength) {
        switch (pType) {
            case SHA2:
                return pLength == GordianLength.LEN_224 || pLength == GordianLength.LEN_256
                        ? GordianDigestState.STATE256
                        : GordianDigestState.STATE512;
            case SKEIN:
                switch (pLength) {
                    case LEN_1024:
                        return GordianDigestState.STATE1024;
                    case LEN_512:
                    case LEN_384:
                        return GordianDigestState.STATE512;
                    default:
                        return GordianDigestState.STATE256;
                }
            case SHAKE:
            case KANGAROO:
                return pLength == GordianLength.LEN_256
                        ? GordianDigestState.STATE128
                        : GordianDigestState.STATE256;
            case BLAKE2:
                return pLength == GordianLength.LEN_128 || pLength == GordianLength.LEN_224
                        ? GordianDigestState.STATE256
                        : GordianDigestState.STATE512;
            case HARAKA:
                return GordianDigestState.STATE256;
            default:
                return null;
        }
    }

    /**
     * State subSpecification.
     */
    enum GordianDigestState implements GordianDigestSubSpec {
        /**
         * 128.
         */
        STATE128(GordianLength.LEN_128),

        /**
         * 256.
         */
        STATE256(GordianLength.LEN_256),

        /**
         * 512.
         */
        STATE512(GordianLength.LEN_512),

        /**
         * 1024.
         */
        STATE1024(GordianLength.LEN_1024);

        /**
         * The length.
         */
        private final GordianLength theLength;

        /**
         * Constructor.
         * @param pLength the length
         */
        GordianDigestState(final GordianLength pLength) {
            theLength = pLength;
        }

        /**
         * Obtain length for state.
         * @return the length
         */
        public GordianLength getLength() {
            return theLength;
        }

        @Override
        public String toString() {
            return theLength.toString();
        }

        /**
         * Is this state a hybrid for sha2 length?
         * @param pLength the length
         * @return true/false
         */
        public boolean isSha2Hybrid(final GordianLength pLength) {
            return this == STATE512
                    && (GordianLength.LEN_224.equals(pLength)
                    || GordianLength.LEN_256.equals(pLength));
        }

        /**
         * Obtain the length for an explicit Xof variant.
         * @param pType the digestType
         * @return the length
         */
        GordianLength lengthForXofType(final GordianDigestType pType) {
            switch (pType) {
                case SKEIN:
                    switch (this) {
                        case STATE256:
                        case STATE512:
                        case STATE1024:
                            return theLength;
                        default:
                            return null;
                    }
                case BLAKE2:
                    switch (this) {
                        case STATE256:
                        case STATE512:
                            return theLength;
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        }

        /**
         * is length available for this type and length?
         * @param pType the digestType
         * @param pLength the length
         * @return true/false
         */
        boolean validForTypeAndLength(final GordianDigestType pType,
                                      final GordianLength pLength) {
            switch (pType) {
                case SHA2:
                    return validForSha2Length(pLength);
                case SHAKE:
                case KANGAROO:
                    return validForSHAKELength(pLength);
                case SKEIN:
                    return validForSkeinLength(pLength);
                case BLAKE2:
                    return validForBlake2Length(pLength);
                case HARAKA:
                    return validForHarakaLength(pLength);
                default:
                    return false;
            }
        }

        /**
         * Is this state valid for the skeinLength?
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSha2Length(final GordianLength pLength) {
            switch (this) {
                case STATE512:
                    switch (pLength) {
                        case LEN_224:
                        case LEN_256:
                        case LEN_384:
                        case LEN_512:
                            return true;
                        default:
                            return false;
                    }
                case STATE256:
                    switch (pLength) {
                        case LEN_224:
                        case LEN_256:
                            return true;
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        }

        /**
         * Is this state valid for the skeinLength?
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSHAKELength(final GordianLength pLength) {
            switch (this) {
                case STATE256:
                    return pLength == GordianLength.LEN_512;
                case STATE128:
                    return pLength == GordianLength.LEN_256;
                default:
                    return false;
            }
        }

        /**
         * Is this state valid for the skeinLength?
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSkeinLength(final GordianLength pLength) {
            switch (this) {
                case STATE1024:
                    switch (pLength) {
                        case LEN_384:
                        case LEN_512:
                        case LEN_1024:
                            return true;
                        default:
                            return false;
                    }
                case STATE512:
                    switch (pLength) {
                        case LEN_128:
                        case LEN_160:
                        case LEN_224:
                        case LEN_256:
                        case LEN_384:
                        case LEN_512:
                            return true;
                        default:
                            return false;
                    }
                case STATE256:
                    switch (pLength) {
                        case LEN_128:
                        case LEN_160:
                        case LEN_224:
                        case LEN_256:
                            return true;
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        }

        /**
         * Is this state valid for the blake2Length.
         * @param pLength the length
         * @return true/false
         */
        private boolean validForBlake2Length(final GordianLength pLength) {
            switch (this) {
                case STATE512:
                    switch (pLength) {
                        case LEN_160:
                        case LEN_256:
                        case LEN_384:
                        case LEN_512:
                            return true;
                        default:
                            return false;
                    }
                case STATE256:
                    switch (pLength) {
                        case LEN_128:
                        case LEN_160:
                        case LEN_224:
                        case LEN_256:
                            return true;
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        }

        /**
         * Is this state valid for the harakaLength.
         * @param pLength the length
         * @return true/false
         */
        private boolean validForHarakaLength(final GordianLength pLength) {
            switch (this) {
                case STATE512:
                case STATE256:
                    return pLength == GordianLength.LEN_256;
                default:
                    return false;
            }
        }

        /**
         * Is this the Blake2b algorithm?
         * @return true/false
         */
        public boolean isBlake2bState() {
            return GordianDigestState.STATE512.equals(this);
        }

        /**
         * Obtain the blake2Algorithm name for State.
         * @param pXofMode is this a Xof variant
         * @return the algorithm name
         */
        public String getBlake2Algorithm(final boolean pXofMode) {
            return (pXofMode ? "X" : "")
                    + (isBlake2bState() ? "b" : "s");
        }

        /**
         * Obtain the kangarooAlgorithm name for State.
         * @return the algorithmName
         */
        String getKangarooAlgorithm() {
            return this == STATE256
                    ? GordianDigestResource.DIGEST_MARSUPILAMI.getValue()
                    : GordianDigestResource.DIGEST_KANGAROO.getValue();
        }
    }
}
