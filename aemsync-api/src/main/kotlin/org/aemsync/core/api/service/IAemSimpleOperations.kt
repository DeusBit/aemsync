package org.aemsync.core.api.service

import org.aemsync.core.api.model.AemInstance
import org.aemsync.core.api.model.BundleInfo
import org.aemsync.core.api.model.OsgiComponentInfo
import org.aemsync.core.api.model.OsgiServiceInfo
import org.aemsync.core.api.model.SyncPoint

/**
 * @author Dmytro Primshyts
 */
interface IAemSimpleOperations {

  /**
   * Read additional info about aem instance.
   *
   * @param instance instance to enrich
   * @return aem instance
   * @throws [org.aemsync.core.api.storage.UnknownAemInstance] in if there is no information
   * about given instance available in underlying storage
   */
  fun instance(instance: AemInstance): AemInstance

  /**
   * Find an aem instance by name and group.
   *
   * @param name the name of instance
   * @param group the group of instance (`Empty` by default)
   *
   * @return the instance with given name and group
   */
  fun instance(name: String, group: String = ""): AemInstance?

  /**
   * Will return list of all existing OSGi bundles on given aem instance at given
   * sync point.
   *
   * @param instance the aem instance
   * @param syncPoint the sync point, *latest* for given aem instance by default
   * @return list of bundle info items
   */
  fun bundles(instance: AemInstance,
              syncPoint: SyncPoint = SyncPoint.latest(instance))
      : List<BundleInfo>

  /**
   * Will return list of all OSGi services from given instance.
   *
   * @param instance aem instance
   * @param syncPoint the sync point, *latest* for given aem instance by default
   * @return list of osgi service info items
   */
  fun services(instance: AemInstance,
               syncPoint: SyncPoint = SyncPoint.latest(instance))
      : List<OsgiServiceInfo>

  /**
   * Will return list of all OSGi service from given instance.
   *
   * @param instance aem instance
   * @param syncPoint the sync point, *latest* for given aem instance by default
   * @return list of osgi component info items
   */
  fun components(instance: AemInstance,
                 syncPoint: SyncPoint = SyncPoint.latest(instance))
      : List<OsgiComponentInfo>

}
