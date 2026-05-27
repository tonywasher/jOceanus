/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.parser.maven;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Maven Location Services.
 */
public final class ThemisMavenLocation {
    /**
     * The Maven Central prefix.
     */
    private static final String MAVEN_CENTRAL = "https://repo.maven.apache.org/maven2/";

    /**
     * The Maven Local prefix.
     */
    private static final String MAVEN_LOCAL = getMavenLocalPrefix();

    /**
     * Private constructor.
     */
    private ThemisMavenLocation() {
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getCentralJarFileName(final ThemisMavenId pId) {
        return MAVEN_CENTRAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenJarFileName(pId);
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getCentralPomFileName(final ThemisMavenId pId) {
        return MAVEN_CENTRAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenPomFileName(pId);
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getLocalJarFileName(final ThemisMavenId pId) {
        return MAVEN_LOCAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenJarFileName(pId);
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getLocalPomFileName(final ThemisMavenId pId) {
        return MAVEN_LOCAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenPomFileName(pId);
    }

    /**
     * Ensure local pom artifact.
     *
     * @param pId the maven id
     * @throws OceanusException on error
     */
    public static void ensurePomArtifact(final ThemisMavenId pId) throws OceanusException {
        /* Ensure pom */
        ensureArtifact(getCentralPomFileName(pId), getLocalPomFileName(pId));
    }

    /**
     * Ensure local jar artifact.
     *
     * @param pId the maven id
     * @throws OceanusException on error
     */
    public static void ensureJarArtifact(final ThemisMavenId pId) throws OceanusException {
        /* Ensure jar */
        ensureArtifact(getCentralJarFileName(pId), getLocalJarFileName(pId));
    }

    /**
     * Ensure local artifact.
     *
     * @param pSource the repository source
     * @param pTarget the target file
     * @throws OceanusException on error
     */
    private static void ensureArtifact(final String pSource,
                                       final String pTarget) throws OceanusException {
        /* If the file does not exist */
        final File myLocalFile = new File(pTarget);
        if (!myLocalFile.exists()) {
            /* Protect against exceptions */
            try {
                /* Ensure that the directory is created */
                final String myParent = myLocalFile.getParent();
                Files.createDirectories(Paths.get(myParent));

                /* download the artifact */
                final URL mySource = new URI(pSource).toURL();
                downloadArtifact(mySource, myLocalFile);

                /* Handle exceptions */
            } catch (Exception e) {
                throw new ThemisIOException("Failed to ensure artifact", e);
            }
        }
    }

    /**
     * Copy File from URL to local.
     *
     * @param pSource the source file
     * @param pTarget the target file
     * @throws OceanusException on error
     */
    private static void downloadArtifact(final URL pSource,
                                         final File pTarget) throws OceanusException {
        /* Protect against exceptions */
        try (InputStream myInput = pSource.openStream();
             FileOutputStream fileOutputStream = new FileOutputStream(pTarget);
             ReadableByteChannel readableByteChannel = Channels.newChannel(myInput)) {
            final FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        } catch (Exception e) {
            throw new ThemisIOException("Failed to download artifact", e);
        }
    }

    /**
     * Obtain the jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    private static String getMavenJarFileName(final ThemisMavenId pId) {
        String myBase = pId.getArtifactId() + ThemisChar.HYPHEN + pId.getVersion();
        final String myClassifier = pId.getClassifier();
        if (myClassifier != null) {
            myBase += ThemisChar.HYPHEN + myClassifier;
        }
        return myBase + ".jar";
    }

    /**
     * Obtain the pom fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    private static String getMavenPomFileName(final ThemisMavenId pId) {
        return pId.getArtifactId() + ThemisChar.HYPHEN + pId.getVersion() + ".pom";
    }

    /**
     * Obtain the mavenVersionedPrefix.
     *
     * @param pId the maven Id
     * @return the prefix
     */
    private static String getMavenVersionedPrefix(final ThemisMavenId pId) {
        /* Determine the repository base */
        final String myBase = getMavenArtifactPrefix(pId);
        return myBase + ThemisChar.COMMENT + pId.getVersion();
    }

    /**
     * Obtain the mavenArtifactPrefix.
     *
     * @param pId the maven Id
     * @return the prefix
     */
    private static String getMavenArtifactPrefix(final ThemisMavenId pId) {
        /* Determine the repository base */
        final String myBase = pId.getGroupId().replace(ThemisChar.PERIOD, ThemisChar.COMMENT);
        return myBase + ThemisChar.COMMENT + pId.getArtifactId();
    }

    /**
     * Obtain the mavenLocal prefix.
     *
     * @return the prefix
     */
    private static String getMavenLocalPrefix() {
        /* Determine the repository base */
        String myBase = System.getProperty("user.home");
        myBase += ThemisChar.COMMENT + ".m2";
        myBase += ThemisChar.COMMENT + "repository";
        myBase += ThemisChar.COMMENT;
        return myBase.replace(ThemisChar.ESCAPE, ThemisChar.COMMENT);
    }
}
