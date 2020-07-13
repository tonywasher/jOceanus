/* *****************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.jar.javafx;

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

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;

/**
 * Launcher utilities.
 */
public final class TethysFXLauncher {
    /**
     * NewLine character.
     */
    private static final String NEWLINE = "\n";

    /**
     * Private constructor.
     */
    private TethysFXLauncher() {
    }

    /**
     * Create launchers for jars in directory.
     * @param pDirectory the directory.
     * @throws OceanusException on error
     */
    public static void processJarFiles(final File pDirectory) throws OceanusException {
        /* Loop through the jar files in the directory */
        for (File myJar: Objects.requireNonNull(pDirectory.listFiles(f -> f.getName().endsWith(".jar")))) {
            /* Process jar file */
            final Manifest myManifest = loadManifest(myJar);
            writeLauncher(myJar, myManifest.getMainAttributes());
        }
    }

    /**
     * Write launcher.
     * @param pJar the Jar file.
     * @param pAttrs the attributes
     * @throws OceanusException on error
     */
    private static void writeLauncher(final File pJar,
                                      final Attributes pAttrs) throws OceanusException {
        /* Access details */
        final String myPreLoader = pAttrs.getValue("JavaFX-Preloader-Class");
        final String myMainClass = pAttrs.getValue("Main-Class");
        final String myClassPath = pAttrs.getValue("Class-Path");

        /* If there is no mainClass, then just return */
        if (myMainClass == null) {
            return;
        }

        /* Create the StringBuilder */
        final StringBuilder myBuilder = new StringBuilder();
        final String myName = pJar.getName();

        /* Output the header */
        myBuilder.append("@echo off\nsetlocal\n\n")
                .append("rem make sure that we are in the same directory as the jar file\n")
                .append("cd %0\\..\n\n");

        /* Report details */
        myBuilder.append("rem set up details of the jarFile\n")
                .append("set JARFILE=").append(myName).append(NEWLINE)
                .append("set MODULE=").append(pAttrs.getValue("ModuleName")).append(NEWLINE)
                .append("set MAIN=").append(myMainClass).append(NEWLINE);
        if (myPreLoader != null) {
            myBuilder.append("set PRELOADER=").append(myPreLoader).append(NEWLINE);
        }
        myBuilder.append(NEWLINE);

        /* Obtain and process the classPath */
        if (myClassPath != null) {
            final String[] myClasses = myClassPath.split(" ");
            myBuilder.append("rem build the modulePath from the classPath\n");
            myBuilder.append("set JARS=").append(myClasses[0]).append(NEWLINE);
            for (int i = 1; i < myClasses.length; i++) {
                myBuilder.append("set JARS=%JARS%;").append(myClasses[i]).append(NEWLINE);
            }
            myBuilder.append(NEWLINE);
        }

        /* Output the header */
        myBuilder.append("rem run the jar\n");
        myBuilder.append("java ");
        if (myPreLoader != null) {
            myBuilder.append("-Djavafx.preloader=%PRELOADER% ");
        }
        myBuilder.append("-p %JARFILE%");
        if (myClassPath != null) {
            myBuilder.append(";%JARS%");
        }
        myBuilder.append(" -m %MODULE%/%MAIN% &");
        myBuilder.append("\n\nendlocal\n");

        /* determine the launch file name */
        final String mySuffix = "-" + pAttrs.getValue("Implementation-Version") + ".jar";
        final String myFileName = myName.substring(0, myName.length() - mySuffix.length()) + ".bat";
        final File myOutFile = new File(pJar.getParent(), myFileName);
        writeBatchFile(myOutFile, myBuilder.toString());
    }

    /**
     * Load Manifest.
     * @param pJar the Jar file.
     * @return the manifest
     * @throws OceanusException on error
     */
    private static Manifest loadManifest(final File pJar) throws OceanusException {
        try (FileInputStream myInStream = new FileInputStream(pJar);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInStream);
             ZipInputStream myZipStream = new ZipInputStream(myInBuffer)) {
            /* Loop through the Zip file entries */
            for (;;) {
                /* Read next entry */
                final ZipEntry myEntry = myZipStream.getNextEntry();

                /* If this is EOF we did not find the manifest */
                if (myEntry == null) {
                    throw new TethysDataException("Manifest not found");
                }

                /* Process manifest file if found */
                if ("META-INF/MANIFEST.MF".equals(myEntry.getName())) {
                    return new Manifest(myZipStream);
                }
            }

            /* Handle exceptions */
        } catch (IOException e) {
            throw new TethysDataException("Exception accessing Zip file", e);
        }
    }

    /**
     * Write batchFile.
     * @param pTarget the target batchFile.
     * @param pText the contents of the batch file
     * @throws OceanusException on error
     */
    private static void writeBatchFile(final File pTarget,
                                       final String pText) throws OceanusException {
        try (FileOutputStream myOutput = new FileOutputStream(pTarget);
             BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
             OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, StandardCharsets.ISO_8859_1)) {
            /* Write the text to the file */
            myWriter.write(pText);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new TethysDataException("Exception accessing Zip file", e);
        }
    }
}
