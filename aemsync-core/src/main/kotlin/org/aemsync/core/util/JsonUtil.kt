package org.aemsync.core.util

import com.google.gson.Gson

/**
 * @author Dmytro Primshyts
 */

/**
 * Convert provided json string into a model.
 *
 * @param input json input
 */
inline fun <reified T> fromJson(input: String, gson: Gson = Gson()) : T? {
  return gson.fromJson(input, T::class.java)
}
