package distributed.plugin.runtime;

import distributed.plugin.core.IConstants;

public class BoardAgentEvent extends Event {

	enum actionType {WRITE_TO_BOARD, READ_FROM_BOARD, REMOVE_FROM_BOARD};
	
	private String nodeId;
	
	private String agentId;
	
	/*
	 * Holding event info of {action_type : data require for action}
	 * e.g. write to board {write_board: "A was here"}
	 *      move to port {move_out: port_label}
	 *      init {init: execTime}
	 *      set alarm {alarm: alarm Time}
	 */
	private IMessage info;
	
	
	public BoardAgentEvent(short eventType, int eventId, int execTime, 
			String nodeId, String agentId, IMessage info) {
		super(eventType, eventId, execTime);

		this.nodeId = nodeId;
		this.agentId = agentId;
		this.info = info;

	}

	public String getNodeId() {
		return nodeId;
	}

	public String getAgentId() {
		return agentId;
	}

	public IMessage getInfo() {
		return info;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof BoardAgentEvent))
			return false;

		BoardAgentEvent e = (BoardAgentEvent) obj;
		return (e.getNodeId().equals(this.nodeId) 
				&& e.getAgentId() == this.agentId
				&& e.getExecTime() == this.getExecTime() 
				&& e.getEventType() == this.getEventType());
	}

	public String toString() {
		return super.toString()+ this.nodeId + IConstants.MAIN_DELIMETER
				+ this.agentId + IConstants.MAIN_DELIMETER + info.getLabel();
	}

	

}
