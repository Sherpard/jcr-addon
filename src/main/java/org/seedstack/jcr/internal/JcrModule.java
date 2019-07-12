/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jcr.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.seedstack.jcr.JcrConfig;
import org.seedstack.jcr.JcrConfig.SessionConfig;
import org.seedstack.jcr.spi.JcrSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

class JcrModule extends AbstractModule {
    private final JcrConfig jcrConfig;
    private final Collection<Class<?>> factories;

    private static final Logger LOGGER = LoggerFactory.getLogger(JcrModule.class);

    JcrModule(Collection<Class<?>> factories, JcrConfig jcrConfig) {
        this.factories = factories;
        this.jcrConfig = jcrConfig;
    }

    @Override
    protected void configure() {
        List<JcrSessionFactory> factoryInstances = new ArrayList<>();
        for (Class<?> factory : factories) {
            try {
                factoryInstances.add(factory.asSubclass(JcrSessionFactory.class).newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            for (Entry<String, SessionConfig> kvp : jcrConfig.getSessions().entrySet()) {
                Session session = buildSession(kvp.getValue(), factoryInstances);
                if (session == null) {
                    // TODO: Throw Exception
                    throw new RuntimeException(String.format("Could not retrieve a session for %s",
                            kvp.getValue().toString()));
                }
                LOGGER.info("Binding Jcr Session with key {}", kvp.getKey());
                bind(Session.class).annotatedWith(Names.named(kvp.getKey()))
                        .toInstance(session);

                if (jcrConfig.getDefaultSession().equals(kvp.getKey())) {
                    bind(Session.class).toInstance(session);
                }
            }
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static Session buildSession(SessionConfig config, List<JcrSessionFactory> factories)
            throws RepositoryException {
        LOGGER.info("Building {} with these factories {}", config, factories);
        for (JcrSessionFactory factory : factories) {
            Session session = factory.createSession(config);
            if (session != null) {
                return session;
            }
        }
        return null;
    }

}