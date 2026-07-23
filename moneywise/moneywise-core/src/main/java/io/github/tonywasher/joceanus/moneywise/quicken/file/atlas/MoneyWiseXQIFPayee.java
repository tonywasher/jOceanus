/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Objects;

/**
 * Class representing a Payee.
 *
 * @author Tony Washer
 */
public class MoneyWiseXQIFPayee
        implements Comparable<MoneyWiseXQIFPayee> {
    /**
     * Payee name.
     */
    private final String theName;

    /**
     * Constructor.
     *
     * @param pPayee the Payee
     */
    public MoneyWiseXQIFPayee(final MoneyWisePayee pPayee) {
        /* Store data */
        theName = pPayee.getName();
    }

    /**
     * Constructor.
     *
     * @param pPayee the Payee
     */
    public MoneyWiseXQIFPayee(final String pPayee) {
        /* Store data */
        theName = pPayee;
    }

    /**
     * Obtain the Name.
     *
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    @Override
    public String toString() {
        return getName();
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

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        final MoneyWiseXQIFPayee myPayee = (MoneyWiseXQIFPayee) pThat;

        /* Check date */
        return theName.equals(myPayee.getName());
    }

    @Override
    public int hashCode() {
        return theName.hashCode();
    }

    @Override
    public int compareTo(final MoneyWiseXQIFPayee pThat) {
        return theName.compareTo(pThat.getName());
    }


    /**
     * The Payee line.
     *
     * @param <X> the line type
     */
    public abstract static class MoneyWiseXQIFPayeeLine<X extends MoneyWiseQLineType>
            extends MoneyWiseXQIFLine<X> {
        /**
         * The payee.
         */
        private final MoneyWiseXQIFPayee thePayee;

        /**
         * Constructor.
         *
         * @param pPayee the Payee
         */
        protected MoneyWiseXQIFPayeeLine(final MoneyWiseXQIFPayee pPayee) {
            /* Store data */
            thePayee = pPayee;
        }

        @Override
        public String toString() {
            return thePayee.toString();
        }

        /**
         * Obtain payee.
         *
         * @return the payee
         */
        public MoneyWiseXQIFPayee getPayee() {
            return thePayee;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(thePayee.getName());
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

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseXQIFPayeeLine<?> myLine = (MoneyWiseXQIFPayeeLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return thePayee.equals(myLine.getPayee());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), thePayee);
        }
    }
}
