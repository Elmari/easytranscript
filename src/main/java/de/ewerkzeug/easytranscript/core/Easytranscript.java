package de.ewerkzeug.easytranscript.core;

/**
 *
 *
 * easytranscript Copyright (C) 2014 e-werkzeug
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
import com.melloware.jintellitype.JIntellitype;

import static de.ewerkzeug.easytranscript.core.Variables.*;

import com.sun.jna.NativeLibrary;
import de.ewerkzeug.easytranscript.core.Variables.Mode;
import de.ewerkzeug.easytranscript.gui.components.ChangeTranscriptFrame;
import de.ewerkzeug.easytranscript.gui.components.ConfigFrame;
import de.ewerkzeug.easytranscript.gui.components.Easynews;
import de.ewerkzeug.easytranscript.gui.components.ExportFrame;
import de.ewerkzeug.easytranscript.gui.components.ImportFrame;
import de.ewerkzeug.easytranscript.gui.components.InfoFrame;
import de.ewerkzeug.easytranscript.gui.components.InstallationDialog;
import de.ewerkzeug.easytranscript.gui.components.MetaFrame;
import de.ewerkzeug.easytranscript.gui.components.NewTranscriptFrame;
import static de.ewerkzeug.easytranscript.gui.components.NewTranscriptFrame.clearProjWindowValues;
import de.ewerkzeug.easytranscript.gui.components.ProjectFrame;
import de.ewerkzeug.easytranscript.gui.components.SearchFrame;
import de.ewerkzeug.easytranscript.gui.components.StartFrame;
import de.ewerkzeug.easytranscript.gui.components.SupportFrame;
import de.ewerkzeug.easytranscript.gui.components.TastenCheckFrame;
import de.ewerkzeug.easytranscript.gui.components.TunerDialog;
import de.ewerkzeug.easytranscript.gui.components.UpdateFrame;
import de.ewerkzeug.easytranscript.gui.components.WorkerFrame;
import de.ewerkzeug.easytranscript.gui.components.ZeitFrame;
import de.ewerkzeug.easytranscript.gui.components.printDocument;
import de.ewerkzeug.easytranscript.gui.misc.CaretPositionInformation;
import de.ewerkzeug.easytranscript.gui.misc.ExtendedEditorPane;
import de.ewerkzeug.easytranscript.gui.misc.FontDropdown;
import de.ewerkzeug.easytranscript.gui.misc.FontFocusListener;
import de.ewerkzeug.easytranscript.gui.misc.MouseWheelZoom;
import de.ewerkzeug.easytranscript.gui.misc.SelectionPreservingCaret;
import de.ewerkzeug.easytranscript.gui.misc.StenoAction;
import de.ewerkzeug.easytranscript.gui.misc.UpdateTimer;
import de.ewerkzeug.easytranscript.gui.misc.WrapEditorKit;
import de.ewerkzeug.easytranscript.IO.data.TranscriptHandler;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.mediaPath;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.transcriptName;
import static de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.transcriptPath;
import de.ewerkzeug.easytranscript.tools.FileSearch;
import de.ewerkzeug.easytranscript.tools.MultiOutputStream;
import de.ewerkzeug.easytranscript.tools.Player;
import de.ewerkzeug.easytranscript.tools.PlayerFX;
import de.ewerkzeug.easytranscript.tools.Tools;
import static de.ewerkzeug.easytranscript.tools.Tools.checkOp;
import static de.ewerkzeug.easytranscript.tools.Tools.deleteFilesWithExtension;
import static de.ewerkzeug.easytranscript.tools.Tools.getOS;
import de.ewerkzeug.easytranscript.tools.UpdateChecker;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import javafx.scene.media.MediaPlayer.Status;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;
import javax.swing.text.html.MinimalHTMLWriter;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.version.LibVlcVersion;
import uk.co.caprica.vlcj.version.Version;

public class Easytranscript extends javax.swing.JFrame {

    private ImageIcon more = new ImageIcon(Easytranscript.class.getResource("images/dialog-more.png"));
    private ImageIcon less = new ImageIcon(Easytranscript.class.getResource("images/dialog-fewer.png"));
    private ImageIcon icon = new ImageIcon(Easytranscript.class.getResource("images/icon.png"));
     LibVlc Instance;

    private final SimpleAttributeSet attributeBold = new SimpleAttributeSet();
    private boolean sprecherwechselWechsel = false;
    private int pointOffsetTextfield;

    private static Handler logHandler;
    private JFileChooser fileChooser;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static String[] audioformate = {"wma", "mp3", "ogg", "wav", "m4a", "flac", "aiff"};
    private static String[] videoformate = {"avi", "mp4", "mov", "3gp", "ogm", "mkv", "wmv", "m2ts", "mts"};
    private static String[] audioformateFX = {"mp3", "wav", "m4a", "aiff"};
    private static String[] videoformateFX = {"flv", "mp4", "m4v"};
    public static String[] formate;

    private ArrayList<AttributeSet> attrList = new ArrayList<>();
    public static int SystemWideKeyWasActive = 0;
    private boolean savedState;

    private java.util.Timer updatetimer = new java.util.Timer();
    private TimerTask task = new UpdateTimeWhilePressed();

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainActionPopupMenu = new javax.swing.JPopupMenu();
        MainActionBoldMenuItem = new javax.swing.JMenuItem();
        MainActionItalicMenuItem = new javax.swing.JMenuItem();
        MainActionUnderlinedMenuItem = new javax.swing.JMenuItem();
        MainActionSep1Separator = new javax.swing.JPopupMenu.Separator();
        MainActionCopyMenuItem = new javax.swing.JMenuItem();
        MainActionCutMenuItem = new javax.swing.JMenuItem();
        MainActionPasteMenuItem = new javax.swing.JMenuItem();
        MainActionTimeStampMenuItem = new javax.swing.JMenuItem();
        MainActionSep2Separator = new javax.swing.JPopupMenu.Separator();
        MainActionSelectAllMenuItem = new javax.swing.JMenuItem();
        MainAlignmentButtonGroup = new javax.swing.ButtonGroup();
        MainSliderPanel = new javax.swing.JPanel();
        MainSlider = new javax.swing.JSlider();
        MainToolbar = new javax.swing.JToolBar();
        MainToolbar1Seperator = new javax.swing.JToolBar.Separator();
        MainToolbarSaveButton = new javax.swing.JButton();
        MainToolbarSaveAsButton = new javax.swing.JButton();
        MainToolbarundoButton = new javax.swing.JButton();
        MainToolbarredoButton = new javax.swing.JButton();
        MainToolbar2Seperator = new javax.swing.JToolBar.Separator();
        MainToolbarcopyButton = new javax.swing.JButton();
        MainToolbarCutButton = new javax.swing.JButton();
        MainToolbarPasteButton = new javax.swing.JButton();
        MainToolbarSearchButton = new javax.swing.JButton();
        MainToolbarTimestampButton = new javax.swing.JButton();
        MainToolbarChangeSpokemanTogglebutton = new javax.swing.JToggleButton();
        MainToolbar3Seperator = new javax.swing.JToolBar.Separator();
        MainToolbarBoldButton = new javax.swing.JToggleButton();
        MainToolbarItalicButton = new javax.swing.JToggleButton();
        MainToolbarUnderlinedButton = new javax.swing.JToggleButton();
        MainToolbarFontsizeCombobox = new javax.swing.JComboBox();
        MainToolbarFontDropdownFakeFiller = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        MainToolbar2Filler = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        MainToolbarZoomCombobox = new javax.swing.JComboBox();
        MainToolbar4Seperator = new javax.swing.JToolBar.Separator();
        MainToolbarTimeButton = new javax.swing.JButton();
        MainToolbarConfigButton = new javax.swing.JButton();
        MainToolbarCheckUpdatesButton = new javax.swing.JButton();
        MainButtonPanel = new javax.swing.JPanel();
        MainTimePanel = new javax.swing.JPanel();
        MainTimeLabel = new javax.swing.JLabel();
        MainSlashLabel = new javax.swing.JLabel();
        MainTotalLabel = new javax.swing.JLabel();
        MainTimeMilliLabel = new javax.swing.JLabel();
        MainTimeButtonPanel = new javax.swing.JPanel();
        MainTimeTunerButton = new javax.swing.JButton();
        MainTime1Filler = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        MainTimePlayerButtonPanel = new javax.swing.JPanel();
        MainTimePlayerbackwButton = new javax.swing.JButton();
        MainTimePlayerplayButton = new javax.swing.JButton();
        MainTimePlayerforwButton = new javax.swing.JButton();
        MainTime2Filler = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        MainTimeIntervalPanel = new javax.swing.JPanel();
        MainTimeIntervalLabel = new javax.swing.JLabel();
        MainTimeIntervalSlider = new javax.swing.JSlider();
        MainTimeRightPanel = new javax.swing.JPanel();
        MainTimeExpandCollapsButton = new javax.swing.JButton();
        MainCenterPanel = new javax.swing.JPanel();
        MainCenterSplitpane = new javax.swing.JSplitPane();
        MainCenterVideoInternalframe = new javax.swing.JInternalFrame();
        MainCenterEditorPanel = new javax.swing.JPanel();
        MainCenterEditorScrollpane = new javax.swing.JScrollPane();
        MainCenterEditorEditorPane = new ExtendedEditorPane();
        BufferingPanel = new javax.swing.JPanel();
        BufferingLabel = new javax.swing.JLabel();
        MainMenubar = new javax.swing.JMenuBar();
        MainfileMenu = new javax.swing.JMenu();
        MaincreateProjectMenuItem = new javax.swing.JMenuItem();
        MaincreateProjectFolderMenuitem = new javax.swing.JMenuItem();
        MainopenProjectMenuItem = new javax.swing.JMenuItem();
        MainopenProjectFolderMenuItem = new javax.swing.JMenuItem();
        MainimportProjectMenuItem = new javax.swing.JMenuItem();
        MainrecentMenu = new javax.swing.JMenu();
        Mainrecent1Menuitem = new javax.swing.JMenuItem();
        Mainrecent2Menuitem = new javax.swing.JMenuItem();
        Mainrecent3Menuitem = new javax.swing.JMenuItem();
        MainfilemenuSeperator = new javax.swing.JPopupMenu.Separator();
        MainchangeProjectMenuItem = new javax.swing.JMenuItem();
        MainsaveMenuItem = new javax.swing.JMenuItem();
        MainSaveAsMenuItem = new javax.swing.JMenuItem();
        MainexportMenu = new javax.swing.JMenu();
        MainprojExportMenuitem = new javax.swing.JMenuItem();
        MainexportSeperator = new javax.swing.JPopupMenu.Separator();
        MainduplicateTransMenuitem = new javax.swing.JMenuItem();
        MainexportTransMenuitem = new javax.swing.JMenuItem();
        Mainexport2Separator = new javax.swing.JPopupMenu.Separator();
        MainexportYoutubeMenuitem = new javax.swing.JMenuItem();
        MainprintMenuitem = new javax.swing.JMenuItem();
        MainProjectInfosMenuitem = new javax.swing.JMenuItem();
        MaincloseProjectMenuitem = new javax.swing.JMenuItem();
        Mainfilemenu2Seperator = new javax.swing.JPopupMenu.Separator();
        MaincloseMenuitem = new javax.swing.JMenuItem();
        MaineditMenu = new javax.swing.JMenu();
        MainundoMenuitem = new javax.swing.JMenuItem();
        MainredoMenuitem = new javax.swing.JMenuItem();
        MaineditSeperator = new javax.swing.JPopupMenu.Separator();
        MainboldMenuitem = new javax.swing.JMenuItem();
        MainitalicMenuitem = new javax.swing.JMenuItem();
        MainunderlinedMenuitem = new javax.swing.JMenuItem();
        Mainedit2Seperator = new javax.swing.JPopupMenu.Separator();
        MaincopyMenuitem = new javax.swing.JMenuItem();
        MaincutMenuitem = new javax.swing.JMenuItem();
        MainpasteMenuitem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        MaintimestampMenuitem = new javax.swing.JMenuItem();
        MainfontsizePlusMenuitem = new javax.swing.JMenuItem();
        MainFontsizeMinusMenuitem = new javax.swing.JMenuItem();
        Mainedit3Seperator = new javax.swing.JPopupMenu.Separator();
        MainchangeSpokemanCheckboxmenuitem = new javax.swing.JCheckBoxMenuItem();
        MainsearchReplaceMenuitem = new javax.swing.JMenuItem();
        MaintoolsMenu = new javax.swing.JMenu();
        MaintoolsAlignmentMenu = new javax.swing.JMenu();
        MaintoolsAlignmentLeftRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        MaintoolsRightRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        MaintoolsTunerMenu = new javax.swing.JMenu();
        MaintoolsTunerPlusMenuitem = new javax.swing.JMenuItem();
        MaintoolsTunerMinusMenuitem = new javax.swing.JMenuItem();
        MaintimeMenuitem = new javax.swing.JMenuItem();
        MainshortcutsMenuitem = new javax.swing.JMenuItem();
        MainhelpMenu = new javax.swing.JMenu();
        MainconfigMenuitem = new javax.swing.JMenuItem();
        MaintoolsSep1Separator = new javax.swing.JPopupMenu.Separator();
        MainhelpMenuitem = new javax.swing.JMenuItem();
        MainsupportMenuitem = new javax.swing.JMenuItem();
        MaindonateMenuitem = new javax.swing.JMenuItem();
        MainSocialMenuitem = new javax.swing.JMenuItem();
        MaininfoMenuitem = new javax.swing.JMenuItem();
        MaincheckUpdatesMenuitem = new javax.swing.JMenuItem();

        MainActionBoldMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/format-text-bold-6_klein.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
        MainActionBoldMenuItem.setText(bundle.getString("Easytranscript.MainActionBoldMenuItem.text")); // NOI18N
        MainActionBoldMenuItem.setIconTextGap(10);
        MainActionBoldMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionBoldMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionBoldMenuItem);

        MainActionItalicMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/format-text-italic-5.png"))); // NOI18N
        MainActionItalicMenuItem.setText(bundle.getString("Easytranscript.MainActionItalicMenuItem.text")); // NOI18N
        MainActionItalicMenuItem.setIconTextGap(10);
        MainActionItalicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionItalicMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionItalicMenuItem);

        MainActionUnderlinedMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/format-text-underline-6.png"))); // NOI18N
        MainActionUnderlinedMenuItem.setText(bundle.getString("Easytranscript.MainActionUnderlinedMenuItem.text")); // NOI18N
        MainActionUnderlinedMenuItem.setIconTextGap(10);
        MainActionUnderlinedMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionUnderlinedMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionUnderlinedMenuItem);
        MainActionPopupMenu.add(MainActionSep1Separator);

        MainActionCopyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-copy-9.png"))); // NOI18N
        MainActionCopyMenuItem.setText(bundle.getString("Easytranscript.MainActionCopyMenuItem.text")); // NOI18N
        MainActionCopyMenuItem.setIconTextGap(10);
        MainActionCopyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionCopyMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionCopyMenuItem);

        MainActionCutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-cut-8.png"))); // NOI18N
        MainActionCutMenuItem.setText(bundle.getString("Easytranscript.MainActionCutMenuItem.text")); // NOI18N
        MainActionCutMenuItem.setIconTextGap(10);
        MainActionCutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionCutMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionCutMenuItem);

        MainActionPasteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-paste-4.png"))); // NOI18N
        MainActionPasteMenuItem.setText(bundle.getString("Easytranscript.MainActionPasteMenuItem.text")); // NOI18N
        MainActionPasteMenuItem.setIconTextGap(10);
        MainActionPasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionPasteMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionPasteMenuItem);

        MainActionTimeStampMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/appointment-new-5.png"))); // NOI18N
        MainActionTimeStampMenuItem.setText(bundle.getString("Easytranscript.MainActionTimeStampMenuItem.text")); // NOI18N
        MainActionTimeStampMenuItem.setIconTextGap(10);
        MainActionTimeStampMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionTimeStampMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionTimeStampMenuItem);
        MainActionPopupMenu.add(MainActionSep2Separator);

        MainActionSelectAllMenuItem.setText(bundle.getString("Easytranscript.MainActionSelectAllMenuItem.text")); // NOI18N
        MainActionSelectAllMenuItem.setIconTextGap(10);
        MainActionSelectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainActionSelectAllMenuItemActionPerformed(evt);
            }
        });
        MainActionPopupMenu.add(MainActionSelectAllMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(bundle.getString("Easytranscript.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 600));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        MainSlider.setValue(0);
        MainSlider.setEnabled(false);
        MainSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MainSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                MainSliderMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout MainSliderPanelLayout = new javax.swing.GroupLayout(MainSliderPanel);
        MainSliderPanel.setLayout(MainSliderPanelLayout);
        MainSliderPanelLayout.setHorizontalGroup(
            MainSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainSliderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        MainSliderPanelLayout.setVerticalGroup(
            MainSliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainSliderPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(MainSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MainSliderPanel.setVisible(false);

        MainToolbar.setFloatable(false);
        MainToolbar.setRollover(true);
        MainToolbar.add(MainToolbar1Seperator);

        MainToolbarSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/document-save-5.png"))); // NOI18N
        MainToolbarSaveButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarSaveButton.toolTipText")); // NOI18N
        MainToolbarSaveButton.setEnabled(false);
        MainToolbarSaveButton.setFocusable(false);
        MainToolbarSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarSaveButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarSaveButton);
        KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
        Action performSave = new AbstractAction("Save") {
            public void actionPerformed(ActionEvent e) {
                de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.save(transcriptPath,false, false);
            }
        };

        MainToolbarSaveButton.getActionMap().put("performSave", performSave);
        MainToolbarSaveButton.getInputMap(MainCenterSplitpane.WHEN_IN_FOCUSED_WINDOW).put(keySave, "performSave");

        MainToolbarSaveAsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/document-save-as-5.png"))); // NOI18N
        MainToolbarSaveAsButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarSaveAsButton.toolTipText")); // NOI18N
        MainToolbarSaveAsButton.setEnabled(false);
        MainToolbarSaveAsButton.setFocusable(false);
        MainToolbarSaveAsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarSaveAsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarSaveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarSaveAsButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarSaveAsButton);
        KeyStroke keySave2 = (KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
        Action performSave2 = new AbstractAction("Save2") {
            public void actionPerformed(ActionEvent e) {
                MainSaveAsMenuItemActionPerformed(e);
            }
        };

        MainToolbarSaveAsButton.getActionMap().put("performSave2", performSave2);
        MainToolbarSaveAsButton.getInputMap(MainCenterSplitpane.WHEN_IN_FOCUSED_WINDOW).put(keySave2, "performSave2");

        MainToolbarundoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-undo-7.png"))); // NOI18N
        MainToolbarundoButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarundoButton.toolTipText")); // NOI18N
        MainToolbarundoButton.setFocusable(false);
        MainToolbarundoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarundoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarundoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarundoButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarundoButton);

        MainToolbarredoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-redo-7.png"))); // NOI18N
        MainToolbarredoButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarredoButton.toolTipText")); // NOI18N
        MainToolbarredoButton.setFocusable(false);
        MainToolbarredoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarredoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarredoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarredoButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarredoButton);

        MainToolbar2Seperator.setPreferredSize(new java.awt.Dimension(3, 0));
        MainToolbar.add(MainToolbar2Seperator);

        MainToolbarcopyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-copy-9.png"))); // NOI18N
        MainToolbarcopyButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarcopyButton.toolTipText")); // NOI18N
        MainToolbarcopyButton.setFocusable(false);
        MainToolbarcopyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarcopyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarcopyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarcopyButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarcopyButton);
        //CopyB.addActionListener(new StyledEditorKit.CopyAction());

        MainToolbarCutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-cut-8.png"))); // NOI18N
        MainToolbarCutButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarCutButton.toolTipText")); // NOI18N
        MainToolbarCutButton.setFocusable(false);
        MainToolbarCutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarCutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbar.add(MainToolbarCutButton);
        MainToolbarCutButton.addActionListener(new StyledEditorKit.CutAction());

        MainToolbarPasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-paste-4.png"))); // NOI18N
        MainToolbarPasteButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarPasteButton.toolTipText")); // NOI18N
        MainToolbarPasteButton.setFocusable(false);
        MainToolbarPasteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarPasteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarPasteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarPasteButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarPasteButton);
        //PasteB.addActionListener(new StyledEditorKit.PasteAction());

        MainToolbarSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/edit-find-and-replace.png"))); // NOI18N
        MainToolbarSearchButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarSearchButton.toolTipText")); // NOI18N
        MainToolbarSearchButton.setFocusable(false);
        MainToolbarSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarSearchButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarSearchButton);

        MainToolbarTimestampButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/appointment-new-5.png"))); // NOI18N
        MainToolbarTimestampButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarTimestampButton.toolTipText")); // NOI18N
        MainToolbarTimestampButton.setFocusable(false);
        MainToolbarTimestampButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarTimestampButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarTimestampButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarTimestampButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarTimestampButton);

        MainToolbarChangeSpokemanTogglebutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/user-group-properties.png"))); // NOI18N
        MainToolbarChangeSpokemanTogglebutton.setToolTipText(bundle.getString("Easytranscript.MainToolbarChangeSpokemanTogglebutton.toolTipText")); // NOI18N
        MainToolbarChangeSpokemanTogglebutton.setFocusable(false);
        MainToolbarChangeSpokemanTogglebutton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarChangeSpokemanTogglebutton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarChangeSpokemanTogglebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarChangeSpokemanTogglebuttonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarChangeSpokemanTogglebutton);
        MainToolbar.add(MainToolbar3Seperator);

        MainToolbarBoldButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/format-text-bold-6_klein.png"))); // NOI18N
        MainToolbarBoldButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarBoldButton.toolTipText")); // NOI18N
        MainToolbarBoldButton.setFocusable(false);
        MainToolbarBoldButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarBoldButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbar.add(MainToolbarBoldButton);
        MainToolbarBoldButton.addActionListener(new StyledEditorKit.BoldAction());

        MainToolbarItalicButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/format-text-italic-5.png"))); // NOI18N
        MainToolbarItalicButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarItalicButton.toolTipText")); // NOI18N
        MainToolbarItalicButton.setFocusable(false);
        MainToolbarItalicButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarItalicButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbar.add(MainToolbarItalicButton);
        MainToolbarItalicButton.addActionListener(new StyledEditorKit.ItalicAction());

        MainToolbarUnderlinedButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/format-text-underline-6.png"))); // NOI18N
        MainToolbarUnderlinedButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarUnderlinedButton.toolTipText")); // NOI18N
        MainToolbarUnderlinedButton.setFocusable(false);
        MainToolbarUnderlinedButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarUnderlinedButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbar.add(MainToolbarUnderlinedButton);
        MainToolbarUnderlinedButton.addActionListener(new StyledEditorKit.UnderlineAction());

        MainToolbarFontsizeCombobox.setEditable(true);
        MainToolbarFontsizeCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "18", "20", "22", "24", "26", "28", "32", "36", "40", "44", "48", "54", "60", "66", "72", "80", "88", "96" }));
        MainToolbarFontsizeCombobox.setSelectedIndex(6);
        MainToolbarFontsizeCombobox.setToolTipText(bundle.getString("Easytranscript.MainToolbarFontsizeCombobox.toolTipText")); // NOI18N
        MainToolbarFontsizeCombobox.setMinimumSize(new java.awt.Dimension(120, 30));
        MainToolbarFontsizeCombobox.setPreferredSize(new java.awt.Dimension(60, 30));
        MainToolbarFontsizeCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarFontsizeComboboxActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarFontsizeCombobox);
        MainToolbarFontsizeCombobox.setLightWeightPopupEnabled(false);

        MainToolbarFontsizeCombobox.getEditor().getEditorComponent().addFocusListener( new FontFocusListener(true) );
        MainToolbar.add(MainToolbarFontDropdownFakeFiller);
        fontDropDown = new FontDropdown();
        fontDropDown.setToolTipText(messages.getString("Fonts"));
        fontDropDown.setMinimumSize(new Dimension(200,30));
        fontDropDown.setPreferredSize(new Dimension(200,30));

        MainToolbar.add(fontDropDown);
        fontDropDown.setLightWeightPopupEnabled(false);
        MainToolbar.add(MainToolbar2Filler);

        MainToolbarZoomCombobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100%", "125%", "150%", "175%", "200%", "225%", "250%", "275%", "300%", "325%", "350%", "375%", "400%", "425%", "450%", "475%", "500%" }));
        MainToolbarZoomCombobox.setSelectedIndex(5);
        MainToolbarZoomCombobox.setToolTipText(bundle.getString("Easytranscript.MainToolbarZoomCombobox.toolTipText")); // NOI18N
        MainToolbarZoomCombobox.setMinimumSize(new java.awt.Dimension(0, 20));
        MainToolbarZoomCombobox.setPreferredSize(new java.awt.Dimension(120, 30));
        MainToolbarZoomCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarZoomComboboxActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarZoomCombobox);
        MainToolbarZoomCombobox.setLightWeightPopupEnabled(false);
        MainToolbar.add(MainToolbar4Seperator);

        MainToolbarTimeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/view-history.png"))); // NOI18N
        MainToolbarTimeButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarTimeButton.toolTipText")); // NOI18N
        MainToolbarTimeButton.setFocusable(false);
        MainToolbarTimeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarTimeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarTimeButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarTimeButton);

        MainToolbarConfigButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/configure-4.png"))); // NOI18N
        MainToolbarConfigButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarConfigButton.toolTipText")); // NOI18N
        MainToolbarConfigButton.setFocusable(false);
        MainToolbarConfigButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarConfigButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarConfigButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarConfigButton);

        MainToolbarCheckUpdatesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/update-product_s.png"))); // NOI18N
        MainToolbarCheckUpdatesButton.setToolTipText(bundle.getString("Easytranscript.MainToolbarCheckUpdatesButton.toolTipText")); // NOI18N
        MainToolbarCheckUpdatesButton.setFocusable(false);
        MainToolbarCheckUpdatesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        MainToolbarCheckUpdatesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        MainToolbarCheckUpdatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainToolbarCheckUpdatesButtonActionPerformed(evt);
            }
        });
        MainToolbar.add(MainToolbarCheckUpdatesButton);

        MainTimeLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        MainTimeLabel.setText(bundle.getString("Easytranscript.MainTimeLabel.text")); // NOI18N

        MainSlashLabel.setText(bundle.getString("Easytranscript.MainSlashLabel.text")); // NOI18N

        MainTotalLabel.setText(bundle.getString("Easytranscript.MainTotalLabel.text")); // NOI18N

        MainTimeMilliLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        MainTimeMilliLabel.setText(bundle.getString("Easytranscript.MainTimeMilliLabel.text")); // NOI18N

        javax.swing.GroupLayout MainTimePanelLayout = new javax.swing.GroupLayout(MainTimePanel);
        MainTimePanel.setLayout(MainTimePanelLayout);
        MainTimePanelLayout.setHorizontalGroup(
            MainTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainTimePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainTimeLabel)
                .addGap(2, 2, 2)
                .addComponent(MainTimeMilliLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainSlashLabel)
                .addGap(2, 2, 2)
                .addComponent(MainTotalLabel)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        MainTimePanelLayout.setVerticalGroup(
            MainTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainTimePanelLayout.createSequentialGroup()
                .addGroup(MainTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MainTimeLabel)
                    .addComponent(MainTotalLabel)
                    .addComponent(MainSlashLabel)
                    .addComponent(MainTimeMilliLabel))
                .addGap(3, 3, 3))
        );

        MainTimeButtonPanel.setVisible(true);

        MainTimeTunerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/audio-volume-high-4.png"))); // NOI18N
        MainTimeTunerButton.setToolTipText(bundle.getString("Easytranscript.MainTimeTunerButton.toolTipText")); // NOI18N
        MainTimeTunerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainTimeTunerButtonActionPerformed(evt);
            }
        });
        MainTimeButtonPanel.add(MainTimeTunerButton);
        MainTimeButtonPanel.add(MainTime1Filler);

        MainTimePlayerbackwButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/media-seek-backward-8.png"))); // NOI18N
        MainTimePlayerbackwButton.setContentAreaFilled(false);
        MainTimePlayerbackwButton.setEnabled(false);
        MainTimePlayerbackwButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainTimePlayerbackwButtonActionPerformed(evt);
            }
        });
        MainTimePlayerButtonPanel.add(MainTimePlayerbackwButton);

        MainTimePlayerplayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/media-playback-start-8.png"))); // NOI18N
        MainTimePlayerplayButton.setContentAreaFilled(false);
        MainTimePlayerplayButton.setEnabled(false);
        MainTimePlayerplayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainTimePlayerplayButtonActionPerformed(evt);
            }
        });
        MainTimePlayerButtonPanel.add(MainTimePlayerplayButton);

        MainTimePlayerforwButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/media-seek-forward-8.png"))); // NOI18N
        MainTimePlayerforwButton.setContentAreaFilled(false);
        MainTimePlayerforwButton.setEnabled(false);
        MainTimePlayerforwButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainTimePlayerforwButtonActionPerformed(evt);
            }
        });
        MainTimePlayerButtonPanel.add(MainTimePlayerforwButton);

        MainTimeButtonPanel.add(MainTimePlayerButtonPanel);
        MainTimeButtonPanel.add(MainTime2Filler);

        MainTimeIntervalPanel.setLayout(new javax.swing.BoxLayout(MainTimeIntervalPanel, javax.swing.BoxLayout.PAGE_AXIS));

        MainTimeIntervalLabel.setText(bundle.getString("Easytranscript.MainTimeIntervalLabel.text")); // NOI18N
        MainTimeIntervalLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        MainTimeIntervalLabel.setEnabled(false);
        MainTimeIntervalPanel.add(MainTimeIntervalLabel);

        MainTimeIntervalSlider.setEnabled(false);
        MainTimeIntervalSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                MainTimeIntervalSliderStateChanged(evt);
            }
        });
        MainTimeIntervalPanel.add(MainTimeIntervalSlider);
        MainTimeIntervalSlider.setMaximum(10);
        MainTimeIntervalSlider.setMajorTickSpacing(2);
        MainTimeIntervalSlider.setMinorTickSpacing(1);
        MainTimeIntervalSlider.setPaintTicks(true);
        MainTimeIntervalSlider.setPaintLabels(true);
        MainTimeIntervalSlider.setValue(4);

        MainTimeButtonPanel.add(MainTimeIntervalPanel);

        MainTimeExpandCollapsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/ewerkzeug/easytranscript/core/images/dialog-fewer.png"))); // NOI18N
        MainTimeExpandCollapsButton.setContentAreaFilled(false);
        MainTimeExpandCollapsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainTimeExpandCollapsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MainTimeRightPanelLayout = new javax.swing.GroupLayout(MainTimeRightPanel);
        MainTimeRightPanel.setLayout(MainTimeRightPanelLayout);
        MainTimeRightPanelLayout.setHorizontalGroup(
            MainTimeRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainTimeRightPanelLayout.createSequentialGroup()
                .addGap(0, 308, Short.MAX_VALUE)
                .addComponent(MainTimeExpandCollapsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        MainTimeRightPanelLayout.setVerticalGroup(
            MainTimeRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainTimeRightPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MainTimeExpandCollapsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout MainButtonPanelLayout = new javax.swing.GroupLayout(MainButtonPanel);
        MainButtonPanel.setLayout(MainButtonPanelLayout);
        MainButtonPanelLayout.setHorizontalGroup(
            MainButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainButtonPanelLayout.createSequentialGroup()
                .addComponent(MainTimePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(MainTimeButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainTimeRightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        MainButtonPanelLayout.setVerticalGroup(
            MainButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainTimeRightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(MainButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MainTimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MainTimeButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        MainCenterPanel.setPreferredSize(new java.awt.Dimension(1280, 500));

        MainCenterSplitpane.setDividerLocation(100000);
        MainCenterSplitpane.setEnabled(false);
        MainCenterSplitpane.setFocusTraversalPolicyProvider(true);
        MainCenterSplitpane.setPreferredSize(new java.awt.Dimension(1280, 572));

        MainCenterVideoInternalframe.setBorder(null);
        MainCenterVideoInternalframe.setEnabled(false);
        MainCenterVideoInternalframe.setVisible(true);

        javax.swing.GroupLayout MainCenterVideoInternalframeLayout = new javax.swing.GroupLayout(MainCenterVideoInternalframe.getContentPane());
        MainCenterVideoInternalframe.getContentPane().setLayout(MainCenterVideoInternalframeLayout);
        MainCenterVideoInternalframeLayout.setHorizontalGroup(
            MainCenterVideoInternalframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        MainCenterVideoInternalframeLayout.setVerticalGroup(
            MainCenterVideoInternalframeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 606, Short.MAX_VALUE)
        );

        MainCenterSplitpane.setRightComponent(MainCenterVideoInternalframe);
        MainCenterVideoInternalframe.setVisible(true);

        MainCenterEditorEditorPane.setContentType("text/rtf"); // NOI18N
        MainCenterEditorEditorPane.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        MainCenterEditorEditorPane.setText(bundle.getString("Easytranscript.MainCenterEditorEditorPane.text")); // NOI18N
        MainCenterEditorEditorPane.setEnabled(false);
        MainCenterEditorEditorPane.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                MainCenterEditorEditorPaneCaretUpdate(evt);
            }
        });
        MainCenterEditorEditorPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MainCenterEditorEditorPaneMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                MainCenterEditorEditorPaneMouseReleased(evt);
            }
        });
        MainCenterEditorScrollpane.setViewportView(MainCenterEditorEditorPane);



        javax.swing.GroupLayout MainCenterEditorPanelLayout = new javax.swing.GroupLayout(MainCenterEditorPanel);
        MainCenterEditorPanel.setLayout(MainCenterEditorPanelLayout);
        MainCenterEditorPanelLayout.setHorizontalGroup(
            MainCenterEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainCenterEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainCenterEditorScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 1251, Short.MAX_VALUE)
                .addContainerGap())
        );
        MainCenterEditorPanelLayout.setVerticalGroup(
            MainCenterEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainCenterEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainCenterEditorScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                .addContainerGap())
        );

        MainCenterSplitpane.setLeftComponent(MainCenterEditorPanel);

        javax.swing.GroupLayout MainCenterPanelLayout = new javax.swing.GroupLayout(MainCenterPanel);
        MainCenterPanel.setLayout(MainCenterPanelLayout);
        MainCenterPanelLayout.setHorizontalGroup(
            MainCenterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainCenterSplitpane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        MainCenterPanelLayout.setVerticalGroup(
            MainCenterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainCenterSplitpane, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
        );

        BufferingPanel.setLayout(new java.awt.BorderLayout());
        BufferingPanel.add(BufferingLabel, java.awt.BorderLayout.CENTER);

        MainfileMenu.setText(bundle.getString("Easytranscript.MainfileMenu.text")); // NOI18N
        MainfileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainfileMenuActionPerformed(evt);
            }
        });

        MaincreateProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        MaincreateProjectMenuItem.setText(bundle.getString("Easytranscript.MaincreateProjectMenuItem.text")); // NOI18N
        MaincreateProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincreateProjectMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MaincreateProjectMenuItem);

        MaincreateProjectFolderMenuitem.setText(bundle.getString("Easytranscript.MaincreateProjectFolderMenuitem.text")); // NOI18N
        MaincreateProjectFolderMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincreateProjectFolderMenuitemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MaincreateProjectFolderMenuitem);

        MainopenProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        MainopenProjectMenuItem.setText(bundle.getString("Easytranscript.MainopenProjectMenuItem.text")); // NOI18N
        MainopenProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainopenProjectMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainopenProjectMenuItem);

        MainopenProjectFolderMenuItem.setText(bundle.getString("Easytranscript.MainopenProjectFolderMenuItem.text")); // NOI18N
        MainopenProjectFolderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainopenProjectFolderMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainopenProjectFolderMenuItem);

        MainimportProjectMenuItem.setText(bundle.getString("Easytranscript.MainimportProjectMenuItem.text")); // NOI18N
        MainimportProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainimportProjectMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainimportProjectMenuItem);

        MainrecentMenu.setText(bundle.getString("Easytranscript.MainrecentMenu.text")); // NOI18N

        Mainrecent1Menuitem.setText(bundle.getString("Easytranscript.Mainrecent1Menuitem.text")); // NOI18N
        MainrecentMenu.add(Mainrecent1Menuitem);
        Mainrecent1Menuitem.setVisible(false);

        Mainrecent2Menuitem.setText(bundle.getString("Easytranscript.Mainrecent2Menuitem.text")); // NOI18N
        MainrecentMenu.add(Mainrecent2Menuitem);
        Mainrecent2Menuitem.setVisible(false);

        Mainrecent3Menuitem.setText(bundle.getString("Easytranscript.Mainrecent3Menuitem.text")); // NOI18N
        MainrecentMenu.add(Mainrecent3Menuitem);
        Mainrecent3Menuitem.setVisible(false);

        MainfileMenu.add(MainrecentMenu);
        MainfileMenu.add(MainfilemenuSeperator);

        MainchangeProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        MainchangeProjectMenuItem.setText(bundle.getString("Easytranscript.MainchangeProjectMenuItem.text")); // NOI18N
        MainchangeProjectMenuItem.setEnabled(false);
        MainchangeProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainchangeProjectMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainchangeProjectMenuItem);

        MainsaveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        MainsaveMenuItem.setText(bundle.getString("Easytranscript.MainsaveMenuItem.text")); // NOI18N
        MainsaveMenuItem.setEnabled(false);
        MainsaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainsaveMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainsaveMenuItem);

        MainSaveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MainSaveAsMenuItem.setText(bundle.getString("Easytranscript.MainSaveAsMenuItem.text")); // NOI18N
        MainSaveAsMenuItem.setEnabled(false);
        MainSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainSaveAsMenuItemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainSaveAsMenuItem);

        MainexportMenu.setText(bundle.getString("Easytranscript.MainexportMenu.text")); // NOI18N
        MainexportMenu.setEnabled(false);

        MainprojExportMenuitem.setText(bundle.getString("Easytranscript.MainprojExportMenuitem.text")); // NOI18N
        MainprojExportMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainprojExportMenuitemActionPerformed(evt);
            }
        });
        MainexportMenu.add(MainprojExportMenuitem);
        MainexportMenu.add(MainexportSeperator);

        MainduplicateTransMenuitem.setText(bundle.getString("Easytranscript.MainduplicateTransMenuitem.text")); // NOI18N
        MainduplicateTransMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainduplicateTransMenuitemActionPerformed(evt);
            }
        });
        MainexportMenu.add(MainduplicateTransMenuitem);

        MainexportTransMenuitem.setText(bundle.getString("Easytranscript.MainexportTransMenuitem.text")); // NOI18N
        MainexportTransMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainexportTransMenuitemActionPerformed(evt);
            }
        });
        MainexportMenu.add(MainexportTransMenuitem);
        MainexportMenu.add(Mainexport2Separator);

        MainexportYoutubeMenuitem.setText(bundle.getString("Easytranscript.MainexportYoutubeMenuitem.text")); // NOI18N
        MainexportYoutubeMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainexportYoutubeMenuitemActionPerformed(evt);
            }
        });
        MainexportMenu.add(MainexportYoutubeMenuitem);

        MainfileMenu.add(MainexportMenu);

        MainprintMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        MainprintMenuitem.setText(bundle.getString("Easytranscript.MainprintMenuitem.text")); // NOI18N
        MainprintMenuitem.setEnabled(false);
        MainprintMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainprintMenuitemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainprintMenuitem);

        MainProjectInfosMenuitem.setText(bundle.getString("Easytranscript.MainProjectInfosMenuitem.text")); // NOI18N
        MainProjectInfosMenuitem.setEnabled(false);
        MainProjectInfosMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainProjectInfosMenuitemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MainProjectInfosMenuitem);

        MaincloseProjectMenuitem.setText(bundle.getString("Easytranscript.MaincloseProjectMenuitem.text")); // NOI18N
        MaincloseProjectMenuitem.setEnabled(false);
        MaincloseProjectMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincloseProjectMenuitemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MaincloseProjectMenuitem);
        MainfileMenu.add(Mainfilemenu2Seperator);

        MaincloseMenuitem.setText(bundle.getString("Easytranscript.MaincloseMenuitem.text")); // NOI18N
        MaincloseMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincloseMenuitemActionPerformed(evt);
            }
        });
        MainfileMenu.add(MaincloseMenuitem);

        MainMenubar.add(MainfileMenu);
        MainMenubar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10,0), "none");

        MainfileMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        MainfileMenu.setMnemonic(MainfileMenu.getText().charAt(0));

        MaineditMenu.setText(bundle.getString("Easytranscript.MaineditMenu.text")); // NOI18N
        MaineditMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaineditMenuActionPerformed(evt);
            }
        });

        MainundoMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        MainundoMenuitem.setText(bundle.getString("Easytranscript.MainundoMenuitem.text")); // NOI18N
        MainundoMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainundoMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainundoMenuitem);

        MainredoMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        MainredoMenuitem.setText(bundle.getString("Easytranscript.MainredoMenuitem.text")); // NOI18N
        MainredoMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainredoMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainredoMenuitem);
        MaineditMenu.add(MaineditSeperator);

        MainboldMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MainboldMenuitem.setText(bundle.getString("Easytranscript.MainboldMenuitem.text")); // NOI18N
        MainboldMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainboldMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainboldMenuitem);
        //bold_MI.addActionListener(new StyledEditorKit.BoldAction());

        MainitalicMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MainitalicMenuitem.setText(bundle.getString("Easytranscript.MainitalicMenuitem.text")); // NOI18N
        MainitalicMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainitalicMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainitalicMenuitem);

        MainunderlinedMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MainunderlinedMenuitem.setText(bundle.getString("Easytranscript.MainunderlinedMenuitem.text")); // NOI18N
        MainunderlinedMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainunderlinedMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainunderlinedMenuitem);
        MaineditMenu.add(Mainedit2Seperator);

        MaincopyMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        MaincopyMenuitem.setText(bundle.getString("Easytranscript.MaincopyMenuitem.text")); // NOI18N
        MaincopyMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincopyMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MaincopyMenuitem);

        MaincutMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        MaincutMenuitem.setText(bundle.getString("Easytranscript.MaincutMenuitem.text")); // NOI18N
        MaincutMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincutMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MaincutMenuitem);

        MainpasteMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        MainpasteMenuitem.setText(bundle.getString("Easytranscript.MainpasteMenuitem.text")); // NOI18N
        MainpasteMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainpasteMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainpasteMenuitem);
        MaineditMenu.add(jSeparator1);

        MaintimestampMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        MaintimestampMenuitem.setText(bundle.getString("Easytranscript.MaintimestampMenuitem.text")); // NOI18N
        MaintimestampMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintimestampMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MaintimestampMenuitem);

        MainfontsizePlusMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.CTRL_MASK));
        MainfontsizePlusMenuitem.setText(bundle.getString("Easytranscript.MainfontsizePlusMenuitem.text")); // NOI18N
        MainfontsizePlusMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainfontsizePlusMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainfontsizePlusMenuitem);

        MainFontsizeMinusMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.CTRL_MASK));
        MainFontsizeMinusMenuitem.setText(bundle.getString("Easytranscript.MainFontsizeMinusMenuitem.text")); // NOI18N
        MainFontsizeMinusMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainFontsizeMinusMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainFontsizeMinusMenuitem);
        MaineditMenu.add(Mainedit3Seperator);

        MainchangeSpokemanCheckboxmenuitem.setSelected(true);
        MainchangeSpokemanCheckboxmenuitem.setText(bundle.getString("Easytranscript.MainchangeSpokemanCheckboxmenuitem.text")); // NOI18N
        MainchangeSpokemanCheckboxmenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainchangeSpokemanCheckboxmenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainchangeSpokemanCheckboxmenuitem);

        MainsearchReplaceMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        MainsearchReplaceMenuitem.setText(bundle.getString("Easytranscript.MainsearchReplaceMenuitem.text")); // NOI18N
        MainsearchReplaceMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainsearchReplaceMenuitemActionPerformed(evt);
            }
        });
        MaineditMenu.add(MainsearchReplaceMenuitem);

        MainMenubar.add(MaineditMenu);
        MaineditMenu.setEnabled(false);
        MaineditMenu.setMnemonic(MaineditMenu.getText().charAt(0));

        MaintoolsMenu.setText(bundle.getString("Easytranscript.MaintoolsMenu.text")); // NOI18N
        MaintoolsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintoolsMenuActionPerformed(evt);
            }
        });

        MaintoolsAlignmentMenu.setText(bundle.getString("Easytranscript.MaintoolsAlignmentMenu.text")); // NOI18N

        MainAlignmentButtonGroup.add(MaintoolsAlignmentLeftRadioButtonMenuItem);
        MaintoolsAlignmentLeftRadioButtonMenuItem.setSelected(true);
        MaintoolsAlignmentLeftRadioButtonMenuItem.setText(bundle.getString("Easytranscript.MaintoolsAlignmentLeftRadioButtonMenuItem.text")); // NOI18N
        MaintoolsAlignmentLeftRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintoolsAlignmentLeftRadioButtonMenuItemActionPerformed(evt);
            }
        });
        MaintoolsAlignmentMenu.add(MaintoolsAlignmentLeftRadioButtonMenuItem);

        MainAlignmentButtonGroup.add(MaintoolsRightRadioButtonMenuItem);
        MaintoolsRightRadioButtonMenuItem.setText(bundle.getString("Easytranscript.MaintoolsRightRadioButtonMenuItem.text")); // NOI18N
        MaintoolsRightRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintoolsRightRadioButtonMenuItemActionPerformed(evt);
            }
        });
        MaintoolsAlignmentMenu.add(MaintoolsRightRadioButtonMenuItem);

        MaintoolsMenu.add(MaintoolsAlignmentMenu);

        MaintoolsTunerMenu.setText(bundle.getString("Easytranscript.MaintoolsTunerMenu.text")); // NOI18N

        MaintoolsTunerPlusMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.ALT_MASK));
        MaintoolsTunerPlusMenuitem.setText(bundle.getString("Easytranscript.MaintoolsTunerPlusMenuitem.text")); // NOI18N
        MaintoolsTunerPlusMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintoolsTunerPlusMenuitemActionPerformed(evt);
            }
        });
        MaintoolsTunerMenu.add(MaintoolsTunerPlusMenuitem);

        MaintoolsTunerMinusMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.ALT_MASK));
        MaintoolsTunerMinusMenuitem.setText(bundle.getString("Easytranscript.MaintoolsTunerMinusMenuitem.text")); // NOI18N
        MaintoolsTunerMinusMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintoolsTunerMinusMenuitemActionPerformed(evt);
            }
        });
        MaintoolsTunerMenu.add(MaintoolsTunerMinusMenuitem);

        MaintoolsMenu.add(MaintoolsTunerMenu);

        MaintimeMenuitem.setText(bundle.getString("Easytranscript.MaintimeMenuitem.text")); // NOI18N
        MaintimeMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintimeMenuitemActionPerformed(evt);
            }
        });
        MaintoolsMenu.add(MaintimeMenuitem);

        MainshortcutsMenuitem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.ALT_MASK));
        MainshortcutsMenuitem.setText(bundle.getString("Easytranscript.MainshortcutsMenuitem.text")); // NOI18N
        MainshortcutsMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainshortcutsMenuitemActionPerformed(evt);
            }
        });
        MaintoolsMenu.add(MainshortcutsMenuitem);

        MainMenubar.add(MaintoolsMenu);
        MaintoolsMenu.setEnabled(false);
        MaintoolsMenu.setMnemonic(MaintoolsMenu.getText().charAt(0));

        MainhelpMenu.setText(bundle.getString("Easytranscript.MainhelpMenu.text")); // NOI18N

        MainconfigMenuitem.setText(bundle.getString("Easytranscript.MainconfigMenuitem.text")); // NOI18N
        MainconfigMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainconfigMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MainconfigMenuitem);
        MainhelpMenu.add(MaintoolsSep1Separator);

        MainhelpMenuitem.setText(bundle.getString("Easytranscript.MainhelpMenuitem.text")); // NOI18N
        MainhelpMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainhelpMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MainhelpMenuitem);

        MainsupportMenuitem.setText(bundle.getString("Easytranscript.MainsupportMenuitem.text")); // NOI18N
        MainsupportMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainsupportMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MainsupportMenuitem);

        MaindonateMenuitem.setText(bundle.getString("Easytranscript.MaindonateMenuitem.text")); // NOI18N
        MaindonateMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaindonateMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MaindonateMenuitem);

        MainSocialMenuitem.setText(bundle.getString("Easytranscript.MainSocialMenuitem.text")); // NOI18N
        MainSocialMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MainSocialMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MainSocialMenuitem);

        MaininfoMenuitem.setText(bundle.getString("Easytranscript.MaininfoMenuitem.text")); // NOI18N
        MaininfoMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaininfoMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MaininfoMenuitem);

        MaincheckUpdatesMenuitem.setText(bundle.getString("Easytranscript.MaincheckUpdatesMenuitem.text")); // NOI18N
        MaincheckUpdatesMenuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaincheckUpdatesMenuitemActionPerformed(evt);
            }
        });
        MainhelpMenu.add(MaincheckUpdatesMenuitem);

        MainMenubar.add(MainhelpMenu);
        MainhelpMenu.setMnemonic(MainhelpMenu.getText().charAt(0));

        setJMenuBar(MainMenubar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MainSliderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MainToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MainCenterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1278, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BufferingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MainToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainCenterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainSliderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BufferingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        MainToolbar.setVisible(false);
        MainButtonPanel.setVisible(false);

        pack();

    }// </editor-fold>//GEN-END:initComponents

    private void MainTimePlayerbackwButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainTimePlayerbackwButtonActionPerformed
        if (!useFXPlayer) {
            player.jump(-player.getInterval());
        } else {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    PlayerFX.jump(-PlayerFX.getInterval());
                }
            });
        }
    }//GEN-LAST:event_MainTimePlayerbackwButtonActionPerformed

    private void MainTimePlayerforwButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainTimePlayerforwButtonActionPerformed
        if (!useFXPlayer) {
            player.jump(player.getInterval());
        } else {
            PlayerFX.jump(PlayerFX.getInterval());
        }
    }//GEN-LAST:event_MainTimePlayerforwButtonActionPerformed

    private void MainToolbarundoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarundoButtonActionPerformed
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_MainToolbarundoButtonActionPerformed

    private void MainToolbarredoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarredoButtonActionPerformed
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_MainToolbarredoButtonActionPerformed

    private void MainSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MainSliderMouseReleased
        if (!useFXPlayer) {
            player.getMediaPlayer().setTime(MainSlider.getValue() * 1000);

        } else {
            PlayerFX.getMediaPlayer().seek(new javafx.util.Duration(MainSlider.getValue() * 1000));
        }

//        currentPlayerTime = MainSlider.getValue() * 1000;
//        new UpdateTimer().updateTimeStrings();
//
//        MainTimeLabel.setText(StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string);
//        String stern = "";
//        if (TranscriptHandler.isUnsaved()) {
//            stern = "*";
//        }
//        setTitle("easytranscript - " + transcriptName + stern + " - " + MainTimeLabel.getText() + " " + messages.getString("vonTime") + " " + MainTotalLabel.getText());
        updatetimer.cancel();
        MainTimeMilliLabel.setText("-" + Millisekunden_current);

        if (!useFXPlayer) {
            if (savedState) {
                player.getMediaPlayer().play();
            }
        } else if (savedState) {
            PlayerFX.getMediaPlayer().play();

            easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.pause);

        }

    }//GEN-LAST:event_MainSliderMouseReleased

    private void MainSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MainSliderMousePressed
        if (!useFXPlayer) {
            savedState = player.getMediaPlayer().isPlaying();
            player.getMediaPlayer().setPause(true);

        } else {
            savedState = PlayerFX.getMediaPlayer().getStatus().equals(Status.PLAYING);
            PlayerFX.getMediaPlayer().pause();
        }
        updatetimer = new java.util.Timer();
        task = new UpdateTimeWhilePressed();

        updatetimer.scheduleAtFixedRate(task, 0, 100);

    }//GEN-LAST:event_MainSliderMousePressed

    private void MainToolbarSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarSaveButtonActionPerformed
        TranscriptHandler.save(transcriptPath, false, false);
    }//GEN-LAST:event_MainToolbarSaveButtonActionPerformed

    private void MainToolbarSaveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarSaveAsButtonActionPerformed
        this.MainSaveAsMenuItemActionPerformed(evt);

    }//GEN-LAST:event_MainToolbarSaveAsButtonActionPerformed

    private void MainToolbarTimestampButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarTimestampButtonActionPerformed
        try {
            MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(),
                    " #" + StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string + "-" + Millisekunden_current + "# ", null);
        } catch (BadLocationException e) {
            logger.log(Level.WARNING, null, e);
        }
    }//GEN-LAST:event_MainToolbarTimestampButtonActionPerformed

    private void MainToolbarConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarConfigButtonActionPerformed
        if (!configFrame.isVisible()) {

            if (prop.getBoolProperty("ConfigStenoActivate")) {
                steno.load();
            }

        }
        if (getOS().equals("Mac")) {
            configFrame.getConfigUseFXPLayerCheckbox().setSelected(true);
            configFrame.getConfigUseFXPLayerCheckbox().setEnabled(false);
        }
        configFrame.setVisible(true);
        configFrame.toFront();

    }//GEN-LAST:event_MainToolbarConfigButtonActionPerformed

    private void MainTimeIntervalSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MainTimeIntervalSliderStateChanged
        if (!useFXPlayer) {
            if (player != null) {
                player.setInterval(MainTimeIntervalSlider.getValue() * 1000);
            }
        } else if (PlayerFX.getMediaPlayer() != null) {
            PlayerFX.setInterval(MainTimeIntervalSlider.getValue() * 1000);
        }
    }//GEN-LAST:event_MainTimeIntervalSliderStateChanged

    private void MainToolbarSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarSearchButtonActionPerformed
//        mySD.setVisible(true);
        this.MainCenterEditorEditorPane.requestFocus();
        if (!searchFrame.isVisible()) {

            searchFrame.setVisible(true);
            searchFrame.getSearchSearchphraseTextfield().setText("");
            searchFrame.getSearchReplaceTextfield().setText("");
            searchFrame.getSearchStatus1Label().setVisible(false);
            searchFrame.getSearchStatus2Label().setVisible(false);
        } else {
            searchFrame.requestFocus();
        }
    }//GEN-LAST:event_MainToolbarSearchButtonActionPerformed

    private void MainToolbarZoomComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarZoomComboboxActionPerformed
        int val = MainCenterEditorScrollpane.getVerticalScrollBar().getValue();
        float zoomOld = Integer.parseInt(((String) MainToolbarZoomCombobox.getSelectedItem()).substring(0, 3)) / 100;
        zoomFactor = Double.parseDouble((MainToolbarZoomCombobox.getSelectedItem().toString().substring(0, 3))) / 100;
        MainCenterEditorEditorPane.getDocument().putProperty("ZOOM_FACTOR", zoomFactor); //!!

        MainCenterEditorEditorPane.requestFocus();
        float zoomNew = Integer.parseInt(((String) MainToolbarZoomCombobox.getSelectedItem()).substring(0, 3)) / 100;
        if (zoomNew > zoomOld) {

            MainCenterEditorScrollpane.getVerticalScrollBar().setValue((int) (val + val * (zoomNew - zoomOld)));
        }
    }//GEN-LAST:event_MainToolbarZoomComboboxActionPerformed

    private void MainToolbarFontsizeComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarFontsizeComboboxActionPerformed
        if (fontsizeHasFocus) {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            AttributeSet attr = ((StyledDocument) MainCenterEditorEditorPane.getDocument()).getCharacterElement(MainCenterEditorEditorPane.getSelectionStart()).getAttributes();
            try {
                if (MainToolbarFontsizeCombobox.getSelectedItem() != null) {
                    if (!MainToolbarFontsizeCombobox.getSelectedItem().equals(" ")) {
                        int fontsize = Integer.parseInt(MainToolbarFontsizeCombobox.getSelectedItem().toString().replaceAll(" ", ""));

                        StyleConstants.setFontSize(attrs, fontsize + Variables.performanceModeFontSizeIncrease);

                        if (MainCenterEditorEditorPane.getSelectionStart() != MainCenterEditorEditorPane.getSelectionEnd()) {
                            ((StyledDocument) MainCenterEditorEditorPane.getDocument()).setCharacterAttributes(MainCenterEditorEditorPane.getSelectionStart(), MainCenterEditorEditorPane.getSelectionEnd() - (MainCenterEditorEditorPane.getSelectionStart()), attrs, false);
                        } else {
                            currentAttributeSet = attrs;
                            casChanged = true;
                            // MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), " ", attrs);

                        }
                    }
                }

                MainCenterEditorEditorPane.requestFocus();
                fontsizeHasFocus = false;

            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, "Nicht erlaubte Fontgröße.", ex);

                MainToolbarFontsizeCombobox.setSelectedItem(String.valueOf(attr.getAttribute(StyleConstants.FontSize)));
            }
//            } catch (BadLocationException ex) {
//                Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }//GEN-LAST:event_MainToolbarFontsizeComboboxActionPerformed

    private void MainTimeExpandCollapsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainTimeExpandCollapsButtonActionPerformed

        MainTimeButtonPanel.setVisible(!MainTimeButtonPanel.isVisible());
        MainSliderPanel.setVisible(!MainSliderPanel.isVisible());
        if (MainTimeButtonPanel.isVisible()) {
            MainTimeExpandCollapsButton.setIcon(less);
        }

        if (!MainTimeButtonPanel.isVisible()) {
            MainTimeExpandCollapsButton.setIcon(more);
        }

    }//GEN-LAST:event_MainTimeExpandCollapsButtonActionPerformed

    private void MainToolbarTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarTimeButtonActionPerformed

        workTime.applyWorkTime();
      /*  if (zeitFrame.getZeitHourlyRadiobutton().isSelected()) {
            zeitFrame.getZeitHourlySpinner().getChangeListeners()[0].stateChanged(null);
        }
        if (zeitFrame.getZeitFixRadiobutton().isSelected()) {
            zeitFrame.getZeitFixSpinner().getChangeListeners()[0].stateChanged(null);
        }
        zeitFrame.getZeitActivateCheckbox().getActionListeners()[0].actionPerformed(null);*/
        zeitFrame.setVisible(true);


    }//GEN-LAST:event_MainToolbarTimeButtonActionPerformed

    private void MainCenterEditorEditorPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MainCenterEditorEditorPaneMouseClicked

        pointOffsetTextfield = MainCenterEditorEditorPane.viewToModel(evt.getPoint());
        try {
            String abPoint = MainCenterEditorEditorPane.getDocument().getText(pointOffsetTextfield, MainCenterEditorEditorPane.getDocument().getLength() - pointOffsetTextfield);

            int index = abPoint.indexOf("# ");

            if (index < 11 && index > -1) {

                abPoint = MainCenterEditorEditorPane.getDocument().getText(pointOffsetTextfield - (11 - index), 12);
                long Zeitstempel_Stunde = Long.valueOf(abPoint.substring(1, 3)) * 1000 * 60 * 60;
                long Zeitstempel_Minute = Long.valueOf(abPoint.substring(4, 6)) * 1000 * 60;
                long Zeitstempel_Sekunde = Long.valueOf(abPoint.substring(7, 9)) * 1000;
                long Zeitstempel_MilliSek = Long.parseLong(abPoint.substring(10, 11)) * 100;

                if (!useFXPlayer) {
                    if (player.isMediaLoaded()) {
                        currentPlayerTime = Zeitstempel_Stunde + Zeitstempel_Minute + Zeitstempel_Sekunde + Zeitstempel_MilliSek;
                        player.getMediaPlayer().setTime(currentPlayerTime);
                        player.getMediaPlayer().play();
//                       if (player.getMediaPlayer().isPlaying()){
//                           easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.pause);
//                       }else{
//                           easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.start);
//                       }

                    }
                } else {

                    currentPlayerTime = Zeitstempel_Stunde + Zeitstempel_Minute + Zeitstempel_Sekunde + Zeitstempel_MilliSek;
                    PlayerFX.getMediaPlayer().seek(new javafx.util.Duration(currentPlayerTime));
                    PlayerFX.getMediaPlayer().play();
                    if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PLAYING)) {
                        easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.start);
                    } else if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PAUSED)) {
                        easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.pause);
                    }

                }
            }

        } catch (BadLocationException ex) {
            //   Logger.getLogger(Jtranscript.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_MainCenterEditorEditorPaneMouseClicked

    private void MainCenterEditorEditorPaneCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_MainCenterEditorEditorPaneCaretUpdate

        (new CaretPositionInformation()).execute();


    }//GEN-LAST:event_MainCenterEditorEditorPaneCaretUpdate

    private void MainfileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainfileMenuActionPerformed
        infoFrame.setVisible(true);
    }//GEN-LAST:event_MainfileMenuActionPerformed

    private void MainconfigMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainconfigMenuitemActionPerformed
        MainToolbarConfigButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainconfigMenuitemActionPerformed

    private void MaincreateProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincreateProjectMenuItemActionPerformed
        clearProjWindowValues();
        int n;
        if (TranscriptHandler.isUnsaved()) {

            Object[] options = {messages.getString("Ja"),
                messages.getString("Nein"),
                messages.getString("Abbrechen")};
            n = JOptionPane.showOptionDialog(null,
                    messages.getString("WarningCloseProject"),
                    messages.getString("Frage"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (n == 0) {
                TranscriptHandler.save(transcriptPath, false, false);
            }

            if (n == 2) {
                return;
            }

        }

        TranscriptHandler.close();
        setTitle("easytranscript");

        if (prop.getBoolProperty("PPinfoskipCb")) {
            newProjectFrame.getNPMainTabbedPane().setSelectedIndex(1);
        }

        newProjectFrame.setVisible(true);

    }//GEN-LAST:event_MaincreateProjectMenuItemActionPerformed

    private void MainopenProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainopenProjectMenuItemActionPerformed
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(messages.getString("etpFiletype"), "etp"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(opFolder + "Projects/"));
        int ruckgabe = fileChooser.showOpenDialog(null);
        fileChooser.getSelectedFile();
        if (ruckgabe == JFileChooser.CANCEL_OPTION) {

            return;
        }

        if (ruckgabe == JFileChooser.APPROVE_OPTION) {
            TranscriptHandler.read(fileChooser.getSelectedFile().getAbsolutePath());
        }


    }//GEN-LAST:event_MainopenProjectMenuItemActionPerformed

    private void MaininfoMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaininfoMenuitemActionPerformed
        Variables.infoFrame.getInfoScrollPane().getVerticalScrollBar().setValue(0);
        Variables.infoFrame.setVisible(!Variables.infoFrame.isVisible());

    }//GEN-LAST:event_MaininfoMenuitemActionPerformed

    private void MaincloseMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincloseMenuitemActionPerformed
        terminate();
    }//GEN-LAST:event_MaincloseMenuitemActionPerformed

    private void MainsaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainsaveMenuItemActionPerformed

        TranscriptHandler.save(transcriptPath, false, false);

    }//GEN-LAST:event_MainsaveMenuItemActionPerformed

    private void MainSaveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainSaveAsMenuItemActionPerformed

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(messages.getString("etpFiletype"), "etp"));
        int ruckgabe = fileChooser.showSaveDialog(null);
        fileChooser.getSelectedFile();
        if (ruckgabe == JFileChooser.CANCEL_OPTION) {

            return;
        }

        String path = fileChooser.getSelectedFile().getAbsolutePath();
        if (!path.endsWith((".etp"))) {
            path = path + ".etp";
        }

        if ((new File(path)).exists()) {
            int response = JOptionPane.showConfirmDialog(null, messages.getString("WarningOverwrite"), messages.getString("WarningOverwriteTitle"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                this.MainSaveAsMenuItemActionPerformed(evt);
            }
        }

        TranscriptHandler.save(path, true, true);


    }//GEN-LAST:event_MainSaveAsMenuItemActionPerformed

    private void MainchangeProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainchangeProjectMenuItemActionPerformed

        changeProjectFrame.toggleChangeProjProp();

    }//GEN-LAST:event_MainchangeProjectMenuItemActionPerformed

    private void MainprintMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainprintMenuitemActionPerformed

        //    zeitFrame.getZeitTotalLabel().getText()
        //                    "easytranscript" + pN
        String pN = "";

        if (transcriptName != null) {
            pN = "-" + transcriptName;
        }
        new printDocument(MainCenterEditorEditorPane, "", "easytranscript " + pN).execute();


    }//GEN-LAST:event_MainprintMenuitemActionPerformed

    @SuppressWarnings("empty-statement")
    private void MainexportTransMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainexportTransMenuitemActionPerformed

        ProcessBuilder builder_t = new ProcessBuilder("pandoc", "--version");
        try {
            builder_t.start();

        } catch (IOException ex) {

            logger.log(Level.WARNING, "Pandoc wurde nicht gefunden.");

            Object[] options = {"OK", messages.getString("Download")};

            int n = JOptionPane.showOptionDialog(null,
                    messages.getString("PandocInformation"), messages.getString("PandocInformationTitle"),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n != 1) {
                return;
            }

            URL url;
            try {
                url = new URL("http://johnmacfarlane.net/pandoc/installing.html");
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(url.toURI());

                    } catch (IOException | URISyntaxException e) {
                        logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("errorOpeningLink")), e);

                        return;
                    }
                }
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("errorOpeningLink")), e);
                return;
            }
            return;
        }

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Microsoft Word (.docx)", "docx"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Microsoft Word (.doc)", "doc"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Plain Text (.txt)", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(messages.getString("htmlFiletype"), "html"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(messages.getString("xmlFiletype"), "xml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(messages.getString("epubFiletype"), "epub"));
        fileChooser.setFileFilter(new FileNameExtensionFilter(messages.getString("odtFiletype"), "odt"));

        fileChooser.setAcceptAllFileFilterUsed(false);
        int ruckgabe = fileChooser.showSaveDialog(null);

        if (ruckgabe == JFileChooser.CANCEL_OPTION) {

            return;
        }

        if (ruckgabe == JFileChooser.APPROVE_OPTION) {
            TranscriptHandler.save(transcriptPath, false, false);

            String s1 = fileChooser.getSelectedFile().getAbsolutePath();

            if (!s1.endsWith((((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0]))) {
                s1 = s1 + "." + (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0]);
            }

            if ((new File(s1)).exists()) {
                int response = JOptionPane.showConfirmDialog(null, messages.getString("WarningOverwrite"), messages.getString("WarningOverwriteTitle"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    MainexportTransMenuitemActionPerformed(evt);
                } else if (response == JOptionPane.CLOSED_OPTION) {
                    MainexportTransMenuitemActionPerformed(evt);
                }
            }

            String path = fileChooser.getSelectedFile().getAbsolutePath();

            logger.log(Level.INFO, null, "Pandoc Export - opfolder " + opFolder);

            if (!path.endsWith("." + (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0]))) {
                path = path + "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
            }

            if (!new File(opFolder + "ext/Pandoc").exists()) {
                new File(opFolder + "ext/Pandoc/").mkdirs();
            }

            if (!new File(opFolder + "ext/Pandoc").exists()) {
                logger.log(Level.SEVERE, null, "Keine Schreibrechte im ext/Pandoc!");
            }

            try {
                new File(opFolder + "ext/Pandoc/in.html").delete();
                new File(opFolder + "ext/Pandoc/in2.html").delete();
                new File(opFolder + "ext/Pandoc/tmp.rtf").delete();
                TranscriptHandler.exportRTF(opFolder + "ext/Pandoc/tmp.rtf");
                try (Writer out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(opFolder + "ext/Pandoc/in.html"), StandardCharsets.UTF_8))) {
                    JEditorPane p = new JEditorPane();
                    p.setContentType("text/rtf");
                    p.setEditorKit((EditorKit) MainCenterEditorEditorPane.getEditorKit().clone());

                    p.read(new FileInputStream(new File(opFolder + "ext/Pandoc/tmp.rtf")), p.getDocument());

                    while (replaceSpaces_HtmlFix(p, " ", "&nbsp;") > -1);
                    p.setCaretPosition(0);
                    while (replaceSpaces_HtmlFix(p, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;") > -1);

                    MinimalHTMLWriter wr = new MinimalHTMLWriter(out, (StyledDocument) p.getDocument());
                    wr.write();
                }

            } catch (IOException | BadLocationException ex) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("ZwischenExport")), ex);

                return;
            }

            try {

                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("odt")) {
                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".odt")) {
                        s = s + ".odt";
                    }
                    ProcessBuilder builder = new ProcessBuilder("pandoc", "-s", "-S", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                    Process p = builder.start();
                }
                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("doc")) {
                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".doc")) {
                        s = s + ".doc";
                    }
                    ProcessBuilder builder = new ProcessBuilder("pandoc", "-s", "-S", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                    Process p = builder.start();
                }
                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("docx")) {
                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".docx")) {
                        s = s + ".docx";
                    }

                    int n = JOptionPane.showConfirmDialog(null, messages.getString("ExportDocumentNotExact"), "", JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        ProcessBuilder builder = new ProcessBuilder("pandoc", "-s", "-S", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                        Process p = builder.start();
                    } else {
                        MainexportTransMenuitemActionPerformed(evt);
                    }
                }

                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("epub")) {
                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".epub")) {
                        s = s + ".epub";
                    }
                    ProcessBuilder builder = new ProcessBuilder("pandoc", "-S", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                    Process p = builder.start();
                }

                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("xml")) {
                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".xml")) {
                        s = s + ".xml";
                    }
                    ProcessBuilder builder = new ProcessBuilder("pandoc", "-s", "-t", "opendocument", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                    Process p = builder.start();
                }

                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("html")) {
                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".html")) {
                        s = s + ".html";
                    }
                    ProcessBuilder builder = new ProcessBuilder("pandoc", "-s", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                    Process p = builder.start();

                }

                if (((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0].equals("txt")) {

                    String s = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!s.endsWith(".txt")) {
                        s = s + ".txt";
                    }
                    ProcessBuilder builder = new ProcessBuilder("pandoc", "-s", "\"" + opFolder + "ext/Pandoc/in.html\"", "-o", "\"" + s + "\"");
                    Process p = builder.start();

                }

                if (new File(path).exists()) {
                    new File(path).delete();
                }

                if (new File(opFolder + "ext/Pandoc/out." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0]).exists()) {
                    new File(opFolder + "ext/Pandoc/out." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0]).delete();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("ExportPandoc")), e);

            }
        }
    }//GEN-LAST:event_MainexportTransMenuitemActionPerformed

    private void MainToolbarCheckUpdatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarCheckUpdatesButtonActionPerformed
        new UpdateChecker(true).execute();

    }//GEN-LAST:event_MainToolbarCheckUpdatesButtonActionPerformed

    private void MainsupportMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainsupportMenuitemActionPerformed
        if (!supportFrame.isVisible()) {
            supportFrame.getSupportFailureCheckbox().setSelected(false);
            supportFrame.getSupportFeatureCheckbox().setSelected(false);
            supportFrame.getSupportQuestionCheckbox().setSelected(false);
            supportFrame.getSupportUpdatesCheckbox().setSelected(false);
            supportFrame.getSupportMediaplayerCheckbox().setSelected(false);
            supportFrame.getSupportEditorCheckbox().setSelected(false);
            supportFrame.getSupportProjectsCheckbox().setSelected(false);
            supportFrame.getSupportImExCheckbox().setSelected(false);
            supportFrame.getSupportTimeCheckbox().setSelected(false);
            supportFrame.getSupportDesignCheckbox().setSelected(false);
            supportFrame.getSupportsthelseCheckbox().setSelected(false);
            supportFrame.getSupportMessageTextarea().setText("");

            supportFrame.setVisible(true);
        }

    }//GEN-LAST:event_MainsupportMenuitemActionPerformed

    private void MainduplicateTransMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainduplicateTransMenuitemActionPerformed
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        fileChooser.setFileFilter(new FileNameExtensionFilter(messages.getString("rtfFiletype"), "rtf"));

        fileChooser.setAcceptAllFileFilterUsed(false);
        int ruckgabe = fileChooser.showSaveDialog(null);
        fileChooser.getSelectedFile();
        if (ruckgabe == JFileChooser.CANCEL_OPTION) {

            return;
        }

        String pfad = fileChooser.getSelectedFile().getAbsolutePath();
        if (!pfad.endsWith(".rtf")) {
            pfad = pfad + ".rtf";
        }

        if (new File(pfad).exists()) {
            int response = JOptionPane.showConfirmDialog(null, messages.getString("WarningOverwrite"), messages.getString("WarningOverwriteTitle"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                this.MainduplicateTransMenuitemActionPerformed(evt);

            } else if (response == JOptionPane.CLOSED_OPTION) {
                this.MainduplicateTransMenuitemActionPerformed(evt);
            }

        }

        try {
            TranscriptHandler.exportRTF(pfad);
        } catch (IOException | BadLocationException ex) {
            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("DuplicationFailure")), ex);

        }

    }//GEN-LAST:event_MainduplicateTransMenuitemActionPerformed

    private void MainTimeTunerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainTimeTunerButtonActionPerformed
        tunerDialog.setVisible(!tunerDialog.isVisible());
        tunerDialog.setLocation(MouseInfo.getPointerInfo().getLocation().x - tunerDialog.getWidth() / 2, MouseInfo.getPointerInfo().getLocation().y - tunerDialog.getHeight());

    }//GEN-LAST:event_MainTimeTunerButtonActionPerformed

    private void MaincloseProjectMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincloseProjectMenuitemActionPerformed
        int n;
        if (TranscriptHandler.isUnsaved()) {

            Object[] options = {messages.getString("Ja"),
                messages.getString("Nein"),
                messages.getString("Abbrechen")};
            n = JOptionPane.showOptionDialog(null,
                    messages.getString("WarningCloseProject"),
                    messages.getString("Frage"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (n == 0) {
                TranscriptHandler.save(transcriptPath, false, false, () -> {
                    TranscriptHandler.close();
                    setTitle("easytranscript");
                    return null;
                });
            }
            else if (n == 2) {
                return;
            } else {
                TranscriptHandler.close();
            }
            setTitle("easytranscript");

        }else {
            TranscriptHandler.close();
            setTitle("easytranscript");
        }

    }//GEN-LAST:event_MaincloseProjectMenuitemActionPerformed

    private void MainprojExportMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainprojExportMenuitemActionPerformed
        if (!exportFrame.isVisible()) {
            exportFrame.getExportexportButton().setEnabled(true);
            exportFrame.getExportCancelButton().setEnabled(true);
            exportFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            exportFrame.setVisible(true);
            exportFrame.setLocationRelativeTo(null);
            exportFrame.getExportPathTextfield().setText("");
            exportFrame.getExportstatusProgressbar().setValue(0);
            exportFrame.getExportstatusProgressbar().setStringPainted(false);

        }

    }//GEN-LAST:event_MainprojExportMenuitemActionPerformed

    private void MainimportProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainimportProjectMenuItemActionPerformed

        importFrame = new ImportFrame();
        importFrame.setVisible(true);

    }//GEN-LAST:event_MainimportProjectMenuItemActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        startFrame.toFront();
        startFrame.repaint();
        startFrame.requestFocus();
        updateFrame.toFront();
        updateFrame.repaint();
        news.toFront();
        news.repaint();
        news.requestFocus();
    }//GEN-LAST:event_formWindowOpened

    private void MaindonateMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaindonateMenuitemActionPerformed

        new Tools().spenden();

    }//GEN-LAST:event_MaindonateMenuitemActionPerformed

    private void MainToolbarChangeSpokemanTogglebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarChangeSpokemanTogglebuttonActionPerformed
        prop.setBoolProperty("Sprecherwechsel", MainToolbarChangeSpokemanTogglebutton.isSelected());
        try {
            prop.save();
        } catch (IOException e) {
            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("SavingConfig")), e);

        }
        prop.applyProperties();
    }//GEN-LAST:event_MainToolbarChangeSpokemanTogglebuttonActionPerformed

    private void MainboldMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainboldMenuitemActionPerformed
        MainToolbarBoldButton.getActionListeners()[0].actionPerformed(evt);
        MainToolbarBoldButton.setSelected(!MainToolbarBoldButton.isSelected());
    }//GEN-LAST:event_MainboldMenuitemActionPerformed

    private void MainitalicMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainitalicMenuitemActionPerformed
        MainToolbarItalicButton.getActionListeners()[0].actionPerformed(evt);
        MainToolbarItalicButton.setSelected(!MainToolbarItalicButton.isSelected());
    }//GEN-LAST:event_MainitalicMenuitemActionPerformed

    private void MainunderlinedMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainunderlinedMenuitemActionPerformed
        MainToolbarUnderlinedButton.getActionListeners()[0].actionPerformed(evt);
        MainToolbarUnderlinedButton.setSelected(!MainToolbarUnderlinedButton.isSelected());
    }//GEN-LAST:event_MainunderlinedMenuitemActionPerformed

    private void MainundoMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainundoMenuitemActionPerformed
        try {
            undoRedo = -1;
            undoManager.undo();

        } catch (CannotUndoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_MainundoMenuitemActionPerformed

    private void MainredoMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainredoMenuitemActionPerformed
        try {
            undoRedo = 1;
            undoManager.redo();
        } catch (CannotRedoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_MainredoMenuitemActionPerformed

    private void MaineditMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaineditMenuActionPerformed

    }//GEN-LAST:event_MaineditMenuActionPerformed

    private void MainsearchReplaceMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainsearchReplaceMenuitemActionPerformed
        this.MainToolbarSearchButtonActionPerformed(evt);

    }//GEN-LAST:event_MainsearchReplaceMenuitemActionPerformed

    private void MaincopyMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincopyMenuitemActionPerformed
        MainToolbarcopyButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MaincopyMenuitemActionPerformed

    private void MaincutMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincutMenuitemActionPerformed
        MainToolbarCutButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MaincutMenuitemActionPerformed

    private void MainpasteMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainpasteMenuitemActionPerformed
        MainToolbarPasteButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainpasteMenuitemActionPerformed

    private void MaintimestampMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintimestampMenuitemActionPerformed
        this.MainToolbarTimestampButtonActionPerformed(evt);

    }//GEN-LAST:event_MaintimestampMenuitemActionPerformed

    private void MainshortcutsMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainshortcutsMenuitemActionPerformed

        tastenCheckFrame.setVisible(!tastenCheckFrame.isVisible());
        MainCenterEditorEditorPane.requestFocus();

    }//GEN-LAST:event_MainshortcutsMenuitemActionPerformed

    private void MaintimeMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintimeMenuitemActionPerformed
        MainToolbarTimeButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MaintimeMenuitemActionPerformed

    private void MaintoolsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintoolsMenuActionPerformed

    }//GEN-LAST:event_MaintoolsMenuActionPerformed

    private void MainchangeSpokemanCheckboxmenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainchangeSpokemanCheckboxmenuitemActionPerformed
        MainToolbarChangeSpokemanTogglebutton.setSelected(!MainToolbarChangeSpokemanTogglebutton.isSelected());
        MainchangeSpokemanCheckboxmenuitem.setSelected(MainToolbarChangeSpokemanTogglebutton.isSelected());
        this.MainToolbarChangeSpokemanTogglebuttonActionPerformed(evt);

    }//GEN-LAST:event_MainchangeSpokemanCheckboxmenuitemActionPerformed

    private void MaincheckUpdatesMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincheckUpdatesMenuitemActionPerformed
        MainToolbarCheckUpdatesButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MaincheckUpdatesMenuitemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        terminate();
    }//GEN-LAST:event_formWindowClosing

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        updateVideoRatioFX();
        adjustSplitter();

    }//GEN-LAST:event_formComponentResized

    private void MainToolbarcopyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarcopyButtonActionPerformed
        saveStyledTextInformation();
        MainCenterEditorEditorPane.copy();


    }//GEN-LAST:event_MainToolbarcopyButtonActionPerformed

    private void MainToolbarPasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainToolbarPasteButtonActionPerformed

        int pos = MainCenterEditorEditorPane.getSelectionStart();
