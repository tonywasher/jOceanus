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
package net.sourceforge.joceanus.gordianknot.impl.core.digest;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Base Digest Factory.
 */
public abstract class GordianCoreDigestFactory
        implements GordianDigestFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    protected GordianCoreDigestFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianDigestSpec> supportedDigestSpecs() {
        return this::validDigestSpec;
    }

    @Override
    public Predicate<GordianDigestType> supportedDigestTypes() {
        return t -> theFactory.getValidator().validDigestType(t);
    }

       /**
     * Check digestSpec.
     * @param pDigestSpec the digestSpec
     * @throws GordianException on error
     */
    public void checkDigestSpec(final GordianDigestSpec pDigestSpec) throws GordianException {
        /* Check validity of DigestType */
        if (!supportedDigestSpecs().test(pDigestSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pDigestSpec));
        }
    }

    /**
     * Check DigestSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    public boolean validDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Reject invalid digestSpec */
        if (pDigestSpec == null || !pDigestSpec.isValid()) {
            return false;
        }

        /* If we have an explicit Xof, check support */
        if (pDigestSpec.isXofMode()
                && !theFactory.getValidator().isXofSupported()) {
            return false;
        }

        /* Check validity */
        return supportedDigestTypes().test(pDigestSpec.getDigestType());
    }

    @Override
    public List<GordianDigestSpec> listAllSupportedSpecs() {
        return listAllPossibleSpecs()
                .stream()
                .filter(supportedDigestSpecs())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianDigestType> listAllSupportedTypes() {
        return Arrays.stream(GordianDigestType.values())
                .filter(supportedDigestTypes())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianDigestSpec> listAllPossibleSpecs() {
        /* Create the array list */
        final List<GordianDigestSpec> myList = new ArrayList<>();

        /* For each digest type */
        for (final GordianDigestType myType : GordianDigestType.values()) {
            /* For each subSpecType */
            for (GordianDigestSubSpec mySubSpec : GordianDigestSubSpec.getPossibleSubSpecsForType(myType)) {
                /* For each length */
                for (final GordianLength myLength : myType.getSupportedLengths()) {
                    final GordianDigestSpec mySpec = new GordianDigestSpec(myType, mySubSpec, myLength);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }
                }

                /* If we have a possible Xof */
                if (mySubSpec instanceof GordianDigestState) {
                    final GordianDigestState myState = (GordianDigestState) mySubSpec;
                    final GordianDigestSpec mySpec = new GordianDigestSpec(myType, myState, myState.getLength(), Boolean.TRUE);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }

                    /* Else look for null Xof type */
                } else {
                    final GordianDigestSpec mySpec = new GordianDigestSpec(myType, null, myType.getDefaultLength(), Boolean.TRUE);

                    /* Add if valid */
                    if (mySpec.isValid()) {
                        myList.add(mySpec);
                    }
                }
            }
        }

        /* Return the list */
        return myList;
    }
}
