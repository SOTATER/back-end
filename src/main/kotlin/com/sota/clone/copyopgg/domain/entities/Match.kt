package com.sota.clone.copyopgg.domain.entities

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "matches")
class Match {

    @Id
    @Column(name = "match_id")
    var id: String? = null

    @Column(name = "data_version")
    var dataVersion: String? = null

    @Column(name = "game_creation")
    var gameCreation: Long? = null

    @Column(name = "game_duration")
    var gameDuration: Long? = null

    @Column(name = "game_id")
    var gameId: Long? = null

    @Column(name = "game_mode")
    @Enumerated(EnumType.STRING)
    var gameMode: GameMode? = null

    @Column(name = "game_name")
    var gameName: String? = null

    @Column(name = "game_start_timestamp")
    var gameStartTimestamp: Long? = null

    @Column(name = "game_type")
    @Enumerated(EnumType.STRING)
    var gameType: GameType? = null

    @Column(name = "game_version")
    var gameVersion: String? = null

    @Column(name = "map_id")
    var mapId: Int? = null

    @Column(name = "platform_id")
    var platformId: String? = null

    @Column(name = "queue_id")
    var queueId: Int? = null

    @Column(name = "tournament_code")
    var tournamentType: String? = null

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    var matchSummoners: MutableList<MatchSummoner> = ArrayList()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    var matchTeams: MutableList<MatchTeam> = ArrayList()

    fun addMatchSummoner(matchSummoner: MatchSummoner) {
        matchSummoner.match = this
        this.matchSummoners.add(matchSummoner)
    }

    fun addMatchTeam(matchTeam: MatchTeam) {
        matchTeam.match = this
        this.matchTeams.add(matchTeam)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Match

        if (id != other.id) return false
        if (dataVersion != other.dataVersion) return false
        if (gameCreation != other.gameCreation) return false
        if (gameDuration != other.gameDuration) return false
        if (gameId != other.gameId) return false
        if (gameMode != other.gameMode) return false
        if (gameName != other.gameName) return false
        if (gameStartTimestamp != other.gameStartTimestamp) return false
        if (gameType != other.gameType) return false
        if (gameVersion != other.gameVersion) return false
        if (mapId != other.mapId) return false
        if (platformId != other.platformId) return false
        if (queueId != other.queueId) return false
        if (tournamentType != other.tournamentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (dataVersion?.hashCode() ?: 0)
        result = 31 * result + (gameCreation?.hashCode() ?: 0)
        result = 31 * result + (gameDuration?.hashCode() ?: 0)
        result = 31 * result + (gameId?.hashCode() ?: 0)
        result = 31 * result + (gameMode?.hashCode() ?: 0)
        result = 31 * result + (gameName?.hashCode() ?: 0)
        result = 31 * result + (gameStartTimestamp?.hashCode() ?: 0)
        result = 31 * result + (gameType?.hashCode() ?: 0)
        result = 31 * result + (gameVersion?.hashCode() ?: 0)
        result = 31 * result + (mapId ?: 0)
        result = 31 * result + (platformId?.hashCode() ?: 0)
        result = 31 * result + (queueId ?: 0)
        result = 31 * result + (tournamentType?.hashCode() ?: 0)
        return result
    }
}

@Entity
@Table(name = "matches_summoners")
class MatchSummoner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id")
    var match: Match? = null

    @Column(name = "puuid")
    var puuid: String? = null

    @Column(name = "game_ended_in_early_surrender")
    var gameEndedInEarlySurrender: Boolean? = null

    @Column(name = "game_ended_in_surrender")
    var gameEndedInSurrender: Boolean? = null

    @Column(name = "individual_position")
    @Enumerated(EnumType.STRING)
    var individualPosition: IndividualPosition? = null

    @Column(name = "lane")
    @Enumerated(EnumType.STRING)
    var lane: LanePosition? = null

    @Column(name = "participant_id")
    var participantId: Int? = null

    @Column(name = "riot_id_name")
    var riotIdName: String? = null

