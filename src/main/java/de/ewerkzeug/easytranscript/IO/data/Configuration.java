/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.IO.data;

import de.ewerkzeug.easytranscript.core.Easytranscript;
import de.ewerkzeug.easytranscript.core.ErrorReport;
import de.ewerkzeug.easytranscript.core.Variables;
import static de.ewerkzeug.easytranscript.core.Variables.configFrame;
import static de.ewerkzeug.easytranscript.core.Variables.currentLocale;
import static de.ewerkzeug.easytranscript.core.Variables.easytranscript;
import static de.ewerkzeug.easytranscript.core.Variables.errors;
import static de.ewerkzeug.easytranscript.core.Variables.installationDialog;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.opFolder;
import static de.ewerkzeug.easytranscript.core.Variables.tastenCheckFrame;
import static de.ewerkzeug.easytranscript.tools.Tools.getOS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

/**
 * Stellt die Anwendungseinstellungen da.
 */
public class Configuration extends Properties {

    /**
     * Lädt die Anwendungseinstellungen und wendet sie ggf gleich an Catched IOException, falls keine Konfigurationsdatei gefunden wurde und erstellt eine Default Konfigurationsdatei
     *
     * @param apply true, falls Einstellungen im Einstellungsfenster angewendet werden sollen. false, sonst.
     *
     */
    public void load(boolean apply) {
        try {
            load(new FileInputStream(opFolder + "conf/config.properties"));
            if (apply) {
                applyProperties();
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Keine Konfigurations-Datei gefunden. Setze auf Default-Werte.", ex);

            try {
                createDefaults();
            } catch (IOException e) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("SavingConfig")), ex);
                return;
            }

