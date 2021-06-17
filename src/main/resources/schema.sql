create table if not exists summoners (
	"accountid" varchar(56),
 	"profileiconid" integer,
  	"revisiondate" bigint,
  	"name" varchar,
  	"id" varchar(63),
  	"puuid" char(78),
  	"summonerlevel" bigint,
  	primary key ("id")
);