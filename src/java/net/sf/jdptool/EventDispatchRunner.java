/*
 * Copyright 2007 Lu Ming
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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import net.sf.jdptool.config.BasicConfig;
import net.sf.jdptool.config.BreaksConfig;
import net.sf.jdptool.config.FilterConfig;
import net.sf.jdptool.config.FilterRuleSet;
import net.sf.jdptool.config.ModuleConfig;
import net.sf.jdptool.utils.Text;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.jdi.request.VMDeathRequest;

public class EventDispatchRunner implements Runner, EventDispatch, Constants {

    private static Log log = LogFactory.getLog(EventDispatchRunner.class);

    private VirtualMachine vm;

    private ModuleConfig config;

    private FilterConfig filterConfig;

    private final EventDispatch handler;

    private Recorder recorder;

    private String[] excludes = defaultExcludes;

    private boolean connected = true; // Connected to VM

    private Map<ThreadReference, Stack> entryTimes;

    /**
     * Construct a EventDispatchRunner instance
     */
    public EventDispatchRunner() {
        this.handler = this;
    }

    /**
     * Add the necessary request into <code>EventRequestManager</code>
     */
    private void initRequest() {
        log.debug("Begin to initialize event request .....");
        EventRequestManager eqmr = vm.eventRequestManager();

        // Set VMDeathRequest
        VMDeathRequest vmdReq = eqmr.createVMDeathRequest();
        vmdReq.enable();

        // Set exception request and suspend all, so we can step it
        ExceptionRequest exReq = eqmr.createExceptionRequest(null, true, true);
        for (int i = 0; i < excludes.length; i++) {
            exReq.addClassExclusionFilter(excludes[i]);
        }
        exReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        exReq.enable();

        // Set Thread start request
        ThreadStartRequest tsReq = eqmr.createThreadStartRequest();
        tsReq.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        tsReq.enable();

        // Set Thread death request
        ThreadDeathRequest tdReq = eqmr.createThreadDeathRequest();
        tdReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        tdReq.enable();

        // Set ClassPrepare request, so we can set watchpoint and step
        // request if needed
        ClassPrepareRequest cpReq = eqmr.createClassPrepareRequest();
        for (int i = 0; i < excludes.length; i++) {
            cpReq.addClassExclusionFilter(excludes[i]);
        }
        cpReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        cpReq.enable();

        // Set ClassPrepare request, so we can set watchpoint and step
        // request if needed
        ClassUnloadRequest cuReq = eqmr.createClassUnloadRequest();
        for (int i = 0; i < excludes.length; i++) {
            cuReq.addClassExclusionFilter(excludes[i]);
        }
        cuReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        cuReq.enable();

        // Want all method entry request, suspend it so we can capture
        // as more as runtime information
        MethodEntryRequest menReq = eqmr.createMethodEntryRequest();
        for (int i = 0; i < excludes.length; ++i) {
            menReq.addClassExclusionFilter(excludes[i]);
        }
        menReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        menReq.enable();

        // Want all method exit request, suspend it so we can capture
        // as more as runtime information
        MethodExitRequest mexReq = eqmr.createMethodExitRequest();
        for (int i = 0; i < excludes.length; ++i) {
            mexReq.addClassExclusionFilter(excludes[i]);
        }
        mexReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        mexReq.enable();

        log.debug("End event request initialization");
    }

    /**
     * Add excludes package
     */
    private void initExcludes() {
        String exStr = null;
        for (int i = 0; i < config.paramterSize(); i++) {
            if ("excludes".equals(config.getParameter(i).getName())) {
                exStr = config.getParameter(i).getValue();
                break;
            }
        }
        if (exStr != null && exStr.length() > 0) {
            excludes = exStr.split(";");
        }
    }

    /**
     * Initialize fitler configuration
     */
    private void initFilter() {
        String filterXml = null;
        for (int i = 0; i < config.paramterSize(); i++) {
            if ("filter".equals(config.getParameter(i).getName())) {
                filterXml = Text.replace(System.getProperties(),
                        config.getParameter(i).getValue(),
                        true);
                break;
            }
        }
        if (filterXml != null) {
            DigesterHelper helper = new DigesterHelper();
            helper.addRuleSet(new FilterRuleSet());
            try {
                filterConfig = (FilterConfig) helper.parse(new File(filterXml));
            } catch (Exception e) {
                throw new RuntimeException("Can't load filter file " +
                        filterXml, e);
            }
        } else {
            throw new RuntimeException("Required <filter> parameter is missing");
        }

    }

    /**
     * Dispatch incoming events
     *
     * @param event
     */
    private void handle(Event event) {
        if (event instanceof ExceptionEvent) {
            handler.exceptionEvent((ExceptionEvent) event);
        } else if (event instanceof BreakpointEvent) {
            handler.breakpointEvent((BreakpointEvent) event);
        } else if (event instanceof ModificationWatchpointEvent) {
            handler.fieldWatchEvent((WatchpointEvent) event);
        } else if (event instanceof AccessWatchpointEvent) {
            handler.fieldWatchEvent((WatchpointEvent) event);
        } else if (event instanceof MethodEntryEvent) {
            handler.methodEntryEvent((MethodEntryEvent) event);
        } else if (event instanceof MethodExitEvent) {
            handler.methodExitEvent((MethodExitEvent) event);
        } else if (event instanceof StepEvent) {
            handler.stepEvent((StepEvent) event);
        } else if (event instanceof ThreadStartEvent) {
            handler.threadStartEvent((ThreadStartEvent) event);
        } else if (event instanceof ThreadDeathEvent) {
            handler.threadDeathEvent((ThreadDeathEvent) event);
        } else if (event instanceof ClassPrepareEvent) {
            handler.classPrepareEvent((ClassPrepareEvent) event);
        } else if (event instanceof ClassUnloadEvent) {
            handler.classUnloadEvent((ClassUnloadEvent) event);
        } else if (event instanceof VMStartEvent) {
            handler.vmStartEvent((VMStartEvent) event);
        } else if (event instanceof VMDeathEvent) {
            handler.vmDeathEvent((VMDeathEvent) event);
        } else if (event instanceof VMDisconnectEvent) {
            handler.vmDisconnectEvent((VMDisconnectEvent) event);
        } else if (event instanceof VMDisconnectEvent) {
            handler.vmDisconnectEvent((VMDisconnectEvent) event);
        } else {
            throw new Error("Unexpected event type");
        }

    }

    /**
     * Get the method entry stack
     *
     * @param thread
     * @return
     */
    private Stack getEntryTimes(ThreadReference thread) {
        if (entryTimes == null) {
            entryTimes = new HashMap<ThreadReference, Stack>();
        }

        if (entryTimes.get(thread) == null) {
            Stack entryStack = new Stack();
            entryTimes.put(thread, entryStack);

            return entryStack;
        } else {
            return entryTimes.get(thread);
        }
    }

    /**
     * A VMDisconnectedException has happened while dealing with another event.
     * We need to flush the event queue, dealing only with exit events (VMDeath,
     * VMDisconnect) so that we terminate correctly.
     */
    private synchronized void handleDisconnect() {
        EventQueue queue = vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator iter = eventSet.eventIterator();
                while (iter.hasNext()) {
                    Event event = iter.nextEvent();
                    if (event instanceof VMDeathEvent) {
                        handler.vmDeathEvent((VMDeathEvent) event);
                    } else if (event instanceof VMDisconnectEvent) {
                        handler.vmDisconnectEvent((VMDisconnectEvent) event);
                    }
                }
                eventSet.resume(); // Resume the VM
            } catch (InterruptedException exc) {
                log.error("Handle disconnected exception error", exc);
            }
        }
    }

    /**
     * Return operating type to field
     *
     * @param field
     * @return operating type to field
     */
    private String filterField(String className, Field field) {

        List<BasicConfig> fields = filterConfig.getFields();
        for (int i = 0; i < fields.size(); i++) {
            BasicConfig config = fields.get(i);
            if (StringUtils.equals(config.getProperty("className"), className)
                    && StringUtils.equals(config.getProperty("fieldName"), field.name())) {
                return config.getProperty("watchType");
            }
        }

        return null;
    }

    /**
     * Check whether class name match the excludes's regular
     * expression
     *
     * @param className
     * @return
     */
    private boolean inExcludes(String className) {
        for (int i = 0; i < excludes.length; i++) {
            if (Pattern.matches(excludes[i], className)) {
                return true;
            }
        }
        return false;
    }

    private int getFrameCount(ThreadReference thread) {
        int frameCount = 0;
        try {
            frameCount = thread.frameCount();
        } catch (IncompatibleThreadStateException e) {
            log.error("Get frame count error", e);
        }

        return frameCount;
    }

    //---------------------------------- Implements methods for Runner

    /**
     *
     */
    public void destory() {
        // TODO Auto-generated method stub

    }

    public void init(VirtualMachine vm, ModuleConfig config) {
        this.vm = vm;
        this.config = config;

        initFilter();
        initExcludes();
        initRequest();

    }

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }

    //---------------------------------- Implements methods for Runnable

    /**
     * Run the event handling thread. As long as we are connected, get event
     * sets off the queue and dispatch the events within them.
     */
    public void run() {
        log.debug("EventDispatchRunner start");
        EventQueue queue = vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator it = eventSet.eventIterator();
                while (it.hasNext()) {
                    handle(it.nextEvent());
                }
                eventSet.resume();
            } catch (InterruptedException exc) {
                log.error("EventDispatch running error", exc);
            } catch (VMDisconnectedException discExc) {
                handleDisconnect();
                break;
            }
        }
    }

    //---------------------------------- Implements methods for EventDispatch

    /**
     * Handle breakpoint event
     *
     * @param event
     */
    public void breakpointEvent(BreakpointEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("Breakpoint at location[");
        sb.append(event.location() + "];");

        try {
            // Get the current StackFrame, index is zero.
            StackFrame stack = event.thread().frame(0);
            List<LocalVariable> variables = stack.visibleVariables();
            sb.append("Local variable[");
            for (int i = 0; i < variables.size(); i++) {
                sb.append(variables.get(i).name() + "=");
                sb.append(stack.getValue(variables.get(i)) + ";");
            }
            sb.append("]");
        } catch (Exception e) {
            log.error("Handle breakpoint event error", e);
        }

        EventRequestManager mgr = vm.eventRequestManager();
        StepRequest request = mgr.createStepRequest(event.thread(),
                StepRequest.STEP_LINE,
                StepRequest.STEP_OVER);
        request.addCountFilter(1);
        request.enable();

        recorder.write(event.thread().name(), sb.toString(),
                getFrameCount(event.thread()));

    }

    /**
     * Handle class prepare event. When A new class has been loaded. Set
     * watchpoints, breakpionts by Jdptool filter config file.
     *
     * @param event
     */
    public void classPrepareEvent(ClassPrepareEvent event) {
        recorder.write("JVM", "Class " +
                event.referenceType().name() + " have been loaded");
        String className = event.referenceType().name();
        if (inExcludes(className)) {
            return;
        }

        EventRequestManager mgr = vm.eventRequestManager();

        // Create Access/Modification field watchpoints by filter config
        List<Field> fields = event.referenceType().allFields();
        for (Iterator it = fields.iterator(); it.hasNext(); ) {
            Field field = (Field) it.next();
            String type = filterField(className, field);
            if (type == null) {
                continue;
            } else if ("modify".equals(type)) {
                ModificationWatchpointRequest req =
                        mgr.createModificationWatchpointRequest(field);
                for (int i = 0; i < excludes.length; ++i) {
                    req.addClassExclusionFilter(excludes[i]);
                }
                req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                req.enable();
            } else if ("access".equals(type)) {
                AccessWatchpointRequest req = mgr.createAccessWatchpointRequest(field);
                for (int i = 0; i < excludes.length; i++) {
                    req.addClassExclusionFilter(excludes[i]);
                }
                req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                req.enable();
            } else if ("both".equals(type)) {
                ModificationWatchpointRequest mreq =
                        mgr.createModificationWatchpointRequest(field);
                for (int i = 0; i < excludes.length; ++i) {
                    mreq.addClassExclusionFilter(excludes[i]);
                }
                mreq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                mreq.enable();
                AccessWatchpointRequest areq = mgr.createAccessWatchpointRequest(field);
                for (int i = 0; i < excludes.length; i++) {
                    areq.addClassExclusionFilter(excludes[i]);
                }
                areq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                areq.enable();
            }
        }

        // Create breakpoint by filter config
        BreaksConfig breaks = filterConfig.getBreaks();
        for (int i = 0; i < breaks.lineSize(); i++) {
            if (StringUtils.equals(className,
                    breaks.getLine(i).getProperty("className"))) {
                int lineNumber = Integer.valueOf(breaks.getLine(i).getProperty("line"));
                try {
                    List<Location> locations = event.referenceType().locationsOfLine(lineNumber);
                    for (Iterator it = locations.iterator(); it.hasNext(); ) {
                        Location current = (Location) it.next();
                        BreakpointRequest bpReq = mgr.createBreakpointRequest(current);
                        bpReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                        bpReq.enable();
                    }
                } catch (AbsentInformationException e) {
                    log.error("Class Prepared error", e);
                }
            }
        }
    }

    /**
     * Handle class unload event
     *
     * @param event
     */
    public void classUnloadEvent(ClassUnloadEvent event) {
        recorder.write("JVM", event.className() + " have been unloaded");
    }

    /**
     * Handle exception event
     *
     * @param event
     */
    public void exceptionEvent(ExceptionEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("location[");
        sb.append(event.location() + "];");
        sb.append("exception at [");
        sb.append(event.catchLocation() + "];");

        recorder.write(event.thread().name(), sb.toString(),
                getFrameCount(event.thread()) + 1);
    }

    /**
     * Handle field watch event
     *
     * @param event
     */
    public void fieldWatchEvent(WatchpointEvent event) {
        StringBuffer sb = new StringBuffer(event.location().toString());
        sb.append(":" + event.field().name());
        sb.append("[current]=" + event.valueCurrent());

        if (event instanceof ModificationWatchpointEvent) {
            sb.append("; [toBe]=");
            sb.append(((ModificationWatchpointEvent) event).valueToBe());
        }

        recorder.write(event.thread().name(), sb.toString(),
                getFrameCount(event.thread()) + 1);
    }

    /**
     * Handle method entry event
     *
     * @param event
     */
    public void methodEntryEvent(MethodEntryEvent event) {
        StringBuffer sb = new StringBuffer(event.method().name());
        sb.append(" method entry.");
        Stack stack = getEntryTimes(event.thread());
        stack.push(new Long(new Date().getTime()));

        recorder.write(event.thread().name(), sb.toString(),
                getFrameCount(event.thread()));
    }

    /**
     * Handle method exit event
     *
     * @param event
     */
    public void methodExitEvent(MethodExitEvent event) {
        StringBuffer sb = new StringBuffer(event.method().name());
        sb.append(" method exit. elapse ");
        long elapse = -1;
        Stack stack = getEntryTimes(event.thread());
        Long begin = (Long) stack.pop();
        if (begin != null) {
            elapse = new Date().getTime() - begin.longValue();
        }
        sb.append(elapse + "ms.");

        recorder.write(event.thread().name(), sb.toString(),
                getFrameCount(event.thread()));
    }

    /**
     * Handle step event
     *
     * @param event
     */
    public void stepEvent(StepEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("Step at location[");
        sb.append(event.location() + "];");

        try {
            // Get the current StackFrame, index is zero.
            StackFrame stack = event.thread().frame(0);
            List<LocalVariable> variables = stack.visibleVariables();
            sb.append("Local variable[");
            for (int i = 0; i < variables.size(); i++) {
                sb.append(variables.get(i).name() + "=");
                sb.append(stack.getValue(variables.get(i)) + ";");
            }
            sb.append("]");
        } catch (Exception e) {
            log.error("Handle step event error", e);
        }

        EventRequestManager mgr = vm.eventRequestManager();
        mgr.deleteEventRequest(event.request());

        recorder.write(event.thread().name(), sb.toString(),
                getFrameCount(event.thread()) + 1);

    }

    /**
     * Handle thread death event
     *
     * @param event
     */
    public void threadDeathEvent(ThreadDeathEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("Thread ");
        sb.append(event.thread().name());
        sb.append(" is dead.");

        recorder.write(event.thread().name(), sb.toString());
    }

    /**
     * Handle thread start event
     *
     * @param event
     */
    public void threadStartEvent(ThreadStartEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("Thread ");
        sb.append(event.thread().name());
        sb.append(" have been started.");

        recorder.write(event.thread().name(), sb.toString());
    }

    /**
     * Handle virtual machine death event
     *
     * @param event
     */
    public void vmDeathEvent(VMDeathEvent event) {
        Jdptool.vmRunning = false;

        StringBuffer sb = new StringBuffer();
        sb.append("The application exited ");
        sb.append(event.toString());

        recorder.write("JVM", sb.toString());

    }

    /**
     * Handle virtual machine disconnect event
     *
     * @param event
     */
    public void vmDisconnectEvent(VMDisconnectEvent event) {
        connected = false;
        if (Jdptool.vmRunning) {
            StringBuffer sb = new StringBuffer();
            sb.append("The application has been disconnected");
            sb.append(event.toString());

            recorder.write("JVM", sb.toString());
        }
    }

    /**
     * Handle virtual machine start event
     *
     * @param event
     */
    public void vmStartEvent(VMStartEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("VM Started ");
        sb.append(event.toString());

        recorder.write("JVM", sb.toString());
    }

}
