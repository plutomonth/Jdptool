/*
 * Copyright 2006 Lu Ming
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.jdptool;

import net.sf.jdptool.config.ModuleConfig;

/**
 * <p><code>Recorder</code> is charge of record debug information into
 * files or database. And it should be config in <code>jdp_config.xml</code>
 * which should be shared for all runner elements</p>
 * 
 * <p>The following is a fragment from a sample configuration using database
 * <pre>
 *   &lt;recorder class="net.sf.jdptool.DatabaseRecorder"&gt;
 *       &lt;param name="driver" value="com.mysql.jdbc.Driver"/&gt;
 *       &lt;param name="url" value="jdbc:mysql:///test?autoReconnect=true"/&gt;
 *       &lt;param name="schema" value="mysql"/&gt;
 *       &lt;param name="table" value="debug_info"/&gt;
 *   &lt;/recorder&gt;</pre>
 * </p>
 * 
 * <p>The following is a fragment from a sample configuration using file system
 * <pre>
 *   &lt;recorder class="net.sf.jdptool.FileRecorder"&gt;
 *       &lt;param name="filepath" value="/etc/logs"/&gt;
 *   &lt;/recorder&gt;</pre>
 * </p>
 */
public interface Recorder {
    
    /**
     * Initial Recorder
     * @param config
     */
    public void init(ModuleConfig config);
    
    /**
     * Record the message classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param message - the detail message should be tracked
     */
    public void write(String thread, String message);

    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param message - the detail message should be tracked
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String message, int frameCount);
    
    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param clazz - the class of target JVM which relate to event
     * @param method - the method of target JVM which relate to event
     * @param line - the line class of target JVM which relate to event
     * @param message - the detail message should be tracked
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String clazz, String method, int line,
                      String message, int frameCount);
    
    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param clazz - the class of target JVM which relate to event
     * @param method - the method of target JVM which relate to event
     * @param line - the line class of target JVM which relate to event
     * @param field - the field of target JVM which relate to event
     * @param value
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String clazz, String method, int line,
                      String field, Object value, int frameCount);
    
    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param clazz - the class of target JVM which relate to event
     * @param method - the method of target JVM which relate to event
     * @param line - the line class of target JVM which relate to event
     * @param field - the field of target JVM which relate to event
     * @param value - the value of field
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String clazz, String method, int line,
                      String field, String value, int frameCount);

    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param clazz - the class of target JVM which relate to event
     * @param method - the method of target JVM which relate to event
     * @param line - the line class of target JVM which relate to event
     * @param field - the field of target JVM which relate to event
     * @param value - the value of field
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String clazz, String method, int line,
                      String field, long value, int frameCount);

    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param clazz - the class of target JVM which relate to event
     * @param method - the method of target JVM which relate to event
     * @param line - the line class of target JVM which relate to event
     * @param field - the field of target JVM which relate to event
     * @param value - the value of field
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String clazz, String method, int line,
                      String field, char value, int frameCount);

    /**
     * Record the message with frame count classified by thread
     * 
     * @param thread - the thread name, or it can be assignment any other
     *          name by invokers, for example <code>SnapshotRunner</code>
     *          may assign it as <code>"snaphshot"</code>
     * @param clazz - the class of target JVM which relate to event
     * @param method - the method of target JVM which relate to event
     * @param line - the line class of target JVM which relate to event
     * @param field - the field of target JVM which relate to event
     * @param value - the value of field
     * @param frameCount - the current frame count of thread
     */
    public void write(String thread, String clazz, String method, int line,
                      String field, double value, int frameCount);
    
    /**
     * Close the I/O resources
     */
    public void close();

}
