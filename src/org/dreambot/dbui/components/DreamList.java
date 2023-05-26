package org.dreambot.dbui.components;



import org.dreambot.dbui.UIColours;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DreamList<T> extends JList<T> {

    DefaultListModel<T> model;

    public DreamList() {
        model = new DefaultListModel<>();
        setModel(model);
        setBackground(UIColours.LIST_COLOR);
        setLayoutOrientation(VERTICAL);
        setBorder(new CompoundBorder(new LineBorder(UIColours.LIST_COLOR.brighter()), new EmptyBorder(2,2,2,2)));
        setForeground(UIColours.TEXT_COLOR);
        setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            Component l = (Component) value;
            if (isSelected || cellHasFocus) {
                l.setBackground(UIColours.BUTTON_COLOUR.brighter());
            } else {
                l.setBackground(UIColours.LIST_COLOR);
            }
            return l;
        });
    }

    public void add(T element) {
        model.addElement(element);
    }

}
