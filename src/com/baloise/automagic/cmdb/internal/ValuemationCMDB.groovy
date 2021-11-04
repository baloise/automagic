package com.baloise.automagic.cmdb.internal

import com.baloise.automagic.cmdb.CMDBService
import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService
import groovy.json.JsonBuilder

class ValuemationCMDB extends Registered implements CMDBService {


    String getValuemationURL(){ registry.getService(PropertyService).get('VALUEMATION_URL') }

    @Override
    String createChange() {
        buildJSON('hallo', [a:123])
    }


    String buildJSON(String workflowName, Map params) {
        registry.getService(CredentialsService).withCredentials('secrets/VALUEMATION',
                ['USERNAME', 'PASSWORD','ACCESS_TOKEN']
        ) {
            new JsonBuilder(
                ["username" : steps.USERNAME,
                "password" :  steps.PASSWORD,
                "service" : workflowName ,
                "accessToken" : steps.ACCESS_TOKEN,
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

    def createOrUpdateTicket(
            String ticketNo,
            String title,
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
            String status = CH_REC,
            // konstant
            String parentCategory = "Standard Change"
    ) {

        Map params = [
                "ticketclass": "RFC/Change",
                "tickettype": "Standard Change",
                "status": status,
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
        return httpPost(valuemationWorkflow,buildJSON(
                ticketNo ? 'UpdateBAStandardChange' : 'CreateBAStandardChange',
                params
        ))
    }
}
