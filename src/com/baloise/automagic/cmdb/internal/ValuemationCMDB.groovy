package com.baloise.automagic.cmdb.internal

import com.baloise.automagic.cmdb.CMDBService
import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService
import groovy.json.JsonBuilder
import jenkins.plugins.http_request.ResponseContentSupplier

class ValuemationCMDB extends Registered implements CMDBService {


    String getValuemationURL(){ registry.getService(PropertyService).get('VALUEMATION_URL') }

    @Override
    String createChange(String title,
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
        ResponseContentSupplier response = createOrUpdateTicket(null, title,
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
                parentCategory)

        steps.readJSON(text: response.content).result.data.ticketno
    }


    String buildJSON(String workflowName, Map params) {
        registry.getService(CredentialsService).withCredentials('VALUEMATION',
              ['USERNAME', 'PASSWORD','ACCESS_TOKEN']
        ) {
            new JsonBuilder(
                ["username" : steps.VALUEMATION_USERNAME,
                "password" : steps.VALUEMATION_PASSWORD,
                "service" : workflowName ,
                "accessToken" : steps.VALUEMATION_ACCESS_TOKEN,
                "encrypted" : "Y",
                 "params" : params
                ]
            ).toString()
        }
    }

    private String mapStatus(String status) { [
            'To Do' : 'BA_CH_INPG',
            'In Progress' : 'CH_INIMP',
            'Approval' : 'BA_CH_TEST',
            'Closed' : 'CH_CLD'
    ][status]
    }

    ResponseContentSupplier createOrUpdateTicket(
            String ticketNo,
            String title,
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
            String parentCategory = "Standard Change"
    ) {

        Map params = [
                "ticketclass": "RFC/Change",
                "tickettype": "Standard Change",
                "status": mapStatus(status),
                "tckShorttext": title,
                "description": description,
                "statementtype": "Information",
                "persnoReqBy": reporterUserId,
                "persnoAffected": assigneeUserId, // changeOwner
                "personChangeApprover" : approverUserId,
                "category": category,
                "catParent": parentCategory,
                "xservice": service,
                "system": system,
                "sbu": sbu,
                "dueDate": dueDate,
                "environment": environment,
                "project": ppmsProject,
                "jiraIssue": issueId,
                "actualUser" : actualUser
        ]

        if(ticketNo) {
            params.ticketno = ticketNo
        }

        return steps.httpRequest(
                acceptType: 'APPLICATION_JSON',
                contentType: 'APPLICATION_JSON',
                httpMode: 'POST',
                requestBody: buildJSON(
                                ticketNo ? 'UpdateBAStandardChange' : 'CreateBAStandardChange',
                                params),
                url: "$valuemationURL/services/workflowExecutionRESTService/runsubworkflowservice/runsubworkflow"
        )
    }
}
