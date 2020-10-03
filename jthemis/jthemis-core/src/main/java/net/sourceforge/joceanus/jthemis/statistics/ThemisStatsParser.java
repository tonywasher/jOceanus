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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisAnnotation;
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
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMethod;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisModule;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisStatement.ThemisAnalysisStatementHolder;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisSwitch;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisTry;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisWhile;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMFile;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMMethod;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMPackage;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;
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
        /* Locate the matching sourceMeter stats */
        final ThemisSMClass mySMClass = theSourceMeter.findClass(pOwner.getSourceMeter(), pClass);

        /* Create the stats */
        final ThemisStatsClass myStats = new ThemisStatsClass(pClass, mySMClass);

        /* process the container */
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
        /* Locate the matching sourceMeter stats */
        final ThemisSMMethod mySMMethod = theSourceMeter.findMethod(pOwner.getSourceMeter(), pMethod);

        /* Create the stats */
        final ThemisStatsMethod myStats = new ThemisStatsMethod(pMethod, mySMMethod);

        /* process the container */
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
                processAnnotation(pOwner, (ThemisAnalysisAnnotation) myElement);

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
                processComment(pOwner, (ThemisAnalysisComment) myElement);

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

                /* If this is a method */
            } else if (myElement instanceof ThemisAnalysisMethod) {
                processMethod(pOwner, (ThemisAnalysisMethod) myElement);

                /* If this is a package */
            } else if (myElement instanceof ThemisAnalysisPackage) {
                processPackage(pOwner, (ThemisAnalysisPackage) myElement);

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
        }
    }

    /**
     * process annotation.
     * @param pOwner the owner
     * @param pAnnotation the annotation
     */
    private void processAnnotation(final ThemisStatsBase pOwner,
                                   final ThemisAnalysisAnnotation pAnnotation) {
        /* Adjust statistics */
        adjustLinesOfCode(pOwner, pAnnotation.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
    }

    /**
     * process blank.
     * @param pOwner the owner
     * @param pBlank the blank
     */
    private void processBlank(final ThemisStatsBase pOwner,
                              final ThemisAnalysisBlank pBlank) {
        /* Adjust statistics */
        final int myBlanks = pBlank.getNumLines();
        pOwner.adjustStat(ThemisSMStat.LOC, myBlanks);
        pOwner.adjustStat(ThemisSMStat.TLOC, myBlanks);
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
        pOwner.incrementStat(ThemisSMStat.NCL);
        pOwner.incrementStat(ThemisSMStat.TNCL);

        /* Adjust the stats and add to owner */
        adjustLinesOfCode(myClass, pClass.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addClass(myClass);
    }

    /**
     * process comment.
     * @param pOwner the owner
     * @param pComment the comment
     */
    private void processComment(final ThemisStatsBase pOwner,
                                final ThemisAnalysisComment pComment) {
        /* Adjust the stats */
        final int myComments = pComment.getNumLines();
        pOwner.adjustStat(ThemisSMStat.LOC, myComments);
        pOwner.adjustStat(ThemisSMStat.TLOC, myComments);
        pOwner.adjustStat(ThemisSMStat.CLOC, myComments);
        pOwner.adjustStat(ThemisSMStat.TCLOC, myComments);
        if (pComment.isJavaDoc()) {
            pOwner.adjustStat(ThemisSMStat.DLOC, myComments);
            pOwner.adjustStat(ThemisSMStat.TDLOC, myComments);
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
     * process embedded.
     * @param pOwner the owner
     * @param pEmbedded the embedded
     */
    private void processEmbedded(final ThemisStatsBase pOwner,
                                 final ThemisAnalysisEmbedded pEmbedded) {
        /* Adjust statistics */
        adjustNumberOfStatements(pOwner, 1);
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
        pOwner.incrementStat(ThemisSMStat.NEN);
        pOwner.incrementStat(ThemisSMStat.TNEN);

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
    private void processField(final ThemisStatsBase pOwner,
                              final ThemisAnalysisField pField) {
        /* Adjust the stats */
        adjustLinesOfCode(pOwner, pField.getNumLines());
        adjustNumberOfAttributes(pOwner, 1);
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
    private void processImports(final ThemisStatsBase pOwner,
                                final ThemisAnalysisImports pImports) {
        /* Adjust the stats */
        adjustLinesOfCode(pOwner, pImports.getNumLines());
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
        pOwner.incrementStat(ThemisSMStat.NIN);
        pOwner.incrementStat(ThemisSMStat.TNIN);

        /* Adjust the stats and add to owner */
        adjustLinesOfCode(myIFace, pInterface.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addClass(myIFace);
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
        pOwner.incrementStat(ThemisSMStat.NM);
        pOwner.incrementStat(ThemisSMStat.TNM);

        /* Adjust the lines of code and add to owner */
        adjustLinesOfCode(myMethod, pMethod.getNumLines());
        adjustNumberOfStatements(pOwner, 1);
        pOwner.addMethod(myMethod);
    }

    /**
     * process package.
     * @param pOwner the owner
     * @param pPackage the package
     */
    private void processPackage(final ThemisStatsBase pOwner,
                                final ThemisAnalysisPackage pPackage) {
        /* Adjust the stats */
        adjustLinesOfCode(pOwner, 1);
    }

    /**
     * process statement.
     * @param pOwner the owner
     * @param pStatement the statement
     */
    private void processStatement(final ThemisStatsBase pOwner,
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
    private void adjustLinesOfCode(final ThemisStatsBase pOwner,
                                   final int pCount) {
        /* Adjust the stats */
        pOwner.adjustStat(ThemisSMStat.LOC, pCount);
        pOwner.adjustStat(ThemisSMStat.TLOC, pCount);
        pOwner.adjustStat(ThemisSMStat.LLOC, pCount);
        pOwner.adjustStat(ThemisSMStat.TLLOC, pCount);
    }

    /**
     * adjust number of statements.
     * @param pOwner the owner
     * @param pCount the statement count
     */
    private void adjustNumberOfStatements(final ThemisStatsBase pOwner,
                                          final int pCount) {
        /* Adjust the stats */
        pOwner.adjustStat(ThemisSMStat.NOS, pCount);
        pOwner.adjustStat(ThemisSMStat.TNOS, pCount);
    }

    /**
     * adjust number of attributes.
     * @param pOwner the owner
     * @param pCount the attribute count
     */
    private void adjustNumberOfAttributes(final ThemisStatsBase pOwner,
                                          final int pCount) {
        /* Adjust the stats */
        pOwner.adjustStat(ThemisSMStat.NA, pCount);
        pOwner.adjustStat(ThemisSMStat.TNA, pCount);
        adjustNumberOfStatements(pOwner, pCount);
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
