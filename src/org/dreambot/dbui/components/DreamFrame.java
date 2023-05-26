package org.dreambot.dbui.components;


import org.dreambot.dbui.UIColours;
import org.dreambot.dbui.util.VisualTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class DreamFrame extends JFrame {



    public DreamFrame() {
        this("");
    }

    public DreamFrame(String title) {
        this(title,null);
    }

    public DreamFrame(String title, BufferedImage logo) {
        setLayout(new BorderLayout());
        setUndecorated(true);
        setTitle(title);
        setBackground(UIColours.BODY_COLOUR);
        getContentPane().setBackground(UIColours.BODY_COLOUR);
        DreamHeader header = new DreamHeader(this,title,logo);
        add(header, BorderLayout.PAGE_START);
    }

    @Override
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        setShape(new RoundRectangle2D.Double(0,0, width,height, 12,12));
    }


}

