package net.sourceforge.joceanus.gordianknot.junit.regression;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * TestSuite for Symmetric Tests.
 */
@Suite
@SuiteDisplayName("Symmetric Test Suite")
@SelectClasses({
        KeySetTest.class,
        RandomSpecTest.class,
        SymmetricTest.class,
        ZipFileTest.class
})
public class SymmetricTestSuite {
}
