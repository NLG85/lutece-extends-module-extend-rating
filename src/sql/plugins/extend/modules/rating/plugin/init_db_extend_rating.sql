INSERT INTO extend_rating_vote_type (id_vote_type, title, template_name) VALUES (1, 'Star', 'extend_rating_vote_type_star' );
INSERT INTO extend_rating_vote_type (id_vote_type, title, template_name) VALUES (2, 'Thumb', 'extend_rating_vote_type_thumb' );
INSERT INTO extend_rating_vote_type (id_vote_type, title, template_name) VALUES (3, 'Vote simple', 'extend_rating_vote_type_simple' );
INSERT INTO extend_rating_vote_type (id_vote_type, title, template_name) VALUES (4, 'Majority', 'extend_rating_vote_type_majority' );

INSERT INTO extend_rating_config (id_extender,id_mailing_list,id_vote_type,is_unique_vote,nb_days_to_vote) VALUES (-1,-1,1,1,0);
