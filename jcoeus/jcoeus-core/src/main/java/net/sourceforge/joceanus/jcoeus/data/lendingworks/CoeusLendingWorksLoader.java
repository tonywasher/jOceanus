/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LendingWorks Loader.
 */
public class CoeusLendingWorksLoader {
    /**
     * The Suffix.
     */
    private static final String SUFFIX = ".csv";

    /**
     * The Statement name.
     */
    private static final String STATEMENT = "LendingWorksStatement";

    /**
     * The formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The base path.
     */
    private final Path theBasePath;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pPath the path to load from
     * @throws OceanusException on error
     */
    public CoeusLendingWorksLoader(final MetisDataFormatter pFormatter,
                                   final String pPath) throws OceanusException {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Adjust and store the path */
        FileSystem mySystem = FileSystems.getDefault();
        String myPath = pPath + mySystem.getSeparator() + CoeusMarketProvider.LENDINGWORKS;
        theBasePath = mySystem.getPath(myPath);
    }

    /**
     * Load market.
     * @return the market
     * @throws OceanusException on error
     */
    public CoeusLendingWorksMarket loadMarket() throws OceanusException {
        /* Create the market */
        CoeusLendingWorksMarket myMarket = new CoeusLendingWorksMarket(theFormatter);

        /* Parse the statement */
        myMarket.parseStatement(theBasePath.resolve(STATEMENT + SUFFIX));

        /* Return the market */
        return myMarket;
    }

}
