package com.mycompany.myapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.mycompany.myapp.domain.Job;
import com.mycompany.myapp.repository.JobRepository;
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
 * Spring Data Elasticsearch repository for the {@link Job} entity.
 */
public interface JobSearchRepository extends ElasticsearchRepository<Job, Long>, JobSearchRepositoryInternal {}

interface JobSearchRepositoryInternal {
    Stream<Job> search(String query);

    Stream<Job> search(Query query);

    void index(Job entity);
}

class JobSearchRepositoryInternalImpl implements JobSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final JobRepository repository;

    JobSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, JobRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Job> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Job> search(Query query) {
        return elasticsearchTemplate.search(query, Job.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Job entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
