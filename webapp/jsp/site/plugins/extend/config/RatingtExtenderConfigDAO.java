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
package fr.paris.lutece.plugins.extend.modules.rating.business.config;

import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfigDAO;
import fr.paris.lutece.plugins.extend.modules.rating.service.RatingPlugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * CommentExtenderConfigDAO
 *
 */
public class RatingtExtenderConfigDAO implements IExtenderConfigDAO<RatingExtenderConfig>
{
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_rating_config ( id_extender, id_mailing_list, id_vote_type, is_unique_vote, nb_days_to_vote ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_UPDATE = " UPDATE extend_rating_config SET id_mailing_list = ?, id_vote_type = ?, is_unique_vote = ?, nb_days_to_vote = ?, nb_vote_per_user = ?, is_connected = ?, delete_vote = ?, date_start = ?, date_end = ? WHERE id_extender = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_rating_config WHERE id_extender = ? ";
    private static final String SQL_QUERY_SELECT = " SELECT id_extender, id_mailing_list, id_vote_type, is_unique_vote, nb_days_to_vote, nb_vote_per_user, is_connected, delete_vote, date_start, date_end FROM extend_rating_config WHERE id_extender = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( RatingExtenderConfig config )
    {
        int nIndex = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, RatingPlugin.getPlugin(  ) );
        daoUtil.setInt( nIndex++, config.getIdExtender(  ) );
        daoUtil.setInt( nIndex++, config.getIdMailingList(  ) );
        daoUtil.setString( nIndex++, config.getRatingType(  ) );
        daoUtil.setBoolean( nIndex++, config.isUniqueVote(  ) );
        daoUtil.setInt( nIndex, config.getNbDaysToVote(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( RatingExtenderConfig config )
    {
        int nIndex = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, RatingPlugin.getPlugin(  ) );
        daoUtil.setInt( nIndex++, config.getIdMailingList(  ) );
        daoUtil.setString( nIndex++, config.getRatingType(  ) );
        daoUtil.setBoolean( nIndex++, config.isUniqueVote(  ) );
        daoUtil.setInt( nIndex++, config.getNbDaysToVote(  ) );

        if ( config.isLimitVote(  ) )
        {
            daoUtil.setInt( nIndex++, config.getNbVotePerUser(  ) );
        }
        else
        {
            daoUtil.setInt( nIndex++, 0 );
        }

        daoUtil.setBoolean( nIndex++, config.isLimitedConnectedUser(  ) );
        daoUtil.setBoolean( nIndex++, config.isDeleteVote(  ) );

        daoUtil.setTimestamp( nIndex++, config.getDateStart(  ) );
        daoUtil.setTimestamp( nIndex++, config.getDateEnd(  ) );

        daoUtil.setInt( nIndex, config.getIdExtender(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdExtender )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, RatingPlugin.getPlugin(  ) );
        daoUtil.setInt( 1, nIdExtender );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RatingExtenderConfig load( int nIdExtender )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, RatingPlugin.getPlugin(  ) );
        daoUtil.setInt( 1, nIdExtender );
        daoUtil.executeQuery(  );

        RatingExtenderConfig config = null;

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            config = new RatingExtenderConfig(  );
            config.setIdExtender( daoUtil.getInt( nIndex++ ) );
            config.setIdMailingList( daoUtil.getInt( nIndex++ ) );
            config.setRatingType( daoUtil.getString( nIndex++ ) );
            config.setUniqueVote( daoUtil.getBoolean( nIndex++ ) );
            config.setNbDaysToVote( daoUtil.getInt( nIndex++ ) );
            config.setNbVotePerUser( daoUtil.getInt( nIndex++ ) );
            config.setLimitedConnectedUser( daoUtil.getBoolean( nIndex++ ) );
            config.setDeleteVote( daoUtil.getBoolean( nIndex++ ) );
            config.setDateStart( daoUtil.getTimestamp( nIndex++ ) );
            config.setDateEnd( daoUtil.getTimestamp( nIndex ) );
        }

        daoUtil.free(  );

        return config;
    }
}
