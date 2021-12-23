package com.sota.clone.copyopgg.domain.entities

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.EnumType
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types

enum class GameType {
    CUSTOM_GAME,
    MATCHED_GAME,
    TUTORIAL_GAME
}

enum class GameMode {
    ARAM,
    ARSR,
    ASCENSION,
    ASSASSINATE,
    CLASSIC,
    DARKSTAR,
    DOOMBOTSTEEMO,
    FIRSTBLOOD,
    INTRO,
    KINGPORO,
    NEXUSBLITZ,
    ODIN,
    ONEFORALL,
    OVERCHARGE,
    PRACTICETOOL,
    PROJECT,
    SIEGE,
    SNOWURF,
    STARTGUARDIAN,
    TUTORIAL,
    TUTORIAL_MODULE_1,
    TUTORIAL_MODULE_2,
    TUTORIAL_MODULE_3,
    URF
}

enum class QueueType {
    RANKED_SOLO_5x5, RANKED_FLEX_SR, RANKED_FLEX_TT
}

enum class Tier {
    IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
}

enum class Rank {
    I, II, III, IV
}

class PostgreSQLEnumType : EnumType<Enum<*>>() {
    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeSet(
        st: PreparedStatement,
        value: Any,
        index: Int,
        session: SharedSessionContractImplementor
    ) {
        st.setObject(
            index,
            value.toString(),
            Types.OTHER
        )
    }
}