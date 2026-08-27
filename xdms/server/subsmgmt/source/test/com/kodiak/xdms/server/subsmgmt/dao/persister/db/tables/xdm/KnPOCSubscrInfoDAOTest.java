/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package test.com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst.DataStores;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnPOCSubscrInfoDAO;
import com.mockrunner.mock.jdbc.MockResultSet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KnPOCSubscrInfoDAO.class, KnDbUtil.class, KnLogger.class})
@SuppressStaticInitializationFor({"com.kodiak.logger.KnLogger"})
public class KnPOCSubscrInfoDAOTest {
	
	@Mock
	public KnLogger knLogger;

	KnPOCSubscrInfoDAO subscrInfoDAO;
	
	private KnPersisterTxn persisterTxn;
	private Connection connection;
	private PreparedStatement preparedStatement;
	
	@Before
	public void setUp() throws Exception {
		mockStatic(KnLogger.class);
		EasyMock.expect(KnLogger.getLogger(KnPOCSubscrInfoDAO.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnPersisterTxn.class)).andReturn(knLogger);
		EasyMock.expect(KnLogger.getLogger(KnDbUtil.class)).andReturn(knLogger);
		
		replay(KnLogger.class);
		
		subscrInfoDAO = new KnPOCSubscrInfoDAO("1234");
		persisterTxn = EasyMock.createMock(KnPersisterTxn.class);
		connection = EasyMock.createMock(Connection.class);
		preparedStatement = EasyMock.createMock(PreparedStatement.class);
		
		EasyMock.expect(persisterTxn.getDBConnection(EasyMock.anyObject(String.class),
				EasyMock.anyObject(DataStores.class), EasyMock.anyBoolean())).andReturn(connection);
		EasyMock.replay(persisterTxn);
		
		
		KnDAOException knDAOException = EasyMock.createMock(KnDAOException.class);
		PowerMock.mockStatic(KnDbUtil.class);
		EasyMock.expect(KnDbUtil.processException(EasyMock.anyObject(Exception.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).
				andReturn(knDAOException);
		KnDbUtil.closeResultSet(EasyMock.anyObject(ResultSet.class));
		EasyMock.expectLastCall();
		PowerMock.replay(KnDbUtil.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPAMAccountProvMdnsSuccess() throws Exception {
		
		int pamAccID = 12345;
		
		Field privateFieldGetQuery = KnPOCSubscrInfoDAO.class.getDeclaredField("PAM_ACCOUNT_PROV_MDNS_QRY");
		privateFieldGetQuery.setAccessible(true);
		String query = (String) privateFieldGetQuery.get(subscrInfoDAO);
		EasyMock.expect(connection.prepareStatement(query)).andReturn(preparedStatement);
		EasyMock.replay(connection);
		
		MockResultSet mockResultSet = new MockResultSet("subResultSet");
		List<String> mdnList = new ArrayList<>();
		for(int i=0; i<5; i++) {
			mdnList.add("900000000"+i);
		}
		mockResultSet.addColumn("MDN", mdnList);
		
		EasyMock.expect(preparedStatement.executeQuery()).andReturn(mockResultSet);
		preparedStatement.setInt(1, pamAccID);
		EasyMock.expectLastCall();
		EasyMock.replay(preparedStatement);
	
		List<String> resList  = subscrInfoDAO.getPAMAccountProvMdns(pamAccID, persisterTxn);
		
		assertNotNull(resList);
		assertSame(5, resList.size());
	}
	
	@Test(expected = KnDAOException.class)  
	public void testGetPAMAccountProvMdnsSqlException() throws Exception {
		
		int pamAccID = 12345;
		
		Field privateFieldGetQuery = KnPOCSubscrInfoDAO.class.getDeclaredField("PAM_ACCOUNT_PROV_MDNS_QRY");
		privateFieldGetQuery.setAccessible(true);
		String query = (String) privateFieldGetQuery.get(subscrInfoDAO);
		EasyMock.expect(connection.prepareStatement(query)).andReturn(preparedStatement);
		EasyMock.replay(connection);
		
		
		EasyMock.expect(preparedStatement.executeQuery()).andThrow(new SQLException());
		preparedStatement.setInt(1, pamAccID);
		EasyMock.expectLastCall();
		EasyMock.replay(preparedStatement);
	
		subscrInfoDAO.getPAMAccountProvMdns(pamAccID, persisterTxn);
	}
	
	@Test
	public void testGetPamAccLastSequenceMdnsSuccess() throws Exception {
		
		int pamAccID = 12345;
		int start = 10;
		int end = 20;
		
		Field privateFieldGetQuery = KnPOCSubscrInfoDAO.class.getDeclaredField("PAM_ACCOUNT_LAST_SEQ_MDNS_QRY");
		privateFieldGetQuery.setAccessible(true);
		String query = (String) privateFieldGetQuery.get(subscrInfoDAO);
		EasyMock.expect(connection.prepareStatement(query)).andReturn(preparedStatement);
		EasyMock.replay(connection);
		
		MockResultSet mockResultSet = new MockResultSet("subResultSet");
		List<String> mdnList = new ArrayList<>();
		for(int i=0; i<5; i++) {
			mdnList.add("900000000"+i);
		}
		mockResultSet.addColumn("MDN", mdnList);
		
		EasyMock.expect(preparedStatement.executeQuery()).andReturn(mockResultSet);
		preparedStatement.setInt(1, start);
		preparedStatement.setInt(2, end);
		preparedStatement.setInt(3, pamAccID);
		
		EasyMock.expectLastCall();
		EasyMock.replay(preparedStatement);
	
		List<String> resList  = subscrInfoDAO.getPamAccLastSequenceMdns(pamAccID, start,end,persisterTxn);
		
		assertNotNull(resList);
		assertSame(5, resList.size());
	}
	
	@Test(expected = KnDAOException.class)  
	public void testGetPamAccLastSequenceMdnsSqlException() throws Exception {
		
		int pamAccID = 12345;
		int start = 10;
		int end = 20;
		
		Field privateFieldGetQuery = KnPOCSubscrInfoDAO.class.getDeclaredField("PAM_ACCOUNT_LAST_SEQ_MDNS_QRY");
		privateFieldGetQuery.setAccessible(true);
		String query = (String) privateFieldGetQuery.get(subscrInfoDAO);
		EasyMock.expect(connection.prepareStatement(query)).andReturn(preparedStatement);
		EasyMock.replay(connection);
		
		
		EasyMock.expect(preparedStatement.executeQuery()).andThrow(new SQLException());
		preparedStatement.setInt(1, pamAccID);preparedStatement.setInt(1, start);
		preparedStatement.setInt(2, end);
		preparedStatement.setInt(3, pamAccID);
		
		EasyMock.expectLastCall();
		EasyMock.replay(preparedStatement);
	
		subscrInfoDAO.getPamAccLastSequenceMdns(pamAccID, start,end,persisterTxn);
	}

}
