package net.sourceforge.joceanus.gordianknot.junit.regression;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * TestSuite for KeyStore Tests.
 */
@Suite
@SuiteDisplayName("KeyStore Test Suite")
@SelectClasses(KeyStoreTest.class)
public class KeyStoreTestSuite {
}
