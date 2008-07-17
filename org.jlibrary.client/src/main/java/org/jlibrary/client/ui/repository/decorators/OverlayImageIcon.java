/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.client.ui.repository.decorators;

import java.util.ArrayList;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.jlibrary.client.SharedImages;

/**
 * @author balajik
 * 
 * This class is used for overlaying image icons
 *
 * 
 */
public class OverlayImageIcon extends CompositeImageDescriptor
{
  /**
   * Base image of the object
   */ 
  private Image baseImage_;
  
  /**
   * Size of the base image 
   */ 
  private Point sizeOfImage_;
  
  /**
   * ArrayList of image keys
   */
  private ArrayList imageKey_; 
  
  private static final int TOP_LEFT = 0;
  private static final int TOP_RIGHT = 1;
  private static final int BOTTOM_LEFT = 2;
  private static final int BOTTOM_RIGHT = 3;
  
  /**
   * Constructor for overlayImageIcon.
   */
  public OverlayImageIcon(Image baseImage, 
                          ArrayList imageKey)
  {
    // Base image of the object
    baseImage_ = baseImage;
    // Demo Image Object 
    imageKey_ = imageKey;
    sizeOfImage_ = new Point(baseImage.getBounds().width, 
                             baseImage.getBounds().height);
  }

  /**
   * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
   * DrawCompositeImage is called to draw the composite image.
   * 
   */
  protected void drawCompositeImage(int arg0, int arg1)
  {
    // Draw the base image
     drawImage(baseImage_.getImageData(), 0, 0); 
     int[] locations = organizeImages();
     for (int i=0; i < imageKey_.size(); i++)
     {
     	String key = (String)imageKey_.get(i);
     	ImageData imageData = SharedImages.getImage(key).getImageData();
        switch(locations[i])
        {
          // Draw on the top left corner
          case TOP_LEFT:
            drawImage(imageData, 0, 0);
            break;
            
          // Draw on top right corner  
          case TOP_RIGHT:
            drawImage(imageData, sizeOfImage_.x - imageData.width, 0);
            break;
            
          // Draw on bottom left  
          case BOTTOM_LEFT:
            drawImage(imageData, 0, sizeOfImage_.y - imageData.height);
            break;
            
          // Draw on bottom right corner  
          case BOTTOM_RIGHT:
            drawImage(imageData, sizeOfImage_.x - imageData.width,
                      sizeOfImage_.y - imageData.height);
            break;
            
        }
     }
   
  }
  
  /**
   * Organize the images. This function scans through the image key and 
   * finds out the location of the images
   */ 
  private int [] organizeImages()
  {
    int[] locations = new int[imageKey_.size()];
    String imageKeyValue;
    for (int i = 0; i < imageKey_.size(); i++)
    {
      imageKeyValue = (String)imageKey_.get(i);
      if (imageKeyValue.equals("Lock"))
      {
        // Draw he lock icon in top left corner. 
        locations[i] = TOP_LEFT;
      }
      if (imageKeyValue.equals("Dirty"))
      {
        // Draw dirty flag indicator in the top right corner
        locations[i] = TOP_RIGHT;
      }
      if (imageKeyValue.equals("Extract"))
      {
        // Draw the extract indicator in the top right corner. 
        locations[i] = TOP_RIGHT;
      }
      if (imageKeyValue.equals("Owner"))
      {
        // Draw he lock icon in top left corner. 
        locations[i] = BOTTOM_LEFT;
      }
      
    }
    return locations;
  }
      

  /**
   * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
   * get the size of the object
   */
  protected Point getSize()
  {
    return sizeOfImage_;
  }
  
  /**
   * Get the image formed by overlaying different images on the base image
   * 
   * @return composite image
   */ 
  public Image getImage()
  {
    return createImage();
  }


}






