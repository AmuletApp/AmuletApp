package com.github.redditvanced.common.models

/**
 * Serializable json class for the manifest.json found in the core build
 */
data class CoreManifest(
	/**
	 * The version of the core
	 */
	val version: String,

	/**
	 * The supported version code of the reddit app
	 */
	val redditVersionCode: Int,
)
