/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package test.com.kodiak.xdms.mediator.impl;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.security.SecureRandom;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import com.kodiak.common.commdto.request.KnCorpPAMSubsReqDTO;
import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnCorpPAMSubsRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnTransaction;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.helper.KnXDMProvMediator;
import com.kodiak.xdms.mediator.impl.KnXDMMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dao.IProvDAOFactory;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.KnProvDBDAOFactory;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.KnProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMSubsProfInfoDTO;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KnXDMMediator.class, KnStatisticsManagerImpl.class, KnProvFactorySelector.class, KnPersisterTxn.class})
@SuppressStaticInitializationFor({"com.kodiak.logger.KnLogger", "com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl"})

public class KnXDMMediatorTest {

	KnXDMMediator xdmMediator;
	
	
	
	@Mock
	public KnLogger knLogger;
	
	
	@Mock
	public KnStatisticsManagerImpl statisticsManagerImpl;
	
	@Mock
	public KnXDMCommonMediator xdmCommonMediator;
	
	@Mock
	public KnAuditHelper auditHelper;
	
	@Mock
	KnPersisterTxn persisterTxn;
	
	private KnProvClientImpl provClientImplMock;
	
	private IProvXDMServerDAO iProvXDMServerDAO;
	
	private IProvDAOFactory iProvDAOFactory;
	
	private KnXDMProvMediator provMediator;
	
	private KnXDMCorpMediator corpMediator;
	
	
	@Before
	public void setUp() throws Exception {
		suppress(constructor(KnXDMMediator.class));

		mockStatic(KnLogger.class);
		mockStatic(KnStatisticsManagerImpl.class);
		
		EasyMock.expect(KnLogger.getLogger(KnStatisticsManagerImpl.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnXDMMediator.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnXDMCommonMediator.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnPersisterTxn.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnProvDBDAOFactory.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnProvXDMServerDAO.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnTransaction.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnGeneralUtil.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnXDMProvMediator.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnPseudoMDNGenerator.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnXDMCorpMediator.class)).andReturn(knLogger);
		
		
		
		replay(KnLogger.class);
		
		//statistical manager
		EasyMock.expect(KnStatisticsManagerImpl.getInstance()).andReturn(statisticsManagerImpl);
		replay(KnStatisticsManagerImpl.class);
		
		xdmMediator = KnXDMMediator.getInstance();
		
		Field privateField = KnXDMMediator.class.getDeclaredField("commonMediator");
		privateField.setAccessible(true);
		privateField.set(xdmMediator, xdmCommonMediator);
		
		Field privateFieldAudit = KnXDMMediator.class.getDeclaredField("auditPam");
		privateFieldAudit.setAccessible(true);
		privateFieldAudit.set(xdmMediator, auditHelper);
		
		provClientImplMock = EasyMock.createMock(KnProvClientImpl.class);
		
		Field privateFieldProvClientIntf = KnXDMMediator.class.getDeclaredField("provClientIntf");
		privateFieldProvClientIntf.setAccessible(true);
		privateFieldProvClientIntf.set(xdmMediator, provClientImplMock);
		
		iProvDAOFactory = EasyMock.createMock(IProvDAOFactory.class);
		iProvXDMServerDAO = EasyMock.createMock(IProvXDMServerDAO.class);
		provMediator = EasyMock.createMock(KnXDMProvMediator.class);
		corpMediator = EasyMock.createMock(KnXDMCorpMediator.class);
		
		mockStatic(KnProvFactorySelector.class);
		
		EasyMock.expect(KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)).andReturn(iProvDAOFactory);
		EasyMock.expect(iProvDAOFactory.createProvXDMServerDAO()).andReturn(iProvXDMServerDAO);
		EasyMock.replay(iProvDAOFactory);
		
		mockStatic(KnPersisterTxn.class);
		EasyMock.expect(KnPersisterTxn.getPersisterTxn()).andReturn(persisterTxn);
		
		replay(KnPersisterTxn.class);
		replay(KnProvFactorySelector.class);
		
