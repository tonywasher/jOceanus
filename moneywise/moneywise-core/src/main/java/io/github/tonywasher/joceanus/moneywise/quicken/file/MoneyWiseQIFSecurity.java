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
package io.github.tonywasher.joceanus.moneywise.quicken.file;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQSecurityLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Class representing a QIF Security record.
 */
public class MoneyWiseQIFSecurity
        extends MoneyWiseQIFRecord<MoneyWiseQSecurityLineType>
        implements Comparable<MoneyWiseQIFSecurity> {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "Security";

    /**
     * Category Map.
     */
    protected static final Map<MoneyWiseSecurityClass, String> QIF_ACTCATMAP = createClassMap();

    /**
     * The Security.
     */
    private final String theName;

    /**
     * The Symbol.
     */
    private final String theSymbol;

    /**
     * The SecurityTypeClass.
     */
    private final MoneyWiseSecurityClass theClass;

    /**
     * Constructor.
     *
     * @param pSecurity the Security
     */
    public MoneyWiseQIFSecurity(final MoneyWiseSecurity pSecurity) {
        /* Call super-constructor */
        super(MoneyWiseQSecurityLineType.class);

        /* Store data */
        theName = pSecurity.getName();
        theSymbol = pSecurity.getSymbol();
        theClass = pSecurity.getCategoryClass();

        /* Build lines */
        addLine(new MoneyWiseQIFSecurityNameLine(theName));
        addLine(new MoneyWiseQIFSecuritySymbolLine(theSymbol));
        addLine(new MoneyWiseQIFSecurityTypeLine(theClass));
    }

    /**
     * Constructor.
     *
     * @param pLines the data lines
     */
    protected MoneyWiseQIFSecurity(final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQSecurityLineType.class);

        /* Determine details */
        String myName = null;
        String mySymbol = null;
        MoneyWiseSecurityClass myClass = null;

        /* Loop through the lines */
        final Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            final String myLine = myIterator.next();

            /* Determine the category */
            final MoneyWiseQSecurityLineType myType = MoneyWiseQSecurityLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new MoneyWiseQIFSecurityNameLine(myData));
                        myName = myData;
                        break;
                    case SYMBOL:
                        addLine(new MoneyWiseQIFSecuritySymbolLine(myData));
                        mySymbol = myData;
                        break;
                    case SECTYPE:
                        final MoneyWiseQIFSecurityTypeLine myQLine = new MoneyWiseQIFSecurityTypeLine(myData);
                        addLine(myQLine);
                        myClass = myQLine.getSecurityClass();
                        break;
                    default:
                        break;
                }
            }
        }

        /* Build details */
        theName = myName;
        theSymbol = mySymbol;
        theClass = myClass;
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
     * Obtain the symbol.
     *
     * @return the Name
     */
    public String getSymbol() {
        return theSymbol;
    }

    /**
     * Create the CategoryClass to type map.
     *
     * @return the map
     */
    private static Map<MoneyWiseSecurityClass, String> createClassMap() {
        /* Create the map */
        final Map<MoneyWiseSecurityClass, String> myMap = new EnumMap<>(MoneyWiseSecurityClass.class);

        /* Add the entries */
        myMap.put(MoneyWiseSecurityClass.SHARES, "Share");
        myMap.put(MoneyWiseSecurityClass.GROWTHUNITTRUST, "Unit/Inv. Trust");
        myMap.put(MoneyWiseSecurityClass.INCOMEUNITTRUST, "Unit/Inv. Trust");
        myMap.put(MoneyWiseSecurityClass.LIFEBOND, "Bond");
        myMap.put(MoneyWiseSecurityClass.ASSET, "Asset");
        myMap.put(MoneyWiseSecurityClass.ENDOWMENT, "Trust");
        myMap.put(MoneyWiseSecurityClass.VEHICLE, "Vehicle");
        myMap.put(MoneyWiseSecurityClass.PROPERTY, "Real Estate");

        /* Return the map */
        return myMap;
    }

    @Override
    public int compareTo(final MoneyWiseQIFSecurity pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Security line.
     *
     * @param <X> the line type
     */
    public abstract static class MoneyWiseQIFSecurityLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The security.
         */
        private final MoneyWiseQIFSecurity theSecurity;

        /**
         * Constructor.
         *
         * @param pSecurity the Security
         */
        protected MoneyWiseQIFSecurityLine(final MoneyWiseQIFSecurity pSecurity) {
            /* Store data */
            theSecurity = pSecurity;
        }

        @Override
        public String toString() {
            return theSecurity.toString();
        }

        /**
         * Obtain account.
         *
         * @return the account
         */
        public MoneyWiseQIFSecurity getSecurity() {
            return theSecurity;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the security name */
            pBuilder.append(theSecurity.getName());
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
            final MoneyWiseQIFSecurityLine<?> myLine = (MoneyWiseQIFSecurityLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theSecurity.equals(myLine.getSecurity());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theSecurity);
        }
    }
    
    /**
     * The Security Name line.
     */
    public static class MoneyWiseQIFSecurityNameLine
            extends MoneyWiseQIFStringLine<MoneyWiseQSecurityLineType> {
        /**
         * Constructor.
         *
         * @param pName the Name
         */
        protected MoneyWiseQIFSecurityNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public MoneyWiseQSecurityLineType getLineType() {
            return MoneyWiseQSecurityLineType.NAME;
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
     * The Security Symbol line.
     */
    public static class MoneyWiseQIFSecuritySymbolLine
            extends MoneyWiseQIFStringLine<MoneyWiseQSecurityLineType> {
        /**
         * Constructor.
         *
         * @param pSymbol the Symbol
         */
        protected MoneyWiseQIFSecuritySymbolLine(final String pSymbol) {
            /* Call super-constructor */
            super(pSymbol);
        }

        @Override
        public MoneyWiseQSecurityLineType getLineType() {
            return MoneyWiseQSecurityLineType.SYMBOL;
        }

        /**
         * Obtain symbol.
         *
         * @return the symbol
         */
        public String getSymbol() {
            return getValue();
        }
    }

    /**
     * The Security Type line.
     */
    public static class MoneyWiseQIFSecurityTypeLine
            extends MoneyWiseQIFStringLine<MoneyWiseQSecurityLineType> {
        /**
         * The Security Type Class.
         */
        private final MoneyWiseSecurityClass theClass;

        /**
         * Constructor.
         *
         * @param pClass the Security Class
         */
        protected MoneyWiseQIFSecurityTypeLine(final MoneyWiseSecurityClass pClass) {
            /* Call super-constructor */
            super(QIF_ACTCATMAP.get(pClass));

            /* Record the class */
            theClass = pClass;
        }

        /**
         * Constructor.
         *
         * @param pType the Security Type
         */
        protected MoneyWiseQIFSecurityTypeLine(final String pType) {
            /* Call super-constructor */
            super(pType);

            /* Loop through the map entries */
            MoneyWiseSecurityClass myClass = null;
            final Iterator<Entry<MoneyWiseSecurityClass, String>> myIterator = QIF_ACTCATMAP.entrySet().iterator();
            while (myIterator.hasNext()) {
                final Entry<MoneyWiseSecurityClass, String> myEntry = myIterator.next();

                /* If we have a match */
                if (pType.equals(myEntry.getValue())) {
                    myClass = myEntry.getKey();
                    break;
                }
            }

            /* Store the class */
            theClass = myClass;
        }

        @Override
        public MoneyWiseQSecurityLineType getLineType() {
            return MoneyWiseQSecurityLineType.SECTYPE;
        }

        @Override
        public String toString() {
            return theClass.toString();
        }

        /**
         * Obtain security class.
         *
         * @return the security class
         */
        public MoneyWiseSecurityClass getSecurityClass() {
            return theClass;
        }
    }
}
