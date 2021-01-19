/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * CoreKeyPairSet.
 */
public class GordianCoreKeyPairSet
    implements GordianKeyPairSet {
    /**
     * The keySetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The keyPairs.
     */
    private final Map<GordianKeyPairSpec, GordianKeyPair> theKeyPairs;

    /**
     * is the keyPairSet public only?
     */
    private boolean isPublicOnly;

    /**
     * Constructor.
     * @param pSpec the spec
     */
    GordianCoreKeyPairSet(final GordianKeyPairSetSpec pSpec) {
        theSpec = pSpec;
        theKeyPairs = new LinkedHashMap<>();
    }

    @Override
    public GordianKeyPairSetSpec getKeyPairSetSpec() {
        return theSpec;
    }

    @Override
    public boolean isPublicOnly() {
        return isPublicOnly;
    }

    /**
     * Obtain a publicOnly version of this keySet.
     * @return the publicOnly keySet
     */
    public GordianCoreKeyPairSet getPublicOnly()  {
        final GordianCoreKeyPairSet myPublicOnly = new GordianCoreKeyPairSet(theSpec);
        for (GordianKeyPair myPair : theKeyPairs.values()) {
            myPublicOnly.theKeyPairs.put(myPair.getKeyPairSpec(), ((GordianCoreKeyPair) myPair).getPublicOnly());
        }
        return myPublicOnly;
    }

    /**
     * Obtain an iterator for the keyPairSpecs.
     * @return the iterator
     */
    public Iterator<GordianKeyPair> iterator() {
        return theKeyPairs.values().iterator();
    }

    /**
     * Add keyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void addKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check publicOnly */
        if (theKeyPairs.isEmpty()) {
            isPublicOnly = pKeyPair.isPublicOnly();
        } else if (isPublicOnly != pKeyPair.isPublicOnly()) {
            throw new GordianLogicException("Mismatch on publicOnly");
        }

        /* Check uniqueness */
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        if (theKeyPairs.containsKey(mySpec)) {
            throw new GordianLogicException("duplicate keyPairSpec");
        }

        /* Add the keyPair */
        theKeyPairs.put(mySpec, pKeyPair);
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

        /* Check object is same class */
        if (!(pThat instanceof GordianCoreKeyPairSet)) {
            return false;
        }
        final GordianCoreKeyPairSet myThat = (GordianCoreKeyPairSet) pThat;
        return Objects.equals(theKeyPairs, myThat.theKeyPairs);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(theKeyPairs);
    }
}