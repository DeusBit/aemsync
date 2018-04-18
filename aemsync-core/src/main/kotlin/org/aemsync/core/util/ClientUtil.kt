package org.aemsync.core.util

import org.aemsync.core.http.Session
import org.asynchttpclient.BoundRequestBuilder

/**
 * @author Dmytro Primshyts
 */
fun BoundRequestBuilder.session(session: Session): BoundRequestBuilder {
  return setCookies(session.cookies)
}
