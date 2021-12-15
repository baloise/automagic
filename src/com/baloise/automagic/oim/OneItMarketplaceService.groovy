package com.baloise.automagic.oim

import org.eclipse.jgit.transport.CredentialsProvider;

interface OneItMarketplaceService {
	def getAllCIDetails(int requestNo)
	def getVMDetails(String ObjectID)
	def getRequest(int requestNo)
	def createVM(String jsonBody)
}