		Field privateFieldKnXDMMediator = KnXDMMediator.class.getDeclaredField("provMediator");
		privateFieldKnXDMMediator.setAccessible(true);
		privateFieldKnXDMMediator.set(xdmMediator, provMediator);
		Field privateFieldCorpMediator= KnXDMMediator.class.getDeclaredField("corpMediator");
		privateFieldCorpMediator.setAccessible(true);
		privateFieldCorpMediator.set(xdmMediator, corpMediator);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	 
	@Ignore @Test
	public void testInvalidDTO() {
		KnMessage knMessage = new KnMessage();
		knMessage.setPayLoad("testpayload");
		IXDMResponseDTO responseDTO = xdmMediator.downgradeRatePlanPamAcc(knMessage);
		assertSame(null, responseDTO);
	}

    @Ignore
	@Test
	public void testMaxSubsLimitIsZero() throws Exception{

		KnOPPAMAccInfoDTO oppamAccInfoDTO = new KnOPPAMAccInfoDTO();
		EasyMock.expect(provClientImplMock.getPAMAccountInfo(EasyMock.anyObject(String.class), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(oppamAccInfoDTO);
			
		KnPAMSubsProfInfoDTO pamSubsProfInfoDTO  = new KnPAMSubsProfInfoDTO();
		EasyMock.expect(iProvXDMServerDAO.retrievePAMSubsProfile(EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(pamSubsProfInfoDTO);
		
		
		EasyMock.replay(provClientImplMock);
		EasyMock.replay(iProvXDMServerDAO);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(0);
		
		KnMessage knMessage = new KnMessage();
		knMessage.setPayLoad(pamAccInfoDTO);
		
		IXDMResponseDTO responseDTO = xdmMediator.downgradeLicensePack(knMessage);
		assertSame(null, responseDTO);
		
	}

    @Ignore
	@Test
	public void testDeleteInProgress() throws Exception{

		KnOPPAMAccInfoDTO oppamAccInfoDTO = new KnOPPAMAccInfoDTO();
		oppamAccInfoDTO.setTotalNoOfLines(100);
		oppamAccInfoDTO.setPamAccState(4);
		
		EasyMock.expect(provClientImplMock.getPAMAccountInfo(EasyMock.anyObject(String.class), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(oppamAccInfoDTO);
			
		KnPAMSubsProfInfoDTO pamSubsProfInfoDTO  = new KnPAMSubsProfInfoDTO();
		EasyMock.expect(iProvXDMServerDAO.retrievePAMSubsProfile(EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(pamSubsProfInfoDTO);
		
		
		EasyMock.replay(provClientImplMock);
		EasyMock.replay(iProvXDMServerDAO);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(1);
		
		KnMessage knMessage = new KnMessage();
		knMessage.setPayLoad(pamAccInfoDTO);
		
		IXDMResponseDTO responseDTO = xdmMediator.downgradeLicensePack(knMessage);
		assertSame(null, responseDTO);
		
	}

    @Ignore
	@Test
	public void testRetrieveUnusedSubsListSuccessLogic1() throws KnXDMServerException, Exception{
		

		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList");
		List<String> mdnList = new ArrayList<>();
		mdnList.add("1234");
		mdnList.add("5678");
		mdnList.add("978665");
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(mdnList);
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(1);
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
		assertSame(1, resMdnList.size());
		
	}

    @Ignore
	@Test
	public void testRetrieveUnusedSubsListSuccessLogic12() throws KnXDMServerException, Exception{
		

		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList");
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add("1234");
		provMdnList.add("5678");
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add("one");
		corpMdnList.add("two");
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(corpMdnList);
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(4);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.re(pamAccInfoDTO, persisterTxn);
		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());

	}
	
	 
	@Ignore @Test
	public void testRetrieveUnusedSubsListSuccessLogic123() throws KnXDMServerException, Exception{
		

		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add("1234");
		provMdnList.add("5678");
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add("one");
		corpMdnList.add("two");
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add("A");
		SEQMdnList.add("B");
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(corpMdnList);
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(SEQMdnList);
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(6);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
//		assertSame(1, resMdnList.size());
		
	}

	 
	@Ignore @Test
    public void testRetrieveUnusedSubsListSuccessLogic13() throws KnXDMServerException, Exception{


        KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
        List<String> provMdnList = new ArrayList<>();
        provMdnList.add("1234");
        provMdnList.add("5678");
        List<String> corpMdnList = new ArrayList<>();
        List<String> SEQMdnList = new ArrayList<>();
        SEQMdnList.add("A");
        SEQMdnList.add("B");
        SEQMdnList.add("C");
        SEQMdnList.add("B");


        expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
        expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(corpMdnList);
        expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(SEQMdnList);


        EasyMock.replay(xdmMediatorMock);

        KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
        pamAccInfoDTO.setTransactionId("12345");
        pamAccInfoDTO.setBillingNumber("984512345");
        pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
        pamAccInfoDTO.setMaxSubs(120);
        pamAccInfoDTO.setPamAccId(1234);
        pamAccInfoDTO.setSubsCount(4);



        KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
        expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
        pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
        EasyMock.expectLastCall();
        replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
        List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
        assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
//		assertSame(1, resMdnList.size());

    }
	
	 
	@Ignore @Test
	public void testRetrieveUnusedSubsListSuccessLogic23() throws KnXDMServerException, Exception{
		

		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add("one");
		corpMdnList.add("two");
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add("C");
		SEQMdnList.add("D");
		SEQMdnList.add("A");
		SEQMdnList.add("B");
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(corpMdnList);
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(SEQMdnList);
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setMaxSubs(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(5);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
//		assertSame(1, resMdnList.size());
		
	}

	 
	@Ignore @Test
	public void testRetrieveUnusedSubsListSuccessLogic2() throws KnXDMServerException, Exception{
		

		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add("one");
		corpMdnList.add("two");
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add("C");
		SEQMdnList.add("D");
		SEQMdnList.add("A");
		SEQMdnList.add("B");
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(corpMdnList);
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(SEQMdnList);
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setMaxSubs(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(2);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
//		assertSame(1, resMdnList.size());
		
	}

	
	 
	@Ignore @Test
	public void testRetrieveUnusedSubsListSuccessLogic3() throws KnXDMServerException, Exception{
		

		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		List<String> corpMdnList = new ArrayList<>();
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add("C");
		SEQMdnList.add("D");
		SEQMdnList.add("A");
		SEQMdnList.add("B");
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(corpMdnList);
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(SEQMdnList);
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(3);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
//		assertSame(1, resMdnList.size());
		
	}

	 
	@Ignore @Test
	public void testRetrieveUnusedSubsListSuccessLogic3Iteration() throws KnXDMServerException, Exception{
		SecureRandom randomGenerator = new SecureRandom();
		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> trial4 = new ArrayList<>();
		trial4.add(String.valueOf(randomGenerator.nextInt(100)));
		
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(new ArrayList<>());
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(new ArrayList<>());
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList).once().andReturn(corpMdnList).once().andReturn(SEQMdnList).once().andReturn(trial4).once();
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(3);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
//		assertSame(1, resMdnList.size());
		
	}

	 
	@Ignore @Test (expected = KnXDMServerException.class)
	public void testRetrieveUnusedSubsListSuccessLogic1Expection() throws KnXDMServerException, Exception{
		SecureRandom randomGenerator = new SecureRandom();
		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> trial4 = new ArrayList<>();
		trial4.add(String.valueOf(randomGenerator.nextInt(100)));
		
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andThrow(new KnXDMServerException("0000", "Logic2Failure"));
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(new ArrayList<>());
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList).once().andReturn(corpMdnList).once().andReturn(SEQMdnList).once().andReturn(trial4).once();
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(3);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
//		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
		
		fail("Logic1Failure");
//		assertSame(1, resMdnList.size());
		
	}
	
	 
	@Ignore @Test (expected = Exception.class)
	public void testRetrieveUnusedSubsListSuccessLogic3Expection() throws KnXDMServerException, Exception{
		SecureRandom randomGenerator = new SecureRandom();
		KnXDMMediator xdmMediatorMock = createPartialMock(KnXDMMediator.class, "getProvisionedPseudoList", "getNoCorpCordMdnsList", "getPamAccLastSequenceMdns");
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> SEQMdnList = new ArrayList<>();
		SEQMdnList.add(String.valueOf(randomGenerator.nextInt(100)));
		List<String> trial4 = new ArrayList<>();
		trial4.add(String.valueOf(randomGenerator.nextInt(100)));
		
		
		
		expectPrivate(xdmMediatorMock, "getProvisionedPseudoList", EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList);
		expectPrivate(xdmMediatorMock, "getNoCorpCordMdnsList", EasyMock.anyObject(KnXDMPAMAccInfoDTO.class), EasyMock.anyObject(KnPersisterTxn.class)).andThrow(new Exception());
		expectPrivate(xdmMediatorMock, "getPamAccLastSequenceMdns", EasyMock.anyInt(),EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class)).andReturn(provMdnList).once().andReturn(corpMdnList).once().andReturn(SEQMdnList).once().andReturn(trial4).once();
		
		
		EasyMock.replay(xdmMediatorMock);
		
		KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
		pamAccInfoDTO.setTransactionId("12345");
		pamAccInfoDTO.setBillingNumber("984512345");
		pamAccInfoDTO.setOpMap(new HashMap<String, Object>());
		pamAccInfoDTO.setTotalNoOfLines(120);
		pamAccInfoDTO.setPamAccId(1234);
		pamAccInfoDTO.setSubsCount(3);
		
		
		
		KnPseudoMDNGenerator pseudoMDNGenerator = createMock(KnPseudoMDNGenerator.class);
		expectNew("com.kodiak.utilities.pseudomdngenerator.KnPseudoMDNGenerator").andReturn(pseudoMDNGenerator);
		pseudoMDNGenerator.updatePseudoMdns(EasyMock.anyObject(List.class), EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class));
		EasyMock.expectLastCall();
		replay(pseudoMDNGenerator, KnPseudoMDNGenerator.class);
		List<String> resMdnList = xdmMediatorMock.retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);
//		assertSame(pamAccInfoDTO.getSubsCount(), resMdnList.size());
		
		fail("Logic2Failure");
//		assertSame(1, resMdnList.size());
		
	}

	@Ignore @Test (expected = KnXDMServerException.class)
	public void testGetProvisionedPseudoListException() throws KnXDMServerException, Exception{

		EasyMock.expect(provMediator.retrievePAMAccountProvMDNs(EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class))).andThrow(new KnXDMServerException("0000", "errorMessage"));
		EasyMock.replay(provMediator);
		Class[] parameterTypes;
		Object[] parameters;
		parameterTypes = new Class[2];
		parameterTypes[0] = Integer.TYPE;
		parameterTypes[1] = KnPersisterTxn.class;
		parameters =  new Object[] {new Integer(1), persisterTxn};
		
		Method method = xdmMediator.getClass().getDeclaredMethod("getProvisionedPseudoList", parameterTypes);
		method.setAccessible(true);
		
		List<String> resMdnList  = (List<String>) method.invoke(xdmMediator, parameters);
		fail();
	    //assertSame(3, resMdnList.size());
		
		
	}
	
	@Ignore @Test
	public void testGetProvisionedPseudoList() throws KnXDMServerException, Exception{
		
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add("1");
		provMdnList.add("2");
		provMdnList.add("3");
		EasyMock.expect(provMediator.retrievePAMAccountProvMDNs(EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(provMdnList);
		EasyMock.replay(provMediator);
		Class[] parameterTypes;
		Object[] parameters;
		parameterTypes = new Class[2];
		parameterTypes[0] = Integer.TYPE;
		parameterTypes[1] = KnPersisterTxn.class;
		parameters =  new Object[] {new Integer(1), persisterTxn};
		
		Method method = xdmMediator.getClass().getDeclaredMethod("getProvisionedPseudoList", parameterTypes);
		method.setAccessible(true);
		
		List<String> resMdnList  = (List<String>) method.invoke(xdmMediator, parameters);
		
	    assertSame(3, resMdnList.size());
		
		
	}

	@Ignore @Test
	public void testGetPamAccLastSequenceMdns() throws KnXDMServerException, Exception{
		
		List<String> provMdnList = new ArrayList<>();
		provMdnList.add("1");
		provMdnList.add("2");
		provMdnList.add("3");
		EasyMock.expect(provMediator.getPamAccLastSequenceMdns(EasyMock.anyInt(), EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(provMdnList);
		EasyMock.replay(provMediator);
		Class[] parameterTypes;
		Object[] parameters;
		parameterTypes = new Class[4];
		parameterTypes[0] = Integer.TYPE;
		parameterTypes[1] = Integer.TYPE;
		parameterTypes[2] = Integer.TYPE;
		parameterTypes[3] = KnPersisterTxn.class;
		parameters =  new Object[] {new Integer(1),new Integer(2),new Integer(3), persisterTxn};
		
		Method method = xdmMediator.getClass().getDeclaredMethod("getPamAccLastSequenceMdns", parameterTypes);
		method.setAccessible(true);
		
		List<String> resMdnList  = (List<String>) method.invoke(xdmMediator, parameters);
		
	    assertSame(3, resMdnList.size());
		
		
	}
	
	@Ignore @Test (expected = KnXDMServerException.class)
	public void testGetPamAccLastSequenceMdnsException() throws KnXDMServerException, Exception{

		EasyMock.expect(provMediator.getPamAccLastSequenceMdns(EasyMock.anyInt(), EasyMock.anyInt(),EasyMock.anyInt(), EasyMock.anyObject(KnPersisterTxn.class))).andThrow(new KnXDMServerException("0000", "errorMessage"));
		EasyMock.replay(provMediator);
		Class[] parameterTypes;
		Object[] parameters;
		parameterTypes = new Class[4];
		parameterTypes[0] = Integer.TYPE;
		parameterTypes[1] = Integer.TYPE;
		parameterTypes[2] = Integer.TYPE;
		parameterTypes[3] = KnPersisterTxn.class;
		parameters =  new Object[] {new Integer(1),new Integer(2),new Integer(3), persisterTxn};
		
		Method method = xdmMediator.getClass().getDeclaredMethod("getPamAccLastSequenceMdns", parameterTypes);
		method.setAccessible(true);
		
		List<String> resMdnList  = (List<String>) method.invoke(xdmMediator, parameters);
		
	   fail();
	   
		
	}
	
	@Ignore @Test
	public void testGetNoCorpCordMdnsList() throws KnXDMServerException, Exception{
		
		List<String> corpMdnList = new ArrayList<>();
		corpMdnList.add("1");
		corpMdnList.add("2");
		corpMdnList.add("3");

		KnCorpPAMSubsRespDTO respDto = new KnCorpPAMSubsRespDTO();

		respDto.setFreePAMSubsList(corpMdnList);
		KnXDMPAMAccInfoDTO pamAccInfoDto = new KnXDMPAMAccInfoDTO();
		pamAccInfoDto.setPamAccId(1);
		pamAccInfoDto.setSubsCount(2);
		KnXDMPAMSubsProfInfoDTO profileDetails = new KnXDMPAMSubsProfInfoDTO();
		profileDetails.setExtCorpId("123");
		profileDetails.setClient_Type(1);
		pamAccInfoDto.setProfileDetails(profileDetails);
		
		EasyMock.expect(corpMediator.getUnusedSubsList(EasyMock.anyObject(KnCorpPAMSubsReqDTO.class), EasyMock.anyObject(KnPersisterTxn.class))).andReturn(respDto);
		EasyMock.replay(corpMediator);
		Class[] parameterTypes;
		Object[] parameters;
		parameterTypes = new Class[2];
		parameterTypes[0] = KnXDMPAMAccInfoDTO.class;
		parameterTypes[1] = KnPersisterTxn.class;
		parameters =  new Object[] {pamAccInfoDto, persisterTxn};
		
		Method method = xdmMediator.getClass().getDeclaredMethod("getNoCorpCordMdnsList", parameterTypes);
		method.setAccessible(true);
		
		List<String> resMdnList    =   (List<String>) method.invoke(xdmMediator, parameters);
		
	    assertSame(3, resMdnList.size());
		
		
	}
	
}
