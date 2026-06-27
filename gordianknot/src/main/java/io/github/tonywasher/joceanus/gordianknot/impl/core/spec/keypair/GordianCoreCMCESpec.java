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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.crypto.params.CMCEParameters;
import org.bouncycastle.jcajce.spec.CMCEParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * CMCE KeySpecs.
 */

public final class GordianCoreCMCESpec
        implements GordianCoreKeyPairIdSpec<GordianCMCESpec> {
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

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.CMCE;
    }

    @Override
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
            case CMCE4608 -> CMCEParameters.mceliece460896;
            case CMCE4608F -> CMCEParameters.mceliece460896f;
            case CMCE4608PC -> CMCEParameters.mceliece460896pc;
            case CMCE4608PCF -> CMCEParameters.mceliece460896pcf;
            case CMCE6688 -> CMCEParameters.mceliece6688128;
            case CMCE6688F -> CMCEParameters.mceliece6688128f;
            case CMCE6688PC -> CMCEParameters.mceliece6688128pc;
            case CMCE6688PCF -> CMCEParameters.mceliece6688128pcf;
            case CMCE6960 -> CMCEParameters.mceliece6960119;
            case CMCE6960F -> CMCEParameters.mceliece6960119f;
            case CMCE6960PC -> CMCEParameters.mceliece6960119pc;
            case CMCE6960PCF -> CMCEParameters.mceliece6960119pcf;
            case CMCE8192 -> CMCEParameters.mceliece8192128;
            case CMCE8192F -> CMCEParameters.mceliece8192128f;
            case CMCE8192PC -> CMCEParameters.mceliece8192128pc;
            case CMCE8192PCF -> CMCEParameters.mceliece8192128pcf;
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
            case CMCE4608 -> CMCEParameterSpec.mceliece460896;
            case CMCE4608F -> CMCEParameterSpec.mceliece460896f;
            case CMCE4608PC -> CMCEParameterSpec.mceliece460896pc;
            case CMCE4608PCF -> CMCEParameterSpec.mceliece460896pcf;
            case CMCE6688 -> CMCEParameterSpec.mceliece6688128;
            case CMCE6688F -> CMCEParameterSpec.mceliece6688128f;
            case CMCE6688PC -> CMCEParameterSpec.mceliece6688128pc;
            case CMCE6688PCF -> CMCEParameterSpec.mceliece6688128pcf;
            case CMCE6960 -> CMCEParameterSpec.mceliece6960119;
            case CMCE6960F -> CMCEParameterSpec.mceliece6960119f;
            case CMCE6960PC -> CMCEParameterSpec.mceliece6960119pc;
            case CMCE6960PCF -> CMCEParameterSpec.mceliece6960119pcf;
            case CMCE8192 -> CMCEParameterSpec.mceliece8192128;
            case CMCE8192F -> CMCEParameterSpec.mceliece8192128f;
            case CMCE8192PC -> CMCEParameterSpec.mceliece8192128pc;
            case CMCE8192PCF -> CMCEParameterSpec.mceliece8192128pcf;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case CMCE4608 -> ISOIECObjectIdentifiers.mceliece460896;
            case CMCE4608F -> ISOIECObjectIdentifiers.mceliece460896f;
            case CMCE4608PC -> ISOIECObjectIdentifiers.mceliece460896pc;
            case CMCE4608PCF -> ISOIECObjectIdentifiers.mceliece460896pcf;
            case CMCE6688 -> ISOIECObjectIdentifiers.mceliece6688128;
            case CMCE6688F -> ISOIECObjectIdentifiers.mceliece6688128f;
            case CMCE6688PC -> ISOIECObjectIdentifiers.mceliece6688128pc;
            case CMCE6688PCF -> ISOIECObjectIdentifiers.mceliece6688128pcf;
            case CMCE6960 -> ISOIECObjectIdentifiers.mceliece6960119;
            case CMCE6960F -> ISOIECObjectIdentifiers.mceliece6960119f;
            case CMCE6960PC -> ISOIECObjectIdentifiers.mceliece6960119pc;
            case CMCE6960PCF -> ISOIECObjectIdentifiers.mceliece6960119pcf;
            case CMCE8192 -> ISOIECObjectIdentifiers.mceliece8192128;
            case CMCE8192F -> ISOIECObjectIdentifiers.mceliece8192128f;
            case CMCE8192PC -> ISOIECObjectIdentifiers.mceliece8192128pc;
            case CMCE8192PCF -> ISOIECObjectIdentifiers.mceliece8192128pcf;
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
