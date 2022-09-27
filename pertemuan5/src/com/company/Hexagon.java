package com.company;
/*
 * @(#)Hexagon.java  1.0 Apr 27, 2008
 *
 *  The MIT License
 *
 *  Copyright (c) 2008 Malachi de AElfweald <malachid@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
//package org.eoti.awt.geom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;


public class Hexagon
        extends Polygon
{
    public enum Corner
    {
        Top, UpperRight, LowerRight, Bottom, LowerLeft, UpperLeft
    }

    public enum Direction
    {
        NorthEast, East, SouthEast, SouthWest, West, NorthWest
    }

    public enum BoundingCorner
    {
        TopLeft, TopRight, BottomLeft, BottomRight
    }

    protected Point2D center;
    protected int size;
    protected double height, width;
    protected double hOffset, wOffset;
    protected Rectangle2D boundingBox;
    protected HashMap<Corner,Point2D> corners;
    protected HashMap<BoundingCorner,Point2D> boundingCorners;

    public Hexagon(double centerX, double centerY, int size)
    {
        this(new Point2D.Double(centerX,centerY), size);
    }

    public Hexagon(Point2D center, int size)
    {
        super();
        this.center = center;
        this.size = size;

        /**
         * MATH:
         *  With the hexagon points={TOP, UPPER-RIGHT, LOWER-RIGHT, BOTTOM, LOWER-LEFT, UPPER-RIGHT}
         *  size = length of each actual segment of the hexagon
         *  width = bounding rectangle width
         *  height = bounding rectangle height
         *  each inner angle is 120 degrees
         *  outside angles are 30-60-90 triangles with 30 near TOP and BOTTOM and 60 near sides
         *  hOffset = height difference between 'size' edge and bounding rectangle corners
         *  wOffset = width difference between TOP/BOTTOM points and bounding rectangle corners
         */

        double thirtyDegrees = Math.toRadians(30);
        hOffset = Math.sin(thirtyDegrees) * size;
        wOffset = Math.cos(thirtyDegrees) * size;

        height = (2 * hOffset) + size;
        width = (2 * wOffset);

        double left = center.getX() - (width/2);
        double right = center.getX() + (width/2);
        double top = center.getY() - (height/2);
        double bottom = center.getY() + (height/2);
        boundingBox = new Rectangle2D.Double(left, top, width, height);

        boundingCorners = new HashMap<BoundingCorner, Point2D>();
        boundingCorners.put(BoundingCorner.TopRight, new Point2D.Double(right, top));
        boundingCorners.put(BoundingCorner.TopLeft, new Point2D.Double(left, top));
        boundingCorners.put(BoundingCorner.BottomRight, new Point2D.Double(right, bottom));
        boundingCorners.put(BoundingCorner.BottomLeft, new Point2D.Double(left, bottom));

        corners = new HashMap<Corner,Point2D>();
        corners.put(Corner.Top, new Point2D.Double(center.getX(), top));
        corners.put(Corner.UpperRight, new Point2D.Double(right, (top + hOffset)));
        corners.put(Corner.LowerRight, new Point2D.Double(right, (bottom - hOffset)));
        corners.put(Corner.Bottom, new Point2D.Double(center.getX(), bottom));
        corners.put(Corner.LowerLeft, new Point2D.Double(left, (bottom - hOffset)));
        corners.put(Corner.UpperLeft, new Point2D.Double(left, (top + hOffset)));

        for(Corner corner : Corner.values())
        {
            Point2D p2d = corners.get(corner);
            addPoint((int)p2d.getX(), (int)p2d.getY());
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[Hexagon: ");
        sb.append(String.format(
                "[Size: %d]",
                size
        ));
        sb.append(String.format(
                "[Height: %2.1f, hOffset: %2.1f]",
                height, hOffset
        ));
        sb.append(String.format(
                "[Width: %2.1f, wOffset: %2.1f]",
                width, wOffset
        ));
        sb.append(String.format(
                "[Center: %2.1fx%2.1f]",
                center.getX(), center.getY()
        ));

        sb.append("[Corners: ");
        for(Corner corner : Corner.values())
        {
            Point2D p2d = corners.get(corner);
            sb.append(String.format(
                    "[%s: %2.1fx%2.1f]",
                    corner, p2d.getX(), p2d.getY()
            ));
        }
        sb.append("]");

        sb.append("[Bounds: ");
        for(BoundingCorner corner : BoundingCorner.values())
        {
            Point2D p2d = boundingCorners.get(corner);
            sb.append(String.format(
                    "[%s: %2.1fx%2.1f]",
                    corner, p2d.getX(), p2d.getY()
            ));
        }
        sb.append("]");


        sb.append(String.format(
                "[BoundingBox: %2.1fx%2.1f to %2.1fx%2.1f]",
                boundingBox.getMinX(),
                boundingBox.getMinY(),
                boundingBox.getMaxX(),
                boundingBox.getMaxY()
        ));

        sb.append("]");
        return sb.toString();
    }

    public Point2D getCenter(){return center;}
    public int getSize(){return size;}
    public double getHeight(){return height;}
    public double getWidth(){return width;}

    @Override
    public Rectangle2D getBounds2D(){return boundingBox;}

    public double getHeightOffset(){return hOffset;}
    public double getWidthOffset(){return wOffset;}

    //public HashMap<Corner,Point2D> getCorners(){return corners;}
    //public HashMap<BoundingCorner,Point2D> getBoundingCorners(){return boundingCorners;}

    public Hexagon resize(int newSize)
    {
        return new Hexagon(center, newSize);
    }

    @Override
    public Object clone()
    {
        return new Hexagon(center, size);
    }
    public Point2D getHexPoint(Corner corner)
    {
        return corners.get(corner);
    }
    public Point2D getBoundPoint(BoundingCorner corner)
    {
        return boundingCorners.get(corner);
    }

    public void translate(double deltaX, double deltaY)
    {
        translate((int)deltaX, (int)deltaY);
    }

    @Override
    public void translate(int deltaX, int deltaY)
    {
        super.translate(deltaX, deltaY);
        boundingBox = super.getBounds2D();

        double top = boundingBox.getMinY();
        double left = boundingBox.getMinX();
        double bottom = boundingBox.getMaxY();
        double right = boundingBox.getMaxX();

        double centerX = boundingBox.getCenterX();
        double centerY = boundingBox.getCenterY();
        center = new Point2D.Double(centerX, centerY);

        boundingCorners.put(BoundingCorner.TopRight, new Point2D.Double(right, top));
        boundingCorners.put(BoundingCorner.TopLeft, new Point2D.Double(left, top));
        boundingCorners.put(BoundingCorner.BottomRight, new Point2D.Double(right, bottom));
        boundingCorners.put(BoundingCorner.BottomLeft, new Point2D.Double(left, bottom));

        corners.put(Corner.Top, new Point2D.Double(center.getX(), top));
        corners.put(Corner.UpperRight, new Point2D.Double(right, (top + hOffset)));
        corners.put(Corner.LowerRight, new Point2D.Double(right, (bottom - hOffset)));
        corners.put(Corner.Bottom, new Point2D.Double(center.getX(), bottom));
        corners.put(Corner.LowerLeft, new Point2D.Double(left, (bottom - hOffset)));
        corners.put(Corner.UpperLeft, new Point2D.Double(left, (top + hOffset)));
    }

    public void align(Rectangle2D bounds, Direction direction)
    {
        // these are defined here INSTEAD of in the switch, or it won't compile
        Point2D newTopRight, newTopLeft, newBottomRight, newBottomLeft;
        Point2D oldTopRight, oldTopLeft, oldBottomRight, oldBottomLeft;

        switch(direction)
        {
            case NorthEast:
                newTopRight = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
                oldTopRight = boundingCorners.get(BoundingCorner.TopRight);
                translate(
                        newTopRight.getX() - oldTopRight.getX(),      // deltaX
                        newTopRight.getY() - oldTopRight.getY()        // deltaY
                );
                break;
            case East:
                newTopRight = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
                oldTopRight = boundingCorners.get(BoundingCorner.TopRight);
                translate(
                        newTopRight.getX() - oldTopRight.getX(),      // deltaX
                        0                          // deltaY
                );
                break;
            case SouthEast:
                newBottomRight = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
                oldBottomRight = boundingCorners.get(BoundingCorner.BottomRight);
                translate(
                        newBottomRight.getX() - oldBottomRight.getX(),    // deltaX
                        newBottomRight.getY() - oldBottomRight.getY()    // deltaY
                );
                break;
            case SouthWest:
                newBottomLeft = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
                oldBottomLeft = boundingCorners.get(BoundingCorner.BottomLeft);
                translate(
                        newBottomLeft.getX() - oldBottomLeft.getX(),    // deltaX
                        newBottomLeft.getY() - oldBottomLeft.getY()      // deltaY
                );
                break;
            case West:
                newTopLeft = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
                oldTopLeft = boundingCorners.get(BoundingCorner.TopLeft);
                translate(
                        newTopLeft.getX() - oldTopLeft.getX(),        // deltaX
                        0                          // deltaY
                );
                break;
            case NorthWest:
                newTopLeft = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
                oldTopLeft = boundingCorners.get(BoundingCorner.TopLeft);
                translate(
                        newTopLeft.getX() - oldTopLeft.getX(),        // deltaX
                        newTopLeft.getY() - oldTopLeft.getY()        // deltaY
                );
                break;
        }
    }

    public void attach(Hexagon toTranslate, Direction direction)
    {
        double horSize = size + hOffset;
        /**
         * To start with, we need to know toTranslate's position RELATIVE to ours
         */
        Point2D topLeft = getBoundPoint(BoundingCorner.TopLeft);
        Point2D toTranslateTopLeft = toTranslate.getBoundPoint(BoundingCorner.TopLeft);
        double deltaX = topLeft.getX() - toTranslateTopLeft.getX();
        double deltaY = topLeft.getY() - toTranslateTopLeft.getY();
        // that should be enough to line them up exactly... now offset it...

        switch(direction)
        {
            case NorthEast:
                deltaX += wOffset;
                deltaY -= horSize;
                break;
            case East:
                deltaX += width;
                break;
            case SouthEast:
                deltaX += wOffset;
                deltaY += horSize;
                break;
            case SouthWest:
                deltaX -= wOffset;
                deltaY += horSize;
                break;
            case West:
                deltaX -= width;
                break;
            case NorthWest:
                deltaX -= wOffset;
                deltaY -= horSize;
                break;
        }
        toTranslate.translate(deltaX, deltaY);
    }

    public static class CreateTile
    {
        public static void main(String[] args)
        {
            if(args.length != 2)
            {
                System.out.println("USAGE: java org.eoti.awt.geom.Hexagon$CreateTile size filename");
                System.out.print("Output file can be: ");
                for(String name : ImageIO.getWriterFormatNames())
                    System.out.print(name + ",");

                System.out.println();
                System.exit(0);
            }

            try{
                int size = Integer.parseInt(args[0]);
                Hexagon hex = new Hexagon(0, 0, size);
                Rectangle2D newBounds = new Rectangle2D.Double(0, 0, hex.getWidth(), hex.getHeight());
                hex.align(newBounds, Direction.NorthWest);

                File file = GraphicsUtil.write(args[1], hex);
                if(file == null)
                    System.out.println("Error creating file");
                else
                    System.out.println("Hexagon tile created: " + file.getAbsolutePath());
            }catch(Exception e){
                System.out.format("Exception: %s", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
class GraphicsUtil
{
    protected Graphics g;
    protected ImageObserver observer;
    public GraphicsUtil(Graphics g, ImageObserver observer)
    {
        this.g = g;
        this.observer = observer;
    }

    public enum Align
    {
        North, NorthEast, East, SouthEast, South, SouthWest, West, NorthWest, Center
    }

    public BufferedImage drawCodePoint(char codePoint, int width, int height)
    {
        return drawCodePoint(codePoint, width, height, g.getFont(), g.getColor());
    }
    public static BufferedImage drawCodePoint(char codePoint, int width, int height, Font font, Color color)
    {
        BufferedImage img = createImage(width, height);
        Graphics2D g2 = img.createGraphics();
        String text = "" + codePoint;
        g2.setColor(color);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), text);
        g2.drawGlyphVector(gv, 0f, (float)gv.getGlyphMetrics(0).getBounds2D().getHeight());
        return img;
    }

    public static BufferedImage createImage(Dimension size)
    {
        return createImage(size.width, size.height);
    }

    public static BufferedImage createImage(int width, int height)
    {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void drawImage(BufferedImage img)
    {
        drawImage(g, img, observer);
    }
    public static void drawImage(Graphics g, BufferedImage img)
    {
        drawImage(g, img, (ImageObserver)null);
    }
    public static void drawImage(Graphics g, BufferedImage img, ImageObserver observer)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), observer);
    }

    public void drawImage(BufferedImage img, Rectangle2D bounds)
    {
        drawImage(g, img, bounds, observer);
    }
    public static void drawImage(Graphics g, BufferedImage img, Rectangle2D bounds)
    {
        drawImage(g, img, bounds, null);
    }
    public static void drawImage(Graphics g, BufferedImage img, Rectangle2D bounds, ImageObserver observer)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(
                img,            // what to draw
                (int)bounds.getMinX(),    // dest left
                (int)bounds.getMinY(),    // dest top
                (int)bounds.getMaxX(),    // dest right
                (int)bounds.getMaxY(),    // dest bottom
                0,              // src left
                0,              // src top
                img.getWidth(),        // src right
                img.getHeight(),      // src bottom
                observer          // to notify of image updates
        );
    }

    public static Rectangle2D contract(RectangularShape rect, double amount)
    {
        return contract(rect, amount, amount);
    }
    public static Rectangle2D contract(RectangularShape rect, double amountX, double amountY)
    {
        return new Rectangle2D.Double(
                rect.getX() + amountX,
                rect.getY() + amountY,
                rect.getWidth() - (2*amountX),
                rect.getHeight() - (2*amountY)
        );
    }
    public static Rectangle2D expand(RectangularShape rect, double amount)
    {
        return expand(rect, amount, amount);
    }
    public static Rectangle2D expand(RectangularShape rect, double amountX, double amountY)
    {
        return new Rectangle2D.Double(
                rect.getX() - amountX,
                rect.getY() - amountY,
                rect.getWidth() + (2*amountX),
                rect.getHeight() + (2*amountY)
        );
    }

    public static Point2D getPoint(RectangularShape bounds, Align align)
    {
        double x = 0.0;
        double y = 0.0;

        switch(align)
        {
            case North:
                x = bounds.getCenterX();
                y = bounds.getMinY();
                break;
            case NorthEast:
                x = bounds.getMaxX();
                y = bounds.getMinY();
                break;
            case East:
                x = bounds.getMaxX();
                y = bounds.getCenterY();
                break;
            case SouthEast:
                x = bounds.getMaxX();
                y = bounds.getMaxY();
                break;
            case South:
                x = bounds.getCenterX();
                y = bounds.getMaxY();
                break;
            case SouthWest:
                x = bounds.getMinX();
                y = bounds.getMaxY();
                break;
            case West:
                x = bounds.getMinX();
                y = bounds.getCenterY();
                break;
            case NorthWest:
                x = bounds.getMinX();
                y = bounds.getMinY();
                break;
            case Center:
                x = bounds.getCenterX();
                y = bounds.getCenterY();
                break;
        }

        return new Point2D.Double(x,y);
    }

    public void drawString(String text, RectangularShape bounds, Align align)
    {
        drawString(g, text, bounds, align, 0.0);
    }
    public void drawString(String text, RectangularShape bounds, Align align, double angle)
    {
        drawString(g, text, bounds, align, angle);
    }
    public static void drawString(Graphics g, String text, RectangularShape bounds, Align align)
    {
        drawString(g, text, bounds, align, 0.0);
    }
    public static void drawString(Graphics g, String text, RectangularShape bounds, Align align, double angle)
    {
        Graphics2D g2 = (Graphics2D)g;
        Font font = g2.getFont();
        if(angle != 0)
            g2.setFont(font.deriveFont(AffineTransform.getRotateInstance(Math.toRadians(angle))));

        Rectangle2D sSize = g2.getFontMetrics().getStringBounds(text, g2);
        Point2D pos = getPoint(bounds, align);
        double x = pos.getX();
        double y = pos.getY() + sSize.getHeight();

        switch(align)
        {
            case North:
            case South:
            case Center:
                x -= (sSize.getWidth() / 2);
                break;
            case NorthEast:
            case East:
            case SouthEast:
                x -= (sSize.getWidth());
                break;
            case SouthWest:
            case West:
            case NorthWest:
                break;
        }

        g2.drawString(text, (float)x, (float)y);
        g2.setFont(font);
    }

/*
  public static void drawGrid(Graphics g, Rectangle2D bounds, int cols, int rows)
  {
    Graphics2D g2 = (Graphics2D)g;
    double minx = bounds.getMinX();
    double miny = bounds.getMinY();
    double maxx = bounds.getMaxX();
    double maxy = bounds.getMaxY();
    double width = bounds.getWidth();
    double height = bounds.getHeight();
    double xInterval = width / cols;
    double yInterval = height / rows;

    for(int col=1; col<=cols; col++)
    {
      for(int row=1; row<=rows; row++)
      {
        Point2D pos = new Point2D.Double(minx + (xInterval * col), miny + (yInterval * row));

        g2.setColor(Color.black);
        g2.drawLine(
          (int)pos.getX(),
          (int)miny,
          (int)pos.getX(),
          (int)maxy
        );
        g2.drawLine(
          (int)minx,
          (int)pos.getY(),
          (int)maxx,
          (int)pos.getY()
        );

        Point2D ctr = new Point2D.Double(
          minx + (xInterval * col) - (xInterval/2),
          miny + (yInterval * row) - (yInterval/2)
        );

        g2.setColor(Color.green);
        g2.drawLine(
          (int)ctr.getX(),
          (int)miny,
          (int)ctr.getX(),
          (int)maxy
        );
        g2.drawLine(
          (int)minx,
          (int)ctr.getY(),
          (int)maxx,
          (int)ctr.getY()
        );
      }
    }
    g2.setColor(Color.black);
    g2.drawRect((int)minx, (int)miny, (int)width, (int)height);
  }
*/

    public void drawImage(BufferedImage img, Align align)
    {
        drawImage(g, img, align, img.getWidth(), img.getHeight(), observer);
    }
    public void drawImage(BufferedImage img, Align align, double newWidth, double newHeight)
    {
        drawImage(g, img, align, newWidth, newHeight, observer);
    }
    public static void drawImage(Graphics g, BufferedImage img, Align align)
    {
        drawImage(g, img, align, img.getWidth(), img.getHeight(), null);
    }
    public static void drawImage(Graphics g, BufferedImage img, Align align, double newWidth, double newHeight)
    {
        drawImage(g, img, align, newWidth, newHeight, null);
    }
    public static void drawImage(Graphics g, BufferedImage img, Align align, double newWidth, double newHeight, ImageObserver observer)
    {
        // TBD
    }

    public void drawCentered(BufferedImage img, Point2D location)
    {
        drawCentered(g, img, location, img.getWidth(), img.getHeight(), observer);
    }
    public void drawCentered(BufferedImage img, Point2D location, double newWidth, double newHeight)
    {
        drawCentered(g, img, location, newWidth, newHeight, observer);
    }
    public static void drawCentered(Graphics g, BufferedImage img, Point2D location)
    {
        drawCentered(g, img, location, img.getWidth(), img.getHeight(), null);
    }
    public static void drawCentered(Graphics g, BufferedImage img, Point2D location, ImageObserver observer)
    {
        drawCentered(g, img, location, img.getWidth(), img.getHeight(), observer);
    }
    public static void drawCentered(Graphics g, BufferedImage img, Point2D location, double newWidth, double newHeight)
    {
        drawCentered(g, img, location, newWidth, newHeight, null);
    }
    public static void drawCentered(Graphics g, BufferedImage img, Point2D location, double newWidth, double newHeight, ImageObserver observer)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(
                img,                  // what to draw
                (int)(location.getX() - (newWidth/2)),  // dest left
                (int)(location.getY() - (newHeight/2)),  // dest top
                (int)(location.getX() + (newWidth/2)),  // dest right
                (int)(location.getY() + (newHeight/2)),  // dest bottom
                0,                    // src left
                0,                    // src top
                img.getWidth(),              // src right
                img.getHeight(),            // src bottom
                observer                // to notify of image updates
        );
    }

    public static File write(String fileName, Shape shape)
            throws IOException
    {
        Rectangle bounds = shape.getBounds();
        BufferedImage img = createImage(bounds.width, bounds.height);
        Graphics2D g2 = (Graphics2D)img.createGraphics();
        //g2.setColor(WebColor.CornSilk.getColor());
        g2.fill(shape);
        g2.setColor(Color.black);
        g2.draw(shape);
        return write(fileName, img);
    }

    public static File write(String fileName, BufferedImage img)
            throws IOException
    {
        File file = new File(fileName);
        if(ImageIO.write(img, "PNG",file))
            return file;

        return null;
    }

    // add something like write(fileName, ArrayList<ArrayList<BufferedImage>>)
    // or write(fileName, BufferedImage ... images)
    // to create a tiled image from multiple source images
}