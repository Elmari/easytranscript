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
package de.ewerkzeug.easytranscript.gui.components;

import de.ewerkzeug.easytranscript.core.Easytranscript;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.transcriptName;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 * SwingWorker. Druckt das Transkript
 */
public class printDocument extends SwingWorker<String, Void> {

    JTable component;
    JEditorPane component2;
    String title, subtitle;
    JFrame frame = new JFrame("Warte auf Drucker");

    /**
     * Überliefert eine JTable und erstellt den Frame
     *
     * @param component
     * @param title
     * @param subtitle
     */
    public printDocument(JTable component, String title, String subtitle) {
        this.component = component;
        createFrame();
        this.title = title;
        this.subtitle = subtitle;
    }

    /**
     * Überliefert ein JEditorPane und erstellt den Frame
     *
     * @param component
     * @param title
     * @param subtitle
     */
    public printDocument(JEditorPane component, String title, String subtitle) {
        this.component2 = component;
        createFrame();
        this.title = title;
        this.subtitle = subtitle;
    }

    /**
     * Erstellt das Bitte warten Fenster
     */
    private void createFrame() {

        JLabel label = new JLabel(messages.getString("Drucker1"));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font("Tahoma", Font.BOLD, 20));
        JLabel label2 = new JLabel(messages.getString("Drucker2"));
        label2.setHorizontalAlignment(JLabel.CENTER);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.getContentPane().add(label2, BorderLayout.SOUTH);
       // frame.getContentPane().setBackground(new Color(245, 245, 245));

        frame.pack();
        frame.setResizable(false);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                printDocument.this.cancel(true);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            if (component != null) {
                //      component.print();
                String pN = "";

                if (transcriptName != null) {
                    pN = "-" + transcriptName;
                }

                Thread.sleep(2000);
                component.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat(title), new MessageFormat(subtitle));
            } else {
                component2.print();
            }
            return "success";

        } catch (PrinterException ex) {
            Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
            return "fail";
        }
    }

    @Override
    protected void done() {
        frame.setVisible(false);

    }
}
