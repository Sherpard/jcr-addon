/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr;

import javax.inject.Named;
import javax.jcr.Session;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.jcr.fixtures.BogusFactory;
import org.seedstack.jcr.fixtures.BogusJUnitRunner;

import com.google.inject.Inject;

@RunWith(BogusJUnitRunner.class)
public class BogusFactoryIT {

    @Inject
    @Named("alternative")
    private Session alternativeSession;

    @Test
    public void testFailure() throws Exception {
        Assertions.assertThat(BogusFactory.getCallCount()).isEqualTo(1);

    }

}
