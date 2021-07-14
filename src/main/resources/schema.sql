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

create table if not exists matches (
    "match_id" char(13) not null,
    primary key("match_id")
)

create table if not exists summoners_matches (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "spells" varchar(32)[] not null,
    "runes" char(4)[] not null,
    "kda" int[] not null,
    "multi_kill" int[] not null,
    "level" int not null,
    "cs" int not null,
    "dealt" int not null,
    "wards" int[] not null,
    "items" char(4)[] not null,
    primary key ("puuid", "match_id")
);