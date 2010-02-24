/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo
 */

package org.nuxeo.dam.core.listener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.nuxeo.dam.core.Constants;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestInitPropertiesListener extends SQLRepositoryJUnit4 {

    public TestInitPropertiesListener() {
        super("TestInitPropertiesListener");
    }

    @Before
    public void setUp() throws Exception {
        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.platform.picture.api");
        deployBundle("org.nuxeo.ecm.platform.picture.core");
        deployBundle("org.nuxeo.ecm.platform.video.core");
        deployBundle("org.nuxeo.ecm.platform.audio.core");
        deployBundle("org.nuxeo.dam.core");

        openSession();
    }

    @Test
    public void testListener() throws Exception {
        // Import set document
        DocumentModel importSet = session.createDocumentModel("/",
                "importSetTest", Constants.IMPORT_SET_TYPE);
        importSet.setPropertyValue("damc:author", "testCreator");
        Calendar cal = GregorianCalendar.getInstance();
        importSet.setPropertyValue("damc:authoringDate", cal);
        importSet.setPropertyValue("dc:description", "testDescription");
        importSet.setPropertyValue("dc:coverage", "testCoverage");
        importSet.setPropertyValue("dc:expired", cal);
        importSet = session.createDocument(importSet);
        assertNotNull(importSet);
        session.saveDocument(importSet);
        session.save();

        // File document
        DocumentModel file = session.createDocumentModel(
                importSet.getPathAsString(), "fileTest", "File");
        file = session.createDocument(file);
        assertNotNull(file);
        session.saveDocument(file);
        session.save();
        assertTrue(file.hasSchema("dam_common"));
        assertTrue(file.hasSchema("dublincore"));
        assertEquals("testCreator", file.getPropertyValue("damc:author"));
        assertEquals(cal, file.getPropertyValue("damc:authoringDate"));
        assertEquals("testDescription", file.getPropertyValue("dc:description"));
        assertEquals("testCoverage", file.getPropertyValue("dc:coverage"));
        assertEquals(cal, file.getPropertyValue("dc:expired"));

        // Picture document
        DocumentModel picture = session.createDocumentModel(
                importSet.getPathAsString(), "pictureTest", "Picture");
        picture = session.createDocument(picture);
        assertNotNull(picture);
        session.saveDocument(picture);
        session.save();
        assertTrue(picture.hasSchema("dam_common"));
        assertTrue(picture.hasSchema("dublincore"));
        assertEquals("testCreator", picture.getPropertyValue("damc:author"));
        assertEquals(cal, picture.getPropertyValue("damc:authoringDate"));
        assertEquals("testDescription", picture.getPropertyValue("dc:description"));
        assertEquals("testCoverage", picture.getPropertyValue("dc:coverage"));
        assertEquals(cal, picture.getPropertyValue("dc:expired"));
    }

    @After
    public void tearDown() throws Exception {
        closeSession(session);
    }

}
