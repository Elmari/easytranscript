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
package de.ewerkzeug.easytranscript.tools;

import de.ewerkzeug.easytranscript.core.Easytranscript;
import static de.ewerkzeug.easytranscript.core.Variables.Millisekunden_current;
import static de.ewerkzeug.easytranscript.core.Variables.MinutenC_string;
import static de.ewerkzeug.easytranscript.core.Variables.SekundenC_string;
import static de.ewerkzeug.easytranscript.core.Variables.StundenC_string;
import static de.ewerkzeug.easytranscript.core.Variables.currentPlayerTime;
import static de.ewerkzeug.easytranscript.core.Variables.easytranscript;
import de.ewerkzeug.easytranscript.gui.misc.UpdateTimer;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javax.swing.ImageIcon;

/**
 * Implementierung eines Ersatzes für VLCJ, welches mit Java 7 ab Mac OS X
 * 10.8 nicht mehr funktioniert. Dieser Ersatz beruht auf dem JavaFX
 * MediaPlayer, eingebunden über ein JFXPanel.
 * @see de.ewerkzeug.easytranscript.tools.Player
 * @since 2.21
 * @version 1.0
 */
public class PlayerFX {

    public static long mediaLength;
    private static boolean MediaLoaded = false;
    private static int spulintervall = 4000;

    private static Media media;
    private static MediaPlayer mediaPlayer;
    public static MediaView mediaViewFX;
    public static Scene scene;
   
    public static JFXPanel fxPanel;//; = new JFXPanel();
    public static BorderPane anchorPaneFX;// = new AnchorPane();

    public static ImageIcon start = new ImageIcon(Easytranscript.class.getResource("images/media-playback-start-8.png"));
    public static ImageIcon pause = new ImageIcon(Easytranscript.class.getResource("images/media-playback-pause-8.png"));

    public static JFXPanel getFXPanel() {
        return fxPanel;
    }

    public static BorderPane getAnchorPaneFX() {
        return anchorPaneFX;
    }

    public static MediaView getMediaViewFX() {
        return mediaViewFX;
    }
        public static void setMediaViewFX(MediaView mv) {
       mediaViewFX = mv;
       mediaViewFX.setPreserveRatio(true);
    }

    public static Media getMedia() {
        return media;
    }

    public static void setMedia(Media mediaFX) {
        PlayerFX.media = mediaFX;
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void setMediaPlayer(MediaPlayer mediaPlayerFX) {
        PlayerFX.mediaPlayer = mediaPlayerFX;
    }

    /**
     * Pausiert bzw. Spielt die Mediendatei ab und führt anschließend notwendige
     * Prozesse au, wie zB das automatische Rückspulen.
     *
     */
    public static void togglePaused() {

        if (getMediaPlayer().getStatus().equals(Status.PLAYING)) {
            getMediaPlayer().pause();

        } else {

            currentPlayerTime = currentPlayerTime - spulintervall;

            getMediaPlayer().seek(new Duration(currentPlayerTime));
            getMediaPlayer().play();

        }

    }

    /**
     * Spult Mediendatei um einen Wert.
     *
     * @param spulinterval
     */
    public static void jump(int spulinterval) {

        currentPlayerTime = currentPlayerTime + spulinterval;
        if (currentPlayerTime < 0) {
            currentPlayerTime = 0;
        }
        if (new Duration(currentPlayerTime).compareTo(getMediaPlayer().getTotalDuration()) > 0) {
            currentPlayerTime = (long) getMediaPlayer().getTotalDuration().toMillis();
        }

        getMediaPlayer().seek(new Duration(currentPlayerTime));
        new UpdateTimer().updateTimeStrings();
        easytranscript.getMainTimeLabel().setText(StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string);
        easytranscript.getMainTimeMilliLabel().setText("-" + Millisekunden_current);
        easytranscript.getMainSlider().setValue((int) getMediaPlayer().getCurrentTime().toMillis() / 1000);

    }

    /**
     * Liefert zurück, ob ein Medium geladen ist
     *
     * @return Medium geladen?
     */
    public static boolean isMediaLoaded() {
        return MediaLoaded;
    }

    /**
     * Setzt, ob ein Medium geladen ist
     *
     * @param MediaLoaded Medium geladen?
     */
    public static void setMediaLoaded(boolean MediaLoaded) {
        PlayerFX.MediaLoaded = MediaLoaded;
    }

    /**
     * Liefert das Spulintervall
     *
     * @return spulintervall
     */
    public static int getInterval() {
        return spulintervall;
    }

    /**
     * Setzt das Spulintervall
     *
     * @param spulintervall
     */
    public static void setInterval(int spulintervall) {
        PlayerFX.spulintervall = spulintervall;
    }

}
