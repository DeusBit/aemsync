package org.aemsync.core.api.service

/**
 * Entry point into Aem integration services.
 *
 * @author Dmytro Primshyts
 */
interface IAemIntegrationFacade :
  IAemSyncService,
  IAemSimpleOperations {

  /**
   * Instance of [IAemSyncService].
   */
  val syncService: IAemSyncService

  /**
   * Instance of [IAemSimpleOperations].
   */
  val simpleOperations: IAemSimpleOperations

}
