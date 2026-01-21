/*
 * Themis: Java Project Framework
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
package io.github.tonywasher.joceanus.themis.lethe.statistics;

import io.github.tonywasher.joceanus.themis.lethe.analysis.ThemisAnalysisEnum;
import io.github.tonywasher.joceanus.themis.lethe.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import io.github.tonywasher.joceanus.themis.lethe.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import io.github.tonywasher.joceanus.themis.lethe.analysis.ThemisAnalysisInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class statistics.
 */
public class ThemisStatsClass
        extends ThemisStatsBase {
    /**
     * The class.
     */
    private final ThemisAnalysisObject theClass;

    /**
     * The class list.
     */
    private final List<ThemisStatsBase> theClasses;

    /**
     * The method list.
     */
    private final List<ThemisStatsBase> theMethods;

    /**
     * The class type.
     */
    private final ThemisStatsClassType theType;

    /**
     * Constructor.
     *
     * @param pClass the class
     */
    ThemisStatsClass(final ThemisAnalysisObject pClass) {
        /* Store parameters */
        theClass = pClass;

        /* Create lists */
        theClasses = new ArrayList<>();
        theMethods = new ArrayList<>();

        /* Determine class type */
        if (pClass instanceof ThemisAnalysisInterface myInterface) {
            theType = myInterface.isAnnotation()
                    ? ThemisStatsClassType.ANNOTATION
                    : ThemisStatsClassType.INTERFACE;
        } else if (pClass instanceof ThemisAnalysisEnum) {
            theType = ThemisStatsClassType.ENUM;
        } else {
            theType = ThemisStatsClassType.CLASS;
        }
    }

    /**
     * Obtain the class.
     *
     * @return the class
     */
    public ThemisAnalysisObject getObject() {
        return theClass;
    }

    /**
     * Obtain the classType.
     *
     * @return the classType
     */
    public ThemisStatsClassType getClassType() {
        return theType;
    }

    @Override
    public Iterator<ThemisStatsBase> childIterator() {
        return new ThemisIteratorChain<>(theClasses.iterator(), theMethods.iterator());
    }

    @Override
    public String toString() {
        return theClass.getShortName();
    }

    @Override
    public void addClass(final ThemisStatsClass pClass) {
        /* Add class to list */
        theClasses.add(pClass);
        pClass.setParent(this);

        /* Adjust counts */
        pClass.addStatsToTotals();
        addChildTotals(pClass);
    }

    @Override
    public void addMethod(final ThemisStatsMethod pMethod) {
        /* Add method to list */
        theMethods.add(pMethod);
        pMethod.setParent(this);

        /* Add method counts to class */
        pMethod.addMethodStatsToTotals();
        addMethodStatsToClass(pMethod);
        addChildTotals(pMethod);
    }
}
