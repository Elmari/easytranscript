/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
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
package de.ewerkzeug.easytranscript.gui.misc;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import static de.ewerkzeug.easytranscript.core.Variables.fontsizeHasFocus;
import static de.ewerkzeug.easytranscript.core.Variables.fontfamilyHasFocus;

/**
 *
 */
public class FontFocusListener implements FocusListener {

    boolean isFontSize;

    public FontFocusListener(boolean fontSize) {
        this.isFontSize = fontSize;
    }

    @Override
    public void focusGained(FocusEvent arg0) {
        if (isFontSize) {
            fontsizeHasFocus = true;
        } else {
            fontfamilyHasFocus = true;
        }

    }

    @Override
    public void focusLost(FocusEvent arg0) {
        if (isFontSize) {
            fontsizeHasFocus = false;
        } else {
            fontfamilyHasFocus = false;
        }
    }
}
