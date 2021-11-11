package com.baloise.automagic.cmdb

import com.baloise.automagic.mock.MockRegistry
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

class CMDBServiceTest {

    private CMDBService service

    @Before
    void setUp() throws Exception {
        service = MockRegistry.get().getService(CMDBService)
    }

    @Test
    void createChange() {
        assertEquals('''{acceptType=APPLICATION_JSON, contentType=APPLICATION_JSON, httpMode=POST, requestBody={"username":"fixme","password":"fixme","service":"CreateBAStandardChange","accessToken":"fixme","encrypted":"Y","params":{"ticketclass":"RFC/Change","tickettype":"Standard Change","status":"BA_CH_INPG","tckShorttext":"123deleteme","description":"jenkins <b>test</b>","statementtype":"Information","persnoReqBy":"L000760","persnoAffected":"L000760","personChangeApprover":"L000760","category":"Firewall: API-Change","catParent":"Standard Change","xservice":"Dummy Service BITS","system":"DUMMY LAGER","sbu":"KB Informatik (IT)","dueDate":"2021-11-11","environment":null,"project":null,"jiraIssue":null,"actualUser":"L000760"}}, url=null/services/workflowExecutionRESTService/runsubworkflowservice/runsubworkflow}''', service.createChange(
                '123deleteme',
                'jenkins <b>test</b>',
                'L000760',
                'L000760',
                'L000760',
                'Dummy Service BITS',
                'DUMMY LAGER',
                'KB Informatik (IT)',
                '2021-11-11',
                null,
                null,
                null,
                'Firewall: API-Change',
                'L000760'
        ))
    }
}