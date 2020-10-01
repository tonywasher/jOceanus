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
package net.sourceforge.joceanus.jthemis.statistics;

import java.util.Iterator;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEmbedded;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEnum;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisInterface;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMethod;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMFile;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMMethod;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMPackage;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStatistics;

/**
 * Package Parser.
 */
public class ThemisStatsParser {
    /**
     * The base statistics.
     */
    private final ThemisSMStatistics theSourceMeter;

    /**
     * Private constructor.
     * @param pBase the sourceMeter stats
     */
    public ThemisStatsParser(final ThemisSMStatistics pBase) {
        theSourceMeter = pBase;
    }

    /**
     * parse a project.
     * @param pProject the project to parse
     * @return the stats
     */
    public ThemisStatsProject parseProject(final ThemisAnalysisProject pProject) {
        /* Create the stats */
        final ThemisStatsProject myStats = new ThemisStatsProject(pProject);

        /* Loop through the modules */
        for (ThemisAnalysisModule myModule : pProject.getModules()) {
            /* Process the module */
            myStats.addModule(parseModule(myModule));
        }

        /* Return the projectStats */
        return myStats;
    }

    /**
     * parse a module.
     * @param pModule the module to parse
     * @return the stats
     */
    public ThemisStatsModule parseModule(final ThemisAnalysisModule pModule) {
        /* Create the stats */
        final ThemisStatsModule myStats = new ThemisStatsModule(pModule);

        /* Loop through the packages */
        for (ThemisAnalysisPackage myPackage : pModule.getPackages()) {
            /* Process the package */
            myStats.addPackage(parsePackage(myPackage));
        }

        /* Return the moduleStats */
        return myStats;
    }

    /**
     * parse a package.
     * @param pPackage the package to parse
     * @return the stats
     */
    public ThemisStatsPackage parsePackage(final ThemisAnalysisPackage pPackage) {
        /* Locate the matching sourceMeter stats */
        final ThemisSMPackage mySMPackage = theSourceMeter.findPackage(pPackage);

        /* Create the stats */
        final ThemisStatsPackage myStats = new ThemisStatsPackage(pPackage, mySMPackage);

        /* Loop through the files */
        for (ThemisAnalysisFile myFile : pPackage.getFiles()) {
            /* Process the file */
            myStats.addFile(parseFile(myStats, myFile));
        }

        /* Return the packageStats */
        return myStats;
    }

    /**
     * parse a file.
     * @param pPackage the owning package
     * @param pFile the file to parse
     * @return the stats
     */
    public ThemisStatsFile parseFile(final ThemisStatsPackage pPackage,
                                     final ThemisAnalysisFile pFile) {
        /* Locate the matching sourceMeter stats */
        final ThemisSMFile mySMFile = theSourceMeter.findFile(pPackage.getSourceMeter(), pFile);

        /* Create the stats */
        final ThemisStatsFile myStats = new ThemisStatsFile(pFile, mySMFile);

        /* Loop through the contents */
        for (ThemisAnalysisElement myElement : pFile.getContents()) {
            /* If this is a class/interface/enum */
            final ThemisAnalysisObject myClass = accessClass(myElement);
            if (myClass != null) {
                /* Process the class */
                myStats.addClass(parseClass(myStats, myClass));
            }
        }

        /* Return the fileStats */
        return myStats;
    }

    /**
     * parse a class.
     * @param pOwner the owner
     * @param pClass the class to parse
     * @return the stats
     */
    public ThemisStatsClass parseClass(final ThemisStatsOwner pOwner,
                                       final ThemisAnalysisObject pClass) {
        /* Locate the matching sourceMeter stats */
        final ThemisSMClass mySMClass = theSourceMeter.findClass(pOwner.getSourceMeter(), pClass);

        /* Create the stats */
        final ThemisStatsClass myStats = new ThemisStatsClass(pClass, mySMClass);

        /* Loop through the contents */
        for (ThemisAnalysisElement myElement : pClass.getContents()) {
            /* If this is a class/interface/enum */
            final ThemisAnalysisObject myClass = accessClass(myElement);
            if (myClass != null) {
                /* Process the class */
                myStats.addClass(parseClass(myStats, myClass));
            }

            /* If this is a method */
            final ThemisAnalysisMethod myMethod = accessMethod(myElement);
            if (myMethod != null) {
                /*  Process the method */
                myStats.addMethod(parseMethod(myStats, myMethod));
            }
        }

        /* Return the classStats */
        return myStats;
    }

