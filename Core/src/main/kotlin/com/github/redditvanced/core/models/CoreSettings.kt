package com.github.redditvanced.core.models

import kotlinx.serialization.Serializable

@Serializable
data class CoreSettings(
	/**
	 * All plugins ever installed on this device
	 * Used for first install analytics
	 */
	val installHistory: List<String> = emptyList(),

	/**
	 * Automatically disable plugins when they cause a crash.
	 */
	val crashAutoDisable: Boolean = true,

	/**
	 * Plugin enabled/disabled toggles
	 * Mapped by plugin name -> status
	 */
	val plugins: Map<String, Boolean> = emptyMap(),
)
