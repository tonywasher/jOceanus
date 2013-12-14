/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QSecurityLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;

/**
 * Class representing a QIF Security record.
 */
public class QIFSecurity
        extends QIFRecord<QSecurityLineType> {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "Security";

    /**
     * Category Map.
     */
    protected static final Map<AccountCategoryClass, String> QIF_ACTCATMAP = createClassMap();

    /**
     * The Security.
     */
    private final String theName;

    /**
     * The Symbol.
     */
    private final String theSymbol;

    /**
     * The Account CategoryClass.
     */
    private final AccountCategoryClass theClass;

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
    private static Map<AccountCategoryClass, String> createClassMap() {
        /* Create the map */
        Map<AccountCategoryClass, String> myMap = new EnumMap<AccountCategoryClass, String>(AccountCategoryClass.class);

        /* Add the entries */
        myMap.put(AccountCategoryClass.SHARES, "Share");
        myMap.put(AccountCategoryClass.UNITTRUST, "Unit/Inv. Trust");
        myMap.put(AccountCategoryClass.LIFEBOND, "Bond");
        myMap.put(AccountCategoryClass.ASSET, "Real Estate");
        myMap.put(AccountCategoryClass.ENDOWMENT, "Trust");

        /* Return the map */
        return myMap;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the Security
     */
    public QIFSecurity(final QIFFile pFile,
                       final Account pSecurity) {
        /* Call super-constructor */
        super(pFile, QSecurityLineType.class);

        /* Store data */
        theName = pSecurity.getName();
        theSymbol = pSecurity.getSymbol();
        theClass = pSecurity.getAccountCategoryClass();

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
        AccountCategoryClass myClass = null;

        /* Loop through the lines */
        Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            String myLine = myIterator.next();

            /* Determine the category */
            QSecurityLineType myType = QSecurityLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case Name:
                        addLine(new QIFSecurityNameLine(myData));
                        myName = myData;
                        break;
                    case Symbol:
                        addLine(new QIFSecuritySymbolLine(myData));
                        mySymbol = myData;
                        break;
                    case SecType:
                        QIFSecurityTypeLine myQLine = new QIFSecurityTypeLine(myData);
                        addLine(myQLine);
                        myClass = myQLine.getAccountClass();
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

    /**
     * The Security Name line.
     */
    public class QIFSecurityNameLine
            extends QIFStringLine<QSecurityLineType> {
        @Override
        public QSecurityLineType getLineType() {
            return QSecurityLineType.Name;
        }

        /**
         * Obtain name.
         * @return the name
         */
        public String getName() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pName the Name
         */
        protected QIFSecurityNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }
    }

    /**
     * The Security Symbol line.
     */
    public class QIFSecuritySymbolLine
            extends QIFStringLine<QSecurityLineType> {
        @Override
        public QSecurityLineType getLineType() {
            return QSecurityLineType.Symbol;
        }

        /**
         * Obtain symbol.
         * @return the symbol
         */
        public String getSymbol() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pSymbol the Symbol
         */
        protected QIFSecuritySymbolLine(final String pSymbol) {
            /* Call super-constructor */
            super(pSymbol);
        }
    }

    /**
     * The Security Type line.
     */
    public class QIFSecurityTypeLine
            extends QIFStringLine<QSecurityLineType> {
        @Override
        public QSecurityLineType getLineType() {
            return QSecurityLineType.SecType;
        }

        /**
         * The Account Category Class.
         */
        private final AccountCategoryClass theClass;

        /**
         * Obtain account class.
         * @return the account class
         */
        public AccountCategoryClass getAccountClass() {
            return theClass;
        }

        /**
         * Constructor.
         * @param pClass the Security Class
         */
        protected QIFSecurityTypeLine(final AccountCategoryClass pClass) {
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
            AccountCategoryClass myClass = null;
            Iterator<Map.Entry<AccountCategoryClass, String>> myIterator = QIF_ACTCATMAP.entrySet().iterator();
            while (myIterator.hasNext()) {
                Map.Entry<AccountCategoryClass, String> myEntry = myIterator.next();

                /* If we have a match */
                if (pType.equals(myEntry.getValue())) {
                    myClass = myEntry.getKey();
                    break;
                }
            }

            /* Store the class */
            theClass = myClass;
        }
    }
}
