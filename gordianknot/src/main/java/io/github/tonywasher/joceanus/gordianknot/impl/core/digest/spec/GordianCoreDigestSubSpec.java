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

package io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestResource;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public interface GordianCoreDigestSubSpec {
    /**
     * Obtain the subSpec.
     *
     * @return the subSpec
     */
    GordianNewDigestSubSpec getSubSpec();

    /**
     * Obtain the possible subSpecTypes for the digestType.
     *
     * @param pType the digestType
     * @return the subSpec types
     */
    static GordianNewDigestSubSpec[] getPossibleSubSpecsForType(final GordianNewDigestType pType) {
        switch (pType) {
            case SHA2:
            case BLAKE2:
            case HARAKA:
                return new GordianNewDigestState[]{GordianNewDigestState.STATE256, GordianNewDigestState.STATE512};
            case SKEIN:
                return new GordianNewDigestState[]{GordianNewDigestState.STATE256, GordianNewDigestState.STATE512, GordianNewDigestState.STATE1024};
            case SHAKE:
            case KANGAROO:
                return new GordianNewDigestState[]{GordianNewDigestState.STATE128, GordianNewDigestState.STATE256};
            default:
                return new GordianNewDigestState[]{null};
        }
    }

    /**
     * Obtain the subSpec for the type and length.
     *
     * @param pType   the digestType
     * @param pLength the length
     * @return the subSpec
     */
    static GordianNewDigestSubSpec getDefaultSubSpecForTypeAndLength(final GordianNewDigestType pType,
                                                                     final GordianLength pLength) {
        switch (pType) {
            case SHA2:
                return pLength == GordianLength.LEN_224 || pLength == GordianLength.LEN_256
                        ? GordianNewDigestState.STATE256
                        : GordianNewDigestState.STATE512;
            case SKEIN:
                switch (pLength) {
                    case LEN_1024:
                        return GordianNewDigestState.STATE1024;
                    case LEN_512:
                    case LEN_384:
                        return GordianNewDigestState.STATE512;
                    default:
                        return GordianNewDigestState.STATE256;
                }
            case SHAKE:
            case KANGAROO:
                return pLength == GordianLength.LEN_256
                        ? GordianNewDigestState.STATE128
                        : GordianNewDigestState.STATE256;
            case BLAKE2:
                return pLength == GordianLength.LEN_128 || pLength == GordianLength.LEN_224
                        ? GordianNewDigestState.STATE256
                        : GordianNewDigestState.STATE512;
            case HARAKA:
                return GordianNewDigestState.STATE256;
            default:
                return null;
        }
    }

    /**
     * State subSpecification.
     */
    final class GordianCoreDigestState
            implements GordianCoreDigestSubSpec {
        /**
         * The digestStateMap.
         */
        private static final Map<GordianNewDigestState, GordianCoreDigestState> STATEMAP = newStateMap();

        /**
         * The State.
         */
        private final GordianNewDigestState theState;

        /**
         * The length.
         */
        private final GordianLength theLength;

        /**
         * Constructor.
         *
         * @param pState the state
         */
        private GordianCoreDigestState(final GordianNewDigestState pState) {
            theState = pState;
            theLength = lengthForDigestState();
        }

        @Override
        public GordianNewDigestSubSpec getSubSpec() {
            return getState();
        }

        /**
         * Obtain the state.
         *
         * @return the state
         */
        public GordianNewDigestState getState() {
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
            return theState == GordianNewDigestState.STATE512
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
            switch (pType.getType()) {
                case SKEIN:
                    switch (theState) {
                        case STATE256:
                        case STATE512:
                        case STATE1024:
                            return theLength;
                        default:
                            return null;
                    }
                case BLAKE2:
                    switch (theState) {
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
         *
         * @param pType   the digestType
         * @param pLength the length
         * @return true/false
         */
        boolean validForTypeAndLength(final GordianCoreDigestType pType,
                                      final GordianLength pLength) {
            switch (pType.getType()) {
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
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSha2Length(final GordianLength pLength) {
            switch (theState) {
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
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSHAKELength(final GordianLength pLength) {
            switch (theState) {
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
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForSkeinLength(final GordianLength pLength) {
            switch (theState) {
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
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForBlake2Length(final GordianLength pLength) {
            switch (theState) {
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
         *
         * @param pLength the length
         * @return true/false
         */
        private boolean validForHarakaLength(final GordianLength pLength) {
            switch (theState) {
                case STATE512:
                case STATE256:
                    return pLength == GordianLength.LEN_256;
                default:
                    return false;
            }
        }

        /**
         * Is this the Blake2b algorithm?
         *
         * @return true/false
         */
        public boolean isBlake2bState() {
            return GordianNewDigestState.STATE512.equals(theState);
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
            return theState == GordianNewDigestState.STATE256
                    ? GordianDigestResource.DIGEST_MARSUPILAMI.getValue()
                    : GordianDigestResource.DIGEST_KANGAROO.getValue();
        }

        /**
         * Obtain length for state.
         *
         * @return the length
         */
        public GordianLength lengthForDigestState() {
            switch (theState) {
                case STATE128:
                    return GordianLength.LEN_128;
                case STATE256:
                    return GordianLength.LEN_256;
                case STATE512:
                    return GordianLength.LEN_512;
                case STATE1024:
                    return GordianLength.LEN_1024;
                default:
                    throw new IllegalArgumentException();
            }
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
         * @param pType the base state
         * @return the core state
         */
        public static GordianCoreDigestState mapCoreState(final GordianNewDigestState pType) {
            return STATEMAP.get(pType);
        }

        /**
         * Build the state map.
         *
         * @return the state map
         */
        private static Map<GordianNewDigestState, GordianCoreDigestState> newStateMap() {
            final Map<GordianNewDigestState, GordianCoreDigestState> myMap = new EnumMap<>(GordianNewDigestState.class);
            for (GordianNewDigestState myState : GordianNewDigestState.values()) {
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
