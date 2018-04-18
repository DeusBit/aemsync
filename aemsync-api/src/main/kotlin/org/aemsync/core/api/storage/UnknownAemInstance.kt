package org.aemsync.core.api.storage

import org.aemsync.core.api.model.AemInstance

/**
 * @author Dmytro Primshyts
 */
class UnknownAemInstance(
  val instance: AemInstance
) : Exception() {

  override val message: String?
    get() = "${instance.name}:${instance.address}"

}
