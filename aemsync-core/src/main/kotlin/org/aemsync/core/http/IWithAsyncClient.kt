package org.aemsync.core.http

import org.asynchttpclient.AsyncHttpClient

/**
 * @author Dmytro Primshyts
 */
interface IWithAsyncClient {
  val client: AsyncHttpClient
}
