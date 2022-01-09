package com.github.redditvanced.injector

import kotlinx.serialization.Serializable

@Serializable
data class CoreManifest(
	val coreVersion: String,
	val redditVersionCode: Int,
	val redditVersionName: String,
)
