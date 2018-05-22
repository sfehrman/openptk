/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This file is available and licensed under the following license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 
 * 
 * Neither the name of Sun Microsystems nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openspml.v2.util;

import org.openspml.v2.transport.RPCRouter;
import org.openspml.v2.transport.RPCRouterMonitor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Jan 26, 2006
 */
public class SwingRPCRouterMonitor extends JFrame implements RPCRouterMonitor {

    private static final String code_id = "$Id: SwingRPCRouterMonitor.java,v 1.3 2006/08/30 18:02:59 kas Exp $";

    private RPCRouter[] _listeners;

    private JButton _clear;
    private JTextArea _trace;

    public static void println(String msg) {
        System.out.println(msg);
    }

    public SwingRPCRouterMonitor() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            initComponents();
            this.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addListener(RPCRouter l) {
        if (_listeners == null) {
            _listeners = new RPCRouter[1];
        }
        else {
            List temp = new ArrayList(Arrays.asList(_listeners));
            temp.add(l);
            _listeners = (RPCRouter[])
                    temp.toArray(new RPCRouter[temp.size()]);
        }
    }

    public void removeListener(RPCRouter l) {
        if (_listeners != null) {
            List temp = new ArrayList(Arrays.asList(_listeners));
            temp.remove(l);
            _listeners = (RPCRouter[])
                    temp.toArray(new RPCRouter[temp.size()]);
            // call this?
            l.closed(this);
        }
    }

    private void initComponents() throws Exception {

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(400, 600));

        URL imageURL = getClass().getResource("openSpml.gif");
        if (imageURL != null) {
            ImageIcon spmlIcon = new ImageIcon(imageURL);
            setIconImage(spmlIcon.getImage());
        }

        setSize(new Dimension(1000, 700));
        String shortName = getClass().getName();
        shortName = shortName.substring(shortName.lastIndexOf(".") + 1);
        setTitle("OpenSPML 2.0 - " + shortName);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // File menu

        JMenu menu = new JMenu();
        menuBar.add(menu);
        menu.setText("File");

        // File->Exit
        JMenuItem item = new JMenuItem();
        menu.add(item);
        item.setText("Exit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        _clear = new JButton("Clear");
        buttons.add(_clear);

        _clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _trace.setText(null);
            }
        });

        contentPane.add(buttons, BorderLayout.SOUTH);

        _trace = new JTextArea();
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(_trace, null);
        contentPane.add(sp, BorderLayout.CENTER);
    }

    private void informListenersOfClose() {

        if (_listeners != null) {
            for (int i = 0; i < _listeners.length; i++) {
                RPCRouter l = _listeners[i];
                if (l != null)
                    l.closed(this);
            }
            // can we remove these?
            _listeners = null;
        }

    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            // for some reason don't get into dispose when X'ing?
            informListenersOfClose();
        }
    }

    //////////////////////////////////////////////////////////////////////
    //
    // SOAPMonitor
    //
    //////////////////////////////////////////////////////////////////////

    public void send(String payload) {
        if (_trace == null)
            return;
        _trace.append("\n------------------------------SEND------------------------------\n");
        _trace.append(payload);
    }

    public void receive(String payload) {

        if (_trace == null)
            return;

        _trace.append("\n-----------------------------RECIEVE----------------------------\n");
        _trace.append(payload);
    }

    public void trace(String msg) {
        if (_trace == null)
            return;
        _trace.append("TRACE: ");
        _trace.append(msg);
        _trace.append("\n");
    }

}
