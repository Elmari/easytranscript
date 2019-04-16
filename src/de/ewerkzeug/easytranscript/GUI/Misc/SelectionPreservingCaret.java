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

package de.ewerkzeug.easytranscript.GUI.Misc;

import java.awt.event.FocusEvent;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;


  
    /**
     * Caret implementation that doesn't blow away the selection when we lose
     * focus.
     */
   public class SelectionPreservingCaret extends DefaultCaret {

        /**
         * The last SelectionPreservingCaret that lost focus
         */
        private SelectionPreservingCaret last = null;
        /**
         * The last event that indicated loss of focus
         */
        private FocusEvent lastFocusEvent = null;

        public SelectionPreservingCaret() {
            // The blink rate is set by BasicTextUI when the text component
            // is created, and is not (re-) set when a new Caret is installed.
            // This implementation attempts to pull a value from the UIManager,
            // and defaults to a 500ms blink rate. This assumes that the
            // look and feel uses the same blink rate for all text components
            // (and hence we just pull the value for TextArea). If you are
            // using a look and feel for which this is not the case, you may
            // need to set the blink rate after creating the Caret.
            int blinkRate = 500;
            Object o = UIManager.get("TextArea.caretBlinkRate");
            if ((o != null) && (o instanceof Integer)) {
                Integer rate = (Integer) o;
                blinkRate = rate;
            }
            setBlinkRate(blinkRate);
        }

        /**
         * Called when the component containing the caret gains focus.
         * DefaultCaret does most of the work, while the subclass checks to see
         * if another instance of SelectionPreservingCaret previously had focus.
         *
     * @param evt
         * @see java.awt.event.FocusListener#focusGained
         */
        @Override
        public void focusGained(FocusEvent evt) {
            super.focusGained(evt);

            // If another instance of SelectionPreservingCaret had focus and
            // we defered a focusLost event, deliver that event now.
            if ((last != null) && (last != this)) {
                last.hide();
            }
        }

        /**
         * Called when the component containing the caret loses focus. Instead
         * of hiding both the caret and the selection, the subclass only hides
         * the caret and saves a (static) reference to the event and this
         * specific caret instance so that the event can be delivered later if
         * appropriate.
         *
     * @param evt
         * @see java.awt.event.FocusListener#focusLost
         */
        @Override
        public void focusLost(FocusEvent evt) {
            setVisible(false);
            last = this;
            lastFocusEvent = evt;
        }

        /**
         * Delivers a defered focusLost event to this caret.
         */
        protected void hide() {
            if (last == this) {
                super.focusLost(lastFocusEvent);
                last = null;
                lastFocusEvent = null;
            }
        }
    }

