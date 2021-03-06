/**
Marvin Project <2007-2010>

Initial version by:

Danilo Rosetto Munoz
Fabio Andrijauskas
Gabriel Ambrosio Archanjo

site: http://marvinproject.sourceforge.net

GPL
Copyright (C) <2007>  

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package org.marvinproject.image.render.mandelbrot;

import marvin.gui.MarvinAttributesPanel;
import marvin.gui.MarvinFilterWindow;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;

/**
 * @author Gabriel Ambr�sio Archanjo
 */
public class Mandelbrot extends MarvinAbstractImagePlugin{

	private final static String MODEL_0 = "Model 0";
	private final static String MODEL_1 = "Model 1";
	
	private MarvinAttributesPanel	attributesPanel;
	private MarvinAttributes 		attributes;
	private int						colorModel;
	int width;
	int height;
	
	public void load() {
		attributes = getAttributes();
		attributes.set("zoom", 1.0);
		attributes.set("xCenter", 0.0);
		attributes.set("yCenter", 0.0);
		attributes.set("iterations", 500);
		attributes.set("colorModel", MODEL_0);
	}

	public void process
	(
		MarvinImage imageIn, 
		MarvinImage imageOut, 
		MarvinAttributes out2,
		MarvinImageMask a_mask, 
		boolean previewMode
	)
	{
		width = imageOut.getWidth();
		height = imageOut.getHeight();
		
		double zoom 	= (Double)attributes.get("zoom");
		double xc   	= (Double)attributes.get("xCenter");
		double yc   	= (Double)attributes.get("yCenter");
		int iterations 	= (Integer)attributes.get("iterations");
		
		if(((String)attributes.get("colorModel")).equals(MODEL_0)){
			colorModel = 0;
		} else{
			colorModel = 1;
		}
		
		
		double factor = 5.0/zoom;
		int iter;

		double x0,y0;
		double nx;
		double ny;
		double nx1;
		double ny1;

		boolean[][] mask = a_mask.getMaskArray();
		
		for (int i=0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				if(mask != null && !mask[j][i]){	continue;	}
				
				x0 = xc - factor/2 + factor*j/width;
				y0 = yc - factor/2 + factor*i/height;

				nx=x0;
				ny=y0;

				iter=0;
				while(Math.sqrt(nx*nx + ny*ny) < 2.0 && iter < iterations){
					nx1 = nx*nx-ny*ny;
					ny1 = 2*(nx*ny);

					nx = nx1+x0;
					ny = ny1+y0;						 
					iter++;
				}
				imageOut.setIntColor(j,height-1-i, getColor(iter, iterations, colorModel));
			}
		}
	}
	
	private int getColor(int iter, int max, int colorModel){
		if(colorModel == 0){
			return getColor0(iter, max);
		}
		return getColor1(iter, max);
	}
	
	 private int getColor1(int iter, int max){
		 double f = 0x00FFFFFF/max;
		 int i = (int)(iter*f);
		 int blue = (i&0xFF0000)>>16;
		 int green = (i&0x00FF00)>>8;
		 int red = (i&0x0000FF);
		 return blue + (green << 8) + (red << 16);
	 }
	 
	 private int getColor0(int iter, int max) {
			int red = (int) ((Math.cos(iter / 10.0f) + 1.0f) * 127.0f);
			int green = (int) ((Math.cos(iter / 20.0f) + 1.0f) * 127.0f);
	        int blue = (int) ((Math.cos(iter / 300.0f) + 1.0f) * 127.0f);
	        return blue + (green << 8) + (red << 16);
		}

	 public MarvinAttributesPanel getAttributesPanel(){
			if(attributesPanel == null){
				attributesPanel = new MarvinAttributesPanel();
				attributesPanel.addLabel("lblXCenter", "X Center:");
				attributesPanel.addTextField("txtXCenter", "xCenter", attributes);
				attributesPanel.newComponentRow();
				
				attributesPanel.addLabel("lblYCenter", "Y Center:");
				attributesPanel.addTextField("txtYCenter", "yCenter", attributes);
				attributesPanel.newComponentRow();
				
				attributesPanel.addLabel("lblZoom", "Zoom:");
				attributesPanel.addTextField("txtZoom", "zoom", attributes);
				attributesPanel.newComponentRow();
				
				attributesPanel.addLabel("lblIterations", "Iterations:");
				attributesPanel.addTextField("txtIterations", "iterations", attributes);
				attributesPanel.newComponentRow();
				
				attributesPanel.addLabel("lblColorModel", "Color Model:");
				attributesPanel.addComboBox("combColorModel", "colorModel", new Object[]{MODEL_0, MODEL_1}, attributes);
			}
			return attributesPanel;
		}
}
