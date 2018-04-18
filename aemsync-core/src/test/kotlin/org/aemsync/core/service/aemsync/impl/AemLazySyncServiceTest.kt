package org.aemsync.core.service.aemsync.impl

import kotlinx.coroutines.experimental.runBlocking
import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.storage.UnknownAemInstance
import org.aemsync.test.AemIntegrationTest
import org.aemsync.test.AemIntegrationTest.Companion.authorInstance
import org.aemsync.test.AemIntegrationTest.Companion.publishInstance
import org.aemsync.test.assertSoftly
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

/**
 * Test for [AemLazySyncService].
 *
 * @author Dmytro Primshyts
 */
class AemLazySyncServiceTest : AemIntegrationTest {

  @Test
  fun `test bundles & components & services`() = withMockServer({ clientAndServer ->
    clientAndServer.apply {
      positiveLogin()
      mockBundles()
      mockComponents()
      mockServices()
      mockConfiguration()
      mockVersion()
    }
  }, { _ ->
    withSyncService { syncService ->
      runBlocking {
        syncService.sync(authorInstance).join()

        assertSoftly {
          assertThat(syncService.bundles(authorInstance).size)
              .isEqualTo(524)

          assertThat(syncService.services(authorInstance).size)
              .isEqualTo(22)

          assertThat(syncService.components(authorInstance).size)
              .isEqualTo(2)
        }
      }
    }
  })

  @Test
  fun `sync with unknown aem instance should throw error`() = withSyncService { syncService ->
    val name = "Who is it?"
    val address = "localhost:444"
    val expectedExceptionMessage = "$name:$address"
    val unknownInstance = AemInstance(name, address)

    assertThatThrownBy {
      val sync = syncService.sync(unknownInstance)
      runBlocking {
        sync.await()
      }
      val result = syncService.sync(unknownInstance).getCompletionExceptionOrNull()
      if (result != null) {
        throw result
      }
    }.isInstanceOf(UnknownAemInstance::class.java)
        .hasMessageContaining(expectedExceptionMessage)

    assertThatThrownBy {
      syncService.syncPoints(unknownInstance)
    }.isInstanceOf(UnknownAemInstance::class.java)
        .hasMessageContaining(expectedExceptionMessage)

    assertThatThrownBy {
      syncService.bundles(unknownInstance)
    }.isInstanceOf(UnknownAemInstance::class.java)
        .hasMessageContaining(expectedExceptionMessage)

    assertThatThrownBy {
      syncService.components(unknownInstance)
    }.isInstanceOf(UnknownAemInstance::class.java)
        .hasMessageContaining(expectedExceptionMessage)

    assertThatThrownBy {
      syncService.services(unknownInstance)
    }.isInstanceOf(UnknownAemInstance::class.java)
        .hasMessageContaining(expectedExceptionMessage)
  }

  private fun withSyncService(block: (AemLazySyncService) -> Unit) = runBlocking {
    val syncService = AemLazySyncService.create()

    val authorConnectResult = syncService.connect(authorInstance)
    val publishConnectResult = syncService.connect(publishInstance)

    authorConnectResult.join()
    publishConnectResult.join()

    try {
      block.invoke(syncService)
    } finally {
      syncService.destroy()
    }
  }

}
