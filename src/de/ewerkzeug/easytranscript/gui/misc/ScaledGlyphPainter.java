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

import javax.swing.text.*;
import java.awt.image.BufferedImage;

import java.awt.*;
import java.lang.reflect.Field;

public class ScaledGlyphPainter extends GlyphView.GlyphPainter {

    static ScaledGlyphPainter instance = new ScaledGlyphPainter();

    public static ScaledGlyphPainter getInstance() {
        return instance;
    }

    static final Graphics2D painterGr;

    static {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        painterGr = (Graphics2D) img.getGraphics();
        painterGr.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * Determine the span the glyphs given a start location (for tab expansion).
     *
     * @param v
     * @param p0
     * @param p1
     * @param e
     * @param x
     * @return
     */
    @Override
    public float getSpan(GlyphView v, int p0, int p1,
            TabExpander e, float x) {
        sync(v);
        Segment text = getText(v, p0, p1);
        int[] justificationData = getJustificationData(v);
        float width = getTabbedTextWidth(v, text, metrics, x, e, p0, justificationData);
        return width;
    }

    @Override
    public float getHeight(GlyphView v) {
        sync(v);
        return metrics.getHeight();
    }

    /**
     * Fetches the ascent above the baseline for the glyphs corresponding to the
     * given range in the model.
     *
     * @param v
     * @return
     */
    @Override
    public float getAscent(GlyphView v) {
        sync(v);
        return metrics.getAscent();
    }

    /**
     * Fetches the descent below the baseline for the glyphs corresponding to
     * the given range in the model.
     *
     * @param v
     * @return
     */
    @Override
    public float getDescent(GlyphView v) {
        sync(v);
        return metrics.getDescent();
    }

    /**
     * Paints the glyphs representing the given range.
     *
     * @param v
     * @param g
     * @param a
     * @param p0
     * @param p1
     */
    @Override
    public void paint(GlyphView v, Graphics g, Shape a, int p0, int p1) {
        sync(v);
        Segment text;
        TabExpander expander = v.getTabExpander();
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();

        // determine the x coordinate to render the glyphs
        int x = alloc.x;
        int p = v.getStartOffset();
        int[] justificationData = getJustificationData(v);
        if (p != p0) {
            text = getText(v, p, p0);
            float width = getTabbedTextWidth(v, text, metrics, x, expander, p, justificationData);
            x += width;
        }

        // determine the y coordinate to render the glyphs
        int y = alloc.y + metrics.getHeight() - metrics.getDescent();

        // render the glyphs
        text = getText(v, p0, p1);
        g.setFont(metrics.getFont());

        drawTabbedText(v, text, x, y, g, expander, p0, justificationData);
    }

    @Override
    public Shape modelToView(GlyphView v, int pos, Position.Bias bias,
            Shape a) throws BadLocationException {
        sync(v);
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
        int p0 = v.getStartOffset();
        int p1 = v.getEndOffset();
        TabExpander expander = v.getTabExpander();
        Segment text;

        if (pos == p1) {
            // The caller of this is left to right and borders a right to
            // left view, return our end location.
            return new Rectangle(alloc.x + alloc.width, alloc.y, 0, metrics.getHeight());
        }
        if ((pos >= p0) && (pos <= p1)) {
            // determine range to the left of the position
            text = getText(v, p0, pos);
            int[] justificationData = getJustificationData(v);
            int width = (int) getTabbedTextWidth(v, text, metrics, alloc.x, expander, p0, justificationData);
            return new Rectangle(alloc.x + width, alloc.y, 0, metrics.getHeight());
        }
        throw new BadLocationException("modelToView - can't convert", p1);
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param v the view containing the view coordinates
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param a the allocated region to render into
     * @param biasReturn always returns <code>Position.Bias.Forward</code> as
     * the zero-th element of this array
     * @return the location within the model that best represents the given
     * point in the view
     * @see View#viewToModel
     */
    @Override
    public int viewToModel(GlyphView v, float x, float y, Shape a,
            Position.Bias[] biasReturn) {
        sync(v);
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
        int p0 = v.getStartOffset();
        int p1 = v.getEndOffset();
        TabExpander expander = v.getTabExpander();
        Segment text = getText(v, p0, p1);
        int[] justificationData = getJustificationData(v);
        int offs = getTabbedTextOffset(v, text, metrics, alloc.x, (int) x, expander, p0, true, justificationData);
        int retValue = p0 + offs;
        if (retValue == p1) {
            // No need to return backward bias as GlyphPainter1 is used for
            // ltr text only.
            retValue--;
        }
        biasReturn[0] = Position.Bias.Forward;
        return retValue;
    }

    /**
     * Determines the best location (in the model) to break the given view. This
     * method attempts to break on a whitespace location. If a whitespace
     * location can't be found, the nearest character location is returned.
     *
     * @param v the view
     * @param p0 the location in the model where the fragment should start its
     * representation >= 0
     * @param x the graphic location along the axis that the broken view would
     * occupy >= 0; this may be useful for things like tab calculations
     * @param len specifies the distance into the view where a potential break
     * is desired >= 0
     * @return the model location desired for a break
     * @see View#breakView
     */
    @Override
    public int getBoundedPosition(GlyphView v, int p0, float x, float len) {
        sync(v);
        TabExpander expander = v.getTabExpander();
        Segment s = getText(v, p0, v.getEndOffset());
        int[] justificationData = getJustificationData(v);
        int index = getTabbedTextOffset(v, s, metrics, (int) x, (int) (x + len), expander, p0, false, justificationData);
        int p1 = p0 + index;
        return p1;
    }

    void sync(GlyphView v) {
        Font f = v.getFont();
        if ((metrics == null) || (!f.equals(metrics.getFont()))) {
            // fetch a new FontMetrics
            Container c = v.getContainer();

            metrics = (c != null) ? c.getFontMetrics(f) : Toolkit.getDefaultToolkit().getFontMetrics(f);
        }
    }

    private int[] getJustificationData(GlyphView v) {
        View parent = v.getParent();
        int[] ret = null;

        //use reflection to get the data
        Class pClass = parent.getClass();
        if (pClass.isAssignableFrom(ParagraphView.class.getDeclaredClasses()[0])) { //if (parent instanceof ParagraphView.Row) {
            try {
                Field f = pClass.getDeclaredField("justificationData");
                if (f != null) {
                    f.setAccessible(true);
                    ret = (int[]) f.get(parent);
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            }
        }

        return ret;
    }

    // --- variables ---------------------------------------------
    FontMetrics metrics;
    static char[] SPACE_CHAR = new char[]{' '};

    static Segment getText(View v, int start, int end) {
        Segment s = new Segment();
        try {
            s.array = v.getDocument().getText(start, end - start).toCharArray();
            s.offset = 0;
            s.count = end - start;
        } catch (BadLocationException e) {
        }
        return s;
    }

    float getTabbedTextWidth(View view, Segment s, FontMetrics metrics, float x,
            TabExpander e, int startOffset,
            int[] justificationData) {
        float nextX = x;
        char[] txt = s.array;
        String txtStr = new String(txt);
        int txtOffset = s.offset;
        int n = s.offset + s.count;
        int charCount = 0;
        int spaceAddon = 0;
        int spaceAddonLeftoverEnd = -1;
        int startJustifiableContent = 0;
        int endJustifiableContent = 0;
        if (justificationData != null) {
            int offset = -startOffset + txtOffset;
            View parent = null;
            if (view != null && (parent = view.getParent()) != null) {
                offset += parent.getStartOffset();
            }
            spaceAddon = justificationData[0];
            spaceAddonLeftoverEnd = justificationData[1] + offset;
            startJustifiableContent = justificationData[2] + offset;
            endJustifiableContent = justificationData[3] + offset;
        }

        for (int i = txtOffset; i < n; i++) {
            if (txt[i] == '\t'
                    || ((spaceAddon != 0 || i <= spaceAddonLeftoverEnd)
                    && (txt[i] == ' ')
                    && startJustifiableContent <= i
                    && i <= endJustifiableContent)) {
                nextX += metrics.getStringBounds(txtStr, i - charCount, i, painterGr).getWidth();
                charCount = 0;
                if (txt[i] == '\t') {
                    if (e != null) {
                        nextX = e.nextTabStop((float) nextX, startOffset + i - txtOffset);
                    } else {
                        nextX += metrics.getStringBounds(SPACE_CHAR, 0, 1, painterGr).getWidth();
                        nextX = (int) nextX;
                    }
                } else if (txt[i] == ' ') {
                    nextX += metrics.getStringBounds(SPACE_CHAR, 0, 1, painterGr).getWidth() + spaceAddon;
                    nextX = (int) nextX;
                    if (i <= spaceAddonLeftoverEnd) {
                        nextX++;
                    }
                }
            } else if (txt[i] == '\n') {
                // Ignore newlines, they take up space and we shouldn't be
                // counting them.
                nextX += (float) metrics.getStringBounds(txtStr, i - charCount, i, painterGr).getWidth();
                charCount = 0;
            } else {
                charCount++;
            }
        }

        nextX += metrics.getStringBounds(txtStr, n - charCount, n, painterGr).getWidth();
        return nextX - x;
    }

    float drawTabbedText(View view, Segment s, float x, float y, Graphics g,
            TabExpander e, int startOffset, int[] justificationData) {
        float nextX = x;
        char[] txt = s.array;
        String txtStr = new String(txt);
        int txtOffset = s.offset;
        int flushLen = 0;
        int flushIndex = s.offset;
        int spaceAddon = 0;
        int spaceAddonLeftoverEnd = -1;
        int startJustifiableContent = 0;
        int endJustifiableContent = 0;
        if (justificationData != null) {
            int offset = -startOffset + txtOffset;
            View parent = null;
            if (view != null && (parent = view.getParent()) != null) {
                offset += parent.getStartOffset();
            }
            spaceAddon = justificationData[0];
            spaceAddonLeftoverEnd = justificationData[1] + offset;
            startJustifiableContent = justificationData[2] + offset;
            endJustifiableContent = justificationData[3] + offset;
        }
        int n = s.offset + s.count;
        for (int i = txtOffset; i < n; i++) {
            if (txt[i] == '\t'
                    || ((spaceAddon != 0 || i <= spaceAddonLeftoverEnd)
                    && (txt[i] == ' ')
                    && startJustifiableContent <= i
                    && i <= endJustifiableContent)) {
                if (flushLen > 0) {
                    ((Graphics2D) g).drawString(txtStr.substring(flushIndex, flushIndex + flushLen), x, y);
                    //corrected position
                    nextX += metrics.getStringBounds(txtStr, flushIndex, flushIndex + flushLen, painterGr).getWidth();
                    flushLen = 0;
                }
                flushIndex = i + 1;
                if (txt[i] == '\t') {
                    if (e != null) {
                        nextX = e.nextTabStop((float) nextX, startOffset + i - txtOffset);
                    } else {
                        nextX += (float) metrics.getStringBounds(SPACE_CHAR, 0, 1, painterGr).getWidth();
                        nextX = (int) nextX;
                    }
                } else if (txt[i] == ' ') {
                    nextX += (float) metrics.getStringBounds(SPACE_CHAR, 0, 1, painterGr).getWidth() + spaceAddon;
                    if (i <= spaceAddonLeftoverEnd) {
                        nextX++;
                    }
                }
                x = nextX;
            } else if ((txt[i] == '\n') || (txt[i] == '\r')) {
                if (flushLen > 0) {
                    ((Graphics2D) g).drawString(txtStr.substring(flushIndex, flushIndex + flushLen), x, y);
                    //corrected
                    nextX += metrics.getStringBounds(txtStr, flushIndex, flushIndex + flushLen, painterGr).getWidth();
                    flushLen = 0;
                }
                flushIndex = i + 1;
                x = nextX;
            } else {
                flushLen += 1;
            }
        }
        if (flushLen > 0) {
            ((Graphics2D) g).drawString(txtStr.substring(flushIndex, flushIndex + flushLen), x, y);
            //corrected
            nextX += metrics.getStringBounds(txtStr, flushIndex, flushIndex + flushLen, painterGr).getWidth();
        }
        return nextX;
    }

    int getTabbedTextOffset(View view,
            Segment s,
            FontMetrics metrics,
            int x0, int x, TabExpander e,
            int startOffset,
            boolean round,
            int[] justificationData) {
        if (x0 >= x) {
            // x before x0, return.
            return 0;
        }
        float currX = x0;
        float nextX = currX;
        // s may be a shared segment, so it is copied prior to calling
        // the tab expander
        char[] txt = s.array;
        int txtOffset = s.offset;
        int txtCount = s.count;
        int spaceAddon = 0;
        int spaceAddonLeftoverEnd = -1;
        int startJustifiableContent = 0;
        int endJustifiableContent = 0;
        if (justificationData != null) {
            int offset = -startOffset + txtOffset;
            View parent = null;
            if (view != null && (parent = view.getParent()) != null) {
                offset += parent.getStartOffset();
            }
            spaceAddon = justificationData[0];
            spaceAddonLeftoverEnd = justificationData[1] + offset;
            startJustifiableContent = justificationData[2] + offset;
            endJustifiableContent = justificationData[3] + offset;
        }
        int n = s.offset + s.count;
        for (int i = s.offset; i < n; i++) {
            if (txt[i] == '\t'
                    || ((spaceAddon != 0 || i <= spaceAddonLeftoverEnd)
                    && (txt[i] == ' ')
                    && startJustifiableContent <= i
                    && i <= endJustifiableContent)) {
                if (txt[i] == '\t') {
                    if (e != null) {
                        nextX = (int) e.nextTabStop((float) nextX,
                                startOffset + i - txtOffset);
                    } else {
                        nextX += metrics.getStringBounds(SPACE_CHAR, 0, 1, painterGr).getWidth();
                    }
                } else if (txt[i] == ' ') {
                    nextX += metrics.getStringBounds(SPACE_CHAR, 0, 1, painterGr).getWidth() + spaceAddon;
                    nextX = (int) nextX;

                    if (i <= spaceAddonLeftoverEnd) {
                        nextX++;
                    }
                }
            } else {
                nextX += metrics.getStringBounds(txt, i, i + 1, painterGr).getWidth();
            }
            if ((x >= currX) && (x < nextX)) {
                // found the hit position... return the appropriate side
                if ((round == false) || ((x - currX) < (nextX - x))) {
                    return i - txtOffset;
                } else {
                    return i + 1 - txtOffset;
                }
            }
            currX = nextX;
        }

        // didn't find, return end offset
        return txtCount;
    }
}
