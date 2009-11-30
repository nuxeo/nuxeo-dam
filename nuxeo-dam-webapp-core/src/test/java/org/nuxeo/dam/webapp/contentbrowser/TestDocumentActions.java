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

package org.nuxeo.dam.webapp.contentbrowser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

public class TestDocumentActions extends SQLRepositoryTestCase {

	private static final Log log = LogFactory.getLog(TestDocumentActions.class);

    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.api");
        openSession();

        session.save();
    }

    public void tearDown() throws Exception {
        session.cancel();
    }
	public void testGetTitleCropped() throws Exception {
		String croppedTitle = null;
		// Initialize the repository
		setUp();
		assertNotNull("session is null ?", session);
		DocumentModel doc = session.createDocumentModel("/", "doc", "File");
		assertNotNull("doc is null", doc);
	    doc.setPropertyValue("dc:title", "Image Name with longish name");
	    // now create the doc in the session and save it to the session so that it gets given an id
	    doc = session.createDocument(doc);
	    session.save();
	    // get the document's id for later tests
	    String docId = doc.getId();

	    DocumentActions actions = new DocumentActions();

	    // Now test a variety of croppings including boundary cases
	    croppedTitle = actions.getTitleCropped(doc, 20);
	    assertEquals("Image Nam...ish name", croppedTitle);

	    doc.setPropertyValue("dc:title", "1234");
	    croppedTitle = actions.getTitleCropped(doc, 5);
	    assertEquals("1234", croppedTitle);

	    doc.setPropertyValue("dc:title", "12345");
	    croppedTitle = actions.getTitleCropped(doc, 4);
	    assertEquals("12345", croppedTitle);

	    doc.setPropertyValue("dc:title", "12345");
	    croppedTitle = actions.getTitleCropped(doc, 5);
	    assertEquals("12345", croppedTitle);

	    doc.setPropertyValue("dc:title", "12345");
	    croppedTitle = actions.getTitleCropped(doc, 6);
		log.warn(croppedTitle);
	    assertEquals("12345", croppedTitle);

	    doc.setPropertyValue("dc:title", "123456");
	    croppedTitle = actions.getTitleCropped(doc, 5);
	    assertEquals("1...6", croppedTitle);

	    doc.setPropertyValue("dc:title", "1234567890123456789");
	    croppedTitle = actions.getTitleCropped(doc, 20);
	    assertEquals("1234567890123456789", croppedTitle);

	    doc.setPropertyValue("dc:title", "12345678901234567890");
	    croppedTitle = actions.getTitleCropped(doc, 20);
	    assertEquals("12345678901234567890", croppedTitle);

	    doc.setPropertyValue("dc:title", "123456789012345678901");
	    croppedTitle = actions.getTitleCropped(doc, 20);
	    assertEquals("123456789...45678901", croppedTitle);

	    // test odd numbered maxLength
	    doc.setPropertyValue("dc:title", "123456789012345678901");
	    croppedTitle = actions.getTitleCropped(doc, 19);
	    assertEquals("12345678...45678901", croppedTitle);

	    // test null or empty title. This should come back with the cropped id. Not really a good test cos it
	    // uses same code as function being tested
	    doc.setPropertyValue("dc:title", null);
	    // we will crop to 21 so that the cropped id will contain the first 9 and last 9 characters
	    String idStart = docId.substring(0, 9);
	    String idEnd = docId.substring(docId.length()-9, docId.length());
	    croppedTitle = actions.getTitleCropped(doc, 21);
	    assertEquals(idStart+"..."+idEnd, croppedTitle);

		tearDown();

	}
}
