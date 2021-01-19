/* *****************************************************************************
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
package net.sourceforge.joceanus.jthemis.sourcemeter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisKeyWord;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMethod;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass.ThemisSMClassType;

/**
 * SourceMeter statistics.
 */
public class ThemisSMStatistics {
    /**
     * SourceMeter version.
     */
    private static final String VERSIONSM = "9.2.0";

    /**
     * The map of ids to statHolders.
     */
    private final Map<String, ThemisSMStatHolder> theIdMap;

    /**
     * The map of paths to files.
     */
    private final Map<String, ThemisSMFile> theFileMap;

    /**
     * The map of orphans.
     */
    private final Map<String, String> theOrphans;

    /**
     * The package List.
     */
    private final List<ThemisSMPackage> thePackages;

    /**
     * The package List.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter.
     */
    public ThemisSMStatistics(final TethysDataFormatter pFormatter) {
        /* Store parameters */
        theFormatter = pFormatter;

        /* Create Maps/Lists */
        theIdMap = new HashMap<>();
        theFileMap = new HashMap<>();
        theOrphans = new HashMap<>();
        thePackages = new ArrayList<>();
    }

    /**
     * Obtain the formatter.
     * @return the formatter.
     */
    TethysDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain a package iterator.
     * @return the iterator.
     */
    public Iterator<ThemisSMPackage> iterator() {
        return thePackages.iterator();
    }

    /**
     * Register statHolder.
     * @param pHolder the statHolder
     */
    void registerStatHolder(final ThemisSMStatHolder pHolder) {
        theIdMap.put(pHolder.getId(), pHolder);
    }

    /**
     * Register package.
     * @param pPackage the package
     */
    void registerPackage(final ThemisSMPackage pPackage) {
        registerStatHolder(pPackage);
        thePackages.add(pPackage);
    }

    /**
     * Register file.
     * @param pFile the file
     */
    void registerFile(final ThemisSMFile pFile) {
        registerStatHolder(pFile);
        theFileMap.put(pFile.getPath(), pFile);
    }

    /**
     * Register orphan.
     * @param pId the id of the orphan
     * @param pParentId the id of the parent
     */
    void registerOrphan(final String pId,
                        final String pParentId) {
        theOrphans.put(pId, pParentId);
    }

    /**
     * Obtain the holder for the id.
     * @param pId the id
     * @return the holder
     */
    ThemisSMStatHolder getHolder(final String pId) {
        return theIdMap.get(pId);
    }

    /**
     * Obtain the file for the path.
     * @param pPath the path
     * @return the file
     */
    ThemisSMFile getFile(final String pPath) {
        return theFileMap.get(pPath);
    }

    /**
     * Obtain most recent stats directory.
     * @param pProject the project name
     * @return the most recent stats (or null)
     * @throws OceanusException on error
     */
    public static Path getRecentStats(final String pProject) throws OceanusException {
        /* The sourceMeter base. */
        final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        final String pathSM = System.getProperty("user.home")
                + "/Downloads/SourceMeter-"
                + VERSIONSM
                + "-x64-"
                + (isWindows ? "Windows" : "linux");

        /* Build results path */
        final FileSystem mySystem = FileSystems.getDefault();
        final String myDir = pathSM + "/java/Results/" + pProject + "/java";
        final Path myResultsPath = mySystem.getPath(myDir);

        /* Return null if there are no results */
        if (!Files.exists(myResultsPath)) {
            return null;
        }

        /* Loop through statement file in the directory */
        Path myLatest = null;
        try (DirectoryStream<Path> myStream = Files.newDirectoryStream(myResultsPath, "*")) {
            for (final Path myFile : myStream) {
                /* Skip null entries */
                final Path myFileName = myFile.getFileName();
                if (myFileName == null) {
                    continue;
                }
                myLatest = myFile;
            }

            /* Catch exceptions */
        } catch (IOException e1) {
            throw new ThemisIOException("Failed to read directory", e1);
        }

        /* Return the most recent stats directory */
        return myLatest;
    }

    /**
     * Parse statistics.
     * @param pBase the base directory for the stats
     * @param pProject the project name
     * @throws OceanusException on error
     */
    public void parseStatistics(final Path pBase,
                                final String pProject) throws OceanusException {
        /* Handle null path (no stats) */
        if (pBase == null) {
            return;
        }

        /* Parse the packages */
        final ThemisSMPackageParser myPackages = new ThemisSMPackageParser(this);
        myPackages.parseStatistics(pBase, pProject);

        /* Parse the files */
        final ThemisSMFileParser myFiles = new ThemisSMFileParser(this);
        myFiles.parseStatistics(pBase, pProject);

        /* Parse the classes */
        final ThemisSMClassParser myClasses = new ThemisSMClassParser(this, ThemisSMClassType.CLASS);
        myClasses.parseStatistics(pBase, pProject);

        /* Parse the interfaces */
        final ThemisSMClassParser myIFaces = new ThemisSMClassParser(this, ThemisSMClassType.INTERFACE);
        myIFaces.parseStatistics(pBase, pProject);

        /* Parse the enums */
        final ThemisSMClassParser myEnums = new ThemisSMClassParser(this, ThemisSMClassType.ENUM);
        myEnums.parseStatistics(pBase, pProject);

        /* Parse the annotations */
        final ThemisSMClassParser myAnnotations = new ThemisSMClassParser(this, ThemisSMClassType.ANNOTATION);
        myAnnotations.parseStatistics(pBase, pProject);

        /* Parse the methods */
        final ThemisSMMethodParser myMethods = new ThemisSMMethodParser(this);
        myMethods.parseStatistics(pBase, pProject);

        /* PostProcess */
        postProcess();
    }

