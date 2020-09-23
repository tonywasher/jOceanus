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
package net.sourceforge.joceanus.jthemis.statements;

import java.util.Iterator;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEmbedded;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;

/**
 * Package Parser.
 */
public final class ThemisProjectParser {
    /**
     * Private constructor.
     */
    private ThemisProjectParser() {
    }

    /**
     * parse a project.
     * @param pProject the project to parse
     */
    public static void parseProject(final ThemisAnalysisProject pProject) {
        /* Loop through the modules */
        for (ThemisAnalysisModule myModule : pProject.getModules()) {
            /* Process the module */
            parseModule(myModule);
        }
    }

    /**
     * parse a module.
     * @param pModule the module to parse
     */
    public static void parseModule(final ThemisAnalysisModule pModule) {
        /* Loop through the packages */
        for (ThemisAnalysisPackage myPackage : pModule.getPackages()) {
            /* Process the package */
            parsePackage(myPackage);
        }
    }

    /**
     * parse a package.
     * @param pPackage the package to parse
     */
    public static void parsePackage(final ThemisAnalysisPackage pPackage) {
        /* Loop through the files */
        for (ThemisAnalysisFile myFile : pPackage.getFiles()) {
            /* Process the file */
            parseContainer(myFile);
        }
    }

    /**
     * parse a container.
     * @param pContainer the container
     */
    public static void parseContainer(final ThemisAnalysisContainer pContainer) {
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
    private static void processStatement(final ThemisAnalysisStatement pStatement) {

    }

    /**
     * Process Embedded Statement.
     * @param pEmbedded the statement
     */
    private static void processEmbedded(final ThemisAnalysisEmbedded pEmbedded) {

    }
}
