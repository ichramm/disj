/*******************************************************************************
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     DisJ Development Group
 *******************************************************************************/

package distributed.plugin.ui.figures;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import distributed.plugin.ui.Activator;
import distributed.plugin.ui.IGraphEditorConstants;

/**
 * @author Me
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NodeFigure extends RoundedRectangle {    

	private static Image IMG_AGENT;
	
	private static ImageDescriptor IDSC_AGENT;
	
	private Label label;
    
    private int numAgent;
    
    private boolean isInit;
    
    private String nodeName;
    
    private String labelText;
    
	static {
		try {
			URL installUrl = Activator.getDefault().getBundle().getEntry("/");
			URL imageUrl = new URL(installUrl, "icons/agent.png");
			IDSC_AGENT = ImageDescriptor.createFromURL(imageUrl);
			IMG_AGENT = IDSC_AGENT.createImage();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}   
    
    public NodeFigure(String name, boolean isInit) {
        super();            
        this.nodeName = name;
        this.isInit = isInit;
        this.labelText = "";        
        this.label = new Label();
        
        this.setIsInit(this.isInit);
        this.add(label);
        ToolbarLayout layout = new ToolbarLayout();
        this.setLayoutManager(layout);
        this.setBackgroundColor(IGraphEditorConstants.DEFAULT_NODE_COLOR);
        this.setOpaque(true);
        this.setSize(IGraphEditorConstants.NODE_SIZE, IGraphEditorConstants.NODE_SIZE);
    }

    /**
     * @see Shape#outlineShape(Graphics)
     */
    public void outlineShape(Graphics graphics) {
    	super.outlineShape(graphics);
    			
       	if(this.numAgent >= 1){
			this.label.setIcon(IMG_AGENT);
		}else{
			this.label.setIcon(null);
		}
       	
//    	if(numAgent == 1){
//    		Rectangle f = Rectangle.SINGLETON;
//    		graphics.drawString("A", f.x, f.y);
//    		System.out.println(this.label.getText() + " num Agent: = 1");
//    		
//    	} else if (numAgent > 1){
//    		//System.out.println("Num Agent: = " + this.numAgent);
//    	}
    	
//    	Rectangle f = Rectangle.SINGLETON;
//    	Rectangle r = getBounds();
//    	f.x = r.x + lineWidth / 2;
//    	f.y = r.y + lineWidth / 2;
//    	f.width = r.width - lineWidth;
//    	f.height = r.height - lineWidth;
//    	graphics.drawRoundRectangle(f, corner.width, corner.height);
    	
    }
    
	public void setName(String newName) {
		this.nodeName = newName;
		this.label.setText(this.nodeName);
	}
    
	public void setNumAgent(int num){
		this.numAgent = num;

	}
    
	public void setIsInit(boolean isInit){
		this.isInit = isInit;
		if(this.isInit){
			this.labelText = "#" + this.nodeName;   		
    	}else{
    		this.labelText = this.nodeName;    		
    	}
		this.label.setText(this.labelText);
	}

}