    /**
     * Process orphans.
     */
    private void postProcess() {
        /* Loop through the orphans */
        for (Entry<String, String> myEntry : theOrphans.entrySet()) {
            /* Link orphans with parents */
            final ThemisSMClass myOrphan = (ThemisSMClass) theIdMap.get(myEntry.getKey());
            final ThemisSMStatHolder myParent = theIdMap.get(myEntry.getValue());
            myOrphan.setParent(myParent);
        }

        /* Clear temporary maps */
        theOrphans.clear();
        theFileMap.clear();
    }

    /**
     * Process statistics.
     * @param pHolder the stats holder
     * @param pHeaders the headers
     * @param pFields the fields
     */
    void processStatistics(final ThemisSMStatHolder pHolder,
                           final String[] pHeaders,
                           final List<String> pFields) {
        /* Loop through the fields */
        final TethysDecimalParser myParser = theFormatter.getDecimalParser();
        final Iterator<String> myIterator = pFields.iterator();
        for (String myHeader : pHeaders) {
            final String myValue = myIterator.next();

            /* If this is a recognised stat */
            final ThemisSMStat myStat = ThemisSMStat.determineStat(myHeader);
            if (myStat != null) {
                /* Parse and store it in the holder */
                final Integer myInteger = myParser.parseIntegerValue(myValue);
                pHolder.setStatistic(myStat, myInteger);
            }
        }
    }

    /**
     * Find package Stats.
     * @param pPackage the package
     * @return the packageStats (or null)
     */
    public ThemisSMPackage findPackage(final ThemisAnalysisPackage pPackage) {
        /* Search the packages */
        final String myName = pPackage.getPackage();
        for (ThemisSMPackage myPackage : thePackages) {
            if (myName.equals(myPackage.getName())) {
                return myPackage;
            }
        }
        return null;
    }

    /**
     * Find file Stats.
     * @param pPackage the package
     * @param pFile the file
     * @return the fileStats (or null)
     */
    public ThemisSMFile findFile(final ThemisSMPackage pPackage,
                                 final ThemisAnalysisFile pFile) {
        /* If package was not found, then file cannot be */
        if (pPackage == null) {
            return null;
        }

        /* Search the files in the package */
        final String myName = pFile.getName() + ThemisAnalysisPackage.SFX_JAVA;
        final Iterator<ThemisSMStatHolder> myIterator = pPackage.childIterator();
        while (myIterator.hasNext()) {
            final ThemisSMFile myFile = (ThemisSMFile) myIterator.next();
            if (myName.equals(myFile.getName())) {
                return myFile;
            }
        }
        return null;
    }

    /**
     * Find class Stats.
     * @param pParent the parent
     * @param pClass the class
     * @return the classStats (or null)
     */
    public ThemisSMClass findClass(final ThemisSMStatHolder pParent,
                                   final ThemisAnalysisObject pClass) {
        /* If holder was not found, then class cannot be */
        if (pParent == null) {
            return null;
        }

        /* Search the children */
        final String myName = pClass.getFullName();
        final Iterator<ThemisSMStatHolder> myIterator = pParent.childIterator();
        while (myIterator.hasNext()) {
            final ThemisSMStatHolder myClass = myIterator.next();
            if (myName.equals(myClass.getName())) {
                return (ThemisSMClass) myClass;
            }
        }
        return null;
    }

    /**
     * Find method Stats.
     * @param pParent the parent
     * @param pMethod the method
     * @return the fileStats (or null)
     */
    public ThemisSMMethod findMethod(final ThemisSMStatHolder pParent,
                                     final ThemisAnalysisMethod pMethod) {
        /* If parent was not found, then method cannot be */
        if (pParent == null) {
            return null;
        }

        /* Extract the name of the method minus any throws clause */
        String myName = pMethod.toString();
        final int myIndex = myName.indexOf(ThemisAnalysisKeyWord.THROWS.toString());
        if (myIndex != -1) {
            myName = myName.substring(0, myIndex - 1);
        }

        /* Search the children */
        final Iterator<ThemisSMStatHolder> myIterator = pParent.childIterator();
        while (myIterator.hasNext()) {
            final ThemisSMStatHolder myMethod = myIterator.next();
            if (myName.equals(myMethod.getName())) {
                return (ThemisSMMethod) myMethod;
            }
        }
        return null;
    }
}