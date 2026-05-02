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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianCMCESpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * CMCE KeySpecs.
 */

public final class GordianCoreCMCESpec {
    /**
     * The specMap.
     */
    private static final Map<GordianCMCESpec, GordianCoreCMCESpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreCMCESpec[] VALUES = SPECMAP.values().toArray(new GordianCoreCMCESpec[0]);

    /**
     * The Spec.
     */
    private final GordianCMCESpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreCMCESpec(final GordianCMCESpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianCMCESpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain CMCE Parameters.
     *
     * @return the parameters.
     */
    public CMCEParameters getParameters() {
        return switch (theSpec) {
            case BASE3488 -> CMCEParameters.mceliece348864r3;
            case PIVOT3488 -> CMCEParameters.mceliece348864fr3;
            case BASE4608 -> CMCEParameters.mceliece460896r3;
            case PIVOT4608 -> CMCEParameters.mceliece460896fr3;
            case BASE6688 -> CMCEParameters.mceliece6688128r3;
            case PIVOT6688 -> CMCEParameters.mceliece6688128fr3;
            case BASE6960 -> CMCEParameters.mceliece6960119r3;
            case PIVOT6960 -> CMCEParameters.mceliece6960119fr3;
            case BASE8192 -> CMCEParameters.mceliece8192128r3;
            case PIVOT8192 -> CMCEParameters.mceliece8192128fr3;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain CMCE ParameterSpec.
     *
     * @return the parameters.
     */
    public CMCEParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case BASE3488 -> CMCEParameterSpec.mceliece348864;
            case PIVOT3488 -> CMCEParameterSpec.mceliece348864f;
            case BASE4608 -> CMCEParameterSpec.mceliece460896;
            case PIVOT4608 -> CMCEParameterSpec.mceliece460896f;
            case BASE6688 -> CMCEParameterSpec.mceliece6688128;
            case PIVOT6688 -> CMCEParameterSpec.mceliece6688128f;
            case BASE6960 -> CMCEParameterSpec.mceliece6960119;
            case PIVOT6960 -> CMCEParameterSpec.mceliece6960119f;
            case BASE8192 -> CMCEParameterSpec.mceliece8192128;
            case PIVOT8192 -> CMCEParameterSpec.mceliece8192128f;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain CMCE algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case BASE3488 -> BCObjectIdentifiers.mceliece348864_r3;
            case PIVOT3488 -> BCObjectIdentifiers.mceliece348864f_r3;
            case BASE4608 -> BCObjectIdentifiers.mceliece460896_r3;
            case PIVOT4608 -> BCObjectIdentifiers.mceliece460896f_r3;
            case BASE6688 -> BCObjectIdentifiers.mceliece6688128_r3;
            case PIVOT6688 -> BCObjectIdentifiers.mceliece6688128f_r3;
            case BASE6960 -> BCObjectIdentifiers.mceliece6960119_r3;
            case PIVOT6960 -> BCObjectIdentifiers.mceliece6960119f_r3;
            case BASE8192 -> BCObjectIdentifiers.mceliece8192128_r3;
            case PIVOT8192 -> BCObjectIdentifiers.mceliece8192128f_r3;
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
        return pThat instanceof GordianCoreCMCESpec myThat
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
    public static GordianCoreCMCESpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianCMCESpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianCMCESpec, GordianCoreCMCESpec> newSpecMap() {
        final Map<GordianCMCESpec, GordianCoreCMCESpec> myMap = new EnumMap<>(GordianCMCESpec.class);
        for (GordianCMCESpec mySpec : GordianCMCESpec.values()) {
            myMap.put(mySpec, new GordianCoreCMCESpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreCMCESpec[] values() {
        return VALUES;
    }
}