    /**
     * parse a method.
     * @param pOwner the owner
     * @param pMethod the method to parse
     * @return the stats
     */
    public ThemisStatsMethod parseMethod(final ThemisStatsOwner pOwner,
                                         final ThemisAnalysisMethod pMethod) {
        /* Locate the matching sourceMeter stats */
        final ThemisSMMethod mySMMethod = theSourceMeter.findMethod(pOwner.getSourceMeter(), pMethod);

        /* Create the stats */
        final ThemisStatsMethod myStats = new ThemisStatsMethod(pMethod, mySMMethod);

        /* Loop through the contents */
        for (ThemisAnalysisElement myElement : pMethod.getContents()) {
            /* If this is a class/interface/enum */
            final ThemisAnalysisObject myClass = accessClass(myElement);
            if (myClass != null) {
                /* Process the class */
                myStats.addClass(parseClass(myStats, myClass));
            }
        }

        /* Return the classStats */
        return myStats;
    }

    /**
     * Access class/interface/enum element.
     * @param pElement the element
     * @return the class or null
     */
    private ThemisAnalysisObject accessClass(final ThemisAnalysisElement pElement) {
        return pElement instanceof ThemisAnalysisClass
                || pElement instanceof ThemisAnalysisEnum
                || pElement instanceof ThemisAnalysisInterface
                ? (ThemisAnalysisObject) pElement
                : null;
    }

    /**
     * Access method element.
     * @param pElement the element
     * @return the method or null
     */
    private ThemisAnalysisMethod accessMethod(final ThemisAnalysisElement pElement) {
        return pElement instanceof ThemisAnalysisMethod
                ? (ThemisAnalysisMethod) pElement
                : null;
    }

    /**
     * parse a container.
     * @param pContainer the container
     */
    public void parseContainer(final ThemisAnalysisContainer pContainer) {
        /* Loop through the contents */
        for (ThemisAnalysisElement myElement : pContainer.getContents()) {
            /* Process a nested container */
            if (myElement instanceof ThemisAnalysisContainer) {
                final ThemisAnalysisContainer myContainer = (ThemisAnalysisContainer) myElement;
                parseContainer(myContainer);
                final Iterator<ThemisAnalysisContainer> myIterator = myContainer.containerIterator();
                while (myIterator.hasNext()) {
                    parseContainer(myIterator.next());
                }
            }

            /* Process a statement holder */
            if (myElement instanceof ThemisAnalysisStatementHolder) {
                final ThemisAnalysisStatementHolder myHolder = (ThemisAnalysisStatementHolder) myElement;
                final Iterator<ThemisAnalysisStatement> myIterator = myHolder.statementIterator();
                while (myIterator.hasNext()) {
                    processStatement(myIterator.next());
                }
            }

            /* Process a statement */
            if (myElement instanceof ThemisAnalysisStatement) {
                processStatement((ThemisAnalysisStatement) myElement);
            }

            /* Process an embedded statement */
            if (myElement instanceof ThemisAnalysisEmbedded) {
                processEmbedded((ThemisAnalysisEmbedded) myElement);
            }
        }
    }

    /**
     * Process Statement.
     * @param pStatement the statement
     */
    private void processStatement(final ThemisAnalysisStatement pStatement) {

    }

    /**
     * Process Embedded Statement.
     * @param pEmbedded the statement
     */
    private void processEmbedded(final ThemisAnalysisEmbedded pEmbedded) {

    }
}
