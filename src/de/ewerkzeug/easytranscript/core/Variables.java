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
package de.ewerkzeug.easytranscript.core;

import de.ewerkzeug.easytranscript.gui.components.ChangeTranscriptFrame;
import de.ewerkzeug.easytranscript.gui.components.ConfigFrame;
import de.ewerkzeug.easytranscript.gui.components.Easynews;
import de.ewerkzeug.easytranscript.gui.components.ExportFrame;
import de.ewerkzeug.easytranscript.gui.components.ImportFrame;
import de.ewerkzeug.easytranscript.gui.components.InfoFrame;
import de.ewerkzeug.easytranscript.gui.components.InstallationDialog;
import de.ewerkzeug.easytranscript.gui.components.NewTranscriptFrame;
import de.ewerkzeug.easytranscript.gui.components.ProjectFrame;
import de.ewerkzeug.easytranscript.gui.components.SearchFrame;
import de.ewerkzeug.easytranscript.gui.components.StartFrame;
import de.ewerkzeug.easytranscript.gui.components.SupportFrame;
import de.ewerkzeug.easytranscript.gui.components.TastenCheckFrame;
import de.ewerkzeug.easytranscript.gui.components.TunerDialog;
import de.ewerkzeug.easytranscript.gui.components.UpdateFrame;
import de.ewerkzeug.easytranscript.gui.components.WorkerFrame;
import de.ewerkzeug.easytranscript.gui.components.ZeitFrame;
import de.ewerkzeug.easytranscript.gui.misc.FontDropdown;
import de.ewerkzeug.easytranscript.IO.data.Configuration;
import de.ewerkzeug.easytranscript.IO.data.ProjectHandler;
import de.ewerkzeug.easytranscript.IO.data.RecentUsed;
import de.ewerkzeug.easytranscript.IO.data.StenoData;
import de.ewerkzeug.easytranscript.IO.data.WorkTime;
import de.ewerkzeug.easytranscript.tools.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.undo.UndoManager;

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class Variables {

    //#####################
    public static final Version VERSION = new Version("2.51.3 Épice " + VersionState.FINAL);
    public static int UPDATECHANNEL = VersionState.getValue(VersionState.FINAL);
    public static String updVersion = "minor";
    //#####################

    public static Easytranscript easytranscript;
    public static Player player;
    public static long currentPlayerTime;
    public static int millisecondsPerStep = 10;
    public static int Stunden_current;
    public static int Minuten_current;
    public static int Sekunden_current;
    public static int Millisekunden_current;
    public static String StundenC_string = "00";
    public static String MinutenC_string = "00";
    public static String SekundenC_string = "00";
    public static String sfull;
    public static FontDropdown fontDropDown;
    public static boolean fontsizeHasFocus;
    public static boolean fontfamilyHasFocus;
    public static boolean useFXPlayer;
    public static boolean fxSupported=true;
    public static boolean orientationRT = false;
    public static boolean systemWideShortcuts = true;

    public static UndoManager undoManager;
    public static SimpleAttributeSet currentAttributeSet;
    public static boolean casChanged = false;
    public static boolean styleChanged= false;
    public static double zoomFactor = 2.25;
    public static boolean PPprojNameWarning = false;
    public static boolean PPtransNameWarning = false;
    public static boolean PPtransNameWarning1 = false;
    public static byte projectCorrupt = 0;
    public static JPopupMenu startRecentUsed = new JPopupMenu();
    public static String opFolder = System.getProperty("user.home") + System.getProperty("file.separator") + "easytranscript" + System.getProperty("file.separator");

    public static List<String> suggestionList = new ArrayList<>();
    public static int suggestionNeededLength = 10;
    public static int maxPlayerRate=200;

    public static enum Mode {

        INSERT, COMPLETION
    };
    public static Mode insertionMode = Mode.INSERT;

    public static int undoRedo = 0;

    public static int performanceModeFontSizeIncrease = 15;

    public static Configuration prop = new Configuration();
    public static StenoData steno = new StenoData();
    public static WorkTime workTime = new WorkTime();
    public static RecentUsed recentUsed = new RecentUsed();
    public static ProjectHandler projFolder = new ProjectHandler();

    public static Locale currentLocale;
    public static ResourceBundle messages;
    public static ResourceBundle errors;

    public static Timer backupTimer;

    public static final Logger logger = Logger.getLogger(Easytranscript.class.getName());

    static InfoFrame infoFrame;
    public static ConfigFrame configFrame;
    public static SearchFrame searchFrame;
    public static ZeitFrame zeitFrame;
    public static WorkerFrame workerFrame;
    public static ProjectFrame projectFolderFrame;
    public static StartFrame startFrame;
    public static TunerDialog tunerDialog;
    public static TastenCheckFrame tastenCheckFrame;
    static SupportFrame supportFrame;
    public static InstallationDialog installationDialog;
    public static UpdateFrame updateFrame;
    static ImportFrame importFrame;
    public static NewTranscriptFrame newProjectFrame;
    public static ChangeTranscriptFrame changeProjectFrame;
    public static ExportFrame exportFrame;
    static Easynews news;

    static Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static Boolean highContrast = (Boolean)toolkit.getDesktopProperty( "win.highContrast.on" );
}
