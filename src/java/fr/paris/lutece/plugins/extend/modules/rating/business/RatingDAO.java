/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.extend.modules.rating.business;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * This class provides Data Access methods for Rating objects.
 */
public class RatingDAO implements IRatingDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_rating ) FROM extend_rating ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_rating ( id_rating, id_resource, resource_type, vote_count, " +
        " score_value, score_positifs_votes, score_negatives_votes ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_rating, id_resource, resource_type, vote_count, score_value, score_positifs_votes, score_negatives_votes " +
        " FROM extend_rating ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_rating = ? ";
    private static final String SQL_QUERY_SELECT_BY_RESOURCE = SQL_QUERY_SELECT_ALL +
        " WHERE id_resource = ? AND resource_type = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_rating WHERE id_rating = ? ";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = " DELETE FROM extend_rating WHERE resource_type = ? ";
    private static final String SQL_QUERY_FILTER_ID_RESOURCE = " AND id_resource = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE extend_rating SET id_resource = ?, resource_type = ?, vote_count = ?, score_value = ?, score_positifs_votes= ?, score_negatives_votes = ? WHERE id_rating = ?  ";
    private static final String SQL_QUERY_SELECT_ID_MOST_RATED_RESOURCES = " SELECT DISTINCT(id_resource) FROM extend_rating WHERE resource_type = ? ORDER BY vote_count ";
    private static final String SQL_QUERY_SELECT_BY_ID_RESOURCE_LIST = SQL_QUERY_SELECT_ALL +" WHERE resource_type= ? ";
    private static final String SQL_FILTER_ID_LIST_RSOURCE = " id_resource IN ( ";
    private static final String SQL_FILTER_ID_LIST_END = " ) ";
    private static final String CONSTANT_AND = " AND ";
    private static final String SQL_LIMIT = " LIMIT ";
    private static final String CONSTANT_COMMA = ",";
    private static final String CONSTANT_QUESTION_MARK = "?";

    /**
     * Generates a new primary key.
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Rating rating, Plugin plugin )
    {
        int nNewPrimaryKey = newPrimaryKey( plugin );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        rating.setIdRating( nNewPrimaryKey );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, rating.getIdRating(  ) );
        daoUtil.setString( nIndex++, rating.getIdExtendableResource(  ) );
        daoUtil.setString( nIndex++, rating.getExtendableResourceType(  ) );
        daoUtil.setInt( nIndex++, rating.getVoteCount(  ) );
        daoUtil.setDouble( nIndex++, rating.getScoreValue(  ) );
        daoUtil.setInt( nIndex++, rating.getScorePositifsVotes(  ) );
        daoUtil.setInt( nIndex, rating.getScoreNegativesVotes(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rating load( int nIdRating, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdRating );
        daoUtil.executeQuery(  );

        Rating rating = null;

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            rating = new Rating(  );
            rating.setIdRating( daoUtil.getInt( nIndex++ ) );
            rating.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            rating.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            rating.setVoteCount( daoUtil.getInt( nIndex++ ) );
            rating.setScoreValue( daoUtil.getDouble( nIndex++ ) );
            rating.setScorePositifsVotes( daoUtil.getInt( nIndex++ ) );
            rating.setScoreNegativesVotes( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free(  );

        return rating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdRating, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdRating );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        int nIndex = 1;
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_DELETE_BY_RESOURCE );

        if ( !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource ) )
        {
            sbSql.append( SQL_QUERY_FILTER_ID_RESOURCE );
        }

        DAOUtil daoUtil = new DAOUtil( sbSql.toString(  ), plugin );
        daoUtil.setString( nIndex++, strExtendableResourceType );

        if ( !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource ) )
        {
            daoUtil.setString( nIndex, strIdExtendableResource );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Rating rating, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( nIndex++, rating.getIdExtendableResource(  ) );
        daoUtil.setString( nIndex++, rating.getExtendableResourceType(  ) );
        daoUtil.setInt( nIndex++, rating.getVoteCount(  ) );
        daoUtil.setDouble( nIndex++, rating.getScoreValue(  ) );
        daoUtil.setInt( nIndex++, rating.getScorePositifsVotes(  ) );
        daoUtil.setInt( nIndex++, rating.getScoreNegativesVotes(  ) );

        daoUtil.setInt( nIndex, rating.getIdRating(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rating loadByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        Rating rating = null;

        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RESOURCE, plugin );
        daoUtil.setString( nIndex++, strIdExtendableResource );
        daoUtil.setString( nIndex, strExtendableResourceType );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nIndex = 1;

            rating = new Rating(  );
            rating.setIdRating( daoUtil.getInt( nIndex++ ) );
            rating.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            rating.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            rating.setVoteCount( daoUtil.getInt( nIndex++ ) );
            rating.setScoreValue( daoUtil.getDouble( nIndex++ ) );
            rating.setScorePositifsVotes( daoUtil.getInt( nIndex++ ) );
            rating.setScoreNegativesVotes( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free(  );

        return rating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findIdMostRatedResources( String strExtendableResourceType, int nItemsOffset,
        int nMaxItemsNumber, Plugin plugin )
    {
        List<Integer> listIds;

        if ( nMaxItemsNumber > 0 )
        {
            listIds = new ArrayList<Integer>( nMaxItemsNumber );
        }
        else
        {
            listIds = new ArrayList<Integer>(  );
        }

        StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_ID_MOST_RATED_RESOURCES );

        if ( nMaxItemsNumber > 0 )
        {
            sbSQL.append( SQL_LIMIT );

            if ( nItemsOffset > 0 )
            {
                sbSQL.append( CONSTANT_QUESTION_MARK ).append( CONSTANT_COMMA );
            }

            sbSQL.append( CONSTANT_QUESTION_MARK );
        }

        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
        daoUtil.setString( nIndex++, strExtendableResourceType );

        if ( nMaxItemsNumber > 0 )
        {
            if ( nItemsOffset > 0 )
            {
                daoUtil.setInt( nIndex++, nItemsOffset );
            }

            daoUtil.setInt( nIndex, nMaxItemsNumber );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIds.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listIds;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Rating> loadByResourceList( List< String > listIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {	
    	List<Rating> listRating = new ArrayList<>( );
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_SELECT_BY_ID_RESOURCE_LIST );
        if ( CollectionUtils.isNotEmpty( listIdExtendableResource ) )
        {
            sbSql.append( CONSTANT_AND );
            sbSql.append( SQL_FILTER_ID_LIST_RSOURCE );
            sbSql.append( listIdExtendableResource.stream( ).map( s -> "?" ).collect( Collectors.joining( "," ) ) );
            sbSql.append( SQL_FILTER_ID_LIST_END );
        }
    	        
        try( DAOUtil daoUtil = new DAOUtil( sbSql.toString( ), plugin )){
        	int nIndex = 0;
    		
	        daoUtil.setString( ++nIndex, strExtendableResourceType );
	        for ( String id : listIdExtendableResource )
	        {
	            daoUtil.setString( ++nIndex, id );
	        }
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            nIndex = 1;
	
	            Rating rating = new Rating(  );
	            rating.setIdRating( daoUtil.getInt( nIndex++ ) );
	            rating.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
	            rating.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
	            rating.setVoteCount( daoUtil.getInt( nIndex++ ) );
	            rating.setScoreValue( daoUtil.getDouble( nIndex++ ) );
	            rating.setScorePositifsVotes( daoUtil.getInt( nIndex++ ) );
	            rating.setScoreNegativesVotes( daoUtil.getInt( nIndex ) );
	            
	            listRating.add( rating );
	        }

        }
        return listRating;
    }
}
