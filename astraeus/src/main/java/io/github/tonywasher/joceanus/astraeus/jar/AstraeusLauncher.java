/*
 * Astraeus: Post-Processing
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
package io.github.tonywasher.joceanus.astraeus.jar;

import io.github.tonywasher.joceanus.astraeus.exc.AstraeusException;
import net.sourceforge.joceanus.oceanus.base.OceanusSystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Launcher utilities.
 */
public final class AstraeusLauncher {
    /**
     * Are we windows?
     */
    private static final boolean OS_WINDOWS = OceanusSystem.WINDOWS.equals(OceanusSystem.determineSystem());

    /**
     * NewLine character.
     */
    private static final String NEWLINE = "\n";

    /**
     * Resources directory.
     */
    private static final String RESOURCES = "../resources";

    /**
     * Private constructor.
     */
    private AstraeusLauncher() {
    }

    /**
     * Create launchers for jars in directory.
     *
     * @param pDirectory the directory.
     * @throws AstraeusException on error
     */
    public static void processJarFiles(final File pDirectory) throws AstraeusException {
        /* Loop through the jar files in the directory */
        for (File myJar : Objects.requireNonNull(pDirectory.listFiles(f -> f.getName().endsWith(".jar")))) {
            /* Process jar file */
            final Manifest myManifest = loadManifest(myJar);
            writeLauncher(myJar, myManifest.getMainAttributes());
        }
    }

    /**
     * Write launcher.
     *
     * @param pJar   the Jar file.
     * @param pAttrs the attributes
     * @throws AstraeusException on error
     */
    private static void writeLauncher(final File pJar,
                                      final Attributes pAttrs) throws AstraeusException {
        /* Access details */
        final String myPreLoader = pAttrs.getValue("JavaFX-Preloader-Class");
        final String myMainClass = pAttrs.getValue("Main-Class");
        final String myClassPath = pAttrs.getValue("Class-Path");
        final String mySplash = pAttrs.getValue("SplashScreen-Image");
        final String myModule = pAttrs.getValue("Automatic-Module-Name");

        /* If there is no mainClass, then just return */
        if (myMainClass == null) {
            return;
        }

        /* Create the StringBuilder */
        final StringBuilder myBuilder = new StringBuilder();
        final String myName = pJar.getName();

        /* Output the header */
        myBuilder.append(getBatchHeader())
                .append(getComment("make sure that we are in the same directory as the jar file"))
                .append(setDirectory());

        /* Report details */
        myBuilder.append(getComment("set up details of the jarFile"))
                .append(setVariable("JARFILE")).append(myName).append(NEWLINE)
                .append(setVariable("MODULE")).append(myModule).append(NEWLINE)
                .append(setVariable("MAIN")).append(myMainClass).append(NEWLINE);
        if (myPreLoader != null) {
            myBuilder.append(setVariable("PRELOADER")).append(myPreLoader).append(NEWLINE);
        }
        if (mySplash != null) {
            myBuilder.append(setVariable("SPLASH")).append(RESOURCES).append("/").append(mySplash).append(NEWLINE);
            extractSplash(pJar, mySplash);
        }
        myBuilder.append(NEWLINE);

        /* Obtain and process the classPath */
        if (myClassPath != null) {
            final String[] myClasses = myClassPath.split(" ");
            myBuilder.append(getComment("build the modulePath from the classPath"));
            myBuilder.append(setVariable("JARS")).append(myClasses[0]).append(NEWLINE);
            for (int i = 1; i < myClasses.length; i++) {
                myBuilder.append(setVariable("JARS")).append(getValue("JARS")).append(File.pathSeparatorChar).append(myClasses[i]).append(NEWLINE);
            }
            myBuilder.append(NEWLINE);
        }

        /* Output the commandLine */
        myBuilder.append(getComment("run the jar"));
        if (OS_WINDOWS) {
            myBuilder.append("start /B ");
        }
        myBuilder.append("..").append(File.separatorChar).append("java").append(File.separatorChar)
                .append("bin").append(File.separatorChar).append("java ");
        if (myPreLoader != null) {
            myBuilder.append("-Djavafx.preloader=").append(getValue("PRELOADER")).append(" ");
        }
        if (mySplash != null) {
            myBuilder.append("-splash:").append(getValue("SPLASH")).append(" ");
        }
        myBuilder.append("-p ").append(getValue("JARFILE"));
        if (myClassPath != null) {
            myBuilder.append(File.pathSeparatorChar).append(getValue("JARS"));
        }
        myBuilder.append(" -m ").append(getValue("MODULE")).append("/").append(getValue("MAIN"));
        if (!OS_WINDOWS) {
            myBuilder.append(" &");
        }
        myBuilder.append(getBatchTrailer());

        /* determine the launch file name */
        final String mySuffix = "-" + pAttrs.getValue("Implementation-Version") + ".jar";
        final String myFileName = myName.substring(0, myName.length() - mySuffix.length()) + getBatchSuffix();
        final File myOutFile = new File(pJar.getParent(), myFileName);
        writeBatchFile(myOutFile, myBuilder.toString());
    }

