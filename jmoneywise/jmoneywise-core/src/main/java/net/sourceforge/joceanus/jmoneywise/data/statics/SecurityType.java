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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SecurityType data type.
 */
public class SecurityType
        extends StaticData<SecurityType, SecurityTypeClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SECURITYTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SECURITYTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the SecurityType with
     * @param pSecType The SecurityType to copy
     */
    protected SecurityType(final SecurityTypeList pList,
                           final SecurityType pSecType) {
        super(pList, pSecType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the SecurityType with
     * @param pName Name of SecurityType
     * @throws OceanusException on error
     */
    private SecurityType(final SecurityTypeList pList,
                         final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the SecurityType with
     * @param pClass Class of SecurityType
     * @throws OceanusException on error
     */
    private SecurityType(final SecurityTypeList pList,
                         final SecurityTypeClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private SecurityType(final SecurityTypeList pList,
                         final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the SecurityTypeClass of the SecurityType.
     * @return the class
     */
    public SecurityTypeClass getSecurityClass() {
        return super.getStaticClass();
    }

    @Override
    public SecurityType getBase() {
        return (SecurityType) super.getBase();
    }

    @Override
    public SecurityTypeList getList() {
        return (SecurityTypeList) super.getList();
    }

    /**
     * Represents a list of {@link SecurityType} objects.
     */
    public static class SecurityTypeList
            extends StaticList<SecurityType, SecurityTypeClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE securityType list.
         * @param pData the DataSet for the list
         */
        public SecurityTypeList(final DataSet<?, ?> pData) {
            super(SecurityType.class, pData, MoneyWiseDataType.SECURITYTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private SecurityTypeList(final SecurityTypeList pSource) {
            super(pSource);
        }

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return SecurityType.FIELD_DEFS;
        }

        @Override
        protected Class<SecurityTypeClass> getEnumClass() {
            return SecurityTypeClass.class;
        }

        @Override
        protected SecurityTypeList getEmptyList(final ListStyle pStyle) {
            SecurityTypeList myList = new SecurityTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public SecurityType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a SecurityType */
            if (!(pItem instanceof SecurityType)) {
                throw new UnsupportedOperationException();
            }

            SecurityType myType = new SecurityType(this, (SecurityType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public SecurityType addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a SecurityType to the list.
         * @param pSecType the Name of the security type
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pSecType) throws OceanusException {
            /* Create a new Security Type */
            SecurityType mySecType = new SecurityType(this, pSecType);

            /* Check that this SecurityTypeId has not been previously added */
            if (!isIdUnique(mySecType.getId())) {
                mySecType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(mySecType, ERROR_VALIDATION);
            }

            /* Add the SecurityType to the list */
            append(mySecType);
        }

        @Override
        public SecurityType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the type */
            SecurityType myType = new SecurityType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }

        @Override
        protected SecurityType newItem(final SecurityTypeClass pClass) throws OceanusException {
            /* Create the type */
            SecurityType myType = new SecurityType(this, pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }
    }
}
