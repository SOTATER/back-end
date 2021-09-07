--create type queue as enum (
--    'RANKED_SOLO_5x5', 'RANKED_FLEX_SR', 'RANKED_FLEX_TT'
--);
--
--create type rank as enum (
--    'I' , 'II', 'III', 'IV'
--);
--
--create type tier as enum (
--    'CHALLENGER', 'GRANDMASTER', 'MASTER', 'DIAMOND', 'PLATINUM', 'GOLD', 'SILVER', 'BRONZE', 'IRON'
--);

create table if not exists leagues (
  queue queue not null,
  tier tier not null,
  league_id char(36),
  name varchar(36) not null,
  primary key ("league_id")
);

create table if not exists summoners (
	"accountid" varchar(56) not null,
 	"profileiconid" integer not null,
  	"revisiondate" bigint not null,
  	"name" varchar not null,
  	"id" varchar(63),
  	"puuid" char(78) not null,
  	"summonerlevel" bigint not null,
  	primary key ("id")
);

create table if not exists league_summoner (
  hot_streak boolean not null,
  fresh_blood boolean not null,
  inactive boolean not null,
  veteran boolean not null,
  loses integer not null,
  wins integer not null,
  rank rank not null,
  league_points integer not null,
  league_id char(36) not null,
  summoner_id varchar(63) not null,
  primary key("summoner_id")
);

create table if not exists matches (
    "match_id" char(13) not null,
    primary key("match_id")
);

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