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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Digest SubSpec.
 */
public interface GordianCoreDigestSubSpec {
    /**
     * Obtain the subSpec.
     *
     * @return the subSpec
     */
    GordianDigestSubSpec getSubSpec();

    /**
     * Obtain the possible subSpecTypes for the digestType.
     *
     * @param pType the digestType
     * @return the subSpec types
     */
    static GordianDigestSubSpec[] getPossibleSubSpecsForType(final GordianDigestType pType) {
        return switch (pType) {
            case SHA2, BLAKE2, HARAKA ->
                    new GordianDigestState[]{GordianDigestState.STATE256, GordianDigestState.STATE512};
            case SKEIN ->
                    new GordianDigestState[]{GordianDigestState.STATE256, GordianDigestState.STATE512, GordianDigestState.STATE1024};
            case SHAKE, KANGAROO -> new GordianDigestState[]{GordianDigestState.STATE128, GordianDigestState.STATE256};
            default -> new GordianDigestState[]{null};
        };
    }

    /**
     * Obtain the subSpec for the type and length.
     *
     * @param pType   the digestType
     * @param pLength the length
     * @return the subSpec
     */
    static GordianDigestSubSpec getDefaultSubSpecForTypeAndLength(final GordianDigestType pType,
                                                                  final GordianLength pLength) {
        return switch (pType) {
            case SHA2 -> pLength == GordianLength.LEN_224 || pLength == GordianLength.LEN_256
                    ? GordianDigestState.STATE256
                    : GordianDigestState.STATE512;
            case SKEIN -> switch (pLength) {
                case LEN_1024 -> GordianDigestState.STATE1024;
                case LEN_512, LEN_384 -> GordianDigestState.STATE512;
                default -> GordianDigestState.STATE256;
            };
            case SHAKE, KANGAROO -> pLength == GordianLength.LEN_256
                    ? GordianDigestState.STATE128
                    : GordianDigestState.STATE256;
            case BLAKE2 -> pLength == GordianLength.LEN_128 || pLength == GordianLength.LEN_224
                    ? GordianDigestState.STATE256
                    : GordianDigestState.STATE512;
            case HARAKA -> GordianDigestState.STATE256;
            default -> null;
        };
    }

