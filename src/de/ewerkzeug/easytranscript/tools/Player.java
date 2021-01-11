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
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.sfull;
import de.ewerkzeug.easytranscript.gui.misc.UpdateTimer;
import de.ewerkzeug.easytranscript.IO.data.TranscriptHandler;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.mediaPath;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.transcriptName;
import java.awt.Panel;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Erweitert den VLCJ Player
 */
public class Player extends EmbeddedMediaPlayerComponent {

    private long mediaLength;
    private boolean MediaLoaded = false;
    private int spulintervall = 4000;

    private boolean buffering = false;

    private ImageIcon start = new ImageIcon(Easytranscript.class.getResource("Images/media-playback-start-8.png"));
    private ImageIcon pause = new ImageIcon(Easytranscript.class.getResource("Images/media-playback-pause-8.png"));

    /**
     * Fügt einen EventAdapter hinzu
     */
    public Player() {

        getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            @Override
            public void error(MediaPlayer mediaPlayer) {
                mediaPlayer.getMediaDetails();
                logger.log(Level.WARNING, "VLCJ Error: {0} (MediaPlayer currentState) - {1}", new Object[]{mediaPlayer.getMediaPlayerState(), mediaPlayer.getLength()});
                String s;
                if (LibVlc.INSTANCE.libvlc_errmsg() == null) {
                    s = "";
                } else {
                    s = LibVlc.INSTANCE.libvlc_errmsg();
                }
                logger.log(Level.WARNING, "VLCJ Error Message: {0}", s);
                
                easytranscript.getBufferingLabel().setText(" ");

            }

            /**
             * Wenn pausiert, setze Icon auf start
             *
             * @param mediaPlayer nicht benötigt
             */
            @Override
            public void paused(MediaPlayer mediaPlayer) {
                easytranscript.getMainTimePlayerplayButton().setIcon(start);

            }

            /**
             * Wenn gespielt wird, setze Icon auf pause, aktualisiere
             * Zeitanzeige
             *
             * @param mediaPlayer
             */
            @Override

            public void playing(MediaPlayer mediaPlayer) {
                easytranscript.getMainTimePlayerplayButton().setIcon(pause);
                
                int Stunden, Minuten, Sekunden, Millisekunden;

//                if (mediaPlayer.getLength() == 0) {
//
//                    logger.log(Level.WARNING, "Length = 0. Versuche erneut...");
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        java.util.logging.Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    if (isMediaLoaded() == true) {
//                        currentPlayerTime = currentPlayerTime - spulintervall;
//
//                        getMediaPlayer().setTime(currentPlayerTime);
//
//                        getMediaPlayer().setPause(true);
//                    }
//
//                }
                if (mediaLength == 0) {
                    mediaLength = mediaPlayer.getLength();

                    Stunden = (int) (mediaLength / 1000 / 60 / 60);
                    Minuten = (int) ((mediaLength - (Stunden * 1000 * 60 * 60)) / 1000 / 60);
                    Sekunden = (int) ((mediaLength - (Stunden * 1000 * 60 * 60) - (Minuten * 1000 * 60)) / 1000);
                    Millisekunden = (int) ((mediaLength - (Stunden * 1000 * 60 * 60) - (Minuten * 1000 * 60) - (Sekunden * 1000)) / 100);

                    sfull = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mediaLength),
                            TimeUnit.MILLISECONDS.toMinutes(mediaLength) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mediaLength)),
                            TimeUnit.MILLISECONDS.toSeconds(mediaLength) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaLength)));

                    easytranscript.getMainSlider().setMaximum((int) mediaLength / 1000);

                    currentPlayerTime = 0;
                    getMediaPlayer().setPause(true);
                    getMediaPlayer().setTime(0);
                    easytranscript.getMainTimeLabel().setText("00:00:00");
                    easytranscript.getMainTimeMilliLabel().setText("-0");

                    easytranscript.getMainTotalLabel().setText(sfull + "-" + Millisekunden);

                }

            }

            @Override
            public void buffering(MediaPlayer mediaPlayer, float newCache) {
               
                if (newCache < 100) {
                    buffering = true;
                    final float n = newCache;
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (mediaPath.startsWith("http")) {
                            easytranscript.getBufferingLabel().setText(messages.getString("Wait") + " " + (int) n + "%");
                            }
                        }
                    });
                } else {
                    buffering = false;
                    currentPlayerTime = easytranscript.getMainSlider().getValue() * 1000;
                    if (mediaPath.startsWith("http")) {
                        new UpdateTimer().updateTimeStrings();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                easytranscript.getBufferingLabel().setText(" ");
                            }
                        });

                    }
                }

            }

            @Override
            public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
                List<String> items = mediaPlayer.subItems();
                System.out.println(items);
            }

            /**
             * Wenn zuende gespielt wurde, stoppe und resette Variablen
             *
             * @param mediaPlayer nicht benötigt
             */
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                //  

                //     mediaPlayer.stop();
                mediaPlayer.setTime(0);
                mediaLength = 0;
                easytranscript.getMainTimeLabel().setText("00:00:00");
                easytranscript.getMainTimeMilliLabel().setText("-0");
                easytranscript.getMainSlider().setValue(0);
                currentPlayerTime = 0;
                easytranscript.getMainTimePlayerplayButton().setIcon(start);
                String url;
                if (!mediaPath.startsWith("http")) {
                    url = new File(mediaPath).toURI().toASCIIString();
                    url = url.replaceFirst("file:/", "file:///");

                    mediaPlayer.playMedia(url);

                } else {

                    mediaPlayer.play();
                }
                mediaPlayer.setPlaySubItems(true);
                mediaPlayer.setPause(true);

            }

            /**
             * Wenn sich die Position verändert hat, setze den Titel des
             * Hauptfensters neu
             *
             * @param mediaPlayer nicht benötigt
             * @param newPosition nicht benötigt
             */
            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                String stern = "";
                if (TranscriptHandler.isUnsaved()) {
                    stern = "*";
                   
                }
                getFrame().setTitle("easytranscript - " + transcriptName + stern + " - " + easytranscript.getMainTimeLabel().getText() + " " + messages.getString("vonTime") + " " + sfull);

            }

            /**
             * Wenn sich das Medium verändert hat, resette den Titel
             *
             * @param mediaPlayer nicht benötigt
             * @param media nicht benötigt
             * @param mrl nicht benötigt
             */
            @Override
            public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
                getFrame().setTitle("easytranscript - " + transcriptName);

                mediaLength = 0;
            }

        });

    }

    /**
     * Liefert den Mainframe
     *
     * @return JFrame
     */
    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor((Panel) this);
    }

    //======
    /**
     * Pausiert bzw. Spielt die Mediendatei ab und führt anschließend notwendige
     * Prozesse au, wie zB das automatische Rückspulen.
     *
     */
    public void togglePaused() {

        if (getMediaPlayer().isPlaying()) {
            getMediaPlayer().setPause(true);

        } else {

            currentPlayerTime = currentPlayerTime - spulintervall;

            getMediaPlayer().setTime(currentPlayerTime);

            getMediaPlayer().setPause(false);

        }

    }

    /**
     * Spult Mediendatei um einen Wert.
     *
     * @param spulinterval
     */
    public void jump(int spulinterval) {

        currentPlayerTime = currentPlayerTime + spulinterval;
        if (currentPlayerTime < 0) {
            currentPlayerTime = 0;
        }
        if (currentPlayerTime > getMediaPlayer().getLength()) {
            currentPlayerTime = getMediaPlayer().getLength();
        }

        getMediaPlayer().setTime(currentPlayerTime);
        new UpdateTimer().updateTimeStrings();
        easytranscript.getMainTimeLabel().setText(StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string);
        easytranscript.getMainTimeMilliLabel().setText("-" + Millisekunden_current);
        easytranscript.getMainSlider().setValue((int) getMediaPlayer().getTime() / 1000);

    }

    /**
     * Liefert zurück, ob ein Medium geladen ist
     *
     * @return Medium geladen?
     */
    public boolean isMediaLoaded() {
        return MediaLoaded;
    }

    /**
     * Setzt, ob ein Medium geladen ist
     *
     * @param MediaLoaded Medium geladen?
     */
    public void setMediaLoaded(boolean MediaLoaded) {
        this.MediaLoaded = MediaLoaded;
    }

    /**
     * Liefert das Spulintervall
     *
     * @return spulintervall
     */
    public int getInterval() {
        return spulintervall;
    }

    public boolean isBuffering() {
        return buffering;
    }

    /**
     * Setzt das Spulintervall
     *
     * @param spulintervall
     */
    public void setInterval(int spulintervall) {
        this.spulintervall = spulintervall;
    }

}
