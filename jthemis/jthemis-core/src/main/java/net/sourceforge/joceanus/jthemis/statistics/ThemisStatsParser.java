/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisAnnotation;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisAnonClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisBlank;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisBlock;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisCase;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisCatch;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisComment;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisDoWhile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElse;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEmbedded;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEnum;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisField;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFinally;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFor;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisImports;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisInterface;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisLambda;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMethod;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisSwitch;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisTry;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisWhile;

/**
 * Package Parser.
 */
public class ThemisStatsParser {
    /**
     * The cached documentation comment.
     */
    private ThemisAnalysisComment theCachedDocComment;

    /**
     * The cached annotation.
     */
    private ThemisAnalysisAnnotation theCachedAnnotation;

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
        /* Create the stats */
        final ThemisStatsPackage myStats = new ThemisStatsPackage(pPackage);

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
        /* Create the stats */
        final ThemisStatsFile myStats = new ThemisStatsFile(pFile);

        /* process the container */
        processContainer(myStats, pFile);

        /* Return the fileStats */
        return myStats;
    }

    /**
     * parse a class.
     * @param pOwner the owner
     * @param pClass the class to parse
     * @return the stats
     */
    public ThemisStatsClass parseClass(final ThemisStatsBase pOwner,
                                       final ThemisAnalysisObject pClass) {
        /* Create the stats */
        final ThemisStatsClass myStats = new ThemisStatsClass(pClass);

        /* process the container */
        processCachedItems(pOwner, myStats);
        processContainer(myStats, pClass);

        /* Return the classStats */
        return myStats;
    }

    /**
     * parse a method.
     * @param pOwner the owner
     * @param pMethod the method to parse
     * @return the stats
     */
    public ThemisStatsMethod parseMethod(final ThemisStatsBase pOwner,
                                         final ThemisAnalysisMethod pMethod) {
        /* Create the stats */
        final ThemisStatsMethod myStats = new ThemisStatsMethod(pMethod);

        /* process the container */
        processCachedItems(pOwner, myStats);
        processContainer(myStats, pMethod);

        /* Return the classStats */
        return myStats;
    }

    /**
     * process container.
     * @param pOwner the owner
     * @param pContainer the container
     */
    public void processContainer(final ThemisStatsBase pOwner,
                                 final ThemisAnalysisContainer pContainer) {
        /* Loop through the contents */
        for (ThemisAnalysisElement myElement : pContainer.getContents()) {
            /* If this is an annotation */
            if (myElement instanceof ThemisAnalysisAnnotation) {
                processAnnotation(pOwner, (ThemisAnalysisAnnotation) myElement, true);

                /* If this is an anonymous class */
            } else if (myElement instanceof ThemisAnalysisAnonClass) {
                processAnonClass(pOwner, (ThemisAnalysisAnonClass) myElement);

                /* If this is a blank */
            } else if (myElement instanceof ThemisAnalysisBlank) {
                processBlank(pOwner, (ThemisAnalysisBlank) myElement);

                /* If this is a block */
            } else if (myElement instanceof ThemisAnalysisBlock) {
                processBlock(pOwner, (ThemisAnalysisBlock) myElement);

                /* If this is a case */
            } else if (myElement instanceof ThemisAnalysisCase) {
                processCase(pOwner, (ThemisAnalysisCase) myElement);

                /* If this is a class */
            } else if (myElement instanceof ThemisAnalysisClass) {
                processClass(pOwner, (ThemisAnalysisClass) myElement);

                /* If this is a comment */
            } else if (myElement instanceof ThemisAnalysisComment) {
                processComment(pOwner, (ThemisAnalysisComment) myElement, true);

            /* If this is a doWhile */
            } else if (myElement instanceof ThemisAnalysisDoWhile) {
                processDoWhile(pOwner, (ThemisAnalysisDoWhile) myElement);

                /* If this is an embedded */
            } else if (myElement instanceof ThemisAnalysisEmbedded) {
                processEmbedded(pOwner, (ThemisAnalysisEmbedded) myElement);

                 /* If this is an enum */
            } else if (myElement instanceof ThemisAnalysisEnum) {
                processEnum(pOwner, (ThemisAnalysisEnum) myElement);

                /* If this is a field */
            } else if (myElement instanceof ThemisAnalysisField) {
                processField(pOwner, (ThemisAnalysisField) myElement);

                /* If this is a for */
            } else if (myElement instanceof ThemisAnalysisFor) {
                processFor(pOwner, (ThemisAnalysisFor) myElement);

                /* If this is an if */
            } else if (myElement instanceof ThemisAnalysisIf) {
                processIf(pOwner, (ThemisAnalysisIf) myElement);

                /* If this is an imports */
            } else if (myElement instanceof ThemisAnalysisImports) {
                processImports(pOwner, (ThemisAnalysisImports) myElement);

                /* If this is an interface */
            } else if (myElement instanceof ThemisAnalysisInterface) {
                processInterface(pOwner, (ThemisAnalysisInterface) myElement);

                /* If this is a lambda */
            } else if (myElement instanceof ThemisAnalysisLambda) {
                processLambda(pOwner, (ThemisAnalysisLambda) myElement);

                /* If this is a method */
            } else if (myElement instanceof ThemisAnalysisMethod) {
                processMethod(pOwner, (ThemisAnalysisMethod) myElement);

                /* If this is a package */
            } else if (myElement instanceof ThemisAnalysisPackage) {
                processPackage(pOwner);

                /* If this is a statement */
            } else if (myElement instanceof ThemisAnalysisStatement) {
                processStatement(pOwner, (ThemisAnalysisStatement) myElement);

                /* If this is a switch */
            } else if (myElement instanceof ThemisAnalysisSwitch) {
                processSwitch(pOwner, (ThemisAnalysisSwitch) myElement);

                /* If this is a try */
            } else if (myElement instanceof ThemisAnalysisTry) {
                processTry(pOwner, (ThemisAnalysisTry) myElement);

                /* If this is a while */
            } else if (myElement instanceof ThemisAnalysisWhile) {
                processWhile(pOwner, (ThemisAnalysisWhile) myElement);
            }

            /* Adjust the cached items */
            adjustCachedItems(myElement);
        }
    }

    /**
     * process annotation.
     * @param pOwner the owner
     * @param pAnnotation the annotation
     * @param pAddToStats addTo stats or remove from Stats?
     */
    private static void processAnnotation(final ThemisStatsBase pOwner,
                                          final ThemisAnalysisAnnotation pAnnotation,
                                          final boolean pAddToStats) {
        /* Adjust statistics */
        final int myAdjust = pAddToStats
                                ? pAnnotation.getNumLines()
                                : -pAnnotation.getNumLines();
        adjustLinesOfCode(pOwner, myAdjust);
    }

    /**
     * process anonymous class.
     * @param pOwner the owner
     * @param pAnon the anonymous class
     */
    private void processAnonClass(final ThemisStatsBase pOwner,
                                  final ThemisAnalysisAnonClass pAnon) {
        /* Parse the class */
        final ThemisStatsClass myClass = parseClass(pOwner, pAnon);

        /* Adjust statistics */
        pOwner.incrementStat(ThemisStat.NCL);

        /* Adjust the stats and add to owner */
        adjustLinesOfCode(myClass, pAnon.getNumLines());
        pOwner.addClass(myClass);
    }

    /**
     * process blank.
     * @param pOwner the owner
     * @param pBlank the blank
     */
    private static void processBlank(final ThemisStatsBase pOwner,
                                     final ThemisAnalysisBlank pBlank) {
        /* Adjust statistics */
        final int myBlanks = pBlank.getNumLines();
        pOwner.adjustStat(ThemisStat.LOC, myBlanks);
    }

    /**
     * process block.
     * @param pOwner the owner
     * @param pBlock the block
     */
    private void processBlock(final ThemisStatsBase pOwner,
                              final ThemisAnalysisBlock pBlock) {
        /* process the container */
        processContainer(pOwner, pBlock);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pBlock.getNumLines());
        if (pBlock.isSynchronized()) {
            adjustNumberOfStatements(pOwner, 1);
        }
    }

    /**
     * process class.
     * @param pOwner the owner
     * @param pClass the class
     */
    private void processClass(final ThemisStatsBase pOwner,
                              final ThemisAnalysisClass pClass) {
        /* Parse the class */
        final ThemisStatsClass myClass = parseClass(pOwner, pClass);

        /* Adjust statistics */
        pOwner.incrementStat(ThemisStat.NCL);

        /* Adjust the stats and add to owner */
        adjustLinesOfCode(myClass, pClass.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addClass(myClass);
    }

    /**
     * process comment.
     * @param pOwner the owner
     * @param pComment the comment
     * @param pAddToStats addTo stats or remove from Stats?
     */
    private static void processComment(final ThemisStatsBase pOwner,
                                       final ThemisAnalysisComment pComment,
                                       final boolean pAddToStats) {
        /* Adjust the stats */
        final int myAdjust = pAddToStats
                                 ? pComment.getNumLines()
                                 : -pComment.getNumLines();
        pOwner.adjustStat(ThemisStat.LOC, myAdjust);
        pOwner.adjustStat(ThemisStat.CLOC, myAdjust);
        if (pComment.isJavaDoc()) {
            pOwner.adjustStat(ThemisStat.DLOC, myAdjust);
        }
    }

    /**
     * process doWhile.
     * @param pOwner the owner
     * @param pDoWhile the doWhile
     */
    private void processDoWhile(final ThemisStatsBase pOwner,
                                final ThemisAnalysisDoWhile pDoWhile) {
        /* process the container */
        processContainer(pOwner, pDoWhile);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pDoWhile.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * process embedded element.
     * @param pOwner the owner
     * @param pEmbedded the embedded
     */
    private void processEmbedded(final ThemisStatsBase pOwner,
                                 final ThemisAnalysisEmbedded pEmbedded) {
        /* process the container */
        processContainer(pOwner, pEmbedded);

        /* Adjust statistics */
        adjustNumberOfStatements(pOwner, 1);
        adjustLinesOfCode(pOwner, pEmbedded.getNumLines());
    }

    /**
     * process enum.
     * @param pOwner the owner
     * @param pEnum the enum
     */
    private void processEnum(final ThemisStatsBase pOwner,
                             final ThemisAnalysisEnum pEnum) {
        /* Parse the enum */
        final ThemisStatsClass myEnum = parseClass(pOwner, pEnum);

        /* Adjust owner statistics */
        pOwner.incrementStat(ThemisStat.NEN);

        /* Adjust the stats and add to owner */
        adjustLinesOfCode(myEnum, pEnum.getNumLines());
        adjustNumberOfAttributes(myEnum, pEnum.getNumEnums());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addClass(myEnum);
    }

    /**
     * process field.
     * @param pOwner the owner
     * @param pField the field
     */
    private static void processField(final ThemisStatsBase pOwner,
                                     final ThemisAnalysisField pField) {
        /* Adjust the stats */
        adjustLinesOfCode(pOwner, pField.getNumLines());

        /* Counts as a statement if there is an initializer */
        if (pField.statementIterator().hasNext()) {
            adjustNumberOfStatements(pOwner, 1);
        }

        /* Attributes are only relevant to classes */
        if (pOwner instanceof ThemisStatsClass) {
            adjustNumberOfAttributes(pOwner, 1);
        }
    }

    /**
     * process for.
     * @param pOwner the owner
     * @param pFor the for
     */
    private void processFor(final ThemisStatsBase pOwner,
                            final ThemisAnalysisFor pFor) {
        /* process the container */
        processContainer(pOwner, pFor);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pFor.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * process if.
     * @param pOwner the owner
     * @param pIf the if
     */
    private void processIf(final ThemisStatsBase pOwner,
                           final ThemisAnalysisIf pIf) {
        /* process the container */
        processContainer(pOwner, pIf);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pIf.getNumLines());
        adjustNumberOfStatements(pOwner, 1);

        /* Handle else */
        final ThemisAnalysisElse myElse = pIf.getElse();
        if (myElse != null) {
            processElse(pOwner, myElse);
        }
    }

    /**
     * process else.
     * @param pOwner the owner
     * @param pElse the else
     */
    private void processElse(final ThemisStatsBase pOwner,
                             final ThemisAnalysisElse pElse) {
        /* process the container */
        processContainer(pOwner, pElse);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pElse.getNumLines());
        adjustNumberOfStatements(pOwner, 1);

        /* Handle additional else */
        final ThemisAnalysisElse myElse = pElse.getElse();
        if (myElse != null) {
            processElse(pOwner, myElse);
        }
    }

    /**
     * process imports.
     * @param pOwner the owner
     * @param pImports the imports
     */
    private static void processImports(final ThemisStatsBase pOwner,
                                       final ThemisAnalysisImports pImports) {
        /* Adjust the stats */
        adjustLinesOfCode(pOwner, pImports.getNumLines());
        adjustNumberOfStatements(pOwner, pImports.getNumLines());
    }

    /**
     * process interface.
     * @param pOwner the owner
     * @param pInterface the interface
     */
    private void processInterface(final ThemisStatsBase pOwner,
                                  final ThemisAnalysisInterface pInterface) {
        /* Parse the interface */
        final ThemisStatsClass myIFace = parseClass(pOwner, pInterface);

        /* Adjust owner statistics */
        pOwner.incrementStat(ThemisStat.NIN);

        /* Adjust the stats and add to owner */
        adjustLinesOfCode(myIFace, pInterface.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addClass(myIFace);
    }

    /**
     * process lambda.
     * @param pOwner the owner
     * @param pLambda the lambda
     */
    private void processLambda(final ThemisStatsBase pOwner,
                               final ThemisAnalysisLambda pLambda) {
        /* process the container */
        processContainer(pOwner, pLambda);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pLambda.getNumLines());
    }

    /**
     * process method.
     * @param pOwner the owner
     * @param pMethod the method
     */
    private void processMethod(final ThemisStatsBase pOwner,
                               final ThemisAnalysisMethod pMethod) {
        /*  Parse the method */
        final ThemisStatsMethod myMethod = parseMethod(pOwner, pMethod);

        /* Adjust owner statistics */
        pOwner.incrementStat(ThemisStat.NM);

        /* Adjust the lines of code and add to owner */
        adjustLinesOfCode(myMethod, pMethod.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addMethod(myMethod);
    }

    /**
     * process package.
     * @param pOwner the owner
     */
    private static void processPackage(final ThemisStatsBase pOwner) {
        /* Adjust the stats */
        adjustLinesOfCode(pOwner, 1);
    }

    /**
     * process statement.
     * @param pOwner the owner
     * @param pStatement the statement
     */
    private static void processStatement(final ThemisStatsBase pOwner,
                                         final ThemisAnalysisStatement pStatement) {
        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pStatement.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * process switch.
     * @param pOwner the owner
     * @param pSwitch the switch
     */
    private void processSwitch(final ThemisStatsBase pOwner,
                               final ThemisAnalysisSwitch pSwitch) {
        /* process the container */
        processContainer(pOwner, pSwitch);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pSwitch.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * process case.
     * @param pOwner the owner
     * @param pCase the case
     */
    private void processCase(final ThemisStatsBase pOwner,
                             final ThemisAnalysisCase pCase) {
        /* process the container */
        processContainer(pOwner, pCase);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pCase.getNumLines());
    }

    /**
     * process try.
     * @param pOwner the owner
     * @param pTry the try
     */
    private void processTry(final ThemisStatsBase pOwner,
                            final ThemisAnalysisTry pTry) {
        /* process the container */
        processContainer(pOwner, pTry);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pTry.getNumLines());

        /* Handle catch */
        final ThemisAnalysisCatch myCatch = pTry.getCatch();
        if (myCatch != null) {
            processCatch(pOwner, myCatch);
        }

        /* Handle finally */
        final ThemisAnalysisFinally myFinally = pTry.getFinally();
        if (myFinally != null) {
            processFinally(pOwner, myFinally);
        }
    }

    /**
     * process catch.
     * @param pOwner the owner
     * @param pCatch the catch
     */
    private void processCatch(final ThemisStatsBase pOwner,
                              final ThemisAnalysisCatch pCatch) {
        /* process the container */
        processContainer(pOwner, pCatch);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pCatch.getNumLines());
        adjustNumberOfStatements(pOwner, 1);

        /* Handle additional catch */
        final ThemisAnalysisCatch myCatch = pCatch.getCatch();
        if (myCatch != null) {
            processCatch(pOwner, myCatch);
        }
    }

    /**
     * process finally.
     * @param pOwner the owner
     * @param pFinally the finally
     */
    private void processFinally(final ThemisStatsBase pOwner,
                                final ThemisAnalysisFinally pFinally) {
        /* process the container */
        processContainer(pOwner, pFinally);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pFinally.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * process while.
     * @param pOwner the owner
     * @param pWhile the while
     */
    private void processWhile(final ThemisStatsBase pOwner,
                              final ThemisAnalysisWhile pWhile) {
        /* process the container */
        processContainer(pOwner, pWhile);

        /* Adjust the owner stats */
        adjustLinesOfCode(pOwner, pWhile.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * adjust lines of Code.
     * @param pOwner the owner
     * @param pCount the line count
     */
    private static void adjustLinesOfCode(final ThemisStatsBase pOwner,
                                          final int pCount) {
        /* Adjust the stats */
        pOwner.adjustStat(ThemisStat.LOC, pCount);
        pOwner.adjustStat(ThemisStat.LLOC, pCount);
    }

    /**
     * adjust number of statements.
     * @param pOwner the owner
     * @param pCount the statement count
     */
    private static void adjustNumberOfStatements(final ThemisStatsBase pOwner,
                                                 final int pCount) {
        /* Adjust the stats */
        pOwner.adjustStat(ThemisStat.NOS, pCount);
    }

    /**
     * adjust number of attributes.
     * @param pOwner the owner
     * @param pCount the attribute count
     */
    private static void adjustNumberOfAttributes(final ThemisStatsBase pOwner,
                                                 final int pCount) {
        /* Adjust the stats */
        pOwner.adjustStat(ThemisStat.NA, pCount);
        adjustNumberOfStatements(pOwner, pCount);
    }

    /**
     * adjust cached items.
     * @param pElement the item
     */
    private void adjustCachedItems(final ThemisAnalysisElement pElement) {
        /* Adjust for Annotation */
        if (pElement instanceof ThemisAnalysisAnnotation) {
            theCachedAnnotation = (ThemisAnalysisAnnotation) pElement;

        /* Adjust for documentation comment */
        } else if (pElement instanceof ThemisAnalysisComment
                    && ((ThemisAnalysisComment) pElement).isJavaDoc()) {
            theCachedDocComment = (ThemisAnalysisComment) pElement;
            theCachedAnnotation = null;

            /* else reset cache */
        } else {
            resetCache();
        }
    }

    /**
     * process cached items.
     * @param pParent the parent
     * @param pOwner the stats owner
     */
    private void processCachedItems(final ThemisStatsBase pParent,
                                    final ThemisStatsBase pOwner) {
        /* If we have an annotation */
        if (theCachedAnnotation != null) {
            /* transfer stats from parent */
            processAnnotation(pParent, theCachedAnnotation, false);
            processAnnotation(pOwner, theCachedAnnotation, true);
        }

        /* If we have a documentation comment */
        if (theCachedDocComment != null) {
            /* transfer stats from parent */
            processComment(pParent, theCachedDocComment, false);
            processComment(pOwner, theCachedDocComment, true);
        }

        /* reset cache */
        resetCache();
    }

    /**
     * resetCache.
     */
    private void resetCache() {
        theCachedDocComment = null;
        theCachedAnnotation = null;
    }
}
