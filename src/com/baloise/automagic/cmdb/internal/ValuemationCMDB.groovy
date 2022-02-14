package com.baloise.automagic.cmdb.internal

import com.baloise.automagic.cmdb.CMDBService
import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService
import groovy.json.JsonBuilder
import jenkins.plugins.http_request.ResponseContentSupplier

import javax.annotation.Nullable

class ValuemationCMDB extends Registered implements CMDBService {


    String getValuemationURL(){ registry.getService(PropertyService).get('VALUEMATION_URL') }

    String getLink(String changeNo) {"$valuemationURL/vmweb?task=runlink&showgui=true&subwfl=RunlinkOpenBO&xparam_botype=Ticket&xparam_businesskey=$changeNo"}

    @Override
    Map<String,String> createChange(String title,
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
                        String parentCategory = "Infrastructure-Network"
                        ) {
        ResponseContentSupplier response = createOrUpdateTicket(null, title,
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
                parentCategory)
        Map responseJSON = steps.readJSON(text: response.content)
        if(!responseJSON.result || responseJSON.result.score != 'success'){
            throw new Exception(responseJSON.toString())
        }
        String changeNo = responseJSON.result.data.ticketno
        [id :changeNo, link :getLink(changeNo)]
    }


    String buildJSON(String workflowName, Map params) {
        registry.getService(CredentialsService).withCredentials('secrets-devops/VALUEMATION',
              ['USERNAME', 'PASSWORD','ACCESS_TOKEN']
        ) {
            new JsonBuilder(
                ["username" : steps.VALUEMATION_USERNAME,
                "password" : steps.VALUEMATION_PASSWORD,
                "service" : workflowName ,
                "accessToken" : steps.VALUEMATION_ACCESS_TOKEN,
                "encrypted" : "N",
                 "params" : params
                ]
            ).toString()
        }
    }

    private String mapStatus(String status) { [
            'To Do' : 'CH_REC',
            'In Progress' : 'CH_INIMP',
            'Approval' : 'BA_CH_TEST',
            'Closed' : 'CH_CLD'
    ][status]
    }
    private String mapEnvironment(String env) { [
            'development' : 1,
            'test' : 2,
            'production' : 3,
            'training' : 4,
            'integration' : 6,
            'acceptance' : 7
    ][env.toLowerCase().trim()]
    }
    private String mapService(String service) { [
            'balsy ch' : 2367
    ][service.toLowerCase().trim()]
    }

    ResponseContentSupplier createOrUpdateTicket(
            String ticketNo,
            String title,
            String description,
            String reporterUserId,
            String approverUserId,
            String assigneeUserId,
            String service,
            @Nullable String system,
            String dueDate,
            String environment,
            String issueId,
            String category,
            String actualUser,
            String status,
            String parentCategory
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
                "catParent": parentCategory,
                "category": category,
                "servicesid": mapService(service),
                "system": system,
                "dueDate": dueDate,
                "environmentId": mapEnvironment(environment),
                "personcurrent": actualUser,
                "actualUser" : actualUser,
                "changeOwnerPersonNo": approverUserId,
                "changeOwnerGroup": "CHANGE MANAGER"
        ]

        if(ticketNo) {
            params.ticketno = ticketNo
        }

        String body = buildJSON(
                ticketNo ? 'UpdateBAStandardChange' : 'CreateBAStandardChange',
                params)
        return steps.httpRequest(
                acceptType: 'APPLICATION_JSON',
                contentType: 'APPLICATION_JSON',
                httpMode: 'POST',
                requestBody: body,
                url: "$valuemationURL/services/api/execwf"
        )
    }
}
