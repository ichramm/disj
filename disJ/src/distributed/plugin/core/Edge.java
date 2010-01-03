/*******************************************************************************
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     DisJ Development Group
 *******************************************************************************/

package distributed.plugin.core;

import java.io.Serializable;
import java.util.Random;

import distributed.plugin.random.*;
import distributed.plugin.runtime.GraphFactory;
import distributed.plugin.ui.models.LinkElement;

/**
 * @author Me
 * 
 * An edge class that stores information about a path between 2 entities
 */
public class Edge implements Serializable {
    
    static final long serialVersionUID = IConstants.SERIALIZE_VERSION;

	private String graphId;
	
	private String edgeId; // unique id system use only

	private short msgFlowType; // i.e. FIFO

	private int lastestMsgTimeForStart;

	private int lastestMsgTimeForEnd;

	private int totalMsg; // total messages have been passed throug this edge

	private boolean isReliable;
	
	private int probOfFailure;

	private short delayType; // a type of delay i.e random uniform,

	// synchronize

	private short delaySeed; // it's usufull for synchronize delay and random

	// uniform type

	private short direction;

	private Node start;

	private Node end;

	private EdgeLog log;

	private LinkElement linkElement;
	
	/**
	 * Constructor, create an edge with default values
	 * 
	 * @param id
	 * @param direction
	 * @throws DisJException
	 */
	public Edge(String graphId, String id, short direction) throws DisJException {
	    this(graphId, id, direction, null, null);
	}

	/**
	 * Constructor, create an edge with default values
	 * 
	 * @param graphId
	 * @param id
	 * @param direction
	 * @param start
	 * @param end
	 * @throws DisJException
	 */
	private Edge(String graphId, String id, short direction, Node start, Node end)
			throws DisJException {
		this(graphId, id, true, direction, IConstants.FIFO_TYPE, IConstants.LOCAL_FIXED,
				(short) 1, start, end);
	}

	public Edge(String graphId, String id, boolean isReliable,
			short direction, short msgFlowType, short delayType,
			short delaySeed, Node start, Node end) throws DisJException {

		this.graphId = graphId;
		this.msgFlowType = msgFlowType;
		this.totalMsg = 0;
		this.edgeId = id;
		this.probOfFailure = 5;
		this.direction = direction;
		this.isReliable = isReliable;
		this.delayType = delayType;
		this.delaySeed = delaySeed;
		if (start == end && start != null)
			throw new DisJException(IConstants.ERROR_10, start.toString());

		this.start = start;
		this.end = end;
		this.log = new EdgeLog(graphId);
	}

	/**
	 * Increment number of message has been use in this link
	 * 
	 */
	public void incNumMsg() {
		this.totalMsg++;
	}

