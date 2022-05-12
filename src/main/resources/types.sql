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

-- 포지션
create type individual_position as enum ('TOP', 'JUNGLE', 'MIDDLE', 'BOTTOM', 'UTILITY');
-- 라인
create type lane_position as enum ('TOP', 'JUNGLE', 'MIDDLE', 'BOTTOM');
-- 역할 (?)
create type match_role as enum ('SOLO', 'NONE', 'CARRY', 'SUPPORT');