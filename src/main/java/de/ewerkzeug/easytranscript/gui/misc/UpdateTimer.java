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

import static de.ewerkzeug.easytranscript.core.Variables.*;

/**
 * Updated die Zeit-Strings.
 */
public final class UpdateTimer implements Runnable {

    @Override
    public void run() {

        if (!useFXPlayer) {
            if (player.getMediaPlayer().isPlaying()) {
                if (currentPlayerTime < 0) {
                    currentPlayerTime = 0;
                }
                if (currentPlayerTime > player.getMediaPlayer().getLength()) {
                    currentPlayerTime = player.getMediaPlayer().getLength();
                }

                easytranscript.getMainSlider().setValue((int) player.getMediaPlayer().getTime() / 1000);
                currentPlayerTime = currentPlayerTime + millisecondsPerStep;

                updateTimeStrings();

                easytranscript.getMainTimeLabel().setText(StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string);

                easytranscript.getMainTimeMilliLabel().setText("-" + Millisekunden_current);

            }
        }
    }

    /**
     * Aktualisiert die Zeit-Anzeige.
     */
    public void updateTimeStrings() {
        if (!useFXPlayer) {
            if (player.isBuffering()) {
                return;
            }
        }
        Stunden_current = (int) (currentPlayerTime / 1000 / 60 / 60);
        Minuten_current = (int) ((currentPlayerTime - (Stunden_current * 1000 * 60 * 60)) / 1000 / 60);
        Sekunden_current = (int) ((currentPlayerTime - (Stunden_current * 1000 * 60 * 60) - (Minuten_current * 1000 * 60)) / 1000);
        Millisekunden_current = (int) ((currentPlayerTime - (Stunden_current * 1000 * 60 * 60) - (Minuten_current * 1000 * 60) - (Sekunden_current * 1000)) / 100);

        if (Stunden_current < 10) {
            StundenC_string = "0" + String.valueOf(Stunden_current);
        } else {
            StundenC_string = String.valueOf(Stunden_current);
        }
        if (Minuten_current < 10) {
            MinutenC_string = "0" + String.valueOf(Minuten_current);
        } else {
            MinutenC_string = String.valueOf(Minuten_current);
        }
        if (Sekunden_current < 10) {
            SekundenC_string = "0" + String.valueOf(Sekunden_current);
        } else {
            SekundenC_string = String.valueOf(Sekunden_current);
        }

    }

}
