/*
 * Created on Jan 20, 2007
 */
package net.sf.jdptool.api.config;

import java.net.URL;

import junit.framework.TestCase;
import net.sf.jdptool.DigesterHelper;
import net.sf.jdptool.config.JdpConfig;
import net.sf.jdptool.config.JdpRuleSet;
import net.sf.jdptool.config.ModuleConfig;

public class JdpConfigTest extends TestCase {
    private static String TEST_CONFIG = 
        "/net/sf/jdptool/api/config/default-jdp-config.xml";
    
    private DigesterHelper helper;
    
    private JdpConfig config;
    
    /**
     * set up enviroment and context
     */
    protected void setUp() throws Exception {
        helper = new DigesterHelper();
        helper.addRuleSet(new JdpRuleSet());
        
        URL url = this.getClass().getResource(TEST_CONFIG);
        config = (JdpConfig)helper.parse(url.openStream());
        
        super.setUp();
    }
    
    /**
     * Tear downn context
     */
    protected void tearDown() throws Exception {
        helper.destroy();
        super.tearDown();
    }
    

    /**
     * Test jdp-config element
     * @throws Exception
     */
    public void testRootTag() throws Exception {
         String expect = "default";
         String filterDir = config.getProperty("name");
         
         assertEquals(expect, filterDir);
    }
    
    
    /**
     * Test recorder element
     * 
     * @throws Exception
     */
    public void testRecorder() throws Exception {
        ModuleConfig recorder = config.getRecorder();
        
        assertEquals("FileRecorder", recorder.getName());
        assertEquals("net.sf.jdptool.recorder.FileRecorder", recorder.getClassName());
        
        assertEquals("filepath", recorder.getParameter(0).getName());
        assertEquals("${jdptool.home}/runtime/logs", recorder.getParameter(0).getValue());
    }

    /**
     * Test runner elements
     * 
     * @throws Exception
     */
    public void testRunners() throws Exception {
        ModuleConfig runner = config.getRunner(0);
        
        assertEquals("snapshot", runner.getName());
        assertEquals("net.sf.jdptool.SnapshotRunner", runner.getClassName());
        
        assertEquals("interval", runner.getParameter(0).getName());
        assertEquals("10000", runner.getParameter(0).getValue());
        
        assertEquals("excludes", runner.getParameter(1).getName());
        assertEquals("java.*;javax.*;sun.*;org.apache.*", runner.getParameter(1).getValue());
        
        runner = config.getRunner(1);
        
        assertEquals("EventDispatch", runner.getName());
        assertEquals("net.sf.jdptool.EventDispatchRunner", runner.getClassName());
        
        assertEquals("excludes", runner.getParameter(0).getName());
        assertEquals("java.*;javax.*;sun.*;org.apache.*", runner.getParameter(0).getValue());
        
    }

    /**
     * Test plugins elements
     * 
     * @throws Exception
     */
    public void testPlugins() throws Exception {
        ModuleConfig plugin = config.getPlugin(0);
        
        assertEquals("FilterPlugin", plugin.getName());
        assertEquals("net.sf.jdptool.filter.FilterPlugin", plugin.getClassName());
        
        assertEquals("filepath", plugin.getParameter(0).getName());
        assertEquals("${jdptool.home}/runtime/conf/default-jdp-filter.xml", plugin.getParameter(0).getValue());
                
    }
}
