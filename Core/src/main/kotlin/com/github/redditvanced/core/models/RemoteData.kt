package com.github.redditvanced.core.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteData(
	/**
	 * All usernames that are blocked from using RedditVanced
	 */
	val blacklistedUsers: List<String> = listOf(),

	/**
	 * Latest core build available
	 */
	val latestCoreVersion: String,
)
