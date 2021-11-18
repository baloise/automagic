import com.baloise.automagic.cmdb.CMDBService
import com.baloise.automagic.common.Automagic
import com.baloise.automagic.common.Registry
import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.properties.PropertyStoreService

String call(String title,
			String description,
			String reporterUserId,
			String approverUserId,
			String assigneeUserId,
			String service,
			String system,
			String dueDate,//"YYYY-MM-dd"
			String environment,
			String issueId,
			String category,
			String actualUser,
			String status = 'To Do',
			String parentCategory = "Infrastructure-Network") {
	Registry.get(this).getService(CMDBService.class).createChange(
			title,
			description,
			reporterUserId,
			approverUserId,
			assigneeUserId,
			service,
			system,
			dueDate,
			environment,
			issueId,
			category,
			actualUser,
			status,
			parentCategory

	)
}
