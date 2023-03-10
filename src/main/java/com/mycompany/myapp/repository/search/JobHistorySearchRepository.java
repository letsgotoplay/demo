package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.repository.JobHistoryRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link JobHistory} entity.
 */
public interface JobHistorySearchRepository extends ElasticsearchRepository<JobHistory, Long>, JobHistorySearchRepositoryInternal {}

interface JobHistorySearchRepositoryInternal {
    Stream<JobHistory> search(String query);

    Stream<JobHistory> search(Query query);

    void index(JobHistory entity);
}

class JobHistorySearchRepositoryInternalImpl implements JobHistorySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final JobHistoryRepository repository;

    JobHistorySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, JobHistoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<JobHistory> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<JobHistory> search(Query query) {
        return elasticsearchTemplate.search(query, JobHistory.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(JobHistory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
