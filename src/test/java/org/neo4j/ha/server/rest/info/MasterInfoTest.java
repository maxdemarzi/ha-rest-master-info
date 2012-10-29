/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.ha.server.rest.info;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.helpers.Pair;
import org.neo4j.kernel.HighlyAvailableGraphDatabase;
import org.neo4j.kernel.ha.Broker;
import org.neo4j.kernel.ha.Master;
import org.neo4j.kernel.ha.zookeeper.Machine;

@RunWith( MockitoJUnitRunner.class )
public class MasterInfoTest
{
    @Mock
    private HighlyAvailableGraphDatabase haDbStub;

    @Test
    public void shouldReturn200OnIsMasterTrue() throws Exception
    {
        when( haDbStub.isMaster() ).thenReturn( true );
        assertEquals( 200, new MasterInfo( haDbStub ).isMaster().getStatus());
    }

    @Test
    public void shouldReturn303OnIsMasterFalse() throws Exception
    {
        when( haDbStub.isMaster() ).thenReturn( false );
        Broker broker = mock( Broker.class );
        when( haDbStub.getBroker() ).thenReturn( broker );
        Pair<Master, Machine> master = mock( Pair.class );
        when( broker.getMaster() ).thenReturn( master );
        Machine masterMachine = mock( Machine.class );
        when( master.other()).thenReturn( masterMachine );
        when( masterMachine.getServerAsString() ).thenReturn( "localhost:6001" );
        Response response = new MasterInfo( haDbStub ).isMaster();
        assertEquals( 303, response.getStatus() );
        assertEquals( "localhost:6001", response.getMetadata().get( HttpHeaders.LOCATION ).get( 0 ) );
    }
}