    @Column(name = "riot_id_tagline")
    var riotIdTagline: String? = null

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var role: MatchRole? = null

    @Column(name = "team_early_surrendered")
    var teamEarlySurrendered: Boolean? = null

    @Column(name = "team_id")
    var teamId: Int? = null

    @Column(name = "team_position")
    var teamPosition: LanePosition? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerChampion: MatchSummonerChampion? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerPerk: MatchSummonerPerk? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerCombat: MatchSummonerCombat? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerObjective: MatchSummonerObjective? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerVision: MatchSummonerVision? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerItem: MatchSummonerItem? = null

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "matchSummoner", cascade = [CascadeType.ALL])
    var matchSummonerRecord: MatchSummonerRecord? = null
}

@Entity
@Table(name = "matches_summoners_perks")
class MatchSummonerPerk : Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "stat_perks_defense")
    var statPerksDefense: Int? = null

    @Column(name = "stat_perks_flex")
    var statPerksFlex: Int? = null

    @Column(name = "stat_perks_offense")
    var statPerksOffense: Int? = null

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "perks", cascade = [CascadeType.ALL])
    var styles: MutableList<MatchSummonerPerkStyle> = ArrayList()

    fun addPerkStyle(perkStyle: MatchSummonerPerkStyle) {
        perkStyle.perks = this
        this.styles.add(perkStyle)
    }
}

@Entity
@Table(name = "matches_summoners_perks_styles")
class MatchSummonerPerkStyle {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perks_id")
    var perks: MatchSummonerPerk? = null

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "style", cascade = [CascadeType.ALL])
    var selections: MutableList<MatchSummonerPerkStyleSelection> = ArrayList()

    @Column(name = "description")
    var description: String? = null

    @Column(name = "style")
    var style: Int? = null

    fun addSelection(sel: MatchSummonerPerkStyleSelection) {
        this.selections.add(sel)
        sel.style = this
    }
}

@Entity
@Table(name = "matches_summoners_perks_styles_selections")
class MatchSummonerPerkStyleSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "styles_id")
    var style: MatchSummonerPerkStyle? = null

    @Column(name = "perk")
    var perk: Int? = null

    @Column(name = "var1")
    var var1: Int? = null

    @Column(name = "var2")
    var var2: Int? = null

    @Column(name = "var3")
    var var3: Int? = null

}


@Entity
@Table(name = "matches_summoners_champion")
class MatchSummonerChampion : Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "champ_experience")
    var champExperience: Int? = null

    @Column(name = "champion_name")
    var championName: String? = null

    @Column(name = "champion_transform")
    var championTransform: Int? = null

    @Column(name = "champ_level")
    var champLevel: Int? = null

    @Column(name = "champion_id")
    var championId: Int? = null

    @Column(name = "summoner1_casts")
    var summoner1Casts: Int? = null

    @Column(name = "summoner1_id")
    var summoner1Id: Int? = null

    @Column(name = "summoner2_casts")
    var summoner2Casts: Int? = null

    @Column(name = "summoner2_id")
    var summoner2Id: Int? = null
}

@Entity
@Table(name = "matches_summoners_combat")
class MatchSummonerCombat : Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "kills")
    var kills: Int? = null

    @Column(name = "deaths")
    var deaths: Int? = null

    @Column(name = "assists")
    var assists: Int? = null

    @Column(name = "first_blood_assist")
    var firstBloodAssist: Boolean? = null

    @Column(name = "first_blood_kill")
    var firstBloodKill: Boolean? = null

    @Column(name = "unreal_kills")
    var unrealKills: Int? = null

    @Column(name = "double_kills")
    var doubleKills: Int? = null

    @Column(name = "triple_kills")
    var tripleKills: Int? = null

    @Column(name = "quadra_kills")
    var quadraKills: Int? = null

    @Column(name = "penta_kills")
    var pentaKills: Int? = null

    @Column(name = "total_time_cc_dealt")
    var totalTimeCcDealt: Int? = null

    @Column(name = "time_ccing_others")
    var timeCcingOthers: Int? = null

    @Column(name = "spell1_casts")
    var spell1Casts: Int? = null

    @Column(name = "spell2_casts")
    var spell2Casts: Int? = null

    @Column(name = "spell3_casts")
    var spell3Casts: Int? = null

    @Column(name = "spell4_casts")
    var spell4Casts: Int? = null

    @Column(name = "killing_sprees")
    var killingSprees: Int? = null

    @Column(name = "largest_critical_strike")
    var largestCriticalStrike: Int? = null

    @Column(name = "largest_killing_spree")
    var largestKillingSpree: Int? = null

    @Column(name = "largest_multi_kill")
    var largestMultiKill: Int? = null
}

