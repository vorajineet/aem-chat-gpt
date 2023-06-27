/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.aem.guides.wknd.it.tests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.testing.clients.instance.InstanceConfiguration;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.graphql.client.AEMHeadlessClient;
import com.adobe.aem.graphql.client.AEMHeadlessClientException;
import com.adobe.aem.graphql.client.GraphQlResponse;
import com.adobe.aem.graphql.client.PersistedQuery;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * GraphQL tests.
 */
public class GraphQlIT {

    private static final String TEST_AUTHOR_FIRST_NAME = "Ian";

    private static final String TEST_AUTHOR_LAST_NAME = "Provo";

    private static final String WKND_SHARED_GRAPHQL_ENDPOINT = "/content/_cq_graphql/wknd-shared/endpoint.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQlIT.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    static AEMHeadlessClient headlessClientAuthor;

    @BeforeClass
    public static void beforeClass() {
        try {
            headlessClientAuthor = getHeadlessClient(cqBaseClassRule.authorRule.getConfiguration());
        } catch (URISyntaxException e) {
            LOGGER.error("Error attempting to initialize headless client", e);
        }
    }

    private static AEMHeadlessClient getHeadlessClient(InstanceConfiguration instanceConfig) throws URISyntaxException {
        final String graphQLEndpoint = instanceConfig.getUrl() + WKND_SHARED_GRAPHQL_ENDPOINT;
        return AEMHeadlessClient.builder() //
                .endpoint(graphQLEndpoint) //
                .basicAuth(instanceConfig.getAdminUser(), instanceConfig.getAdminPassword()).build();
    }

    @Test
    public void testQuery() {

        String query = "{\n" + //
                "  articleList{\n" + //
                "    items{ \n" + //
                "      _path\n" + //
                "      authorFragment{\n" + //
                "        firstName\n" + //
                "        lastName\n" + //
                "         }\n" + //
                "    } \n" + //
                "  }\n" + //
                "}";
        GraphQlResponse response = headlessClientAuthor.runQuery(query);
        assertNull(response.getErrors());
        JsonNode responseData = response.getData();

        assertNotNull(responseData);
        JsonNode articleList = responseData.get("articleList");
        assertNotNull(articleList);
        JsonNode articleListItems = articleList.get("items");
        assertNotNull(articleListItems);
        assertEquals(7, articleListItems.size());
        assertNotNull(articleListItems.get(0).get("_path"));
        assertNotNull(articleListItems.get(0).get("authorFragment"));
    }

    @Test
    public void testQueryWithSyntaxError() {

        thrown.expect(AEMHeadlessClientException.class);
        thrown.expectMessage("Invalid Syntax : offending token");

        String query = "{\n" + //
                "  articleList{\n" + //
                "    items{ \n" + //
                "      _path\n" + //
                "      author\n";

        headlessClientAuthor.runQuery(query);
    }

    @Test
    public void testQueryWithErrorResponse() {

        thrown.expect(AEMHeadlessClientException.class);
        thrown.expectMessage("Field 'nonExisting' in type 'QueryType' is undefined");

        String query = "{ nonExisting { items{  _path } } }";
        headlessClientAuthor.runQuery(query);

    }

    @Test
    public void testQueryWithParameters() {

        String query = "query($authorFirstName: String, $authorLastName: String) {\n" +
                "articleList(filter: {\n" +
                "authorFragment: {\n" +
                "firstName: {\n" +
                "_expressions: {\n" +
                "value: $authorFirstName\n" +
                "}\n" +
                "}\n" +
                "lastName: {\n" +
                "_expressions: {\n" +
                "value: $authorLastName\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}) {\n" +
                "items {\n" +
                "_path\n" +
                "authorFragment {\n" +
                "firstName\n" +
                "lastName\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}";

        Map<String, Object> vars = new HashMap<>();
        vars.put("authorFirstName", TEST_AUTHOR_FIRST_NAME);
        vars.put("authorLastName", TEST_AUTHOR_LAST_NAME);

        GraphQlResponse response = headlessClientAuthor.runQuery(query, vars);
        assertNull(response.getErrors());
        JsonNode responseData = response.getData();

        assertNotNull(responseData);
        JsonNode articleList = responseData.get("articleList");
        assertNotNull(articleList);
        JsonNode articleListItems = articleList.get("items");
        assertNotNull(articleListItems);
        assertEquals(1, articleListItems.size());
        assertEquals(TEST_AUTHOR_FIRST_NAME, articleListItems.get(0).get("authorFragment").get("firstName").asText());
        assertEquals(TEST_AUTHOR_LAST_NAME, articleListItems.get(0).get("authorFragment").get("lastName").asText());
    }

    @Test
    public void testPersistedQuery() {

        GraphQlResponse response = headlessClientAuthor.runPersistedQuery("/wknd-shared/adventures-all");
        assertNull(response.getErrors());
        JsonNode responseData = response.getData();

        assertNotNull(responseData);
        JsonNode adventureListList = responseData.get("adventureList");
        assertNotNull(adventureListList);
        JsonNode adventureListItems = adventureListList.get("items");
        assertNotNull(adventureListItems);
        assertEquals(16, adventureListItems.size());
        JsonNode firstAdvantureItem = adventureListItems.get(0);
        assertNotNull(firstAdvantureItem.get("_path"));
        assertNotNull(firstAdvantureItem.get("title"));
        assertNotNull(firstAdvantureItem.get("price"));
        assertNotNull(firstAdvantureItem.get("tripLength"));
        assertNotNull(firstAdvantureItem.get("primaryImage"));

    }

    @Test
    public void testListPersistedQueries() {

        List<PersistedQuery> listPersistedQueries = headlessClientAuthor.listPersistedQueries("wknd-shared");

        assertFalse(listPersistedQueries.isEmpty());
        PersistedQuery adventuresQuery = listPersistedQueries.stream()
                .filter(p -> p.getShortPath().equals("/wknd-shared/adventures-all")).findFirst().get();
        assertEquals("/wknd-shared/settings/graphql/persistentQueries/adventures-all", adventuresQuery.getLongPath());
        assertThat(adventuresQuery.getQuery(), containsString("adventureList {"));
    }

}
