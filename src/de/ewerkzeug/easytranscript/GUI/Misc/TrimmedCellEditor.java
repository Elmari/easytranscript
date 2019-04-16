/*
 * Copyright (C) 2014 e-werkzeug <administrator@e-werkzeug.eu>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.ewerkzeug.easytranscript.GUI.Misc;

import static de.ewerkzeug.easytranscript.Core.V.messages;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class TrimmedCellEditor extends DefaultCellEditor {

    public TrimmedCellEditor() {
        super(new JTextField());
    }

    @Override
    public boolean stopCellEditing() {
        JTable table = (JTable) getComponent().getParent();

        try {
            String editingValue = (String) getCellEditorValue();

            if (editingValue.contains(" ") || editingValue.contains("\t")) {
                JTextField textField = (JTextField) getComponent();
                textField.setBorder(new LineBorder(Color.red));
                textField.selectAll();
                textField.requestFocusInWindow();

                JOptionPane.showMessageDialog(
                        null,
                        messages.getString("ConfigStenoShortcutError"),
                        messages.getString("ConfigStenoShortcutErrorTitle"), JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int duplikat = 0;
            if (!editingValue.equals("")) {
                for (int i = 0; i < table.getModel().getRowCount(); i++) {
                    if (table.getModel().getValueAt(i, 0) != null) {
                        if (table.getModel().getValueAt(i, 0).equals(editingValue)) {
                            if (table.getSelectedRow() != i) {
                                duplikat++;

                                if (duplikat == 1) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (duplikat == 1) {
                    JTextField textField = (JTextField) getComponent();
                    textField.setBorder(new LineBorder(Color.red));
                    textField.selectAll();
                    textField.requestFocusInWindow();

                    JOptionPane.showMessageDialog(
                            null,
                            "Es sind keine Duplikate erlaubt.",
                            messages.getString("ConfigStenoShortcutErrorTitle"), JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (ClassCastException exception) {
            return false;
        }

        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        Component c = super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
        ((JComponent) c).setBorder(new LineBorder(Color.black));

        return c;
    }
}
