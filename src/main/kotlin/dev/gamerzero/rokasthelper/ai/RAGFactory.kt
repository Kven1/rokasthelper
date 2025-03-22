package dev.gamerzero.rokasthelper.ai

import dev.langchain4j.data.document.loader.ClassPathDocumentLoader
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.rag.content.Content
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter
import dev.langchain4j.spi.ServiceHelper
import dev.langchain4j.spi.model.embedding.EmbeddingModelFactory
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import javax.sql.DataSource


@Component
class RAGFactory(dataSource: DataSource) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private final val recipesStore: PgVectorEmbeddingStore
    private final val tipsStore: PgVectorEmbeddingStore

    init {
        logger.info("Loading RAG documents")

        val recipesDocs = ClassPathDocumentLoader.loadDocument("docs/recipes.txt")
        val tipsDocs = ClassPathDocumentLoader.loadDocument("docs/tips.txt")

        val defaultEmbeddingModel = ServiceHelper
            .loadFactories(EmbeddingModelFactory::class.java)
            .first()
            .create()

        recipesStore = PgVectorEmbeddingStore.datasourceBuilder()
            .datasource(dataSource)
            .table("recipes_embedding")
            .dimension(defaultEmbeddingModel.dimension())
            .createTable(true)
            .dropTableFirst(true)
            .build()

        tipsStore = PgVectorEmbeddingStore.datasourceBuilder()
            .datasource(dataSource)
            .table("tips_embedding")
            .dimension(defaultEmbeddingModel.dimension())
            .createTable(true)
            .dropTableFirst(true)
            .build()

        EmbeddingStoreIngestor.builder()
            .embeddingModel(defaultEmbeddingModel)
            .embeddingStore(recipesStore)
            .documentSplitter(DocumentByLineSplitter(800, 0))
            .build()
            .ingest(recipesDocs)

        EmbeddingStoreIngestor.builder()
            .embeddingModel(defaultEmbeddingModel)
            .embeddingStore(tipsStore)
            .documentSplitter(DocumentSplitters.recursive(500, 200))
            .build()
            .ingest(tipsDocs)

        logger.info("Loaded RAG documents")
    }

    class EmptyContentRetriever : ContentRetriever {
        override fun retrieve(query: Query): List<Content> = emptyList()
    }

    @Bean
    fun retrievalAugmentor(dataSource: DataSource, chatLanguageModel: ChatLanguageModel): RetrievalAugmentor {
        val recipesRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(recipesStore).build()
        val tipsRetriever = EmbeddingStoreContentRetriever.builder().embeddingStore(tipsStore).build()
        val emptyRetriever = EmptyContentRetriever()

        val queryRouter = LanguageModelQueryRouter(
            chatLanguageModel,
            mapOf(
                recipesRetriever to "culinary recipes and info about info about dishes",
                tipsRetriever to "tips about nutrition",
                emptyRetriever to "nothing"
            )
        )

        return DefaultRetrievalAugmentor.builder()
            .contentRetriever(recipesRetriever)
            .queryRouter(queryRouter)
            .build()
    }
}
