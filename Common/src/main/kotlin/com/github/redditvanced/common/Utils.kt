package com.github.redditvanced.common

import com.beust.klaxon.Klaxon
import java.io.File

/**
 * Stringifies a json object and write it to a file
 */
fun Klaxon.toJsonFile(value: Any?, file: File) =
	file.writeText(this.toJsonString(value))

/**
 * Stringifies a json object and write it to a file
 */
fun Klaxon.toJsonFile(value: Any?, path: String) =
	toJsonFile(value, File(path))
