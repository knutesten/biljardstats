package com.billiardsstats.web

import com.fasterxml.jackson.databind.ObjectMapper
import spark.ResponseTransformer

val toJson = ResponseTransformer { obj -> ObjectMapper().writeValueAsString(obj) }
