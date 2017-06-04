package com.billiardsstats.read.user

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author: Knut Esten Melandsø Nekså
 */
data class User(@JsonProperty("id") val id: String,
                @JsonProperty("email") val email: String,
                @JsonProperty("givenName") val givenName: String,
                @JsonProperty("familyName") val familyName: String)
