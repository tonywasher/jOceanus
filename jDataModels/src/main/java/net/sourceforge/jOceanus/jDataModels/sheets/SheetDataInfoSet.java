/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.jOceanus.jDataModels.sheets;

import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.data.DataInfo;
import net.sourceforge.jOceanus.jDataModels.data.DataInfo.DataInfoList;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoClass;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;

/**
 * Utility class to handle DataInfo associated with an owner.
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <E> the Info type class
 */
public class SheetDataInfoSet<T extends DataInfo<T, O, I, E>, O extends DataItem & Comparable<? super O>, I extends StaticData<I, E>, E extends Enum<E> & DataInfoClass> {
    /**
     * Class column.
     */
    private final Class<E> theClass;

    /**
     * Owning data sheet.
     */
    private final SheetDataItem<O> theOwner;

    /**
     * Base column.
     */
    private final int theBaseCol;

    /**
     * Constructor.
     * @param pClass the InfoClass
     * @param pOwner the owning spreadsheet
     * @param pBaseCol the base column
     */
    public SheetDataInfoSet(final Class<E> pClass,
                            final SheetDataItem<O> pOwner,
                            final int pBaseCol) {
        /* Store parameters */
        theClass = pClass;
        theOwner = pOwner;
        theBaseCol = pBaseCol + 1;
    }

    /**
     * Fill in titles.
     * @throws JDataException on error
     */
    public void prepareSheet() throws JDataException {
        /* Loop through the class values */
        for (E myClass : theClass.getEnumConstants()) {
            /* Obtain the id and data columns */
            int iCol = getIdColumn(myClass);
            int iData = iCol + 1;

            /* Write titles */
            theOwner.writeHeader(iCol, DataItem.FIELD_ID.getName());
            theOwner.writeHeader(iData, myClass.name());
        }
    }

    /**
     * Fill in titles.
     * @throws JDataException on error
     */
    public void formatSheet() throws JDataException {
        /* Loop through the class values */
        for (E myClass : theClass.getEnumConstants()) {
            /* Obtain the id and data columns */
            int iCol = getIdColumn(myClass);
            int iData = iCol + 1;

            /* Write titles */
            theOwner.setIntegerColumn(iCol);
            theOwner.setHiddenColumn(iCol);

            /* Switch on the data type */
            switch (myClass.getDataType()) {
                case MONEY:
                    theOwner.setMoneyColumn(iData);
                    break;
                case RATE:
                    theOwner.setRateColumn(iData);
                    break;
                case UNITS:
                    theOwner.setUnitsColumn(iData);
                    break;
                case DILUTION:
                    theOwner.setDilutionColumn(iData);
                    break;
                case PRICE:
                    theOwner.setPriceColumn(iData);
                    break;
                case DATEDAY:
                    theOwner.setDateColumn(iData);
                    break;
                case INTEGER:
                    if (!myClass.isLink()) {
                        theOwner.setIntegerColumn(iData);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Set column width.
     * @param pClass the class to set
     * @param pNumChars the character width to set
     */
    protected void setColumnWidth(final E pClass,
                                  final int pNumChars) {
        /* Obtain the data column */
        int iCol = 1 + getIdColumn(pClass);

        /* Set the width */
        theOwner.setColumnWidth(iCol, pNumChars);
    }

    /**
     * Apply Data Validation.
     * @param pClass the class to set
     * @param pList name of validation range
     * @throws JDataException on error
     */
    protected void applyDataValidation(final E pClass,
                                       final String pList) throws JDataException {
        /* Obtain the data column */
        int iCol = 1 + getIdColumn(pClass);

        /* Set the width */
        theOwner.applyDataValidation(iCol, pList);
    }

    /**
     * Write data info set.
     * @param pInfoSet the DataInfoSet to write
     * @throws JDataException on error
     */
    public void writeDataInfoSet(final DataInfoSet<T, O, I, E> pInfoSet) throws JDataException {
        /* Loop through the items */
        for (T myInfo : pInfoSet) {
            /* Skip if deleted */
            if (myInfo.isDeleted()) {
                continue;
            }

            /* Write the info */
            writeDataInfo(myInfo);
        }
    }

    /**
     * Write data info.
     * @param pInfo the Data info to write
     * @throws JDataException on error
     */
    private void writeDataInfo(final T pInfo) throws JDataException {
        /* Obtain the id and data columns */
        E myClass = pInfo.getInfoClass();
        int iCol = getIdColumn(myClass);
        int iData = iCol + 1;

        /* Write the id */
        theOwner.writeInteger(iCol, pInfo.getId());

        /* Switch on the data type */
        switch (myClass.getDataType()) {
            case MONEY:
            case RATE:
            case PRICE:
            case UNITS:
            case DILUTION:
                theOwner.writeDecimal(iData, pInfo.getValue(JDecimal.class));
                break;
            case CHARARRAY:
                theOwner.writeChars(iData, pInfo.getValue(char[].class));
                break;
            case DATEDAY:
                theOwner.writeDate(iData, pInfo.getValue(JDateDay.class));
                break;
            case INTEGER:
                /* If this is a link */
                if (myClass.isLink()) {
                    /* Write the link name */
                    theOwner.writeString(iData, pInfo.getLinkName());
                } else {
                    /* Write as integer */
                    theOwner.writeInteger(iData, pInfo.getValue(Integer.class));
                }
                break;
            default:
                break;
        }
    }

    /**
     * Load data info set.
     * @param pInfoList the DataInfoList to add to
     * @param pOwner the owner of the info
     * @throws JDataException on error
     */
    public void loadDataInfoSet(final DataInfoList<T, O, I, E> pInfoList,
                                final O pOwner) throws JDataException {
        /* Loop through the class values */
        for (E myClass : theClass.getEnumConstants()) {
            /* Access the id and data columns */
            int iCol = getIdColumn(myClass);
            int iData = iCol + 1;

            /* Switch on data type */
            switch (myClass.getDataType()) {
                case DATEDAY:
                    Date myDate = theOwner.loadDate(iData);
                    if (myDate != null) {
                        Integer myId = theOwner.loadInteger(iCol);
                        pInfoList.addOpenItem(myId, pOwner, myClass, myDate);
                    }
                    break;
                case INTEGER:
                    /* If this is a link */
                    if (myClass.isLink()) {
                        String myValue = theOwner.loadString(iData);
                        if (myValue != null) {
                            Integer myId = theOwner.loadInteger(iCol);
                            pInfoList.addOpenItem(myId, pOwner, myClass, myValue);
                        }
                        /* else standard integer */
                    } else {
                        Integer myValue = theOwner.loadInteger(iData);
                        if (myValue != null) {
                            Integer myId = theOwner.loadInteger(iCol);
                            pInfoList.addOpenItem(myId, pOwner, myClass, myValue);
                        }
                    }
                    break;
                default:
                    String myValue = theOwner.loadString(iData);
                    if (myValue != null) {
                        Integer myId = theOwner.loadInteger(iCol);
                        pInfoList.addOpenItem(myId, pOwner, myClass, myValue);
                    }
                    break;
            }
        }
    }

    /**
     * Obtain id column for class.
     * @param pClass the class
     * @return the id column.
     */
    private int getIdColumn(final E pClass) {
        /* Calculate the id column */
        return theBaseCol
               + (2 * (pClass.getClassId() - 1));
    }

    /**
     * Obtain count of additional columns.
     * @return the additional columns.
     */
    public int getXtraColumnCount() {
        /* Calculate the extra columns */
        return 2 * theClass.getEnumConstants().length;
    }
}
