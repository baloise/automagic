package com.baloise.automagic.cmdb

interface CMDBService {
	/*
 	 * @param dueDate "YYYY-MM-dd"
 	 * @param status 'To Do','In Progress','Approval','Closed'
 	*/
	String createChange(String title,
						String description,
						String reporterUserId,
						String approverUserId,
						String assigneeUserId,
						String service,
						String system,
						String sbu,
						String dueDate,
						String environment,
						String ppmsProject,
						String issueId,
						String category,
						String actualUser,
						String status,
						String parentCategory)


}