//        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
//        Transferable transfer = sysClip.getContents(null);
//        String data = "";
//        try {
//            data = (String) transfer.getTransferData(DataFlavor.stringFlavor);
//        } catch (UnsupportedFlavorException | IOException ex) {
//            Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
//        }

        int index = 0;
        //  if (data.length() != attrList.size() - 1) {
        //    logger.log(Level.WARNING, "Size mismatch: {0}!={1}", new Object[]{data.length(), attrList.size()});

        MainCenterEditorEditorPane.paste();

//        } else {
//            try {
//                
//              
//                DefaultStyledDocument doc = new DefaultStyledDocument();
//                
//                doc.insertString(0, data, null);
//                for (int i = 0; i < data.length(); i++) {
//                    doc.setCharacterAttributes(i, 1, attrList.get(index), false);
//
//                    index++;
//                }
//                
//                try {
//                  
//                    mergeDocument(doc, (DefaultStyledDocument) MainCenterEditorEditorPane.getDocument(), pos);
//                    
//                    int newPos = MainCenterEditorEditorPane.getCaretPosition();
//                    
//                    if (MainCenterEditorEditorPane.getText(pos, 0).equals("\n")){
//                    MainCenterEditorEditorPane.getDocument().remove(pos-1, 1);
//                    }
//                    if (MainCenterEditorEditorPane.getText(pos-1, 0).equals("\n")){
//                    MainCenterEditorEditorPane.getDocument().remove(newPos, 1);
//                    }
//                    
//                } catch (BadLocationException e) {
//
//                }
//            } catch (BadLocationException ex) {
//                Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }//GEN-LAST:event_MainToolbarPasteButtonActionPerformed

    private void MaincreateProjectFolderMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaincreateProjectFolderMenuitemActionPerformed

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(messages.getString("etmFiletype"), "etm"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(opFolder + "Projects/"));
        int ruckgabe = fileChooser.showSaveDialog(null);

        if (ruckgabe == JFileChooser.CANCEL_OPTION) {

            return;
        }

        if (ruckgabe == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(".etm")) {
                file = new File(file.getAbsolutePath() + ".etm");
            }
            projFolder.save(file.getAbsolutePath());
            projectFolderFrame.setTitle(file.getName());
            projectFolderFrame.setVisible(true);
        }


    }//GEN-LAST:event_MaincreateProjectFolderMenuitemActionPerformed

    private void MainopenProjectFolderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainopenProjectFolderMenuItemActionPerformed
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(messages.getString("etmFiletype"), "etm"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(opFolder + "Projects/"));
        int ruckgabe = fileChooser.showOpenDialog(null);

        if (ruckgabe == JFileChooser.CANCEL_OPTION) {

            return;
        }

        if (ruckgabe == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            projFolder.close();
            projFolder.load(file.getAbsolutePath());

        }

    }//GEN-LAST:event_MainopenProjectFolderMenuItemActionPerformed

    private void MainhelpMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainhelpMenuitemActionPerformed
        new Tools().browse("http://e-werkzeug.eu/software/easytranscript/versions/" + Variables.VERSION.getMajorVersion() + "." + Variables.VERSION.getMinorVersion() + "/help/" + currentLocale.getLanguage() + "/");
    }//GEN-LAST:event_MainhelpMenuitemActionPerformed

    private void MainActionCopyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionCopyMenuItemActionPerformed
        MainToolbarcopyButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionCopyMenuItemActionPerformed

    private void MainActionCutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionCutMenuItemActionPerformed
        MainToolbarCutButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionCutMenuItemActionPerformed

    private void MainActionPasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionPasteMenuItemActionPerformed
        MainToolbarPasteButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionPasteMenuItemActionPerformed

    private void MainCenterEditorEditorPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MainCenterEditorEditorPaneMouseReleased
        if (evt.isPopupTrigger()) {

            MainActionUnderlinedMenuItem.setEnabled(!(MainCenterEditorEditorPane.getSelectionEnd() == MainCenterEditorEditorPane.getSelectionStart()));
            MainActionItalicMenuItem.setEnabled(!(MainCenterEditorEditorPane.getSelectionEnd() == MainCenterEditorEditorPane.getSelectionStart()));
            MainActionBoldMenuItem.setEnabled(!(MainCenterEditorEditorPane.getSelectionEnd() == MainCenterEditorEditorPane.getSelectionStart()));

            this.MainActionPopupMenu.show(evt.getComponent(),
                    evt.getX(), evt.getY());

        }
    }//GEN-LAST:event_MainCenterEditorEditorPaneMouseReleased

    private void MainActionBoldMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionBoldMenuItemActionPerformed
        MainToolbarBoldButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionBoldMenuItemActionPerformed

    private void MainActionItalicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionItalicMenuItemActionPerformed
        MainToolbarItalicButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionItalicMenuItemActionPerformed

    private void MainActionUnderlinedMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionUnderlinedMenuItemActionPerformed
        MainToolbarUnderlinedButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionUnderlinedMenuItemActionPerformed

    private void MainActionSelectAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionSelectAllMenuItemActionPerformed
        MainCenterEditorEditorPane.selectAll();
    }//GEN-LAST:event_MainActionSelectAllMenuItemActionPerformed

    private void MainActionTimeStampMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainActionTimeStampMenuItemActionPerformed
        MainToolbarTimestampButton.getActionListeners()[0].actionPerformed(evt);
    }//GEN-LAST:event_MainActionTimeStampMenuItemActionPerformed

    private void MainexportYoutubeMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainexportYoutubeMenuitemActionPerformed
        try {
            String mydata;

            mydata = MainCenterEditorEditorPane.getDocument().getText(0, MainCenterEditorEditorPane.getDocument().getLength());

            new de.ewerkzeug.easytranscript.gui.components.YotubeFrame(mydata).setVisible(true);
        } catch (BadLocationException ex) {
            Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_MainexportYoutubeMenuitemActionPerformed

    private void MainSocialMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainSocialMenuitemActionPerformed
        new Tools().social();
    }//GEN-LAST:event_MainSocialMenuitemActionPerformed

    private void MainProjectInfosMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainProjectInfosMenuitemActionPerformed

        new MetaFrame(transcriptPath).setVisible(true);

    }//GEN-LAST:event_MainProjectInfosMenuitemActionPerformed

    private void MainfontsizePlusMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainfontsizePlusMenuitemActionPerformed
        int newIndex = MainToolbarFontsizeCombobox.getSelectedIndex() + 1;
        if (newIndex > MainToolbarFontsizeCombobox.getItemCount() - 1) {
            newIndex = MainToolbarFontsizeCombobox.getItemCount() - 1;
        }
        MainToolbarFontsizeCombobox.setSelectedIndex(newIndex);
        fontsizeHasFocus = true;
        MainToolbarFontsizeCombobox.actionPerformed(null);
    }//GEN-LAST:event_MainfontsizePlusMenuitemActionPerformed

    private void MainFontsizeMinusMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainFontsizeMinusMenuitemActionPerformed
        int newIndex = MainToolbarFontsizeCombobox.getSelectedIndex() - 1;
        if (newIndex < 0) {
            newIndex = 0;
        }
        MainToolbarFontsizeCombobox.setSelectedIndex(newIndex);
        fontsizeHasFocus = true;
        MainToolbarFontsizeCombobox.actionPerformed(null);
    }//GEN-LAST:event_MainFontsizeMinusMenuitemActionPerformed

    private void MaintoolsRightRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintoolsRightRadioButtonMenuItemActionPerformed
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
        ((StyledDocument) MainCenterEditorEditorPane.getDocument()).setParagraphAttributes(0, MainCenterEditorEditorPane.getDocument().getLength(), attribs, false);
    }//GEN-LAST:event_MaintoolsRightRadioButtonMenuItemActionPerformed

    private void MaintoolsAlignmentLeftRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintoolsAlignmentLeftRadioButtonMenuItemActionPerformed
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_LEFT);
        ((StyledDocument) MainCenterEditorEditorPane.getDocument()).setParagraphAttributes(0, MainCenterEditorEditorPane.getDocument().getLength(), attribs, false);
    }//GEN-LAST:event_MaintoolsAlignmentLeftRadioButtonMenuItemActionPerformed

    private void MainTimePlayerplayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MainTimePlayerplayButtonActionPerformed
        if (!useFXPlayer) {
            if (player.isMediaLoaded()) {
                player.togglePaused();
            }
        } else {

            PlayerFX.togglePaused();

            if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PLAYING)) {
                easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.start);
            } else if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PAUSED)) {
                easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.pause);
            }

        }
    }//GEN-LAST:event_MainTimePlayerplayButtonActionPerformed

    private void MaintoolsTunerPlusMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintoolsTunerPlusMenuitemActionPerformed
        tunerDialog.getTunerrateSlider().setValue(tunerDialog.getTunerrateSlider().getValue() + 25);
        tunerDialog.updateSpeed();
    }//GEN-LAST:event_MaintoolsTunerPlusMenuitemActionPerformed

    private void MaintoolsTunerMinusMenuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintoolsTunerMinusMenuitemActionPerformed
        tunerDialog.getTunerrateSlider().setValue(tunerDialog.getTunerrateSlider().getValue() - 25);
        tunerDialog.updateSpeed();
    }//GEN-LAST:event_MaintoolsTunerMinusMenuitemActionPerformed

    public Easytranscript() {

        Properties props = System.getProperties();
        props.setProperty("vlcj.log", "INFO");
        props.setProperty("file.encoding", "UTF-8");
        props.setProperty("jna.nosys", "true");

        useFXPlayer = getOS().equals("Mac");

        checkOp();

        checkFolders();

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MMM'-'W");
            Date date = new Date();

            logHandler = new FileHandler(opFolder + "Logs/log_" + dateFormat.format(date) + ".log", true);
            logger.addHandler(logHandler);
            logHandler.setFormatter(new SimpleFormatter());

            logger.info(">>> Willkommen bei easytranscript. <<<");
            logger.log(Level.INFO, "Version ist: {0} (" + VERSION.asInteger() + "), JVM Bit-Architektur ist: {1}, Java Version ist: {2}", new Object[]{VERSION.toString(), System.getProperty("os.arch"), System.getProperty("java.version")});
            logger.log(Level.INFO, "OS: {0}", getOS());
            logger.log(Level.INFO, "Vendor: {0}", System.getProperty("java.vendor"));
            logger.log(Level.INFO, "VM Name: {0}", System.getProperty("java.vm.name"));

            try {
                FileOutputStream fout = new FileOutputStream(opFolder + "Logs/VLCJ-Info-log_" + dateFormat.format(date) + ".log");
                FileOutputStream ferr = new FileOutputStream(opFolder + "Logs/VLCJ-Info-log_" + dateFormat.format(date) + ".log");

                MultiOutputStream multiOut = new MultiOutputStream(System.out, fout);
                MultiOutputStream multiErr = new MultiOutputStream(System.err, ferr);

                PrintStream stdout = new PrintStream(multiOut);
                PrintStream stderr = new PrintStream(multiErr);

                System.setOut(stdout);
                System.setErr(stderr);
            } catch (FileNotFoundException ex) {
                //Could not create/open the file
            }

        } catch (IOException | SecurityException ex) {
            Logger.getLogger(Easytranscript.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        installationDialog = new InstallationDialog(null, true);
        prop.load(false);
        if (!getOS().equals("Mac")) {
            useFXPlayer = prop.getBoolProperty("useFXPlayer");
        }

        loadLocale();
        if (!useFXPlayer) {
            detectVLC();
        }

        if (useFXPlayer) {
            logger.log(Level.INFO, "PlayerFX: JavaFX Ersatz für VLCJ aktiv.");
        }

        if (!useFXPlayer) {
            formate = new String[audioformate.length + videoformate.length];

            System.arraycopy(audioformate, 0, formate, 0, audioformate.length);
            System.arraycopy(videoformate, 0, formate, audioformate.length, videoformate.length);
        } else {

            formate = new String[audioformateFX.length + videoformateFX.length];
            System.arraycopy(audioformateFX, 0, formate, 0, audioformateFX.length);
            System.arraycopy(videoformateFX, 0, formate, audioformateFX.length, videoformateFX.length);
        }

        initComponents();
        customInitComponents();

        if (!useFXPlayer) {
            MainCenterSplitpane.setRightComponent(MainCenterVideoInternalframe);
        } else {
            PlayerFX.fxPanel = new javafx.embed.swing.JFXPanel();
            PlayerFX.anchorPaneFX = new javafx.scene.layout.BorderPane();

            MainCenterSplitpane.setRightComponent(PlayerFX.fxPanel);

            MainCenterSplitpane.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent changeEvent) {

                    String propertyName = changeEvent.getPropertyName();
                    if (propertyName.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {

                        easytranscript.updateVideoRatioFX();

                    }
                }
            });

            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    PlayerFX.scene = new javafx.scene.Scene(PlayerFX.anchorPaneFX);
                    PlayerFX.scene.setFill(javafx.scene.paint.Color.BLACK);
                    PlayerFX.fxPanel.setScene(PlayerFX.scene);

                }

            });

            PlayerFX.fxPanel.setVisible(false);
        }

        steno.load();

        configureTextfield();
        configureFontDropDown();

