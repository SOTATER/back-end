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
  	"puuid" char(78) not null unique,
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

create table if not exists summoner_champion_statistics (
    "minions_killed_all" integer not null,
    "kills_all" integer not null,
    "assists_all" integer not null,
    "deaths_all" integer not null,
    "played" integer not null,
    "wins" integer not null,
    "champion_id" smallint not null,
    "puuid" varchar(78) not null,
    "season" varchar(63) not null,
    "queue" queue not null,
    primary key ("champion_id", "puuid", "season", "queue")
);

create table if not exists matches (
    "match_id" char(13) not null,
    "data_version" varchar not null,
    "game_creation" bigint not null,
    "game_duration" bigint not null,
    "game_id" bigint not null,
    "game_mode" text not null,
    "game_name" text not null,
    "game_start_timestamp" bigint not null,
    "game_end_timestamp" bigint not null,
    "game_type" text not null,
    "game_version" text not null,
    "map_id" int not null,
    "platform_id" text not null,
    "queue_id" int not null,
    "tournament_code" text,
    primary key("match_id")
);

create table if not exists matches_teams (
    "id" serial,
    "match_id" char(13) references matches("match_id"),
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
    primary key("id"),
    unique ("match_id", "team_id")
);

create table if not exists matches_teams_ban_info (
    "id" serial,
    "match_team_id" int references matches_teams("id"),
    "pick_turn" smallint not null,
    "champion_id" smallint not null,
    primary key("id")
);

create table if not exists matches_summoners (
    "id" serial,
    "match_id" char(13) references matches,
    "puuid" char(78) not null,
    "game_ended_in_early_surrender" boolean not null,
    "game_ended_in_surrender" boolean not null,
    "individual_position" text not null,
    "lane" text not null,
    "participant_id" int not null,
    "riot_id_name" text,
    "riot_id_tagline" text,
    "role" text not null,
    "team_early_surrendered" boolean not null,
    "team_id" int not null,
    "team_position" text not null,
    "summoner_name" text not null,
    primary key("id"),
    unique ("match_id", "puuid"),
    foreign key ("puuid") references summoners ("puuid")
);

create table if not exists matches_summoners_champion (
    "match_summoner_id" int references matches_summoners("id"),
    "champ_experience" int not null,
    "champion_name" text not null,
    "champion_transform" int not null,
    "champ_level" int not null,
    "champion_id" int not null,
    "summoner1_casts" int not null,
    "summoner1_id" int not null,
    "summoner2_casts" int not null,
    "summoner2_id" int not null,
    primary key("match_summoner_id")
);

create table if not exists matches_summoners_perks (
    "id" serial,
    "match_summoner_id" int references matches_summoners("id"),
    "stat_perks_defense" int not null,
    "stat_perks_flex" int not null,
    "stat_perks_offense" int not null,
    primary key("id")
);

create table if not exists matches_summoners_perks_styles (
    "id" serial,
    "perks_id" int references matches_summoners_perks("id"),
    "style" smallint not null,
    "description" text not null,
    primary key("id")
);

create table if not exists matches_summoners_perks_styles_selections (
    "id" serial,
    "styles_id" int references matches_summoners_perks_styles("id"),
    "perk" smallint not null,
    "var1" smallint not null,
    "var2" smallint not null,
    "var3" smallint not null,
    primary key("id")
);

create table if not exists matches_summoners_combat (
    "match_summoner_id" int references matches_summoners("id"),
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
    primary key ("match_summoner_id")
);

create table if not exists matches_summoners_objective (
    "match_summoner_id" int references matches_summoners("id"),
    "turret_kills" int not null,
    "turret_takedowns" int not null,
    "turret_lost" int not null,
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
    primary key ("match_summoner_id")
);

create table if not exists matches_summoners_vision (
    "match_summoner_id" int references matches_summoners("id"),
    "vision_wards_bought_in_game" int not null,
    "sight_wards_bought_in_game" int not null,
    "vision_score" int not null,
    "wards_killed" int not null,
    "wards_placed" int not null,
    "detector_wards_placed" int not null,
    primary key ("match_summoner_id")
);

create table if not exists matches_summoners_item (
    "match_summoner_id" int references matches_summoners("id"),
    "item0" int not null,
    "item1" int not null,
    "item2" int not null,
    "item3" int not null,
    "item4" int not null,
    "item5" int not null,
    "item6" int not null,
    "item_purchased" int not null,
    "consumable_purchased" int not null,
    "gold_earned" int not null,
    "gold_spent" int not null,
    primary key ("match_summoner_id")
);

create table if not exists matches_summoners_record (
    "match_summoner_id" int references matches_summoners("id"),
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
    primary key ("match_summoner_id")
);