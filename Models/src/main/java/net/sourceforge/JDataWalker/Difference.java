/*******************************************************************************
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
package net.sourceforge.JDataWalker;

import java.util.Arrays;

import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Decimal;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedField;

public enum Difference {
    /**
     * Identical
     */
    Identical,

    /**
     * Value Changed
     */
    Different,

    /**
     * Security Changed
     */
    Security;

    /**
     * Is there differences
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
     * Is there no differences
     * @return true/false
     */
    public boolean isIdentical() {
        return !isDifferent();
    }

    /**
     * Is there value differences
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
     * Is there security differences
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
     * Combine Differences
     * @param pThat the difference to combine
     * @return the combined difference
     */
    public Difference combine(Difference pThat) {
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
    public static Difference getDifference(Object pCurr,
                                           Object pNew) {
        /* Handle case where current value is null */
        if (pCurr == null)
            return (pNew != null) ? Difference.Different : Difference.Identical;

        /* Handle case where new value is null */
        if (pNew == null)
            return Difference.Different;

        /* Handle class differences */
        if (pCurr.getClass() != pNew.getClass())
            return Difference.Different;

        /* Handle standard java data-types separately */
        if (pCurr instanceof String)
            return ((String) pCurr).equals(pNew) ? Difference.Identical : Difference.Different;
        if (pCurr instanceof Boolean)
            return ((Boolean) pCurr).equals(pNew) ? Difference.Identical : Difference.Different;
        if (pCurr instanceof Short)
            return ((Short) pCurr).equals(pNew) ? Difference.Identical : Difference.Different;
        if (pCurr instanceof Integer)
            return ((Integer) pCurr).equals(pNew) ? Difference.Identical : Difference.Different;
        if (pCurr instanceof Long)
            return ((Long) pCurr).equals(pNew) ? Difference.Identical : Difference.Different;
        if (pCurr instanceof char[])
            return Arrays.equals((char[]) pCurr, (char[]) pNew) ? Difference.Identical : Difference.Different;
        if (pCurr instanceof byte[])
            return Arrays.equals((byte[]) pCurr, (byte[]) pNew) ? Difference.Identical : Difference.Different;

        /* Handle model data-types separately */
        if (pCurr instanceof DateDay)
            return DateDay.isDifferent((DateDay) pCurr, (DateDay) pNew) ? Difference.Different
                    : Difference.Identical;
        if (pCurr instanceof DateDayRange)
            return DateDayRange.isDifferent((DateDayRange) pCurr, (DateDayRange) pNew) ? Difference.Different
                    : Difference.Identical;
        if (pCurr instanceof Decimal)
            return Decimal.isDifferent((Decimal) pCurr, (Decimal) pNew) ? Difference.Different
                    : Difference.Identical;
        if (pCurr instanceof EncryptedField)
            return ((EncryptedField<?>) pCurr).differs((EncryptedField<?>) pNew);
        if (pCurr instanceof ReportItem)
            return ((ReportItem<?>) pCurr).differs((ReportItem<?>) pNew);

        /* Handle Standard cases */
        return (pCurr.equals(pNew)) ? Difference.Identical : Difference.Different;
    }
}
