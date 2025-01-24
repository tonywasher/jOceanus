/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.quicken.file;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQSecurityLineType;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;

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
     * @param pFile the QIF File
     * @param pSecurity the Security
     */
    public MoneyWiseQIFSecurity(final MoneyWiseQIFFile pFile,
                                final MoneyWiseSecurity pSecurity) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQSecurityLineType.class);

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
     * @param pFile the QIF File
     * @param pLines the data lines
     */
    protected MoneyWiseQIFSecurity(final MoneyWiseQIFFile pFile,
                                   final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQSecurityLineType.class);

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
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the symbol.
     * @return the Name
     */
    public String getSymbol() {
        return theSymbol;
    }

    /**
     * Create the CategoryClass to type map.
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
     * The Security Name line.
     */
    public class MoneyWiseQIFSecurityNameLine
            extends MoneyWiseQIFStringLine<MoneyWiseQSecurityLineType> {
        /**
         * Constructor.
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
         * @return the name
         */
        public String getName() {
            return getValue();
        }
    }

    /**
     * The Security Symbol line.
     */
    public class MoneyWiseQIFSecuritySymbolLine
            extends MoneyWiseQIFStringLine<MoneyWiseQSecurityLineType> {
        /**
         * Constructor.
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
         * @return the symbol
         */
        public String getSymbol() {
            return getValue();
        }
    }

    /**
     * The Security Type line.
     */
    public class MoneyWiseQIFSecurityTypeLine
            extends MoneyWiseQIFStringLine<MoneyWiseQSecurityLineType> {
        /**
         * The Security Type Class.
         */
        private final MoneyWiseSecurityClass theClass;

        /**
         * Constructor.
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
         * @return the security class
         */
        public MoneyWiseSecurityClass getSecurityClass() {
            return theClass;
        }
    }
}