            installationDialog.setVisible(true);
            load(apply);
        }
    }

    /**
     * Speichert die Conf.Properties im Configurationsordner des Programmes
     *
     * @throws IOException, falls Fehler beim Speichern der Datei
     */
    public void save() throws IOException {

        FileOutputStream fos = new FileOutputStream(opFolder + "conf/config.properties");
        store(fos, null);
        fos.close();

    }

    /**
     * Erstellt die Default Property Datei, falls keine gefunden wurde.
     *
     * @throws IOException, falls Datei nicht gespeichert werden konnte.
     */
    public void createDefaults() throws IOException {

        new File("opFolder.cfg").delete();

        setProperty("updatechannel", "final");
        setProperty("usePerformanceMode","false");
        setProperty("useFXPlayer","false");
        setProperty("ConfigPlayerSystemWideCheckbox", "false");
        setProperty("ConfigStenoActivate", "false");
        setProperty("showStart", "false");
        setProperty("ZeitmarkenActive", "true");           // Boolean
        setProperty("ZMleerzeile", "true");                // Boolean
        setProperty("Sprecherwechsel", "false");            // Boolean
        setProperty("SprecherwechselP1", "Interviewer: "); // String
        setProperty("SprecherwechselP2", "Person: ");      // String
        setProperty("backupTimer", "300000");              // Integer
        setProperty("backupEnabled", "true");              // Boolean
        setProperty("TextbausteineFett", "false");         // Boolean
        setProperty("COSFett", "false");
        setProperty("zeitmarkeBeginning", "false");        // Boolean
        setProperty("zeitmarkeEnding", "false");
        setProperty("Tbs1", "Interviewer: ");                            // String
        setProperty("Tbs2", "Person 1: ");                            // String
        setProperty("Tbs3", "Person 2: ");                            // String
        setProperty("Tbs4", "Person 3: ");                            // String
        setProperty("Tbs5", "Person 4: ");                            // String
        setProperty("Tbs6", "Person 5: ");                            // String
        setProperty("Tbs7", "Person 6: ");                            // String
        setProperty("Tbs8", " (unv.) ");                            // String
        setProperty("Tbs9", " (lachen) ");                            // String
        setProperty("Tbs10", " (...) ");                           // String
        setProperty("PPinfoskipCb", "false");
        setProperty("updateAuto", "true");
        setProperty("maxPlayerRate","200");
        setStringProperty("playerBack", "F3");
        setStringProperty("playerPlay", "F4");
        setStringProperty("playerForw", "F5");
        setBoolProperty("showToolTip", true);

        save();

    }

    /**
     * Liefert einen Bool-Wert für den angegebenen Property-Key.
     *
     * @param property
     * @return
     *
     */
    public boolean getBoolProperty(String property) {

        return Boolean.parseBoolean(getProperty(property));
    }

    /**
     * Setzt einen Bool-Wert für den angegebenen Property-Key.
     *
     * @param property
     * @param value
     * * @deprecated
     */
    public void setBoolProperty(String property, Boolean value) {
        setProperty(property, Boolean.toString(value));
    }

    /**
     * Liefert einen int-Wert für den angegebenen Property-Key.
     *
     * @param property
     * @return
     *
     */
    public int getIntProperty(String property) {
        if (getProperty(property) != null) {
            return Integer.parseInt(getProperty(property));
        } else {
            return -1;
        }
    }

    /**
     * Setzt einen int-Wert für den angegebenen Property-Key.
     *
     * @param property
     * @param value
     * * @deprecated
     */
    public void setIntProperty(String property, int value) {
        setProperty(property, Integer.toString(value));
    }

    /**
     * Liefert einen String-Wert für den angegebenen Property-Key.
     *
     * @param property
     *
     * @return
     */
    public String getStringProperty(String property) {
        return getProperty(property);
    }

    /**
     * Liefert einen String-Wert für den angegebenen Property-Key.
     *
     * @param property
     * @param value
     *
     */
    public void setStringProperty(String property, String value) {
        setProperty(property, value);
    }

    /*
     * Wendet die Werte der Conf.property an.
     * Erstellt die Tastenzuweisung neu und setzt die Werte im Config-Dialog.
     */
    public void applyProperties() {

        if (getProperty("lang") != null) {
            switch (getProperty("lang")) {
                case "en":
                    currentLocale = new Locale("en", "US");
                    Locale.setDefault(currentLocale);
                    configFrame.getConfigLanguageCombobox().setSelectedIndex(1);
                    break;
                case "de":
                    currentLocale = new Locale("de", "DE");
                    Locale.setDefault(currentLocale);
                    configFrame.getConfigLanguageCombobox().setSelectedIndex(0);
                    break;
                case "ja":
                    currentLocale = new Locale("ja", "JP");
                    Locale.setDefault(currentLocale);
                    configFrame.getConfigLanguageCombobox().setSelectedIndex(3);
                    break;
                case "fr":
                    currentLocale = new Locale("fr", "FR");
                    Locale.setDefault(currentLocale);
                    configFrame.getConfigLanguageCombobox().setSelectedIndex(2);
                    break;
            }
        } else {
            setProperty("lang", "en");
            try {
                save();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("SavingConfig")), ex);

            }
            load(true);
            return;
        }

        messages = ResourceBundle.getBundle("Bundle", currentLocale);

        if (getStringProperty("playerBack") == null) {
            setStringProperty("playerBack", "F3");
            setStringProperty("playerPlay", "F4");
            setStringProperty("playerForw", "F5");
            try {
                save();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("SavingConfig")), ex);

            }
            load(true);
            return;
        }

        if (Variables.fxSupported==false){
            setBoolProperty("useFXPlayer",false);
            configFrame.getConfigUseFXPLayerCheckbox().setEnabled(false);
            try {
                save();
            } catch (IOException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       if (!getOS().equals("Mac")) configFrame.getConfigUseFXPLayerCheckbox().setSelected(getBoolProperty("useFXPlayer"));
        
        configFrame.getConfigOPUpdatechannelComboBox().setSelectedItem(de.ewerkzeug.easytranscript.core.VersionState.getValue(getStringProperty("updatechannel")));
        easytranscript.getMainchangeSpokemanCheckboxmenuitem().setSelected(getBoolProperty("Sprecherwechsel"));
      
            configFrame.getConfigSuggestionsCheckbox().setSelected(getBoolProperty("suggestions"));
        
     //   configFrame.getConfigPlayerSystemWideCheckbox().setSelected(getBoolProperty("ConfigPlayerSystemWideCheckbox"));
        configFrame.getConfigTimestampsEndingCheckbox().setSelected(getBoolProperty("zeitmarkeEnding"));
        configFrame.getConfigStenoActivateCheckbox().setSelected(getBoolProperty("ConfigStenoActivate"));
        configFrame.getConfigStenoTable().setEnabled(getBoolProperty("ConfigStenoActivate"));
        configFrame.getConfigShowStartCheckbox().setSelected(getBoolProperty("showStart"));
        configFrame.getConfigTimestampsActiveCheckbox().setSelected(getBoolProperty("ZeitmarkenActive"));
        configFrame.getConfigTimestampsBeginningCheckbox().setSelected(getBoolProperty("zeitmarkeBeginning"));
        configFrame.getConfigPassageCheckbox().setSelected(getBoolProperty("ZMleerzeile"));
        configFrame.getConfigCOSCheckbox().setSelected(getBoolProperty("Sprecherwechsel"));
        configFrame.getConfigCOSInterviewerTextfield().setText(getStringProperty("SprecherwechselP1"));
        configFrame.getConfigCOSPersonTextfield().setText(getStringProperty("SprecherwechselP2"));
        configFrame.getConfigCreateBackupCheckbox().setSelected(getBoolProperty("backupEnabled"));
        configFrame.getConfigBackupMinutesSpinner().setValue(getIntProperty("backupTimer") / 1000 / 60);
        configFrame.getConfigModulesBoldCheckbox().setSelected((getBoolProperty("TextbausteineFett")));
        configFrame.getConfigCOSBoldCheckbox().setSelected(getBoolProperty("COSFett"));
        configFrame.getConfigTM1Textfield().setText(getStringProperty("Tbs1"));
        configFrame.getConfigTM2Textfield().setText(getStringProperty("Tbs2"));
        configFrame.getConfigTM3Textfield().setText(getStringProperty("Tbs3"));
        configFrame.getConfigTM4Textfield().setText(getStringProperty("Tbs4"));
        configFrame.getConfigTM5Textfield().setText(getStringProperty("Tbs5"));
        configFrame.getConfigTM6Textfield().setText(getStringProperty("Tbs6"));
        configFrame.getConfigTM7Textfield().setText(getStringProperty("Tbs7"));
        configFrame.getConfigTM8Textfield().setText(getStringProperty("Tbs8"));
        configFrame.getConfigTM9Textfield().setText(getStringProperty("Tbs9"));
        configFrame.getConfigTM10Textfield().setText(getStringProperty("Tbs10"));
        configFrame.getConfigTooltipCheckbox().setSelected(getBoolProperty("showToolTip"));
        configFrame.getConfigPerformanceModeCheckbox().setSelected(getBoolProperty("usePerformanceMode"));
     
        easytranscript.getMainToolbarZoomCombobox().setEnabled(!getBoolProperty("usePerformanceMode"));
        
        
        
        
        configFrame.getConfigWindBackCombobox().setSelectedItem(getStringProperty("playerBack"));
        configFrame.getConfigPlayCombobox().setSelectedItem(getStringProperty("playerPlay"));
        configFrame.getConfigFastForwardCombobox().setSelectedItem(getStringProperty("playerForw"));
        configFrame.updateAvailKeys();

        Easytranscript.MainToolbarChangeSpokemanTogglebutton.setSelected(getBoolProperty("Sprecherwechsel"));
        tastenCheckFrame.getTCtm1Textfield().setText(getStringProperty("Tbs1"));
        tastenCheckFrame.getTCtm2Textfield().setText(getStringProperty("Tbs2"));
        tastenCheckFrame.getTCtm3Textfield().setText(getStringProperty("Tbs3"));
        tastenCheckFrame.getTCtm4Textfield().setText(getStringProperty("Tbs4"));
        tastenCheckFrame.getTCtm5Textfield().setText(getStringProperty("Tbs5"));
        tastenCheckFrame.getTCtm6Textfield().setText(getStringProperty("Tbs6"));
        tastenCheckFrame.getTCtm7Textfield().setText(getStringProperty("Tbs7"));
        tastenCheckFrame.getTCtm8Textfield().setText(getStringProperty("Tbs8"));
        tastenCheckFrame.getTCtm9Textfield().setText(getStringProperty("Tbs9"));
        tastenCheckFrame.getTCtm10Textfield().setText(getStringProperty("Tbs10"));

        ToolTipManager.sharedInstance().setEnabled(getBoolProperty("showToolTip"));

        tastenCheckFrame.getTCPlayerbackTextfield().setText(getStringProperty("playerBack"));
        tastenCheckFrame.getTCPlayerPlayTextfield().setText(getStringProperty("playerPlay"));
        tastenCheckFrame.getTCPlayerforwTextfield().setText(getStringProperty("playerForw"));

        easytranscript.getMainCenterEditorEditorPane().getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).clear();

       // if (!getBoolProperty("ConfigPlayerSystemWideCheckbox")) {
            easytranscript.getMainCenterEditorEditorPane().getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigWindBackCombobox().getSelectedItem())).getKeyCode(), 0, true), "zuruckspulen");
            easytranscript.getMainCenterEditorEditorPane().getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigPlayCombobox().getSelectedItem())).getKeyCode(), 0, true), "PausePlaying");
            easytranscript.getMainCenterEditorEditorPane().getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigFastForwardCombobox().getSelectedItem())).getKeyCode(), 0, true), "vorspulen");
      /*  } else {

            String os = getOS();

            if (os.equals("Win")) {
               JIntellitype.getInstance().unregisterHotKey(0);
                JIntellitype.getInstance().unregisterHotKey(1);
                JIntellitype.getInstance().unregisterHotKey(2);
                JIntellitype.getInstance().registerHotKey(0, 0, KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigWindBackCombobox().getSelectedItem())).getKeyCode());
                JIntellitype.getInstance().registerHotKey(1, 0, KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigPlayCombobox().getSelectedItem())).getKeyCode());
                JIntellitype.getInstance().registerHotKey(2, 0, KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigFastForwardCombobox().getSelectedItem())).getKeyCode());
            }
            if (os.equals("Lin")) {
                JXGrabKey.getInstance().unregisterHotKey(0);
                JXGrabKey.getInstance().unregisterHotKey(1);
                JXGrabKey.getInstance().unregisterHotKey(2);
                try {

                    JXGrabKey.getInstance().registerAwtHotkey(0, 0, KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigWindBackCombobox().getSelectedItem())).getKeyCode());
                    JXGrabKey.getInstance().registerAwtHotkey(1, 0, KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigPlayCombobox().getSelectedItem())).getKeyCode());
                    JXGrabKey.getInstance().registerAwtHotkey(2, 0, KeyStroke.getKeyStroke(String.valueOf(configFrame.getConfigFastForwardCombobox().getSelectedItem())).getKeyCode());

                } catch (HotkeyConflictException ex) {
                    Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (os.equals("Mac")) {

            }
            //    }

        }*/

        if (new File("opFolder.cfg").exists()) {
            configFrame.getConfigOPCheckbox().setSelected(true);
            InputStreamReader converter;
            try {
                converter = new InputStreamReader(new FileInputStream("opFolder.cfg"));
                BufferedReader in = new BufferedReader(converter);

                String s = in.readLine();

                if (s == null) {
                    return;
                }

                configFrame.getConfigOPTextfield().setText(s);

            } catch (FileNotFoundException ex) {
                logger.log(Level.WARNING, ex.getLocalizedMessage());
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.getLocalizedMessage());
            }
        }

    }

}