//        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
//            @Override
//
//            public void flavorsChanged(FlavorEvent e) {
//                if (isActive()) {
//
//                    attrList.clear();
//                }
//            }
//
//        });
        startFrame.setVisible(!prop.getBoolProperty("showStart"));

        recentUsed.load();

        for (int i = 0; i < 3; i++) {
            JMenuItem item = new JMenuItem(recentUsed.getProperty("recent" + i));
            item.addActionListener(new RecentUsedAction(item));
            startRecentUsed.add(item);
        }

        Mainrecent1Menuitem.addActionListener(new RecentUsedAction(Mainrecent1Menuitem));
        Mainrecent2Menuitem.addActionListener(new RecentUsedAction(Mainrecent2Menuitem));
        Mainrecent3Menuitem.addActionListener(new RecentUsedAction(Mainrecent3Menuitem));

        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setEnabled(prop.getBoolProperty("showToolTip"));

        undoManager = new UndoManager();

        ((javax.swing.plaf.basic.BasicInternalFrameUI) MainCenterVideoInternalframe.getUI()).setNorthPane(null);

        attributeBold.addAttribute(StyleConstants.FontConstants.Bold, true);

        if (prop.getBoolProperty("updateAuto")) {

            new UpdateChecker(false).execute();
        }

        setLocationRelativeTo(null);

        fontDropDown.setSelectedItem("Arial");

        setIconImage(icon.getImage());

        backupTimer = new Timer(prop.getIntProperty("backupTimer"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (TranscriptHandler.isBackupNeeded()) {
                    TranscriptHandler.saveBackup();
                }
            }
        });
        backupTimer.start();

        if (!useFXPlayer) {
            executorService.scheduleAtFixedRate(new UpdateTimer(), 0L, 10L, TimeUnit.MILLISECONDS);
        }
        setTitle("easytranscript");

        news.setVisible();

    }

    /**
     * Lädt die Sprache, welche in der Konfiguration angegeben ist.
     */
    private void loadLocale() {
        Properties propT = new Properties();
        try {
            propT.load(new FileInputStream(opFolder + "conf/config.properties"));
        } catch (IOException ex) {
            Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (propT.getProperty("lang") != null) {
            switch (propT.getProperty("lang")) {
                case "en":
                    currentLocale = new Locale("en", "US");
                    Locale.setDefault(currentLocale);
                    break;
                case "ja":
                    currentLocale = new Locale("ja", "JP");
                    Locale.setDefault(currentLocale);
                    break;
                case "de":
                    currentLocale = new Locale("de", "DE");
                    Locale.setDefault(currentLocale);
                    break;
                case "fr":
                    currentLocale = new Locale("fr", "FR");
                    Locale.setDefault(currentLocale);
                    break;
                default:
                    currentLocale = new Locale("en", "EN");
                    Locale.setDefault(currentLocale);
                    break;
            }

        } else {
            currentLocale = new Locale("en", "US");
            Locale.setDefault(currentLocale);
        }
        messages = ResourceBundle.getBundle("Bundle", currentLocale);
        errors = ResourceBundle.getBundle("errors", currentLocale);

        logger.log(Level.INFO, "Locale = {0}", currentLocale.getDisplayLanguage());
    }

    /**
     * Stellt den Splitter je nach geladener Mediendatei passend ein.
     */
    public void adjustSplitter() {

        if (!useFXPlayer) {
            if (player == null) {
                MainCenterSplitpane.setDividerLocation(0);

                MainCenterSplitpane.setEnabled(false);
                return;
            }

            if (!player.isMediaLoaded()) {
                MainCenterSplitpane.setDividerLocation(0);

                MainCenterSplitpane.setEnabled(false);

            } else if (player.isMediaLoaded()) {

                boolean audio = false;
                for (String audioformate1 : audioformate) {
                    if (mediaPath.toLowerCase().endsWith(audioformate1)) {
                        audio = true;
                        break;
                    }
                }

                if (audio) {
                    MainCenterSplitpane.setDividerLocation(1.0d);

                    MainCenterSplitpane.setEnabled(false);

                } else {
                    MainCenterSplitpane.setDividerLocation(0.5d);

                    MainCenterSplitpane.setEnabled(true);
                }

            }
        } else {
            if (PlayerFX.getMediaPlayer() == null) {
                MainCenterSplitpane.setDividerLocation(0);

                MainCenterSplitpane.setEnabled(false);
                return;
            }

            if (PlayerFX.getMedia() == null) {
                MainCenterSplitpane.setDividerLocation(0);

                MainCenterSplitpane.setEnabled(false);
            } else {

                boolean audio = false;
                for (String audioformate1 : audioformate) {
                    if (mediaPath.toLowerCase().endsWith(audioformate1)) {
                        audio = true;
                        break;
                    }
                }

                if (audio) {
                    MainCenterSplitpane.setDividerLocation(1.0d);

                    MainCenterSplitpane.setEnabled(false);

                } else {
                    MainCenterSplitpane.setDividerLocation(0.5d);

                    MainCenterSplitpane.setEnabled(true);
                }

            }
        }
    }

    /**
     * Speichert die Formatierungsinformationen jedes Zeichens in der aktuellen
     * Selektion in die attrList
     */
    private void saveStyledTextInformation() {
        attrList.clear();

        for (int i = MainCenterEditorEditorPane.getSelectionStart(); i <= MainCenterEditorEditorPane.getSelectionEnd(); i++) {
            attrList.add(((StyledDocument) MainCenterEditorEditorPane.getDocument()).getCharacterElement(i).getAttributes());
        }
    }

    //EDITOR
    //======
    /**
     * Weist dem Textfield die Shortcut-Actions zu etc. Nur einmal benötigt.
     */
    private void configureTextfield() {

        setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);

                    List<File> droppedFiles = (List<File>) evt
                            .getTransferable().getTransferData(
                                    DataFlavor.javaFileListFlavor);
                    if (droppedFiles.get(0).getAbsolutePath().endsWith(".etp")) {
                        if (TranscriptHandler.isUnsaved()) {

                            Object[] options = {messages.getString("Ja"),
                                messages.getString("Nein"),
                                messages.getString("Abbrechen")};
                            int n = JOptionPane.showOptionDialog(null,
                                    messages.getString("WarningCloseProject"),
                                    messages.getString("Frage"),
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[2]);

                            if (n == 0) {
                                easytranscript.getStartFrame().setVisible(false);
                                TranscriptHandler.read(droppedFiles.get(0).getAbsolutePath());

                            }

                            if (n == 2) {
                                return;
                            }

                        } else {
                            easytranscript.getStartFrame().setVisible(false);
                            TranscriptHandler.read(droppedFiles.get(0).getAbsolutePath());
                        }

                    }
                    if (droppedFiles.get(0).getAbsolutePath().endsWith(".etm")) {
                        easytranscript.getStartFrame().setVisible(false);
                        projFolder.close();
                        projFolder.load(new File(droppedFiles.get(0).getAbsolutePath()).getAbsolutePath());

                    }

                } catch (UnsupportedFlavorException | IOException ex) {

                    logger.log(Level.WARNING, ex.getLocalizedMessage());
                }
            }
        });

        MainCenterEditorEditorPane.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt
                            .getTransferable().getTransferData(
                                    DataFlavor.javaFileListFlavor);
                    if (droppedFiles.get(0).getAbsolutePath().endsWith(".etp")) {
                        if (TranscriptHandler.isUnsaved()) {

                            Object[] options = {messages.getString("Ja"),
                                messages.getString("Nein"),
                                messages.getString("Abbrechen")};
                            int n = JOptionPane.showOptionDialog(null,
                                    messages.getString("WarningCloseProject"),
                                    messages.getString("Frage"),
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[2]);

                            if (n == 0) {
                                easytranscript.getStartFrame().setVisible(false);
                                TranscriptHandler.read(droppedFiles.get(0).getAbsolutePath());
                            }

                            if (n == 2) {
                                return;
                            }

                        } else {
                            easytranscript.getStartFrame().setVisible(false);
                            TranscriptHandler.read(droppedFiles.get(0).getAbsolutePath());
                        }
                    }
                    if (droppedFiles.get(0).getAbsolutePath().endsWith(".etm")) {
                        easytranscript.getStartFrame().setVisible(false);
                        projFolder.close();
                        projFolder.load(new File(droppedFiles.get(0).getAbsolutePath()).getAbsolutePath());

                    }

                } catch (UnsupportedFlavorException | IOException ex) {
                    logger.log(Level.WARNING, ex.getLocalizedMessage());
                }
            }
        });

        if (!prop.getBoolProperty("usePerformanceMode")) {
            MainCenterEditorEditorPane.setEditorKit(new WrapEditorKit());
            Variables.performanceModeFontSizeIncrease = 0;
        } else {
            MainCenterEditorEditorPane.setEditorKit(new RTFEditorKit());
        }