    /**
     * Load Manifest.
     *
     * @param pJar the Jar file.
     * @return the manifest
     * @throws AstraeusException on error
     */
    private static Manifest loadManifest(final File pJar) throws AstraeusException {
        try (FileInputStream myInStream = new FileInputStream(pJar);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInStream);
             ZipInputStream myZipStream = new ZipInputStream(myInBuffer)) {
            /* Loop through the Zip file entries */
            for (; ; ) {
                /* Read next entry */
                final ZipEntry myEntry = myZipStream.getNextEntry();

                /* If this is EOF we did not find the manifest */
                if (myEntry == null) {
                    throw new AstraeusException("Manifest not found");
                }

                /* Process manifest file if found */
                if ("META-INF/MANIFEST.MF".equals(myEntry.getName())) {
                    return new Manifest(myZipStream);
                }
            }

            /* Handle exceptions */
        } catch (IOException e) {
            throw new AstraeusException("Exception accessing Zip file", e);
        }
    }

    /**
     * Extract splash file.
     *
     * @param pJar    the Jar file.
     * @param pSplash the path to the splash file
     * @throws AstraeusException on error
     */
    private static void extractSplash(final File pJar,
                                      final String pSplash) throws AstraeusException {
        try (FileInputStream myInStream = new FileInputStream(pJar);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInStream);
             ZipInputStream myZipStream = new ZipInputStream(myInBuffer)) {
            /* Loop through the Zip file entries */
            for (; ; ) {
                /* Read next entry */
                final ZipEntry myEntry = myZipStream.getNextEntry();

                /* If this is EOF we did not find the manifest */
                if (myEntry == null) {
                    throw new AstraeusException("Splash not found");
                }

                /* Process manifest file if found */
                if (pSplash.equals(myEntry.getName())) {
                    /* Determine location for splashFile */
                    final File myBase = new File(pJar.getParent(), RESOURCES);
                    final File myTarget = new File(myBase, pSplash);
                    final File myDir = new File(myTarget.getParent());

                    /* If we created the directory OK */
                    if (myDir.mkdirs()) {
                        /* Copy the splashScreen */
                        try (FileOutputStream myStream = new FileOutputStream(myTarget)) {
                            myZipStream.transferTo(myStream);
                        }
                    }

                    /* Return */
                    return;
                }
            }

            /* Handle exceptions */
        } catch (IOException e) {
            throw new AstraeusException("Exception copying Splash file", e);
        }
    }

    /**
     * Write batchFile.
     *
     * @param pTarget the target batchFile.
     * @param pText   the contents of the batch file
     * @throws AstraeusException on error
     */
    private static void writeBatchFile(final File pTarget,
                                       final String pText) throws AstraeusException {
        try (FileOutputStream myOutput = new FileOutputStream(pTarget);
             BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
             OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, StandardCharsets.UTF_8)) {
            /* Write the text to the file */
            myWriter.write(pText);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new AstraeusException("Exception writing batch file", e);
        }

        /* Try to make file executable */
        if (!OS_WINDOWS && !pTarget.setExecutable(true)) {
            throw new AstraeusException("Failed to set executable indication");
        }
    }

    /**
     * Obtain the batch file header.
     *
     * @return the header.
     */
    private static String getBatchHeader() {
        return OS_WINDOWS ? "@echo off\nsetlocal\n\n" : "#!/usr/bin/ksh\n\n";
    }

    /**
     * Obtain the batch setDirectory command.
     *
     * @return the command.
     */
    private static String setDirectory() {
        return OS_WINDOWS ? "cd %0\\..\n\n" : "cd $(dirname %0)\n\n";
    }

    /**
     * Obtain the batch file trailer.
     *
     * @return the trailer.
     */
    private static String getBatchTrailer() {
        return OS_WINDOWS ? "\n\nendlocal\n" : "\n\n";
    }

    /**
     * Obtain the batch file comment.
     *
     * @param pComment the comment
     * @return the comment.
     */
    private static String getComment(final String pComment) {
        return (OS_WINDOWS ? "rem " : "# ") + pComment + NEWLINE;
    }

    /**
     * Set a variable's value.
     *
     * @param pVar the variable
     * @return the set clause.
     */
    private static String setVariable(final String pVar) {
        return (OS_WINDOWS ? "set " : "") + pVar + '=';
    }

    /**
     * Obtain a variable's value.
     *
     * @param pVar the variable
     * @return the value.
     */
    private static String getValue(final String pVar) {
        return OS_WINDOWS ? "%" + pVar + "%" : "$" + pVar;
    }

    /**
     * Obtain the batch suffix.
     *
     * @return the suffix.
     */
    private static String getBatchSuffix() {
        return OS_WINDOWS ? ".bat" : ".ksh";
    }
}
