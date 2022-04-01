/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QSecurityLineType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFStringLine;

/**
 * Class representing a QIF Security record.
 */
public class QIFSecurity
        extends QIFRecord<QSecurityLineType>
        implements Comparable<QIFSecurity> {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "Security";

    /**
     * Category Map.
     */
    protected static final Map<SecurityTypeClass, String> QIF_ACTCATMAP = createClassMap();

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
    private final SecurityTypeClass theClass;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the Security
     */
    public QIFSecurity(final QIFFile pFile,
                       final Security pSecurity) {
        /* Call super-constructor */
        super(pFile, QSecurityLineType.class);

        /* Store data */
        theName = pSecurity.getName();
        theSymbol = pSecurity.getSymbol();
        theClass = pSecurity.getCategoryClass();

        /* Build lines */
        addLine(new QIFSecurityNameLine(theName));
        addLine(new QIFSecuritySymbolLine(theSymbol));
        addLine(new QIFSecurityTypeLine(theClass));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pLines the data lines
     */
    protected QIFSecurity(final QIFFile pFile,
                          final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QSecurityLineType.class);

        /* Determine details */
        String myName = null;
        String mySymbol = null;
        SecurityTypeClass myClass = null;

        /* Loop through the lines */
        final Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            final String myLine = myIterator.next();

            /* Determine the category */
            final QSecurityLineType myType = QSecurityLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new QIFSecurityNameLine(myData));
                        myName = myData;
                        break;
                    case SYMBOL:
                        addLine(new QIFSecuritySymbolLine(myData));
                        mySymbol = myData;
                        break;
                    case SECTYPE:
                        final QIFSecurityTypeLine myQLine = new QIFSecurityTypeLine(myData);
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
    private static Map<SecurityTypeClass, String> createClassMap() {
        /* Create the map */
        final Map<SecurityTypeClass, String> myMap = new EnumMap<>(SecurityTypeClass.class);

        /* Add the entries */
        myMap.put(SecurityTypeClass.SHARES, "Share");
        myMap.put(SecurityTypeClass.GROWTHUNITTRUST, "Unit/Inv. Trust");
        myMap.put(SecurityTypeClass.INCOMEUNITTRUST, "Unit/Inv. Trust");
        myMap.put(SecurityTypeClass.LIFEBOND, "Bond");
        myMap.put(SecurityTypeClass.ASSET, "Asset");
        myMap.put(SecurityTypeClass.ENDOWMENT, "Trust");
        myMap.put(SecurityTypeClass.VEHICLE, "Vehicle");
        myMap.put(SecurityTypeClass.PROPERTY, "Real Estate");

        /* Return the map */
        return myMap;
    }

    @Override
    public int compareTo(final QIFSecurity pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Security Name line.
     */
    public class QIFSecurityNameLine
            extends QIFStringLine<QSecurityLineType> {
        /**
         * Constructor.
         * @param pName the Name
         */
        protected QIFSecurityNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public QSecurityLineType getLineType() {
            return QSecurityLineType.NAME;
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
    public class QIFSecuritySymbolLine
            extends QIFStringLine<QSecurityLineType> {
        /**
         * Constructor.
         * @param pSymbol the Symbol
         */
        protected QIFSecuritySymbolLine(final String pSymbol) {
            /* Call super-constructor */
            super(pSymbol);
        }

        @Override
        public QSecurityLineType getLineType() {
            return QSecurityLineType.SYMBOL;
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
    public class QIFSecurityTypeLine
            extends QIFStringLine<QSecurityLineType> {
        /**
         * The Security Type Class.
         */
        private final SecurityTypeClass theClass;

        /**
         * Constructor.
         * @param pClass the Security Class
         */
        protected QIFSecurityTypeLine(final SecurityTypeClass pClass) {
            /* Call super-constructor */
            super(QIF_ACTCATMAP.get(pClass));

            /* Record the class */
            theClass = pClass;
        }

        /**
         * Constructor.
         * @param pType the Security Type
         */
        protected QIFSecurityTypeLine(final String pType) {
            /* Call super-constructor */
            super(pType);

            /* Loop through the map entries */
            SecurityTypeClass myClass = null;
            final Iterator<Entry<SecurityTypeClass, String>> myIterator = QIF_ACTCATMAP.entrySet().iterator();
            while (myIterator.hasNext()) {
                final Entry<SecurityTypeClass, String> myEntry = myIterator.next();

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
        public QSecurityLineType getLineType() {
            return QSecurityLineType.SECTYPE;
        }

        @Override
        public String toString() {
            return theClass.toString();
        }

        /**
         * Obtain security class.
         * @return the security class
         */
        public SecurityTypeClass getSecurityClass() {
            return theClass;
        }
    }
}
