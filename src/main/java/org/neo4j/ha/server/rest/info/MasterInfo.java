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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.HighlyAvailableGraphDatabase;

/**
 * Simple REST endpoint returning master information for HA clusters. Knows if
 * this machine is master (boolean) and which machine is the master (String)
 */
@Path( "/masterinfo" )
public class MasterInfo
{
    private final HighlyAvailableGraphDatabase db;

    public MasterInfo( @Context GraphDatabaseService database )
    {
        if ( database instanceof HighlyAvailableGraphDatabase )
        {
            db = (HighlyAvailableGraphDatabase) database;
        }
        else
        {
            db = null;
        }
    }

    /**
     * @return A String representation of true iff this machine is a master of a
     *         cluster, false otherwise, as returned by
     *         {@link Boolean#toString(boolean)} of
     */
    @GET
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/isMaster" )
    public Response isMaster()
    {
        if ( db == null )
        {
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( Boolean.toString( false ).getBytes()).build();
        }
        if ( db.isMaster() )
        {
            return Response.status( Response.Status.OK ).entity( Boolean.toString( true ).getBytes()).build();
        }
        else
        {
            return Response.status( Response.Status.SEE_OTHER ).entity( Boolean.toString( false ).getBytes() ).header(
                    HttpHeaders.LOCATION, db.getBroker().getMaster().other().getServerAsString() ).build();
        }
    }

    /**
     * @return A String representation of true iff this machine is a slave of a
     *         cluster, false otherwise, as returned by
     *         {@link Boolean#toString(boolean)} of
     */
    @GET
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/isSlave" )
    public Response isSlave()
    {
        if ( db == null )
        {
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( Boolean.toString( false ).getBytes()).build();
        }
        if ( !(db.isMaster() ) )
        {
            return Response.status( Response.Status.OK ).entity( Boolean.toString( true ).getBytes()).build();
        }
        else
        {
            return Response.status( Response.Status.SERVICE_UNAVAILABLE ).entity( Boolean.toString( false ).getBytes() ).build();
        }
    }

    /**
     *
     * @return A String representation of the form hostname:port if this is an
     *         HA deployment, empty string otherwise
     */
    @GET
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/getMaster" )
    public String getMaster()
    {
        if ( db == null )
        {
            return "";
        }
        return db.getBroker().getMaster().other().getServerAsString();
    }
}
