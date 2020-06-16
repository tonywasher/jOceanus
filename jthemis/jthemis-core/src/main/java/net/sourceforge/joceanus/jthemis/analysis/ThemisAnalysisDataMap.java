/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.analysis;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericVar;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisImports.ThemisAnalysisImport;

/**
 * The data map manager.
 */
public class ThemisAnalysisDataMap {
    /**
     * Marker interface for dataType.
     */
    interface ThemisAnalysisDataType {
    }

    /**
     * The base dataTypes.
     */
    private static final Map<String, ThemisAnalysisDataType> BASETYPES = createDataTypeMap();

    /**
     * The local dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theLocalTypes;

    /**
     * The map of all classes.
     */
    private final Map<String, ThemisAnalysisDataType> theClassMap;

    /**
     * The parent.
     */
    private final ThemisAnalysisDataMap theParent;

    /**
     * The file dataTypes.
     */
    private Map<String, ThemisAnalysisDataType> theFileTypes;

    /**
     * Base constructor.
     */
    ThemisAnalysisDataMap() {
        theParent = null;
        theClassMap = new HashMap<>();
        theLocalTypes = new HashMap<>();
        theFileTypes = theLocalTypes;
    }

    /**
     * Nested constructor.
     * @param pParent the parent dataMap
     */
    ThemisAnalysisDataMap(final ThemisAnalysisDataMap pParent) {
        theParent = pParent;
        theClassMap = pParent.theClassMap;
        theLocalTypes = new HashMap<>();
        theFileTypes = pParent.theFileTypes;
    }

    /**
     * lookUp dataType.
     * @param pToken the token.
     * @return the dataType (or null)
     */
    ThemisAnalysisDataType lookUpDataType(final String pToken) {
        /* Look up in local types */
        final ThemisAnalysisDataType myDataType = theLocalTypes.get(pToken);
        if (myDataType != null) {
            return myDataType;
        }

        /* Pass call on */
        return theParent == null
               ? BASETYPES.get(pToken)
               : theParent.lookUpDataType(pToken);
    }

    /**
     * declare class.
     * @param pClass the class
     */
    void declareClass(final ThemisAnalysisClass pClass) {
        theClassMap.put(pClass.getFullName(), pClass);
        theFileTypes.put(pClass.getShortName(), pClass);
    }

    /**
     * declare interface.
     * @param pInterface the interface
     */
    void declareInterface(final ThemisAnalysisInterface pInterface) {
        theClassMap.put(pInterface.getFullName(), pInterface);
        theFileTypes.put(pInterface.getShortName(), pInterface);
    }

    /**
     * declare enum.
     * @param pEnum the enum
     */
    void declareEnum(final ThemisAnalysisEnum pEnum) {
        theClassMap.put(pEnum.getFullName(), pEnum);
        theFileTypes.put(pEnum.getShortName(), pEnum);
    }

    /**
     * declare file.
     * @param pFile the file
     */
    void declareFile(final ThemisAnalysisFile pFile) {
        theFileTypes = theLocalTypes;
        theLocalTypes.put(pFile.getName(), new ThemisAnalysisDataTypeWrapper(pFile));
    }

    /**
     * declare import.
     * @param pImport the import
     */
    void declareImport(final ThemisAnalysisImport pImport) {
        theLocalTypes.put(pImport.getSimpleName(), new ThemisAnalysisDataTypeWrapper(pImport));
    }

    /**
     * declare generic variable.
     * @param pVar the generic variable
     */
    void declareGenericVar(final ThemisAnalysisGenericVar pVar) {
        theLocalTypes.put(pVar.getName(), pVar);
    }

    /**
     * declare unknown type.
     * @param pName the name
     * @return the new type
     */
    ThemisAnalysisDataType declareUnknown(final String pName) {
        final ThemisAnalysisDataType myType = new ThemisAnalysisDataTypeUnknown(pName);
        theFileTypes.put(pName, myType);
        return myType;
    }

    /**
     * Loop through the localTypes and update from classMap.
     */
    void updateFromClassMap() {
        /* Loop through the localDataTypes */
        for (ThemisAnalysisDataType myType : theLocalTypes.values()) {
            /* If this is a wrapped value */
            if (myType instanceof ThemisAnalysisDataTypeWrapper) {
                final ThemisAnalysisDataTypeWrapper myWrapper = (ThemisAnalysisDataTypeWrapper) myType;
                final ThemisAnalysisDataType myActual = theClassMap.get(myWrapper.getFullName());
                if (myActual != null) {
                    /* update it */
                    myWrapper.updateItem(myActual);
                }
            }
            /* If this is an unknownvalue */
            if (myType instanceof ThemisAnalysisDataTypeUnknown) {
                final ThemisAnalysisDataTypeUnknown myUnknown = (ThemisAnalysisDataTypeUnknown) myType;
                System.out.println("Unknown: " + myUnknown.toString());
            }
        }
    }

    /**
     * Create the dataTypeMap.
     * @return the new map
     */
    private static Map<String, ThemisAnalysisDataType> createDataTypeMap() {
        /* create the map */
        final Map<String, ThemisAnalysisDataType> myMap = new HashMap<>();

        /* Add the primitives */
        for (ThemisAnalysisPrimitive myPrimitive : ThemisAnalysisPrimitive.values()) {
            myMap.put(myPrimitive.toString(), myPrimitive);
            if (myPrimitive.getBoxed() != null) {
                myMap.put(myPrimitive.getBoxed(), myPrimitive);
            }
        }

        /* Add the java.lang classes */
        for (ThemisAnalysisJavaLang myClass : ThemisAnalysisJavaLang.values()) {
            myMap.put(myClass.toString(), myClass);
        }

        /* return the map */
        return myMap;
    }

    /**
     * DataType Wrapper.
     */
    public static class ThemisAnalysisDataTypeWrapper implements ThemisAnalysisDataType {
        /**
         * The Name.
         */
        private final String theName;

        /**
         * The fullName.
         */
        private final String theFullName;

        /**
         * The dataType.
         */
        private ThemisAnalysisDataType theDataType;

        /**
         * Constructor.
         * @param pFile the file
         */
        ThemisAnalysisDataTypeWrapper(final ThemisAnalysisFile pFile) {
            theName = pFile.getName();
            theFullName = pFile.getPackageName() + ThemisAnalysisChar.PERIOD + theName;
            theDataType = pFile;
        }

        /**
         * Constructor.
         * @param pImport the import
         */
        ThemisAnalysisDataTypeWrapper(final ThemisAnalysisImport pImport) {
            theName = pImport.getSimpleName();
            theFullName = pImport.getFullName();
            theDataType = pImport;
        }

        /**
         * Update the wrapped item.
         * @param pType the updated type
         */
        void updateItem(final ThemisAnalysisDataType pType) {
            theDataType = pType;
        }

        /**
         * Obtain the fullName.
         * @return the fullName
         */
        String getFullName() {
            return theFullName;
        }

        /**
         * Obtain the dataType.
         * @return the dataType
         */
        public ThemisAnalysisDataType getDataType() {
            return theDataType;
        }

        @Override
        public String toString() {
            return theName;
        }
    }

    /**
     * DataType Unknown.
     */
    public static class ThemisAnalysisDataTypeUnknown implements ThemisAnalysisDataType {
        /**
         * The Name.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pName the name
         */
        ThemisAnalysisDataTypeUnknown(final String pName) {
            theName = pName;
        }

        @Override
        public String toString() {
            return theName;
        }
    }
}
