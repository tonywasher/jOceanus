/*******************************************************************************
 * jDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jDataManager;

import java.util.Arrays;

import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataDiffers;

/**
 * Difference enum and utility.
 * @author Tony Washer
 */
public enum Difference {
    /**
     * Identical.
     */
    Identical,

    /**
     * Value Changed.
     */
    Different,

    /**
     * Security Changed.
     */
    Security;

    /**
     * Is there differences?
     * @return true/false
     */
    public boolean isDifferent() {
        switch (this) {
            case Identical:
                return false;
            default:
                return true;
        }
    }

    /**
     * Is there no differences?
     * @return true/false
     */
    public boolean isIdentical() {
        return !isDifferent();
    }

    /**
     * Is there value differences?
     * @return true/false
     */
    public boolean isValueChanged() {
        switch (this) {
            case Different:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is there security differences?
     * @return true/false
     */
    public boolean isSecurityChanged() {
        switch (this) {
            case Security:
                return false;
            default:
                return true;
        }
    }

    /**
     * Combine Differences.
     * @param pThat the difference to combine
     * @return the combined difference
     */
    public Difference combine(final Difference pThat) {
        switch (this) {
            case Identical:
                return pThat;
            case Security:
                return (pThat == Different) ? pThat : this;
            default:
                return this;
        }
    }

    /**
     * Determine whether two Generic objects differ.
     * @param pCurr The current object
     * @param pNew The new object
     * @return the Difference between the objects
     */
    public static Difference getDifference(final Object pCurr,
                                           final Object pNew) {
        /* Handle identity */
        if (pCurr == pNew) {
            return Identical;
        }

        /* Neither value can be null */
        if ((pCurr == null) || (pNew == null)) {
            return Different;
        }

        /* Handle class differences */
        if (pCurr.getClass() != pNew.getClass()) {
            return Different;
        }

        /* Handle differs support */
        if (pCurr instanceof JDataDiffers) {
            return ((JDataDiffers) pCurr).differs(pNew);
        }

        /* Handle Standard cases */
        return (pCurr.equals(pNew)) ? Identical : Different;
    }

    /**
     * Determine whether two Generic objects are equal.
     * @param pCurr The current object
     * @param pNew The new object
     * @return true/false
     */
    public static boolean isEqual(final Object pCurr,
                                  final Object pNew) {
        /* Handle identity */
        if (pCurr == pNew) {
            return true;
        }

        /* Neither value can be null */
        if ((pCurr == null) || (pNew == null)) {
            return false;
        }

        /* Handle class differences */
        if (pCurr.getClass() != pNew.getClass()) {
            return false;
        }

        /* Handle arrays */
        if (pCurr.getClass().isArray()) {
            /* Handle special cases for efficiency */
            if (pCurr instanceof byte[]) {
                return Arrays.equals((byte[]) pCurr, (byte[]) pNew);
            }
            if (pCurr instanceof char[]) {
                return Arrays.equals((char[]) pCurr, (char[]) pNew);
            }

            /* Handle generic arrays */
            return Arrays.equals((Object[]) pCurr, (Object[]) pNew);
        }

        /* Handle Standard cases */
        return pCurr.equals(pNew);
    }

    /**
     * Compare two similar objects for order.
     * @param <X> the object type
     * @param pCurr The current object
     * @param pNew The new object
     * @return true/false
     */
    public static <X extends Comparable<? super X>> int compareObject(final X pCurr,
                                                                      final X pNew) {
        /* Handle identity */
        if (pCurr == pNew) {
            return 0;
        }

        /* Null is at the end of the list */
        if (pCurr == null) {
            return 1;
        }
        if (pNew == null) {
            return -1;
        }

        /* Pass the call on */
        return pCurr.compareTo(pNew);
    }
}