@Entity
@Table(name = "matches_summoners_objective")
class MatchSummonerObjective : Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "turret_kills")
    var turretKills: Int? = null

    @Column(name = "turret_takedowns")
    var turretTakedowns: Int? = null

    @Column(name = "turret_lost")
    var turretLost: Int? = null

    @Column(name = "neutral_minions_killed")
    var neutralMinionsKilled: Int? = null

    @Column(name = "nexus_kills")
    var nexusKills: Int? = null

    @Column(name = "nexus_lost")
    var nexusLost: Int? = null

    @Column(name = "nexus_takedowns")
    var nexusTakedowns: Int? = null

    @Column(name = "objectives_stolen")
    var objectiveStolen: Int? = null

    @Column(name = "objectives_stolen_assists")
    var objectivesStolenAssists: Int? = null

    @Column(name = "inhibitor_kills")
    var inhibitorKills: Int? = null

    @Column(name = "inhibitor_takedowns")
    var inhibitorTakedowns: Int? = null

    @Column(name = "inhibitor_lost")
    var inhibitorLost: Int? = null

    @Column(name = "first_tower_assist")
    var firstTowerAssist: Boolean? = null

    @Column(name = "first_tower_kill")
    var firstTowerKill: Boolean? = null

    @Column(name = "dragon_kills")
    var dragonKills: Int? = null

    @Column(name = "baron_kills")
    var baronKills: Int? = null

    @Column(name = "total_minions_killed")
    var totalMinionsKilled: Int? = null
}

@Entity
@Table(name = "matches_summoners_vision")
class MatchSummonerVision : Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "vision_wards_bought_in_game")
    var visionWardsBoughtInGame: Int? = null

    @Column(name = "sight_wards_bought_in_game")
    var sightWardsBoughtInGame: Int? = null

    @Column(name = "vision_score")
    var visionScore: Int? = null

    @Column(name = "wards_killed")
    var wardsKilled: Int? = null

    @Column(name = "wards_placed")
    var wardsPlaced: Int? = null

    @Column(name = "detector_wards_placed")
    var detectorWardsPlaced: Int? = null

}

@Entity
@Table(name = "matches_summoners_item")
class MatchSummonerItem : Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "item0")
    var item0: Int? = null

    @Column(name = "item1")
    var item1: Int? = null

    @Column(name = "item2")
    var item2: Int? = null

    @Column(name = "item3")
    var item3: Int? = null

    @Column(name = "item4")
    var item4: Int? = null

    @Column(name = "item5")
    var item5: Int? = null

    @Column(name = "item6")
    var item6: Int? = null

    @Column(name = "item_purchased")
    var itemPurchased: Int? = null

    @Column(name = "consumable_purchased")
    var consumablePurchased: Int? = null

    @Column(name = "gold_earned")
    var goldEarned: Int? = null

    @Column(name = "gold_spent")
    var goldSpent: Int? = null
}

@Entity
@Table(name = "matches_summoners_record")
class MatchSummonerRecord : Serializable {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_summoner_id")
    var matchSummoner: MatchSummoner? = null

    @Column(name = "total_damage_dealt")
    var totalDamageDealt: Int? = null