//

        MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "none");
        MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "none");
        MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "none");

        MainCenterEditorEditorPane.addMouseWheelListener(new MouseWheelZoom());
        //   if (!getOS().equals("Lin")) {
        MainCenterEditorEditorPane.setCaret(new SelectionPreservingCaret());
        //  }

        StyledDocument doc = ((StyledDocument) MainCenterEditorEditorPane.getDocument());

        Action PPAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!useFXPlayer) {
                    if (player.isMediaLoaded()) {
                        player.togglePaused();
                    }
                } else {
                    PlayerFX.togglePaused();

                    if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PLAYING)) {
                        easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.start);
                    } else if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PAUSED)) {
                        easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.pause);

                    }
                }

            }
        };
        Action VSAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!useFXPlayer) {
                    if (player.isMediaLoaded()) {
                        player.jump(player.getInterval());
                    }
                } else {
                    //   if (PlayerFX.isMediaLoaded()) {
                    PlayerFX.jump(PlayerFX.getInterval());
                    //   }
                }
            }
        };
        Action ZSAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!useFXPlayer) {
                    if (player.isMediaLoaded()) {
                        player.jump(-player.getInterval());
                    }
                } else {

                    PlayerFX.jump(-PlayerFX.getInterval());

                }
            }
        };
        Action ZeitstempelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (insertionMode == Mode.COMPLETION) {
                    int pos = getMainCenterEditorEditorPane().getSelectionEnd();

                    try {
                        getMainCenterEditorEditorPane().getDocument().insertString(pos, " ", ((StyledDocument) getMainCenterEditorEditorPane().getDocument()).getCharacterElement(MainCenterEditorEditorPane.getCaretPosition() - 1).getAttributes());
                    } catch (BadLocationException ex) {
                        Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    getMainCenterEditorEditorPane().setCaretPosition(pos + 1);
                    insertionMode = Mode.INSERT;
                } else {
                    insertTimeStamp();
                }

            }
        };
        Action newlineAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), "\n", null);
                } catch (BadLocationException ex) {
                    logger.log(Level.WARNING, ex.toString());
                }
            }
        };
        Action TabAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), "\t", SimpleAttributeSet.EMPTY);
                } catch (BadLocationException ex) {
                    Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };

        MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspaced");
        // MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tab");
        MainCenterEditorEditorPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");

        MainCenterEditorEditorPane.getActionMap().put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainCenterEditorEditorPane.setCaretPosition(MainCenterEditorEditorPane.getSelectionEnd() + 1);

            }
        });

        MainCenterEditorEditorPane.getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (MainCenterEditorEditorPane.getSelectionStart() != MainCenterEditorEditorPane.getSelectionEnd()) {
                        int start = MainCenterEditorEditorPane.getSelectionStart();
                        int end = MainCenterEditorEditorPane.getSelectionEnd();
                        int caret;
                        if (start < end) {
                            caret = start;
                        } else {
                            caret = end;
                        }
                        MainCenterEditorEditorPane.getDocument().remove(MainCenterEditorEditorPane.getSelectionStart(), MainCenterEditorEditorPane.getSelectedText().length());
                        MainCenterEditorEditorPane.setCaretPosition(caret);

                    } else if (orientationRT) {
                        MainCenterEditorEditorPane.getDocument().remove(MainCenterEditorEditorPane.getCaretPosition() - 1, 1);
                    } else {
                        int caret = MainCenterEditorEditorPane.getCaretPosition();
                        MainCenterEditorEditorPane.getDocument().remove(caret, 1);
                        MainCenterEditorEditorPane.setCaretPosition(caret);
                    }
                } catch (BadLocationException ex) {
                    // Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        MainCenterEditorEditorPane.getActionMap().put("backspaced", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (MainCenterEditorEditorPane.getSelectionStart() != MainCenterEditorEditorPane.getSelectionEnd()) {
                        int caret = MainCenterEditorEditorPane.getSelectionStart();
                        MainCenterEditorEditorPane.getDocument().remove(MainCenterEditorEditorPane.getSelectionStart(), MainCenterEditorEditorPane.getSelectedText().length());
                        if (caret < 0) {
                            caret = 0;
                        }
                        MainCenterEditorEditorPane.setCaretPosition(caret);
                    } else {
                        String hash;
                        if (!orientationRT) {
                            hash = MainCenterEditorEditorPane.getDocument().getText(MainCenterEditorEditorPane.getCaretPosition() - 1, 1);
                        } else {
                            hash = MainCenterEditorEditorPane.getDocument().getText(MainCenterEditorEditorPane.getCaretPosition(), 1);
                        }
                        if (hash.equals("#")) {
                            int caret = MainCenterEditorEditorPane.getCaretPosition() - 12;
                            MainCenterEditorEditorPane.getDocument().remove(MainCenterEditorEditorPane.getCaretPosition() - 12, 12);
                            MainCenterEditorEditorPane.setCaretPosition(caret);
                        } else if (orientationRT) {
                            MainCenterEditorEditorPane.getDocument().remove(MainCenterEditorEditorPane.getCaretPosition(), 1);
                        } else {
                            int caret = MainCenterEditorEditorPane.getCaretPosition() - 1;
                            MainCenterEditorEditorPane.getDocument().remove(caret, 1);
                            MainCenterEditorEditorPane.setCaretPosition(caret);
                        }
                    }
                } catch (BadLocationException ex) {
                    // Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });
        MainCenterEditorEditorPane.getActionMap().put("steno", new StenoAction());

        MainCenterEditorEditorPane.getActionMap().put("enter", ZeitstempelAction);
        MainCenterEditorEditorPane.getActionMap().put("newline", newlineAction);
        MainCenterEditorEditorPane.getActionMap().put("zuruckspulen", ZSAction);
        MainCenterEditorEditorPane.getActionMap().put("PausePlaying", PPAction);
        MainCenterEditorEditorPane.getActionMap().put("vorspulen", VSAction);
        MainCenterEditorEditorPane.getActionMap().put("tab", TabAction);

        for (int i = 1; i < 11; i++) {
            final int j = i;
            MainCenterEditorEditorPane.getActionMap().put("altaction" + j, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        SimpleAttributeSet attributes = new SimpleAttributeSet();
                        if (prop.getBoolProperty("TextbausteineFett")) {
                            StyleConstants.setBold(attributes, true);
                        }
                        StyleConstants.setFontSize(attributes, Integer.parseInt(MainToolbarFontsizeCombobox.getSelectedItem().toString().replaceAll(" ", "")));

                        String txtbs1 = prop.getStringProperty("Tbs" + j).replace("\\n", "\n");

                        MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), txtbs1, attributes);

                        if (prop.getBoolProperty("TextbausteineFett")) {
                            new StyledEditorKit.BoldAction().actionPerformed(null);
                        }

                    } catch (BadLocationException ignored) {
                    }

                }
            });
        }

        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, true), "PausePlaying");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "vorspulen");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "zuruckspulen");

            MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, true), "PausePlaying");
            MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "vorspulen");
            MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "zuruckspulen");

            /*

            if (getOS().equals("Win")) {

//                JIntellitype.getInstance().registerHotKey(0, 0, KeyEvent.VK_F3);
  //              JIntellitype.getInstance().registerHotKey(1, 0, KeyEvent.VK_F4);
    //            JIntellitype.getInstance().registerHotKey(2, 0, KeyEvent.VK_F5);
            }
            if (getOS().equals("Lin")) {

                try {
                    JXGrabKey.getInstance().registerAwtHotkey(0, 0, KeyEvent.VK_F3);
                    JXGrabKey.getInstance().registerAwtHotkey(1, 0, KeyEvent.VK_F4);
                    JXGrabKey.getInstance().registerAwtHotkey(2, 0, KeyEvent.VK_F5);
                } catch (HotkeyConflictException ex) {
                    Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (getOS().equals("Mac")) {

            }
        }*/
        // Wenn hier die Belegung geändert wird, muss in StenoAction die suggestions.add Zeile gelöscht werden und der Leertaste wieder zugeiwesen werden!!

        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(' '), "steno");

        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK), "newline");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_MASK), "altaction1");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_MASK), "altaction2");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK), "altaction3");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_MASK), "altaction4");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_MASK), "altaction5");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_MASK), "altaction6");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.ALT_MASK), "altaction7");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.ALT_MASK), "altaction8");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.ALT_MASK), "altaction9");
        MainCenterEditorEditorPane.getInputMap(JEditorPane.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_MASK), "altaction10");

        if (!prop.getBoolProperty("usePerformanceMode")) {
            MainCenterEditorEditorPane.getDocument().putProperty("ZOOM_FACTOR", zoomFactor);
        }

    }

    /**
     * Setzt einen Zeitstempel
     */
    private void insertTimeStamp() {
        try {

            if (prop.getBoolProperty("ZeitmarkenActive")) {

                if (!prop.getBoolProperty("zeitmarkeBeginning") || prop.getBoolProperty("zeitmarkeEnding")) {

                    MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(),
                            " #" + StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string + "-" + Millisekunden_current + "# ", null);

                }

            }

            MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), "\n", null);
            if (prop.getBoolProperty("ZMleerzeile")) {
                MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), "\n", null);
            }

            if (prop.getBoolProperty("ZeitmarkenActive") && prop.getBoolProperty("zeitmarkeBeginning")) {

                if (!MainCenterEditorEditorPane.getDocument().getText(0, MainCenterEditorEditorPane.getDocument().getLength()).trim().isEmpty()) {

                    MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(),
                            " #" + StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string + "-" + Millisekunden_current + "# ", null);
                }
            }

            if (prop.getBoolProperty("Sprecherwechsel")) {

                String text = MainCenterEditorEditorPane.getDocument().getText(0, MainCenterEditorEditorPane.getCaretPosition());
                int ind1 = text.lastIndexOf(prop.getStringProperty("SprecherwechselP1"));
                int ind2 = text.lastIndexOf(prop.getStringProperty("SprecherwechselP2"));
                sprecherwechselWechsel = ind2 > ind1 || ind2 == ind1;

                if (sprecherwechselWechsel) {
                    if (prop.getBoolProperty("COSFett")) {
                        MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), prop.getStringProperty("SprecherwechselP1"), attributeBold);
                        MainToolbarBoldButton.getActionListeners()[0].actionPerformed(null);
                        MainCenterEditorEditorPaneCaretUpdate(null);
                    }
                    if (!prop.getBoolProperty("COSFett")) {
                        MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), prop.getStringProperty("SprecherwechselP1"), null);
                    }
                } else {
                    if (prop.getBoolProperty("COSFett")) {
                        MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), prop.getStringProperty("SprecherwechselP2"), attributeBold);
                        MainToolbarBoldButton.getActionListeners()[0].actionPerformed(null);
                        MainCenterEditorEditorPaneCaretUpdate(null);
                    }
                    if (!prop.getBoolProperty("COSFett")) {
                        MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), prop.getStringProperty("SprecherwechselP2"), null);
                    }
                }
            }
        } catch (BadLocationException ex) {
            logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FehlerTextbaustein")), ex);

        }
    }

    //WEITERES
    //========
    /**
     * Erstellt benötigte Programmordner, falls diese noch nicht existieren.
     *
     */
    private void checkFolders() {

        if (!new File(opFolder).exists()) {
            new File(opFolder).mkdir();
        }

        if (!new File(opFolder + "conf").exists()) {
            new File(opFolder + "conf").mkdir();
        }

        if (!new File(opFolder + "Projects").exists()) {
            new File(opFolder + "Projects").mkdir();
            try {
                if (getOS().equals("Win")) {
                    Tools.createWindowsShortcut(messages.getString("shortcutProjectFolder"), FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath(), opFolder + "Projects", Easytranscript.class.getResource("images/folder.ico").getFile());
                }
            } catch (Exception ex) {
                Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!new File(opFolder + "Logs").exists()) {
            new File(opFolder + "Logs").mkdir();
        }

        //  deleteFilesWithExtension(new File(opFolder + "Projects"), Pattern.compile(".*\\.tmp"));
        try{
        deleteFilesWithExtension(new File(opFolder + "Logs"), Pattern.compile(".*\\.lck"));
        deleteFilesWithExtension(new File(opFolder + "Logs"), Pattern.compile(".*\\.1"));
        deleteFilesWithExtension(new File(opFolder + "Logs"), Pattern.compile(".*\\.2"));
        }catch(Exception e){
            Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, e);
        }

        try{
        File log = new File(opFolder + "/Logs/");

        ArrayList<File> toBeD = new ArrayList<>();

        for (File f : log.listFiles()) {
            String name = f.getName();
            if (f.getName().endsWith(".log")) {
                name = name.substring(0, name.lastIndexOf(".log"));
                if (name.contains("VLCJ-Info-log_")) {
                    name = name.substring("VLCJ-Info-log_".length());
                } else {
                    name = name.substring("log_".length());
                }

                DateFormat dateFormat = new SimpleDateFormat("yyy'-'MMM'-'W");
                try {
                    Date date = dateFormat.parse(name);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int monthO = c.get(Calendar.MONTH);
                    Date datec = new Date();
                    c.setTime(datec);
                    int month = c.get(Calendar.MONTH);

                    if (TimeUnit.MILLISECONDS.toDays(datec.getTime() - date.getTime()) > 90) {
                        toBeD.add(f);
                    }

                } catch (ParseException ex) {
                    //      Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        for (File f : toBeD) {
            f.delete();
        }
        }catch(Exception e){
            Logger.getLogger(Easytranscript.class.getName()).log(Level.WARNING,"Fehler beim Lehren des Log-Ordners.",e);
        }

        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new File(new File("").getAbsolutePath() + System.getProperty("file.separator") + "etutmp"));
        } catch (IOException ex) {
            Logger.getLogger(Easytranscript.class.getName()).log(Level.SEVERE, "tmp Update Ordner konnte nicht gelöscht werden.", ex);
        }
    }

    /**
     * Beendet das Programm sauber.
     */
    public static void terminate() {

        int n;
        if (TranscriptHandler.isUnsaved()) {

            Object[] options = {messages.getString("Ja"), messages.getString("Nein"),
                messages.getString("backToEasy")};
            n = JOptionPane.showOptionDialog(null,
                    messages.getString("DocumentChanged"),
                    messages.getString("Frage"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (n == 0) {
                TranscriptHandler.save(transcriptPath, false, false, Easytranscript::_terminateAfter);
            } else if (n == 1){
                _terminateAfter();
            }
        } else {
            _terminateAfter();
        }



    }

    private static Void _terminateAfter(){
        if (workTime.getRecordingTime()) {

            try {
                workTime.endCurrentWorkTimeEntry();
            } catch (Exception e) {
                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("FehlerBeendenArbeitszeiteintrag")), e);

            }
            workTime.setRecordingTime(false);
        }

        if (logHandler != null) {
            logHandler.close();
        }

        System.exit(0);
        return null;
    }

    /**
     * Sucht nach VLC und fordert, falls nicht gefunden den Benutzer auf, den
     * Pfad zur VLC Installation anzugeben.
     */
    private void detectVLC() {

        logger.log(Level.INFO, "detect VLC");

        int ruckgabe = 0;

        new NativeDiscovery().discover();
        String path = prop.getStringProperty("VLCPath");

        if (path != null) {

            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), path);
        }

        try {

            Version version = LibVlcVersion.getVersion();

            boolean supported = LibVlcVersion.getVersion().atLeast(new Version("2.1.0"));

            if (!supported) {
                JOptionPane.showMessageDialog(null, messages.getString("VLCVersionLow"));
                System.exit(0);
            }

            logger.log(Level.INFO, "VLC Version ist: {0}", version);
        } catch (Error ex) {

            String bit = "64";
            if (System.getProperty("os.arch").equals("x86")) {
                bit = "32";
            }
            logger.log(Level.WARNING, "VLC-Funktionsbibliotheken wurden nicht gefunden.", ex);

            Object[] options = {messages.getString("manuellesSuchen"), bit + messages.getString("VLCbutton"), messages.getString("VLCFortfahren"), messages.getString("Easytranscript.MaincloseMenuitem.text")};

            int n = JOptionPane.showOptionDialog(null,
                    messages.getString("VLCmessage3") + bit + " " + messages.getString("VLCmessage4") + " " + bit + " " + messages.getString("VLCmessage5"), messages.getString("VLCmessage2"),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(Easytranscript.class
                            .getResource("images/VLC_Icon.png")),
                    options,
                    options[0]);

            if (n == 0) {
                fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                ruckgabe = fileChooser.showDialog(null, messages.getString("VLCmessage6"));

                if (ruckgabe == JFileChooser.APPROVE_OPTION) {
                    String patht = fileChooser.getSelectedFile().getAbsolutePath();

                    if (patht.toLowerCase().contains("vlc") || patht.toLowerCase().contains("videolan")) {
                        FileSearch fs = new FileSearch();
                        fs.searchDirectory(new File(patht), RuntimeUtil.getLibVlcName());

                        int size = fs.getResult().size();

                        if (size > 0) {
                            patht = fs.getResult().get(0);
                            patht = patht.substring(0, patht.lastIndexOf(System.getProperty("file.separator")));

                            prop.setStringProperty("VLCPath", patht);

                            try {
                                prop.save();
                                JOptionPane.showMessageDialog(null, messages.getString("VLCpossiblyFound1") + patht + "\n" + messages.getString("VLCpossiblyFound2"));
                                System.exit(0);
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("SavingConfig")), e);

                            }
                        } else {
                            detectVLC();
                        }
                    } else {
                        detectVLC();
                    }

                }

            } else if (n
                    == 1) {

                URL url = null;
                try {
                    if (!bit.equals("64")) {
                        switch (getOS()) {
                            case "Win":
                                url = new URL("http://www.videolan.org/vlc/");
                                break;
                            case "Lin":
                                url = new URL("http://www.videolan.org/vlc/download-ubuntu.html");
                                break;
                            case "Mac":
                                url = new URL("http://www.videolan.org/vlc/download-macosx.html");
                                break;
                        }
                    } else {
                        switch (getOS()) {
                            case "Win":
                                url = new URL("http://download.videolan.org/pub/videolan/vlc/last/win64/vlc-2.1.5-win64.exe");
                                break;
                            case "Lin":
                                url = new URL("http://www.videolan.org/vlc/download-ubuntu.html");
                                break;
                            case "Mac":
                                url = new URL("http://www.videolan.org/vlc/download-macosx.html");
                                break;
                        }
                    }
                    new Tools().browse(url.toString());
                    System.exit(0);
                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, new ErrorReport().show(errors.getString("errorOpeningLink")), e);
                    System.exit(1);
                    return;
                }
                return;
            } else if (n == 2) {
                if (!fxSupported) {
                    JOptionPane.showMessageDialog(null, messages.getString("fxNotSupported"), messages.getString("Error"), JOptionPane.ERROR_MESSAGE);
                    detectVLC();
                } else {
                    useFXPlayer = true;
                }
            } else {
                System.exit(0);
            }

            if (ruckgabe != JFileChooser.APPROVE_OPTION) {
                detectVLC();

            }
        }

