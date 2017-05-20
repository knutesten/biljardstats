package com.billiardsstats.read.user

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

/**
 * @author: Knut Esten Melandsø Nekså
 */

@Component
open class UserDao(val namedTemplate: NamedParameterJdbcTemplate) {
    fun findById(id: String): User {
        return namedTemplate.queryForObject(
                "SELECT * FROM user WHERE id = :id",
                MapSqlParameterSource().addValue("id", id),
                { rs, _ ->
                    User(
                            rs.getString("id"),
                            rs.getString("email"),
                            rs.getString("given_name"),
                            rs.getString("family_name"))
                })
    }

    fun create(user: User) {
        namedTemplate.update("" +
                "INSERT INTO user (id, email, given_name, family_name)" +
                "VALUES (:id, :email, :given_name, :family_name)",
                MapSqlParameterSource()
                        .addValue("id", user.id)
                        .addValue("email", user.email)
                        .addValue("given_name", user.givenName)
                        .addValue("family_name", user.familyName))
    }
}
