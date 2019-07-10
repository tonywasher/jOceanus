/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.GroupedItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Formatter/Parser class for DataValues.
 * @param <T> the dataSet type
 * @param <E> the data type enum class
 */
public class DataValuesFormatter<T extends DataSet<T, E>, E extends Enum<E>> {
    /**
     * Entry suffix.
     */
    private static final String SUFFIX_ENTRY = ".xml";

    /**
     * The report.
     */
    private final MetisThreadStatusReport theReport;

    /**
     * The security manager.
     */
    private final GordianSecurityManager theSecurityMgr;

    /**
     * The document builder.
     */
    private final DocumentBuilder theBuilder;

    /**
     * The transformer.
     */
    private final Transformer theXformer;

    /**
     * The Data version.
     */
    private Integer theVersion;

    /**
     * Constructor.
     * @param pReport the report
     * @param pSecureMgr the security manager
     * @throws PrometheusIOException on error
     */
    public DataValuesFormatter(final MetisThreadStatusReport pReport,
                               final GordianSecurityManager pSecureMgr) throws PrometheusIOException {
        /* Store values */
        theReport = pReport;
        theSecurityMgr = pSecureMgr;

        /* protect against exceptions */
        try {
            /* Create a Document builder */
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            theBuilder = myFactory.newDocumentBuilder();

            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            theXformer = myXformFactory.newTransformer();

        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new PrometheusIOException("Failed to initialise parser", e);
        }
    }

    /**
     * Create a Backup ZipFile.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws OceanusException on error
     */
    public void createBackup(final T pData,
                             final File pFile) throws OceanusException {
        /* Obtain the active profile */
        final MetisProfile myTask = theReport.getActiveTask();
        final MetisProfile myStage = myTask.startTask("Writing");

        /* Create a similar security control */
        final GordianSecurityManager mySecure = pData.getSecurity();
        final GordianKeySetHash myBase = pData.getKeySetHash();
        final GordianKeySetHash myHash = mySecure.similarKeySetHash(myBase);
        final GordianZipFactory myZips = mySecure.getSecurityFactory().getZipFactory();

        /* Access the data version */
        theVersion = pData.getControl().getDataVersion();

        /* Declare the number of stages */
        theReport.setNumStages(pData.getListMap().size());

        /* Protect the workbook access */
        boolean writeFailed = false;
        try (GordianZipWriteFile myZipFile = myZips.createZipFile(myHash, pFile)) {
            /* Loop through the data lists */
            final Iterator<DataList<?, E>> myIterator = pData.iterator();
            while (myIterator.hasNext()) {
                final DataList<?, E> myList = myIterator.next();

                /* Declare the new stage */
                theReport.setNewStage(myList.listName());

                /* If this list should be written */
                if (myList.includeDataXML()) {
                    /* Write the list details */
                    myStage.startTask(myList.listName());
                    writeXMLListToFile(myList, myZipFile, true);
                }
            }

            /* Complete the task */
            myStage.end();

        } catch (IOException
                | OceanusException e) {
            writeFailed = true;
            throw new PrometheusIOException("Failed to create backup XML", e);
        } finally {
            /* Try to delete the file if required */
            if (writeFailed) {
                MetisToolkit.cleanUpFile(pFile);
            }
        }
    }

    /**
     * Create an Extract ZipFile.
     * @param pData Data to write out
     * @param pFile the extract file to write to
     * @throws OceanusException on error
     */
    public void createExtract(final T pData,
                              final File pFile) throws OceanusException {
        /* Obtain the active profile */
        final MetisProfile myTask = theReport.getActiveTask();
        final MetisProfile myStage = myTask.startTask("Writing");

        /* Access the data version */
        theVersion = pData.getControl().getDataVersion();

        /* Declare the number of stages */
        theReport.setNumStages(pData.getListMap().size());
        final GordianZipFactory myZips = theSecurityMgr.getSecurityFactory().getZipFactory();

        /* Protect the workbook access */
        boolean writeFailed = false;
        try (GordianZipWriteFile myZipFile = myZips.createZipFile(pFile)) {
            /* Loop through the data lists */
            final Iterator<DataList<?, E>> myIterator = pData.iterator();
            while (myIterator.hasNext()) {
                final DataList<?, E> myList = myIterator.next();

                /* Declare the new stage */
                theReport.setNewStage(myList.listName());

                /* If this list should be written */
                if (myList.includeDataXML()) {
                    /* Write the list details */
                    myStage.startTask(myList.listName());
                    writeXMLListToFile(myList, myZipFile, false);
                }
            }

            /* Complete the task */
            myStage.end();

        } catch (IOException
                | OceanusException e) {
            writeFailed = true;
            throw new PrometheusIOException("Failed to create extract XML", e);
        } finally {
            /* Try to delete the file if required */
            if (writeFailed) {
                MetisToolkit.cleanUpFile(pFile);
            }
        }
    }

