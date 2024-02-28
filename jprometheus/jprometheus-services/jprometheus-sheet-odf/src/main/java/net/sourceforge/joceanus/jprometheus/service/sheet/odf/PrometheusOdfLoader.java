/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Loader and Archiver for Odf files.
 */
public final class PrometheusOdfLoader {
    /**
     * Read error.
     */
    private static final String ERROR_READ = "Failed to load SpreadSheet";

    /**
     * Write error.
     */
    private static final String ERROR_WRITE = "Failed to create SpreadSheet";

    /**
     * Empty ODS file.
     */
    private static final String FILE_EMPTY = "Empty.ods";

    /**
     * Content element name.
     */
    private static final String FILE_CONTENT = "content.xml";

    /**
     * Mainifest element name.
     */
    private static final String FILE_MANIFEST = "manifest.xml";

    /**
     * Mainifest2 element name.
     */
    private static final String FILE_MANIFEST2 = "manifest.rdf";

    /**
     * Closed stream failure.
     */
    private static final String ERROR_CLOSED = "Stream is closed";

    /**
     * Private constructor.
     */
    private PrometheusOdfLoader() {
    }

    /**
     * load spreadSheet from stream.
     * @param pInput the input stream
     * @return the contents
     * @throws OceanusException on error
     */
    static Document loadNewSpreadSheet(final InputStream pInput) throws OceanusException {
        /* Create a spreadSheet from scratch */
        try (BufferedInputStream myBufferedIn = new BufferedInputStream(pInput)) {
            /* Load document */
            return loadSpreadSheet(myBufferedIn);

        } catch (IOException e) {
            throw new PrometheusSheetException(ERROR_READ, e);
        }
    }

    /**
     * load initial spreadSheet from resource.
     * @return the contents
     * @throws OceanusException on error
     */
    static Document loadInitialSpreadSheet() throws OceanusException {
        /* Load the initial spreadSheet */
        try (InputStream myInput = PrometheusOdfLoader.class.getResourceAsStream(FILE_EMPTY);
             BufferedInputStream myBufferedIn = new BufferedInputStream(myInput)) {
            /* Load document */
            return loadSpreadSheet(myBufferedIn);

        } catch (IOException e) {
            throw new PrometheusSheetException(ERROR_READ, e);
        }
    }

