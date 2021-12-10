CREATE TYPE ban_info AS (
	champion_id integer,
	pick_turn integer
);

CREATE TYPE game_mode AS ENUM (
    'ARAM',
    'CLASSIC'
);

CREATE TYPE game_type AS ENUM (
    'MATCHED_GAME'
);

create type queue as enum (
    'RANKED_SOLO_5x5', 'RANKED_FLEX_SR', 'RANKED_FLEX_TT'
);

create type rank as enum (
    'I' , 'II', 'III', 'IV'
);

create type tier as enum (
    'CHALLENGER', 'GRANDMASTER', 'MASTER', 'DIAMOND', 'PLATINUM', 'GOLD', 'SILVER', 'BRONZE', 'IRON'
);

create table if not exists leagues (
  queue queue not null,
  tier tier not null,
  league_id char(36),
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
  losses integer not null,
  wins integer not null,
  rank rank not null,
  league_points integer not null,
  league_id char(36) not null,
  summoner_id varchar(63) not null,
  primary key("summoner_id")
);

create table if not exists matches (
    "match_id" char(13) not null,
    "data_version" varchar not null,
    "game_creation" bigint not null,
    "game_duration" bigint not null,
    "game_id" bigint not null,
    "game_mode" game_mode not null,
    "game_name" text not null,
    "game_start_timestamp" bigint not null,
    "game_type" game_type not null,
    "game_version" text not null,
    "map_id" int not null,
    "platform_id" text not null,
    "queue_id" int not null,
    "tournament_code" text,
    primary key("match_id")
);

create table if not exists matches_teams (
    "match_id" char(13) references matches,
    "bans" ban_info[],
    "team_id" int not null,
    "win" boolean not null,
    "baron_first" boolean not null,
    "baron_kills" int not null,
    "champion_first" boolean not null,
    "champion_kills" int not null,
    "dragon_first" boolean not null,
    "dragon_kills" int not null,
    "inhibitor_first" boolean not null,
    "inhibitor_kills" int not null,
    "rift_herald_first" boolean not null,
    "rift_herald_kills" int not null,
    "tower_first" boolean not null,
    "tower_kills" int not null,
    primary key("match_id", "team_id")
);

-- 특성 선택 구조
create type perk_selection as (perk int, var1 int, var2 int, var3 int);
create type perk_style as (description text, selections perk_selection[], style int);
create type stat_perks as (defense int, flex int, offense int);
create type perks as (stat_perks stat_perks, styles perk_style[]);

-- 포지션
create type individual_position as enum ('TOP', 'JUNGLE', 'MIDDLE', 'BOTTOM', 'UTILITY');
-- 라인
create type lane_position as enum ('TOP', 'JUNGLE', 'MIDDLE', 'BOTTOM');
-- 역할 (?)
create type match_role as enum ('SOLO', 'NONE', 'CARRY', 'SUPPORT');

create table if not exists matches_summoners (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "game_ended_in_early_surrender" boolean not null,
    "game_ended_in_surrender" boolean not null,
    "individual_position" individual_position not null,
    "lane" lane_position not null,
    "participant_id" int not null,
    "riot_id_name" text,
    "riot_id_tagline" text,
    "role" match_role not null,
    "team_early_surrendered" boolean not null,
    "team_id" int not null,
    "team_position" individual_position not null,
    primary key ("puuid", "match_id")
);

create table if not exists matches_summoners_champion (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "perks" perks not null,
    "champ_experience" int not null,
    "champion_name" text not null,
    "champion_transform" int not null,
    "champ_level" int not null,
    "champion_id" int not null,
    "summoner1_casts" int not null,
    "summoner1_id" int not null,
    "summoner2_casts" int not null,
    "summoner2_id" int not null,
    primary key ("puuid", "match_id")
);

create table if not exists matches_summoners_combat (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "kills" int not null,
    "deaths" int not null,
    "assists" int not null,
    "first_blood_assist" boolean not null,
    "first_blood_kill" boolean not null,
    "unreal_kills" int not null,
    "double_kills" int not null,
    "triple_kills" int not null,
    "quadra_kills" int not null,
    "penta_kills" int not null,
    "total_time_cc_dealt" int not null,
    "time_ccing_others" int not null,
    "spell1_casts" int not null,
    "spell2_casts" int not null,
    "spell3_casts" int not null,
    "spell4_casts" int not null,
    "killing_sprees" int not null,
    "largest_critical_strike" int not null,
    "largest_killing_spree" int not null,
    "largest_multi_kill" int not null,
    primary key ("puuid", "match_id")
);

create table if not exists matches_summoners_objective (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "turret_kills" int not null,
    "turret_takedowns" int not null,
    "turrets_lost" int not null,
    "neutral_minions_killed" int not null,
    "nexus_kills" int not null,
    "nexus_lost" int not null,
    "nexus_takedowns" int not null,
    "objectives_stolen" int not null,
    "objectives_stolen_assists" int not null,
    "inhibitor_kills" int not null,
    "inhibitor_takedowns" int not null,
    "inhibitor_lost" int not null,
    "first_tower_assist" boolean not null,
    "first_tower_kill" boolean not null,
    "dragon_kills" int not null,
    "baron_kills" int not null,
    "total_minions_killed" int not null,
    primary key ("puuid", "match_id")
);

create table if not exists matches_summoners_vision (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "vision_wards_bought_in_game" int not null,
    "sight_wards_bought_in_game" int not null,
    "vision_score" int not null,
    "wards_killed" int not null,
    "wards_placed" int not null,
    "detector_wards_placed" int not null,
    primary key ("puuid", "match_id")
);

create table if not exists matches_summoners_item (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "items" int[] not null,
    "item_purchased" int not null,
    "consumable_purchased" int not null,
    "gold_earned" int not null,
    "gold_spent" int not null,
    primary key ("puuid", "match_id")
);

create table if not exists matches_summoners_record (
    "match_id" char(13) references matches,
    "puuid" char(78) references summoners,
    "total_damage_dealt" int not null,
    "total_damage_dealt_to_champions" int not null,
    "total_damage_shielded_on_teammates" int not null,
    "total_damage_taken" int not null,
    "physical_damage_dealt" int not null,
    "physical_damage_dealt_to_champions" int not null,
    "physical_damage_taken" int not null,
    "magic_damage_dealt" int not null,
    "magic_damage_dealt_to_champions" int not null,
    "magic_damage_taken" int not null,
    "true_damage_dealt" int not null,
    "true_damage_dealt_to_champions" int not null,
    "true_damage_taken" int not null,
    "damage_dealt_to_buildings" int not null,
    "damage_dealt_to_objectives" int not null,
    "damage_dealt_to_turrets" int not null,
    "damage_self_mitigated" int not null,
    "time_played" int not null,
    "total_heal" int not null,
    "total_heal_on_teammates" int not null,
    "total_time_spent_dead" int not null,
    "total_units_healed" int not null,
    "longest_time_spent_living" int not null,
    primary key ("puuid", "match_id")
);