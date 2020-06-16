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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
     * Marker interface for intermediate.
     */
    interface ThemisAnalysisIntermediate extends ThemisAnalysisDataType {
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
     * The list of all references.
     */
    private List<ThemisAnalysisReference> theReferences;

    /**
     * Base constructor.
     */
    ThemisAnalysisDataMap() {
        theParent = null;
        theClassMap = new HashMap<>();
        theLocalTypes = new HashMap<>();
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
        theReferences = pParent.theReferences;
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
     * Set up file resources.
     */
    void setUpFileResoureces() {
        /* Local types are file wide */
        theFileTypes = theLocalTypes;

        /* Allocate the references list */
        theReferences = new ArrayList<>();
    }

    /**
     * declare file.
     * @param pFile the file
     */
    void declareFile(final ThemisAnalysisFile pFile) {
        theLocalTypes.put(pFile.getName(), pFile);
    }

    /**
     * declare import.
     * @param pImport the import
     */
    void declareImport(final ThemisAnalysisImport pImport) {
        theLocalTypes.put(pImport.getSimpleName(), pImport);
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
     * declare reference.
     * @param pRef the reference
     */
    void declareReference(final ThemisAnalysisReference pRef) {
        theReferences.add(pRef);
    }

    /**
     * Loop through the localTypes and update from classMap.
     */
    void updateFromClassMap() {
        /* Loop through the localDataTypes */
        for (Entry<String, ThemisAnalysisDataType> myEntry : theLocalTypes.entrySet()) {
            /* Access the value */
            final ThemisAnalysisDataType myType = myEntry.getValue();

            /* If this is an intermediate */
            if (myType instanceof ThemisAnalysisIntermediate) {
                /* Look up actual value */
                final ThemisAnalysisDataType myActual = lookUpActualDataType((ThemisAnalysisIntermediate) myType);
                if (myActual != null) {
                    myEntry.setValue(myActual);
                }
            }

            /* If this is an unknown value */
            if (myType instanceof ThemisAnalysisDataTypeUnknown) {
                final ThemisAnalysisDataTypeUnknown myUnknown = (ThemisAnalysisDataTypeUnknown) myType;
                System.out.println("Unknown: " + myUnknown.toString());
            }
        }

        /* Loop through the References */
        for (ThemisAnalysisReference myRef : theReferences) {
            /* Access the dataType */
            final ThemisAnalysisDataType myType = myRef.getDataType();

            /* If this is an intermediate */
            if (myType instanceof ThemisAnalysisIntermediate) {
                /* Look up actual value */
                final ThemisAnalysisDataType myActual = lookUpActualDataType((ThemisAnalysisIntermediate) myType);
                if (myActual != null) {
                    myRef.updateDataType(myActual);
                }
            }
        }
    }

    /**
     * Look up actual dataType.
     * @param pIntermediate the intermediate dataType.
     * @return the actual dataType (or null)
     */
    ThemisAnalysisDataType lookUpActualDataType(final ThemisAnalysisIntermediate pIntermediate) {
        /* If this is an import */
        if (pIntermediate instanceof ThemisAnalysisImport) {
            /* Replace it with actual object if known */
            final ThemisAnalysisImport myImport = (ThemisAnalysisImport) pIntermediate;
            return theClassMap.get(myImport.getFullName());
        }

        /* If this is a file */
        if (pIntermediate instanceof ThemisAnalysisFile) {
            /* Replace it with actual object if known */
            final ThemisAnalysisFile myFile = (ThemisAnalysisFile) pIntermediate;
            final String myFullName = myFile.getPackageName() + ThemisAnalysisChar.PERIOD + myFile.getName();
            return theClassMap.get(myFullName);
        }

        /* No change */
        return null;
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
