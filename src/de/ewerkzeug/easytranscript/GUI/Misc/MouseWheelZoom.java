/**
 *
 *
 * easytranscript Copyright (C) 2013 Elias John
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der
 * GNU General Public License, wie von der Free Software Foundation
 * veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3
 * der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die
 * Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von
 * Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite
 * Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.
 * Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.GUI.Misc;

import static de.ewerkzeug.easytranscript.Core.Easytranscript.MainCenterEditorScrollpane;
import static de.ewerkzeug.easytranscript.Core.Easytranscript.MainToolbarZoomCombobox;
import static de.ewerkzeug.easytranscript.Core.V.easytranscript;
import static de.ewerkzeug.easytranscript.Core.V.prop;
import static de.ewerkzeug.easytranscript.Core.V.zoomFactor;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Zoom mit STRG+Mausrad
 */
public class MouseWheelZoom implements MouseWheelListener {

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.isControlDown()) {
            if (!prop.getBoolProperty("usePerformanceMode")) {
                if (e.getWheelRotation() < 0) {

                    if (MainToolbarZoomCombobox.getSelectedIndex() + 1 < MainToolbarZoomCombobox.getItemCount()) {
                        MainToolbarZoomCombobox.setSelectedIndex(MainToolbarZoomCombobox.getSelectedIndex() + 1);
                    }
                    zoomFactor = Double.parseDouble((MainToolbarZoomCombobox.getSelectedItem().toString().substring(0, 3))) / 100;
                    easytranscript.getMainCenterEditorEditorPane().getDocument().putProperty("ZOOM_FACTOR", new Double(zoomFactor)); //!!
                    easytranscript.getMainCenterEditorEditorPane().requestFocus();

                } else {

                    if (MainToolbarZoomCombobox.getSelectedIndex() - 1 > -1) {
                        MainToolbarZoomCombobox.setSelectedIndex(MainToolbarZoomCombobox.getSelectedIndex() - 1);
                    }
                    zoomFactor = Double.parseDouble((MainToolbarZoomCombobox.getSelectedItem().toString().substring(0, 3))) / 100;
                    easytranscript.getMainCenterEditorEditorPane().getDocument().putProperty("ZOOM_FACTOR", new Double(zoomFactor)); //!!
                    easytranscript.getMainCenterEditorEditorPane().requestFocus();
                }
            }

        } else {
            if (e.getWheelRotation() > 0) {

                MainCenterEditorScrollpane.getVerticalScrollBar().setValue(MainCenterEditorScrollpane.getVerticalScrollBar().getValue() + (int) (zoomFactor * 10));
            } else {
                MainCenterEditorScrollpane.getVerticalScrollBar().setValue(MainCenterEditorScrollpane.getVerticalScrollBar().getValue() - (int) (zoomFactor * 10));
            }
        }
    }

}
