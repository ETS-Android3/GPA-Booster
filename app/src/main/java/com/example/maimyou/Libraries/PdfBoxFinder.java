package com.example.maimyou.Libraries;

import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.itextpdf.awt.geom.Point2D;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <a href="https://stackoverflow.com/questions/51380677/extracting-text-from-pdf-java-using-pdfbox-library-from-a-tables-rows-with-di">
 * Extracting text from pdf (java using pdfbox library) from a table's rows with different heights
 * </a>
 * <br/>
 * <a href="https://www.info.uvt.ro/wp-content/uploads/2018/07/Programare-licenta-5-Iulie-2018_1.pdf">
 * Programare-licenta-5-Iulie-2018_1.pdf
 * </a>
 * <p>
 * This stream engine class determines the lines framing table cells. It is
 * implemented to recognize lines created like in the example PDF shared by
 * the OP, i.e. lines drawn as long thin filled rectangles. It is easily
 * possible to generalize this for frame lines drawn differently, c.f. the
 * method {@link #processPath()}.
 * </p>
 * <p>
 * For a given {@link PDPage} <code>page</code> use this class like this:
 * </p>
 * <pre>
 * PdfBoxFinder boxFinder = new PdfBoxFinder(page);
 * boxFinder.processPage(page);
 * </pre>
 * <p>
 * After this you can retrieve the boxes ({@link Rectangle2D} instances with
 * coordinates according to the PDF coordinate system, e.g. for decorating
 * the table cells) or regions ({@link Rectangle2D} instances with coordinates
 * according to the PDFBox text extraction API, e.g. for initializing the
 * </p>
 *
 * @author mkl
 */
public class PdfBoxFinder extends PDFGraphicsStreamEngine {
    /**
     * Supply the page to analyze here; to analyze multiple pages
     * create multiple {@link PdfBoxFinder} instances.
     */
    public PdfBoxFinder(PDPage page) {
        super(page);
    }

    @Override
    public void appendRectangle(PointF p0, PointF p1, PointF p2, PointF p3) throws IOException {
        path.add(new Rectangle(new Point2D.Double(p0.x, p0.y), new Point2D.Double(p1.x, p1.y), new Point2D.Double(p2.x, p2.y), new Point2D.Double(p3.x, p3.y)));
    }

    /**
     * The boxes ({@link Rectangle2D} instances with coordinates according to
     * the PDF coordinate system, e.g. for decorating the table cells) the
     * {@link PdfBoxFinder} has recognized on the current page.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map<String, Rectangle2D> getBoxes() {
        consolidateLists();
        Map<String, Rectangle2D> result = new HashMap<>();
        if (!horizontalLines.isEmpty() && !verticalLines.isEmpty()) {
            Interval top = horizontalLines.get(horizontalLines.size() - 1);
            char rowLetter = 'A';
            for (int i = horizontalLines.size() - 2; i >= 0; i--, rowLetter++) {
                Interval bottom = horizontalLines.get(i);
                Interval left = verticalLines.get(0);
                int column = 1;
                for (int j = 1; j < verticalLines.size(); j++, column++) {
                    Interval right = verticalLines.get(j);
                    String name = String.format("%s%s", rowLetter, column);
                    Rectangle2D rectangle = new Rectangle2D.Float(left.from, bottom.from, right.to - left.from, top.to - bottom.from);
                    result.put(name, rectangle);
                    left = right;
                }
                top = bottom;
            }
        }
        return result;
    }

    //    /**
//     * The regions ({@link Rectangle2D} instances with coordinates according
//     * to the PDFBox text extraction API, e.g. for initializing the regions of
//     * a {@link PDFTextStripperByArea}) the {@link PdfBoxFinder} has recognized
//     * on the current page.
//     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map<String, Rectangle2D> getRegions() {
        PDRectangle cropBox = getPage().getCropBox();
        float xOffset = cropBox.getLowerLeftX();
        float yOffset = cropBox.getUpperRightY();
        Map<String, Rectangle2D> result = getBoxes();
        for (Map.Entry<String, Rectangle2D> entry : result.entrySet()) {
            Rectangle2D box = entry.getValue();
            Rectangle2D region = new Rectangle2D.Float(xOffset + (float) box.getX(), yOffset - (float) (box.getY() + box.getHeight()), (float) box.getWidth(), (float) box.getHeight());
            entry.setValue(region);
        }
        return result;
    }

    /**
     * <p>
     * Processes the path elements currently in the {@link #path} list and
     * eventually clears the list.
     * </p>
     * <p>
     * Currently only elements are considered which
     * </p>
     * <ul>
     * <li>are {@link Rectangle} instances;
     * <li>are filled fairly black;
     * <li>have a thin and long form; and
     * <li>have sides fairly parallel to the coordinate axis.
     * </ul>
     */
    void processPath() throws IOException {
        PDColor color = getGraphicsState().getNonStrokingColor();
        if (!isBlack(color)) {
            logger.debug("Dropped path due to non-black fill-color.");
            return;
        }

        for (PathElement pathElement : path) {
            if (pathElement instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) pathElement;

                double p0p1 = rectangle.p0.distance(rectangle.p1);
                double p1p2 = rectangle.p1.distance(rectangle.p2);
                boolean p0p1small = p0p1 < 3;
                boolean p1p2small = p1p2 < 3;

                if (p0p1small) {
                    if (p1p2small) {
                        logger.debug("Dropped rectangle too small on both sides.");
                    } else {
                        processThinRectangle(rectangle.p0, rectangle.p1, rectangle.p2, rectangle.p3);
                    }
                } else if (p1p2small) {
                    processThinRectangle(rectangle.p1, rectangle.p2, rectangle.p3, rectangle.p0);
                } else {
                    logger.debug("Dropped rectangle too large on both sides.");
                }
            }
        }
        path.clear();
    }

    /**
     * The argument points shall be sorted to have (p0, p1) and (p2, p3) be the small
     * edges and (p1, p2) and (p3, p0) the long ones.
     */
    void processThinRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        float longXDiff = (float) Math.abs(p2.getX() - p1.getX());
        float longYDiff = (float) Math.abs(p2.getY() - p1.getY());
        boolean longXDiffSmall = longXDiff * 10 < longYDiff;
        boolean longYDiffSmall = longYDiff * 10 < longXDiff;

        if (longXDiffSmall) {
            verticalLines.add(new Interval(p0.getX(), p1.getX(), p2.getX(), p3.getX()));
        } else if (longYDiffSmall) {
            horizontalLines.add(new Interval(p0.getY(), p1.getY(), p2.getY(), p3.getY()));
        } else {
            logger.debug("Dropped rectangle too askew.");
        }
    }

    /**
     * Sorts the {@link #horizontalLines} and {@link #verticalLines} lists and
     * merges fairly identical entries.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    void consolidateLists() {
        for (List<Interval> intervals : Arrays.asList(horizontalLines, verticalLines)) {
            intervals.sort(null);
            for (int i = 1; i < intervals.size(); ) {
                if (intervals.get(i - 1).combinableWith(intervals.get(i))) {
                    Interval interval = intervals.get(i - 1).combineWith(intervals.get(i));
                    intervals.set(i - 1, interval);
                    intervals.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    /**
     * Checks whether the given color is black'ish.
     */
    boolean isBlack(PDColor color) throws IOException {
        int value = color.toRGB();
        for (int i = 0; i < 2; i++) {
            int component = value & 0xff;
            if (component > 5)
                return false;
            value /= 256;
        }
        return true;
    }

    //
    // PDFGraphicsStreamEngine overrides
    //


    @Override
    public void endPath() throws IOException {
        path.clear();
    }

    @Override
    public void strokePath() throws IOException {
        path.clear();
    }

    @Override
    public void fillPath(Path.FillType windingRule) throws IOException {
        processPath();
    }

    @Override
    public void fillAndStrokePath(Path.FillType windingRule) throws IOException {
        processPath();
    }


    @Override
    public void drawImage(PDImage pdImage) throws IOException {
    }

    @Override
    public void clip(Path.FillType windingRule) throws IOException {

    }

    @Override
    public void moveTo(float x, float y) throws IOException {
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
    }

    @Override
    public PointF getCurrentPoint() throws IOException {
        return null;
    }

    @Override
    public void closePath() throws IOException {
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    }

    //
    // inner classes
    //
    class Interval implements Comparable<Interval> {
        final float from;
        final float to;

        Interval(float... values) {
            Arrays.sort(values);
            this.from = values[0];
            this.to = values[values.length - 1];
        }

        Interval(double... values) {
            Arrays.sort(values);
            this.from = (float) values[0];
            this.to = (float) values[values.length - 1];
        }

        boolean combinableWith(Interval other) {
            if (this.from > other.from)
                return other.combinableWith(this);
            if (this.to < other.from)
                return false;
            float intersectionLength = Math.min(this.to, other.to) - other.from;
            float thisLength = this.to - this.from;
            float otherLength = other.to - other.from;
            return (intersectionLength >= thisLength * .9f) || (intersectionLength >= otherLength * .9f);
        }

        Interval combineWith(Interval other) {
            return new Interval(this.from, this.to, other.from, other.to);
        }

        @Override
        public int compareTo(Interval o) {
            return this.from == o.from ? Float.compare(this.to, o.to) : Float.compare(this.from, o.from);
        }

        @Override
        public String toString() {
            return String.format("[%3.2f, %3.2f]", from, to);
        }
    }

    interface PathElement {
    }

    class Rectangle implements PathElement {
        Point2D p0, p1, p2, p3;

        Rectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
            this.p0 = p0;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }
    }

    //
    // members
    //
    final List<PathElement> path = new ArrayList<>();
    final List<Interval> horizontalLines = new ArrayList<>();
    final List<Interval> verticalLines = new ArrayList<>();
    final Logger logger = LoggerFactory.getLogger(PdfBoxFinder.class);
}