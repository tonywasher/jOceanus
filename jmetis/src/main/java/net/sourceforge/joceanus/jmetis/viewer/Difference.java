/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.viewer;

import java.util.Arrays;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataDiffers;

/**
 * Difference enum and utility.
 * @author Tony Washer
 */
public enum Difference {
    /**
     * Identical.
     */
    IDENTICAL,

    /**
     * Value Changed.
     */
    DIFFERENT,

    /**
     * Security Changed.
     */
    SECURITY;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Difference.class.getName());

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }

    /**
     * Is there differences?
     * @return true/false
     */
    public boolean isDifferent() {
        switch (this) {
            case IDENTICAL:
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
            case DIFFERENT:
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
            case SECURITY:
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
            case IDENTICAL:
                return pThat;
            case SECURITY:
                return (pThat == DIFFERENT)
                        ? pThat
                        : this;
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
            return IDENTICAL;
        }

        /* Neither value can be null */
        if ((pCurr == null)
            || (pNew == null)) {
            return DIFFERENT;
        }

        /* Handle class differences */
        if (pCurr.getClass() != pNew.getClass()) {
            return DIFFERENT;
        }

        /* Handle differs support */
        if (pCurr instanceof JDataDiffers) {
            return ((JDataDiffers) pCurr).differs(pNew);
        }

        /* Handle Standard cases */
        return (pCurr.equals(pNew))
                ? IDENTICAL
                : DIFFERENT;
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
        if ((pCurr == null)
            || (pNew == null)) {
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
     * @return order
     */
    public static <X extends Comparable<? super X>> int compareObject(final X pCurr,
                                                                      final X pNew) {
        /* Handle identity */
        if (pCurr == pNew) {
            return 0;
        }

        /* Pass the call on, defaulting null to the end of the list */
        return compareObject(pCurr, pNew, true);
    }

    /**
     * Compare two similar objects for order.
     * @param <X> the object type
     * @param pCurr The current object
     * @param pNew The new object
     * @param pNullLast is Null at end of list?
     * @return order
     */
    public static <X extends Comparable<? super X>> int compareObject(final X pCurr,
                                                                      final X pNew,
                                                                      final boolean pNullLast) {
        /* Handle identity */
        if (pCurr == pNew) {
            return 0;
        }

        /* Handle positioning of nulls */
        if (pCurr == null) {
            return (pNullLast)
                    ? 1
                    : -1;
        }
        if (pNew == null) {
            return (pNullLast)
                    ? -1
                    : 1;
        }

        /* Both non-Null, so pass the call on */
        return pCurr.compareTo(pNew);
    }
}