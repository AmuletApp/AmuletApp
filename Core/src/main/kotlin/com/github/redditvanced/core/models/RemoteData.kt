package com.github.redditvanced.core.models

/**
 * Serializable json class for the data present at /data.json on the builds branch of the Github repo
 */
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
