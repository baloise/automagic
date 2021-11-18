package com.baloise.automagic.cmdb

interface CMDBService {
	/*
 	 * @param dueDate "YYYY-MM-dd"
 	 * @param status 'To Do','In Progress','Approval','Closed'
 	 * @return [id :changeNo, link :link]
 	*/
	Map<String,String> createChange(String title,
						String description,
						String reporterUserId,
						String approverUserId,
						String assigneeUserId,
						String service,
						String system,
						String dueDate,
						String environment,
						String issueId,
						String category,
						String actualUser,
						String status,
						String parentCategory)


}