	/**
	 * Get a arrival time of a message on this edge based on the sender and delay
	 * type
	 * 
	 * @param sender
	 * @return
	 */
	public int getDelayTime(Node sender, int curTime) {

		if (this.delayType == IConstants.LOCAL_FIXED) {	
			if (sender.equals(this.start)) {
				this.lastestMsgTimeForStart = curTime + this.delaySeed;				
				return this.lastestMsgTimeForStart;
				
			} else if (sender.equals(this.end)) {
				this.lastestMsgTimeForEnd = curTime + this.delaySeed;
				return this.lastestMsgTimeForEnd;
				
			} else {				
				String msg = "Edge " +  this + " does not contain sender node " + sender;
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
	
		} else if (this.delayType == IConstants.LOCAL_RANDOM_UNIFORM) {
			int base = 0;
			if (sender.equals(this.start)) {
				if(this.lastestMsgTimeForStart > curTime){
					base = this.lastestMsgTimeForStart;
				}else{
					base = curTime;
				}							
			} else if (sender.equals(this.end)) {
				if(this.lastestMsgTimeForEnd > curTime){
					base = this.lastestMsgTimeForEnd;
				}else{
					base = curTime;
				}				
			}  else {				
				String msg = "Edge " +  this + " does not contain sender node " + sender;
				System.err.println(msg);
				throw new RuntimeException(msg);
			}

			IRandom r = Uniform.getInstance(System.currentTimeMillis());
			int t = 0;						
			if (this.msgFlowType == IConstants.FIFO_TYPE) {
				t = r.nextInt(IConstants.MAX_RANDOM_RANGE) + base + 1;
			}else{
				t = r.nextInt(IConstants.MAX_RANDOM_RANGE) + curTime + 1;
			}
			if (sender.equals(this.start)) {
				this.lastestMsgTimeForStart = t;
				
			} else {
				this.lastestMsgTimeForEnd = t;
			}
			return t;

		} else if (this.delayType == IConstants.LOCAL_RANDOM_POISSON) {
			int base = 0;
			if (sender.equals(this.start)) {
				if(this.lastestMsgTimeForStart > curTime){
					base = this.lastestMsgTimeForStart;
				}else{
					base = curTime;
				}							
			} else if (sender.equals(this.end)) {
				if(this.lastestMsgTimeForEnd > curTime){
					base = this.lastestMsgTimeForEnd;
				}else{
					base = curTime;
				}				
			}  else {				
				String msg = "Edge " +  this + " does not contain sender node " + sender;
				System.err.println(msg);
				throw new RuntimeException(msg);
			}

			IRandom r = Poisson.getInstance(System.currentTimeMillis());
			int t = 0;						
			if (this.msgFlowType == IConstants.FIFO_TYPE) {
				t = r.nextInt(IConstants.MAX_RANDOM_RANGE) + base + 1;
			}else{
				t = r.nextInt(IConstants.MAX_RANDOM_RANGE) + curTime + 1;
			}
			if (sender.equals(this.start)) {
				this.lastestMsgTimeForStart = t;
				
			} else {
				this.lastestMsgTimeForEnd = t;
			}
			return t;
		} else {
			IRandom clientRandom = GraphFactory.getGraph(graphId).getClientRandom();
			if(clientRandom == null)
			{
				String msg = "You haven't set your random number generator!";
				System.err.println(msg);
				throw new RuntimeException(msg);
			}
			else{
				int base = 0;
				if (sender.equals(this.start)) {
					if(this.lastestMsgTimeForStart > curTime){
						base = this.lastestMsgTimeForStart;
					}else{
						base = curTime;
					}							
				} else if (sender.equals(this.end)) {
					if(this.lastestMsgTimeForEnd > curTime){
						base = this.lastestMsgTimeForEnd;
					}else{
						base = curTime;
					}				
				}  else {				
					String msg = "Edge " +  this + " does not contain sender node " + sender;
					System.err.println(msg);
					throw new RuntimeException(msg);
				}

				int t = 0;						
				if (this.msgFlowType == IConstants.FIFO_TYPE) {
					t = clientRandom.nextInt(IConstants.MAX_RANDOM_RANGE) + base + 1;
				}else{
					t = clientRandom.nextInt(IConstants.MAX_RANDOM_RANGE) + curTime + 1;
				}
				if (sender.equals(this.start)) {
					this.lastestMsgTimeForStart = t;
					
				} else {
					this.lastestMsgTimeForEnd = t;
				}
				return t;
			}
		}

	}

	/**
	 * Log a message passing through this edge
	 * 
	 * @param msgLabel
	 * @param sender
	 */
	public void logMsgPassed(String msgLabel, String sender) {
		this.log.logMsgPassed(msgLabel, sender);
	}

	/**
	 * @return Returns the end.
	 */
	public Node getEnd() {
		return end;
	}

	/**
	 * @return Returns the isReliable.
	 */
	public boolean isReliable() {
		return isReliable;
	}

	/**
	 * @return Returns the edgeId.
	 */
	public String getEdgeId() {
		return edgeId;
	}

	/**
	 * @return Returns the start.
	 */
	public Node getStart() {
		return start;
	}

	/**
	 * @return Returns the numMsg.
	 */
	public int getNumMsg() {
		return totalMsg;
	}

	public void resetNumMsg(){
	    this.totalMsg = 0;
	}
	
	public String getGraphId() {
		return this.graphId;
	}

	/**
	 * Get other end node that connected to this edge with a given node
	 * 
	 * @param node
	 * @return
	 */
	public Node getOthereEnd(Node node) throws DisJException {

		if (node == null)
			throw new NullPointerException(IConstants.RUNTIME_ERROR_0);

		if (this.start == node)
			return this.end;

		if (this.end == node)
			return this.start;
		else
			throw new DisJException(IConstants.ERROR_9, "Node="
					+ node.toString() + " : Edge=" + this.toString());
	}

	/**
	 * @return Returns the delayType.
	 */
	public short getDelayType() {
		return delayType;
	}

	/**
	 * @return Returns the delaySeed.
	 */
	public short getDelaySeed() {
		return delaySeed;
	}

	/**
	 * @param delaySeed
	 *            The delaySeed to set.
	 */
	public void setDelaySeed(short delaySeed) {
		this.delaySeed = delaySeed;
	}

	/**
	 * @return Returns the msgFlowType.
	 */
	public short getMsgFlowType() {
		return msgFlowType;
	}

	/**
	 * Wrtie a current states in a readable format
	 */
	public String toString() {
		return ("\n\nEdge: " + this.edgeId + "\nReliable: " + this.isReliable
				+ "\nMessage Flow Type: " + this.msgFlowType
				+ "\nNum Message Entered: " + this.totalMsg + "\nDelay type: "
				+ this.delayType + "\nNode 1: " + this.start.getNodeId()
				+ "\nNode 2: " + this.end.getNodeId());
	}

	/**
	 * @return Returns the direction.
	 */
	public short getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            The direction to set.
	 */
	public void setDirection(short direction) {
		this.direction = direction;
	}

	/**
	 * @param delayType
	 *            The delayType to set.
	 */
	public void setDelayType(short delayType) {
		this.delayType = delayType;
	}

	/**
	 * @param isReliable
	 *            The isReliable to set.
	 */
	public void setReliable(boolean isReliable) {
		this.isReliable = isReliable;
	}

	/**
	 * @param msgFlowType
	 *            The msgFlowType to set.
	 */
	public void setMsgFlowType(short msgFlowType) {
		this.msgFlowType = msgFlowType;
	}
    public void setEnd(Node end) {
        this.end = end;
    }
    public void setStart(Node start) {
        this.start = start;
    }
    public int getProbOfFailure() {
        return this.probOfFailure;
    }
    public void setProbOfFailure(int probOfFailure) {
        this.probOfFailure = probOfFailure;
    }

	public void setLinkElement(LinkElement linkElement) {
		this.linkElement=linkElement;		
	}
	
	public LinkElement getLinkElement(){
		return this.linkElement;
	}

	public int getTotalMsg() {
		return totalMsg;
	}

	public void setTotalMsg(int totalMsg) {
		this.totalMsg = totalMsg;
	}
	
}