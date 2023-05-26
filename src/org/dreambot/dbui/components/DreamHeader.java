package org.dreambot.dbui.components;

import org.dreambot.dbui.util.VisualTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class DreamHeader extends DreamPanel {

    private String title;
    private BufferedImage img;
    private DreamLabel logoContainer;
    private DreamLabel titleContainer;
    private DreamPanel left, right;
    private DreamButton closeBtn;
    private DreamButton minBtn;
    private Point initialClick;
    private JFrame frame;

    public DreamHeader(JFrame frame, String title) {
        this(frame, title, null);
    }

    public DreamHeader(JFrame frame, String title, BufferedImage img) {
        this.frame = frame;
        add(left = new DreamPanel(new FlowLayout(FlowLayout.LEFT)), BorderLayout.WEST);
        add(right = new DreamPanel(new FlowLayout(FlowLayout.RIGHT)), BorderLayout.EAST);
        setBorder(new EmptyBorder(3,3,3,3));
        this.title = title;
        this.img = img;
        if (this.img != null) {
            this.img = VisualTools.resize(img, 20, 20, true);
            System.out.println("Adding logo");
            left.add(logoContainer = new DreamLabel());
            logoContainer.setIcon(new ImageIcon(this.img));
        }
        left.add(titleContainer = new DreamLabel(this.title));
        titleContainer.setBorder(new EmptyBorder(0,2,0,2));
        right.add(minBtn = new DreamButton("\uD83D\uDDD5"));
        right.add(closeBtn = new DreamButton("\u274C"));
        closeBtn.addActionListener(l -> System.exit(0));
        minBtn.addActionListener(l -> frame.setState(JFrame.ICONIFIED));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                frame.setLocation(X, Y);
            }
        });
    }
}