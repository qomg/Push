package com.boanda.tool.push.bean;

import org.json.JSONObject;

/**   
* @Title: WorkflowContent.java 
* @Package com.boanda.tool.push.bean 
* @Description: 工作流消息内容实体类
* @author 苏浩 
* @date 2015年12月18日 下午12:20:24 
* @version V1.0   
*/

public class WorkflowContent extends TextContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6215962255981075282L;
	private String workflowId;
	private String stepId;
	
	public WorkflowContent(TextContent content){
		super(content);
	}

	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public void fromJSONObject(JSONObject json) {
		super.fromJSONObject(json);
		try {
			setStepId(json.optString("BZBH"));
			setWorkflowId(json.optString("LCSLBH"));
		} catch (Exception e) {
			// handle exception
		}
	}
	
	@Override
	public String toString() {
		JSONObject json = toJSONObject();
		try {
			json.put("BZBH", getStepId());
			json.put("LCSLBH", getWorkflowId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
}
