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

create table if not exists summoners_matches (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "spells" varchar(32)[],
    "runes" char(4)[],
    "kda" int[],
    "multi_kill" int[],
    "level" int,
    "cs" int,
    "dealt" int,
    "wards" int[],
    "items" char(4)[],
    primary key ("puuid", "match_id")
);