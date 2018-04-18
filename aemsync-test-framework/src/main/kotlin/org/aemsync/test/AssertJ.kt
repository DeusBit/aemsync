package org.aemsync.test

import org.assertj.core.api.SoftAssertions

/**
 * @author Dmytro Primshyts
 */

fun assertSoftly(block: SoftAssertions.() -> Unit) =
    SoftAssertions().apply(block).assertAll()
