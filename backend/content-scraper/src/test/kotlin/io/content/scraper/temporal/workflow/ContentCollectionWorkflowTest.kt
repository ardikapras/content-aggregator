package io.content.scraper.temporal.workflow

import io.content.scraper.temporal.activity.ActivityLogActivity
import io.content.scraper.temporal.activity.DatabaseActivity
import io.content.scraper.temporal.activity.RSSFeedActivity
import io.content.scraper.temporal.activity.RSSFeedResult
import io.content.scraper.temporal.workflow.impl.ContentCollectionWorkflowImpl
import io.mockk.every
import io.mockk.mockk
import io.temporal.testing.TestWorkflowRule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ContentCollectionWorkflowTest {
    @RegisterExtension
    @JvmField
    val testWorkflowRule: TestWorkflowRule =
        TestWorkflowRule
            .newBuilder()
            .setWorkflowTypes(ContentCollectionWorkflowImpl::class.java)
            .setDoNotStart(true)
            .build()

    @Test
    fun `should successfully collect content from source`() {
        val mockRSSFeedActivity = mockk<RSSFeedActivity>()
        val mockDatabaseActivity = mockk<DatabaseActivity>()
        val mockActivityLogActivity = mockk<ActivityLogActivity>()

        testWorkflowRule.worker.registerActivitiesImplementations(
            mockRSSFeedActivity,
            mockDatabaseActivity,
            mockActivityLogActivity,
        )
        testWorkflowRule.testEnvironment.start()

        val sourceId = UUID.randomUUID()
        val mockSource = mockk<io.content.scraper.models.Source>()

        every { mockSource.name } returns "Test Source"
        every { mockSource.url } returns "https://example.com/rss"

        every { mockDatabaseActivity.findSourceById(sourceId) } returns mockSource
        every { mockRSSFeedActivity.fetchRSSFeed(sourceId) } returns
            RSSFeedResult(
                success = true,
                feedContent = "<rss><channel><item><title>Test Article</title></item></channel></rss>",
                errorMessage = null,
            )
        every { mockRSSFeedActivity.extractArticleUrls(any(), any()) } returns emptyList()
        every { mockRSSFeedActivity.saveNewArticles(any(), any()) } returns emptyList()
        every { mockRSSFeedActivity.updateSourceLastScraped(any(), any()) } returns Unit
        every { mockActivityLogActivity.logWorkflowStarted(any(), any()) } returns Unit
        every { mockActivityLogActivity.logWorkflowCompleted(any(), any(), any()) } returns Unit

        val workflow = testWorkflowRule.workflowClient.newWorkflowStub(ContentCollectionWorkflow::class.java)
        val result = workflow.collectContentFromSource(sourceId)

        assertNotNull(result)
        assertEquals(sourceId, result.sourceId)
        assertEquals("Test Source", result.sourceName)
        assertEquals(0, result.articlesProcessed)
    }

    @Test
    fun `should handle source not found`() {
        val mockRSSFeedActivity = mockk<RSSFeedActivity>()
        val mockDatabaseActivity = mockk<DatabaseActivity>()
        val mockActivityLogActivity = mockk<ActivityLogActivity>()

        testWorkflowRule.worker.registerActivitiesImplementations(
            mockRSSFeedActivity,
            mockDatabaseActivity,
            mockActivityLogActivity,
        )
        testWorkflowRule.testEnvironment.start()

        val sourceId = UUID.randomUUID()

        every { mockDatabaseActivity.findSourceById(sourceId) } returns null
        every { mockActivityLogActivity.logWorkflowStarted(any(), any()) } returns Unit

        val workflow = testWorkflowRule.workflowClient.newWorkflowStub(ContentCollectionWorkflow::class.java)
        val result = workflow.collectContentFromSource(sourceId)

        assertNotNull(result)
        assertEquals(sourceId, result.sourceId)
        assertEquals("Unknown", result.sourceName)
        assertEquals(0, result.articlesProcessed)
        assertEquals(1, result.articlesFailed)
    }
}
