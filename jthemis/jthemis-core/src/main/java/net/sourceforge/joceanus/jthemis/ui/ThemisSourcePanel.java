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
package net.sourceforge.joceanus.jthemis.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager.TethysTreeItem;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisAnonClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisBlock;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisCase;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisCatch;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisClass;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisContainer;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisDoWhile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElement;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElse;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisEnum;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisField;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile.ThemisAnalysisObject;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFinally;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFor;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf.ThemisIteratorChain;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisIf.ThemisReducedIterator;
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
 * Source Panel.
 */
public class ThemisSourcePanel
        implements TethysComponent {
    /**
     * The Next entryId.
     */
    private static final AtomicInteger NEXT_ENTRY_ID = new AtomicInteger(1);

    /**
     * The Allowed File children.
     */
    private static final Class<?>[] FILE_CHILDREN =  {
        ThemisAnalysisClass.class,
        ThemisAnalysisInterface.class,
        ThemisAnalysisEnum.class
    };

    /**
     * The Allowed Class children.
     */
    private static final Class<?>[] CLASS_CHILDREN =  {
            ThemisAnalysisClass.class,
            ThemisAnalysisInterface.class,
            ThemisAnalysisEnum.class,
            ThemisAnalysisMethod.class,
            ThemisAnalysisField.class,
            ThemisAnalysisBlock.class
    };

    /**
     * The Allowed Code children.
     */
    private static final Class<?>[] CODE_CHILDREN =  {
            ThemisAnalysisClass.class,
            ThemisAnalysisInterface.class,
            ThemisAnalysisEnum.class,
            ThemisAnalysisIf.class,
            ThemisAnalysisFor.class,
            ThemisAnalysisDoWhile.class,
            ThemisAnalysisWhile.class,
            ThemisAnalysisSwitch.class,
            ThemisAnalysisTry.class,
            ThemisAnalysisStatement.class,
            ThemisAnalysisField.class,
            ThemisAnalysisBlock.class
    };

    /**
     * The Switch Code children.
     */
    private static final Class<?>[] SWITCH_CHILDREN =  {
            ThemisAnalysisCase.class
    };

    /**
     * The SplitTree Manager.
     */
    private final TethysSplitTreeManager<ThemisSourceEntry> theSplitTree;

    /**
     * The Tree Manager.
     */
    private final TethysTreeManager<ThemisSourceEntry> theTree;

    /**
     * The HTML Manager.
     */
    private final TethysHTMLManager theHTML;

    /**
     * The Current root.
     */
    private TethysTreeItem<ThemisSourceEntry> theRoot;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisSourcePanel(final TethysGuiFactory pFactory) throws OceanusException {
        /* Create the splitTree Manager */
        theSplitTree = pFactory.newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHTML = theSplitTree.getHTMLManager();
        theHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theTree.setVisible(true);

        /* Listen to the TreeManager */
        //theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
    }

    @Override
    public TethysNode getNode() {
        return theSplitTree.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSplitTree.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theSplitTree.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return theSplitTree.getId();
    }

    /**
     * Get NextId.
     * @return the next id
     */
    static int getNextId() {
        return NEXT_ENTRY_ID.getAndIncrement();
    }

    /**
     * Initialise tree.
     * @param pProject the project
     */
    void initialiseTree(final ThemisAnalysisProject pProject) {
        /* If there is currently a root item */
        if (theRoot != null) {
            /* Hide it */
            theRoot.setVisible(false);
        }

        /* Create root item for project */
        final ThemisSourceEntry myEntry = new ThemisSourceEntry(pProject);
        theRoot = theTree.addRootItem(myEntry.getUniqueName(), myEntry);
        theRoot.setIcon(ThemisSourceIcon.getElementIcon(pProject));
        theRoot.setVisible(true);

        /* Create child entries */
        createChildEntries(theRoot, pProject);
    }

    /**
     * Create child entries tree.
     * @param pParent the parent
     * @param pChild the child
     */
    void createChildEntries(final TethysTreeItem<ThemisSourceEntry> pParent,
                            final ThemisAnalysisElement pChild) {
        /* Loop through the root children */
        final Iterator<? extends ThemisAnalysisElement> myIterator = childIterator(pChild);
        while (myIterator.hasNext()) {
            final ThemisAnalysisElement myChild = myIterator.next();

            /* Create a new root entry */
            final ThemisSourceEntry myEntry = new ThemisSourceEntry(pParent.getItem(), myChild);
            final TethysTreeItem<ThemisSourceEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
            myTreeItem.setIcon(ThemisSourceIcon.getElementIcon(myChild));
            myTreeItem.setVisible(true);

            /* Create child entries */
            createChildEntries(myTreeItem, myChild);
        }
    }

    /**
     * Derive list of children for an element.
     * @param pElement the project
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> childIterator(final ThemisAnalysisElement pElement) {
        if (pElement instanceof ThemisAnalysisProject) {
            return projectIterator((ThemisAnalysisProject) pElement);
        }
        if (pElement instanceof ThemisAnalysisModule) {
            return moduleIterator((ThemisAnalysisModule) pElement);
        }
        if (pElement instanceof ThemisAnalysisPackage) {
            return packageIterator((ThemisAnalysisPackage) pElement);
        }
        if (pElement instanceof ThemisAnalysisFile) {
            return allowedIterator((ThemisAnalysisFile) pElement, FILE_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisClass
            || pElement instanceof ThemisAnalysisAnonClass
            || pElement instanceof ThemisAnalysisInterface
            || pElement instanceof ThemisAnalysisEnum) {
            return allowedIterator((ThemisAnalysisObject) pElement, CLASS_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisMethod
            || pElement instanceof ThemisAnalysisLambda
            || pElement instanceof ThemisAnalysisBlock) {
            return allowedIterator((ThemisAnalysisContainer) pElement, CODE_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisFor
            || pElement instanceof ThemisAnalysisWhile
            || pElement instanceof ThemisAnalysisDoWhile) {
            return allowedIterator((ThemisAnalysisContainer) pElement, CODE_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisTry) {
            return tryIterator((ThemisAnalysisTry) pElement);
        }
        if (pElement instanceof ThemisAnalysisIf) {
            return ifIterator((ThemisAnalysisIf) pElement);
        }
        if (pElement instanceof ThemisAnalysisSwitch) {
            return allowedIterator((ThemisAnalysisContainer) pElement, SWITCH_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisCase
                || pElement instanceof ThemisAnalysisElse) {
            return allowedIterator((ThemisAnalysisContainer) pElement, CODE_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisCatch
                || pElement instanceof ThemisAnalysisFinally) {
            return allowedIterator((ThemisAnalysisContainer) pElement, CODE_CHILDREN);
        }
        if (pElement instanceof ThemisAnalysisField) {
            return fieldIterator((ThemisAnalysisField) pElement);
        }
        if (pElement instanceof ThemisAnalysisStatement) {
            return statementIterator((ThemisAnalysisStatement) pElement);
        }
        return Collections.emptyIterator();
    }

    /**
     * Derive list of children for a project.
     * @param pProject the project
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> projectIterator(final ThemisAnalysisProject pProject) {
        return new ThemisReducedIterator<>(pProject.getModules().iterator());
    }

    /**
     * Derive list of children for a module.
     * @param pModule the module
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> moduleIterator(final ThemisAnalysisModule pModule) {
        return new ThemisReducedIterator<>(pModule.getPackages().iterator());
    }

    /**
     * Derive list of children for a package.
     * @param pPackage the package
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> packageIterator(final ThemisAnalysisPackage pPackage) {
        return new ThemisReducedIterator<>(pPackage.getFiles().iterator());
    }

    /**
     * Derive list of children for a container.
     * @param pContainer the container
     * @param pAllowed the allowed array.
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> allowedIterator(final ThemisAnalysisContainer pContainer,
                                                                   final Class<?>[] pAllowed) {
        return pContainer.getContents().stream().filter(e -> filterAllowed(e, pAllowed)).iterator();
    }

    /**
     * Derive list of children for a try.
     * @param pTry the try
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> tryIterator(final ThemisAnalysisTry pTry) {
        Iterator<ThemisAnalysisElement> myIterator = pTry.getContents().stream().filter(e -> filterAllowed(e, CODE_CHILDREN)).iterator();
        ThemisAnalysisCatch myCatch = pTry.getCatch();
        while (myCatch != null) {
            myIterator = new ThemisIteratorChain<>(myIterator, Collections.singleton((ThemisAnalysisElement) myCatch).iterator());
            myCatch = myCatch.getCatch();
        }
        final ThemisAnalysisFinally myFinally = pTry.getFinally();
        if (myFinally != null) {
            myIterator = new ThemisIteratorChain<>(myIterator,  Collections.singleton((ThemisAnalysisElement) myFinally).iterator());
        }
        return myIterator;
    }

    /**
     * Derive list of children for an if.
     * @param pIf the if
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> ifIterator(final ThemisAnalysisIf pIf) {
        Iterator<ThemisAnalysisElement> myIterator = pIf.getContents().stream().filter(e -> filterAllowed(e, CODE_CHILDREN)).iterator();
        ThemisAnalysisElse myElse = pIf.getElse();
        while (myElse != null) {
            myIterator = new ThemisIteratorChain<>(myIterator,  Collections.singleton((ThemisAnalysisElement) myElse).iterator());
            myElse = myElse.getElse();
        }
        return myIterator;
    }

    /**
     * Derive list of children for a field.
     * @param pField the field
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> fieldIterator(final ThemisAnalysisField pField) {
        return new ThemisReducedIterator<>(pField.statementIterator());
    }

    /**
     * Derive list of children for a statement.
     * @param pStatement the statement
     * @return the list of children
     */
    private static Iterator<ThemisAnalysisElement> statementIterator(final ThemisAnalysisStatement pStatement) {
        final ThemisAnalysisElement myEmbedded = pStatement.getEmbedded();
        return myEmbedded != null
                ? Collections.singleton(myEmbedded).iterator()
                : Collections.emptyIterator();
    }

    /**
     * Filter allowed objects.
     * @param pElement the element
     * @param pAllowed the allowed array.
     * @return allowedd true/false
     */
    private static boolean filterAllowed(final ThemisAnalysisElement pElement,
                                         final Class<?>[] pAllowed) {
        /* Loop through the allowed list */
        for (Class<?> myClazz : pAllowed) {
            /* If the child is allowed */
            if (myClazz.isInstance(pElement)) {
                return true;
            }
        }

        /* Not allowed */
        return false;
    }

    ///**
    // * Handle the split tree action event.
    // * @param pEvent the event
    // */
    //protected void handleSplitTreeAction(final TethysEvent<TethysUIEvent> pEvent) {
    //    /* If this is a new value */
    //    if (pEvent.getEventId() == TethysUIEvent.NEWVALUE) {
    //        handleNewTreeItem(pEvent.getDetails(ThemisStatsEntry.class));
    //    }
    //}

    ///**
    // * Handle the new tree item.
    // * @param pEntry the new entry
    // */
    //private void handleNewTreeItem(final ThemisStatsEntry pEntry) {
    //    /* If we have an entry */
    //    if (pEntry != null) {
    //        /* Access the StatsHolder */
    //        final ThemisStatsBase myHolder = pEntry.getObject();
    //
    //        /* Build report */
    //        final String myDoc = ThemisStatsReport.reportOnStats(myHolder);
    //        theHTML.setHTMLContent(myDoc, "");
    //    }
    //}
}