    /**
     * Write XML list to file.
     * @param pList the data list
     * @param pZipFile the output zipFile
     * @param pStoreIds do we include IDs in XML
     * @throws OceanusException on error
     */
    private void writeXMLListToFile(final DataList<?, E> pList,
                                    final GordianZipWriteFile pZipFile,
                                    final boolean pStoreIds) throws OceanusException {
        /* Access the list name */
        final String myName = pList.listName() + SUFFIX_ENTRY;

        /* Protect the workbook access */
        try (OutputStream myStream = pZipFile.createOutputStream(new File(myName), true)) {
            /* Create a new document */
            final Document myDocument = theBuilder.newDocument();

            /* Populate the document from the list */
            populateXML(myDocument, pList, pStoreIds);

            /* Format the XML and write to stream */
            theXformer.transform(new DOMSource(myDocument), new StreamResult(myStream));

        } catch (TransformerException | IOException e) {
            throw new PrometheusIOException("Failed to transform XML", e);
        }
    }

    /**
     * Create XML for a list.
     * @param pDocument the document to hold the list.
     * @param pList the data list
     * @param pStoreIds do we include IDs in XML
     * @throws OceanusException on error
     */
    private void populateXML(final Document pDocument,
                             final DataList<?, E> pList,
                             final boolean pStoreIds) throws OceanusException {
        /* Create an element for the item */
        final Element myElement = pDocument.createElement(pList.listName());
        pDocument.appendChild(myElement);

        /* Access the Data formatter */
        final MetisDataFormatter myFormatter = pList.getDataSet().getDataFormatter();

        /* Declare the number of steps */
        final int myTotal = pList.size();
        theReport.setNumSteps(myTotal);

        /* Set the list type and size */
        myElement.setAttribute(DataValues.ATTR_TYPE, pList.getItemType().name());
        myElement.setAttribute(DataValues.ATTR_SIZE, Integer.toString(myTotal));
        myElement.setAttribute(DataValues.ATTR_VERS, Integer.toString(theVersion));

        /* Iterate through the list */
        final Iterator<?> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final Object myObject = myIterator.next();

            /* Ignore if not a DataItem */
            if (!(myObject instanceof DataItem)) {
                continue;
            }

            /* Access as DataItem */
            @SuppressWarnings("unchecked")
            final DataItem<E> myItem = (DataItem<E>) myObject;

            /* Skip over child items */
            if ((myItem instanceof GroupedItem)
                && (((GroupedItem<?>) myItem).isChild())) {
                continue;
            }

            /* Create DataValues for item */
            final DataValues<E> myValues = new DataValues<>(myItem);

            /* Add the child to the list */
            final Element myChild = myValues.createXML(pDocument, myFormatter, pStoreIds);
            myElement.appendChild(myChild);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Load a ZipFile.
     * @param pData DataSet to load into
     * @param pFile the file to load
     * @throws OceanusException on error
     */
    public void loadZipFile(final T pData,
                            final File pFile) throws OceanusException {
        /* Obtain the active profile */
        final MetisProfile myTask = theReport.getActiveTask();
        final MetisProfile myStage = myTask.startTask("Loading");
        myStage.startTask("Parsing");

        /* Access the zip file */
        final GordianZipFactory myZips = theSecurityMgr.getSecurityFactory().getZipFactory();
        final GordianZipReadFile myZipFile = myZips.openZipFile(pFile);

        /* Obtain the hash bytes from the file */
        final byte[] myHashBytes = myZipFile.getHashBytes();

        /* If this is a secure ZipFile */
        if (myHashBytes != null) {
            /* Obtain the initialised password hash */
            final GordianKeySetHash myHash = theSecurityMgr.resolveKeySetHash(myHashBytes, pFile.getName());

            /* Associate this keySetHash with the ZipFile */
            myZipFile.setKeySetHash(myHash);
        }

        /* Parse the Zip File */
        parseZipFile(myStage, pData, myZipFile);

        /* Complete the task */
        myStage.end();
    }

    /**
     * Parse a ZipFile.
     * @param pProfile the active profile
     * @param pData DataSet to load into
     * @param pZipFile the file to parse
     * @throws OceanusException on error
     */
    private void parseZipFile(final MetisProfile pProfile,
                              final T pData,
                              final GordianZipReadFile pZipFile) throws OceanusException {
        /* Start new stage */
        final MetisProfile myStage = pProfile.startTask("Loading");

        /* Declare the number of stages */
        theReport.setNumStages(pData.getListMap().size());

        /* Loop through the data lists */
        final Iterator<DataList<?, E>> myIterator = pData.iterator();
        while (myIterator.hasNext()) {
            final DataList<?, E> myList = myIterator.next();

            /* Declare the new stage */
            theReport.setNewStage(myList.listName());

            /* If this list should be read */
            if (myList.includeDataXML()) {
                /* Write the list details */
                myStage.startTask(myList.listName());
                readXMLListFromFile(myList, pZipFile);
            }

            /* postProcessList after load */
            myList.postProcessOnLoad();
        }

        /* Create the control data */
        pData.getControlData().addNewControl(theVersion);

        /* Complete the task */
        myStage.end();
    }

    /**
     * Read XML list from file.
     * @param pList the data list
     * @param pZipFile the input zipFile
     * @throws OceanusException on error
     */
    private void readXMLListFromFile(final DataList<?, E> pList,
                                     final GordianZipReadFile pZipFile) throws OceanusException {
        /* Access the list name */
        final String myName = pList.listName() + SUFFIX_ENTRY;

        /* Locate the correct entry */
        final GordianZipFileContents myContents = pZipFile.getContents();
        final GordianZipFileEntry myEntry = myContents.findFileEntry(myName);
        if (myEntry == null) {
            throw new PrometheusDataException("List not found " + myName);
        }

        /* Protect the workbook access */
        try (InputStream myStream = pZipFile.createInputStream(myEntry)) {
            /* Read the document from the stream and parse it */
            final Document myDocument = theBuilder.parse(myStream);

            /* Populate the list from the document */
            parseXMLDocument(myDocument, pList);

        } catch (IOException
                | SAXException e) {
            throw new PrometheusIOException("Failed to parse XML", e);
        }
    }

    /**
     * parse an XML document into DataValues.
     * @param pDocument the document that holds the list.
     * @param pList the data list
     * @throws OceanusException on error
     */
    private void parseXMLDocument(final Document pDocument,
                                  final DataList<?, E> pList) throws OceanusException {
        /* Access the parent element */
        final Element myElement = pDocument.getDocumentElement();
        final E myItemType = pList.getItemType();

        /* Check that the document name and dataType are correct */
        if (!MetisDataDifference.isEqual(myElement.getNodeName(), pList.listName())
            || !MetisDataDifference.isEqual(myElement.getAttribute(DataValues.ATTR_TYPE), myItemType.name())) {
            throw new PrometheusDataException("Invalid list type");
        }

        /* If this is the first Data version */
        final Integer myVersion = Integer.valueOf(myElement.getAttribute(DataValues.ATTR_VERS));
        if (theVersion == null) {
            theVersion = myVersion;
        } else if (!theVersion.equals(myVersion)) {
            throw new PrometheusDataException("Inconsistent data version");
        }

        /* Access field types for list */
        final MetisFields myFields = pList.getItemFields();

        /* Access the Data formatter */
        final MetisDataFormatter myFormatter = pList.getDataSet().getDataFormatter();

        /* Declare the number of steps */
        final int myTotal = getListCount(myFormatter, myElement);
        theReport.setNumSteps(myTotal);

        /* Loop through the children */
        for (Node myChild = myElement.getFirstChild(); myChild != null; myChild = myChild.getNextSibling()) {
            /* Ignore non-elements */
            if (!(myChild instanceof Element)) {
                continue;
            }

            /* Access as Element */
            final Element myItem = (Element) myChild;

            /* Create DataArguments for item */
            final DataValues<E> myValues = new DataValues<>(myItem, myFields);

            /* Add the child to the list */
            pList.addValuesItem(myValues);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Obtain count attribute.
     * @param pFormatter the formatter.
     * @param pElement the element that holds the count.
     * @return the list count
     * @throws OceanusException on error
     */
    private static Integer getListCount(final MetisDataFormatter pFormatter,
                                        final Element pElement) throws OceanusException {
        try {
            /* Access the list count */
            final String mySize = pElement.getAttribute(DataValues.ATTR_SIZE);
            return pFormatter.parseValue(mySize, Integer.class);
        } catch (NumberFormatException e) {
            throw new PrometheusDataException("Invalid list count", e);
        }
    }
}
