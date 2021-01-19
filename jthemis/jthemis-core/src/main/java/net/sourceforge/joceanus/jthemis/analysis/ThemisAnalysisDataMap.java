/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
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
     * The logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(ThemisAnalysisDataMap.class);

    /**
     * The base dataTypes.
     */
    private static final Map<String, ThemisAnalysisDataType> BASETYPES = createDataTypeMap();

    /**
     * The hidden dataTypes.
     */
    private static final Map<String, String> HIDDENTYPES = createHiddenTypeMap();

    /**
     * The local dataTypes.
     */
    private final Map<String, ThemisAnalysisDataType> theLocalTypes;

    /**
     * The map of all classes.
     */
    private final Map<String, ThemisAnalysisObject> theClassMap;

    /**
     * The map of all classes.
     */
    private final Map<String, ThemisAnalysisObject> theShortClassMap;

    /**
     * The map of local/anonymous classes.
     */
    private final Map<String, Integer> theLocalIdMap;

    /**
     * The file dataTypes.
     */
    private Map<String, ThemisAnalysisDataType> theFileTypes;

    /**
     * The list of file classes.
     */
    private List<ThemisAnalysisObject> theFileClasses;

    /**
     * The list of all references.
     */
    private List<ThemisAnalysisReference> theReferences;

    /**
     * The parent.
     */
    private ThemisAnalysisDataMap theParent;

    /**
     * Base constructor.
     */
    ThemisAnalysisDataMap() {
        theParent = null;
        theClassMap = new LinkedHashMap<>();
        theShortClassMap = new LinkedHashMap<>();
        theLocalTypes = new HashMap<>();
        theLocalIdMap = new HashMap<>();
    }

    /**
     * Nested constructor.
     * @param pParent the parent dataMap
     */
    ThemisAnalysisDataMap(final ThemisAnalysisDataMap pParent) {
        theParent = pParent;
        theClassMap = pParent.theClassMap;
        theShortClassMap = pParent.theShortClassMap;
        theLocalTypes = new HashMap<>();
        theFileTypes = pParent.theFileTypes;
        theFileClasses = pParent.theFileClasses;
        theReferences = pParent.theReferences;
        theLocalIdMap = new HashMap<>();
    }

    /**
     * lookUp thedataType.
     * @param pToken the token.
     * @param pMethod is this a method call/def?
     * @return the dataType (or null)
     */
    ThemisAnalysisDataType lookUpDataType(final String pToken,
                                          final boolean pMethod) {
        /* Look up standard data type */
        ThemisAnalysisDataType myDataType = lookUpTheDataType(pToken);

        /* If it is not found and this is not a method call */
        if (myDataType == null && !pMethod) {
            /* Look for a root dataType */
            final int myIndex = pToken.indexOf(ThemisAnalysisChar.PERIOD);
            if (myIndex != -1) {
                /* Check for child of imported dataType */
                final String myStart = pToken.substring(0, myIndex);
                final String myEnd = pToken.substring(myIndex + 1);
                final ThemisAnalysisDataType myParent = lookUpTheDataType(myStart);

                /* If we have found it */
                if (myParent != null) {
                    myDataType = new ThemisAnalysisDataTypeChild(myParent, myEnd);
                    theFileTypes.put(pToken, myDataType);
                }
            }
        }

        /* Return the dataType */
        return myDataType;
    }

    /**
     * lookUp dataType.
     * @param pToken the token.
     * @return the dataType (or null)
     */
    ThemisAnalysisDataType lookUpTheDataType(final String pToken) {
        /* Look up in local types */
        final ThemisAnalysisDataType myDataType = theLocalTypes.get(pToken);
        if (myDataType != null) {
            return myDataType;
        }

        /* Pass call on */
        return theParent == null
               ? BASETYPES.get(pToken)
               : theParent.lookUpTheDataType(pToken);
    }

    /**
     * Record Object.
     * @param pObject the object.
     * @throws OceanusException on error
     */
    void declareObject(final ThemisAnalysisObject pObject) throws OceanusException {
        /* Access properties */
        final ThemisAnalysisProperties myProps = pObject.getProperties();
        final String myShortName = pObject.getShortName();

        /* Only register class globally if it is non-private */
        if (!myProps.hasModifier(ThemisAnalysisModifier.PRIVATE)) {
            /* Check that the shortName is unique */
            if (theShortClassMap.get(myShortName) != null) {
                throw new ThemisDataException("Duplicate class shortName: " + myShortName);
            }

            /* Register the object */
            theShortClassMap.put(myShortName, pObject);
            theClassMap.put(pObject.getFullName(), pObject);
        }

        /* Store locally */
        theFileTypes.put(myShortName, pObject);
        theFileClasses.add(pObject);
    }

    /**
     * Set up file resources.
     */
    void setUpFileResources() {
        /* Local types are file wide */
        theFileTypes = theLocalTypes;

        /* Allocate the fileClasses */
        theFileClasses = new ArrayList<>();

        /* Allocate the references list */
        theReferences = new ArrayList<>();
    }

    /**
     * Set parent.
     * @param pParent the parent dataMap.
     */
    void setParent(final ThemisAnalysisDataMap pParent) {
        theParent = pParent;
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
     * Consolidate the class map.
     * @throws OceanusException on error
     */
    void consolidateMap() throws OceanusException {
        /* Update intermediate references */
        updateIntermediates();

        /* resolve the immediate ancestors */
        resolveImmediateAncestors();
    }

    /**
     * Update intermediate references from classMap.
     */
    private void updateIntermediates() {
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
    ThemisAnalysisObject lookUpActualDataType(final ThemisAnalysisIntermediate pIntermediate) {
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
     * Resolve immediate ancestors.
     * @throws OceanusException on error
     */
    private void resolveImmediateAncestors() throws OceanusException {
        /* Loop through the fileClasses */
        for (ThemisAnalysisObject myClass : theFileClasses) {
            /* Loop through the ancestors of each class */
            for (ThemisAnalysisReference myRef : myClass.getAncestors()) {
                /* Resolve the ancestor */
                resolveAncestor(myRef);
            }
        }
    }

    /**
     * Resolve ancestors.
     * @param pAncestor the ancestor
     * @throws OceanusException on error
     */
    private void resolveAncestor(final ThemisAnalysisReference pAncestor) throws OceanusException {
        /* Only worry about unknown ancestors */
        final ThemisAnalysisDataType myDataType = pAncestor.getDataType();
        if (!(myDataType instanceof ThemisAnalysisDataTypeUnknown)) {
            return;
        }

        /* Look up the shortName */
        final String myName = ((ThemisAnalysisDataTypeUnknown) myDataType).theName;
        final ThemisAnalysisObject myActual = theShortClassMap.get(myName);
        if (myActual == null) {
            throw new ThemisDataException("Unknown ancestor: " + myName);
        }

        /* Update the reference */
        pAncestor.updateDataType(myActual);
    }

    /**
     * Resolve references.
     */
    void resolveReferences() {
        /* Process implicit imports */
        processImplicit();

        /* Loop through the References */
        for (ThemisAnalysisReference myRef : theReferences) {
            /* Access the dataType */
            final ThemisAnalysisDataType myType = myRef.getDataType();

            /* If this is an unknown */
            if (myType instanceof ThemisAnalysisDataTypeUnknown) {
                /* Check for implicit import */
                final ThemisAnalysisDataType myActual = theLocalTypes.get(myType.toString());
                if (!(myActual instanceof ThemisAnalysisDataTypeUnknown)) {
                    myRef.updateDataType(myActual);
                }
            }
        }
    }

    /**
     * Process implicit imports.
     */
    private void processImplicit() {
        /* Loop through the fileClasses */
        for (ThemisAnalysisObject myClass : theFileClasses) {
            /* Process the ancestors */
            processAncestors(myClass);
        }
    }

    /**
     * Process implicit imports.
     * @param pClass the class
     */
    private void processAncestors(final ThemisAnalysisObject pClass) {
        /* Loop through the ancestors */
        for (ThemisAnalysisReference myRef : pClass.getAncestors()) {
            /* Process the ancestor */
            final ThemisAnalysisDataType myDataType = myRef.getDataType();
            if (myDataType instanceof ThemisAnalysisObject) {
                final ThemisAnalysisObject myObject = (ThemisAnalysisObject) myDataType;
                processAncestor(myObject);
                processAncestors(myObject);
            }
        }
    }

    /**
     * Process ancestor.
     * @param pAncestor the ancestor
     */
    private void processAncestor(final ThemisAnalysisObject pAncestor) {
        /* Loop through the fileClasses */
        for (ThemisAnalysisObject myClass : theClassMap.values()) {
            /* If this is a direct child of the ancestor */
            final String myName = pAncestor.getFullName() + ThemisAnalysisChar.PERIOD + myClass.getShortName();
            if (myName.equals(myClass.getFullName())) {
                /* Add it to the local types */
                theLocalTypes.put(myClass.getShortName(), myClass);
            }
        }
    }

    /**
     * Report unknown references.
     */
    void reportUnknown() {
        /* Loop through the localDataTypes */
        for (Entry<String, ThemisAnalysisDataType> myEntry : theLocalTypes.entrySet()) {
            /* Access the value */
            final ThemisAnalysisDataType myType = myEntry.getValue();

            /* If this is an unknown value */
            if (myType instanceof ThemisAnalysisDataTypeUnknown) {
                /* Process the unknown reference */
                processUnknown(myEntry, (ThemisAnalysisDataTypeUnknown) myType);
            }
        }
    }

    /**
     * Process unknown reference.
     * @param pEntry the entry
     * @param pUnknown the unknown reference
     */
    private void processUnknown(final Entry<String, ThemisAnalysisDataType> pEntry,
                                final ThemisAnalysisDataTypeUnknown pUnknown) {
        /* If this is a hidden child */
        final String myName = pEntry.getKey();
        if (HIDDENTYPES.containsKey(myName)) {
            /* Look up the parent */
            final ThemisAnalysisDataType myParent = theLocalTypes.get(HIDDENTYPES.get(myName));
            if (myParent instanceof ThemisAnalysisImport) {
                /* Register a fake import and return */
                final String myFullName = ((ThemisAnalysisImport) myParent).getFullName() + ThemisAnalysisChar.PERIOD + myName;
                final ThemisAnalysisImport myChild = new ThemisAnalysisImport(myFullName);
                pEntry.setValue(myChild);
                return;
            }
        }

        /* Report it if we have not rectified the problem */
        LOGGER.info("Unknown: " + pUnknown.toString());
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
     * Create hidden dataType map.
     * @return the map
     */
    private static Map<String, String> createHiddenTypeMap() {
        final Map<String, String> myMap = new HashMap<>();
        myMap.put("StateChangeNotification", "Preloader");
        myMap.put("SortKey", "RowSorter");
        myMap.put("Entry", "RowFilter");
        return myMap;
    }

    /**
     * Obtain local id.
     * @param pName the name
     * @return the localId
     */
    public int getLocalId(final String pName) {
        final int myId = 1 + theLocalIdMap.computeIfAbsent(pName, s -> 0);
        theLocalIdMap.put(pName, myId);
        return myId;
    }

    /**
     * DataType Unknown.
     */
    public static class ThemisAnalysisDataTypeUnknown
            implements ThemisAnalysisDataType {
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

    /**
     * DataType Child.
     */
    public static class ThemisAnalysisDataTypeChild
            implements ThemisAnalysisDataType {
        /**
         * The Name.
         */
        private final ThemisAnalysisDataType theParent;

        /**
         * The Child.
         */
        private final String theChild;

        /**
         * Constructor.
         * @param pParent the parent
         * @param pChild the child
         */
        ThemisAnalysisDataTypeChild(final ThemisAnalysisDataType pParent,
                                    final String pChild) {
            theParent = pParent;
            theChild = pChild;
        }

        @Override
        public String toString() {
            return theParent.toString() + ThemisAnalysisChar.PERIOD + theChild;
        }
    }
}
