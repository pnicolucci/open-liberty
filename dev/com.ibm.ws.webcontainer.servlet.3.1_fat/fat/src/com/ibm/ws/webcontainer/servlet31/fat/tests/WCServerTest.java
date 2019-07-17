package com.ibm.ws.webcontainer.servlet31.fat.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.ibm.ws.fat.util.browser.WebBrowser;
import com.ibm.ws.fat.util.browser.WebResponse;

import componenttest.annotation.ExpectedFFDC;
import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;
import junit.framework.Assert;

/**
 * All Servlet 3.1 tests with all applicable server features enabled.
 */
@MinimumJavaLevel(javaLevel = 7)
@RunWith(FATRunner.class)
public class WCServerTest extends LoggingTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(WCServerTest.class.getName());
    private static final String TESTSERVLET31_APP_NAME = "TestServlet31";
    private static final String TESTSERVLET31_JAR_NAME = "TestServlet31";
    private static final String SESSIONIDLISTENER_JAR_NAME = "SessionIdListener";
    private static final String SESSIONIDLISTENER_APP_NAME = "SessionIdListener";
    private static final String SESSION_ID_LISTENER_ADD_LISTENER_APP_NAME = "SessionIdListenerAddListener";
    private static final String TEST_SERVLET_MAPPING_APP_NAME = "TestServletMapping";
    private static final String TEST_SERVLET_MAPPING_ANNO_APP_NAME = "TestServletMappingAnno";
    private static final String SERVLET_CONTEXT_ADD_LISTENER_APP_NAME = "ServletContextAddListener";
    private static final String TEST_PROGRAMMATIC_LISTENER_ADDITION_JAR_NAME = "TestProgrammaticListenerAddition";
    private static final String TEST_PROGRAMMATIC_LISTENER_ADDITION_APP_NAME = "TestProgrammaticListenerAddition";
    private static final String SERVLET_CONTEXT_CREATE_LISTENER_APP_NAME = "ServletContextCreateListener";
    private static final String TEST_METADATA_COMPLETE_JAR_NAME = "TestMetadataComplete";
    private static final String TEST_MEATADATA_COMPLETE_EXCLUDED_FRAGMENT_JAR_NAME = "TestMetadataCompleteExcludedFragment";
    private static final String TEST_METADATA_COMPLETE_APP_NAME = "TestMetadataComplete";
    private static final String SINGLETON_STORE_JAR_NAME = "SingletonStore";

    protected static final Map<String, String> testUrlMap = new HashMap<String, String>();

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("servlet31_wcServer");

    @BeforeClass
    public static void setup() throws Exception {
        // Create the TestServlet31 jar
        JavaArchive testServlet31Jar = ShrinkWrap.create(JavaArchive.class, TESTSERVLET31_JAR_NAME + ".jar");
        testServlet31Jar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.testservlet31.jar.servlets");

        // Create the SessionIdListener jar
        JavaArchive sessionIdListenerJar = ShrinkWrap.create(JavaArchive.class, SESSIONIDLISTENER_JAR_NAME + ".jar");
        sessionIdListenerJar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.sessionidlistener.listeners");
        sessionIdListenerJar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.sessionidlistener.servlets");

        // Create the TestProgrammaticListenerAddition.jar
        JavaArchive testProgrammaticListenerAdditionJar = ShrinkWrap.create(JavaArchive.class, TEST_PROGRAMMATIC_LISTENER_ADDITION_JAR_NAME + ".jar");
        ShrinkHelper.addDirectory(testProgrammaticListenerAdditionJar, "test-applications/" + TEST_PROGRAMMATIC_LISTENER_ADDITION_JAR_NAME + ".jar" + "/resources");

        // Create the TestMetaDataComplete.jar
        JavaArchive testMetadataCompleteJar = ShrinkWrap.create(JavaArchive.class, TEST_METADATA_COMPLETE_JAR_NAME + ".jar");
        testMetadataCompleteJar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.initializer");
        ShrinkHelper.addDirectory(testMetadataCompleteJar, "test-applications/" + TEST_METADATA_COMPLETE_JAR_NAME + ".jar" + "/resources");

        // Create the TestMetaDataCompleteExcludedFragment.jar
        JavaArchive testMetadataCompleteExcludedFragmentJar = ShrinkWrap.create(JavaArchive.class, TEST_MEATADATA_COMPLETE_EXCLUDED_FRAGMENT_JAR_NAME + ".jar");
        testMetadataCompleteExcludedFragmentJar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.excluded.fragment.initializer");
        testMetadataCompleteExcludedFragmentJar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.excluded.fragment.servlets");
        ShrinkHelper.addDirectory(testMetadataCompleteExcludedFragmentJar, "test-applications/" + TEST_MEATADATA_COMPLETE_EXCLUDED_FRAGMENT_JAR_NAME + ".jar" + "/resources");

        // Create SingletonStore.jar
        JavaArchive singletonStoreJar = ShrinkWrap.create(JavaArchive.class, SINGLETON_STORE_JAR_NAME + ".jar");
        singletonStoreJar.addPackage("teststorage");

        // Create the TestServlet31.war application
        WebArchive testServlet31War = ShrinkWrap.create(WebArchive.class, TESTSERVLET31_APP_NAME + ".war");
        testServlet31War.addAsLibrary(testServlet31Jar);
        testServlet31War.addAsLibrary(sessionIdListenerJar);
        testServlet31War.addPackage("com.ibm.ws.webcontainer.servlet31.fat.testservlet31.war.listeners");
        testServlet31War.addPackage("com.ibm.ws.webcontainer.servlet31.fat.testservlet31.war.servlets");

        ShrinkHelper.addDirectory(testServlet31War, "test-applications/" + TESTSERVLET31_APP_NAME + ".war" + "/resources");

        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), testServlet31War);

        // Create the SessionIdListenerAddListener.war
        // This also includes the SessionIdListenerAddListener resources directory and the SessionIdListener Jar
        WebArchive sessionIdListenerAddListenerWar = ShrinkWrap.create(WebArchive.class, SESSION_ID_LISTENER_ADD_LISTENER_APP_NAME + ".war");
        sessionIdListenerAddListenerWar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.sessionidlistener.addlistener.listeners");
        sessionIdListenerAddListenerWar.addAsLibrary(sessionIdListenerJar);

        ShrinkHelper.addDirectory(sessionIdListenerAddListenerWar, "test-applications/" + SESSION_ID_LISTENER_ADD_LISTENER_APP_NAME + ".war" + "/resources");

        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), sessionIdListenerAddListenerWar);

        // Create TestServletMapping.war
        WebArchive testServletMappingWar = ShrinkWrap.create(WebArchive.class, TEST_SERVLET_MAPPING_APP_NAME + ".war");
        testServletMappingWar.addPackage("servlets");

        ShrinkHelper.addDirectory(testServletMappingWar, "test-applications/" + TEST_SERVLET_MAPPING_APP_NAME + ".war" + "/resources");

        ShrinkHelper.exportAppToServer(SHARED_SERVER.getLibertyServer(), testServletMappingWar);

        // Create TestServletMappingAnno.war
        WebArchive testServletMappingAnnoWar = ShrinkWrap.create(WebArchive.class, TEST_SERVLET_MAPPING_ANNO_APP_NAME + ".war");
        testServletMappingAnnoWar.addPackage("servlets");

        ShrinkHelper.addDirectory(testServletMappingAnnoWar, "test-applications/" + TEST_SERVLET_MAPPING_ANNO_APP_NAME + ".war" + "/resources");

        ShrinkHelper.exportAppToServer(SHARED_SERVER.getLibertyServer(), testServletMappingAnnoWar);

        // Create ServletContextAddListener.war
        WebArchive servletContextAddListenerWar = ShrinkWrap.create(WebArchive.class, SERVLET_CONTEXT_ADD_LISTENER_APP_NAME + ".war");
        servletContextAddListenerWar.addPackage("com.ibm.ws.fat.wc.servlet31.listeners");
        servletContextAddListenerWar.addAsLibrary(testServlet31Jar);

        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), servletContextAddListenerWar);

        // Create TestProgrammaticListenerAddition.war
        WebArchive testProgrammaticListenerAdditionWar = ShrinkWrap.create(WebArchive.class, TEST_PROGRAMMATIC_LISTENER_ADDITION_APP_NAME + ".war");
        testProgrammaticListenerAdditionWar.addPackage("listeners");
        testProgrammaticListenerAdditionWar.addAsLibrary(testServlet31Jar);
        testProgrammaticListenerAdditionWar.addAsLibrary(testProgrammaticListenerAdditionJar);

        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), testProgrammaticListenerAdditionWar);

        // Create ServletContextCreateListener.war
        WebArchive servletContextCreateListenerWar = ShrinkWrap.create(WebArchive.class, SERVLET_CONTEXT_CREATE_LISTENER_APP_NAME + ".war");
        servletContextCreateListenerWar.addPackage("com.ibm.ws.fat.wc.servlet31.listeners");
        servletContextCreateListenerWar.addAsLibrary(testServlet31Jar);

        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), servletContextCreateListenerWar);

        // Create SessionIdListener.war
        WebArchive sessionIdListenerWar = ShrinkWrap.create(WebArchive.class, SESSIONIDLISTENER_APP_NAME + ".war");
        sessionIdListenerWar.addAsLibrary(sessionIdListenerJar);

        ShrinkHelper.addDirectory(sessionIdListenerWar, "test-applications/" + SESSIONIDLISTENER_APP_NAME + ".war" + "/resources");
        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), sessionIdListenerWar);

        // Create TestMetadataComplete.war
        WebArchive testMetadataCompleteWar = ShrinkWrap.create(WebArchive.class, TEST_METADATA_COMPLETE_APP_NAME + ".war");
        testMetadataCompleteWar.addAsLibrary(testMetadataCompleteJar);
        testMetadataCompleteWar.addAsLibrary(testMetadataCompleteExcludedFragmentJar);
        testMetadataCompleteWar.addAsLibrary(singletonStoreJar);
        testMetadataCompleteWar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.servlets");
        testMetadataCompleteWar.addPackage("com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.stack");
        ShrinkHelper.addDirectory(testMetadataCompleteWar, "test-applications/" + TEST_METADATA_COMPLETE_APP_NAME + ".war" + "/resources");

        ShrinkHelper.exportDropinAppToServer(SHARED_SERVER.getLibertyServer(), testMetadataCompleteWar);

        // Add the apps to the server for validation
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(TESTSERVLET31_APP_NAME);
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(SESSION_ID_LISTENER_ADD_LISTENER_APP_NAME);
        // PAN: TODO we are in the apps dir so not installed SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(TEST_SERVLET_MAPPING_APP_NAME);
        // PAN: TODO same as above SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(TEST_SERVLET_MAPPING_ANNO_APP_NAME);
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(SERVLET_CONTEXT_ADD_LISTENER_APP_NAME);
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(TEST_PROGRAMMATIC_LISTENER_ADDITION_APP_NAME);
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(SERVLET_CONTEXT_CREATE_LISTENER_APP_NAME);
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(SESSIONIDLISTENER_APP_NAME);
        //SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(TEST_METADATA_COMPLETE_APP_NAME);

        // Create the TestServletMapping.war

        SHARED_SERVER.startIfNotStarted();

        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + TESTSERVLET31_APP_NAME);
        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + SESSION_ID_LISTENER_ADD_LISTENER_APP_NAME);
        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + SERVLET_CONTEXT_ADD_LISTENER_APP_NAME);
        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + TEST_PROGRAMMATIC_LISTENER_ADDITION_APP_NAME);
        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + SERVLET_CONTEXT_CREATE_LISTENER_APP_NAME);
        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + SESSIONIDLISTENER_APP_NAME);
        SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKZ0001I.* " + TEST_METADATA_COMPLETE_APP_NAME);
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        if (SHARED_SERVER.getLibertyServer() != null && SHARED_SERVER.getLibertyServer().isStarted()) {
            SHARED_SERVER.getLibertyServer().stopServer("SRVE9014E:.*", "SRVE9016E:.*", "CWWKZ0002E:.*", "SRVE9002E:.*", "SRVE8015E:.*");
        }
    }

    protected String parseResponse(WebResponse wr, String beginText, String endText) {
        String s;
        String body = wr.getResponseBody();
        int beginTextIndex = body.indexOf(beginText);
        if (beginTextIndex < 0)
            return "begin text, " + beginText + ", not found";
        int endTextIndex = body.indexOf(endText, beginTextIndex);
        if (endTextIndex < 0)
            return "end text, " + endText + ", not found";
        s = body.substring(beginTextIndex + beginText.length(), endTextIndex);
        return s;
    }

    /**
     * Sample test
     *
     * @throws Exception
     *                       if something goes horribly wrong
     */
    @Test
    public void testServlet() throws Exception {
        WebResponse response = this.verifyResponse("/TestServlet31/MyServlet", "Hello World");

        // verify the X-Powered-By Response header
        response.verifyResponseHeaderEquals("X-Powered-By", false, "Servlet/3.1",
                                            true, false);
    }

    @Test
    public void testProgrammaticallyAddedServlet() throws Exception {
        // 130998: This tests that the servlet that was programmatically
        // added with a different servlet name in "MyServletContextListener"
        // was created and is accessible.
        this.verifyResponse("/TestServlet31/ProgrammaticServlet", "Hello World");
    }

    @Test
    public void testMetadataCompleteHandlesTypesServlet() throws Exception {
        WebBrowser wb = createWebBrowserForTestCase();

        this.verifyResponse(wb, "/TestMetadataComplete/DisplayInits",
                            new String[] { "ParentServletInitializer: com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.servlets.DisplayInits",
                                           "HashSetChildInitializer: com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.stack.HelperMethodChild",
                                           "HashSetChildInitializer: com.ibm.ws.webcontainer.servlet31.fat.metadatacomplete.stack.HelperMethod" });
    }

    //This isn't duplicating testMetadataCompleteHandlesTypesServlet since we want granularity on the functions.
    //This is for excluded JARs only, the one above is for general SCI function.
    @Test
    public void testMetadataCompleteExcludedHandlesTypesServlet() throws Exception {
        WebBrowser wb = createWebBrowserForTestCase();

        String[] expected = new String[] {};

        String[] unexpected = new String[] { "ExcludedServletInitializer: servlets.ExcludedServlet", "ExcludedServletInitializer: servlets.DisplayInits",
                                             "ParentServletInitializer: servlets.ExcludedServlet" };

        this.verifyResponse(wb, "/TestMetadataComplete/DisplayInits", expected, unexpected);
    }

    /**
     * Verifies that container lifecycle events are behaving correctly (JSR-299 section 11.5).
     *
     * @throws Exception
     *                       if validation fails, or if an unexpected error occurs
     */
    @Test
    public void testSessionIdListenerChangeServlet() throws Exception {
        // Make sure the test framework knows that SRVE9014E is expected
        // PAN: TODO
        // MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE9014E:.*");

        testSessionIdListener("/TestServlet31/SessionIdListenerChangeServlet");
    }

    /**
     * Perform the same test as "testSessionIdListenerChangeServlet" except for this time the HttpSessionIdListener is
     * registered in the web.xml and not via the @WebListener annotation. The SessionIdListener application is used
     * for this test.
     *
     * @throws Exception
     */
    @Test
    public void testSessionIdListenerRegisteredWebXml() throws Exception {
        // Make sure the test framework knows that SRVE9014E is expected
        // PAN: TODO
        // MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE9014E:.*");

        testSessionIdListener("/SessionIdListener/SessionIdListenerChangeServlet");
    }

    /**
     * Perform the same test as "testSessionIdListenerChangeServlet" except for this time the HttpSessionIdListener is
     * registered via a ServletContextListener using the ServletContext.addListener API. The SessionIdListenerAddListener
     * application is used for this test.
     *
     * @throws Exception
     */
    @Test
    public void testSessionIdListenerServletContextAddListener() throws Exception {
        // Make sure the test framework knows that SRVE9014E is expected
        // PAN: TODO
        // PAN: MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE9014E:.*");

        testSessionIdListener("/SessionIdListenerAddListener/SessionIdListenerChangeServlet");
    }

    /*
     * Common test code for HttpSessionIdListener tests.
     */
    private void testSessionIdListener(String url) throws Exception {
        // PAN: TODO
        this.verifyResponse(url, "Expected IllegalStateException");
        WebBrowser wb = createWebBrowserForTestCase();

        WebResponse wr = this.verifyResponse(wb, url + "?getSessionFirst=true", new String[] { "Session id returned from changeSessionId",
                                                                                               "Change count = 1" });
        String oldSessionId = parseResponse(wr, "Original session id = <sessionid>", "</sessionid>");
        String newSessionId = parseResponse(wr, "Session id returned from changeSessionId = <sessionid>", "</sessionid>");
        Assert.assertTrue("ids are equal: old=" + oldSessionId + ":new=" + newSessionId, !oldSessionId.equals(newSessionId));
        verifyResponse(wb, url, "Original session id = <sessionid>" + newSessionId + "</sessionid>");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestedSessionId() throws Exception {
        WebBrowser wb = createWebBrowserForTestCase();
        // PAN: TODO
        this.verifyResponse(wb, "/TestServlet31/SessionIdTest;jsessionid=mysessionid", new String[] { "Requested session id was mysessionid",
                                                                                                      "Requested Session id is invalid" });
        String[] expectedResponses = new String[] { "Requested session id was mysessionid", "Requested Session id is invalid" };
        verifyResponse(wb, "/TestServlet31/SessionIdTest;jsessionid=mysessionid", expectedResponses);
    }

    @Test
    @Mode(TestMode.LITE)
    public void testGetServerInfo() throws Exception {
        WebBrowser wb = createWebBrowserForTestCase();

        String v = System.getProperty("version", "");
        File f = new File("../build.image/wlp/lib/versions/WebSphereApplicationServer.properties");
        if (f.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileReader(f));
                v = props.getProperty("com.ibm.websphere.productVersion");
            } catch (IOException e) {
            }
        }

        this.verifyResponse(wb, "/TestServlet31/GetServerInfoTest", "GetServerInfoTest: ServletContext.getServerInfo()=IBM WebSphere Liberty/" + v);
    }

    /**
     * Verifies that a response.reset works
     *
     * @throws Exception
     *                       if validation fails, or if an unexpected error occurs
     */
    @Test
    //@Mode(TestMode.FULL)
    public void testResponseReset() throws Exception {
        WebBrowser wb = createWebBrowserForTestCase();
        String url = "/TestServlet31/ResponseReset?firstType=pWriter&secondType=pWriter";
        WebResponse wr = this.verifyResponse(wb, url, "SUCCESS");
        String body = wr.getResponseBody();
        Assert.assertTrue("contained content before the reset: url=" + url + "::" + body, body.indexOf("FAILURE") == -1);
        url = "/TestServlet31/ResponseReset?firstType=pWriter&secondType=outputStream";
        wr = this.verifyResponse(wb, url, "SUCCESS");
        body = wr.getResponseBody();
        Assert.assertTrue("contained content before the reset: url=" + url + "::" + body, body.indexOf("FAILURE") == -1);
        url = "/TestServlet31/ResponseReset?firstType=outputStream&secondType=pWriter";
        wr = this.verifyResponse(wb, url, "SUCCESS");
        body = wr.getResponseBody();
        Assert.assertTrue("contained content before the reset: url=" + url + "::" + body, body.indexOf("FAILURE") == -1);
        url = "/TestServlet31/ResponseReset?firstType=outputStream&secondType=outputStream";
        wr = this.verifyResponse(wb, url, "SUCCESS");
        body = wr.getResponseBody();
        Assert.assertTrue("contained content before the reset: url=" + url + "::" + body, body.indexOf("FAILURE") == -1);
    }

    /**
     * Verifies that the ServletContext.getMinorVersion() returns 1 for Servlet 3.1.
     *
     * @throws Exception
     */
    @Test
    public void testServletContextMinorVersion() throws Exception {
        this.verifyResponse("/TestServlet31/MyServlet?TestMinorVersion=true",
                            "minorVersion: 1");
    }

    /**
     * Verify that a duplicate <servlet-mapping> element results in a deployment error. Servlet 3.1 spec, section 12.2
     *
     * @throws Exception
     */
    @Test
    @Mode(TestMode.FULL)
    @ExpectedFFDC({ "com.ibm.wsspi.adaptable.module.UnableToAdaptException", "com.ibm.ws.container.service.metadata.MetaDataException" })
    public void testServletMapping() throws Exception {

        LibertyServer wlp = SHARED_SERVER.getLibertyServer();
        wlp.setMarkToEndOfLog();
        // PAN: TODO
        // MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE9016E:.*", "CWWKZ0002E:.*");

        wlp.saveServerConfiguration();
        // copy server.xml for TestServletMapping.war
        // should use updateServerConfiguration(wlp.getServerRoot() +
        wlp.setServerConfigurationFile("TestServletMapping/server.xml");
        // check for error message
        String logmsg = wlp.waitForStringInLogUsingMark("CWWKZ0002E:.*TestServletMapping");
        Assert.assertNotNull("TestServletMapping application should have failed to start ", logmsg);

        // application failed to start, verify that it is because of a duplicate servlet-mapping
        logmsg = wlp.waitForStringInLogUsingMark("SRVE9016E:");
        Assert.assertNotNull("TestServletMapping application deployment did not result in  message SRVE9016E: ", logmsg);

        wlp.setMarkToEndOfLog();
        // copy server.xml for TestServletMappingAnno.war
        wlp.setServerConfigurationFile("TestServletMappingAnno/server.xml");
        // check for error message
        logmsg = wlp.waitForStringInLogUsingMark("CWWKZ0002E:.*TestServletMappingAnno");
        Assert.assertNotNull("TestServletMappingAnno application should have failed to start ", logmsg);

        // application failed to start, verify that it is because of a duplicate servlet-mapping
        logmsg = wlp.waitForStringInLogUsingMark("SRVE9016E:");
        Assert.assertNotNull("TestServletMappingAnno application deployment did not result in  message SRVE9016E ", logmsg);

        wlp.restoreServerConfiguration();
    }

    /**
     * Verifies that the correct message is printed out when the class name specified for the ServletContext.addListener(String className)
     * API can not be found.
     *
     * @throws Exception
     */
    @Test
    public void testServletContextAddListener() throws Exception {

        // Make sure the test framework knows that SRVE8015E is expected
        // PAN: TODO
        // MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE8015E:.*");

        this.verifyResponse("/ServletContextAddListener/SimpleTestServlet", "Hello World");

        // application should be initialized with the above request, now check the logs for the proper output.
        Assert.assertNotNull(SHARED_SERVER.getLibertyServer().findStringsInLogs("SRVE8015E:.*ThisListenerDoesNotExist"));
    }

    /**
     * This test case will use a ServletContainerInitializer to add a ServletContextListener in a
     * programmatic way. Then in the ServletContextListener contextInitialized method calls a method
     * on the ServletContext.
     *
     * This method should throw an UnsupportedOperationException according to the Servlet 3.1 ServletContext API.
     *
     * Check to ensure that this message is thrown and the NLS message is resolved correctly.
     *
     * @throws Exception
     */
    @Test
    @Mode(TestMode.LITE)
    public void testProgrammaticListenerAddition() throws Exception {
        // PAN: TODO
        // MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE9002E:.*");

        // Drive a request to the SimpleTestServlet to initialize the application
        this.verifyResponse("/TestProgrammaticListenerAddition/SimpleTestServlet", "Hello World");

        // Ensure that the proper exception was output
        LibertyServer server = SHARED_SERVER.getLibertyServer();

        server.resetLogMarks();

        // PI41941: Changed the message. Wait for the full message.
        String logMessage = server
                        .waitForStringInLog("SRVE9002E:.*\\(Operation: getVirtualServerName \\| Listener: listeners.MyProgrammaticServletContextListener \\| Application: TestProgrammaticListenerAddition\\)");
        Assert.assertNotNull("The correct message was not logged.", logMessage);

    }

    /**
     * This test case uses the ServletContext.createListener API to try and create a listener that
     * does not implement one of the expected listener interfaces. The test will ensure that the proper
     * exception is thrown in this scenario.
     */
    @Test
    @Mode(TestMode.LITE)
    public void testServletContextCreateListenerBadListener() throws Exception {

        // Make sure the test framework knows that SRVE9014E is expected
        // PAN: TODO
        // MOVED TO AFTER CLASS
        //SHARED_SERVER.setExpectedErrors("SRVE9014E:.*");

        // Drive a request to the SimpleTestServlet to initialize the application
        this.verifyResponse("/ServletContextCreateListener/SimpleTestServlet", "Hello World");

        // Ensure that the proper exception was output
        LibertyServer server = SHARED_SERVER.getLibertyServer();
        List<String> logMessage = server.findStringsInLogs("SRVE8014E:");

        Assert.assertNotNull("The correct message was not logged.", logMessage);

        // Ensure a REQUEST_INITIALIZED can be found in the logs.  If it can't be that means that a
        // correct listener was not created and added as expected.
        logMessage = server.findStringsInLogs("REQUEST_INITIALIZED");
        Assert.assertNotNull("REQUEST_INITIALIZED was not found in the logs. The listener must not have been created and added correctly",
                             logMessage);

    }

    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }
}