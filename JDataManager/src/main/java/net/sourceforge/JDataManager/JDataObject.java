/*******************************************************************************
 * JDataManager: Java Data Manager
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
package net.sourceforge.JDataManager;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Decimal;

/**
 * Data object formatting and interfaces.
 * @author Tony Washer
 */
public final class JDataObject {
    /**
     * Private constructor to avoid instantiation.
     */
    private JDataObject() {
    }

    /**
     * Format object interface.
     */
    public interface JDataFormat {
        /**
         * Obtain Object summary.
         * @return the display summary of the object
         */
        String formatObject();
    }

    /**
     * Detail object interface.
     */
    public interface JDataContents extends JDataFormat {
        /**
         * Obtain the Report Fields.
         * @return the report fields
         */
        JDataFields getDataFields();

        /**
         * Obtain Field value.
         * @param pField the field
         * @return the value of the field
         */
        Object getFieldValue(final JDataField pField);
    }

    /**
     * ValueSet object interface.
     */
    public interface JDataValues extends JDataContents {
        /**
         * Obtain Object ValueSet.
         * @return the ValueSet of the object
         */
        ValueSet getValueSet();

        /**
         * Declare the valueSet as active.
         * @param pValues the active values
         */
        void declareValues(final ValueSet pValues);
    }

    /**
     * Difference interface.
     */
    public interface JDataDiffers {
        /**
         * Test for difference with another object.
         * @param pThat the other object
         * @return the difference
         */
        Difference differs(final Object pThat);
    }

    /**
     * Special values for return by getFieldValue.
     */
    public enum JDataFieldValue {
        /**
         * Field not known.
         */
        UnknownField,

        /**
         * Field to be skipped.
         */
        SkipField;
    }

    /**
     * Format a field value.
     * @param pValue the value of the field
     * @return the formatted value
     */
    public static String formatField(final Object pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Handle ones that we can directly format */
        if (JDataFormat.class.isInstance(pValue)) {
            return ((JDataFormat) pValue).formatObject();
        }

        /* Access the class */
        Class<?> myClass = pValue.getClass();

        /* Handle Native classes */
        if (myClass == String.class) {
            return (String) pValue;
        }
        if (myClass == Boolean.class) {
            return (((Boolean) pValue)) ? "true" : "false";
        }
        if (myClass == Short.class) {
            return ((Short) pValue).toString();
        }
        if (myClass == Integer.class) {
            return ((Integer) pValue).toString();
        }
        if (myClass == Long.class) {
            return ((Long) pValue).toString();
        }
        if (myClass == Float.class) {
            return ((Float) pValue).toString();
        }
        if (myClass == Double.class) {
            return ((Double) pValue).toString();
        }
        if (myClass == BigInteger.class) {
            return ((BigInteger) pValue).toString();
        }
        if (myClass == BigDecimal.class) {
            return ((BigDecimal) pValue).toString();
        }

        /* Handle Enumerated classes */
        if (Enum.class.isInstance(pValue)) {
            return ((Enum<?>) pValue).toString();
        }

        /* Handle Class */
        if (Class.class.isInstance(pValue)) {
            return ((Class<?>) pValue).getCanonicalName();
        }

        /* Handle Native array classes */
        if (myClass == byte[].class) {
            return DataConverter.bytesToHexString((byte[]) pValue);
        }
        if (myClass == char[].class) {
            return new String((char[]) pValue);
        }

        /* Handle date classes */
        if (myClass == DateDay.class) {
            return ((DateDay) pValue).toString();
        }
        if (myClass == DateDayRange.class) {
            return ((DateDayRange) pValue).toString();
        }

        /* Handle decimal classes */
        if (Decimal.class.isInstance(pValue)) {
            return ((Decimal) pValue).format(true);
        }

        /* Standard format option */
        return pValue.getClass().getSimpleName();
    }
}
