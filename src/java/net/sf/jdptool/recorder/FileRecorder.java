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
package net.sf.jdptool.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.jdptool.Recorder;
import net.sf.jdptool.config.ModuleConfig;
import net.sf.jdptool.utils.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class FileRecorder implements Recorder {
    
    private static Log log = LogFactory.getLog(FileRecorder.class);
    
    private ModuleConfig config;
    
    private HashMap<String, OutputStream> outputs = null;
    
    public FileRecorder() {
    }
    
    public void init(ModuleConfig config) {
        this.config = config;
    }
    
    private File getLogDir(String filePath) {
        File logDir = new File(filePath);
        if (!logDir.exists()) {
            if (!logDir.mkdir()) {
                logDir = new File(System.getProperty("user.home"));
            }
        }
        
        return logDir;
    }

    /**
     * Get the <code>OutputStream</code> by thread
     * @param thread
     * @return
     */
    private synchronized OutputStream getOutput(String thread) {
        if (outputs == null) {
            outputs = new HashMap<String, OutputStream>();
        }
        if (outputs.get(thread) != null) {
            return outputs.get(thread);
        }

        File logDir = getLogDir(getFilePath());

        String fileName = thread + "_" + new Date().getTime() + ".log";
        File logFile = new File(logDir, fileName);
        try {
            outputs.put(thread, new FileOutputStream(logFile));
        } catch (FileNotFoundException e) {
            log.error("Log file not found", e);
            return null;
        }

        return outputs.get(thread);
    }
    
    private String getFilePath() {
        if ("filepath".equals(config.getParameter(0).getName())){
            String filepath = config.getParameter(0).getValue();
            return Text.replace(System.getProperties(), filepath, true);
        }
        
        throw new RuntimeException("Attribute [filepath] is must for FileRecorder");
    }
    
    /**
     * Print log messsage into FileOutputStream
     * @param out
     * @param message
     */
    private static void write(OutputStream out, String message) {
        try {
            out.write(new Date().toString().getBytes());
            out.write('\t');
            out.write(message.toString().getBytes());
            out.write('\n');
        } catch (IOException e) {
            log.error("Unable write log into file", e);
        }
    }
    
    public void write(String thread, String message) {
        write(getOutput(thread), message);
    }

    public void write(String thread, String message, int frameCount) {
        // TODO Auto-generated method stub
        write(thread, message);
    }

    public void write(String thread, String clazz, String method, int line,
                      String message, int frameCount) {
        // TODO Auto-generated method stub
        
    }

    public void write(String thread, String clazz, String method, int line,
                      String field, Object value, int frameCount) {
        // TODO Auto-generated method stub
        
    }

    public void write(String thread, String clazz, String method, int line,
                      String field, String value, int frameCount) {
        // TODO Auto-generated method stub
        
    }

    public void write(String thread, String clazz, String method, int line, 
                      String field, long value, int frameCount) {
        // TODO Auto-generated method stub
        
    }

    public void write(String thread, String clazz, String method, int line,
                      String field, char value, int frameCount) {
        // TODO Auto-generated method stub
        
    }

    public void write(String thread, String clazz, String method, int line,
                      String field, double value, int frameCount) {
        // TODO Auto-generated method stub
        
    }
    
    public void close() {
        if (outputs == null) return;
        
        for (Iterator it = outputs.values().iterator(); it.hasNext();) {
            OutputStream out = (OutputStream) it.next();
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                log.error("Close outputstream error", e);
            }
        }
    }
}
