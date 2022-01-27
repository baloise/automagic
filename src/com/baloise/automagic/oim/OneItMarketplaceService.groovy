package com.baloise.automagic.oim

import org.eclipse.jgit.transport.CredentialsProvider;

import com.baloise.automagic.oim.internal.masterdata.OneItMarketplaceMasterDataService

interface OneItMarketplaceService extends OneItMarketplaceMasterDataService{
	def getAllCIDetails(int requestNo)
	def getVMDetails(String ObjectID)
	def getRequest(int requestNo)
	def createVM(Map metadata, Map spec)
	String decodePassword(String encodedPassword)
}
