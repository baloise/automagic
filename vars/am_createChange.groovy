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
			String sbu,
			String dueDate,//"YYYY-MM-dd"
			String environment,
			String ppmsProject,
			String issueId,
			String category,
			String actualUser,
			String status = 'To Do',
			String parentCategory = "Standard Change") {
	Registry.get(this).getService(CMDBService.class).createChange(
			title,
			description,
			reporterUserId,
			approverUserId,
			assigneeUserId,
			service,
			system,
			sbu,
			dueDate,
			environment,
			ppmsProject,
			issueId,
			category,
			actualUser,
			status,
			parentCategory

	)
}
