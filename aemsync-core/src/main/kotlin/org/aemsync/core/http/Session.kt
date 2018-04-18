package org.aemsync.core.http

import io.netty.handler.codec.http.cookie.Cookie

/**
 * Holder for http session cookies.
 *
 * @author Dmytro Primshyts
 */
data class Session(
    val cookies: List<Cookie>
)
