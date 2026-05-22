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

package io.github.tonywasher.joceanus.themis.parser.xmaven;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
public final class ThemisXMavenLocation {
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
    private ThemisXMavenLocation() {
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getCentralJarFileName(final ThemisXMavenId pId) {
        return MAVEN_CENTRAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenJarFileName(pId);
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getCentralPomFileName(final ThemisXMavenId pId) {
        return MAVEN_CENTRAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenPomFileName(pId);
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getLocalJarFileName(final ThemisXMavenId pId) {
        return MAVEN_LOCAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenJarFileName(pId);
    }

    /**
     * Obtain the central jar fileName.
     *
     * @param pId the maven Id
     * @return the fileName
     */
    public static String getLocalPomFileName(final ThemisXMavenId pId) {
        return MAVEN_LOCAL + getMavenVersionedPrefix(pId)
                + ThemisChar.COMMENT + getMavenPomFileName(pId);
    }

    /**
     * Determine latest version.
     *
     * @param pId the maven id
     * @return the latest version
     * @throws OceanusException on error
     */
    public static String determineLatestVersion(final ThemisXMavenId pId) throws OceanusException {
        /* Determine directory to check */
        final String myDir = MAVEN_CENTRAL + getMavenArtifactPrefix(pId) + ThemisChar.COMMENT;

        /* Protect against exceptions */
        try {
            /* Access list of versions */
            final Document myDoc = Jsoup.connect(myDir).get();
            final Element myIds = myDoc.getElementById("contents");

            /* Handle failure to find versions */
            if (myIds == null) {
                throw new ThemisIOException("Unable to find list of versions");
            }

            /* Loop through the versions */
            final ThemisXMavenVersionParser myParser = new ThemisXMavenVersionParser();
            ThemisXMavenVersion myLatest = null;
            for (Element myChild : myIds.children()) {
                /* Only check directories and non parent */
                final String myVersion = myChild.text();
                if (myVersion.endsWith("/") && !myVersion.equals("../")) {
                    final String myVers = myVersion.substring(0, myVersion.length() - 1);

                    /* Check version and record latest */
                    final ThemisXMavenVersion myParsed = myParser.parseVersion(myVers);
                    if (myLatest == null
                            || (myParsed != null && myParsed.compareTo(myLatest) > 0)) {
                        myLatest = myParsed;
                    }
                }
            }

            /* Return the latest */
            return myLatest == null ? null : myLatest.getVersion();

            /* Handle exceptions */
        } catch (OceanusException e) {
            throw e;
        } catch (IOException e) {
            throw new ThemisIOException("Failed to determine latest version", e);
        }
    }

    /**
     * Ensure local pom artifact.
     *
     * @param pId the maven id
     * @throws OceanusException on error
     */
    public static void ensurePomArtifact(final ThemisXMavenId pId) throws OceanusException {
        /* Ensure pom */
        ensureArtifact(getCentralPomFileName(pId), getLocalPomFileName(pId));
    }

    /**
     * Ensure local jar artifact.
     *
     * @param pId the maven id
     * @throws OceanusException on error
     */
    public static void ensureJarArtifact(final ThemisXMavenId pId) throws OceanusException {
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
    private static String getMavenJarFileName(final ThemisXMavenId pId) {
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
    private static String getMavenPomFileName(final ThemisXMavenId pId) {
        return pId.getArtifactId() + ThemisChar.HYPHEN + pId.getVersion() + ".pom";
    }

    /**
     * Obtain the mavenVersionedPrefix.
     *
     * @param pId the maven Id
     * @return the prefix
     */
    private static String getMavenVersionedPrefix(final ThemisXMavenId pId) {
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
    private static String getMavenArtifactPrefix(final ThemisXMavenId pId) {
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