    /**
     * Write new spreadSheet.
     * @param pContents the contents
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    static void writeNewSpreadSheet(final Document pContents,
                                    final OutputStream pOutput) throws OceanusException {
        /* Create a spreadSheet from scratch */
        try (BufferedOutputStream myBufferedOut = new BufferedOutputStream(pOutput)) {
            /* Load document */
            createSpreadSheet(pContents, myBufferedOut);

        } catch (IOException e) {
            throw new PrometheusSheetException(ERROR_WRITE, e);
        }
    }

    /**
     * Write updated spreadSheet.
     * @param pContents the contents
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    static void writeUpdatedSpreadSheet(final Document pContents,
                                        final OutputStream pOutput) throws OceanusException {
        /* Write the updated spreadsheet */
        try (InputStream myInput = PrometheusOdfLoader.class.getResourceAsStream(FILE_EMPTY);
             BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
             BufferedOutputStream myBufferedOut = new BufferedOutputStream(pOutput)) {
            /* Create updated document */
            updateSpreadSheet(myBufferedIn, pContents, myBufferedOut);

        } catch (IOException e) {
            throw new PrometheusSheetException(ERROR_WRITE, e);
        }
    }

    /**
     * Load an ODF spreadSheet.
     * @param pInput the input stream
     * @return the parsed document
     * @throws OceanusException on error
     */
    private static Document loadSpreadSheet(final InputStream pInput) throws OceanusException {
        /* Protect against exceptions */
        try (ZipInputStream myZipStream = new ZipInputStream(pInput)) {
            /* Create the Document builder */
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            myFactory.setNamespaceAware(true);
            final DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Loop through the Zip file entries */
            for (;;) {
                /* Read next entry */
                final ZipEntry myEntry = myZipStream.getNextEntry();

                /* If this is EOF break the loop */
                if (myEntry == null) {
                    break;
                }

                /* If we have found the contents */
                if (FILE_CONTENT.equals(myEntry.getName())) {
                    /* Parse the contents and return the document */
                    return myBuilder.parse(new WrapInputStream(myZipStream));
                }
            }

        } catch (ParserConfigurationException
                | SAXException
                | IOException e) {
            throw new PrometheusSheetException(ERROR_READ, e);
        }

        return null;
    }

    /**
     * create an ODF spreadSheet.
     * @param pDoc the (updated) document
     * @param pOutput the input stream
     * @throws OceanusException on error
     */
    private static void createSpreadSheet(final Document pDoc,
                                          final OutputStream pOutput) throws OceanusException {
        /* Protect against exceptions */
        try (ZipOutputStream myOutStream = new ZipOutputStream(pOutput)) {
            /* Create the manifest.xml file */
            resourceToZipEntry("META-INF/" + FILE_MANIFEST, FILE_MANIFEST, myOutStream);
            stringToZipEntry("mimetype", "application/vnd.oasis.opendocument.spreadsheet", myOutStream);
            resourceToZipEntry(FILE_MANIFEST2, FILE_MANIFEST2, myOutStream);
            documentToZipEntry(FILE_CONTENT, pDoc, myOutStream);

        } catch (IOException e) {
            throw new PrometheusSheetException(ERROR_WRITE, e);
        }
    }

    /**
     * Update an ODF spreadSheet, based on an input spreadSheet.
     * @param pInput the input stream
     * @param pContents the (updated) contents document
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
     private static void updateSpreadSheet(final InputStream pInput,
                                           final Document pContents,
                                           final OutputStream pOutput) throws OceanusException {
        /* Protect against exceptions */
        try (ZipInputStream myInStream = new ZipInputStream(pInput);
             ZipOutputStream myOutStream = new ZipOutputStream(pOutput)) {
            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            final Transformer myXformer = myXformFactory.newTransformer();

            /* Loop through the Zip file entries */
            for (;;) {
                /* Read next entry */
                final ZipEntry myEntry = myInStream.getNextEntry();

                /* If this is EOF break the loop */
                if (myEntry == null) {
                    break;
                }

                /* Create the new output entry */
                final String myName = myEntry.getName();
                final ZipEntry myOutEntry = new ZipEntry(myName);
                myOutStream.putNextEntry(myOutEntry);

                /* If we have found the contents */
                if (FILE_CONTENT.equals(myName)) {
                    myXformer.transform(new DOMSource(pContents),
                            new StreamResult(new WrapOutputStream(myOutStream)));

                    /* Else it is a standard entry, so just copy it */
                } else  {
                    myInStream.transferTo(myOutStream);
                }
            }

        } catch (TransformerException
                | IOException e) {
            throw new PrometheusSheetException(ERROR_WRITE, e);
        }
    }

    /**
     * build a Zip entry from document.
     * @param pEntryName the entry name
     * @param pDocument the document
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    private static void documentToZipEntry(final String pEntryName,
                                           final Document pDocument,
                                           final ZipOutputStream pOutput) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            final Transformer myXformer = myXformFactory.newTransformer();

            /* Create the new output entry */
            final ZipEntry myOutEntry = new ZipEntry(pEntryName);
            pOutput.putNextEntry(myOutEntry);

            /* Write the document out */
            myXformer.transform(new DOMSource(pDocument),
                    new StreamResult(new WrapOutputStream(pOutput)));

        } catch (IOException
                | TransformerException e) {
            throw new PrometheusSheetException("Failed to write document to ZipFile", e);
        }
    }

    /**
     * build a Zip entry from resource.
     * @param pEntryName the entry name
     * @param pResourceName the resource name
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    private static void resourceToZipEntry(final String pEntryName,
                                           final String pResourceName,
                                           final ZipOutputStream pOutput) throws OceanusException {
        /* Protect against exceptions */
        try (InputStream myInput = PrometheusOdfLoader.class.getResourceAsStream(pResourceName)) {
            /* Create the new output entry */
            final ZipEntry myOutEntry = new ZipEntry(pEntryName);
            pOutput.putNextEntry(myOutEntry);

            /* Copy the resource to the zip file */
            myInput.transferTo(pOutput);
        } catch (IOException e) {
            throw new PrometheusSheetException("Failed to copy resource to ZipFile", e);
        }
    }

    /**
     * write a Zip entry from string.
     * @param pEntryName the entry name
     * @param pContents the contents
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    private static void stringToZipEntry(final String pEntryName,
                                         final String pContents,
                                         final ZipOutputStream pOutput) throws OceanusException {
        /* Protect against exceptions */
        try {
           /* Create the new output entry */
            final ZipEntry myOutEntry = new ZipEntry(pEntryName);
            pOutput.putNextEntry(myOutEntry);

            /* Protect against exceptions */
            final byte[] myData = pContents.getBytes();
            pOutput.write(myData);
        } catch (IOException e) {
            throw new PrometheusSheetException("Failed to write string to ZipFile", e);
        }
    }

    /**
     * Wrapper class to catch close of input stream and prevent it from closing the ZipFile.
     */
    private static final class WrapInputStream
            extends InputStream {
        /**
         * The underlying Zip output stream.
         */
        private final ZipInputStream theStream;

        /**
         * Are we closed?
         */
        private boolean isClosed;

        /**
         * Constructor.
         * @param pStream the ZipStream
         */
        WrapInputStream(final ZipInputStream pStream) {
            theStream = pStream;
        }

        @Override
        public int available() throws IOException {
            return theStream.available();
        }

        @Override
        public long skip(final long pNumToSkip) throws IOException {
            /* If we are already closed then throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call on */
            return theStream.skip(pNumToSkip);
        }

        @Override
        public boolean markSupported() {
            return theStream.markSupported();
        }

        @Override
        public synchronized void mark(final int pReadLimit) {
            if (!isClosed) {
                theStream.mark(pReadLimit);
            }
        }

        @Override
        public synchronized void reset() throws IOException {
            /* If we are already closed then throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call through */
            theStream.reset();
        }

        @Override
        public int read(final byte[] pOutBytes) throws IOException {
            /* If we are already closed then throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Read the next bytes from the stream */
            return theStream.read(pOutBytes);
        }

        @Override
        public int read() throws IOException {
            /* If we are already closed then throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call through */
            return theStream.read();
        }

        @Override
        public int read(final byte[] pBuffer,
                        final int pOffset,
                        final int pLength) throws IOException {
            /* If we are already closed throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call through */
            return theStream.read(pBuffer, pOffset, pLength);
        }

        @Override
        public void close() {
            isClosed = true;
        }
    }

    /**
     * Wrapper class to catch close of output stream and prevent it from closing the ZipFile.
     */
    private static final class WrapOutputStream
            extends OutputStream {
        /**
         * The underlying Zip output stream.
         */
        private final ZipOutputStream theStream;

        /**
         * Are we closed?
         */
        private boolean isClosed;

        /**
         * Constructor.
         * @param pStream the ZipStream
         */
        WrapOutputStream(final ZipOutputStream pStream) {
            theStream = pStream;
        }

        @Override
        public void flush() throws IOException {
            if (!isClosed) {
                theStream.flush();
            }
        }

        @Override
        public void write(final int b) throws IOException {
            /* If we are already closed throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call through */
            theStream.write(b);
        }

        @Override
        public void write(final byte[] b) throws IOException {
            /* If we are already closed throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call through */
            theStream.write(b);
        }

        @Override
        public void write(final byte[] b,
                          final int offset,
                          final int length) throws IOException {
            /* If we are already closed throw IO Exception */
            if (isClosed) {
                throw new IOException(ERROR_CLOSED);
            }

            /* Pass call through */
            theStream.write(b, offset, length);
        }

        @Override
        public void close() {
            isClosed = true;
        }
    }
}
