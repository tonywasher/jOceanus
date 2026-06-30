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

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQClassLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFStringLine;

import java.util.Iterator;
import java.util.List;

/**
 * Class representing a XQIF Class record.
 */
public class MoneyWiseXQIFClass
        extends MoneyWiseXQIFRecord<MoneyWiseQClassLineType>
        implements Comparable<MoneyWiseXQIFClass> {
    /**
     * Item type.
     */
    public static final String XQIF_ITEM = "Class";

    /**
     * The Class Name.
     */
    private final String theName;

    /**
     * The Class Description.
     */
    private final String theDesc;

    /**
     * Constructor.
     *
     * @param pTag the Event Tag
     */
    public MoneyWiseXQIFClass(final MoneyWiseTransTag pTag) {
        /* Call super-constructor */
        super(MoneyWiseQClassLineType.class);

        /* Store data */
        theName = pTag.getName();
        theDesc = pTag.getDesc();

        /* Build lines */
        addLine(new MoneyWiseXQIFClassNameLine(theName));
        if (theDesc != null) {
            addLine(new MoneyWiseXQIFClassDescLine(theDesc));
        }
    }

    /**
     * Constructor.
     *
     * @param pLines the data lines
     */
    public MoneyWiseXQIFClass(final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQClassLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;

        /* Loop through the lines */
        final Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            final String myLine = myIterator.next();

            /* Determine the category */
            final MoneyWiseQClassLineType myType = MoneyWiseQClassLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new MoneyWiseXQIFClassNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new MoneyWiseXQIFClassDescLine(myData));
                        myDesc = myData;
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected QClassLineType: " + myLine);
                }
            }
        }

        /* Build details */
        theName = myName;
        theDesc = myDesc;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the Name.
     *
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the Description.
     *
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    @Override
    public int compareTo(final MoneyWiseXQIFClass pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Class Name line.
     */
    public class MoneyWiseXQIFClassNameLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQClassLineType> {
        /**
         * Constructor.
         *
         * @param pName the Name
         */
        protected MoneyWiseXQIFClassNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public MoneyWiseQClassLineType getLineType() {
            return MoneyWiseQClassLineType.NAME;
        }

        /**
         * Obtain name.
         *
         * @return the name
         */
        public String getName() {
            return getValue();
        }
    }

    /**
     * The Class Description line.
     */
    public class MoneyWiseXQIFClassDescLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQClassLineType> {
        /**
         * Constructor.
         *
         * @param pDesc the Description
         */
        protected MoneyWiseXQIFClassDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }

        @Override
        public MoneyWiseQClassLineType getLineType() {
            return MoneyWiseQClassLineType.DESCRIPTION;
        }

        /**
         * Obtain description.
         *
         * @return the description
         */
        public String getDescription() {
            return getValue();
        }
    }
}