    @Column(name = "total_damage_dealt_to_champions")
    var totalDamageDealtToChampions: Int? = null

    @Column(name = "total_damage_shielded_on_teammates")
    var totalDamageShieldedOnTeammates: Int? = null

    @Column(name = "total_damage_taken")
    var totalDamageTaken: Int? = null

    @Column(name = "physical_damage_dealt")
    var physicalDamageDealt: Int? = null

    @Column(name = "physical_damage_dealt_to_champions")
    var physicalDamageDealtToChampions: Int? = null

    @Column(name = "physical_damage_taken")
    var physicalDamageTaken: Int? = null

    @Column(name = "magic_damage_dealt")
    var magicDamageDealt: Int? = null

    @Column(name = "magic_damage_dealt_to_champions")
    var magicDamageDealtToChampions: Int? = null

    @Column(name = "magic_damage_taken")
    var magicDamageTaken: Int? = null

    @Column(name = "true_damage_dealt")
    var trueDamageDealt: Int? = null

    @Column(name = "true_damage_dealt_to_champions")
    var trueDamageDealtToChampions: Int? = null

    @Column(name = "true_damage_taken")
    var trueDamageTaken: Int? = null

    @Column(name = "damage_dealt_to_buildings")
    var damageDealtToBuildings: Int? = null

    @Column(name = "damage_dealt_to_objectives")
    var damageDealtToObjectives: Int? = null

    @Column(name = "damage_dealt_to_turrets")
    var damageDealtToTurrets: Int? = null

    @Column(name = "damage_self_mitigated")
    var damageSelfMitigated: Int? = null

    @Column(name = "time_played")
    var timePlayed: Int? = null

    @Column(name = "total_heal")
    var totalHeal: Int? = null

    @Column(name = "total_heal_on_teammates")
    var totalHealOnTeammates: Int? = null

    @Column(name = "total_time_spent_dead")
    var totalTimeSpentDead: Int? = null

    @Column(name = "total_units_healed")
    var totalUnitsHealed: Int? = null

    @Column(name = "longest_time_spent_living")
    var longestTimeSpentLiving: Int? = null

}

@Entity
@Table(name = "matches_teams_ban_info")
class BanInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_team_id")
    var matchTeam: MatchTeam? = null

    @Column(name = "champion_id")
    var championId: Int? = null

    @Column(name = "pick_turn")
    var pickTurn: Int? = null
}

@Entity
@Table(name = "matches_teams")
class MatchTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    var match: Match? = null

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "matchTeam", cascade = [CascadeType.ALL])
    var bans: MutableList<BanInfo> = ArrayList()

    @Column(name = "team_id")
    var teamId: Int? = null

    @Column(name = "win")
    var win: Boolean? = null

    @Column(name = "baron_first")
    var baronFirst: Boolean? = null

    @Column(name = "baron_kills")
    var baronKills: Int? = null

    @Column(name = "champion_first")
    var championFirst: Boolean? = null

    @Column(name = "champion_kills")
    var championKills: Int? = null

    @Column(name = "dragon_first")
    var dragonFirst: Boolean? = null

    @Column(name = "dragon_kills")
    var dragonKills: Int? = null

    @Column(name = "inhibitor_first")
    var inhibitorFirst: Boolean? = null

    @Column(name = "inhibitor_kills")
    var inhibitorKills: Int? = null

    @Column(name = "rift_herald_first")
    var riftHeraldFirst: Boolean? = null

    @Column(name = "rift_herald_kills")
    var riftHeraldKills: Int? = null

    @Column(name = "tower_first")
    var towerFirst: Boolean? = null

    @Column(name = "tower_kills")
    var towerKills: Int? = null

    fun addBanInfo(banInfo: BanInfo) {
        bans.add(banInfo)
        banInfo.matchTeam = this
    }
}

enum class IndividualPosition {
    TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY
}

enum class LanePosition {
    TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY
}

enum class MatchRole {
    SOLO, NONE, CARRY, SUPPORT, DUO
}