    /**
     * State subSpecification.
     */
    final class GordianCoreDigestState
            implements GordianCoreDigestSubSpec {
        /**
         * The digestStateMap.
         */
        private static final Map<GordianDigestState, GordianCoreDigestState> STATEMAP = newStateMap();

        /**
         * The State.
         */
        private final GordianDigestState theState;

        /**
         * The length.
         */
        private final GordianLength theLength;

        /**
         * Constructor.
         *
         * @param pState the state
         */
        private GordianCoreDigestState(final GordianDigestState pState) {
            theState = pState;
            theLength = lengthForDigestState();
        }

        @Override
        public GordianDigestSubSpec getSubSpec() {
            return getState();
        }

        /**
         * Obtain the state.
         *
         * @return the state
         */
        public GordianDigestState getState() {
            return theState;
        }

        /**
         * Obtain length for state.
         *
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
         *
         * @param pLength the length
         * @return true/false
         */
        public boolean isSha2Hybrid(final GordianLength pLength) {
            return theState == GordianDigestState.STATE512
                    && (GordianLength.LEN_224.equals(pLength)
                    || GordianLength.LEN_256.equals(pLength));
        }

        /**
         * Obtain the length for an explicit Xof variant.
         *
         * @param pType the digestType
         * @return the length
         */
        GordianLength lengthForXofType(final GordianCoreDigestType pType) {
            return switch (pType.getType()) {
                case SKEIN -> switch (theState) {
                    case STATE256, STATE512, STATE1024 -> theLength;
                    default -> null;
                };
                case BLAKE2 -> switch (theState) {
                    case STATE256, STATE512 -> theLength;
                    default -> null;
                };
                default -> null;
            };
        }

        /**
         * is length available for this type and length?
         *
         * @param pType   the digestType
         * @param pLength the length
         * @return true/false
         */
        boolean validForTypeAndLength(final GordianCoreDigestType pType,
                                      final GordianLength pLength) {
            return switch (pType.getType()) {
                case SHA2 -> validForSha2Length(pLength);
                case SHAKE, KANGAROO -> validForSHAKELength(pLength);
                case SKEIN -> validForSkeinLength(pLength);
                case BLAKE2 -> validForBlake2Length(pLength);
                case HARAKA -> validForHarakaLength(pLength);
                default -> false;
            };
        }

        /**
         * Is this state valid for the skeinLength?
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSha2Length(final GordianLength pLength) {
            return switch (theState) {
                case STATE512 -> switch (pLength) {
                    case LEN_224, LEN_256, LEN_384, LEN_512 -> true;
                    default -> false;
                };
                case STATE256 -> switch (pLength) {
                    case LEN_224, LEN_256 -> true;
                    default -> false;
                };
                default -> false;
            };
        }

        /**
         * Is this state valid for the skeinLength?
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSHAKELength(final GordianLength pLength) {
            return switch (theState) {
                case STATE256 -> pLength == GordianLength.LEN_512;
                case STATE128 -> pLength == GordianLength.LEN_256;
                default -> false;
            };
        }

        /**
         * Is this state valid for the skeinLength?
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSkeinLength(final GordianLength pLength) {
            return switch (theState) {
                case STATE1024 -> switch (pLength) {
                    case LEN_384, LEN_512, LEN_1024 -> true;
                    default -> false;
                };
                case STATE512 -> switch (pLength) {
                    case LEN_128, LEN_160, LEN_224, LEN_256, LEN_384, LEN_512 -> true;
                    default -> false;
                };
                case STATE256 -> switch (pLength) {
                    case LEN_128, LEN_160, LEN_224, LEN_256 -> true;
                    default -> false;
                };
                default -> false;
            };
        }

        /**
         * Is this state valid for the blake2Length.
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForBlake2Length(final GordianLength pLength) {
            return switch (theState) {
                case STATE512 -> switch (pLength) {
                    case LEN_160, LEN_256, LEN_384, LEN_512 -> true;
                    default -> false;
                };
                case STATE256 -> switch (pLength) {
                    case LEN_128, LEN_160, LEN_224, LEN_256 -> true;
                    default -> false;
                };
                default -> false;
            };
        }

        /**
         * Is this state valid for the harakaLength.
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForHarakaLength(final GordianLength pLength) {
            return switch (theState) {
                case STATE512, STATE256 -> pLength == GordianLength.LEN_256;
                default -> false;
            };
        }

        /**
         * Is this the Blake2b algorithm?
         *
         * @return true/false
         */
        public boolean isBlake2bState() {
            return GordianDigestState.STATE512.equals(theState);
        }

        /**
         * Obtain the blake2Algorithm name for State.
         *
         * @param pXofMode is this a Xof variant
         * @return the algorithm name
         */
        public String getBlake2Algorithm(final boolean pXofMode) {
            return (pXofMode ? "X" : "")
                    + (isBlake2bState() ? "b" : "s");
        }

        /**
         * Obtain the kangarooAlgorithm name for State.
         *
         * @return the algorithmName
         */
        String getKangarooAlgorithm() {
            return theState == GordianDigestState.STATE256
                    ? GordianDigestResource.DIGEST_MARSUPILAMI.getValue()
                    : GordianDigestResource.DIGEST_KANGAROO.getValue();
        }

        /**
         * Obtain length for state.
         *
         * @return the length
         */
        public GordianLength lengthForDigestState() {
            return switch (theState) {
                case STATE128 -> GordianLength.LEN_128;
                case STATE256 -> GordianLength.LEN_256;
                case STATE512 -> GordianLength.LEN_512;
                case STATE1024 -> GordianLength.LEN_1024;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check subFields */
            return pThat instanceof GordianCoreDigestState myThat
                    && theState == myThat.getState();
        }

        @Override
        public int hashCode() {
            return theState.hashCode();
        }

        /**
         * Obtain the core state.
         *
         * @param pState the base state
         * @return the core state
         */
        public static GordianCoreDigestState mapCoreState(final Object pState) {
            return pState instanceof GordianDigestState myState ? STATEMAP.get(myState) : null;
        }

        /**
         * Build the state map.
         *
         * @return the state map
         */
        private static Map<GordianDigestState, GordianCoreDigestState> newStateMap() {
            final Map<GordianDigestState, GordianCoreDigestState> myMap = new EnumMap<>(GordianDigestState.class);
            for (GordianDigestState myState : GordianDigestState.values()) {
                myMap.put(myState, new GordianCoreDigestState(myState));
            }
            return myMap;
        }

        /**
         * Obtain the values.
         *
         * @return the values
         */
        public static Collection<GordianCoreDigestState> values() {
            return STATEMAP.values();
        }
    }
}
