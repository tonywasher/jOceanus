/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypair;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;

/**
 * CompositeKeyPair.
 */
public class GordianCompositeKeyPair
        implements GordianKeyPair {
    /**
     * The KeySpec.
     */
    private final GordianKeyPairSpec theSpec;

    /**
     * The keyPairs.
     */
    private final Map<GordianKeyPairSpec, GordianKeyPair> theKeyPairs;

    /**
     * is the keyPair public only?
     */
    private boolean isPublicOnly;

    /**
     * Constructor.
     * @param pSpec the spec
     */
    public GordianCompositeKeyPair(final GordianKeyPairSpec pSpec) {
        theSpec = pSpec;
        theKeyPairs = new LinkedHashMap<>();
    }

    @Override
    public GordianKeyPairSpec getKeyPairSpec() {
        return theSpec;
    }

    @Override
    public boolean isPublicOnly() {
        return isPublicOnly;
    }

    /**
     * Obtain a publicOnly version of this keyPair.
     * @return the publicOnly keyPair
     */
    public GordianCompositeKeyPair getPublicOnly()  {
        final GordianCompositeKeyPair myPublicOnly = new GordianCompositeKeyPair(theSpec);
        for (GordianKeyPair myPair : theKeyPairs.values()) {
            myPublicOnly.theKeyPairs.put(myPair.getKeyPairSpec(), ((GordianCoreKeyPair) myPair).getPublicOnly());
        }
        return myPublicOnly;
    }

    /**
     * Obtain an iterator for the keyPairs.
     * @return the iterator
     */
    public Iterator<GordianKeyPair> iterator() {
        return theKeyPairs.values().iterator();
    }

    /**
     * Add keyPair.
     * @param pKeyPair the keyPair
     */
    void addKeyPair(final GordianKeyPair pKeyPair) {
        /* Add the keyPair */
        theKeyPairs.put(pKeyPair.getKeyPairSpec(), pKeyPair);
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
        if (!(pThat instanceof GordianCompositeKeyPair)) {
            return false;
        }
        final GordianCompositeKeyPair myThat = (GordianCompositeKeyPair) pThat;
        return Objects.equals(theKeyPairs, myThat.theKeyPairs);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(theKeyPairs);
    }
}
