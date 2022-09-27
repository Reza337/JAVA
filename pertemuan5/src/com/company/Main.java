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