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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianPicnicSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;
import org.bouncycastle.pqc.legacy.picnic.PicnicParameters;

import java.util.EnumMap;
import java.util.Map;

/**
 * PICNIC KeySpec.
 */
public final class GordianCorePicnicSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianPicnicSpec, GordianCorePicnicSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCorePicnicSpec[] VALUES = SPECMAP.values().toArray(new GordianCorePicnicSpec[0]);

    /**
     * The Spec.
     */
    private final GordianPicnicSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCorePicnicSpec(final GordianPicnicSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianPicnicSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain Picnic Parameters.
     *
     * @return the parameters.
     */
    public PicnicParameters getParameters() {
        return switch (theSpec) {
            case L1UR -> PicnicParameters.picnicl1ur;
            case L1FS -> PicnicParameters.picnicl1fs;
            case L1FULL -> PicnicParameters.picnicl1full;
            case L13 -> PicnicParameters.picnic3l1;
            case L3UR -> PicnicParameters.picnicl3ur;
            case L3FS -> PicnicParameters.picnicl3fs;
            case L3FULL -> PicnicParameters.picnicl3full;
            case L33 -> PicnicParameters.picnic3l3;
            case L5UR -> PicnicParameters.picnicl5ur;
            case L5FS -> PicnicParameters.picnicl5fs;
            case L5FULL -> PicnicParameters.picnicl5full;
            case L53 -> PicnicParameters.picnic3l5;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Picnic ParameterSpec.
     *
     * @return the parameters.
     */
    public PicnicParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case L1UR -> PicnicParameterSpec.picnicl1ur;
            case L1FS -> PicnicParameterSpec.picnicl1fs;
            case L1FULL -> PicnicParameterSpec.picnicl1full;
            case L13 -> PicnicParameterSpec.picnic3l1;
            case L3UR -> PicnicParameterSpec.picnicl3ur;
            case L3FS -> PicnicParameterSpec.picnicl3fs;
            case L3FULL -> PicnicParameterSpec.picnicl3full;
            case L33 -> PicnicParameterSpec.picnic3l3;
            case L5UR -> PicnicParameterSpec.picnicl5ur;
            case L5FS -> PicnicParameterSpec.picnicl5fs;
            case L5FULL -> PicnicParameterSpec.picnicl5full;
            case L53 -> PicnicParameterSpec.picnic3l5;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Picnic algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case L1UR -> BCObjectIdentifiers.picnicl1ur;
            case L1FS -> BCObjectIdentifiers.picnicl1fs;
            case L1FULL -> BCObjectIdentifiers.picnicl1full;
            case L13 -> BCObjectIdentifiers.picnic3l1;
            case L3UR -> BCObjectIdentifiers.picnicl3ur;
            case L3FS -> BCObjectIdentifiers.picnicl3fs;
            case L3FULL -> BCObjectIdentifiers.picnicl3full;
            case L33 -> BCObjectIdentifiers.picnic3l3;
            case L5UR -> BCObjectIdentifiers.picnicl5ur;
            case L5FS -> BCObjectIdentifiers.picnicl5fs;
            case L5FULL -> BCObjectIdentifiers.picnicl5full;
            case L53 -> BCObjectIdentifiers.picnic3l5;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public String toString() {
        return theSpec.toString();
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
        return pThat instanceof GordianCorePicnicSpec myThat
                && theSpec == myThat.getSpec();
    }

    @Override
    public int hashCode() {
        return theSpec.hashCode();
    }

    /**
     * Obtain the core spec.
     *
     * @param pSpec the base spec
     * @return the core spec
     */
    public static GordianCorePicnicSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianPicnicSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianPicnicSpec, GordianCorePicnicSpec> newSpecMap() {
        final Map<GordianPicnicSpec, GordianCorePicnicSpec> myMap = new EnumMap<>(GordianPicnicSpec.class);
        for (GordianPicnicSpec mySpec : GordianPicnicSpec.values()) {
            myMap.put(mySpec, new GordianCorePicnicSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCorePicnicSpec[] values() {
        return VALUES;
    }
}
