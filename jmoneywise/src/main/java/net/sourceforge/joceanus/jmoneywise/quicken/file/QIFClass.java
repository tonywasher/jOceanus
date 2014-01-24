/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.EventClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QClassLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;

/**
 * Class representing a QIF Class record.
 */
public class QIFClass
        extends QIFRecord<QClassLineType> {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "Cat";

    /**
     * The Class Name.
     */
    private final String theName;

    /**
     * The Class Description.
     */
    private final String theDesc;

    /**
     * Obtain the Name.
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the Description.
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pTag the Event Tag
     */
    public QIFClass(final QIFFile pFile,
                    final EventClass pTag) {
        /* Call super-constructor */
        super(pFile, QClassLineType.class);

        /* Store data */
        theName = pTag.getName();
        theDesc = pTag.getDesc();

        /* Build lines */
        addLine(new QIFClassNameLine(theName));
        if (theDesc != null) {
            addLine(new QIFClassDescLine(theDesc));
        }
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pLines the data lines
     */
    protected QIFClass(final QIFFile pFile,
                       final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QClassLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;

        /* Loop through the lines */
        Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            String myLine = myIterator.next();

            /* Determine the category */
            QClassLineType myType = QClassLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new QIFClassNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new QIFClassDescLine(myData));
                        myDesc = myData;
                        break;
                    default:
                        break;
                }
            }
        }

        /* Build details */
        theName = myName;
        theDesc = myDesc;
    }

    /**
     * The Class Name line.
     */
    public class QIFClassNameLine
            extends QIFStringLine<QClassLineType> {
        @Override
        public QClassLineType getLineType() {
            return QClassLineType.NAME;
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
        protected QIFClassNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }
    }

    /**
     * The Class Description line.
     */
    public class QIFClassDescLine
            extends QIFStringLine<QClassLineType> {
        @Override
        public QClassLineType getLineType() {
            return QClassLineType.DESCRIPTION;
        }

        /**
         * Obtain description.
         * @return the description
         */
        public String getDescription() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pDesc the Description
         */
        protected QIFClassDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }
    }
}