//  }
    }

    public static void mergeDocument(DefaultStyledDocument source, DefaultStyledDocument dest, int offset) throws BadLocationException {
        ArrayList<DefaultStyledDocument.ElementSpec> specs = new ArrayList<>();
        DefaultStyledDocument.ElementSpec spec = new DefaultStyledDocument.ElementSpec(new SimpleAttributeSet(),
                DefaultStyledDocument.ElementSpec.EndTagType);
        specs.add(spec);
        fillSpecs(source.getDefaultRootElement(), specs, false);
        spec = new DefaultStyledDocument.ElementSpec(new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.StartTagType);
        specs.add(spec);

        DefaultStyledDocument.ElementSpec[] arr = new DefaultStyledDocument.ElementSpec[specs.size()];
        specs.toArray(arr);
        insertSpecs(dest, offset, arr);

    }

    private static void insertSpecs(DefaultStyledDocument doc, int offset, DefaultStyledDocument.ElementSpec[] specs) {
        try {
//            doc.insert(0, specs);  method is protected so we have to
            //extend document or use such a hack
            Method m = DefaultStyledDocument.class
                    .getDeclaredMethod("insert", int.class, DefaultStyledDocument.ElementSpec[].class);
            m.setAccessible(true);

            m.invoke(doc, offset, specs);

        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            logger.log(Level.WARNING, "insertSpecs Error", e);
        }
    }

    private static void fillSpecs(Element elem, ArrayList<DefaultStyledDocument.ElementSpec> specs, boolean includeRoot) throws BadLocationException {
        DefaultStyledDocument.ElementSpec spec;
        if (elem.isLeaf()) {
            String str = elem.getDocument().getText(elem.getStartOffset(), elem.getEndOffset() - elem.getStartOffset());
            spec = new DefaultStyledDocument.ElementSpec(elem.getAttributes(),
                    DefaultStyledDocument.ElementSpec.ContentType, str.toCharArray(), 0, str.length());
            specs.add(spec);
        } else {
            if (includeRoot) {
                spec = new DefaultStyledDocument.ElementSpec(elem.getAttributes(), DefaultStyledDocument.ElementSpec.StartTagType);
                specs.add(spec);
            }
            for (int i = 0; i < elem.getElementCount(); i++) {
                fillSpecs(elem.getElement(i), specs, true);
            }

            if (includeRoot) {
                spec = new DefaultStyledDocument.ElementSpec(elem.getAttributes(), DefaultStyledDocument.ElementSpec.EndTagType);
                specs.add(spec);
            }
        }
    }

    private void customInitComponents() {
        infoFrame = new InfoFrame();
        configFrame = new ConfigFrame();
        searchFrame = new SearchFrame();
        zeitFrame = new ZeitFrame();
        workerFrame = new WorkerFrame();
        projectFolderFrame = new ProjectFrame();
        startFrame = new StartFrame();
        tunerDialog = new TunerDialog();
        tastenCheckFrame = new TastenCheckFrame();
        supportFrame = new SupportFrame();
        news = new Easynews();

        updateFrame = new UpdateFrame();
        importFrame = new ImportFrame();
        newProjectFrame = new NewTranscriptFrame();
        changeProjectFrame = new ChangeTranscriptFrame();
        exportFrame = new ExportFrame();
        if (VersionState.getValue(VERSION.getVersionState()) < 5) {
            System.out.println(VERSION.asInteger());
            JOptionPane.showMessageDialog(null, "This version of easytranscript (" + VERSION.toString() + ") is still in development process. \nPossibly there are still unresolved errors and unimplemented features.", "Early Stage", JOptionPane.WARNING_MESSAGE);
        }

        if (!useFXPlayer) {
            player = new Player();
        }

        if (prop.getIntProperty("suggestionNeededLength") >= 5) {
            suggestionNeededLength = prop.getIntProperty("suggestionNeededLength");
        }
    }

    public JPanel getMainButtonPanel() {
        return MainButtonPanel;
    }

    public JEditorPane getMainCenterEditorEditorPane() {
        return MainCenterEditorEditorPane;
    }

    public JSplitPane getMainCenterSplitpane() {
        return MainCenterSplitpane;
    }

    public JInternalFrame getMainCenterVideoInternalframe() {
        return MainCenterVideoInternalframe;
    }

    public JMenuBar getMainMenubar() {
        return MainMenubar;
    }

    public JPanel getMainSliderPanel() {
        return MainSliderPanel;
    }

    public JPanel getMainTimeButtonPanel() {
        return MainTimeButtonPanel;
    }

    public JButton getMainToolbarSaveAsButton() {
        return MainToolbarSaveAsButton;
    }

    public JButton getMainToolbarSaveButton() {
        return MainToolbarSaveButton;
    }

    public JMenu getMaineditMenu() {
        return MaineditMenu;
    }

    public JMenuItem getMainchangeProjectMenuItem() {
        return MainchangeProjectMenuItem;
    }

    public JMenuItem getMaincloseProjectMenuitem() {
        return MaincloseProjectMenuitem;
    }

    public JMenuItem getMainexportTransMenuitem() {
        return MainexportTransMenuitem;
    }

    public JMenuItem getMainSaveAsMenuItem() {
        return MainSaveAsMenuItem;
    }

    public JMenuItem getMainSaveMenuItem() {
        return MainsaveMenuItem;
    }

    public JToolBar getMainToolbar() {
        return MainToolbar;
    }

    public JMenu getMaintoolsMenu() {
        return MaintoolsMenu;
    }

    public JMenu getMainexportMenu() {
        return MainexportMenu;
    }

    public JMenuItem getMainprintMenuitem() {
        return MainprintMenuitem;
    }

    public JLabel getMainTimeLabel() {
        return MainTimeLabel;
    }

    public JLabel getMainTimeMilliLabel() {
        return MainTimeMilliLabel;
    }

    public JLabel getMainTotalLabel() {
        return MainTotalLabel;
    }

    public JButton getMainTimePlayerbackwButton() {
        return MainTimePlayerbackwButton;
    }

    public JButton getMainTimePlayerforwButton() {
        return MainTimePlayerforwButton;
    }

    public JButton getMainTimePlayerplayButton() {
        return MainTimePlayerplayButton;
    }

    public JSlider getMainSlider() {
        return MainSlider;
    }

    public JSlider getMainTimeIntervalSlider() {
        return MainTimeIntervalSlider;
    }

    public JLabel getBufferingLabel() {
        return BufferingLabel;
    }

    public JCheckBoxMenuItem getMainchangeSpokemanCheckboxmenuitem() {
        return MainchangeSpokemanCheckboxmenuitem;
    }

    public JMenuItem getMainProjectInfosMenuitem() {
        return MainProjectInfosMenuitem;

    }

    public JFrame getStartFrame() {
        return startFrame;
    }

    private void configureFontDropDown() {

        fontDropDown.getEditor().getEditorComponent().addFocusListener(new FontFocusListener(false));

        fontDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (fontfamilyHasFocus) {
                    String fontName = fontDropDown.getSelectedFontName();
                    String name = (fontName != null) ? fontName : "";

                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    if (!name.equals("")) {
                        if (!name.equals(" ")) {

                            StyleConstants.setFontFamily(attrs, name);
                            if (MainCenterEditorEditorPane.getSelectionStart() != MainCenterEditorEditorPane.getSelectionEnd()) {
                                ((StyledDocument) MainCenterEditorEditorPane.getDocument()).setCharacterAttributes(MainCenterEditorEditorPane.getSelectionStart(), MainCenterEditorEditorPane.getSelectionEnd() - MainCenterEditorEditorPane.getSelectionStart(), attrs, false);

                            } else {

                                currentAttributeSet = attrs;
                                casChanged = true;
                                // MainCenterEditorEditorPane.getDocument().insertString(MainCenterEditorEditorPane.getCaretPosition(), " ", attrs);

                            }
                            MainCenterEditorEditorPane.requestFocus();
                            fontfamilyHasFocus = false;
                        }
                    }

                }
            }
        });
    }

    private void updateVideoRatioFX() {
        if (PlayerFX.getMediaViewFX() != null && PlayerFX.getMedia() != null && PlayerFX.getMediaPlayer() != null) {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    PlayerFX.getMediaViewFX().setFitWidth(MainCenterSplitpane.getRightComponent().getWidth());
                    PlayerFX.getMediaViewFX().setFitHeight(MainCenterSplitpane.getRightComponent().getHeight());

                }
            });
        }
    }

    public void loadSystemWideHotkeysLibraries() {
        String os = getOS();

        if (os.equals("Win")) {
            SystemWideKeyWasActive = 1;

            try {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    JIntellitype.setLibraryLocation(new File("lib/JIntellitype64.dll"));
                } else {
                    JIntellitype.setLibraryLocation(new File("lib/JIntellitype.dll"));
                }
                JIntellitype.getInstance();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to load Library for system wide keys!");
                //e.printStackTrace();
                //System.exit(0);

            }

            JIntellitype.getInstance().addHotKeyListener(new com.melloware.jintellitype.HotkeyListener() {

                @Override
                public void onHotKey(int i) {
                    if (i == 1) {
                        if (!useFXPlayer) {
                            if (player.isMediaLoaded()) {
                                player.togglePaused();
                            }
                        } else {

                            PlayerFX.togglePaused();
                            if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PLAYING)) {
                                easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.start);
                            } else if (PlayerFX.getMediaPlayer().getStatus().equals(javafx.scene.media.MediaPlayer.Status.PAUSED)) {
                                easytranscript.getMainTimePlayerplayButton().setIcon(PlayerFX.pause);
                            }

                        }
                    }

                    if (i == 0) {
                        if (!useFXPlayer) {
                            if (player.isMediaLoaded()) {
                                player.jump(-player.getInterval());
                            }
                        } else {

                            PlayerFX.jump(-PlayerFX.getInterval());

                        }
                    }

                    if (i == 2) {
                        if (!useFXPlayer) {
                            if (player.isMediaLoaded()) {
                                player.jump(player.getInterval());
                            }
                        } else {

                            PlayerFX.jump(PlayerFX.getInterval());

                        }
                    }
                }

            });
        }
        if (os.equals("Lin")) {


        }
    }

    public JComboBox getMainToolbarZoomCombobox() {
        return MainToolbarZoomCombobox;
    }

    /**
     * ActionListener für RecentUsed. Lädt das ausgewählte Projekt.
     */
    class RecentUsedAction implements java.awt.event.ActionListener {

        JMenuItem item = new JMenuItem();

        RecentUsedAction(JMenuItem recItem) {
            item = recItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TranscriptHandler.read(item.getText());
        }
    }

    /**
     * Eingeführt als Bugfix für den Zwischenhtml-export bei dem Space-Ketten
     * ersetzt wurden durch ein einzelnes Space.
     *
     * @param p JeditorPane
     * @return
     */
    private static int searchForSpaces_HtmlFix(JEditorPane p, String search) {

        String searchPhrase = search;

        int index = -1;

        try {

            String text = p.getDocument().getText(p.getCaretPosition(), p.getDocument().getLength() - p.getCaretPosition());

            text = text.toLowerCase();
            searchPhrase = searchPhrase.toLowerCase();

            index = text.indexOf(searchPhrase);

            if (index > -1) {
                index = index + p.getCaretPosition();
                //p.requestFocus();
                p.setCaretPosition(index);
                p.moveCaretPosition(index + searchPhrase.length());
            }
        } catch (BadLocationException e) {
            logger.log(Level.WARNING, "Fehler", e);
        }

        return index;
    }

    public void activateElementsAfterTranscriptRead() {
        easytranscript.getMainTimePlayerplayButton().setEnabled(true);
        easytranscript.getMainTimePlayerbackwButton().setEnabled(true);
        easytranscript.getMainTimePlayerforwButton().setEnabled(true);
        tunerDialog.getTunerVolumeSlider().setEnabled(true);
        easytranscript.getMainTimeIntervalSlider().setEnabled(true);
        easytranscript.getMainSlider().setEnabled(true);
    }

    /**
     * Eingeführt als Bugfix für den Zwischenhtml-export bei dem Space-Ketten
     * ersetzt wurden durch ein einzelnes Space.
     *
     * @param p JeditorPane
     * @return
     */
    private static int replaceSpaces_HtmlFix(JEditorPane p, String search, String replace) {
        if (p.getSelectedText() != null) {
            p.setCaretPosition(p.getCaretPosition() - p.getSelectedText().length());
        }

        int index = searchForSpaces_HtmlFix(p, search);
        if (index > -1) {
            p.replaceSelection(replace);
        }
        return index;
    }

    private class UpdateTimeWhilePressed extends TimerTask {

        @Override
        public void run() {
            currentPlayerTime = MainSlider.getValue() * 1000;
            new UpdateTimer().updateTimeStrings();

            MainTimeLabel.setText(StundenC_string + ":" + MinutenC_string + ":" + SekundenC_string);
            String stern = "";
            if (TranscriptHandler.isUnsaved()) {
                stern = "*";
            }
            setTitle("easytranscript - " + transcriptName + stern + " - " + MainTimeLabel.getText() + " " + messages.getString("vonTime") + " " + MainTotalLabel.getText());
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BufferingLabel;
    private javax.swing.JPanel BufferingPanel;
    private javax.swing.JMenuItem MainActionBoldMenuItem;
    private javax.swing.JMenuItem MainActionCopyMenuItem;
    private javax.swing.JMenuItem MainActionCutMenuItem;
    private javax.swing.JMenuItem MainActionItalicMenuItem;
    private javax.swing.JMenuItem MainActionPasteMenuItem;
    private javax.swing.JPopupMenu MainActionPopupMenu;
    private javax.swing.JMenuItem MainActionSelectAllMenuItem;
    private javax.swing.JPopupMenu.Separator MainActionSep1Separator;
    private javax.swing.JPopupMenu.Separator MainActionSep2Separator;
    private javax.swing.JMenuItem MainActionTimeStampMenuItem;
    private javax.swing.JMenuItem MainActionUnderlinedMenuItem;
    private javax.swing.ButtonGroup MainAlignmentButtonGroup;
    private javax.swing.JPanel MainButtonPanel;
    private javax.swing.JEditorPane MainCenterEditorEditorPane;
    private javax.swing.JPanel MainCenterEditorPanel;
    public static javax.swing.JScrollPane MainCenterEditorScrollpane;
    private javax.swing.JPanel MainCenterPanel;
    private javax.swing.JSplitPane MainCenterSplitpane;
    private javax.swing.JInternalFrame MainCenterVideoInternalframe;
    private javax.swing.JMenuItem MainFontsizeMinusMenuitem;
    private javax.swing.JMenuBar MainMenubar;
    private javax.swing.JMenuItem MainProjectInfosMenuitem;
    private javax.swing.JMenuItem MainSaveAsMenuItem;
    private javax.swing.JLabel MainSlashLabel;
    private javax.swing.JSlider MainSlider;
    private javax.swing.JPanel MainSliderPanel;
    private javax.swing.JMenuItem MainSocialMenuitem;
    private javax.swing.Box.Filler MainTime1Filler;
    private javax.swing.Box.Filler MainTime2Filler;
    private javax.swing.JPanel MainTimeButtonPanel;
    private javax.swing.JButton MainTimeExpandCollapsButton;
    private javax.swing.JLabel MainTimeIntervalLabel;
    private javax.swing.JPanel MainTimeIntervalPanel;
    private javax.swing.JSlider MainTimeIntervalSlider;
    private javax.swing.JLabel MainTimeLabel;
    private javax.swing.JLabel MainTimeMilliLabel;
    private javax.swing.JPanel MainTimePanel;
    private javax.swing.JPanel MainTimePlayerButtonPanel;
    private javax.swing.JButton MainTimePlayerbackwButton;
    private javax.swing.JButton MainTimePlayerforwButton;
    private javax.swing.JButton MainTimePlayerplayButton;
    private javax.swing.JPanel MainTimeRightPanel;
    private javax.swing.JButton MainTimeTunerButton;
    private javax.swing.JToolBar MainToolbar;
    private javax.swing.JToolBar.Separator MainToolbar1Seperator;
    private javax.swing.Box.Filler MainToolbar2Filler;
    private javax.swing.JToolBar.Separator MainToolbar2Seperator;
    private javax.swing.JToolBar.Separator MainToolbar3Seperator;
    private javax.swing.JToolBar.Separator MainToolbar4Seperator;
    public static javax.swing.JToggleButton MainToolbarBoldButton;
    public static javax.swing.JToggleButton MainToolbarChangeSpokemanTogglebutton;
    private javax.swing.JButton MainToolbarCheckUpdatesButton;
    private javax.swing.JButton MainToolbarConfigButton;
    private javax.swing.JButton MainToolbarCutButton;
    private javax.swing.Box.Filler MainToolbarFontDropdownFakeFiller;
    public static javax.swing.JComboBox MainToolbarFontsizeCombobox;
    public static javax.swing.JToggleButton MainToolbarItalicButton;
    private javax.swing.JButton MainToolbarPasteButton;
    private javax.swing.JButton MainToolbarSaveAsButton;
    private javax.swing.JButton MainToolbarSaveButton;
    private javax.swing.JButton MainToolbarSearchButton;
    private javax.swing.JButton MainToolbarTimeButton;
    private javax.swing.JButton MainToolbarTimestampButton;
    public static javax.swing.JToggleButton MainToolbarUnderlinedButton;
    public static javax.swing.JComboBox MainToolbarZoomCombobox;
    private javax.swing.JButton MainToolbarcopyButton;
    private javax.swing.JButton MainToolbarredoButton;
    private javax.swing.JButton MainToolbarundoButton;
    private javax.swing.JLabel MainTotalLabel;
    private javax.swing.JMenuItem MainboldMenuitem;
    private javax.swing.JMenuItem MainchangeProjectMenuItem;
    private javax.swing.JCheckBoxMenuItem MainchangeSpokemanCheckboxmenuitem;
    private javax.swing.JMenuItem MaincheckUpdatesMenuitem;
    private javax.swing.JMenuItem MaincloseMenuitem;
    private javax.swing.JMenuItem MaincloseProjectMenuitem;
    private javax.swing.JMenuItem MainconfigMenuitem;
    private javax.swing.JMenuItem MaincopyMenuitem;
    private javax.swing.JMenuItem MaincreateProjectFolderMenuitem;
    public static javax.swing.JMenuItem MaincreateProjectMenuItem;
    private javax.swing.JMenuItem MaincutMenuitem;
    private javax.swing.JMenuItem MaindonateMenuitem;
    private javax.swing.JMenuItem MainduplicateTransMenuitem;
    private javax.swing.JPopupMenu.Separator Mainedit2Seperator;
    private javax.swing.JPopupMenu.Separator Mainedit3Seperator;
    private javax.swing.JMenu MaineditMenu;
    private javax.swing.JPopupMenu.Separator MaineditSeperator;
    private javax.swing.JPopupMenu.Separator Mainexport2Separator;
    private javax.swing.JMenu MainexportMenu;
    private javax.swing.JPopupMenu.Separator MainexportSeperator;
    private javax.swing.JMenuItem MainexportTransMenuitem;
    private javax.swing.JMenuItem MainexportYoutubeMenuitem;
    private javax.swing.JMenu MainfileMenu;
    private javax.swing.JPopupMenu.Separator Mainfilemenu2Seperator;
    private javax.swing.JPopupMenu.Separator MainfilemenuSeperator;
    private javax.swing.JMenuItem MainfontsizePlusMenuitem;
    private javax.swing.JMenu MainhelpMenu;
    private javax.swing.JMenuItem MainhelpMenuitem;
    public static javax.swing.JMenuItem MainimportProjectMenuItem;
    private javax.swing.JMenuItem MaininfoMenuitem;
    private javax.swing.JMenuItem MainitalicMenuitem;
    private javax.swing.JMenuItem MainopenProjectFolderMenuItem;
    private javax.swing.JMenuItem MainopenProjectMenuItem;
    private javax.swing.JMenuItem MainpasteMenuitem;
    private javax.swing.JMenuItem MainprintMenuitem;
    private javax.swing.JMenuItem MainprojExportMenuitem;
    public static javax.swing.JMenuItem Mainrecent1Menuitem;
    public static javax.swing.JMenuItem Mainrecent2Menuitem;
    public static javax.swing.JMenuItem Mainrecent3Menuitem;
    private javax.swing.JMenu MainrecentMenu;
    private javax.swing.JMenuItem MainredoMenuitem;
    private javax.swing.JMenuItem MainsaveMenuItem;
    private javax.swing.JMenuItem MainsearchReplaceMenuitem;
    private javax.swing.JMenuItem MainshortcutsMenuitem;
    private javax.swing.JMenuItem MainsupportMenuitem;
    private javax.swing.JMenuItem MaintimeMenuitem;
    private javax.swing.JMenuItem MaintimestampMenuitem;
    private javax.swing.JRadioButtonMenuItem MaintoolsAlignmentLeftRadioButtonMenuItem;
    private javax.swing.JMenu MaintoolsAlignmentMenu;
    private javax.swing.JMenu MaintoolsMenu;
    private javax.swing.JRadioButtonMenuItem MaintoolsRightRadioButtonMenuItem;
    private javax.swing.JPopupMenu.Separator MaintoolsSep1Separator;
    private javax.swing.JMenu MaintoolsTunerMenu;
    private javax.swing.JMenuItem MaintoolsTunerMinusMenuitem;
    private javax.swing.JMenuItem MaintoolsTunerPlusMenuitem;
    private javax.swing.JMenuItem MainunderlinedMenuitem;
    private javax.swing.JMenuItem MainundoMenuitem;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
